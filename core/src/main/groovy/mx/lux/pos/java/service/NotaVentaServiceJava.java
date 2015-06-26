package mx.lux.pos.java.service;


import mx.lux.pos.java.TipoParametro;
import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.*;
import mx.lux.pos.model.JbTrack;
import mx.lux.pos.service.business.Registry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class NotaVentaServiceJava {

    private static final String TAG_TIPO_NOTA_VENTA = String.valueOf('F');
    private static final String TAG_GENERICOS_LENTECONTACTO2 = "H";
    private static final String TAG_GEN_TIPO_NC = "NC";
    private static final String TAG_GEN_TIPO_C = "C";
    private static final String TAG_GENERICOS_B = "B";
    private static final String TAG_ARTICULO_COLOR = "COG";
    private static final String TAG_GENERICOS_H = "H";
    private static final String TAG_GENERICOS_INVENTARIABLES = "A,E,H";
    private static final String TAG_TRANSFERENCIA = "TR";
    static final Logger log = LoggerFactory.getLogger(NotaVentaQuery.class);


    public NotaVentaJava abrirNotaVenta(String clienteID, String empleadoID ) throws ParseException {
      log.info( "abriendo nueva notaVenta" ) ;
      Parametros parametro = new Parametros();
      parametro.setValor(clienteID);
      NotaVentaJava notaVenta = new NotaVentaJava();
      notaVenta.setIdFactura(NotaVentaQuery.getNotaVentaSequence());
      notaVenta.setIdSucursal(SucursalesQuery.getCurrentSucursalId());
      notaVenta.setIdCliente(StringUtils.isNumeric(parametro.getValor()) ? NumberFormat.getInstance().parse(parametro.getValor()).intValue() : null);
      notaVenta.setIdEmpleado(empleadoID);

      try {
        notaVenta = notaVenta.trim();
        notaVenta = NotaVentaQuery.saveNotaVenta( notaVenta );
        log.info( "notaVenta registrada id: "+notaVenta.getIdFactura() );
        return notaVenta;
      } catch ( Exception ex ) {
        log.error( "problema al registrar notaVenta: ${notaVenta?.dump()}", ex );
      }
      return null;
    }


    public static NotaVentaJava registrarNotaVenta(NotaVentaJava notaVenta) throws ParseException {
        log.info( "registrando notaVenta id: ${notaVenta?.id}," );
        log.info( "fechaHoraFactura: ${notaVenta?.fechaHoraFactura?.format( DATE_TIME_FORMAT )}" );
        if ( StringUtils.isNotBlank( notaVenta.getIdFactura() ) ) {
            String idNotaVenta = notaVenta.getIdFactura();
            if ( NotaVentaQuery.busquedaNotaById(idNotaVenta) != null ) {
                notaVenta.setIdSucursal(Utilities.toInteger(ParametrosQuery.BuscaParametroPorId(TipoParametro.ID_SUCURSAL.getValor()).getValor()));
                BigDecimal total = BigDecimal.ZERO;
                List<DetalleNotaVentaJava> detalles = DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFactura(idNotaVenta);
                for(DetalleNotaVentaJava detalleNotaVenta : detalles){
                    BigDecimal precio = detalleNotaVenta.getPrecioUnitFinal();
                    Integer cantidad = detalleNotaVenta.getCantidadFac().intValue();
                    BigDecimal subtotal = precio.multiply( new BigDecimal(cantidad) );
                    total = total.add( subtotal );
                }
                BigDecimal pagado = BigDecimal.ZERO;
                List<PagoJava> pagos = PagoQuery.busquedaPagosPorIdFactura(idNotaVenta);
                for(PagoJava pago : pagos){
                    BigDecimal monto = pago.getMontoPago();
                    pagado = pagado.add( monto );
                }
                log.debug( "ventaNeta: "+total );
                log.debug( "ventaTotal: "+total );
                log.debug( "sumaPagos: "+pagado );
                BigDecimal diferencia = notaVenta.getVentaNeta().subtract(total);
                if( ((notaVenta.getVentaNeta().subtract(total)).compareTo(new BigDecimal(0.05)) < 0) &&
                        (notaVenta.getVentaNeta().subtract(total).compareTo(new BigDecimal(-0.05)) > 0) ){
                    log.debug( "redondeo monto total" );
                    DetalleNotaVentaJava det = null;
                    for(DetalleNotaVentaJava detalleNotaVenta : detalles){
                        ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(detalleNotaVenta.getIdArticulo());
                        if( !StringUtils.trimToEmpty(articulo.getIdGenerico()).equalsIgnoreCase("J") ){
                            det = detalleNotaVenta;
                        }
                    }
                    if( detalles.size() > 0 && det != null ){
                        BigDecimal monto = det.getPrecioUnitFinal().add(diferencia);
                        if( diferencia.compareTo(BigDecimal.ZERO) > 0 || diferencia.compareTo(BigDecimal.ZERO) < 0 ){
                            det.setPrecioUnitFinal( monto );
                            det.setPrecioFactura( monto );
                            DetalleNotaVentaQuery.updateDetalleNotaVenta(det);
                        }
                    }
                } else {
                    notaVenta.setVentaNeta(total);
                    notaVenta.setVentaTotal(total);
                }

                notaVenta.setSumaPagos(pagado);
                notaVenta.setTipoNotaVenta(TAG_TIPO_NOTA_VENTA);
                try {
                    notaVenta = NotaVentaQuery.updateNotaVenta(notaVenta);
                    log.info( "notaVenta registrada id: ${notaVenta?.id}" );
                } catch ( Exception ex ) {
                    log.error( "problema al registrar notaVenta: ${notaVenta?.dump()}", ex );
                }
            } else {
                log.warn( "no se registra notaVenta, id no existe" );
            }
        } else {
            log.warn( "no se registra notaVenta, parametros invalidos" );
        }
        return notaVenta;
    }


    public EmpleadoJava obtenerEmpleadoDeNotaVenta(String pOrderId) throws ParseException {
      EmpleadoJava employee = null;
      if ( StringUtils.trimToNull( pOrderId ) != null ) {
        NotaVentaJava order = NotaVentaQuery.busquedaNotaById(StringUtils.trimToEmpty(pOrderId));
        if ( ( order != null ) && ( StringUtils.trimToNull( order.getIdEmpleado() ) != null ) ) {
          employee = EmpleadoQuery.buscaEmpPorIdEmpleado(StringUtils.trimToEmpty(order.getIdEmpleado()));
        }
      }
      return employee;
    }


  public List<ArticulosJava> validaLentesContacto( String idFactura ) throws ParseException {
    List<ArticulosJava> articulo = new ArrayList<ArticulosJava>();
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById( StringUtils.trimToEmpty(idFactura) );
    List<ModeloLcJava> modelosLc = ModeloLcQuery.buscaTodoModeloLc();
    for(DetalleNotaVentaJava det : nota.getDetalles()){
      if( StringUtils.trimToEmpty(det.getArticulo().getIdGenerico()).equalsIgnoreCase(TAG_GENERICOS_LENTECONTACTO2) ){
        if( StringUtils.trimToEmpty(det.getArticulo().getIdGenTipo()).equalsIgnoreCase(TAG_GEN_TIPO_C) ){
          if(StringUtils.trimToEmpty(det.getIdRepVenta()).length() <= 0 ){
            articulo.add(det.getArticulo());
          }
          String[] lotes = StringUtils.trimToEmpty(det.getIdRepVenta()).split(",");
          if( lotes.length < det.getCantidadFac() ){
            Integer faltantes = det.getCantidadFac().intValue()-lotes.length;
            for(int i=0; i < faltantes; i++){
              articulo.add(det.getArticulo());
            }
          }
        } else if( StringUtils.trimToEmpty(det.getArticulo().getIdGenTipo()).equalsIgnoreCase(TAG_GEN_TIPO_NC) ){
          for(ModeloLcJava mod : modelosLc){
            if( StringUtils.trimToEmpty(det.getArticulo().getArticulo()).equalsIgnoreCase(StringUtils.trimToEmpty(mod.getIdModelo())) ){
              PedidoLcJava pedidoLc = PedidoLcQuery.buscaPedidoLcPorId( idFactura );
              List<PedidoLcDetJava> pedidoDet = PedidoLcQuery.buscaPedidoLcDetPorIdYModelo(det.getIdFactura(), det.getArticulo().getArticulo());
              if( pedidoLc != null ){
                if(pedidoDet.size() <= 0){
                  articulo.add(det.getArticulo());
                }
              } else {
                articulo.add(det.getArticulo());
              }
            }
          }
        }
      }
    }
    return articulo;
  }



  public Boolean validaLentes( String idFactura ) throws ParseException {
    Boolean hasLente = false;
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(StringUtils.trimToEmpty(idFactura));
    for(DetalleNotaVentaJava det : nota.getDetalles()){
      if( det.getArticulo().getIndiceDioptra() != null && !StringUtils.trimToEmpty(det.getArticulo().getIndiceDioptra()).equalsIgnoreCase("")){
        hasLente = true;
      }
    }
    return hasLente;
  }



  public Boolean existePromoEnOrden( String idFactura, Integer idPromo ){
    Boolean existPromo = false;
    List<OrdenPromDetJava> lstOrdenPromDet = OrdenPromDetQuery.BuscaOrdenPromDetPorIdFactura(idFactura);
    if( lstOrdenPromDet.size() > 0 ){
      if(idPromo.equals(lstOrdenPromDet.get(0).getIdProm())){
        existPromo = true;
      }
    }
    return existPromo;
  }



  public NotaVentaJava eliminarDetalleNotaVentaEnNotaVenta( String idNotaVenta, Integer idArticulo ) throws ParseException {
    log.info( String.format("eliminando detalleNotaVenta idArticulo: %d de notaVenta id: %s", idArticulo, idNotaVenta) );
    if ( idArticulo != null && StringUtils.isNotBlank( idNotaVenta ) ) {
      DetalleNotaVentaJava detalle = DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFacturaEIdArticulo(idNotaVenta, idArticulo);
      if ( detalle.getId() != null ) {
        log.debug( String.format("obtiene detalleNotaVenta id: %d", detalle.getId()) );
        NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById( idNotaVenta );
        if ( StringUtils.isNotBlank( notaVenta.getIdFactura() ) ) {
          DetalleNotaVentaQuery.eliminaDetalleNotaVenta( detalle );
          log.debug( "detalleNotaVenta eliminado" );
          return registrarNotaVenta( notaVenta );
        } else {
          log.warn( String.format("no se elimina detalleNotaVenta, no existe notaVenta id: %s", idNotaVenta) );
        }
      } else {
        log.warn( String.format("no se elimina detalleNotaVenta, no existe con idNotaVenta: %s idArticulo: %d", idNotaVenta, idArticulo) );
      }
    } else {
      log.warn( "no se elimina detalleNotaVenta, parametros invalidos" );
    }
    return null;
  }


  public void removePedidoLc( String orderId, Integer idArticulo ) throws ParseException {
    ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(idArticulo);
    List<PedidoLcDetJava> pedidoLcDet = PedidoLcQuery.buscaPedidoLcDetPorIdYModelo(orderId, articulo.getArticulo());
    for(PedidoLcDetJava det : pedidoLcDet){
      PedidoLcQuery.eliminaPedidoLcDet( det );
    }
    PedidoLcJava pedidoLc = PedidoLcQuery.buscaPedidoLcPorId(orderId);
    if(pedidoLc != null && pedidoLc.getPedidoLcDets().size() <= 0){
      PedidoLcQuery.eliminaPedidoLc( pedidoLc );
    }
  }



  public void saveBatch( String idFactura, Integer idArticulo, String lote ) throws ParseException {
    DetalleNotaVentaJava detalleNota = DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFacturaEIdArticulo(idFactura, idArticulo);
    if( detalleNota != null ){
      detalleNota.setIdRepVenta(StringUtils.trimToEmpty(detalleNota.getIdRepVenta()) + "," + StringUtils.trimToEmpty(lote));
      if( detalleNota.getIdRepVenta().startsWith(",") ){
        detalleNota.setIdRepVenta(detalleNota.getIdRepVenta().replaceFirst( ",","" ));
      }
      DetalleNotaVentaQuery.updateDetalleNotaVenta(detalleNota);
    }
  }


  public NotaVentaJava saveFrame(String idNotaVenta, String opciones, String forma) throws ParseException {
    NotaVentaJava rNotaVenta = NotaVentaQuery.busquedaNotaById(idNotaVenta);
    rNotaVenta.setUdf2(opciones);
    rNotaVenta.setUdf3(forma);
    try{
      rNotaVenta =  NotaVentaQuery.updateNotaVenta( rNotaVenta );
    } catch ( Exception e ){
      System.out.println(e);
    }
    return rNotaVenta;
  }


  public void saveProDate(NotaVentaJava rNotaVenta, Date fechaPrometida) throws ParseException {
    if ( StringUtils.isNotBlank( rNotaVenta.getIdFactura()) ) {
      if ( NotaVentaQuery.exists( rNotaVenta.getIdFactura() ) ) {
        rNotaVenta.setFechaPrometida(fechaPrometida);
        registrarNotaVenta( rNotaVenta );
      } else {
        log.warn( "id no existe" );
      }
    } else {
      log.warn( "No hay receta" );
    }
  }


  public Boolean validaSoloInventariables( String idFactura ) throws ParseException {
    log.debug( "validaSoloInventariables( )" );
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(idFactura);
    Boolean esInventariable = true;
    for(DetalleNotaVentaJava det : nota.getDetalles()){
      if( TAG_GENERICOS_B.contains(det.getArticulo().getIdGenerico().trim()) ){
        esInventariable = false;
      }
    }
    return esInventariable;
  }


  public NotaVentaJava cerrarNotaVenta( NotaVentaJava notaVenta ) throws ParseException {
    log.info( String.format("cerrando notaVenta id: %s", notaVenta.getIdFactura()) );
    if ( StringUtils.isNotBlank( notaVenta.getIdFactura() ) ) {
      String idNotaVenta = notaVenta.getIdFactura();
      if ( NotaVentaQuery.exists( idNotaVenta ) ) {
        Boolean agregarColor = false;
        for(DetalleNotaVentaJava det : notaVenta.getDetalles()){
          if( StringUtils.trimToEmpty(det.getArticulo().getArticulo()).equalsIgnoreCase(TAG_ARTICULO_COLOR) ){
            agregarColor = true;
          }
        }
        if( agregarColor && StringUtils.trimToEmpty(notaVenta.getCodigoLente()).length() > 0 ){
          String dioptra = notaVenta.getCodigoLente();
          String dioptraTmp = dioptra.substring( 0, dioptra.length()-1 );
          dioptra = dioptraTmp+"T";
          ArticulosServiceJava articulosServiceJava = new ArticulosServiceJava();
          if( articulosServiceJava.validaCodigoDioptra( StringUtils.trimToEmpty(dioptra) ) ){
            notaVenta.setCodigoLente(dioptra);
          }
          articulosServiceJava = null;
        }
        Date fecha = new Date();
        String factura = StringUtils.trimToEmpty( notaVenta.getFactura() );
        if( factura.length() <= 0 ){
          factura = String.format( "%06d", NotaVentaQuery.getFacturaSequence() );
        }
        notaVenta.setFactura(factura);
        notaVenta.setTipoNotaVenta("F");
        notaVenta.setTipoDescuento("N");
        notaVenta.setTipoEntrega("S");
        notaVenta.setfExpideFactura( true );
        notaVenta.setFechaPrometida(notaVenta.getFechaPrometida() != null ? notaVenta.getFechaPrometida() : fecha);
        return registrarNotaVenta( notaVenta );
      } else {
        log.warn( "no se cierra notaVenta, id no existe" );
      }
    } else {
      log.warn( "no se cierra notaVenta, parametros invalidos" );
    }
    return null;
  }



  public NotaVentaJava buscarNotaInicial( Integer idCliente, String idFactura ) throws ParseException {
    List<NotaVentaJava> nota = null;
    Date fechaStart = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
    Date fechaEnd = new Date( DateUtils.ceiling( new Date(), Calendar.DAY_OF_MONTH ).getTime() - 1 );
    nota = NotaVentaQuery.busquedaNotasHoyPorIdClienteAndIdFacturaEmpty( idCliente, idFactura, fechaStart, fechaEnd);
    if( nota.size() > 0 ){
      return  nota.get(nota.size()-1);
    } else {
      return null;
    }
  }


  public void saveOrder( NotaVentaJava pNotaVenta ) throws ParseException {
    if ( pNotaVenta != null ) {
      NotaVentaQuery.updateNotaVenta(pNotaVenta);
    }
  }



  public List<NotaVentaJava> obtenerNotaVentaPorClienteFF( Integer idCliente ) throws ParseException {
    log.debug( "obtenerNotaVentaPorCliente(  )" );
    Date fechaStart = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH );
    Date fechaEnd = new Date( DateUtils.ceiling( new Date(), Calendar.DAY_OF_MONTH ).getTime() - 1 );
    List<NotaVentaJava> notasTmp = NotaVentaQuery.busquedaNotasHoyPorIdCliente( idCliente, fechaStart, fechaEnd );
    return notasTmp;
  }



  public List<CuponMvJava> obtenerCuponMvFacturaOriFF( String factura ){
    return CuponMvQuery.buscaCuponMvPorFacturaOrigen(factura);
  }


  public List<CuponMvJava> obtenerCuponMvFacturaDest( String factura ){
    return CuponMvQuery.buscaCuponMvPorFacturaDestino( factura );
  }



  public CuponMvJava actualizarCuponMv( String idFacturaOrigen, String idFacturaDestino, BigDecimal montoCupon, Integer numeroCupon, Boolean ffCupon ) throws ParseException {
    CuponMvJava cuponMv = CuponMvQuery.buscaCuponMvPorFacturaDestinoAndFacturaOrigen( idFacturaDestino, idFacturaOrigen);
    if( cuponMv != null && StringUtils.trimToEmpty(idFacturaDestino).length() > 0 ){
      CuponMvQuery.deleteCuponMv(cuponMv.getClaveDescuento());
      NotaVentaJava notaOrigen = NotaVentaQuery.busquedaNotaById(idFacturaOrigen);
      if( notaOrigen == null ){
        NotaVentaJava n = NotaVentaQuery.busquedaNotaByFactura( idFacturaOrigen );
        List<NotaVentaJava> lstNotas = new ArrayList<NotaVentaJava>();
        if( n != null ){
          lstNotas.add(n);
        }
        if( lstNotas.size() > 0 ){
          notaOrigen = lstNotas.get(0);
        }
      }
      NotaVentaJava notaDestino = NotaVentaQuery.busquedaNotaById(idFacturaDestino);
      Integer factura = 0;
      try{
        factura = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(notaOrigen.getFactura())).intValue();
      } catch ( ParseException e ){ System.out.println(e); }
      String clave = StringUtils.trimToEmpty(cuponMv.getClaveDescuento()).length() > 0 ?
                    StringUtils.trimToEmpty(cuponMv.getClaveDescuento()) :
                    claveAleatoria( StringUtils.trimToEmpty(factura.toString()), StringUtils.trimToEmpty(numeroCupon.toString()) );
      String facturaDestino = StringUtils.trimToEmpty(notaDestino != null ? notaDestino.getFactura() : "");
      Date fechaAplicacion = cuponMv.getFechaAplicacion() != null ? cuponMv.getFechaAplicacion() : new Date();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      calendar.add(Calendar.DAY_OF_YEAR, Registry.getDiasVigenciaCupon());
      Date fechaVigencia = cuponMv.getFechaVigencia() != null ? cuponMv.getFechaVigencia() : calendar.getTime();
      Boolean hasLc = false;
      for( DetalleNotaVentaJava det : notaOrigen.getDetalles() ){
        if( StringUtils.trimToEmpty(det.getArticulo().getIdGenerico()).equalsIgnoreCase(TAG_GENERICOS_H) ){
          hasLc = true;
        }
      }
      if( hasLc ){
        clave = clave.replaceFirst(String.valueOf(clave.charAt(0)),"H");
      }
      CuponMvQuery.insertClave( clave, notaOrigen.getFactura(), facturaDestino, fechaAplicacion, fechaVigencia );
      cuponMv = CuponMvQuery.buscaCuponMvPorClave(clave);
      cuponMv.setMontoCupon(montoCupon);
      CuponMvQuery.updateCuponMv(cuponMv);
      DescuentosJava descuento = DescuentosQuery.buscaDescuentosPorIdFacturaAndClaveVacia(notaDestino.getIdFactura());
      if( descuento != null ){
        descuento.setClave(cuponMv.getClaveDescuento());
        descuento.setTipoClave(cuponMv.getClaveDescuento());
        DescuentosQuery.saveOrUpdateDescuentos(descuento);
      }
    } else {
      NotaVentaJava notaOrigen = NotaVentaQuery.busquedaNotaById(idFacturaOrigen);
      Boolean hasLc = false;
      if( notaOrigen == null ){
        NotaVentaJava n = NotaVentaQuery.busquedaNotaByFactura( idFacturaOrigen );
        List<NotaVentaJava> lstNotas = new ArrayList<NotaVentaJava>();
        if( n != null ){
          lstNotas.add(n);
        }
        if( lstNotas.size() > 0 ){
          notaOrigen = lstNotas.get(0);
        }
      } else {
        for( DetalleNotaVentaJava det : notaOrigen.getDetalles() ){
          if( StringUtils.trimToEmpty(det.getArticulo().getIdGenerico()).equalsIgnoreCase(TAG_GENERICOS_H) ){
            hasLc = true;
          }
        }
      }
      Integer factura = 0;
      try{
        factura = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(notaOrigen.getFactura())).intValue();
      } catch ( ParseException e ){ System.out.println(e); }
      String clave = claveAleatoria( StringUtils.trimToEmpty(factura.toString()), StringUtils.trimToEmpty(numeroCupon.toString()) );
      if( ffCupon ){
        clave = clave.replaceFirst(String.valueOf(clave.charAt(0)),"F");
      } else if( hasLc ){
        clave = clave.replaceFirst(String.valueOf(clave.charAt(0)),"H");
      }
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      calendar.add(Calendar.DAY_OF_YEAR, ffCupon ? Registry.getDiasVigenciaCuponFF() : Registry.getDiasVigenciaCupon());
      Date fechaVigencia = calendar.getTime();
      cuponMv = new CuponMvJava();
      cuponMv.setClaveDescuento(clave);
      cuponMv.setFacturaOrigen(StringUtils.trimToEmpty(notaOrigen.getFactura()));
      cuponMv.setFacturaDestino("");
      cuponMv.setFechaAplicacion(null);
      cuponMv.setFechaVigencia(fechaVigencia);
      cuponMv.setMontoCupon(montoCupon);
      CuponMvQuery.insertCuponMv( cuponMv );
    }
    return cuponMv;
  }



  static String claveAleatoria(String factura, String numeroPar) {
    String digitos = "" + numeroPar+"0";
    String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String resultado = String.format("%01d", Integer.parseInt(digitos)) + factura;
    for (int i = 0; i < resultado.length(); i++) {
      int numAleatorio = (int) (Math.random() * abc.length());
      if (resultado.charAt(i) == '0') {
        resultado = replaceCharAt(resultado, i, abc.charAt(numAleatorio));
      } else {
        int numero = Integer.parseInt ("" + resultado.charAt(i));
        numero = 10 - numero;
        char diff = Character.forDigit(numero, 10);
        resultado = replaceCharAt(resultado, i, diff);
      }
    }
    return resultado;
  }


  static String replaceCharAt(String s, int pos, char c) {
    StringBuffer buf = new StringBuffer( s );
    buf.setCharAt( pos, c );
    return buf.toString( );
  }



  public void entregaPedidoLc( String idPedido ) throws ParseException {
    PedidoLcJava pedidoLc = PedidoLcQuery.buscaPedidoLcPorId(idPedido);
    if( pedidoLc != null ){
      pedidoLc.setFechaEntrega(new Date());
      PedidoLcQuery.savePedidoLc(pedidoLc);
    }
  }


  Boolean diaActualEstaAbierto(){
    Boolean isOpen = true;
    CierreDiarioJava cierreDiario = CierreDiarioQuery.buscaCierreDiarioPorFecha( new Date() );
    if( cierreDiario != null ){
      if( StringUtils.trimToEmpty(cierreDiario.getEstado()).equalsIgnoreCase("c") ){
        isOpen = false;
      }
    }
    return isOpen;
  }


  SalesWithNoInventory obtenerConfigParaVentasSinInventario( ) {
    SalesWithNoInventory autorizacion = Registry.getConfigForSalesWithNoInventory();
    return autorizacion;
  }



  public NotaVentaJava registrarDetalleNotaVentaEnNotaVenta( String idNotaVenta, DetalleNotaVentaJava detalleNotaVenta ) throws ParseException {
    log.info( "registrando detalleNotaVenta id: "+detalleNotaVenta.getIdFactura()+"idArticulo: "+detalleNotaVenta.getIdArticulo() );
    log.info( "en notaVenta id: "+idNotaVenta );
    NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById( idNotaVenta );
    if ( StringUtils.isNotBlank( notaVenta.getIdFactura() ) && detalleNotaVenta.getIdArticulo() != null ) {
      detalleNotaVenta.setIdFactura(idNotaVenta);
      detalleNotaVenta.setIdSucursal(SucursalesQuery.getCurrentSucursalId());
      DetalleNotaVentaJava tmp = DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFacturaEIdArticulo( idNotaVenta, detalleNotaVenta.getIdArticulo() );
      log.debug( "obtiene detalleNotaVenta existente" );
      if ( tmp != null && tmp.getId() != null ) {
        log.debug( "actualizando detalleNotaVenta con id: "+tmp.getId().toString()+"cantidadFac: "+tmp.getCantidadFac().toString() );
        detalleNotaVenta.setId(tmp.getId());
        detalleNotaVenta.setCantidadFac(detalleNotaVenta.getCantidadFac()+tmp.getCantidadFac());
        detalleNotaVenta.setIdRepVenta(tmp.getIdRepVenta());
        log.debug( "actualizados cantidadFac: "+detalleNotaVenta.getCantidadFac() );
      } else {
        log.debug( "registrando nuevo detalleNotaVenta" );
      }
      detalleNotaVenta = establecerPrecios( detalleNotaVenta );
      try {
        detalleNotaVenta = DetalleNotaVentaQuery.updateDetalleNotaVenta( detalleNotaVenta );
        log.debug( "detalleNotaVenta registrado id: "+detalleNotaVenta.getId() );
        return registrarNotaVenta( notaVenta );
      } catch ( Exception ex ) {
        log.error( "problema al registrar detalleNotaVenta: ${detalleNotaVenta?.dump()}", ex );
      }
    } else {
      log.warn("no se registra detalleNotaVenta, parametros invalidos");
    }
    return null;
  }


  private DetalleNotaVentaJava establecerPrecios( DetalleNotaVentaJava detalle ) throws ParseException {
    log.debug( "estableciendo precios para detalleNotaVenta articulo: "+detalle.getIdArticulo() );
    if ( detalle.getIdArticulo() != null ) {
      ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(detalle.getIdArticulo());
      log.debug( "obtiene articulo id: "+articulo.getIdArticulo()+", codigo: "+articulo.getArticulo()+", color: "+articulo.getColorCode() );
      if ( articulo.getIdArticulo() != null ) {
        List<PreciosJava> precios = PreciosQuery.buscaPreciosPorArticulo( articulo.getArticulo() );
        if ( precios.size() > 0 ) {
          PreciosJava precioLista = new PreciosJava();
          for(PreciosJava tmp : precios){
            if( "L".equalsIgnoreCase( StringUtils.trimToEmpty(tmp.getLista())) ){
              precioLista = tmp;
            }
          }
          log.debug( "precio lista: "+precioLista.getPrecio() );
          BigDecimal lista = precioLista.getPrecio() != null  ? precioLista.getPrecio() : BigDecimal.ZERO;
          PreciosJava precioOferta = new PreciosJava();
          for(PreciosJava tmp : precios){
            if( "O".equalsIgnoreCase( StringUtils.trimToEmpty(tmp.getLista())) ){
                    precioLista = tmp;
            }
          }
          log.debug( "precio oferta: "+precioOferta.getPrecio() );
          BigDecimal oferta = precioOferta.getPrecio() != null ? precioOferta.getPrecio() : BigDecimal.ZERO;
          BigDecimal unitario = oferta.compareTo(BigDecimal.ZERO) > 0 && ( oferta.compareTo(lista) < 0 ) ? oferta : lista;
          detalle.setPrecioCalcLista(lista);
          detalle.setPrecioCalcOferta(oferta);
          detalle.setPrecioUnitLista(unitario);
          detalle.setPrecioUnitFinal(unitario);
          detalle.setPrecioFactura(unitario);
          detalle.setPrecioConv(BigDecimal.ZERO);
          log.debug( "detalleNotaVenta actualizado" );
        } else {
          log.warn( "no se establecen precios, lista de precios vacia" );
        }
      } else {
        log.warn( "no se establecen precios, articulo invalido" );
      }
    } else {
      log.warn( "no se establecen precios, parametros invalidos" );
    }
    return detalle;
  }


  public void registraImpuestoPorFactura( NotaVentaJava notaVenta ) throws ParseException {
    Parametros parametro = ParametrosQuery.BuscaParametroPorId(TipoParametro.IVA_VIGENTE.getValor());
    FacturasImpuestosJava impuesto = new FacturasImpuestosJava();
    impuesto.setIdFactura(notaVenta.getIdFactura());
    impuesto.setIdImpuesto(parametro.getValor());
    impuesto.setIdSucursal(notaVenta.getIdSucursal());
    impuesto.setFecha(new Date());
    impuesto = FacturasImpuestosQuery.updateFacturasImpuestos( impuesto );
    log.debug( "guardando idImpuesto "+impuesto.getIdImpuesto()+"a factura: "+impuesto.getIdFactura() );
  }



  public void eliminarCUponMv( String idFactura ) throws ParseException {
    List<CuponMvJava> cuponesMv = CuponMvQuery.buscaCuponMvPorFacturaDestino(idFactura);
    for( CuponMvJava cuponMv1 : cuponesMv ){
      cuponMv1.setFacturaDestino("");
      cuponMv1.setFechaAplicacion(null);
      CuponMvQuery.updateCuponMv(cuponMv1);
    }
    List<DescuentosJava> lstDescuentos = DescuentosQuery.buscaDescuentosPorIdFactura( idFactura );
    for(DescuentosJava descuento : lstDescuentos){
      DescuentosQuery.eliminaDescuento(descuento);
    }
  }



  public void validaSurtePorGenericoInventariable( NotaVentaJava notaVenta ) throws ParseException {
    List<DetalleNotaVentaJava> detalles = DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFactura(notaVenta.getIdFactura());
    for(DetalleNotaVentaJava det : detalles){
      if(!TAG_GENERICOS_INVENTARIABLES.contains(det.getArticulo().getIdGenerico())){
        det.setSurte(" ");
        DetalleNotaVentaQuery.updateDetalleNotaVenta(det);
      }
    }
  }



  public void insertaJbAnticipoInventariables( String idFactura ) throws ParseException {
    log.debug( "insertaJbAnticipoInventariables( )" );
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(idFactura);
    String articulos = "";
    for(DetalleNotaVentaJava det : nota.getDetalles()){
      articulos = articulos+","+StringUtils.trimToEmpty(det.getArticulo().getArticulo());
    }
    articulos = articulos.replaceFirst( ",", "" );
    if( nota != null ){
      JbJava jb = new JbJava();
      jb.setRx(nota.getFactura());
      jb.setEstado("RTN");
      jb.setIdCliente(StringUtils.trimToEmpty(nota.getIdCliente().toString()));
      jb.setEmpAtendio(nota.getIdEmpleado());
      jb.setNumLlamada(0);
      jb.setSaldo(nota.getVentaNeta().subtract(nota.getSumaPagos()));
      jb.setMaterial(articulos);
      jb.setFechaPromesa(nota.getFechaPrometida());
      jb.setJbTipo("REF");
      jb.setIdMod("0");
      jb.setFechaMod(new Date());
      jb.setCliente(nota.getCliente().getNombreCompleto());
      jb.setFechaVenta(nota.getFechaHoraFactura());
      jb = JbQuery.saveJb( jb );
      mx.lux.pos.java.repository.JbTrack jbTrack = new mx.lux.pos.java.repository.JbTrack();
      jbTrack.setRx(jb.getRx());
      jbTrack.setEstado("RTN");
      jbTrack.setObs("TRABAJO CON SALDO");
      jbTrack.setEmp(jb.getEmpAtendio());
      jbTrack.setFecha(new Date());
      jbTrack.setIdMod("0");
      JbQuery.saveJbTrack(jbTrack);
    }
  }


  public CuponMvJava obtenerCuponMvClave( String clave ){
    return CuponMvQuery.buscaCuponMvPorClave(clave);
  }


  public String claveDescuentoNota( String idFactura ){
    String clave = "";
    List<DescuentosJava> descuento = DescuentosQuery.buscaDescuentosPorIdFactura(idFactura);
    if( descuento.size() > 0 ){
      clave = StringUtils.trimToEmpty(descuento.get(0).getClave());
    }
    return clave;
  }


  public Boolean cuponGeneraCupon( String claveCupon ){
    Boolean generaCupon = true;
    DescuentosClaveJava descuentoClave = DescuentosClaveQuery.buscaDescuentoClavePorClave( claveCupon );
    if( descuentoClave != null ){
      generaCupon = descuentoClave.getCupon();
    }
    return generaCupon;
  }


  public void actualizarCuponMvPorClave( String idFacturaDestino, String clave ) throws ParseException {
    CuponMvJava cuponMv1 = CuponMvQuery.buscaCuponMvPorClave(clave);
    if( cuponMv1 != null ){
      NotaVentaJava notaVenta1 = NotaVentaQuery.busquedaNotaById(idFacturaDestino);
      if( notaVenta1 != null ){
        String factura = StringUtils.trimToEmpty(notaVenta1.getFactura()).length() > 0 ? StringUtils.trimToEmpty(notaVenta1.getFactura()) : notaVenta1.getIdFactura();
        cuponMv1.setFacturaDestino(StringUtils.trimToEmpty( factura ));
        cuponMv1.setFechaAplicacion(new Date());
        CuponMvQuery.updateCuponMv(cuponMv1);
      }
    }
  }


  public BigDecimal obtenerMontoCupon( String idNotaVenta ) throws ParseException {
    log.debug( "obtenerMontoCupon( )" );
    BigDecimal montoCupon = BigDecimal.ZERO;
    ArticulosJava articulo = new ArticulosJava();
    Integer paqueteCant = 0;
    String paqueteStr = "";
    List<MontoCuponJava> lstMontosCupon = new ArrayList<MontoCuponJava>();
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(idNotaVenta);
    if( nota != null){
      List<DetalleNotaVentaJava> lstDet = new ArrayList<DetalleNotaVentaJava>(nota.getDetalles());
      Collections.sort(lstDet, new Comparator<DetalleNotaVentaJava>() {
        @Override
        public int compare(DetalleNotaVentaJava o1, DetalleNotaVentaJava o2) {
          return o1.getArticulo().getSubtipo().compareTo(o2.getArticulo().getSubtipo());
        }
      });
      for( DetalleNotaVentaJava det : lstDet ){
        List<PreciosJava> lstPrecios = PreciosQuery.buscaPreciosPorArticulo(det.getArticulo().getArticulo());
        BigDecimal precio = det.getArticulo().getPrecio();//.multiply(det.cantidadFac)
        if( lstPrecios.size() > 0 ){
          precio = lstPrecios.get(0).getPrecio();//.multiply(det.cantidadFac)
        }
        MontoCuponJava montosCup = MontoCuponQuery.buscaMontoCuponPorGenericoTipoYMontoIgual(det.getArticulo().getIdGenerico(),
                det.getArticulo().getTipo(), precio);
        if( montosCup == null ){
           montosCup = MontoCuponQuery.buscaMontoCuponPorGenericoTipoYMontoMenorMayor(articulo.getIdGenerico(),
                   det.getArticulo().getTipo(), precio);
        }
        if( montosCup == null ){
          montosCup = MontoCuponQuery.buscaMontoCuponPorGenericoTipoYMontoMenorMayor(articulo.getIdGenerico(),
                  det.getArticulo().getTipo(), precio);
        }
        if( montosCup == null ){
          montosCup = MontoCuponQuery.buscaMontoCuponPorGenericoYMontoMenorMayor(articulo.getIdGenerico(),
                  precio);
          if( montosCup != null && StringUtils.trimToEmpty(montosCup.getTipo()).length() > 0 ){
            montosCup = null;
          }
        }
        if( montosCup == null ){
          List<MontoCuponJava> lstMontosCup = MontoCuponQuery.buscaMontoCuponPorGenericoYSubtipo(det.getArticulo().getIdGenerico(),
                  det.getArticulo().getSubtipo());
          montosCup = lstMontosCup.size() > 0 ? lstMontosCup.get(0) : null;
          if( montosCup != null ){
            if( StringUtils.trimToEmpty(paqueteStr).length() > 0 ){
              if( StringUtils.trimToEmpty(paqueteStr).equalsIgnoreCase(StringUtils.trimToEmpty(det.getArticulo().getSubtipo())) ){
                paqueteCant = paqueteCant+det.getCantidadFac().intValue();
              } else {
                paqueteStr = montosCup.getSubtipo();
                paqueteCant = det.getCantidadFac().intValue();
              }
            } else {
              paqueteStr = montosCup.getSubtipo();
              paqueteCant = paqueteCant+det.getCantidadFac().intValue();
            }
            if( (montosCup.getCantidad() == 0 && StringUtils.trimToEmpty(montosCup.getSubtipo()).length() <= 0) || paqueteCant < montosCup.getCantidad() ){
              montosCup = null;
            }
          }
        }
        if( montosCup != null ){
          lstMontosCupon.add( montosCup );
        }
      }
      montoCupon = montoCuponCalculo( lstMontosCupon );
    }
    return montoCupon;
  }



  static BigDecimal montoCuponCalculo( List<MontoCuponJava> lstMontos ){
    BigDecimal montoCupon = BigDecimal.ZERO;
    if( lstMontos.size() > 1 ){
      montoCupon = lstMontos.get(0).getMonto().max(lstMontos.get(1).getMonto());
    } else if(lstMontos.size() > 0){
      montoCupon = lstMontos.get(0).getMonto();
    }
    return montoCupon;
  }


  public BigDecimal obtenerMontoCuponTercerPar( String idNotaVenta ) throws ParseException {
    log.debug( "obtenerMontoCuponTercerPar( )" );
    BigDecimal montoCupon = BigDecimal.ZERO;
    ArticulosJava articulo = new ArticulosJava();
    List<MontoCuponJava> lstMontosCupon = new ArrayList<MontoCuponJava>();
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(idNotaVenta);
    if( nota != null){
      for( DetalleNotaVentaJava det : nota.getDetalles() ){
        List<PreciosJava> lstPrecios = PreciosQuery.buscaPreciosPorArticulo(det.getArticulo().getArticulo());
        BigDecimal precio = det.getArticulo().getPrecio().multiply(new BigDecimal(det.getCantidadFac()));
        if( lstPrecios.size() > 0 ){
          precio = lstPrecios.get(0).getPrecio().multiply(new BigDecimal(det.getCantidadFac()));
        }
        MontoCuponJava montosCup = MontoCuponQuery.buscaMontoCuponPorGenericoTipoYMontoIgual(det.getArticulo().getIdGenerico(),
                det.getArticulo().getTipo(), precio);
        if( montosCup == null ){
          montosCup = MontoCuponQuery.buscaMontoCuponPorGenericoTipoYMontoMenorMayor(det.getArticulo().getIdGenerico(),
                  det.getArticulo().getTipo(), precio);
        }
        if( montosCup == null ){
          montosCup = MontoCuponQuery.buscaMontoCuponPorGenericoTipoYMontoMenorMayor(det.getArticulo().getIdGenerico(),
                  det.getArticulo().getTipo(), precio);
        }
        if( montosCup == null ){
          montosCup = MontoCuponQuery.buscaMontoCuponPorGenericoYMontoMenorMayor(det.getArticulo().getIdGenerico(), precio);
          if( montosCup != null && StringUtils.trimToEmpty(montosCup.getTipo()).length() > 0 ){
            montosCup = null;
          }
        }
        if( montosCup == null ){
          List<MontoCuponJava> lstMontosCup = MontoCuponQuery.buscaMontoCuponPorGenericoYSubtipo( det.getArticulo().getIdGenerico(),
                  StringUtils.trimToEmpty(det.getArticulo().getSubtipo()));
          montosCup = lstMontosCup.size() > 0 ? lstMontosCup.get(0) : null;
          if( montosCup != null && montosCup.getCantidad() < det.getCantidadFac() ){
            montosCup = null;
          }
        }
        if( montosCup != null ){
          lstMontosCupon.add( montosCup );
        }
      }
      montoCupon = montoCuponCalculoTercerPar( lstMontosCupon );
    }
    return montoCupon;
  }



  static BigDecimal montoCuponCalculoTercerPar( List<MontoCuponJava> lstMontos ){
    BigDecimal montoCupon = BigDecimal.ZERO;
    if( lstMontos.size() > 1 ){
      montoCupon = lstMontos.get(0).getMontoTercerPar().max(lstMontos.get(1).getMontoTercerPar());
    } else if(lstMontos.size() > 0){
      montoCupon = lstMontos.get(0).getMontoTercerPar();
    }
    return montoCupon;
  }


  public NotaVentaJava obtenerNotaVentaOrigen( String idNotaVenta ) throws ParseException {
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(idNotaVenta);
    NotaVentaJava notaOrigen = null;
    String idNotaOrigen = "";
    for(PagoJava payment : nota.getPagos()){
      if(TAG_TRANSFERENCIA.equalsIgnoreCase(payment.getIdFPago()) && StringUtils.trimToEmpty(payment.getReferenciaPago()).length() > 0){
        idNotaOrigen = StringUtils.trimToEmpty(payment.getReferenciaPago());
        notaOrigen = NotaVentaQuery.busquedaNotaById( idNotaOrigen );
      }
    }
    return notaOrigen;
  }



  public NotaVentaJava obtenerSiguienteNotaVenta( Integer pIdCustomer ) throws ParseException {
    Date fechaStart = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH );
    Date fechaEnd = new Date( DateUtils.ceiling( new Date(), Calendar.DAY_OF_MONTH ).getTime() - 1 );
    List<NotaVentaJava> orders = NotaVentaQuery.busquedaNotasPorIdClienteAndFecha(pIdCustomer, fechaStart, fechaEnd);
    NotaVentaJava order = null;
    for (NotaVentaJava o : orders) {
      if ( o.getDetalles().size() > 0 && StringUtils.isBlank( o.getFactura() )) {
        order = o;
        break;
      }
    }
    if (order == null) {
      ClientesProcesoQuery.eliminaClienteProceso( pIdCustomer );
    }
    return order;
  }

}