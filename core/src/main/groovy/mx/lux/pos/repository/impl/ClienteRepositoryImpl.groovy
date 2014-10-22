package mx.lux.pos.repository.impl

import groovy.util.logging.Slf4j
import com.mysema.query.jpa.JPQLQuery
import com.mysema.query.types.Predicate
import mx.lux.pos.model.Cliente
import mx.lux.pos.model.QCliente
import mx.lux.pos.repository.custom.ClienteRepositoryCustom
import org.apache.commons.lang.time.DateUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport

class ClienteRepositoryImpl extends QueryDslRepositorySupport implements ClienteRepositoryCustom {

  @Override
  List<Cliente> findByNombreApellidos( String nombre, String apellidoPaterno, String apellidoMaterno ) {
    QCliente cliente = QCliente.cliente
    def predicates = [ ]
    if ( StringUtils.isNotBlank( nombre ) ) {
      predicates.add( cliente.nombre.startsWithIgnoreCase( nombre ) )
    }
    if ( StringUtils.isNotBlank( apellidoPaterno ) ) {
      predicates.add( cliente.apellidoPaterno.startsWithIgnoreCase( apellidoPaterno ) )
    }
    if ( StringUtils.isNotBlank( apellidoMaterno ) ) {
      predicates.add( cliente.apellidoMaterno.startsWithIgnoreCase( apellidoMaterno ) )
    }
    JPQLQuery query = from( cliente )
    query.where( predicates as Predicate[] )
    return query.list( cliente )
  }
  
  @Override
  List<Cliente> findByFechaAlta( Date fecha ) {
	if ( fecha != null ) {
	  QCliente cliente = QCliente.cliente
	  def predicates = [ ]
	  Date fechaInicio = fecha
	  Date fechaFin = DateUtils.addDays( fecha, 1 )
	  predicates.add( cliente.fechaAlta.between( fechaInicio, fechaFin ) )
	  JPQLQuery query = from( cliente )
	  query.where( predicates as Predicate[] )
	  return query.list( cliente )
	}
	[ ]
  }

    @Override
    List<Cliente> findByStartApellidoPaterno(String apellido ) {

        if ( apellido != null ) {
            QCliente cliente = QCliente.cliente
            def predicates = []
            predicates.add( cliente.apellidoPaterno.startsWithIgnoreCase( apellido ) )
            JPQLQuery query = from ( cliente )
            query.where( predicates as Predicate [] )
            return query.list( cliente )
        }
        []
    }

    @Override
    List<Cliente> findByFechaNacimiento(Date fecha) {

        if ( fecha != null ) {
            QCliente cliente = QCliente.cliente
            def predicates = [ ]
            Date fechaInicio = fecha
            predicates.add( cliente.fechaNacimiento.between( fechaInicio, fechaInicio ) )
            JPQLQuery query = from ( cliente )
            query.where( predicates as Predicate[] )
            return query.list( cliente )
        }
        [ ]
    }

    @Override
    List<Cliente> findByStartApellidoPaternoAndFechaNacimiento(String apellido, Date fecha ) {

        if ( apellido != null && fecha != null ) {
            QCliente cliente = QCliente.cliente
            def predicates = []
            Date fechaNac = fecha

            predicates.add( cliente.apellidoPaterno.startsWithIgnoreCase( apellido ) )
            predicates.add( cliente.fechaNacimiento.between(fechaNac, fechaNac) )

            JPQLQuery query = from( cliente )
            query.where( predicates as Predicate[] )

            return query.list( cliente )
        }
        []
    }

}
