package mx.lux.pos.service.impl

import groovy.util.logging.Slf4j
import mx.lux.pos.model.Examen
import mx.lux.pos.model.QExamen
import mx.lux.pos.repository.ExamenRepository
import mx.lux.pos.service.ExamenService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

@Slf4j
@Service( "examenService" )
@Transactional( readOnly = true )
class ExamenServiceImpl implements ExamenService {

  @Resource
  private ExamenRepository examenRepository


  @Override
  @Transactional
  Examen guardarExamen( Examen examen ) {
    log.info( "guardando examen" )
      examenRepository.save( examen )
  }

  @Override
  @Transactional
  Examen actualizarExamen( Examen examen ){
    log.info( "guardando examen" )
    Examen exam = examenRepository.findOne( examen.id )
    exam.fechaMod = new Date()
    examenRepository.save( exam )
  }

  @Override
  Examen obtenerExamenPorIdCliente( Integer idCliente ) {
      log.info( "obtenerExamenPorIdCliente" )
      Examen examen = null
      QExamen ex = QExamen.examen
      List<Examen> lstExamenes = examenRepository.findAll( ex.idCliente.eq(idCliente), ex.fechaAlta.asc() )
      if( lstExamenes.size() > 0 ){
        for(Examen exam : lstExamenes){
          if(!exam.tipoOft.equalsIgnoreCase("SE")){
            examen = exam
          }
        }
      }
    return examen
  }
}
