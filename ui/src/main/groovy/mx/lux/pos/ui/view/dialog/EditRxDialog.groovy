
package mx.lux.pos.ui.view.dialog

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.RecetaJava
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.model.Rx
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.view.renderer.DateCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.MouseEvent
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.List

class EditRxDialog extends JDialog{

    private def sb
    private Component component
    private Logger logger = LoggerFactory.getLogger(this.getClass())

    Rx receta
    private List<Rx> lstRecetas = new ArrayList<>()
    private RecetaJava rec
    private Integer idRx
    private Integer idCliente
    private Integer idSucursal

    private JTextField txtEmpleado
    private JTextField txtFolio
    private JLabel lblEmpleado
    private JLabel lblFolio
    private JComboBox cbUso

    private JButton btnTraerReceta
    private DefaultTableModel rxModel
    private JPanel rxPanel

    private JTextField txtOdEsfera
    private JTextField txtOdCil
    private JTextField txtOdEje
    private JTextField txtOdAd
    private JTextField txtOdAv
    private JTextField txtOdDm
    private JTextField txtOdPrisma
    private JComboBox cbOdUbic

    private JTextField txtOiEsfera
    private JTextField txtOiCil
    private JTextField txtOiEje
    private JTextField txtOiAd
    private JTextField txtOiAv
    private JTextField txtOiDm
    private JTextField txtOiPrisma
    private JComboBox cbOiUbic

    private JTextField txtDICerca
    private JTextField txtAltOblea
    private JTextField txtDILejos

    private JTextArea txtObservaciones

    private JPanel empleadoPanel
    private final double VALOR_MULTIPLO = 0.25;
    private boolean mostrarParametroSV = true
    private boolean mostrarParametroP = true
    private boolean mostrarParametroB = true
    private boolean editRx = true

    private Boolean dataRxValid = true

    private static String itemUso = null
    private static String limpiarAux

    private static List<Rx> lstRecetasFinal = new ArrayList<>()

    private static final TAG_LEJOS = 'LEJOS'
    private static final TAG_CERCA = 'CERCA'
    private static final TAG_BIFOCAL = 'BIFOCAL'
    private static final TAG_PROGRESIVO = 'PROGRESIVO'

    List<String> ubicacion = ["", "ARRIBA", "ABAJO", "AFUERA", "ADENTRO"]
    List<String> usoM = ["LEJOS", "CERCA"]
    List<String> usoP = ["PROGRESIVO"]
    List<String> usoB = ["BIFOCAL"]
    List<String> comboUso = []

    Boolean canceled
    Boolean obligatory
    String Title

    EditRxDialog(Component parent, Rx receta, Integer idCliente, Integer idSucursal, String titulo, String uso, Boolean edit, Boolean obligatory) {
        sb = new SwingBuilder()
        component = parent
        itemUso = uso
        rec = null
        editRx = edit
        title = titulo
        this.obligatory = obligatory
        if (itemUso.trim().equals('MONOFOCAL')) {
            mostrarParametroSV = true
            mostrarParametroP = false
            mostrarParametroB = false
            comboUso.add( " " )
            comboUso.addAll(usoM)
        } else if (itemUso.trim().contains('BIFOCAL')) {
            mostrarParametroSV = false
            mostrarParametroP = true
            mostrarParametroB = false
            comboUso.addAll(usoB)
        } else if (itemUso.trim().contains('PROGRESIVO')) {
            mostrarParametroSV = false
            mostrarParametroP = true
            mostrarParametroB = true
            comboUso.addAll(usoP)
        }
        if (receta?.id == null) {
            this.receta = new Rx()
            this.idCliente = idCliente
            this.idSucursal = idSucursal
        } else {
            this.receta = receta
            this.idRx = receta.id
            this.idCliente = idCliente
            this.idSucursal = idSucursal
        }
        lstRecetas = CustomerController.requestRxByCustomer( idCliente )
        lstRecetasFinal.addAll( lstRecetas )
        buildUI()
        doBindings()
        if( receta?.id != null ){
            cbUso.selectedItem = receta.tipo
        }
        String selected = StringUtils.trimToEmpty(cbUso.selectedItem.toString())
        if( selected != '' ){
          getRxByClient()
        }
    }

    // UI Layout Definition
    void buildUI() {
        sb.dialog(this,
                title: title,
                resizable: true,
                pack: true,
                modal: true,
                preferredSize: [600, 460],
                layout: new MigLayout('wrap,center', '[fill,grow]'),
                location: [200, 200],
                undecorated: true,
        ) {
            empleadoPanel = panel(layout: new MigLayout('fill,wrap 3, left', '[fill][fill][fill,grow,left]')) {
                label(text: 'Uso:')
                cbUso = comboBox(items: comboUso)
                cbUso.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String selected = StringUtils.trimToEmpty(cbUso.selectedItem.toString())
                        if( selected != '' ){
                            getRxByClient()
                        }
                    }
                })
                label()
                label(text: 'Optometrista:')
                txtEmpleado = textField(minimumSize: [50, 20], actionPerformed: { doOptSearch() })
                txtEmpleado.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {}

                    @Override
                    void focusLost(FocusEvent e) {
                        if (txtEmpleado.text.length() > 0) {
                            doOptSearch()
                        }
                    }
                })
                lblEmpleado = label(border: titledBorder(title: ''), minimumSize: [150, 20])
                lblFolio = label(text: 'FolioPlantilla: ')
                txtFolio = textField(minimumSize: [50, 20])
                label()

            }
            rxPanel = panel( layout: new MigLayout('fill,wrap', '[fill]'), visible: false, constraints: 'hidemode 3' ) {
                borderLayout()
                scrollPane( constraints: BorderLayout.CENTER ) {
                    table( selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doClick ) {
                        rxModel = tableModel( list: lstRecetas ) {
                            closureColumn( header: 'Fecha', read: {Rx tmp -> tmp?.rxDate}, minWidth: 90, cellRenderer: new DateCellRenderer() )
                            closureColumn( header: 'Optometrista', read: {Rx tmp -> tmp?.idOpt}, minWidth: 60 )
                            closureColumn( header: 'Esfera', read: {Rx tmp -> tmp?.odEsfR+","+tmp?.oiEsfR}, minWidth: 80 )
                            closureColumn( header: 'Cilindro', read: {Rx tmp -> tmp?.odCilR+","+tmp?.oiCilR}, minWidth: 80 )
                            closureColumn( header: 'Adicion', read: {Rx tmp -> tmp?.rxAddFormatter}, minWidth: 80 )
                            closureColumn( header: 'Distancia', read: {Rx tmp -> tmp?.rxDistanceFormatter}, minWidth: 70 )
                            closureColumn( header: 'Factura', read: {Rx tmp -> org.apache.commons.lang.StringUtils.trimToEmpty(tmp?.order?.bill)}, minWidth: 70 )
                            closureColumn( header: 'Tipo', read: {Rx tmp -> tmp?.getTipo()}, minWidth: 80 )
                        } as DefaultTableModel
                    }
                }
            }
            panel(border: titledBorder("Rx"), layout: new MigLayout('fill,wrap ,center', '[fill,grow]')) {
                panel(layout: new MigLayout('fill,wrap 8,center',
                        '''[center][fill,grow,center][fill,grow,center][fill,grow,center][fill,grow,center]
                            [fill,grow,center][center][fill,grow,center]''')) {
                    label()
                    label(text: 'Esfera', horizontalAlignment: JTextField.CENTER)
                    label(text: 'Cil.', toolTipText: 'Cilindro', horizontalAlignment: JTextField.CENTER)
                    label(text: 'Eje', horizontalAlignment: JTextField.CENTER)
                    label(text: 'Ad.', toolTipText: 'Adición', horizontalAlignment: JTextField.CENTER, visible: mostrarParametroP, enabled: mostrarParametroP)
                    label(text: 'D.M.', toolTipText: 'Distancia Monocular', horizontalAlignment: JTextField.CENTER, visible: (mostrarParametroP && mostrarParametroB), enabled: (mostrarParametroP && mostrarParametroB))
                    label()
                    label()
                    label(text: 'O.D.', toolTipText: 'Ojo Derecho')
                    txtOdEsfera = textField(horizontalAlignment: JTextField.RIGHT)
                    txtOdEsfera.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {

                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtOdEsfera, 35, -35, 0.25, '.00', '+')
                          } catch ( Exception ex ){
                            dataRxValid = false
                            println ex
                          }
                        }
                    })
                    txtOdCil = textField(toolTipText: 'Cilindro', horizontalAlignment: JTextField.RIGHT)
                    txtOdCil.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {

                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtOdCil, 12, -12, 0.25, '.00', '-')
                          } catch ( Exception ex ){
                            dataRxValid = false
                            println ex
                          }
                        }
                    })
                    txtOdEje = textField(toolTipText: 'Eje', horizontalAlignment: JTextField.RIGHT)
                    txtOdEje.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtOdEje, 180, 0, 1, '0', '')
                          } catch ( Exception ex ){
                            dataRxValid = false
                              println ex
                          }
                        }
                    })
                    txtOdAd = textField(toolTipText: 'Adición', horizontalAlignment: JTextField.RIGHT, visible: mostrarParametroP, enabled: mostrarParametroP)
                    txtOdAd.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {

                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtOdAd, 4, 0.75, 0.25, '.00', '+')
                          } catch ( Exception ex ){
                            dataRxValid = false
                            println ex
                          }
                        }
                    })
                    txtOdDm = textField(toolTipText: 'Distancia Monocular', horizontalAlignment: JTextField.RIGHT, visible: (mostrarParametroP && mostrarParametroB), enabled: (mostrarParametroP && mostrarParametroB))
                    txtOdDm.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {

                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtOdDm, 45, 22, 0.1, '.0', '')
                          } catch ( Exception ex ){
                            dataRxValid = false
                            println ex
                          }
                        }
                    })
                    label(text: 'D.I. Binocular', toolTipText: 'Distancia Interpupilar Binocular', visible: (mostrarParametroP && !mostrarParametroB) || mostrarParametroSV, enabled: (mostrarParametroP && !mostrarParametroB) || mostrarParametroSV)
                    txtDILejos = textField(minimumSize: [20, 20], toolTipText: 'Distancia Interpupilar Binocular', horizontalAlignment: JTextField.RIGHT, visible: (mostrarParametroP && !mostrarParametroB) || mostrarParametroSV, enabled: (mostrarParametroP && !mostrarParametroB) || mostrarParametroSV)
                    txtDILejos.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {

                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtDILejos, 90, 45, 1, '0', '')
                          } catch( Exception ex ){
                            dataRxValid = false
                            println ex
                          }
                        }
                    })
                    label(text: 'O.I.', toolTipText: 'Ojo Izquierdo')
                    txtOiEsfera = textField(horizontalAlignment: JTextField.RIGHT)
                    txtOiEsfera.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {

                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtOiEsfera, 35, -35, 0.25, '.00', '+')
                          } catch( Exception ex ){
                            dataRxValid = false
                            println ex
                          }
                        }
                    })
                    txtOiCil = textField(toolTipText: 'Cilindro', horizontalAlignment: JTextField.RIGHT)
                    txtOiCil.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {

                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtOiCil, 12, -12, 0.25, '.00', '-')
                          } catch(Exception ex ){
                            dataRxValid = false
                            println ex
                          }
                        }
                    })
                    txtOiEje = textField(horizontalAlignment: JTextField.RIGHT)
                    txtOiEje.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {

                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtOiEje, 180, 0, 1, '0', '')
                          } catch( Exception ex ){
                            dataRxValid = false
                            println ex
                          }
                        }
                    })
                    txtOiAd = textField(toolTipText: 'Adición', horizontalAlignment: JTextField.RIGHT, visible: mostrarParametroP, enabled: mostrarParametroP)
                    txtOiAd.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {

                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtOiAd, 4, 0.75, 0.25, '.00', '+')
                          } catch( Exception ex ){
                            dataRxValid = false
                            println ex
                          }
                        }
                    })
                    txtOiDm = textField(toolTipText: 'Distancia Monocular', horizontalAlignment: JTextField.RIGHT, visible: (mostrarParametroP && mostrarParametroB), enabled: (mostrarParametroP && mostrarParametroB))
                    txtOiDm.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {

                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtOiDm, 45, 22, 0.1, '.0', '')
                          } catch( Exception ex ){
                            dataRxValid = false
                            println ex
                          }
                        }
                    })
                    label(text: 'Alt. Seg.', toolTipText: 'Altura Segmento', visible: mostrarParametroP, enabled: mostrarParametroP)
                    txtAltOblea = textField(minimumSize: [20, 20], toolTipText: 'Altura Segmento', horizontalAlignment: JTextField.RIGHT, visible: mostrarParametroP, enabled: mostrarParametroP)
                    txtAltOblea.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {

                        }
                        @Override
                        void focusLost(FocusEvent e) {
                          try{
                            validacion(txtAltOblea, 40, 10, 0.5, '.00', '')
                          } catch( Exception ex ){
                            dataRxValid = false
                            println ex
                          }
                        }
                    })
                }
                scrollPane(border: titledBorder(title: 'Observaciones')) {
                    txtObservaciones = textArea(document: new UpperCaseDocument(), lineWrap: true)
                }
            }
            panel(layout: new MigLayout('wrap 3,right', '[right][right]')) {
                btnTraerReceta = button(text: 'Ver Recetas', actionPerformed: { doBringRx() },maximumSize: [110, 90])
                button(text: 'Guardar', actionPerformed: { doRxSave() }, maximumSize: [110, 90])
                button(text: 'Cancelar', actionPerformed: { doCancel() }, maximumSize: [110, 90])
            }
        }
    }

    private void limpiar(JTextField txtField) {

    }

    private void validacion(JTextField txtField, double max, double min, double interval, String format, String mask) {
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
            logger.debug(signo)
            logger.debug(txt)
            txtField.text = ''
            Double multiplo = 0.0
            if (txt.length() > 0) {
                number = Double.parseDouble(txt);
                multiplo = number / interval;
                multiplo = multiplo % 1
                if (multiplo == 0 || multiplo.toString().equals('-0.0')) {
                    if (number >= min && number <= max) {
                        println('nimber: ' + number)
                        println(number.toString().substring(number.toString().indexOf('.'), number.toString().size()))
                        if (format.equals('.00') && number.toString().substring(number.toString().indexOf('.'), number.toString().size()).equals('.0')) {
                            txt = txt + '.00'
                        } else if (format.equals('.0') && number.toString().substring(number.toString().indexOf('.'), number.toString().size()).equals('.0')) {
                            txt = txt + '.0'
                        }
                        println('val: ' + txt)
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

    private void doBindings() {
        sb.build {
            txtEmpleado.setText( receta?.idOpt?.trim() )
            txtFolio.setText(receta?.folio)
            txtOdEsfera.setText(fillDecimals(receta.odEsfR))
            txtOdCil.setText(fillDecimals(receta.odCilR))
            txtOdEje.setText(receta.odEjeR)
            txtOdAd.setText(fillDecimals(receta.odAdcR))
            txtOdDm.setText(fillDecimals(receta.diOd))
            txtDILejos.setText(receta.diLejosR)
            txtOiEsfera.setText(fillDecimals(receta.oiEsfR))
            txtOiCil.setText(fillDecimals(receta.oiCilR))
            txtOiEje.setText(receta.oiEjeR)
            txtOiDm.setText(fillDecimals(receta.diOi))
            txtOiAd.setText(fillDecimals(receta.oiAdcR))
            txtAltOblea.setText(fillDecimals(receta.altOblR))
            txtObservaciones.setText(receta.observacionesR)
            if( editRx ){
              btnTraerReceta.visible = false
            }
        }
        if(CustomerController.requestRxByCustomer(idCliente).size() > 0){
          btnTraerReceta.enabled = true
        } else {
          btnTraerReceta.enabled = false
        }
        //rxModel.fireTableDataChanged()
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

    void doCancel() {
        component.rec = rec
        //this.setVisible(false)
        lstRecetasFinal.clear()
        lstRecetas.clear()
        dispose()
    }




    private void useGlasess() {
        println(cbUso.selectedItem.toString().trim() + '   USO')
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
        receta.setOiAdcR(txtOiAd.text)
        receta.setOiEsfR(txtOiEsfera.text)
        receta.setOiCilR(txtOiCil.text)
        receta.setOiEjeR(txtOiEje.text)
        receta.setDiOi(txtOiDm.text)
        receta.setAltOblR(txtAltOblea.text)

        receta.setObservacionesR(txtObservaciones.text)
        receta.setIdOpt(txtEmpleado.text)
        receta.setFolio(txtFolio.text)
        if (!receta?.idClient) {
            receta.setIdStore(idSucursal)
            receta.setIdClient(idCliente)
        }
        if(idRx != null && idRx > 0){
          receta.id = idRx
        }
        rec = CustomerController.saveRx(receta, "OP")
        doCancel()
    }

    private void doRxSave() {
      if ( dataRxValid && validDataRx() ) {
        if(doOptSearch()){
          String useGlass = cbUso.selectedItem.toString().trim()
          println('UseGlass = ' + useGlass)
          println('ItemUse = ' + itemUso)

          /*B*/ if (useGlass.equals(usoB[0])/*BIFOCAL*/) {
                    useGlass = 'BIFOCAL'

                    if ((itemUso != null) && (itemUso.trim().contains(useGlass))) {
                        useGlasess()
                    } else {
                        sb.optionPane(message: "Receta: " + useGlass + " Articulo: " + itemUso, optionType: JOptionPane.DEFAULT_OPTION)
                                .createDialog(new JTextField(), "Error")
                                .show()
                    }
                    /*P*/
          } else if (useGlass.equals(usoP[0])/*PROGRESIVO*/) {
                    useGlass = 'PROGRESIVO'

                    if ((itemUso != null) && (itemUso.trim().contains(useGlass))) {
                        useGlasess()
                    } else {
                        sb.optionPane(message: "Receta: " + useGlass + " Articulo: " + itemUso, optionType: JOptionPane.DEFAULT_OPTION)
                                .createDialog(new JTextField(), "Error")
                                .show()
                    }

                    /*SV*/
          } else if (useGlass.equals(usoM[0])/*LEJOS*/ || useGlass.equals(usoM[1])/*CERCA*/) {
                    useGlass = 'MONOFOCAL'

                    if ((itemUso != null) && (itemUso.trim().contains(useGlass))) {
                        useGlasess()
                    } else {
                        sb.optionPane(message: "Receta: " + useGlass + " Articulo: " + itemUso, optionType: JOptionPane.DEFAULT_OPTION)
                                .createDialog(new JTextField(), "Error")
                                .show()
                    }
          }
        }
      } else {
          if( !dataRxValid ){
            sb.optionPane(message: "Debe ingresar empleado y folio:", optionType: JOptionPane.DEFAULT_OPTION)
                  .createDialog(new JTextField(), "Error")
                  .show()
          } else {
            sb.optionPane(message: "Verifique los datos de la receta", optionType: JOptionPane.DEFAULT_OPTION)
                  .createDialog(new JTextField(), "Error")
                  .show()
          }
        }
    }


    private void cleanFieldsReceta(){
      receta.altOblR = ''
      receta.diLejosR = ''
      receta.odAdcR = ''
      receta.odCilR = ''
      receta.diOd = ''
      receta.odEjeR = ''
      receta.odEsfR = ''
      receta.oiAdcR = ''
      receta.oiCilR = ''
      receta.diOi = ''
      receta.oiEjeR = ''
      receta.oiEsfR = ''
    }


    private def doClick = { MouseEvent ev ->
        if ( SwingUtilities.isLeftMouseButton( ev ) && ev.source.selectedElement != null ) {
            //Rx selection = new Rx()
            Rx selection = ev.source.selectedElement as Rx
            cleanFieldsReceta()
            fillRxPanel( selection )
            doBindings()
            sb.doOutside {
              lstRecetas.clear()
              lstRecetas.addAll( CustomerController.requestRxByCustomer( idCliente ) )
              rxModel.fireTableDataChanged()
            }
        }
    }


    private void doBringRx() {
      if( cbUso.selectedItem.toString().trim().length() > 0 ){
        List<Rx> lstRx = CustomerController.requestRxByCustomer( idCliente )
        if( lstRx.size() > 0 ){
          /*lstRecetas.clear()
          lstRecetas.addAll( lstRx )*/
          if( !editRx ){
            rxPanel.visible = true
          }
        }
      } else {
          sb.optionPane(message: "Debe seleccionar el uso del lente", optionType: JOptionPane.DEFAULT_OPTION)
                  .createDialog(new JTextField(), "Error")
                  .show()
      }
    }


    private void getRxByClient(){
        List<Rx> lstRx = CustomerController.requestRxByCustomer( idCliente )
        if( lstRx.size() > 1 ){
            if( !editRx ){
              rulesRx( lstRx )
            } else {
                fillRxPanel( receta )
            }
        } else if( lstRx.size() > 0 ) {
            lstRx.get(0).id = null
            fillRxPanel( lstRx.get(0) )
        }
    }


    private void fillRxPanel( Rx rx ){
      //rx.id = null
      String selected = cbUso.selectedItem.toString().trim()
      receta?.observacionesR = rx.observacionesR
      if( selected.equalsIgnoreCase(rx.tipo) ){
        receta = rx
      } else if( selected.equalsIgnoreCase(TAG_LEJOS) && rx.tipo.equalsIgnoreCase(TAG_CERCA) ){
        receta.odCilR = rx.odCilR
        receta.oiCilR = rx.oiCilR
        receta.odEjeR = rx.odEjeR
        receta.oiEjeR = rx.oiEjeR
        receta.diLejosR = rx.diLejosR
      } else if( selected.equalsIgnoreCase(TAG_CERCA) && rx.tipo.equalsIgnoreCase(TAG_LEJOS) ){
        receta.odCilR = rx.odCilR
        receta.oiCilR = rx.oiCilR
        receta.odEjeR = rx.odEjeR
        receta.oiEjeR = rx.oiEjeR
        receta.diLejosR = rx.diLejosR
      } else if( selected.equalsIgnoreCase(TAG_LEJOS) &&
              (!rx.tipo.equalsIgnoreCase(TAG_LEJOS) && !rx.tipo.equalsIgnoreCase(TAG_CERCA))){
        Double diOd = 0.00
        Double diOi = 0.00
        Double diLejosR = 0.00
        try{
          diOd = NumberFormat.getInstance().parse((rx.diOd != null && rx.diOd.trim() != '')
          ? rx.diOd.trim() : '0')
          diOi = NumberFormat.getInstance().parse((rx.diOi != null && rx.diOi.trim() != '')
          ? rx.diOi.trim() : '0')
          diLejosR = NumberFormat.getInstance().parse((rx.diLejosR != null && rx.diLejosR.trim() != '')
          ? rx.diLejosR.trim() : '0')
        } catch ( NumberFormatException e ) { println e }
        receta.odEsfR = rx.odEsfR
        receta.oiEsfR = rx.oiEsfR
        receta.odCilR = rx.odCilR
        receta.oiCilR = rx.oiCilR
        receta.odEjeR = rx.odEjeR
        receta.oiEjeR = rx.oiEjeR
        receta.diLejosR = rx.tipo.equalsIgnoreCase(TAG_BIFOCAL) ? diLejosR : (diOd+diOi).toString()
      }else if( selected.equalsIgnoreCase(TAG_CERCA) &&
              (!rx.tipo.equalsIgnoreCase(TAG_LEJOS) && !rx.tipo.equalsIgnoreCase(TAG_CERCA))){
          Double diOd = 0.00
          Double diOi = 0.00
          Double odEsfera = 0.00
          Double oiEsfera = 0.00
          Double odAdi = 0.00
          Double oiAdi = 0.00
          Double diLejosR = 0.00
          try{
              diOd = NumberFormat.getInstance().parse((rx.diOd != null && rx.diOd.trim() != '')
              ? rx.diOd.trim() : '0')
              diOi = NumberFormat.getInstance().parse((rx.diOi != null && rx.diOi.trim() != '')
              ? rx.diOi.trim() : '0')
              odEsfera = NumberFormat.getInstance().parse((rx.odEsfR != null && rx.odEsfR.trim() != '')
              ? rx.odEsfR.trim().replace('+','') : '0')
              oiEsfera = NumberFormat.getInstance().parse((rx.oiEsfR != null && rx.oiEsfR.trim() != '')
              ? rx.oiEsfR.trim().replace('+','') : '0')
              odAdi = NumberFormat.getInstance().parse((rx.odAdcR != null && rx.odAdcR.trim() != '')
              ? rx.odAdcR.trim().replace('+','') : '0')
              oiAdi = NumberFormat.getInstance().parse((rx.oiAdcR != null && rx.oiAdcR.trim() != '')
              ? rx.oiAdcR.trim().replace('+','') : '0')
              diLejosR = NumberFormat.getInstance().parse((rx.diLejosR != null && rx.diLejosR.trim() != '')
              ? rx.diLejosR.trim() : '0')
          } catch ( NumberFormatException e ) { println e }
          receta.odEsfR = odEsfera+odAdi
          receta.oiEsfR = oiEsfera+oiAdi
          receta.odCilR = rx.odCilR
          receta.oiCilR = rx.oiCilR
          receta.odEjeR = rx.odEjeR
          receta.oiEjeR = rx.oiEjeR
          receta.diLejosR = rx.tipo.equalsIgnoreCase(TAG_BIFOCAL) ? diLejosR : (diOd+diOi).toString()
      } else if( (!selected.equalsIgnoreCase(TAG_LEJOS) && !selected.equalsIgnoreCase(TAG_CERCA)) &&
              rx.tipo.equalsIgnoreCase(TAG_LEJOS)){
          Double diLejos = 0.00
          try{
              diLejos = NumberFormat.getInstance().parse((rx.diLejosR != null && rx.diLejosR.trim() != '') ?
                  rx.diLejosR.trim() : '0')
          } catch ( NumberFormatException e ) { println e }
          receta.odEsfR = rx.odEsfR
          receta.oiEsfR = rx.oiEsfR
          receta.odCilR = rx.odCilR
          receta.oiCilR = rx.oiCilR
          receta.odEjeR = rx.odEjeR
          receta.oiEjeR = rx.oiEjeR
          if( selected.equalsIgnoreCase(TAG_BIFOCAL) ){
            receta.diLejosR = diLejos
          } else {
            receta.diOd = diLejos/2
            receta.diOi = diLejos/2
          }
      } else if( (!selected.equalsIgnoreCase(TAG_LEJOS) && !selected.equalsIgnoreCase(TAG_CERCA)) &&
              rx.tipo.equalsIgnoreCase(TAG_CERCA)){
          Double diCerca = 0.00
          try{
              diCerca = NumberFormat.getInstance().parse((rx.diLejosR != null && rx.diLejosR.trim() != '')
              ? rx.diLejosR.trim() : '0')
          } catch ( NumberFormatException e ) { println e }
          receta.odEsfR = rx.odEsfR
          receta.oiEsfR = rx.oiEsfR
          receta.odCilR = rx.odCilR
          receta.oiCilR = rx.oiCilR
          receta.odEjeR = rx.odEjeR
          receta.oiEjeR = rx.oiEjeR
          if( selected.equalsIgnoreCase(TAG_BIFOCAL) ){
              receta.diLejosR = diCerca
          } else {
              receta.diOd = diCerca/2
              receta.diOi = diCerca/2
          }
      }else if( (!selected.equalsIgnoreCase(TAG_LEJOS) && !selected.equalsIgnoreCase(TAG_CERCA)) &&
              !rx.tipo.equalsIgnoreCase(TAG_LEJOS) && !rx.tipo.equalsIgnoreCase(TAG_LEJOS) ){
          receta = rx
          Double diOd = 0.00
          Double diOi = 0.00
          Double diLejosR = 0.00
          try{
              diOd = NumberFormat.getInstance().parse((rx.diOd != null && rx.diOd.trim() != '')
              ? rx.diOd.trim() : '0')
              diOi = NumberFormat.getInstance().parse((rx.diOi != null && rx.diOi.trim() != '')
              ? rx.diOi.trim() : '0')
              diLejosR = NumberFormat.getInstance().parse((rx.diLejosR != null && rx.diLejosR.trim() != '')
              ? rx.diLejosR.trim() : '0')
            } catch (NumberFormatException e){ println e }
          receta.diLejosR = rx.tipo.equalsIgnoreCase(TAG_BIFOCAL) ? diLejosR : (diOd+diOi).toString()
      }
      receta?.folio = StringUtils.trimToEmpty(rx.folio) != '' ? rx.folio : txtFolio.text.trim()
      SimpleDateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
      String dateRx = df.format( rx.rxDate )
      String today = df.format( new Date() )
      if( txtEmpleado.text.trim().equalsIgnoreCase('') && dateRx.trim().equalsIgnoreCase(today.trim()) ){
          receta?.idOpt = rx.idOpt
      } else {
          receta?.idOpt = txtEmpleado.text.trim()
      }
      receta.id = null
      doBindings()
    }


    private void rulesRx( List<Rx> lstRx ){
      String selected = cbUso.selectedItem.toString().trim()
      Rx rxSelected = new Rx()
      Integer contaUso = 0
      for(Rx rx : lstRx){
          if(rx.tipo.equalsIgnoreCase(selected)){
            contaUso = contaUso+1
            if( contaUso == 1 ){
              rxSelected = rx
            }
          }
        }
      if( contaUso > 1 || (contaUso <= 0 && lstRx.size() > 1)){
        lstRecetas.clear()
        lstRecetas.addAll( lstRx )
        if( !editRx ){
          rxPanel.visible = true
        }
        cleanFieldsReceta()
        doBindings()
      } else if( contaUso == 1 ){
        rxPanel.visible = false
        rxSelected.id = null
        fillRxPanel( rxSelected )
      }
    }


    static String fillDecimals( String data ){
        String value = ''
        if( StringUtils.trimToEmpty(data) != '' && StringUtils.trimToEmpty(data).isNumber() ){
            Double numberVal = 0.00
            try{
                numberVal = NumberFormat.getInstance().parse( data.replace("+","") )
            } catch ( NumberFormatException e ) { println e }
            value = String.format( "%s%.02f", data.contains("+") ? "+" : "", numberVal )
        }
        return value
    }



    Boolean validDataRx(){
      Boolean dataValid = true
      if(StringUtils.trimToEmpty(txtEmpleado.text).isEmpty()){
        dataValid = false
      } else if(txtDILejos.visible && StringUtils.trimToEmpty(txtDILejos.text).length() <= 0){
        dataValid = false
      } else if(txtAltOblea.visible && StringUtils.trimToEmpty(txtAltOblea.text).length() <= 0){
        dataValid = false
      } else if( txtOdDm.visible && StringUtils.trimToEmpty(txtOdDm.text).length() <= 0 ){
        dataValid = false
      } else if( txtOiDm.visible && StringUtils.trimToEmpty(txtOiDm.text).length() <= 0 ){
        dataValid = false
      } else if(StringUtils.trimToEmpty(txtOdCil.text).length() > 0 && StringUtils.trimToEmpty(txtOdEje.text).length() <= 0){
        dataValid = false
      } else if(StringUtils.trimToEmpty(txtOiCil.text).length() > 0 && StringUtils.trimToEmpty(txtOiEje.text).length() <= 0){
        dataValid = false
      }
      return dataValid
    }




}