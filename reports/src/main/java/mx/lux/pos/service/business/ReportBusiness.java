package mx.lux.pos.service.business;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.OrderSpecifier;
import mx.lux.pos.model.*;
import mx.lux.pos.repository.*;
import mx.lux.pos.service.impl.ReportServiceImpl;
import net.sf.jasperreports.engine.*;
import org.apache.commons.lang.StringUtils;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Component
public class ReportBusiness {

    private static Logger log = LoggerFactory.getLogger( ReportServiceImpl.class );

    @Resource
    private ImpuestoRepository impuestoRepository;

    @Resource
    private ExamenRepository examenRepository;

    @Resource
    private JbRepository jbRepository;

    @Resource
    private TipoPagoRepository tipoPagoRepository;

    @Resource
    private BancoEmisorRepository bancoEmisorRepository;

    @Resource
    private ClienteRepository clienteRepository;

    @Resource
    private CotizacionRepository cotizacionRepository;

    @Resource
    private DescuentoRepository descuentoRepository;

    @Resource
    private PromocionRepository promocionRepository;

    @Resource
    private TrabajoRepository trabajoRepository;

    @Resource
    private TrabajoTrackRepository trabajoTrackRepository;

    @Resource
    private ArticuloRepository articuloRepository;

    @Resource
    private ParametroRepository parametroRepository;

    @Resource
    private RecetaRepository recetaRepository;

    @Resource
    private PagoRepository pagoRepository;

    @Resource
    private DevolucionRepository devolucionRepository;

    @Resource
    private EmpleadoRepository empleadoRepository;

    @Resource
    private NotaVentaRepository notaVentaRepository;

    @Resource
    private ModificacionRepository modificacionRepository;

    @Resource
    private DetalleNotaVentaRepository detalleNotaVentaRepository;

    @Resource
    private TransInvRepository transInvRepository;

    @Resource
    private TransInvDetalleRepository transInvDetalleRepository;

    @Resource
    private CuponMvRepository cuponMvRepository;

    @Resource
    private SucursalRepository sucursalRepository;

    @Resource
    private OrdenPromDetRepository ordenPromDetRepository;

    @Resource
    private PrecioRepository precioRepository;

    private static final Integer TAG_PUESTO_OFTALMOLOGO = 3;
    private static final String TAG_CANCELADO = "T";
    private static final String TAG_TIPO_CANCELADO = "can";
    private static final String TAG_ORDEN_SERVICIO = "OS";
    private static final String TAG_ESTADO_ENTREGADO = "TE";
    private static final String TAG_ESTADO_CANCELADO = "CN";
    private static final String TAG_ESTADO_GARANTIA = "GAR";

    private static final String TAG_ROTO_POR_ENVIAR = "RPE";
    private static final String TAG_ROTO_EN_PINO = "REP";
    private static final String TAG_ROTO_NO_ENVIAR = "RNE";
    private static final String TAG_DESENTREGADO_POR_ROTO = "DPR";
    private static final String TAG_BODEGA = "BD";

    
    public List<IngresoPorDia> obtenerIngresoporDia( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerIngresoporDia()" );

        Parametro ivaVigenteParam = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
        Impuesto iva = impuestoRepository.findOne( ivaVigenteParam.getValor() );
        Double ivaTasa = iva.getTasa();

        List<IngresoPorDia> lstIngresos = new ArrayList<IngresoPorDia>();
        log.info( "fechaInicio:{},  fechaFin:{}", fechaInicio, fechaFin );
        List<Pago> lstpagos = pagoRepository.findByFechaBetweenOrderByFechaAsc( fechaInicio, fechaFin );

        for ( Pago pago : lstpagos ) {
            if ( isPagoValid( pago ) && pago.getNotaVenta().getFactura() != null && pago.getNotaVenta().getFactura() != ""
                    && !TAG_CANCELADO.equalsIgnoreCase(pago.getNotaVenta().getsFactura()) ) {
                IngresoPorDia ingreso = FindOrCreate( lstIngresos, pago.getFecha() );
                ingreso.AcumulaMonto( pago.getMonto(), ivaTasa );
            }
        }

        List<Devolucion> lstDevoluciones = devolucionRepository.findByFechaBetween( fechaInicio, fechaFin );
        for ( Devolucion devolucion : lstDevoluciones ) {
            IngresoPorDia devoluciones = FindOrCreate( lstIngresos, devolucion.getFecha() );
            devoluciones.AcumulaDevolucion( devolucion.getMonto(), ivaTasa );
        }

        for(IngresoPorDia ingreso : lstIngresos){
            ingreso.setMontoAcumulado(ingreso.getMontoAcumulado().divide(new BigDecimal(1+(ivaTasa/100)), 10, RoundingMode.HALF_EVEN));
        }
        return lstIngresos;
    }

    protected boolean isPagoValid(  Pago pPago ) {
        boolean valid = true;
        if ( valid )
            valid = !pPago.getIdFormaPago().equalsIgnoreCase( "BD" );
        if ( valid )
            valid = !pPago.getIdFormaPago().equalsIgnoreCase( "EX" );

        Parametro convenios = parametroRepository.findOne( TipoParametro.CONV_NOMINA.getValue() );
        if ( valid )
            valid = !pPago.getNotaVenta().getIdConvenio().contains( convenios.getValor() );

        return valid;
    }

    
    public IngresoPorDia FindOrCreate(  List<IngresoPorDia> lstIngresos, Date fecha ) {
        Date onlyDay = DateUtils.truncate( fecha, Calendar.DATE );
        IngresoPorDia found = null;
        for ( IngresoPorDia ingresos : lstIngresos ) {
            if ( ingresos.getFecha().equals( onlyDay ) ) {

                found = ingresos;
                break;
            }
        }
        if ( found == null ) {
            found = new IngresoPorDia( onlyDay );
            lstIngresos.add( found );
        }
        return found;
    }

    
    public IngresoPorVendedor FindorCreate(  List<IngresoPorVendedor> lstIngresos, String idEmpleado ) {
        IngresoPorVendedor found = null;
        for ( IngresoPorVendedor ingresos : lstIngresos ) {
            if ( ingresos.getIdEmpleado().equals( idEmpleado ) ) {
                found = ingresos;
                break;
            }
        }
        if ( found == null ) {
            found = new IngresoPorVendedor( idEmpleado );
            Empleado empleado = empleadoRepository.findOne( idEmpleado );
            if ( empleado != null ) {
                found.setNombre( empleado.nombreCompleto() );
                //found.setPagos(pagos)
            }
            lstIngresos.add( found );
        }
        return found;
    }

    public String CompilayGeneraReporte(  org.springframework.core.io.Resource template, Map<String, Object> parametros,  File report ) {

        try {
            report.setExecutable( true );
            report.setReadable( true );
            report.setWritable( true );

            String tmpPath = System.getProperty( "java.io.tmpdir" );
            String cmd = "chmod 777 -R "+tmpPath;
            Process p = Runtime.getRuntime().exec(cmd);
            JasperReport jasperReport = JasperCompileManager.compileReport( template.getInputStream() );
            JasperPrint jasperPrint = JasperFillManager.fillReport( jasperReport, parametros, new JREmptyDataSource() );
            jasperPrint.setProperty("net.sf.jasperreports.expo rt.character.encoding","ISO-8859-1");
            //JasperExportManager.exportReportToHtmlFile( jasperPrint, report.getPath() );
            //JasperExportManager.exportReportToPdfFile( jasperPrint, report.getPath() );

            try{
            JRTextExporter exporter = new JRTextExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, report.getAbsolutePath() );
            exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Float(4));
            exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Float(9));
            exporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, new Float(300));
            exporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, new Float(500));
            exporter.setParameter(JRTextExporterParameter.BETWEEN_PAGES_TEXT, "");
            exporter.exportReport();
            Runtime.getRuntime().exec("firefox "+report.getAbsolutePath());
            } catch (JRException jRException) {
                System.err.println(jRException);
            }

            //Desktop.getDesktop().open( report );

            log.info( "Mostrar Reporte" );

            Runtime garbage = Runtime.getRuntime();
            garbage.gc();

            return report.getPath();
        } catch ( JRException e ) {
            log.error( "error al compilar y generar reporte", e );
        } catch ( IOException e ) {
            log.error( "error al compilar y generar reporte", e );
        }
        return report.getPath();
    }

    
    public List<IngresoPorVendedor> obtenerVentasporVendedor( Date fechaInicio, Date fechaFin ) {
        String pagosNoTransf = Registry.getPaymentsNoRefound();
        List<IngresoPorVendedor> lstIngresos = new ArrayList<IngresoPorVendedor>();
        List<IngresoPorVendedor> lstIngresosCan = new ArrayList<IngresoPorVendedor>();
        //List<IngresoPorVendedor> lstIngresosTmp = new ArrayList<IngresoPorVendedor>();
        List<String> empleados = notaVentaRepository.empleadosFechas(fechaInicio,fechaFin);

        QNotaVenta notaVenta = QNotaVenta.notaVenta;
        List<NotaVenta> lstVentas = ( List<NotaVenta> ) notaVentaRepository.findAll( notaVenta.factura.isNotEmpty().and(notaVenta.factura.isNotNull()).
                and(notaVenta.fechaHoraFactura.between(fechaInicio, fechaFin)),
                notaVenta.idEmpleado.asc(), notaVenta.fechaHoraFactura.asc() );

        QPago payment = QPago.pago;
        List<Pago> lstPagosBod = (List<Pago>) pagoRepository.findAll(payment.fecha.between(fechaInicio,fechaFin).
                and(payment.idFPago.eq(TAG_BODEGA)));


        for( NotaVenta nota : lstVentas ){
            IngresoPorVendedor venta = FindorCreate(lstIngresos, nota.getIdEmpleado());
            venta.AcumulaVentaPorVendedor( nota, pagosNoTransf );
        }

        for(IngresoPorVendedor ingreso : lstIngresos){
          Collections.sort( ingreso.getPagos(), new Comparator<IngresoPorFactura>() {
              @Override
              public int compare(IngresoPorFactura o1, IngresoPorFactura o2) {
                  return o1.getIdFactura().compareTo(o2.getIdFactura());
              }
          });
        }
        QModificacion modificacion = QModificacion.modificacion;
        List<Modificacion> lstCancelaciones = (List<Modificacion>)modificacionRepository.findAll( modificacion.fecha.between(fechaInicio,fechaFin).
                and(modificacion.tipo.eq("can")),
                modificacion.notaVenta.idEmpleado.asc() );

        for( Modificacion mod : lstCancelaciones ){
            IngresoPorVendedor cancelacion = FindorCreate( lstIngresosCan, mod.getNotaVenta().getIdEmpleado() );
            cancelacion.AcumulaCancelacionesPorVendedor( mod, pagosNoTransf );
        }

        for(Pago pago : lstPagosBod){
          NotaVenta nota = notaVentaRepository.findOne( pago.getIdFactura() );
          if( nota != null ){
            IngresoPorVendedor venta = FindorCreate(lstIngresos, nota.getIdEmpleado());
            venta.AcumulaVentaBodPorVendedor( nota, pago );
          }
        }

        for(IngresoPorVendedor inCan : lstIngresosCan){
            for(IngresoPorVendedor in : lstIngresos){
              if(in.getIdEmpleado().equalsIgnoreCase(inCan.getIdEmpleado())){
                in.getPagos().addAll(inCan.getPagos());
              }
            }
        }

        for(IngresoPorVendedor ingreso : lstIngresos){
            Collections.sort( ingreso.getPagos(), new Comparator<IngresoPorFactura>() {
                @Override
                public int compare(IngresoPorFactura o1, IngresoPorFactura o2) {
                    return o1.getIdFactura().compareTo(o2.getIdFactura());
                }
            });
        }

        return lstIngresos;
    }

    private IngresoPorVendedor agregaRegistros( List<NotaVenta> lstVentas){
        List<IngresoPorFactura> ingresoPorFacturas = new ArrayList<IngresoPorFactura>();
        NotaVenta nVenta = new NotaVenta();
        for ( NotaVenta venta : lstVentas ) {
            if ( venta.getFactura() != null ) {
                String idEmpleado = venta.getIdEmpleado();
                String articulos = new String();

                for(DetalleNotaVenta detalle : venta.getDetalles()){

                    if( detalle.getArticulo() != null){

                        articulos =  detalle.getArticulo().getArticulo()  + "," + articulos;
                    }
                }
                IngresoPorFactura ingresoPorFactura = new IngresoPorFactura(venta.getFactura());
                ingresoPorFactura.setTotal(venta.getVentaNeta());
                ingresoPorFactura.setFechaPago(venta.getFechaHoraFactura());
                ingresoPorFactura.setDescripcion(articulos);

                List<Pago> pagos = pagoRepository.findByIdFactura(venta.getId());
                BigDecimal cupon = new BigDecimal(0);
                for(Pago pago : pagos){
                    if(pago.getIdFPago() != null){
                        if(pago.getIdFPago().trim().equals("C1") ||pago.getIdFPago().trim().equals("C2") || pago.getIdFPago().trim().equals("C3") || pago.getIdFPago().trim().equals("C4") ){
                            cupon = pago.getMonto();
                        }
                    }
                }
                String cuponString = "";
               if(cupon != null){
                   System.out.println(cupon.equals( new BigDecimal(0)));
                if(cupon.equals( new BigDecimal(0))){
                    cuponString = "-";
                }  else{
                    cuponString = cupon.toString();
                }
               }
                ingresoPorFactura.setSumaMonto(venta.getVentaTotal().subtract(cupon));
                ingresoPorFactura.setColor(cuponString);
                ingresoPorFacturas.add(ingresoPorFactura);
            }
            nVenta=venta;
        }

        IngresoPorVendedor ingreso = new  IngresoPorVendedor();
        ingreso.setIdEmpleado(nVenta.getIdEmpleado() != null ? nVenta.getIdEmpleado() : "");
        ingreso.setNombre(nVenta.getEmpleado() != null ? nVenta.getEmpleado().getNombreCompleto() : "");
        ingreso.setPagos(ingresoPorFacturas);
        return ingreso;
    }

    
    public List<FacturasPorEmpleado> obtenerFacturasporVendedor( Date fechaInicio, Date fechaFin ) {

        List<FacturasPorEmpleado> lstFacturas = new ArrayList<FacturasPorEmpleado>();
        QModificacion mod = QModificacion.modificacion;
        List<Modificacion> lstModificaciones = (List<Modificacion>) modificacionRepository.findAll( mod.fecha.between(fechaInicio, fechaFin).
                and( mod.tipo.eq(TAG_TIPO_CANCELADO)), mod.idEmpleado.asc(), mod.fecha.asc() );
        for ( Modificacion modificacion : lstModificaciones ) {
            String IdEmpleado = modificacion.getNotaVenta().getIdEmpleado();
            FacturasPorEmpleado factura = FindOrCreate( lstFacturas, IdEmpleado );
            factura.AcumulaCancelaciones( modificacion );
        }
        QNotaVenta nv = QNotaVenta.notaVenta;
        List<NotaVenta> lstVentas = ( List<NotaVenta> ) notaVentaRepository.findAll( nv.fechaHoraFactura.between(fechaInicio, fechaFin).
                and(nv.factura.isNotEmpty().and(nv.factura.isNotNull())));
        for ( NotaVenta venta : lstVentas ) {
            String IdEmpleado = venta.getIdEmpleado();
            FacturasPorEmpleado factura = FindOrCreate( lstFacturas, IdEmpleado );
            factura.AcumulaVentas();
        }
        return lstFacturas;
    }

    
    public FacturasPorEmpleado FindOrCreate(  List<FacturasPorEmpleado> lstFacturas, String idEmpleado ) {
        FacturasPorEmpleado found = null;

        for ( FacturasPorEmpleado facturas : lstFacturas ) {
            if ( facturas.getIdEmpleado().equals( idEmpleado ) ) {
                found = facturas;
                break;
            }
        }
        if ( found == null ) {
            found = new FacturasPorEmpleado( idEmpleado );
            Empleado empleado = empleadoRepository.findOne( idEmpleado );
            if ( empleado != null ) {
                found.setNombre( empleado.nombreCompleto() );
            }
            lstFacturas.add( found );
        }
        return found;
    }

    
    public List<NotaVenta> obtenerVentasLineaporFacturas( Date fechaInicio, Date fechaFin,  String articulo, boolean gogle, boolean oftalmico, boolean todo ) {

        QNotaVenta venta = QNotaVenta.notaVenta;
        log.info( "Verifica que se halla seleccionado un articulo especifico" );
        BooleanBuilder builderArt = new BooleanBuilder();
        if ( !articulo.equals( null ) && !articulo.isEmpty() && articulo.length() > 0 ) {
            builderArt.and( venta.factura.isNotNull() ).and( venta.factura.isNotEmpty() ).and( venta.detalles.any().articulo.articulo.eq( articulo ) );
        } else {
            builderArt.and( venta.factura.isNotNull() ).and( venta.factura.isNotEmpty() );
        }

        BooleanBuilder builderGogle = new BooleanBuilder();
        if ( gogle ) {
            builderGogle.and( venta.detalles.any().articulo.idGenTipo.eq( "G" ) );
        } else {
            builderGogle.and( venta.factura.isNotNull() ).and( venta.factura.isNotEmpty() );
        }

        BooleanBuilder builderOft = new BooleanBuilder();
        if ( oftalmico ) {
            builderOft.and( venta.detalles.any().articulo.idGenTipo.eq( "O" ) );
        } else {
            builderOft.and( venta.factura.isNotNull() ).and( venta.factura.isNotEmpty() );
        }

        BooleanBuilder builder = new BooleanBuilder();
        if ( todo ) {
            builder.and( venta.factura.isNotNull() ).and( venta.factura.isNotEmpty() );
        } else {
            builder.and( venta.factura.isNotNull() ).and( venta.factura.isNotEmpty() );
        }

        List<NotaVenta> lstArticulos = ( List<NotaVenta> ) notaVentaRepository.findAll( venta.fechaHoraFactura.between( fechaInicio, fechaFin ).
                and( builderArt ).and( builderOft ).and( builderGogle ).and( builder ) );

        return lstArticulos;

    }

    
    public List<FacturasPorEmpleado> obtenerVentasLineaporArticulos( Date fechaInicio, Date fechaFin,  String articulo, boolean gogle, boolean oftalmico, boolean todo ) {

        QDetalleNotaVenta venta = QDetalleNotaVenta.detalleNotaVenta;

        log.info( "Verifica que se halla seleccionado un articulo especifico" );
        BooleanBuilder builderArt = new BooleanBuilder();
        if ( !articulo.equals( null ) && !articulo.isEmpty() && articulo.length() > 0 ) {
            builderArt.and( venta.articulo.articulo.eq( articulo ) );
        } else {
            builderArt.and( venta.precioUnitFinal.isNotNull() );
        }

        BooleanBuilder builderGogle = new BooleanBuilder();
        if ( gogle ) {
            builderGogle.and( venta.articulo.idGenTipo.eq( "G" ) );
        } else {
            builderGogle.and( venta.precioUnitFinal.isNotNull() );
        }

        BooleanBuilder builderOft = new BooleanBuilder();
        if ( oftalmico ) {
            builderOft.and( venta.articulo.idGenTipo.eq( "O" ) );
        } else {
            builderOft.and( venta.precioUnitFinal.isNotNull() );
        }

        BooleanBuilder builder = new BooleanBuilder();
        if ( todo ) {
            builder.and( venta.precioUnitFinal.isNotNull() );
        } else {
            builder.and( venta.precioUnitFinal.isNotNull() );
        }

        Parametro ivaVigenteParam = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
        Impuesto iva = impuestoRepository.findOne( ivaVigenteParam.getValor() );
        Double ivaTasa = iva.getTasa();
        List<FacturasPorEmpleado> lstArticulos = new ArrayList<FacturasPorEmpleado>();
        List<DetalleNotaVenta> lstArticulo = ( List<DetalleNotaVenta> ) detalleNotaVentaRepository.findAll( venta.notaVenta.fechaHoraFactura.between( fechaInicio, fechaFin ).
                and( builder ).and( builderOft ).and( builderGogle ).and( builderArt ).and(venta.precioUnitFinal.ne(BigDecimal.ZERO)).
                and( venta.notaVenta.factura.isNotEmpty()).and( venta.notaVenta.factura.isNotNull()), venta.articulo.id.asc() );
        QModificacion modificacion = QModificacion.modificacion;
        List<Modificacion> lstModificacion = ( List<Modificacion>) modificacionRepository.findAll( modificacion.fecha.between( fechaInicio, fechaFin ) );

        for ( DetalleNotaVenta ventas : lstArticulo ) {
            String art = ventas.getArticulo().getArticulo();
            FacturasPorEmpleado idArt = FindOrCreated( lstArticulos, art );
            idArt.AcumulaArticulos( false, ventas, ivaTasa, "" );
        }

        for( Modificacion mod : lstModificacion ){
            for(DetalleNotaVenta det : mod.getNotaVenta().getDetalles()){
                if( det.getPrecioUnitFinal().compareTo( BigDecimal.ZERO ) > 0 ){
                    Articulo article = articuloRepository.findOne( det.getIdArticulo() );
                    FacturasPorEmpleado art = FindOrCreated( lstArticulos, article.getDescripcion() );
                    art.AcumulaCancelaciones( det, ivaTasa );
                }
            }
        }

        return lstArticulos;
    }

    
    public FacturasPorEmpleado FindOrCreated(  List<FacturasPorEmpleado> lstFacturas,  String art ) {
        FacturasPorEmpleado found = null;

        for ( FacturasPorEmpleado articulos : lstFacturas ) {
            if ( art.equals( articulos.getArticulo() ) ) {
                found = articulos;
                break;
            }
        }
        if ( found == null ) {
            found = new FacturasPorEmpleado( art );
            lstFacturas.add( found );
        }
        return found;
    }

    
    public List<FacturasPorEmpleado> obtenerVentasMarca( Date fechaInicio, Date fechaFin,  String marca, boolean noMostrarArticulos, boolean gogle, boolean oftalmico, boolean todo ) {

        QDetalleNotaVenta venta = QDetalleNotaVenta.detalleNotaVenta;
        log.info( "Verifica que se halla seleccionado un articulo especifico" );

        BooleanBuilder builderArt = new BooleanBuilder();
        if ( !marca.equals( null ) && !marca.isEmpty() && marca.length() > 0 ) {
            builderArt.and( venta.articulo.marca.eq( marca ) );
        } else {
            builderArt.and( venta.precioUnitFinal.isNotNull() );
        }

        BooleanBuilder builderGogle = new BooleanBuilder();
        if ( gogle ) {
            builderGogle.and( venta.articulo.idGenTipo.eq( "G" ) );
        } else {
            builderGogle.and( venta.precioUnitFinal.isNotNull() );
        }

        BooleanBuilder builderOft = new BooleanBuilder();
        if ( oftalmico ) {
            builderOft.and( venta.articulo.idGenTipo.eq( "O" ) );
        } else {
            builderOft.and( venta.precioUnitFinal.isNotNull() );
        }

        BooleanBuilder builder = new BooleanBuilder();
        if ( todo ) {
            builder.and( venta.precioUnitFinal.isNotNull() );
        } else {
            builder.and( venta.precioUnitFinal.isNotNull() );
        }

        Parametro ivaVigenteParam = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
        Impuesto iva = impuestoRepository.findOne( ivaVigenteParam.getValor() );
        Double ivaTasa = ( iva.getTasa() ) / 100;
        List<FacturasPorEmpleado> lstArticulos = new ArrayList<FacturasPorEmpleado>();
        List<DetalleNotaVenta> lstArticulo = ( List<DetalleNotaVenta> ) detalleNotaVentaRepository.findAll( venta.notaVenta.fechaHoraFactura.between( fechaInicio, fechaFin ).
                and( venta.notaVenta.factura.isNotNull() ).and( venta.notaVenta.factura.isNotEmpty() ).and(venta.precioUnitLista.ne(BigDecimal.ZERO)).
                and(venta.notaVenta.sFactura.ne(TAG_CANCELADO)).and( builder ).and( builderOft ).and( builderGogle ).and( builderArt ), venta.articulo.marca.asc() );

        QModificacion modificacion = QModificacion.modificacion;
        List<Modificacion> lstCancelaciones = ( List<Modificacion> ) modificacionRepository.findAll( modificacion.fecha.between(fechaInicio,fechaFin).
                and(modificacion.notaVenta.fechaHoraFactura.notBetween(fechaInicio,fechaFin)));

        for ( DetalleNotaVenta ventas : lstArticulo ) {
            String art = ventas.getArticulo().getMarca();
            String idArticulo = String.format( "[%s] %s", ventas.getIdArticulo(), ventas.getArticulo().getArticulo() );
            if( BigDecimal.ZERO.compareTo(ventas.getPrecioUnitLista()) < 0 ){
                FacturasPorEmpleado idArt = FindorCreated( lstArticulos, art );
                idArt.AcumulaArticulos( noMostrarArticulos, ventas, ivaTasa, idArticulo );
            }
        }

        for( Modificacion mod : lstCancelaciones ){
            if( StringUtils.trimToEmpty(marca).length() > 0 ){
                List<DetalleNotaVenta> lstDetalles = new ArrayList<DetalleNotaVenta>(mod.getNotaVenta().getDetalles());
                Collections.sort(lstDetalles, new Comparator<DetalleNotaVenta>() {
                    @Override
                    public int compare(  DetalleNotaVenta o1,  DetalleNotaVenta o2 ) {
                        return o1.getArticulo().getIdGenTipo().compareTo(o2.getArticulo().getIdGenTipo());
                    }
                });
                for( DetalleNotaVenta ventas : lstDetalles ){
                    if( marca.equalsIgnoreCase(ventas.getArticulo().getMarca()) ){
                        String art = ventas.getArticulo().getMarca();
                        String idArticulo = String.format( "[%s] %s", ventas.getIdArticulo(), ventas.getArticulo().getArticulo() );
                        if( BigDecimal.ZERO.compareTo(ventas.getPrecioUnitLista()) < 0 ){
                            FacturasPorEmpleado idArt = FindorCreated( lstArticulos, art );
                            idArt.AcumulaArticulosCancelados( noMostrarArticulos, ventas, ivaTasa, idArticulo );
                        }
                    }
                }
            } else {
                List<DetalleNotaVenta> lstDetalles = new ArrayList<DetalleNotaVenta>(mod.getNotaVenta().getDetalles());
                Collections.sort(lstDetalles, new Comparator<DetalleNotaVenta>() {
                    @Override
                    public int compare(  DetalleNotaVenta o1,  DetalleNotaVenta o2 ) {
                        return o1.getArticulo().getIdGenTipo().compareTo(o2.getArticulo().getIdGenTipo());
                    }
                });
                for( DetalleNotaVenta ventas : lstDetalles ){
                    String art = ventas.getArticulo().getMarca();
                    String idArticulo = String.format( "[%s] %s", ventas.getIdArticulo(), ventas.getArticulo().getArticulo() );
                    if( BigDecimal.ZERO.compareTo(ventas.getPrecioUnitLista()) < 0 ){
                        FacturasPorEmpleado idArt = FindorCreated( lstArticulos, art );
                        idArt.AcumulaArticulosCancelados( noMostrarArticulos, ventas, ivaTasa, idArticulo );
                    }
                }
            }
        }

        Collections.sort( lstArticulo, new Comparator<DetalleNotaVenta>() {
            @Override
            public int compare( DetalleNotaVenta o1,  DetalleNotaVenta o2) {
                return o1.getIdFactura().compareTo(o2.getIdFactura());
            }
        });
        String idFactura = " ";
        Boolean isNotaCredito = false;
        for( DetalleNotaVenta notaCredito : lstArticulo ) {
            if( (!idFactura.equalsIgnoreCase(notaCredito.getIdFactura())) || (idFactura.equalsIgnoreCase(notaCredito.getIdFactura()) && isNotaCredito) ){
                    for( Pago pago : notaCredito.getNotaVenta().getPagos() ){
                        if( pago.getIdFormaPago().equalsIgnoreCase("NOT") ){
                            String art = notaCredito.getArticulo().getMarca();
                            if( BigDecimal.ZERO.compareTo(notaCredito.getPrecioUnitLista()) < 0 ){
                                FacturasPorEmpleado idArt = FindorCreated( lstArticulos, art );
                                idArt.AcumulaMontoNotasCredito( notaCredito, pago.getMonto(), ivaTasa );
                            }
                            isNotaCredito = true;
                        }
                    }
                }
            for( Pago pago : notaCredito.getNotaVenta().getPagos() ){
                if( pago.getIdFormaPago().equalsIgnoreCase("NOT") ){
                    String art = notaCredito.getArticulo().getMarca();
                    if( BigDecimal.ZERO.compareTo(notaCredito.getPrecioUnitLista()) < 0 ){
                        FacturasPorEmpleado idArt = FindorCreated( lstArticulos, art );
                        idArt.AcumulaArticulosNotasCredito( notaCredito, pago.getMonto(), ivaTasa );
                    }
                }
            }
            idFactura = notaCredito.getIdFactura();
        }

        return lstArticulos;
    }

    
    public FacturasPorEmpleado FindorCreated(  List<FacturasPorEmpleado> lstFacturas, String art ) {
        FacturasPorEmpleado found = null;

        for ( FacturasPorEmpleado articulos : lstFacturas ) {
            if ( articulos.getMarca().equals( art ) ) {
                found = articulos;
                break;
            }
        }
        if ( found == null ) {
            found = new FacturasPorEmpleado( art );
            lstFacturas.add( found );
        }
        return found;
    }

    
    public List<IngresoPorVendedor> obtenerVentasporVendedorporMarca( Date fechaInicio, Date fechaFin,  String marca, boolean mostrarArticulos, boolean gogle, boolean oftalmico, boolean todo ) {
        log.info( "obtenerVentasporVendedor()" );

        log.info( "Se obtiene elvalor del IVA" );
        Parametro ivaVigenteParam = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
        Impuesto iva = impuestoRepository.findOne( ivaVigenteParam.getValor() );
        Double ivaTasa = ( iva.getTasa() ) / 100;

        QDetalleNotaVenta venta = QDetalleNotaVenta.detalleNotaVenta;

        log.info( "Verifica que se halla seleccionado un articulo especifico" );
        BooleanBuilder builderArt = new BooleanBuilder();
        if ( !marca.equals( null ) && !marca.isEmpty() && marca.length() > 0 ) {
            builderArt.and( venta.articulo.marca.eq( marca ) );
        } else {
            builderArt.and( venta.precioUnitFinal.isNotNull() );
        }

        BooleanBuilder builderGogle = new BooleanBuilder();
        if ( gogle ) {
            builderGogle.and( venta.articulo.idGenTipo.eq( "G" ) );
        } else {
            builderGogle.and( venta.precioUnitFinal.isNotNull() );
        }

        BooleanBuilder builderOft = new BooleanBuilder();
        if ( oftalmico ) {
            builderOft.and( venta.articulo.idGenTipo.eq( "O" ) );
        } else {
            builderOft.and( venta.precioUnitFinal.isNotNull() );
        }

        BooleanBuilder builder = new BooleanBuilder();
        if ( todo ) {
            builder.and( venta.precioUnitFinal.isNotNull() );
        } else {
            builder.and( venta.precioUnitFinal.isNotNull() );
        }

        List<IngresoPorVendedor> lstIngresos = new ArrayList<IngresoPorVendedor>();
        List<DetalleNotaVenta> lstVentas = ( List<DetalleNotaVenta> ) detalleNotaVentaRepository.findAll( venta.notaVenta.fechaHoraFactura.between( fechaInicio, fechaFin ).
                and( venta.notaVenta.factura.isNotNull() ).and( venta.notaVenta.factura.isNotEmpty() ).
                and( builder ).and( builderOft ).and( builderGogle ).and( builderArt ).and(venta.precioUnitLista.ne(BigDecimal.ZERO)).
                and(venta.notaVenta.sFactura.ne(TAG_CANCELADO)).and(venta.precioUnitLista.ne(BigDecimal.ZERO)),
                venta.notaVenta.idEmpleado.asc(), venta.articulo.marca.asc() );

        QNotaVenta nv = QNotaVenta.notaVenta;
        List<NotaVenta> lstNotasVentas = ( List<NotaVenta> ) notaVentaRepository.findAll(nv.fechaHoraFactura.between(fechaInicio,fechaFin).
                and(nv.factura.isNotEmpty()).and(nv.factura.isNotNull()).and(nv.sFactura.ne(TAG_CANCELADO)));

        QModificacion modificacion = QModificacion.modificacion;
        List<Modificacion> lstCancelaciones = ( List<Modificacion> ) modificacionRepository.findAll(modificacion.fecha.between(fechaInicio,fechaFin).
                and(modificacion.notaVenta.fechaHoraFactura.notBetween(fechaInicio,fechaFin)));

        for ( DetalleNotaVenta ventas : lstVentas ) {
            if ( ventas.getNotaVenta().getFactura() != null && !ventas.getNotaVenta().getsFactura().equals( "T" ) ) {
                String articulo = String.format( "[%s] %s", ventas.getIdArticulo().toString(), ventas.getArticulo().getArticulo() );
                /*if( articulo.length() > 20 ){
                    articulo = articulo.substring(0, 20);
                }*/
                String idEmpleado = ventas.getNotaVenta().getIdEmpleado();
                IngresoPorVendedor ingreso = FindorCreate( lstIngresos, idEmpleado );
                ingreso.AcumulaPagos( mostrarArticulos, articulo, ventas, ventas.getNotaVenta().getFechaHoraFactura(), ventas.getArticulo().getMarca(), ventas.getPrecioUnitFinal(), ivaTasa, ventas.getArticulo().getIdGenTipo() );
            }
        }

        for( Modificacion mod : lstCancelaciones){
            if( StringUtils.trimToEmpty(marca).length() > 0 ){
                    String idEmpleado = mod.getNotaVenta().getIdEmpleado();
                    IngresoPorVendedor ingreso = FindorCreate( lstIngresos, idEmpleado );
                    for(DetalleNotaVenta ventas : mod.getNotaVenta().getDetalles()){
                        if( marca.equalsIgnoreCase(ventas.getArticulo().getMarca()) && ventas.getPrecioUnitLista().compareTo(BigDecimal.ZERO) > 0 ){
                        String articulo = String.format( "[%s] %s", ventas.getIdArticulo().toString(), ventas.getArticulo().getArticulo() );
                        ingreso.AcumulaPagosCan( ventas, articulo, ventas.getNotaVenta().getFechaHoraFactura(), ventas.getArticulo().getMarca(), ventas.getPrecioUnitFinal(), ivaTasa, ventas.getArticulo().getIdGenTipo() );
                        }
                }
            } else {
                String idEmpleado = mod.getNotaVenta().getIdEmpleado();
                IngresoPorVendedor ingreso = FindorCreate( lstIngresos, idEmpleado );
                for(DetalleNotaVenta ventas : mod.getNotaVenta().getDetalles()){
                    if( ventas.getPrecioUnitLista().compareTo(BigDecimal.ZERO) > 0 ){
                        String articulo = String.format( "[%s] %s", ventas.getIdArticulo().toString(), ventas.getArticulo().getArticulo() );
                        ingreso.AcumulaPagosCan( ventas, articulo, ventas.getNotaVenta().getFechaHoraFactura(), ventas.getArticulo().getMarca(), ventas.getPrecioUnitFinal(), ivaTasa, ventas.getArticulo().getIdGenTipo() );
                    }
                }
            }
        }

        Boolean isNotaCredito = false;
        String idFactura = " ";
        for( DetalleNotaVenta notaVenta : lstVentas ){
            if( StringUtils.trimToEmpty(marca).length() > 0 ){
                for( Pago pago : notaVenta.getNotaVenta().getPagos() ){
                    if( pago.getIdFPago().equalsIgnoreCase("NOT") ){
                            if( (!idFactura.equalsIgnoreCase(notaVenta.getIdFactura())) || (idFactura.equalsIgnoreCase(notaVenta.getIdFactura()) && isNotaCredito ) ){
                                if( marca.equalsIgnoreCase(notaVenta.getArticulo().getMarca()) ){
                                    String idEmpleado = pago.getNotaVenta().getIdEmpleado();
                                    IngresoPorVendedor ingreso = FindorCreate( lstIngresos, idEmpleado );
                                    Integer cantArticulos = notaVenta.getCantidadFac().intValue();
                                    ingreso.AcumulaPagosNotaCredito( cantArticulos, notaVenta, notaVenta.getNotaVenta().getFechaHoraFactura(), notaVenta.getArticulo().getMarca(), pago.getMonto(), ivaTasa, notaVenta.getArticulo().getIdGenTipo() );
                                }
                            }
                            if( marca.equalsIgnoreCase(notaVenta.getArticulo().getMarca()) ){
                                String idEmpleado = pago.getNotaVenta().getIdEmpleado();
                                IngresoPorVendedor ingreso = FindorCreate( lstIngresos, idEmpleado );
                                Integer cantArticulos = notaVenta.getCantidadFac().intValue();
                                ingreso.AcumulaArticulosNotaCredito( cantArticulos, notaVenta, notaVenta.getNotaVenta().getFechaHoraFactura(), notaVenta.getArticulo().getMarca(), pago.getMonto(), ivaTasa, notaVenta.getArticulo().getIdGenTipo() );
                            }
                            idFactura = pago.getIdFactura();
                            isNotaCredito = true;
                    }
                }
            } else {
                for( Pago pago : notaVenta.getNotaVenta().getPagos() ){
                    if( pago.getIdFPago().equalsIgnoreCase("NOT") ){
                            if( !idFactura.equalsIgnoreCase(notaVenta.getIdFactura()) || (idFactura.equalsIgnoreCase(notaVenta.getIdFactura()) && isNotaCredito) ){
                                String idEmpleado = pago.getNotaVenta().getIdEmpleado();
                                IngresoPorVendedor ingreso = FindorCreate( lstIngresos, idEmpleado );
                                Integer cantArticulos = notaVenta.getCantidadFac().intValue();
                                ingreso.AcumulaPagosNotaCredito( cantArticulos, notaVenta, notaVenta.getNotaVenta().getFechaHoraFactura(), notaVenta.getArticulo().getMarca(), pago.getMonto(), ivaTasa, notaVenta.getArticulo().getIdGenTipo() );
                            }
                            String idEmpleado = pago.getNotaVenta().getIdEmpleado();
                            IngresoPorVendedor ingreso = FindorCreate( lstIngresos, idEmpleado );
                            Integer cantArticulos = notaVenta.getCantidadFac().intValue();
                            ingreso.AcumulaArticulosNotaCredito( cantArticulos, notaVenta, notaVenta.getNotaVenta().getFechaHoraFactura(), notaVenta.getArticulo().getMarca(), pago.getMonto(), ivaTasa, notaVenta.getArticulo().getIdGenTipo() );
                            idFactura = pago.getIdFactura();
                            isNotaCredito = true;
                    }
                }
            }

        }

        for(IngresoPorVendedor ingreso : lstIngresos){
            Collections.sort( ingreso.getPagos(), new Comparator<IngresoPorFactura>() {
                @Override
                public int compare( IngresoPorFactura o1,  IngresoPorFactura o2) {
                    return o1.getFechaPago().compareTo(o2.getFechaPago());
                }
            });
        }
        return lstIngresos;
    }

    
    public List<Articulo> obtenerExistenciasporMarcaDetallado(  String marca, boolean gogle, boolean oftalmico, boolean todo ) {
        log.info( "obtenerVentasporVendedor()" );


        QArticulo articulo = QArticulo.articulo1;

        log.info( "Verifica que se halla seleccionado un articulo especifico" );
        BooleanBuilder builderArt = new BooleanBuilder();
        if ( !marca.equals( null ) && !marca.isEmpty() && marca.length() > 0 ) {
            builderArt.and( articulo.idGenSubtipo.equalsIgnoreCase( marca ).or( articulo.articulo.equalsIgnoreCase( marca ) ) );
        } else {
            builderArt.and( articulo.id.isNotNull() );
        }

        BooleanBuilder builderGogle = new BooleanBuilder();
        if ( gogle ) {
            builderGogle.and( articulo.idGenTipo.eq( "G" ) );
        } else {
            builderGogle.and( articulo.id.isNotNull() );
        }

        BooleanBuilder builderOft = new BooleanBuilder();
        if ( oftalmico ) {
            builderOft.and( articulo.idGenTipo.eq( "O" ) );
        } else {
            builderOft.and( articulo.id.isNotNull() );
        }

        BooleanBuilder builder = new BooleanBuilder();
        if ( todo ) {
            builder.and( articulo.id.isNotNull() );
        } else {
            builder.and( articulo.id.isNotNull() );
        }

        List<Articulo> lstArticulos = new ArrayList<Articulo>();
        List<Articulo> lstArticulo = ( List<Articulo> ) articuloRepository.findAll( articulo.cantExistencia.isNotNull().and( articulo.cantExistencia.ne( 0 ) ).
                and( builder ).and( builderOft ).and( builderGogle ).and( builderArt ).and( articulo.idGenerico.equalsIgnoreCase( "A" ) ),
                articulo.idGenTipo.asc() );
        log.info( "tamañoLista:{}", lstArticulos.size() );

        for ( Articulo articulos : lstArticulo ) {
            if ( articulos.getCantExistencia() > 0 || articulos.getCantExistencia() < 0 ) {
                lstArticulos.add( articulos );
            }
        }

        return lstArticulos;
    }

    
    public List<FacturasPorEmpleado> obtenerExistenciasporMarcaCompleto( String marca, boolean gogle, boolean oftalmico, boolean todo ) {
        log.info( "obtenerVentasporVendedor()" );

        QArticulo articulo = QArticulo.articulo1;
        log.info( "Verifica que se halla seleccionado un articulo especifico" );
        BooleanBuilder builderArt = new BooleanBuilder();
        if ( !StringUtils.trimToEmpty( marca ).isEmpty() ) {
            builderArt.and( articulo.marca.like( marca + "%" ) );
        } else {
            builderArt.and( articulo.id.isNotNull() );
        }

        BooleanBuilder builderGogle = new BooleanBuilder();
        if ( gogle ) {
            builderGogle.and( articulo.idGenTipo.eq( "G" ) );
        } else {
            builderGogle.and( articulo.id.isNotNull() );
        }

        BooleanBuilder builderOft = new BooleanBuilder();
        if ( oftalmico ) {
            builderOft.and( articulo.idGenTipo.eq( "O" ) );
        } else {
            builderOft.and( articulo.id.isNotNull() );
        }

        BooleanBuilder builder = new BooleanBuilder();
        if ( todo ) {
            builder.and( articulo.id.isNotNull() );
        } else {
            builder.and( articulo.id.isNotNull() );
        }

        List<FacturasPorEmpleado> lstArticulos = new ArrayList<FacturasPorEmpleado>();
        List<Articulo> lstArticulo = new ArrayList<Articulo>();
        List<Articulo> lstArt = ( List<Articulo> ) articuloRepository.findAll( articulo.cantExistencia.isNotNull().
                and( builder ).and( builderOft ).and( builderGogle ).and( builderArt ), articulo.marca.asc() );

        for ( Articulo artic : lstArt ) {
            if ( artic.getCantExistencia() > 0 ) {
                if( artic.getDescripcion().length() >= 60 ){
                    artic.setDescripcion( artic.getDescripcion().substring(0,60));
                }
                lstArticulo.add( artic );
            }
        }

        for ( Articulo articulos : lstArticulo ) {
            String linea = articulos.getMarca();
            Precio precio = new Precio();
            precio.setPrecio( BigDecimal.ZERO );
            QPrecio price = QPrecio.precio1;
            List<Precio> precios = (List<Precio>) precioRepository.findAll(price.articulo.trim().eq(articulos.getArticulo()));
            if(precios.size() == 1){
              precio = precios.get(0);
            }
            FacturasPorEmpleado facturas = FindOorCreate( lstArticulos, linea );
            facturas.AcumulaMarcas( articulos.getMarca(), articulos, precio );
            Collections.sort( facturas.getFacturasVendedor(), new Comparator<IngresoPorFactura>() {
                @Override
                public int compare( IngresoPorFactura o1,  IngresoPorFactura o2) {
                    return o1.getMarca().compareTo(o2.getMarca());
                }
            } );
        }

        return lstArticulos;
    }


    
    public List<FacturasPorEmpleado> obtenerExistenciasporMarcaResumido( String marca, boolean gogle, boolean oftalmico, boolean todo ) {
        log.info( "obtenerVentasporVendedor()" );

        QArticulo articulo = QArticulo.articulo1;
        log.info( "Verifica que se halla seleccionado un articulo especifico" );
        BooleanBuilder builderArt = new BooleanBuilder();
        if ( !StringUtils.trimToEmpty( marca ).isEmpty() ) {
            builderArt.and( articulo.marca.like( marca + "%" ) );
        } else {
            builderArt.and( articulo.id.isNotNull() );
        }

        BooleanBuilder builderGogle = new BooleanBuilder();
        if ( gogle ) {
            builderGogle.and( articulo.idGenTipo.eq( "G" ) );
        } else {
            builderGogle.and( articulo.id.isNotNull() );
        }

        BooleanBuilder builderOft = new BooleanBuilder();
        if ( oftalmico ) {
            builderOft.and( articulo.idGenTipo.eq( "O" ) );
        } else {
            builderOft.and( articulo.id.isNotNull() );
        }

        BooleanBuilder builder = new BooleanBuilder();
        if ( todo ) {
            builder.and( articulo.id.isNotNull() );
        } else {
            builder.and( articulo.id.isNotNull() );
        }

        List<FacturasPorEmpleado> lstArticulos = new ArrayList<FacturasPorEmpleado>();
        List<Articulo> lstArticulo = new ArrayList<Articulo>();
        List<Articulo> lstArt = ( List<Articulo> ) articuloRepository.findAll( articulo.cantExistencia.isNotNull().
                and( builder ).and( builderOft ).and( builderGogle ).and( builderArt ), articulo.idGenerico.asc() );

        for ( Articulo artic : lstArt ) {
            if ( artic.getCantExistencia() > 0 || artic.getCantExistencia() < 0 ) {
                if( artic.getDescripcion().length() >= 60 ){
                    artic.setDescripcion( artic.getDescripcion().substring(0,60));
                }
                lstArticulo.add( artic );
            }
        }

        for ( Articulo articulos : lstArticulo ) {
            String linea = articulos.getIdGenerico();
            FacturasPorEmpleado facturas = FindOorCreate( lstArticulos, linea );
            facturas.AcumulaMarcasResumido( articulos.getMarca(), articulos );
            Collections.sort( facturas.getFacturasVendedor(), new Comparator<IngresoPorFactura>() {
                @Override
                public int compare( IngresoPorFactura o1,  IngresoPorFactura o2) {
                    return o1.getMarca().compareTo(o2.getMarca());
                }
            } );
        }

        return lstArticulos;
    }

    
    public FacturasPorEmpleado FindOorCreate(  List<FacturasPorEmpleado> lstFacturas, String idEmpleado ) {
        FacturasPorEmpleado found = null;
        for ( FacturasPorEmpleado facturas : lstFacturas ) {
            if ( facturas.getIdEmpleado().equals( idEmpleado ) ) {
                found = facturas;
                break;
            }
        }
        if ( found == null ) {
            found = new FacturasPorEmpleado( idEmpleado );
            lstFacturas.add( found );
        }
        return found;
    }

    
    public List<Articulo> obtenerExistenciasporArticulo( String marca, String descripcion, String color ) {
        log.info( "obtenerVentasporVendedor()" );

        QArticulo articulo = QArticulo.articulo1;
        log.info( "Verifica que se halla seleccionado un articulo especifico" );
        BooleanBuilder builderArt = new BooleanBuilder();
        if ( !StringUtils.trimToEmpty( marca ).isEmpty() ) {
            builderArt.and( articulo.articulo.like( marca + "%" ) );
        } else {
            builderArt.and( articulo.id.isNotNull() );
        }

        BooleanBuilder builderCol = new BooleanBuilder();
        if ( !StringUtils.trimToEmpty( color ).isEmpty() ) {
            builderCol.and( articulo.codigoColor.like( color + "%" ) );
        } else {
            builderCol.and( articulo.id.isNotNull() );
        }

        List<Articulo> lstArticulos = new ArrayList<Articulo>();
        List<Articulo> lstArticulo = ( List<Articulo> ) articuloRepository.findAll( articulo.cantExistencia.isNotNull().
                and( builderArt ).and( builderCol ), articulo.id.asc() );
        log.info( "tamañoLista:{}", lstArticulos.size() );

        for ( Articulo articulos : lstArticulo ) {
            if ( articulos.getCantExistencia() > 0 || articulos.getCantExistencia() < 0 ) {
                QPrecio price = QPrecio.precio1;
                List <Precio> precio = (List<Precio>) precioRepository.findAll(price.articulo.trim().eq(articulos.getArticulo()));
                if(precio.size() == 1){
                  articulos.setPrecio( precio.get(0).getPrecio() );
                }
                if( articulos.getDescripcion().length() >= 55 ){
                    articulos.setDescripcion( articulos.getDescripcion().substring(0,50) );
                }
                lstArticulos.add( articulos );
            }
        }

        String desc = StringUtils.trimToEmpty( descripcion ).toUpperCase();
        if( desc.length() > 0 ){
            List<Articulo> articulos = new ArrayList<Articulo>();
            articulos.addAll( lstArticulos );
            lstArticulos.clear();
            for( Articulo art : articulos ){
                if( art.getDescripcion().toUpperCase().contains( desc ) ) {
                    if( art.getDescripcion().length() >= 55 ){
                        art.setDescripcion( art.getDescripcion().substring(0,50));
                    }
                    lstArticulos.add( art );
                }
            }
        }

        Collections.sort( lstArticulos, new Comparator<Articulo>() {
            @Override
            public int compare( Articulo o1,  Articulo o2) {
                return o1.getArticulo().compareTo(o2.getArticulo());
            }
        });

        return lstArticulos;
    }

    
    public List<SaldoPorEstado> obtenerTrabajos( boolean retenidos, boolean porEnviar, boolean pino, boolean sucursal, boolean todos, boolean factura, boolean fechaPromesa ) {

        QTrabajo trabajo = QTrabajo.trabajo;
        BooleanBuilder builderRet = new BooleanBuilder();
        if ( retenidos ) {
            builderRet.and( trabajo.estado.equalsIgnoreCase( "RTN" ) );
        } else {
            builderRet.and( trabajo.estado.isNotNull() ).and( trabajo.estado.isNotEmpty() );
        }

        BooleanBuilder builderPorEnv = new BooleanBuilder();
        if ( porEnviar ) {
            builderPorEnv.and( trabajo.estado.equalsIgnoreCase( "PE" ).or( trabajo.estado.equalsIgnoreCase( "RPE" ) ).
                    or( trabajo.estado.equalsIgnoreCase( "X1" ) ) );
        } else {
            builderPorEnv.and( trabajo.estado.isNotNull() ).and( trabajo.estado.isNotEmpty() );
        }

        BooleanBuilder builderPino = new BooleanBuilder();
        if ( pino ) {
            builderPino.and( trabajo.estado.equalsIgnoreCase( "EP" ).or( trabajo.estado.equalsIgnoreCase( "REP" ) ) );
        } else {
            builderPino.and( trabajo.estado.isNotNull() ).and( trabajo.estado.isNotEmpty() );
        }

        BooleanBuilder builderSuc = new BooleanBuilder();
        if ( sucursal ) {
            builderSuc.and( trabajo.estado.equalsIgnoreCase( "RS" ).or( trabajo.estado.equalsIgnoreCase( "X3" ) ) );
        } else {
            builderSuc.and( trabajo.estado.isNotNull() ).and( trabajo.estado.isNotEmpty() );
        }

        BooleanBuilder builderTodo = new BooleanBuilder();
        if ( todos ) {
            builderTodo.and( trabajo.estado.isNotNull() ).and( trabajo.estado.isNotEmpty().and( trabajo.estado.ne( "TE" ).and( trabajo.estado.ne( "CN" ) ) ) );
        } else {
            builderTodo.and( trabajo.estado.isNotNull() ).and( trabajo.estado.isNotEmpty().and( trabajo.estado.ne( "TE" ).and( trabajo.estado.ne( "CN" ) ) ) );
        }

        OrderSpecifier<String> fact = null;
        if ( factura ) {
            fact = trabajo.id.asc();
        } else {
            fact = trabajo.id.desc();
        }

        OrderSpecifier<Date> fechProm = null;
        if ( fechaPromesa ) {
            fechProm = trabajo.fechaPromesa.asc();
        } else {
            fechProm = trabajo.fechaPromesa.desc();
        }

        List<SaldoPorEstado> lstTrabajos = new ArrayList<SaldoPorEstado>();
        List<Trabajo> lstTrabajo = ( List<Trabajo> ) trabajoRepository.findAll( trabajo.jbTipo.ne( "GRUPO" ).and( builderTodo ).and( builderSuc ).and( builderPino ).
                and( builderRet ).and( builderPorEnv ).and( trabajo.estado.ne( "BD" ) ), fact, fechProm, trabajo.jbTipo.desc() );
        log.info( "tamañoLista:{}", lstTrabajo.size() );
        for ( Trabajo trabajos : lstTrabajo ) {
            List<NotaVenta> lstNotas = notaVentaRepository.findByFactura( trabajos.getId() );
            NotaVenta notaVenta = lstNotas.size() > 0 ? lstNotas.get(0) : null;
            String estado = trabajos.getTrabajoEstado().getDescr();
            SaldoPorEstado saldo = FindoorCreate( lstTrabajos, estado );
            saldo.AcumulaSaldos( trabajos, notaVenta );
        }

        return lstTrabajos;
    }

    
    public SaldoPorEstado FindoorCreate(  List<SaldoPorEstado> lstSaldos, String estado ) {

        SaldoPorEstado found = null;

        for ( SaldoPorEstado saldos : lstSaldos ) {

            if ( saldos.getEstado().equals( estado ) ) {

                found = saldos;
                break;
            }
        }
        if ( found == null ) {
            found = new SaldoPorEstado( estado );
            lstSaldos.add( found );
        }
        return found;
    }

    
    public List<TrabajoTrack> obtenerTrabajosporEntregar( Date fechaInicio, Date fechaFin ) {

        QTrabajoTrack trabajo = QTrabajoTrack.trabajoTrack;
        List<TrabajoTrack> lstTrabajos = ( List<TrabajoTrack> ) trabajoTrackRepository.findAll( trabajo.fecha.between( fechaInicio, fechaFin ).
                and( trabajo.estado.equalsIgnoreCase( "TE" ) ).and( trabajo.trabajo.estado.equalsIgnoreCase( "TE" ) ) );
        log.info( "tamañoListas:{}", lstTrabajos.size() );

        for(TrabajoTrack tk : lstTrabajos){
          List<NotaVenta> lstNota = notaVentaRepository.findByFactura( tk.getId() );
          String articulos = "";
          for(NotaVenta nota : lstNota){
            for(DetalleNotaVenta det : nota.getDetalles()){
              String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
              articulos = articulos+","+det.getArticulo().getArticulo()+color;
            }
          }
          articulos = articulos.replaceFirst( ",","" );
          tk.getTrabajo().setMaterial( articulos.length() > 0 ? articulos : tk.getTrabajo().getMaterial() );
        }

        return lstTrabajos;
    }

    
    public List<SaldoPorEstado> obtenerTrabajosporEntregarporEmpleado( Date fechaInicio, Date fechaFin ) {

        QTrabajoTrack trabajo = QTrabajoTrack.trabajoTrack;
        List<SaldoPorEstado> lstTrabajos = new ArrayList<SaldoPorEstado>();
        List<TrabajoTrack> lstTrabajo = ( List<TrabajoTrack> ) trabajoTrackRepository.findAll( trabajo.fecha.between( fechaInicio, fechaFin ).
                and( trabajo.estado.equalsIgnoreCase( "TE" ) ).and( trabajo.trabajo.estado.equalsIgnoreCase( "TE" ) ), trabajo.fecha.asc() );
        log.info( "tamañoListas:{}", lstTrabajo.size() );

        for ( TrabajoTrack trabajos : lstTrabajo ) {
            List<NotaVenta> lstNotas = notaVentaRepository.findByFactura( trabajos.getId() );
            NotaVenta notaVenta = lstNotas.size() > 0 ? lstNotas.get(0) : null;
            String empleado = trabajos.getTrabajo().getEmpAtendio();
            SaldoPorEstado saldo = FindoOrCreate( lstTrabajos, empleado );
            saldo.AcumulaTrabajos( trabajos, lstTrabajo.size(), notaVenta );
        }

        return lstTrabajos;
    }

    
    public SaldoPorEstado FindoOrCreate(  List<SaldoPorEstado> lstSaldos,  String empleado ) {

        SaldoPorEstado found = null;

        for ( SaldoPorEstado saldos : lstSaldos ) {

            if ( saldos.getIdEmpleado().equals( empleado ) ) {
                found = saldos;
                break;
            }
        }
        if ( found == null ) {
            found = new SaldoPorEstado( empleado );
            Empleado empleados = empleadoRepository.findOne( empleado );
            if ( empleado != null ) {
                found.setNomEmpleado( empleados.nombreCompleto() );
            }
            lstSaldos.add( found );
        }
        return found;
    }

    
    public List<IngresoPorDia> obtenerVentasporDia( Date fechaInicio, Date fechaFin ) {

        Parametro ivaVigenteParam = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
        Impuesto iva = impuestoRepository.findOne( ivaVigenteParam.getValor() );
        Double ivaTasa = iva.getTasa();

        List<IngresoPorDia> lstVentas = new ArrayList<IngresoPorDia>();
        QNotaVenta ventas = QNotaVenta.notaVenta;
        List<NotaVenta> lstVenta = ( List<NotaVenta> ) notaVentaRepository.findAll( ventas.fechaHoraFactura.between( fechaInicio, fechaFin ).and( ventas.factura.isNotNull() ),
                ventas.fechaHoraFactura.asc() );
        QModificacion mod = QModificacion.modificacion;
        List<Modificacion> lstCancelaciones = ( List<Modificacion>) modificacionRepository.findAll( mod.fecha.between(fechaInicio,fechaFin).
                and(mod.notaVenta.fechaHoraFactura.notBetween(fechaInicio,fechaFin)));

        for ( NotaVenta venta : lstVenta ) {
            IngresoPorDia ingreso = FindOrCreate( lstVentas, venta.getFechaHoraFactura() );
            ingreso.AcumulaMonto( venta.getVentaTotal(), ivaTasa );
        }

        return lstVentas;
    }

    
    public List<DescuentosPorTipo> obtenerDescuentosporTipo( Date fechaInicio, Date fechaFin ) {

        List<DescuentosPorTipo> lstDescuentos = new ArrayList<DescuentosPorTipo>();
        QDescuento descuento = QDescuento.descuento;
        List<Descuento> lstDescuento = ( List<Descuento> ) descuentoRepository.findAll( descuento.fecha.between( fechaInicio, fechaFin ) );
        Integer noDesc = lstDescuento.size();
        for ( Descuento descuentos : lstDescuento ) {
            DescuentosPorTipo desc = FindOoorCreate( lstDescuentos, descuentos.getTipoClave() );
            desc.AcumulaDescuentos( descuentos, noDesc );
        }

        return lstDescuentos;
    }

    
    public DescuentosPorTipo FindOoorCreate(  List<DescuentosPorTipo> lstDescuentos, String tipo ) {
        DescuentosPorTipo found = null;
        for ( DescuentosPorTipo desc : lstDescuentos ) {
            if ( desc.getTipo().equalsIgnoreCase( tipo ) ) {
                found = desc;
                break;
            }
        }
        if ( found == null ) {
            found = new DescuentosPorTipo( tipo );
            lstDescuentos.add( found );
        }
        return found;
    }

    
    public List<DescuentosPorTipo> obtenerPagosporTipo( Date fechaInicio, Date fechaFin,  String formaPago,  String factura ) {

        List<DescuentosPorTipo> lstPagos = new ArrayList<DescuentosPorTipo>();
        QPago pago = QPago.pago;

        BooleanBuilder builderformaPago = new BooleanBuilder();
        if ( !formaPago.equals( null ) && !formaPago.isEmpty() && formaPago.length() > 0 ) {
            builderformaPago.and( pago.idFormaPago.equalsIgnoreCase( formaPago ) );
        } else {
            builderformaPago.and( pago.idFactura.isNotNull() );
        }

        BooleanBuilder builderFactura = new BooleanBuilder();
        if ( !factura.equals( null ) && !factura.isEmpty() && factura.length() > 0 ) {
            builderFactura.and( pago.notaVenta.factura.equalsIgnoreCase( factura ) );
        } else {
            builderFactura.and( pago.notaVenta.factura.isNotNull().and( pago.notaVenta.factura.isNotEmpty() ) );
        }

        List<Pago> lstPago = ( List<Pago> ) pagoRepository.findAll( pago.fecha.between( fechaInicio, fechaFin ).
                and( builderformaPago ).and( builderFactura ) );
        QBancoEmisor banco = QBancoEmisor.bancoEmisor;
        for ( Pago pagos : lstPago ) {
            String descPago = tipoPagoRepository.findOne( pagos.getIdFPago() ).getDescripcion();
            BancoEmisor bancos = new BancoEmisor();
            Boolean esPagoDolares = Registry.isCardPaymentInDollars(pagos.geteTipoPago().getId());
            if ( !StringUtils.trimToEmpty(pagos.getIdBancoEmisor()).equalsIgnoreCase("") ) {
                Boolean idBancNum = true;
                Integer idBanco = 0;
                try{
                 idBanco = Integer.parseInt( pagos.getIdBancoEmisor() );
                } catch ( Exception e ) {
                    idBancNum = false;
                }
                if( idBancNum ){
                bancos = ( BancoEmisor ) bancoEmisorRepository.findOne( idBanco );
                }
                DescuentosPorTipo desc = FindeOrCreate( lstPagos, pagos.getIdFPago() );
                desc.AcumulaTipoPagos( pagos, bancos, descPago, esPagoDolares );
                Collections.sort( desc.getDescuentos(), new Comparator<TipoDescuento>() {
                    @Override
                    public int compare(  TipoDescuento o1,  TipoDescuento o2 ) {
                        return o1.getFecha().compareTo(o2.getFecha());
                    }
                });
            } else {
                DescuentosPorTipo desc = FindeOrCreate( lstPagos, pagos.getIdFPago() );
                desc.AcumulaTipoPagos( pagos, bancos, descPago, esPagoDolares );
                Collections.sort( desc.getDescuentos(), new Comparator<TipoDescuento>() {
                    @Override
                    public int compare(  TipoDescuento o1,  TipoDescuento o2 ) {
                        return o1.getFecha().compareTo(o2.getFecha());
                    }
                });
            }
        }

        return lstPagos;
    }

    
    public DescuentosPorTipo FindeOrCreate(  List<DescuentosPorTipo> lstPagos, String tipoPago ) {
        DescuentosPorTipo found = null;

        for ( DescuentosPorTipo tipo : lstPagos ) {
            if ( tipo.getTipo().equalsIgnoreCase( tipoPago ) ) {
                found = tipo;
                break;
            }
        }
        if ( found == null ) {
            found = new DescuentosPorTipo( tipoPago );
            lstPagos.add( found );
        }
        return found;
    }

    
    public List<DescuentosPorTipo> obtenerExamenesporEmpleado( Date fechaInicio, Date fechaFin ) {

        List<DescuentosPorTipo> lstExamenes = new ArrayList<DescuentosPorTipo>();
        QReceta rx = QReceta.receta;
        List<Receta> lstRecetas = (List<Receta>)recetaRepository.findAll( rx.fechaReceta.between(fechaInicio,fechaFin),
                rx.idCliente.asc(), rx.fechaReceta.asc() );
        Integer total = lstRecetas.size();
        for ( Receta receta : lstRecetas ) {
            Examen examen = examenRepository.findOne( receta.getExamen() );
            if( examen != null ){
              String idEmpleado = receta.getIdOptometrista();
              DescuentosPorTipo desc = EncontraroCrear( lstExamenes, idEmpleado );
              desc.AcumulaEmpleados( examen, total, receta );
            }
        }

        return lstExamenes;
    }

    
    public DescuentosPorTipo EncontraroCrear(  List<DescuentosPorTipo> lstExamenes, String idEmpleado ) {
        DescuentosPorTipo found = null;

        for ( DescuentosPorTipo tipo : lstExamenes ) {
            if ( tipo.getIdEmpleado().equals( idEmpleado ) ) {
                found = tipo;
                break;
            }
        }
        if ( found == null ) {
            found = new DescuentosPorTipo( idEmpleado );
            Empleado empleado = empleadoRepository.findOne( idEmpleado );
            if ( empleado != null ) {
                found.setNombreEmpleado( empleado.nombreCompleto() );
            }
            lstExamenes.add( found );
        }
        return found;
    }


    
    public List<IngresoPorVendedor> obtenerVentasporOptometristaCompleto( Date fechaInicio, Date fechaFin ) {
        String pagosNoTransf = Registry.getPaymentsNoRefound();
        Parametro ivaVigenteParam = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
        Impuesto iva = impuestoRepository.findOne( ivaVigenteParam.getValor() );
        Double ivaTasa = iva.getTasa() / 100;
        List<IngresoPorVendedor> lstVentas = new ArrayList<IngresoPorVendedor>();
        List<IngresoPorVendedor> lstVentasCan = new ArrayList<IngresoPorVendedor>();

        QNotaVenta venta = QNotaVenta.notaVenta;
        List<NotaVenta> lstVenta = ( List<NotaVenta> ) notaVentaRepository.findAll( venta.fechaHoraFactura.between( fechaInicio, fechaFin ).
                and( venta.receta.isNotNull() ).and( venta.factura.isNotEmpty() ).
                and( venta.factura.isNotNull() ), venta.idEmpleado.asc() );

        QPago payment = QPago.pago;
        List<Pago> lstPagosBod = (List<Pago>) pagoRepository.findAll(payment.fecha.between(fechaInicio,fechaFin).
                and(payment.idFPago.eq(TAG_BODEGA)));

        QModificacion modificacion = QModificacion.modificacion;
        List<Modificacion> lstCancelaciones = (List<Modificacion>) modificacionRepository.findAll(modificacion.tipo.eq("can").
                and(modificacion.fecha.between(fechaInicio, fechaFin)).
                and(modificacion.notaVenta.receta.isNotNull()).
                and(modificacion.notaVenta.factura.isNotNull()).and(modificacion.notaVenta.factura.isNotEmpty()));


        BigDecimal montoTotal = BigDecimal.ZERO;
        Integer totalFacturas = lstVenta.size();
        for ( NotaVenta ventas : lstVenta ) {
            montoTotal = montoTotal.add( ventas.getVentaNeta() );
            if( ventas.getRx() != null && !ventas.getIdEmpleado().trim().equalsIgnoreCase(ventas.getRx().getIdOptometrista().trim()) ){
              String idEmpleado = ventas.getRx().getIdOptometrista();
              IngresoPorVendedor ingresos = FindorCreate( lstVentas, idEmpleado );
              ingresos.AcumulaOptometrista( ventas, montoTotal, totalFacturas, ivaTasa, pagosNoTransf );
            }
        }

        for ( Modificacion mod : lstCancelaciones ) {
            String idEmpleado = mod.getNotaVenta().getRx().getIdOptometrista();
            if( mod.getNotaVenta().getRx() != null && !mod.getNotaVenta().getIdEmpleado().trim().equalsIgnoreCase(mod.getNotaVenta().getRx().getIdOptometrista().trim()) ){
                IngresoPorVendedor ingresos = FindorCreate( lstVentasCan, idEmpleado );
                ingresos.AcumulaCanOptometrista( mod.getNotaVenta(), totalFacturas, ivaTasa, pagosNoTransf );
            }
        }

        for(Pago pago : lstPagosBod){
          NotaVenta nota = notaVentaRepository.findOne( pago.getIdFactura() );
          if( nota != null ){
            montoTotal = montoTotal.subtract( pago.getMonto() );
            if( nota.getRx() != null && !nota.getIdEmpleado().trim().equalsIgnoreCase(nota.getRx().getIdOptometrista().trim()) ){
                  String idEmpleado = nota.getRx().getIdOptometrista();
                  IngresoPorVendedor ingresos = FindorCreate( lstVentas, idEmpleado );
                  ingresos.AcumulaOptometristaBod( nota, montoTotal, totalFacturas, ivaTasa, pagosNoTransf );
            }
          }
        }

        for(IngresoPorVendedor inCan : lstVentasCan){
            for(IngresoPorVendedor in : lstVentas){
                if(in.getIdEmpleado().equalsIgnoreCase(inCan.getIdEmpleado())){
                    in.getPagos().addAll(inCan.getPagos());
                }
            }
        }

        for(IngresoPorVendedor ing : lstVentas){
          Collections.sort( ing.getPagos(), new Comparator<IngresoPorFactura>() {
              @Override
              public int compare(IngresoPorFactura o1, IngresoPorFactura o2) {
                  return o1.getIdFactura().compareTo(o2.getIdFactura());
              }
          } );
        }

        return lstVentas;
    }

    
    public IngresoPorVendedor FindorCreatePrimera(  List<IngresoPorVendedor> lstIngresos, String idEmpleado ) {
        IngresoPorVendedor found = null;
        for ( IngresoPorVendedor ingresos : lstIngresos ) {
            if ( ingresos.getIdEmpleado().equals( idEmpleado ) ) {
                found = new IngresoPorVendedor( idEmpleado );
                break;
            }
        }
        if ( found == null ) {
            found = new IngresoPorVendedor( idEmpleado );
            Empleado empleado = empleadoRepository.findOne( idEmpleado );
            if ( empleado != null ) {
                found.setNombre( empleado.nombreCompleto() );
            }
            lstIngresos.add( found );
        }
        return found;
    }


    
    public IngresoPorVendedor FindorCreateMayor(  List<IngresoPorVendedor> lstIngresos, String idEmpleado ) {
        IngresoPorVendedor found = null;
        for ( IngresoPorVendedor ingresos : lstIngresos ) {
            if ( ingresos.getIdEmpleado().equals( idEmpleado ) ) {
                found = ingresos;
                break;
            }
        }
        if ( found == null ) {
            found = new IngresoPorVendedor( idEmpleado );
            Empleado empleado = empleadoRepository.findOne( idEmpleado );
            if ( empleado != null ) {
                found.setNombre( empleado.nombreCompleto() );
            }
            lstIngresos.add( found );
        }
        return found;
    }


    
    public List<Promocion> obtenerPromociones( Date fechaImpresion ) {

        Date fechaInicio;
        Date fechaFin;

        List<Promocion> lstPromociones = new ArrayList<Promocion>();
        List<Promocion> lstPromo = promocionRepository.findAll();

        for ( Promocion prom : lstPromo ) {
            fechaInicio = DateUtils.addDays( prom.getVigenciaIni(), -7 );
            fechaFin = DateUtils.addDays( prom.getVigenciaFin(), 7 );

            if ( fechaInicio.compareTo( fechaImpresion ) <= 0 && fechaFin.compareTo( fechaImpresion ) >= 0 ) {
                lstPromociones.add( prom );
            }
        }

        return lstPromociones;
    }


    
    public List<KardexPorArticulo> obtenerKardex(  String article, Date fechaInicio, Date fechaFin ){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        QTransInv transInv = QTransInv.transInv;
        QTransInvDetalle transInvDet = QTransInvDetalle.transInvDetalle;
        List<TransInvDetalle> lstMovimientos = new ArrayList<TransInvDetalle>();
        List<TransInv> lstTransInvDate = new ArrayList<TransInv>();
        Date fechaInicial = new Date();
        try{
          fechaInicial = df.parse("2012-12-01");
        } catch (ParseException e ){
          System.out.println( e );
        }

        List<TransInv> lstTransTotal = ( List<TransInv> ) transInvRepository.findAll( transInv.fecha.between( fechaInicial, new Date() ), transInv.fechaMod.desc() );
        List<KardexPorArticulo> lstKardezSku = new ArrayList<KardexPorArticulo>();
        for(TransInv trans : lstTransTotal){
          //if( trans.getFecha().compareTo(fechaInicio) > 0 && trans.getFecha().compareTo(fechaFin) < 0){
            lstTransInvDate.add( trans );
          //}
        }
        Articulo articulo = new Articulo();
        QArticulo art = QArticulo.articulo1;
        String [] articuloColor = article.split(",");
        String artl = articuloColor[0];
        String color = "";
        if(articuloColor.length > 1){
          color = articuloColor[1] != null ? articuloColor[1] : "" ;
        }
        BooleanBuilder booleanColor = new BooleanBuilder();
        if( color.trim().length() > 0 ){
          booleanColor.and(art.codigoColor.eq(color));
        } else {
          booleanColor.and(art.codigoColor.isEmpty()).or(art.codigoColor.isNull());
        }
        List<Articulo> articulos = (List<Articulo>) articuloRepository.findAll( art.articulo.trim().equalsIgnoreCase(artl.trim()).
                and(booleanColor) );
        if( articulos.size() == 1){
            articulo = articulos.get(0);
        }

        if( articulo != null ){
        for( TransInv movimiento : lstTransInvDate ){
            TransInvDetalle transInvSku = ( TransInvDetalle ) transInvDetalleRepository.findOne( transInvDet.idTipoTrans.eq(movimiento.getIdTipoTrans()).
                        and( transInvDet.folio.eq( movimiento.getFolio() )).and( transInvDet.sku.eq(articulo.getId() != null ? articulo.getId() : 0) ) );
            if( transInvSku != null ){
                lstMovimientos.add( transInvSku );
            }
        }
        }

        Integer exisActual = articulo.getCantExistencia();
        Integer saldoInicio = 0;
        Integer saldoFin = 0;
        for( TransInvDetalle movimiento : lstMovimientos ){
            Parametro parametro = parametroRepository.findOne( TipoParametro.ID_SUCURSAL.getValue() );
            Sucursal sucursal = sucursalRepository.findOne( Integer.parseInt(parametro.getValor()) );
            NotaVenta venta = notaVentaRepository.findOne( movimiento.getTransInv().getReferencia() );
            String factura;
            if( venta != null ){
                 factura = sucursal.getCentroCostos()+"-"+venta.getFactura();
            } else if( StringUtils.trimToEmpty(movimiento.getTransInv().getReferencia()) != "" ){
                factura = StringUtils.trimToEmpty(movimiento.getTransInv().getReferencia());
            } else {
                factura = "-";
            }
            KardexPorArticulo kardexArticulo = new KardexPorArticulo( movimiento, factura );
            kardexArticulo.setFecha( movimiento.getTransInv().getFecha() );
            kardexArticulo.setFolio( movimiento.getFolio().toString() );
            kardexArticulo.setReferencia( factura );
            kardexArticulo.setTipoTransaccion( movimiento.getIdTipoTrans() );
            Empleado empleado = empleadoRepository.findById( movimiento.getTransInv().getIdEmpleado() );
            kardexArticulo.setEmpleado( empleado.getNombreCompleto() );
            if( movimiento.getTipoMov().equalsIgnoreCase( "S" )){
                if( lstMovimientos.get(0).equals(movimiento)){
                    saldoFin = exisActual;
                } else {
                    saldoFin = saldoInicio;
                }
                saldoInicio = saldoFin+movimiento.getCantidad();
                kardexArticulo.setSalida( movimiento.getCantidad() );
            } else if( movimiento.getTipoMov().equalsIgnoreCase( "E" ) ){
                if( lstMovimientos.get(0).equals(movimiento)){
                    saldoFin = exisActual;
                } else {
                    saldoFin = saldoInicio;
                }
                saldoInicio = saldoFin-movimiento.getCantidad();
                kardexArticulo.setEntrada( movimiento.getCantidad() );
            }
            kardexArticulo.setSaldoInicio( saldoInicio );
            kardexArticulo.setSaldoFinal( saldoFin );
            lstKardezSku.add( kardexArticulo );
        }
        return lstKardezSku;
    }


    public List<VentasPorDia> obtenerVentasDelDiaActual( Date fechaInicio, Date fechaFin, Boolean artPrecioMayorcero ){

        QNotaVenta nv = QNotaVenta.notaVenta;
        List<NotaVenta> lstNotasVentas = ( List<NotaVenta> ) notaVentaRepository.findAll( nv.fechaHoraFactura.between(fechaInicio, fechaFin).
                and( nv.factura.isNotEmpty() ).and(nv.factura.isNotNull()), nv.factura.asc());
        List<VentasPorDia> lstVentasDia = new ArrayList<VentasPorDia>();
        for( NotaVenta venta : lstNotasVentas ){
            if( !TAG_CANCELADO.equalsIgnoreCase(venta.getsFactura()) ){
                VentasPorDia ventaDia = findorCreateFactura( lstVentasDia, venta.getFactura() );
                ventaDia.acumulaArticulos( venta, artPrecioMayorcero );
            }
        }

        return lstVentasDia;
    }



    
    public List<VentasPorDia> obtenerNotasDeCreditoEnVentasDelDiaActual( Date fechaInicio, Date fechaFin, Boolean artPrecioMayorcero ){

        QNotaVenta nv = QNotaVenta.notaVenta;
        List<NotaVenta> lstNotasVentas = ( List<NotaVenta> ) notaVentaRepository.findAll( nv.fechaHoraFactura.between(fechaInicio, fechaFin).
                and( nv.factura.isNotEmpty() ).and(nv.factura.isNotNull()), nv.factura.asc());
        List<VentasPorDia> lstVentasDia = new ArrayList<VentasPorDia>();
        for( NotaVenta venta : lstNotasVentas ){
            if( !TAG_CANCELADO.equalsIgnoreCase(venta.getsFactura()) ){
                VentasPorDia ventaDia = findorCreateFactura( lstVentasDia, venta.getFactura() );
                ventaDia.acumulaNotasDeCredito( venta, artPrecioMayorcero );
            }
        }

        return lstVentasDia;
    }

    
    public List<VentasPorDia> obtenerCancelacionesDelDiaActual( Date fechaInicio, Date fechaFin, Boolean artPrecioMayorcero ){

        List<VentasPorDia> lstVentasDia = new ArrayList<VentasPorDia>();
        QModificacion modificacion = QModificacion.modificacion;
        List<Modificacion> lstCancelaciones = ( List<Modificacion> ) modificacionRepository.findAll( modificacion.fecha.between(fechaInicio, fechaFin).
                and(modificacion.notaVenta.fechaHoraFactura.notBetween(fechaInicio,fechaFin)), modificacion.notaVenta.factura.asc() );

        for( Modificacion mod : lstCancelaciones ){
            VentasPorDia cancelacion = findorCreateFactura( lstVentasDia, mod.getNotaVenta().getFactura() );
            cancelacion.acumulaCancelaciones( mod.getNotaVenta(), mod, artPrecioMayorcero );
        }
        return lstVentasDia;
    }


    
    public List<VentasPorDia> obtenerVentasDelDiaActualPorGenerico( Date fechaInicio, Date fechaFin, Boolean artPrecioMayorCero ){

        List<VentasPorDia> lstVentasDia = new ArrayList<VentasPorDia>();
        BigDecimal notasCredito = BigDecimal.ZERO;
        QNotaVenta nv = QNotaVenta.notaVenta;
        List<NotaVenta> lstNotasVentas = ( List<NotaVenta> ) notaVentaRepository.findAll( nv.fechaHoraFactura.between(fechaInicio, fechaFin).
                and( nv.factura.isNotEmpty() ).and(nv.factura.isNotNull()), nv.factura.asc());
        QModificacion modificacion = QModificacion.modificacion;
        List<Modificacion> lstCancelaciones = ( List<Modificacion> ) modificacionRepository.findAll( modificacion.fecha.between(fechaInicio, fechaFin).
                and(modificacion.notaVenta.fechaHoraFactura.notBetween(fechaInicio,fechaFin)) );

        for( NotaVenta nota : lstNotasVentas ){
            if( !TAG_CANCELADO.equalsIgnoreCase(nota.getsFactura()) ){
                for( DetalleNotaVenta det : nota.getDetalles() ){
                        VentasPorDia ventasGenericos = FindOrCreateGenerico( lstVentasDia, det.getArticulo().getGenerico().getDescripcion() );
                        ventasGenericos.acumulaArticulosPorgenericos( det, artPrecioMayorCero );
                    }
                }
        }

        for( Modificacion mod : lstCancelaciones ){
            List<DetalleNotaVenta> lstDet = new ArrayList<DetalleNotaVenta>(mod.getNotaVenta().getDetalles());
            Collections.sort( lstDet, new Comparator<DetalleNotaVenta>() {
            @Override
            public int compare(  DetalleNotaVenta o1,  DetalleNotaVenta o2 ) {
            return o1.getArticulo().getGenerico().getDescripcion().compareToIgnoreCase(o2.getArticulo().getGenerico().getDescripcion());
            }
            });
            for( DetalleNotaVenta det : lstDet ){
            VentasPorDia cancelacion = FindOrCreateGenerico( lstVentasDia, det.getArticulo().getGenerico().getDescripcion() );
            cancelacion.acumulaCancelacionesPorgenericos( det, artPrecioMayorCero );
            }
        }

        for( NotaVenta nota : lstNotasVentas ){
            if( !TAG_CANCELADO.equalsIgnoreCase(nota.getsFactura()) ){
                for( Pago pago : nota.getPagos()){
                    if( "NOT".equalsIgnoreCase(pago.getIdFPago()) ){
                        notasCredito = notasCredito.add(pago.getMonto());
                        for( DetalleNotaVenta det : nota.getDetalles() ){
                            VentasPorDia ventasGenericos = FindOrCreateGenerico( lstVentasDia, det.getArticulo().getGenerico().getDescripcion() );
                            ventasGenericos.acumulaNotasDeCreditoGenericos( det, pago.getMonto(), artPrecioMayorCero );
                        }
                    }
                }
            }
        }

        if( lstVentasDia.size() > 0 ){
            lstVentasDia.get(0).setMontoTotal( lstVentasDia.get(0).getMontoTotal().subtract(notasCredito) );
            lstVentasDia.get(0).setMontoConDescuento( lstVentasDia.get(0).getMontoConDescuento().subtract(notasCredito) );
        }

        return lstVentasDia;
    }

    
    protected VentasPorDia FindOrCreateGenerico(  List<VentasPorDia> lstVemtas, String idGenerico ) {
        VentasPorDia found = null;

        for ( VentasPorDia ventas : lstVemtas ) {
            if ( ventas.getGenerico().equals( idGenerico ) ) {
                found = ventas;
                break;
            }
        }
        if ( found == null ) {
            found = new VentasPorDia( "", idGenerico, new Date() );
            lstVemtas.add( found );
        }
        return found;
    }


    
    public VentasPorDia findorCreateFactura(  List<VentasPorDia> lstVentas, String factura ) {
        VentasPorDia found = null;
        for ( VentasPorDia ventas : lstVentas ) {
            if ( ventas.getFactura().equals( factura ) ) {
                found = ventas;
                break;
            }
        }
        if ( found == null ) {
            found = new VentasPorDia( factura, "", new Date() );
            lstVentas.add( found );
        }
        return found;
    }


    
    public List<IngresoPorDia> obtenerPagosPorPeriodo( Date fechaInicio, Date fechaFin ){
        List<IngresoPorDia> lstIngresos = new ArrayList<IngresoPorDia>();

        QNotaVenta nota = QNotaVenta.notaVenta;
        List<NotaVenta> lstNotasVentas = ( List<NotaVenta> ) notaVentaRepository.findAll( nota.fechaHoraFactura.between(fechaInicio, fechaFin).
                and( nota.factura.isNotEmpty() ).and(nota.factura.isNotNull()), nota.fechaHoraFactura.asc() );
        for( NotaVenta notaVenta : lstNotasVentas ){
            List<Pago> lstPagos = new ArrayList<Pago>(notaVenta.getPagos());
            if( lstPagos.size() > 0 ){
                IngresoPorDia ingreso = FindOrCreate( lstIngresos, notaVenta.getFechaHoraFactura() );
                ingreso.AcumulaIngresosPorDia( notaVenta );
            }
        }

        return lstIngresos;
    }



    
    public List<VentasPorDia> obtenerVentasPorPeriodo( Date fechaInicio, Date fechaFin ){
        List<VentasPorDia> lstVentas = new ArrayList<VentasPorDia>();
        QNotaVenta nv = QNotaVenta.notaVenta;
        List<NotaVenta> lstNotasVentas = ( List<NotaVenta> ) notaVentaRepository.findAll( nv.fechaHoraFactura.between(fechaInicio, fechaFin).
                and(nv.factura.isNotEmpty()).and(nv.factura.isNotNull()), nv.fechaHoraFactura.asc() );
        Parametro parametro = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
        double iva = 1;
        try {
        iva = 1+(NumberFormat.getInstance().parse(parametro.getValor()).doubleValue()/100);
        } catch ( Exception e ){}

        for( NotaVenta nota : lstNotasVentas ){
            VentasPorDia ventas = FindOrCreatePorFecha( lstVentas, nota.getFechaHoraFactura() );
            ventas.acumulaVentasPorDia( nota, iva );
        }
        return lstVentas;
    }


    
    public List<VentasPorDia> obtenerVentasCanceladasPorPeriodo( Date fechaInicio, Date fechaFin ){
        List<VentasPorDia> lstVentas = new ArrayList<VentasPorDia>();
        QNotaVenta nv = QNotaVenta.notaVenta;
        List<NotaVenta> lstNotasVentas = ( List<NotaVenta> ) notaVentaRepository.findAll( nv.fechaHoraFactura.between(fechaInicio, fechaFin).
                and(nv.factura.isNotEmpty()).and(nv.factura.isNotNull()), nv.fechaHoraFactura.asc() );
        QModificacion mod = QModificacion.modificacion;
        List<Modificacion> lstCancelaciones = ( List<Modificacion> ) modificacionRepository.findAll( mod.fecha.between(fechaInicio, fechaFin), mod.fecha.asc() );
        Parametro parametro = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
        double iva = 1;
        try {
            iva = 1+(NumberFormat.getInstance().parse(parametro.getValor()).doubleValue()/100);
        } catch ( Exception e ){}

        for( Modificacion modificacion : lstCancelaciones ){
            VentasPorDia cancelaciones = FindOrCreatePorFecha( lstVentas, modificacion.getNotaVenta().getFechaHoraFactura() );
            cancelaciones.acumulaCancelacionesPorDia( modificacion, lstNotasVentas, iva );
        }

        return lstVentas;
    }

    
    public List<VentasPorDia> obtenerNotasDeCreditoEnVentasPorPeriodo( Date fechaInicio, Date fechaFin ){
        List<VentasPorDia> lstVentas = new ArrayList<VentasPorDia>();
        List<VentasPorDia> lstNotasCredito = new ArrayList<VentasPorDia>();
        QNotaVenta nv = QNotaVenta.notaVenta;
        List<NotaVenta> lstNotasVentas = ( List<NotaVenta> ) notaVentaRepository.findAll( nv.fechaHoraFactura.between(fechaInicio, fechaFin).
                and(nv.factura.isNotEmpty()).and(nv.factura.isNotNull()), nv.fechaHoraFactura.asc() );
        Parametro parametro = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
        double iva = 1;
        try {
            iva = 1+(NumberFormat.getInstance().parse(parametro.getValor()).doubleValue()/100);
        } catch ( Exception e ){}

        for( NotaVenta nota : lstNotasVentas ){
            if( !TAG_CANCELADO.equalsIgnoreCase(nota.getsFactura()) ){
                VentasPorDia ventas = FindOrCreatePorFecha( lstVentas, nota.getFechaHoraFactura() );
                ventas.acumulaNotasCreditoVentasPorDia( nota, iva );
            }
        }

        for(VentasPorDia venta : lstVentas){
            if(venta.getEsNotaCredito()){
                lstNotasCredito.add( venta );
            }
        }
        return lstNotasCredito;
    }

    
    protected VentasPorDia FindOrCreatePorFecha(  List<VentasPorDia> lstVentas, Date fecha ) {
        VentasPorDia found = null;

        for ( VentasPorDia ventas : lstVentas ) {
            if ( ventas.getFecha().equals( fecha ) ) {
                found = ventas;
                break;
            }
        }
        if ( found == null ) {
            found = new VentasPorDia( "", "", fecha );
            lstVentas.add( found );
        }
        return found;
    }


    
    public List<PromocionesAplicadas> obtenerPromocionesAplicadas( Date fechaInicio, Date fechaFin ){
        List<PromocionesAplicadas> lstPromociones = new ArrayList<PromocionesAplicadas>();

        QOrdenPromDet promocion = QOrdenPromDet.ordenPromDet;
        List<OrdenPromDet> lstPromos = ( List<OrdenPromDet> ) ordenPromDetRepository.findAll( promocion.fechaMod.between( fechaInicio, fechaFin ) );
        for(OrdenPromDet promo : lstPromos){
            NotaVenta nota = notaVentaRepository.findOne(promo.getIdFactura());
            Articulo articulo = articuloRepository.findOne(promo.getIdArticulo());
            QDetalleNotaVenta det = QDetalleNotaVenta.detalleNotaVenta;
            DetalleNotaVenta detalle = detalleNotaVentaRepository.findOne( det.idFactura.eq(nota.getId()).and(det.idArticulo.eq(articulo.getId())) );

            PromocionesAplicadas promocionAplicada = new PromocionesAplicadas();
            promocionAplicada.setFecha( nota.getFechaHoraFactura() );
            promocionAplicada.setFactura( nota.getFactura() );
            promocionAplicada.setIdArticulo( articulo.getId().toString() );
            promocionAplicada.setArticulo( articulo.getArticulo() );
            promocionAplicada.setImporteLista( detalle.getPrecioUnitLista() );
            promocionAplicada.setImporteDesc( promo.getDescuentoMonto() );
            promocionAplicada.setPorcentajeDesc( promo.getDescuentoPorcentaje().doubleValue() );
            promocionAplicada.setImporteTotal( detalle.getPrecioUnitFinal() );
            lstPromociones.add(promocionAplicada);
        }

        QNotaVenta nv = QNotaVenta.notaVenta;
        List<NotaVenta> lstNotasConDesc = ( List<NotaVenta> ) notaVentaRepository.findAll( nv.fechaHoraFactura.between(fechaInicio,fechaFin).
                and(nv.factura.isNotEmpty()).and(nv.factura.isNotNull()).and(nv.montoDescuento.ne(BigDecimal.ZERO).and(nv.por100Descuento.ne(0))) );
        for(NotaVenta nota : lstNotasConDesc ){
            PromocionesAplicadas promocionAplicada = new PromocionesAplicadas();
            promocionAplicada.setFecha( nota.getFechaHoraFactura() );
            promocionAplicada.setFactura( nota.getFactura() );
            promocionAplicada.setIdArticulo( "-" );
            promocionAplicada.setArticulo( "-" );
            BigDecimal importeLista = BigDecimal.ZERO;
            BigDecimal importeTotal = BigDecimal.ZERO;
            for(DetalleNotaVenta det : nota.getDetalles() ){
                importeLista = importeLista.add(det.getPrecioUnitLista());
                importeTotal = importeTotal.add(det.getPrecioUnitFinal());
            }
            promocionAplicada.setImporteLista( importeLista );
            promocionAplicada.setImporteDesc( nota.getMontoDescuento() );
            promocionAplicada.setPorcentajeDesc( nota.getPor100Descuento().doubleValue() );
            promocionAplicada.setImporteTotal( importeTotal );
            lstPromociones.add(promocionAplicada);
        }

        return lstPromociones;
    }

    
    public List<ResumenCierre> obtenerVentasCierreDiario( Date fechaInicio, Date fechaFin ){
        List<ResumenCierre> lstIngresos = new ArrayList<ResumenCierre>();
        QNotaVenta nota = QNotaVenta.notaVenta;
        String saleDate = new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio );
        List<NotaVenta> lstNotasVentas = ( List<NotaVenta> ) notaVentaRepository.findAll( nota.fechaHoraFactura.between(fechaInicio, fechaFin).
                and( nota.factura.isNotEmpty() ).and(nota.factura.isNotNull()), nota.fechaHoraFactura.asc() );
        for( NotaVenta notaVenta : lstNotasVentas ){
            Boolean canMismoDia = false;
            if( notaVenta.getPagos().size() > 0 ){
              if( notaVenta.getsFactura().trim().equalsIgnoreCase(TAG_CANCELADO) ){
                List<Modificacion> lstModificaciones = modificacionRepository.findByIdFactura( notaVenta.getId() );
                if(lstModificaciones.size() > 0){
                  for(Modificacion mod : lstModificaciones){
                    String modDate = new SimpleDateFormat( "dd/MM/yyyy" ).format( mod.getFecha() );
                    if( modDate.trim().equalsIgnoreCase(saleDate.trim()) ){
                      canMismoDia = true;
                    }
                  }
                }
              }
              if( !canMismoDia ){
                ResumenCierre ingreso = FindOrCreateCierreDiario(lstIngresos, notaVenta.getFechaHoraFactura());
                ingreso.acumulaIngresosPorDia(notaVenta, saleDate);
              }
            }
        }

        return lstIngresos;

    }



    public List<ResumenCierre> obtenerSaldosCierreDiario( Date fechaInicio, Date fechaFin ){
        List<ResumenCierre> lstIngresos = new ArrayList<ResumenCierre>();
        QNotaVenta nota = QNotaVenta.notaVenta;
        /*List<NotaVenta> lstNotasVentas = ( List<NotaVenta> ) notaVentaRepository.findAll( nota.fechaHoraFactura.between(fechaInicio, fechaFin).
                and( nota.factura.isNotEmpty() ).and(nota.factura.isNotNull()).and(nota.sFactura.ne(TAG_CANCELADO)), nota.fechaHoraFactura.asc() );*/
        QPago payment = QPago.pago;
        List<Pago> lstPagos = (List<Pago>) pagoRepository.findAll(payment.fecha.between(fechaInicio,fechaFin).
                and(payment.notaVenta.fechaHoraFactura.notBetween(fechaInicio,fechaFin)));
        Boolean esSaldo = false;
        for(Pago pago : lstPagos){
          ResumenCierre ingreso = FindOrCreateCierreDiario(lstIngresos, pago.getFecha());
          ingreso.acumulaSaldosPorDia( pago );
        }
        return lstIngresos;
    }

    
    public List<ResumenCierre> obtenerDevolucionesCierreDiario( Date fechaInicio, Date fechaFin ){
        List<ResumenCierre> lstIngresos = new ArrayList<ResumenCierre>();
        List<Pago> lstPagos = new ArrayList<Pago>();
        QDevolucion dev = QDevolucion.devolucion;
        List<Devolucion> lstDevoluciones = ( List<Devolucion> ) devolucionRepository.findAll( dev.fecha.between( fechaInicio, fechaFin ),
                dev.idPago.asc() );
        for( Devolucion devolucion : lstDevoluciones ){
            ResumenCierre ingreso = FindOrCreateCierreDiario( lstIngresos, fechaInicio );
            ingreso.acumulaDevolucionesPorDia( devolucion );
        }
        Collections.sort( lstPagos, new Comparator<Pago>() {
            @Override
            public int compare( Pago o1,  Pago o2) {
                return o1.getIdFactura().compareTo(o2.getIdFactura());
            }
        });

        return lstIngresos;
    }

    
    public ResumenCierre FindOrCreateCierreDiario(  List<ResumenCierre> lstIngresos, Date fecha ) {
        Date onlyDay = DateUtils.truncate( fecha, Calendar.DATE );
        ResumenCierre found = null;
        for ( ResumenCierre ingresos : lstIngresos ) {
            if ( ingresos.getFecha().equals( onlyDay ) ) {

                found = ingresos;
                break;
            }
        }
        if ( found == null ) {
            found = new ResumenCierre( onlyDay );
            lstIngresos.add( found );
        }
        return found;
    }



    
    public List<VentasPorDia> obtenerVentasPorPeriodoMasVision( Date fechaInicio, Date fechaFin ){
        List<VentasPorDia> lstVentas = new ArrayList<VentasPorDia>();
        List<VentasPorDia> lstVentasCan = new ArrayList<VentasPorDia>();
        String pagosNoTransf = Registry.getPaymentsNoRefound();
        QNotaVenta nv = QNotaVenta.notaVenta;
        List<NotaVenta> lstNotas = (List<NotaVenta>) notaVentaRepository.findAll(
                nv.fechaHoraFactura.between(fechaInicio,fechaFin).and(nv.factura.isNotEmpty()), nv.factura.asc() );

        QPago payment = QPago.pago;
        List<Pago> lstPagosBod = (List<Pago>) pagoRepository.findAll(payment.fecha.between(fechaInicio,fechaFin).
                and(payment.idFPago.eq(TAG_BODEGA)));

        QModificacion modificacion = QModificacion.modificacion;
        List<Modificacion> lstCanceladas = (List<Modificacion>) modificacionRepository.findAll(modificacion.tipo.eq(TAG_TIPO_CANCELADO).
                and(modificacion.fecha.between(fechaInicio,fechaFin)));

        for( NotaVenta notas : lstNotas ){
            VentasPorDia ventas = findorCreateFactura( lstVentas, notas.getFactura() );
            ventas.acumulaVentasPorDiaMasVision( notas, pagosNoTransf );
        }
        for( Modificacion mod : lstCanceladas){
            VentasPorDia cancelaciones = findorCreateFactura( lstVentasCan, mod.getNotaVenta().getFactura() );
            cancelaciones.acumulaCancelacionesPorDiaMasVision( mod, pagosNoTransf );
        }

        for( Pago pago : lstPagosBod ){
          NotaVenta nota = notaVentaRepository.findOne( pago.getIdFactura() );
          if(nota != null){
            VentasPorDia ventas = findorCreateFactura( lstVentas, nota.getFactura() );
            ventas.acumulaVentasBodPorDiaMasVision( nota, pagosNoTransf );
          }
        }

        for(VentasPorDia inCan : lstVentasCan){
            lstVentas.add( inCan );
        }
        Collections.sort( lstVentas, new Comparator<VentasPorDia>() {
            @Override
            public int compare(VentasPorDia o1, VentasPorDia o2) {
                return o1.getFactura().compareTo(o2.getFactura());
            }
        } );
        return lstVentas;
    }



    
    public List<Descuento> obtenerDescuentosMasVision( Date fechaInicio, Date fechaFin,  String key ) {
        BooleanBuilder claveBuilder = new BooleanBuilder();
        QDescuento descuento = QDescuento.descuento;
        if( !key.trim().equalsIgnoreCase("") ){
          claveBuilder.and( descuento.clave.eq(key) );
        } else {
          claveBuilder.and( descuento.idFactura.isNotNull() );
        }
        List<Descuento> lstDescuentos = ( List<Descuento> ) descuentoRepository.findAll( descuento.fecha.between( fechaInicio, fechaFin ).
                and(claveBuilder) );

        return lstDescuentos;
    }


    
    public List<Cotizaciones> obtenerCotizaciones( Date fechaInicio, Date fechaFin ) {
        List<Cotizaciones> lstCotizaciones = new ArrayList<Cotizaciones>();
        QCotizacion cotiza = QCotizacion.cotizacion;
        List<Cotizacion> cotizaciones = (List<Cotizacion>) cotizacionRepository.findAll( cotiza.fechaCotizacion.between(fechaInicio,fechaFin) );

        for(Cotizacion cot : cotizaciones){
          List<Articulo> lstArticulos = new ArrayList<Articulo>();
          List<Precio> lstPrecios = new ArrayList<Precio>();
          for(CotizaDet cotizaDet : cot.getCotizaDet() ){
            QArticulo art1 = QArticulo.articulo1;
            List<Articulo> art = (List<Articulo>)articuloRepository.findAll( art1.articulo.eq(cotizaDet.getArticulo()) );
            if( art.size() > 0 ){
              QPrecio pryce = QPrecio.precio1;
              List<Precio> precio = precioRepository.findByArticulo( art.get(0).getArticulo().trim());
              if(precio.size() > 0){
                lstPrecios.add( precio.get(0) );
              }
              lstArticulos.add(art.get(0));
            }
          }
          Cliente cliente = clienteRepository.findOne( cot.getIdCliente() );
          NotaVenta nota = null;
          if( cot.getIdFactura() != null && cot.getIdFactura().trim().length() > 0 ){
            nota = notaVentaRepository.findOne( cot.getIdFactura() );
          }
          Cotizaciones coti = FindorCreateCot( lstCotizaciones, cot.getIdEmpleado() );
          coti.AcumulaCotizacionesDet( cot, lstArticulos, lstPrecios, nota, cliente );
        }

        return lstCotizaciones;
    }


  
  public List<VentasPorDia> obtenerVentasPorCupones( Date fechaInicio, Date fechaFin ){
    log.debug( "obtenerVentasPorCupones" );

    List<VentasPorDia> lstCupones = new ArrayList<VentasPorDia>();
    QPago payment = QPago.pago;
    List<Pago> lstPagos = (List<Pago>) pagoRepository.findAll( payment.fecha.between(fechaInicio,fechaFin).
            and(payment.idFPago.startsWith("C")).and(payment.notaVenta.sFactura.ne(TAG_CANCELADO)), payment.tipoPago.asc());


    for(Pago pago : lstPagos){
      VentasPorDia venta = findorCreateFactura(lstCupones, pago.getIdFPago());
      venta.acumulaCupones( pago );
    }

    return lstCupones;
  }


  public List<CuponesMvDesc> obtenerVentasPorCuponesMv( Date dateStart, Date dateEnd, Boolean todo, Boolean noAplicados, Boolean extemporaneos ){
    log.debug( "obtenerVentasPorCuponesMv" );
    List<CuponesMvDesc> lstCuponesMv = new ArrayList<CuponesMvDesc>();
    SimpleDateFormat df = new SimpleDateFormat( "dd/MM/yyyy" );

    QCuponMv qCuponMv = QCuponMv.cuponMv;
    BooleanBuilder builderTodo = new BooleanBuilder();
    BooleanBuilder builderNoApli = new BooleanBuilder();
    BooleanBuilder builderExtemp = new BooleanBuilder();
    if( todo ){
      builderTodo.and( qCuponMv.claveDescuento.isNotNull() );
    } else {
      builderTodo.and( qCuponMv.claveDescuento.isNotNull() );
    }
    if( noAplicados ){
      builderNoApli.and(qCuponMv.facturaDestino.isNull().or(qCuponMv.facturaDestino.isEmpty())).and(qCuponMv.fechaAplicacion.isNull());
    } else {
      builderNoApli.and( qCuponMv.claveDescuento.isNotNull() );
    }
    if( extemporaneos ){
      builderExtemp.and( qCuponMv.fechaAplicacion.isNotNull().and(qCuponMv.notaVenta.fechaHoraFactura.isNotNull()) );
    } else {
      builderExtemp.and( qCuponMv.claveDescuento.isNotNull() );
    }
    List<CuponMv> cupones = (List<CuponMv>) cuponMvRepository.findAll( qCuponMv.notaVenta.fechaHoraFactura.between(dateStart,dateEnd).
            and(qCuponMv.claveDescuento.isNotNull()).and(builderTodo.and(builderNoApli).and(builderExtemp)) );
    List<CuponMv> lstCupones = new ArrayList<CuponMv>();
    if(extemporaneos){
      for(CuponMv cuponMv : cupones){
        if(!StringUtils.trimToEmpty(df.format(cuponMv.getFechaAplicacion())).equalsIgnoreCase(
                StringUtils.trimToEmpty(df.format(cuponMv.getNotaVenta().getFechaHoraFactura())))){
          lstCupones.add(cuponMv);
        }
      }
    } else {
        lstCupones.addAll( cupones );
    }

    for(CuponMv cuponMv : lstCupones){
      String telCasa = cuponMv.getNotaVenta() != null ? StringUtils.trimToEmpty(cuponMv.getNotaVenta().getCliente().getTelefonoAdicional()) : "";
      String telAdi = cuponMv.getNotaVenta() != null ? StringUtils.trimToEmpty(cuponMv.getNotaVenta().getCliente().getTelefonoAdicional()) : "";
      String cliente = cuponMv.getNotaVenta() != null ? StringUtils.trimToEmpty(cuponMv.getNotaVenta().getCliente().getNombreCompleto()) : "";
      String tipoCupon = "";
      if( StringUtils.trimToEmpty(cuponMv.getClaveDescuento()).startsWith("8") ){
        tipoCupon = "2";
      } else if( StringUtils.trimToEmpty(cuponMv.getClaveDescuento()).startsWith("7") ){
        tipoCupon = "3";
      } else if( StringUtils.trimToEmpty(cuponMv.getClaveDescuento()).startsWith("F") ){
        tipoCupon = "F";
      }
      CuponesMvDesc cuponesMvDesc = new CuponesMvDesc();
      cuponesMvDesc.setCliente( cliente );
      cuponesMvDesc.setTelefono( telCasa.length() > 0 ? telCasa+"," : ""+telAdi );
      if(cuponesMvDesc.getTelefono().endsWith(",")){
        cuponesMvDesc.setTelefono( cuponesMvDesc.getTelefono().replace(",","") );
      }
      cuponesMvDesc.setFacturaOri( cuponMv.getFacturaOrigen());
      cuponesMvDesc.setFechaVenta( cuponMv.getNotaVenta() != null ? cuponMv.getNotaVenta().getFechaHoraFactura() : null );
      cuponesMvDesc.setFechaEntrega( cuponMv.getNotaVenta() != null ? cuponMv.getNotaVenta().getFechaEntrega() : null );
      cuponesMvDesc.setMontoCupon( cuponMv.getMontoCupon() );
      cuponesMvDesc.setTipoCupon( tipoCupon );
      cuponesMvDesc.setVigencia( cuponMv.getFechaVigencia() );
      cuponesMvDesc.setFacturaDest( StringUtils.trimToEmpty(cuponMv.getFacturaDestino()));
      cuponesMvDesc.setFechaAplic( cuponMv.getFechaAplicacion() );
      cuponesMvDesc.setMontoCupon( cuponMv.getMontoCupon() );
      if( !cuponesMvDesc.getTipoCupon().equalsIgnoreCase("F") ){
        lstCuponesMv.add( cuponesMvDesc );
      }
    }
    return lstCuponesMv;
  }



  public List<TrabajosSinEntregar> obtenerTrabajosSinEntregar( Date fechaInicio, Date fechaFin ){
      List<TrabajosSinEntregar> lstTrabajos = new ArrayList<TrabajosSinEntregar>();
      QJb jb = QJb.jb;
      /*List<Jb> trabajos = ( List<Jb> )jbRepository.findAll(jb.notaVenta.fechaHoraFactura.isNull().
              and(jb.estado.eq('RS').and()));

      for(Jb trabajo : trabajos){
        String linea = articulos.getIdGenerico();
        FacturasPorEmpleado facturas = FindOorCreate( lstArticulos, linea );
        facturas.AcumulaMarcasResumido( articulos.getMarca(), articulos );
      }*/

      return lstTrabajos;
  }


   public List<TrabajosSinEntregar> obtenerTrabajosSinEntregarAuditoria( ){
     log.debug( "obtenerTrabajosSinEntregarAuditoria( )" );
     List<TrabajosSinEntregar> lstTrabajos = new ArrayList<TrabajosSinEntregar>();
     QNotaVenta nota = QNotaVenta.notaVenta;
     List<NotaVenta> lstNotas = (List<NotaVenta>) notaVentaRepository.findAll( nota.factura.isNotEmpty().and(nota.factura.isNotNull()).
             and(nota.sFactura.ne(TAG_CANCELADO)).and(nota.fechaEntrega.isNull()), nota.factura.asc() );
     QJb jb = QJb.jb;
     List<Jb> lstJbs = (List<Jb>) jbRepository.findAll( jb.jb_tipo.eq(TAG_ORDEN_SERVICIO).and(jb.estado.ne(TAG_ESTADO_ENTREGADO)).and(jb.estado.ne(TAG_ESTADO_CANCELADO)).
             and(jb.estado.ne(TAG_ESTADO_GARANTIA)) );

     for(NotaVenta notaVenta : lstNotas){
       TrabajosSinEntregar trabajo = new TrabajosSinEntregar();
       Boolean add = false;
       BigDecimal saldo = notaVenta.getVentaNeta().subtract(notaVenta.getSumaPagos());
       trabajo.setFecha( notaVenta.getFechaHoraFactura() );
       trabajo.setFactura( notaVenta.getFactura() );
       trabajo.setIdFactura( notaVenta.getId() );
       trabajo.setMonto( notaVenta.getVentaNeta() );
       trabajo.setSaldo( saldo );
       lstTrabajos.add( trabajo );
     }

     for(Jb jbTmp : lstJbs){
       TrabajosSinEntregar trabajo = new TrabajosSinEntregar();
       if( jbTmp.getNotaVenta() != null ){
         BigDecimal saldo = jbTmp.getNotaVenta().getVentaNeta().subtract(jbTmp.getNotaVenta().getSumaPagos());
         trabajo.setFecha( jbTmp.getNotaVenta().getFechaHoraFactura() );
         trabajo.setFactura( jbTmp.getNotaVenta().getFactura() );
         trabajo.setIdFactura( jbTmp.getNotaVenta().getId() );
         trabajo.setMonto( jbTmp.getNotaVenta().getVentaNeta() );
         trabajo.setSaldo( saldo );
       } else {
         trabajo.setFecha( jbTmp.getFecha_venta() );
         trabajo.setFactura( "SERVICIO" );
         trabajo.setIdFactura( jbTmp.getRx() );
         trabajo.setMonto( BigDecimal.ZERO );
         trabajo.setSaldo( BigDecimal.ZERO );
       }
       lstTrabajos.add( trabajo );
     }

     return lstTrabajos;
   }



    public List<DescuentosPorTipo> obtenerExamenesporOptometrista( Date fechaInicio, Date fechaFin ) {
        List<DescuentosPorTipo> lstExamenes = new ArrayList<DescuentosPorTipo>();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        QReceta rx = QReceta.receta;
        QExamen exam = QExamen.examen;
        List<Examen> lstExamenesValid = (List<Examen>)examenRepository.findAll( exam.fechaAlta.between(fechaInicio,fechaFin),
                exam.idAtendio.asc(),exam.idCliente.asc() );

        List<Examen> lstExams = (List<Examen>)examenRepository.findAll( exam.idAtendio.eq("9999").and(exam.tipoOft.eq("SE")).
                and(exam.fechaAlta.between(fechaInicio,fechaFin)));
        Integer total = lstExamenesValid.size();
        String fecha = "";
        Integer idCliente = 0;
        for ( Examen examenValido : lstExamenesValid ) {
            Examen examen = examenValido;
            if( examen != null ){
                String idEmpleado = examen.getIdAtendio().trim();
                DescuentosPorTipo desc = EncontraroCrear( lstExamenes, idEmpleado );
                desc.AcumulaClientes( examenValido );
                if( examen.getIdAtendio().equalsIgnoreCase("9999") && examen.getObservacionesEx().equalsIgnoreCase("SE") ){
                  /*desc.AcumulaExamenTotal();
                  desc.AcumulaExamenNoVentas();*/
                } else {
                  if( examenValido.getFactura().trim().length() > 0 && !examenValido.getIdAtendio().equalsIgnoreCase("9999") ){
                      if( df.format(examenValido.getFechaAlta()).equals(fecha) && examenValido.getIdCliente().equals(idCliente) ){

                      } else {
                        desc.AcumulaExamenTotal();
                        desc.AcumulaExamenVenta();
                      }
                      fecha = df.format(examenValido.getFechaAlta());
                      idCliente = examenValido.getIdCliente();
                  } else if( examenValido.getTipoOft().equalsIgnoreCase("CO") ){
                      if(!examen.getIdAtendio().equalsIgnoreCase("9999")){
                          desc.AcumulaExamenTotal();
                          desc.AcumulaExamenCotizacion();
                      }
                  } else if( !examen.getIdAtendio().equalsIgnoreCase("9999") && examenValido.getTipoOft().equalsIgnoreCase("NV") ){
                      desc.AcumulaExamenTotal();
                      desc.AcumulaExamenNoVentas();
                  }
                  desc.CalculaPorcentaje();
                }
            }
        }

        for(Examen ex : lstExams){
          String idEmpleado = ex.getIdAtendio();
          DescuentosPorTipo desc = EncontraroCrear( lstExamenes, idEmpleado );
          desc.AcumulaExamenTotal();
          desc.AcumulaExamenNoVentas();
        }

        return lstExamenes;
    }


    public Cotizaciones FindorCreateCot(  List<Cotizaciones> lstIngresos, String idEmpleado ) {
        Cotizaciones found = null;
        for ( Cotizaciones ingresos : lstIngresos ) {
            if ( ingresos.getIdEmpleado().equals( idEmpleado ) ) {
                found = ingresos;
                break;
            }
        }
        if ( found == null ) {
            found = new Cotizaciones( idEmpleado );
            Empleado empleado = empleadoRepository.findOne( idEmpleado );
            if ( empleado != null ) {
                found.setNombre( empleado.nombreCompleto() );
            }
            lstIngresos.add( found );
        }
        return found;
    }


    public Multipago FindorCreateMultiPago(  List<Multipago> lstNotas, Integer idCliente, Date fecha ) {
      Multipago found = null;
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      for ( Multipago ingresos : lstNotas ) {
        if ( ingresos.getIdCliente().equals( idCliente )
              && StringUtils.trimToEmpty(df.format(ingresos.getFechaVenta())).
              equalsIgnoreCase(StringUtils.trimToEmpty(df.format(fecha))) ) {
          found = ingresos;
          break;
        }
      }
      if ( found == null ) {
            found = new Multipago( idCliente, fecha );
            lstNotas.add( found );
      }
      return found;
    }



    public List<Multipago> obtenerVentasMultipago( Date dateStart, Date dateEnd ) {
      List<Multipago> lstVentasMulti = new ArrayList<Multipago>();
      QNotaVenta qNotaVenta = QNotaVenta.notaVenta;
      List<NotaVenta> lstNotas = (List<NotaVenta>) notaVentaRepository.findAll( qNotaVenta.fechaHoraFactura.between(dateStart,dateEnd).
              and(qNotaVenta.factura.isNotNull()).
            and(qNotaVenta.factura.isNotEmpty()).and(qNotaVenta.udf4.eq("M")), qNotaVenta.fechaHoraFactura.asc(), qNotaVenta.idCliente.asc() );
      for(NotaVenta notaVenta : lstNotas){
        Multipago multipago = FindorCreateMultiPago(lstVentasMulti, notaVenta.getIdCliente(), notaVenta.getFechaHoraFactura());
        multipago.AcumulaNotas(notaVenta);
      }
      return lstVentasMulti;
    }



    public List<Bodegas> obtenerBodegas( Date fechaBodegas ){
      String formato="MM";
      List<Bodegas> lstBodegas = new ArrayList<Bodegas>();
      List<NotaVenta> lstSinEntregar = new ArrayList<NotaVenta>();
      Calendar cal = Calendar.getInstance();
      cal.setTime( fechaBodegas );
      cal.set(Calendar.DAY_OF_MONTH,Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
      cal.add(Calendar.MONTH, -2);
      Date fechaInicio = cal.getTime();

      QNotaVenta nv = QNotaVenta.notaVenta;
      List<NotaVenta> lstSinEntregarTmp = (List<NotaVenta>) notaVentaRepository.findAll(nv.fechaPrometida.loe(fechaInicio).
              and(nv.fechaEntrega.isNull()).and(nv.sFactura.ne(TAG_CANCELADO)).and(nv.factura.isNotEmpty()).
              and(nv.factura.isNotNull()), nv.fechaEntrega.asc());
      for(NotaVenta notaTmp : lstSinEntregarTmp){
          Jb jb = jbRepository.findOne( notaTmp.getFactura() );
          if( jb!= null ){
            if( !jb.getEstado().trim().equalsIgnoreCase(TAG_ROTO_EN_PINO) && !jb.getEstado().trim().equalsIgnoreCase(TAG_ROTO_NO_ENVIAR) &&
                    !jb.getEstado().trim().equalsIgnoreCase(TAG_ROTO_POR_ENVIAR) && !jb.getEstado().trim().equalsIgnoreCase(TAG_DESENTREGADO_POR_ROTO)){
              if( notaTmp.getFechaPrometida().before(fechaInicio) ){
                lstSinEntregar.add( notaTmp );
              }
            }
          } else {
            if( notaTmp.getFechaPrometida().before(fechaInicio) ){
              lstSinEntregar.add( notaTmp );
            }
          }
      }
      for(NotaVenta nota : lstSinEntregar){
        SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
        Integer mes = Integer.parseInt(dateFormat.format(nota.getFechaHoraFactura()));
        Bodegas bodega = FindorCreateCellar( lstBodegas, mes );
        bodega.AcumulaBodega( nota );
      }
      return lstBodegas;
    }



    public Bodegas FindorCreateCellar(  List<Bodegas> lstBodegas, Integer month ) {
        Bodegas found = null;
        for ( Bodegas bodegas : lstBodegas ) {
            if ( bodegas.getMes() == month ) {
                found = bodegas;
                break;
            }
        }
        if ( found == null ) {
            found = new Bodegas( month );
            lstBodegas.add( found );
        }
        return found;
    }
}