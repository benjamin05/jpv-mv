package mx.lux.pos.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloLcJava {

	String idModelo;
	String modelo;
	String curva;
    String diametro;
    String esfera;
    String cilindro;
    String eje;
    String color;
    Integer idProveedor;

    public String getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(String idModelo) {
        this.idModelo = idModelo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCurva() {
        return curva;
    }

    public void setCurva(String curva) {
        this.curva = curva;
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

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public ModeloLcJava mapeoModeloLc(ResultSet rs) throws SQLException{
	  this.setIdModelo(rs.getString("id_modelo"));
      this.setModelo(rs.getString("modelo"));
      this.setCurva(rs.getString("curva"));
      this.setDiametro(rs.getString("diametro"));
      this.setEsfera(rs.getString("esfera"));
      this.setCilindro(rs.getString("cilindro"));
      this.setEje(rs.getString("eje"));
      this.setColor(rs.getString("color"));
      this.setIdProveedor(rs.getInt("id_proveedor"));
	  return this;
	}
	
	
}
