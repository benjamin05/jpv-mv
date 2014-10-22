package mx.lux.pos.repository

import mx.lux.pos.model.Cliente
import mx.lux.pos.repository.impl.RepositoryFactory
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration( 'classpath:spring-config.xml' )
class ClienteRepositoryIntegration extends Specification {

  def "Test List By Hint"( ) {
    when:
    ClienteRepository customers = RepositoryFactory.customerCatalog
    List<Cliente> custList = customers.listByTextContainedInName( 'R' )
    for (Cliente c : custList) {
      println c.nombreCompleto
    }

    then:
    custList.size() > 0
  }


}
