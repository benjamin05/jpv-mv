package mx.lux.pos.repository.impl

import com.mysema.query.jpa.JPQLQuery
import com.mysema.query.types.Predicate
import mx.lux.pos.model.QTmpServicios
import mx.lux.pos.model.TmpServicios
import mx.lux.pos.repository.custom.TmpServiciosRepositoryCustom
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport

class TmpServiciosRepositoryImpl extends QueryDslRepositorySupport implements TmpServiciosRepositoryCustom {


    @Override
    TmpServicios findbyIdFactura(String idFactura) {
        QTmpServicios tmpServicios = QTmpServicios.tmpServicios
        def predicates = [ ]
        predicates.add( tmpServicios.id_factura.eq( idFactura ) )
        JPQLQuery query = from( tmpServicios )
        query.where( predicates as Predicate[] )
       return query.singleResult( tmpServicios )

    }
}
