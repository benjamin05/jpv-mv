package mx.lux.pos.service;


import mx.lux.pos.model.*;
import mx.lux.pos.querys.NotaVentaQuery;
import mx.lux.pos.repository.NotaVentaJava;
import mx.lux.pos.service.business.PromotionCommit;
import mx.lux.pos.service.business.PromotionCommitJava;
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
