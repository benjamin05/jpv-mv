package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.model.LcDataset
import mx.lux.pos.ui.model.adapter.LcAdapter
import mx.lux.pos.ui.model.adapter.LcFilter
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import javax.swing.table.AbstractTableModel
import java.awt.*
import java.awt.event.MouseEvent

class LcSelectorDialog extends JDialog {

  private static final String TXT_BUTTON_CLEAR_CAPTION = "Limpiar"
  private static final String TXT_BUTTON_OK_CAPTION = "Aceptar"
  private static final String TXT_BUTTON_SEARCH_CAPTION = "Buscar"
  private static final String TXT_FROM_DATE_LABEL = "De fecha"
  private static final String TXT_TO_DATE_LABEL = "a"
  private static final String TXT_TR_TYPE_LABEL = "Id Pedido"
  private static final String TXT_SITE_TO_LABEL = "Almacén"
  private static final String TXT_REFERENCE_TO_LABEL = "Folio"
  private static final String TXT_PART_NUMBER_LABEL = "Cliente"
  private static final String TXT_PART_CODE_LABEL = "Modelo"
  private static final String TXT_TR_EFF_DATE_LABEL = "Fecha"
  private static final String TXT_TR_MODEL = "Modelo"
  private static final String TXT_TR_NUMBER_LABEL = "Folio"
  private static final String TXT_TR_PART_LIST_LABEL = "Artículos"


  private Logger logger = LoggerFactory.getLogger( this.class )
  private def sb = new SwingBuilder()

  private LcDataset dsInvTr
  private LcAdapter adapter = new LcAdapter( )
  private DateVerifier dv = DateVerifier.instance

  private AbstractTableModel trBrowser
  private JTextField txtDateFrom
  private JTextField txtDateTo
  private JTextField txtType
  private JTextField txtReference
  private JTextField txtSiteTo
  private JTextField txtSku
  private JTextField txtPart
  private JTable tBrowser

    LcSelectorDialog( LcDataset pDataset ) {
    dsInvTr = pDataset
    buildUI()
  }
  
  // UI Layout Definition
  private void buildUI() {
    sb.dialog( this,
        title: "Selector de Pedidos",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [600, 400] as Dimension,
        location: [180, 80] as Point
    ) {
      borderLayout()
      composeFilterPanel( )
      composeTablePanel( )
      composeButtonsPanel( )
    }
  }

  
  private def composeButtonsPanel( ) {
    sb.panel( constraints: BorderLayout.PAGE_END
    ) {
      borderLayout()
      panel( constraints: BorderLayout.LINE_END ) {
        button( text: TXT_BUTTON_OK_CAPTION,
            preferredSize: UI_Standards.BUTTON_SIZE,
            actionPerformed: { onButtonOk() }
        )
      }
    }
  }
  
  private def composeFilterPanel( ) {
    sb.panel( constraints: BorderLayout.PAGE_START ) {
      borderLayout()
      panel(  constraints: BorderLayout.PAGE_START,
             layout: new MigLayout( 'fill, wrap 4', '[][fill,grow][][fill,grow]' )
      ) {
        label( TXT_FROM_DATE_LABEL )
        txtDateFrom = textField ( text: "23/07/12" )
        label( TXT_TO_DATE_LABEL )
        txtDateTo = textField ( text: "23/07/12" )

        label( TXT_TR_TYPE_LABEL, )
        txtType = textField (  )
        label( visible: false, constraints: 'hidemode 3' )
        txtSiteTo = textField ( visible: false, constraints: 'hidemode 3' )

        label( TXT_REFERENCE_TO_LABEL )
        txtReference = textField( )

        label( TXT_PART_NUMBER_LABEL )
        txtSku = textField (  )
        label( TXT_PART_CODE_LABEL )
        txtPart = textField (  )
      }
      panel( constraints: BorderLayout.LINE_END,
             border: BorderFactory.createEmptyBorder(0, 0, 0, 5)
      ) {
          button( text: TXT_BUTTON_CLEAR_CAPTION,
              preferredSize: UI_Standards.BUTTON_SIZE,
              actionPerformed: { onButtonClear() } 
          )
          button( text: TXT_BUTTON_SEARCH_CAPTION,
                  preferredSize: UI_Standards.BUTTON_SIZE,
                  constraints: BorderLayout.EAST,
                  actionPerformed: { onButtonSearch() }
          )
      }
    }
  }
  
  private def composeTablePanel( ) {
    sb.scrollPane( constraints: BorderLayout.CENTER ) {
      tBrowser = table( mouseClicked: doShowItemClick ) {
        trBrowser = tableModel( list: dsInvTr.dataset ) {
          closureColumn( header: TXT_TR_EFF_DATE_LABEL, width: 160, read: { PedidoLc tr -> adapter.getText( tr, LcAdapter.FLD_TR_EFF_DATE ) } )
          //propertyColumn( header: TXT_TR_TYPE_LABEL, propertyName: "idTipoTrans", width: 140, editable: false )
          propertyColumn( header: TXT_TR_TYPE_LABEL, propertyName: "id", width: 140, editable: false )
          closureColumn( header: TXT_TR_MODEL, read: { PedidoLc tr ->  adapter.getModelList( tr.id ) }, minWidth: 150 )
          propertyColumn( header: TXT_TR_NUMBER_LABEL, propertyName: "folio", maxWidth: 60, editable: false )
          closureColumn( header: TXT_PART_NUMBER_LABEL, minWidth: 200, read: { PedidoLc tr ->  adapter.getClient( tr.cliente ) } )
        } as AbstractTableModel
      }
    }
  }
  
  // Public Methods
  void activate() {
    refreshUI()
    setVisible(true)
  }
  
  void assignFilter() {
    logger.debug( "[Dialog] Assign Filter")
    LcFilter filter = dsInvTr.filter
    Date from = dv.parse( txtDateFrom.getText( ).trim( ).length() > 0 ? txtDateFrom.getText( ).trim( ) : "" )
    Date to = dv.parse( txtDateTo.getText( ).trim().length() > 0 ? txtDateTo.getText( ).trim() : "" )
    String factura = StringUtils.trimToEmpty(txtType.getText())
    String[] data = factura.split("-")
    if( factura.contains("-") && data.length > 1 ){
      factura = data[1]
    } else {
      factura = null
    }
    filter.setDateRange( from, to )
    filter.setTrType( factura )
    filter.setReference( txtReference.getText() )
    filter.setPartCode( txtPart.getText() )
    filter.setSku( txtSku.getText() )
    filter.setSiteTo( txtSiteTo.getText() )
  }
  
  void refreshUI() {
    logger.debug( "[Dialog] Update UI" )
    String order = txtType.text
    LcFilter filter = dsInvTr.filter
    txtDateFrom.setText( filter.dateFrom != null ? adapter.getText( filter.dateFrom ) : "" )
    txtDateTo.setText( filter.dateTo != null ? adapter.getText( filter.dateTo ) : "" )
    if( !StringUtils.trimToEmpty(order).equalsIgnoreCase(StringUtils.trimToEmpty(currentSite()))){
      txtType.setText( currentSite()+StringUtils.trimToEmpty( filter.trType ) )
    }
    txtReference.setText( StringUtils.trimToEmpty( filter.reference ) )
    txtSiteTo.setText( filter.siteTo == null ? "" : filter.siteTo.toString( ) )
    txtSku.setText( filter.sku == null ? "" : filter.sku.toString( ) )
    txtPart.setText( StringUtils.trimToEmpty( filter.partCode ) )
    trBrowser.fireTableDataChanged()
  }
  
  // UI Response
  private void onButtonClear() {
    logger.debug( "[Dialog] Button Clear Trigger")
    (dsInvTr.filter as LcFilter).reset( )
    refreshUI( )
  }


  private String currentSite(){
    return "${Registry.currentSite.toString()}-"
  }


  private void onButtonOk() {
    logger.debug( "[Dialog] Button Ok Trigger")
    if ( tBrowser.getSelectedRows( ).size( ) > 0 ) {
      dsInvTr.currentIndex = tBrowser.selectedRows [0]
    }
    setVisible( false )
  }
  
  private void onButtonSearch() {
    logger.debug( "[Dialog] Button Search Trigger")
    assignFilter()
    dsInvTr.requestTransactions( false )
    refreshUI( )
  }


  private def doShowItemClick = { MouseEvent ev ->
    if (SwingUtilities.isLeftMouseButton(ev)) {
      if (ev.clickCount == 2) {
        onButtonOk()
      }
    }
  }


}
