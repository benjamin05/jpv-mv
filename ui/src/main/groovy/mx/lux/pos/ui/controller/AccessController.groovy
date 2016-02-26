package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.java.repository.EmpleadoJava
import mx.lux.pos.java.service.EmpleadoServiceJava
import mx.lux.pos.model.Empleado
import mx.lux.pos.model.LogAsignaSubgerente
import mx.lux.pos.service.EmpleadoService
import mx.lux.pos.service.ListaPreciosService
import mx.lux.pos.service.SucursalService
import mx.lux.pos.ui.model.Branch
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import mx.lux.pos.ui.model.User
import mx.lux.pos.ui.resources.ServiceManager
import org.apache.commons.lang3.StringUtils
import mx.lux.pos.service.business.Registry
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.regex.Pattern

@Slf4j
@Component
class AccessController {

  private static EmpleadoService empleadoService
  private static EmpleadoServiceJava empleadoServiceJava
  private static SucursalService sucursalService
  private static ListaPreciosService listaPreciosService

  @Autowired
  AccessController( EmpleadoService empleadoService, SucursalService sucursalService, ListaPreciosService listaPreciosService ) {
    this.empleadoService = empleadoService
    this.sucursalService = sucursalService
    this.listaPreciosService = listaPreciosService
    empleadoServiceJava = new EmpleadoServiceJava()
  }

  static User getUser( String username ) {
    log.info( "solicitando usuario: ${username}" )
    return User.toUser( empleadoServiceJava.obtenerEmpleado( username ) )
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

  private static boolean isAuthorizer( EmpleadoJava empleado ) {
    log.info( "verificando si empleado es autorizador: ${empleado?.idEmpleado}" )
    if ( empleado?.idEmpleado ) {
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
      EmpleadoJava empleado = empleadoServiceJava.obtenerEmpleado( user.username )
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
      EmpleadoJava empleado = empleadoServiceJava.obtenerEmpleado( username )
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

  static String validaDatos( String usuario, String password, String nuevoPass, String confirmPass ){
    log.debug( "Cambiando password de usuario $usuario" )
    String empleadoValido = ""
    Empleado empleado = empleadoService.obtenerEmpleado( usuario )
    if( empleado == null ){
      empleadoValido = "Empleado no existe"
    } else if( !StringUtils.trimToEmpty(nuevoPass).equals(StringUtils.trimToEmpty(confirmPass)) ){
      empleadoValido = "El Password no coincide con la cofirmacion"
    } else if( StringUtils.trimToEmpty(nuevoPass).length() < 8 ){
      empleadoValido = "El password debe contener al menos 8 caracteres"
    } else if( StringUtils.trimToEmpty(empleado.passwd).equals(StringUtils.trimToEmpty(nuevoPass)) ){
      empleadoValido = "El password no debe ser el mismo que el anterior"
    }
    Integer contador = 0
    Pattern capitalPat = Pattern.compile("[A-Z]");
    Pattern lowerPat = Pattern.compile("[a-z]");
    Pattern numberPat = Pattern.compile("[0-9]");
    Pattern specialPat = Pattern.compile ("[!@#"+/\$/+"+%&*()_+=|<>?{}\\[\\]~-]");

    if (capitalPat.matcher(nuevoPass).find()){
      contador = contador+1
    }
    if (lowerPat.matcher(nuevoPass).find()){
      contador = contador+1
    }
    if (numberPat.matcher(nuevoPass).find()){
      contador = contador+1
    }
    if (specialPat.matcher(nuevoPass).find()){
      contador = contador+1
    }
    if( contador < 2 ){
      empleadoValido = "<html>El password debe contener al menos 2 de las siguientes combinaciones:<br>-Mayusculas<br>-Minusculas<br>-Numeros<br>-Simbolos(#"+/\$/+"%&)<html>"
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


  static boolean isManager( Empleado empleado ) {
    log.info( "verificando si empleado es autorizador: ${empleado?.id}" )
    if ( empleado?.id ) {
      Boolean isSubManager = false
      LogAsignaSubgerente logSub = ServiceManager.employeeService.obtenerSubgerenteActual()
      if( logSub != null && StringUtils.trimToEmpty(empleado.id).equalsIgnoreCase(logSub.empleadoAsignado) ){
        isSubManager = true
      }
      if ( empleado.idPuesto == 1 || empleado.idPuesto == 15 || isSubManager ) {
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


  static Boolean saveSubManager( String idEmployee, Date initialDate, Date finalDate, Integer hours ) {
    try{
      User user = (User)Session.get( SessionItem.USER );
      empleadoService.insertaSubgerente( idEmployee, user.username, initialDate, finalDate, hours )
      return true
    } catch ( Exception e ){
      println e.message
      return false
    }
  }


  static Boolean existSubmanager(){
    LogAsignaSubgerente log = ServiceManager.employeeService.obtenerSubgerenteActual()
    return log != null
  }


  static Boolean validTimePass( String username ){
    Boolean valid = true
    EmpleadoJava emp = empleadoServiceJava.obtenerEmpleado( username )
    if( emp != null ){
      Date today = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH );
      Calendar cal = Calendar.getInstance()
      cal.setTime(emp.fechaMod)
      cal.add( Calendar.MONTH, Registry.monthsToChangePass)
      Date fechaFin = new Date( DateUtils.ceiling( cal.getTime(), Calendar.DAY_OF_MONTH ).getTime() - 1 );
      if( today.compareTo(fechaFin) > 0 ){
        valid = false
      }
    }
    return valid
  }


  static Empleado findEmployee( String idEmp ){
    return empleadoService.obtenerEmpleado( idEmp )
  }
}
