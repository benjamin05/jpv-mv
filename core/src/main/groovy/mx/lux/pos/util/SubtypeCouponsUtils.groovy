package mx.lux.pos.util

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SubtypeCouponsUtils {

  private static Logger log = LoggerFactory.getLogger( SubtypeCouponsUtils.class )
  private static final TAG_SUBTIPO_BR = "BR"
  private static final TAG_SUBTIPO_PL = "PL"
  private static final TAG_SUBTIPO_OR = "OR"
  private static final TAG_SUBTIPO_PT = "PT"
  private static final TAG_SUBTIPO_PTT = "PTT"
  private static final TAG_SUBTIPO_DM = "DM"

  private static final TAG_TITULO_BR = "BRONCE"
  private static final TAG_TITULO_PL = "PLATA"
  private static final TAG_TITULO_OR = "ORO"
  private static final TAG_TITULO_PT = "PLATINO"
  private static final TAG_TITULO_PTT = "PLATINO TORICO"
  private static final TAG_TITULO_DM = "DIAMANTE"

  static String getTitle2( String text ) {
    String title = ""
    if( StringUtils.trimToEmpty(text).equalsIgnoreCase(TAG_SUBTIPO_BR) ){
      title = TAG_TITULO_BR
    } else if( StringUtils.trimToEmpty(text).equalsIgnoreCase(TAG_SUBTIPO_PL) ){
        title = TAG_TITULO_PL
    } else if( StringUtils.trimToEmpty(text).equalsIgnoreCase(TAG_SUBTIPO_OR) ){
        title = TAG_TITULO_OR
    } else if( StringUtils.trimToEmpty(text).equalsIgnoreCase(TAG_SUBTIPO_PT) ){
        title = TAG_TITULO_PT
    } else if( StringUtils.trimToEmpty(text).equalsIgnoreCase(TAG_SUBTIPO_PTT) ){
        title = TAG_TITULO_PTT
    } else if( StringUtils.trimToEmpty(text).equalsIgnoreCase(TAG_SUBTIPO_DM) ){
        title = TAG_TITULO_DM
    }
    return title
  }

}
