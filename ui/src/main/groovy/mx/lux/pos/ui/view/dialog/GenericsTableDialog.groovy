package mx.lux.pos.ui.view.dialog

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.model.Generico
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.ItemController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.HelpSearchItem
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.util.List

class GenericsTableDialog extends JDialog {

  private def sb = new SwingBuilder()

  private String generics
  private List<HelpSearchItem> options = new ArrayList<>()

  public boolean button = false

  GenericsTableDialog( ) {
    fillTable()
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Seleccionar fechas",
        resizable: true,
        pack: true,
        modal: true,
        //preferredSize: [ 360, 220 ],
        location: [ 200, 250 ],
    ) {
        panel() {
            borderLayout()
            panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap ", "[grow,fill,275!]", "[fill,300!]" ) ) {
              scrollPane(border: titledBorder(title: '')) {
                table(selectionMode: ListSelectionModel.SINGLE_SELECTION, background: Color.YELLOW) {
                  tableModel(list: options) {
                    closureColumn(header: 'Letra', read: { HelpSearchItem tmp -> tmp?.example }, maxWidth: 60)
                    closureColumn(header: 'Generico', read: { HelpSearchItem tmp -> tmp?.description }, maxWidth: 200)
                  } as DefaultTableModel
                }
              }
            }
            panel( constraints: BorderLayout.PAGE_END ) {
                borderLayout()
                panel( constraints: BorderLayout.LINE_END ) {
                    button( text: "Aceptar", preferredSize: UI_Standards.BUTTON_SIZE,
                            actionPerformed: { onButtonOk() }
                    )
                }
            }
        }

    }
  }

  // UI Management

  // UI Response
  protected void onButtonOk( ) {
    dispose()
  }


  protected void fillTable(){
    List<Generico> optionsTmp = ItemController.generics()
    for( Generico option : optionsTmp ){
      this.options.add(HelpSearchItem.toHelp(option.id,option.descripcion))
    }
  }
}
