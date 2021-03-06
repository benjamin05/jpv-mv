package mx.lux.pos.service.impl

import mx.lux.pos.java.querys.AcusesQuery
import mx.lux.pos.java.querys.NotaVentaQuery
import mx.lux.pos.java.repository.AcusesJava
import mx.lux.pos.java.repository.DetalleNotaVentaJava
import mx.lux.pos.java.repository.NotaVentaJava
import mx.lux.pos.java.repository.PagoJava
import mx.lux.pos.model.*
import mx.lux.pos.repository.*
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.IOService
import mx.lux.pos.service.business.*
import mx.lux.pos.service.io.AsynchronousNotificationDispatcher
import mx.lux.pos.util.CustomDateUtils
import mx.lux.pos.util.FileFilterUtil
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional( readOnly = true )
class IOServiceImpl implements IOService {

  private static final String MSG_PART_CLASS_FILE_LOAD = 'Importar Clasificacion de Articulos'
  private static final String MSG_PART_CLASS_FILE_LOADED = 'Clasif de Articulos  Registros:%,d  Actualizados: %,d'

  private static final String TAG_ACK_SALES = 'venta'
  private static final String TAG_ACK_REMITTANCES = 'REM'
  private static final String TAG_ACK_ADJUST = AckType.MODIF_VENTA

  private static final String TAG_ESTADO_REM_CARGADA = 'cargado'

  private static IOServiceImpl instance

  private Logger logger = LoggerFactory.getLogger( this.getClass() )

  @Autowired
  MonedaRepository currMaster

  @Autowired
  MonedaDetalleRepository currRateDetail

  ArticuloClassReaderTask classReader = new ArticuloClassReaderTask()

  IOServiceImpl( ) {
    instance = this
  }

  static IOService getInstance( ) {
    return instance
  }

  String getPartFilename( ) {
    return Registry.partMasterFile
  }

  void loadPartFile( ) {
    ArticuloSunglassImportTask task = new ArticuloSunglassImportTask()
    try {
      task.filename = Registry.partMasterFile
      task.run()
      File f = new File( Registry.partMasterFile )
      if ( f.exists() ) {
        f.renameTo( new File( Registry.processedFilesPath, f.name ) )
      }
    } catch ( Exception e ) {
      this.logger.error( String.format( "Error loading %s", Registry.partMasterFile ), e )
    }
  }

  void loadPartFile( File pFile ) {
    ArticuloSunglassImportTask task = new ArticuloSunglassImportTask()
    try {
      task.filename = pFile.absolutePath
      task.run()
      if ( pFile.exists() ) {
        pFile.renameTo( new File( Registry.processedFilesPath, pFile.name ) )
      }
    } catch ( Exception e ) {
      this.logger.error( String.format( "Error loading %s", pFile.getAbsolutePath() ), e )
    }
  }

  String getPartClassFilename( ) {
    return classReader.getFilename()
  }

  Map<String, Object> loadPartClassFile( File pFile  ) {
    logger.debug( MSG_PART_CLASS_FILE_LOAD )
    Map<String, Object> taskSummary = new HashMap<String, Object>()
    classReader.run( pFile )
    taskSummary.put( 'numFiles', classReader.fileCount )
    taskSummary.put( 'filename', classReader.filename )
    taskSummary.put( 'records', classReader.linesRead )
    taskSummary.put( 'items', classReader.partCount )
    taskSummary.put( 'updates', classReader.partUpdatedCount )
    logger.debug( String.format( MSG_PART_CLASS_FILE_LOADED, classReader.linesRead, classReader.partUpdatedCount ) )

    File source = new File( classReader.filename )
    File destination = new File( Registry.getParametroOS("ruta_recibidos") )
    if ( destination.exists() ) {
        def newFile = new File( destination, source.name )
        source.renameTo( newFile )
    }
    return taskSummary
  }

  File getIncomingLocation( ) {
    return new File( Registry.inputFilePath )
  }

  FilenameFilter getPartMasterFilter( ) {
    String regex = FileFilterUtil.toRegExpLowerCase( Registry.productsFilePattern )
    LocalFilenameFilter filter = new LocalFilenameFilter( regex )
    return filter
  }

  File getArchiveLocation( ) {
    return new File( Registry.processedFilesPath )
  }

  FilenameFilter getAllFilesFilter( ) {
    return LocalFilenameFilter.ALL_FILES
  }

  String getProductsFilePattern( ) {
    return Registry.productsFilePattern
  }

  String getClasificationsFilePattern( ) {
      return Registry.clasificationFilePattern
  }

  String getEmployeeFilePattern( ) {
    return Registry.employeeFilePattern
  }

  String getFxRatesFilePattern( ) {
    return Registry.fxRatesFilePattern
  }

  Map<String, Object> loadEmployeeFile( File pInputFile ) {
    Map<String, Object> taskSummary = new HashMap<String, Object>()
    EmployeeImportTask task = new EmployeeImportTask()
    task.setInputFile( pInputFile )
    task.run()
    taskSummary.put( 'filename', pInputFile.getAbsolutePath() )
    taskSummary.put( 'records', task.getReadCount() )
    taskSummary.put( 'updates', task.getUpdatedCount() )
    if ( pInputFile.exists() ) {
      pInputFile.renameTo( new File( Registry.processedFilesPath, pInputFile.name ) )
    }
    return taskSummary
  }

  Map<String, Object> loadFxRatesFile( File pInputFile ) {
    Map<String, Object> taskSummary = new HashMap<String, Object>()
    FxRatesImportTask task = new FxRatesImportTask()
    task.setInputFile( pInputFile )
    task.run()
    taskSummary.put( 'filename', pInputFile.getAbsolutePath() )
    taskSummary.put( 'records', task.getReadCount() )
    taskSummary.put( 'updates', task.getUpdatedCount() )
    if ( pInputFile.exists() ) {
      pInputFile.renameTo( new File( Registry.processedFilesPath, pInputFile.name ) )
    }
    return taskSummary
  }

  @Transactional
  void logSalesNotification( String pIdFactura ) {
    NotaVentaJava order = NotaVentaQuery.busquedaNotaById( pIdFactura )
    if ( order != null ) {
      logger.debug( String.format( 'Notify Sales[Order:%s  Date:%s  Amount:%,.2f', order.idFactura,
          CustomDateUtils.format( order.fechaHoraFactura ), order.ventaTotal ) )
      String strItemList = ''
      for ( DetalleNotaVentaJava det : order.detalles ) {
        strItemList += String.format( "%s,%s~", det?.articulo?.articulo?.trim(), det?.articulo?.colorCode?.trim() )
      }
      String strPaymentList = ''
      for ( PagoJava p : order.pagos ) {
        strPaymentList += StringUtils.trimToEmpty( p.idFPago ) + ',' + String.format( '%.2f', p.montoPago ) + '~'
      }
      AcusesJava acuse = new AcusesJava()
      acuse.idTipo = TAG_ACK_SALES
      acuse.fechaCarga = new Date()
      try {
        acuse = AcusesQuery.saveAcuses( acuse )
        logger.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.idAcuse, acuse.idTipo, acuse.contenido ) )
      } catch ( Exception e ) {
        logger.error( e.getMessage() )
      }
      //acuse.contenido = String.format( 'ImporteVal=%s|', URLEncoder.encode( String.format( '%.2f', order.ventaNeta ), 'UTF-8' ) )
      acuse.contenido = String.format( 'ImporteVal=%s|', String.format( '%.2f', order.ventaNeta ) )
      acuse.contenido += String.format( 'articulosVal=%s|', strItemList )
      acuse.contenido += String.format( 'fechaVal=%s|', CustomDateUtils.format(order.fechaHoraFactura, 'ddMMyyyy') )
      acuse.contenido += String.format( 'id_acuseVal=%s|', String.format( '%d', acuse.idAcuse ) )
      acuse.contenido += String.format( 'id_clienteVal=%s|', String.format( '%d', order.idCliente ) )
      acuse.contenido += String.format( 'id_facturaVal=%s|', order.factura.trim() )
      acuse.contenido += String.format( 'id_sucVal=%s|', String.format( '%d', order.idSucursal ) )
      acuse.contenido += String.format( 'no_soiVal=%s|', order.idFactura.trim() )
      acuse.contenido += String.format( 'pagosVal=%s|', strPaymentList )
      acuse.fechaCarga = new Date()
      try {
        acuse = AcusesQuery.saveAcuses( acuse )
        logger.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.idAcuse, acuse.idTipo, acuse.contenido ) )
      } catch ( Exception e ) {
        logger.error( e.getMessage() )
      }
    }
  }

  @Transactional
  void logAdjustmentNotification( Integer pIdMod ) {
    ModificacionRepository adjustments = RepositoryFactory.adjustments
    Modificacion adjustment = adjustments.findOne( pIdMod )
    if ( adjustment != null ) {
      logger.debug( String.format( 'Notify Adjustment[Adjust:%d %s  Order:%s  Date:%s', adjustment.id, adjustment.tipo,
          adjustment.idFactura, CustomDateUtils.format( adjustment.fecha ) ) )
      NotaVentaRepository orders = RepositoryFactory.orders
      NotaVenta order = orders.findOne( adjustment.idFactura )
      AcuseRepository acuses = RepositoryFactory.acknowledgements
      Acuse acuse = new Acuse()
      acuse.idTipo = TAG_ACK_ADJUST
      try {
        acuse = acuses.saveAndFlush( acuse )
        logger.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
      } catch ( Exception e ) {
        logger.error( e.getMessage() )
      }
      acuse.contenido = String.format( 'id_acuse=%s', URLEncoder.encode( String.format( '%d', acuse.id ), 'UTF-8' ) )
      acuse.contenido += String.format( '&id_suc=%s', URLEncoder.encode( String.format( '%d', order.idSucursal ), 'UTF-8' ) )
      acuse.contenido += String.format( '&no_soi=%s', URLEncoder.encode( adjustment.idFactura, 'UTF-8' ) )
      acuse.contenido += String.format( '&id_factura=%s', URLEncoder.encode( order.factura, 'UTF-8' ) )
      acuse.contenido += String.format( '&fecha=%s', URLEncoder.encode( CustomDateUtils.format( adjustment.fecha, 'ddMMyyyy' ), 'UTF-8' ) )
      acuse.fechaCarga = new Date()
      try {
        acuse = acuses.saveAndFlush( acuse )
        logger.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
      } catch ( Exception e ) {
        logger.error( e.getMessage() )
      }
    }
  }

  void startAsyncNotifyDispatcher( ) {
    AsynchronousNotificationDispatcher dispatcher = AsynchronousNotificationDispatcher.getInstance()
    Thread t = new Thread( dispatcher, dispatcher.name )
    t.setDaemon( true )
    t.start()
    logger.debug( String.format( 'Thread started: %s', t.name ) )
  }

  @Transactional
  void saveAcknowledgement( Acuse pAcknowledgement ) {
    RepositoryFactory.acknowledgements.saveAndFlush( pAcknowledgement )
  }


  @Transactional
  void saveActualDate( String date ) {
      Parametro paramFecha = RepositoryFactory.registry.findOne(TipoParametro.FECHA_PRIMER_ARRANQUE.value)
      paramFecha.valor = date
      RepositoryFactory.registry.save( paramFecha )
  }


  @Transactional
  void logRemittanceNotification( String idTipoTrans, Integer folio, String codigo, Remesas remesa ) {
      TransInvRepository transactionRep = RepositoryFactory.inventoryMaster
      TipoTransInvRepository tipoTransactionRep = RepositoryFactory.trTypes
      QTipoTransInv tipoTrans = QTipoTransInv.tipoTransInv
      TipoTransInv tipoTransInv = tipoTransactionRep.findOne( idTipoTrans )
      QTransInv trans = QTransInv.transInv
      TransInv transInv = transactionRep.findOne(trans.idTipoTrans.eq(idTipoTrans).and(trans.folio.eq(tipoTransInv.ultimoFolio)))
      if ( transInv != null ) {
          AcuseRepository acuses = RepositoryFactory.acknowledgements
          Acuse acuse = new Acuse()
          acuse.idTipo = TAG_ACK_REMITTANCES
          try {
              acuse = acuses.saveAndFlush( acuse )
              logger.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
          } catch ( Exception e ) {
              logger.error( e.getMessage() )
          }
          String referencia = transInv.referencia.substring(0,6)
          String tipo = transInv.referencia.substring( transInv.referencia.length()-1 )
          println referencia
          //acuse.contenido = String.format( 'ImporteVal=%s|', URLEncoder.encode( String.format( '%.2f', order.ventaNeta ), 'UTF-8' ) )
          acuse.contenido = String.format( 'sistemaVal=%s|', tipo.trim() )
          acuse.contenido += String.format( 'id_sucVal=%s|', transInv.sucursal.toString().trim() )
          acuse.contenido += String.format( 'horaVal=%s|', CustomDateUtils.format(transInv.fechaMod, 'HH:mm') )
          acuse.contenido += String.format( 'doctoVal=%s|', String.format( '%s', StringUtils.trimToEmpty(remesa?.idDocto)) )
          acuse.contenido += String.format( 'id_acuseVal=%s|', String.format( '%d', acuse.id ) )
          acuse.contenido += String.format( 'transaVal=%s|', String.format( '%s', StringUtils.trimToEmpty(remesa?.docto) ) )
          try {
              acuse = acuses.saveAndFlush( acuse )
              logger.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
          } catch ( Exception e ) {
              logger.error( e.getMessage() )
          }
      }
  }


  Remesas updateRemesa( String idTipoTrans ){
    Remesas remesa = new Remesas()
    TransInvRepository transactionRep = RepositoryFactory.inventoryMaster
    TipoTransInvRepository tipoTransactionRep = RepositoryFactory.trTypes
    QTipoTransInv tipoTrans = QTipoTransInv.tipoTransInv
    TipoTransInv tipoTransInv = tipoTransactionRep.findOne( idTipoTrans )
    QTransInv trans = QTransInv.transInv
    TransInv transInv = transactionRep.findOne(trans.idTipoTrans.eq(idTipoTrans).and(trans.folio.eq(tipoTransInv.ultimoFolio)))
    if ( transInv != null ) {
        RemesasRepository repo = RepositoryFactory.remittanceRepository
        QRemesas rem = QRemesas.remesas
        List<Remesas> lstRemesas = repo.findAll( rem.clave.eq(transInv.referencia.trim()) ) as List<Remesas>
        if(lstRemesas.size() > 0){
          for(Remesas rem1 : lstRemesas){
            rem1.estado = TAG_ESTADO_REM_CARGADA
            rem1.fecha_carga = new Date()
            rem1 = repo.save( rem1 )
            repo.flush()
            remesa = rem1
          }
        } else {
          String sistema = ''
          TransInvDetalleRepository transInvDetalleRepository = RepositoryFactory.inventoryDetail
          List<TransInvDetalle> detalles = transInvDetalleRepository.findByIdTipoTransAndFolio(StringUtils.trimToEmpty(transInv.idTipoTrans), transInv.folio) as List<TransInvDetalle>
          for( TransInvDetalle det : detalles ){
            ArticuloRepository articuloRepository = RepositoryFactory.partMaster
            Articulo articulo = articuloRepository.findOne(det.sku)
            if( articulo != null ){
              sistema = StringUtils.trimToEmpty(articulo.idGenerico)
            }
          }
          String letra = sistema.equalsIgnoreCase("A") ? "I" : "RAC"
          remesa = new Remesas()
          remesa.idTipoDocto = "RN"
          remesa.idDocto = letra+StringUtils.trimToEmpty(transInv.referencia).substring(0,6)
          remesa.docto = StringUtils.trimToEmpty(transInv.referencia).substring(0,6)
          remesa.clave = StringUtils.trimToEmpty(transInv.referencia)
          remesa.letra = letra
          remesa.archivo = "4."+StringUtils.trimToEmpty(Registry.currentSite.toString())+".REM."+StringUtils.trimToEmpty(transInv.referencia)
          remesa.articulos = detalles.size()
          remesa.sistema = sistema
          remesa.fecha_recibido = new Date()
          remesa.estado = TAG_ESTADO_REM_CARGADA
          remesa.fecha_carga = new Date()
          remesa = repo.save( remesa )
          repo.flush()
        }
        Integer idSuc = Registry.currentSite
        ParametroRepository repoParam = RepositoryFactory.registry
        String rutaPorEnviar = Registry.archivePath.trim()
        File file = new File( "${rutaPorEnviar}/4.${idSuc}.REM.${remesa.clave}.ACU" )
        PrintStream strOut = new PrintStream( file )
        StringBuffer sb = new StringBuffer()
        sb.append("${idSuc}|REM|${remesa.docto}|")
        sb.append( "\n" )
        sb.append("${remesa.fecha_carga.format('dd/MM/yyyy')}|${remesa.fecha_carga.format('HH:mm')}|${remesa.letra.trim()}${remesa.docto.trim()}|${remesa.sistema}|")
        strOut.println sb.toString()
        strOut.close()
        logger.debug(file.absolutePath)
    }
    return remesa
  }


}
