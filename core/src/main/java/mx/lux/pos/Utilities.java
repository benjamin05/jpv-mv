package mx.lux.pos;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

  public static Integer toInteger(String strValue){
    Integer valueTmp = 0;
    try{
      valueTmp = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(strValue)).intValue();
    } catch (NumberFormatException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return valueTmp;
  }

  public static String toString( Date fecha, String format ){
    String value = null;
    SimpleDateFormat df = new SimpleDateFormat( format );
    if( fecha != null ){
      value = df.format( fecha );
    }
    return  value;
  }

  public static String toMoney( BigDecimal amount ){
    String value = "'$0.00'";
    NumberFormat nf = NumberFormat.getCurrencyInstance( Locale.US );
    if( amount != null ){
      value = "'"+StringUtils.trimToEmpty(nf.format( amount ))+"'";
    }
    return  value;
  }


  public static Integer trimtoNull( Integer amount ){
    Integer value = null;
    if( amount != null && amount > 0 ){
      value = amount;
    }
    return  value;
  }


}
