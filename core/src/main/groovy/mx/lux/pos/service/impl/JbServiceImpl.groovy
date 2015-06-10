package mx.lux.pos.service.impl

import groovy.util.logging.Slf4j
import mx.lux.pos.model.Jb
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.repository.JbRepository
import mx.lux.pos.repository.JbTrackRepository
import mx.lux.pos.service.JbService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

@Slf4j
@Service( 'JbService' )
@Transactional( readOnly = true )
class JbServiceImpl implements JbService {

  @Resource
  private JbRepository jbRepository

  @Resource
  private JbTrackRepository jbTrackRepository

  private static final String TAG_JB_GRUPO = 'GRUPO'
    @Override
    Jb findJBbyRx(String rx) {
        return jbRepository.findOne(rx)
    }

    @Override
    @Transactional
    Jb saveJb( Jb pJb ){
      Jb jb = jbRepository.saveAndFlush( pJb )
      return jb
    }



    @Override
    @Transactional
    Jb saveJbFamilia( NotaVenta nota1, NotaVenta nota2 ){
      Jb jb1 = jbRepository.findOne(nota1.factura)
      Jb jb2 = jbRepository.findOne(nota2.factura)
      Jb jb = new Jb()
      if( jb1 != null && jb2 != null ){
          String estado = 'PN'
          Integer idJbGrupo = jbRepository.jbGroupSequence.intValue()
          jb.rx = 'F'+idJbGrupo
          jb.estado = estado
          jb.emp_atendio = jb1?.emp_atendio
          jb.num_llamada = 0
          jb.saldo = BigDecimal.ZERO
          jb.jb_tipo = TAG_JB_GRUPO
          jb.fecha_promesa = jb1?.fecha_promesa
          jb.cliente = nota1.cliente.nombreCompleto
          jb.id_mod = '0'
          jb.tipo_venta = TAG_JB_GRUPO
          jb.fecha_mod = new Date()
          jb = jbRepository.save(jb)

          jb1.id_grupo = jb.rx
          jbRepository.save(jb1)
          jbRepository.flush()
          jb2.id_grupo = jb.rx
          jbRepository.save(jb2)
          jbRepository.flush()

          /*JbTrack jbTrack = new JbTrack()
          jbTrack.rx = jb.rx
          jbTrack.estado = jb.estado
          jbTrack.obs = jb.cliente
          jbTrack.emp = jb.emp_atendio
          jbTrack.fecha = new Date()
          jbTrack.id_mod = '0'
          jbTrackRepository.save(jbTrack)*/
      }
        return jb
    }

}
