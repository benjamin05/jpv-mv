package mx.lux.pos.repository;

import mx.lux.pos.querys.ClientesQuery;
import mx.lux.pos.querys.EmpleadoQuery;
import mx.lux.pos.querys.NotaVentaQuery;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class RecetaJava {

	Integer idReceta;
	Integer examen;
	Integer idCliente;
	Date fechaReceta;
	String sUsoAnteojos;
	String idOptometrista;
	String tipoOpt;
	String odEsfR;
	String odCilR;
	String odEjeR;
	String odAdcR;
	String odAdiR;
	String odPrismaH;
	String oiEsfR;
	String oiCilR;
	String oiEjeR;
    String oiAdcR;
    String oiAdiR;
    String oiPrismaH;
    String diLejosR;
    String diCercaR;
    String odAvR;
    String oiAvR;
    String altOblR;
    String observacionesR;
    Boolean fImpresa;
    String idSync;
    Date fechaMod;
    String idMod;
    Integer idSucursal;
    String diOd;
    String diOi;
    String materialArm;
    String odPrismaV;
    String oiPrismaV;
    String tratamientos;
    String udf5;
    String udf6;
    String idRxOri;
    String folio;
    EmpleadoJava empleado;
    NotaVentaJava notaVenta;

    public Integer getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(Integer idReceta) {
        this.idReceta = idReceta;
    }

    public Integer getExamen() {
        return examen;
    }

    public void setExamen(Integer examen) {
        this.examen = examen;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Date getFechaReceta() {
        return fechaReceta;
    }

    public void setFechaReceta(Date fechaReceta) {
        this.fechaReceta = fechaReceta;
    }

    public String getsUsoAnteojos() {
        return trim(sUsoAnteojos);
    }

    public void setsUsoAnteojos(String sUsoAnteojos) {
        this.sUsoAnteojos = sUsoAnteojos;
    }

    public String getIdOptometrista() {
        return trim(idOptometrista);
    }

    public void setIdOptometrista(String idOptometrista) {
        this.idOptometrista = idOptometrista;
    }

    public String getTipoOpt() {
        return trim(tipoOpt);
    }

    public void setTipoOpt(String tipoOpt) {
        this.tipoOpt = tipoOpt;
    }

    public String getOdEsfR() {
        return trim(odEsfR);
    }

    public void setOdEsfR(String odEsfR) {
        this.odEsfR = odEsfR;
    }

    public String getOdCilR() {
        return trim(odCilR);
    }

    public void setOdCilR(String odCilR) {
        this.odCilR = odCilR;
    }

    public String getOdEjeR() {
        return trim(odEjeR);
    }

    public void setOdEjeR(String odEjeR) {
        this.odEjeR = odEjeR;
    }

    public String getOdAdcR() {
        return trim(odAdcR);
    }

    public void setOdAdcR(String odAdcR) {
        this.odAdcR = odAdcR;
    }

    public String getOdAdiR() {
        return trim(odAdiR);
    }

    public void setOdAdiR(String odAdiR) {
        this.odAdiR = odAdiR;
    }

    public String getOdPrismaH() {
        return trim(odPrismaH);
    }

    public void setOdPrismaH(String odPrismaH) {
        this.odPrismaH = odPrismaH;
    }

    public String getOiEsfR() {
        return trim(oiEsfR);
    }

    public void setOiEsfR(String oiEsfR) {
        this.oiEsfR = oiEsfR;
    }

    public String getOiCilR() {
        return trim(oiCilR);
    }

    public void setOiCilR(String oiCilR) {
        this.oiCilR = oiCilR;
    }

    public String getOiEjeR() {
        return trim(oiEjeR);
    }

    public void setOiEjeR(String oiEjeR) {
        this.oiEjeR = oiEjeR;
    }

    public String getOiAdcR() {
        return trim(oiAdcR);
    }

    public void setOiAdcR(String oiAdcR) {
        this.oiAdcR = oiAdcR;
    }

    public String getOiAdiR() {
        return trim(oiAdiR);
    }

    public void setOiAdiR(String oiAdiR) {
        this.oiAdiR = oiAdiR;
    }

    public String getOiPrismaH() {
        return trim(oiPrismaH);
    }

    public void setOiPrismaH(String oiPrismaH) {
        this.oiPrismaH = oiPrismaH;
    }

    public String getDiLejosR() {
        return trim(diLejosR);
    }

    public void setDiLejosR(String diLejosR) {
        this.diLejosR = diLejosR;
    }

    public String getDiCercaR() {
        return trim(diCercaR);
    }

    public void setDiCercaR(String diCercaR) {
        this.diCercaR = diCercaR;
    }

    public String getOdAvR() {
        return trim(odAvR);
    }

    public void setOdAvR(String odAvR) {
        this.odAvR = odAvR;
    }

    public String getOiAvR() {
        return trim(oiAvR);
    }

    public void setOiAvR(String oiAvR) {
        this.oiAvR = oiAvR;
    }

    public String getAltOblR() {
        return trim(altOblR);
    }

    public void setAltOblR(String altOblR) {
        this.altOblR = altOblR;
    }

    public String getObservacionesR() {
        return trim(observacionesR);
    }

    public void setObservacionesR(String observacionesR) {
        this.observacionesR = observacionesR;
    }

    public String getDiOd() {
        return trim(diOd);
    }

    public void setDiOd(String diOd) {
        this.diOd = diOd;
    }

    public String getDiOi() {
        return trim(diOi);
    }

    public void setDiOi(String diOi) {
        this.diOi = diOi;
    }

    public String getMaterialArm() {
        return trim(materialArm);
    }

    public void setMaterialArm(String materialArm) {
        this.materialArm = materialArm;
    }

    public String getOdPrismaV() {
        return trim(odPrismaV);
    }

    public void setOdPrismaV(String odPrismaV) {
        this.odPrismaV = odPrismaV;
    }

    public String getOiPrismaV() {
        return trim(oiPrismaV);
    }

    public void setOiPrismaV(String oiPrismaV) {
        this.oiPrismaV = oiPrismaV;
    }

    public String getTratamientos() {
        return trim(tratamientos);
    }

    public void setTratamientos(String tratamientos) {
        this.tratamientos = tratamientos;
    }

    public String getUdf5() {
        return trim(udf5);
    }

    public void setUdf5(String udf5) {
        this.udf5 = udf5;
    }

    public String getUdf6() {
        return trim(udf6);
    }

    public void setUdf6(String udf6) {
        this.udf6 = udf6;
    }

    public String getIdRxOri() {
        return trim(idRxOri);
    }

    public void setIdRxOri(String idRxOri) {
        this.idRxOri = idRxOri;
    }

    public String getFolio() {
        return trim(folio);
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Boolean getfImpresa() {
        return fImpresa;
    }

    public void setfImpresa(Boolean fImpresa) {
        this.fImpresa = fImpresa;
    }

    public String getIdSync() {
        return trim(idSync);
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
        return trim(idMod);
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

    public EmpleadoJava getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadoJava empleado) {
        this.empleado = empleado;
    }

    public NotaVentaJava getNotaVenta() {
        return notaVenta;
    }

    public void setNotaVenta(NotaVentaJava notaVenta) {
        this.notaVenta = notaVenta;
    }

    private String trim( String data ){
      return StringUtils.trimToEmpty(data);
    }

    public RecetaJava setValores( ResultSet rs ) throws ParseException {
        try {
            this.setIdReceta(rs.getInt("id_receta"));
            this.setExamen(rs.getInt("examen"));
            this.setIdCliente(rs.getInt("id_cliente"));
            this.setFechaReceta(rs.getDate("fecha_receta"));
            this.setsUsoAnteojos(trim(rs.getString("s_uso_anteojos")));
            this.setIdOptometrista(trim(rs.getString("id_optometrista")));
            this.setTipoOpt(trim(rs.getString("tipo_opt")));
            this.setOdEsfR(trim(rs.getString("od_esf_r")));
            this.setOdCilR(trim(rs.getString("od_cil_r")));
            this.setOdEjeR(trim(rs.getString("od_eje_r")));
            this.setOdAdcR((rs.getString("od_adc_r")));
            this.setOdAdiR(trim(rs.getString("od_adi_r")));
            this.setOdPrismaH(trim(rs.getString("od_prisma_h")));
            this.setOiEsfR(trim(rs.getString("oi_esf_r")));
            this.setOiCilR(trim(rs.getString("oi_cil_r")));
            this.setOiEjeR(trim(rs.getString("oi_eje_r")));
            this.setOiAdcR(trim(rs.getString("oi_adc_r")));
            this.setOiAdiR(trim(rs.getString("oi_adi_r")));
            this.setOiPrismaH(trim(rs.getString("oi_prisma_h")));
            this.setDiLejosR(trim(rs.getString("di_lejos_r")));
            this.setDiCercaR(trim(rs.getString("di_cerca_r")));
            this.setOdAvR(trim(rs.getString("od_av_r")));
            this.setOiAvR(trim(rs.getString("oi_av_r")));
            this.setAltOblR(trim(rs.getString("alt_obl_r")));
            this.setObservacionesR(trim(rs.getString("observaciones_r")));
            this.setDiOd(trim(rs.getString("di_od")));
            this.setDiOi(trim(rs.getString("di_oi")));
            this.setMaterialArm(trim(rs.getString("material_arm")));
            this.setOdPrismaV(trim(rs.getString("od_prisma_v")));
            this.setOiPrismaV(trim(rs.getString("oi_prisma_v")));
            this.setTratamientos(trim(rs.getString("tratamientos")));
            this.setUdf5(trim(rs.getString("udf5")));
            this.setUdf6(trim(rs.getString("udf6")));
            this.setIdRxOri(trim(rs.getString("id_rx_ori")));
            this.setFolio(trim(rs.getString("folio")));
            this.setIdSucursal(rs.getInt("id_sucursal"));
            this.setfImpresa(rs.getBoolean("f_impresa"));
            this.setIdSync(rs.getString("id_sync"));
            this.setFechaMod(rs.getDate("fecha_mod"));
            this.setIdMod(rs.getString("id_mod"));
            this.setEmpleado( empleado() );
            this.setNotaVenta( notaVenta() );
        } catch (SQLException e) {
          e.printStackTrace();
        }
		return this;
	}
	
	public String cliNombreCompleto( ){
      String nombre = "";
      ClientesJava clientesJava = null;
      try {
        clientesJava = ClientesQuery.busquedaClienteById( idCliente );
      } catch (ParseException e) {
        e.printStackTrace();
      }
      if( clientesJava != null ){
        nombre = StringUtils.trimToEmpty(clientesJava.getNombreCli())+" "+StringUtils.trimToEmpty(clientesJava.getApellidoPatCli())+" "+StringUtils.trimToEmpty(clientesJava.getApellidoMatCli());
      } else {
        nombre = "";
      }
      return nombre;
    }

  public EmpleadoJava empleado( ){
    EmpleadoJava empleadoJava = new EmpleadoJava();
    empleadoJava = EmpleadoQuery.buscaEmpPorIdEmpleado(idOptometrista);
    return empleadoJava;
  }

  public NotaVentaJava notaVenta( ) throws ParseException {
    NotaVentaJava notaVentaJava = new NotaVentaJava();
    notaVentaJava = NotaVentaQuery.busquedaNotaByReceta( idReceta );
    return notaVentaJava;
  }

}