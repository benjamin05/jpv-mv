package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.java.repository.JbLlamadaJava
import mx.lux.pos.java.repository.JbTrack
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Invoice
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.dialog.ContactosDialog
import mx.lux.pos.ui.view.dialog.PopUpMenu
import mx.lux.pos.ui.view.dialog.ReceiveDialog
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import java.text.SimpleDateFormat

class ContactosPanel extends JPanel{

  private static final String DATE_TIME_FORMAT = 'dd-MM-yyyy HH:mm'
  private static final String TAG_ESTADO_EP = 'EP'
  private static final String TAG_ESTADO_REP = 'REP'
  private static final String TAG_ESTADO_RS = 'RS'
  private static final String TAG_ESTADO_TE = 'TE'
  private static final String TAG_ESTADO_CN = 'CN'
  private static final String TAG_ESTADO_PE = 'PE'

  private SwingBuilder sb
  private JTextField txtPendRet
  private JTextField txtAtendio
  private JSpinner dateEnd
  private List<JbLlamadaJava> lstCalls = new ArrayList<>()
  public DefaultTableModel callsModel
  private String pendRet

  public SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy")

  private static final String TAG_CANCELADO = 'T'

  ContactosPanel( ) {
    sb = new SwingBuilder()
    loadData( "" )
    buildUI()
    doBindings()
  }

  private void buildUI( ) {
    sb.panel( this, layout: new MigLayout('wrap 2', '[fill,grow][fill,grow]', '[fill]') ) {
      panel( border: titledBorder(title: ''), layout: new MigLayout( 'center,wrap 2', '[fill][fill,grow]' ), maximumSize: [580,100] ) {
          label( 'Pendientes/Retrasados' )
          txtPendRet = textField( text: pendRet, editable: false, horizontalAlignment: SwingConstants.CENTER )
      }
      panel( border: titledBorder(title: ''), layout: new MigLayout( 'center,wrap 3', '[fill][fill,grow]' ), maximumSize: [350,100] ) {
        label( 'Atendio' )
        txtAtendio = textField( )
        button( 'Buscar', actionPerformed: doSearchRx, )
      }
      panel( layout: new MigLayout( 'wrap ', '[fill,grow]', '[]' ), constraints: 'span2' ) {
        scrollPane( ) {
          table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowItemClickSend){
            callsModel = tableModel(list: lstCalls) {
              closureColumn( header: 'Rx', read: {JbLlamadaJava tmp -> tmp.rx}, preferredWidth: 50)
              closureColumn( header: 'Cliente', read: {JbLlamadaJava tmp -> tmp.jb.cliente}, preferredWidth: 210)
              closureColumn( header: 'Venta', read: {JbLlamadaJava tmp -> df.format(tmp.jb.fechaVenta)}, preferredWidth: 75)
              closureColumn( header: 'Atendio', read: {JbLlamadaJava tmp -> tmp.empAtendio}, preferredWidth: 50)
              closureColumn( header: 'Tipo Con.', read: {JbLlamadaJava tmp -> tmp.tipo}, preferredWidth: 90)
              closureColumn( header: 'Contacto', read: {JbLlamadaJava tmp -> tmp.formaContacto?.tipoContacto?.descripcion}, preferredWidth: 65)
              closureColumn( header: 'Estado Con.', read: {JbLlamadaJava tmp -> getEstadoCon(StringUtils.trimToEmpty(tmp.estado))},
                      preferredWidth: 80)
              closureColumn( header: 'Promesa', read: {JbLlamadaJava tmp -> df.format(tmp.jb.fechaPromesa)}, preferredWidth: 75)
            } as DefaultTableModel
          }
        }
      }
    }
  }

  public void doBindings( ) {
    callsModel.rowsModel.setValue(lstCalls)
    callsModel.fireTableDataChanged()
    txtPendRet.text = pendRet
  }


  private def doSearchRx = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    loadData( StringUtils.trimToEmpty(txtAtendio.text) )
    doBindings()
    source.enabled = true
  }

  void loadData( String empAtendio ){
    lstCalls = OrderController.findJbPendingCalls( empAtendio )
    Integer retrasados = 0
    for(JbLlamadaJava jb : lstCalls){
      if(StringUtils.trimToEmpty(jb.tipo).equalsIgnoreCase("RETRASADO")){
        retrasados = retrasados+1
      }
    }
    pendRet = "${StringUtils.trimToEmpty(lstCalls.size().toString())}/${StringUtils.trimToEmpty(retrasados.toString())}"
  }


  private def doShowItemClickSend = { MouseEvent ev ->
    JbLlamadaJava selectedData = ev.source.selectedElement as JbLlamadaJava
    if (SwingUtilities.isRightMouseButton(ev)) {
      if( selectedData != null ){
        PopUpMenu menu = new PopUpMenu( ev.component, ev.component.getX(), ev.component.getY(), StringUtils.trimToEmpty(selectedData.rx), "contactos", this );
      }
    } else if (ev.clickCount == 2) {
      if( selectedData != null ){
        ContactosDialog dialog = new ContactosDialog( StringUtils.trimToEmpty(selectedData.rx) )
        dialog.show()
        loadData( StringUtils.trimToEmpty(txtAtendio.text) )
        doBindings()
      }
    }
  }

  private static String getEstadoCon( String estado ){
    String estadoDesc = ""
    if(estado.equalsIgnoreCase("PN")){
      estadoDesc = "Pendiente"
    } else if( estado.equalsIgnoreCase("NC") ){
      estadoDesc = "No Contesto"
    }
    return estadoDesc
  }


}
