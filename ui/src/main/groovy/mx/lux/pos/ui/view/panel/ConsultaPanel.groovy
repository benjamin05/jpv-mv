package mx.lux.pos.ui.view.panel

import mx.lux.pos.ui.MainWindow;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import mx.lux.pos.java.querys.JbQuery;
import mx.lux.pos.java.repository.JbEstadosGrupo;
import mx.lux.pos.java.repository.JbJava;
import org.apache.commons.lang3.StringUtils;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import mx.lux.pos.ui.view.dialog.*;

//VS4E -- DO NOT REMOVE THIS LINE!
public class ConsultaPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel0;
	private JPanel busquedaPanel;
	private JTextField txtRx;
	private JLabel jLabel1;
	private JTextField txtCliente;
	private JLabel jLabel2;
	private JComboBox cbEstados;
	private JLabel jLabel3;
	private JTextField txtAtendio;
	private JPanel saldoPanel;
	private JPanel jPanel1;
	private JLabel lblSaldo;
	private static JButton btnBuscar;
	private static JButton btnLimpiar;
	private JTable tblBusqueda;
	private JScrollPane jScrollPane0;
	final String[] columnNames = [ "Rx", "Cliente", "Estado", "Fecha", "Atendió", "Promesa" ];
	String[] rowData = [ ];
	private Vector cabecerasArr = null;
	private DefaultTableModel modelo = null;
	List<JbJava> lstJbs = new ArrayList<JbJava>();
	private Component component;
	private static Font fontSaldo = new Font("Verdana", Font.BOLD, 34);
	
	private static final String TAG_DESCRIPCION_SUCURSAL = "SUCURSAL";
	private static final String TAG_RECIBE_SUCURSAL = "RECIBE SUC";
	
	private static Font font = new Font("Verdana", Font.BOLD, 14);
	
	public ConsultaPanel() throws IOException {
		initComponents();
	}

	private void initComponents() {		
		setLayout(new GroupLayout());
        add(getJPanel0(), new Constraints(new Leading(40, 350, 10, 10), new Leading(15, 180, 10, 10)));
        add(getJPanel1(), new Constraints(new Leading(430, 350, 10, 10), new Leading(9, 170, 10, 10)));
        add(getJScrollPane0(), new Constraints(new Leading(40, 730, 10, 10), new Leading(250, 260, 10, 10)));
		setSize(855, 620);
		component = this;
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getTblBusqueda());			
		}
		return jScrollPane0;
	}

	private JTable getTblBusqueda() {
		if (tblBusqueda == null) {
			//tblBusqueda = new JTable(rowData, columnNames);
            tblBusqueda = new JTable();
			modelo = new DefaultTableModel(){
			    @Override
			    public boolean isCellEditable(int row, int column) {
			       return false;
			    }
			};
			modelo.addColumn("Rx");
			modelo.addColumn("Cliente");
			modelo.addColumn("Estado");
			modelo.addColumn("Fecha");
			modelo.addColumn("Atendió");
			modelo.addColumn("Promesa");
			JScrollPane scrollPane = new JScrollPane(tblBusqueda);		    
		    tblBusqueda.setModel(modelo);
		    tblBusqueda.getColumnModel().getColumn(0).setMaxWidth(60);
		    tblBusqueda.getColumnModel().getColumn(1).setMaxWidth(250);
		    tblBusqueda.getColumnModel().getColumn(2).setMaxWidth(120);
		    tblBusqueda.getColumnModel().getColumn(3).setMaxWidth(90);
		    tblBusqueda.getColumnModel().getColumn(4).setMaxWidth(70);
		    tblBusqueda.getColumnModel().getColumn(5).setMaxWidth(110);
		    tblBusqueda.setCellSelectionEnabled(true);
		    tblBusqueda.setRowSelectionAllowed(true);
		    tblBusqueda.setColumnSelectionAllowed(false);
		    tblBusqueda.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    tblBusqueda.setColumnSelectionInterval(0, tblBusqueda.getColumnCount()-1);
		    tblBusqueda.addMouseListener(new MouseAdapter() {
		    	public void mousePressed(MouseEvent me) {
		            if (me.getClickCount() == 2 && tblBusqueda.getSelectedRow() >= 0) {
		            	Integer selectedRow = tblBusqueda.getSelectedRow();
		            	String rxSelected = "";
				        if( tblBusqueda.getRowCount() > 0 ){
				        	rxSelected = (String) tblBusqueda.getValueAt(selectedRow, 0);			            					        
				        }
		            	new ConsultaTrabajoDialog( MainWindow.instance, rxSelected );
		            }
		        }
		    	public void mouseReleased(MouseEvent me){
		    		if(me.getButton() == MouseEvent.BUTTON3 && tblBusqueda.getSelectedRow() >= 0){
                        Integer selectedRow = tblBusqueda.getSelectedRow();
                        String selectedData = (String) tblBusqueda.getValueAt(selectedRow, 0);

                        PopUpMenu menu = new PopUpMenu( me.getComponent(), me.getX(), me.getY(), selectedData, "consulta", new JPanel() );

		            }
		          }

			});		    
		    tblBusqueda.getSelectionModel().addListSelectionListener(new ListSelectionListener() {				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					String selectedData = null;			        
			        if( tblBusqueda.getRowCount() > 0 && tblBusqueda.getSelectedRow() >= 0){
			        	Integer selectedRow = tblBusqueda.getSelectedRow();
			        	selectedData = (String) tblBusqueda.getValueAt(selectedRow, 0);			            
				        String saldo = String.format("%s", NumberFormat.getCurrencyInstance(Locale.US).format(JbQuery.buscarPorRx(StringUtils.trimToEmpty(selectedData)).getSaldo()));
				        lblSaldo.setText(saldo.length() <= 0 ? "" : saldo);
			        }			        
			      }									
			});
		}
		return tblBusqueda;
	}

	private JButton getBtnBuscar() {
		if (btnBuscar == null) {
			btnBuscar = new JButton();
            //ImageIcon imageIcon = new ImageIcon(getClass().getResource("img/search.png"));
            ImageIcon imageIcon = new ImageIcon("img/search.png");
			btnBuscar.setIcon( imageIcon );
			btnBuscar.setText("Buscar");
			btnBuscar.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					try {
//						btnBuscarActionActionPerformed(event);
                        btnBuscarActionActionPerformed(event);
					} catch (ParseException e) { e.printStackTrace(); }
				}
			});
		}
		return btnBuscar;
	}

	private JButton getBtnLimpiar() {
		if (btnLimpiar == null) {
			btnLimpiar = new JButton();
            //new ImageIcon(getClass().getResource("img/clear.png"))
			btnLimpiar.setIcon(new ImageIcon("img/clear.png"));
			btnLimpiar.setText("Limpiar");
			btnLimpiar.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					btnLimpiarActionActionPerformed(event);
				}
			});
		}
		return btnLimpiar;
	}

	private JLabel getLblSaldo() {
		if (lblSaldo == null) {
			lblSaldo = new JLabel();
			lblSaldo.setText("");
			lblSaldo.setFont(fontSaldo);
		}
		return lblSaldo;
	}

	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GroupLayout());
			jPanel1.add(getSaldoPanel(), new Constraints(new Leading(12, 300, 10, 10), new Leading(5, 89, 10, 10)));
			jPanel1.add(getBtnBuscar(), new Constraints(new Leading(80, 110, 10, 10), new Leading(126, 12, 12)));
			jPanel1.add(getBtnLimpiar(), new Constraints(new Leading(200, 110, 10, 10), new Leading(126, 12, 12)));
		}
		return jPanel1;
	}

	private JPanel getSaldoPanel() {
		if (saldoPanel == null) {
			saldoPanel = new JPanel();
			saldoPanel.setBorder(BorderFactory.createTitledBorder(null, "Saldo", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, null));
			saldoPanel.setLayout(new GroupLayout());
			saldoPanel.add(getLblSaldo(), new Constraints(new Leading(18, 255, 10, 10), new Bilateral(6, 12, 15)));
		}
		return saldoPanel;
	}

	private JTextField getTxtAtendio() {
		if (txtAtendio == null) {
			txtAtendio = new JTextField();
			txtAtendio.setFont(font);
			txtAtendio.setText("");
		}
		return txtAtendio;
	}

	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("Atendió:");
		}
		return jLabel3;
	}

	private JComboBox getCbEstados() {
		if (cbEstados == null) {
			cbEstados = new JComboBox();
			List<JbEstadosGrupo> lstEstados = new ArrayList<>();
            lstEstados.add(new JbEstadosGrupo())
            lstEstados.addAll(JbQuery.listaJbEstadosGrupo());
			List<String> lstDescripcion = new ArrayList<String>();
			//lstEstados.set(0, new JbEstadosGrupo());
			for(JbEstadosGrupo edos : lstEstados){
				lstDescripcion.add(edos.getDescripcionGrupo());
			}
			String[] estados = new String[lstEstados.size()];
			estados = lstDescripcion.toArray(estados);
			cbEstados.setModel(new DefaultComboBoxModel(estados));			
			cbEstados.setDoubleBuffered(false);
			cbEstados.setBorder(null);
			cbEstados.setFont(font);
		}
		return cbEstados;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Estado:");
		}
		return jLabel2;
	}

	private JTextField getTxtCliente() {
		if (txtCliente == null) {
			txtCliente = new JTextField();
			txtCliente.setFont(new Font("Verdana", Font.BOLD, 14));			
		}
		return txtCliente;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Cliente:");
		}
		return jLabel1;
	}

	private JTextField getTxtRx() {
		if (txtRx == null) {
			txtRx = new JTextField();
			txtRx.setText("");
			txtRx.setFont(font);
		}
		return txtRx;
	}

	private JPanel getJPanel0() {
		if (busquedaPanel == null) {
			busquedaPanel = new JPanel();
			busquedaPanel.setBorder(BorderFactory.createTitledBorder(null, "Búsqueda", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, null));
			busquedaPanel.setLayout(new GroupLayout());
			busquedaPanel.add(getJLabel0(), new Constraints(new Leading(5, 10, 10), new Leading(15, 10, 10)));
			busquedaPanel.add(getTxtRx(), new Constraints(new Leading(81, 85, 10, 10), new Leading(10, 25, 12, 12)));
			busquedaPanel.add(getJLabel1(), new Constraints(new Leading(5, 12, 12), new Leading(50, 12, 12)));
			busquedaPanel.add(getTxtCliente(), new Constraints(new Leading(81, 210, 12, 12), new Leading(45, 25, 12, 12)));
			busquedaPanel.add(getJLabel2(), new Constraints(new Leading(5, 12, 12), new Leading(87, 12, 12)));
			busquedaPanel.add(getCbEstados(), new Constraints(new Leading(81, 210, 12, 12), new Leading(80, 25, 12, 12)));
			busquedaPanel.add(getJLabel3(), new Constraints(new Leading(5, 12, 12), new Leading(120, 97, 97)));
			busquedaPanel.add(getTxtAtendio(), new Constraints(new Leading(81, 210, 12, 12), new Leading(115, 25, 96, 96)));
		}
		return busquedaPanel;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Rx:");
		}
		return jLabel0;
	}

	private void btnBuscarActionActionPerformed(ActionEvent event) throws ParseException {
		limpiarTabla();
        tblBusqueda.repaint();
		String rx = StringUtils.trimToEmpty(txtRx.getText());
		String cliente = StringUtils.trimToEmpty(txtCliente.getText());
		String estado = StringUtils.trimToEmpty(cbEstados.getSelectedItem() != null ? cbEstados.getSelectedItem().toString() : "");
		String atendio = StringUtils.trimToEmpty(txtAtendio.getText());
		if(StringUtils.trimToEmpty(estado).equalsIgnoreCase(TAG_DESCRIPCION_SUCURSAL)){			
			estado = TAG_RECIBE_SUCURSAL;
		}
		lstJbs = JbQuery.busquedaJb(rx, cliente, estado, atendio);
		Integer cont = 0;				
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		for(JbJava jb : lstJbs){
			rowData = new String[cont][5];
			modelo.addRow(rowData);
			tblBusqueda.setValueAt(jb.getRx(), cont, 0);
			tblBusqueda.setValueAt(jb.getCliente(), cont, 1);
			tblBusqueda.setValueAt(jb.getEstado(), cont, 2);
			tblBusqueda.setValueAt(jb.getFechaVenta() != null ? df.format(jb.getFechaVenta()) : "", cont, 3);
			tblBusqueda.setValueAt(jb.getEmpAtendio(), cont, 4);
			tblBusqueda.setValueAt(jb.getFechaPromesa() != null ? df.format(jb.getFechaPromesa()) : "", cont, 5);
			cont = cont+1;
		}
		modelo.fireTableDataChanged();
	}

	private void btnLimpiarActionActionPerformed(ActionEvent event) {
		limpiaPantalla();
	}

    public void limpiaPantalla(){
        limpiarTabla();
        txtAtendio.setText("");
        txtCliente.setText("");
        txtRx.setText("");
        cbEstados.setSelectedIndex(0);
        lblSaldo.setText(" ");
    }
	
	private void limpiarTabla(){
		while(modelo.getRowCount() > 0)
		{
		    modelo.removeRow(0);
		}		
	}

    public static void buscar() {
        btnBuscar.doClick();
    }
}
