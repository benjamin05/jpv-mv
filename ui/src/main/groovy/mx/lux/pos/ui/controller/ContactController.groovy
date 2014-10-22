package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.model.Cliente
import mx.lux.pos.model.FormaContacto
import mx.lux.pos.model.Jb
import mx.lux.pos.model.TipoContacto
import mx.lux.pos.repository.TipoContactoRepository
import mx.lux.pos.service.ClienteService
import mx.lux.pos.service.JbService
import mx.lux.pos.service.impl.FormaContactoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Slf4j
@Component
class ContactController {

    private static JbService jbService
    private static FormaContactoService formaContactoService
    private static TipoContactoRepository tipoContactoRepository
    private static ClienteService clienteService

    @Autowired
    ContactController(JbService jbService, FormaContactoService formaContactoService, TipoContactoRepository tipoContactoRepository, ClienteService clienteService) {
        this.jbService = jbService
        this.formaContactoService = formaContactoService
        this.tipoContactoRepository = tipoContactoRepository
        this.clienteService = clienteService

    }
    private static final Integer TAG_ID_TELEFONO = 4
    private static final Integer TAG_ID_SMS = 3
    private static final Integer TAG_ID_CORREO = 1

    static Jb findJbxRX(String rx) {

        return jbService.findJBbyRx(rx)
    }

    static FormaContacto findFCbyRx(String rx) {
        return formaContactoService.findFCbyRx(rx)
    }

    static List<FormaContacto> findCustomerContact(Integer idCliente) {
        List<FormaContacto> contactos = new ArrayList<FormaContacto>()

        Cliente cliente = clienteService.obtenerCliente(idCliente)
        FormaContacto formaContacto = new FormaContacto()
        if (cliente.email != '') {
            formaContacto = new FormaContacto()
            formaContacto?.contacto = cliente.email
            if(TAG_ID_CORREO == cliente.principal){
              //formaContacto?.principal = true
            } else {
                //formaContacto?.principal = false
            }
            TipoContacto tipoContacto = new TipoContacto()
            tipoContacto?.id_tipo_contacto = 1
            tipoContacto?.descripcion = 'Correo'

            formaContacto?.tipoContacto = tipoContacto
            contactos.add(formaContacto)
        }
        if (cliente.telefonoCasa != '') {
            formaContacto = new FormaContacto()
            formaContacto?.contacto = cliente.telefonoCasa
            if(TAG_ID_TELEFONO == cliente.principal){
              //formaContacto?.principal = true
            } else {
              //formaContacto?.principal = false
            }
            TipoContacto tipoContacto = new TipoContacto()
            tipoContacto?.id_tipo_contacto = 3
            tipoContacto?.descripcion = 'Telefono'
            formaContacto?.tipoContacto = tipoContacto
            contactos.add(formaContacto)
        }
        if (cliente.telefonoTrabajo != '') {
            formaContacto = new FormaContacto()
            formaContacto?.contacto = cliente.telefonoTrabajo
            TipoContacto tipoContacto = new TipoContacto()
            tipoContacto?.id_tipo_contacto = 2
            tipoContacto?.descripcion = 'Recados'
            formaContacto?.tipoContacto = tipoContacto
            contactos.add(formaContacto)
        }
        if (cliente.telefonoAdicional != '') {
            formaContacto = new FormaContacto()
            formaContacto?.contacto = cliente.telefonoAdicional
            if(TAG_ID_SMS == cliente.principal){
                //formaContacto?.principal = true
            } else {
                //formaContacto?.principal = false
            }
            TipoContacto tipoContacto = new TipoContacto()
            tipoContacto?.id_tipo_contacto = 4
            tipoContacto?.descripcion = 'SMS'
            formaContacto?.tipoContacto = tipoContacto
            contactos.add(formaContacto)
        }
        return contactos
    }

    static List<FormaContacto> findByIdCliente(Integer idCliente) {
        List<FormaContacto> formaContactos = formaContactoService.findByidCliente(idCliente)
        List<FormaContacto> contactos = new ArrayList<FormaContacto>()
        Iterator iterator = formaContactos.iterator();
        while (iterator.hasNext()) {
            FormaContacto formaContacto = iterator.next()
            formaContacto?.tipoContacto = tipoContactoRepository.findOne(formaContacto?.id_tipo_contacto)
            contactos.add(formaContacto)
        }
       Cliente cliente = clienteService.obtenerCliente(idCliente)
       if( cliente?.email != null ){
           if (!cliente?.email.trim().equals('') ){
           FormaContacto formaContacto = new FormaContacto()
           formaContacto?.contacto = cliente?.email
           formaContacto?.tipoContacto = tipoContactoRepository.findOne(1)
           contactos.add(formaContacto)
       }
       }
       if( cliente?.telefonoCasa != null ){
           if( !cliente?.telefonoCasa.trim().equals('') ){
           FormaContacto formaContacto = new FormaContacto()
           formaContacto?.contacto = cliente?.telefonoCasa
           formaContacto?.tipoContacto = tipoContactoRepository.findOne(4)
           contactos.add(formaContacto)
           }
       }
       if( cliente?.telefonoTrabajo != null ){
           if( !cliente?.telefonoTrabajo.trim().equals('') ){
           FormaContacto formaContacto = new FormaContacto()
           formaContacto?.contacto = cliente?.telefonoTrabajo
           formaContacto?.tipoContacto = tipoContactoRepository.findOne(2)
           contactos.add(formaContacto)
       }
       }
       if( cliente?.telefonoAdicional != null  ){
           if( !cliente?.telefonoAdicional.trim().equals('')  ){
           FormaContacto formaContacto = new FormaContacto()
           formaContacto?.contacto = cliente?.telefonoAdicional
           formaContacto?.tipoContacto = tipoContactoRepository.findOne(3)
           contactos.add(formaContacto)
           }
       }
       return contactos
    }

    static FormaContacto saveFormaContacto(FormaContacto formaContacto) {
        formaContacto = formaContactoService.saveFC(formaContacto)
        return formaContacto
    }


}
