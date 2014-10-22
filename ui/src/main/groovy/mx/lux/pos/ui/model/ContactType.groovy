package mx.lux.pos.ui.model

enum ContactType {
  HOME_PHONE( 'Tel. Casa' ),
  OFFICE_PHONE( 'Tel. Trabajo' ),
  MOBILE_PHONE( 'Tel. Móvil' ),
  EMAIL( 'E-mail' )

  final String value

  private ContactType( String value ) {
    this.value = value
  }

  static ContactType parse( String value ) {
    for ( item in values() ) {
      if ( item.value.equalsIgnoreCase( value?.trim() ) ) {
        return item
      }
    }
    return null
  }

  Boolean isMail( ) {
    return ( EMAIL.equals( this ) )
  }

  Boolean isPhone( ) {
    return ( HOME_PHONE.equals( this ) || OFFICE_PHONE.equals( this ) || MOBILE_PHONE.equals( this ) )
  }

  String toString( ) {
    value
  }
}
