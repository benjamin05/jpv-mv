package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.FormaContactoJava
import mx.lux.pos.java.repository.NotaVentaJava
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.ContactController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt.*
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.text.DateFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class RescheduleDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtRx
  private JTextField txtCustomer
  private JTextField txtContactType
  private JTextField txtContact
  private JTextField txtDays
  private Date selectedDateStart
  private Date selectedDateEnd
  private JSpinner date
  private String bill

  public boolean button = false

  RescheduleDialog( String bill ) {
    this.bill = bill
    buildUI()
    fillFields( StringUtils.trimToEmpty(bill) )
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: bill,
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 450, 360 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.PAGE_START, layout: new MigLayout( "wrap 2", "[][grow,fill]", "20[]10[]" ) ) {
          label( text: "Rx" )
          txtRx = textField( editable: false )
          label( text: "Cliente" )
          txtCustomer = textField( editable: false )
          label( text: "Tipo Con." )
          txtContactType = textField( editable: false )
          label( text: "Tipo Con." )
          txtContact = textField( editable: false )
        }
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 4", "[fill]", "20[]10[]" ) ) {
          label( text: "Volver a Contactar" )
          date = spinner( model: spinnerDateModel() )
          date.editor = new JSpinner.DateEditor( date as JSpinner, 'dd-MM-yyyy' )
          date.value = DateUtils.addDays( new Date(), Registry.callAgain )
          date.addChangeListener(new ChangeListener() {
              @Override
              void stateChanged(ChangeEvent e) {
                  Date fechainicial = new Date()
                  Date fechafinal = date.value as Date
                  DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
                  String fechainiciostring = df.format(fechainicial);
                  try {
                      fechainicial = df.parse(fechainiciostring);
                  }
                  catch (ParseException ex) {
                  }

                  String fechafinalstring = df.format(fechafinal);
                  try {
                      fechafinal = df.parse(fechafinalstring);
                  }
                  catch (ParseException ex) {
                  }

                  long fechainicialms = fechainicial.getTime();
                  long fechafinalms = fechafinal.getTime();
                  long diferencia = fechafinalms - fechainicialms;
                  double dias = Math.floor(diferencia / 86400000L);// 3600*24*1000
                  txtDays.text = StringUtils.trimToEmpty(dias.intValue().toString());
              }
          })
          label( text: "o" )
          txtDays = textField( StringUtils.trimToEmpty(Registry.callAgain.toString()), preferredSize: [ 60, 21 ] )
          txtDays.addFocusListener(new FocusListener() {
              @Override
              void focusGained(FocusEvent e) {
                  //To change body of implemented methods use File | Settings | File Templates.
              }

              @Override
              void focusLost(FocusEvent e) {
                Integer days = 0
                try{
                  days = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtDays.text))
                } catch ( ParseException ex ){
                  println ex
                }
                Calendar cal = Calendar.getInstance()
                cal.setTime(new Date())
                cal.add(Calendar.DAY_OF_MONTH, days)
                date.value = cal.getTime()
              }
          })
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Guardar", preferredSize: UI_Standards.BUTTON_SIZE,
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
    if ( selectedDateStart == null || selectedDateEnd == null ) {
      selectedDateStart = DateUtils.truncate( new Date(), Calendar.MONTH )
      selectedDateEnd = DateUtils.truncate( new Date(), Calendar.DATE )
    }
  }

  private void fillFields( String bill ){
    Order order = OrderController.findOrderByTicketJava( bill )
    if( order != null && StringUtils.trimToEmpty(order.id).length() > 0 ){
      txtRx.text = StringUtils.trimToEmpty(order?.rx?.toString())
      txtCustomer.text = StringUtils.trimToEmpty(order?.customer?.fullName)
      FormaContactoJava formaContacto = ContactController.findFCbyRx(bill)
      if( formaContacto != null ){
        txtContactType.text = StringUtils.trimToEmpty( StringUtils.trimToEmpty(formaContacto.tipoContacto.descripcion) )
      }
      txtContact.text = StringUtils.trimToEmpty( StringUtils.trimToEmpty(formaContacto.contacto) )
    }
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
    OrderController.reschedule( bill, date.value as Date )
    dispose()
  }



}
