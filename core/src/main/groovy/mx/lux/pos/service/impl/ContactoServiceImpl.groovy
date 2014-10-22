package mx.lux.pos.service.impl

import groovy.util.logging.Slf4j
import mx.lux.pos.model.TipoContacto
import mx.lux.pos.repository.TipoContactoRepository
import mx.lux.pos.service.ContactoService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

@Slf4j
@Service( 'contactoService' )
@Transactional( readOnly = true )
class ContactoServiceImpl implements ContactoService{

  @Resource
  private TipoContactoRepository tipoContactoRepository

    @Override
    List<TipoContacto> obtenerTiposContacto() {
       List<TipoContacto> contactos = tipoContactoRepository.findAll()

     return contactos
    }
}
