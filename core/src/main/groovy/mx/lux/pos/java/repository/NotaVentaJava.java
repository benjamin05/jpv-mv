package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.querys.*;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    ClientesJava cliente;
    SucursalesJava sucursal;
    List<DetalleNotaVentaJava> detalles;
    List<PagoJava> pagos;
    List<OrdenPromDetJava> ordenPromDet;
    EmpleadoJava empleado;
    EmpleadoJava empleadoEntrego;
    DescuentosJava descuentosJava;

    public String getIdFactura() {
        return StringUtils.trimToEmpty(idFactura);
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getIdEmpleado() {
        return StringUtils.trimToEmpty(idEmpleado);
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
        return StringUtils.trimToEmpty(idConvenio);
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
        return StringUtils.trimToEmpty(tipoNotaVenta);
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
        return StringUtils.trimToEmpty(tipoCli);
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
        return StringUtils.trimToEmpty(tipoDescuento);
    }

    public void setTipoDescuento(String tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public String getIdEmpleadoDescto() {
        return StringUtils.trimToEmpty(idEmpleadoDescto);
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
        return StringUtils.trimToEmpty(sFactura);
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
        return StringUtils.trimToEmpty(tipoEntrega);
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getObservacionesNv() {
        return StringUtils.trimToEmpty(observacionesNv);
    }

    public void setObservacionesNv(String observacionesNv) {
        this.observacionesNv = observacionesNv;
    }

    public String getIdSync() {
        return StringUtils.trimToEmpty(idSync);
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
        return StringUtils.trimToEmpty(idMod);
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
        return StringUtils.trimToEmpty(factura);
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getCantLente() {
        return StringUtils.trimToEmpty(cantLente);
    }

    public void setCantLente(String cantLente) {
        this.cantLente = cantLente;
    }

    public String getUdf2() {
        return StringUtils.trimToEmpty(udf2);
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

    public String getUdf3() {
        return StringUtils.trimToEmpty(udf3);
    }

    public void setUdf3(String udf3) {
        this.udf3 = udf3;
    }

    public String getUdf4() {
        return StringUtils.trimToEmpty(udf4);
    }

    public void setUdf4(String udf4) {
        this.udf4 = udf4;
    }

    public String getUdf5() {
        return StringUtils.trimToEmpty(udf5);
    }

    public void setUdf5(String udf5) {
        this.udf5 = udf5;
    }

    public String getSucDest() {
        return StringUtils.trimToEmpty(sucDest);
    }

    public void setSucDest(String sucDest) {
        this.sucDest = sucDest;
    }

    public String gettDeduc() {
        return StringUtils.trimToEmpty(tDeduc);
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
        return StringUtils.trimToEmpty(empEntrego);
    }

    public void setEmpEntrego(String empEntrego) {
        this.empEntrego = empEntrego;
    }

    public String getLc() {
        return StringUtils.trimToEmpty(lc);
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
        return StringUtils.trimToEmpty(tipoVenta);
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
        return StringUtils.trimToEmpty(codigoLente);
    }

    public void setCodigoLente(String codigoLente) {
        this.codigoLente = codigoLente;
    }

    public ClientesJava getCliente() {
        return cliente;
    }

    public void setCliente(ClientesJava cliente) {
        this.cliente = cliente;
    }

    public SucursalesJava getSucursal() {
        return sucursal;
    }

    public void setSucursal(SucursalesJava sucursal) {
        this.sucursal = sucursal;
    }

    public List<DetalleNotaVentaJava> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleNotaVentaJava> detalles) {
        this.detalles = detalles;
    }

    public List<PagoJava> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoJava> pagos) {
        this.pagos = pagos;
    }

    public EmpleadoJava getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadoJava empleado) {
        this.empleado = empleado;
    }

    public EmpleadoJava getEmpleadoEntrego() {
        return empleadoEntrego;
    }

    public void setEmpleadoEntrego(EmpleadoJava empleadoEntrego) {
        this.empleadoEntrego = empleadoEntrego;
    }

    public List<OrdenPromDetJava> getOrdenPromDet() {
        return ordenPromDet;
    }

    public void setOrdenPromDet(List<OrdenPromDetJava> ordenPromDet) {
        this.ordenPromDet = ordenPromDet;
    }

    public DescuentosJava getDescuentosJava() {
        return descuentosJava;
    }

    public void setDescuentosJava(DescuentosJava descuentosJava) {
        this.descuentosJava = descuentosJava;
    }

    public NotaVentaJava setValores( ResultSet rs ) throws SQLException {
      try {
            this.setIdFactura(rs.getString("id_factura"));
            this.setIdEmpleado(rs.getString("id_empleado"));
            this.setIdCliente(rs.getInt("id_cliente"));
            this.setIdConvenio(rs.getString("id_convenio"));
            this.setIdRepVenta(rs.getInt("id_rep_venta"));
            this.setTipoNotaVenta(rs.getString("tipo_nota_venta"));
            this.setFechaRecOrd(rs.getDate("fecha_rec_ord"));
            this.setTipoCli(rs.getString("tipo_cli"));
            this.setfExpideFactura(Utilities.toBoolean(rs.getBoolean("f_expide_factura")));
            this.setVentaTotal(Utilities.toBigDecimal(rs.getString("venta_total")));
            this.setVentaNeta(Utilities.toBigDecimal(rs.getString("venta_neta")));
            this.setSumaPagos(Utilities.toBigDecimal(rs.getString("suma_pagos")));
            this.setFechaHoraFactura(rs.getDate("fecha_hora_factura"));
            this.setFechaPrometida(rs.getDate("fecha_prometida"));
            this.setFechaEntrega(rs.getDate("fecha_entrega"));
            this.setfArmazonCli(Utilities.toBoolean(rs.getBoolean("f_armazon_cli")));
            this.setPor100Descuento(rs.getInt("por100_descuento"));
            this.setMontoDescuento(Utilities.toBigDecimal(rs.getString("monto_descuento")));
            this.setTipoDescuento(rs.getString("tipo_descuento"));
            this.setIdEmpleadoDescto(rs.getString("id_empleado_descto"));
            this.setfResumenNotasMo(Utilities.toBoolean(rs.getBoolean("f_resumen_notas_mo")));
            this.setsFactura(rs.getString("s_factura"));
            this.setNumeroOrden(rs.getInt("numero_orden"));
            this.setTipoEntrega(rs.getString("tipo_entrega"));
            this.setObservacionesNv(rs.getString("observaciones_nv"));
            this.setIdSync(rs.getString("id_sync"));
            this.setFechaMod(rs.getDate("fecha_mod"));
            this.setIdMod(rs.getString("id_mod"));
            this.setIdSucursal(rs.getInt("id_sucursal"));
            this.setFactura(rs.getString("factura"));
            this.setCantLente(rs.getString("cant_lente"));
            this.setUdf2(rs.getString("udf2"));
            this.setUdf3(rs.getString("udf3"));
            this.setUdf4(rs.getString("udf4"));
            this.setUdf5(rs.getString("udf5"));
            this.setSucDest(rs.getString("suc_dest"));
            this.settDeduc(rs.getString("t_deduc"));
            this.setReceta(rs.getInt("receta"));
            this.setEmpEntrego(rs.getString("emp_entrego"));
            this.setLc(rs.getString("lc"));
            this.setHoraEntrega(rs.getTime("hora_entrega"));
            this.setDescuento(Utilities.toBoolean(rs.getBoolean("descuento")));
            this.setPolEnt(Utilities.toBoolean(rs.getBoolean("pol_ent")));
            this.setTipoVenta(rs.getString("tipo_venta"));
            this.setPoliza(Utilities.toBigDecimal(rs.getString("poliza")));
            this.setCodigoLente(rs.getString("codigo_lente"));
            this.setCliente(cliente());
            this.setSucursal(sucursal());
            this.setDetalles(detalles());
            this.setPagos(pagos());
            this.setEmpleado(empleado());
            this.setOrdenPromDet(ordenPromDet());
            this.setDescuentosJava(descuentosJava());
        } catch (SQLException e) {
          System.out.println( e );
          e.printStackTrace();
        } catch (ParseException e) {
          System.out.println( e );
          e.printStackTrace();
        }
        return this;
	}


  private SucursalesJava sucursal( ) throws ParseException {
    SucursalesJava sucursalesJava = new SucursalesJava();
    sucursalesJava = SucursalesQuery.BuscaSucursalPorIdSuc(idSucursal);
    return sucursalesJava;
  }


  private ClientesJava cliente( ) throws ParseException {
    ClientesJava clientesJava = new ClientesJava();
    clientesJava = ClientesQuery.busquedaClienteById( idCliente );
    return clientesJava;
  }

  private DescuentosJava descuentosJava( ) throws ParseException {
    DescuentosJava descuentosJava1 = new DescuentosJava();
    List<DescuentosJava> lstDesc = DescuentosQuery.buscaDescuentosPorIdFactura( idFactura );
    descuentosJava1 = lstDesc.size() > 0 ? lstDesc.get(0) : null;
    return descuentosJava1;
  }

  public EmpleadoJava empleado( ){
    EmpleadoJava empleadoJava = new EmpleadoJava();
    empleadoJava = EmpleadoQuery.buscaEmpPorIdEmpleado(idEmpleado);
    return empleadoJava;
  }

  public EmpleadoJava empleadoEntrego( ){
    EmpleadoJava empleadoJava = new EmpleadoJava();
    empleadoJava = EmpleadoQuery.buscaEmpPorIdEmpleado(empEntrego);
    return empleadoJava;
  }

  private List<DetalleNotaVentaJava> detalles( ) throws ParseException {
    List<DetalleNotaVentaJava> lstDetalles = new ArrayList<DetalleNotaVentaJava>();
    lstDetalles = DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFactura(StringUtils.trimToEmpty(idFactura));
    return lstDetalles;
  }

  private List<PagoJava> pagos( ) throws ParseException {
    List<PagoJava> lstPagos = new ArrayList<PagoJava>();
    lstPagos = PagoQuery.busquedaPagosPorIdFactura(StringUtils.trimToEmpty(idFactura));
    return lstPagos;
  }

  private List<OrdenPromDetJava> ordenPromDet( ) throws ParseException {
    List<OrdenPromDetJava> lstOrdenPromDet = new ArrayList<OrdenPromDetJava>();
    lstOrdenPromDet = OrdenPromDetQuery.BuscaOrdenPromDetPorIdFactura(StringUtils.trimToEmpty(idFactura));
    return lstOrdenPromDet;
  }

  public NotaVentaJava trim(){
    this.setIdFactura(StringUtils.trimToEmpty(this.getIdFactura()));
    this.setIdEmpleado(StringUtils.trimToEmpty(this.getIdEmpleado()));
    this.setIdConvenio(StringUtils.trimToEmpty(this.getIdConvenio()));
    this.setTipoNotaVenta(StringUtils.trimToEmpty(this.getTipoNotaVenta()));
    this.setTipoCli(StringUtils.trimToEmpty(this.getTipoCli()));
    this.setTipoDescuento(StringUtils.trimToEmpty(this.getTipoDescuento()));
    this.setIdEmpleadoDescto(StringUtils.trimToEmpty(this.getIdEmpleadoDescto()));
    this.setsFactura(StringUtils.trimToEmpty(this.getsFactura()));
    this.setTipoEntrega(StringUtils.trimToEmpty(this.getTipoEntrega()));
    this.setObservacionesNv(StringUtils.trimToEmpty(this.getObservacionesNv()));
    this.setIdSync(StringUtils.trimToEmpty(this.getIdSync()));
    this.setIdMod(StringUtils.trimToEmpty(this.getIdMod()));
    this.setFactura(StringUtils.trimToEmpty(this.getFactura()));
    this.setCantLente(StringUtils.trimToEmpty(this.getCantLente()));
    this.setUdf2(StringUtils.trimToEmpty(this.getUdf2()));
    this.setUdf3(StringUtils.trimToEmpty(this.getUdf3()));
    this.setUdf4(StringUtils.trimToEmpty(this.getUdf4()));
    this.setUdf5(StringUtils.trimToEmpty(this.getUdf5()));
    this.setSucDest(StringUtils.trimToEmpty(this.getSucDest()));
    this.settDeduc(StringUtils.trimToEmpty(this.gettDeduc()));
    this.setEmpEntrego(StringUtils.trimToEmpty(this.getEmpEntrego()));
    this.setLc(StringUtils.trimToEmpty(this.getLc()));
    this.setTipoVenta(StringUtils.trimToEmpty(this.getTipoVenta()));
    this.setCodigoLente(StringUtils.trimToEmpty(this.getCodigoLente()));
    this.setfArmazonCli(this.getfArmazonCli() != null ? this.getfArmazonCli() : false);
    this.setfResumenNotasMo(this.getfResumenNotasMo() != null ? this.getfResumenNotasMo() : false);
    this.setPor100Descuento(this.getPor100Descuento() != null ? this.getPor100Descuento() : 0);
    return this;
  }

}
