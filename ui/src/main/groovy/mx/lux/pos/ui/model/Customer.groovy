package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import mx.lux.pos.model.Cliente
import mx.lux.pos.model.Examen
import mx.lux.pos.model.FormaContacto
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.Titulo
import mx.lux.pos.java.repository.ClientesJava
import mx.lux.pos.ui.controller.CustomerController
import org.apache.commons.lang3.StringUtils

import java.text.SimpleDateFormat

@Slf4j
@Bindable
@ToString
@EqualsAndHashCode
class Customer {
    Integer id
    String name
    String fathersName
    String mothersName
    String title
    boolean legalEntity
    CustomerType type = CustomerType.DOMESTIC
    String rfc = CustomerType.DOMESTIC.rfc
    Date dob
    GenderType gender = GenderType.MALE
    Address address = new Address()
    List<Contact> contacts = []
    Integer age = EDAD_DEFAULT
    Date fechaNacimiento
    Date fechaUltimoExamen
    Date fechaUltimaVenta
    String celular

    private static final Integer EDAD_DEFAULT = 25

    String getFullName() {
        "${title ? "${StringUtils.trimToEmpty(title)} " : ''}${StringUtils.trimToEmpty(name) ?: ''} ${StringUtils.trimToEmpty(fathersName) ?: ''} ${StringUtils.trimToEmpty(mothersName) ?: ''}"
    }

    String getOnlyFullName() {
        "${name ?: ''} ${fathersName ?: ''} ${mothersName ?: ''}"
    }

    static Integer parse(String edad) {
        Integer age = EDAD_DEFAULT
        try {
            if (edad.length() > 0) {
                age = Integer.parseInt(edad)
            } else {
                age = null
            }
        } catch (Exception e) {
            log.error("Error en la edad", e)
        }
        return age
    }

    static Customer toCustomer(Cliente cliente) {

        if (cliente?.id) {
            Customer customer = new Customer(
                    id: cliente.id,
                    name: cliente.nombre,
                    fathersName: cliente.apellidoPaterno,
                    mothersName: cliente.apellidoMaterno,
                    title: cliente.titulo,
                    rfc: cliente.rfc,
                    dob: cliente.fechaNacimiento,
                    gender: GenderType.parse(cliente.sexo),
                    address: Address.toAddress(cliente),
                    age: parse(StringUtils.trimToEmpty(cliente.udf1)),
                    fechaNacimiento: cliente.fechaNacimiento

            )
            if (cliente.clientePais?.id) {
                customer.type = CustomerType.FOREIGN
                customer.rfc = CustomerType.FOREIGN.rfc
            }
            if (StringUtils.isNotBlank(cliente.telefonoCasa)) {
                Contact phone = new Contact(type: ContactType.HOME_PHONE)
                phone.setPhoneNumber(cliente.telefonoCasa)
                customer.contacts.add(phone)
            }
            if (StringUtils.isNotBlank(cliente.email)) {
                Contact mail = new Contact(type: ContactType.EMAIL)
                mail.setEmail(cliente.email)
                customer.contacts.add(mail)
            }

//            this.fechaNacimiento = this.fechaNacimiento.format('dd/MMM/yyyy')
            return customer
        }
        return null
    }


    static Customer toCustomer(ClientesJava cliente) {

        if (cliente?.idCliente) {
            Customer customer = new Customer(
                    id: cliente.idCliente,
                    name: cliente.nombreCli,
                    fathersName: cliente.apellidoPatCli,
                    mothersName: cliente.apellidoMatCli,
                    title: cliente.titulo,
                    rfc: cliente.rfcCli,
                    dob: cliente.fechaNac,
                    gender: GenderType.parse(cliente.sexoCli),
                    address: Address.toAddress(cliente),
                    age: parse(StringUtils.trimToEmpty(cliente.udf1)),
                    fechaNacimiento: cliente.fechaNac

            )
            if (cliente.clientePais?.idCliente) {
                customer.type = CustomerType.FOREIGN
                customer.rfc = CustomerType.FOREIGN.rfc
            }
            if (StringUtils.isNotBlank(cliente.telCasaCli)) {
                Contact phone = new Contact(type: ContactType.HOME_PHONE)
                phone.setPhoneNumber(cliente.telCasaCli)
                customer.contacts.add(phone)
            }
            if (StringUtils.isNotBlank(cliente.emailCli)) {
                Contact mail = new Contact(type: ContactType.EMAIL)
                mail.setEmail(cliente.emailCli)
                customer.contacts.add(mail)
            }

            return customer
        }
        return null
    }


    boolean equals(Object pObj) {
        boolean result = false;
        if (pObj instanceof Customer) {
            result = this.getId().equals((pObj as Customer).getId())
        }
        return result
    }

    static List<Customer> toList(List<Cliente> pClienteList) {
        List<Customer> custList = new ArrayList<Customer>()
        for (Cliente c : pClienteList) {
            custList.add(toCustomer(c))
        }
        return custList
    }

    Boolean isLocal() {
        return CustomerType.DOMESTIC.equals(this.type)
    }

    Titulo getTitle() {
        Titulo t = Titles.instance.find(this.title)
        if (t == null) {
            t = Titles.instance.getDefault(this.gender)
        }
        return t
    }

    Contact getPhone(Integer pContactIndex) {
        Contact telefono = null
        Integer ix = 0
        for (Contact c : this.contacts) {
            if (c.type.isPhone()) {
                if (ix == pContactIndex) {
                    telefono = c
                }
                ix++
            }
        }
        return telefono
    }

    Contact getEmail(Integer pContactIndex) {
        Contact email = null
        Integer ix = 0
        for (Contact c : this.contacts) {
            if (c.type.isMail()) {
                if (ix == pContactIndex) {
                    email = c
                }
                ix++
            }
        }
        return email
    }


    Examen getUltimoExamen(Integer id) {
        Examen examen = null

    }

    Date fechaUltimaNotaVenta() {

        if ( this.fechaUltimaVenta == null ) {
            NotaVenta notaVenta = CustomerController.buscarUltimaNotaVentaPorIdCliente(this.id)

            if (notaVenta != null)
                this.fechaUltimaVenta = notaVenta.fechaHoraFactura

        }

        return this.fechaUltimaVenta
    }

    Date fechaUltimoExamen() {

        if (this.fechaUltimoExamen == null) {
            Examen examen = CustomerController.buscarUltimoExamenPorIdCliente(this.id)

            if (examen != null) {
                this.fechaUltimoExamen = examen.fechaAlta
            }
        }

        this.fechaUltimoExamen;
    }


    String getFormaContactoMovil() {
        Integer tipoContacto = 3; // Para celulares

        FormaContacto formaContacto = CustomerController.buscarFormaContactoPorIdClienteTipoContacto(this.id, tipoContacto)

        if ( formaContacto != null )
            this.celular = formaContacto.contacto

        return this.celular
    }

    String getFechaFormato(String tipo){

        Date fecha = null

        if ( tipo.equals("fechaNacimiento") )
            fecha = this.fechaNacimiento

        if ( tipo.equals("fechaUltimaVenta") )
            fecha = fechaUltimaNotaVenta()

        if ( tipo.equals("fechaUltimoExamen") )
            fecha = fechaUltimoExamen()

        if ( fecha == null )
            return null

        String oldDate
        Date date
        String newDate

        oldDate = fecha.toString()
        date = new SimpleDateFormat("yyyy-MM-dd").parse(oldDate)
        newDate = new SimpleDateFormat("dd-MM-yyyy").format(date)

        return newDate
    }

}
