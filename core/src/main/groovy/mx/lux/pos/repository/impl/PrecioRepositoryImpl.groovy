package mx.lux.pos.repository.impl

import com.mysema.query.jpa.JPQLQuery
import com.mysema.query.types.Predicate
import mx.lux.pos.model.Precio
import mx.lux.pos.model.QPrecio
import mx.lux.pos.repository.custom.PrecioRepositoryCustom
import org.apache.commons.lang3.StringUtils
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport

class PrecioRepositoryImpl extends QueryDslRepositorySupport implements PrecioRepositoryCustom{

    @Override
    Precio findbyArt(String articulo) {
        QPrecio precio = QPrecio.precio1
        def predicates = [ ]
        if ( StringUtils.isNotBlank( articulo ) ) {
            predicates.add( precio.articulo.eq(articulo))
        }
        JPQLQuery query = from (precio)
        query.where( predicates as Predicate[] )
        return query.singleResult( precio )

    }

}

