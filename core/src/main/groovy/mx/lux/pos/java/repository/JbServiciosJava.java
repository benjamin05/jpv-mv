package mx.lux.pos.java.repository;

import mx.lux.pos.java.querys.JbQuery;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class JbServiciosJava {

	Integer idServicio;
	String servicio;




    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public JbServiciosJava mapeoJbServicio( ResultSet rs ) throws SQLException, ParseException {
      this.setIdServicio(rs.getInt("id_servicio"));
      this.setServicio(rs.getString("servicio"));
      return this;
    }


}
