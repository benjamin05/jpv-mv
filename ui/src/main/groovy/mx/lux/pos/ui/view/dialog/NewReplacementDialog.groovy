package mx.lux.pos.ui.view.dialog

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.ClientesJava
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.java.repository.Repo
import mx.lux.pos.java.repository.RepoCausa
import mx.lux.pos.java.repository.RepoDetJava
import mx.lux.pos.java.repository.RepoResp
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.IOController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Rx
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.model.User
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.panel.OrderPanel
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.text.NumberFormat
import java.util.List
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.text.DateFormat
import java.text.SimpleDateFormat

class NewReplacementDialog extends JDialog implements FocusListener {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private Logger log = LoggerFactory.getLogger(this.getClass())

  private JTextField txtRx
  private JTextField txtSucursal
  private JComboBox cbArea
  private JComboBox cbCausa
  private JTextField txtProblema
  private JTextField txtDiagnostico
  private JTextField txtUso
  private JTextField txtIdResponsable
  private JTextField txtResponsable

  private JCheckBox cbOd
  private JCheckBox cbOi

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

  private JTextField txtDh
  private JTextField txtDv
  private JTextField txtPte
  private JTextField txtBase

  private JButton btnModificar
  private JButton btnRestablecer

  private Rx receta
  private Rx rec

  private JTextArea txtObservaciones

  public DefaultTableModel repoModel

  private List<RepoResp> lstRepoResp = new ArrayList<RepoResp>()
  private List<RepoCausa> lstRepoCausa = new ArrayList<>()
  private List<RepoDetJava> lstRepoDet = new ArrayList<>()

  public boolean button = false

  NewReplacementDialog( ) {
    lstRepoResp = OrderController.buscaResponsables( )
    lstRepoCausa = OrderController.buscaCausas()
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Reposicion",
        resizable: true,
        pack: true,
        modal: true,
        minimumSize: [ 800, 450 ],
        location: [ 200, 10 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 5", "[][grow,fill][][fill][grow,fill]", "[][]" ) ) {
          label( text: "Rx" )
          txtRx = textField( )
          txtRx.addFocusListener( this )
          label( text: "Sucursal" )
          txtSucursal = textField( text: currentSite(), constraints: 'span 2', enabled: false  )
          label( text: "Area" )
          cbArea = comboBox( items: lstRepoResp*.responsable, actionPerformed: { onSelectedArea( ) } )
          label( text: "Responsable" )
          txtIdResponsable = textField( enabled: false )
          txtIdResponsable.addFocusListener( this )
          txtResponsable = textField( enabled: false )
          label( text: "Causa" )
          cbCausa = comboBox( items: lstRepoCausa*.descr, constraints: 'span 2' )
          label( " ", constraints: 'span 2' )
          label( text: "Problema" )
          txtProblema = textField( constraints: 'span 5', document: new UpperCaseDocument() )
          label( text: "Diagnostico" )
          txtDiagnostico = textField( constraints: 'span 5', document: new UpperCaseDocument() )
          label( "Receta" )
          label( " " )
          label( "Uso" )
          txtUso = textField( enabled: false )
          label( " ", constraints: 'span 2' )
          label( " " )
          panel(constraints: 'span 5', layout: new MigLayout('fill,wrap ,center', '[fill,grow]')) {
                panel(layout: new MigLayout('fill,wrap 8,center',
                        '''[center][fill,grow,center][fill,grow,center][fill,grow,center][fill,grow,center]
                            [fill,grow,center][center][fill,grow,center]''')) {
                    label()
                    label(text: 'Esfera', horizontalAlignment: JTextField.CENTER)
                    label(text: 'Cil.', toolTipText: 'Cilindro', horizontalAlignment: JTextField.CENTER )
                    label(text: 'Eje', horizontalAlignment: JTextField.CENTER )
                    label(text: 'Ad.', toolTipText: 'Adición', horizontalAlignment: JTextField.CENTER)
                    label(text: 'D.M.', toolTipText: 'Distancia Monocular', horizontalAlignment: JTextField.CENTER  )
                    label()
                    label()
                    cbOd = checkBox( text: "OD", toolTipText: 'Ojo Derecho' )
                    //label(text: 'O.D.', toolTipText: 'Ojo Derecho')
                    txtOdEsfera = textField( horizontalAlignment: JTextField.RIGHT, enabled: false )
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
                    txtOdCil = textField(toolTipText: 'Cilindro', horizontalAlignment: JTextField.RIGHT, enabled: false )
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
                    txtOdEje = textField(toolTipText: 'Eje',horizontalAlignment: JTextField.RIGHT, enabled: false )
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
                    txtOdAd = textField( toolTipText: 'Adición', horizontalAlignment: JTextField.RIGHT, enabled: false)
                    txtOdAd.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                            //limpiar(txtOdAd)
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
                    txtOdDm = textField( toolTipText: 'Distancia Monocular', horizontalAlignment: JTextField.RIGHT, enabled: false)
                    txtOdDm.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                            //limpiar(txtOdDm)
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
                    label(text: 'D.I. Binocular', toolTipText: 'Distancia Interpupilar Binocular')
                    txtDILejos = textField(minimumSize: [20, 20], toolTipText: 'Distancia Interpupilar Binocular', horizontalAlignment: JTextField.RIGHT, enabled: false )
                    txtDILejos.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                            //limpiar(txtDILejos)
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
                    cbOi = checkBox( text: "OI", toolTipText: 'Ojo Izquierdo' )
                    //label(text: 'O.I.', toolTipText: 'Ojo Izquierdo')
                    txtOiEsfera = textField( horizontalAlignment: JTextField.RIGHT, enabled: false )
                    txtOiEsfera.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                            //limpiar(txtOiEsfera)
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
                    txtOiCil = textField( toolTipText: 'Cilindro', horizontalAlignment: JTextField.RIGHT, enabled: false )
                    txtOiCil.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                            //limpiar(txtOiCil)
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
                    txtOiEje = textField( horizontalAlignment: JTextField.RIGHT, enabled: false )
                    txtOiEje.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                            //limpiar(txtOiEje)
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
                    txtOiAd = textField( toolTipText: 'Adición', horizontalAlignment: JTextField.RIGHT, enabled: false )
                    txtOiAd.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                            //limpiar(txtOiAd)
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
                    txtOiDm = textField( toolTipText: 'Distancia Monocular', horizontalAlignment: JTextField.RIGHT, enabled: false )
                    txtOiDm.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                            //limpiar(txtOiDm)
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
                    label(text: 'Alt. Seg.', toolTipText: 'Altura Segmento')
                    txtAltOblea = textField( minimumSize: [20, 20], toolTipText: 'Altura Segmento', horizontalAlignment: JTextField.RIGHT, enabled: false )
                    txtAltOblea.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                            //limpiar(txtAltOblea)
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
                    txtObservaciones = textArea(document: new UpperCaseDocument(), lineWrap: true, enabled: false )
                }
          }
          label( " ", constraints: 'span 3' )
          btnModificar = button( text: "Modificar", actionPerformed: { onButtonModify( ) }, preferredSize: [100,25], enabled: false )
          btnRestablecer = button( text: "Restablecer", actionPerformed: { onButtonRestore( ) }, preferredSize: [70,25], enabled: false )

          label( "Cambios" )
          panel( constraints: 'span 5', layout: new MigLayout( 'wrap ', '[fill,grow]', '[fill,150!]' ) ) {
            scrollPane( ) {
              table(selectionMode: ListSelectionModel.SINGLE_SELECTION/*, mouseClicked: doShowItemClickSend*/){
                repoModel = tableModel(list: lstRepoDet) {
                  closureColumn( header: 'Ojo', read: {RepoDetJava tmp -> tmp.ojo}, preferredWidth: 30)
                  closureColumn( header: 'Tipo', read: {RepoDetJava tmp -> tmp.tipo}, preferredWidth: 60)
                  closureColumn( header: 'Anterior', read: {RepoDetJava tmp -> tmp.vOld}, preferredWidth: 80)
                  closureColumn( header: 'Nuevo', read: {RepoDetJava tmp -> tmp.vNew}, preferredWidth: 80)
                } as DefaultTableModel
              }
            }
          }
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


    private void doBindings() {
        sb.build {
            txtUso.setText(receta.tipo)
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
        }
    }


  // UI Management
  protected void refreshUI( ) {

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
    if( receta != null && receta.id != null && txtOdEsfera.enabled && txtOiEsfera.enabled ){
      if( validData() ){
      List<RepoDetJava> lstRepoDet = new ArrayList<>()
      Integer numOrden = OrderController.buscaNextNumeroOrdenRepo( StringUtils.trimToEmpty(txtRx.text) )
      if(!StringUtils.trimToEmpty(txtOdEsfera.text).equalsIgnoreCase(receta.odEsfR)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = "D"
        repoDet.tipo = "ESF"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.odEsfR))
        repoDet.setvNew(StringUtils.trimToEmpty(txtOdEsfera.text))
        repoDet.campo = "od_esf"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setOdEsfR(txtOdEsfera.text)
      if(!StringUtils.trimToEmpty(txtOdCil.text).equalsIgnoreCase(receta.odCilR)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = "D"
        repoDet.tipo = "CIL"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.odCilR))
        repoDet.setvNew(StringUtils.trimToEmpty(txtOdCil.text))
        repoDet.campo = "od_cil"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setOdCilR(txtOdCil.text)
      if(!StringUtils.trimToEmpty(txtOdEje.text).equalsIgnoreCase(receta.odEjeR)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = "D"
        repoDet.tipo = "EJE"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.odEjeR))
        repoDet.setvNew(StringUtils.trimToEmpty(txtOdEje.text))
        repoDet.campo = "od_eje"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setOdEjeR(txtOdEje.text)
      if(!StringUtils.trimToEmpty(txtOdAd.text).equalsIgnoreCase(receta.odAdcR)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = "D"
        repoDet.tipo = "ADC"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.odAdcR))
        repoDet.setvNew(StringUtils.trimToEmpty(txtOdAd.text))
        repoDet.campo = "od_adc"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setOdAdcR(txtOdAd.text)
      if(!StringUtils.trimToEmpty(txtOdDm.text).equalsIgnoreCase(receta.diOd)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = "D"
        repoDet.tipo = "DI"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.diOd))
        repoDet.setvNew(StringUtils.trimToEmpty(txtOdDm.text))
        repoDet.campo = "di_od"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setDiOd(txtOdDm.text)
      if(!StringUtils.trimToEmpty(txtDILejos.text).equalsIgnoreCase(receta.diLejosR)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = ""
        repoDet.tipo = "DI_LEJOS"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.diLejosR))
        repoDet.setvNew(StringUtils.trimToEmpty(txtDILejos.text))
        repoDet.campo = "di_lejos"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setDiLejosR(txtDILejos.text)
      if(!StringUtils.trimToEmpty(txtOiAd.text).equalsIgnoreCase(receta.oiAdcR)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = "I"
        repoDet.tipo = "ADC"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.oiAdcR))
        repoDet.setvNew(StringUtils.trimToEmpty(txtOiAd.text))
        repoDet.campo = "oi_adc"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setOiAdcR(txtOiAd.text)
      if(!StringUtils.trimToEmpty(txtOiEsfera.text).equalsIgnoreCase(receta.oiEsfR)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = "I"
        repoDet.tipo = "ESF"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.oiEsfR))
        repoDet.setvNew(StringUtils.trimToEmpty(txtOiEsfera.text))
        repoDet.campo = "oi_esf"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setOiEsfR(txtOiEsfera.text)
      if(!StringUtils.trimToEmpty(txtOiCil.text).equalsIgnoreCase(receta.oiCilR)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = "I"
        repoDet.tipo = "CIL"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.oiCilR))
        repoDet.setvNew(StringUtils.trimToEmpty(txtOiCil.text))
        repoDet.campo = "oi_cil"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setOiCilR(txtOiCil.text)
      if(!StringUtils.trimToEmpty(txtOiEje.text).equalsIgnoreCase(receta.oiEjeR)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = "I"
        repoDet.tipo = "EJE"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.oiEjeR))
        repoDet.setvNew(StringUtils.trimToEmpty(txtOiEje.text))
        repoDet.campo = "oi_eje"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setOiEjeR(txtOiEje.text)
      if(!StringUtils.trimToEmpty(txtOiDm.text).equalsIgnoreCase(receta.diOi)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = "I"
        repoDet.tipo = "DI"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.getDiOi()))
        repoDet.setvNew(StringUtils.trimToEmpty(txtOiDm.text))
        repoDet.campo = "di_oi"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setDiOi(txtOiDm.text)
      if(!StringUtils.trimToEmpty(txtAltOblea.text).equalsIgnoreCase(receta.altOblR)){
        RepoDetJava repoDet = new RepoDetJava()
        repoDet.factura = StringUtils.trimToEmpty(txtRx.text)
        repoDet.numOrden = numOrden
        repoDet.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
        repoDet.ojo = ""
        repoDet.tipo = "ALT_OBL"
        repoDet.setvOld(StringUtils.trimToEmpty(receta.altOblR))
        repoDet.setvNew(StringUtils.trimToEmpty(txtAltOblea.text))
        repoDet.campo = "alt_obl"
        repoDet.fecha = new Date()
        lstRepoDet.add(repoDet)
      }
      receta.setAltOblR(txtAltOblea.text)
      receta.setObservacionesR(txtObservaciones.text)
      receta.setDh(StringUtils.trimToEmpty(receta.dh).length() > 0 ? StringUtils.trimToEmpty(receta.dh) : "")
      receta.setDv(StringUtils.trimToEmpty(receta.dv).length() > 0 ? StringUtils.trimToEmpty(receta.dv) : "")
      receta.setPte(StringUtils.trimToEmpty(receta.pte).length() > 0 ? StringUtils.trimToEmpty(receta.pte) : "")
      receta.setBase(StringUtils.trimToEmpty(receta.base).length() > 0 ? StringUtils.trimToEmpty(receta.base) : "")
      CustomerController.saveRx(receta, "")
      User user = Session.get(SessionItem.USER) as User
      String idCausa = ""
      String repoCausa = cbCausa.selectedItem as String
      for(RepoCausa causa : lstRepoCausa){
        if( StringUtils.trimToEmpty(repoCausa).equalsIgnoreCase(StringUtils.trimToEmpty(causa.descr)) ){
          idCausa = StringUtils.trimToEmpty(causa.idCausa.toString());
        }
      }
      String repoResp = cbArea.selectedItem as String
      ClientesJava customer = CustomerController.buscaClientePorRx( StringUtils.trimToEmpty(txtRx.text) )
      String ojo = ""
      if( cbOd.selected && cbOi.selected ){
        ojo = "P"
      } else if( cbOd.selected ){
        ojo = "R"
      } else if( cbOi.selected ){
        ojo = "L"
      }
      Repo repo = new Repo()
      repo.factura = StringUtils.trimToEmpty(txtRx.text)
      repo.numOrden = numOrden
      repo.emp = user.username
      repo.resp = StringUtils.trimToEmpty(txtIdResponsable.text)
      repo.fecha = new Date()
      repo.tipo = 'N'
      repo.idCliente = receta.idClient
      repo.causa = StringUtils.trimToEmpty(idCausa)
      repo.problema = StringUtils.trimToEmpty(txtProblema.text)
      repo.dx = StringUtils.trimToEmpty(txtDiagnostico.text)
      repo.instrucciones = ""
      repo.setsUsoAnteojos(StringUtils.trimToEmpty(receta.useGlasses))
      repo.odEsf = StringUtils.trimToEmpty(receta.odEsfR)
      repo.odCil = StringUtils.trimToEmpty(receta.odCilR)
      repo.odEje = StringUtils.trimToEmpty(receta.odEjeR)
      repo.odAdc = StringUtils.trimToEmpty(receta.odAdcR)
      repo.odAv = StringUtils.trimToEmpty(receta.odAvR.endsWith("/") ? "" : receta.odAvR)
      repo.diOd = StringUtils.trimToEmpty(receta.diOd)
      repo.odPrisma = StringUtils.trimToEmpty(receta.odPrismH)
      repo.odPrismaV = StringUtils.trimToEmpty(receta.odPrismaV)
      repo.oiEsf = StringUtils.trimToEmpty(receta.oiEsfR)
      repo.oiCil = StringUtils.trimToEmpty(receta.oiCilR)
      repo.oiEje = StringUtils.trimToEmpty(receta.oiEjeR)
      repo.oiAdc = StringUtils.trimToEmpty(receta.oiAdcR)
      repo.oiAv = StringUtils.trimToEmpty(receta.oiAvR.endsWith("/") ? "" : receta.oiAvR )
      repo.diOi = StringUtils.trimToEmpty(receta.diOi)
      repo.oiPrisma = StringUtils.trimToEmpty(receta.oiPrismH)
      repo.oiPrismaV = StringUtils.trimToEmpty(receta.oiPrismaV)
      repo.diLejos = StringUtils.trimToEmpty(receta.diLejosR)
      repo.diCerca = StringUtils.trimToEmpty(receta.diCercaR)
      repo.altObl = StringUtils.trimToEmpty(receta.altOblR)
      repo.observaciones = StringUtils.trimToEmpty(receta.observacionesR)
      repo.area = StringUtils.trimToEmpty(repoResp)
      repo.folio = StringUtils.trimToEmpty("R"+StringUtils.trimToEmpty(txtRx.text)+StringUtils.trimToEmpty(numOrden.toString()))
      repo.cliente = customer != null ? customer.nombreCompleto : ""
      repo.material = ""
      repo.tratamientos = ""
      repo.suc = StringUtils.trimToEmpty(Registry.currentSite.toString())
      repo.ojo = ojo
      CustomerController.saveReposicion(repo)
      CustomerController.saveListaReposicionDet(lstRepoDet)
      OrderController.printReposicion( repo )
      OrderController.makeAcusesReposicion(repo)
      dispose()
      } else {
        sb.optionPane(message: 'Verifique que todos los campos esten llenos',messageType: JOptionPane.ERROR_MESSAGE).
                createDialog(this, 'Campo Vacio').show()
      }
    }
  }


  protected void onButtonModify( ) {
    txtOdEsfera.enabled = true
    txtOdCil.enabled = true
    txtOdEje.enabled = true
    txtOdAd.enabled = true
    txtOdDm.enabled = true
    txtDILejos.enabled = true
    txtOiEsfera.enabled = true
    txtOiCil.enabled = true
    txtOiEje.enabled = true
    txtOiDm.enabled = true
    txtOiAd.enabled = true
    txtAltOblea.enabled = true
    txtObservaciones.enabled = true
  }

  protected void onButtonRestore( ) {
    txtOdEsfera.enabled = false
    txtOdCil.enabled = false
    txtOdEje.enabled = false
    txtOdAd.enabled = false
    txtOdDm.enabled = false
    txtDILejos.enabled = false
    txtOiEsfera.enabled = false
    txtOiCil.enabled = false
    txtOiEje.enabled = false
    txtOiDm.enabled = false
    txtOiAd.enabled = false
    txtAltOblea.enabled = false
    txtObservaciones.enabled = false
    receta = OrderController.findRxByBill( StringUtils.trimToEmpty(txtRx.text) )
    if( receta != null && receta.id != null ){
      doBindings()
    }
  }


    private void validacion(JTextField txtField, double max, double min, double interval, String format, String mask){
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

    String currentSite( ){
      return IOController.instance.findNameCurrentSite()
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

    @Override
    void focusGained(FocusEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void focusLost(FocusEvent e) {
      receta = OrderController.findRxByBill( StringUtils.trimToEmpty(txtRx.text) )
      if( receta != null && receta.id != null ){
        lstRepoDet.clear()
        lstRepoDet.addAll(OrderController.buscaRepoDetByBill(StringUtils.trimToEmpty(txtRx.text)))
        repoModel.rowsModel.setValue(lstRepoDet)
        repoModel.fireTableDataChanged()
        btnModificar.enabled = true
        btnRestablecer.enabled = true
        doBindings()
      } else {
        btnModificar.enabled = false
        btnRestablecer.enabled = false
      }
      if( StringUtils.trimToEmpty(txtIdResponsable.text).length() > 0 ){
        Boolean empValid = false
        String input = StringUtils.trimToEmpty(txtIdResponsable.text)
        String optometrista = CustomerController.findOptometrista(input)
        if (optometrista != null) {
          txtResponsable.setText(optometrista)
        } else {
          txtResponsable.text = ""
          txtIdResponsable.text = ""
        }
      }
    }


    void onSelectedArea( ) {
        txtIdResponsable.enabled = StringUtils.trimToEmpty(cbArea.selectedItem as String).equalsIgnoreCase("SUC")
    }


  Boolean validData(){
    Boolean valid = true
    if( StringUtils.trimToEmpty(cbArea.selectedItem as String).length() <= 0 ){
      valid = false
    } else if( txtIdResponsable.enabled && StringUtils.trimToEmpty(txtIdResponsable.text).length() <= 0){
      valid = false
    } else if( StringUtils.trimToEmpty(cbCausa.selectedItem as String).length() <= 0 ){
      valid = false
    } else if( StringUtils.trimToEmpty(txtProblema.text).length() <= 0 ){
      valid = false
    } else if( StringUtils.trimToEmpty(txtDiagnostico.text).length() <= 0 ){
      valid = false
    }
    return valid
  }


}
