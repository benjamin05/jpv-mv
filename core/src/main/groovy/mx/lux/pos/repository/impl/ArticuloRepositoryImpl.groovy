package mx.lux.pos.repository.impl

import com.mysema.query.jpa.JPQLQuery
import com.mysema.query.types.Predicate
import mx.lux.pos.model.Articulo
import mx.lux.pos.model.QArticulo
import mx.lux.pos.repository.custom.ArticuloRepositoryCustom
import org.apache.commons.lang3.StringUtils
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport

class ArticuloRepositoryImpl extends QueryDslRepositorySupport implements ArticuloRepositoryCustom {


    @Override
    Articulo findbyName(String articulo) {
        QArticulo art = QArticulo.articulo1
        def predicates = [ ]
        if ( StringUtils.isNotBlank( articulo ) ) {
            predicates.add( art.articulo.startsWithIgnoreCase(articulo))
        }
        JPQLQuery query = from( art )
        query.where( predicates as Predicate[] )


        return query.singleResult( art )
    }

    @Override
    Articulo findbyId(Integer idArticulo) {
        QArticulo art = QArticulo.articulo1
        def predicates = [ ]
        if ( idArticulo != null ) {
            predicates.add( art.id.eq(idArticulo))
        }
        JPQLQuery query = from( art )
        query.where( predicates as Predicate[] )


        return query.singleResult( art )
    }
}

