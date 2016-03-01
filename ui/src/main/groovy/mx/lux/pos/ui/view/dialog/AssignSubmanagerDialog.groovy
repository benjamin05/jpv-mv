package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.AccessController
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.model.User
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.text.NumberFormat

class AssignSubmanagerDialog extends JDialog {

  private SwingBuilder sb
  private String definedMessage
  private JTextField username
  private JSpinner initialDate
  private JSpinner finalDate
  private JTextField hours
  private JPasswordField password
  private JLabel fullName
  private JLabel messages
  private JRadioButton rbDays
  private JRadioButton rbHours
  private ButtonGroup group
  private boolean authorized
  private Date today = new Date()

    AssignSubmanagerDialog( Component parent, String message ) {
    sb = new SwingBuilder()
    definedMessage = message ?: ''
    authorized = false
    buildUI( parent )
  }

  boolean isAuthorized( ) {
    return authorized
  }

  private void buildUI( Component parent ) {
    sb.dialog( this,
        title: "Asignar Subgerente",
        location: parent.locationOnScreen,
        resizable: false,
        modal: true,
        pack: true,
        preferredSize: [400, 350],
        layout: new MigLayout( 'fill,wrap,center', '[fill,grow]' ),
    ) {
      label( definedMessage, font: new Font( '', Font.BOLD, 14 ) )

      panel( layout: new MigLayout( 'fill,wrap 2', '[][fill,130!]', '[fill,25!]' ) ) {
        label( 'Empleado' )
        username = textField( document: new UpperCaseDocument(),
            horizontalAlignment: JTextField.CENTER,
            keyReleased: usernameChanged
        )

        fullName = label( constraints: 'span' )

        group = buttonGroup()
        rbDays = radioButton( text:"Por dias", buttonGroup:group, actionPerformed: onSelect, constraints:"span 2", selected: true)
        label( 'Fecha Inicial:' )
        initialDate = spinner( model: spinnerDateModel() )
        initialDate.editor = new JSpinner.DateEditor( initialDate as JSpinner, 'dd-MM-yyyy' )
        initialDate.value = new Date()
        label( 'Fecha Final:' )
        finalDate = spinner( model: spinnerDateModel() )
        finalDate.editor = new JSpinner.DateEditor( finalDate as JSpinner, 'dd-MM-yyyy' )
        //finalDate.value = DateUtils.addDays( today, 1 )
        rbHours = radioButton( text:"Por Horas", buttonGroup:group, actionPerformed: onSelect, constraints:"span 2")
        label( 'Horas:' )
        hours = textField( enabled: false )
        messages = label( foreground: Color.RED, constraints: 'span' )
      }

      panel( layout: new MigLayout( 'right', '[fill,100!]' ) ) {
        button( 'Aceptar', defaultButton: true, actionPerformed: doAuthorize )
        button( 'Cancelar', actionPerformed: {dispose()} )
      }
    }
  }

  private def usernameChanged = { KeyEvent ev ->
    JTextField source = ev.source as JTextField
    sb.doOutside {
      User user = AccessController.getUser( source.text )
      fullName.text = user?.fullName ?: null
    }
    pack()
  }

  private def doAuthorize = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    if ( fullName.text != null ) {
      if( !AccessController.existSubmanager() ){
      if( rbDays.selected ){
        Date today = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH );
        Date dayStart = rbDays.selected ? initialDate.value as Date : null
        Date dayEnd = rbDays.selected ? new Date( DateUtils.ceiling( finalDate.value as Date, Calendar.DAY_OF_MONTH ).getTime() - 1 ) : null
        if(dayStart != null && dayEnd != null && dayStart.compareTo(today) >= 0 && dayStart.compareTo(dayEnd) < 0){
          if( AccessController.saveSubManager( StringUtils.trimToEmpty(username.text), dayStart, dayEnd, null ) ){
            sb.optionPane(message: 'Se registro el subgerente correctamente', messageType: JOptionPane.DEFAULT_OPTION).
                    createDialog(this, 'Registro exitoso')
                    .show()
            dispose()
          } else {
            sb.optionPane(message: 'Error al registrar subgerente', messageType: JOptionPane.ERROR_MESSAGE).
                    createDialog(this, 'Error')
                    .show()
          }
        } else {
          messages.text = 'Verifique las fechas'
          messages.visible = true
        }
      } else if( rbHours.selected ){
        if( StringUtils.trimToEmpty(hours.text).length() > 0 && StringUtils.trimToEmpty(hours.text).isNumber() ){
          Integer hoursInt = 0
          try{
            hoursInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(hours.text))
          } catch ( NumberFormatException e ){
            println e.message
          }
          if(AccessController.saveSubManager( StringUtils.trimToEmpty(username.text), null, null, hoursInt )){
            sb.optionPane(message: 'Se registro el subgerente correctamente', messageType: JOptionPane.DEFAULT_OPTION).
                    createDialog(this, 'Registro exitoso')
                    .show()
            dispose()
          } else {
            sb.optionPane(message: 'Error al registrar subgerente', messageType: JOptionPane.ERROR_MESSAGE).
                    createDialog(this, 'Error')
                    .show()
          }
        } else {
          messages.text = 'Verifique las horas'
          messages.visible = true
        }
      }
      } else {
        messages.text = 'Ya existe un empleado como subgerente'
        messages.visible = true
      }
    } else {
      messages.text = 'Empleado/Contrase\u00f1a incorrectos'
      messages.visible = true
    }
    source.enabled = true
  }



    private def onSelect = { ActionEvent ev ->
      if(rbDays.selected){
        hours.enabled = false
        initialDate.enabled = true
        finalDate.enabled = true
      } else if(rbHours.selected){
        hours.enabled = true
        initialDate.enabled = false
        finalDate.enabled = false
      }
    }
}
