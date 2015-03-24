package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import mx.lux.pos.model.BancoDev
import mx.lux.pos.model.Cliente

@Bindable
@ToString
@EqualsAndHashCode
class DevBank {
  Integer id
  String name

  static DevBank toDevBank( BancoDev bancoDev ) {
    DevBank bank = new DevBank(
      id: bancoDev.id,
      name: bancoDev.nombre
    )
    return bank
  }
}
