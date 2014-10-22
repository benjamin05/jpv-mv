package mx.lux.pos.repository.impl

import com.mysema.query.jpa.JPQLQuery
import com.mysema.query.types.Predicate
import mx.lux.pos.model.NotaFactura
import mx.lux.pos.model.QNotaFactura
import mx.lux.pos.repository.custom.NotaFacturaRepositoryCustom
import org.apache.commons.lang.time.DateUtils
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport

class NotaFacturaRepositoryImpl extends QueryDslRepositorySupport implements NotaFacturaRepositoryCustom {
  
  @Override
  List<NotaFactura> findByFechaImpresion( Date fecha ) {
	if ( fecha != null ) {
	  QNotaFactura factura = QNotaFactura.notaFactura
	  def predicates = [ ]
	  Date fechaInicio = fecha
	  Date fechaFin = DateUtils.addDays( fecha, 1 )
	  predicates.add( factura.fechaImpresion.between( fechaInicio, fechaFin ) )
	  JPQLQuery query = from( factura )
	  query.where( predicates as Predicate[] )
	  return query.list( factura )
	}
	[ ]
  }
}
