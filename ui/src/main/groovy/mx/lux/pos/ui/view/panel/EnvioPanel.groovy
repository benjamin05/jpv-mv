package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.model.IPromotionAvailable
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.InvoiceController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.controller.TaxpayerController
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.view.dialog.PopUpMenu
import mx.lux.pos.ui.view.dialog.SuggestedTaxpayersDialog
import mx.lux.pos.ui.view.renderer.MoneyCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.util.List

class EnvioPanel extends JPanel {

  private static final String DATE_TIME_FORMAT = 'dd-MM-yyyy HH:mm'

  private SwingBuilder sb
  private JLabel lblCliente
  private JLabel lblFolio
  private JLabel lblFecha
  private JLabel lblEstatus
  private JLabel lblCallNum
  private JLabel lblColonia
  private JLabel lblEstado
  private JLabel lblPais
  private JLabel lblCP
  private JLabel lblCiudad
  private JTextField txtViaje
  private JTextField txtFolio
  private JCheckBox cbExtranjero
  private JCheckBox cbDesgloseLente
  private JCheckBox cbDesgloseLenteArmazon
  private JCheckBox cbDesgloseRx
  private JCheckBox cbDesgloseCliente
  private JComboBox cbEstado
  private JComboBox cbCorreo
  private JButton searchButton
  private JSpinner dateEnd
  private JButton editButton
  private JButton printInvoiceButton
  private JButton printReferenceButton
  private JButton displayButton
  private JButton requestButton
  private boolean invoiced
  private boolean editable
  private Branch branch
  private String estadoDefault
  private List<JbJava> lstBySend
  private List<JbJava> lstNotSend
  private List<String> dominios
  private Order order
  private Invoice invoice
  private Date today = new Date()
  private DefaultTableModel devModel
  private DefaultTableModel noSendModel
  private DefaultTableModel bySendModel
  private String travel

  private static final String TAG_CANCELADO = 'T'

  EnvioPanel( ) {
    sb = new SwingBuilder()
    lstBySend = OrderController.jbBySend()
    lstNotSend = OrderController.jbNotSend()
    travel = OrderController.findCurrentTravel()
    /*invoiced = false
    editable = false
    branch = Session.get( SessionItem.BRANCH ) as Branch
    estadoDefault = CustomerController.findDefaultState()
    estados = CustomerController.findAllStates()
    dominios = CustomerController.findAllCustomersDomains()
    order = new Order()
    invoice = new Invoice( state: estadoDefault )*/
    buildUI()
    //doBindings()
  }

  private void buildUI( ) {
    sb.panel( this, layout: new MigLayout('wrap', '[fill,grow]', '[fill,grow]') ) {
      panel( layout: new MigLayout( 'center,wrap 6', '[][fill,120!]30[][fill,100!]30[][fill,100!]' ) ) {
          label( 'Fecha' )
          dateEnd = spinner( model: spinnerDateModel(), enabled: false )
          dateEnd.editor = new JSpinner.DateEditor( dateEnd as JSpinner, 'dd/MM/yyyy' )
          dateEnd.value = today
          dateEnd.addChangeListener( new ChangeListener() {
              @Override
              void stateChanged( ChangeEvent e ) {
                  /*if ( dateEnd.value < dateStart.value ) {
                      dateStart.value = DateUtils.addDays( dateEnd.value as Date, -10 )
                  }*/
              }
          } )
          label( 'Viaje' )
          txtViaje = textField( text: travel, enabled: false )
          label( 'Folio' )
          txtFolio = textField( )
      }

      panel( layout: new MigLayout( 'wrap 2', '[fill,200!,center]110[fill,200!,center]110[fill,200!,center]', '[270!]' ) ) {
        /*scrollPane( ) {
          table(selectionMode: ListSelectionModel.SINGLE_SELECTION) {
            devModel = tableModel(list: new ArrayList<String>()) {
              propertyColumn(header: "Devoluciones SP", propertyName: "", editable: false)
            } as DefaultTableModel
          }
        }*/
        scrollPane( ) {
          table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowItemClickNotSend) {
            noSendModel = tableModel(list: lstNotSend) {
              closureColumn(
                header: 'No Enviar',
                read: { JbJava tmp -> tmp?.rx },
                minWidth: 200,
                maxWidth: 200
              )
            } as DefaultTableModel
          }
        }
        scrollPane( ) {
          table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowItemClickSend) {
            bySendModel = tableModel(list: lstBySend) {
              closureColumn(
                header: 'Enviar',
                read: { JbJava tmp -> tmp?.rx },
                minWidth: 200,
                maxWidth: 200
              )
            } as DefaultTableModel
          }
        }
      }

      panel( layout: new MigLayout( 'center', '80[fill,100!]80' ) ) {
        button( 'Cerrar Via'  )//actionPerformed: doPrintInvoice )
        button( 'Reimpresion' )//actionPerformed: doPrintReference )
        button( 'Previo' )//actionPerformed: doShowInvoice )
        button( 'Actualizar' )//actionPerformed: doRequest )
      }
    }
  }

  private doBindings( ) {
    sb.build {
      lstBySend = OrderController.jbBySend()
      lstNotSend = OrderController.jbNotSend()
      travel = OrderController.findCurrentTravel()
      bySendModel.fireTableDataChanged()
      noSendModel.fireTableDataChanged()
    }

  }

  private void fillEmailFields( String email ) {
    List<String> tokens = StringUtils.splitPreserveAllTokens( email, '@' )
    if ( tokens?.any() ) {
      txtCorreo.text = tokens.get( 0 )
      cbCorreo.selectedItem = tokens.get( 1 )
    }
  }

  private void fillTaxpayerFields( Taxpayer taxpayer ) {
    if ( taxpayer?.id ) {
      rfcInput.text = taxpayer.rfc
      txtRazonSocial.text = taxpayer.name
      txtCallNum.text = taxpayer.primary
      txtCol.text = taxpayer.location
      txtDelMun.text = taxpayer.city
      cbEstado.selectedItem = CustomerController.findStateById( taxpayer.stateId )
      txtCP.text = taxpayer.zipcode
      fillEmailFields( taxpayer.email )
    }
  }

  private void clearTaxpayerFields( ) {
    rfcInput.text = null
    txtRazonSocial.text = null
    txtCallNum.text = null
    txtCol.text = null
    txtDelMun.text = null
    cbEstado.selectedItem = null
    txtCP.text = null
    txtCorreo.text = null
    cbCorreo.selectedItem = null
  }

  private void fillInvoiceFields( Invoice invoceTmp ) {
    if ( invoceTmp?.id ) {
      invoice = invoceTmp
      invoiced = true
      editable = false
      lblEstatus.text = 'Facturado'
      fillEmailFields( invoceTmp.email )
    } else {
      invoiced = false
      editable = true
      lblEstatus.text = 'Sin Facturar'
    }
    doBindings()
  }

  private void clearFields( ) {
    editable = false
    invoiced = false
    lblEstatus.text = null
    clearTaxpayerFields()
    order = new Order()
    invoice = new Invoice( state: estadoDefault )
    cbDesgloseLente.selected = true
    cbDesgloseRx.selected = true
    cbDesgloseCliente.selected = true
    cbDesgloseLenteArmazon.selected = false
    doBindings()
  }

  private def doTicketSearch = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    clearFields()
    Order orderTmp = OrderController.findOrderByTicket( txtTicket.text )
    if ( orderTmp?.id ) {
      if ( orderTmp.due.compareTo(new BigDecimal(5)) <= 0 ) {
        if( TAG_CANCELADO.compareToIgnoreCase(orderTmp.status) ){
          if( OrderController.amountvalid( txtTicket.text ) ){
        order = orderTmp
        txtTicket.enabled = false
        if ( CustomerType.FOREIGN.equals( orderTmp.customer?.type ) ) {
          cbExtranjero.selected = true
          txtDelMun.text = orderTmp.customer?.address?.city
          txtPais.text = orderTmp.customer?.address?.country
          cbDesglose.selected = true
        }
        Invoice invoceTmp = InvoiceController.findInvoiceByTicket( txtTicket.text )
        fillInvoiceFields( invoceTmp )
          } else {
              sb.optionPane(
                      message: "No existe monto a facturar",
                      messageType: JOptionPane.ERROR_MESSAGE
              ).createDialog( this, 'Ticket sin monto' )
                      .show()
          }
        } else {
          sb.optionPane(
              message: "Ticket cancelado",
              messageType: JOptionPane.ERROR_MESSAGE
          ).createDialog( this, 'No se puede facturar ticket' )
              .show()
        }
      } else {
        sb.optionPane(
            message: "No se puede facturar ticket: ${txtTicket.text} debido a que tiene un saldo pendiente",
            messageType: JOptionPane.ERROR_MESSAGE
        ).createDialog( this, 'No se puede facturar ticket' )
            .show()
      }
    } else {
      sb.optionPane(
          message: "No se encontraron resultados para el ticket: ${txtTicket.text}",
          messageType: JOptionPane.ERROR_MESSAGE
      ).createDialog( this, 'Sin resultados' )
          .show()
    }
    source.enabled = true
  }

  private def doEdit = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    editable = true
    doBindings()
    source.enabled = true
  }

  private def doRfcSearch = { ActionEvent ev ->
    JTextField source = ev.source as JTextField
    String input = source.text
    if ( StringUtils.isNotBlank( input ) ) {
      clearTaxpayerFields()
      sb.doOutside {
        List<Taxpayer> results = TaxpayerController.findTaxpayersLike( input )
        if ( results?.any() ) {
          if ( results.size() == 1 ) {
            fillTaxpayerFields( results.first() )
          } else {
            SuggestedTaxpayersDialog dialog = new SuggestedTaxpayersDialog( source, input, results )
            dialog.show()
            fillTaxpayerFields( dialog.taxpayer )
          }
          doBindings()
        } else {
          sb.optionPane(
              message: "No se encontraron resultados para el RFC: ${input}",
              messageType: JOptionPane.ERROR_MESSAGE
          ).createDialog( this, 'Sin resultados' )
              .show()
        }
      }
    } else {
      sb.optionPane(
          message: 'Es necesario ingresar una b\u00fasqeda v\u00e1lida',
          messageType: JOptionPane.ERROR_MESSAGE
      ).createDialog( this, 'B\u00fasqueda inv\u00e1lida' )
          .show()
    }
  }

  private def doToggleForeign = {
    if ( cbExtranjero.selected ) {
      lblCallNum.visible = false
      txtCallNum.visible = false
      txtCallNum.text = null
      lblColonia.visible = false
      txtCol.visible = false
      txtCol.text = null
      lblEstado.visible = false
      cbEstado.visible = false
      cbEstado.selectedItem = estadoDefault
      lblPais.visible = true
      txtPais.visible = true
      txtPais.text = null
      lblCP.visible = false
      txtCP.visible = false
      txtCP.text = null
      lblCiudad.text = 'Ciudad'
      rfcInput.text = 'XEXX010101000'
      txtRazonSocial.text = order?.customer?.fullName ?: ''
    } else {
      lblCallNum.visible = true
      txtCallNum.visible = true
      lblColonia.visible = true
      txtCol.visible = true
      lblEstado.visible = true
      cbEstado.visible = true
      lblCP.visible = true
      txtCP.visible = true
      lblPais.visible = false
      txtPais.visible = false
      txtPais.text = 'MEXICO'
      lblCiudad.text = 'Delegaci\u00f3n/Municipio'
      rfcInput.text = null
      txtRazonSocial.text = ''
    }
  }

  private boolean isValidInput( ) {
    if ( InvoiceController.isValidRfc( rfcInput.text ) ) {
      if( ( txtCorreo.text.length() > 0 && ( cbCorreo.getSelectedItem() != null ) )
      || ( txtCorreo.text.length() <= 0 && cbCorreo.getSelectedItem() == null ) ){
        return true
      } else {
        sb.optionPane(
            message: "Direccion de correo inv\u00e1lida",
            messageType: JOptionPane.INFORMATION_MESSAGE
        ).createDialog( this, 'Verificar Datos' )
            .show()
        return false
      }
    } else {
      sb.optionPane(
          message: "RFC inv\u00e1lido",
          messageType: JOptionPane.INFORMATION_MESSAGE
      ).createDialog( this, 'Verificar Datos' )
          .show()
      return false
    }
  }


  public void limpiaPantalla(){
    lstBySend = OrderController.jbBySend()
    lstNotSend = OrderController.jbNotSend()
    travel = OrderController.findCurrentTravel()
    bySendModel.fireTableDataChanged()
    noSendModel.fireTableDataChanged()
  }


  private def doPrintInvoice = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    InvoiceController.printInvoice( invoice.invoiceId )
    source.enabled = true
  }

  private def doPrintReference = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    InvoiceController.printInvoiceReference( invoice.invoiceId )
    source.enabled = true
  }

  private def doShowInvoice = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    InvoiceController.showInvoice( invoice.invoiceId )
    source.enabled = true
  }

  private def doRequest = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    if ( isValidInput() ) {
      Invoice invoiceTmp = InvoiceController.requestInvoice( invoice, cbDesgloseLente.selected, cbDesgloseRx.selected,
              cbDesgloseCliente.selected, cbDesgloseLenteArmazon.selected )
      if ( invoiceTmp?.id ) {
        fillInvoiceFields( invoiceTmp )
        sb.optionPane(
            message: "Ticket facturado correctamente, folio fiscal: ${invoiceTmp.invoiceId}",
            messageType: JOptionPane.INFORMATION_MESSAGE
        ).createDialog( this, 'Ticket facturado correctamente' )
            .show()
        InvoiceController.printInvoiceReference( invoiceTmp.invoiceId )
      } else {
        sb.optionPane(
            message: "Ocurrio un error al facturar ticket, intente nuevamente",
            messageType: JOptionPane.ERROR_MESSAGE
        ).createDialog( this, 'No se puede facturar ticket' )
            .show()
      }
    }
    source.enabled = true
  }


  private def doShowItemClickSend = { MouseEvent ev ->
    if (SwingUtilities.isRightMouseButton(ev)) {
      JbJava selectedData = ev.source.selectedElement as JbJava
      if( selectedData != null ){
        PopUpMenu menu = new PopUpMenu( ev.component, ev.component.getX(), ev.component.getY(), StringUtils.trimToEmpty(selectedData.rx), "envio,send", this );
        doBindings()
      }
    }
  }

  private def doShowItemClickNotSend = { MouseEvent ev ->
    if (SwingUtilities.isRightMouseButton(ev)) {
      JbJava selectedData = ev.source.selectedElement as JbJava
      if( selectedData != null ){
        PopUpMenu menu = new PopUpMenu( ev.component, ev.component.getX(), ev.component.getY(), StringUtils.trimToEmpty(selectedData.rx), "envio,notsend", this );
        doBindings()
      }
    }
  }
}
