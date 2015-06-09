package mx.lux.pos.repository;

import mx.lux.pos.Utilities;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class OrdenPromJava {

    Integer id;
	String idFactura;
	Integer idProm;
	Integer idSuc;
    BigDecimal totalDescMonto;
    Date fechaMod;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public Integer getIdProm() {
        return idProm;
    }

    public void setIdProm(Integer idProm) {
        this.idProm = idProm;
    }

    public Integer getIdSuc() {
        return idSuc;
    }

    public void setIdSuc(Integer idSuc) {
        this.idSuc = idSuc;
    }

    public BigDecimal getTotalDescMonto() {
        return totalDescMonto;
    }

    public void setTotalDescMonto(BigDecimal totalDescMonto) {
        this.totalDescMonto = totalDescMonto;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public OrdenPromJava mapeoOrdenProm(ResultSet rs) throws SQLException{
	  this.setId(rs.getInt("id"));
      this.setIdFactura(StringUtils.trimToEmpty(rs.getString("id_factura")));
      this.setIdProm(rs.getInt("id_prom"));
      this.setIdSuc(rs.getInt("id_suc"));
      this.setTotalDescMonto(Utilities.toBigDecimal(StringUtils.trimToEmpty(rs.getString("total_desc_monto"))));
      this.setFechaMod(rs.getDate("fecha_mod"));
  	  return this;
	}



    public boolean equals( String pIdFactura, Integer pIdPromocion ) {
      boolean result = ( ( this.getIdFactura().equalsIgnoreCase( StringUtils.trimToEmpty(pIdFactura) ) )
              && ( this.getIdProm().equals( pIdPromocion ) ) );
      return result;
    }


}
