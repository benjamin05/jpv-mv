package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.model.CuponMv
import mx.lux.pos.model.Descuento
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.Item
import mx.lux.pos.model.DescuentoClave
import mx.lux.pos.model.DetalleNotaVenta
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.ICorporateKeyVerifier
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.component.NumericTextField
import mx.lux.pos.ui.view.component.PercentTextField
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils

import javax.swing.*
import java.awt.*
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class DiscountCouponDialog extends JDialog {



  private static final String TXT_AMOUNT_LABEL = "Monto"
  private static final String TXT_CORPORATE_KEY_LABEL = "Clave Descuento"
  private static final String TXT_PERCENT_LABEL = "% Descuento"
  private static final String TXT_BUTTON_CANCEL = "Cancelar"
  private static final String TXT_BUTTON_OK = "Aplicar"
  private static final String TXT_WARNING_MAX_AMOUNT = "Límite de descuento en tienda: %.1f%% (%,.2f)"
  private static final String TXT_VERIFY_PASS = ""
  private static final String TXT_VERIFY_FAILED = "Clave incorrecta"
  private static final String TXT_VERIFY_AMOUNT_FAILED = "Monto incorrecto"
  private static final String TAG_GENERICO_J = "J"
  private static final String TAG_GENERICO_B = "B"
  private static final String TAG_GENERICO_A = "A"
  private static final String TAG_TIPO_G = "G"
  private static final String TAG_SEGURO_INFANTIL = "N"
  private static final String TAG_SEGURO_OFTALMICO = "L"
  private static final String TAG_SEGURO_SOLAR = "S"
  private static  JLabel porceLabel = new JLabel()
  private static  JTextField porceText = new JTextField()

  private static final Double ZERO_TOLERANCE = 0.001

  private SwingBuilder sb = new SwingBuilder( )

  private Font bigLabel
  private Font bigInput

  private NumericTextField txtDiscountAmount
  private PercentTextField txtDiscountPercent
  private JTextField txtCorporateKey
  private JButton btnOk
  private JLabel lblStatus

  private String warning
  private FocusListener trgDiscAmountLeave
  private FocusListener trgDiscPercentLeave
  private FocusListener trgCorporateKeyLeave

  DescuentoClave descuentoClave
  ICorporateKeyVerifier verifier
  Double orderTotal = 0
  Double maximumDiscount = 0
  Boolean corporateEnabled = false
  BigDecimal discountAmt = 0
  Double discountPct = 0
  Boolean discountSelected
  String idOrder
  Item item
  String title

    DiscountCouponDialog( Boolean pCorporate, String idOrder, Item item, String title ) {
    corporateEnabled = pCorporate
    this.item = item
    this.title = title
    this.idOrder = idOrder
    init( )
    buildUI( )
    setupTriggers( )
  }

  // Internal Methods
  protected void init( ) {
    bigLabel = new Font( '', Font.PLAIN, 14 )
    bigInput = new Font( '', Font.BOLD, 14 )
    txtDiscountAmount = new NumericTextField( )
    txtDiscountPercent = new PercentTextField( )
  }
  
  protected void buildUI( JComponent pParent) {
    sb.dialog( this,
        title: title,
        location: [ 300, 300 ] ,
        resizable: false,
        modal: true,
        pack: true,
    ) {
      borderLayout( )
      panel( constraints: BorderLayout.CENTER,
          layout: new MigLayout( "wrap 2", "[]30[fill,grow,140!]", "[]10[]10[]"),
          border: BorderFactory.createEmptyBorder( 10, 20, 0, 20)
      ) {
          if ( corporateEnabled ) {
              label( TXT_CORPORATE_KEY_LABEL, font: bigLabel )
              txtCorporateKey = textField( font: bigInput,
                      horizontalAlignment: JTextField.LEFT,
                      actionPerformed: { onCorporateKeyLeave( ) }
              )
          }

       porceLabel =   label( text: TXT_PERCENT_LABEL, font: bigLabel,visible: false )
       porceText = textField( txtDiscountPercent,
            font: bigInput,
            horizontalAlignment: JTextField.LEFT,
            actionPerformed: { onDiscountPercentLeave( ) }  ,visible: false
        )
        label( TXT_AMOUNT_LABEL, font: bigLabel )
        textField( txtDiscountAmount, 
            font: bigInput,
            horizontalAlignment: JTextField.LEFT,

            actionPerformed: { onDiscountAmountLeave( ) } 
        )

        lblStatus = label( TXT_WARNING_MAX_AMOUNT, 
            foreground: UI_Standards.WARNING_FOREGROUND,
            constraints: "span 2,center",

            visible: true 
        )
      }
      
      panel( constraints: BorderLayout.PAGE_END,
          border: BorderFactory.createEmptyBorder( 0, 10, 10, 20 )
      ) {
        borderLayout( )
        panel( constraints: BorderLayout.LINE_END ) {
          btnOk = button( text: TXT_BUTTON_OK, 
              preferredSize: UI_Standards.BUTTON_SIZE,
              actionPerformed: { onButtonOk( ) } 
          )
          button( text: TXT_BUTTON_CANCEL,  
              preferredSize: UI_Standards.BUTTON_SIZE,
              actionPerformed: { onButtonCancel( ) }
          ) 
        }  
      }
    }
  }

  protected Boolean isMaximumDiscountEnabled( ) {
    return ( maximumDiscount > ZERO_TOLERANCE )
  }
  
  protected void setupTriggers( ) {
    trgDiscAmountLeave = new FocusAdapter( ) {
      public void focusLost( FocusEvent pEvent ) {
        DiscountCouponDialog.this.onDiscountAmountLeave()
      }
    }
    txtDiscountAmount.addFocusListener( trgDiscAmountLeave )
    
    trgDiscPercentLeave = new FocusAdapter( ) {
      public void focusLost( FocusEvent pEvent ) {
        DiscountCouponDialog.this.onDiscountPercentLeave()
      }
    }
    txtDiscountPercent.addFocusListener( trgDiscPercentLeave )
    
    if ( corporateEnabled ) {
      trgCorporateKeyLeave = new FocusAdapter( ) {
        public void focusLost( FocusEvent pEvent ) {
          DiscountCouponDialog.this.onCorporateKeyLeave( )
        }
      }
      txtCorporateKey.addFocusListener( trgCorporateKeyLeave )
      btnOk.setEnabled( false )
    }
    
  }

  protected void verifyCorporateKey( ) {
    if ( txtCorporateKey.getText( ).length( ) > 0 ) {
        descuentoClave = OrderController.descuentoClavexId(txtCorporateKey.text)
        if( descuentoClave == null ){
          descuentoClave = OrderController.descuentoClaveCupon(StringUtils.trimToEmpty(txtCorporateKey.text))//aqui
        }
        if( descuentoClave != null && StringUtils.trimToEmpty(title).equalsIgnoreCase("Seguro")){
          descuentoClave = null
        }
        if( descuentoClave == null ){
          descuentoClave = requestVerify()//OrderController.descuentoClaveCupon(StringUtils.trimToEmpty(txtCorporateKey.text))//aqui
        }

        if( descuentoClave == null ){
          descuentoClave = requestCrmVerify()
        }

        if (  descuentoClave != null ) {
            if(descuentoClave?.vigente == true){
                if(descuentoClave?.tipo != null && descuentoClave?.tipo.trim().equals('P')){
            txtDiscountPercent.setValue(descuentoClave?.porcenaje_descuento)
            txtDiscountAmount.setValue( txtDiscountPercent.getValue( ) * orderTotal / 100.0 )
                    porceLabel.setVisible(true)
                    porceText.setVisible(true)
                    lblStatus.text = TXT_VERIFY_PASS
                    lblStatus.foreground = UI_Standards.NORMAL_FOREGROUND
                    btnOk.setEnabled( true )
                }else if(descuentoClave?.tipo != null && descuentoClave?.tipo.trim().equals('M')){
                  if( descuentoClave.clave_descuento.startsWith("F") ){
                    NotaVenta nv = OrderController.findOrderByidOrder( idOrder )
                    BigDecimal amount = BigDecimal.ZERO
                    if( nv != null ){
                      for(DetalleNotaVenta det : nv.detalles){
                        if( !StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_J) ){
                          amount = amount.add(det.precioUnitFinal.multiply(det.cantidadFac))
                        }
                      }
                    }
                    if( amount.doubleValue() >= Registry.amountToApplyFFCoupon ){
                      txtDiscountPercent.setValue(descuentoClave?.porcenaje_descuento)
                      txtDiscountAmount.setValue(  descuentoClave?.porcenaje_descuento)
                      porceLabel.setVisible(false)
                      porceText.setVisible(false)

                      lblStatus.text = TXT_VERIFY_PASS
                      lblStatus.foreground = UI_Standards.NORMAL_FOREGROUND
                      btnOk.setEnabled( true )
                    } else {
                      lblStatus.setText( TXT_VERIFY_AMOUNT_FAILED )
                      btnOk.setEnabled( false )
                    }
                  } else {
                    txtDiscountPercent.setValue(descuentoClave?.porcenaje_descuento)
                    txtDiscountAmount.setValue(  descuentoClave?.porcenaje_descuento)
                    porceLabel.setVisible(false)
                    porceText.setVisible(false)

                    lblStatus.text = TXT_VERIFY_PASS
                    lblStatus.foreground = UI_Standards.NORMAL_FOREGROUND
                    btnOk.setEnabled( true )
                  }
                }
            } else {
                lblStatus.text = 'Descuento Inactivo'
                lblStatus.foreground = UI_Standards.WARNING_FOREGROUND
                btnOk.setEnabled( false )
            }
      } else {
        if( StringUtils.trimToEmpty(warning).length() > 0 ){
          lblStatus.text = warning
        } else {
          lblStatus.text = TXT_VERIFY_FAILED
        }
        lblStatus.foreground = UI_Standards.WARNING_FOREGROUND
        btnOk.setEnabled( false )
      }
    } else {
      lblStatus.setText( "" )
      btnOk.setEnabled( false )
    }
  }
  
  protected void verifyDiscountConstraint( ) {
    Double maxAmountAllowed = orderTotal * maximumDiscount / 100.0
    Boolean verified = ( txtDiscountAmount.getValue( ) <= maxAmountAllowed )
    lblStatus.setText( 
      String.format( TXT_WARNING_MAX_AMOUNT, maximumDiscount, maxAmountAllowed )
    )
    lblStatus.setVisible( !verified )
    btnOk.setEnabled( verified )
  }
   
  // Public methods
  void activate( ) {
    discountSelected = false
    lblStatus.setText( "" )
    txtDiscountAmount.setValue( discountAmt.toDouble( ) )
    onDiscountAmountLeave( )
    this.setVisible( true )
  }
  
  String getCorporateKey( ) {
    String key = "0"
    if ( this.corporateEnabled ) {
      key = "2"
    }
    return key  
  }
  
  void setMaximumDiscount( Double pDiscountPercent ) {
    maximumDiscount = Math.abs( pDiscountPercent * 100 )
    if ( maximumDiscount < ZERO_TOLERANCE ) maximumDiscount = 0.0  
  }
  
  // UI Response
  void onButtonCancel() {
    setVisible( false )  
  }
  
  void onButtonOk() {
    CuponMv cuponMv = OrderController.obtenerCuponMvByClave(StringUtils.trimToEmpty(txtCorporateKey.text))
    if( cuponMv != null ){
      //OrderController.updateCuponMvByClave(idOrder, cuponMv.claveDescuento)
    }
    discountSelected = true
    setDiscountAmt( txtDiscountAmount.getValue( ) )
    setDiscountPct( txtDiscountPercent.getValue( ) )
    setVisible( false ) 
  }
  
  void onDiscountAmountLeave( ) {
    txtDiscountPercent.setValue( 100.0 * txtDiscountAmount.getValue( ) / orderTotal )
    if ( !corporateEnabled && isMaximumDiscountEnabled( ) ) {
      verifyDiscountConstraint( )
    }
    if ( corporateEnabled ) {
      verifyCorporateKey( )
    }
  }
  
  void onDiscountPercentLeave( ) {
    txtDiscountAmount.setValue( txtDiscountPercent.getValue( ) * orderTotal / 100.0 )
    if ( !corporateEnabled && isMaximumDiscountEnabled( ) ) {
      verifyDiscountConstraint( )
    }
    if ( corporateEnabled ) {
      verifyCorporateKey( )
    }
  }
  
  void onCorporateKeyLeave( ) {
    txtCorporateKey.setText( txtCorporateKey.getText( ).toUpperCase( ) )
    verifyCorporateKey( )
  }


  DescuentoClave requestVerify( ){
    Boolean valid = false
    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");
    String clave = ""
    BigDecimal amount = BigDecimal.ZERO
    if( StringUtils.trimToEmpty(txtCorporateKey.text).length() == 10 || StringUtils.trimToEmpty(txtCorporateKey.text).length() == 11 ){
      for(int i=0;i<StringUtils.trimToEmpty(txtCorporateKey.text).length();i++){
        if(StringUtils.trimToEmpty(txtCorporateKey.text.charAt(i).toString()).isNumber()){
          Integer number = 0
          try{
            number = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtCorporateKey.text.charAt(i).toString()))
          } catch ( NumberFormatException e ) { println e }
          clave = clave+StringUtils.trimToEmpty((10-number).toString())
        } else {
          clave = clave+0
        }
      }
      String dateStr = ""
      String amountStr = ""
      if( StringUtils.trimToEmpty(clave).length() == 11 ){
        dateStr = StringUtils.trimToEmpty(clave).substring(1,7)
        amountStr = StringUtils.trimToEmpty(clave).substring(7,11)
      } else if( StringUtils.trimToEmpty(clave).length() == 10 ){
        dateStr = "0"+StringUtils.trimToEmpty(clave).substring(1,6)
        amountStr = StringUtils.trimToEmpty(clave).substring(6,10)
      }
      Date date = null
      try{
        date = formatter.parse(dateStr)
        amount = NumberFormat.getInstance().parse(amountStr)
      } catch ( ParseException e) {
        e.printStackTrace()
      } catch ( NumberFormatException e) {
        e.printStackTrace()
      }
      if( item.price.compareTo(amount) <= 0 ){
        amount = (item.price.multiply(new BigDecimal(Registry.percentageWarranty).divide(new BigDecimal(100)))).doubleValue()
      }
      Boolean itemsValid = false
      warning = ""
      NotaVenta notaVenta = OrderController.findOrderByidOrder( StringUtils.trimToEmpty(idOrder) )
      if( notaVenta != null ){
        String ensureType = StringUtils.trimToEmpty(txtCorporateKey.text).substring(0,1)
        Boolean hasEnsure = false
        for(DetalleNotaVenta det : notaVenta.detalles){
          if( StringUtils.trimToEmpty(det.articulo.idGenerico).startsWith(TAG_GENERICO_J) ){
            hasEnsure = true
          }
        }
        if( !hasEnsure ){
        if( TAG_SEGURO_INFANTIL.equalsIgnoreCase(ensureType) ){
          for(DetalleNotaVenta det : notaVenta.detalles){
            if( StringUtils.trimToEmpty(det.articulo.subtipo).startsWith(TAG_SEGURO_INFANTIL) ){
              itemsValid = true
              warning = ""
              break
            } else {
              warning = 'El producto no corresponde al seguro'
            }
          }
        } else if( TAG_SEGURO_OFTALMICO.equalsIgnoreCase(ensureType) ){
          for(DetalleNotaVenta det : notaVenta.detalles){
            if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_B) ){
              itemsValid = true
              warning = ""
              break
            } else {
                warning = 'El producto no corresponde al seguro'
            }
          }
        } else if( TAG_SEGURO_SOLAR.equalsIgnoreCase(ensureType) ){
          for(DetalleNotaVenta det : notaVenta.detalles){
            if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_A) &&
                    StringUtils.trimToEmpty(det.articulo.tipo).equalsIgnoreCase(TAG_TIPO_G)){
              itemsValid = true
              warning = ""
              break
            } else {
                warning = 'El producto no corresponde al seguro'
            }
          }
        }
      } else {
        warning = 'Combinacion de productos invalida'
      }
      }
      if( StringUtils.trimToEmpty(warning).length() > 0 ){
        lblStatus.text = warning
      }
      if( date.compareTo(new Date()) >= 0 && amount.compareTo(BigDecimal.ZERO) > 0 &&
            OrderController.keyFree(StringUtils.trimToEmpty(txtCorporateKey.text).toUpperCase()) && itemsValid ){
        if( item != null && item.price.compareTo(amount) < 0 ){
          txtDiscountAmount.setText( StringUtils.trimToEmpty((item.price.multiply(new BigDecimal(Registry.percentageWarranty/100))).toString()) )
        } else {
          txtDiscountAmount.setText( StringUtils.trimToEmpty(amount.doubleValue().toString()) )
        }
        valid = true
      }
    }
    DescuentoClave descuentoClave = null
        if( valid ){
      descuentoClave = new DescuentoClave()
      descuentoClave.clave_descuento = StringUtils.trimToEmpty(txtCorporateKey.text)
      descuentoClave.porcenaje_descuento = amount.doubleValue()
      descuentoClave.descripcion_descuento = "Seguro"
      descuentoClave.tipo = "M"
      descuentoClave.vigente = true
      descuentoClave.cupon = false
    }
    return descuentoClave
  }



    DescuentoClave requestCrmVerify( ){
      Boolean valid = false
      String clave = ""
      BigDecimal amount = BigDecimal.ZERO
      Integer percentajeInt = 0
      if( StringUtils.trimToEmpty(txtCorporateKey.text).length() >= 11 ){
        for(int i=0;i<StringUtils.trimToEmpty(txtCorporateKey.text).length();i++){
          if(StringUtils.trimToEmpty(txtCorporateKey.text.charAt(i).toString()).isNumber()){
            Integer number = 0
            try{
              number = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtCorporateKey.text.charAt(i).toString()))
            } catch ( NumberFormatException e ) { println e }
            clave = clave+StringUtils.trimToEmpty((10-number).toString())
          } else {
            clave = clave+0
          }
        }
        String generic = ""
        String percentaje = ""
        generic = StringUtils.trimToEmpty(txtCorporateKey.text).substring(1,3)
        percentaje = StringUtils.trimToEmpty(clave).substring(3,5)
        try{
          percentajeInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(percentaje))
        } catch ( NumberFormatException e) { e.printStackTrace() }

        Boolean claveClear = false
        warning = ""
        NotaVenta notaVenta = OrderController.findOrderByidOrder( StringUtils.trimToEmpty(idOrder) )
        if( notaVenta != null ){
          Boolean claveValid = false
          Boolean allGen = false
          Boolean oneValGen = false
          Boolean oneNotValGen = false
          if( generic.contains("**") ){
            allGen = true
          } else if( generic.contains("*") ){
            oneValGen = true
          } else if( generic.replace("!","\\!").contains("\\!") ){
            oneNotValGen = true
          }
          if( allGen ){
            claveValid = true
          } else {
            orderTotal = 0.00
            for(DetalleNotaVenta det : notaVenta.detalles){
              if( oneValGen ){
                if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(generic.substring(1)) ){
                  claveValid = true
                  orderTotal = orderTotal + det.precioUnitLista
                }
              } else if( oneNotValGen ){
                if( !StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(generic.substring(1)) ){
                  claveValid = true
                  orderTotal = orderTotal + det.precioUnitLista
                }
              }
            }
          }

          if( claveValid ){
            Descuento descuento = OrderController.findClaveApplied( txtCorporateKey.text )
            if( descuento == null ){
              if( OrderController.validCrmClaveWeb( txtCorporateKey.text ) ){
                claveClear = true
              } else {
                  warning = "Clave incorrecta"
                  println "Clave ya aplicada"
              }
            } else {
              warning = "Clave incorrecta"
              println "Clave ya aplicada"
            }
          } else {
            warning = "El producto no corresponde al descuento"
            println "Genericos de productos no coinciden con los de la clave"
          }
        }
        if( StringUtils.trimToEmpty(warning).length() > 0 ){
          lblStatus.text = warning
        }
        if( claveClear ){
          txtDiscountAmount.setValue( new BigDecimal(percentajeInt.doubleValue()*Registry.multiplyDiscountCrm) )
          valid = true
          amount = percentajeInt.doubleValue()*Registry.multiplyDiscountCrm
        }
      }
      DescuentoClave descuentoClave = null
      if( valid ){
        descuentoClave = new DescuentoClave()
        descuentoClave.clave_descuento = StringUtils.trimToEmpty(txtCorporateKey.text)
        descuentoClave.porcenaje_descuento = amount
        descuentoClave.descripcion_descuento = "Descuentos CRM"
        descuentoClave.tipo = "M"
        descuentoClave.vigente = true
        descuentoClave.cupon = false
      }
      return descuentoClave
    }


}
