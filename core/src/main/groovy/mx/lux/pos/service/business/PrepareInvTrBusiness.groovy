package mx.lux.pos.service.business

import mx.lux.pos.java.querys.AcusesQuery
import mx.lux.pos.java.querys.ParametrosQuery
import mx.lux.pos.java.querys.TipoTransInvQuery
import mx.lux.pos.java.repository.AcusesJava
import mx.lux.pos.java.repository.DetalleNotaVentaJava
import mx.lux.pos.java.repository.NotaVentaJava
import mx.lux.pos.java.repository.Parametros
import mx.lux.pos.java.repository.SucursalesJava
import mx.lux.pos.java.repository.TipoTransInvJava
import mx.lux.pos.java.repository.TransInvDetJava
import mx.lux.pos.java.repository.TransInvJava
import mx.lux.pos.java.service.ArticulosServiceJava
import mx.lux.pos.java.service.SucursalServiceJava
import mx.lux.pos.model.InvTrDetRequest
import mx.lux.pos.model.InvTrRequest
import mx.lux.pos.model.LcRequest
import mx.lux.pos.model.TipoParametro
import mx.lux.pos.model.TipoTransInv
import mx.lux.pos.model.TransInv
import mx.lux.pos.model.Sucursal
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.Acuse
import mx.lux.pos.model.Parametro
import mx.lux.pos.model.DetalleNotaVenta
import mx.lux.pos.model.TransInvDetalle
import mx.lux.pos.repository.AcuseRepository
import mx.lux.pos.repository.ParametroRepository
import mx.lux.pos.repository.RetornoDetRepository
import mx.lux.pos.repository.RetornoRepository
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.InventarioService
import mx.lux.pos.service.SucursalService
import mx.lux.pos.util.CustomDateUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PrepareInvTrBusiness {
  private static final String TR_TYPE_ISSUE_SALES = 'VENTA'
  private static final String TR_TYPE_ENTER_SP = 'ENTRADA_SP'
  private static final String TR_TYPE_RECEIPT_RETURN = 'DEVOLUCION'
  private static final String TR_TYPE_RECEIPT_RETURN_SP = 'SALIDA'

  private static ArticuloService parts
  private static ArticulosServiceJava partsJava
  private static InventarioService inventory
  private static SucursalService sites
  private static SucursalServiceJava sitesJava
  private static ParametroRepository parameters
  private static AcuseRepository acuseRepository
  private static RetornoRepository retornoRepository
  private static RetornoDetRepository retornoDetRepository
  private static final String TAG_SURTE_SUCURSAL = 'S'
  private static final String TAG_GENERICO_ARMAZON = 'A'

  static PrepareInvTrBusiness instance

  private Logger log = LoggerFactory.getLogger( this.class )
  public String genericoInvalido
  public String genericoNoInventariable

  @Autowired
  PrepareInvTrBusiness( ArticuloService pArticuloService, InventarioService pInventarioService, SucursalService pSucursalService,
                        ParametroRepository pParametroRepository, AcuseRepository pAcuseRepository, RetornoRepository pRetornoRepository,
                        RetornoDetRepository pRetornoDetRepository) {
    parts = pArticuloService
    inventory = pInventarioService
    sites = pSucursalService
    parameters = pParametroRepository
    acuseRepository = pAcuseRepository
    retornoRepository = pRetornoRepository
    retornoDetRepository = pRetornoDetRepository
    partsJava = new ArticulosServiceJava()
    sitesJava = new SucursalServiceJava()
    instance = this
  }

  private TransInv prepareTransaction( InvTrRequest pRequest ) {
    TipoTransInv trType = inventory.obtenerTipoTransaccion( pRequest.trType )
    Sucursal site = sites.obtenSucursalActual()
    TransInv trMstr = null

    if ( ( trType != null ) && ( site != null ) ) {
      trMstr = new TransInv()
      trMstr.fecha = DateUtils.truncate( pRequest.effDate, Calendar.DATE )
        if( trType.idTipoTrans.equalsIgnoreCase("ENTRADA_TIENDA")){
            trMstr.sucursalDestino = pRequest.siteFrom
            trMstr.sucursal = pRequest.siteTo
        } else {
            trMstr.sucursalDestino = pRequest.siteTo
        }
      trMstr.observaciones = pRequest.remarks
      trMstr.sucursal = site.id
      trMstr.idEmpleado = pRequest.idUser

        String aleatoria = claveAleatoria(trMstr.sucursal, trType.ultimoFolio+1)
        Parametro p = Registry.find( TipoParametro.TRANS_INV_TIPO_SALIDA_ALMACEN )
        if (trType.idTipoTrans.equalsIgnoreCase(p.valor)) {
            Integer ultimoFolio = trType.ultimoFolio+1
            String url = Registry.getURL( trType.idTipoTrans);
            String variable = trMstr.sucursal + '>' + trMstr.sucursalDestino + '>' +
                     ultimoFolio+ '>' +
                    aleatoria + '>' +
                    trMstr.idEmpleado.trim() + '>'

            for (int i = 0; i < pRequest.skuList.size(); i++) {
                variable += pRequest.skuList[i].sku + ',' + pRequest.skuList[i].qty +'|'
            }
            log.debug( "cadena a enviar: ${variable}" )
            url += String.format( '?arg=%s', URLEncoder.encode( String.format( '%s', variable ), 'UTF-8' ) )
            String response = ''
            try{
                response = url.toURL().text
                response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
            } catch ( Exception e ){
                println e
            }
            println "respuesta: ${response}"
            trMstr.referencia = aleatoria
            insertAcuseTransaction( trMstr, variable, response, trType.idTipoTrans )
            } else {
                trMstr.referencia = pRequest.reference
            }


            Integer iDet = 0
            Collections.sort( pRequest.skuList, new Comparator<InvTrDetRequest>() {
                @Override
                int compare(InvTrDetRequest o1, InvTrDetRequest o2) {
                    return o1.sku.compareTo(o2.sku)
                }
            } )
            for ( InvTrDetRequest detReq in pRequest.skuList ) {
                if ( parts.esInventariable( detReq.sku ) ) {
                  if( trMstr.trDet.size() <= 0 ){
                    TransInvDetalle det = new TransInvDetalle()
                    det.linea = ++iDet
                    det.sku = detReq.sku
                    det.cantidad = detReq.qty
                    det.tipoMov = trType.tipoMovObj.codigo
                    trMstr.add( det )
                  } else if( trMstr.trDet.last().sku != detReq.sku ){
                    TransInvDetalle det = new TransInvDetalle()
                    det.linea = ++iDet
                    det.sku = detReq.sku
                    det.cantidad = detReq.qty
                    det.tipoMov = trType.tipoMovObj.codigo
                    trMstr.add( det )
                  } else {
                    trMstr.trDet.last().cantidad = trMstr.trDet.last().cantidad+detReq.qty
                  }
                }
            }
            trMstr.idTipoTrans = trType.idTipoTrans
        }
        return trMstr
    }


    private TransInvJava prepareTransactionJava( InvTrRequest pRequest ) {
      TipoTransInvJava trType = TipoTransInvQuery.buscaTipoTransInvPorIdTipo( pRequest.trType )
      SucursalesJava site = sitesJava.obtenSucursalActual()
      TransInvJava trMstr = null
      if ( ( trType != null ) && ( site != null ) ) {
        trMstr = new TransInvJava()
        trMstr.fecha = DateUtils.truncate( pRequest.effDate, Calendar.DATE )
        if( trType.idTipoTrans.equalsIgnoreCase("ENTRADA_TIENDA")){
          trMstr.idSucursalDestino = pRequest.siteFrom
          trMstr.idSucursal = pRequest.siteTo
        } else {
          trMstr.idSucursalDestino = pRequest.siteTo
        }
        trMstr.observaciones = pRequest.remarks
        trMstr.idSucursal = site.idSucursal
        trMstr.idEmpleado = pRequest.idUser
        String aleatoria = claveAleatoria(trMstr.idSucursal, trType.ultimoFolio+1)
        Parametros p = Registry.find( mx.lux.pos.java.TipoParametro.TRANS_INV_TIPO_SALIDA_ALMACEN )
        if (trType.idTipoTrans.equalsIgnoreCase(p.valor)) {
          Integer ultimoFolio = trType.ultimoFolio+1
          String url = Registry.getURL( trType.idTipoTrans);
          String variable = trMstr.idSucursal + '>' + trMstr.idSucursalDestino + '>' +ultimoFolio+ '>' +
                  aleatoria + '>' +trMstr.idEmpleado.trim() + '>'
          for (int i = 0; i < pRequest.skuList.size(); i++) {
            variable += pRequest.skuList[i].sku + ',' + pRequest.skuList[i].qty +'|'
          }
          log.debug( "cadena a enviar: ${variable}" )
          url += String.format( '?arg=%s', URLEncoder.encode( String.format( '%s', variable ), 'UTF-8' ) )
          String response = ''
          try{
            response = url.toURL().text
            response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
          } catch ( Exception e ){
            println e
          }
          println "respuesta: ${response}"
          trMstr.referencia = aleatoria
          insertAcuseTransaction( trMstr, variable, response, trType.idTipoTrans )
        } else {
          trMstr.referencia = pRequest.reference
        }
        Integer iDet = 0
        Collections.sort( pRequest.skuList, new Comparator<InvTrDetRequest>() {
          @Override
          int compare(InvTrDetRequest o1, InvTrDetRequest o2) {
            return o1.sku.compareTo(o2.sku)
          }
        } )
        for ( InvTrDetRequest detReq in pRequest.skuList ) {
          if ( partsJava.esInventariable( detReq.sku ) ) {
            if( trMstr.trDet.size() <= 0 ){
              TransInvDetJava det = new TransInvDetJava()
              det.linea = ++iDet
              det.sku = detReq.sku
              det.cantidad = detReq.qty
              det.tipoMov = trType.tipoMovObj.codigo
              trMstr.add( det )
            } else if( trMstr.trDet.last().sku != detReq.sku ){
              TransInvDetJava det = new TransInvDetJava()
              det.linea = ++iDet
              det.sku = detReq.sku
              det.cantidad = detReq.qty
              det.tipoMov = trType.tipoMovObj.codigo
              trMstr.add( det )
            } else {
              trMstr.trDet.last().cantidad = trMstr.trDet.last().cantidad+detReq.qty
            }
          }
        }
        trMstr.idTipoTrans = trType.idTipoTrans
      }
      return trMstr
    }


    private TransInv prepareTransactionLc( LcRequest pRequest ) {
        TipoTransInv trType = inventory.obtenerTipoTransaccion( pRequest.trType )
        Sucursal site = sites.obtenSucursalActual()
        TransInv trMstr = null

        if ( ( trType != null ) && ( site != null ) ) {
            trMstr = new TransInv()
            trMstr.fecha = DateUtils.truncate( pRequest.effDate, Calendar.DATE )
            if( trType.idTipoTrans.equalsIgnoreCase("ENTRADA_TIENDA")){
                trMstr.sucursalDestino = pRequest.siteFrom
                trMstr.sucursal = pRequest.siteTo
            } else {
                trMstr.sucursalDestino = pRequest.siteTo
            }
            trMstr.observaciones = pRequest.remarks
            trMstr.sucursal = site.id
            trMstr.idEmpleado = pRequest.idUser

            String aleatoria = claveAleatoria(trMstr.sucursal, trType.ultimoFolio+1)
            Parametro p = Registry.find( TipoParametro.TRANS_INV_TIPO_SALIDA_ALMACEN )
            if (trType.idTipoTrans.equalsIgnoreCase(p.valor)) {
                Integer ultimoFolio = trType.ultimoFolio+1
                String url = Registry.getURL( trType.idTipoTrans);
                String variable = trMstr.sucursal + '>' + trMstr.sucursalDestino + '>' +
                        ultimoFolio+ '>' +
                        aleatoria + '>' +
                        trMstr.idEmpleado.trim() + '>'

                for (int i = 0; i < pRequest.skuList.size(); i++) {
                    variable += pRequest.skuList[i].sku + ',' + pRequest.skuList[i].qty +'|'
                }
                log.debug( "cadena a enviar: ${variable}" )
                url += String.format( '?arg=%s', URLEncoder.encode( String.format( '%s', variable ), 'UTF-8' ) )
                String response = ''
                try{
                    response = url.toURL().text
                    response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
                } catch ( Exception e ){
                    println e
                }
                println "respuesta: ${response}"
                trMstr.referencia = aleatoria
                insertAcuseTransaction( trMstr, variable, response, trType.idTipoTrans )
            } else {
                trMstr.referencia = pRequest.reference
            }


            Integer iDet = 0
            Collections.sort( pRequest.skuList, new Comparator<InvTrDetRequest>() {
                @Override
                int compare(InvTrDetRequest o1, InvTrDetRequest o2) {
                    return o1.sku.compareTo(o2.sku)
                }
            } )
            for ( InvTrDetRequest detReq in pRequest.skuList ) {
                if ( parts.esInventariable( detReq.sku ) ) {
                    if( trMstr.trDet.size() <= 0 ){
                        TransInvDetalle det = new TransInvDetalle()
                        det.linea = ++iDet
                        det.sku = detReq.sku
                        det.cantidad = detReq.qty
                        det.tipoMov = trType.tipoMovObj.codigo
                        trMstr.add( det )
                    } else if( trMstr.trDet.last().sku != detReq.sku ){
                        TransInvDetalle det = new TransInvDetalle()
                        det.linea = ++iDet
                        det.sku = detReq.sku
                        det.cantidad = detReq.qty
                        det.tipoMov = trType.tipoMovObj.codigo
                        trMstr.add( det )
                    } else {
                        trMstr.trDet.last().cantidad = trMstr.trDet.last().cantidad+detReq.qty
                    }
                }
            }
            trMstr.idTipoTrans = trType.idTipoTrans
        }
        return trMstr
    }


  private verifyRequest( InvTrRequest pRequest ) {
    boolean valid = true
    // SiteTo
    if ( valid && pRequest.siteTo )
      valid = sitesJava.validarSucursal( pRequest.siteTo )
    // Part
    if ( valid ) {
      for ( part in pRequest.skuList ) {
        if ( valid )
          valid = partsJava.validarArticulo( part.sku )
      }
    }

    return valid
  }

    private verifyRequestLc( LcRequest pRequest ) {
        boolean valid = true
        // SiteTo
        if ( valid && pRequest.siteTo )
            valid = sites.validarSucursal( pRequest.siteTo )
        // Part
        if ( valid ) {
            for ( part in pRequest.skuList ) {
                if ( valid )
                    valid = parts.validarArticulo( part.sku )
            }
        }
        return valid
    }

  // Public methods
  TransInv prepareRequest( InvTrRequest pRequest ) {
    TransInv tr = null
    if ( verifyRequest( pRequest ) ) {
      tr = prepareTransaction( pRequest )
    }
    return tr
  }


  TransInvJava prepareRequestJava( InvTrRequest pRequest ) {
    TransInvJava tr = null
    if ( verifyRequest( pRequest ) ) {
      tr = prepareTransactionJava( pRequest )
    }
    return tr
  }

  TransInv prepareRequestLc( LcRequest pRequest ) {
    TransInv tr = null
    if ( verifyRequestLc( pRequest ) ) {
      tr = prepareTransactionLc( pRequest )
    }
    return tr
  }

  InvTrRequest requestSalesIssue( NotaVenta pNotaVenta ) {
    InvTrRequest request = new InvTrRequest()

    request.trType = TR_TYPE_ISSUE_SALES
    String trType = parameters.findOne( TipoParametro.TRANS_INV_TIPO_VENTA.value )?.valor
      println('Trans: ' + trType)
    if ( StringUtils.trimToNull( trType ) != null ) {
      request.trType = trType
    }

    request.effDate = pNotaVenta.fechaMod
    request.idUser = pNotaVenta.idEmpleado
    request.reference = pNotaVenta.id

    Boolean salidaVentaSP = Registry.validSPToStore
    for ( DetalleNotaVenta det in pNotaVenta.detalles ) {
      Boolean validaSurte = true
      if( salidaVentaSP ){
        validaSurte = true
      } else {
        validaSurte = StringUtils.trimToEmpty(det?.surte).equals(TAG_SURTE_SUCURSAL)
      }
      if ( parts.validarArticulo( det.idArticulo ) && validaSurte ) {
        request.skuList.add( new InvTrDetRequest( det.idArticulo, det.cantidadFac.intValue() ) )
      }
    }
    return request
  }


  static InvTrRequest requestSalesIssue( NotaVentaJava pNotaVenta ) {
    InvTrRequest request = new InvTrRequest()
    request.trType = TR_TYPE_ISSUE_SALES
    String trType = ParametrosQuery.BuscaParametroPorId( mx.lux.pos.java.TipoParametro.TRANS_INV_TIPO_VENTA.valor )?.valor
    println('Trans: ' + trType)
    if ( StringUtils.trimToNull( trType ) != null ) {
      request.trType = trType
    }
    request.effDate = pNotaVenta.fechaMod
    request.idUser = StringUtils.trimToEmpty(pNotaVenta.idEmpleado)
    request.reference = StringUtils.trimToEmpty(pNotaVenta.idFactura)
    Boolean salidaVentaSP = Registry.validSPToStore
    for ( DetalleNotaVentaJava det in pNotaVenta.detalles ) {
      Boolean validaSurte = true
      if( salidaVentaSP ){
        validaSurte = true
      } else {
        validaSurte = StringUtils.trimToEmpty(det?.surte).equals(TAG_SURTE_SUCURSAL)
      }
      if ( partsJava.validarArticulo( det.idArticulo ) && validaSurte ) {
        request.skuList.add( new InvTrDetRequest( det.idArticulo, det.cantidadFac.intValue() ) )
      }
    }
    return request
  }


  InvTrRequest requestEnterSP( NotaVenta pNotaVenta ) {
    InvTrRequest request = new InvTrRequest()

    request.trType = TR_TYPE_ENTER_SP
    String trType = parameters.findOne( TipoParametro.TRANS_INV_TIPO_ENTRADA_SP.value )?.valor
    println('Trans: ' + trType)
    if ( StringUtils.trimToNull( trType ) != null ) {
      request.trType = trType
    }

    request.effDate = pNotaVenta.fechaMod
    request.idUser = pNotaVenta.idEmpleado
    request.reference = pNotaVenta.id

    for ( DetalleNotaVenta det in pNotaVenta.detalles ) {
      if ( parts.validarArticulo( det.idArticulo ) && StringUtils.trimToEmpty(det.surte).equalsIgnoreCase("P") ) {
        request.skuList.add( new InvTrDetRequest( det.idArticulo, det.cantidadFac.intValue() ) )
      }
    }
    return request
  }


  InvTrRequest requestEnterSP( NotaVentaJava pNotaVenta ) {
    InvTrRequest request = new InvTrRequest()
    request.trType = TR_TYPE_ENTER_SP
    String trType = ParametrosQuery.BuscaParametroPorId( TipoParametro.TRANS_INV_TIPO_ENTRADA_SP.value )?.valor
    println('Trans: ' + trType)
    if ( StringUtils.trimToNull( trType ) != null ) {
      request.trType = trType
    }
    request.effDate = pNotaVenta.fechaMod
    request.idUser = pNotaVenta.idEmpleado
    request.reference = pNotaVenta.idFactura
    for ( DetalleNotaVentaJava det in pNotaVenta.detalles ) {
      if ( partsJava.validarArticulo( det.idArticulo ) && StringUtils.trimToEmpty(det.surte).equalsIgnoreCase("P") ) {
        request.skuList.add( new InvTrDetRequest( det.idArticulo, det.cantidadFac.intValue() ) )
      }
    }
    return request
  }


  InvTrRequest requestReturnReceipt( NotaVenta pNotaVenta, Boolean fromFile ) {
    InvTrRequest request = new InvTrRequest()

    request.trType = TR_TYPE_RECEIPT_RETURN
    String trType = parameters.findOne( TipoParametro.TRANS_INV_TIPO_CANCELACION.value )?.valor
    if ( StringUtils.trimToNull( trType ) != null ) {
      request.trType = trType
    }

    request.effDate = pNotaVenta.fechaMod
    request.idUser = pNotaVenta.idEmpleado
    request.reference = pNotaVenta.id

    for ( DetalleNotaVenta det in pNotaVenta.detalles ) {
      if ( parts.validarSP( StringUtils.trimToEmpty(det.surte), StringUtils.trimToEmpty(pNotaVenta.factura), fromFile, StringUtils.trimToEmpty(det.articulo.idGenerico) ) ) {
        if ( parts.validarArticulo( det.idArticulo ) ) {
          request.skuList.add( new InvTrDetRequest( det.idArticulo, det.cantidadFac.intValue() ) )
        }
      }
    }
    return request
  }


    InvTrRequest requestReturnReceiptSP( NotaVenta pNotaVenta ) {
      InvTrRequest request = new InvTrRequest()

      request.trType = TR_TYPE_RECEIPT_RETURN_SP
      String trType = parameters.findOne( TipoParametro.TRANS_INV_TIPO_SALIDA.value )?.valor
      if ( StringUtils.trimToNull( trType ) != null ) {
        request.trType = trType
      }

      request.effDate = pNotaVenta.fechaMod
      request.idUser = pNotaVenta.idEmpleado
      request.reference = pNotaVenta.id
      request.remarks = "Salida Surte Pino"

      for ( DetalleNotaVenta det in pNotaVenta.detalles ) {
        if ( parts.validarArticulo( det.idArticulo ) && StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) &&
                StringUtils.trimToEmpty(det.surte).equalsIgnoreCase("P") ) {
          request.skuList.add( new InvTrDetRequest( det.idArticulo, det.cantidadFac.intValue() ) )
        }
      }
      return request
    }


    protected  String claveAleatoria(Integer sucursal, Integer folio) {
        String folioAux = "" + folio.intValue();
        String sucursalAux = "" + sucursal.intValue()
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        if (folioAux.size() < 4) {
            folioAux = folioAux?.padLeft( 4, '0' )
        }
        else {
            folioAux = folioAux.substring(0,4);
        }
        String resultado = sucursalAux?.padLeft( 3, '0' ) + folioAux


        for (int i = 0; i < resultado.size(); i++) {
            int numAleatorio = (int) (Math.random() * abc.size());
            if (resultado.charAt(i) == '0') {
                resultado = replaceCharAt(resultado, i, abc.charAt(numAleatorio))
            }
            else {
                int numero = Integer.parseInt ("" + resultado.charAt(i));
                numero = 10 - numero
                char diff = Character.forDigit(numero, 10);
                resultado = replaceCharAt(resultado, i, diff)
            }


        }
        return resultado;
    }

    protected static String replaceCharAt(String s, int pos, char c) {
        StringBuffer buf = new StringBuffer( s );
        buf.setCharAt( pos, c );
        return buf.toString( );
    }


    private void insertAcuseTransaction( TransInv transInv, String contenido, String folio, String tipoTransaccion ){
        log.debug( "Insertando Acuse" )
        Acuse acuse = new Acuse()
        acuse.idTipo = tipoTransaccion
        acuse.folio = StringUtils.trimToEmpty(folio).isNumber() ? StringUtils.trimToEmpty(folio) : ""
        if( StringUtils.trimToEmpty(folio) != '' ){
            acuse.fechaAcuso = transInv.fecha
        }
        acuse.intentos = 0
        acuse.contenido = contenido
        acuse.fechaCarga = new Date()
        acuseRepository.save( acuse )
    }


    private void insertAcuseTransaction( TransInvJava transInv, String contenido, String folio, String tipoTransaccion ){
      log.debug( "Insertando Acuse" )
      AcusesJava acuse = new AcusesJava()
      acuse.idTipo = tipoTransaccion
      acuse.folio = StringUtils.trimToEmpty(folio).isNumber() ? StringUtils.trimToEmpty(folio) : ""
      if( StringUtils.trimToEmpty(folio) != '' ){
        acuse.fechaAcuso = transInv.fecha
      }
      acuse.intentos = 0
      acuse.contenido = contenido
      acuse.fechaCarga = new Date()
      AcusesQuery.saveAcuses( acuse )
    }
}