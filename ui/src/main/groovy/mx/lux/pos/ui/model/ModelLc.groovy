package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import mx.lux.pos.java.repository.ModeloLcJava
import mx.lux.pos.model.ModeloLc
import mx.lux.pos.model.Pago
import org.apache.commons.lang.StringUtils

import java.text.NumberFormat

@Bindable
@ToString
@EqualsAndHashCode
class ModelLc {
  String id
  String model
  String curve
  String diameter
  String sphere
  String cylinder
  String axis
  String color
  String idSupplier

  static toModelLc( ModeloLc modeloLc ) {
    if ( modeloLc?.id ) {
      ModelLc model = new ModelLc(
          id: modeloLc.id,
          model: modeloLc.modelo,
          curve: modeloLc.curva,
          diameter: modeloLc.diametro,
          sphere: modeloLc.esfera,
          cylinder: modeloLc.cilindro,
          axis: modeloLc.eje,
          color: modeloLc.color,
          idSupplier: modeloLc.idProveedor
      )
      return model
    }
    return null
  }


  static toModelLc( ModeloLcJava modeloLc ) {
    if ( modeloLc?.idModelo ) {
      ModelLc model = new ModelLc(
              id: StringUtils.trimToEmpty(modeloLc.idModelo),
              model: StringUtils.trimToEmpty(modeloLc.modelo),
              curve: StringUtils.trimToEmpty(modeloLc.curva),
              diameter: StringUtils.trimToEmpty(modeloLc.diametro),
              sphere: StringUtils.trimToEmpty(modeloLc.esfera),
              cylinder: StringUtils.trimToEmpty(modeloLc.cilindro),
              axis: StringUtils.trimToEmpty(modeloLc.eje),
              color: StringUtils.trimToEmpty(modeloLc.color),
              idSupplier: modeloLc.idProveedor
      )
      return model
    }
    return null
  }
}
