package mx.lux.pos.model;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Multipago {

    private Integer idCliente;
    private String cliente;
    private Date fechaVenta;
    private List<MultipagoDet> lstDetalles;



    public Multipago( Integer idCliente, Date fechaVenta ) {
      this.idCliente = idCliente;
      lstDetalles = new ArrayList<MultipagoDet>();
      this.fechaVenta = fechaVenta;
    }


    public void AcumulaNotas( NotaVenta notaVenta ){
      cliente = notaVenta.getCliente().getNombreCompleto();
      String articulos = "";
      String formasPago = "";
      MultipagoDet multipagoDet = new MultipagoDet();
      multipagoDet.setFactura( StringUtils.trimToEmpty(notaVenta.getFactura()) );
      multipagoDet.setFechaVenta( notaVenta.getFechaHoraFactura());
      multipagoDet.setImporte( notaVenta.getVentaNeta() );
      for(DetalleNotaVenta det : notaVenta.getDetalles()){
        articulos = articulos+","+det.getArticulo().getArticulo();
      }
      multipagoDet.setArticulos( articulos.replaceFirst(",","") );
      for(Pago pago : notaVenta.getPagos()){
        formasPago = formasPago+","+pago.getIdFormaPago()+"-"+String.format("$%.2f",pago.getMonto());
      }
      multipagoDet.setFormasPago( formasPago.replaceFirst(",","") );
      lstDetalles.add( multipagoDet );
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public List<MultipagoDet> getLstDetalles() {
        return lstDetalles;
    }

    public void setLstDetalles(List<MultipagoDet> lstDetalles) {
        this.lstDetalles = lstDetalles;
    }
}