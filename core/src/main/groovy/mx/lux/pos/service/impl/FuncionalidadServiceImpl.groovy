package mx.lux.pos.service.impl

import groovy.util.logging.Slf4j
import mx.lux.pos.model.Feature
import mx.lux.pos.model.Funcionalidad
import mx.lux.pos.repository.FuncionalidadRepository
import mx.lux.pos.service.FuncionalidadService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

@Slf4j
@Service( "funcionalidadService" )
@Transactional( readOnly = true )
class FuncionalidadServiceImpl implements FuncionalidadService {

  @Resource
  private FuncionalidadRepository funcionalidadRepository

  @Transactional
  private Funcionalidad createFeature( Feature pFeature ) {
    this.log.debug( String.format( 'Creating Feature %s', pFeature.toString() ))
    Funcionalidad f = null
    try {
      f = new Funcionalidad()
      f.id = pFeature.featureId
      f.activo = pFeature.activeOnDefault
      funcionalidadRepository.saveAndFlush( f )
    } catch (Exception e) {
      this.log.error( e.getMessage() )
    }
    return f
  }

  private Funcionalidad findOrCreateFeature( Feature pFeature ) {
    Funcionalidad f = funcionalidadRepository.findOne( Feature.RECETAS.featureId )
    if ( f == null ) {
      f = this.createFeature( pFeature )
    }
    return f
  }

  boolean isRxEnabled( ) {
    return this.findOrCreateFeature( Feature.RECETAS ).activo
  }
}
