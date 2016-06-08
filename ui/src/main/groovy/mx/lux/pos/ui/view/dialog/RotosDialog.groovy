package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.OrderItem
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.text.DateFormat
import java.text.SimpleDateFormat

class RotosDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtRx
  private ButtonGroup typeRoto
  private JRadioButton cbFrame
  private JRadioButton cbLens
  private JTextField txtCauseRoto
  private JTextField txtResponsable
  private JLabel lblReponsable
  private JTextField txtMaterial
  private Date selectedDateStart
  private Date selectedDateEnd
  private ButtonGroup contact
  private JRadioButton cbYes
  private JRadioButton cbNo
  private JTextField txtPromiseDate
  private JButton btnGuardar

  private Order order

  public boolean button = false

  RotosDialog( ) {
    buildUI()
    disableComponents()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Rotos",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 750, 400 ],
        location: [ 200, 200 ],
    ) {
      panel( layout: new MigLayout( "wrap ", "[fill]", "10[]5[]10" ) ){
      borderLayout()
      panel( border: titledBorder(""), constraints: BorderLayout.PAGE_START, maximumSize: [ 600, 100 ],
              layout: new MigLayout( "wrap 3", "[fill][fill][grow,fill]", "[][]" ) ) {
          typeRoto = buttonGroup()
          label( "Rx" )
          txtRx = textField( )
          label( " " )
          txtRx.addFocusListener( new FocusListener() {
              @Override
              void focusGained(FocusEvent e) {
                  //To change body of implemented methods use File | Settings | File Templates.
              }

              @Override
              void focusLost(FocusEvent e) {
                order = OrderController.orderValidToRoto( txtRx.text )
                if(order != null ){
                  txtPromiseDate.text = OrderController.promiseDateByBill(txtRx.text)
                  enableComponents()
                } else {
                  txtPromiseDate.text = ""
                  disableComponents()
                }
              }
          })
            label( text: "Tipo Roto" )
            cbFrame = radioButton( text: "Armazon", buttonGroup: typeRoto )
            cbFrame.addActionListener( new ActionListener() {
                @Override
                void actionPerformed(ActionEvent e) {
                  doBindings()
                }
            })
            cbLens = radioButton( text: "Lente", buttonGroup: typeRoto )
            cbLens.addActionListener( new ActionListener() {
                @Override
                void actionPerformed(ActionEvent e) {
                  doBindings()
                }
            })
            label( text: "Causa Roto" )
            txtCauseRoto = textField( constraints: 'span2' )
            label( text: "Responsable" )
            txtResponsable = textField( )
            txtResponsable.addFocusListener( new FocusListener() {
                @Override
                void focusGained(FocusEvent e) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                void focusLost(FocusEvent e) {
                  String optometrista = CustomerController.findOptometrista(txtResponsable.text)
                  if (optometrista != null) {
                    lblReponsable.setText(optometrista)
                  } else {
                    txtResponsable.text = ""
                    lblReponsable.text = ""
                  }
                }
            })
            lblReponsable = label( " " )
            label( text: "Material" )
            txtMaterial = textField( constraints: 'span2' )
          }
          panel( border: titledBorder(""), constraints: BorderLayout.CENTER, maximumSize: [ 600, 100 ],
                  layout: new MigLayout( "wrap 4", "[fill][fill][fill][grow,fill]", "[][]" ) ) {
            contact = buttonGroup()
            label( text: "Contactar" )
            cbYes = radioButton( text: "Si", buttonGroup: contact )
            cbNo = radioButton( text: "No", buttonGroup: contact )
            label( " " )
            label( text: "Fecha Promesa" )
            txtPromiseDate = textField( constraints: 'span2' )
          }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            btnGuardar = button( text: "Guardar", preferredSize: UI_Standards.BUTTON_SIZE,
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
  public void doBindings( ) {
    if(order != null){
      if( cbFrame.selected ){
        for(OrderItem oi : order.items){
          if( StringUtils.trimToEmpty(oi.item.type).equalsIgnoreCase("A") ){
            txtMaterial.text = StringUtils.trimToEmpty(oi?.item?.name)
          }
        }
      } else if( !cbFrame.selected && cbLens.selected ){
        txtMaterial.text = ""
      }
    }
  }

  protected void refreshUI( ) {

  }

  // Public Methods
  void activate( ) {
    refreshUI()
    setVisible( true )
  }

  Date getSelectedDateStart( ) {
    return selectedDateStart
  }

  Date getSelectedDateEnd( ) {
    return selectedDateEnd
  }

  void setDefaultDates( Date pDateStart, Date pDateEnd ) {
    selectedDateStart = DateUtils.truncate( pDateStart, Calendar.DATE )
    selectedDateEnd = DateUtils.truncate( pDateEnd, Calendar.DATE )
  }

  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    if( validData() ){
      dispose()
    }
  }

  void enableComponents(){
    cbLens.enabled = true
    cbFrame.enabled = true
    cbYes.enabled = true
    cbNo.enabled = true
    txtCauseRoto.enabled = true
    txtResponsable.enabled = true
    txtMaterial.enabled = true
    txtPromiseDate.enabled = true
    btnGuardar.enabled = true
  }


  void disableComponents(){
    //cbLens.enabled = false
    //cbFrame.enabled = false
    cbYes.enabled = false
    cbNo.enabled = false
    txtCauseRoto.enabled = false
    txtResponsable.enabled = false
    txtMaterial.enabled = false
    txtPromiseDate.enabled = false
    btnGuardar.enabled = false

  }


  Boolean validData( ){

  }


}
