package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.ItemController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.OrderItem
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils

import java.util.List
import javax.swing.*
import java.awt.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class AdjustSaleDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JCheckBox cbResume
  private JTextField txtBill
  private JTextField txtArticle
  private JTextField txtArticleSelected
  private JTextField txtColour
  private JRadioButton cbArticulo
  private JRadioButton cbFactura
  private ButtonGroup reportTipo
  private ButtonGroup lentTipo
  private JRadioButton cbGogle
  private JRadioButton cbOftalmico
  private JRadioButton todo
  private JLabel lblWarning
  private Date selectedDateStart
  private Date selectedDateEnd
  private String selectedArticle
  private Item itemSelected
  private Item oldItem
  private Order order

    public boolean button = false

    AdjustSaleDialog( ) {
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Reclasificar Venta",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 550, 300 ],
        location: [ 200, 200 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 4", "10[][grow,fill][fill][grow,fill]10", "20[]10[]" ) ) {
          label( text: "Numero de Ticket:" )
          txtBill = textField( document: new UpperCaseDocument() )
          label( " " )
          button( text: "Buscar", preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onSearch() }
          )
          lblWarning = label( foreground: UI_Standards.WARNING_FOREGROUND, text: " ", constraints: "span 4" )
          label( text: "Articulo Anterior:" )
          txtArticle = textField( constraints: "span 2", editable: false )
          label( text: " " )
          label( text: "Articulo Nuevo:" )
          txtArticleSelected = textField( editable: false )
          label( text: "Color:" )
          txtColour = textField( editable: false )
        }

        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Aplicar", preferredSize: UI_Standards.BUTTON_SIZE,
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

  // UI Management
  protected void refreshUI( ) {

  }

  // Public Methods
  void activate( ) {
    refreshUI()
    setVisible( true )
  }

  boolean getCbResume( ) {
    return cbResume.selected
  }

  String getselectedArticle( ) {
    return selectedArticle
  }

  boolean getCbGogle( ) {
    return cbGogle.selected
  }

  boolean getCbTodo( ) {
    return todo.selected
  }

  boolean getCbOftalmico( ) {
    return cbOftalmico.selected
  }

  void setDefaultDates( Date pDateStart, Date pDateEnd ) {
    selectedDateStart = DateUtils.truncate( pDateStart, Calendar.MONTH )
    selectedDateEnd = DateUtils.truncate( pDateEnd, Calendar.DATE )
  }

  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    if( order != null && oldItem != null && itemSelected != null ){
      if( OrderController.reclassifyFrame( order, oldItem, itemSelected ) ){
        sb.optionPane(message: "Transaccion Actualizada Correctamente", optionType: JOptionPane.DEFAULT_OPTION)
                .createDialog(new JTextField(), "Transaccion").show()
        dispose()
      } else {
        sb.optionPane(message: "Error al Actualizar la transaccion", optionType: JOptionPane.DEFAULT_OPTION)
                .createDialog(new JTextField(), "Error").show()
      }
    }
  }


  protected void onSearch( ) {
    if( StringUtils.trimToEmpty(txtBill.text).length() > 0 ){
    order = OrderController.findOrderByTicketJava(StringUtils.trimToEmpty(txtBill.text))
    if( order != null ){
      oldItem = ItemController.findFrameWithoutColor( order )
      if( oldItem != null ){
        List<Item> lstItemsTmp = ItemController.findItemsByQuery(StringUtils.trimToEmpty(oldItem.name))
        List<Item> results = new ArrayList<>()
        for(Item i : lstItemsTmp){
          if( StringUtils.trimToEmpty(i.color).length() > 0 ){
            results.add(i)
          }
        }
        if( results ){
          lblWarning.text = " "
          SuggestedItemsDialog dialog = new SuggestedItemsDialog(this, StringUtils.trimToEmpty(oldItem.name), results, true)
          dialog.show()
          itemSelected = dialog.item
          txtArticle.text = StringUtils.trimToEmpty(oldItem != null ? "[${oldItem.id}]${oldItem.name}" : "")
          txtArticleSelected.text = StringUtils.trimToEmpty(itemSelected != null ? "[${itemSelected.id}]${itemSelected.name}" : "")
          txtColour.text = StringUtils.trimToEmpty(itemSelected != null ? itemSelected.color : "")
        } else {
          lblWarning.text = "No existen articulos validos para esta factura."
        }
      } else {
        lblWarning.text = "La factura no contiene articulos sin color."
      }
    } else {
      lblWarning.text = "La factura no existe."
    }
    }
  }
}
