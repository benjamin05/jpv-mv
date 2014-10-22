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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import mx.lux.pos.querys.JbQuery;
import mx.lux.pos.repository.JbJava;

//VS4E -- DO NOT REMOVE THIS LINE!
public class InfoLaboratorioDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jPanel0;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JTextField txtRx;
	private JTextArea txtSeguimiento;
	private JTextField txtUltimaEstacion;
	private JTextField txtFechaEstacion;
	private List<String> lstDatos;
	private String rx;
	private JTable tblDatos;
	String rowData[][] = { };
	private DefaultTableModel modelo = null;
	private JScrollPane jScrollPane0;
    private JScrollPane JScrollPane1;
    private JButton btnCerrar;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public static String TIPO_SEGUIMIENTO = "S";
	public static String TIPO_ESTACION = "E";

	public InfoLaboratorioDialog(Dialog parent) {
		super(parent);
		initComponents();
	}

	public InfoLaboratorioDialog(Dialog parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	public InfoLaboratorioDialog(Frame parent, String title, boolean modal) {
		super(parent, title, modal);
		initComponents();
	}

	public InfoLaboratorioDialog(Frame parent, String title, boolean modal,
			GraphicsConfiguration arg) {
		super(parent, title, modal, arg);
		initComponents();
	}

	public InfoLaboratorioDialog(Frame parent) {
		super(parent);
		initComponents();
	}

	public InfoLaboratorioDialog(Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	public InfoLaboratorioDialog(Frame parent, String title) {
		super(parent, title);
		initComponents();
	}

	public InfoLaboratorioDialog( List<String> lstDatos, String rx ) {		
		this.lstDatos = lstDatos;
		this.rx = rx;
		initComponents();
		this.setDefaultCloseOperation(InfoLaboratorioDialog.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.getContentPane().setPreferredSize(this.getSize());
		//this.pack();
        this.setLocation(220, 80);
		cargarDatos();
		this.setVisible(true);		
	}

	public InfoLaboratorioDialog(Window parent, ModalityType modalityType) {
		super(parent, modalityType);
		initComponents();
	}

	public InfoLaboratorioDialog(Window parent, String title) {
		super(parent, title);
		initComponents();
	}

	public InfoLaboratorioDialog(Window parent, String title,
			ModalityType modalityType) {
		super(parent, title, modalityType);
		initComponents();
	}

	public InfoLaboratorioDialog(Window parent, String title,
			ModalityType modalityType, GraphicsConfiguration arg) {
		super(parent, title, modalityType, arg);
		initComponents();
	}

	public InfoLaboratorioDialog(Dialog parent, String title) {
		super(parent, title);
		initComponents();
	}

	public InfoLaboratorioDialog(Dialog parent, String title, boolean modal) {
		super(parent, title, modal);
		initComponents();
	}

	public InfoLaboratorioDialog(Dialog parent, String title, boolean modal,
			GraphicsConfiguration arg) {
		super(parent, title, modal, arg);
		initComponents();
	}

	public InfoLaboratorioDialog(Window parent) {
		super(parent);
		initComponents();
	}

	private void initComponents() {
		setTitle("Rx: "+rx);
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setBackground(new Color(223, 223, 223));
		setForeground(Color.gray);
		setLayout(new GroupLayout());
		add(getJPanel0(), new Constraints(new Leading(10, 440, 10, 10), new Leading(10, 220, 10, 10)));
		add(getJScrollPane0(), new Constraints(new Leading(25, 402, 10, 10), new Leading(240, 180, 10, 10)));
		add(getBtnCerrar(), new Constraints(new Leading(210, 12, 12), new Leading(425, 12, 12)));
		setSize(470, 500);
	}

	private JButton getBtnCerrar() {
		if (btnCerrar == null) {
			btnCerrar = new JButton();
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
		if (tblDatos == null) {
			tblDatos = new JTable();					
			modelo = new DefaultTableModel(){
			    @Override
			    public boolean isCellEditable(int row, int column) {
			       return false;
			    }
			};
			modelo.addColumn("Hora Estación");
			modelo.addColumn("Fecha Estación");
			modelo.addColumn("Estación");
			JScrollPane scrollPane = new JScrollPane(tblDatos);		    
		    tblDatos.setModel(modelo);
		    tblDatos.getColumnModel().getColumn(0).setMaxWidth(110);
		    tblDatos.getColumnModel().getColumn(1).setMaxWidth(140);
		    tblDatos.getColumnModel().getColumn(2).setMaxWidth(150);
		    tblDatos.setCellSelectionEnabled(true);
		    tblDatos.setRowSelectionAllowed(true);
		    tblDatos.setColumnSelectionAllowed(false);
		}
		return tblDatos;
	}

	private JTextField getTxtFechaEstacion() {
		if (txtFechaEstacion == null) {
			txtFechaEstacion = new JTextField();
            txtFechaEstacion.setEditable(false);
			txtFechaEstacion.setText("");
		}
		return txtFechaEstacion;
	}

	private JTextField getTxtUltimaEstacion() {
		if (txtUltimaEstacion == null) {
			txtUltimaEstacion = new JTextField();
            txtUltimaEstacion.setEditable(false);
			txtUltimaEstacion.setText("");
		}
		return txtUltimaEstacion;
	}

	private JScrollPane getTxtSeguimiento() {
		if (txtSeguimiento == null) {

            txtSeguimiento = new JTextArea();
            txtSeguimiento.setBorder(BorderFactory.createLineBorder(Color.gray));
            txtSeguimiento.setEditable(false);
            txtSeguimiento.setLineWrap(true);

            JScrollPane1 = new JScrollPane(txtSeguimiento,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            JScrollBar scrollBar = new JScrollBar();
            JScrollPane1.add(scrollBar);
            txtSeguimiento.setText("");
		}
		return JScrollPane1;
	}

	private JTextField getTxtRx() {
		if (txtRx == null) {
			txtRx = new JTextField();
            txtRx.setEditable(false);
            txtRx.setHorizontalAlignment(JTextField.CENTER);
			txtRx.setText("");
		}
		return txtRx;
	}

	private JLabel getJLabelFechaUltima() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("Fecha Ult. Estación:");
		}
		return jLabel3;
	}

	private JLabel getJLabelEstacion() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Ultima Estación:");
		}
		return jLabel2;
	}

	private JLabel getJLabelSeguimiento() {
		if (jLabel1 == null) {

			jLabel1 = new JLabel();
			jLabel1.setText("Seguimiento:");
		}
		return jLabel1;
	}

	private JLabel getJLabelRx() {
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
//			jPanel0.add(getJLabelRx(), new Constraints(new Leading(82, 12, 12), new Leading(12, 12, 12)));
			jPanel0.add(getJLabelSeguimiento(), new Constraints(new Leading(12, 12, 12), new Leading(12, 10, 10)));
			jPanel0.add(getJLabelEstacion(), new Constraints(new Leading(38, 12, 12), new Leading(140, 12, 12)));
			jPanel0.add(getJLabelFechaUltima(), new Constraints(new Leading(12, 12, 12), new Leading(180, 10, 10)));
//			jPanel0.add(getTxtRx(), new Constraints(new Leading(173, 90, 10, 10), new Leading(10, 12, 12)));
			jPanel0.add(getTxtSeguimiento(), new Constraints(new Leading(10, 410, 250, 12), new Leading(35, 100, 12, 12)));
			jPanel0.add(getTxtUltimaEstacion(), new Constraints(new Leading(173, 210, 12, 12), new Leading(140, 12, 12)));
			jPanel0.add(getTxtFechaEstacion(), new Constraints(new Leading(173, 210, 12, 12), new Leading(180, 12, 12)));
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

	public InfoLaboratorioDialog() {
		initComponents();
	}

	private void btnCerrarActionActionPerformed(ActionEvent event) {
		dispose();
	}	

	private void cargarDatos(){
		limpiarTabla();

		Integer cont = 0;
		String ultimaEstacion = "";
		String fechaUltimaEstacion = "";
        String seguimiento = "";
        String contenido = "";
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String[] detalle;


        for ( String datos : lstDatos ) {
            if ( ! datos.equals("?") )
                contenido += datos + "~";
        }

        detalle = contenido.split("\\|");

        for ( int i = 0; i < detalle.length; i++ ) {

            if ( detalle[i].equals("S") ){
                seguimiento = detalle[i+1];
            }

            if ( detalle[i].equals("E") || detalle[i].equals("~E") ){

                rowData = new String[cont][2];
                modelo.addRow(rowData);
                tblDatos.setValueAt(detalle[i+1], cont, 0);
                tblDatos.setValueAt(detalle[i+2], cont, 1);
                tblDatos.setValueAt(detalle[i+3], cont, 2);

                fechaUltimaEstacion = detalle[i+2];
                ultimaEstacion = detalle[i+3];

                cont = cont+1;
            }
        }

        seguimiento = seguimiento.replaceAll("~", "\n");

        txtSeguimiento.setText(seguimiento);
		txtFechaEstacion.setText(fechaUltimaEstacion);
		txtUltimaEstacion.setText(ultimaEstacion);
		modelo.fireTableDataChanged();
	}
	
	
	private void limpiarTabla(){
		while(modelo.getRowCount() > 0)
		{
		    modelo.removeRow(0);
		}		
	}
	
}
