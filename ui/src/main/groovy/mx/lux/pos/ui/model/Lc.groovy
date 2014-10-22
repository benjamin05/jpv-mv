package mx.lux.pos.ui.model

import mx.lux.pos.model.*
import mx.lux.pos.service.InventarioService
import mx.lux.pos.service.PedidoService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.model.adapter.LcFilter
import mx.lux.pos.ui.resources.ServiceManager
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.NumberFormat

class Lc {

  private static final Boolean TESTING = true
  private static final String REF_DELIMITER = TransInvAdapter.REF_DELIMITER
  Logger logger = LoggerFactory.getLogger( Lc.class )

  LcViewMode viewMode = null

  List<Sucursal> siteList
  List<LcViewMode> viewModeList

  Boolean dirty = false
  String partSeed = ""
  Date today = DateUtils.truncate( new Date(), Calendar.DATE )
  String txtStatus = ""

  TipoTransInv postTrType = null
  Sucursal postSiteTo = null
  Sucursal postSiteFrom = null
  String postReference = ""
  String postRemarks = ""
  Integer postQty = 1
  Shipment receiptDocument = null
  InvAdjustSheet adjustDocument = null
  String documentWarning = ""
  String cliente = ""
  String folio = ""
  File inFile = null

  PedidoLc qryInvTr = null
  PedidoLcDet qryInvTrDet = null
  NotaVenta order = null

  TipoTransInv qryTrType = null
  User qryUser = null
  Sucursal qrySiteTo = null
  Sucursal qrySiteFrom = null
  LcDataset qryDataset = null
  List<LcSku> skuList = new ArrayList<LcSku>()
  List<LcSku> orderList = new ArrayList<LcSku>()

  // Extraordinary Return
  String ticketNum
  String empName
  Double returnAmount

  Boolean flagOnSiteTo = false
  Boolean flagOnPartSeed = false
  Boolean flagOnRemarks = false
  Boolean flagOnDocument = false
  Boolean flagOnQty = false

  //clave codificada ERO
  String claveCodificada

    Lc( ) {
    initSelectionLists()
  }

  // Internal Methods
  protected void removePartsQtyZero( ) {
    List<LcSku> toDelete = new ArrayList<LcSku>()
    for ( LcSku trLine : skuList ) {
      if ( trLine.qty == 0 ) {
        toDelete.add( trLine )
      }
    }
    for ( LcSku trLine : toDelete ) {
      skuList.remove( trLine )
    }
  }

  // Public methods
  String accessStatus( ) {
    String text = txtStatus
    txtStatus = ""
    return text
  }

  void addPart( PedidoLcDet pPart ) {
    Integer qty = pPart.cantidad
    if ( LcViewMode.ADJUST.equals( viewMode ) ) {
      if ( skuList.size()%2 != 0 ) {
        qty = -1 * postQty
      }
    }
      if ( LcViewMode.RETURN.equals( viewMode ) ) {
          String[] part = partSeed.split(",")
          if( part.length > 1 ){
              qty = NumberFormat.getInstance().parse(part[1])
          }
      }
    addPart( pPart, qty )
    this.removePartsQtyZero()
  }

  void addPart( PedidoLcDet pPart, Integer pQty ) {
    LcSku trLine = findPart( pPart )
    if ( trLine == null ) {
      Articulo art = ServiceManager.partService.obtenerArticuloPorArticulo( StringUtils.trimToEmpty(pPart.modelo), false )
      if( art != null ){
        String graduation = "${pPart.curvaBase},${pPart.diametro},${pPart.esfera},${pPart.cilindro},${pPart.eje},${pPart.color}"
        if( graduation.startsWith( "," ) ){
          graduation = graduation.replaceFirst( ",","" )
        }
        skuList.add( new LcSku( this, art, pQty, graduation ) )
      }
    } else {
      trLine.qty += pQty
    }
    dirty = true
  }

  void clear( ) {
    qryInvTr = null
    order = null

    qryTrType = null
    qryUser = null
    qrySiteTo = null
    // postTrType = null
    postSiteTo = null
    receiptDocument = null
    adjustDocument = null
    inFile = null
    postReference = ""
    postRemarks = ""
    partSeed = ""
    this.ticketNum = ""
    this.empName = ""
    this.returnAmount = 0
    skuList.clear()
    flagOnSiteTo = false
    flagOnPartSeed = false
    flagOnRemarks = false
    flagOnDocument = false
    flagOnQty = false
    documentWarning = ""
    dirty = false
  }

  LcSku findPart( PedidoLcDet pPart ) {
    LcSku found = null
    for ( LcSku trLine in skuList ) {
      if ( trLine.sku.equals( pPart.id ) ) {
        found = trLine
        break
      }
    }
    return found
  }

  User getCurrentUser( ) {
    User user = Session.get( SessionItem.USER ) as User
    if ( ( user == null ) && ( TESTING ) ) {
      user = new User(
          name: "Pruebas",
          fathersName: "Sistemas",
          mothersName: "Sistema",
          username: "9999",
          password: "0000"
      )
    }
    return user
  }

  String getMovType( ) {
    String movType = "-"
    if ( postTrType != null ) {
      movType = postTrType.tipoMov
    } else if ( qryTrType != null ) {
      movType = qryTrType.tipoMov
    }
    return movType
  }

  String getSelectorText( ) {
    String text = ""
    if ( qryDataset != null ) {
      text = StringUtils.trimToEmpty( qryDataset.getDatasetLabel() )
    }
    return text
  }

  void initDataset( ) {
    qryDataset = new LcDataset()
    PedidoService service = ServiceManager.requestService
    Date lastDate = service.obtenerUltimaFechaTransaccion()
    qryDataset.reset()
    LcFilter filter = qryDataset.filter
    filter.setDateRange( lastDate )
    qryDataset.requestTransactions( true )
  }

  void initSelectionLists( ) {
    siteList = ServiceManager.getInventoryService().listarSucursales()
    viewModeList = LcViewMode.listViewModes()
  }

  Integer nextLine( ) {
    return skuList.size() + 1
  }

  void postReturn( ) {
    this.postReference = String.format( '%s%s%,.2f%s%s%s', this.ticketNum, REF_DELIMITER,
        this.returnAmount, REF_DELIMITER, this.empName, REF_DELIMITER )
  }

  void setQryInvTr( PedidoLc pPedidoLc ) {
    qryInvTr = pPedidoLc
    order = ServiceManager.orderService.obtenerNotaVentaPorTicket( qryInvTr.id )

    qryTrType = ServiceManager.inventoryService.obtenerTipoTransaccion( "" )

    Sucursal sucursal = ServiceManager.siteService.obtenerSucursal(Registry.currentSite)
    NotaVenta nota = ServiceManager.orderService.obtenerNotaVentaPorTicket( sucursal.centroCostos+"-"+pPedidoLc.id )
    if( nota == null ){
      nota = ServiceManager.orderService.obtenerNotaVenta( pPedidoLc.id )
    }
    qryUser = User.toUser( ServiceManager.employeeService.obtenerEmpleado( nota.idEmpleado.toString() ) )
    if ( pPedidoLc.sucursal != null ) {
      Integer suc = 0
      try{
        suc = NumberFormat.getInstance().parse( pPedidoLc.sucursal )
      } catch ( NumberFormatException e ){ println e }
      qrySiteTo = ServiceManager.inventoryService.obtenerSucursal( suc )
    } else {
      qrySiteTo = null
    }
    skuList.clear()
    for ( PedidoLcDet det in pPedidoLc.pedidoLcDets ) {
      Articulo part = ServiceManager.partService.obtenerArticuloPorArticulo( det.modelo, false )
      String curva = StringUtils.trimToEmpty(det.curvaBase).length() > 0 ? "${det.curvaBase}," : ''
      String diametro = StringUtils.trimToEmpty(det.diametro).length() > 0 ? ",${det.diametro}" : ''
      String esfera = StringUtils.trimToEmpty(det.esfera).length() > 0 ? ",${det.esfera}" : ''
      String cilindro = StringUtils.trimToEmpty(det.cilindro).length() > 0 ? ",${det.cilindro}" : ''
      String eje = StringUtils.trimToEmpty(det.eje).length() > 0 ? ",${det.eje}" : ''
      String color = StringUtils.trimToEmpty(det.color).length() > 0 ? ",${det.color}" : ''
      String graduation = "${curva}${diametro}${esfera}${cilindro}${eje}${color}"
      if( graduation.startsWith( "," ) ){
        graduation = graduation.replaceFirst( ",","" )
      }
      skuList.add( new LcSku( this, 1, part, det.cantidad, graduation, pPedidoLc ) )
    }
  }

  void setViewMode( LcViewMode pViewMode ) {
    viewMode = pViewMode
    if ( viewMode.equals( LcViewMode.ISSUE ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( LcViewMode.RECEIPT ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( LcViewMode.ADJUST ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( LcViewMode.FILE_ADJUST ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( LcViewMode.RETURN ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( LcViewMode.QUERY ) ) {
      postTrType = null
        initDataset()
    }
    else if ( viewMode.equals( LcViewMode.OUTBOUND ) ) {
        postTrType = viewMode.getTrType()
    }
    else if ( viewMode.equals( LcViewMode.INBOUND ) ) {
        postTrType = viewMode.getTrType()
    }
  }


  void setGraduationLc(Integer numReg){
    qryInvTrDet = ServiceManager.requestService.obtenerPedidoDetPorNumReg(numReg)
  }


    void setOtherEntrieInvTr( NotaVenta nota ) {
        qryInvTr = ServiceManager.requestService.obtenerPedidoPoridPedido( StringUtils.trimToEmpty(nota.factura) )
        order = nota

        qryTrType = ServiceManager.inventoryService.obtenerTipoTransaccion( "" )

        Sucursal sucursal = ServiceManager.siteService.obtenerSucursal(Registry.currentSite)
        //NotaVenta nota = ServiceManager.orderService.obtenerNotaVentaPorTicket( sucursal.centroCostos+"-"+pPedidoLc.id )
        if( nota == null ){
            nota = ServiceManager.orderService.obtenerNotaVenta( pPedidoLc.id )
        }
        qryUser = User.toUser( ServiceManager.employeeService.obtenerEmpleado( nota.idEmpleado.toString() ) )
        if ( pPedidoLc.sucursal != null ) {
            Integer suc = 0
            try{
                suc = NumberFormat.getInstance().parse( pPedidoLc.sucursal )
            } catch ( NumberFormatException e ){ println e }
            qrySiteTo = ServiceManager.inventoryService.obtenerSucursal( suc )
        } else {
            qrySiteTo = null
        }
        skuList.clear()
        for ( PedidoLcDet det in pPedidoLc.pedidoLcDets ) {
            Articulo part = ServiceManager.partService.obtenerArticuloPorArticulo( det.modelo, false )
            skuList.add( new LcSku( this, 1, part, det.cantidad ) )
        }
    }
}
