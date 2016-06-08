package mx.lux.pos.ui.view.dialog

import mx.lux.pos.ui.MainWindow
import mx.lux.pos.ui.model.Customer
import mx.lux.pos.ui.view.panel.EnvioPanel

import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import javax.swing.*;

import mx.lux.pos.java.repository.JbJava;
import mx.lux.pos.model.Jb;
import mx.lux.pos.java.querys.JbQuery;
import mx.lux.pos.java.querys.JbTrackQuery;
import mx.lux.pos.java.querys.ParametrosQuery;
import mx.lux.pos.java.repository.JbTrack;
import mx.lux.pos.java.repository.Parametros;
import mx.lux.pos.ui.controller.CustomerController;
import mx.lux.pos.ui.controller.ItemController;
import mx.lux.pos.ui.controller.OrderController;
import mx.lux.pos.ui.model.Rx;
import mx.lux.pos.ui.model.Session;
import mx.lux.pos.ui.model.SessionItem;
import mx.lux.pos.ui.model.User;
import mx.lux.pos.ui.resources.utilidades.Constantes;
import mx.lux.pos.ui.resources.utilidades.SolicitaUrl;
import mx.lux.pos.ui.view.panel.ConsultaPanel;
import org.apache.commons.lang3.StringUtils;

import static mx.lux.pos.java.querys.JbQuery.getMaterialJbRx;
import static mx.lux.pos.java.querys.JbQuery.getRotoJbRx;
import static mx.lux.pos.java.querys.JbQuery.updateEstadoJbRx;
import static org.jfree.util.Log.debug;
import static org.jfree.util.Log.info;
import static org.jfree.util.Log.log;


public class PopUpMenu extends JFrame implements TableModelListener {

	private JPopupMenu pMenu;
    private JMenuItem itemRxData;
	private JMenuItem itemConsultaTrabajo;
	private JMenuItem itemInfoPino;
    private JMenuItem itemCustomerData;
    private JMenuItem itemReschedule;
    private JMenuItem itemRetener;
    private JMenuItem itemStopCall;
    private JMenuItem itemNotSend;
    private JMenuItem itemSend;
    private JMenuItem itemNewReplacement;
    private JMenuItem itemDesretener;
    private EnvioPanel sendPanel;

    private static final String TAG_PANEL_CONSULTA = "consulta"
    private static final String TAG_PANEL_ENVIO = "envio"
    private static final String TAG_PANEL_RECEPCION = "recepcion"
    private static final String TAG_PANEL_CONTACTOS = "contactos"
    private static final String TAG_PANEL_REPOSICION = "reposicion"
	
	public PopUpMenu( Component component, Integer x, Integer y, final String rx, String panel, JPanel jPanel ){
	    pMenu = new JPopupMenu();
        itemNewReplacement = new JMenuItem("Nueva Reposicion");
        itemConsultaTrabajo = new JMenuItem("Consultar Trabajo");
        itemRxData = new JMenuItem("Datos Receta");
        itemCustomerData = new JMenuItem("Datos Cliente");
        itemReschedule = new JMenuItem("Reprogramar");
        itemStopCall = new JMenuItem("No Contactar");
        itemNotSend = new JMenuItem("No Enviar");
        itemSend = new JMenuItem("Enviar");
        //itemInfoPino = new JMenuItem("Info Laboratorio");
        //itemRetener = new JMenuItem("Retener");
        //itemDesretener = new JMenuItem("Desretener");
        pMenu.add(itemNewReplacement)
        pMenu.add(itemRxData);
        pMenu.add(itemConsultaTrabajo);
        pMenu.add(itemCustomerData);
        pMenu.add(itemReschedule);
        pMenu.add(itemStopCall);
        pMenu.add(itemNotSend);
        pMenu.add(itemSend);

        //pMenu.add(itemInfoPino);
        //pMenu.add( itemRetener );
        //pMenu.add(itemDesretener);
        itemNewReplacement.visible = false
        String[] data = StringUtils.trimToEmpty(panel).split(",")
        if( StringUtils.trimToEmpty(data[0]).equalsIgnoreCase(TAG_PANEL_CONSULTA) ){
          itemNotSend.visible = false
          itemSend.visible = false
        } else if( StringUtils.trimToEmpty(data[0]).equalsIgnoreCase(TAG_PANEL_ENVIO) ){
          this.sendPanel = jPanel as EnvioPanel
          if( StringUtils.trimToEmpty(data[1]).equalsIgnoreCase("send") ){
            itemSend.visible = false
          } else if( StringUtils.trimToEmpty(data[1]).equalsIgnoreCase("notsend") ){
            itemNotSend.visible = false
          }
          itemReschedule.visible = false
          itemStopCall.visible = false
        } else if( StringUtils.trimToEmpty(data[0]).equalsIgnoreCase(TAG_PANEL_RECEPCION) ){
          itemNotSend.visible = false
          itemSend.visible = false
        } else if( StringUtils.trimToEmpty(data[0]).equalsIgnoreCase(TAG_PANEL_CONTACTOS) ){
          itemNotSend.visible = false
          itemSend.visible = false
        } else if( StringUtils.trimToEmpty(data[0]).equalsIgnoreCase(TAG_PANEL_REPOSICION) ){
          itemNewReplacement.visible = true
        }
	    pMenu.show(component, x, y);
        //pMenu.setLocation(x,y);

        habilitaOpciones(rx);

	    itemRxData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                Rx prescription = OrderController.findRxByBill( rx )
                if( prescription != null && prescription.id != null ){
                  ConsultRxDialog rxConsulta = new ConsultRxDialog(prescription, false);
                  rxConsulta.show()
                }
			}
		});

        itemConsultaTrabajo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConsultaTrabajoDialog consulta = new ConsultaTrabajoDialog( MainWindow.instance, rx );
                consulta.show();
            }
        });

	    /*itemInfoPino.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

                Parametros idSucursal = ParametrosQuery.BuscaParametroPorId(Constantes.TAG_ID_SUCURSAL);
				Parametros urlInfoPino = ParametrosQuery.BuscaParametroPorId(Constantes.TAG_URL_INFO_PINO);

                if ( urlInfoPino.getValor() == null || urlInfoPino.getValor().equals("") ) {
                    System.err.println("Error, no se encontro pagina para INFO PINO");
                    return;
                }

				String url = String.format("%s?arg=%s|%s", StringUtils.trimToEmpty(urlInfoPino.getValor()),StringUtils.trimToEmpty(rx),
						StringUtils.trimToEmpty(idSucursal.getValor()));

				List<String> lstDatos = SolicitaUrl.getReponseFromURL(url);

				InfoLaboratorioDialog dialogo = new InfoLaboratorioDialog(lstDatos, rx);
			}
		});*/


        itemCustomerData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer customer = CustomerController.findCustomerByBill( rx )
                if( customer != null && customer.id != null ){
                  ConsultCustomerDialog dialog = new ConsultCustomerDialog( component, customer, false )
                  dialog.show()
                }
            }
        });


        itemReschedule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              RescheduleDialog dialog = new RescheduleDialog( rx )
              dialog.show()
            }
        });


        itemStopCall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              StopContactDialog dialog = new StopContactDialog( rx )
              dialog.show()
            }
        });


        itemNotSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              NotSendDialog dialog = new NotSendDialog( component, rx )
              dialog.show()
              sendPanel.updateData()
              sendPanel.doBindings()
            }
        });

        itemSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              OrderController.send( rx, "" )
              sendPanel.updateData()
              sendPanel.doBindings()
            }
        });

        itemNewReplacement.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              NewReplacementDialog dialog = new NewReplacementDialog()
              dialog.show()
            }
        });

        /*itemRetener.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RetenidosDialog d = new RetenidosDialog(rx);
            }
        });

        itemDesretener.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int confirm = JOptionPane.showConfirmDialog (null, "¿Desea enviar el trabajo?","Warning", JOptionPane.YES_NO_OPTION);

                if ( confirm == 0 ) {
                    desretenerJb(rx);
                    ConsultaPanel.buscar();
                }
            }
        });*/
	}

    private JMenuItem getItemRetener(String rx) {

        if ( itemRetener == null ) {
            itemRetener = new JMenuItem("Retener");

            itemRetener.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    String url = rx;
//                    InfoLaboratorioDialog dialogo = new InfoLaboratorioDialog(lstDatos, rx);
                }
            });
        }

        return itemRetener;
    }

    private void desretenerJb (String rx) {

        String material = getMaterialJbRx(rx);
        String roto = getRotoJbRx(rx);
        String emp = ( (User) Session.get(SessionItem.USER) ).getUsername();
        String tipo = "PE";

        if ( roto != null ) {
            if (!roto.equals("")) {
                tipo = "RPE";
            }
        }

        updateEstadoJbRx(rx, tipo);

        JbTrack jbTrack = new JbTrack();

        jbTrack.setEmp(emp);
        jbTrack.setEstado(tipo);
        jbTrack.setObs(material);
        jbTrack.setRx(rx);
        jbTrack.setIdModM("0");
        jbTrack.setIdViaje("");

        JbTrackQuery.insertJbTrack(jbTrack);
    }

    private void habilitaOpciones (String rx) {

        if ( rx == null )
            return;

        JbJava jb = JbQuery.buscarPorRx(rx);

        if ( jb == null )
            return;

        /*itemRetener.setEnabled(false);
        itemDesretener.setEnabled(false);

        if ( ! (jb.getEstado().equals("TE") || jb.getEstado().equals("CN") || jb.getEstado().equals("RTN")) ) {
            itemRetener.setEnabled(true);
        }

        if ( jb.getEstado().equals("RTN") ) {
            itemDesretener.setEnabled(true);
        }*/
    }

    @Override
    void tableChanged(TableModelEvent e) {
      sendPanel.noSendModel.fireTableDataChanged()
      sendPanel.bySendModel.fireTableDataChanged()
    }
}
