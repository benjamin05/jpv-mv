package mx.lux.pos.java.service;


import mx.lux.pos.java.service.business.PromotionCommitJava;
import mx.lux.pos.model.PromotionModel;
import mx.lux.pos.java.querys.NotaVentaQuery;
import mx.lux.pos.java.repository.NotaVentaJava;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class PromotionServiceJava {

  static final Logger log = LoggerFactory.getLogger(PromotionServiceJava.class);

  public void saveTipoDescuento(String idNotaVenta, String idTipoDescuento ) throws ParseException {
    if(idTipoDescuento != null){
      if(idTipoDescuento.trim().equalsIgnoreCase("P")){
        NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById(idNotaVenta);
        if(notaVenta != null){
          notaVenta.setTipoDescuento(idTipoDescuento.trim());
          NotaVentaQuery.updateNotaVenta(notaVenta);
        }
      }
    }
  }


  public void requestPersist( PromotionModel pModel, Boolean saveOrder ) throws ParseException {
    PromotionCommitJava.writePromotions(pModel);
    PromotionCommitJava.writeDiscounts( pModel, saveOrder );
  }
}
