package mx.lux.pos.service.business

import mx.lux.pos.model.CuponMv
import mx.lux.pos.model.Descuento
import mx.lux.pos.model.Modificacion
import mx.lux.pos.model.QCuponMv
import mx.lux.pos.repository.*
import mx.lux.pos.repository.impl.RepositoryFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EliminarNotaVentaTask {

  private static final String MSG_PURGE_ORDER_INVALID =
    "Aviso: Eliminar NotaVenta:%s no es posible, existen modificaciones relacionadas"

  private enum TaskStatus {
    Init, Running, Failed, Completed
  }
  private Logger logger = LoggerFactory.getLogger( this.getClass() )

  private List<String> idFacturas
  private TaskStatus status

  EliminarNotaVentaTask( ) {
    idFacturas = new ArrayList<String>()
    status = TaskStatus.Init
  }

  // Proteted methods
  private void deleteDetalleNotaVenta( ) {
    DetalleNotaVentaRepository catalog = RepositoryFactory.orderLines
    try {
      for ( String idFactura : idFacturas ) {
        catalog.deleteByIdFactura( idFactura )
      }
      catalog.flush()
    } catch ( Exception e ) {
      this.logger.error( "No se pudo borrar ", e )
    }
  }

  private void deleteNotaVenta( ) {
    NotaVentaRepository catalog = RepositoryFactory.orders
    try {
      for ( String idFactura : idFacturas ) {
        catalog.deleteByIdFactura( idFactura )
      }
      catalog.flush()
    } catch ( Exception e ) {
      this.logger.error( "No se pudo borrar ", e )
    }
  }

  private void deletePagos( ) {
    PagoRepository catalog = RepositoryFactory.payments
    try {
      for ( String idFactura : idFacturas ) {
        catalog.deleteByIdFactura( idFactura )
      }
      catalog.flush()
    } catch ( Exception e ) {
      this.logger.error( "No se pudo borrar ", e )
    }
  }

  private void deleteHistoricoPromocion( ) {
    OrdenPromDetRepository detCatalog = RepositoryFactory.orderLinePromotionDetail
    OrdenPromRepository catalog = RepositoryFactory.orderPromotionDetail
    try {
      for ( String idFactura : idFacturas ) {
        detCatalog.deleteByIdFactura( idFactura )
        catalog.deleteByIdFactura( idFactura )
      }
      detCatalog.flush()
      catalog.flush()
    } catch ( Exception e ) {
      this.logger.error( "No se pudo borrar ", e )
    }
  }


  private void deleteHistoricoDiscounts( ) {
    DescuentoRepository detCatalog = RepositoryFactory.discounts
    CuponMvRepository coupon = RepositoryFactory.coupon
    QCuponMv qCuponMv = QCuponMv.cuponMv
    try {
      for ( String idFactura : idFacturas ) {
        List<Descuento> lstDescuentos = detCatalog.findByIdFactura( idFactura )
        List<CuponMv> lstCupones = coupon.findAll( qCuponMv.facturaDestino.eq(idFactura) ) as List<CuponMv>
        for(Descuento descuento : lstDescuentos){
          detCatalog.delete( descuento.id )
        }
        for(CuponMv cuponMv : lstCupones){
          cuponMv.facturaDestino = ""
          cuponMv.fechaAplicacion = null
          coupon.saveAndFlush( cuponMv )
        }
      }
      detCatalog.flush()
    } catch ( Exception e ) {
      this.logger.error( "No se pudo borrar ", e )
    }
  }

  private Boolean validateModificacion( String pIdFactura ) {
    ModificacionRepository catalog = RepositoryFactory.orderModifications
    Collection<Modificacion> modifications = catalog.findByIdFactura( pIdFactura )
    return ( modifications.size() == 0 )
  }

  // Public methods
  boolean addNotaVenta( String pIdNotaVenta ) {
    if ( validateModificacion( pIdNotaVenta ) ) {
      this.idFacturas.add( pIdNotaVenta )
    } else {
      this.logger.info( String.format( MSG_PURGE_ORDER_INVALID, pIdNotaVenta ) )
    }
  }

  void run( ) {
    status = TaskStatus.Running
    if ( idFacturas.size() > 0 ) {
      this.deleteHistoricoPromocion()
      this.deleteHistoricoDiscounts()
      this.deletePagos()
      this.deleteDetalleNotaVenta()
      this.deleteNotaVenta()
    }
    if ( TaskStatus.Running.equals( status ) ) {
      status = TaskStatus.Completed
    }
  }

  String toString( ) {
    return String.format( "[%s] %s (%d Ordenes)", this.status.toString(), this.getClass().getSimpleName(),
        this.idFacturas.size() )
  }
}
