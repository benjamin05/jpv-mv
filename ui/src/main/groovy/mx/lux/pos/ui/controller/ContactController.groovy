package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.java.querys.TipoContactoQuery
import mx.lux.pos.java.repository.ClientesJava
import mx.lux.pos.java.repository.FormaContactoJava
import mx.lux.pos.java.service.ClienteServiceJava
import mx.lux.pos.java.service.FormaContactoServiceJava
import mx.lux.pos.model.Cliente
import mx.lux.pos.model.FormaContacto
import mx.lux.pos.model.Jb
import mx.lux.pos.model.TipoContacto
import mx.lux.pos.repository.TipoContactoRepository
import mx.lux.pos.service.ClienteService
import mx.lux.pos.service.JbService
import mx.lux.pos.service.impl.FormaContactoService
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Slf4j
@Component
class ContactController {

    private static JbService jbService
    private static FormaContactoService formaContactoService
    private static FormaContactoServiceJava formaContactoServiceJava
    private static TipoContactoRepository tipoContactoRepository
    private static ClienteService clienteService
    private static ClienteServiceJava clienteServiceJava

    @Autowired
    ContactController(JbService jbService, FormaContactoService formaContactoService, TipoContactoRepository tipoContactoRepository, ClienteService clienteService) {
        this.jbService = jbService
        this.formaContactoService = formaContactoService
        this.tipoContactoRepository = tipoContactoRepository
        this.clienteService = clienteService
        formaContactoServiceJava = new FormaContactoServiceJava()
        clienteServiceJava = new ClienteServiceJava()

    }
    private static final Integer TAG_ID_TELEFONO = 4
    private static final Integer TAG_ID_SMS = 3
    private static final Integer TAG_ID_CORREO = 1

    static Jb findJbxRX(String rx) {

        return jbService.findJBbyRx(rx)
    }

    static FormaContactoJava findFCbyRx(String rx) {
        return formaContactoServiceJava.findFormaContactobyRx(rx)
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

    static List<FormaContactoJava> findByIdCliente(Integer idCliente) {
      List<FormaContactoJava> formaContactos = formaContactoServiceJava.findByidCliente(idCliente)
      List<FormaContactoJava> contactos = new ArrayList<FormaContactoJava>()
      Iterator iterator = formaContactos.iterator();
      while (iterator.hasNext()) {
        FormaContactoJava formaContacto = iterator.next()
        formaContacto?.tipoContacto = TipoContactoQuery.buscaTipoContactoPorIdTipoContacto(formaContacto?.idTipoContacto)
        contactos.add(formaContacto)
      }
      ClientesJava cliente = clienteServiceJava.obtenerCliente(idCliente)
      if( cliente?.emailCli != null ){
        if (!StringUtils.trimToEmpty(cliente?.emailCli).equals('') ){
          FormaContactoJava formaContacto = new FormaContactoJava()
          formaContacto?.contacto = cliente?.emailCli
          formaContacto?.tipoContacto = TipoContactoQuery.buscaTipoContactoPorIdTipoContacto(1)
          contactos.add(formaContacto)
        }
      }
      if( cliente?.telCasaCli != null ){
        if( !StringUtils.trimToEmpty(cliente?.telCasaCli).equals('') ){
          FormaContactoJava formaContacto = new FormaContactoJava()
          formaContacto?.contacto = cliente?.telCasaCli
          formaContacto?.tipoContacto = TipoContactoQuery.buscaTipoContactoPorIdTipoContacto(4)
          contactos.add(formaContacto)
        }
      }
      if( cliente?.telTrabCli != null ){
        if( !StringUtils.trimToEmpty(cliente?.telTrabCli).equals('') ){
          FormaContactoJava formaContacto = new FormaContactoJava()
          formaContacto?.contacto = cliente?.telTrabCli
          formaContacto?.tipoContacto = TipoContactoQuery.buscaTipoContactoPorIdTipoContacto(2)
          contactos.add(formaContacto)
        }
      }
      if( cliente?.telAdiCli != null  ){
        if( !StringUtils.trimToEmpty(cliente?.telAdiCli).equals('')  ){
          FormaContactoJava formaContacto = new FormaContactoJava()
          formaContacto?.contacto = cliente?.telAdiCli
          formaContacto?.tipoContacto = TipoContactoQuery.buscaTipoContactoPorIdTipoContacto(3)
          contactos.add(formaContacto)
        }
      }
      return contactos
    }

    static FormaContactoJava saveFormaContacto(FormaContactoJava formaContacto) {
      formaContacto = formaContactoServiceJava.saveFC(formaContacto)
      return formaContacto
    }


}
