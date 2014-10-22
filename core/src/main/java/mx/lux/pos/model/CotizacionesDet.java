package mx.lux.pos.model;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CotizacionesDet {

    private Date fecha;
    private Integer idCotizacion;
    private String cliente;
    private String contacto;
    private List<Articulo> lstArticulos;
    private BigDecimal importeTotal;
    private String factura;
    private String articulos;


    public CotizacionesDet( Integer idCotizacion ) {
      this.idCotizacion = idCotizacion;
      idCotizacion = 0;
      lstArticulos = new ArrayList<Articulo>();
      importeTotal = BigDecimal.ZERO;
      factura = "";
    }

    public void AcumulaDetalles( Cotizacion cotizacion, List<Articulo> lstArticulos, List<Precio> lstPrecios,
                                NotaVenta nota, Cliente client ){
      this.articulos = "";
      BigDecimal montoArticulos = BigDecimal.ZERO;
      fecha = cotizacion.getFechaMod();
      if( cotizacion.getNombre().trim().length() >= 30 ){
        cliente = cotizacion.getNombre().substring( 0, 28 );
      } else {
        cliente = cotizacion.getNombre();
      }
      contacto = getFormatTelephone( cotizacion.getTel(), client );
      this.lstArticulos.addAll( lstArticulos );
      /*for(Articulo articulo : lstArticulos){
        articulos = articulos+","+articulo.getArticulo().trim();
      }*/
      for(Precio precio : lstPrecios){
        montoArticulos = montoArticulos.add(precio.getPrecio());
      }
      articulos = articulos.replaceFirst( ",","" );
      importeTotal = montoArticulos;
      factura = StringUtils.trimToEmpty( cotizacion.getIdFactura() );
    }

    public String getFormatTelephone( String contacto, Cliente cliente ){
      String telefono = "";
      String[] contactos = contacto.split(",");
      for(String tel : contactos){
        if( StringUtils.trimToEmpty(tel).length() > 0 && !StringUtils.trimToEmpty(tel).startsWith("C") &&
                !StringUtils.trimToEmpty(tel).startsWith("M") ){
            if( StringUtils.trimToEmpty(cliente.getTelefonoCasa()).equalsIgnoreCase(tel) ){
                telefono = "C:"+tel;
            } else if( StringUtils.trimToEmpty(cliente.getTelefonoAdicional()).equalsIgnoreCase(tel) ){
                telefono = ",M:"+tel;
            }
        } else {
          telefono += tel;
        }
      }
      if( telefono.trim().startsWith( "," ) ){
        telefono = telefono.replaceFirst( ",", "" );
      }
      return telefono;
    }



    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(Integer idCotizacion) {
        this.idCotizacion = idCotizacion;
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

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getArticulos() {
        return articulos;
    }

    public void setArticulos(String articulos) {
        this.articulos = articulos;
    }
}
