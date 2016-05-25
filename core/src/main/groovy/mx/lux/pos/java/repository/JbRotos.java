package mx.lux.pos.java.repository;

import mx.lux.pos.java.querys.EmpleadoQuery;
import mx.lux.pos.java.querys.JbQuery;
import mx.lux.pos.java.querys.RepoQuery;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class JbRotos {

    Integer idRoto;
	String rx;
	String tipo;
	String material;
	String causa;
	String emp;
	Integer numRoto;
    Boolean alta;
	Date fechaProm;
	Boolean llamada;
    Date fecha;
    String idMod;
    Repo repo;



    public Integer getIdRoto() {
        return idRoto;
    }

    public void setIdRoto(Integer idRoto) {
        this.idRoto = idRoto;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    public String getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public Integer getNumRoto() {
        return numRoto;
    }

    public void setNumRoto(Integer numRoto) {
        this.numRoto = numRoto;
    }

    public Boolean getAlta() {
        return alta;
    }

    public void setAlta(Boolean alta) {
        this.alta = alta;
    }

    public Date getFechaProm() {
        return fechaProm;
    }

    public void setFechaProm(Date fechaProm) {
        this.fechaProm = fechaProm;
    }

    public Boolean getLlamada() {
        return llamada;
    }

    public void setLlamada(Boolean llamada) {
        this.llamada = llamada;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod(String idMod) {
        this.idMod = idMod;
    }

    public Repo getRepo() {
        return repo;
    }

    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    public JbRotos mapeoJbRotos( ResultSet rs ) throws SQLException, ParseException {
      this.setIdRoto(rs.getInt("id_roto"));
      this.setRx(rs.getString("rx"));
      this.setTipo(rs.getString("tipo"));
      this.setMaterial(rs.getString("material"));
      this.setCausa(rs.getString("causa"));
      this.setEmp(rs.getString("emp"));
      this.setNumRoto(rs.getInt("num_roto"));
      this.setAlta(rs.getBoolean("alta"));
      this.setFechaProm(rs.getDate("fecha_prom"));
      this.setLlamada(rs.getBoolean("llamada"));
      this.setFecha(rs.getTimestamp("fecha"));
      this.setIdMod(rs.getString("id_mod"));
      this.setRepo(repo());
      return this;
    }


    public Repo repo( ){
      Repo repo = new Repo();
      repo = RepoQuery.buscaRepoPorNumAndFactura(numRoto,rx);
      return repo;
    }
}
