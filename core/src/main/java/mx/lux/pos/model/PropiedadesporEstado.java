package mx.lux.pos.model;

import java.math.BigDecimal;
import java.util.Date;

public class PropiedadesporEstado {
	
	private String id;
	private String factura;
	private Date fecha;
	private String tipo;
	private Integer contactos;
	private BigDecimal saldo;
	private String material;
    private String empleadoEntrego;
	
	public PropiedadesporEstado( String pId ){
		id = pId;
		contactos = 0;
		saldo = BigDecimal.ZERO;
	}

	public void AcumulaPropiedades( Trabajo trabajo, NotaVenta notaVenta ){
        material = "";
		fecha = trabajo.getFechaVenta();
		tipo = trabajo.getJbTipo();
		contactos = trabajo.getNumLlamada();
		saldo = trabajo.getSaldo();
        if( notaVenta != null ){
          for(DetalleNotaVenta det : notaVenta.getDetalles()){
              String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
              material = material+","+det.getArticulo().getArticulo()+color;
          }
          material = material.replaceFirst( ",","" );
        } else {
          material = trabajo.getMaterial();
        }

	}
	
	public void AcumulaTrabajos( TrabajoTrack trabajo, NotaVenta notaVenta ){
        if(notaVenta != null){
          empleadoEntrego = notaVenta.getEmpEntrego().trim()+" "+notaVenta.getEmpleadoEntrego().getNombreCompleto().trim();
        } else {
          if( trabajo.getEmpleado() != null ){
            empleadoEntrego = trabajo.getEmp().trim()+" "+trabajo.getEmpleado().getNombreCompleto().trim();
          } else {
            empleadoEntrego = trabajo.getEmp().trim();
          }
        }
        material = "";
		fecha = trabajo.getFecha();
		factura = trabajo.getId();
        if( notaVenta != null ){
            for(DetalleNotaVenta det : notaVenta.getDetalles()){
                String color = (det.getArticulo().getCodigoColor() != null && det.getArticulo().getCodigoColor().trim().length() > 0) ? "["+det.getArticulo().getCodigoColor().trim()+"]" : "";
                material = material+","+det.getArticulo().getArticulo()+color;
            }
            material = material.replaceFirst(",","");
        } else {
          material = trabajo.getTrabajo().getMaterial();
        }
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Integer getContactos() {
		return contactos;
	}

	public void setContactos(Integer contactos) {
		this.contactos = contactos;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getFactura() {
		return factura;
	}

	public void setFactura(String factura) {
		this.factura = factura;
	}

    public String getEmpleadoEntrego() {
        return empleadoEntrego;
    }

    public void setEmpleadoEntrego(String empleadoEntrego) {
        this.empleadoEntrego = empleadoEntrego;
    }
}
