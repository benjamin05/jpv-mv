package mx.lux.pos.ui.model

import mx.lux.pos.model.Articulo
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.ui.model.adapter.PartAdapter
import org.apache.commons.lang3.StringUtils

import java.text.SimpleDateFormat

class LcSku {

  private static PartAdapter adapter = PartAdapter.instance
  private Lc parent
  private Integer line
  private String descripcion
  private String lote
  String graduation
  private String fechaAlta
  private String fechaAcuse
  private String fechaEnvio
  private String fechaRecepcion
  private String fechaEntrega
  def Articulo part
  def Integer qty

    LcSku( Lc pParent, Articulo pPart ) {
    this( pParent, pParent.nextLine( ), pPart, 1)
  }

    LcSku( Lc pParent, Articulo pPart, Integer pQty, String graduation ) {
    this( pParent, pParent.nextLine( ), pPart, pQty, graduation, null )
  }

    LcSku( Lc pParent, Integer pLinea, Articulo pPart, Integer pQty, String graduation, PedidoLc pedidoLc ) {
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy")
    parent = pParent
    //lote = pParent.order.udf4
    this.graduation = graduation
    line = pLinea
    part = pPart
    qty = pQty
    fechaAlta = pedidoLc?.fechaAlta != null ? df.format(pedidoLc.fechaAlta) : ""
    fechaAcuse = pedidoLc?.fechaAcuse != null ? df.format(pedidoLc.fechaAcuse) : ""
    fechaEnvio = pedidoLc?.fechaEnvio != null ? df.format(pedidoLc.fechaEnvio) : ""
    fechaRecepcion = pedidoLc?.fechaRecepcion != null ? df.format(pedidoLc.fechaRecepcion) : ""
    fechaEntrega = pedidoLc?.fechaEntrega != null ? df.format(pedidoLc.fechaEntrega) : ""
  }

    LcSku( Integer linea, Date fecha, String factura, String descripcion, Integer cantidad ) {
        //parent = pParent
        //lote = pParent.order.udf4
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy")
        line = linea
        this.descripcion = descripcion
        //lote = descripcion
        graduation = factura
        qty = cantidad
        Articulo art = new Articulo()
        art.articulo = df.format(fecha)
        part = art
    }

  Integer getLine() {
    return line
  }

  Integer getLote() {
    return lote
  }

  String getDescripcion() {
    return descripcion
  }
  
  Integer getSku() {
    return part.id
  }

  String getModel() {
    return StringUtils.trimToEmpty(part.articulo)
  }
  
  String getDescription() {
    return adapter.getText( part, PartAdapter.FLD_INV_DESC )
  }
  
  String getMovType() {
    return parent.movType
  }
  
  String toString() {
    return adapter.getText( part )
  }

}
