package mx.lux.pos.ui.view.dialog

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.renderer.MoneyCellRenderer

import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import java.awt.*
import java.awt.event.MouseEvent
import java.util.List

class SuggestedItemsDialog extends JDialog {

  private SwingBuilder sb
  private String code
  private List<Item> suggestions = new ArrayList<Item>()
  private List<Item> allSuggestions = new ArrayList<Item>()
  private Item item
  private Item itemDesc
  private JCheckBox cbExistencias
  private JLabel lblDescripcion
  private DefaultTableModel model
  private JTable tableItems
  private static final Integer COLUMN_DESCRIPTION = 2
  private Boolean adjust

  SuggestedItemsDialog( Component parent, String code, List<Item> suggestions, Boolean adjust ) {
    this.code = code
    this.suggestions.addAll( suggestions )
    this.adjust = adjust
    Collections.sort(this.suggestions, new Comparator<Item>() {
        @Override
        int compare(Item o1, Item o2) {
            return o1.id.compareTo(o2.id)
        }
    })
    this.allSuggestions.addAll( suggestions )
    Collections.sort(this.allSuggestions, new Comparator<Item>() {
      @Override
      int compare(Item o1, Item o2) {
        return o1.id.compareTo(o2.id)
      }
    })
    sb = new SwingBuilder()
    item = null
    buildUI( parent )
  }

  Item getItem( ) {
    return item
  }

  private void buildUI( Component parent ) {
    sb.dialog( this,
        title: "Art\u00edculos sugeridos con: ${code ?: ''}",
        location: parent.locationOnScreen,
        resizable: false,
        preferredSize: adjust ? [ 600  , 380 ] as Dimension : [ 520  , 380 ] as Dimension,
        modal: true,
        pack: true,
    ) {
      panel(border: BorderFactory.createEmptyBorder(5, 8, 5, 8)) {
      borderLayout()
      panel(constraints: BorderLayout.PAGE_START, border: BorderFactory.createEmptyBorder(0,0,3,0)) {
        borderLayout()
        label( " Se encontraron ${suggestions.size()} art\u00edculos similares a: ${code}",
               constraints: BorderLayout.PAGE_START)
        label( minimumSize: [10, 3] as Dimension)
        cbExistencias = checkBox(
            text:'Solo con existencias',
            constraints: BorderLayout.PAGE_END,
            actionPerformed: { doValueChange() } )
      }
      scrollPane( constraints: BorderLayout.CENTER ) {
        tableItems = table( selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doItemClick ) {
          model = tableModel( list: suggestions ) {
            if( adjust ){
              closureColumn( header: 'Sku', read: {Item tmp -> tmp?.id}, maxWidth: 60 )
              closureColumn( header: 'Art\u00edculo', read: {Item tmp -> tmp?.name}, maxWidth: 90 )
              closureColumn( header: 'Descripci\u00f3n', read: {Item tmp -> tmp?.description}, maxWidth: 380)
              closureColumn( header: 'Precio', read: {Item tmp -> tmp?.price}, cellRenderer: new MoneyCellRenderer(), maxWidth: 80 )
              closureColumn( header: 'Existencia', read: {Item tmp -> tmp?.stock}, type: Integer, maxWidth: 50 )
            } else {
              closureColumn( header: 'Sku', read: {Item tmp -> tmp?.id}, maxWidth: 60 )
              closureColumn( header: 'Art\u00edculo', read: {Item tmp -> tmp?.name}, maxWidth: 120 )
              closureColumn( header: 'Descripci\u00f3n', read: {Item tmp -> tmp?.description}, maxWidth: 180)
              closureColumn( header: 'Precio', read: {Item tmp -> tmp?.price}, cellRenderer: new MoneyCellRenderer(), maxWidth: 100 )
              closureColumn( header: 'Existencia', read: {Item tmp -> tmp?.stock}, type: Integer, maxWidth: 80 )
            }
          } as DefaultTableModel
        }

        tableItems.selectionModel.addListSelectionListener( new ListSelectionListener() {
          @Override
          void valueChanged( ListSelectionEvent ev ) {

            String description = tableItems.getModel().getValueAt( tableItems.selectedRow, COLUMN_DESCRIPTION )
            if( description != null ){
              description = description.trim().replace( ' ','' )
              if( description.length() > 80 ){
                if( description.length() < 160 ){
                  lblDescripcion.text = "<html>${description.substring(0,80)}<br>${description.substring(80)}<br><html>"
                } else {
                  lblDescripcion.text = "<html>${description.substring(0,80)}<br>${description.substring(80,160)}<br>${description.substring(160)}<br><html>"
                }
              } else {
                lblDescripcion.text = description
              }
            } else {
              lblDescripcion.text = ' '
            }
          }
        } )
      }

        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          lblDescripcion = label( text: '<html> <br> <html> ', border: titledBorder( '' ), constraints: BorderLayout.PAGE_START )
          button( 'Cerrar',
              defaultButton: true,
              preferredSize: UI_Standards.BUTTON_SIZE,
              constraints: BorderLayout.LINE_END,
              actionPerformed: {dispose()} )
        }
    }
    }
  }

  private def doItemClick = { MouseEvent ev ->
    if ( SwingUtilities.isLeftMouseButton( ev ) ) {
      if ( ev.clickCount == 2 ) {
        item = ev.source.selectedElement
        dispose()
      }
    }
  }

  private void doValueChange() {
    suggestions.clear()
    if( cbExistencias.selected ){
      for( Item item : allSuggestions ){
        if( item.stock != 0 ){
          suggestions.add( item )
        }
      }
    } else {
      suggestions.addAll( allSuggestions )
    }
    Collections.sort(this.suggestions, new Comparator<Item>() {
      @Override
      int compare(Item o1, Item o2) {
        return o1.id.compareTo(o2.id)
      }
    })
    model.fireTableDataChanged()
  }

  /*@Override
  void valueChanged( ListSelectionEvent ev ) {
    itemDesc = ev.source.selectedElement
    if( itemDesc.reference.length() > 45 ){
      lblDescripcion.text = "<html>${itemDesc.reference.substring(0,45)}<br>${itemDesc.reference.substring(45)}<br><html>"
    } else {
      lblDescripcion.text = itemDesc.reference
    }
  }*/
}
