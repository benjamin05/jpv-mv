package mx.lux.pos.service.impl

import mx.lux.pos.model.Parametro
import mx.lux.pos.model.TipoPago
import mx.lux.pos.model.TipoParametro
import mx.lux.pos.repository.ParametroRepository
import mx.lux.pos.repository.TipoPagoRepository
import mx.lux.pos.service.TipoPagoService
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

@Service( 'tipoPagoService' )
@Transactional( readOnly = true )
class TipoPagoServiceImpl implements TipoPagoService {

  private static final Logger log = LoggerFactory.getLogger( TipoPagoServiceImpl.class )
  private static final String TAG_TIPO_PAGO_EFECTIVO = 'EF'
  private static final String TAG_TIPO_PAGO_TARJETA_CREDITO = 'TC'
  private static final String TAG_TIPO_PAGO_TARJETA_DEBITO = 'TD'
  private static final String TAG_TIPO_PAGO_CUPON1 = 'C1'
  private static final String TAG_TIPO_PAGO_CUPON2 = 'C2'
  private static final String TAG_TIPO_PAGO_CUPON3 = 'C3'
  private static final String TAG_TIPO_PAGO_CUPON4 = 'C4'
  private static final String TAG_TIPO_PAGO_TRANSFERENCIA = 'TR'

  @Resource
  private TipoPagoRepository tipoPagoRepository

  @Resource
  private ParametroRepository parametroRepository

  private List<TipoPago> listarTiposPagoRegistrados( Parametro parametro ) {
    List<TipoPago> results = tipoPagoRepository.findAll() ?: [ ]
    List<TipoPago> resultadosTmp = new ArrayList<TipoPago>()
    List<TipoPago> resultados = new ArrayList<TipoPago>()
    String [] tipos = parametro.valor.split(',')
    for(int i=0;i<=tipos.length-1;i++){
      resultadosTmp.add(new TipoPago())
    }
    for(TipoPago pago : results){
      if(TAG_TIPO_PAGO_EFECTIVO.equalsIgnoreCase(pago.id.trim())){
        setTipoPago(0, pago, resultadosTmp)
      } else if(TAG_TIPO_PAGO_TARJETA_CREDITO.equalsIgnoreCase(pago.id.trim())){
          setTipoPago(1, pago, resultadosTmp)
      } else if(TAG_TIPO_PAGO_TARJETA_DEBITO.equalsIgnoreCase(pago.id.trim())){
          setTipoPago(2, pago, resultadosTmp)
      } else if(TAG_TIPO_PAGO_CUPON1.equalsIgnoreCase(pago.id.trim())){
          setTipoPago(3, pago, resultadosTmp)
      } else if(TAG_TIPO_PAGO_CUPON2.equalsIgnoreCase(pago.id.trim())){
          setTipoPago(4, pago, resultadosTmp)
      } else if(TAG_TIPO_PAGO_CUPON3.equalsIgnoreCase(pago.id.trim())){
          setTipoPago(5, pago, resultadosTmp)
      } else if(TAG_TIPO_PAGO_CUPON4.equalsIgnoreCase(pago.id.trim())){
            setTipoPago(6, pago, resultadosTmp)
      }else {
        resultadosTmp.add(pago)
      }
    }
    for(TipoPago pago : resultadosTmp){
      if(pago.id != null){
        resultados.add(pago)
      }
    }
    resultados.retainAll { TipoPago tipoPago ->
      StringUtils.isNotBlank( tipoPago?.id )
    }
    return resultados/*.sort { TipoPago tipoPago ->
      tipoPago.descripcion
    }*/
  }

  @Override
  TipoPago obtenerTipoPagoPorDefecto( ) {
    log.info( "obteniendo tipo pago por defecto" )
    return tipoPagoRepository.findOne( 'EFM' )
  }

  @Override
  List<TipoPago> listarTiposPago( ) {
    log.info( "listando tipos de pago" )
    return listarTiposPagoRegistrados()
  }

  @Override
  List<TipoPago> listarTiposPagoActivos( ) {
    log.info( "listando tipos de pago activos" )
    List<TipoPago> tiposPago = [ ]
    Parametro parametro = parametroRepository.findOne( TipoParametro.TIPO_PAGO.value )
    String[] valores = parametro?.valores
    log.debug( "obteniendo parametro de formas de pago activas id: ${parametro?.id} valores: ${valores}" )
    if ( valores.any() ) {
      List<TipoPago> resultados = listarTiposPagoRegistrados( parametro )
      log.debug( "tipos de pago existentes: ${resultados*.id}" )
      tiposPago = resultados.findAll { TipoPago tipoPago ->
        valores.contains( tipoPago?.id?.trim() )
      }
      log.debug( "tipos de pago obtenidos: ${tiposPago*.id}" )
    }
    return tiposPago
  }

  void setTipoPago( Integer position, TipoPago tipoPago, List<TipoPago> tiposPago ){
      tiposPago.get(position).setId(tipoPago.getId())
      tiposPago.get(position).setDescripcion(tipoPago.getDescripcion())
      tiposPago.get(position).setTipoSoi(tipoPago.getTipoSoi())
      tiposPago.get(position).setTipoCon(tipoPago.getTipoCon())
      tiposPago.get(position).setF1(tipoPago.getF1())
      tiposPago.get(position).setF2(tipoPago.getF2())
      tiposPago.get(position).setF3(tipoPago.getF3())
      tiposPago.get(position).setF4(tipoPago.getF4())
      tiposPago.get(position).setF5(tipoPago.getF5())
  }


  @Override
  TipoPago obtenerTipoPagosPorId( String idFormaPago ){
    return tipoPagoRepository.findOne( idFormaPago )
  }


    @Override
    List<TipoPago> listarTiposPagoActivosMultipago( ) {
        log.info( "listando tipos de pago activos para multipago" )
        List<TipoPago> tiposPago = [ ]
        List<TipoPago> tiposPagoTmp = [ ]
        Parametro parametro = parametroRepository.findOne( TipoParametro.TIPO_PAGO.value )
        String[] valores = parametro?.valores
        log.debug( "obteniendo parametro de formas de pago activas id: ${parametro?.id} valores: ${valores}" )
        if ( valores.any() ) {
            List<TipoPago> resultados = listarTiposPagoRegistrados( parametro )
            log.debug( "tipos de pago existentes: ${resultados*.id}" )
            tiposPagoTmp = resultados.findAll { TipoPago tipoPago ->
                valores.contains( tipoPago?.id?.trim() )
            }
            for(TipoPago tipo : tiposPagoTmp){
              if( tipo.id.startsWith('C') ){
                if(tipo.id.equalsIgnoreCase(TAG_TIPO_PAGO_CUPON1)){
                  tiposPago.add( tipo )
                }
              } else {
                tiposPago.add( tipo )
              }
            }
            log.debug( "tipos de pago obtenidos: ${tiposPago*.id}" )
        }
        return tiposPago
    }
}
