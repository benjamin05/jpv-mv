package mx.lux.pos.ui.view.dialog;

import mx.lux.pos.querys.JbQuery;
import mx.lux.pos.repository.JbTrack;
import mx.lux.pos.querys.JbTrackQuery;
import mx.lux.pos.ui.model.Branch;
import mx.lux.pos.ui.model.Session;
import mx.lux.pos.ui.model.SessionItem;
import mx.lux.pos.ui.model.User;
import mx.lux.pos.ui.view.panel.ConsultaPanel;
import org.dyno.visual.swing.layouts.*;
import org.dyno.visual.swing.layouts.GroupLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by magno on 8/09/14.
 */
public class RetenidosDialog extends JDialog {

    private JPanel jPanel0;
    private JLabel jLabelRazon;
    private JTextField jTextFieldRazon;
    private JButton jButtonCerrar;
    private JButton jButtonGuardar;
    private String rx;

    public RetenidosDialog(String rx) {
//        super(parent);
        this.rx = rx;
        this.setDefaultCloseOperation(RetenidosDialog.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.getContentPane().setPreferredSize(this.getSize());
        this.pack();
        initComponents();
        this.setVisible(true);
    }

    private void initComponents() {
        setTitle("Rx: " + rx);
        setFont(new Font("Dialog", Font.PLAIN, 12));
        setBackground(new Color(223, 223, 223));
        setForeground(Color.gray);
        setLayout(new org.dyno.visual.swing.layouts.GroupLayout());
        add(getJPanel0(), new Constraints(new Leading(10, 440, 10, 10), new Leading(10, 60, 10, 10)));
        add(getButtonCerrar(), new Constraints(new Leading(300, 12, 12), new Leading(80, 12, 12)));
        add(getButtonGuardar(), new Constraints(new Leading(380, 12, 12), new Leading(80, 12, 12)));
        setSize(470, 160);

//        this.setVisible(true);
    }

    private JPanel getJPanel0() {

        if (jPanel0 == null) {
            jPanel0 = new JPanel();
            jPanel0.setBorder(new LineBorder(Color.black, 1, false));
            jPanel0.setLayout(new GroupLayout());
            jPanel0.add(getLabelRazon(), new Constraints(new Leading(12, 12, 12), new Leading(12, 10, 10)));
            jPanel0.add(getTextRazon(), new Constraints(new Leading(80, 350, 250, 12), new Leading(12, 30, 12, 12)));
        }

        return jPanel0;
    }

    private JLabel getLabelRazon() {
        if ( jLabelRazon == null ) {
            jLabelRazon = new JLabel();
            jLabelRazon.setText("Razon: ");
        }

        return jLabelRazon;
    }

    private JTextField getTextRazon() {
        if ( jTextFieldRazon == null ) {
            jTextFieldRazon = new JTextField();
            jTextFieldRazon.setText("");
        }

        return jTextFieldRazon;
    }

    private JButton getButtonGuardar() {
        if ( jButtonGuardar == null ) {

            jButtonGuardar = new JButton();
            jButtonGuardar.setText("Guardar");
            jButtonGuardar.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {

                    User user = (User)Session.get( SessionItem.USER );

                    String razon;
                    razon = jTextFieldRazon.getText().toUpperCase();
                    razon = razon.trim();

                    JbTrack jbTrack = new JbTrack();

                    jbTrack.setEmp(((User) Session.get(SessionItem.USER)).getUsername());
                    jbTrack.setEstado("RTN");
                    jbTrack.setObs(razon);
                    jbTrack.setRx(rx);
                    jbTrack.setIdModM("0");
                    jbTrack.setIdViaje("");

                    if ( razon.equals("SA") ) {

                    }

                    JbTrackQuery.insertJbTrack(jbTrack);
                    JbQuery.updateEstadoJbRx(rx, "RTN");

                    dispose();

                    ConsultaPanel.buscar();
                }
            });
        }

        return jButtonGuardar;
    }

    private JButton getButtonCerrar() {
        if ( jButtonCerrar == null ) {
            jButtonCerrar = new JButton();
            jButtonCerrar.setText("Cerrar");
            jButtonCerrar.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    dispose();
                }
            });
        }

        return jButtonCerrar;
    }
}
