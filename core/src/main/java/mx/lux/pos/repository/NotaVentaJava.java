package mx.lux.pos.repository;

import java.math.BigDecimal;
import java.util.Date;

public class NotaVentaJava {

	String idFactura;
	String idEmpleado;
	Integer idCliente;
	String idConvenio;
	Integer idRepVenta;
	String tipoNotaVenta;
	Date fechaRecOrd;
	String tipoCli;
	Boolean fExpideFactura;
	BigDecimal ventaTotal;
	BigDecimal ventaNeta;
	BigDecimal sumaPagos;
	Date fechaHoraFactura;
	Date fechaPrometida;
	Date fechaEntrega;
	Boolean fArmazonCli;
	Integer por100Descuento;
	BigDecimal montoDescuento;
	String tipoDescuento;
	String idEmpleadoDescto;
	Boolean fResumenNotasMo;
	String sFactura;
	Integer numeroOrden;
	String tipoEntrega;
	String observacionesNv;
    String idSync;
    Date fechaMod;
    String idMod;
    Integer idSucursal;
    String factura;
    String cantLente;
    String udf2;
    String udf3;
    String udf4;
    String udf5;
    String sucDest;
    String tDeduc;
    Integer receta;
    String empEntrego;
    String lc;
    Date horaEntrega;
    Boolean descuento;
    Boolean polEnt;
    String tipoVenta;
    BigDecimal poliza;
    String codigoLente;

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdConvenio() {
        return idConvenio;
    }

    public void setIdConvenio(String idConvenio) {
        this.idConvenio = idConvenio;
    }

    public Integer getIdRepVenta() {
        return idRepVenta;
    }

    public void setIdRepVenta(Integer idRepVenta) {
        this.idRepVenta = idRepVenta;
    }

    public String getTipoNotaVenta() {
        return tipoNotaVenta;
    }

    public void setTipoNotaVenta(String tipoNotaVenta) {
        this.tipoNotaVenta = tipoNotaVenta;
    }

    public Date getFechaRecOrd() {
        return fechaRecOrd;
    }

    public void setFechaRecOrd(Date fechaRecOrd) {
        this.fechaRecOrd = fechaRecOrd;
    }

    public String getTipoCli() {
        return tipoCli;
    }

    public void setTipoCli(String tipoCli) {
        this.tipoCli = tipoCli;
    }

    public Boolean getfExpideFactura() {
        return fExpideFactura;
    }

    public void setfExpideFactura(Boolean fExpideFactura) {
        this.fExpideFactura = fExpideFactura;
    }

    public BigDecimal getVentaTotal() {
        return ventaTotal;
    }

    public void setVentaTotal(BigDecimal ventaTotal) {
        this.ventaTotal = ventaTotal;
    }

    public BigDecimal getVentaNeta() {
        return ventaNeta;
    }

    public void setVentaNeta(BigDecimal ventaNeta) {
        this.ventaNeta = ventaNeta;
    }

    public BigDecimal getSumaPagos() {
        return sumaPagos;
    }

    public void setSumaPagos(BigDecimal sumaPagos) {
        this.sumaPagos = sumaPagos;
    }

    public Date getFechaHoraFactura() {
        return fechaHoraFactura;
    }

    public void setFechaHoraFactura(Date fechaHoraFactura) {
        this.fechaHoraFactura = fechaHoraFactura;
    }

    public Date getFechaPrometida() {
        return fechaPrometida;
    }

    public void setFechaPrometida(Date fechaPrometida) {
        this.fechaPrometida = fechaPrometida;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Boolean getfArmazonCli() {
        return fArmazonCli;
    }

    public void setfArmazonCli(Boolean fArmazonCli) {
        this.fArmazonCli = fArmazonCli;
    }

    public Integer getPor100Descuento() {
        return por100Descuento;
    }

    public void setPor100Descuento(Integer por100Descuento) {
        this.por100Descuento = por100Descuento;
    }

    public BigDecimal getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(BigDecimal montoDescuento) {
        this.montoDescuento = montoDescuento;
    }

    public String getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(String tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public String getIdEmpleadoDescto() {
        return idEmpleadoDescto;
    }

    public void setIdEmpleadoDescto(String idEmpleadoDescto) {
        this.idEmpleadoDescto = idEmpleadoDescto;
    }

    public Boolean getfResumenNotasMo() {
        return fResumenNotasMo;
    }

    public void setfResumenNotasMo(Boolean fResumenNotasMo) {
        this.fResumenNotasMo = fResumenNotasMo;
    }

    public String getsFactura() {
        return sFactura;
    }

    public void setsFactura(String sFactura) {
        this.sFactura = sFactura;
    }

    public Integer getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(Integer numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getObservacionesNv() {
        return observacionesNv;
    }

    public void setObservacionesNv(String observacionesNv) {
        this.observacionesNv = observacionesNv;
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

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getCantLente() {
        return cantLente;
    }

    public void setCantLente(String cantLente) {
        this.cantLente = cantLente;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

    public String getUdf3() {
        return udf3;
    }

    public void setUdf3(String udf3) {
        this.udf3 = udf3;
    }

    public String getUdf4() {
        return udf4;
    }

    public void setUdf4(String udf4) {
        this.udf4 = udf4;
    }

    public String getUdf5() {
        return udf5;
    }

    public void setUdf5(String udf5) {
        this.udf5 = udf5;
    }

    public String getSucDest() {
        return sucDest;
    }

    public void setSucDest(String sucDest) {
        this.sucDest = sucDest;
    }

    public String gettDeduc() {
        return tDeduc;
    }

    public void settDeduc(String tDeduc) {
        this.tDeduc = tDeduc;
    }

    public Integer getReceta() {
        return receta;
    }

    public void setReceta(Integer receta) {
        this.receta = receta;
    }

    public String getEmpEntrego() {
        return empEntrego;
    }

    public void setEmpEntrego(String empEntrego) {
        this.empEntrego = empEntrego;
    }

    public String getLc() {
        return lc;
    }

    public void setLc(String lc) {
        this.lc = lc;
    }

    public Date getHoraEntrega() {
        return horaEntrega;
    }

    public void setHoraEntrega(Date horaEntrega) {
        this.horaEntrega = horaEntrega;
    }

    public Boolean getDescuento() {
        return descuento;
    }

    public void setDescuento(Boolean descuento) {
        this.descuento = descuento;
    }

    public Boolean getPolEnt() {
        return polEnt;
    }

    public void setPolEnt(Boolean polEnt) {
        this.polEnt = polEnt;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public BigDecimal getPoliza() {
        return poliza;
    }

    public void setPoliza(BigDecimal poliza) {
        this.poliza = poliza;
    }

    public String getCodigoLente() {
        return codigoLente;
    }

    public void setCodigoLente(String codigoLente) {
        this.codigoLente = codigoLente;
    }

    public NotaVentaJava setValores( String idFactura, String idEmpleado, Integer idCliente, String idConvenio, Integer idRepVenta, String tipoNotaVenta,
			Date fechaRecOrd, String tipoCli, Boolean fExpideFactura, BigDecimal ventaTotal, BigDecimal ventaNeta, BigDecimal sumaPagos,
			Date fechaHoraFactura, Date fechaPrometida, Date fechaEntrega, Boolean fArmazonCli, Integer por100Descuento, BigDecimal montoDescuento,
			String tipoDescuento, String idEmpleadoDescto, Boolean fResumenNotasMo, String sFactura, Integer numeroOrden, String tipoEntrega,
			String observacionesNv, String idSync, Date fechaMod, String idMod, Integer idSucursal, String factura, String cantLente,
            String udf2, String udf3, String udf4, String udf5, String sucDest, String tDeduc, Integer receta, String empEntrego, String lc,
            Date horaEntrega, Boolean descuento, Boolean polEnt, String tipoVenta, BigDecimal poliza, String codigoLente ){
		
		NotaVentaJava notaVentaJava = new NotaVentaJava();
		this.setIdFactura(idFactura);
		this.setIdEmpleado(idEmpleado);
		this.setIdCliente(idCliente);
		this.setIdConvenio(idConvenio);
		this.setIdRepVenta(idRepVenta);
		this.setTipoNotaVenta(tipoNotaVenta);
		this.setFechaRecOrd(fechaRecOrd);
		this.setTipoCli(tipoCli);
		this.setfExpideFactura(fExpideFactura);
		this.setVentaTotal(ventaTotal);
		this.setVentaNeta(ventaNeta);
		this.setSumaPagos(sumaPagos);
		this.setFechaHoraFactura(fechaHoraFactura);
		this.setFechaPrometida(fechaPrometida);
		this.setFechaEntrega(fechaEntrega);
		this.setfArmazonCli(fArmazonCli);
		this.setPor100Descuento(por100Descuento);
		this.setMontoDescuento(montoDescuento);
		this.setTipoDescuento(tipoDescuento);
		this.setIdEmpleadoDescto(idEmpleadoDescto);
		this.setfResumenNotasMo(fResumenNotasMo);
		this.setsFactura(sFactura);
		this.setNumeroOrden(numeroOrden);
		this.setTipoEntrega(tipoEntrega);
        this.setObservacionesNv(observacionesNv);
        this.setIdSync(idSync);
        this.setFechaMod(fechaMod);
        this.setIdMod(idMod);
        this.setIdSucursal(idSucursal);
        this.setFactura(factura);
        this.setCantLente(cantLente);
        this.setUdf2(udf2);
        this.setUdf3(udf3);
        this.setUdf4(udf4);
        this.setUdf5(udf5);
        this.setSucDest(sucDest);
        this.settDeduc(tDeduc);
        this.setReceta(receta);
        this.setEmpEntrego(empEntrego);
        this.setLc(lc);
        this.setHoraEntrega(horaEntrega);
        this.setDescuento(descuento);
        this.setPolEnt(polEnt);
        this.setTipoVenta(tipoVenta);
        this.setPoliza(poliza);
        this.setCodigoLente(codigoLente);
		
		return notaVentaJava;
	}
	
	
}
