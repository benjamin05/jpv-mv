package mx.lux.pos;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by Benjamin Ivan Martinez Mendoza.
 * User: sucursal
 * Date: 1/06/15
 * Time: 10:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class Utilities {


  public static BigDecimal toBigDecimal(String strValue){
    Double valueTmp = 0.00;
    strValue = strValue != null ? strValue.replace("$", "") : "0.00";
    strValue = strValue != null ? strValue.replace(",", "") : "0.00";
    try{
      valueTmp = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(strValue)).doubleValue();
    } catch (NumberFormatException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return new BigDecimal( valueTmp );
  }


}
