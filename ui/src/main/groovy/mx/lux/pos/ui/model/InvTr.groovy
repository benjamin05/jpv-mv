package mx.lux.pos.ui.model

import mx.lux.pos.java.repository.ArticulosJava
import mx.lux.pos.model.*
import mx.lux.pos.service.InventarioService
import mx.lux.pos.ui.model.adapter.InvTrFilter
import mx.lux.pos.ui.resources.ServiceManager
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.NumberFormat

class InvTr {

  private static final Boolean TESTING = true
  private static final String REF_DELIMITER = TransInvAdapter.REF_DELIMITER
  Logger logger = LoggerFactory.getLogger( InvTr.class )

  InvTrViewMode viewMode = null

  List<Sucursal> siteList
  List<InvTrViewMode> viewModeList

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
  File inFile = null

  TransInv qryInvTr = null
  NotaVenta order = null

  TipoTransInv qryTrType = null
  User qryUser = null
  Sucursal qrySiteTo = null
  Sucursal qrySiteFrom = null
  InvTrDataset qryDataset = null
  List<InvTrSku> skuList = new ArrayList<InvTrSku>()

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
  InvTr( ) {
    initSelectionLists()
  }

  // Internal Methods
  protected void removePartsQtyZero( ) {
    List<InvTrSku> toDelete = new ArrayList<InvTrSku>()
    for ( InvTrSku trLine : skuList ) {
      if ( trLine.qty == 0 ) {
        toDelete.add( trLine )
      }
    }
    for ( InvTrSku trLine : toDelete ) {
      skuList.remove( trLine )
    }
  }

  // Public methods
  String accessStatus( ) {
    String text = txtStatus
    txtStatus = ""
    return text
  }

  void addPart( Articulo pPart ) {
    Integer qty = postQty
    /*if ( InvTrViewMode.ADJUST.equals( viewMode ) ) {
      if ( skuList.size()%2 != 0 ) {
        qty = -1 * postQty
      }
    }*/
      if ( InvTrViewMode.RETURN.equals( viewMode ) ) {
          String[] part = partSeed.split(",")
          if( part.length > 1 ){
              qty = NumberFormat.getInstance().parse(part[1])
          }
      }
    addPart( pPart, qty )
    this.removePartsQtyZero()
  }


  void addPart( ArticulosJava pPart ) {
    Integer qty = postQty
    if ( InvTrViewMode.ADJUST.equals( viewMode ) ) {
      if ( skuList.size()%2 != 0 ) {
        qty = -1 * postQty
      }
    }
    if ( InvTrViewMode.RETURN.equals( viewMode ) ) {
      String[] part = partSeed.split(",")
      if( part.length > 1 ){
        qty = NumberFormat.getInstance().parse(part[1])
      }
    }
    addPart( pPart, qty )
    this.removePartsQtyZero()
  }


  void addPart( Articulo pPart, Integer pQty ) {
    InvTrSku trLine = findPart( pPart )
    if ( trLine == null ) {
      skuList.add( new InvTrSku( this, pPart, pQty ) )
    } else {
      trLine.qty += pQty
    }
    dirty = true
  }


  void addPart( ArticulosJava pPart, Integer pQty ) {
    InvTrSku trLine = findPart( pPart )
    if ( trLine == null ) {
      skuList.add( new InvTrSku( this, pPart, pQty ) )
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

  InvTrSku findPart( Articulo pPart ) {
    InvTrSku found = null
    for ( InvTrSku trLine in skuList ) {
      if ( trLine.sku.equals( pPart.id ) ) {
        found = trLine
        break
      }
    }
    return found
  }

  InvTrSku findPart( ArticulosJava pPart ) {
    InvTrSku found = null
    for ( InvTrSku trLine in skuList ) {
      if ( trLine.sku.equals( pPart.idArticulo ) ) {
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
    qryDataset = new InvTrDataset()
    InventarioService service = ServiceManager.inventoryService
    Date lastDate = service.obtenerUltimaFechaTransaccion()
    qryDataset.reset()
    InvTrFilter filter = qryDataset.filter
    filter.setDateRange( lastDate )
    qryDataset.requestTransactions()
  }

  void initSelectionLists( ) {
    siteList = ServiceManager.getInventoryService().listarSucursales()
    viewModeList = InvTrViewMode.listViewModes()
  }

  Integer nextLine( ) {
    return skuList.size() + 1
  }

  void postReturn( ) {
    this.postReference = String.format( '%s%s%,.2f%s%s%s', this.ticketNum, REF_DELIMITER,
        this.returnAmount, REF_DELIMITER, this.empName, REF_DELIMITER )
  }

  void setQryInvTr( TransInv pTransInv ) {
    qryInvTr = pTransInv
    order = ServiceManager.orderService.obtenerNotaVenta( qryInvTr.referencia )

    qryTrType = ServiceManager.inventoryService.obtenerTipoTransaccion( pTransInv.idTipoTrans )
    qryUser = User.toUser( ServiceManager.employeeService.obtenerEmpleado( pTransInv.idEmpleado ) )
    if ( pTransInv.sucursalDestino != null ) {
      qrySiteTo = ServiceManager.inventoryService.obtenerSucursal( pTransInv.sucursalDestino )
    } else {
      qrySiteTo = null
    }
    skuList.clear()
    for ( TransInvDetalle det in pTransInv.trDet ) {
      Articulo part = ServiceManager.partService.obtenerArticulo( det.sku, false )
      skuList.add( new InvTrSku( this, det.linea, part, det.cantidad ) )
    }
  }

  void setViewMode( InvTrViewMode pViewMode ) {
    viewMode = pViewMode
    if ( viewMode.equals( InvTrViewMode.ISSUE ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( InvTrViewMode.RECEIPT ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( InvTrViewMode.MASSIVE_RECEIPT ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( InvTrViewMode.ADJUST ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( InvTrViewMode.FILE_ADJUST ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( InvTrViewMode.RETURN ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( InvTrViewMode.QUERY ) ) {
      postTrType = null
        initDataset()
    }
    else if ( viewMode.equals( InvTrViewMode.OUTBOUND ) ) {
        postTrType = viewMode.getTrType()
    }
    else if ( viewMode.equals( InvTrViewMode.INBOUND ) ) {
        postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( InvTrViewMode.OTHER_ISSUE ) ) {
      postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( InvTrViewMode.OTHER_RECEIPT ) ) {
        postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( InvTrViewMode.ISSUE_FRAMES ) ) {
        postTrType = viewMode.getTrType()
    } else if ( viewMode.equals( InvTrViewMode.ISSUE_ACCESORIES ) ) {
        postTrType = viewMode.getTrType()
    }
  }
}
