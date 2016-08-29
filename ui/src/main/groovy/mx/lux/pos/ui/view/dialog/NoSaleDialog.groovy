package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import groovy.util.logging.Slf4j
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.model.Customer
import mx.lux.pos.ui.model.Rx
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.awt.*
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.List

@Slf4j
class NoSaleDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private SwingBuilder sb
  private JComboBox cbUso
  private Component component
  private Customer customer
  private Rx receta
  private Integer idCliente
  private Integer idSucursal
  private String employee

  private JTextField txtOdEsfera
  private JTextField txtOdCil
  private JTextField txtOdEje
  private JTextField txtOdAd
  private JTextField txtOdAv
  private JTextField txtOdDm
  private JTextField txtOdPrisma
  private JTextField txtOdUbic

  private JTextField txtOiEsfera
  private JTextField txtOiCil
  private JTextField txtOiEje
  private JTextField txtOiAd
  private JTextField txtOiAv
  private JTextField txtOiDm
  private JTextField txtOiPrisma
  private JTextField txtOiUbic

  private JTextField txtDICerca
  private JTextField txtAltOblea
  private JTextField txtDILejos

  private JTextArea txtObservaciones

  private JTextField txtEmpleado
  private JLabel lblEmpleado
  private JTextField txtFolio
  private JTextField txtComentario
  private JComboBox cbRazon
  private List<String> lstRazones
  private Integer idEmpleado
  private boolean mostrarParametroSV = true
  private boolean mostrarParametroP = true
  private boolean mostrarParametroB = true
  private boolean razon

  private static String itemUso = null
  private static String limpiarAux
  private static final String TAG_PROGRESIVO = 'PROGRESIVO'
  private static final String TAG_BIFOCAL = 'BIFOCAL'
  private static final String TAG_LEJOS = 'LEJOS'
  private static final String TAG_CERCA = 'CERCA'

  List<String> uso = ["LEJOS", "CERCA", "PROGRESIVO", "BIFOCAL"]

  NoSaleDialog( Component parent, Integer idCliente, Integer idSucursal, Boolean razon ) {
    this.sb = new SwingBuilder()
    this.component = parent
    this.razon = razon
    lstRazones = Arrays.asList('NO QUISO', 'NO PRESUPUESTO', 'OTRO DIA') as List<String>
    this.receta = new Rx()
    this.idCliente = idCliente
    this.idSucursal = idSucursal
    buildUI()
    refreshRx()
    doBindings()
  }

  // UI Layout Definition
  private void buildUI( ) {
    sb.dialog( this,
        title: "Rx",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 700, 400 ],
        location: [ 200, 250 ],
        layout: new MigLayout( 'fill,wrap', '[fill]' )
    ) {
        panel( layout: new MigLayout( "wrap 5", "[][fill][grow,fill][][grow,fill]", "[][]" ) ) {
          label( text: "Examino:" )
          txtEmpleado = textField( minimumSize: [50, 20], actionPerformed: {doOptSearch()})
          txtEmpleado.addFocusListener(new FocusListener() {
              @Override
              void focusGained(FocusEvent e) { }
              @Override
              void focusLost(FocusEvent e) {
                  if (txtEmpleado.text.length() > 0) {
                      doOptSearch()
                  }
              }
          })
          lblEmpleado = label(border: titledBorder(title: ''), minimumSize: [150, 20])
          label( text: "Folio:", horizontalAlignment: SwingConstants.RIGHT )
          txtFolio = textField()
          label( text: "Razon:", visible: razon )
          cbRazon = comboBox( items: lstRazones, constraints: 'span 2', visible: razon )
          label(text: 'Uso:', horizontalAlignment: SwingConstants.RIGHT )
          cbUso = comboBox(items: uso, itemStateChanged: {refreshRx()} )
          label( text: '' )
        }
        panel(border: titledBorder("Rx"), layout: new MigLayout('fill,wrap ,center', '[fill,grow]')) {
            panel(layout: new MigLayout('fill,wrap 8,center',
                    '''[center][fill,grow,center][fill,grow,center][fill,grow,center][fill,grow,center]
                            [fill,grow,center][center][fill,grow,center]''')) {
                label()
                label(text: 'Esfera', horizontalAlignment: JTextField.CENTER)
                label(text: 'Cil.', toolTipText: 'Cilindro', horizontalAlignment: JTextField.CENTER )
                label(text: 'Eje', horizontalAlignment: JTextField.CENTER )
                label(text: 'Ad.', toolTipText: 'Adición', horizontalAlignment: JTextField.CENTER,visible:mostrarParametroP,enabled:  mostrarParametroP)
                label(text: 'D.M.', toolTipText: 'Distancia Monocular', horizontalAlignment: JTextField.CENTER,visible:(mostrarParametroP && mostrarParametroB),enabled:(mostrarParametroP && mostrarParametroB)  )
                label()
                label()
                label(text: 'O.D.', toolTipText: 'Ojo Derecho')
                txtOdEsfera = textField( horizontalAlignment: JTextField.RIGHT )
                txtOdEsfera.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                        //limpiar(txtOdEsfera)
                    }
                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtOdEsfera, 35, -35, 0.25, '.00', '+')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })
                txtOdCil = textField(toolTipText: 'Cilindro', horizontalAlignment: JTextField.RIGHT )
                txtOdCil.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {

                    }
                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtOdCil,12,-12,0.25,'.00','-')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })
                txtOdEje = textField(toolTipText: 'Eje',horizontalAlignment: JTextField.RIGHT )
                txtOdEje.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {

                    }
                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtOdEje,180,0,1,'0','')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })
                txtOdAd = textField( toolTipText: 'Adición', horizontalAlignment: JTextField.RIGHT,visible:mostrarParametroP,enabled:mostrarParametroP)
                txtOdAd.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                        limpiar(txtOdAd)
                    }
                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtOdAd,4,0.75,0.25,'.00','+')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })
                txtOdDm = textField( toolTipText: 'Distancia Monocular', horizontalAlignment: JTextField.RIGHT,visible:(mostrarParametroP && mostrarParametroB),enabled:(mostrarParametroP && mostrarParametroB))
                txtOdDm.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                        limpiar(txtOdDm)
                    }
                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtOdDm,45,22,0.1,'.0','')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })
                label(text: 'D.I. Binocular', toolTipText: 'Distancia Interpupilar Binocular',visible:(mostrarParametroP && !mostrarParametroB)||mostrarParametroSV ,enabled:(mostrarParametroP && !mostrarParametroB)||mostrarParametroSV)
                txtDILejos = textField(minimumSize: [20, 20], toolTipText: 'Distancia Interpupilar Binocular', horizontalAlignment: JTextField.RIGHT,visible:(mostrarParametroP && !mostrarParametroB)||mostrarParametroSV  ,enabled:(mostrarParametroP && !mostrarParametroB)||mostrarParametroSV )
                txtDILejos.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                        limpiar(txtDILejos)
                    }

                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtDILejos,90,45,1,'0','')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })
                label(text: 'O.I.', toolTipText: 'Ojo Izquierdo')
                txtOiEsfera = textField( horizontalAlignment: JTextField.RIGHT )
                txtOiEsfera.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                        limpiar(txtOiEsfera)
                    }
                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtOiEsfera,35,-35,0.25,'.00','+')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })
                txtOiCil = textField( toolTipText: 'Cilindro', horizontalAlignment: JTextField.RIGHT )
                txtOiCil.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                        limpiar(txtOiCil)
                    }
                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtOiCil,12,-12,0.25,'.00','-')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })
                txtOiEje = textField( horizontalAlignment: JTextField.RIGHT )
                txtOiEje.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                        limpiar(txtOiEje)
                    }
                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtOiEje,180,0,1,'0','')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })
                txtOiAd = textField( toolTipText: 'Adición', horizontalAlignment: JTextField.RIGHT,visible:mostrarParametroP,enabled:mostrarParametroP )
                txtOiAd.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                        limpiar(txtOiAd)
                    }
                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtOiAd,4,0.75,0.25,'.00','+')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })
                txtOiDm = textField( toolTipText: 'Distancia Monocular', horizontalAlignment: JTextField.RIGHT,visible:(mostrarParametroP && mostrarParametroB),enabled:(mostrarParametroP && mostrarParametroB) )
                txtOiDm.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                        limpiar(txtOiDm)
                    }
                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtOiDm,45,22,0.1,'.0','')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })
                label(text: 'Alt. Seg.', toolTipText: 'Altura Segmento',visible:mostrarParametroP,enabled:mostrarParametroP)
                txtAltOblea = textField( minimumSize: [20, 20], toolTipText: 'Altura Segmento', horizontalAlignment: JTextField.RIGHT,visible:mostrarParametroP,enabled:mostrarParametroP )
                txtAltOblea.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                        limpiar(txtAltOblea)
                    }
                    @Override
                    void focusLost(FocusEvent e) {
                      try{
                        validacion(txtAltOblea,40,10,0.5,'.00','')
                      } catch ( Exception ex ){
                        println ex
                      }
                    }
                })

            }
            scrollPane( border: titledBorder( title: 'Observaciones' ) ) {
                txtObservaciones = textArea(document: new UpperCaseDocument(), lineWrap: true )
            }
        }
        panel(  ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Guardar",
                actionPerformed: { onButtonOk() },
            )
            button( text: "Cerrar",
                actionPerformed: { onButtonCancel() }
            )
          }
      }
    }
  }

    private void doBindings() {
        sb.build {
          bean( txtOdEsfera, text: bind( source: receta, sourceProperty: 'odEsfR' ) )
          bean( txtOdCil, text: bind( source: receta, sourceProperty: 'odCilR' ) )
          bean( txtOdEje, text: bind( source: receta, sourceProperty: 'odEjeR' ) )
          bean( txtOdAd, text: bind( source: receta, sourceProperty: 'odAdcR' ), enabled: mostrarParametroP )
          bean( txtOdDm, text: bind( source: receta, sourceProperty: 'diOd' ), enabled: (mostrarParametroP && mostrarParametroB) )
          bean( txtDILejos, text: bind( source: receta, sourceProperty: 'diLejosR' ) )
          bean( txtOiEsfera, text: bind( source: receta, sourceProperty: 'oiEsfR' ) )
          bean( txtOiCil, text: bind( source: receta, sourceProperty: 'oiCilR' ) )
          bean( txtOiEje, text: bind( source: receta, sourceProperty: 'oiEjeR' ) )
          bean( txtOiAd, text: bind( source: receta, sourceProperty: 'oiAdcR' ), enabled: mostrarParametroP )
          bean( txtOiDm, text: bind( source: receta, sourceProperty: 'diOi' ), enabled: (mostrarParametroP && mostrarParametroB) )
          bean( txtAltOblea, text: bind( source: receta, sourceProperty: 'altOblR' ), enabled: mostrarParametroP )
          bean( txtObservaciones, text: bind( source: receta, sourceProperty: 'observacionesR' ) )
        }
    }

    private void useGlasess(){
        log.debug(cbUso.selectedItem.toString().trim() + '   USO')
        if (cbUso.selectedItem.toString().trim().equals('LEJOS')) {
            receta.setUseGlasses('l')
        } else if (cbUso.selectedItem.toString().trim().equals('CERCA')) {
            receta.setUseGlasses('c')
        } else if (cbUso.selectedItem.toString().trim().equals('BIFOCAL')) {
            receta.setUseGlasses('b')
        } else if (cbUso.selectedItem.toString().trim().equals('PROGRESIVO')) {
            receta.setUseGlasses('p')
        } else if (cbUso.selectedItem.toString().trim().equals('INTERMEDIO')) {
            receta.setUseGlasses('i')
        } else if (cbUso.selectedItem.toString().trim().equals('BIFOCAL INTERMEDIO')) {
            receta.setUseGlasses('t')
        }
        receta.setOdEsfR(txtOdEsfera.text)
        receta.setOdCilR(txtOdCil.text)
        receta.setOdEjeR(txtOdEje.text)
        receta.setOdAdcR(txtOdAd.text)
        receta.setDiOd(txtOdDm.text)
        receta.setDiLejosR(txtDILejos.text)
        receta.setOiEsfR(txtOiEsfera.text)
        receta.setOiCilR(txtOiCil.text)
        receta.setOiEjeR(txtOiEje.text)
        receta.setDiOi(txtOiDm.text)
        receta.setAltOblR(txtAltOblea.text)
        receta.setObservacionesR(txtObservaciones.text)
        receta.setUdf6(cbRazon.getSelectedItem().toString())
        receta.setIdOpt(txtEmpleado.text)
        receta.setFolio(txtFolio.text)
        receta.setIdStore(idSucursal)
        receta.setIdClient(idCliente)
        CustomerController.saveRx(receta, "NV")
        if( razon ){
          CustomerController.deletedClienteProceso( idCliente )
        }
        onButtonCancel()
    }


  void activate( ) {
    setVisible( true )
  }

  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
      if ( validDataRx() ) {
          if( doOptSearch() ){
              String useGlass = cbUso.selectedItem.toString().trim()
              itemUso = useGlass.trim()
              log.debug('UseGlass = ' + useGlass)

              /*B*/   if(useGlass.equals(TAG_BIFOCAL)/*BIFOCAL*/ ){
                  useGlass = 'BIFOCAL'
                  if((itemUso != null) && (useGlass.equals(itemUso.trim()))){
                      /*if( txtOdEsfera.text != '' &&
                              txtOdCil.text != '' &&
                              txtOdEje.text != '' &&
                              txtOdAd.text  != '' &&
                              txtDILejos.text   != '' &&
                              txtOiEsfera.text  != '' &&
                              txtOiCil.text  != '' &&
                              txtOiEje.text  != '' &&
                              txtOiAd.text != '' &&
                              txtAltOblea.text  != ''
                      ){*/
                          useGlasess()
                      /*}else{
                          sb.optionPane(message: "Llenar todos los campos", optionType: JOptionPane.DEFAULT_OPTION)
                                  .createDialog(new JTextField(), "Error")
                                  .show()
                      }*/
                  } else{
                      sb.optionPane(message: "Receta: "+useGlass+" Articulo: "+itemUso, optionType: JOptionPane.DEFAULT_OPTION)
                              .createDialog(new JTextField(), "Error")
                              .show()
                  }

                  /*P*/     }else if(useGlass.equals(TAG_PROGRESIVO)/*PROGRESIVO*/){
                  useGlass = 'PROGRESIVO'
                  if((itemUso != null) && (useGlass.equals(itemUso.trim()))){
                      /*if( txtOdEsfera.text != '' &&
                              txtOdCil.text != '' &&
                              txtOdEje.text != '' &&
                              txtOdAd.text  != '' &&
                              txtOdDm.text  != '' &&
                              txtOiEsfera.text  != '' &&
                              txtOiCil.text  != '' &&
                              txtOiEje.text  != '' &&
                              txtOiAd.text != '' &&
                              txtOiDm.text  != '' &&
                              txtAltOblea.text  != ''
                      ){*/
                          useGlasess()
                      /*}else{
                          sb.optionPane(message: "Llenar todos los campos", optionType: JOptionPane.DEFAULT_OPTION)
                                  .createDialog(new JTextField(), "Error")
                                  .show()
                      }*/
                  } else{
                      sb.optionPane(message: "Receta: "+useGlass+" Articulo: "+itemUso, optionType: JOptionPane.DEFAULT_OPTION)
                              .createDialog(new JTextField(), "Error")
                              .show()
                  }

                  /*SV*/    }else if(useGlass.equals(TAG_LEJOS)/*LEJOS*/ || useGlass.equals(TAG_CERCA)/*CERCA*/  ){
                  useGlass = 'MONOFOCAL'
                  itemUso = useGlass
                  if((itemUso != null) && (useGlass.equals(itemUso.trim()))){
                      /*if( txtOdEsfera.text != '' &&
                              txtOdCil.text != '' &&
                              txtOdEje.text != '' &&
                              txtDILejos.text   != '' &&
                              txtOiEsfera.text  != '' &&
                              txtOiCil.text  != '' &&
                              txtOiEje.text  != ''
                      ){*/
                          useGlasess()
                      /*}else{
                          sb.optionPane(message: "Llenar todos los campos", optionType: JOptionPane.DEFAULT_OPTION)
                                  .createDialog(new JTextField(), "Error")
                                  .show()
                      }*/
                  } else{
                      sb.optionPane(message: "Receta: "+useGlass+" Articulo: "+itemUso, optionType: JOptionPane.DEFAULT_OPTION)
                              .createDialog(new JTextField(), "Error")
                              .show()
                  }
              }
          }
      } else {
          sb.optionPane(message: "Verifique los datos de la receta", optionType: JOptionPane.DEFAULT_OPTION)
                  .createDialog(new JTextField(), "Error")
                  .show()
      }

  }

  private void limpiar(JTextField txtField){

  }

    private String signoMas(String numero){
      if(numero.toDouble()>0.0){
          numero = '+' + numero
      }
      return numero
    }

    private String signoMenos(String numero){
      numero = '-' + numero
      return numero
  }

    private void validacion(JTextField txtField, double max, double min, double interval, String format, String mask){
        if( StringUtils.trimToEmpty(txtField.text).contains("-.") ){
          txtField.text = ""
        }
        if (txtField.text.trim().length() > 0 && !txtField.text.trim().equals('0')) {
            double number
            String txt = txtField.text.trim()
            String signo = ''
            if (txt.substring(0, 1) == '-') {
                txt = txt.substring(1, txt.size())
                signo = '-'
            } else if (txt.substring(0, 1) == '+') {
                txt = txt.substring(1, txt.size())
                signo = '+'
            }
            if (txt.substring(0, 1) == '0') {
                txt = txt.substring(1, txt.size())
            }
            try {
                if (txt.substring(txt.size() - 2, txt.size() - 1).equals('.')) {
                    txt = txt + '0'
                }
            } catch (e) {

            }
            try {

                if (txt.substring(txt.size() - 3, txt.size()).equals('.00')) {
                    txt = txt.substring(0, txt.indexOf('.'))
                } else if (txt.substring(txt.size() - 2, txt.size()).equals('.0')) {
                    txt = txt.substring(0, txt.indexOf('.'))
                }
            } catch (e) {

            }
            log.debug(signo)
            log.debug(txt)
            txtField.text = ''
            Double multiplo = 0.0
            if (txt.length() > 0) {
                number = Double.parseDouble(txt);
                multiplo = number / interval;
                multiplo = multiplo % 1
                if (multiplo == 0 || multiplo.toString().equals('-0.0')) {

                    if (number >= min && number <= max) {
                        log.debug('nimber: ' + number)
                        log.debug(number.toString().substring(number.toString().indexOf('.'), number.toString().size()))
                        if (format.equals('.00') && number.toString().substring(number.toString().indexOf('.'), number.toString().size()).equals('.0')) {
                            txt = txt + '.00'
                        } else if (format.equals('.0') && number.toString().substring(number.toString().indexOf('.'), number.toString().size()).equals('.0')) {
                            txt = txt + '.0'
                        }
                        log.debug('val: ' + txt)
                        if (number > -1 && number < 1 && number != 0) {
                            txt = '0' + txt
                        }
                        if (mask.equals('+')) {
                            if (signo.equals('')) {
                                txtField.text = '+' + txt
                            } else if (signo.equals('-')) {
                                txtField.text = signo + txt
                            } else if (signo.equals('+')) {
                                txtField.text = signo + txt
                            }
                        } else if (mask.equals('-')) {
                            if (signo.equals('')) {
                                txtField.text = '-' + txt
                            } else if (signo.equals('-')) {
                                txtField.text = signo + txt
                            } else if (signo.equals('+')) {
                                txtField.text = signo + txt
                            }
                        } else {
                            if (signo.equals('')) {
                                txtField.text = txt
                            } else if (signo.equals('-')) {
                                txtField.text = signo + txt
                            } else if (signo.equals('+')) {
                                txtField.text = signo + txt
                            }
                        }


                    } else {
                        txtField.text = ''
                    }
                } else {
                    txtField.text = ''
                }
            } else {
                txtField.text = ''
            }
        } else if (txtField.text.trim().length() > 0 && txtField.text.trim().equals('0')) {
            if(format.equals('0')){
                txtField.text = '0'
            }else{
                txtField.text = '0'+ format
            }

        } else {
            txtField.text = ''
        }
    }

    private void refreshRx( ){
        String uso = cbUso.selectedItem.toString().trim()
        if(uso.trim().equals('LEJOS') || uso.trim().equals('CERCA'))
        {
            mostrarParametroSV = true
            mostrarParametroP = false
            mostrarParametroB = false
        }else if(uso.trim().equals('BIFOCAL')){
            mostrarParametroSV = false
            mostrarParametroP = true
            mostrarParametroB = false
        }else if(uso.trim().equals('PROGRESIVO')){
            mostrarParametroSV = false
            mostrarParametroP = true
            mostrarParametroB = true
        }
        doBindings()
    }


    private Boolean doOptSearch() {
        Boolean empValid = false
        String input = txtEmpleado.text
        if (StringUtils.isNotBlank(input)) {
            String optometrista = CustomerController.findOptometrista(input)
            if (optometrista != null) {
                empValid = true
                lblEmpleado.setText(optometrista)
            } else {
                sb.optionPane(message: "No existe el empleado", optionType: JOptionPane.DEFAULT_OPTION)
                        .createDialog(new JTextField(), "Error")
                        .show()
            }
        }
        return empValid
    }




    Boolean validDataRx(){
        Boolean dataValid = true
        if(StringUtils.trimToEmpty(txtEmpleado.text).isEmpty() ){
            dataValid = false
        } else if(txtDILejos.enabled && StringUtils.trimToEmpty(txtDILejos.text).length() <= 0){
            dataValid = false
        } else if(txtAltOblea.enabled && StringUtils.trimToEmpty(txtAltOblea.text).length() <= 0){
            dataValid = false
        } else if( txtOdDm.enabled && StringUtils.trimToEmpty(txtOdDm.text).length() <= 0 ){
            dataValid = false
        } else if( txtOiDm.enabled && StringUtils.trimToEmpty(txtOiDm.text).length() <= 0 ){
            dataValid = false
        } else if(StringUtils.trimToEmpty(txtOdCil.text).length() > 0 && StringUtils.trimToEmpty(txtOdEje.text).length() <= 0){
            dataValid = false
        } else if(StringUtils.trimToEmpty(txtOiCil.text).length() > 0 && StringUtils.trimToEmpty(txtOiEje.text).length() <= 0){
            dataValid = false
        }
        return dataValid
    }

}
