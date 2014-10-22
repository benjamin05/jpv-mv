package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.model.PedidoLcDet
import mx.lux.pos.ui.controller.ItemController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.ModelLc
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.util.List
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

class LentesContactoDialog extends JDialog implements FocusListener {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtCantidad
  private JTextField txtEsfera
  private JTextField txtCilindro
  private JTextField txtEje
  private JComboBox cbColor
  private JComboBox cbCurva
  private JTextField txtDiametro

  private JLabel lblWarning

  private ModelLc model

  private String titulo
  private Integer idItem
  private Integer idCustomer
  private String idOrder
  private Boolean visibleCurva = false
  private Boolean visibleDiametro = false
  private Boolean visibleEsfera = false
  private Boolean visibleCilindro = false
  private Boolean visibleEje = false
  private Boolean visibleColor = false
  private Boolean fillOblig = false
  private List<String> lstColors = new ArrayList<String>()
  private List<String> lstCurva = new ArrayList<String>()
  private PedidoLcDet pedidoLcDet
  Integer cantidad = 0

  public boolean button = false

    LentesContactoDialog( String idFactura, String titulo, Integer idItem, String curva, String diametro, String esfera,
                          String cilindro, String eje, String color, Integer idCustomer, Boolean fillOblig, PedidoLcDet pedidoLcDet ) {
    this.titulo = titulo
    this.idItem = idItem
    this.idOrder = idFactura
    this.idCustomer = idCustomer
    this.fillOblig = fillOblig
    if(StringUtils.trimToEmpty(curva).length() > 0){
      String[] curve = curva.split( "," )
      for(String crv : curve){
        lstCurva.add( crv )
      }
      visibleCurva = true
    }
    if(StringUtils.trimToEmpty(diametro).length() > 0){
      visibleDiametro = true
    }
    if(StringUtils.trimToEmpty(esfera).length() > 0){
      visibleEsfera = true
    }
    if(StringUtils.trimToEmpty(cilindro).length() > 0){
      visibleCilindro = true
    }
    if(StringUtils.trimToEmpty(eje).length() > 0){
      visibleEje = true
    }
    if(StringUtils.trimToEmpty(color).length() > 0){
      String[] colors = color.split( "," )
      for(String clr : colors){
        lstColors.add( clr )
      }
      visibleColor = true
    }
    buildUI(  )
    if( pedidoLcDet != null ){
      this.pedidoLcDet = pedidoLcDet
      setData(  )
    }
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: titulo,
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 300, 280 ],
        location: [ 200, 250 ],
        undecorated: true,
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 2", "20[][grow,fill]40", "20[]10[]" ) ) {
          label( text: "Esfera:", visible: visibleEsfera, constraints: 'hidemode 3' )
          txtEsfera = textField( constraints: 'hidemode 3', visible: visibleEsfera )
          txtEsfera.addFocusListener( this )
          label( text: "Cilindro:", visible: visibleCilindro, constraints: 'hidemode 3' )
          txtCilindro = textField( constraints: 'hidemode 3', visible: visibleCilindro )
          txtCilindro.addFocusListener( this )
          label( text: "Eje:", visible: visibleEje, constraints: 'hidemode 3' )
          txtEje = textField( constraints: 'hidemode 3', visible: visibleEje )
          txtEje.addFocusListener( this )
          label( text: "Curva:", visible: visibleCurva, constraints: 'hidemode 3' )
          cbCurva = comboBox( items: lstCurva, constraints: 'hidemode 3', visible: visibleCurva )
          //txtCurva.addFocusListener( this )
          label( text: "Diametro:", visible: visibleDiametro, constraints: 'hidemode 3' )
          txtDiametro = textField( constraints: 'hidemode 3', visible: visibleDiametro )
          txtDiametro.addFocusListener( this )
          label( text: "Color:", visible: visibleColor, constraints: 'hidemode 3' )
          cbColor = comboBox(items: lstColors, constraints: 'hidemode 3', visible: visibleColor )
          label( text: "Cantidad:" )
          txtCantidad = textField( )
          lblWarning = label( visible: false, foreground: UI_Standards.WARNING_FOREGROUND, constraints: 'span' )

        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Aceptar", preferredSize: UI_Standards.BUTTON_SIZE,
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

  // Public Methods

  // UI Response
  protected void onButtonCancel( ) {
    if( fillOblig ){
      lblWarning.visible = true
      lblWarning.text = 'Favor de llenar todos los campos'
    } else {
      dispose()
    }
  }

  protected void onButtonOk( ) {
    if( validData() && notEmptyData() ){
      String curvaBase = cbCurva.selectedItem != null ? cbCurva.selectedItem.toString() : ""
      String color = cbColor.selectedItem != null ? cbColor.selectedItem.toString() : ""
      if( pedidoLcDet == null ){
        Order order = ItemController.saveRequest( idOrder, StringUtils.trimToEmpty(curvaBase), StringUtils.trimToEmpty(txtDiametro.text),
            StringUtils.trimToEmpty(txtEsfera.text), StringUtils.trimToEmpty(txtCilindro.text), StringUtils.trimToEmpty(model.id),
            StringUtils.trimToEmpty(txtEje.text), StringUtils.trimToEmpty(color), StringUtils.trimToEmpty(txtCantidad.text),
            idCustomer)
        OrderController.updateOrderLc( order )
      } else {
        ItemController.updateRequest(idOrder, StringUtils.trimToEmpty(curvaBase), StringUtils.trimToEmpty(txtDiametro.text),
                StringUtils.trimToEmpty(txtEsfera.text), StringUtils.trimToEmpty(txtCilindro.text), StringUtils.trimToEmpty(model.id),
                StringUtils.trimToEmpty(txtEje.text), StringUtils.trimToEmpty(color), StringUtils.trimToEmpty(txtCantidad.text),
                idCustomer, pedidoLcDet.numReg)
      }
      dispose()
    }
  }

    @Override
    void focusGained(FocusEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void focusLost(FocusEvent e) {
      validData()
    }


    Boolean validData(){
        Boolean valid = true
        lblWarning.visible = false
        lblWarning.text = ''
        model = ItemController.findLenteContacto( idItem )
        /*if(cbCurva.visible && StringUtils.trimToEmpty(cbCurva.selectedItem.toString())){
            if(!ItemController.validaRangosLc( model.curve, StringUtils.trimToEmpty(txtCurva.text) )){
                valid = false
                lblWarning.visible = true
                lblWarning.text = 'Curva no valida'
            } else {
              txtCurva.text = giveFormat( StringUtils.trimToEmpty(txtCurva.text), "" )
            }
        }*/
        if(txtDiametro.visible && StringUtils.trimToEmpty(txtDiametro.text)){
            if(!ItemController.validaRangosLc( model.diameter, StringUtils.trimToEmpty(txtDiametro.text) )){
                valid = false
                lblWarning.visible = true
                lblWarning.text = 'Diametro no valido'
            } else {
                txtDiametro.text = giveFormat( StringUtils.trimToEmpty(txtDiametro.text), "" )
            }
        }
        if(txtEsfera.visible && StringUtils.trimToEmpty(txtEsfera.text)){
            if(!ItemController.validaRangosLc( model.sphere, StringUtils.trimToEmpty(txtEsfera.text) )){
                valid = false
                lblWarning.visible = true
                lblWarning.text = 'Esfera no valida'
            } else {
                txtEsfera.text = giveFormat( StringUtils.trimToEmpty(txtEsfera.text), "+" )
            }
        }
        if(txtCilindro.visible && StringUtils.trimToEmpty(txtCilindro.text)){
            if(!ItemController.validaRangosLc( model.cylinder,
                    txtCilindro.text.contains("-") ? StringUtils.trimToEmpty(txtCilindro.text) : "-"+StringUtils.trimToEmpty(txtCilindro.text) )){
                valid = false
                lblWarning.visible = true
                lblWarning.text = 'Cilindro no valido'
            } else {
                txtCilindro.text = giveFormat( StringUtils.trimToEmpty(txtCilindro.text), "-" )
            }
        }
        if(txtEje.visible && StringUtils.trimToEmpty(txtEje.text)){
            if(!ItemController.validaRangosLc( model.axis, StringUtils.trimToEmpty(txtEje.text) )){
                valid = false
                lblWarning.visible = true
                lblWarning.text = 'Eje no valido'
            } else {
                txtEje.text = StringUtils.trimToEmpty(txtEje.text)
            }
        }
        return valid
    }


    String giveFormat( String pValue, String signo ){
      String value = ''
      Double iValue = 0.00
      try{
        iValue = NumberFormat.getInstance().parse(pValue.replace("+","")).doubleValue()
      } catch ( NumberFormatException e ){ println e }
      value = String.format( "%s%.02f", iValue < 0 ? "" : signo, iValue )
      return value
    }


    Boolean notEmptyData(){
        Boolean valid = true
        lblWarning.visible = false
        lblWarning.text = ''
        if(cbCurva.visible && StringUtils.trimToEmpty(cbCurva.selectedItem.toString()).isEmpty()){
            valid = false
            lblWarning.visible = true
            lblWarning.text = 'Favor de llenar todos los campos'
        }
        if(txtDiametro.visible && StringUtils.trimToEmpty(txtDiametro.text).isEmpty()){
            valid = false
            lblWarning.visible = true
            lblWarning.text = 'Favor de llenar todos los campos'
        }
        if(txtEsfera.visible && StringUtils.trimToEmpty(txtEsfera.text).isEmpty()){
            valid = false
            lblWarning.visible = true
            lblWarning.text = 'Favor de llenar todos los campos'
        }
        if(txtCilindro.visible && StringUtils.trimToEmpty(txtCilindro.text).isEmpty()){
            valid = false
            lblWarning.visible = true
            lblWarning.text = 'Favor de llenar todos los campos'
        }
        if(txtEje.visible && StringUtils.trimToEmpty(txtEje.text).isEmpty()){
            valid = false
            lblWarning.visible = true
            lblWarning.text = 'Favor de llenar todos los campos'
        }
        if(txtCantidad.visible && StringUtils.trimToEmpty(txtCantidad.text).isEmpty()){
            valid = false
            lblWarning.visible = true
            lblWarning.text = 'Favor de llenar todos los campos'
        }
        return valid
    }


    void setData( ){
        txtCantidad.editable = false
        txtCantidad.setText( StringUtils.trimToEmpty(pedidoLcDet.cantidad.toString()) )
        txtCilindro.setText( StringUtils.trimToEmpty(pedidoLcDet.cilindro) )
        txtDiametro.setText( StringUtils.trimToEmpty(pedidoLcDet.diametro) )
        txtEje.setText( StringUtils.trimToEmpty(pedidoLcDet.eje) )
        txtEsfera.setText( StringUtils.trimToEmpty(pedidoLcDet.esfera) )
    }


}
