package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import mx.lux.pos.model.Cliente
import mx.lux.pos.java.repository.ClientesJava

@Bindable
@ToString
@EqualsAndHashCode
class Address {
  String primary = ''
  String zipcode = ''
  String location = ''
  String city = ''
  String state = ''
  String country = ''

  static Address toAddress( Cliente cliente ) {
    if ( cliente?.id ) {
      Address address = new Address(
          primary: cliente.direccion,
          zipcode: cliente.codigo,
          location: cliente.colonia,
          city: cliente.municipio?.nombre,
          state: cliente.municipio?.estado?.nombre
      )
      if ( cliente.clientePais?.id ) {
        address.city = cliente.clientePais.ciudad
        address.country = cliente.clientePais.pais
      }
      return address
    }
    return null
  }


  static Address toAddress( ClientesJava cliente ) {
    if ( cliente?.idCliente ) {
      Address address = new Address(
            primary: cliente.direccionCli,
            zipcode: cliente.codigo,
            location: cliente.coloniaCli,
            city: cliente.municipio?.nombre,
            state: cliente.municipio?.estado?.nombre
      )
      if ( cliente.clientePais?.idCliente ) {
        address.city = cliente.clientePais.ciudad
        address.country = cliente.clientePais.pais
      }
      return address
    }
    return null
  }
}
