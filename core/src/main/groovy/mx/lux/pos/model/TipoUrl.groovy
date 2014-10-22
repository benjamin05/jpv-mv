package mx.lux.pos.model

enum TipoUrl {
  URL_ACUSE_VENTA_DIA( 'venta', '' ),
  URL_ACUSE_REMESA( 'REM', '' )

  final String value
  final String defaultValue

  private TipoUrl( String value ) {
    this( value, '' )
  }

  private TipoUrl( String value, String defaultValue ) {
    this.value = value
    this.defaultValue = defaultValue
  }

  static TipoUrl parse( String value ) {
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
