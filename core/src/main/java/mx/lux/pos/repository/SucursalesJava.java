package mx.lux.pos.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SucursalesJava {
	private String nombre;
	private String direccion;
	private String colonia;
	private String localidad;
	private String idEstado;
	private String cp;
	private String telefonos;
	private String idGerente;
	private Integer letraAscii;
	private Integer numFactura;
    private Boolean sears;
    private Integer por100Anticipo;
    private Boolean impresionFact;
    private Integer serieRepVentas;
    private Integer numRepVentas;
    private Boolean serieNumOrden;
    private String idSync;
    private Date fechaMod;
    private String idMod;
    private Integer idSucursal;
    private String centroCostos;
    private String ciudad;
    private Boolean domingo;
    private String marca;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getIdGerente() {
        return idGerente;
    }

    public void setIdGerente(String idGerente) {
        this.idGerente = idGerente;
    }

    public Integer getLetraAscii() {
        return letraAscii;
    }

    public void setLetraAscii(Integer letraAscii) {
        this.letraAscii = letraAscii;
    }

    public Integer getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(Integer numFactura) {
        this.numFactura = numFactura;
    }

    public Boolean getSears() {
        return sears;
    }

    public void setSears(Boolean sears) {
        this.sears = sears;
    }

    public Integer getPor100Anticipo() {
        return por100Anticipo;
    }

    public void setPor100Anticipo(Integer por100Anticipo) {
        this.por100Anticipo = por100Anticipo;
    }

    public Boolean getImpresionFact() {
        return impresionFact;
    }

    public void setImpresionFact(Boolean impresionFact) {
        this.impresionFact = impresionFact;
    }

    public Integer getSerieRepVentas() {
        return serieRepVentas;
    }

    public void setSerieRepVentas(Integer serieRepVentas) {
        this.serieRepVentas = serieRepVentas;
    }

    public Integer getNumRepVentas() {
        return numRepVentas;
    }

    public void setNumRepVentas(Integer numRepVentas) {
        this.numRepVentas = numRepVentas;
    }

    public Boolean getSerieNumOrden() {
        return serieNumOrden;
    }

    public void setSerieNumOrden(Boolean serieNumOrden) {
        this.serieNumOrden = serieNumOrden;
    }

    public String getIdSync() {
        return idSync;
    }

    public void setIdSync(String idSync) {
        this.idSync = idSync;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod(String idMod) {
        this.idMod = idMod;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getCentroCostos() {
        return centroCostos;
    }

    public void setCentroCostos(String centroCostos) {
        this.centroCostos = centroCostos;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Boolean getDomingo() {
        return domingo;
    }

    public void setDomingo(Boolean domingo) {
        this.domingo = domingo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }


    public SucursalesJava mapeoSucursales( ResultSet rs ){
	  try{
		this.setNombre(rs.getString("nombre"));
        this.setDireccion(rs.getString("direccion"));
        this.setColonia(rs.getString("colonia"));
        this.setLocalidad(rs.getString("localidad"));
        this.setIdEstado(rs.getString("id_estado"));
        this.setCp(rs.getString("cp"));
        this.setTelefonos(rs.getString("telefonos"));
        this.setIdGerente(rs.getString("id_gerente"));
        this.setLetraAscii(rs.getInt("letra_ascii"));
        this.setSears(rs.getBoolean("sears"));
        this.setPor100Anticipo(rs.getInt("por100_anticipo"));
        this.setImpresionFact(rs.getBoolean("impresion_fact"));
        this.setSerieRepVentas(rs.getInt("serie_rep_ventas"));
        this.setNumRepVentas(rs.getInt("num_rep_ventas"));
        this.setSerieNumOrden(rs.getBoolean("serie_num_orden"));
        this.setIdSync(rs.getString("id_sync"));
        this.setFechaMod(rs.getDate("fecha_mod"));
        this.setIdMod(rs.getString("id_mod"));
        this.setIdSucursal(rs.getInt("id_sucursal"));
        this.setCentroCostos(rs.getString("centro_costos"));
        this.setCiudad(rs.getString("ciudad"));
        this.setDomingo(rs.getBoolean("domingo"));
        this.setMarca(rs.getString("marca"));
	  } catch (SQLException err) {
        System.out.println( err );
      }
	  return this;
	}
	
	
}
