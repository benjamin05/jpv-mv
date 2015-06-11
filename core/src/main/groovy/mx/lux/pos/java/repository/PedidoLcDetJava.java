package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PedidoLcDetJava {

    Integer id;
	String idPedido;
	String curvaBase;
	String diametro;
    String esfera;
    String cilindro;
    String modelo;
    String eje;
    String color;
    Integer cantidad;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getCurvaBase() {
        return curvaBase;
    }

    public void setCurvaBase(String curvaBase) {
        this.curvaBase = curvaBase;
    }

    public String getDiametro() {
        return diametro;
    }

    public void setDiametro(String diametro) {
        this.diametro = diametro;
    }

    public String getEsfera() {
        return esfera;
    }

    public void setEsfera(String esfera) {
        this.esfera = esfera;
    }

    public String getCilindro() {
        return cilindro;
    }

    public void setCilindro(String cilindro) {
        this.cilindro = cilindro;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getEje() {
        return eje;
    }

    public void setEje(String eje) {
        this.eje = eje;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public PedidoLcDetJava mapeoPedidoLcDet(ResultSet rs) throws SQLException{
      this.setId(rs.getInt("num_reg"));
	  this.setIdPedido(rs.getString("id_pedido"));
      this.setCurvaBase(rs.getString("curva_base"));
      this.setDiametro(rs.getString("diametro"));
      this.setEsfera(rs.getString("esfera"));
      this.setCilindro(rs.getString("cilindro"));
      this.setModelo(rs.getString("modelo"));
      this.setEje(rs.getString("eje"));
      this.setColor(rs.getString("color"));
      this.setCantidad(rs.getInt("cantidad"));
	  return this;
	}
	
	
}
