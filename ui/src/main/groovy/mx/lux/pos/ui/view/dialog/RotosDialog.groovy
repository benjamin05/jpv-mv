package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.JbRotos
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.OrderItem
import mx.lux.pos.ui.model.UpperCaseDocument
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
import java.text.NumberFormat
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

  Date validDate = null

  private Order order

  public boolean button = false

  RotosDialog( Order order ) {
    this.order = order
    buildUI()
    if(order != null){
      txtRx.requestFocus()
    }
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
          txtRx = textField( text: order != null ? order.bill : "" )
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
            txtCauseRoto = textField( constraints: 'span2', document: new UpperCaseDocument() )
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
        for(OrderItem oi : order.items){
          if( StringUtils.trimToEmpty(oi.item.type).equalsIgnoreCase("B") ){
            txtMaterial.text = StringUtils.trimToEmpty(oi?.item?.name)
          }
        }
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
      JbRotos jbRotos = new JbRotos()
      jbRotos.rx = StringUtils.trimToEmpty(order.bill)
      jbRotos.tipo = cbFrame.selected ? "A" : "B"
      jbRotos.material = StringUtils.trimToEmpty(txtMaterial.text)
      jbRotos.causa = StringUtils.trimToEmpty(txtCauseRoto.text)
      jbRotos.emp = StringUtils.trimToEmpty(txtResponsable.text)
      jbRotos.numRoto = OrderController.getRotoNumber(txtRx.text)+1
      jbRotos.alta = true
      jbRotos.fechaProm = validDate
      jbRotos.llamada = cbYes.selected
      jbRotos.fecha = new Date()
      jbRotos.idMod = '0'
      OrderController.saveJbRoto( jbRotos )
      OrderController.updateJbAndNotaVenta( jbRotos.rx, validDate, jbRotos.causa )
      if( cbYes.selected ){
        OrderController.saveJbLlamadaPend( jbRotos.rx )
      }
      if(StringUtils.trimToEmpty(jbRotos.tipo).equalsIgnoreCase("A")){
        OrderController.saveJbSobre( jbRotos.rx )
        OrderController.printRoto( jbRotos )
      }
      dispose()
    } else {
      sb.optionPane(message: 'Verifique los datos').createDialog(txtPromiseDate, 'Datos incorrectos').show()
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
    if( order == null || StringUtils.trimToEmpty(order.id).length() <= 0 ){
      cbYes.enabled = false
      cbNo.enabled = false
      txtCauseRoto.enabled = false
      txtResponsable.enabled = false
      txtMaterial.enabled = false
      txtPromiseDate.enabled = false
      btnGuardar.enabled = false
    }
  }


  Boolean validData( ){
    Boolean valid = true
    if( !cbFrame.selected && !cbLens.selected ){
      valid = false
    } else if( StringUtils.trimToEmpty(txtCauseRoto.text).length() <= 0 ){
      valid = false
    } else if( StringUtils.trimToEmpty(txtResponsable.text).length() <= 0 ){
      valid = false
    } else if( !cbYes.selected && !cbNo.selected ){
      valid = false
    } else if( StringUtils.trimToEmpty(txtPromiseDate.text).length() <= 0 ){
      valid = false
    } else if( StringUtils.trimToEmpty(txtPromiseDate.text).length() > 0 ){
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy")
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy")
        SimpleDateFormat df2 = new SimpleDateFormat("ddMMyyyy")
        Integer dayInt = 0
        Integer monthInt = 0
        Integer yearInt = 0
        try{
            dayInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtPromiseDate.text).substring(0,2))
            monthInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtPromiseDate.text).substring(3,5))
            yearInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtPromiseDate.text).substring(6,10))
            validDate = df.parse( StringUtils.trimToEmpty(txtPromiseDate.text) )
            Calendar fecha = new GregorianCalendar();
            Integer currentYear = fecha.get(Calendar.YEAR);
            if( validDate != null && validDate.after(new Date()) && (dayInt <= 31 && monthInt <= 12 && yearInt >= currentYear) ){
              println "fecha valida"
            } else {
              valid = false
            }
        } catch ( Exception e ) {println e}
        if( validDate == null ){
          try{
            dayInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtPromiseDate.text).substring(0,2))
            monthInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtPromiseDate.text).substring(3,5))
            yearInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtPromiseDate.text).substring(6,10))
            validDate = df1.parse( StringUtils.trimToEmpty(txtPromiseDate.text) )
            Calendar fecha = new GregorianCalendar();
            Integer currentYear = fecha.get(Calendar.YEAR);
            if( validDate != null && validDate.after(new Date()) && (dayInt <= 31 && monthInt <= 12 && yearInt >= currentYear) ){
              println "fecha valida"
            } else {
              valid = false
            }
          } catch ( Exception e ) {println e}
        }
        if( validDate == null ){
          try{
            dayInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtPromiseDate.text).substring(0,2))
            monthInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtPromiseDate.text).substring(3,5))
            yearInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtPromiseDate.text).substring(6,10))
            validDate = df2.parse( StringUtils.trimToEmpty(txtPromiseDate.text) )
            Calendar fecha = new GregorianCalendar();
            Integer currentYear = fecha.get(Calendar.YEAR);
            if( validDate != null && validDate.after(new Date()) && (dayInt <= 31 && monthInt <= 12 && yearInt >= currentYear) ){
              println "fecha valida"
            } else {
              valid = false
            }
          } catch ( Exception e ) {println e}
        }
    }
    return valid
  }


}
