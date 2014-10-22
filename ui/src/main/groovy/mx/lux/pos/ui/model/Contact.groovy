package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.commons.lang3.StringUtils

@Bindable
@ToString
@EqualsAndHashCode
class Contact {
  String primary
  String extra
  String comments
  ContactType type = ContactType.HOME_PHONE

  boolean equals( Object pObject ) {
    boolean result = false
    if ( pObject instanceof Contact ) {
      result = ( this.type.equals( pObject.type as ContactType )
          && this.primary.equalsIgnoreCase( pObject.primary as String) )
    }
    return result
  }

  int hashCode() {
    return this.primary.hashCode()
  }

  Boolean isPhone() {
    return ( ContactType.HOME_PHONE.equals(this.type) || ContactType.OFFICE_PHONE.equals(this.type)
        || ContactType.MOBILE_PHONE.equals(this.type) )
  }

  Boolean isMail() {
    return ( ContactType.EMAIL.equals(this.type)  )
  }

  String getPhoneNumber() {
    String phone = ''
    if (this.isPhone()) {
      phone = this.primary
    }
    return phone
  }

  void setPhoneNumber( String pPhone ) {
    if ( this.isPhone() ) {
      this.primary = StringUtils.trimToEmpty( pPhone ).toUpperCase()
    }
  }

  String getDomain() {
    String domain = ''
    if ( this.isMail( ) ) {
      domain = StringUtils.trimToEmpty( this.primary.tokenize( '@' ).get( 1 ) ).toLowerCase()
    }
    return domain
  }

  String getLocal() {
    String local = ''
    if ( this.isMail( ) ) {
      local = StringUtils.trimToEmpty( this.primary.tokenize( '@' ).get( 0 ) ).toLowerCase()
    }
    return local
  }

  String setEmail( String pMail ) {
    if ( this.isMail() ) {
      this.primary = StringUtils.trimToEmpty( pMail ).toUpperCase( )
    }
  }

  String setEmail( String pLocal, String pDomain ) {
    if ( this.isMail() && StringUtils.isNotBlank( pLocal ) && StringUtils.isNotBlank( pDomain ) ) {
      this.primary = String.format( '%s@%s', StringUtils.trimToEmpty( pLocal ).toLowerCase( ),
          StringUtils.trimToEmpty( pDomain ).toLowerCase() )
    }
  }

  String toString( ) {
    return String.format( '[%s] %s', this.type, StringUtils.trimToEmpty(this.primary) )
  }
}
