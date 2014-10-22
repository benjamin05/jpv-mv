package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Bindable
@ToString
@EqualsAndHashCode
class Dioptra {
  String material
  String lente
  String tipo
  String especial
  String tratamiento
  String color

    Dioptra(String material, String lente, String tipo, String especial, String tratamiento, String color) {
        this.material = material
        this.lente = lente
        this.tipo = tipo
        this.especial = especial
        this.tratamiento = tratamiento
        this.color = color
    }

    Dioptra() {
    }

    void setMaterial(String material) {
        this.material = material
    }

    void setLente(String lente) {
        this.lente = lente
    }

    void setTipo(String tipo) {
        this.tipo = tipo
    }

    void setEspecial(String especial) {
        this.especial = especial
    }

    void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento
    }

    void setColor(String color) {
        this.color = color
    }

    String getMaterial() {
        return material
    }

    String getLente() {
        return lente
    }

    String getTipo() {
        return tipo
    }

    String getEspecial() {
        return especial
    }

    String getTratamiento() {
        return tratamiento
    }

    String getColor() {
        return color
    }
}
