package mx.lux.pos.model;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class ResumenCierre {

    private String factura;
    private Date fecha;
    private List<DetalleIngresoPorDia> lstPagos;
    private BigDecimal totalDolares;
    private BigDecimal totalDolaresEnPesos;
    private String totalDolaresYPesos;
    private NotaVenta notaVenta;
    private String idPlan;
    private BigDecimal monto;
    private Terminal terminal;
    private String idFPago;
    private String tipoPago;

    private static final String TAG_TIPO_PAGO = String.valueOf("l");


    public ResumenCierre(Date fecha) {
        this.factura = factura;
        this.   fecha = fecha;
        lstPagos = new ArrayList<DetalleIngresoPorDia>();
        totalDolares = BigDecimal.ZERO;
        totalDolaresEnPesos = BigDecimal.ZERO;
        notaVenta = new NotaVenta();
        monto = BigDecimal.ZERO;
        terminal = new Terminal();
    }

    public void acumulaIngresosPorDia(NotaVenta notaVenta, String diaConsulta) {
        NumberFormat formatter = new DecimalFormat("$#,##0.00");
        NumberFormat format = new DecimalFormat("#,##0.00");
        DetalleIngresoPorDia detalle = FindOrCreate(lstPagos, notaVenta.getFactura());
        if (new ArrayList<Pago>(notaVenta.getPagos()).size() > 0) {
            for (Pago pago : notaVenta.getPagos()) {
                if (pago.getIdFPago().equalsIgnoreCase("EFD")) {
                    totalDolaresEnPesos = totalDolaresEnPesos.add(pago.getMonto());
                    try {
                        if (StringUtils.trimToEmpty(pago.getIdPlan()).length() > 0) {
                            totalDolares = totalDolares.add(new BigDecimal(NumberFormat.getInstance().parse(pago.getIdPlan()).doubleValue()));
                        }
                    } catch (ParseException e) {
                    }
                    totalDolaresYPesos = String.format("%s (%s)", formatter.format(totalDolaresEnPesos), format.format(totalDolares));
                }
            }
            detalle.AcumulaPagosCierre(new ArrayList<Pago>(notaVenta.getPagos()), notaVenta.getVentaTotal(), notaVenta.getFechaHoraFactura(), totalDolaresYPesos,
                    diaConsulta);
        }
        Collections.sort(lstPagos, new Comparator<DetalleIngresoPorDia>() {
            @Override
            public int compare(DetalleIngresoPorDia o1, DetalleIngresoPorDia o2) {
                return o1.getFactura().compareToIgnoreCase(o2.getFactura());
            }
        });
    }


    public void acumulaSaldosPorDia(Pago pago) {
        if (pago.getTipoPago().equalsIgnoreCase(TAG_TIPO_PAGO)) {
            DetalleIngresoPorDia detalle = FindOrCreate(lstPagos, pago.getNotaVenta().getFactura());
            detalle.AcumulaSaldosCierre(pago, pago.getNotaVenta().getVentaTotal(), pago.getNotaVenta().getFechaHoraFactura(), totalDolaresYPesos);
        }
        Collections.sort(lstPagos, new Comparator<DetalleIngresoPorDia>() {
            @Override
            public int compare(DetalleIngresoPorDia o1, DetalleIngresoPorDia o2) {
                return o1.getFactura().compareToIgnoreCase(o2.getFactura());
            }
        });
    }

    public void acumulaDevolucionesPorDia(Devolucion devolucion) {
        NumberFormat formatter = new DecimalFormat("$#,##0.00");
        NumberFormat format = new DecimalFormat("#,##0.00");
        DetalleIngresoPorDia detalle = FindOrCreate( lstPagos, devolucion.getPago().getNotaVenta().getFactura() );
        if (devolucion != null) {
            if (devolucion.getIdFormaPago().equalsIgnoreCase("EFD")) {
                totalDolaresEnPesos = totalDolaresEnPesos.add(devolucion.getMonto());
                totalDolaresYPesos = String.format("%s", formatter.format(totalDolaresEnPesos) );
            }
            detalle.AcumulaDevolucionesCierre(devolucion, devolucion.getPago().getNotaVenta().getVentaTotal(), devolucion.getPago().getNotaVenta().getFechaHoraFactura(), totalDolaresYPesos);
        }
        Collections.sort(lstPagos, new Comparator<DetalleIngresoPorDia>() {
            @Override
            public int compare(DetalleIngresoPorDia o1, DetalleIngresoPorDia o2) {
                return o1.getFactura().compareToIgnoreCase(o2.getFactura());
            }
        });
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<DetalleIngresoPorDia> getLstPagos() {
        return lstPagos;
    }

    public void setLstPagos(List<DetalleIngresoPorDia> lstPagos) {
        this.lstPagos = lstPagos;
    }

    public BigDecimal getTotalDolares() {
        return totalDolares;
    }

    public void setTotalDolares(BigDecimal totalDolares) {
        this.totalDolares = totalDolares;
    }

    protected DetalleIngresoPorDia FindOrCreate(List<DetalleIngresoPorDia> lstIngresos, String idFactura) {
        DetalleIngresoPorDia found = null;

        for (DetalleIngresoPorDia ingresos : lstIngresos) {
            if (ingresos.getFactura().equals(idFactura)) {
                found = ingresos;
                break;
            }
        }
        if (found == null) {
            found = new DetalleIngresoPorDia(idFactura);
            lstIngresos.add(found);
        }
        return found;
    }

    public BigDecimal getTotalDolaresEnPesos() {
        return totalDolaresEnPesos;
    }

    public void setTotalDolaresEnPesos(BigDecimal totalDolaresEnPesos) {
        this.totalDolaresEnPesos = totalDolaresEnPesos;
    }

    public String getTotalDolaresYPesos() {
        return totalDolaresYPesos;
    }

    public void setTotalDolaresYPesos(String totalDolaresYPesos) {
        this.totalDolaresYPesos = totalDolaresYPesos;
    }

    public NotaVenta getNotaVenta() {
        return notaVenta;
    }

    public void setNotaVenta(NotaVenta notaVenta) {
        this.notaVenta = notaVenta;
    }

    public String getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(String idPlan) {
        this.idPlan = idPlan;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public String getIdFPago() {
        return idFPago;
    }

    public void setIdFPago(String idFPago) {
        this.idFPago = idFPago;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
}
