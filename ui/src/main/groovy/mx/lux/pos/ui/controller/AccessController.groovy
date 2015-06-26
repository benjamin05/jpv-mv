package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.model.Empleado
import mx.lux.pos.service.EmpleadoService
import mx.lux.pos.service.ListaPreciosService
import mx.lux.pos.service.SucursalService
import mx.lux.pos.ui.model.Branch
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import mx.lux.pos.ui.model.User
import org.apache.commons.lang3.StringUtils
import mx.lux.pos.service.business.Registry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Slf4j
@Component
class AccessController {

  private static EmpleadoService empleadoService
  private static SucursalService sucursalService
  private static ListaPreciosService listaPreciosService

  @Autowired
  AccessController( EmpleadoService empleadoService, SucursalService sucursalService, ListaPreciosService listaPreciosService ) {
    this.empleadoService = empleadoService
    this.sucursalService = sucursalService
    this.listaPreciosService = listaPreciosService
  }

  static User getUser( String username ) {
    log.info( "solicitando usuario: ${username}" )
    return User.toUser( empleadoService.obtenerEmpleado( username ) )
  }

  static boolean checkCredentials( String username, String password ) {
    log.info( "comprobando credenciales para el usuario: ${username}" )
    if ( StringUtils.isNotBlank( username ) && StringUtils.isNotBlank( password ) ) {
      User user = getUser( username )
      if ( StringUtils.isNotBlank( user?.username ) ) {
        if ( password.equalsIgnoreCase( user?.password ) ) {
          log.info( "credenciales correctas" )
          return true
        } else {
          log.warn( "acceso denegado, credenciales incorrectas" )
        }
      } else {
        log.warn( "usuario no existente" )
      }
    } else {
      log.warn( "no se comprueban credenciales, parametros invalidos" )
    }
    return false
  }

  static User logIn( String username, String password ) {
    log.info( "solicitando autorizacion de acceso para el usuario: $username" )
    if ( checkCredentials( username, password ) ) {
      User user = getUser( username )
      Branch branch = Branch.toBranch( sucursalService.obtenSucursalActual() )
        println('sucursal..................'+ branch?.id)
      Session.put( SessionItem.USER, user )
      Session.put( SessionItem.BRANCH, branch )
      log.info( "acceso autorizado: $username" )
      return user
    } else {
      log.warn( "acceso denegado, credenciales incorrectas" )
    }
    return null
  }

  static void logOut( ) {
    Session.clear()
    log.info( "log out" )
  }

  private static boolean isAuthorizer( Empleado empleado ) {
    log.info( "verificando si empleado es autorizador: ${empleado?.id}" )
    if ( empleado?.id ) {
      if ( ( 1..2 ).contains( empleado.idPuesto ) ) {
        log.info( "usuario es autorizador" )
        return true
      } else {
        log.info( "no es usuario autorizador" )
      }
    } else {
      log.warn( "no se verifica usuario, parametros invalidos" )
    }
    return false
  }

  static boolean isAuthorizerInSession( ) {
    log.info( "comprobando si usuario en sesion requiere autorizacion" )
    User user = Session.get( SessionItem.USER ) as User
    log.debug( "usuario en sesion: ${user?.username}" )
    if ( StringUtils.isNotBlank( user?.username ) ) {
      Empleado empleado = empleadoService.obtenerEmpleado( user.username )
      if ( isAuthorizer( empleado ) ) {
        log.info( "usuario autorizador, no requiere autorizacion" )
        return true
      } else {
        log.info( "usuario requiere autorizacion" )
      }
    } else {
      log.warn( "no se realiza comprobacion, no existe usuario en sesion" )
    }
    return false
  }

  static boolean canAuthorize( String username, String password ) {
    log.info( "solicitando autorizacion por usuario: $username" )
    if ( checkCredentials( username, password ) ) {
      Empleado empleado = empleadoService.obtenerEmpleado( username )
      if ( isAuthorizer( empleado ) ) {
        log.info( "autorizacion realizada: $username" )
        return true
      } else {
        log.info( "autorizacion rechazada, no es usuario autorizador" )
      }
    } else {
      log.warn( "credenciales erroneas" )
    }
    return false
  }

  static boolean validPass( String password ) {
    log.info( "solicitando autorizacion por password: $password" )
    String[] pass = Registry.isActiveValidSP().split(/\|/)
    if ( pass.length > 1 && password.trim().equalsIgnoreCase(pass[1].trim()) ) {
      log.info( "autorizacion realizada: $password" )
      return true
    } else {
      log.info( "autorizacion rechazada, no es usuario autorizador" )
    }
    return false
  }

  static boolean validPassAudit( String user, String password ) {
    log.info( "solicitando autorizacion de auditora por password: $password" )
    Boolean valid = false
    Empleado emp = empleadoService.obtenerEmpleado( user )
    Boolean validUser = validateUser( emp )
    if ( validUser ) {
      //String[] ids = validUsers.split( "," )
      //for(String id : ids){
        if( StringUtils.trimToEmpty(emp.passwd).equalsIgnoreCase(password) ){
          valid = true
          //break
        }
      //}
    } else {
      log.info( "autorizacion rechazada, no es usuario autorizador" )
    }
    return valid
  }

  static boolean validaDatos( String usuario, String password, String nuevoPass, String confirmPass ){
      log.debug( "Cambiando password de usuario $usuario" )
      Boolean empleadoValido = false
      Empleado empleado = empleadoService.obtenerEmpleado( usuario )
      if( empleado != null && empleado.passwd.trim().equalsIgnoreCase(password.trim()) && nuevoPass.trim().equalsIgnoreCase(confirmPass.trim()) ){
          empleadoValido = true
      } else {
          empleadoValido = false
      }
      return empleadoValido
  }


  static boolean cambiaPassword( String usuario, String nuevoPass ){
      log.debug( "Cambiando password de usuario $usuario" )
      try{
          Empleado empleado = empleadoService.obtenerEmpleado( usuario )
          empleado.passwd = nuevoPass
          empleadoService.actualizarPass( empleado )
          return true
      }catch (Exception e){
          println e
          return false
      }
  }

  static Integer listaPreciosPendientes(){
      return listaPreciosService.listasPreciosPendientes()
  }



  static User importEmployee( String idEmployee ){
    Empleado empleado = new Empleado()
    if( StringUtils.trimToEmpty(idEmployee).length() > 0 ){
      empleado = empleadoService.importaEmpleado( idEmployee )
    }
    if( empleado?.id == null ){
      empleado.id = 0
    }
    return User.toUser(empleado)
  }



  static boolean canAuthorizeManager( String username, String password ) {
    log.info( "solicitando autorizacion por usuario: $username" )
    if ( checkCredentials( username, password ) ) {
      Empleado empleado = empleadoService.obtenerEmpleado( username )
      if ( isManager( empleado ) ) {
        log.info( "autorizacion realizada: $username" )
        return true
      } else {
        log.info( "autorizacion rechazada, no es usuario autorizador" )
      }
    } else {
      log.warn( "credenciales erroneas" )
    }
    return false
  }


  private static boolean isManager( Empleado empleado ) {
    log.info( "verificando si empleado es autorizador: ${empleado?.id}" )
    if ( empleado?.id ) {
      if ( StringUtils.trimToEmpty( Registry.idManager ).contains( StringUtils.trimToEmpty(empleado.id) ) ) {
        log.info( "usuario es autorizador" )
        return true
      } else {
        log.info( "no es usuario autorizador" )
      }
    } else {
      log.warn( "no se verifica usuario, parametros invalidos" )
    }
    return false
  }


  static boolean canAuthorizeIp( String username, String password ) {
    log.info( "solicitando autorizacion por usuario sistemas: $username" )
    if ( checkCredentials( username, password ) ) {
      String usuarioSistemas = StringUtils.trimToEmpty(Registry.usuarioSistemas)
      Empleado empleado = empleadoService.obtenerEmpleado( username )
      if ( usuarioSistemas.equalsIgnoreCase(StringUtils.trimToEmpty(empleado.id)) ) {
        log.info( "autorizacion realizada: $username" )
        return true
      } else {
        log.info( "autorizacion rechazada, no es usuario autorizador" )
      }
    } else {
            log.warn( "credenciales erroneas" )
    }
    return false
  }

  static Boolean validateUser( Empleado emp ){
    Boolean valid = false
    String validUsers = Registry.idAuditoras()
    if( validUsers != null && validUsers.trim().contains(StringUtils.trimToEmpty(emp.id)) ){
      valid = true
    } else if( emp.idPuesto == 15 ){
      valid = true
    }
    return valid
  }


}
