package mx.lux.pos.model

import org.apache.commons.lang3.StringUtils

enum Feature {
  RECETAS( 'recetas', false )

  private final String featureId
  private final boolean defaultActive

  private Feature( String pId, boolean pActive ) {
    this.featureId = StringUtils.trimToEmpty( pId ).toLowerCase()
    this.defaultActive = pActive
  }

  static Feature parse( String pId ) {
    for ( item in values() ) {
      if ( item.featureId.equalsIgnoreCase( StringUtils.trimToEmpty( pId ) ) ) {
        return item
      }
    }
    return null
  }

  String getFeatureId( ) {
    return this.featureId
  }

  boolean getActiveOnDefault( ) {
    return this.defaultActive
  }

  String toString( ) {
    return featureId
  }
}
