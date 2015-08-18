package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.querys.NotaVentaQuery;
import mx.lux.pos.java.querys.PlanQuery;
import mx.lux.pos.java.querys.TerminalQuery;
import mx.lux.pos.java.querys.TipoPagoQuery;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class PagoJava {

	Integer idPago;
	String idFactura;
	String idBanco;
	String idFormaPago;
	String tipoPago;
	String referenciaPago;
	BigDecimal montoPago;
	Date fechaPago;
	String idEmpleado;
	String idSync;
    Date fechaMod;
	String idMod;
	Integer idSucursal;
	String idRecibo;
	String parcialidad;
	String idFPago;
    String claveP;
    String refClave;
    String idBancoEmi;
    String idTerm;
    String idPlan;
    Boolean confirm;
    BigDecimal porDev;
    TipoPagoJava eTipoPago;
    TerminalJava terminal;
    PlanJava plan;
    //NotaVentaJava notaVenta;
    String factura;

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(String idBanco) {
        this.idBanco = idBanco;
    }

    public String getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(String idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }

    public BigDecimal getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(BigDecimal montoPago) {
        this.montoPago = montoPago;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getIdSync() {
        return idSync;
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
        return idMod;
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

    public String getIdRecibo() {
        return idRecibo;
    }

    public void setIdRecibo(String idRecibo) {
        this.idRecibo = idRecibo;
    }

    public String getParcialidad() {
        return parcialidad;
    }

    public void setParcialidad(String parcialidad) {
        this.parcialidad = parcialidad;
    }

    public String getIdFPago() {
        return idFPago;
    }

    public void setIdFPago(String idFPago) {
        this.idFPago = idFPago;
    }

    public String getClaveP() {
        return claveP;
    }

    public void setClaveP(String claveP) {
        this.claveP = claveP;
    }

    public String getRefClave() {
        return refClave;
    }

    public void setRefClave(String refClave) {
        this.refClave = refClave;
    }

    public String getIdBancoEmi() {
        return idBancoEmi;
    }

    public void setIdBancoEmi(String idBancoEmi) {
        this.idBancoEmi = idBancoEmi;
    }

    public String getIdTerm() {
        return idTerm;
    }

    public void setIdTerm(String idTerm) {
        this.idTerm = idTerm;
    }

    public String getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(String idPlan) {
        this.idPlan = idPlan;
    }

    public Boolean getConfirm() {
        return confirm;
    }

    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
    }

    public BigDecimal getPorDev() {
        return porDev;
    }

    public void setPorDev(BigDecimal porDev) {
        this.porDev = porDev;
    }

    public TipoPagoJava geteTipoPago() {
        return eTipoPago;
    }

    public void seteTipoPago(TipoPagoJava eTipoPago) {
        this.eTipoPago = eTipoPago;
    }

    public TerminalJava getTerminal() {
        return terminal;
    }

    public void setTerminal(TerminalJava terminal) {
        this.terminal = terminal;
    }

    public PlanJava getPlan() {
        return plan;
    }

    public void setPlan(PlanJava plan) {
        this.plan = plan;
    }

    /*public NotaVentaJava getNotaVenta() {
        return notaVenta;
    }

    public void setNotaVenta(NotaVentaJava notaVenta) {
        this.notaVenta = notaVenta;
    }*/

    public String getFactura() throws ParseException {
        return NotaVentaQuery.busquedaFacturaByIdFactura(idFactura);
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    private String trim( String data ){
      return StringUtils.trimToEmpty(data);
    }

    public PagoJava setValores( ResultSet rs ) throws SQLException, ParseException {
        this.setIdPago(rs.getInt("id_pago"));
        this.setIdFactura(trim(rs.getString("id_factura")));
        this.setIdBanco(trim(rs.getString("id_banco")));
        this.setIdFormaPago(trim(rs.getString("id_forma_pago")));
        this.setTipoPago(trim(rs.getString("tipo_pago")));
        this.setReferenciaPago(trim(rs.getString("referencia_pago")));
        this.setMontoPago(Utilities.toBigDecimal(trim(rs.getString("monto_pago"))));
        this.setFechaPago(rs.getDate("fecha_pago"));
        this.setIdEmpleado(trim(rs.getString("id_empleado")));
        this.setIdSync(trim(rs.getString("id_sync")));
        this.setFechaMod(rs.getDate("fecha_mod"));
        this.setIdMod(trim(rs.getString("id_mod")));
        this.setIdSucursal(rs.getInt("id_sucursal"));
        this.setIdRecibo(trim(rs.getString("id_recibo")));
        this.setParcialidad(trim(rs.getString("parcialidad")));
        this.setIdFPago(trim(rs.getString("id_f_pago")));
        this.setClaveP(trim(rs.getString("clave_p")));
        this.setRefClave(trim(rs.getString("ref_clave")));
        this.setIdBancoEmi(trim(rs.getString("id_banco_emi")));
        this.setIdTerm(trim(rs.getString("id_term")));
        this.setIdPlan(trim(rs.getString("id_plan")));
        this.setConfirm(Utilities.toBoolean(rs.getBoolean("confirm")));
        this.setPorDev(Utilities.toBigDecimal(trim(rs.getString("por_dev"))));
        this.seteTipoPago( tipoPago() );
        this.setTerminal( terminal() );
        this.setPlan( plan() );
        //this.setNotaVenta(notaVenta());

        return this;
	}


    public PagoJava setValoresTmp( ResultSet rs ) throws SQLException, ParseException {
        this.setIdPago(rs.getInt("id_pago"));
        this.setIdFactura(trim(rs.getString("id_factura")));
        this.setIdBanco(trim(rs.getString("id_banco")));
        this.setIdFormaPago(trim(rs.getString("id_forma_pago")));
        this.setTipoPago(trim(rs.getString("tipo_pago")));
        this.setReferenciaPago(trim(rs.getString("referencia_pago")));
        this.setMontoPago(Utilities.toBigDecimal(trim(rs.getString("monto_pago"))));
        this.setFechaPago(rs.getDate("fecha_pago"));
        this.setIdEmpleado(trim(rs.getString("id_empleado")));
        this.setIdSync(trim(rs.getString("id_sync")));
        this.setFechaMod(rs.getDate("fecha_mod"));
        this.setIdMod(trim(rs.getString("id_mod")));
        this.setIdSucursal(rs.getInt("id_sucursal"));
        this.setIdRecibo(trim(rs.getString("id_recibo")));
        this.setParcialidad(trim(rs.getString("parcialidad")));
        this.setIdFPago(trim(rs.getString("id_f_pago")));
        this.setClaveP(trim(rs.getString("clave_p")));
        this.setRefClave(trim(rs.getString("ref_clave")));
        this.setIdBancoEmi(trim(rs.getString("id_banco_emi")));
        this.setIdTerm(trim(rs.getString("id_term")));
        this.setIdPlan(trim(rs.getString("id_plan")));
        this.setConfirm(Utilities.toBoolean(rs.getBoolean("confirm")));
        this.setPorDev(Utilities.toBigDecimal(trim(rs.getString("por_dev"))));
        this.seteTipoPago( new TipoPagoJava().mapeoTipoPago(rs) );
        this.setTerminal( new TerminalJava().mapeoTerminal(rs) );
        this.setPlan( new PlanJava().mapeoPlan(rs) );
        //this.setNotaVenta(notaVenta());

        return this;
    }


    private TipoPagoJava tipoPago( ) throws ParseException {
      TipoPagoJava tipoPagoJava = new TipoPagoJava();
      tipoPagoJava = TipoPagoQuery.buscaParametroPoridFPago(idFPago);
      return tipoPagoJava;
    }

    private TerminalJava terminal( ) throws ParseException {
      TerminalJava terminalJava = new TerminalJava();
      terminalJava = TerminalQuery.busquedaTerminalPorIdTerm(idTerm);
      return terminalJava;
    }

    private PlanJava plan( ) throws ParseException {
      PlanJava planJava = new PlanJava();
      planJava = PlanQuery.BuscaPlanPorIdPlan(idPlan);
      return planJava;
    }

    /*private NotaVentaJava notaVenta( ) throws ParseException {
      NotaVentaJava notaVentaJava = new NotaVentaJava();
      notaVentaJava = NotaVentaQuery.busquedaNotaById( idFactura );
      return notaVentaJava;
    }*/

    public void trim(){
      this.setIdFactura(StringUtils.trimToEmpty(this.getIdFactura()));
      this.setIdBanco(StringUtils.trimToEmpty(this.getIdBanco()));
      this.setIdFormaPago(StringUtils.trimToEmpty(this.getIdFormaPago()));
      this.setTipoPago(StringUtils.trimToEmpty(this.getTipoPago()));
      this.setReferenciaPago(StringUtils.trimToEmpty(this.getReferenciaPago()));
      this.setIdEmpleado(StringUtils.trimToEmpty(this.getIdEmpleado()));
      this.setIdSync(StringUtils.trimToEmpty(this.getIdSync()));
      this.setIdMod(StringUtils.trimToEmpty(this.getIdMod()));
      this.setIdRecibo(StringUtils.trimToEmpty(this.getIdRecibo()));
      this.setParcialidad(StringUtils.trimToEmpty(this.getParcialidad()));
      this.setIdFPago(StringUtils.trimToEmpty(this.getIdFPago()));
      this.setClaveP(StringUtils.trimToEmpty(this.getClaveP()));
      this.setRefClave(StringUtils.trimToEmpty(this.getRefClave()));
      this.setIdBancoEmi(StringUtils.trimToEmpty(this.getIdBancoEmi()));
      this.setIdTerm(StringUtils.trimToEmpty(this.getIdTerm()));
      this.setIdPlan(StringUtils.trimToEmpty(this.getIdPlan()));
    }
}
