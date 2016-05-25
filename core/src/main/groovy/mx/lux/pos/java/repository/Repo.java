package mx.lux.pos.java.repository;

import mx.lux.pos.java.querys.RepoQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Repo {

	String factura;
    Integer numOrden;
	String emp;
	String resp;
	Date fecha;
	String tipo;
    Integer idCliente;
    String causa;
    String problema;
    String dx;
    String instrucciones;
    String sUsoAnteojos;
    String odEsf;
    String odCil;
    String odEje;
    String odAdc;
    String odAdi;
    String odAv;
    String diOd;
    String odPrisma;
    String odPrismaV;
    String oiEsf;
    String oiCil;
    String oiEje;
    String oiAdc;
    String oiAdi;
    String oiAv;
    String diOi;
    String oiPrisma;
    String oiPrismaV;
    String diLejos;
    String diCerca;
    String altObl;
    String observaciones;
    String area;
    String folio;
    String cliente;
    String material;
    String tratamientos;
    String suc;
    String ojo;
    String alturaIndDer;
    String alturaIndIzq;
    String distanciaVertex;
    String anguloPantoscopico;
    String anguloFacial;
    String tamanoCorredor;
    String diametroLenticular;
    List<RepoDetJava> repoDet;


    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public Integer getNumOrden() {
        return numOrden;
    }

    public void setNumOrden(Integer numOrden) {
        this.numOrden = numOrden;
    }

    public String getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getDx() {
        return dx;
    }

    public void setDx(String dx) {
        this.dx = dx;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public String getsUsoAnteojos() {
        return sUsoAnteojos;
    }

    public void setsUsoAnteojos(String sUsoAnteojos) {
        this.sUsoAnteojos = sUsoAnteojos;
    }

    public String getOdEsf() {
        return odEsf;
    }

    public void setOdEsf(String odEsf) {
        this.odEsf = odEsf;
    }

    public String getOdCil() {
        return odCil;
    }

    public void setOdCil(String odCil) {
        this.odCil = odCil;
    }

    public String getOdEje() {
        return odEje;
    }

    public void setOdEje(String odEje) {
        this.odEje = odEje;
    }

    public String getOdAdc() {
        return odAdc;
    }

    public void setOdAdc(String odAdc) {
        this.odAdc = odAdc;
    }

    public String getOdAdi() {
        return odAdi;
    }

    public void setOdAdi(String odAdi) {
        this.odAdi = odAdi;
    }

    public String getOdAv() {
        return odAv;
    }

    public void setOdAv(String odAv) {
        this.odAv = odAv;
    }

    public String getDiOd() {
        return diOd;
    }

    public void setDiOd(String diOd) {
        this.diOd = diOd;
    }

    public String getOdPrisma() {
        return odPrisma;
    }

    public void setOdPrisma(String odPrisma) {
        this.odPrisma = odPrisma;
    }

    public String getOdPrismaV() {
        return odPrismaV;
    }

    public void setOdPrismaV(String odPrismaV) {
        this.odPrismaV = odPrismaV;
    }

    public String getOiEsf() {
        return oiEsf;
    }

    public void setOiEsf(String oiEsf) {
        this.oiEsf = oiEsf;
    }

    public String getOiCil() {
        return oiCil;
    }

    public void setOiCil(String oiCil) {
        this.oiCil = oiCil;
    }

    public String getOiEje() {
        return oiEje;
    }

    public void setOiEje(String oiEje) {
        this.oiEje = oiEje;
    }

    public String getOiAdc() {
        return oiAdc;
    }

    public void setOiAdc(String oiAdc) {
        this.oiAdc = oiAdc;
    }

    public String getOiAdi() {
        return oiAdi;
    }

    public void setOiAdi(String oiAdi) {
        this.oiAdi = oiAdi;
    }

    public String getOiAv() {
        return oiAv;
    }

    public void setOiAv(String oiAv) {
        this.oiAv = oiAv;
    }

    public String getDiOi() {
        return diOi;
    }

    public void setDiOi(String diOi) {
        this.diOi = diOi;
    }

    public String getOiPrisma() {
        return oiPrisma;
    }

    public void setOiPrisma(String oiPrisma) {
        this.oiPrisma = oiPrisma;
    }

    public String getOiPrismaV() {
        return oiPrismaV;
    }

    public void setOiPrismaV(String oiPrismaV) {
        this.oiPrismaV = oiPrismaV;
    }

    public String getDiLejos() {
        return diLejos;
    }

    public void setDiLejos(String diLejos) {
        this.diLejos = diLejos;
    }

    public String getDiCerca() {
        return diCerca;
    }

    public void setDiCerca(String diCerca) {
        this.diCerca = diCerca;
    }

    public String getAltObl() {
        return altObl;
    }

    public void setAltObl(String altObl) {
        this.altObl = altObl;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getTratamientos() {
        return tratamientos;
    }

    public void setTratamientos(String tratamientos) {
        this.tratamientos = tratamientos;
    }

    public String getSuc() {
        return suc;
    }

    public void setSuc(String suc) {
        this.suc = suc;
    }

    public String getOjo() {
        return ojo;
    }

    public void setOjo(String ojo) {
        this.ojo = ojo;
    }

    public String getAlturaIndDer() {
        return alturaIndDer;
    }

    public void setAlturaIndDer(String alturaIndDer) {
        this.alturaIndDer = alturaIndDer;
    }

    public String getAlturaIndIzq() {
        return alturaIndIzq;
    }

    public void setAlturaIndIzq(String alturaIndIzq) {
        this.alturaIndIzq = alturaIndIzq;
    }

    public String getDistanciaVertex() {
        return distanciaVertex;
    }

    public void setDistanciaVertex(String distanciaVertex) {
        this.distanciaVertex = distanciaVertex;
    }

    public String getAnguloPantoscopico() {
        return anguloPantoscopico;
    }

    public void setAnguloPantoscopico(String anguloPantoscopico) {
        this.anguloPantoscopico = anguloPantoscopico;
    }

    public String getAnguloFacial() {
        return anguloFacial;
    }

    public void setAnguloFacial(String anguloFacial) {
        this.anguloFacial = anguloFacial;
    }

    public String getTamanoCorredor() {
        return tamanoCorredor;
    }

    public void setTamanoCorredor(String tamanoCorredor) {
        this.tamanoCorredor = tamanoCorredor;
    }

    public String getDiametroLenticular() {
        return diametroLenticular;
    }

    public void setDiametroLenticular(String diametroLenticular) {
        this.diametroLenticular = diametroLenticular;
    }

    public List<RepoDetJava> getRepoDet() {
        return repoDet;
    }

    public void setRepoDet(List<RepoDetJava> repoDet) {
        this.repoDet = repoDet;
    }

    public Repo mapeoRepo( ResultSet rs ) throws SQLException, ParseException {
      this.setFactura(rs.getString("factura"));
      this.setNumOrden(rs.getInt("num_orden"));
      this.setEmp(rs.getString("emp"));
      this.setResp(rs.getString("resp"));
      this.setFecha(rs.getDate("fecha"));
      this.setTipo(rs.getString("tipo"));
      this.setIdCliente(rs.getInt("id_cliente"));
      this.setCausa(rs.getString("causa"));
      this.setProblema(rs.getString("problema"));
      this.setDx(rs.getString("dx"));
      this.setInstrucciones(rs.getString("instrucciones"));
      this.setsUsoAnteojos(rs.getString("s_uso_anteojos"));
      this.setOdEsf(rs.getString("od_esf"));
      this.setOdCil(rs.getString("od_cil"));
      this.setOdEje(rs.getString("od_eje"));
      this.setOdAdc(rs.getString("od_adc"));
      this.setOdAdi(rs.getString("od_adi"));
      this.setOdAv(rs.getString("od_av"));
      this.setDiOd(rs.getString("di_od"));
      this.setOdPrisma(rs.getString("od_prisma"));
      this.setOdPrismaV(rs.getString("od_prisma_v"));
      this.setOiEsf(rs.getString("oi_esf"));
      this.setOiCil(rs.getString("oi_cil"));
      this.setOiEje(rs.getString("oi_eje"));
      this.setOiAdc(rs.getString("oi_adc"));
      this.setOiAdi(rs.getString("oi_adi"));
      this.setOiAv(rs.getString("oi_av"));
      this.setDiOi(rs.getString("di_oi"));
      this.setOiPrisma(rs.getString("oi_prisma"));
      this.setOiPrismaV(rs.getString("oi_prisma_v"));
      this.setDiLejos(rs.getString("di_lejos"));
      this.setDiCerca(rs.getString("di_cerca"));
      this.setAltObl(rs.getString("alt_obl"));
      this.setObservaciones(rs.getString("observaciones"));
      this.setArea(rs.getString("area"));
      this.setFolio(rs.getString("folio"));
      this.setCliente(rs.getString("cliente"));
      this.setMaterial(rs.getString("material"));
      this.setTratamientos(rs.getString("tratamientos"));
      this.setSuc(rs.getString("suc"));
      this.setOjo(rs.getString("ojo"));
      this.setAlturaIndDer(rs.getString("altura_ind_der"));
      this.setAlturaIndIzq(rs.getString("altura_ind_izq"));
      this.setDistanciaVertex(rs.getString("distancia_vertex"));
      this.setAnguloPantoscopico(rs.getString("angulo_pantoscopico"));
      this.setAnguloFacial(rs.getString("angulo_facial"));
      this.setTamanoCorredor(rs.getString("tamano_corredor"));
      this.setDiametroLenticular(rs.getString("diametro_lenticular"));
      this.setRepoDet( repoDet() );
      return this;
    }


    public List<RepoDetJava> repoDet( ) throws ParseException {
      List<RepoDetJava> repoDet = new ArrayList<RepoDetJava>();
      repoDet = RepoQuery.busquedaRepoDetByNumOrderAndFactura(numOrden, factura);
      return repoDet;
    }
}
