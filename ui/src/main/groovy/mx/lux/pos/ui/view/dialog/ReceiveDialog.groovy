package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.java.repository.NotaVentaJava
import mx.lux.pos.java.repository.RecetaJava
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Rx
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class ReceiveDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtCustomer
  private JTextField txtMaterial
  private JTextField txtSellDate
  private JTextField txtPromiseDate
  private JTextField txtObservationsRx
  private JTextField txtObservationsBill
  private JTextField txtdestinationSite
  private JCheckBox cbExternal
  private Date selectedDateStart
  private Date selectedDateEnd

  private JbJava jb
  private NotaVentaJava nota
  private Rx receta
  private String viaje

  public boolean button = false

  ReceiveDialog( JbJava jb, String viaje ) {
    this.jb = jb
    this.viaje = viaje
    nota = OrderController.findOrderJavaByBill( StringUtils.trimToEmpty(jb.rx) )
    receta = OrderController.findRxByBill( StringUtils.trimToEmpty(jb.rx) )
    buildUI()
    doBindings()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Recepcion",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 500, 450 ],
        location: [ 100, 150 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 2", "20[][grow,fill]40", "20[]10[]" ) ) {
          label( text: "Cliente" )
          txtCustomer = textField( editable: false )
          label( text: "Material" )
          txtMaterial = textField( editable: false )
          label( text: "Fecha Venta" )
          txtSellDate = textField( editable: false )
          label( text: "Fecha Promesa" )
          txtPromiseDate = textField( editable: false )
          label( text: "Observasiones Rx" )
          txtObservationsRx = textField( editable: false )
          label( text: "Observaciones Factura" )
          txtObservationsBill = textField( editable: false )
          /*label( text: "Externo" )
          cbExternal = checkBox( enabled: false )
          label( text: "Sucursal Destino" )
          txtdestinationSite = textField( editable: false )*/
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Aceptar", preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onButtonOk() }
            )
            /*button( text: "No Satisfactorio", preferredSize: [140,35],
                actionPerformed: { onButtonNotOk() }
            )*/
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



  private void doBindings() {
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy")
    txtCustomer.text = StringUtils.trimToEmpty( jb.cliente )
    txtMaterial.text = StringUtils.trimToEmpty( jb.material )
    txtSellDate.text = df.format(jb.fechaVenta)
    txtPromiseDate.text = df.format(jb.fechaPromesa)
    txtObservationsBill.text = nota != null ? nota.observacionesNv : ""
    txtObservationsRx.text = receta != null ? receta.observacionesR : ""
    //txtdestinationSite.text = ""
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
    OrderController.receivedJb( jb.rx, viaje, "ENTREGAR" )
    dispose()
  }

  protected void onButtonNotOk( ) {
    OrderController.receivedJb( jb.rx, viaje, "RETRASADO" )
    dispose()
  }


}
