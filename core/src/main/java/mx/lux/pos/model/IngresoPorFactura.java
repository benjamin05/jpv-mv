package mx.lux.pos.model;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class IngresoPorFactura {

    private String idFactura;
    private Integer idArticulo;
    private String marca;
    private String tipo;
    private String color;
    private String descripcion;
    private BigDecimal montoPago;
    private BigDecimal montoPagoIVA;
    private BigDecimal montoPagoSinIVA;
    private BigDecimal montoDevolucion;

    private BigDecimal montoDescuento;
    private BigDecimal montoConDesc;
    private BigDecimal montoSinDesc;

    private BigDecimal acumulaPago;
    private BigDecimal acumulaPagoIva;
    private BigDecimal acumulaDevolucion;
    private BigDecimal total;
    private BigDecimal totalCupon;
    private BigDecimal iva;
    private BigDecimal promedio;
    private Date fechaPago;
    private Date fechaCancelacion;
    private Integer modId;
    private Integer existencia;
    private Set<DetalleNotaVenta> lstDetalles;
    private BigDecimal contador;
    private String paciente;
    private List<String> lstIdsArticulos;
    private BigDecimal sumaMonto;
    private Boolean mostrarArticulos;
    private String idGenerico;
    private String tipoCan;
    private String factTransf;
    private Integer noFacturas;
    private List<DetalleNotaVenta> lstArticulos;
    private String articulos;
    private static final String TAG_CANCELADO = "T";
    private static final String TAG_DEVUELTO = "d";
    private static final String TAG_TRANSFERIDO = "t";
    private static final String TAG_CUPON = "C";
    private static final String TAG_BODEGA = "BD";

    public IngresoPorFactura( String idFactura ) {
        this.idFactura = idFactura;
        montoPago = BigDecimal.valueOf( 0 );
        montoDevolucion = BigDecimal.valueOf( 0 );
        acumulaDevolucion = BigDecimal.valueOf( 0 );
        acumulaPago = BigDecimal.valueOf( 0 );
        montoPagoIVA = BigDecimal.valueOf( 0 );
        montoPagoSinIVA = BigDecimal.valueOf( 0 );
        iva = BigDecimal.valueOf( 0 );
        contador = BigDecimal.valueOf( 0 );
        acumulaPagoIva = BigDecimal.valueOf( 0 );
        idArticulo = 0;
        existencia = 0;
        noFacturas = 0;
        lstIdsArticulos = new ArrayList<String>();
        sumaMonto = BigDecimal.ZERO;
        mostrarArticulos = true;
        lstArticulos = new ArrayList<DetalleNotaVenta>();
        montoDescuento = BigDecimal.ZERO;
        montoConDesc = BigDecimal.ZERO;
        montoSinDesc = BigDecimal.ZERO;
        factTransf = "";
        tipo = "";
        articulos = "";
        totalCupon = BigDecimal.ZERO;
        total = BigDecimal.ZERO;
        descripcion = "";
    }

    public IngresoPorFactura( BigDecimal monto ) {
        montoPago = monto;
        montoPago = BigDecimal.valueOf( 0 );
        montoDevolucion = BigDecimal.valueOf( 0 );
        acumulaDevolucion = BigDecimal.valueOf( 0 );
        acumulaPago = BigDecimal.valueOf( 0 );
        montoPagoIVA = BigDecimal.valueOf( 0 );
        montoPagoSinIVA = BigDecimal.valueOf( 0 );
        iva = BigDecimal.valueOf( 0 );
        contador = BigDecimal.valueOf( 0 );
        acumulaPagoIva = BigDecimal.valueOf( 0 );
    }

    public void AcumulaPago( BigDecimal pago, Date fecha ) {
        montoPago = montoPago.add( pago );
        fechaPago = fecha;
    }

    public void AcumulaVentaPorVendedor( NotaVenta nota, String pagosNoTransf ) {
        fechaPago = nota.getFechaHoraFactura();
        for(DetalleNotaVenta det : nota.getDetalles()){
          String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
          descripcion = descripcion+","+det.getArticulo().getArticulo()+color;
        }
        total = total.add(nota.getVentaTotal());
        descripcion = descripcion.replaceFirst(",","");
        for(Pago pago : nota.getPagos()){
          if( pagosNoTransf.contains(pago.getIdFPago().trim()) ){
            totalCupon = totalCupon.add(pago.getMonto());
          }
        }
        sumaMonto = total.subtract(totalCupon);
        for(Pago pago : nota.getPagos()){
            if( TAG_BODEGA.equalsIgnoreCase(pago.getIdFPago().trim()) ){
                totalCupon = totalCupon.subtract(pago.getMonto());
            }
        }
    }


    public void AcumulaVentaBodPorVendedor( NotaVenta nota, Pago pago ) {
        fechaPago = nota.getFechaHoraFactura();
        for(DetalleNotaVenta det : nota.getDetalles()){
            String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
            descripcion = descripcion+","+det.getArticulo().getArticulo()+color;
        }
        total = total.subtract(pago.getMonto());
        descripcion = descripcion.replaceFirst(",","");
        sumaMonto = sumaMonto.add(total);
    }


    public void AcumulaCancelaciones( BigDecimal pago, Date fecha ) {
        montoPago = montoPago.add( pago ).negate();
        fechaPago = fecha;
    }

    public void AcumulaCancelacionesPorVendedor( Modificacion modificacion, String pagosNoTransf ) {
        fechaPago = modificacion.getFecha();
        for(DetalleNotaVenta det : modificacion.getNotaVenta().getDetalles()){
            String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
            descripcion = descripcion+","+det.getArticulo().getArticulo()+color;
        }
        descripcion = descripcion.replaceFirst(",","");
        total = total.subtract(modificacion.getNotaVenta().getVentaTotal());
        for(Pago pago : modificacion.getNotaVenta().getPagos()){
            if( pagosNoTransf.contains(pago.getIdFPago().trim()) ){
              totalCupon = totalCupon.subtract(pago.getMonto());
            }
        }
        sumaMonto = total.subtract(totalCupon);
        for(Pago pago : modificacion.getNotaVenta().getPagos()){
            if( TAG_BODEGA.equalsIgnoreCase(pago.getIdFPago().trim()) ){
                totalCupon = totalCupon.add(pago.getMonto());
            }
        }
    }

    public void AcumulaNotasCredito( BigDecimal pago, Date fecha ) {
        montoPago = montoPago.subtract( pago );
        fechaPago = fecha;
    }

    public void AcumulaCancelacionesSinIva(  BigDecimal pago, Date fecha, BigDecimal montoIva, Integer piezas ) {
        montoPagoSinIVA = pago.negate();
        acumulaPago = (acumulaPago.add( pago )).negate();
        montoPagoIVA = (acumulaPago.add( acumulaPago.multiply( montoIva ) )).negate();
        fechaPago = fecha;
        iva = montoIva;
        contador = new BigDecimal( contador.intValue() - 1 );
        sumaMonto.add( montoPagoSinIVA );
        promedio = sumaMonto.divide( contador, 10, RoundingMode.CEILING );
        noFacturas = noFacturas-1;
        existencia = existencia-piezas;
    }

    public void AcumulaNotasCreditoSinIva( BigDecimal pago, Date fecha, BigDecimal montoIva, Boolean isNotaCredito ) {
        montoPagoSinIVA = montoPagoSinIVA.subtract(pago);
        acumulaPago = (acumulaPago.subtract( pago ));
        montoPagoIVA = (acumulaPago.add( acumulaPago.multiply( montoIva ) )).negate();
        fechaPago = fecha;
        iva = montoIva;
        if( isNotaCredito ){
            contador = new BigDecimal( contador.intValue() - 1 );
        }
        if( contador.compareTo(BigDecimal.ZERO) == 0 ){
            promedio = BigDecimal.ZERO;
        } else {
            promedio = montoPagoSinIVA.divide( contador, 10, RoundingMode.CEILING );
        }
    }

    public void AcumulaPagosinIva( BigDecimal pago, Date fecha, BigDecimal montoIva, Integer piezas, BigDecimal noFacturas ) {
        montoPagoSinIVA = pago;
        acumulaPago = acumulaPago.add( pago );
        montoPagoIVA = acumulaPago.add( acumulaPago.multiply( montoIva ) );
        fechaPago = fecha;
        iva = montoIva;
        contador = new BigDecimal( contador.intValue() + 1 );
        sumaMonto = sumaMonto.add( montoPagoSinIVA );
        promedio = montoPagoSinIVA.divide( contador, 10, RoundingMode.CEILING );
        existencia = piezas;
        this.noFacturas = this.noFacturas+1;
    }

    public void AcumulaDevolucion( BigDecimal devolucion ) {
        montoDevolucion = montoDevolucion.add( devolucion );
    }

    public void AcumulaFacturas(  Modificacion modificacion ) {
        montoPago = modificacion.getNotaVenta().getVentaNeta();
        fechaPago = modificacion.getNotaVenta().getFechaHoraFactura();
        fechaCancelacion = modificacion.getFecha();
        modId = modificacion.getId();
        idFactura = modificacion.getNotaVenta().getFactura();
        lstDetalles = modificacion.getNotaVenta().getDetalles();
        for(Devolucion dev : modificacion.getDevolucion()){
            if(StringUtils.trimToEmpty(dev.getTipo()).equalsIgnoreCase(TAG_DEVUELTO) && !tipo.contains("Devolucion") ){
                tipo = tipo + ", Dev.";
            } else if(StringUtils.trimToEmpty(dev.getTipo()).equalsIgnoreCase(TAG_TRANSFERIDO) && !tipo.contains("Transferencia")){
                tipo = tipo + ", Trans.";
            }

            if(StringUtils.trimToEmpty(dev.getTransf()) != "" && dev.getNotaVenta() != null){
                factTransf = factTransf + ", " + dev.getNotaVenta().getFactura().trim();
            }
        }
        tipo = tipo.replaceFirst( ", ", "" );
        factTransf = factTransf.replaceFirst( ", ", "" );
    }

    public void AcumulaMarca(  Articulo articulo,  Precio precio ) {
        String color = (articulo.getCodigoColor() != null && articulo.getCodigoColor().trim().length() > 0) ? "["+articulo.getCodigoColor().trim()+"]" : "";
        idArticulo = articulo.getId();
        marca = articulo.getArticulo()+color;
        this.color = articulo.getCodigoColor();
        descripcion = articulo.getDescripcion();
        acumulaPago = precio.getPrecio();
        idGenerico = articulo.getIdGenerico();
        this.existencia = articulo.getCantExistencia();
    }

    public void AcumulaMarcaResumido(  Articulo articulo ) {
        marca = articulo.getMarca();
        this.existencia = existencia+articulo.getCantExistencia();
    }


    public void AcumulaMarcas( boolean mostrarArticulos, String idArticulo,  DetalleNotaVenta notaVenta,  BigDecimal importe, Double iva, Date fecha, String articulo, String descripcion ) {
        fechaPago = fecha;
        marca = articulo;
        montoPago = montoPago.add( importe.multiply( new BigDecimal(notaVenta.getCantidadFac()) ) );
        acumulaPago = acumulaPago.add( montoPago );
        montoPagoIVA = new BigDecimal( montoPago.doubleValue()/( 1+iva ) );
        acumulaPagoIva = acumulaPagoIva.add( montoPagoIVA );
        tipo = descripcion;
        Boolean esNotaCrecdito = false;
        for( Pago pago : notaVenta.getNotaVenta().getPagos() ){
            if( "NOT".equalsIgnoreCase(pago.getIdFPago() ) ){
                esNotaCrecdito = true;
            }
        }
        if( !esNotaCrecdito ){
            for(int i = 0; i < notaVenta.getCantidadFac().intValue(); i++){
                lstIdsArticulos.add( idArticulo );
            }
        }
        contador = contador.add(new BigDecimal(notaVenta.getCantidadFac()));
        this.mostrarArticulos = mostrarArticulos;
    }

    public void AcumulaMarcasCan(  DetalleNotaVenta detalles, String idArticulo,  BigDecimal importe, Double iva, Date fecha, String articulo, String descripcion ) {
        fechaPago = fecha;
        marca = articulo;
        montoPago = montoPago.subtract( importe.multiply( new BigDecimal(detalles.getCantidadFac()) ) );
        acumulaPago = acumulaPago.add( montoPago );
        montoPagoIVA = new BigDecimal( montoPago.doubleValue()/( 1+iva ) );
        acumulaPagoIva = acumulaPagoIva.add( montoPagoIVA );
        tipo = descripcion;
        //lstIdsArticulos.add( idArticulo );
        contador = contador.subtract( new BigDecimal(detalles.getCantidadFac()) );
    }

    public void AcumulaPagosMarcasNotasCredito( Integer cantArticulos,  DetalleNotaVenta notaVenta, BigDecimal importe, Double iva, Date fecha, String articulo, String descripcion ) {
        fechaPago = fecha;
        marca = articulo;
        montoPago = montoPago.subtract( notaVenta.getPrecioUnitFinal().multiply(new BigDecimal(notaVenta.getCantidadFac())) );
        acumulaPago = acumulaPago.add( montoPago );
        montoPagoIVA = new BigDecimal( montoPago.doubleValue()/( 1+iva ) );
        acumulaPagoIva = acumulaPagoIva.add( montoPagoIVA );
    }

    public void AcumulaArticulosMarcasNotasCredito( Integer cantArticulos,  DetalleNotaVenta notaVenta, BigDecimal importe, Double iva, Date fecha, String articulo, String descripcion ) {
        contador = contador.subtract( new BigDecimal(notaVenta.getCantidadFac()) );
    }

    public void AcumulaVentasOpto(  NotaVenta venta, String pagosNoTransf ) {
        for(DetalleNotaVenta det : venta.getDetalles()){
          String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
          articulos =  articulos + ", " + det.getArticulo().getArticulo().trim()+color;
        }
        articulos = articulos.replaceFirst( ", ", "" );
        fechaPago = venta.getFechaHoraFactura();
        idFactura = venta.getFactura();
        montoSinDesc = montoSinDesc.add( venta.getVentaTotal() );
        for(Pago pago : venta.getPagos()){
          if(pagosNoTransf.contains(pago.getIdFPago().trim())){
            montoDescuento = montoDescuento.add( pago.getMonto() );
          }
        }
        montoConDesc = montoSinDesc.subtract(montoDescuento);
        paciente = venta.getCliente().getNombreCompleto();
        for(Pago pago : venta.getPagos()){
            if(TAG_BODEGA.equalsIgnoreCase(pago.getIdFPago().trim())){
                montoDescuento = montoDescuento.subtract( pago.getMonto() );
            }
        }
    }


    public void AcumulaVentasBodOpto(  NotaVenta venta, String pagosNoTransf ) {
        for(DetalleNotaVenta det : venta.getDetalles()){
            String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
            articulos =  articulos + ", " + det.getArticulo().getArticulo().trim()+color;
        }
        articulos = articulos.replaceFirst( ", ", "" );
        fechaPago = venta.getFechaHoraFactura();
        idFactura = venta.getFactura();
        for(Pago pago : venta.getPagos()){
            if(pagosNoTransf.contains(pago.getIdFPago().trim())){
                montoSinDesc = montoSinDesc.subtract( pago.getMonto() );
            }
        }
        montoConDesc = montoConDesc.add(montoSinDesc);
        paciente = venta.getCliente().getNombreCompleto();
    }



    public void AcumulaVentasCanOpto(  NotaVenta venta, String pagoNoTransf ) {
        for(DetalleNotaVenta det : venta.getDetalles()){
            String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
            articulos =  articulos + ", " + det.getArticulo().getArticulo().trim()+color;
        }
        articulos = articulos.replaceFirst( ", ", "" );
        fechaPago = venta.getFechaHoraFactura();
        idFactura = venta.getFactura();
        montoSinDesc = montoSinDesc.subtract( venta.getVentaTotal() );
        for(Pago pago : venta.getPagos()){
            if(pagoNoTransf.contains(pago.getIdFPago().trim())){
                montoDescuento = montoDescuento.subtract( pago.getMonto() );
            }
        }
        montoConDesc = montoSinDesc.subtract(montoDescuento);
        paciente = venta.getCliente().getNombreCompleto();
        for(Pago pago : venta.getPagos()){
            if(TAG_BODEGA.equalsIgnoreCase(pago.getIdFPago().trim())){
                montoDescuento = montoDescuento.add( pago.getMonto() );
            }
        }
    }

    public void AcumulaVentasOptoMayor(  NotaVenta venta ) {
        fechaPago = venta.getFechaHoraFactura();
        idFactura = venta.getFactura();
        //montoPago = venta.getVentaNeta();
        paciente = venta.getCliente().getNombreCompleto();
    }

    public String getIdFactura() {
        return idFactura;
    }

    public BigDecimal getMontoPago() {
        return montoPago;
    }

    public BigDecimal getMontoDevolucion() {
        return montoDevolucion;
    }

    public BigDecimal getAcumulaPago() {
        return acumulaPago;
    }

    public BigDecimal getAcumulaDevolucion() {
        return acumulaDevolucion;
    }

    public BigDecimal getTotal() {

        return total;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public BigDecimal getMontoPagoIVA() {
        return montoPagoIVA;
    }

    public BigDecimal getMontoPagoSinIVA() {
        return montoPagoSinIVA;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public Date getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion( Date fechaCancelacion ) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public Integer getModId() {
        return modId;
    }

    public void setModId( Integer modId ) {
        this.modId = modId;
    }

    
    public Set<DetalleNotaVenta> getLstDetalles() {
        return lstDetalles;
    }

    public void setLstDetalles( Set<DetalleNotaVenta> lstDetalles ) {
        this.lstDetalles = lstDetalles;
    }

    public void setIdFactura( String idFactura ) {
        this.idFactura = idFactura;
    }

    public void setMontoPago( BigDecimal montoPago ) {
        this.montoPago = montoPago;
    }

    public void setMontoPagoIVA( BigDecimal montoPagoIVA ) {
        this.montoPagoIVA = montoPagoIVA;
    }

    public void setMontoPagoSinIVA( BigDecimal montoPagoSinIVA ) {
        this.montoPagoSinIVA = montoPagoSinIVA;
    }

    public void setMontoDevolucion( BigDecimal montoDevolucion ) {
        this.montoDevolucion = montoDevolucion;
    }

    public void setAcumulaPago( BigDecimal acumulaPago ) {
        this.acumulaPago = acumulaPago;
    }

    public void setAcumulaDevolucion( BigDecimal acumulaDevolucion ) {
        this.acumulaDevolucion = acumulaDevolucion;
    }

    public void setTotal( BigDecimal total ) {
        this.total = total;
    }

    public void setIva( BigDecimal iva ) {
        this.iva = iva;
    }

    public void setFechaPago( Date fechaPago ) {
        this.fechaPago = fechaPago;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca( String marca ) {
        this.marca = marca;
    }

    public BigDecimal getContador() {
        return contador;
    }

    public void setContador( BigDecimal contador ) {
        this.contador = contador;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo( String tipo ) {
        this.tipo = tipo;
    }

    public BigDecimal getAcumulaPagoIva() {
        return acumulaPagoIva;
    }

    public void setAcumulaPagoIva( BigDecimal acumulaPagoIva ) {
        this.acumulaPagoIva = acumulaPagoIva;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente( String paciente ) {
        this.paciente = paciente;
    }

    public String getColor() {
        return color;
    }

    public void setColor( String color ) {
        this.color = color;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion( String descripcion ) {
        this.descripcion = descripcion;
    }

    public Integer getExistencia() {
        return existencia;
    }

    public void setExistencia( Integer existencia ) {
        this.existencia = existencia;
    }

    public Integer getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo( Integer idArticulo ) {
        this.idArticulo = idArticulo;
    }

    public List<String> getLstIdsArticulos() {
        return lstIdsArticulos;
    }

    public void setLstIdsArticulos( List<String> lstIdsArticulos ) {
        this.lstIdsArticulos = lstIdsArticulos;
    }

    public BigDecimal getPromedio() {
        return promedio;
    }

    public void setPromedio( BigDecimal promedio ) {
        this.promedio = promedio;
    }

    public Boolean getMostrarArticulos() {
        return mostrarArticulos;
    }

    public void setMostrarArticulos( Boolean mostrarArticulos ) {
        this.mostrarArticulos = mostrarArticulos;
    }

    public String getIdGenerico() {
        return idGenerico;
    }

    public void setIdGenerico(String idGenerico) {
        this.idGenerico = idGenerico;
    }

    public BigDecimal getSumaMonto() {
        return sumaMonto;
    }

    public void setSumaMonto(BigDecimal sumaMonto) {
        this.sumaMonto = sumaMonto;
    }

    public Integer getNoFacturas() {
        return noFacturas;
    }

    public void setNoFacturas(Integer noFacturas) {
        this.noFacturas = noFacturas;
    }

    public List<DetalleNotaVenta> getLstArticulos() {
        return lstArticulos;
    }

    public void setLstArticulos(List<DetalleNotaVenta> lstArticulos) {
        this.lstArticulos = lstArticulos;
    }

    public BigDecimal getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(BigDecimal montoDescuento) {
        this.montoDescuento = montoDescuento;
    }

    public BigDecimal getMontoConDesc() {
        return montoConDesc;
    }

    public void setMontoConDesc(BigDecimal montoConDesc) {
        this.montoConDesc = montoConDesc;
    }

    public BigDecimal getMontoSinDesc() {
        return montoSinDesc;
    }

    public void setMontoSinDesc(BigDecimal montoSinDesc) {
        this.montoSinDesc = montoSinDesc;
    }

    public String getTipoCan() {
        return tipoCan;
    }

    public void setTipoCan(String tipoCan) {
        this.tipoCan = tipoCan;
    }

    public String getFactTransf() {
        return factTransf;
    }

    public void setFactTransf(String factTransf) {
        this.factTransf = factTransf;
    }

    public String getArticulos() {
        return articulos;
    }

    public void setArticulos(String articulos) {
        this.articulos = articulos;
    }

    public BigDecimal getTotalCupon() {
        return totalCupon;
    }

    public void setTotalCupon(BigDecimal totalCupon) {
        this.totalCupon = totalCupon;
    }
}
