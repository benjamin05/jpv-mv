package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import mx.lux.pos.model.Empleado
import mx.lux.pos.repository.EmpleadoJava
import org.apache.commons.lang.StringUtils

@Bindable
@ToString( excludes = 'password' )
@EqualsAndHashCode
public class User {

  String name
  String fathersName
  String mothersName
  String username
  String password
  String idSucursal

  String getFullName( ) {
    "${name ?: ''} ${fathersName ?: ''} ${mothersName ?: ''}"
  }

  static toUser( Empleado empleado ) {
    if ( empleado?.id ) {
      def user = new User()
      user.name = empleado.nombre
      user.fathersName = empleado.apellidoPaterno
      user.mothersName = empleado.apellidoMaterno
      user.username = empleado.id
      user.password = empleado.passwd
        user.idSucursal = empleado.idSucursal
      return user
    }
    return null
  }

  static toUser( EmpleadoJava empleado ) {
    if ( empleado?.idEmpleado ) {
      def user = new User()
      user.name = StringUtils.trimToEmpty(empleado.nombreEmpleado)
      user.fathersName = StringUtils.trimToEmpty(empleado.apMatEmpleado)
      user.mothersName = StringUtils.trimToEmpty(empleado.apMatEmpleado)
      user.username = StringUtils.trimToEmpty(empleado.idEmpleado)
      user.password = StringUtils.trimToEmpty(empleado.passwd)
      user.idSucursal = empleado.idSucursal
      return user
    }
    return null
  }

  boolean equals(Object pObj) {
    boolean result = false
    if (pObj instanceof User) {
      result = this.getUsername().trim().equalsIgnoreCase(pObj.getUsername().trim())
    } else if ( pObj instanceof Empleado ) {
      result = this.getUsername().trim().equalsIgnoreCase(pObj.getId().trim())
    }
    return result
  }

  String toString() {
    return String.format('(%s) %s', this.getUsername(), this.getFullName())
  }
}
