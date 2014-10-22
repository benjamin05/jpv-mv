package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.model.DetalleNotaVenta
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.Pago
import mx.lux.pos.model.Articulo
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.renderer.DateCellRenderer
import mx.lux.pos.ui.view.renderer.MoneyCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils

import javax.swing.*
import java.awt.*
import java.awt.event.MouseEvent
import java.text.SimpleDateFormat
import java.util.List

class ClientActivityPanel extends JPanel {

    private static final String TAG_GENERICO_LENTE = "B"
    private static final String TAG_GENERICO_LENTE_CONTACTO_INV = "C"
    private static final String TAG_GENERICO_LENTE_CONTACTO_NO_INV = "H"
    private static final String TAG_GENERICO_ARMAZON = "A"
    private static final String TAG_GENERICO_ACCESORIOS = "E"
    private SwingBuilder sb

    JPanel details
    JPanel detailsCancelled
    private JTextField txtFolio
    private JTextField txtVendedor
    private JTextField txtLente
    private JTextField txtMontoLente
    private JTextField txtArmazon
    private JTextField txtMontoArmazon
    private JTextField txtAccesorios
    private JTextField txtMontoAccesorios
    private JTextField txtMontoDescuento
    private JTextField txtPromocion
    private JTextField txtTotal
    private JTextField txtObservaciones
    private JTextField txtEmpCan
    private JTextField txtTransfer
    private JTextField txtNota
    private JTextField txtDevolucion
    private JTextField txtCausCanc

    private DefaultTableModel rxModel
    private DefaultTableModel pagosModel
    private DefaultTableModel articulosModel
    private List<NotaVenta> lstNotas
    private List<Pago> lstPagos = new ArrayList<Pago>()
    private List<DetalleNotaVenta> lstArticulos = new ArrayList<Pago>()
    private NotaVenta notaVenta
    private Integer idCliente
    private Integer idSucursal

    private JPanel facturasActivasPanel
    private JPanel facturasCanceladasPanel
    private JLabel lblCancelada


    private final String TAG_CANCELADA = "T"

    public ClientActivityPanel( Integer idCliente ){
        sb = new SwingBuilder()
        lstNotas = [ ] as ObservableList
        if( idCliente != null ){
          this.idCliente = idCliente
        } else {
          this.idCliente = 0
        }
        lstNotas.addAll( CustomerController.findAllActiveOrders( this.idCliente ) )
        this.idSucursal = CustomerController.findCurrentSucursal()
        buildUI()
    }

    private void buildUI( ) {
        sb.panel( this, border: BorderFactory.createEmptyBorder( 10, 5, 10, 5 ) ) {
            borderLayout()
            scrollPane( constraints: BorderLayout.CENTER ) {
                table( selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doClick ) {
                    rxModel = tableModel( list: lstNotas ) {
                        closureColumn( header: 'Fecha', read: {NotaVenta tmp -> tmp?.fechaHoraFactura}, minWidth: 90, cellRenderer: new DateCellRenderer() )
                        closureColumn( header: 'Tipo', read: {NotaVenta tmp -> setTipoOrden(tmp)}, minWidth: 190 )
                        closureColumn( header: 'Factura', read: {NotaVenta tmp -> StringUtils.trimToEmpty(tmp?.factura)} )
                        closureColumn( header: 'Importe', read: {NotaVenta tmp -> tmp?.ventaNeta}, minWidth: 90, cellRenderer: new MoneyCellRenderer() )
                    } as DefaultTableModel
                }
            }
            detailsOrder( BorderLayout.PAGE_END )
        }

    }

    private JPanel detailsOrder( String pConstraint ) {
        details = sb.panel( constraints: pConstraint ) {
            borderLayout()
            facturasActivasPanel = panel( minimumSize: [ 660, 150 ] as Dimension,
                    border: titledBorder( "Datos" ),
                    constraints: BorderLayout.CENTER,
                    layout: new MigLayout( 'fill,wrap 3, hidemode 3','[fill,grow][fill,grow][fill,grow]' )
            ) {
              lblCancelada = label( "CANCELADA", constraints: 'span, hidemode 3', foreground: UI_Standards.WARNING_FOREGROUND, visible: false  )
              label( "FOLIO" )
              label( "VENDEDOR", constraints: 'span' )
              txtFolio = textField( editable: false )
              txtVendedor = textField( constraints: 'span', editable: false )
              /*label( "LENTE" )
              txtLente = textField( editable: false )
              txtMontoLente = textField( editable: false )
              label( "ARMAZON" )
              txtArmazon = textField( editable: false )
              txtMontoArmazon = textField( editable: false )
              label( "ACCESORIOS" )
              txtAccesorios = textField( editable: false )
              txtMontoAccesorios = textField( editable: false )*/
              scrollPane( constraints: "span", maximumSize: [600,80] as Dimension ) {
                table( selectionMode: ListSelectionModel.SINGLE_SELECTION ) {
                  articulosModel = tableModel( list: lstArticulos ) {
                    closureColumn( header: 'Articulo', read: {DetalleNotaVenta tmp -> tmp?.articulo?.articulo}, minWidth: 50 )
                    closureColumn( header: 'Descripcion', read: {DetalleNotaVenta tmp -> tmp?.articulo?.descripcion}, minWidth: 150 )
                    closureColumn( header: 'Monto', read: {DetalleNotaVenta tmp -> tmp?.precioUnitLista}, minWidth: 50, cellRenderer: new MoneyCellRenderer() )
                    closureColumn( header: 'Cantidad', read: {DetalleNotaVenta tmp -> tmp?.cantidadFac?.intValue()}, minWidth: 50 )
                    closureColumn( header: 'Monto Total', read: {DetalleNotaVenta tmp -> setTotal(tmp?.precioUnitFinal, tmp?.cantidadFac?.intValue())}, minWidth: 50 )
                  } as DefaultTableModel
                }
              }
              label( "PROMOCION" )
              label( "DESCUENTO" )
              txtMontoDescuento = textField( editable: false )
              txtPromocion = textField( editable: false, foreground: UI_Standards.WARNING_FOREGROUND )
              label( "TOTAL" )
              txtTotal = textField( editable: false )
              label( "PAGOS",constraints: 'span' )
              scrollPane( constraints: "span", maximumSize: [600,80] as Dimension ) {
                 table( selectionMode: ListSelectionModel.SINGLE_SELECTION ) {
                   pagosModel = tableModel( list: lstPagos ) {
                    closureColumn( header: 'Tipo Pago', read: {Pago tmp -> tmp?.idFPago}, minWidth: 50 )
                    closureColumn( header: 'Monto', read: {Pago tmp -> tmp?.monto}, minWidth: 100 )
                  } as DefaultTableModel
                }
              }
              label( "OBSERVACIONES", constraints: 'span' )
              txtObservaciones = textField( editable: false, constraints: 'span' )
            }
        }
        return details
    }


    private JPanel detailsOrderCancelled( String pConstraint ) {
        detailsCancelled = sb.panel( constraints: pConstraint ) {
            borderLayout()
            facturasCanceladasPanel = panel( minimumSize: [ 660, 150 ] as Dimension,
                    border: titledBorder( "Datos" ),
                    constraints: BorderLayout.CENTER,
                    layout: new MigLayout( 'fill,wrap 3, hidemode 3','[fill,grow][fill,grow][fill,grow]' )
            ) {
                label( "FOLIO" )
                label( "VENDEDOR", constraints: 'span' )
                txtFolio = textField( editable: false )
                txtVendedor = textField( constraints: 'span', editable: false )
                label( "EMP. CAN" )
                txtEmpCan = textField( constraints: 'span' )
                label( "TRANSFER" )
                label( "NOTA" )
                label( "DEVOLUCION" )
                txtTransfer = textField( )
                txtNota = textField( )
                txtDevolucion = textField( )
                label( "CAUSA CANCELACION",constraints: 'span' )
                txtCausCanc = textField( constraints: 'span' )
            }
        }
        return detailsCancelled
    }

    private void doBindings( ) {
        txtFolio.setText( StringUtils.trimToEmpty(notaVenta.id) )
        txtVendedor.setText(StringUtils.trimToEmpty(notaVenta.idEmpleado)+" "+StringUtils.trimToEmpty(notaVenta.empleado.nombreCompleto))
        /*txtLente.setText( descripcion(notaVenta, TAG_GENERICO_LENTE) )
        txtMontoLente.setText( monto(notaVenta, TAG_GENERICO_LENTE) )
        txtArmazon.setText( descripcion(notaVenta, TAG_GENERICO_ARMAZON) )
        txtMontoArmazon.setText( monto(notaVenta, TAG_GENERICO_ARMAZON) )
        txtAccesorios.setText(descripcion(notaVenta, TAG_GENERICO_ACCESORIOS) )
        txtMontoAccesorios.setText(monto(notaVenta, TAG_GENERICO_ACCESORIOS) )*/
        txtMontoDescuento.setText( descuento(notaVenta, true) )
        txtPromocion.setText(descuento(notaVenta, false) )
        txtTotal.setText(String.format("%s%s",/$/,StringUtils.trimToEmpty(notaVenta.ventaNeta.toString())).replace(" ",""))
        txtObservaciones.setText(notaVenta.observacionesNv)
        lstPagos.clear()
        lstPagos.addAll(notaVenta.pagos)
        lstArticulos.clear()
        lstArticulos.addAll( notaVenta.detalles )
        pagosModel.fireTableDataChanged()
        articulosModel.fireTableDataChanged()
    }

    private def doClick = { MouseEvent ev ->
        if ( SwingUtilities.isLeftMouseButton( ev ) && ev.source.selectedElement != null ) {
            notaVenta = ev.source.selectedElement as NotaVenta
            if( StringUtils.trimToEmpty(notaVenta.sFactura).equalsIgnoreCase(TAG_CANCELADA)){
              lblCancelada.setVisible( true )
            } else {
              lblCancelada.setVisible( false )
            }
            doBindings()
        }
    }


    String setTipoOrden( NotaVenta nota ){
      String tipo = "F"
      /*if( StringUtils.trimToEmpty(nota.sFactura).equalsIgnoreCase(TAG_CANCELADA) ){
          tipo = "C"
      }*/
      return  tipo
    }

    static String descripcion( NotaVenta nota, String desc ){
        String lente = ""
        for(DetalleNotaVenta detalleNotaVenta : nota.detalles){
          if( desc.equalsIgnoreCase(TAG_GENERICO_LENTE) ){
              String generico = StringUtils.trimToEmpty(detalleNotaVenta?.articulo?.generico?.id)
              if(generico.equalsIgnoreCase(TAG_GENERICO_LENTE) || generico.equalsIgnoreCase(TAG_GENERICO_LENTE_CONTACTO_INV)
                      || generico.equalsIgnoreCase(TAG_GENERICO_LENTE_CONTACTO_NO_INV) ){
                  lente = lente+","+StringUtils.trimToEmpty(detalleNotaVenta.articulo.articulo)
              }
          } else if( desc.equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
              if(StringUtils.trimToEmpty(detalleNotaVenta?.articulo?.generico?.id).equalsIgnoreCase(TAG_GENERICO_ARMAZON)){
                  lente = lente+","+StringUtils.trimToEmpty(detalleNotaVenta.articulo.articulo)
              }
          } else if( desc.equalsIgnoreCase(TAG_GENERICO_ACCESORIOS) ){
              if(StringUtils.trimToEmpty(detalleNotaVenta?.articulo?.generico?.id).equalsIgnoreCase(TAG_GENERICO_ACCESORIOS)){
                  lente = lente+","+StringUtils.trimToEmpty(detalleNotaVenta.articulo.articulo)
              }
          }

        }
        if( lente.startsWith(",")){
          lente = lente.replaceFirst(",","")
        }
        return  lente
    }

    static String monto( NotaVenta nota, String desc ){
        BigDecimal montoLente = BigDecimal.ZERO
        for(DetalleNotaVenta detalleNotaVenta : nota.detalles){
            if(desc.equalsIgnoreCase(TAG_GENERICO_LENTE)){
                String generico = StringUtils.trimToEmpty(detalleNotaVenta?.articulo?.generico?.id)
                if(generico.equalsIgnoreCase(TAG_GENERICO_LENTE) || generico.equalsIgnoreCase(TAG_GENERICO_LENTE_CONTACTO_INV)
                        || generico.equalsIgnoreCase(TAG_GENERICO_LENTE_CONTACTO_NO_INV) ){
                    montoLente = montoLente.add( detalleNotaVenta.precioUnitLista )
                }
            } else if(desc.equalsIgnoreCase(TAG_GENERICO_ARMAZON)){
                if(StringUtils.trimToEmpty(detalleNotaVenta?.articulo?.generico?.id).equalsIgnoreCase(TAG_GENERICO_ARMAZON)){
                    montoLente = montoLente.add( detalleNotaVenta.precioUnitLista )
                }
            } else if(desc.equalsIgnoreCase(TAG_GENERICO_ACCESORIOS)){
                if(StringUtils.trimToEmpty(detalleNotaVenta?.articulo?.generico?.id).equalsIgnoreCase(TAG_GENERICO_ACCESORIOS)){
                    montoLente = montoLente.add( detalleNotaVenta.precioUnitLista )
                }
            }
        }
        return String.format("%s%s", /$/, StringUtils.trimToEmpty(montoLente.toString())).replace(" ","")
    }


    static String descuento( NotaVenta nota, Boolean monto ){
      String descuento = ""
      String[] valores = OrderController.montoDescuento( nota.id )
      if( monto ){
        descuento = StringUtils.trimToEmpty(valores[1]).length() <= 0 ? "" : String.format("%s%s", /$/, valores[1]).replace(" ","")
      } else {
        descuento = StringUtils.trimToEmpty( valores [0] )
      }
      return descuento
    }


    static String setTotal( BigDecimal monto, Integer cantidad){
      String total = ""
      BigDecimal montoTotal = monto.multiply(new BigDecimal( cantidad ))
      total = String.format("%s%s", /$/, StringUtils.trimToEmpty(montoTotal.toString())).replace(" ","")
      return total
    }


}
