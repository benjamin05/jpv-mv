package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.querys.ClientePaisQuery;
import mx.lux.pos.java.querys.MunicipioQuery;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ClientesJava {
	private Integer idCliente;
	private String idConvenio;
	private String titulo;
	private Integer idOftalmologo;
	private String tipoOft;
	private String idLocalidad;
	private String idEstado;
	private Date fechaAltaCli;
	private Boolean sexoCli;
	private String apellidoPatCli;
	private String apellidoMatCli;
    private Boolean fCasadaCli;
    private String nombreCli;
    private String rfcCli;
    private String direccionCli;
    private String coloniaCli;
    private String codigo;
    private String telCasaCli;
    private String telTrabCli;
    private String extTrabCli;
    private String telAdiCli;
    private String extAdiCli;
    private String emailCli;
    private String sUsaAnteojos;
    private Boolean avisar;
    private String idAtendio;
    private String udf1;
    private String udf2;
    private String cliOri;
    private String udf4;
    private String udf5;
    private String udf6;
    private Integer receta;
    private String obs;
    private Date fechaNac;
    private String cuc;
    private Date horaAlta;
    private Boolean finado;
    private Date fechaImp;
    private Integer principal;
    private MunicipioJava municipio;
    private ClientePaisJava clientePais;
    private String nombreCompleto;

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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getIdOftalmologo() {
        return idOftalmologo;
    }

    public void setIdOftalmologo(Integer idOftalmologo) {
        this.idOftalmologo = idOftalmologo;
    }

    public String getTipoOft() {
        return tipoOft;
    }

    public void setTipoOft(String tipoOft) {
        this.tipoOft = tipoOft;
    }

    public String getIdLocalidad() {
        return idLocalidad;
    }

    public void setIdLocalidad(String idLocalidad) {
        this.idLocalidad = idLocalidad;
    }

    public String getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    public Date getFechaAltaCli() {
        return fechaAltaCli;
    }

    public void setFechaAltaCli(Date fechaAltaCli) {
        this.fechaAltaCli = fechaAltaCli;
    }

    public Boolean getSexoCli() {
        return sexoCli;
    }

    public void setSexoCli(Boolean sexoCli) {
        this.sexoCli = sexoCli;
    }

    public String getApellidoPatCli() {
        return apellidoPatCli;
    }

    public void setApellidoPatCli(String apellidoPatCli) {
        this.apellidoPatCli = apellidoPatCli;
    }

    public String getApellidoMatCli() {
        return apellidoMatCli;
    }

    public void setApellidoMatCli(String apellidoMatCli) {
        this.apellidoMatCli = apellidoMatCli;
    }

    public Boolean getfCasadaCli() {
        return fCasadaCli;
    }

    public void setfCasadaCli(Boolean fCasadaCli) {
        this.fCasadaCli = fCasadaCli;
    }

    public String getNombreCli() {
        return nombreCli;
    }

    public void setNombreCli(String nombreCli) {
        this.nombreCli = nombreCli;
    }

    public String getRfcCli() {
        return rfcCli;
    }

    public void setRfcCli(String rfcCli) {
        this.rfcCli = rfcCli;
    }

    public String getDireccionCli() {
        return direccionCli;
    }

    public void setDireccionCli(String direccionCli) {
        this.direccionCli = direccionCli;
    }

    public String getColoniaCli() {
        return coloniaCli;
    }

    public void setColoniaCli(String coloniaCli) {
        this.coloniaCli = coloniaCli;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTelCasaCli() {
        return telCasaCli;
    }

    public void setTelCasaCli(String telCasaCli) {
        this.telCasaCli = telCasaCli;
    }

    public String getTelTrabCli() {
        return telTrabCli;
    }

    public void setTelTrabCli(String telTrabCli) {
        this.telTrabCli = telTrabCli;
    }

    public String getExtTrabCli() {
        return extTrabCli;
    }

    public void setExtTrabCli(String extTrabCli) {
        this.extTrabCli = extTrabCli;
    }

    public String getTelAdiCli() {
        return telAdiCli;
    }

    public void setTelAdiCli(String telAdiCli) {
        this.telAdiCli = telAdiCli;
    }

    public String getExtAdiCli() {
        return extAdiCli;
    }

    public void setExtAdiCli(String extAdiCli) {
        this.extAdiCli = extAdiCli;
    }

    public String getEmailCli() {
        return emailCli;
    }

    public void setEmailCli(String emailCli) {
        this.emailCli = emailCli;
    }

    public String getsUsaAnteojos() {
        return sUsaAnteojos;
    }

    public void setsUsaAnteojos(String sUsaAnteojos) {
        this.sUsaAnteojos = sUsaAnteojos;
    }

    public Boolean getAvisar() {
        return avisar;
    }

    public void setAvisar(Boolean avisar) {
        this.avisar = avisar;
    }

    public String getIdAtendio() {
        return idAtendio;
    }

    public void setIdAtendio(String idAtendio) {
        this.idAtendio = idAtendio;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

    public String getCliOri() {
        return cliOri;
    }

    public void setCliOri(String cliOri) {
        this.cliOri = cliOri;
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

    public String getUdf6() {
        return udf6;
    }

    public void setUdf6(String udf6) {
        this.udf6 = udf6;
    }

    public Integer getReceta() {
        return receta;
    }

    public void setReceta(Integer receta) {
        this.receta = receta;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public Date getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(Date fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getCuc() {
        return cuc;
    }

    public void setCuc(String cuc) {
        this.cuc = cuc;
    }

    public Date getHoraAlta() {
        return horaAlta;
    }

    public void setHoraAlta(Date horaAlta) {
        this.horaAlta = horaAlta;
    }

    public Boolean getFinado() {
        return finado;
    }

    public void setFinado(Boolean finado) {
        this.finado = finado;
    }

    public Date getFechaImp() {
        return fechaImp;
    }

    public void setFechaImp(Date fechaImp) {
        this.fechaImp = fechaImp;
    }

    public Integer getPrincipal() {
        return principal;
    }

    public void setPrincipal(Integer principal) {
        this.principal = principal;
    }

    public MunicipioJava getMunicipio() {
        return municipio;
    }

    public void setMunicipio(MunicipioJava municipio) {
        this.municipio = municipio;
    }

    public ClientePaisJava getClientePais() {
        return clientePais;
    }

    public void setClientePais(ClientePaisJava clientePais) {
        this.clientePais = clientePais;
    }

    public String getNombreCompleto() {
        return StringUtils.trimToEmpty(getNombreCli())+" "+StringUtils.trimToEmpty(getApellidoPatCli())+" "+StringUtils.trimToEmpty(getApellidoMatCli());
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public ClientesJava mapeoCliente( ResultSet rs ){
      try{
		this.setIdCliente(rs.getInt("id_cliente"));
        this.setIdConvenio(rs.getString("id_convenio"));
        this.setTitulo(rs.getString("titulo"));
        this.setIdOftalmologo(rs.getInt("id_oftalmologo"));
        this.setTipoOft(rs.getString("tipo_oft"));
        this.setIdLocalidad(rs.getString("id_localidad"));
        this.setIdEstado(rs.getString("id_estado"));
        this.setFechaAltaCli(rs.getDate("fecha_alta_cli"));
        this.setSexoCli(Utilities.toBoolean(rs.getBoolean("sexo_cli")));
        this.setApellidoPatCli(rs.getString("apellido_pat_cli"));
        this.setApellidoMatCli(rs.getString("apellido_mat_cli"));
        this.setfCasadaCli(Utilities.toBoolean(rs.getBoolean("f_casada_cli")));
        this.setNombreCli(rs.getString("nombre_cli"));
        this.setRfcCli(rs.getString("rfc_cli"));
        this.setDireccionCli(rs.getString("direccion_cli"));
        this.setColoniaCli(rs.getString("colonia_cli"));
        this.setCodigo(rs.getString("codigo"));
        this.setTelCasaCli(rs.getString("tel_casa_cli"));
        this.setTelTrabCli(rs.getString("tel_trab_cli"));
        this.setExtTrabCli(rs.getString("ext_trab_cli"));
        this.setTelAdiCli(rs.getString("tel_adi_cli"));
        this.setExtAdiCli(rs.getString("ext_adi_cli"));
        this.setEmailCli(rs.getString("email_cli"));
        this.setsUsaAnteojos(rs.getString("s_usa_anteojos"));
        this.setAvisar(Utilities.toBoolean(rs.getBoolean("avisar")));
        this.setIdAtendio(rs.getString("id_atendio"));
        this.setUdf1(rs.getString("udf1"));
        this.setUdf2(rs.getString("udf2"));
        this.setCliOri(rs.getString("cli_ori"));
        this.setUdf4(rs.getString("udf4"));
        this.setUdf5(rs.getString("udf5"));
        this.setUdf6(rs.getString("udf6"));
        this.setReceta(rs.getInt("receta"));
        this.setObs(rs.getString("obs"));
        this.setFechaNac(rs.getDate("fecha_nac"));
        this.setCuc(rs.getString("cuc"));
        this.setHoraAlta(rs.getDate("hora_alta"));
        this.setFinado(Utilities.toBoolean(rs.getBoolean("finado")));
        this.setFechaImp(rs.getDate("fecha_imp"));
        this.setPrincipal(rs.getInt("principal"));
        this.setMunicipio( municipioJava() );
        this.setClientePais( clientaPaisJava() );
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return this;
	}


    public MunicipioJava municipioJava( ){
      MunicipioJava municipioJava = new MunicipioJava();
      municipioJava = MunicipioQuery.BuscaMunicipioPorEstadoYLocalidad(idEstado, idLocalidad);
      return municipioJava;
    }

    public ClientePaisJava clientaPaisJava( ){
      ClientePaisJava clientePaisJava = new ClientePaisJava();
      clientePaisJava = ClientePaisQuery.BuscaClientePaisPoridCliente( idCliente );
      return clientePaisJava;
    }


}
