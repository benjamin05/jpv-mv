package mx.lux.pos.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table( name = "receta", schema = "public" )
public class    Receta implements Serializable {

    private static final long serialVersionUID = -3539764397838488807L;

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO, generator = "receta_seq" )
    @SequenceGenerator( name = "receta_seq", sequenceName = "receta_seq" )
    @Column( name = "id_receta" )
    private Integer id;

    @Column( name = "examen" )
    private Integer examen;

    @Column( name = "id_cliente" )
    private Integer idCliente;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_receta" )
    private Date fechaReceta;

    @Column( name = "s_uso_anteojos", length = 1 )
    private String sUsoAnteojos;

    @Column( name = "id_optometrista", length = 13 )
    private String idOptometrista;

    @Column( name = "tipo_opt" )
    private String tipoOpt;

    @Column( name = "od_esf_r", length = 6 )
    private String odEsfR;

    @Column( name = "od_cil_r", length = 6 )
    private String odCilR;

    @Column( name = "od_eje_r", length = 3 )
    private String odEjeR;

    @Column( name = "od_adc_r", length = 6 )
    private String odAdcR;

    @Column( name = "od_adi_r", length = 6 )
    private String odAdiR;

    @Column( name = "od_prisma_h" )
    private String odPrismaH;

    @Column( name = "oi_esf_r", length = 6 )
    private String oiEsfR;

    @Column( name = "oi_cil_r", length = 6 )
    private String oiCilR;

    @Column( name = "oi_eje_r", length = 3 )
    private String oiEjeR;

    @Column( name = "oi_adc_r", length = 6 )
    private String oiAdcR;

    @Column( name = "oi_adi_r", length = 6 )
    private String oiAdiR;

    @Column( name = "oi_prisma_h" )
    private String oiPrismaH;

    @Column( name = "di_lejos_r" )
    private String diLejosR;

    @Column( name = "di_cerca_r", length = 2 )
    private String diCercaR;

    @Column( name = "od_av_r", length = 3 )
    private String odAvR;

    @Column( name = "oi_av_r", length = 3 )
    private String oiAvR;

    @Column( name = "alt_obl_r", length = 4 )
    private String altOblR;

    @Column( name = "observaciones_r" )
    private String observacionesR;

    @Column( name = "f_impresa" )
    private Boolean fImpresa;

    @Column( name = "id_sync", length = 1 )
    private String idSync;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_mod" )
    private Date fechaMod;

    @Column( name = "id_mod", length = 13 )
    private String idMod;

    @Column( name = "id_sucursal" )
    private Integer idSucursal;

    @Column( name = "di_od", length = 6 )
    private String diOd;

    @Column( name = "di_oi", length = 6 )
    private String diOi;

    @Column( name = "material_arm" )
    private String material_arm;

    @Column( name = "od_prisma_v" )
    private String odPrismaV;

    @Column( name = "oi_prisma_v" )
    private String oiPrismaV;

    @Column( name = "tratamientos" )
    private String tratamientos;

    @Column( name = "udf5" )
    private String udf5;

    @Column( name = "udf6" )
    private String udf6;

    @Column( name = "id_rx_ori" )
    private String idRxOri;

    @Column( name = "folio" )
    private String folio;


    @ManyToOne
    @NotFound( action = NotFoundAction.IGNORE )
    @JoinColumn( name = "id_cliente", insertable = false, updatable = false )
    private Cliente cliente;

    @ManyToOne
    @NotFound( action = NotFoundAction.IGNORE )
    @JoinColumn( name = "id_optometrista", insertable = false, updatable = false )
    private Empleado empleado;

    @ManyToOne
    @NotFound( action = NotFoundAction.IGNORE )
    @JoinColumn( name = "id_receta", referencedColumnName = "receta", insertable = false, updatable = false )
    private NotaVenta notaVenta;


    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public Integer getExamen() {
        return examen;
    }

    public void setExamen( Integer examen ) {
        this.examen = examen;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente( Integer idCliente ) {
        this.idCliente = idCliente;
    }

    public Date getFechaReceta() {
        return fechaReceta;
    }

    public void setFechaReceta( Date fechaReceta ) {
        this.fechaReceta = fechaReceta;
    }

    public String getsUsoAnteojos() {
        return sUsoAnteojos;
    }

    public void setsUsoAnteojos( String sUsoAnteojos ) {
        this.sUsoAnteojos = sUsoAnteojos;
    }

    public String getIdOptometrista() {
        return idOptometrista;
    }

    public void setIdOptometrista( String idOptometrista ) {
        this.idOptometrista = idOptometrista;
    }

    public String getTipoOpt() {
        return tipoOpt;
    }

    public void setTipoOpt( String tipoOpt ) {
        this.tipoOpt = tipoOpt;
    }

    public String getOdEsfR() {
        return odEsfR;
    }

    public void setOdEsfR( String odEsfR ) {
        this.odEsfR = odEsfR;
    }

    public String getOdCilR() {
        return odCilR;
    }

    public void setOdCilR( String odCilR ) {
        this.odCilR = odCilR;
    }

    public String getOdEjeR() {
        return odEjeR;
    }

    public void setOdEjeR( String odEjeR ) {
        this.odEjeR = odEjeR;
    }

    public String getOdAdcR() {
        return odAdcR;
    }

    public void setOdAdcR( String odAdcR ) {
        this.odAdcR = odAdcR;
    }

    public String getOdAdiR() {
        return odAdiR;
    }

    public void setOdAdiR( String odAdiR ) {
        this.odAdiR = odAdiR;
    }

    public String getOdPrismaH() {
        return odPrismaH;
    }

    public void setOdPrismaH( String odPrismaH ) {
        this.odPrismaH = odPrismaH;
    }

    public String getOiEsfR() {
        return oiEsfR;
    }

    public void setOiEsfR( String oiEsfR ) {
        this.oiEsfR = oiEsfR;
    }

    public String getOiCilR() {
        return oiCilR;
    }

    public void setOiCilR( String oiCilR ) {
        this.oiCilR = oiCilR;
    }

    public String getOiEjeR() {
        return oiEjeR;
    }

    public void setOiEjeR( String oiEjeR ) {
        this.oiEjeR = oiEjeR;
    }

    public String getOiAdcR() {
        return oiAdcR;
    }

    public void setOiAdcR( String oiAdcR ) {
        this.oiAdcR = oiAdcR;
    }

    public String getOiAdiR() {
        return oiAdiR;
    }

    public void setOiAdiR( String oiAdiR ) {
        this.oiAdiR = oiAdiR;
    }

    public String getOiPrismaH() {
        return oiPrismaH;
    }

    public void setOiPrismaH( String oiPrismaH ) {
        this.oiPrismaH = oiPrismaH;
    }

    public String getDiLejosR() {
        return diLejosR;
    }

    public void setDiLejosR( String diLejosR ) {
        this.diLejosR = diLejosR;
    }

    public String getDiCercaR() {
        return diCercaR;
    }

    public void setDiCercaR( String diCercaR ) {
        this.diCercaR = diCercaR;
    }

    public String getOdAvR() {
        return odAvR;
    }

    public void setOdAvR( String odAvR ) {
        this.odAvR = odAvR;
    }

    public String getOiAvR() {
        return oiAvR;
    }

    public void setOiAvR( String oiAvR ) {
        this.oiAvR = oiAvR;
    }

    public String getAltOblR() {
        return altOblR;
    }

    public void setAltOblR( String altOblR ) {
        this.altOblR = altOblR;
    }

    public String getObservacionesR() {
        return observacionesR;
    }

    public void setObservacionesR( String observacionesR ) {
        this.observacionesR = observacionesR;
    }

    public Boolean getfImpresa() {
        return fImpresa;
    }

    public void setfImpresa( Boolean fImpresa ) {
        this.fImpresa = fImpresa;
    }

    public String getIdSync() {
        return idSync;
    }

    public void setIdSync( String idSync ) {
        this.idSync = idSync;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod( Date fechaMod ) {
        this.fechaMod = fechaMod;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod( String idMod ) {
        this.idMod = idMod;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal( Integer idSucursal ) {
        this.idSucursal = idSucursal;
    }

    public String getDiOd() {
        return diOd;
    }

    public void setDiOd( String diOd ) {
        this.diOd = diOd;
    }

    public String getDiOi() {
        return diOi;
    }

    public void setDiOi( String diOi ) {
        this.diOi = diOi;
    }

    public String getMaterial_arm() {
        return material_arm;
    }

    public void setMaterial_arm( String material_arm ) {
        this.material_arm = material_arm;
    }

    public String getOdPrismaV() {
        return odPrismaV;
    }

    public void setOdPrismaV( String odPrismaV ) {
        this.odPrismaV = odPrismaV;
    }

    public String getOiPrismaV() {
        return oiPrismaV;
    }

    public void setOiPrismaV( String oiPrismaV ) {
        this.oiPrismaV = oiPrismaV;
    }

    public String getTratamientos() {
        return tratamientos;
    }

    public void setTratamientos( String tratamientos ) {
        this.tratamientos = tratamientos;
    }

    public String getUdf5() {
        return udf5;
    }

    public void setUdf5( String udf5 ) {
        this.udf5 = udf5;
    }

    public String getUdf6() {
        return udf6;
    }

    public void setUdf6( String udf6 ) {
        this.udf6 = udf6;
    }

    public String getIdRxOri() {
        return idRxOri;
    }

    public void setIdRxOri( String idRxOri ) {
        this.idRxOri = idRxOri;
    }


    public String getFolio() {
        return folio;
    }

    public void setFolio( String folio ) {
        this.folio = folio;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente( Cliente cliente ) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado( Empleado empleado ) {
        this.empleado = empleado;
    }

    public NotaVenta getNotaVenta() {
        return notaVenta;
    }

    public void setNotaVenta(NotaVenta notaVenta) {
        this.notaVenta = notaVenta;
    }
}
