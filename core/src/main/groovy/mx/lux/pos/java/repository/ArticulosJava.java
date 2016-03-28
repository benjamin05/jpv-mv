package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.querys.ArticulosQuery;
import mx.lux.pos.java.querys.GenericosQuery;
import mx.lux.pos.model.Articulo;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
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
    GenericosJava generico;

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

    public GenericosJava getGenerico() {
        return generico;
    }

    public void setGenerico(GenericosJava generico) {
        this.generico = generico;
    }

    public ArticulosJava setValores( ResultSet rs ) throws SQLException, ParseException {
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
        this.setGenerico( generico() );
		
		return this;
	}


    private GenericosJava generico( ) throws ParseException {
      GenericosJava genericosJava = new GenericosJava();
      genericosJava = GenericosQuery.buscaGenericosPorId(idGenerico);
      return genericosJava;
    }

    public void trim(){
      this.setArticulo( StringUtils.trimToEmpty(this.getArticulo()) );
      this.setColorCode( StringUtils.trimToEmpty(this.getColorCode()) );
      this.setDescArticulo(StringUtils.trimToEmpty(this.getDescArticulo()));
      this.setIdGenerico(StringUtils.trimToEmpty(this.getIdGenerico()));
      this.setIdGenTipo(StringUtils.trimToEmpty(this.getIdGenTipo()));
      this.setIdGenSubtipo(StringUtils.trimToEmpty(this.getIdGenSubtipo()));
      this.setsArticulo(StringUtils.trimToEmpty(this.getsArticulo()));
      this.setIdSync(StringUtils.trimToEmpty(this.getIdSync()));
      this.setIdMod(StringUtils.trimToEmpty(this.getIdMod()));
      this.setColorDesc(StringUtils.trimToEmpty(this.getColorDesc()));
      this.setIdCb(StringUtils.trimToEmpty(this.getIdCb()));
      this.setIdDisenoLente(StringUtils.trimToEmpty(this.getIdDisenoLente()));
      this.setTipo(StringUtils.trimToEmpty(this.getTipo()));
      this.setSubtipo(StringUtils.trimToEmpty(this.getSubtipo()));
      this.setMarca(StringUtils.trimToEmpty(this.getMarca()));
      this.setProveedor(StringUtils.trimToEmpty(this.getProveedor()));
      this.setIndiceDioptra(StringUtils.trimToEmpty(this.getIndiceDioptra()));
    }


    public ArticulosJava castToArticulosJava( Articulo articulosJava ) throws ParseException {
        this.setIdArticulo(articulosJava.getId());
        this.setArticulo(articulosJava.getArticulo());
        this.setColorCode(articulosJava.getCodigoColor());
        this.setDescArticulo(articulosJava.getDescripcion());
        this.setIdGenerico(articulosJava.getIdGenerico());
        this.setIdGenTipo(articulosJava.getIdGenTipo());
        this.setIdGenSubtipo(articulosJava.getIdGenSubtipo());
        this.setPrecio(articulosJava.getPrecio());
        this.setPrecioO(articulosJava.getPrecioO());
        this.setsArticulo(articulosJava.getsArticulo());
        this.setIdSync(articulosJava.getIdSync());
        this.setFechaMod(articulosJava.getFechaMod());
        this.setIdMod(articulosJava.getIdMod());
        this.setIdSucursal(articulosJava.getIdSucursal());
        this.setColorDesc(articulosJava.getDescripcionColor());
        this.setIdCb(articulosJava.getIdCb());
        this.setIdDisenoLente(articulosJava.getIdDisenoLente());
        this.setExistencia(articulosJava.getCantExistencia());
        this.setTipo(articulosJava.getTipo());
        this.setSubtipo(articulosJava.getSubtipo());
        this.setMarca(articulosJava.getMarca());
        this.setProveedor(articulosJava.getProveedor());
        this.setIndiceDioptra(articulosJava.getIndice_dioptra());
        this.setGenerico( generico() );

        return this;
    }
}
