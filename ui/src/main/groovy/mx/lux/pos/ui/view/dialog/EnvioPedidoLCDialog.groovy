package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.model.PedidoLcDet
import mx.lux.pos.ui.controller.ItemController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.LcSku
import mx.lux.pos.ui.model.ModelLc
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import javax.swing.table.TableModel
import java.awt.*
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.List

class EnvioPedidoLCDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtCantidad
  private JTextField txtEsfera
  private JTextField txtCilindro
  private JTextField txtEje
  private JComboBox cbColor
  private JComboBox cbCurva
  private JTextField txtDiametro

  private JLabel lblWarning

  private ModelLc model

  private JTable tblArticulos
  TableModel browserSku
  Integer cantidad = 0
  PedidoLc pedidoLc
  List<PedidoLcDet> lstArticulos = new ArrayList<>();

  public boolean button = false

    EnvioPedidoLCDialog( PedidoLc pedidoLc ) {
    this.pedidoLc = pedidoLc
    for(PedidoLcDet pedidoLcDet : pedidoLc.pedidoLcDets){
      lstArticulos.add( pedidoLcDet )
    }
    buildUI(  )
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Articulos de pedido ${pedidoLc.id}",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 700, 380 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap", "[grow,fill]", "20[]10[]" ) ) {
            scrollPane( constraints: BorderLayout.CENTER,
                    border:BorderFactory.createTitledBorder( "Artículos" ),
            ) {
                tblArticulos = table( selectionMode: ListSelectionModel.SINGLE_SELECTION,
                        mouseClicked: { ev -> if (ev.clickCount == 2) { onDoubleClick() } },
                ) {
                    browserSku = sb.tableModel( list: lstArticulos ) {
                        propertyColumn( header: "Articulo", propertyName: "modelo", maxWidth: 150, editable: false )
                        propertyColumn( header: "Curva Base", propertyName: "curvaBase", maxWidth: 80, editable: false )
                        propertyColumn( header: "Diametro", propertyName: "diametro", maxWidth: 70, editable: false )
                        propertyColumn( header: "Esfera", propertyName: "esfera", maxWidth: 70, editable: false )
                        propertyColumn( header: "Cilindro", propertyName: "cilindro", maxWidth: 70, editable: false )
                        propertyColumn( header: "Eje", propertyName: "eje", maxWidth: 50, editable: false )
                        propertyColumn( header: "Color", propertyName: "color", maxWidth: 150, editable: false )
                        propertyColumn( header: "Cantidad", propertyName: "cantidad", maxWidth: 70, editable: false )
                    }
                }
            }
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Enviar", preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onButtonOk() }
            )
            button( text: "Cerrar", preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onButtonCancel() }
            )
          }
        }
      }
    }
  }


  protected void onDoubleClick ( ){
    PedidoLcDet pedidoLcDet = lstArticulos [tblArticulos.selectedRow]
    List<Item> lstItems = ItemController.findItemByArticleAndColor( pedidoLcDet.modelo.trim(), "" )
    Item item = lstItems.get(0)
    Integer idCliente = 0
    try{
      idCliente = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(pedidoLc.getCliente())).intValue()
    } catch (NumberFormatException e ) {println e}
    ModelLc modeloLc = ItemController.findLenteContacto( lstItems.get(0).id )
    if( modeloLc != null ){
          LentesContactoDialog dialog = new LentesContactoDialog( pedidoLc.id, modeloLc.model, item.id, modeloLc.curve,
             modeloLc.diameter, modeloLc.sphere, modeloLc.cylinder, modeloLc.axis, modeloLc.color, idCliente, false, pedidoLcDet )
      dialog.show()
      lstArticulos.clear()
      lstArticulos.addAll( ItemController.findPedidoLcDetPorId( StringUtils.trimToEmpty(pedidoLcDet.id) ) )
      browserSku.fireTableDataChanged()
    }
  }


  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    OrderController.createAcuse( pedidoLc.id )
    dispose()
  }

}