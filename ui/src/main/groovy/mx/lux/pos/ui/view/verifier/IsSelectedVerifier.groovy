package mx.lux.pos.ui.view.verifier

import javax.swing.*
import java.awt.*

class IsSelectedVerifier extends InputVerifier {

  @Override
  boolean verify( JComponent component ) {
    if ( component instanceof JComboBox ) {
      JComboBox cb = component
      if ( cb?.selectedIndex < 0 ) {
        component.border = BorderFactory.createLineBorder( Color.RED )
        return false
      } else {
        component.border = UIManager.getBorder( 'ComboBox.border' )
      }
    }
    return true
  }
}
