package mx.lux.pos.java.repository;

import java.math.BigDecimal;
import java.util.Date;

public class ArticulosJava {

	Integer idArticulo;
	String articulo;
	String colorCode;
	String descArticulo;
	String idGenerico;
	String idGenTipo;
	String idGenSubtipo;
	BigDecimal precio;
	BigDecimal precioO;
	String sArticulo;
	String idSync;
	Date fechaMod;
	String idMod;
	Integer idSucursal;
	String colorDesc;
	String idCb;
	String idDisenoLente;
    Integer existencia;
    String tipo;
    String subtipo;
    String marca;
    String proveedor;
    String indiceDioptra;
    String operacion;
    String tipoPrecio;
    String ubicacion;

    public Integer getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Integer idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getDescArticulo() {
        return descArticulo;
    }

    public void setDescArticulo(String descArticulo) {
        this.descArticulo = descArticulo;
    }

    public String getIdGenerico() {
        return idGenerico;
    }

    public void setIdGenerico(String idGenerico) {
        this.idGenerico = idGenerico;
    }

    public String getIdGenTipo() {
        return idGenTipo;
    }

    public void setIdGenTipo(String idGenTipo) {
        this.idGenTipo = idGenTipo;
    }

    public String getIdGenSubtipo() {
        return idGenSubtipo;
    }

    public void setIdGenSubtipo(String idGenSubtipo) {
        this.idGenSubtipo = idGenSubtipo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getPrecioO() {
        return precioO;
    }

    public void setPrecioO(BigDecimal precioO) {
        this.precioO = precioO;
    }

    public String getsArticulo() {
        return sArticulo;
    }

    public void setsArticulo(String sArticulo) {
        this.sArticulo = sArticulo;
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

    public String getColorDesc() {
        return colorDesc;
    }

    public void setColorDesc(String colorDesc) {
        this.colorDesc = colorDesc;
    }

    public String getIdCb() {
        return idCb;
    }

    public void setIdCb(String idCb) {
        this.idCb = idCb;
    }

    public String getIdDisenoLente() {
        return idDisenoLente;
    }

    public void setIdDisenoLente(String idDisenoLente) {
        this.idDisenoLente = idDisenoLente;
    }

    public Integer getExistencia() {
        return existencia;
    }

    public void setExistencia(Integer existencia) {
        this.existencia = existencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(String subtipo) {
        this.subtipo = subtipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getIndiceDioptra() {
        return indiceDioptra;
    }

    public void setIndiceDioptra(String indiceDioptra) {
        this.indiceDioptra = indiceDioptra;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getTipoPrecio() {
        return tipoPrecio;
    }

    public void setTipoPrecio(String tipoPrecio) {
        this.tipoPrecio = tipoPrecio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public ArticulosJava setValores( Integer idArticulo, String articulo, String colorCode, String descArticulo, String idGenerico,
            String idGenTipo, String idGenSubtipo, BigDecimal precio, BigDecimal precioO, String sArticulo, String idSync, Date fechaMod,
			String idMod, Integer idSucursal, String colorDesc, String idCb, String idDisenoLente, Integer existencia, String tipo,
            String subtipo, String marca, String proveedor, String indiceDioptra){

		ArticulosJava articulosJava = new ArticulosJava();
		this.setIdArticulo(idArticulo);
		this.setArticulo(articulo);
		this.setColorCode(colorCode);
		this.setDescArticulo(descArticulo);
		this.setIdGenerico(idGenerico);
		this.setIdGenTipo(idGenTipo);
		this.setIdGenSubtipo(idGenSubtipo);
        this.setPrecio(precio);
        this.setPrecioO(precioO);
        this.setsArticulo(sArticulo);
        this.setIdSync(idSync);
        this.setFechaMod(fechaMod);
        this.setIdMod(idMod);
        this.setIdSucursal(idSucursal);
        this.setColorDesc(colorDesc);
        this.setIdCb(idCb);
        this.setIdDisenoLente(idDisenoLente);
        this.setExistencia(existencia);
        this.setTipo(tipo);
        this.setSubtipo(subtipo);
        this.setMarca(marca);
        this.setProveedor(proveedor);
        this.setIndiceDioptra(indiceDioptra);
		
		return articulosJava;
	}
	
	
}
