package mx.lux.pos.service.impl;

import com.mysema.query.BooleanBuilder;
import mx.lux.pos.model.*;
import mx.lux.pos.repository.*;
import mx.lux.pos.service.ReportService;
import mx.lux.pos.service.SucursalService;
import mx.lux.pos.service.business.Registry;
import mx.lux.pos.service.business.ReportBusiness;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Service( "reportService" )
@Transactional( readOnly = true )
public class ReportServiceImpl implements ReportService {

    private static Logger log = LoggerFactory.getLogger( ReportServiceImpl.class );
    
    private static String CIERRE_DIARIO = "reports/Cierre_Diario.jrxml";
    
    private static String INGRESOS_POR_SUCURSAL = "reports/Ingresos_Sucursal.jrxml";
    
    private static String INGRESOS_POR_VENDEDOR_RESUMIDO = "reports/Ingresos_Vendedor_Resumido.jrxml";
    
    private static String INGRESOS_POR_VENDEDOR_COMPLETO = "reports/Ingresos_Vendedor_Completo.jrxml";
    
    private static String VENTAS = "reports/Ventas.jrxml";
    
    private static String CUPONES = "reports/Cupones.jrxml";

    private static String CUPONESMV = "reports/CuponesMv.jrxml";

    private static String VENTAS_MASVISION = "reports/Ventas_MasVision.jrxml";
    
    private static String VENTAS_COMPLETO = "reports/Ventas_Completo.jrxml";
    
    private static String VENTAS_POR_VENDEDOR_COMPLETO = "reports/Venta_Por_Vendedor_Completo.jrxml";
    
    private static String VENTAS_POR_VENDEDOR_RESUMIDO = "reports/Venta_Por_Vendedor_Resumido.jrxml";

    private static String BODEGAS = "reports/Bodegas.jrxml";

    private static String TRABAJOS_SIN_ENTREGAR = "reports/Trabajos_Sin_Entregar.jrxml";

    private static String TRABAJOS_SIN_ENTREGAR_AUDITORIA = "reports/Trabajos_Sin_Entregar_Auditoria.jrxml";
    
    private static String CANCELACIONES_RESUMIDO = "reports/Cancelaciones.jrxml";

    private static String MULTIPAGO = "reports/Multipago.jrxml";

    private static String CANCELACIONES_COMPLETO = "reports/Cancelaciones_Completo.jrxml";
    
    private static String VENTA_POR_LINEA_FACTURA = "reports/Venta_Por_Linea.jrxml";
    
    private static String VENTA_POR_LINEA_ARTICULO = "reports/Venta_Por_Linea_Articulo.jrxml";
    
    private static String VENTA_POR_MARCA = "reports/Ventas_Por_Marca.jrxml";
    
    private static String VENTA_POR_VENDEDOR_MARCA = "reports/Ventas_Por_Vendedor_Por_Marca.jrxml";
    
    private static String EXISTENCIAS_POR_MARCA = "reports/Existencias_Por_Marca.jrxml";
    
    private static String EXISTENCIAS_POR_MARCA_RESUMIDO = "reports/Existencias_Por_Marca_Resumido.jrxml";
    
    private static String EXISTENCIAS_POR_ARTICULO = "reports/Existencias_Por_Articulo.jrxml";
    
    private static String CONTROL_DE_TRABAJOS = "reports/Control_de_Trabajos.jrxml";
    
    private static String TRABAJOS_ENTREGADOS = "reports/Trabajos_Entregados.jrxml";
    
    private static String TRABAJOS_ENTREGADOS_POR_EMPLEADO = "reports/Trabajos_Entregados_Por_Empleado.jrxml";
    
    private static String FACTURAS_FISCALES = "reports/Facturas_Fiscales.jrxml";
    
    private static String DESCUENTOS = "reports/Descuentos.jrxml";
    
    private static String PROMOCIONES_APLICADAS = "reports/Promociones.jrxml";
    
    private static String PAGOS = "reports/Pagos.jrxml";
    
    private static String COTIZACIONES = "reports/Cotizaciones.jrxml";
    
    private static String EXAMENES_RESUMIDO = "reports/Examenes.jrxml";
    
    private static String EXAMENES_COMPLETO = "reports/Examenes_Completo.jrxml";

    private static String EXAMENES_POR_OPTOMETRISTA = "reports/Examenes_Por_Optometrista.jrxml";

    private static String EXAMENES_POR_OPTOMETRISTA_COMPLETO = "reports/Examenes_Por_Optometrista_Completo.jrxml";
    
    private static String VENTAS_POR_OPTOMETRISTA_COMPLETO = "reports/Ventas_Por_Optometrista_Completo.jrxml";
    
    private static String VENTAS_POR_OPTOMETRISTA_RESUMIDO = "reports/Ventas_Por_Optometrista_Resumido.jrxml";
    
    private static String PROMOCIONES = "reports/Lista_de_Promociones.jrxml";
    
    private static String KARDEX = "reports/Kardex.jrxml";
    
    private static String VENTAS_DEL_DIA = "reports/Ventas_Del_Dia.jrxml";
    
    private static String INGRESOS_POR_PERIODO = "reports/Ingresos_Por_Periodo.jrxml";

    @Resource
    private NotaVentaRepository notaVentaRepository;

    @Resource
    private CotizacionRepository cotizacionRepository;

    @Resource
    private OrdenPromDetRepository ordenPromDetRepository;

    @Resource
    private NotaFacturaRepository notaFacturaRepository;

    @Resource
    private ModificacionRepository modificacionRepository;

    @Resource
    private TrabajoRepository trabajoRepository;

    @Resource
    private JbRepository jbRepository;

    @Resource
    private ExternoRepository externoRepository;

    @Resource
    private DevolucionRepository devolucionRepository;

    @Resource
    private PagoRepository pagoRepository;

    @Resource
    private SucursalService sucursalService;

    @Resource
    private ParametroRepository parametroRepository;

    @Resource
    private ImpuestoRepository impuestoRepository;

    @Resource
    private ReportBusiness reportBusiness;

    @Resource
    private EmpleadoRepository empleadoRepository;

    @Resource
    private ArticuloRepository articuloRepository;

    @Resource
    private PrecioRepository precioRepository;


    
    @Override
    public String obtenerReporteCierreDiario(  Date fecha ) {
        log.info( "obtenerReporteCierreDiario" );

        if ( fecha != null ) {
            Random random = new Random();
            File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Cierre-Diario%s.txt",random.nextInt()) );
            org.springframework.core.io.Resource template = new ClassPathResource( CIERRE_DIARIO );
            log.info( "Ruta:{}", report.getAbsolutePath() );

            Date fechaInicio = DateUtils.truncate( fecha, Calendar.DAY_OF_MONTH );
            Date fechaFin = new Date( DateUtils.ceiling( fecha, Calendar.DAY_OF_MONTH ).getTime() - 1 );

            Sucursal sucursal = sucursalService.obtenSucursalActual();
            List<ResumenCierre> lstPagos = reportBusiness.obtenerVentasCierreDiario( fechaInicio, fechaFin );
            List<ResumenCierre> lstSaldos = reportBusiness.obtenerSaldosCierreDiario( fechaInicio, fechaFin );
            List<ResumenCierre> lstDevoluciones = reportBusiness.obtenerDevolucionesCierreDiario( fechaInicio, fechaFin );
            Parametro ivaVigenteParam = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
            Impuesto iva = impuestoRepository.findOne( ivaVigenteParam.getValor() );


            Integer totalFacturas = 0;
            BigDecimal totalVenta = BigDecimal.ZERO;
            BigDecimal totalEfectivo = BigDecimal.ZERO;
            BigDecimal totalTarjeta = BigDecimal.ZERO;
            BigDecimal totalTarjetaUSD = BigDecimal.ZERO;
            BigDecimal totalUSDEfectivo = BigDecimal.ZERO;
            BigDecimal totalTransferencia = BigDecimal.ZERO;
            BigDecimal totalOtros = BigDecimal.ZERO;
            BigDecimal total = BigDecimal.ZERO;
            double totalVentaIVA = 0.0;
            double totalEfectivoIVA = 0.0;
            double totalTarjetaIVA = 0.0;
            double totalTarjetaUSDIVA = 0.0;
            double totalUSDEfectivoIVA = 0.0;
            double totalTransferenciaIVA = 0.0;
            double totalOtrosIVA = 0.0;
            double totalIVA = 0.0;

            for(ResumenCierre venta : lstPagos){
                for(DetalleIngresoPorDia ingreso : venta.getLstPagos()){
                    totalFacturas = totalFacturas+1;
                    totalVenta = totalVenta.add(ingreso.getMontoPago());
                    totalEfectivo = totalEfectivo.add(ingreso.getPagoEf());
                    totalTarjeta = totalTarjeta.add(ingreso.getPagoTN());
                    totalTarjetaUSD = totalTarjetaUSD.add(ingreso.getPagoTD());
                    totalUSDEfectivo = totalUSDEfectivo.add(ingreso.getPagoEfUs());
                    totalTransferencia = totalTransferencia.add(ingreso.getPagoTR());
                    totalOtros = totalOtros.add(ingreso.getPagoOtros());
                    total = total.add(ingreso.getMontoTotal());
                }
            }
            for(ResumenCierre saldo : lstSaldos){
              for(DetalleIngresoPorDia montoDev : saldo.getLstPagos()){
                  totalEfectivo = totalEfectivo.add(montoDev.getPagoEf());
                  totalTarjeta = totalTarjeta.add(montoDev.getPagoTN());
                  totalTarjetaUSD = totalTarjetaUSD.add(montoDev.getPagoTD());
                  totalUSDEfectivo = totalUSDEfectivo.add(montoDev.getPagoEfUs());
                  totalTransferencia = totalTransferencia.add(montoDev.getPagoTR());
                  totalOtros = totalOtros.add(montoDev.getPagoOtros());
                  total = total.add(montoDev.getMontoTotal());
              }
            }
            for(ResumenCierre devolucion : lstDevoluciones){
                for(DetalleIngresoPorDia montoDev : devolucion.getLstPagos()){
                    totalFacturas = totalFacturas-1;
                    totalVenta = totalVenta.add(montoDev.getMontoPago());
                    totalEfectivo = totalEfectivo.add(montoDev.getPagoEf());
                    totalTarjeta = totalTarjeta.add(montoDev.getPagoTN());
                    totalTarjetaUSD = totalTarjetaUSD.add(montoDev.getPagoTD());
                    totalUSDEfectivo = totalUSDEfectivo.add(montoDev.getPagoEfUs());
                    totalTransferencia = totalTransferencia.add(montoDev.getPagoTR());
                    totalOtros = totalOtros.add(montoDev.getPagoOtros());
                    total = total.add(montoDev.getMontoTotal());
                }

            }

            totalVentaIVA = totalVenta.doubleValue() / ( 1 + ( iva.getTasa().doubleValue() / 100 ) );
            totalEfectivoIVA = totalEfectivo.doubleValue() / ( 1 + ( iva.getTasa().doubleValue() / 100 ) );
            totalTarjetaIVA = totalTarjeta.doubleValue() / ( 1 + ( iva.getTasa().doubleValue() / 100 ) );
            totalTarjetaUSDIVA = totalTarjetaUSD.doubleValue() / ( 1 + ( iva.getTasa().doubleValue() / 100 ) );
            totalUSDEfectivoIVA = totalUSDEfectivo.doubleValue() / ( 1 + ( iva.getTasa().doubleValue() / 100 ) );
            totalTransferenciaIVA = totalTransferencia.doubleValue() / ( 1 + ( iva.getTasa().doubleValue() / 100 ) );
            totalOtrosIVA = totalOtros.doubleValue() / ( 1 + ( iva.getTasa().doubleValue() / 100 ) );
            totalIVA = total.doubleValue() / ( 1 + ( iva.getTasa().doubleValue() / 100 ) );

            Boolean isSunglass = Registry.isSunglass();
            //NumberFormat formatter = new DecimalFormat( "$#,##0.00" );

            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
            parametros.put( "fechaCierre", new SimpleDateFormat( "dd/MM/yyyy" ).format( fecha ) );
            parametros.put( "lstPagos", lstPagos );
            parametros.put( "lstSaldos", lstSaldos );
            parametros.put( "lstDevoluciones", lstDevoluciones );

            parametros.put( "totalFacturas", totalFacturas );
            parametros.put( "totalVenta", totalVenta );
            parametros.put( "totalEfectivo", totalEfectivo );
            parametros.put( "totalTarjeta", totalTarjeta );
            parametros.put( "totalTarjetaUSD", totalTarjetaUSD );
            parametros.put( "totalUSDEfectivo", totalUSDEfectivo );
            parametros.put( "totalTransferencia", totalTransferencia );
            parametros.put( "totalOtros", totalOtros );
            parametros.put( "total", total );
            parametros.put( "totalVentaIVA", totalVentaIVA );
            parametros.put( "totalEfectivoIVA", totalEfectivoIVA );
            parametros.put( "totalTarjetaIVA", totalTarjetaIVA );
            parametros.put( "totalTarjetaUSDIVA", totalTarjetaUSDIVA );
            parametros.put( "totalUSDEfectivoIVA", totalUSDEfectivoIVA );
            parametros.put( "totalTransferenciaIVA", totalTransferenciaIVA );
            parametros.put( "totalOtrosIVA", totalOtrosIVA );
            parametros.put( "totalIVA", totalIVA );

            parametros.put( "isSunglass", isSunglass );
            parametros.put( "sucursal", sucursal.getNombre() );

            String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
            log.info( "reporte:{}", reporte );

        }
        return null;
    }


    
    @Override
    public String obtenerReporteIngresosXSucursal(  Date fechaInicio,  Date fechaFin ) {
        log.info( "obtenerReporteIngresosXSucursal()" );

        if ( fechaInicio != null && fechaFin != null ) {

            File report = new File( System.getProperty( "java.io.tmpdir" ), "Ingresos-por-Sucursal.txt" );
            org.springframework.core.io.Resource template = new ClassPathResource( INGRESOS_POR_SUCURSAL );
            log.info( "Ruta:{}", report.getAbsolutePath() );

            Parametro ivaVigenteParam = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
            Impuesto iva = impuestoRepository.findOne( ivaVigenteParam.getValor() );
            Double ivaTasa = iva.getTasa();
            BigDecimal ivaMonto = new BigDecimal( ivaTasa ).divide( new BigDecimal( 100 ) );

            fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
            fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );
            List<IngresoPorDia> listaPagos = reportBusiness.obtenerIngresoporDia(fechaInicio, fechaFin);
            Sucursal sucursal = sucursalService.obtenSucursalActual();

            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
            parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
            parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
            parametros.put( "sucursal", sucursal.getNombre() );
            parametros.put( "lstpagos", listaPagos );
            if ( listaPagos.size() > 0 ) {
                parametros.put( "lstpagosPrim", listaPagos.get( 0 ).getMontoAcumulado() );
                parametros.put( "ivaMonto", ivaMonto );
            }

            String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
            log.info( "reporte:{}", reporte );

        }

        return null;
    }


    
    public String obtenerReporteVentas(  Date fechaInicio,  Date fechaFin ) {
        log.info( "obtenerReporteVentas()" );

        if ( fechaInicio != null && fechaFin != null ) {
            fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
            fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

            File report = new File( System.getProperty( "java.io.tmpdir" ), "Ventas.txt" );
            org.springframework.core.io.Resource template = new ClassPathResource( VENTAS );
            log.info( "Ruta:{}", report.getAbsolutePath() );

            List<IngresoPorDia> lstIngresos = reportBusiness.obtenerVentasporDia(fechaInicio, fechaFin);
            Sucursal sucursal = sucursalService.obtenSucursalActual();

            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
            parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
            parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
            parametros.put( "sucursal", sucursal.getNombre() );
            parametros.put( "lstIngresos", lstIngresos );
            if ( lstIngresos.size() > 0 ) {
                parametros.put( "monto", lstIngresos.get( 0 ).getMontoAcumulado() );
            }

            String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
            log.info("reporte:{}", reporte);

        }
        return null;

    }



    
    public String obtenerReporteVentasporVendedorCompleto(  Date fechaInicio,  Date fechaFin ) {
        log.info( "obtenerReporteVentasporVendedorCompleto()" );

        if ( fechaInicio != null && fechaFin != null ) {
            fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
            fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

            Random random = new Random();
            File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Ventas-por-Vendedor%s.txt", random.nextInt()) );
            org.springframework.core.io.Resource template = new ClassPathResource( VENTAS_POR_VENDEDOR_COMPLETO );
            log.info( "Ruta:{}", report.getAbsolutePath() );

           List<IngresoPorVendedor> lstVentas = reportBusiness.obtenerVentasporVendedor( fechaInicio, fechaFin );

            Sucursal sucursal = sucursalService.obtenSucursalActual();

            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
            parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
            parametros.put( "sucursal", sucursal.getNombre() );
            parametros.put( "lstVentas", lstVentas );

            String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
            log.info( "reporte:{}", reporte );
        }
        return null;

    }


    public String obtenerReporteTrabajosSinEntregar() {
        log.info( "obtenerReporteTrabajosSinEntregar()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Trabajos-Sin-Entregar%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( TRABAJOS_SIN_ENTREGAR );
        log.info( "Ruta:{}", report.getAbsolutePath() );
        Integer totalFacturas = 0;
        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalSaldos = BigDecimal.ZERO;
        Integer totalFacturasSuc = 0;
        BigDecimal totalVentasSuc = BigDecimal.ZERO;
        BigDecimal totalSaldosSuc = BigDecimal.ZERO;
        Integer totalFacturasPin = 0;
        BigDecimal totalVentasPin = BigDecimal.ZERO;
        BigDecimal totalSaldosPin = BigDecimal.ZERO;
        Integer totalFacturasRet = 0;
        BigDecimal totalVentasRet = BigDecimal.ZERO;
        BigDecimal totalSaldosRet = BigDecimal.ZERO;

        QJb trabajo = QJb.jb;
        List<Jb> lstTrabajosSuc = ( List<Jb> ) jbRepository.findAll( trabajo.estado.eq( "RS" ).and( trabajo.notaVenta.factura.isNotNull() ) );
        BigDecimal trabajoSaldoSuc = BigDecimal.ZERO;
        for ( Jb trabajos : lstTrabajosSuc ) {
            totalFacturas = totalFacturas+1;
            totalFacturasSuc = totalFacturasSuc+1;
            totalVentas = totalVentas.add(trabajos.getNotaVenta() != null ? trabajos.getNotaVenta().getVentaNeta() :BigDecimal.ZERO);
            totalVentasSuc = totalVentasSuc.add(trabajos.getNotaVenta() != null ? trabajos.getNotaVenta().getVentaNeta() :BigDecimal.ZERO);
            totalSaldos = totalSaldos.add(trabajos.getSaldo());
            totalSaldosSuc = totalSaldosSuc.add(trabajos.getSaldo());
        }
        Collections.sort( lstTrabajosSuc, new Comparator<Jb>() {
            @Override
            public int compare(Jb o1, Jb o2) {
                return (o1.getNotaVenta() != null ? o1.getNotaVenta().getFactura() : "").compareTo(o2.getNotaVenta() != null ? o2.getNotaVenta().getFactura() : "");
            }
        } );

        List<Jb> lstTrabajosPin = ( List<Jb> ) jbRepository.findAll( trabajo.estado.eq( "EP" ).
                or(trabajo.jb_tipo.eq( "REP" )).and( trabajo.notaVenta.factura.isNotNull() ) );
        BigDecimal trabajoSaldoPin = BigDecimal.ZERO;
        for ( Jb trabajos : lstTrabajosPin ) {
            totalFacturas = totalFacturas+1;
            totalFacturasPin = totalFacturasPin+1;
            totalVentas = totalVentas.add(trabajos.getNotaVenta() != null ? trabajos.getNotaVenta().getVentaNeta() :BigDecimal.ZERO);
            totalVentasPin = totalVentasPin.add(trabajos.getNotaVenta() != null ? trabajos.getNotaVenta().getVentaNeta() :BigDecimal.ZERO);
            totalSaldos = totalSaldos.add(trabajos.getSaldo());
            totalSaldosPin = totalSaldosPin.add(trabajos.getSaldo());
        }
        Collections.sort( lstTrabajosPin, new Comparator<Jb>() {
            @Override
            public int compare(Jb o1, Jb o2) {
                return (o1.getNotaVenta() != null ? o1.getNotaVenta().getFactura() : "").compareTo(o2.getNotaVenta() != null ? o2.getNotaVenta().getFactura() : "");
            }
        } );

        List<Jb> lstTrabajosRet = ( List<Jb> ) jbRepository.findAll( trabajo.estado.eq( "RTN" ).and( trabajo.notaVenta.factura.isNotNull() ) );
        BigDecimal trabajoSaldoRet = BigDecimal.ZERO;
        for ( Jb trabajos : lstTrabajosRet ) {
            totalFacturas = totalFacturas+1;
            totalFacturasRet = totalFacturasRet+1;
            totalVentas = totalVentas.add(trabajos.getNotaVenta() != null ? trabajos.getNotaVenta().getVentaNeta() :BigDecimal.ZERO);
            totalVentasRet = totalVentasRet.add(trabajos.getNotaVenta() != null ? trabajos.getNotaVenta().getVentaNeta() :BigDecimal.ZERO);
            totalSaldos = totalSaldos.add(trabajos.getSaldo());
            totalSaldosRet = totalSaldosRet.add(trabajos.getSaldo());
        }
        Collections.sort( lstTrabajosRet, new Comparator<Jb>() {
            @Override
            public int compare(Jb o1, Jb o2) {
                return (o1.getNotaVenta() != null ? o1.getNotaVenta().getFactura() : "").compareTo(o2.getNotaVenta() != null ? o2.getNotaVenta().getFactura() : "");
            }
        } );

        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstTrabajosSuc", lstTrabajosSuc );
        parametros.put( "lstTrabajosPin", lstTrabajosPin );
        parametros.put( "lstTrabajosRet", lstTrabajosRet );
        parametros.put( "totalVentas", totalVentas );
        parametros.put( "totalSaldos", totalSaldos );
        parametros.put( "totalFacturas", totalFacturas );
        parametros.put( "totalVentasSuc", totalVentasSuc );
        parametros.put( "totalSaldosSuc", totalSaldosSuc );
        parametros.put( "totalFacturasSuc", totalFacturasSuc );
        parametros.put( "totalVentasPin", totalVentasPin );
        parametros.put( "totalSaldosPin", totalSaldosPin );
        parametros.put( "totalFacturasPin", totalFacturasPin );
        parametros.put( "totalVentasRet", totalVentasRet );
        parametros.put( "totalSaldosRet", totalSaldosRet );
        parametros.put( "totalFacturasRet", totalFacturasRet );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;

    }


    public String obtenerReporteVentasMultipago( Date dateStart, Date dateEnd ){
      log.info( "obtenerReporteVentasMultipago()" );

      Random random = new Random();
      File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Multipago%s.txt",random.nextInt()) );
      org.springframework.core.io.Resource template = new ClassPathResource( MULTIPAGO );
      log.info( "Ruta:{}", report.getAbsolutePath() );

      Sucursal sucursal = sucursalService.obtenSucursalActual();
      List<Multipago> lstNotas = reportBusiness.obtenerVentasMultipago( dateStart, dateEnd );
      log.info( "tamañoLista:{}", lstNotas.size() );

        Map<String, Object> parametros = new HashMap<String, Object>();
      parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
      parametros.put( "sucursal", sucursal.getNombre() );
      parametros.put( "lstNotas", lstNotas );

      String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
      log.info( "reporte:{}", reporte );

      return null;
    }


    public String obtenerReporteCancelacionesResumido( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteCancelacionesResumido()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Cancelaciones%s.txt",random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( CANCELACIONES_RESUMIDO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<FacturasPorEmpleado> lstFacturas = reportBusiness.obtenerFacturasporVendedor( fechaInicio, fechaFin );
        log.info( "tamañoLista:{}", lstFacturas.size() );

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstFacturas", lstFacturas );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;

    }


    
    public String obtenerReporteCancelacionesCompleto( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteCancelacionesCompleto()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Cancelaciones%s.txt",random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( CANCELACIONES_COMPLETO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );
        List<FacturasPorEmpleado> lstModificaciones = reportBusiness.obtenerFacturasporVendedor( fechaInicio, fechaFin );
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstModificaciones", lstModificaciones );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;

    }


    
    public String obtenerReporteVentasporLineaFactura( Date fechaInicio, Date fechaFin, String articulo, boolean gogle, boolean oftalmico, boolean todo ) {
        log.info( "obtenerReporteVentasporLineaFactura()" );

        File report = new File( System.getProperty( "java.io.tmpdir" ), "Ventas-Por-Linea-Factura.txt" );
        org.springframework.core.io.Resource template = new ClassPathResource( VENTA_POR_LINEA_FACTURA );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );
        List<NotaVenta> lstArticulos = reportBusiness.obtenerVentasLineaporFacturas( fechaInicio, fechaFin, articulo, gogle, oftalmico, todo );
        QModificacion modificacion = QModificacion.modificacion;
        List<Modificacion> lstCancelaciones = ( List<Modificacion> ) modificacionRepository.findAll( modificacion.fecha.between( fechaInicio, fechaFin ),
                modificacion.idFactura.asc() );
        Collections.sort( lstArticulos, new Comparator<NotaVenta>() {
            @Override
            public int compare(  NotaVenta o1,  NotaVenta o2 ) {
                return o1.getFactura().compareToIgnoreCase( o2.getFactura() );
            }
        } );
        Integer totalFact = 0;
        BigDecimal totalMonto = BigDecimal.ZERO;
        Integer totalFactCan = 0;
        BigDecimal totalMontoCan = BigDecimal.ZERO;
        for ( NotaVenta nota : lstArticulos ) {
            totalFact = totalFact + 1;
            for ( DetalleNotaVenta det : nota.getDetalles() ) {
                totalMonto = totalMonto.add( det.getPrecioUnitFinal() );
            }
        }
        for ( Modificacion mod : lstCancelaciones ) {
            totalFactCan = totalFactCan + 1;
            for ( DetalleNotaVenta det : mod.getNotaVenta().getDetalles() ) {
                totalMontoCan = totalMontoCan.add( det.getPrecioUnitFinal() );
            }
        }
        Integer facturas = totalFact - totalFactCan;
        BigDecimal monto = totalMonto.subtract( totalMontoCan );
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstArticulos", lstArticulos );
        parametros.put( "lstCancelaciones", lstCancelaciones );
        parametros.put( "facturas", facturas );
        parametros.put( "monto", monto );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteVentasporLineaArticulo( Date fechaInicio, Date fechaFin, String articulo, boolean gogle, boolean oftalmico, boolean todo ) {
        log.info( "obtenerReporteVentasporLineaArticulo()" );

        File report = new File( System.getProperty( "java.io.tmpdir" ), "Ventas-Por-Linea-Factura.txt" );
        org.springframework.core.io.Resource template = new ClassPathResource( VENTA_POR_LINEA_ARTICULO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );
        List<FacturasPorEmpleado> lstArticulos = reportBusiness.obtenerVentasLineaporArticulos( fechaInicio, fechaFin, articulo, gogle, oftalmico, todo );
        Collections.sort( lstArticulos, new Comparator<FacturasPorEmpleado>() {
            @Override
            public int compare(  FacturasPorEmpleado o1,  FacturasPorEmpleado o2 ) {
                return o1.getIdArticulo().compareTo( o2.getIdArticulo() );
            }
        } );
        Integer totalArt = 0;
        BigDecimal totalMonto = BigDecimal.ZERO;
        for ( FacturasPorEmpleado art : lstArticulos ) {
            totalArt = totalArt + art.getCantidad();
            totalMonto = totalMonto.add( art.getImporte() );
        }
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstArticulos", lstArticulos );
        parametros.put( "totalArt", totalArt );
        parametros.put( "totalMonto", totalMonto );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteVentasMarca( Date fechaInicio, Date fechaFin,  String marca, boolean noMostrarArticulos, boolean gogle, boolean oftalmico, boolean todo ) {
        log.info( "obtenerReporteVentasMarca()" );

        File report = new File( System.getProperty( "java.io.tmpdir" ), "Ventas-Por-Marca.txt" );
        org.springframework.core.io.Resource template = new ClassPathResource( VENTA_POR_MARCA );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        Sucursal sucursal = sucursalService.obtenSucursalActual();

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );
        List<FacturasPorEmpleado> lstVentas = reportBusiness.obtenerVentasMarca( fechaInicio, fechaFin, marca.toUpperCase().trim(), noMostrarArticulos, gogle, oftalmico, todo );
        Integer totalArticulos = 0;
        BigDecimal totalMonto = BigDecimal.ZERO;
        for ( FacturasPorEmpleado factura : lstVentas ) {
            totalArticulos = totalArticulos + factura.getCantidad();
            totalMonto = totalMonto.add( factura.getImporte() );
        }

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstVentas", lstVentas );
        parametros.put( "totalArticulos", totalArticulos );
        parametros.put( "totalMonto", totalMonto );
        parametros.put( "noMostrarArticulos", noMostrarArticulos );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteVentasVendedorporMarca( Date fechaInicio, Date fechaFin, String marca, boolean mostrarArticulos, boolean gogle, boolean oftalmico, boolean todo ) {
        log.info( "obtenerReporteVentasVendedorporMarca()" );

        File report = new File( System.getProperty( "java.io.tmpdir" ), "Ventas-Por-Vendedor-Por-Marca.txt" );
        org.springframework.core.io.Resource template = new ClassPathResource( VENTA_POR_VENDEDOR_MARCA );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );
        List<IngresoPorVendedor> lstVentas = reportBusiness.obtenerVentasporVendedorporMarca( fechaInicio, fechaFin, marca, mostrarArticulos, gogle, oftalmico, todo );

        Integer totalArticulos = 0;
        BigDecimal montoTotal = BigDecimal.ZERO;
        BigDecimal montoTotalSinIva = BigDecimal.ZERO;
        for(IngresoPorVendedor ingreso : lstVentas){
            totalArticulos = totalArticulos+ingreso.getContador().intValue();
            montoTotal = montoTotal.add(ingreso.getTotalPagos());
            montoTotalSinIva = montoTotalSinIva.add(ingreso.getTotalPagosIva());
        }
        Collections.sort(lstVentas, new Comparator<IngresoPorVendedor>() {
            @Override
            public int compare( IngresoPorVendedor o1,  IngresoPorVendedor o2) {
                return o1.getIdEmpleado().compareTo(o2.getIdEmpleado());
            }
        });
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstVentas", lstVentas );
        parametros.put( "totalArticulos", totalArticulos );
        parametros.put( "montoTotal", montoTotal );
        parametros.put( "montoTotalSinIva", montoTotalSinIva );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteExistenciasporMarca( String marca, boolean gogle, boolean oftalmico, boolean todo ) {
        log.info( "obtenerReporteExistenciasporMarca()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Existencias-Por-Marca%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( EXISTENCIAS_POR_MARCA );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        List<FacturasPorEmpleado> lstArticulos = reportBusiness.obtenerExistenciasporMarcaCompleto( marca, gogle, oftalmico, todo );
        Collections.sort( lstArticulos, new Comparator<FacturasPorEmpleado>() {
            @Override
            public int compare( FacturasPorEmpleado o1,  FacturasPorEmpleado o2) {
                return o1.getMarca().compareTo(o2.getMarca());
            }
        });
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstArticulos", lstArticulos );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteExistenciasporMarcaResumido( String marca, boolean gogle, boolean oftalmico, boolean todo ) {
        log.info( "obtenerReporteExistenciasporMarcaResumido()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Existencias-Por-Marca%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( EXISTENCIAS_POR_MARCA_RESUMIDO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        List<FacturasPorEmpleado> lstArticulos = reportBusiness.obtenerExistenciasporMarcaResumido( marca, gogle, oftalmico, todo );
        Integer cantTotal = 0;
        for(FacturasPorEmpleado factura : lstArticulos){
            cantTotal = cantTotal+factura.getCantidad();
        }
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstArticulos", lstArticulos );
        parametros.put( "cantTotal", cantTotal );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteExistenciasporArticulo( String marca, String descripcion, String color ) {
        log.info( "obtenerReporteExistenciasporArticulo()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Existencias-Por-Articulo%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( EXISTENCIAS_POR_ARTICULO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        List<Articulo> lstArticulos = reportBusiness.obtenerExistenciasporArticulo( marca, descripcion, color );
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Integer totalArticulos = 0;
        BigDecimal totalMonto = BigDecimal.ZERO;
        for( Articulo articulo : lstArticulos ){
            totalArticulos = totalArticulos+articulo.getCantExistencia().intValue();
            totalMonto = totalMonto.add(articulo.getPrecio());
        }

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstArticulos", lstArticulos );
        parametros.put( "totalMonto", totalMonto );
        parametros.put( "totalArticulos", totalArticulos );


        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteControldeTrabajos( boolean retenidos, boolean porEnviar, boolean pino, boolean sucursal, boolean todos, boolean factura, boolean fechaPromesa ) {
        log.info( "obtenerReporteControldeTrabajos()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Control-de-Trabajos%s.txt",random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( CONTROL_DE_TRABAJOS );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        List<SaldoPorEstado> lstTrabajos = reportBusiness.obtenerTrabajos( retenidos, porEnviar, pino, sucursal, todos, factura, fechaPromesa );
        Sucursal sucursalNom = sucursalService.obtenSucursalActual();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "sucursal", sucursalNom.getNombre() );
        parametros.put( "lstTrabajos", lstTrabajos );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteTrabajosEntregados( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteTrabajosEntregados()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Trabajos-Entregados%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( TRABAJOS_ENTREGADOS );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );
        List<TrabajoTrack> lstTrabajos = reportBusiness.obtenerTrabajosporEntregar( fechaInicio, fechaFin );
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstTrabajos", lstTrabajos );
        parametros.put( "cantTrabajos", lstTrabajos.size() );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteTrabajosEntregadosporEmpleado( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteTrabajosEntregadosporEmpleado()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Trabajos-Entregados-Por-Empleado%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( TRABAJOS_ENTREGADOS_POR_EMPLEADO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );
        List<SaldoPorEstado> lstTrabajos = reportBusiness.obtenerTrabajosporEntregarporEmpleado( fechaInicio, fechaFin );
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstTrabajos", lstTrabajos );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteVentasCompleto( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteVentasCompleto()" );

        File report = new File( System.getProperty( "java.io.tmpdir" ), "Ventas-Completo.txt" );
        org.springframework.core.io.Resource template = new ClassPathResource( VENTAS_COMPLETO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Parametro ivaVigenteParam = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
        Impuesto iva = impuestoRepository.findOne( ivaVigenteParam.getValor() );
        BigDecimal ivaTasa = new BigDecimal( iva.getTasa() ).divide( new BigDecimal( 100 ) );

        List<VentasPorDia> lstVentas = reportBusiness.obtenerVentasPorPeriodo( fechaInicio, fechaFin );
        List<VentasPorDia> lstVentasCanc = reportBusiness.obtenerVentasCanceladasPorPeriodo( fechaInicio, fechaFin );
        List<VentasPorDia> lstNotasCredito = reportBusiness.obtenerNotasDeCreditoEnVentasPorPeriodo( fechaInicio, fechaFin );

        Integer totalFacturas = 0;
        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalVentasSinIva = BigDecimal.ZERO;
        BigDecimal totalVentasCanc = BigDecimal.ZERO;
        BigDecimal totalVentasCancSinIva = BigDecimal.ZERO;
        BigDecimal totalNotasCredito = BigDecimal.ZERO;
        BigDecimal totalNotasCreditoSinIva = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalSinIva = BigDecimal.ZERO;
        for ( VentasPorDia venta : lstVentas ) {
            if(venta.getMontoTotal().compareTo(BigDecimal.ZERO) > 0){
                totalFacturas = totalFacturas + 1;
                totalVentas = totalVentas.add( venta.getMontoTotal() );
                totalVentasSinIva = totalVentasSinIva.add( new BigDecimal( venta.getMontoSinIva() ) );
            }
        }

        for( VentasPorDia cancelaciones : lstVentasCanc ){
            totalFacturas = totalFacturas-1;
            totalVentasCanc = totalVentasCanc.add( cancelaciones.getMontoTotal() );
            totalVentasCancSinIva = totalVentasCancSinIva.add( new BigDecimal( cancelaciones.getMontoSinIva() ) );
        }

        for( VentasPorDia notaCredito : lstNotasCredito ){
            totalNotasCredito = totalNotasCredito.add( notaCredito.getMontoTotal() );
            totalNotasCreditoSinIva = totalNotasCreditoSinIva.add( new BigDecimal( notaCredito.getMontoSinIva() ) );
            if( notaCredito.getEsNotaCredito() ){
                totalFacturas = totalFacturas-1;
            }
        }
        total = totalVentas.subtract(totalVentasCanc.abs().add(totalNotasCredito.abs()));
        totalSinIva = totalVentasSinIva.subtract(totalVentasCancSinIva.abs().add(totalNotasCreditoSinIva.abs()));
        BigDecimal promedioVentas = total.divide( new BigDecimal(totalFacturas), 10, BigDecimal.ROUND_CEILING );

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstVentas", lstVentas );
        parametros.put( "lstVentasCanc", lstVentasCanc );
        parametros.put( "lstNotasCredito", lstNotasCredito );
        parametros.put( "ivaTasa", ivaTasa );
        parametros.put( "totalFacturas", totalFacturas );
        parametros.put( "totalVentas", total );
        parametros.put( "totalVentasSinIva", totalSinIva );
        parametros.put( "promedioVentas", promedioVentas );
        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteFacturasFiscales( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteFacturasFiscales()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Facturas-Fiscales%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( FACTURAS_FISCALES );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        QNotaFactura factura = QNotaFactura.notaFactura;
        List<NotaFactura> lstFactura = ( List<NotaFactura> ) notaFacturaRepository.findAll( factura.fechaImpresion.between( fechaInicio, fechaFin ) );

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstFactura", lstFactura );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );


        return null;
    }


    
    public String obtenerReporteDescuentos( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteDescuentos()" );

        File report = new File( System.getProperty( "java.io.tmpdir" ), "Descuentos.txt" );
        org.springframework.core.io.Resource template = new ClassPathResource( DESCUENTOS );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<DescuentosPorTipo> lstDescuentos = reportBusiness.obtenerDescuentosporTipo( fechaInicio, fechaFin );
        Integer totalDesc = lstDescuentos.size();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstDescuentos", lstDescuentos );
        parametros.put( "totalDesc", totalDesc );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReportePromocionesAplicadas( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReportePromociones()" );

        File report = new File( System.getProperty( "java.io.tmpdir" ), "Promociones-Aplicadas.txt" );
        org.springframework.core.io.Resource template = new ClassPathResource( PROMOCIONES_APLICADAS );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        List<PromocionesAplicadas> lstPromociones = reportBusiness.obtenerPromocionesAplicadas( fechaInicio, fechaFin );
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstPromociones", lstPromociones );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReportePagos( Date fechaInicio, Date fechaFin, String formaPago, String factura ) {
        log.info( "obtenerReportePagos()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Pagos%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( PAGOS );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<DescuentosPorTipo> lstPagos = reportBusiness.obtenerPagosporTipo( fechaInicio, fechaFin, formaPago, factura );

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstPagos", lstPagos );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteCotizaciones( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteCotizaciones()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Cotizaciones%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( COTIZACIONES );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();

        QCotizacion cotizacion = QCotizacion.cotizacion;
        List<Cotizaciones> lstCotizaciones = ( List<Cotizaciones> ) reportBusiness.obtenerCotizaciones(fechaInicio, fechaFin);
        Double totalCotizaciones = 0.00;
        Double totalCotizacionesConVenta = 0.00;
        BigDecimal porcentajeTotal = BigDecimal.ZERO;
        for( Cotizaciones cotiza : lstCotizaciones ){
          for(CotizacionesDet cotizacionesDet : cotiza.getLstDetalles()){
            if(StringUtils.trimToEmpty(cotizacionesDet.getFactura()).length() > 0){
                totalCotizacionesConVenta = totalCotizacionesConVenta+1;
            }
          }
          totalCotizaciones = totalCotizaciones+cotiza.getLstDetalles().size();
        }
        porcentajeTotal = totalCotizaciones > 0.00 ? new BigDecimal(totalCotizacionesConVenta/totalCotizaciones) : BigDecimal.ZERO;

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstCotizaciones", lstCotizaciones );
        parametros.put( "totalCotizaciones", totalCotizaciones );
        parametros.put( "totalCotizacionesConVenta", totalCotizacionesConVenta );
        parametros.put( "porcentajeTotal", porcentajeTotal );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteExamenesResumido( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteExamenesResumido()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Examenes-Resumido%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( EXAMENES_RESUMIDO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<DescuentosPorTipo> lstExamenes = reportBusiness.obtenerExamenesporEmpleado( fechaInicio, fechaFin );

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstExamenes", lstExamenes );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteExamenesCompleto( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteExamenesCompleto()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Examenes-Completo%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( EXAMENES_COMPLETO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<DescuentosPorTipo> lstExamenes = reportBusiness.obtenerExamenesporEmpleado( fechaInicio, fechaFin );
        Integer totalRxSinVenta = 0;
        Integer totalRxConVenta = 0;
        Integer totalRx = 0;
        for(DescuentosPorTipo examen : lstExamenes){
          Integer rxXOptSin = 0;
          Integer rxXOptCon = 0;
          for(TipoDescuento examenDet : examen.getDescuentos()){
            examenDet.setFactura(examenDet.getFactura().replaceFirst(", ",""));
            if( StringUtils.trimToEmpty(examenDet.getFactura()).length() > 0 ){
              totalRxConVenta = totalRxConVenta + 1;
              rxXOptCon = rxXOptCon+1;
            } else {
              totalRxSinVenta = totalRxSinVenta + 1;
              rxXOptSin = rxXOptSin+1;
            }
              totalRx = totalRx+1;
          }
          examen.setRxConVenta( rxXOptCon );
          examen.setRxSinVenta( rxXOptSin );
        }

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstExamenes", lstExamenes );
        parametros.put( "totalRxConVenta", totalRxConVenta );
        parametros.put( "totalRxSinVenta", totalRxSinVenta );
        parametros.put( "totalRx", totalRx );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteVentasporOptometrista( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteVentasporOptometrista()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Ventas-Por-Optometrista-Completo%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( VENTAS_POR_OPTOMETRISTA_COMPLETO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<IngresoPorVendedor> lstVentas = reportBusiness.obtenerVentasporOptometristaCompleto( fechaInicio, fechaFin );

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstVentas", lstVentas );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteVentasporOptometristaResumido( Date fechaInicio, Date fechaFin, boolean todoTipo, boolean referido, boolean rx,
                                                               boolean lux, boolean todaVenta, boolean primera, boolean mayor, boolean resumen ) {
        log.info( "obtenerReporteVentasporOptometrista()" );

        File report = new File( System.getProperty( "java.io.tmpdir" ), "Ventas-Por-Optometrista-Resumido.txt" );
        org.springframework.core.io.Resource template = new ClassPathResource( VENTAS_POR_OPTOMETRISTA_RESUMIDO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<IngresoPorVendedor> lstVentas = reportBusiness.obtenerVentasporOptometristaCompleto( fechaInicio, fechaFin );

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstVentas", lstVentas );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }

    
    public String obtenerReportePromociones( Date fechaImpresion ) {
        log.info( "obtenerReportePromociones()" );
        log.info( "fecha::", fechaImpresion );

        File report = new File( System.getProperty( "java.io.tmpdir" ), "Promociones.txt" );
        org.springframework.core.io.Resource template = new ClassPathResource( PROMOCIONES );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<Promocion> lstPromociones = reportBusiness.obtenerPromociones( fechaImpresion );

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstPromociones", lstPromociones );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteDeKardex(  String article, Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteDeKardex" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Kardex-Por-SKU%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( KARDEX );
        log.info( "Ruta:{}", report.getAbsolutePath() );
        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<KardexPorArticulo> lstKardexTmp = reportBusiness.obtenerKardex( article, fechaInicio, fechaFin );
        Collections.reverse( lstKardexTmp );
        List<KardexPorArticulo> lstKardex = new ArrayList<KardexPorArticulo>();
        Articulo articulo = new Articulo();
        BigDecimal precio = BigDecimal.ZERO;
        String [] articuloColor = article.split(",");
        String artl = articuloColor[0];
        String color = "";
        QArticulo art = QArticulo.articulo1;
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
        Integer exisInicial = 0;
        Integer exisActual = 0;
        for ( KardexPorArticulo kardex : lstKardexTmp ) {
            if ( ( kardex.getFecha().after( fechaInicio ) || ( new Date( kardex.getFecha().getTime() ).equals( fechaInicio ) ) ) && ( kardex.getFecha().before( fechaFin ) || kardex.getFecha().equals( fechaFin ) ) ) {
                lstKardex.add( kardex );
            }
        }

        if ( lstKardex.size() > 0 ) {
          exisInicial = lstKardex.get( 0 ).getSaldoInicio();
          exisActual = articulo.getCantExistencia();
        } else {
          exisInicial = articulo.getCantExistencia();
          exisActual = articulo.getCantExistencia();
        }

        if( articulo.getArticulo() != null ){
          List<Precio> price = precioRepository.findByArticulo( articulo.getArticulo() );
          if(price.size() >= 1){
            precio = price.get(0).getPrecio();
          }
        }

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "articuloSku", articulo.getId() );
        parametros.put( "articuloArticulo", articulo.getArticulo() != null ? articulo.getArticulo() : "" );
        parametros.put( "articuloColor", articulo.getCodigoColor() != null ? articulo.getCodigoColor() : "" );
        parametros.put( "articuloDescripcion", articulo.getDescripcion() );
        parametros.put( "articuloPrecio", precio );
        parametros.put( "lstKardex", lstKardex );
        parametros.put( "existenciaInicial", exisInicial );
        parametros.put( "existenciaActual", exisActual );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        return null;
    }


    
    public String obtenerReporteDeVentasDelDiaActual( Date fechaVentas, Boolean artPrecioMayorCero ) {
        log.debug( "obtenerReporteDeVentasDelDiaActual()" );
        File report = new File( System.getProperty( "java.io.tmpdir" ), "Ventas-del-dia.txt" );
        org.springframework.core.io.Resource template = new ClassPathResource( VENTAS_DEL_DIA );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        Date fechaInicio = DateUtils.truncate( fechaVentas, Calendar.DAY_OF_MONTH );
        Date fechaFin = new Date( DateUtils.ceiling( fechaVentas, Calendar.DAY_OF_MONTH ).getTime() - 1 );
        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<VentasPorDia> lstVentas = reportBusiness.obtenerVentasDelDiaActual( fechaInicio, fechaFin, artPrecioMayorCero );
        List<VentasPorDia> lstCancelaciones = reportBusiness.obtenerCancelacionesDelDiaActual( fechaInicio, fechaFin, artPrecioMayorCero );
        List<VentasPorDia> lstVentasGen = reportBusiness.obtenerVentasDelDiaActualPorGenerico( fechaInicio, fechaFin, artPrecioMayorCero );
        List<VentasPorDia> lstNotasCredito = reportBusiness.obtenerNotasDeCreditoEnVentasDelDiaActual( fechaInicio, fechaFin, artPrecioMayorCero );
        Integer totalArticulos = 0;
        Integer totalFacturas = 0;
        BigDecimal totalMonto = BigDecimal.ZERO;
        BigDecimal totalDescuento = BigDecimal.ZERO;
        BigDecimal totalMontoConDescuento = BigDecimal.ZERO;
        Integer totalCanArticulos = 0;
        Integer totalCanFacturas = 0;
        BigDecimal totalCanMonto = BigDecimal.ZERO;
        BigDecimal totalCanDescuento = BigDecimal.ZERO;
        BigDecimal totalCanMontoConDescuento = BigDecimal.ZERO;
        BigDecimal totalNotaCreditoMonto = BigDecimal.ZERO;
        BigDecimal totalNotaCreditoMontoConDescuento = BigDecimal.ZERO;
        for ( VentasPorDia venta : lstVentas ) {
            if( venta.getMontoConDescuento().compareTo(BigDecimal.ZERO) > 0 ){
                totalFacturas = totalFacturas + 1;
                totalArticulos = totalArticulos + ( venta.getContadorArt() );
                totalMonto = totalMonto.add( venta.getMontoTotal() );
                totalDescuento = totalDescuento.add( venta.getMontoDescuento() );
                totalMontoConDescuento = totalMontoConDescuento.add( venta.getMontoConDescuento() );
            }
        }
        for ( VentasPorDia cancelaciones : lstCancelaciones ) {
            totalCanFacturas = totalCanFacturas + 1;
            totalCanArticulos = totalCanArticulos + ( cancelaciones.getContadorArtNeg() );
            totalCanMonto = totalCanMonto.add( cancelaciones.getMontoTotalCancelado() );
            totalCanMontoConDescuento = totalCanMontoConDescuento.add( cancelaciones.getMontoConDescCancelado() );
            totalCanDescuento = totalCanDescuento.add( cancelaciones.getMontoTotalDescuentoCan() );
        }
        for( VentasPorDia notasCredito : lstNotasCredito ){
            totalNotaCreditoMonto = totalNotaCreditoMonto.add( notasCredito.getMontoTotal() );
            totalNotaCreditoMontoConDescuento = totalNotaCreditoMontoConDescuento.add( notasCredito.getMontoConDescuento() );
            if( notasCredito.getEsNotaCredito() ){
                totalFacturas = totalFacturas-1;
            }
        }
        totalArticulos = totalArticulos - totalCanArticulos;
        totalMonto = totalMonto.subtract( totalCanMonto.add(totalNotaCreditoMonto) );
        totalMontoConDescuento = totalMontoConDescuento.subtract( totalCanMontoConDescuento.add(totalNotaCreditoMontoConDescuento) );

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaVentas ) );
        parametros.put( "horaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstVentas", lstVentas );
        parametros.put( "totalArticulos", totalArticulos );
        parametros.put( "totalFacturas", totalFacturas );
        parametros.put( "totalMonto", totalMonto );
        parametros.put( "totalDescuento", totalDescuento );
        parametros.put( "totalMontoConDescuento", totalMontoConDescuento );
        parametros.put( "totalCanMontoConDescuento", totalCanMontoConDescuento );
        parametros.put( "totalNotaCreditoMonto", totalNotaCreditoMontoConDescuento );
        parametros.put( "lstCancelaciones", lstCancelaciones );
        parametros.put( "lstVentasGen", lstVentasGen );
        parametros.put( "lstNotasCredito", lstNotasCredito );


        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        return null;
    }


    
    public String obtenerReporteDeIngresosPorPeriodo( Date dateStart, Date dateEnd ) {
        log.debug( "obtenerReporteDeIngresosPorPeriodo()" );
        File report = new File( System.getProperty( "java.io.tmpdir" ), "Ventas-del-dia.txt" );
        org.springframework.core.io.Resource template = new ClassPathResource( INGRESOS_POR_PERIODO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        Date fechaInicio = DateUtils.truncate( dateStart, Calendar.DAY_OF_MONTH );
        Date fechaFin = new Date( DateUtils.ceiling( dateEnd, Calendar.DAY_OF_MONTH ).getTime() - 1 );
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        List<IngresoPorDia> lstIngresos = reportBusiness.obtenerPagosPorPeriodo( fechaInicio, fechaFin );

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstIngresos", lstIngresos );


        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        return null;
    }



    
    public String obtenerReporteVentasMasVision( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteVentasMasVision()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Ventas-Completo%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( VENTAS_MASVISION );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );
        Sucursal sucursal = sucursalService.obtenSucursalActual();

        Parametro ivaVigenteParam = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
        Impuesto iva = impuestoRepository.findOne( ivaVigenteParam.getValor() );
        BigDecimal ivaTasa = new BigDecimal( iva.getTasa() ).divide( new BigDecimal( 100 ) );

        List<VentasPorDia> lstVentas = reportBusiness.obtenerVentasPorPeriodoMasVision( fechaInicio, fechaFin );

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalCupones = BigDecimal.ZERO;
        BigDecimal totalVentaNeta = BigDecimal.ZERO;
        for(VentasPorDia ventas : lstVentas){
          totalVentas = totalVentas.add(ventas.getMontoTotal());
          totalCupones = totalCupones.add(ventas.getMontoDescuento());
        }
        totalVentaNeta = totalVentas.subtract(totalCupones);

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "totalVentas", totalVentas );
        parametros.put( "totalCupones", totalCupones );
        parametros.put( "totalVentaNeta", totalVentaNeta );
        parametros.put( "lstVentas", lstVentas );
        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteDescuentosMasVision( Date fechaInicio, Date fechaFin, String key ) {
        log.info( "obtenerReporteDescuentos()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Descuentos%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( DESCUENTOS );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<Descuento> lstDescuentos = reportBusiness.obtenerDescuentosMasVision( fechaInicio, fechaFin, key );
        Integer totalDesc = lstDescuentos.size();
        BigDecimal importeTotalDesc = BigDecimal.ZERO;
        for(Descuento desc : lstDescuentos){
            importeTotalDesc = importeTotalDesc.add(desc.getNotaVenta() != null ? desc.getNotaVenta().getMontoDescuento() : BigDecimal.ZERO);
            if( desc.getDescuentosClave() == null ){
                desc.setDescuentosClave(new DescuentoClave());
                if( isNumeric(StringUtils.trimToEmpty(desc.getClave())) ){
                    desc.getDescuentosClave().setClave_descuento(desc.getClave());
                    desc.getDescuentosClave().setDescripcion_descuento("DIRECCION");
                } else {
                    desc.getDescuentosClave().setClave_descuento(desc.getClave());
                    desc.getDescuentosClave().setDescripcion_descuento(desc.getClave().startsWith("8") ? "CUPON 2P" : "CUPON 3P");
                }
            }
        }

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstDescuentos", lstDescuentos );
        parametros.put( "totalDesc", totalDesc );
        parametros.put( "importeTotalDesc", importeTotalDesc );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    
    public String obtenerReporteDeCupones( Date dateStart, Date dateEnd ){
      log.info( "obtenerReporteDeCupones()" );

      Random random = new Random();
      File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Cupones%s.txt", random.nextInt()) );
      org.springframework.core.io.Resource template = new ClassPathResource( CUPONES );
      log.info( "Ruta:{}", report.getAbsolutePath() );

      dateStart = DateUtils.truncate( dateStart, Calendar.DAY_OF_MONTH );
      dateEnd = new Date( DateUtils.ceiling( dateEnd, Calendar.DAY_OF_MONTH ).getTime() - 1 );
      Sucursal sucursal = sucursalService.obtenSucursalActual();

      Parametro ivaVigenteParam = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.getValue() );
      Impuesto iva = impuestoRepository.findOne( ivaVigenteParam.getValor() );
      BigDecimal ivaTasa = new BigDecimal( iva.getTasa() ).divide( new BigDecimal( 100 ) );

      List<VentasPorDia> lstCupones = reportBusiness.obtenerVentasPorCupones( dateStart, dateEnd );
      Collections.sort( lstCupones, new Comparator<VentasPorDia>() {
          @Override
          public int compare(VentasPorDia o1, VentasPorDia o2) {
              return o1.getFactura().compareToIgnoreCase(o2.getFactura());
          }
      });

      Map<String, Object> parametros = new HashMap<String, Object>();
      parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
      parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( dateStart ) );
      parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( dateEnd ) );
      parametros.put( "sucursal", sucursal.getNombre() );
      parametros.put( "lstCupones", lstCupones );
      String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
      log.info( "reporte:{}", reporte );

      return null;
    }


    public String obtenerReporteDeCuponesMv( Date dateStart, Date dateEnd, Boolean todo, Boolean noAplicados, Boolean extemporaneos ){
      log.info( "obtenerReporteDeCuponesMv()" );

      Random random = new Random();
      File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Cupones%s.txt", random.nextInt()) );
      org.springframework.core.io.Resource template = new ClassPathResource( CUPONESMV );
      log.info( "Ruta:{}", report.getAbsolutePath() );

      dateStart = DateUtils.truncate( dateStart, Calendar.DAY_OF_MONTH );
      dateEnd = new Date( DateUtils.ceiling( dateEnd, Calendar.DAY_OF_MONTH ).getTime() - 1 );
      Sucursal sucursal = sucursalService.obtenSucursalActual();
      String titulo = "";
      if( todo ){
        titulo = "TOTAL";
      } else if( noAplicados ) {
        titulo = "NO APLICADOS";
      } else if( extemporaneos ){
        titulo = "EXTEMPORANEOS";
      }

      List<CuponesMvDesc> lstCuponesMv = reportBusiness.obtenerVentasPorCuponesMv( dateStart, dateEnd, todo, noAplicados, extemporaneos );
      Collections.sort(lstCuponesMv, new Comparator<CuponesMvDesc>() {
          @Override
          public int compare(CuponesMvDesc o1, CuponesMvDesc o2) {
              return o1.getFacturaOri().compareTo(o2.getFacturaOri());
          }
      });
      Integer total2Par = 0;
      Integer total3Par = 0;
      Integer aplicados2Par = 0;
      Integer aplicados3Par = 0;
      Integer cuponesApli = 0;
      Double porcentajeCupApli = 0.00;
      Double porcentajeCupApli2 = 0.00;
      Double porcentajeCupApli3 = 0.00;
      String porcentajeFormat = "";
      String porcentajeFormat2 = "";
      String porcentajeFormat3 = "";
      for(CuponesMvDesc cuponesMvDesc : lstCuponesMv){
        if( StringUtils.trimToEmpty(cuponesMvDesc.getTipoCupon()).equalsIgnoreCase("2") ){
          total2Par = total2Par+1;
          if( cuponesMvDesc.getFechaAplic() != null ){
            aplicados2Par = aplicados2Par+1;
          }
        } else if( StringUtils.trimToEmpty(cuponesMvDesc.getTipoCupon()).equalsIgnoreCase("3") ){
          total3Par = total3Par+1;
          if( cuponesMvDesc.getFechaAplic() != null ){
                aplicados3Par = aplicados3Par+1;
          }
        }
        if( cuponesMvDesc.getFechaAplic() != null ){
          cuponesApli = cuponesApli+1;
        }
      }
      if( cuponesApli > 0 ){
        porcentajeCupApli = (double) (cuponesApli * 100) / lstCuponesMv.size();
        porcentajeFormat = String.format("%.2f %s", porcentajeCupApli, "%");
        porcentajeCupApli2 = (double) (aplicados2Par * 100) / total2Par;
        porcentajeFormat2 = String.format("%.2f %s", porcentajeCupApli2, "%");
        porcentajeCupApli3 = (double) (aplicados3Par * 100) / total3Par;
        porcentajeFormat3 = String.format("%.2f %s", porcentajeCupApli3, "%");
      }
      Map<String, Object> parametros = new HashMap<String, Object>();
      parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
      parametros.put( "sucursal", sucursal.getNombre() );
      parametros.put( "lstCuponesMv", lstCuponesMv );
      parametros.put( "totalCupones", lstCuponesMv.size() );
      parametros.put( "total2Par", total2Par );
      parametros.put( "total3Par", total3Par );
      parametros.put( "cuponesApli", cuponesApli );
      parametros.put( "porcentajeCupApli",  porcentajeFormat);
      parametros.put( "aplicados2Par", aplicados2Par );
      parametros.put( "aplicados3Par", aplicados3Par );
      parametros.put( "porcentajeCupApli",  porcentajeFormat);
      parametros.put( "porcentajeCupApli2",  porcentajeFormat2);
      parametros.put( "porcentajeCupApli3",  porcentajeFormat3);
      parametros.put( "extemporaneos",  extemporaneos);
      parametros.put( "titulo",  titulo);
      parametros.put( "todo",  todo);
      String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
      log.info( "reporte:{}", reporte );

      return null;
    }



    public String obtenerReporteTrabajosSinEntregarAuditoria() {
        log.info( "obtenerReporteTrabajosSinEntregarAuditoria()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Trabajos-Sin-Entregar-Auditoria%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( TRABAJOS_SIN_ENTREGAR_AUDITORIA );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<TrabajosSinEntregar> lstTrabajosSinEntregar = reportBusiness.obtenerTrabajosSinEntregarAuditoria( );

        Integer totalFacturas = 0;
        BigDecimal totalVenta = BigDecimal.ZERO;
        BigDecimal totalSaldo = BigDecimal.ZERO;

        Collections.sort( lstTrabajosSinEntregar, new Comparator<TrabajosSinEntregar>() {
            @Override
            public int compare(TrabajosSinEntregar o1, TrabajosSinEntregar o2) {
                return o1.getFactura().compareTo(o2.getFactura());
            }
        });
        for(TrabajosSinEntregar trabajo : lstTrabajosSinEntregar){
          totalFacturas = totalFacturas+1;
          totalVenta = totalVenta.add(trabajo.getMonto());
          totalSaldo = totalSaldo.add(trabajo.getSaldo());
        }

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstTrabajosSinEntregar", lstTrabajosSinEntregar );
        parametros.put( "totalFacturas", totalFacturas );
        parametros.put( "totalVenta", totalVenta );
        parametros.put( "totalSaldo", totalSaldo );
        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    public String obtenerReporteExamenesPorOpto( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteExamenesPorOpto( )" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Examenes-Por-Optometrista%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( EXAMENES_POR_OPTOMETRISTA );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<DescuentosPorTipo> lstExamenes = reportBusiness.obtenerExamenesporOptometrista( fechaInicio, fechaFin );

        Integer total = 0;
        Integer totalVenta = 0;
        Integer totalCotiza = 0;
        Integer totalNoVenta = 0;
        BigDecimal porcentajeVentas = BigDecimal.ZERO;
        for(DescuentosPorTipo empleado : lstExamenes){
          if(empleado.getIdEmpleado().trim().equalsIgnoreCase("9999")){
            empleado.setNombreEmpleado( "PROCESO" );
          }
          total = total+empleado.getTotal();
          totalVenta = totalVenta+empleado.getRxConVenta();
          totalCotiza = totalCotiza+empleado.getRxCotizacion();
          totalNoVenta = totalNoVenta+empleado.getRxSinVenta();
          porcentajeVentas = new BigDecimal(new Double(totalVenta)/new Double(total == 0 ? 1 : total));
        }

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstExamenes", lstExamenes );
        parametros.put( "total", total );
        parametros.put( "totalVenta", totalVenta );
        parametros.put( "totalCotiza", totalCotiza );
        parametros.put( "totalNoVenta", totalNoVenta );
        parametros.put( "porcentajeVentas", porcentajeVentas );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }


    public String obtenerReporteExamenesPorOptoCompleto( Date fechaInicio, Date fechaFin ) {
        log.info( "obtenerReporteExamenesPorOptoCompleto( )" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Examenes-Por-Optometrista-Detallado%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( EXAMENES_POR_OPTOMETRISTA_COMPLETO );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        fechaInicio = DateUtils.truncate( fechaInicio, Calendar.DAY_OF_MONTH );
        fechaFin = new Date( DateUtils.ceiling( fechaFin, Calendar.DAY_OF_MONTH ).getTime() - 1 );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<DescuentosPorTipo> lstExamenes = reportBusiness.obtenerExamenesporOptometrista( fechaInicio, fechaFin );

        Integer total = 0;
        Integer totalVenta = 0;
        Integer totalCotiza = 0;
        Integer totalNoVenta = 0;
        BigDecimal porcentajeVentas = BigDecimal.ZERO;
        for(DescuentosPorTipo empleado : lstExamenes){
            if(empleado.getIdEmpleado().trim().equalsIgnoreCase("9999")){
                empleado.setNombreEmpleado( "PROCESO" );
            }
            total = total+empleado.getTotal();
            totalVenta = totalVenta+empleado.getRxConVenta();
            totalCotiza = totalCotiza+empleado.getRxCotizacion();
            totalNoVenta = totalNoVenta+empleado.getRxSinVenta();
            porcentajeVentas = new BigDecimal(new Double(totalVenta)/new Double(total == 0 ? 1 : total));
        }

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaInicio", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaInicio ) );
        parametros.put( "fechaFin", new SimpleDateFormat( "dd/MM/yyyy" ).format( fechaFin ) );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstExamenes", lstExamenes );
        parametros.put( "total", total );
        parametros.put( "totalVenta", totalVenta );
        parametros.put( "totalCotiza", totalCotiza );
        parametros.put( "totalNoVenta", totalNoVenta );
        parametros.put( "porcentajeVentas", porcentajeVentas );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }



    public String obtenerReporteBodegas( Date fechaBodega ){
        log.info( "obtenerReporteBodegas()" );

        Random random = new Random();
        File report = new File( System.getProperty( "java.io.tmpdir" ), String.format("Bodegas%s.txt", random.nextInt()) );
        org.springframework.core.io.Resource template = new ClassPathResource( BODEGAS );
        log.info( "Ruta:{}", report.getAbsolutePath() );

        Sucursal sucursal = sucursalService.obtenSucursalActual();
        List<Bodegas> lstBodegas = reportBusiness.obtenerBodegas( fechaBodega );
        Integer totalFacturas = 0;
        BigDecimal ventaTotal = BigDecimal.ZERO;
        BigDecimal saldoTotal = BigDecimal.ZERO;
        for(Bodegas bodega : lstBodegas){
          totalFacturas = totalFacturas+bodega.getCantFacturas();
          ventaTotal = ventaTotal.add(bodega.getTotalVenta());
          saldoTotal = saldoTotal.add(bodega.getTotalSaldo());
        }
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put( "fechaActual", new SimpleDateFormat( "hh:mm" ).format( new Date() ) );
        parametros.put( "fechaBodegas", fechaBodega );
        parametros.put( "sucursal", sucursal.getNombre() );
        parametros.put( "lstBodegas", lstBodegas );
        parametros.put( "totalFacturas", totalFacturas );
        parametros.put( "ventaTotal", ventaTotal );
        parametros.put( "saldoTotal", saldoTotal );

        String reporte = reportBusiness.CompilayGeneraReporte( template, parametros, report );
        log.info( "reporte:{}", reporte );

        return null;
    }

    public static boolean isNumeric(String str) {
      try {
        double d = Double.parseDouble(str);
      } catch(NumberFormatException nfe) {
        return false;
      }
      return true;
    }
}
