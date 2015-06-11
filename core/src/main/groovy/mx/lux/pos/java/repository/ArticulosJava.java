package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        return StringUtils.trimToEmpty(articulo);
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public String getColorCode() {
        return StringUtils.trimToEmpty(colorCode);
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getDescArticulo() {
        return StringUtils.trimToEmpty(descArticulo);
    }

    public void setDescArticulo(String descArticulo) {
        this.descArticulo = descArticulo;
    }

    public String getIdGenerico() {
        return StringUtils.trimToEmpty(idGenerico);
    }

    public void setIdGenerico(String idGenerico) {
        this.idGenerico = idGenerico;
    }

    public String getIdGenTipo() {
        return StringUtils.trimToEmpty(idGenTipo);
    }

    public void setIdGenTipo(String idGenTipo) {
        this.idGenTipo = idGenTipo;
    }

    public String getIdGenSubtipo() {
        return StringUtils.trimToEmpty(idGenSubtipo);
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
        return StringUtils.trimToEmpty(sArticulo);
    }

    public void setsArticulo(String sArticulo) {
        this.sArticulo = sArticulo;
    }

    public String getIdSync() {
        return StringUtils.trimToEmpty(idSync);
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
        return StringUtils.trimToEmpty(idMod);
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
        return StringUtils.trimToEmpty(colorDesc);
    }

    public void setColorDesc(String colorDesc) {
        this.colorDesc = colorDesc;
    }

    public String getIdCb() {
        return StringUtils.trimToEmpty(idCb);
    }

    public void setIdCb(String idCb) {
        this.idCb = idCb;
    }

    public String getIdDisenoLente() {
        return StringUtils.trimToEmpty(idDisenoLente);
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
        return StringUtils.trimToEmpty(tipo);
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSubtipo() {
        return StringUtils.trimToEmpty(subtipo);
    }

    public void setSubtipo(String subtipo) {
        this.subtipo = subtipo;
    }

    public String getMarca() {
        return StringUtils.trimToEmpty(marca);
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getProveedor() {
        return StringUtils.trimToEmpty(proveedor);
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getIndiceDioptra() {
        return StringUtils.trimToEmpty(indiceDioptra);
    }

    public void setIndiceDioptra(String indiceDioptra) {
        this.indiceDioptra = indiceDioptra;
    }

    public String getOperacion() {
        return StringUtils.trimToEmpty(operacion);
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getTipoPrecio() {
        return StringUtils.trimToEmpty(tipoPrecio);
    }

    public void setTipoPrecio(String tipoPrecio) {
        this.tipoPrecio = tipoPrecio;
    }

    public String getUbicacion() {
        return StringUtils.trimToEmpty(ubicacion);
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public ArticulosJava setValores( ResultSet rs ) throws SQLException {
		this.setIdArticulo(rs.getInt("id_articulo"));
		this.setArticulo(rs.getString("articulo"));
		this.setColorCode(rs.getString("color_code"));
		this.setDescArticulo(rs.getString("desc_articulo"));
		this.setIdGenerico(rs.getString("id_generico"));
		this.setIdGenTipo(rs.getString("id_gen_subtipo"));
		this.setIdGenSubtipo(rs.getString("id_gen_subtipo"));
        this.setPrecio(Utilities.toBigDecimal(rs.getString("precio")));
        this.setPrecioO(Utilities.toBigDecimal(rs.getString("precio_o")));
        this.setsArticulo(rs.getString("s_articulo"));
        this.setIdSync(rs.getString("id_sync"));
        this.setFechaMod(rs.getDate("fecha_mod"));
        this.setIdMod(rs.getString("id_mod"));
        this.setIdSucursal(rs.getInt("id_sucursal"));
        this.setColorDesc(rs.getString("color_desc"));
        this.setIdCb(rs.getString("id_cb"));
        this.setIdDisenoLente(rs.getString("id_diseno_lente"));
        this.setExistencia(rs.getInt("existencia"));
        this.setTipo(rs.getString("tipo"));
        this.setSubtipo(rs.getString("subtipo"));
        this.setMarca(rs.getString("marca"));
        this.setProveedor(rs.getString("proveedor"));
        this.setIndiceDioptra(rs.getString("indice_dioptra"));
		
		return this;
	}
	
	
}
