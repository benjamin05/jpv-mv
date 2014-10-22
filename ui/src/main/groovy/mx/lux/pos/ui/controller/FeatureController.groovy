package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.service.FuncionalidadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Slf4j
@Component
class FeatureController {

  private static FuncionalidadService funcionalidadService

  @Autowired
  public FeatureController( FuncionalidadService funcionalidadService ) {
    this.funcionalidadService = funcionalidadService
  }

  static boolean isRxEnabled( ) {
    return this.funcionalidadService.rxEnabled
  }

}
