package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import mx.lux.pos.model.DetalleNotaVenta
import mx.lux.pos.repository.DetalleNotaVentaJava
import mx.lux.pos.ui.resources.ServiceManager
import org.apache.commons.lang.StringUtils

@Slf4j
@Bindable
@ToString
@EqualsAndHashCode
class OrderItem {
  Item item
  Integer quantity = 1
  String delivers

  String getDescription( ) {
    String descripcion
    if ( ServiceManager.partService.useShortItemDescription() ) {
      String desc = item?.description?.replaceFirst(StringUtils.trimToEmpty(item.name) + "/", "" )
      descripcion = "[${item?.id ?: ''}] ${desc ?: ''}"
    } else {
      descripcion = "${item?.description ?: ''}${delivers ? " (${delivers})" : ''}"

    }
    return descripcion
  }

    String getTipo(){
        String tipo
        tipo = item?.type

        return tipo
    }


  static OrderItem toOrderItem( DetalleNotaVenta detalleNotaVenta ) {
    try {
      if ( detalleNotaVenta?.id ) {
        OrderItem orderItem = new OrderItem(
            item: Item.toItem( detalleNotaVenta ),
            quantity: detalleNotaVenta.cantidadFac,
            delivers: detalleNotaVenta.surte

        )
        return orderItem
      }
    } catch ( Exception e ) {
      log.error( "Error en el DetalleNotaVenta ", e.toString() )
    }
    return null
  }

  static OrderItem toOrderItem( DetalleNotaVentaJava detalleNotaVenta ) {
    try {
      if ( detalleNotaVenta?.id ) {
        OrderItem orderItem = new OrderItem(
              item: Item.toItem( detalleNotaVenta ),
              quantity: detalleNotaVenta.cantidadFac,
              delivers: detalleNotaVenta.surte
        )
        return orderItem
      }
    } catch ( Exception e ) {
      log.error( "Error en el DetalleNotaVenta ", e.toString() )
    }
    return null
  }
}
