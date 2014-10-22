package mx.lux.pos.service.impl

import mx.lux.pos.model.CuponMv
import mx.lux.pos.model.Empleado
import mx.lux.pos.model.ResumenDiario
import mx.lux.pos.model.TransInv
import mx.lux.pos.service.business.InventorySearch
import org.apache.commons.lang3.time.DateUtils
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration('classpath:spring-config.xml')
class TicketSampleIntegration extends Specification {

  def "Print Adjust Ticket"() {
    when:
    TransInv tr = InventorySearch.obtenerTransaccion( 'AJUSTE', 7 )
    ServiceFactory.ticketEngine.imprimeTransInv( tr )

    then:
    tr != null
  }

  def "Print Daily Close Terminal Ticket"() {
    setup:
    Date closeDate = DateUtils.parseDate( '2013-04-02', 'yyyy-MM-dd' )
    String term = 'MNAMX'
    String user = '9999'

    when:
    List<ResumenDiario> results = ServiceFactory.dailyClose.buscarResumenDiarioPorFechaPorTerminal( closeDate, term )
    Empleado emp = ServiceFactory.employeeCatalog.obtenerEmpleado( user )
    boolean result = ServiceFactory.ticketEngine.imprimeCierreTerminales( closeDate, results, emp, term )

    then:
    true
  }

  def "Print Cupon Ticket"() {
    setup:
      CuponMv cuponMv = new CuponMv()

      when:
        cuponMv = ServiceFactory.salesOrders.obtenerCuponMv( "204178" )
        boolean result = ServiceFactory.ticketEngine.imprimeCupon( cuponMv, "CUPON SEGUNDO PAR" )

      then:
        true
  }

}
