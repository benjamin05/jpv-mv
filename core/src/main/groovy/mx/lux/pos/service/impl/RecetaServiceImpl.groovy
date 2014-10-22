package mx.lux.pos.service.impl
import groovy.util.logging.Slf4j
import mx.lux.pos.model.*
import mx.lux.pos.repository.*
import mx.lux.pos.service.NotaVentaService
import mx.lux.pos.service.RecetaService
import mx.lux.pos.service.business.Registry
import org.hibernate.service.spi.ServiceException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

@Slf4j
@Service( "recetaService" )
@Transactional( readOnly = true )
class RecetaServiceImpl implements RecetaService {

  @Resource
  private RecetaRepository recetaRepository

    @Resource
  private NotaVentaService notaVentaService

    @Resource
    private DetalleNotaVentaRepository detalleNotaVentaRepository

   @Resource
   private ParametroRepository parametroRepository

    @Resource
    private ReimpresionRepository reimpresionRepository

    @Resource
    private AcuseRepository acuseRepository

    @Override
  @Transactional
  Receta guardarReceta( Receta receta ) {
     log.info( "guardando receta con folio: ${receta.folio}" )
     try {
      receta = recetaRepository.save( receta )
      return receta
     }catch(ex){
         log.info(ex)
        return null
     }
  }

    @Override
    @Transactional
    Receta findbyId(Integer idRx){

        try  {
            Receta receta = recetaRepository.findById(idRx)
           return receta

        }catch(ex){
            return null
        }
    }

    @Override
    @Transactional
    List<Receta> recetaCliente (Integer IdCliente){
        List<Receta> recetas = recetaRepository.findByIdCliente(IdCliente)

    }

    @Override
    @Transactional
    void generaAcuse(String orderID){
       NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(orderID)
       Receta rx = findbyId(notaVenta.receta)

        DetalleNotaVenta artArmazon = new DetalleNotaVenta()
        List<DetalleNotaVenta> articulos = detalleNotaVentaRepository.findByIdFactura(notaVenta?.id)
        Iterator iterator = articulos.iterator();
        while (iterator.hasNext()) {
            DetalleNotaVenta detalle = iterator.next()
            if(detalle?.articulo?.idGenerico.trim().equals('A')){
                artArmazon = detalle
            }
        }

        String trat = notaVenta?.udf2

        if(artArmazon?.surte.equals('P')){
            trat =trat+',SP'
        }

        Parametro reg_clases = parametroRepository.findOne('reg_clases')
        if(reg_clases.valor.equals('SI')){
           trat= trat + ',' +notaVenta?.cliente?.udf1
        }

        BigInteger primerTicket = reimpresionRepository.noReimpresiones(notaVenta?.factura).toInteger()
        String pTicket = ''
        if(primerTicket != 0){
            pTicket = primerTicket.toString()
        }

      String contenido = 'eje_dVal='+rx?.odEjeR
        contenido = contenido+'|cilindro_iVal='+rx?.oiCilR
        contenido = contenido+'|armazonVal='+artArmazon?.articulo?.articulo
        contenido = contenido+'|parVal=P'
        contenido = contenido+'|adicion_dVal='+rx?.odAdcR
        contenido = contenido+'|id_acuseVal='+(acuseRepository.nextIdAcuse().toInteger() + 1)
        contenido = contenido+'|esfera_iVal='+ rx?.oiEsfR
        contenido = contenido+'|prisma_d_hVal='+ rx?.odPrismaH
        contenido = contenido+'|sucursalVal='+ notaVenta?.sucursal.id
        contenido = contenido+'|distancia_cVal='+rx?.diCercaR
        contenido = contenido+'|archivoVal='+notaVenta?.sucursal?.id.toString()+ notaVenta?.factura + pTicket + 'RX'
        contenido = contenido+'|recetaVal='+notaVenta?.factura
        contenido = contenido+'|cilindro_dVal='+rx?.odCilR
        contenido = contenido+'|alturaVal='+ rx?.altOblR
        contenido = contenido+'|formaVal='+notaVenta?.udf3
        contenido = contenido+'|prisma_i_vVal='+rx?.oiPrismaV
        contenido = contenido+'|adicion_iVal='+  rx?.oiAdcR
        contenido = contenido+'|distancia_lVal='+ rx?.diLejosR
        contenido = contenido+'|tratamientosVal='+ trat
        contenido = contenido+'|observacionesVal='+ rx?.observacionesR+ ','+ notaVenta?.observacionesNv+',' + rx?.sUsoAnteojos
        contenido = contenido+',uso='+ rx?.sUsoAnteojos
        contenido = contenido+'|prisma_d_vVal='+ rx?.odPrismaV
        contenido = contenido+'|distancia_m_iVal='+  rx?.diOi
        contenido = contenido+'|distancia_m_dVal='+  rx?.diOd
        contenido = contenido+'|prisma_i_hVal='+ rx?.oiPrismaH
        contenido = contenido+'|esfera_dVal='+rx?.odEsfR
        contenido = contenido+'|codigoVal='+ notaVenta.codigo_lente
        contenido = contenido+'|eje_iVal='+ rx?.oiEjeR
        contenido = contenido+'|'
        String cont = contenido
        contenido= ''
        for (int x=0; x < cont.length(); x++) {
            if (cont.charAt(x) != ' ')
                contenido += cont.charAt(x)
        }
        contenido= contenido.replace('null','')

        Acuse acuseRx = new Acuse()
        acuseRx.contenido = contenido
        acuseRx.idTipo = 'RX'
        acuseRx.fechaCarga = new Date()
        acuseRx.intentos = 0

        acuseRepository.saveAndFlush(acuseRx)

        String contenido2 = ''+ notaVenta?.sucursal.id
        contenido2 = contenido2+'|'+notaVenta?.factura
        contenido2 = contenido2+'|'+ notaVenta.codigo_lente
        contenido2 = contenido2+'|'+rx?.odEsfR
        contenido2 = contenido2+'|'+rx?.odCilR
        contenido2 = contenido2+'|'+rx?.odEjeR
        contenido2 = contenido2+'|'+rx?.odAdcR
        contenido2 = contenido2+'|'+ rx?.odPrismaH
        contenido2 = contenido2+'|'+ rx?.odPrismaV
        contenido2 = contenido2+'|'+ rx?.oiEsfR
        contenido2 = contenido2+'|'+rx?.oiCilR
        contenido2 = contenido2+'|'+ rx?.oiEjeR
        contenido2 = contenido2+'|'+  rx?.oiAdcR
        contenido2 = contenido2+'|'+ rx?.oiPrismaH
        contenido2 = contenido2+'|'+rx?.oiPrismaV
        contenido2 = contenido2+'|'+ rx?.diLejosR
        contenido2 = contenido2+'|'+rx?.diCercaR
        contenido2 = contenido2+'|'+  rx?.diOd
        contenido2 = contenido2+'|'+  rx?.diOi
        contenido2 = contenido2+'|'+ rx?.altOblR
        contenido2 = contenido2+'|'+ trat
        contenido2 = contenido2+'|'+ rx?.observacionesR+ ','+ notaVenta?.observacionesNv+',' + 'uso=' +rx?.sUsoAnteojos
        contenido2 = contenido2+'|P'
        contenido2 = contenido2+'|'+notaVenta?.udf3
        contenido2 = contenido2+'|'+artArmazon?.articulo?.articulo
        contenido2 = contenido2+'|'+ rx?.odPrismaV
        contenido2 = contenido2+'|'+ rx?.oiPrismaV
        contenido2 = contenido2+'|'
        String cont2 = contenido2
        contenido2= ''
        for (int x=0; x < cont2.length(); x++) {
            if (cont2.charAt(x) != ' ')
                contenido2 += cont2.charAt(x)
        }
        contenido2= contenido2.replace('null','')
        generaArchivoEnvio(contenido2, notaVenta?.sucursal?.id.toString()+ notaVenta?.factura + pTicket + 'RX')
    }

    @Override
    private void generaArchivoEnvio( String contenido, String nombre) throws ServiceException {
            try {
                Parametro ruta = Registry.find(TipoParametro.RUTA_POR_ENVIAR)
                File archivo = new File( ruta?.valor, nombre.toString() )
                BufferedWriter out = new BufferedWriter( new FileWriter( archivo ) )
                out.write( contenido )
                out.close()
           } catch ( Exception e ) {
                e.printStackTrace()
            }
    }
}
