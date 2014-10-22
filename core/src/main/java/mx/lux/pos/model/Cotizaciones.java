package mx.lux.pos.model;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Cotizaciones {

    private Date fechaMod;
    private Date fechaVenta;
    private String idEmpleado;
    private String idCotizacion;
    private String cliente;
    private String contacto;
    private String factura;
    private String nombre;
    private List<Articulo> lstArticulos;
    private List<CotizacionesDet> lstDetalles;
    private BigDecimal importeTotal;
    private Double cantCotizaciones;
    private Double cantVentas;
    private BigDecimal porcentajeVentas;

    public Cotizaciones( String idEmpleado ) {
        this.idEmpleado = idEmpleado;
        fechaMod = new Date();
        fechaVenta = new Date();
        lstArticulos = new ArrayList<Articulo>();
        importeTotal = BigDecimal.ZERO;
        lstDetalles = new ArrayList<CotizacionesDet>();
        cantCotizaciones = 0.00;
        cantVentas = 0.00;
        porcentajeVentas = BigDecimal.ZERO;
    }

    public void AcumulaCotizacionesDet( Cotizacion cotizacion, List<Articulo> lstArticulos, List<Precio> lstPrecios,
                                        NotaVenta nota, Cliente cliente ){
      cantCotizaciones = cantCotizaciones+1;
      if(cotizacion.getIdFactura() != null && cotizacion.getIdFactura().trim().length() > 0){
        if(StringUtils.trimToEmpty( cotizacion.getIdFactura()) != ""){
          cantVentas = cantVentas+1;
        }
      }
      porcentajeVentas = new BigDecimal(cantVentas/cantCotizaciones);
      CotizacionesDet cotiza = FindOrCreate( lstDetalles, cotizacion.getIdCotiza() );
      cotiza.AcumulaDetalles( cotizacion, lstArticulos, lstPrecios, nota, cliente );
    }


    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public List<Articulo> getLstArticulos() {
        return lstArticulos;
    }

    public void setLstArticulos(List<Articulo> lstArticulos) {
        this.lstArticulos = lstArticulos;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(String idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    protected CotizacionesDet FindOrCreate( List<CotizacionesDet> lstCotizacionesDet, Integer idCotiza ) {
        CotizacionesDet found = null;

        for ( CotizacionesDet cotiza : lstCotizacionesDet ) {
            if ( cotiza.getIdCotizacion().equals( idCotiza ) ) {
                found = cotiza;
                break;
            }
        }
        if ( found == null ) {
            found = new CotizacionesDet( idCotiza );
            lstCotizacionesDet.add( found );
        }
        return found;
    }

    public List<CotizacionesDet> getLstDetalles() {
        return lstDetalles;
    }

    public void setLstDetalles(List<CotizacionesDet> lstDetalles) {
        this.lstDetalles = lstDetalles;
    }

    public Double getCantCotizaciones() {
        return cantCotizaciones;
    }

    public void setCantCotizaciones(Double cantCotizaciones) {
        this.cantCotizaciones = cantCotizaciones;
    }

    public BigDecimal getPorcentajeVentas() {
        return porcentajeVentas;
    }

    public void setPorcentajeVentas(BigDecimal porcentajeVentas) {
        this.porcentajeVentas = porcentajeVentas;
    }

    public Double getCantVentas() {
        return cantVentas;
    }

    public void setCantVentas(Double cantVentas) {
        this.cantVentas = cantVentas;
    }
}
