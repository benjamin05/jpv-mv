package mx.lux.pos.ui.model

enum OperationType {
  DEFAULT( 'Público General' ),
  NEW( 'Cliente Nuevo' ),
  PENDING( 'Cliente en Proceso' ),
  PAYING( 'Cliente en Caja' ),
  WALKIN( 'Cliente Estadística' ),
  DOMESTIC( 'Cliente Nacional' ),
  FOREIGN( 'Cliente Extranjero' ),
  QUOTE( 'Cotización' ) ,
  AGREEMENT( 'Convenio' ),
  MULTYPAYMENT( 'Multipago' )

  final String value

  private OperationType( String value ) {
    //String clientesActivos = OrderController.obtieneTiposClientesActivos()
    //if(clientesActivos.contains(value)){
      this.value = value
    //}
  }

  static OperationType parse( String value ) {
    for ( item in values() ) {
      if ( item.value.equalsIgnoreCase( value?.trim() ) ) {
        return item
      }
    }
    return null
  }

  @Override
  String toString( ) {
    value
  }
}
