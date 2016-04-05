package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.querys.FormaContactoQuery;
import mx.lux.pos.java.querys.JbQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JbLlamadaJava {

    Integer numLlamada;
	String rx;
    Date fecha;
	String estado;
	String contesto;
    String empAtendio;
    String empLlamo;
    String tipo;
    String obs;
    Boolean grupo;
    String idMod;
    JbJava jb;
    FormaContactoJava formaContacto;

    public Integer getNumLlamada() {
        return numLlamada;
    }

    public void setNumLlamada(Integer numLlamada) {
        this.numLlamada = numLlamada;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getContesto() {
        return contesto;
    }

    public void setContesto(String contesto) {
        this.contesto = contesto;
    }

    public String getEmpAtendio() {
        return empAtendio;
    }

    public void setEmpAtendio(String empAtendio) {
        this.empAtendio = empAtendio;
    }

    public String getEmpLlamo() {
        return empLlamo;
    }

    public void setEmpLlamo(String empLlamo) {
        this.empLlamo = empLlamo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public Boolean getGrupo() {
        return grupo;
    }

    public void setGrupo(Boolean grupo) {
        this.grupo = grupo;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod(String idMod) {
        this.idMod = idMod;
    }

    public JbJava getJb() {
        return jb;
    }

    public void setJb(JbJava jb) {
        this.jb = jb;
    }

    public FormaContactoJava getFormaContacto() {
        return formaContacto;
    }

    public void setFormaContacto(FormaContactoJava formaContacto) {
        this.formaContacto = formaContacto;
    }

    public JbLlamadaJava mapeoParametro(ResultSet rs) throws SQLException, ParseException {
	  this.setNumLlamada(rs.getInt("num_llamada"));
	  this.setRx(rs.getString("rx"));
	  this.setFecha(rs.getDate("fecha"));
      this.setEstado(rs.getString("estado"));
      this.setContesto(rs.getString("contesto"));
      this.setEmpAtendio(rs.getString("emp_atendio"));
      this.setEmpLlamo(rs.getString("emp_llamo"));
      this.setTipo(rs.getString("tipo"));
      this.setObs(rs.getString("obs"));
      this.setGrupo(Utilities.toBoolean(rs.getBoolean("grupo")));
      this.setIdMod(rs.getString("id_mod"));
      //this.setJb( jbJava() );
      //this.setFormaContacto( formaContacto() );
	  return this;
	}


    public JbJava jbJava() throws ParseException {
      JbJava jbJava = new JbJava();
      jbJava = JbQuery.buscarPorRx(this.rx);
      return jbJava;
    }


    public List<FormaContactoJava> formaContacto() throws ParseException {
      List<FormaContactoJava> lstFormasContacto = new ArrayList<FormaContactoJava>();
      FormaContactoJava formaContacto = new FormaContactoJava();
      formaContacto = FormaContactoQuery.buscaFormaContactoPorRx( this.rx );
      if( formaContacto != null ){
        lstFormasContacto.add( formaContacto );
      }
      return lstFormasContacto;
    }


}
