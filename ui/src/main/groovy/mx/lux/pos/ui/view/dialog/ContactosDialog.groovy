package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.FormaContactoJava
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.java.repository.TipoContactoJava
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.OrderController
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

class ContactosDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtCustomer
  private JTextField txtMaterial
  private JTextField txtBalance
  private JTextField txtContactType
  private JTextField txtContact
  private JTextField txtContacts
  private JTextField txtObservations
  private JTextField txtInfo
  private JTextField txtDays
  private JSpinner date
  private String rx
  private JbJava jb
  private FormaContactoJava tipoContacto
  private JButton btnSave

  public boolean button = false

  ContactosDialog( String rx ) {
    this.rx = StringUtils.trimToEmpty(rx)
    this.jb = OrderController.findJbByRx( rx )
    this.tipoContacto = OrderController.findWayContactByBill( StringUtils.trimToEmpty(rx) )
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Rx: "+rx,
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 700, 500 ],
        location: [ 200, 150 ],
    ) {
      panel() {
        borderLayout()
        panel( border: titledBorder(title: ''), constraints: BorderLayout.PAGE_START, layout: new MigLayout( "wrap 2", "[][grow,fill]", "" ) ) {
          label( text: "Cliente" )
          txtCustomer = textField( text: jb.cliente, editable: false )
          label( text: "Material" )
          txtMaterial = textField( text: jb.material, editable: false )
          label( text: "Saldo" )
          txtBalance = textField( text: jb.saldo, editable: false )
          label( text: "Tipo Contacto" )
          txtContactType = textField( text: tipoContacto?.tipoContacto?.descripcion, editable: false )
          label( text: "Contacto" )
          txtContactType = textField( text: tipoContacto?.contacto, editable: false )
          label( text: "Contactos" )
          txtContactType = textField( text: "1", editable: false )
          label( text: "Observaciones" )
          txtContactType = textField( text: jb.obsExt, editable: false )
        }
        panel( border: titledBorder(title: ''), constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 4", "[][grow,fill]", "" ) ) {
          label( text: "Info." )
          txtInfo = textField( constraints: "span 3", enabled: false )
          label( text: "Volver a Contactar" )
          date = spinner( model: spinnerDateModel() )
          date.editor = new JSpinner.DateEditor( date as JSpinner, 'dd-MM-yyyy' )
          date.value = DateUtils.addDays( new Date(), Registry.callAgain )
          date.enabled = false
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
            label( text: "o dias" )
          txtDays = textField( StringUtils.trimToEmpty(Registry.callAgain.toString()), preferredSize: [ 60, 21 ], enabled: false )
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
          label( " " )
          button( text: "Enviar", preferredSize: UI_Standards.BUTTON_SIZE,
                  actionPerformed: { onButtonSend() }
          )
          button( text: "No Realizado", preferredSize: UI_Standards.BUTTON_SIZE,
                  actionPerformed: { onButtonNotSend() }, constraints: "span 2"
          )
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            btnSave = button( text: "Guardar", preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onButtonOk() }, enabled: false
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



  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    if( date.enabled && txtDays.enabled ){
      if( StringUtils.trimToEmpty(txtInfo.text).length() > 0 ){
        OrderController.saveCall(rx, txtInfo.text, date.value as Date)
        dispose()
      } else {
        sb.optionPane(message: 'Debe llenar el campo Info.',messageType: JOptionPane.ERROR_MESSAGE).
                createDialog(this, 'Error').show()
      }
    } else {
      OrderController.saveCallNotDone(rx, txtInfo.text)
      dispose()
    }
  }

  protected void onButtonSend( ) {
    txtInfo.enabled = true
    date.enabled = true
    txtDays.enabled = true
    btnSave.enabled = true
  }

  protected void onButtonNotSend( ) {
    txtInfo.enabled = true
    date.enabled = false
    txtDays.enabled = false
    btnSave.enabled = true
  }

}
