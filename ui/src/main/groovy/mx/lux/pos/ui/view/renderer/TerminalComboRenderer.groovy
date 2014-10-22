package mx.lux.pos.ui.view.renderer

import mx.lux.pos.ui.model.Terminal

import javax.swing.*
import java.awt.*

class TerminalComboRenderer extends JLabel implements ListCellRenderer {

  @Override
  Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
    Terminal terminal = value as Terminal
    setText( terminal.getDescription() )
    return this
  }
}
