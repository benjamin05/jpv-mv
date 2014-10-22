package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.commons.lang3.StringUtils

@Bindable
@ToString
@EqualsAndHashCode
class CuponMvView {
  BigDecimal amount
  String idOrderSource

    BigDecimal getAmount() {
        return amount
    }

    void setAmount(BigDecimal amount) {
        this.amount = amount
    }

    String getIdOrderSource() {
        return idOrderSource
    }

    void setIdOrderSource(String idOrderSource) {
        this.idOrderSource = idOrderSource
    }
}
