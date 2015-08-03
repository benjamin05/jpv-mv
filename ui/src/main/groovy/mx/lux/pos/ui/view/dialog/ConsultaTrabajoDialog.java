package mx.lux.pos.ui.view.dialog;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import mx.lux.pos.java.querys.EmpleadoQuery;
import mx.lux.pos.java.querys.JbQuery;
import mx.lux.pos.java.repository.JbTrack;
import mx.lux.pos.model.Jb;
import org.apache.commons.lang3.StringUtils;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;


//VS4E -- DO NOT REMOVE THIS LINE!
public class ConsultaTrabajoDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jPanel0;
	private JLabel jLabel0;
	private static String rx;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JTextField txtRx;
	private JTextField txtEstadoRx;
	private JTextField txtFechaEstado;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JLabel jLabel5;
	private JTextField txtCliente;
	private JTextField txtAtendio;
	private JTextField txtSaldo;
	private JTable tblDatosRx;
	private JScrollPane jScrollPane0;
	private DefaultTableModel modelo = null;
	final String columnNames[] = { "Rx", "Cliente", "Estado", "Fecha", "Atendió", "Promesa" };
	String rowData[][] = { };	
	private Vector cabecerasArr = null;
	private JButton btnCerrar;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	private static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	public ConsultaTrabajoDialog(Dialog parent) {
		super(parent);
		initComponents();		
	}

	public ConsultaTrabajoDialog(Dialog parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	public ConsultaTrabajoDialog(Frame parent, String title, boolean modal) {
		super(parent, title, modal);
		initComponents();
	}

	public ConsultaTrabajoDialog(Frame parent, String title, boolean modal,
			GraphicsConfiguration arg) {
		super(parent, title, modal, arg);
		initComponents();
	}

	public ConsultaTrabajoDialog(Frame parent) {
		super(parent);
		initComponents();
	}

	public ConsultaTrabajoDialog(Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	public ConsultaTrabajoDialog(Frame parent, String title) {
		super(parent, title);
		initComponents();
	}

	public ConsultaTrabajoDialog( String rx ) {		
		initComponents();
		this.rx = rx;
		this.setDefaultCloseOperation(ConsultaTrabajoDialog.DISPOSE_ON_CLOSE);		
		this.setTitle("Rx:");
		this.setLocation(70, 35);
		this.getContentPane().setPreferredSize(this.getSize());
		this.pack();
		setData();
		this.setVisible(true);
	}

	public ConsultaTrabajoDialog(Window parent, ModalityType modalityType) {
		super(parent, modalityType);
		initComponents();
	}

	public ConsultaTrabajoDialog(Window parent, String title) {
		super(parent, title);
		initComponents();
	}

	public ConsultaTrabajoDialog(Window parent, String title,
			ModalityType modalityType) {
		super(parent, title, modalityType);
		initComponents();
	}

	public ConsultaTrabajoDialog(Window parent, String title,
			ModalityType modalityType, GraphicsConfiguration arg) {
		super(parent, title, modalityType, arg);
		initComponents();
	}

	public ConsultaTrabajoDialog(Dialog parent, String title) {
		super(parent, title);
		initComponents();
	}

	public ConsultaTrabajoDialog(Dialog parent, String title, boolean modal) {
		super(parent, title, modal);
		initComponents();
	}

	public ConsultaTrabajoDialog(Dialog parent, String title, boolean modal,
			GraphicsConfiguration arg) {
		super(parent, title, modal, arg);
		initComponents();
	}

	public ConsultaTrabajoDialog(Window parent) {
		super(parent);
		initComponents();
	}

	private void initComponents() {
		setTitle("Rx");
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setBackground(new Color(223, 223, 223));
		setForeground(Color.black);
		setLayout(new GroupLayout());
		add(getJPanel0(), new Constraints(new Leading(10, 600, 12, 12), new Leading(12, 100, 12, 12)));
		add(getJScrollPane0(), new Constraints(new Leading(11, 600, 12, 12), new Leading(120, 200, 10, 10)));
		add(getBtnCerrar(), new Constraints(new Trailing(13, 12, 12), new Leading(329, 10, 10)));
		setSize(620, 380);
	}

	private JButton getBtnCerrar() {
		if (btnCerrar == null) {
			btnCerrar = new JButton();
			btnCerrar.setText("Cerrar");
            //new ImageIcon(getClass().getResource("img/close_icon.png"))
			btnCerrar.setIcon(new ImageIcon("img/close_icon.png"));
			btnCerrar.setText("Cerrar");
			btnCerrar.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					btnCerrarActionActionPerformed(event);
				}
			});
		}
		return btnCerrar;
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJTable0());
		}
		return jScrollPane0;
	}

	private JTable getJTable0() {
		if (tblDatosRx == null) {
			tblDatosRx = new JTable();
			tblDatosRx = new JTable(rowData, columnNames);					
			modelo = new DefaultTableModel(){
			    @Override
			    public boolean isCellEditable(int row, int column) {
			       return false;
			    }
			};
			modelo.addColumn("Fecha");
			modelo.addColumn("Hora");
			modelo.addColumn("Estado Rx");
			modelo.addColumn("Obs. Estado");
			modelo.addColumn("Empleado");
			JScrollPane scrollPane = new JScrollPane(tblDatosRx);		    
			tblDatosRx.setModel(modelo);
			tblDatosRx.getColumnModel().getColumn(0).setMaxWidth(70);
			tblDatosRx.getColumnModel().getColumn(1).setMaxWidth(50);
			tblDatosRx.getColumnModel().getColumn(2).setMaxWidth(140);
			tblDatosRx.getColumnModel().getColumn(3).setMaxWidth(150);
			tblDatosRx.getColumnModel().getColumn(4).setMaxWidth(188);			
			tblDatosRx.setCellSelectionEnabled(true);
			tblDatosRx.setRowSelectionAllowed(true);
			tblDatosRx.setColumnSelectionAllowed(false);
			tblDatosRx.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tblDatosRx.setColumnSelectionInterval(0, tblDatosRx.getColumnCount()-1);
		}
		return tblDatosRx;
	}

	private JTextField getTxtSaldo() {
		if (txtSaldo == null) {
			txtSaldo = new JTextField();
			txtSaldo.setText("");
			txtSaldo.setEnabled(false);
		}
		return txtSaldo;
	}

	private JTextField getTxtAtendio() {
		if (txtAtendio == null) {
			txtAtendio = new JTextField();
			txtAtendio.setText("");
			txtAtendio.setEnabled(false);
		}
		return txtAtendio;
	}

	private JTextField getTxtCliente() {
		if (txtCliente == null) {
			txtCliente = new JTextField();
			txtCliente.setText("");
			txtCliente.setEnabled(false);
		}
		return txtCliente;
	}

	private JLabel getJLabel5() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText("Saldo:");
		}
		return jLabel5;
	}

	private JLabel getJLabel4() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("Atendió:");
		}
		return jLabel4;
	}

	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("Cliente:");
		}
		return jLabel3;
	}

	private JTextField getTxtFechaEstado() {
		if (txtFechaEstado == null) {
			txtFechaEstado = new JTextField();
			txtFechaEstado.setText("");
			txtFechaEstado.setEnabled(false);
		}
		return txtFechaEstado;
	}

	private JTextField getTxtEstadoRx() {
		if (txtEstadoRx == null) {
			txtEstadoRx = new JTextField();
			txtEstadoRx.setText("");
			txtEstadoRx.setEnabled(false);
		}
		return txtEstadoRx;
	}

	private JTextField getTxtRx() {
		if (txtRx == null) {
			txtRx = new JTextField();
			txtRx.setText("");
			txtRx.setEnabled(false);
		}
		return txtRx;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Fecha Estado:");
		}
		return jLabel2;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Estado Rx:");
		}
		return jLabel1;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Rx:");
		}
		return jLabel0;
	}

	private JPanel getJPanel0() {
		if (jPanel0 == null) {
			jPanel0 = new JPanel();
			jPanel0.setBorder(new LineBorder(Color.black, 1, false));
			jPanel0.setLayout(new GroupLayout());
			jPanel0.add(getJLabel0(), new Constraints(new Leading(9, 10, 10), new Leading(12, 12, 12)));
			jPanel0.add(getJLabel1(), new Constraints(new Leading(7, 10, 10), new Leading(42, 12, 12)));
			jPanel0.add(getJLabel2(), new Constraints(new Leading(7, 12, 12), new Trailing(12, 66, 66)));
			jPanel0.add(getTxtRx(), new Constraints(new Leading(122, 120, 10, 10), new Leading(10, 20, 12, 12)));
			jPanel0.add(getTxtEstadoRx(), new Constraints(new Leading(122, 120, 10, 12), new Leading(40, 12, 12)));
			jPanel0.add(getTxtFechaEstado(), new Constraints(new Leading(122, 120, 10, 10), new Leading(69, 12, 12)));
			jPanel0.add(getJLabel3(), new Constraints(new Leading(270, 10, 10), new Leading(12, 12, 12)));
			jPanel0.add(getJLabel4(), new Constraints(new Leading(272, 12, 12), new Leading(42, 12, 12)));
			jPanel0.add(getJLabel5(), new Constraints(new Leading(272, 12, 12), new Trailing(14, 66, 66)));
			jPanel0.add(getTxtCliente(), new Constraints(new Leading(343, 245, 12, 12), new Leading(8, 12, 12)));
			jPanel0.add(getTxtAtendio(), new Constraints(new Leading(343, 245, 12, 12), new Leading(40, 12, 12)));
			jPanel0.add(getTxtSaldo(), new Constraints(new Leading(343, 245, 12, 12), new Trailing(12, 67, 67)));
		}
		return jPanel0;
	}

	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			if (lnfClassname == null)
				lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
					+ " on this platform:" + e.getMessage());
		}
	}

	public ConsultaTrabajoDialog() {
		initComponents();
	}	

	private void btnCerrarActionActionPerformed(ActionEvent event) {
		this.dispose();
	}
	
	private void setData( ){

        Jb jb = JbQuery.getJbRxSimple(rx);

        if ( jb != null ) {

            txtRx.setText(jb.getRx());
            txtEstadoRx.setText( JbQuery.buscarEstadoPorId( StringUtils.trimToEmpty(jb.getEstado()) ).getDescr() );

            if ( jb.getFecha_venta() != null )
                txtFechaEstado.setText(df.format(jb.getFecha_venta()));
            else
                txtFechaEstado.setText("");

            txtCliente.setText(jb.getCliente());
            txtAtendio.setText(jb.getEmp_atendio());
            txtSaldo.setText(jb.getSaldo() != null ? String.format("$%s", jb.getSaldo()) : "$0.00");

            modelo.setRowCount(0);
            List<JbTrack> lstJbs = JbQuery.buscarJbTrackPorRx(StringUtils.trimToEmpty(rx));
            Integer cont = 0;
            SimpleDateFormat dfDia = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dfHora = new SimpleDateFormat("HH:mm");

            for(JbTrack jbTrack : lstJbs){
                rowData = new String[cont][4];
                modelo.addRow(rowData);
                tblDatosRx.setValueAt(jbTrack.getFecha() != null ? dfDia.format(jbTrack.getFecha()) : "", cont, 0);
                tblDatosRx.setValueAt(jbTrack.getFecha() != null ? dfHora.format(jbTrack.getFecha()) : "", cont, 1);
                tblDatosRx.setValueAt(JbQuery.buscarEstadoPorId( StringUtils.trimToEmpty(jbTrack.getEstado()) ).getDescr(), cont, 2);
                tblDatosRx.setValueAt(jbTrack.getObs(), cont, 3);
                tblDatosRx.setValueAt(EmpleadoQuery.buscaEmpPorIdEmpleado(StringUtils.trimToEmpty(jbTrack.getEmp())).getNombreApellidos(), cont, 4);
                cont = cont+1;
            }

            modelo.fireTableDataChanged();
        }

        return;
	}
	
}
