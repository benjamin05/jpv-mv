package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.java.repository.JbNotasJava
import mx.lux.pos.java.repository.JbServiciosJava
import mx.lux.pos.java.repository.JbTrack
import mx.lux.pos.model.JbServicios
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Customer
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import mx.lux.pos.ui.model.User
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.List

class ServiceOrderDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtApePaterno
  private JTextField txtApeMaterno
  private JTextField txtNombre
  private JTextArea txtDejo
  private JComboBox cbServicio
  private JTextArea txtInstruccion
  private JTextField txtFechaPromesa
  private JTextArea txtCondGenerales

  private List<JbServiciosJava> lstServicios
  private Customer customer
  private Date validDate
  private String rx

  public boolean button = false

  ServiceOrderDialog( Customer customer ) {
    rx = ""
    lstServicios = OrderController.findJbServices( )
    this.customer = customer
    buildUI()
    doBindings()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Orden de Servicio",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 530, 550 ],
        location: [ 200, 100 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 2", "20[][grow,fill]40", "20[]10[]" ) ) {
          label( text: "Apellido Paterno" )
          txtApePaterno = textField( editable: false )
          label( text: "Apellido Materno" )
          txtApeMaterno = textField( editable: false )
          label( text: "Nombre" )
          txtNombre = textField( editable: false )
          label( text: "Dejo" )
          txtDejo = textArea( preferredSize: [ 100 , 70 ], lineWrap: true )
          label( text: "Servicio" )
          cbServicio = comboBox( items: lstServicios*.servicio )
          label( text: "Instruccion" )
          txtInstruccion = textArea( preferredSize: [ 100 , 70 ], lineWrap: true )
          label( text: "Fecha Promesa" )
          txtFechaPromesa = textField( )
          label( text: "Condiciones Generales" )
          txtCondGenerales = textArea( preferredSize: [ 100 , 70 ], lineWrap: true )
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
  protected void doBindings( ) {
    txtApePaterno.text = StringUtils.trimToEmpty( customer?.fathersName )
    txtApeMaterno.text = StringUtils.trimToEmpty( customer?.mothersName )
    txtNombre.text = StringUtils.trimToEmpty( customer?.name )
    txtFechaPromesa.text = new Date().format("dd-MM-yyyy")
  }

  // Public Methods
  void activate( ) {

  }

  Date getSelectedDateStart( ) {
    return selectedDateStart
  }

  Date getSelectedDateEnd( ) {
    return selectedDateEnd
  }

  void setDefaultDates( Date pDateStart, Date pDateEnd ) {

  }

  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    if( validData() ){
      User user = Session.get(SessionItem.USER) as User
      String servicio = cbServicio.selectedItem as String
      JbNotasJava jbNotas = new JbNotasJava()
      jbNotas.idNota = OrderController.idJbNota( )
      jbNotas.idCliente = StringUtils.trimToEmpty(customer.id.toString())
      jbNotas.cliente = StringUtils.trimToEmpty(customer.onlyFullName)
      jbNotas.dejo = StringUtils.trimToEmpty(txtDejo.text)
      jbNotas.instruccion = StringUtils.trimToEmpty(txtInstruccion.text)
      jbNotas.emp = StringUtils.trimToEmpty(user.username)
      jbNotas.servicio = StringUtils.trimToEmpty(servicio)
      jbNotas.condicion = StringUtils.trimToEmpty(txtCondGenerales.text)
      jbNotas.fechaProm = validDate
      jbNotas.fechaOrden = new Date()
      jbNotas.fechaMod = new Date()
      jbNotas.tipoServ = "SERVICIO"
      jbNotas.idMod = "0"

      JbJava jb = new JbJava()
      jb.rx = "S"+StringUtils.trimToEmpty(jbNotas.idNota.toString())
      jb.estado = 'PE'
      jb.idCliente = StringUtils.trimToEmpty(customer.id.toString())
      jb.empAtendio = StringUtils.trimToEmpty(user.username)
      jb.numLlamada = 0
      jb.material = StringUtils.trimToEmpty(txtDejo.text)
      jb.jbTipo = 'OS'
      jb.fechaPromesa = validDate
      jb.fechaMod = new Date()
      jb.cliente = customer.onlyFullName
      jb.idMod = "0"
      jb.noLlamar = false
      jb.fechaVenta = new Date()
      jb.noEnviar = false

      JbTrack jbTrack = new JbTrack()
      jbTrack.rx = "S"+StringUtils.trimToEmpty(jbNotas.idNota.toString())
      jbTrack.estado = "PE"
      jbTrack.obs = StringUtils.trimToEmpty(txtDejo.text)
      jbTrack.emp = StringUtils.trimToEmpty(user.username)
      jbTrack.fecha = new Date()
      jbTrack.idMod = "0"

      OrderController.saveJbs( jbNotas, jb, jbTrack )
      OrderController.printJbNota( jbNotas )
      OrderController.printJbNota( jbNotas )
      rx = StringUtils.trimToEmpty(jb.rx)
      dispose()
    } else {
      sb.optionPane(message: 'Verifique los datos',messageType: JOptionPane.ERROR_MESSAGE).
            createDialog(this, 'Error').show()
    }
  }



  private Boolean validData(){
    Boolean valid = true
    if(StringUtils.trimToEmpty(txtDejo.text).length() <= 0 || StringUtils.trimToEmpty(cbServicio.selectedItem as String).length() <= 0 ||
          StringUtils.trimToEmpty(txtInstruccion.text).length() <= 0 || StringUtils.trimToEmpty(txtFechaPromesa.text).length() <= 0 ||
            StringUtils.trimToEmpty(txtCondGenerales.text).length() <= 0){
      valid = false
    }
      if( StringUtils.trimToEmpty(txtFechaPromesa.text).length() > 0 ){
          SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy")
          SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy")
          SimpleDateFormat df2 = new SimpleDateFormat("ddMMyyyy")
          Integer dayInt = 0
          Integer monthInt = 0
          Integer yearInt = 0
          Date today = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH );
          try{
              dayInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtFechaPromesa.text).substring(0,2))
              monthInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtFechaPromesa.text).substring(3,5))
              yearInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtFechaPromesa.text).substring(6,10))
              validDate = df.parse( StringUtils.trimToEmpty(txtFechaPromesa.text) )
              Calendar fecha = new GregorianCalendar();
              Integer currentYear = fecha.get(Calendar.YEAR);
              if( validDate != null && (validDate.equals(today) || validDate.after(today)) && (dayInt <= 31 && monthInt <= 12 && yearInt >= currentYear) ){
                  println "fecha valida"
              } else {
                  valid = false
              }
          } catch ( Exception e ) {println e}
          if( validDate == null ){
              try{
                  dayInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtFechaPromesa.text).substring(0,2))
                  monthInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtFechaPromesa.text).substring(3,5))
                  yearInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtFechaPromesa.text).substring(6,10))
                  validDate = df1.parse( StringUtils.trimToEmpty(txtFechaPromesa.text) )
                  Calendar fecha = new GregorianCalendar();
                  Integer currentYear = fecha.get(Calendar.YEAR);
                  if( validDate != null && (validDate.equals(today) || validDate.after(today)) && (dayInt <= 31 && monthInt <= 12 && yearInt >= currentYear) ){
                      println "fecha valida"
                  } else {
                      valid = false
                  }
              } catch ( Exception e ) {println e}
          }
          if( validDate == null ){
              try{
                  dayInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtFechaPromesa.text).substring(0,2))
                  monthInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtFechaPromesa.text).substring(3,5))
                  yearInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtFechaPromesa.text).substring(6,10))
                  validDate = df2.parse( StringUtils.trimToEmpty(txtFechaPromesa.text) )
                  Calendar fecha = new GregorianCalendar();
                  Integer currentYear = fecha.get(Calendar.YEAR);
                  if( validDate != null && (validDate.equals(today) || validDate.after(today)) && (dayInt <= 31 && monthInt <= 12 && yearInt >= currentYear) ){
                      println "fecha valida"
                  } else {
                      valid = false
                  }
              } catch ( Exception e ) {println e}
          }
      }
    return valid
  }


  public String getRx(){
    return rx
  }


}
