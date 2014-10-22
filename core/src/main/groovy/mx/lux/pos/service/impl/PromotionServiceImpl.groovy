package mx.lux.pos.service.impl

import mx.lux.pos.model.*
import mx.lux.pos.repository.*
import mx.lux.pos.service.PromotionService
import mx.lux.pos.service.business.*
import mx.lux.pos.service.io.PromotionsAdapter
import mx.lux.pos.util.StringList
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

import mx.lux.pos.service.business.*
import mx.lux.pos.model.PromotionDiscount

import java.text.NumberFormat
import java.text.SimpleDateFormat

@Service( 'promotionService' )
@Transactional( readOnly = true )
class PromotionServiceImpl implements PromotionService {

  private static Logger log = LoggerFactory.getLogger( PromotionServiceImpl.class )

  private Map<String, PromotionsAdapter> prData = new TreeMap<String, PromotionsAdapter>()

  @Resource
  private ParametroRepository parametroRepository

  @Resource
  private PromocionRepository promocionRepository

  @Resource
  private SucursalRepository sucursalRepository

  @Resource
  private DescuentoClaveRepository descuentoClaveRepository

  @Resource
   private NotaVentaRepository notaVentaRepository

  @Resource
    private DescuentoRepository descuentoRepository


  public void updateOrder( PromotionModel pModel, String pOrderNbr ) {
    log.debug( "Update Order: ${ pOrderNbr } " )
    PromotionEngine.instance.updateOrder( pModel, pOrderNbr )
  }

  @Transactional
  Boolean requestApplyPromotion( PromotionModel pModel, PromotionAvailable pPromotion ) {
    log.debug( "Apply Promotion: ${ pPromotion.description } " )
    return PromotionEngine.instance.applyPromotion( pModel, pPromotion )
  }

    @Transactional
    Boolean getApplyPromotion( PromotionModel pModel, PromotionAvailable pPromotion ) {
        log.debug( "Apply Promotion: ${ pPromotion.description } " )
        return PromotionEngine.instance.getPromotion( pModel, pPromotion )
    }

  @Transactional
  Boolean requestCancelPromotion( PromotionModel pModel, PromotionAvailable pPromotion ) {
    log.debug( "Cancel Promotion: ${ pPromotion.description } " )
    return PromotionEngine.instance.cancelPromotion( pModel, pPromotion, true )
  }

  @Transactional
  Boolean requestCancelPromotionDiscount( PromotionModel pModel, PromotionDiscount pPromotion ) {
      log.debug( "Cancel Promotion: ${ pPromotion.description } " )
      return PromotionEngine.instance.cancelPromotionDiscount( pModel, pPromotion, true )
  }

  Boolean requestOrderDiscount( PromotionModel pModel, String pCorporateKey, Double pDiscountPercent ) {
    log.debug( String.format( "Request Order Discount (Key:%s, Discount:%,.1f%%)",
        pCorporateKey, ( pDiscountPercent * 100.0 ) ) )
    return PromotionEngine.instance.applyOrderDiscount( pModel, pCorporateKey, pDiscountPercent )
  }

  void requestPersist( PromotionModel pModel, Boolean saveOrder ) {
    log.debug( String.format( "Request Persist Promotions for Order:%s", pModel.order.orderNbr ) )
    PromotionCommit.writePromotions( pModel )
    PromotionCommit.writeDiscounts( pModel, saveOrder )
  }

    void saveTipoDescuento(String idNotaVenta, String idTipoDescuento ){
       if(idTipoDescuento != null){
        if(idTipoDescuento.trim().equals('P')){
            NotaVenta notaVenta =  notaVentaRepository.findOne(idNotaVenta)
              if(notaVenta != null){
                notaVenta?.tipoDescuento = idTipoDescuento.trim()
                 notaVentaRepository?.saveAndFlush(notaVenta)
             }


        }
       }

    }

  Double requestTopStoreDiscount( ) {
    Double discount = PromotionQuery.getTopStoreDiscount()
    log.debug( String.format( "Request Top Discount in Store: %,.1f%%", discount * 100.0 ) )
    return discount
  }

  Boolean requestVerify( String pCorporateKey, Double pDiscountPct ) {
    log.debug( String.format( "RequestVerify( %s, %,.1f%%)", pCorporateKey, pDiscountPct ) )
    return PromotionEngine.instance.verifyCorporateKey( pCorporateKey, pDiscountPct )
  }

  @Override
  String obtenRutaPorRecibir( ) {
    log.debug( "obteniendo ruta por recibir" )
    def parametro = parametroRepository.findOne( TipoParametro.RUTA_POR_RECIBIR.value )
    log.debug( "ruta por recibir: ${parametro?.valor}" )
    return parametro?.valor
  }

  @Override
  String obtenRutaRecibidos( ) {
    log.debug( "obteniendo ruta recibidos" )
    def parametro = parametroRepository.findOne( TipoParametro.RUTA_RECIBIDOS.value )
    log.debug( "ruta recibidos: ${parametro?.valor}" )
    return parametro?.valor
  }


  @Override
  void RegistrarPromociones( ) {
    log.debug( "RegistrarPromociones()" )
    try {
      Parametro ubicacion = Registry.find( TipoParametro.RUTA_POR_RECIBIR )
      log.debug( "Ubicacion:: %s", ubicacion.valor )
      Parametro parametro = parametroRepository.findOne( TipoParametro.RUTA_RECIBIDOS.value )
      PromotionImportTask promotionImportTask = new PromotionImportTask()

      List<String> lstGrupoPromociones = promotionImportTask.runGroupPromotions( ubicacion.valor, parametro.valor )
      if ( lstGrupoPromociones.size() > 0 ) {
        PromotionCommit.updateGroupPromotions( lstGrupoPromociones )
        log.debug( "Se registraron los grupos de promociones" )
      }

      List<PromotionsAdapter> lstPromociones = promotionImportTask.run( ubicacion.valor, parametro.valor )
      log.debug( "Tamaño lista de Promociones::", lstPromociones.size() )

      PromotionCommit.updatePromotions( lstPromociones )
      log.debug( "Promociones Registradas" )
    } catch ( Exception e ) {
      log.error( "Error al registrar promociones: ", e )
    }
  }

  @Override
  void RegistrarClavesDescuento(){
    StringList nameFile
    Parametro ubicacion = Registry.find( TipoParametro.RUTA_POR_RECIBIR )
    log.debug( "Ubicacion:: %s", ubicacion.valor )
    Parametro parametro = parametroRepository.findOne( TipoParametro.RUTA_RECIBIDOS.value )
    String ubicacionSource = ubicacion.valor
    String ubicacionsDestination = parametro.valor
    File source = new File( ubicacionSource )
    File destination = new File( ubicacionsDestination )
    if ( source.exists() && destination.exists() ) {
      source.eachFile() { file ->
        if ( file.getName().endsWith( "CD" ) ) {
          String[] title = file.name.split(/\./)
          if(title.length > 2 && ( StringUtils.trimToEmpty(title[1]).equalsIgnoreCase("0") ||
                  StringUtils.trimToEmpty(title[1]).equalsIgnoreCase(StringUtils.trimToEmpty(Registry.currentSite.toString())) )){
            try {
              Integer nRead = 0
              String renglones
              file.eachLine { String pLine ->
                String[] claveDesc = pLine.split(/\|/)
                if(claveDesc.length >= 5){
                  Double porcentaje = 0.00
                  try{
                    porcentaje = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(claveDesc[1]))
                  } catch ( NumberFormatException e ){ println e }
                  DescuentoClave dc = descuentoClaveRepository.findOne(claveDesc[0])
                  if( dc == null ){
                    dc = new DescuentoClave()
                  }
                  dc.clave_descuento = claveDesc[0]
                  dc.porcenaje_descuento = porcentaje
                  dc.descripcion_descuento = claveDesc[2]
                  dc.tipo = claveDesc[3]
                  dc.vigente = StringUtils.trimToEmpty(claveDesc[4]).equalsIgnoreCase("yes") ? true : false
                  descuentoClaveRepository.save( dc )
                  descuentoClaveRepository.flush()
                }
              }
            } catch ( Exception ex ) {
              System.out.println( ex )
            }
            def newFile = new File( destination, file.name )
            def moved = file.renameTo( newFile )
          }
        }
      }
    } else {
        log.debug( "carpeta por_recibir o recibidos no existe" )
    }
  }


  @Override
  Promocion obtenerPromocion( Integer idPromocion ){
    log.debug( "obtenerPromocion( Integer idPromocion )" )

    Promocion promocion = promocionRepository.findOne( idPromocion )
    return promocion
  }

}