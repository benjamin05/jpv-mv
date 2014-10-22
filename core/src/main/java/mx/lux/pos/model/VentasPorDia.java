package mx.lux.pos.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class VentasPorDia {

    private String factura;
    private String articulos;
    private String tipoPago;
    private String facturaCancelada;
    private Date fecha;
    private Date fechaEntrega;
    private BigDecimal montoConDescuento;
    private BigDecimal montoTotal;
    private BigDecimal montoDescuento;
    private Integer contadorArt;
    private String generico;
    private BigDecimal genMontoConDescuento;
    private BigDecimal genMontoTotal;
    private BigDecimal genMontoDescuento;
    private Integer genContadorArt;
    private  Integer contadorArtNeg;
    private BigDecimal montoTotalCancelado;
    private BigDecimal montoConDescCancelado;
    private BigDecimal montoTotalDescuentoCan;
    private BigDecimal total;
    private Double montoSinIva;
    private String empleado;
    private String descripcion;
    private Boolean esNotaCredito;

    private static final String TAG_CUPON = "CUPON";
    private static final String TAG_BODEGA = "BD";

    BigDecimal porcentaje = new BigDecimal( 100 );
    private static final BigDecimal CERO = BigDecimal.valueOf( 0.005 );

    public VentasPorDia( String factura, String generico, Date fecha ) {
        this.factura = factura;
        this.generico = generico;
        this.fecha = fecha;
        montoConDescuento = BigDecimal.ZERO;
        montoTotal = BigDecimal.ZERO;
        montoDescuento = BigDecimal.ZERO;
        contadorArt = 0;
        contadorArtNeg = 0;
        genMontoConDescuento = BigDecimal.ZERO;
        genMontoDescuento = BigDecimal.ZERO;
        genMontoTotal = BigDecimal.ZERO;
        genContadorArt = 0;
        montoTotalCancelado = BigDecimal.ZERO;
        montoConDescCancelado = BigDecimal.ZERO;
        montoTotalDescuentoCan = BigDecimal.ZERO;
        total = BigDecimal.ZERO;
        esNotaCredito = false;
        articulos = "";
        tipoPago = "";
    }

    public void acumulaArticulos(  NotaVenta notaVenta, Boolean artPrecioMayorCero ) {
        for( DetalleNotaVenta detalle : notaVenta.getDetalles() ){
            if( artPrecioMayorCero){
                if( detalle.getPrecioUnitFinal().compareTo( CERO ) > 0 ){
                    contadorArt = contadorArt+ detalle.getCantidadFac().intValue();
                }
            } else {
                contadorArt = contadorArt+ detalle.getCantidadFac().intValue();
            }
            //BigDecimal precio = detalle.getPrecioUnitLista().multiply( new BigDecimal(detalle.getCantidadFac()) );
            //montoTotal = montoTotal.add( precio );
        }
        for( OrdenPromDet promo : notaVenta.getOrdenPromDet() ){
            montoDescuento = montoDescuento.add(promo.getDescuentoMonto());
        }
        montoDescuento = montoDescuento.add(notaVenta.getMontoDescuento());
        for( Pago pago : notaVenta.getPagos() ){
            montoConDescuento = montoConDescuento.add(pago.getMonto());
            if( "NOT".equalsIgnoreCase(pago.getIdFPago()) ){
                contadorArt = 0;
            }
        }
        montoTotal = montoDescuento.add(montoConDescuento);
        empleado = notaVenta.getEmpleado().getNombreCompleto();
    }


    public void acumulaNotasDeCredito( NotaVenta notaVenta, Boolean artPrecioMayorCero ) {
        BigDecimal montoPagos = BigDecimal.ZERO;
        for( Pago pago : notaVenta.getPagos() ){
            if( "NOT".equalsIgnoreCase(pago.getIdFPago()) ){
                montoConDescuento = montoConDescuento.add(pago.getMonto());
                montoPagos = montoPagos.add( pago.getMonto() );
            }
        }
        BigDecimal diferencia = notaVenta.getVentaNeta().subtract(montoPagos);
        if( diferencia.compareTo(BigDecimal.ZERO) <= 0 ){
            esNotaCredito = true;
        }
        empleado = notaVenta.getEmpleado().getNombreCompleto();
    }

    public void acumulaArticulosPorgenericos( DetalleNotaVenta detalleNotaVenta, Boolean artPrecioMayorCero ) {
        BigDecimal precio = detalleNotaVenta.getPrecioUnitFinal().multiply( new BigDecimal(detalleNotaVenta.getCantidadFac()) );
        if( artPrecioMayorCero ){
            if(detalleNotaVenta.getPrecioUnitFinal().compareTo( CERO ) > 0){
                contadorArt = contadorArt+detalleNotaVenta.getCantidadFac().intValue();
                montoConDescuento = montoConDescuento.add( precio );
                montoTotal = montoTotal.add( detalleNotaVenta.getPrecioUnitLista().multiply( new BigDecimal( detalleNotaVenta.getCantidadFac() ) ) );
                montoDescuento = montoTotal.subtract(montoConDescuento);
            }
        } else {
            contadorArt = contadorArt+detalleNotaVenta.getCantidadFac().intValue();
            montoConDescuento = montoConDescuento.add( detalleNotaVenta.getPrecioUnitFinal() );
            montoTotal = montoTotal.add( detalleNotaVenta.getPrecioUnitLista().multiply( new BigDecimal( detalleNotaVenta.getCantidadFac() ) ) );
            montoDescuento = montoTotal.subtract(montoConDescuento);
        }

    }

    public void acumulaCancelacionesPorgenericos( DetalleNotaVenta detalleNotaVenta, Boolean artPrecioMayorCero ) {
        BigDecimal precio = detalleNotaVenta.getPrecioUnitFinal().multiply( new BigDecimal(detalleNotaVenta.getCantidadFac()) );
        if( artPrecioMayorCero ){
            if(detalleNotaVenta.getPrecioUnitFinal().compareTo( CERO ) > 0){
                contadorArt = contadorArt-detalleNotaVenta.getCantidadFac().intValue();
                montoConDescuento = montoConDescuento.subtract( precio );
                montoConDescCancelado = montoConDescCancelado.add( precio );
            }
        } else {
            contadorArt = contadorArt-detalleNotaVenta.getCantidadFac().intValue();
            montoConDescCancelado = montoConDescCancelado.add( precio );
            montoConDescuento = montoConDescuento.subtract( precio );
        }

    }

    public void acumulaNotasDeCreditoGenericos( DetalleNotaVenta det, BigDecimal montoNotaCred, Boolean artPrecioMayorCero ) {
        contadorArt = contadorArt-det.getCantidadFac().intValue();
        //montoConDescuento = montoConDescuento.subtract( montoNotaCred );
        //montoTotal = montoTotal.subtract( montoNotaCred );

    }


    public void acumulaCancelaciones( NotaVenta notaVenta, Modificacion mod, Boolean artPrecioMayorCero ) {
        for( DetalleNotaVenta detalle : notaVenta.getDetalles() ){
            if( artPrecioMayorCero ){
                if( detalle.getPrecioUnitFinal().compareTo( CERO ) > 0 ){
                    contadorArtNeg = contadorArtNeg+detalle.getCantidadFac().intValue();
                }
            } else {
                contadorArtNeg = contadorArtNeg+detalle.getCantidadFac().intValue();
            }
            BigDecimal precio = detalle.getPrecioUnitFinal().multiply( new BigDecimal(detalle.getCantidadFac()) );
            montoConDescCancelado = montoConDescCancelado.add( precio );
        }
        empleado = mod.getNotaVenta().getEmpleado().getNombreCompleto();
    }

    public void acumulaVentasPorDia( NotaVenta notaVenta, double iva ){
        BigDecimal importeTotal = BigDecimal.ZERO;
        Double importeTotalSinIva = 0.00;
        for(Pago pago : notaVenta.getPagos()){
            if( !"TR".equalsIgnoreCase(pago.getIdFPago()) ){
                importeTotal = importeTotal.add(pago.getMonto());
                importeTotalSinIva = importeTotalSinIva+pago.getMonto().doubleValue();
            }
        }
        importeTotalSinIva = importeTotalSinIva/iva;
        factura = notaVenta.getFactura();
        montoTotal =importeTotal;
        montoSinIva = importeTotalSinIva;
    }


    public void acumulaVentasPorDiaMasVision( NotaVenta nota, String pagosNoTransf ){
      for(DetalleNotaVenta det : nota.getDetalles()){
        String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
        articulos = articulos + "," + det.getArticulo().getArticulo().trim()+color;
      }
      fecha = nota.getFechaHoraFactura();
      montoTotal = nota.getVentaTotal();
      for(Pago pago : nota.getPagos()){
          if(pagosNoTransf.contains(pago.geteTipoPago().getId().trim())){
            montoDescuento = montoDescuento.add(pago.getMonto());
          }
          tipoPago = tipoPago + "," + pago.getIdFPago();
      }
      montoConDescuento = montoTotal.subtract(montoDescuento);
      for(Pago pago : nota.getPagos()){
        if(TAG_BODEGA.equalsIgnoreCase(pago.geteTipoPago().getId().trim())){
           montoDescuento = montoDescuento.subtract(pago.getMonto());
        }
      }
      fechaEntrega = nota.getFechaEntrega();

      articulos = articulos.replaceFirst(",", "");
      tipoPago = tipoPago.replaceFirst(",", "");
    }


    public void acumulaCancelacionesPorDiaMasVision( Modificacion modificacion, String pagosNoTransf ){
        if(articulos.trim().length() <= 0){
          for(DetalleNotaVenta det : modificacion.getNotaVenta().getDetalles()){
            String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
            articulos = articulos + "," + det.getArticulo().getArticulo().trim()+color;
          }
        }
        fecha = modificacion.getNotaVenta().getFechaHoraFactura();
        montoTotal = montoTotal.subtract(modificacion.getNotaVenta().getVentaTotal());
        for(Pago pago : modificacion.getNotaVenta().getPagos()){
            if(pagosNoTransf.contains(pago.geteTipoPago().getId().trim())){
                montoDescuento = montoDescuento.subtract(pago.getMonto());
            }
            if(tipoPago.trim().length() <= 0){
              tipoPago = tipoPago + "," + pago.getIdFPago();
            }
        }
        montoConDescuento = montoTotal.subtract(montoDescuento);
        for(Pago pago : modificacion.getNotaVenta().getPagos()){
            if(pago.geteTipoPago().getId().trim().equalsIgnoreCase(TAG_BODEGA)){
                montoDescuento = montoDescuento.add(pago.getMonto());
            }
        }
        fechaEntrega = modificacion.getNotaVenta().getFechaEntrega();
        articulos = articulos.replaceFirst(",", "");
        tipoPago = tipoPago.replaceFirst(",", "");
    }

    public void acumulaVentasBodPorDiaMasVision( NotaVenta nota, String pagosNoTransf ){
        if(articulos.trim().length() <= 0){
            for(DetalleNotaVenta det : nota.getDetalles()){
                String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
                articulos = articulos + "," + det.getArticulo().getArticulo().trim()+color;
            }
        }
        fecha = nota.getFechaHoraFactura();
        for(Pago pago : nota.getPagos()){
            if(TAG_BODEGA.equalsIgnoreCase(pago.geteTipoPago().getId().trim())){
                montoTotal = montoTotal.subtract(pago.getMonto());
            }
            if(tipoPago.trim().length() <= 0){
                tipoPago = TAG_BODEGA;
            }
        }
        montoConDescuento = montoConDescCancelado.add(montoTotal);
        fechaEntrega = nota.getFechaEntrega();
        articulos = articulos.replaceFirst(",", "");
        tipoPago = tipoPago.replaceFirst(",", "");
    }

    public void acumulaNotasCreditoVentasPorDia( NotaVenta notaVenta, double iva ){
        BigDecimal importeTotal = BigDecimal.ZERO;
        Double importeTotalSinIva = 0.00;
        for(Pago pago : notaVenta.getPagos()){
            if( "NOT".equalsIgnoreCase(pago.getIdFPago()) || "TR".equalsIgnoreCase(pago.getIdFPago()) ){
                importeTotal = (importeTotal.add(pago.getMonto())).negate();
                importeTotalSinIva = (importeTotalSinIva+pago.getMonto().doubleValue())*-1;
                if( "NOT".equalsIgnoreCase(pago.getIdFPago()) ){
                    esNotaCredito = true;
                }
            }
        }
        /*BigDecimal diferencia = importeTotal.add(notaVenta.getVentaNeta());
        if( diferencia.compareTo(BigDecimal.ZERO) <= 0  ){
            esNotaCredito = true;
        }*/
        importeTotalSinIva = importeTotalSinIva/iva;
        factura = notaVenta.getFactura();
        montoTotal =importeTotal;
        montoSinIva = importeTotalSinIva;
    }

    public void acumulaCancelacionesPorDia( Modificacion mod, List<NotaVenta> notasVenta, double iva ){
        BigDecimal importeTotal = BigDecimal.ZERO;
        Double importeTotalSinIva = 0.00;
        for(Pago pago : mod.getNotaVenta().getPagos()){
            factura = pago.getNotaVenta().getFactura();
            importeTotal = importeTotal.add(pago.getMonto());
            importeTotalSinIva = importeTotalSinIva+pago.getMonto().doubleValue();
        }
        importeTotalSinIva = importeTotalSinIva/iva;
        factura = mod.getNotaVenta().getFactura();
        montoTotal =importeTotal.negate();
        montoSinIva = importeTotalSinIva*-1;
    }


    public void acumulaCupones( Pago pago ){
      descripcion = pago.geteTipoPago().getDescripcion();
      montoTotal = montoTotal.add(pago.getMonto());
      contadorArt = contadorArt+1;
    }



    public Integer getContadorArt() {
        return contadorArt;
    }

    public void setContadorArt( Integer contadorArt ) {
        this.contadorArt = contadorArt;
    }

    public Integer getContadorArtNeg() {
        return contadorArtNeg;
    }

    public void setContadorArtNeg( Integer contadorArtNeg ) {
        this.contadorArtNeg = contadorArtNeg;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado( String empleado ) {
        this.empleado = empleado;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura( String factura ) {
        this.factura = factura;
    }

    public String getFacturaCancelada() {
        return facturaCancelada;
    }

    public void setFacturaCancelada( String facturaCancelada ) {
        this.facturaCancelada = facturaCancelada;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha( Date fecha ) {
        this.fecha = fecha;
    }

    public Integer getGenContadorArt() {
        return genContadorArt;
    }

    public void setGenContadorArt( Integer genContadorArt ) {
        this.genContadorArt = genContadorArt;
    }

    public String getGenerico() {
        return generico;
    }

    public void setGenerico( String generico ) {
        this.generico = generico;
    }

    public BigDecimal getGenMontoConDescuento() {
        return genMontoConDescuento;
    }

    public void setGenMontoConDescuento( BigDecimal genMontoConDescuento ) {
        this.genMontoConDescuento = genMontoConDescuento;
    }

    public BigDecimal getGenMontoDescuento() {
        return genMontoDescuento;
    }

    public void setGenMontoDescuento( BigDecimal genMontoDescuento ) {
        this.genMontoDescuento = genMontoDescuento;
    }

    public BigDecimal getGenMontoTotal() {
        return genMontoTotal;
    }

    public void setGenMontoTotal( BigDecimal genMontoTotal ) {
        this.genMontoTotal = genMontoTotal;
    }

    public BigDecimal getMontoConDescCancelado() {
        return montoConDescCancelado;
    }

    public void setMontoConDescCancelado( BigDecimal montoConDescCancelado ) {
        this.montoConDescCancelado = montoConDescCancelado;
    }

    public BigDecimal getMontoConDescuento() {
        return montoConDescuento;
    }

    public void setMontoConDescuento( BigDecimal montoConDescuento ) {
        this.montoConDescuento = montoConDescuento;
    }

    public BigDecimal getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento( BigDecimal montoDescuento ) {
        this.montoDescuento = montoDescuento;
    }

    public Double getMontoSinIva() {
        return montoSinIva;
    }

    public void setMontoSinIva( Double montoSinIva ) {
        this.montoSinIva = montoSinIva;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal( BigDecimal montoTotal ) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getMontoTotalCancelado() {
        return montoTotalCancelado;
    }

    public void setMontoTotalCancelado( BigDecimal montoTotalCancelado ) {
        this.montoTotalCancelado = montoTotalCancelado;
    }

    public BigDecimal getMontoTotalDescuentoCan() {
        return montoTotalDescuentoCan;
    }

    public void setMontoTotalDescuentoCan( BigDecimal montoTotalDescuentoCan ) {
        this.montoTotalDescuentoCan = montoTotalDescuentoCan;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje( BigDecimal porcentaje ) {
        this.porcentaje = porcentaje;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal( BigDecimal total ) {
        this.total = total;
    }

    public Boolean getEsNotaCredito() {
        return esNotaCredito;
    }

    public void setEsNotaCredito( Boolean esNotaCredito ) {
        this.esNotaCredito = esNotaCredito;
    }

    public String getArticulos() {
        return articulos;
    }

    public void setArticulos(String articulos) {
        this.articulos = articulos;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
