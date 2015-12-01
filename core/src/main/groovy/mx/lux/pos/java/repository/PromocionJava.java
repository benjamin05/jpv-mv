package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class PromocionJava {

    Integer idPromocion;
	String descripcion;
    String articuloProm;
    Boolean aplicaConv;
    Integer prioridad;
    Boolean precioOferta;
    Date vigenciaIni;
    Date vigenciaFin;
	String idGrupoTienda;
    Boolean aplicaAuto;
    Boolean obligatoria;
	String tipoPromocion;
    String idGenerico;
    String tipo;
    String subtipo;
    String marca;
    String articulo;
    String tipoPrecio;
    BigDecimal precioDescontado;
    BigDecimal descuento;
    String genericoc;
    String tipoc;
    String subtipoc;
    String marcac;
    String articuloc;
    String tipoPrecioc;
    BigDecimal precioDescontadoc;
    BigDecimal descuentoc;
    BigDecimal montoMinimo;

    public Integer getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(Integer idPromocion) {
        this.idPromocion = idPromocion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getArticuloProm() {
        return articuloProm;
    }

    public void setArticuloProm(String articuloProm) {
        this.articuloProm = articuloProm;
    }

    public Boolean getAplicaConv() {
        return aplicaConv;
    }

    public void setAplicaConv(Boolean aplicaConv) {
        this.aplicaConv = aplicaConv;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public Boolean getPrecioOferta() {
        return precioOferta;
    }

    public void setPrecioOferta(Boolean precioOferta) {
        this.precioOferta = precioOferta;
    }

    public Date getVigenciaIni() {
        return vigenciaIni;
    }

    public void setVigenciaIni(Date vigenciaIni) {
        this.vigenciaIni = vigenciaIni;
    }

    public Date getVigenciaFin() {
        return vigenciaFin;
    }

    public void setVigenciaFin(Date vigenciaFin) {
        this.vigenciaFin = vigenciaFin;
    }

    public String getIdGrupoTienda() {
        return idGrupoTienda;
    }

    public void setIdGrupoTienda(String idGrupoTienda) {
        this.idGrupoTienda = idGrupoTienda;
    }

    public Boolean getAplicaAuto() {
        return aplicaAuto;
    }

    public void setAplicaAuto(Boolean aplicaAuto) {
        this.aplicaAuto = aplicaAuto;
    }

    public Boolean getObligatoria() {
        return obligatoria;
    }

    public void setObligatoria(Boolean obligatoria) {
        this.obligatoria = obligatoria;
    }

    public String getTipoPromocion() {
        return tipoPromocion;
    }

    public void setTipoPromocion(String tipoPromocion) {
        this.tipoPromocion = tipoPromocion;
    }

    public String getIdGenerico() {
        return idGenerico;
    }

    public void setIdGenerico(String idGenerico) {
        this.idGenerico = idGenerico;
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

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public String getTipoPrecio() {
        return tipoPrecio;
    }

    public void setTipoPrecio(String tipoPrecio) {
        this.tipoPrecio = tipoPrecio;
    }

    public BigDecimal getPrecioDescontado() {
        return precioDescontado;
    }

    public void setPrecioDescontado(BigDecimal precioDescontado) {
        this.precioDescontado = precioDescontado;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public String getGenericoc() {
        return genericoc;
    }

    public void setGenericoc(String genericoc) {
        this.genericoc = genericoc;
    }

    public String getTipoc() {
        return tipoc;
    }

    public void setTipoc(String tipoc) {
        this.tipoc = tipoc;
    }

    public String getSubtipoc() {
        return subtipoc;
    }

    public void setSubtipoc(String subtipoc) {
        this.subtipoc = subtipoc;
    }

    public String getMarcac() {
        return marcac;
    }

    public void setMarcac(String marcac) {
        this.marcac = marcac;
    }

    public String getArticuloc() {
        return articuloc;
    }

    public void setArticuloc(String articuloc) {
        this.articuloc = articuloc;
    }

    public String getTipoPrecioc() {
        return tipoPrecioc;
    }

    public void setTipoPrecioc(String tipoPrecioc) {
        this.tipoPrecioc = tipoPrecioc;
    }

    public BigDecimal getPrecioDescontadoc() {
        return precioDescontadoc;
    }

    public void setPrecioDescontadoc(BigDecimal precioDescontadoc) {
        this.precioDescontadoc = precioDescontadoc;
    }

    public BigDecimal getDescuentoc() {
        return descuentoc;
    }

    public void setDescuentoc(BigDecimal descuentoc) {
        this.descuentoc = descuentoc;
    }

    public BigDecimal getMontoMinimo() {
        return montoMinimo;
    }

    public void setMontoMinimo(BigDecimal montoMinimo) {
        this.montoMinimo = montoMinimo;
    }

    public PromocionJava mapeoPromocion(ResultSet rs) throws SQLException{
	  this.setIdPromocion(rs.getInt("id_promocion"));
	  this.setDescripcion(rs.getString("descripcion"));
      this.setArticuloProm(rs.getString("articulo_prom"));
      this.setAplicaConv(rs.getBoolean("aplica_conv"));
	  this.setPrioridad(rs.getInt("prioridad"));
      this.setPrecioOferta(rs.getBoolean("precio_oferta"));
      this.setVigenciaIni(rs.getDate("vigencia_ini"));
      this.setVigenciaFin(rs.getDate("vigencia_fin"));
      this.setIdGrupoTienda(rs.getString("id_grupo_tienda"));
      this.setAplicaAuto(rs.getBoolean("aplica_auto"));
      this.setObligatoria(rs.getBoolean("obligatoria"));
      this.setTipoPromocion(rs.getString("tipo_promocion"));
      this.setIdGenerico(rs.getString("id_generico"));
      this.setTipo(rs.getString("tipo"));
      this.setSubtipo(rs.getString("subtipo"));
      this.setMarca(rs.getString("marca"));
      this.setArticulo(rs.getString("articulo"));
      this.setTipoPrecio(rs.getString("tipo_precio"));
      this.setPrecioDescontado(Utilities.toBigDecimal(rs.getString("precio_descontado")));
      this.setDescuento(Utilities.toBigDecimal(rs.getString("descuento")));
      this.setGenericoc(rs.getString("genericoc"));
      this.setTipoc(rs.getString("tipoc"));
      this.setSubtipoc(rs.getString("subtipoc"));
      this.setMarcac(rs.getString("marcac"));
      this.setArticuloc(rs.getString("articuloc"));
      this.setTipoPrecioc(rs.getString("tipo_precioc"));
      this.setPrecioDescontadoc(Utilities.toBigDecimal(rs.getString("precio_descontadoc")));
      this.setDescuentoc(Utilities.toBigDecimal(rs.getString("descuentoc")));
      this.setMontoMinimo(Utilities.toBigDecimal(rs.getString("monto_minimo")));
	  return this;
	}
	
	
}
