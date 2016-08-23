package mx.lux.pos.ui.controller

import mx.lux.pos.model.*
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.InventarioService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.model.LcViewMode
import mx.lux.pos.ui.model.adapter.InvTrFilter
import mx.lux.pos.ui.model.adapter.LcFilter
import mx.lux.pos.ui.model.adapter.LcRequestAdapter
import mx.lux.pos.ui.model.adapter.RequestAdapter
import mx.lux.pos.ui.resources.ServiceManager
import mx.lux.pos.ui.view.component.NavigationBar.Command
import mx.lux.pos.ui.view.dialog.InboundDialog
import mx.lux.pos.ui.view.dialog.LcSelectorDialog
import mx.lux.pos.ui.view.dialog.PartSelectionDialog
import mx.lux.pos.ui.view.panel.LcView
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.text.DateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class LcController {

  private static final Logger log = LoggerFactory.getLogger( LcController.class )

  private static LcController instance
  private PartSelectionDialog dlgPartSelection
  private LcSelectorDialog dlgSelector
  private JFileChooser dlgFile

  private static final String TAG_REMESA = 'ENTRADA'
  private static final String TAG_OTHER_RECEIP = 'OTRAS_ENTRADAS'
  private static final String TAG_REMESA_LENTE = 'A'

  private LcController( ) { }

  static LcController getInstance( ) {
    if ( instance == null ) {
      instance = new LcController()
    }
    return instance
  }

  // Initialize
  private JFileChooser getDialogFile( ) {
    if ( dlgFile == null ) {
      dlgFile = new JFileChooser()
    }
    return dlgFile
  }

  // Dispatch Actions
  protected void dispatchDocument( LcView pView, Shipment pDocument ) {
    log.debug( "[Controller] Dispatch receiving document" )
    pView.notifyDocument( pDocument )
  }

  protected void dispatchDocument( LcView pView, InvAdjustSheet pDocument ) {
    log.debug( "[Controller] Dispatch receiving document" )
    pView.notifyDocument( pDocument )
  }

  protected void dispatchDocumentEmpty( LcView pView, Boolean fileAlreadyProccessed, String articleNotFound ) {
    log.debug( "[Controller] Dispatch document unavailable" )
    LcController controller = this
    SwingUtilities.invokeLater( new Runnable() {
      public void run( ) {
        controller.dispatchViewModeQuery( pView )
        if( fileAlreadyProccessed ){
          pView.data.txtStatus = pView.panel.MSG_DOCUMENT_ALREADY_PROCCESED
        } else if( articleNotFound != '' ){
          pView.data.txtStatus = String.format( pView.panel.MSG_ARTICLE_NOT_FOUND, articleNotFound )
        } else {
          pView.data.txtStatus = pView.panel.MSG_NO_DOCUMENT_AVAILABLE
        }
        pView.fireRefreshUI()
      }
    } )
  }

  protected void dispatchPartMasterUpdate( Shipment pDocument ) {
    log.debug( "[Controller] Update Part Master with Parts in Receiving Document" )
    if ( ( pDocument != null ) && ( pDocument.partShadows.size() > 0 ) ) {
      ArticuloService partMaster = ServiceManager.partService

      partMaster.actualizarArticulosConSombra( pDocument.partShadows )
    }
  }

  protected void dispatchPartsSelected( LcView pView, List<PedidoLcDet> pPartList ) {
    for ( PedidoLcDet part in pPartList ) {
      pView.data.addPart( part )
    }
    pView.fireConsumePartSeed()
    pView.fireRefreshUI()
  }

  protected void dispatchPrintTransaction( String pTrType, Integer pTrNbr ) {
    TransInv tr = ServiceManager.inventoryService.obtenerTransaccion( pTrType, pTrNbr )
    if ( tr != null ) {
      ServiceManager.ticketService.imprimeTransInv( tr )
    }
  }
   /*
    protected static String replaceCharAt(String s, int pos, char c) {
        StringBuffer buf = new StringBuffer( s );
        buf.setCharAt( pos, c );
        return buf.toString( );
    }

    protected  String claveAleatoria(Integer sucursal, Integer folio) {
    String folioAux = "" + folio.intValue();
    String sucursalAux = "" + sucursal.intValue()
    String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    if (folioAux.size() < 4) {
        folioAux = folioAux?.padLeft( 4, '0' )
    }
    else {
        folioAux = folioAux.substring(0,4);
    }
    String resultado = sucursalAux?.padLeft( 3, '0' ) + folioAux


    for (int i = 0; i < resultado.size(); i++) {
        int numAleatorio = (int) (Math.random() * abc.size());
        if (resultado.charAt(i) == '0') {
            resultado = replaceCharAt(resultado, i, abc.charAt(numAleatorio))
        }
        else {
            int numero = Integer.parseInt ("" + resultado.charAt(i));
            numero = 10 - numero
            char diff = Character.forDigit(numero, 10);
            resultado = replaceCharAt(resultado, i, diff)
        }


    }
    return resultado;
  }

  protected String generaSalida( InvTrViewMode viewMode, InvTrView pView ) {
      String url = Registry.getURL( viewMode.trType.idTipoTrans );
      if ( StringUtils.trimToNull( url ) != null ) {
          User user = Session.get( SessionItem.USER ) as User

         String variable = pView.data.qryDataset.dataset[0].sucursal + '>' + pView.data.postSiteTo.id + '>' +
                          pView.data.postTrType.ultimoFolio + '>' +
                          claveAleatoria(pView.data.qryDataset.dataset[0].sucursal, pView.data.postTrType.ultimoFolio)  +
                          '>' + user.username + '>'

         for (int i = 0; i < pView.data.skuList.size(); i++) {
             variable += pView.data.skuList[i].part.id + '|'
         }
         url += String.format( '?arg=%s', URLEncoder.encode( String.format( '%s', variable ), 'UTF-8' ) )
         String response = url.toURL().text
         response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
         log.debug( "resultado solicitud: ${response}" )
         return response
      }
  }
       */

  protected String confirmaEntrada(LcViewMode viewMode, LcView pView){
      String url = Registry.getURLConfirmacion( viewMode.trType.idTipoTrans );
      if( TAG_OTHER_RECEIP.equalsIgnoreCase(viewMode.trType.idTipoTrans.trim()) ){
        PedidoLc pedidoLc = ServiceManager.orderService.actualizaFechaRecepcionPedidoLc( pView.data.claveCodificada )
        url = Registry.urlAcuseRecibidoLc
        url += String.format( '?folio=%s', StringUtils.trimToEmpty(pedidoLc.folio) )
          String response = ""
          ExecutorService executor = Executors.newFixedThreadPool(1)
          int timeoutSecs = 15
          final Future<?> future = executor.submit(new Runnable() {
              public void run() {
                  try {
                      response = url.toURL().text
                      response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
                      log.debug( "resultado solicitud: ${response}" )
                  } catch (Exception e) {
                      throw new RuntimeException(e)
                  }
              }
          })
          try {
              future.get(timeoutSecs, TimeUnit.SECONDS)
          }  catch (Exception e) {println e}

          String fichero = "${Registry.archivePath}/${Registry.currentSite}-${pedidoLc.id}.LCR"
          String ficheroDrop = "${Registry.archivePathDropbox}/${Registry.currentSite}-${pedidoLc.id}.LCR"
          Integer sucursal = Registry.currentSite
          log.debug( "Generando Fichero: ${ fichero }" )
          File file = new File( fichero )
          File fileDrop = new File( ficheroDrop )
          if ( file.exists() ) { file.delete() }
          log.debug( 'Creando archivo de Recepcion de LC' )
          PrintStream strOut = new PrintStream( file )
          PrintStream strOutDrop = new PrintStream( fileDrop )
          StringBuffer sb = new StringBuffer()
          sb.append("${sucursal}-${pedidoLc.id}|${pedidoLc.fechaRecepcion.format("dd-MM-yyyy")}|")
          strOut.println sb.toString()
          strOut.close()
          strOutDrop.println sb.toString()
          strOutDrop.close()
        return response
      } else if ( StringUtils.trimToNull( url ) != null ) {
        String variable = pView.data.claveCodificada + ">" + pView.data.postTrType.ultimoFolio
        url += String.format( '?arg=%s', URLEncoder.encode( String.format( '%s', variable ), 'UTF-8' ) )
        String response = url.toURL().text
        response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
        log.debug( "resultado solicitud: ${response}" )
        return response
      }
  }

  protected void dispatchViewModeAdjust( LcView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( LcViewMode.ADJUST )
    pView.fireDisplay()
  }

  protected void dispatchViewModeFileAdjust( LcView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( LcViewMode.FILE_ADJUST )
    pView.fireDisplay()
    this.requestAdjustFile( pView )
  }

  protected void dispatchViewModeIssue( LcView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.ISSUE )
    pView.data.postSiteTo = null
    pView.fireDisplay()
  }

  List<Sucursal> listaAlmacenes(){
      List<Sucursal> lstAlmacenes = ServiceManager.getInventoryService().listarAlmacenes()
      return lstAlmacenes
  }

    List<Sucursal> listaSoloSucursales(){
        List<Sucursal> lstAlmacenes = ServiceManager.getInventoryService().listarSoloSucursales()
        return lstAlmacenes
    }

  protected void dispatchViewModeQuery( LcView pView ) {
    pView.data.clear()
    pView.controller.dlgSelector = null
    pView.fireResetUI()
    pView.notifyViewMode( LcViewMode.QUERY )
    if ( pView.data.qryDataset.size > 0 ) {
      pView.data.qryInvTr = pView.data.qryDataset.first
    }
    pView.fireDisplay()
  }

  protected void dispatchViewModeReceipt( LcView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( LcViewMode.RECEIPT )
    pView.fireDisplay()
    //requestReceipt( pView )
  }

  protected void dispatchViewModeSendOrder( LcView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( LcViewMode.SEND_ORDER )
    pView.fireDisplay()
  }

  protected void dispatchViewModeReturn( LcView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( LcViewMode.RETURN )
    //pView.data.qryInvTr = pView.data.qryDataset.first
    pView.fireDisplay()
  }


  protected void dispatchViewModeOutBound( LcView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( LcViewMode.OUTBOUND )
    pView.data.postSiteTo = null
    pView.fireDisplay()
  }

  protected void dispatchViewModeInBound( LcView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( LcViewMode.INBOUND )
    pView.fireDisplay()
    requestInBound( pView )
  }
    // Actions started
  protected void fireChangeViewMode( LcView pView, LcViewMode pNewMode ) {
    Boolean confirmed = true
    if ( !pNewMode.equals( pView.data.viewMode ) ) {
      if ( pView.data.dirty ) {
        String msg = String.format( pView.panel.MSG_CONFIRM_TO_PROCEED, pNewMode.toString() )
        Integer confirm = JOptionPane.showConfirmDialog( pView.panel, msg, pView.panel.TXT_CONFIRM_TITLE,
            JOptionPane.YES_NO_OPTION )
        confirmed = ( confirm == JOptionPane.YES_OPTION )
      }
      if ( confirmed ) {
        if ( pNewMode.equals( LcViewMode.ISSUE ) ) {
          dispatchViewModeIssue( pView )
        } else if ( pNewMode.equals( LcViewMode.RECEIPT ) ) {
          dispatchViewModeReceipt( pView )
        } else if ( pNewMode.equals( LcViewMode.QUERY ) ) {
          dispatchViewModeQuery( pView )
        } else if ( pNewMode.equals( LcViewMode.ADJUST ) ) {
          dispatchViewModeAdjust( pView )
        } else if ( pNewMode.equals( LcViewMode.RETURN ) ) {
          dispatchViewModeReturn( pView )
        } else if ( pNewMode.equals( LcViewMode.FILE_ADJUST ) ) {
            dispatchViewModeFileAdjust( pView )
        } else if ( pNewMode.equals( LcViewMode.OUTBOUND) ) {
            dispatchViewModeOutBound( pView )
        } else if ( pNewMode.equals( LcViewMode.INBOUND) ) {
            dispatchViewModeInBound( pView )
        } else if ( pNewMode.equals( LcViewMode.SEND_ORDER ) ) {
            dispatchViewModeSendOrder( pView )
        }
      } else {
        pView.notifyViewModeChangeCancelled()
      }
    }
  }

  // Requests
  void requestAdjustFile( LcView pView ) {
    log.debug( "[Controller] Request Adjust File" )
    InvAdjustSheet document = null
    JFileChooser dialog = this.getDialogFile()
    dialog.currentDirectory = new File(SettingsController.instance.incomingPath)
    dialog.fileSelectionMode = JFileChooser.FILES_ONLY
    String filename = null
    int fileAction = getDialogFile().showOpenDialog( pView.panel )
    if ( fileAction == JFileChooser.APPROVE_OPTION ) {
      filename = dlgFile.getSelectedFile().absolutePath
      log.debug( String.format( "[Controller] File chosen: %s", filename ) )
      document = ServiceManager.getInventoryService().leerArchivoAjuste( filename )
    }
    if ( document != null ) {
      dispatchDocument( pView, document )
      pView.data.inFile = new File( filename )
      log.debug ( String.format('Adjust File: %s', document.headerToString()) )
    } else {
      dispatchDocumentEmpty( pView, false, '' )
      log.debug ( 'No document' )
    }

  }

  void requestItem( LcView pView, Command pCommand ) {
    log.debug( String.format( "[Controller] Navigate to <%s>", pCommand.toString() ) )
    pView.data.qryInvTr = pView.data.qryDataset.get( pCommand )
    pView.fireRefreshUI()
  }

  void requestNewSearch( LcView pView ) {
    log.debug( "[Controller] New Search" )
    pView.data.qryDataset.filter.dateFrom = new Date()
    pView.data.qryDataset.filter.dateTo = new Date()
    if ( dlgSelector == null ) {
      dlgSelector = new LcSelectorDialog( pView.data.qryDataset )
    }
    dlgSelector.activate()
    if ( pView.data.qryDataset.currentIndex == null ) {
      pView.data.qryInvTr = pView.data.qryDataset.first
    } else {
      pView.data.qryInvTr = pView.data.qryDataset.getCurrent()
    }
    pView.fireRefreshUI()
  }

  void requestPart( LcView pView ) {
      String[] part = pView.data.partSeed.split(',')
      log.debug( String.format( "[Controller] Request Part with seed <%s>", part[0] ) )
    String seed = part[0]
    List<Articulo> partList = ItemController.findPartsByQuery( seed, false )
    if(seed.startsWith('00')){
      seed = seed.replaceFirst("^0*", "")
    }
    if ( ( partList.size() == 0 ) && ( seed.length() > 6 ) ) {
      partList = ItemController.findPartsByQuery( seed.substring( 0, 6 ), false )
      if( partList.size() == 0 ){
          if( seed.contains(/$/) ){
              String[] inputTmp = seed.split(/\$/)
              if( seed.trim().contains(/$$/) ) {
                  seed = inputTmp[0]
              } else {
                  seed = inputTmp[0] + ',' + inputTmp[1].substring(0,3)
              }
            partList = ItemController.findPartsByQuery( seed.substring( 0, 6 ), false )
          } else {
              seed = part[0]
              partList = ItemController.findPartsByQuery( seed.substring( 0, 6 ), false )
          }
      }
    }
    if ( partList?.any() ) {
      if ( partList.size() == 1 )  {
        if( partList.first().cantExistencia <= 0 && pView.data.viewMode.trType.tipoMov.trim().equalsIgnoreCase('S') ){
          Integer question =JOptionPane.showConfirmDialog( new JDialog(), pView.panel.MSG_NO_STOCK, pView.panel.TXT_NO_STOCK,
                  JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE )
          if( question == 0){
            dispatchPartsSelected( pView, partList )
          } else {
            pView.panel.stock = false
          }
        } else {
          dispatchPartsSelected( pView, partList )
        }

      } else {
        if ( dlgPartSelection == null ) {
          dlgPartSelection = new PartSelectionDialog( pView.panel )
        }
        dlgPartSelection.setItems( partList )
        dlgPartSelection.setSeed( seed )
        if ( LcViewMode.ADJUST.equals( pView.data.viewMode ) ) {
          dlgPartSelection.multiSelection = false
        }
        dlgPartSelection.activate()
        List<Articulo> selection = dlgPartSelection.getSelection()
        if ( selection != null ) {
          if( selection.first().cantExistencia <= 0 && pView.data.viewMode.trType.tipoMov.trim().equalsIgnoreCase('S') ){
              Integer question =JOptionPane.showConfirmDialog( new JDialog(), pView.panel.MSG_NO_STOCK, pView.panel.TXT_NO_STOCK,
                      JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE )
              if( question == 0){
                  dispatchPartsSelected( pView, selection )
              } else {
                pView.panel.stock = false
              }
          } else {
            log.debug( String.format( "[Controller] %d Selected, (%d) %s", selection.size(), selection[ 0 ].id, selection[ 0 ].descripcion ) )
            dispatchPartsSelected( pView, selection )
          }
        }
      }
    } else {
      JOptionPane.showMessageDialog( pView.panel, String.format( pView.panel.MSG_NO_RESULTS_FOUND, seed ),
          String.format( pView.panel.TXT_QUERY_TITLE, seed ), JOptionPane.INFORMATION_MESSAGE )
    }
  }



  void requestOrder( LcView pView ) {
    String order = StringUtils.trimToEmpty(pView.data.partSeed)
    PedidoLc pedidoLc = ServiceManager.requestService.obtenerPedidoPoridPedido( order )
    if( pedidoLc != null ){
      NotaVenta nota = ServiceManager.orderService.obtenerNotaVentaPorTicket(StringUtils.trimToEmpty("${Registry.currentSite.toString()}-${pedidoLc.id}"))
      if( nota != null ){
        pView.data.cliente = StringUtils.trimToEmpty(nota.cliente.nombreCompleto)
      }
      pView.data.folio = StringUtils.trimToEmpty(pedidoLc.folio)
      pView.data.claveCodificada = order
      dispatchPartsSelected( pView, new ArrayList<PedidoLcDet>(pedidoLc.pedidoLcDets) )
    }
  }


  void requestReceipt( LcView pView ) {
    log.debug( "[Controller] Request Receipt" )
    Shipment document = null
      /*int fileAction = getDialogFile().showOpenDialog( pView.panel )
      if ( fileAction == JFileChooser.APPROVE_OPTION ) {
        log.debug( String.format( "[Controller] File chosen: %s", dlgFile.getSelectedFile().absolutePath ) )
        document = ServiceManager.getInventoryService().leerArchivoRemesa( dlgFile.getSelectedFile().absolutePath )
      }
      if ( document != null ) {
        InventarioService service = ServiceManager.inventoryService
        List<TransInv> trList = service.listarTransaccionesPorTipoAndReferencia( LcViewMode.RECEIPT.trType.idTipoTrans,
                document?.fullRef?.trim() )
        if( trList.size() <= 0 ){
            dispatchPartMasterUpdate( document )
            dispatchDocument( pView, document )
        } else {
            dispatchDocumentEmpty( pView, true, '' )
        }
      } else {*/
      dispatchDocumentEmpty( pView, false, '' )
    //}

  }


  void requestInBound( LcView pView ) {
    log.debug( "[Controller] Request Inbound" )
    Shipment document = null
      InboundDialog inboundDialog = new InboundDialog()
      inboundDialog.activate()
      log.debug(inboundDialog.getTxtClave())
      if (inboundDialog.button) {
          Boolean claveNoCargada = ServiceManager.inventoryService.transaccionCargada( inboundDialog.getTxtClave() )
              pView.data.claveCodificada = inboundDialog.getTxtClave()
              Sucursal sucursal = ServiceManager.inventoryService.sucursalActual()
              log.debug("" + sucursal.id)
              document = ServiceManager.getInventoryService().obtieneArticuloEntrada(inboundDialog.getTxtClave(),sucursal.id, pView.data.viewMode.trType.idTipoTrans)
              Boolean articleExist = true
              String articles = ''
                if( document != null ){
                    for(ShipmentLine line : document.lines ){
                        if(line.partCode == null || line.partCode == ''){
                            articleExist = false
                            articles = articles+"["+line.sku.toString()+"]"+" "
                        }
                    }
                }
               if ( document != null && !claveNoCargada && articleExist ) {
                  dispatchPartMasterUpdate( document )
                  dispatchDocument( pView, document )
              } else {
                   dispatchDocumentEmpty( pView, false, articles )
              }
      }
    }

  void requestSaveAndPrint( LcView pView ) {
    log.debug( "[Controller] Save and Print" )
    LcRequest request = LcRequestAdapter.getRequest( pView.data )
    if ( request != null ) {
        request.remarks = request.remarks.replaceAll("[^a-zA-Z0-9]+"," ");
      Integer trNbr = ServiceManager.getInventoryService().solicitarTransaccionLc( request )
      if ( trNbr != null ) {
        if(request.trType.equalsIgnoreCase(TAG_REMESA)){
          String receivedPath = Registry.processedFilesPath
          String[] filename = pView.controller.dlgFile.selectedFile.path.split("/")
          String[] filePathTmp = pView.controller.dlgFile.selectedFile.path.split("/")
          String filePath = ''
          for(int i=0;i < filePathTmp.length-1;i++){
            filePath = filePath+'/'+filePathTmp[i]
          }
          filePath = filePath.replaceFirst("/","")
          def file = new File( filePath, filename.last() )
          log.debug( "archivo de carga: ${filename.last()} en: ${filePath} - ${file?.exists()}" )
          def newFile = new File( receivedPath, filename.last() )
          def moved = file.renameTo( newFile )
          log.debug( "renombrando archivo a: ${newFile.path} - ${moved}" )
        }
        if ( pView.data.inFile != null ) {
          try {
            File moved = new File( SettingsController.instance.processedPath, pView.data.inFile.name )
            if (LcViewMode.OUTBOUND.equals( viewMode )) {
                pView.data.inFile.delete();
            }
            else {
                pView.data.inFile.renameTo( moved )
            }
          } catch (Exception e) {
            this.log.debug( e.getMessage() )
          }
        }
        LcViewMode viewMode = pView.data.viewMode
        if ( LcViewMode.ISSUE.equals( viewMode ) || LcViewMode.ADJUST.equals( viewMode )
            || LcViewMode.RETURN.equals( viewMode ) || LcViewMode.FILE_ADJUST.equals( viewMode )
            || LcViewMode.RECEIPT.equals( viewMode ) || LcViewMode.OUTBOUND.equals( viewMode )
            || LcViewMode.INBOUND.equals( viewMode )) {
          dispatchPrintTransaction( viewMode.trType.idTipoTrans, trNbr )
          if (LcViewMode.RECEIPT.equals( viewMode )) {
            String resultado = confirmaEntrada(viewMode, pView)
          }
          if( ServiceManager.getInventoryService().isReceiptDuplicate() ){
            dispatchPrintTransaction( viewMode.trType.idTipoTrans, trNbr )
          }
        }
        pView.fireResetUI()
        pView.data.clear()
        if ( LcViewMode.RECEIPT.equals( viewMode ) || LcViewMode.FILE_ADJUST.equals( viewMode ) ) {
          LcController controller = this
          SwingUtilities.invokeLater( new Runnable() {
            void run( ) {
              pView.uiDisabled = true
              controller.dispatchViewModeQuery( pView )
              LcFilter filter = pView.data.qryDataset.getFilter()
              filter.reset()
              filter.setDateRange( new Date() )
              pView.data.qryDataset.requestTransactions( false )
              pView.data.txtStatus = pView.panel.MSG_TRANSACTION_POSTED
              pView.fireRefreshUI()
              pView.uiDisabled = false
            }
          } )
        } else {
          pView.data.txtStatus = pView.panel.MSG_TRANSACTION_POSTED
          pView.fireRefreshUI()
        }
      } else {
        JOptionPane.showMessageDialog( pView.panel, pView.panel.MSG_POST_FAILED, pView.panel.TXT_POST_TITLE, JOptionPane.ERROR_MESSAGE )
      }
    } else {
      log.debug( "[Controller] Request not available" )
    }
  }

  void requestPrint( String pIdTipoTrans, Integer pTrNbr ) {
    log.debug( "[Controller] Print Transaction" )
    dispatchPrintTransaction( pIdTipoTrans, pTrNbr )
  }

  void requestViewModeChange( LcView pView ) {
    log.debug( String.format( "[Controller] View Mode change: <%s>", pView.panel.comboViewMode.selection ) )
    fireChangeViewMode( pView, pView.panel.comboViewMode.selection )
  }

  void requestPrintTransactions( Date fechaTicket ){
    log.debug( "requestPrintTransactions" )
    ServiceManager.ticketService.imprimeTransaccionesInventario( fechaTicket )
  }

}

