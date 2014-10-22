package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.model.Receta
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Branch
import mx.lux.pos.ui.model.Rx
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import mx.lux.pos.ui.view.dialog.EditRxDialog
import mx.lux.pos.ui.view.dialog.NoSaleDialog
import mx.lux.pos.ui.view.renderer.DateCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils

import javax.swing.*
import java.awt.*
import java.awt.event.MouseEvent
import java.util.List

class RXPanel extends JPanel {

    private static final String TXT_TAB_TITLE = 'Rx'
    private SwingBuilder sb

    private JTextField txtEmpleado
    private JLabel lblEmpleado
    private JLabel lblFolio
    private ButtonGroup groupUso
    private JRadioButton rbLejos
    private JRadioButton rbCerca
    private JRadioButton rbBifocal
    private JRadioButton rbProgresivo
    private JRadioButton rbIntermedio
    private JRadioButton rbBifocalInter
    Receta rec = new Receta()

    private JTextField txtOdEsfera
    private JTextField txtOdCil
    private JTextField txtOdEje
    private JTextField txtOdAd
    private JTextField txtOdAv
    private JTextField txtOdDm
    private JTextField txtOdPrisma
    private JTextField txtOdUbic

    private JTextField txtOiEsfera
    private JTextField txtOiCil
    private JTextField txtOiEje
    private JTextField txtOiAd
    private JTextField txtOiAv
    private JTextField txtOiDm
    private JTextField txtOiPrisma
    private JTextField txtOiUbic

    private JTextField txtDICerca
    private JTextField txtAltOblea
    private JTextField txtDILejos

    private JTextField txtObservaciones

    private JPanel empleadoPanel
    private JPanel usoRxPanel1
    private JPanel usoRxPanel2

    private DefaultTableModel rxModel
    private List<Rx> lstRecetas
    private Rx receta
    private Integer idCliente
    private Integer idSucursal


    private final String EDITAR = 'Editar Receta'
    private final String NUEVA = 'Nueva Receta'

    public boolean cancel = false

    public RXPanel( Integer idCliente )
    {
        sb = new SwingBuilder()
        lstRecetas = [ ] as ObservableList
        lstRecetas.addAll( CustomerController.findAllPrescriptions( idCliente ) )
        this.idCliente = idCliente
        this.idSucursal = CustomerController.findCurrentSucursal()
        buildUI()
    }

    private void buildUI( ) {
        sb.panel( this, border: BorderFactory.createEmptyBorder( 10, 5, 10, 5 ) ) {
            borderLayout()
            scrollPane( constraints: BorderLayout.CENTER, mousePressed: doNewRx ) {
                table( selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doClick, mousePressed: doPress ) {
                    rxModel = tableModel( list: lstRecetas ) {
                        closureColumn( header: 'Fecha', read: {Rx tmp -> tmp?.rxDate}, minWidth: 90, cellRenderer: new DateCellRenderer() )
                        closureColumn( header: 'Optometrista', read: {Rx tmp -> tmp?.optNameFormatter}, minWidth: 190 )
                        closureColumn( header: 'Factura', read: {Rx tmp -> StringUtils.trimToEmpty(tmp?.order?.bill)} )
                        closureColumn( header: 'Tipo', read: {Rx tmp -> tmp?.getTipo()}, minWidth: 90 )
                        closureColumn( header: 'Folio', read: {Rx tmp -> tmp?.folio} )
                    } as DefaultTableModel
                }
            }
            detailsRx( BorderLayout.PAGE_END )
        }

    }

    private JPanel detailsRx( String pConstraint ) {
        JPanel details = sb.panel( constraints: pConstraint ) {
            borderLayout()
            panel( minimumSize: [ 660, 150 ] as Dimension,
                    border: titledBorder( "Rx" ),
                    constraints: BorderLayout.CENTER,
                    layout: new MigLayout( 'fill,wrap 8',
                            '[][fill,grow][fill,grow][fill,grow][fill,grow][fill,grow]30[fill][fill,grow]' )
            ) {
                label()
                label( text: 'Esfera', horizontalAlignment: JTextField.CENTER )
                label( text: 'Cil.', toolTipText: 'Cilindro', horizontalAlignment: JTextField.CENTER )
                label( text: 'Eje', horizontalAlignment: JTextField.CENTER )
                label( text: 'Ad.', toolTipText: 'Adición', horizontalAlignment: JTextField.CENTER )
                label( text: 'A.V.', toolTipText: 'Agudeza Visual', horizontalAlignment: JTextField.CENTER, visible: false, constraints: 'hidemode 3' )
                label( text: 'D.M.', toolTipText: 'Distancia Monocular', horizontalAlignment: JTextField.CENTER )
                label( text: '' )
                label( text: '' )
                label( text: 'Prisma', horizontalAlignment: JTextField.CENTER, visible: false, constraints: 'hidemode 3' )
                label( text: 'Ubic.', toolTipText: 'Ubicación', horizontalAlignment: JTextField.CENTER, visible: false, constraints: 'hidemode 3' )


                label( text: 'O.D.', toolTipText: 'Ojo Derecho' )
                txtOdEsfera = textField( editable: false, horizontalAlignment: JTextField.RIGHT )
                txtOdCil = textField( editable: false, toolTipText: 'Cilindro', horizontalAlignment: JTextField.RIGHT )
                txtOdEje = textField( editable: false, horizontalAlignment: JTextField.RIGHT )
                txtOdAd = textField( editable: false, toolTipText: 'Adición', horizontalAlignment: JTextField.RIGHT )
                txtOdAv = textField( editable: false, toolTipText: 'Agudeza Visual', horizontalAlignment: JTextField.LEFT, visible: false, constraints: 'hidemode 3' )
                txtOdDm = textField(
                        editable: false,
                        toolTipText: 'Distancia Monocular',
                        horizontalAlignment: JTextField.RIGHT
                )
                label( text: 'D.I. Binocular', toolTipText: 'Distancia Interpupilar Lejos' )
                txtDILejos = textField(
                        editable: false,
                        minimumSize: [ 20, 20 ] as Dimension,
                        toolTipText: 'Distancia Interpupilar Lejos',
                        horizontalAlignment: JTextField.RIGHT
                )
                txtOdPrisma = textField( editable: false, horizontalAlignment: JTextField.RIGHT, visible: false, constraints: 'hidemode 3' )
                txtOdUbic = textField( editable: false, toolTipText: 'Ubicación', horizontalAlignment: JTextField.LEFT, visible: false, constraints: 'hidemode 3' )
                label( text: 'D.I. Cerca', toolTipText: 'Distancia Interpupilar Cerca', visible: false, constraints: 'hidemode 3' )
                txtDICerca = textField(
                        editable: false,
                        minimumSize: [ 20, 20 ] as Dimension,
                        toolTipText: 'Distancia Interpupilar Cerca',
                        horizontalAlignment: JTextField.RIGHT, visible: false, constraints: 'hidemode 3'
                )

                label( text: 'O.I.', toolTipText: 'Ojo Izquierdo' )
                txtOiEsfera = textField( editable: false, horizontalAlignment: JTextField.RIGHT )
                txtOiCil = textField( editable: false, toolTipText: 'Cilindro', horizontalAlignment: JTextField.RIGHT )
                txtOiEje = textField( editable: false, horizontalAlignment: JTextField.RIGHT )
                txtOiAd = textField( editable: false, toolTipText: 'Adición', horizontalAlignment: JTextField.RIGHT )
                txtOiAv = textField( editable: false, toolTipText: 'Agudeza Visual', horizontalAlignment: JTextField.LEFT, visible: false, constraints: 'hidemode 3' )
                txtOiDm = textField(
                        editable: false,
                        toolTipText: 'Distancia Monocular',
                        horizontalAlignment: JTextField.RIGHT
                )
                txtOiPrisma = textField( editable: false, horizontalAlignment: JTextField.RIGHT, visible: false, constraints: 'hidemode 3' )
                txtOiUbic = textField( editable: false, toolTipText: 'Ubicación', horizontalAlignment: JTextField.LEFT, visible: false, constraints: 'hidemode 3' )
                label( text: 'Alt. Seg.', toolTipText: 'Altura Oblea' )
                txtAltOblea = textField(
                        editable: false,
                        minimumSize: [ 20, 20 ] as Dimension,
                        toolTipText: 'Altura Oblea',
                        horizontalAlignment: JTextField.RIGHT
                )
            }
            panel( constraints: BorderLayout.PAGE_END, border: BorderFactory.createEmptyBorder( 5, 0, 0, 0 ) ) {
                borderLayout()
                label( text: 'Observaciones:', constraints: BorderLayout.LINE_START )
                txtObservaciones = textField( editable: false, constraints: BorderLayout.CENTER )
            }

        }
        return details
    }

    private void doBindings( ) {
        sb.build {
            bean( txtOdEsfera, text: bind( source: receta, sourceProperty: 'odEsfR' ) )
            bean( txtOdCil, text: bind( source: receta, sourceProperty: 'odCilR' ) )
            bean( txtOdEje, text: bind( source: receta, sourceProperty: 'odEjeR' ) )
            bean( txtOdAd, text: bind( source: receta, sourceProperty: 'odAdcR' ) )
            if ( receta?.odAvR != null ) {
                bean( txtOdAv, text: bind( source: receta, sourceProperty: 'odAvR' ) )
            } else {
                txtOdAv.setText( '20/' )
            }
            bean( txtOdDm, text: bind( source: receta, sourceProperty: 'diOd' ) )
            bean( txtOdPrisma, text: bind( source: receta, sourceProperty: 'odPrismH' ) )
            bean( txtOdUbic, text: bind( source: receta, sourceProperty: 'odPrismaV' ) )
            bean( txtDILejos, text: bind( source: receta, sourceProperty: 'diLejosR' ) )
            bean( txtOiEsfera, text: bind( source: receta, sourceProperty: 'oiEsfR' ) )
            bean( txtOiCil, text: bind( source: receta, sourceProperty: 'oiCilR' ) )
            bean( txtOiEje, text: bind( source: receta, sourceProperty: 'oiEjeR' ) )
            bean( txtOiAd, text: bind( source: receta, sourceProperty: 'oiAdcR' ) )
            if ( receta?.oiAvR != null ) {
                bean( txtOiAv, text: bind( source: receta, sourceProperty: 'oiAvR' ) )
            } else {
                txtOiAv.setText( '20/' )
            }
            bean( txtOiDm, text: bind( source: receta, sourceProperty: 'diOi' ) )
            bean( txtOiPrisma, text: bind( source: receta, sourceProperty: 'oiPrismH' ) )
            bean( txtOiUbic, text: bind( source: receta, sourceProperty: 'oiPrismaV' ) )
            bean( txtDICerca, text: bind( source: receta, sourceProperty: 'diCercaR' ) )
            bean( txtAltOblea, text: bind( source: receta, sourceProperty: 'altOblR' ) )
            bean( txtObservaciones, text: bind( source: receta, sourceProperty: 'observacionesR' ) )
        }
    }

    private def doClick = { MouseEvent ev ->
        if ( SwingUtilities.isLeftMouseButton( ev ) && ev.source.selectedElement != null ) {
            Rx selection = ev.source.selectedElement as Rx
            receta = selection
            doBindings()
        }
    }

    private def doPress = { MouseEvent ev ->
        if ( SwingUtilities.isRightMouseButton( ev ) && ev.source.selectedElement != null ) {
            Rx selection = ev.source.selectedElement as Rx
            if ( selection.id ) {
                sb.popupMenu {
                    menuItem( text: 'Nueva Receta',
                            actionPerformed: {
                                NoSaleDialog newRx = new NoSaleDialog( this, selection.idClient, selection.idStore, false)
                                newRx.show()
                                lstRecetas.clear()
                                lstRecetas.addAll( CustomerController.findAllPrescriptions( idCliente ) )
                                doBindings()
                                rxModel.fireTableDataChanged()
                            }
                    )
                    menuItem( text: 'Editar', visible: selection?.order?.deliveryDate == null,
                            actionPerformed: {
                                EditRxDialog editRx = new EditRxDialog( this, selection, selection.idClient, selection.idStore, selection.clientName, selection.tipoEditRx, true, false )
                                editRx.show()
                                lstRecetas.clear()
                                lstRecetas.addAll( CustomerController.findAllPrescriptions( idCliente ) )
                                doBindings()
                                rxModel.fireTableDataChanged()
                            }
                    )
                    menuItem( text: 'Reimprimir',
                            actionPerformed: {
                               println( 'RxID: ' + selection.id)
                                Order orderTmp = OrderController.notaVentaxRx(selection.id)
                                if( orderTmp != null ){
                                  OrderController.printRx(orderTmp.id, true)
                                  OrderController.fieldRX(orderTmp.id)
                                } else {
                                    sb.optionPane(
                                            message: 'No existe venta para esta receta',
                                            messageType: JOptionPane.ERROR_MESSAGE
                                    ).createDialog(this, 'Venta no encontrada')
                                            .show()
                                }
                            }
                    )
                }.show( ev.component, ev.x, ev.y )
            }
        }
    }

    private def doNewRx = { MouseEvent ev ->
        if ( SwingUtilities.isRightMouseButton( ev ) ) {
            sb.popupMenu {
                menuItem( text: 'Nueva Receta',
                        actionPerformed: {
                            Branch branch = Session.get( SessionItem.BRANCH ) as Branch
                            NoSaleDialog newRx = new NoSaleDialog( this, idCliente, idSucursal, false)
                            newRx.show()
                            lstRecetas.clear()
                            lstRecetas.addAll( CustomerController.findAllPrescriptions( idCliente ) )
                            doBindings()
                            rxModel.fireTableDataChanged()
                        }
                )
            }.show( ev.component, ev.x, ev.y )
        }
    }


    private void edit( ) {
        empleadoPanel.visible = true
        usoRxPanel1.visible = true
        usoRxPanel2.visible = true
        txtOdEsfera.editable = true
        txtOdCil.editable = true
        txtOdEje.editable = true
        txtOdAd.editable = true
        txtOdAv.editable = true
        txtOdDm.editable = true
        txtOdPrisma.editable = true
        txtOdUbic.editable = true
        txtOiEsfera.editable = true
        txtOiCil.editable = true
        txtOiEje.editable = true
        txtOiAd.editable = true
        txtOiAv.editable = true
        txtOiDm.editable = true
        txtOiPrisma.editable = true
        txtOiUbic.editable = true
        txtDICerca.editable = true
        txtAltOblea.editable = true
        txtDILejos.editable = true
        txtObservaciones.editable = true
    }

    String getTitle( ) {
        return TXT_TAB_TITLE
    }

}
