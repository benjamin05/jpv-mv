package mx.lux.pos.service.impl

import com.mysema.query.jpa.JPQLQuery
import com.mysema.query.types.Predicate
import groovy.util.logging.Slf4j
import mx.lux.pos.model.FormaContacto
import mx.lux.pos.model.Jb
import mx.lux.pos.model.QFormaContacto
import mx.lux.pos.repository.FormaContactoRepository
import org.apache.commons.lang.StringUtils
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

@Slf4j
@Service( 'FormaContactoService' )
@Transactional( readOnly = true )
class FormaContactoServiceImpl extends QueryDslRepositorySupport  implements FormaContactoService {

  @Resource
  private FormaContactoRepository formaContactoRepository


    @Override
    FormaContacto findFCbyRx(String rx) {
        return formaContactoRepository.findOne(rx)
    }

    @Override
    FormaContacto saveFC(FormaContacto formaContacto) {

        formaContacto = formaContactoRepository.saveAndFlush(formaContacto)

        return formaContacto
    }



    @Override
    List<FormaContacto> findByidCliente( Integer idCliente ) {
        QFormaContacto formaContacto = QFormaContacto.formaContacto
        def predicates = [ ]
        if (  idCliente != null ) {
            predicates.add( formaContacto.id_cliente.eq( idCliente ) )
        }
        JPQLQuery query = from( formaContacto )

        query.where( predicates as Predicate[] )
      //  query.groupBy(formaContacto?.contacto,formaContacto?.id_tipo_contacto)

        return query.list( formaContacto )

    }



    @Override
    FormaContacto saveFCFam (String idFactura, Jb jbFam, String idFactura2){
      FormaContacto fcFam = new FormaContacto()
      String factura = StringUtils.trimToEmpty( idFactura )
      String factura2 = StringUtils.trimToEmpty( idFactura2 )
      QFormaContacto fc = QFormaContacto.formaContacto
      List<FormaContacto> lstFormasContacto = formaContactoRepository.findAll( fc.rx.eq(factura), fc.fecha_mod.desc() )
      List<FormaContacto> lstFormasContacto2 = formaContactoRepository.findAll( fc.rx.eq(factura2), fc.fecha_mod.desc() )
      if( lstFormasContacto2.size() <= 0 && lstFormasContacto.size() > 0 ){
        FormaContacto formaContacto2 = new FormaContacto()
        formaContacto2.rx = factura2
        formaContacto2.id_cliente = lstFormasContacto.first().id_cliente
        formaContacto2.id_tipo_contacto = lstFormasContacto.first().id_tipo_contacto
        formaContacto2.contacto = lstFormasContacto.first().contacto
        formaContacto2.fecha_mod = new Date()
        formaContacto2.id_sucursal = lstFormasContacto.first().id_sucursal
        formaContactoRepository.saveAndFlush( formaContacto2 )
      }
      if( lstFormasContacto.size() > 0 ){
        fcFam.rx = jbFam.rx
        fcFam.id_cliente = lstFormasContacto.first().id_cliente
        fcFam.id_tipo_contacto = lstFormasContacto.first().id_tipo_contacto
        fcFam.contacto = lstFormasContacto.first().contacto
        fcFam.fecha_mod = new Date()
        fcFam.id_sucursal = lstFormasContacto.first().id_sucursal
        formaContactoRepository.save( fcFam )
        formaContactoRepository.flush()
      }
      return fcFam
    }

    @Override
    FormaContacto findByidClienteTipoContacto( Integer idCliente, Integer tipoContacto ) {
        FormaContacto fcFam = new FormaContacto()

        QFormaContacto fc = QFormaContacto.formaContacto
        List<FormaContacto> lstFormasContacto = formaContactoRepository.findAll( fc.id_cliente.eq(idCliente).and(fc.id_tipo_contacto.eq(tipoContacto)), fc.fecha_mod.asc() )

        FormaContacto formaContacto = null;

        for( FormaContacto f : lstFormasContacto ){
            formaContacto = f
        }

        if ( formaContacto != null )
            return formaContacto

        return null
    }
}
