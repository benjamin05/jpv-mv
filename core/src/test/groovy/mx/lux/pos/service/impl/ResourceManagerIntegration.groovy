package mx.lux.pos.service.impl

import mx.lux.pos.model.TipoParametro
import mx.lux.pos.service.business.ResourceManager
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration('classpath:spring-config.xml')
class ResourceManagerIntegration extends Specification {
  
  def "Test Remission Location"( ) {
    when:
      File f = ResourceManager.getLocation( TipoParametro.RUTA_REMISION )
      println f.path
      
    then:
      true
  } 
}
