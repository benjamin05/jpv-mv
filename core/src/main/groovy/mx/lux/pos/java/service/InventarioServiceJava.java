package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.EmpleadoQuery;
import mx.lux.pos.java.querys.TipoTransInvQuery;
import mx.lux.pos.java.querys.TransInvQuery;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.*;
import mx.lux.pos.service.business.InventoryCommit;
import mx.lux.pos.service.business.PrepareInvTrBusiness;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class InventarioServiceJava {

  static final Logger log = LoggerFactory.getLogger(InventarioServiceJava.class);

  private static final String TR_TYPE_SALE = "VENTA";
  private static final String TR_TYPE_RECEIPT = "ENTRADA";
  private static final String TR_TYPE_RECEIPT_SP = "ENTRADA_SP";


  public Boolean solicitarTransaccionVenta( NotaVentaJava pNotaVenta ) {
    Boolean registrado = false;
    List<TransInvJava> transInv = TransInvQuery.BuscaTransInvPorTipoYReferencia(TR_TYPE_SALE, pNotaVenta.getIdFactura());
    if( transInv.size() <= 0 ){
      InvTrRequest request = PrepareInvTrBusiness.requestSalesIssue(pNotaVenta);
      System.out.println("Resquest: " + request.getTrType());
      if ( request != null ) {
        registrado = ( solicitarTransaccion( request ) != null );
      }
    }
    return registrado;
  }



  Integer solicitarTransaccion( InvTrRequest pRequest ) {
    Integer registrado = null;
    PrepareInvTrBusiness task = PrepareInvTrBusiness.getInstance();
    TransInvJava tr = task.prepareRequestJava(pRequest);
    if ( tr != null ) {
      registrado = registrarTransaccion( tr );
    }
    return registrado;
  }


  Integer registrarTransaccion( TransInvJava pTrMstr ) {
    Integer trnbr = InventoryCommit.registrarTransaccion(pTrMstr);
    if ( trnbr != null ) {
      InventoryCommit.exportarTransaccion( pTrMstr );
    }
    return trnbr;
  }


  public Integer obtenerSiguienteFolio( String pIdTipoTransInv ) {
    TipoTransInvJava tipo = TipoTransInvQuery.buscaTipoTransInvPorIdTipo( pIdTipoTransInv );
    Integer folio = tipo.getUltimoFolio() + 1;
    tipo.setUltimoFolio( folio );
    if ( tipo != null )
      TipoTransInvQuery.updateTipoTransInv(tipo);
    return folio;
  }


  public Boolean solicitarTransaccionEntradaSP( NotaVentaJava pNotaVenta ) {
    Boolean registrado = false;
    List<TransInvJava> transInv = TransInvQuery.BuscaTransInvPorTipoYReferencia(TR_TYPE_RECEIPT, pNotaVenta.getIdFactura());
    if( transInv.size() <= 0 ){
      PrepareInvTrBusiness task = PrepareInvTrBusiness.getInstance();
      InvTrRequest request = task.requestEnterSP( pNotaVenta );
      System.out.println("Resquest: " + request.getTrType());
      if ( request != null ) {
        registrado = ( solicitarTransaccion( request ) != null );
      }
    }
    return registrado;
  }



  public void insertarRegistroRemesa( NotaVentaJava pNotaVenta ){
    RemesasJava remesa = new RemesasJava();
    Integer articulos = 0;
    for( DetalleNotaVentaJava det : pNotaVenta.getDetalles() ){
      if( StringUtils.trimToEmpty(det.getSurte()).equalsIgnoreCase("P") ){
        articulos = articulos+det.getCantidadFac().intValue();
      }
    }
    TransInvJava transInv = TransInvQuery.BuscaTransInvPorTipoYReferencia(TR_TYPE_RECEIPT_SP, StringUtils.trimToEmpty(pNotaVenta.id) );
    if( transInv != null ){
      remesa.setIdTipoDocto("RS");
      remesa.setIdDocto(StringUtils.trimToEmpty(transInv.getFolio().toString()));
      remesa.setDocto(StringUtils.trimToEmpty(pNotaVenta.getFactura()));
      remesa.setClave("");
      remesa.setLetra("X");
      remesa.setArchivo("");
      remesa.setArticulos(articulos);
      remesa.setEstado("cargado");
      remesa.setSistema("A");
      remesa.setFecha_mod(new Date());
      remesa.fecha_recibido = new Date()
      remesa.fecha_carga = new Date()
      remesasRepository.saveAndFlush( remesa )
    }
  }


}
