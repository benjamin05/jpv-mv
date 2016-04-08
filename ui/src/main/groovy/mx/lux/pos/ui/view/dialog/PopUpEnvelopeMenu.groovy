package mx.lux.pos.ui.view.dialog

import mx.lux.pos.java.querys.JbQuery
import mx.lux.pos.java.querys.JbTrackQuery
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.java.repository.JbSobres
import mx.lux.pos.java.repository.JbTrack
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.view.panel.EnvioPanel
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent

import static mx.lux.pos.java.querys.JbQuery.*

public class PopUpEnvelopeMenu extends JDialog {

	private JPopupMenu pMenu;
    private JMenuItem itemNewEnvelope;
	private JMenuItem itemDeleteEnvelope;

	public PopUpEnvelopeMenu( Component component, MouseEvent ev, JbSobres jbSobre ){
	  pMenu = new JPopupMenu();
      EnvelopesDialog envelopesDialog = component as EnvelopesDialog
      itemNewEnvelope = new JMenuItem("Nuevo Sobre");
      itemDeleteEnvelope = new JMenuItem("Eliminar Sobre");

      pMenu.add(itemNewEnvelope);
      pMenu.add(itemDeleteEnvelope);
      if( jbSobre == null ){
        itemDeleteEnvelope.enabled = false
      }
	  pMenu.show(ev.component, ev.getX(), ev.getY());

	  itemNewEnvelope.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
        EditEnvelopeDialog dialog = new EditEnvelopeDialog(null)
        dialog.show()
        envelopesDialog.refreshUI()
	  }
	  });

      itemDeleteEnvelope.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          OrderController.deleteEnvelope( jbSobre.id )
          envelopesDialog.refreshUI()
        }
      });
	}
}
