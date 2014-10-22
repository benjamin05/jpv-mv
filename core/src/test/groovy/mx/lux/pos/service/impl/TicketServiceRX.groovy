package mx.lux.pos.service.impl

import mx.lux.pos.service.TicketService
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.annotation.Resource

@ContextConfiguration( 'classpath:spring-config.xml' )
class TicketServiceRX extends Specification {

    @Resource
    private TicketService ticketService

    def "Test: Imprime Ticket de Receta"() {
    setup:
    String orderID = 'A07611'

    when:

    ticketService.imprimeRx(orderID)

    then:
    true
  }

}
