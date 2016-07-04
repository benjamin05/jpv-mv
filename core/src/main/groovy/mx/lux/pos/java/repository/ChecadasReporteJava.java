package mx.lux.pos.java.repository;

import mx.lux.pos.java.querys.EmpleadoQuery;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChecadasReporteJava {

    String empleado;
    String idEmpleado;
    List<ChecadasJava> lstChecadas;


    public ChecadasReporteJava( String idEmpleado ){
      this.idEmpleado = idEmpleado;
      lstChecadas = new ArrayList<ChecadasJava>();
    }


    public void AcumulaChecadas( ChecadasJava checada ) {
      empleado = StringUtils.trimToEmpty(checada.getNombreEmp());
      idEmpleado = StringUtils.trimToEmpty(checada.getIdEmpleado());
      lstChecadas.add(checada);
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public List<ChecadasJava> getLstChecadas() {
        return lstChecadas;
    }

    public void setLstChecadas(List<ChecadasJava> lstChecadas) {
        this.lstChecadas = lstChecadas;
    }

    public String getEmpleado() {
        return empleado;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}
