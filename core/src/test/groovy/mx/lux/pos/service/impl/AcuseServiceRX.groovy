package mx.lux.pos.service.impl

import mx.lux.pos.service.RecetaService
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.annotation.Resource




@ContextConfiguration( 'classpath:spring-config.xml' )
class AcuseServiceRX extends Specification {

    @Resource
    private RecetaService recetaService



      def "Test: Acuse de Receta"() {
      setup:
      String orderID = 'A07591'

      when:

      recetaService.generaAcuse(orderID)

      then:
      true
    }


}
