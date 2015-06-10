package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.NotaVentaJava;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;

/**
 * Created by Benjamin Ivan Martinez Mendoza.
 * User: sucursal
 * Date: 9/06/15
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class PromotionQueryJava {

  public static Integer findSiteNbr( String pOrderNbr ) throws ParseException {
    Integer siteNbr = 0;
    NotaVentaJava dbOrder = NotaVentaQuery.busquedaNotaById(StringUtils.trimToEmpty(pOrderNbr));
    if ( dbOrder != null ) {
      siteNbr = dbOrder.getIdSucursal();
    }
    return siteNbr;
  }


  public static String findEmpId(String pOrderNbr) throws ParseException {
    String empId = "";
    NotaVentaJava dbOrder = NotaVentaQuery.busquedaNotaById(StringUtils.trimToEmpty(pOrderNbr));
    if ( dbOrder != null ) {
      empId = dbOrder.getIdEmpleado();
    }
    return empId;
  }


}
