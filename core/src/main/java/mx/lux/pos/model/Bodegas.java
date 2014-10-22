package mx.lux.pos.model;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bodegas {

    private Integer mes;
    private String mesDesc;
    private Integer cantFacturas;
    private BigDecimal totalVenta;
    private BigDecimal totalSaldo;
    private List<BodegasDetalle> lstBodegasDet;
    private static final String TAG_CUPON = "C";

    public Bodegas( Integer mes ) {
      this.mes = mes;
      lstBodegasDet = new ArrayList<BodegasDetalle>();
      cantFacturas = 0;
      totalVenta = BigDecimal.ZERO;
      totalSaldo = BigDecimal.ZERO;
    }


    public void AcumulaBodega( NotaVenta nota ) {
      cantFacturas = cantFacturas+1;
      totalVenta = totalVenta.add( nota.getVentaNeta() );
      totalSaldo = totalSaldo.add( nota.getVentaNeta().subtract(nota.getSumaPagos()));
      mesDesc = descMes( mes );
      List<Articulo> lstArticulos = new ArrayList<Articulo>();
      for(DetalleNotaVenta det : nota.getDetalles()){
          lstArticulos.add( det.getArticulo() );
      }
      String contacto = "";
      String nombreCli = "";
      if( nota.getCliente() != null ){
        nombreCli = nota.getCliente().getNombreCompleto();
        if( StringUtils.trimToEmpty(nota.getCliente().getTelefonoCasa()).length() > 0 ){
          contacto = nota.getCliente().getTelefonoCasa();
        }
        if( StringUtils.trimToEmpty(nota.getCliente().getTelefonoTrabajo()).length() > 0 ){
          contacto = contacto+(contacto.trim().length() > 0 ? "," : "")+nota.getCliente().getTelefonoTrabajo();
        }
        if( StringUtils.trimToEmpty(nota.getCliente().getTelefonoAdicional()).length() > 0 ){
          contacto = contacto+(contacto.trim().length() > 0 ? "," : "")+nota.getCliente().getTelefonoAdicional();
        }
      }
      BodegasDetalle bodegasDet = new BodegasDetalle();
      bodegasDet.setFecha( nota.getFechaHoraFactura() );
      bodegasDet.setFechaPromesa( nota.getFechaPrometida() );
      bodegasDet.setFactura( nota.getFactura() );
      bodegasDet.setLstArticulos( lstArticulos );
      bodegasDet.setCliente( nombreCli );
      bodegasDet.setContacto( contacto );
      bodegasDet.setVenta( nota.getVentaNeta() );
      bodegasDet.setSaldo( nota.getVentaNeta().subtract(nota.getSumaPagos()));
      lstBodegasDet.add( bodegasDet );
    }


    public String descMes( Integer mes ){
      String result = "";
        switch(mes){
            case 1:
            {
                result="Enero";
                break;
            }
            case 2:
            {
                result="Febrero";
                break;
            }
            case 3:
            {
                result="Marzo";
                break;
            }
            case 4:
            {
                result="Abril";
                break;
            }
            case 5:
            {
                result="Mayo";
                break;
            }
            case 6:
            {
                result="Junio";
                break;
            }
            case 7:
            {
                result="Julio";
                break;
            }
            case 8:
            {
                result="Agosto";
                break;
            }
            case 9:
            {
                result="Septiembre";
                break;
            }
            case 10:
            {
                result="Octubre";
                break;
            }
            case 11:
            {
                result="Noviembre";
                break;
            }
            case 12:
            {
                result="Diciembre";
                break;
            }
            default:
            {
                result="";
                break;
            }
        }
      return result;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public List<BodegasDetalle> getLstBodegasDet() {
        return lstBodegasDet;
    }

    public void setLstBodegasDet(List<BodegasDetalle> lstBodegasDet) {
        this.lstBodegasDet = lstBodegasDet;
    }

    public String getMesDesc() {
        return mesDesc;
    }

    public void setMesDesc(String mesDesc) {
        this.mesDesc = mesDesc;
    }

    public Integer getCantFacturas() {
        return cantFacturas;
    }

    public void setCantFacturas(Integer cantFacturas) {
        this.cantFacturas = cantFacturas;
    }

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(BigDecimal totalVenta) {
        this.totalVenta = totalVenta;
    }

    public BigDecimal getTotalSaldo() {
        return totalSaldo;
    }

    public void setTotalSaldo(BigDecimal totalSaldo) {
        this.totalSaldo = totalSaldo;
    }
}
