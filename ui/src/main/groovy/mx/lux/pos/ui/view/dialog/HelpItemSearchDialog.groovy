package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.resources.UI_Standards
import net.miginfocom.swing.MigLayout

import javax.swing.*
import java.awt.*

class HelpItemSearchDialog extends JDialog {

  private def sb = new SwingBuilder()

  public boolean button = false
  private String generics

  HelpItemSearchDialog( ) {
    fillGenericTable()
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Busqueda de Articulos",
        resizable: true,
        pack: true,
        modal: true,
        //preferredSize: [ 360, 220 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap ", "[fill][grow,fill]", "" ) ) {
          def displayFont = new Font('', Font.BOLD, 12)
          label( text: "Ejemplo", border: titledBorder(title: ''), font: displayFont, alignmentX: CENTER_ALIGNMENT )
          label( text: "Muestra articulos que coincidan con:", border: titledBorder(title: ''), font: displayFont, alignmentX: CENTER_ALIGNMENT )
            label( text: "RB*", border: titledBorder(title: '') )
            label( text: "Letras especificadas", border: titledBorder(title: '') )
            label( text: "RB*+A", border: titledBorder(title: ''), toolTipText: generics )
            label( text: "Letras especificadas+genericos.", border: titledBorder(title: ''), toolTipText: generics )
            label( text: "D+BIOMED", border: titledBorder(title: '') )
            label( text: "D+Descripcion", border: titledBorder(title: '') )
            label( text: "+Q", border: titledBorder(title: ''), toolTipText: generics )
            label( text: "Generico", border: titledBorder(title: ''), toolTipText: generics )
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
  protected void fillGenericTable(){
    generics = '<html><table border="1"> <tr><td>Letra</td><td>Generico</td></tr> <tr><td align="center">A</td><td>Armazones</td></tr> ' +
            '<tr><td align="center">B</td><td>Lentes</td></tr> <tr><td align="center">C</td><td>Lentes de Contacto</td></tr>' +
            '<tr><td align="center">E</td><td>Accesorios</td></tr> <tr><td align="center">F</td><td>Lentes HD</td></tr>' +
            '<tr><td align="center">G</td><td>Tratamientos</td></tr> <tr><td align="center">J</td><td>Seguros</td></tr>' +
            '<tr><td align="center">L</td><td>Especiales</td></tr> <tr><td align="center">M</td><td>Materiales</td></tr>' +
            '<tr><td align="center">Q</td><td>Paquetes</td></tr> <tr><td align="center">S</td><td>Servicios</td></tr>' +
            '<tr><td align="center">T</td><td>Color</td></tr> <html>'
  }


  // Public Methods

  // UI Response
  protected void onButtonOk( ) {
    dispose()
  }
}
