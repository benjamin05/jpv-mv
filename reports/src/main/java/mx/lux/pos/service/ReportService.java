package mx.lux.pos.service;

import java.util.Date;

public interface ReportService {

    
    String obtenerReporteCierreDiario( Date fecha );

    
    String obtenerReporteIngresosXSucursal( Date fechaInicio, Date fechaFin );



    
    String obtenerReporteVentas( Date fechaInicio, Date fechaFin );


    
    String obtenerReporteVentasporVendedorCompleto( Date fechaInicio, Date fechaFin );

    
    String obtenerReporteTrabajosSinEntregar();


    String obtenerReporteTrabajosSinEntregarAuditoria();

    
    String obtenerReporteCancelacionesResumido( Date fechaInicio, Date fechaFin );

    
    String obtenerReporteCancelacionesCompleto( Date fechaInicio, Date fechaFin );

    
    String obtenerReporteVentasporLineaFactura( Date fechaInicio, Date fechaFin, String articulo, boolean gogle, boolean oftalmico, boolean todo );

    
    String obtenerReporteVentasporLineaArticulo( Date fechaInicio, Date fechaFin, String articulo, boolean gogle, boolean oftalmico, boolean todo );

    
    String obtenerReporteVentasMarca( Date fechaInicio, Date fechaFin, String marca, boolean noMostrarArticulos, boolean gogle, boolean oftalmico, boolean todo );

    
    String obtenerReporteVentasVendedorporMarca( Date fechaInicio, Date fechaFin, String marca, boolean mostrarArticulos, boolean gogle, boolean oftalmico, boolean todo );

    
    String obtenerReporteExistenciasporMarca( String marca, boolean gogle, boolean oftalmico, boolean todo );

    
    String obtenerReporteExistenciasporMarcaResumido( String marca, boolean gogle, boolean oftalmico, boolean todo );

    
    String obtenerReporteExistenciasporArticulo( String marca, String descripcion, String color );

    
    String obtenerReporteControldeTrabajos( boolean retenidos, boolean porEnviar, boolean pino, boolean sucursal, boolean todos, boolean factura, boolean fechaPromesa );

    
    String obtenerReporteTrabajosEntregados( Date fechaInicio, Date fechaFin );

    
    String obtenerReporteTrabajosEntregadosporEmpleado( Date fechaInicio, Date fechaFin );

    
    String obtenerReporteVentasCompleto( Date fechaInicio, Date fechaFin );

    
    String obtenerReporteFacturasFiscales( Date fechaInicio, Date fechaFin );

    
    String obtenerReporteDescuentos( Date fechaInicio, Date fechaFin );

    
    String obtenerReportePromocionesAplicadas( Date fechaInicio, Date fechaFin );

    
    String obtenerReportePagos( Date fechaInicio, Date fechaFin, String fromaPago, String factura );

    
    String obtenerReporteCotizaciones( Date fechaInicio, Date fechaFin );

    
    String obtenerReporteExamenesResumido( Date fechaInicio, Date fechaFin );

    
    String obtenerReporteExamenesCompleto( Date fechaInicio, Date fechaFin );

    
    String obtenerReporteVentasporOptometrista( Date fechaInicio, Date fechaFin );

    
    String obtenerReporteVentasporOptometristaResumido( Date fechaInicio, Date fechaFin, boolean todoTipo, boolean referido, boolean rx,
                                                        boolean lux, boolean todaVenta, boolean primera, boolean mayor, boolean resumen );

    
    String obtenerReportePromociones( Date fechaImpresion );

    
    String obtenerReporteDeKardex( String articulo, Date fechaInicio, Date fechaFin );

    
    public String obtenerReporteDeVentasDelDiaActual( Date fechaVentas, Boolean artPrecioMayorCero );

    
    public String obtenerReporteDeIngresosPorPeriodo( Date dateStart, Date dateEnd );

    
    String obtenerReporteVentasMasVision( Date fechaInicio, Date fechaFin );

    
    public String obtenerReporteDescuentosMasVision( Date fechaInicio, Date fechaFin, String key );

    
    public String obtenerReporteDeCupones( Date dateStart, Date dateEnd );


    public String obtenerReporteDeCuponesMv( Date dateStart, Date dateEnd, Boolean todo, Boolean noAplicados, Boolean extemporaneos );


    String obtenerReporteExamenesPorOpto( Date fechaInicio, Date fechaFin );


    String obtenerReporteExamenesPorOptoCompleto( Date fechaInicio, Date fechaFin );


    String obtenerReporteBodegas( Date fechaBodega );


    String obtenerReporteVentasMultipago( Date dateStart, Date dateEnd );


    String obtenerArticuloPorSku( String Sku );


    String obtenerReporteSubgerentesAsignados( Date dateStart, Date dateEnd );


    String obtenerReporteChecadasPorFecha( Date dateStart, Date dateEnd );
}
