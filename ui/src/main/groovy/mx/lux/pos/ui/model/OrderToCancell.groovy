package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import mx.lux.pos.model.Cliente

@Bindable
@ToString
@EqualsAndHashCode
class OrderToCancell {
  String idOrder
  String client
  String discount
  Boolean selected

    String getIdOrder() {
        return idOrder
    }

    void setIdOrder(String idOrder) {
        this.idOrder = idOrder
    }

    String getClient() {
        return client
    }

    void setClient(String client) {
        this.client = client
    }

    String getDiscount() {
        return discount
    }

    void setDiscount(String discount) {
        this.discount = discount
    }

    Boolean getSelected() {
        return selected
    }

    void setSelected(Boolean selected) {
        this.selected = selected
    }
}
