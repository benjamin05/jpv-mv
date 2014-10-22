package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.MainWindow
import mx.lux.pos.ui.controller.AccessController
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.model.User
import net.miginfocom.swing.MigLayout

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent

class AuthorizationSPDialog extends JDialog {

  private SwingBuilder sb
  private String definedMessage
  private JTextField username
  private JPasswordField password
  private JLabel fullName
  private JLabel messages
  private boolean authorized

    AuthorizationSPDialog( Component parent, String message ) {
    sb = new SwingBuilder()
    definedMessage = message ?: ''
    authorized = false
    buildUI( )
  }

  boolean isAuthorized( ) {
    return authorized
  }

  private void buildUI(  ) {
    sb.dialog( this,
        title: "Autorizar Operaci\u00f3n",
        location: [20,20],
        resizable: false,
        modal: true,
        pack: true,
        layout: new MigLayout( 'fill,wrap,center', '[fill,350]' )
    ) {
      label( definedMessage, font: new Font( '', Font.BOLD, 14 ) )

      panel( border: titledBorder( 'Ingresar datos:' ),
          layout: new MigLayout( 'fill,wrap 2', '[][fill,130!]', '[fill,25!]' )
      ) {
        label( 'Contrase\u00f1a' )
        password = passwordField( document: new UpperCaseDocument(), horizontalAlignment: JTextField.CENTER )
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
    authorized = AccessController.validPass( password.text )
    if ( authorized ) {
      dispose()
    } else {
      messages.text = 'Contrase\u00f1a incorrecta'
      messages.visible = true
    }
    password.text = null
    source.enabled = true
  }


}
