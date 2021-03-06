package mx.lux.pos.ui.controller

import mx.lux.pos.java.querys.DoctoInvQuery
import mx.lux.pos.java.querys.JbQuery
import mx.lux.pos.java.querys.NotaVentaQuery
import mx.lux.pos.java.repository.ArticulosJava
import mx.lux.pos.java.repository.DetalleNotaVentaJava
import mx.lux.pos.java.repository.DoctoInvJava
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.java.repository.NotaVentaJava
import mx.lux.pos.model.*
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.InventarioService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.service.impl.ServiceFactory
import mx.lux.pos.ui.model.InvTr
import mx.lux.pos.ui.model.InvTrSku
import mx.lux.pos.ui.model.InvTrViewMode
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.User
import mx.lux.pos.ui.model.adapter.InvTrFilter
import mx.lux.pos.ui.model.adapter.RequestAdapter
import mx.lux.pos.ui.resources.ServiceManager
import mx.lux.pos.ui.view.component.NavigationBar.Command
import mx.lux.pos.ui.view.dialog.AuthorizationAuditDialog
import mx.lux.pos.ui.view.dialog.InboundDialog
import mx.lux.pos.ui.view.dialog.InvTrSelectorDialog
import mx.lux.pos.ui.view.dialog.PartSelectionDialog
import mx.lux.pos.ui.view.dialog.ReceiptDialog
import mx.lux.pos.ui.view.panel.InvTrView
import mx.lux.pos.util.CustomDateUtils
import mx.lux.pos.util.StringList
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.nio.channels.FileChannel
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class InvTrController {

  private static final Logger log = LoggerFactory.getLogger( InvTrController.class )

  private static InvTrController instance
  private PartSelectionDialog dlgPartSelection
  private InvTrSelectorDialog dlgSelector
  private JFileChooser dlgFile

  private static final String TAG_REMESA = 'ENTRADA'
  private static final String TAG_SALIDA_TIENDA = 'SALIDA_TIENDA'
  private static final String TAG_REMESA_LENTE = 'A'

  private InvTrController( ) { }

  static InvTrController getInstance( ) {
    if ( instance == null ) {
      instance = new InvTrController()
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
  protected void dispatchDocument( InvTrView pView, Shipment pDocument ) {
    log.debug( "[Controller] Dispatch receiving document" )
    pView.notifyDocument( pDocument )
  }

  protected void dispatchDocument( InvTrView pView, InvAdjustSheet pDocument ) {
    log.debug( "[Controller] Dispatch receiving document" )
    pView.notifyDocument( pDocument )
  }

  protected void dispatchDocumentEmpty( InvTrView pView, Boolean fileAlreadyProccessed, String articleNotFound ) {
    log.debug( "[Controller] Dispatch document unavailable" )
    InvTrController controller = this
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

  protected void dispatchPartsSelected( InvTrView pView, List<Articulo> pPartList ) {
    for ( Articulo part in pPartList ) {
      pView.data.addPart( part )
    }
    pView.fireConsumePartSeed()
    pView.fireRefreshUI()
  }

  protected void dispatchPartsJavaSelected( InvTrView pView, List<ArticulosJava> pPartList ) {
    for ( ArticulosJava part in pPartList ) {
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

  protected String confirmaEntrada(InvTrViewMode viewMode, InvTrView pView){
      String url = Registry.getURLConfirmacion( viewMode.trType.idTipoTrans );
      if( TAG_REMESA.equalsIgnoreCase(viewMode.trType.idTipoTrans.trim()) ){
        Remesas remesa = ServiceManager.getIoServices().updateRemesa( viewMode.trType.idTipoTrans.trim() )
        ServiceManager.getIoServices().logRemittanceNotification( viewMode.trType.idTipoTrans.trim(), viewMode.trType.ultimoFolio+1, pView.data.receiptDocument.code, remesa )
      } else if ( StringUtils.trimToNull( url ) != null ) {
        String variable = ""
        if( InvTrType.INBOUND.trType.equalsIgnoreCase(viewMode.trType.idTipoTrans) ){
          variable = StringUtils.trimToEmpty(pView.data.claveCodificada)
        } else {
          variable = pView.data.claveCodificada + ">" + pView.data.postTrType.ultimoFolio
        }
        url += String.format( '?arg=%s', URLEncoder.encode( String.format( '%s', variable ), 'UTF-8' ) )
        log.debug( "url: ${url}" )
        String response = url.toURL().text
        response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
        log.debug( "resultado solicitud: ${response}" )
        return response
      }
  }


  protected void generaAcuseAjusteInventario(File inFile){
    String[] dataFileName = inFile.name.split(/\./)
    String newFileName = ""
    if( dataFileName.length >= 3 ){
      newFileName = dataFileName[0]+"."+dataFileName[1]+"."+"aja"
    } else {
      newFileName = inFile.name
    }
    File deleted = new File( SettingsController.instance.processedPath, inFile.name )
    FileChannel source = null;
    FileChannel destination = null;
      source = new FileInputStream(inFile).getChannel();
      destination = new FileOutputStream(deleted).getChannel();
      if (destination != null && source != null) {
          destination.transferFrom(source, 0, source.size());
      }
      if (source != null) {
          source.close();
      }
      if (destination != null) {
          destination.close();
      }


    File moved = new File( Registry.archivePath, newFileName )
    inFile.renameTo( moved )
  }


  protected void dispatchViewModeAdjust( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.ADJUST )
    pView.fireDisplay()
  }

  protected void dispatchViewModeFileAdjust( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.FILE_ADJUST )
    pView.fireDisplay()
    this.requestAdjustFile( pView )
  }

  protected void dispatchViewModeIssue( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.ISSUE )
    pView.data.postSiteTo = null
    pView.fireDisplay()
  }

  protected void dispatchViewModeMassiveReceipt( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.MASSIVE_RECEIPT )
    pView.data.postSiteTo = null
    pView.fireDisplay()
  }

  protected void dispatchViewModeIssueFrames( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.ISSUE_FRAMES )
    pView.data.postSiteTo = null
    pView.fireDisplay()
    this.requestIssueFrames( pView )
  }


  protected void dispatchViewModeIssueAccesories( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.ISSUE_ACCESORIES )
    pView.data.postSiteTo = null
    pView.fireDisplay()
    this.requestIssueAccesories( pView )
  }


  protected void dispatchViewModeOtherIssue( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.OTHER_ISSUE )
    pView.data.postSiteTo = null
    pView.fireDisplay()
  }

  List<Sucursal> listaAlmacenes(){
      List<Sucursal> lstAlmacenes = ServiceManager.getInventoryService().listarAlmacenes()
      return lstAlmacenes
  }

    List<Sucursal> listaSoloSucursales(){
        List<Sucursal> lstAlmacenes = ServiceManager.getInventoryService().listarSoloSucursales()
        Sucursal sucursalVacia = new Sucursal()
        sucursalVacia.centroCostos = ""
        sucursalVacia.nombre = ""
        sucursalVacia.id = -1
        lstAlmacenes.add( 0, sucursalVacia )
        return lstAlmacenes
    }

    List<Sucursal> listaSoloAlmacenPorAclarar( Integer id ){
        List<Sucursal> lstAlmacenes = ServiceManager.getInventoryService().listarSoloAlmacenPorAclarar( id )
        return lstAlmacenes
    }

  protected void dispatchViewModeQuery( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.QUERY )
    if ( pView.data.qryDataset.size > 0 ) {
      pView.data.qryInvTr = pView.data.qryDataset.first
    }
    pView.fireDisplay()
  }

  protected void dispatchViewModeReceipt( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.RECEIPT )
    pView.fireDisplay()
    requestReceipt( pView )
  }

  protected void dispatchViewModeOtherReceipt( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.OTHER_RECEIPT )
    pView.fireDisplay()
  }

  protected void dispatchViewModeReturn( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.RETURN )
    pView.fireDisplay()
  }


  protected void dispatchViewModeOutBound( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.OUTBOUND )
    pView.data.postSiteTo = null
    pView.fireDisplay()
  }

  protected void dispatchViewModeInBound( InvTrView pView ) {
    pView.data.clear()
    pView.fireResetUI()
    pView.notifyViewMode( InvTrViewMode.INBOUND )
    pView.fireDisplay()
    requestInBound( pView )
  }
    // Actions started
  protected void fireChangeViewMode( InvTrView pView, InvTrViewMode pNewMode ) {
    Boolean confirmed = true
    if ( !pNewMode.equals( pView.data.viewMode ) ) {
      if ( pView.data.dirty ) {
        String msg = String.format( pView.panel.MSG_CONFIRM_TO_PROCEED, pNewMode.toString() )
        Integer confirm = JOptionPane.showConfirmDialog( pView.panel, msg, pView.panel.TXT_CONFIRM_TITLE,
            JOptionPane.YES_NO_OPTION )
        confirmed = ( confirm == JOptionPane.YES_OPTION )
      }
      if ( confirmed ) {
        if ( pNewMode.equals( InvTrViewMode.ISSUE ) || pNewMode.equals( InvTrViewMode.ISSUE_ACCESORIES ) ||
                pNewMode.equals( InvTrViewMode.ISSUE_FRAMES ) ) {
          dispatchViewModeIssue( pView )
        } else if ( pNewMode.equals( InvTrViewMode.RECEIPT ) ) {
          dispatchViewModeReceipt( pView )
        } else if ( pNewMode.equals( InvTrViewMode.MASSIVE_RECEIPT ) ) {
          dispatchViewModeMassiveReceipt( pView )
        } else if ( pNewMode.equals( InvTrViewMode.QUERY ) ) {
          dispatchViewModeQuery( pView )
        } else if ( pNewMode.equals( InvTrViewMode.ADJUST ) ) {
          dispatchViewModeAdjust( pView )
        } else if ( pNewMode.equals( InvTrViewMode.RETURN ) ) {
          dispatchViewModeReturn( pView )
        } else if ( pNewMode.equals( InvTrViewMode.FILE_ADJUST ) ) {
          dispatchViewModeFileAdjust( pView )
        } else if ( pNewMode.equals( InvTrViewMode.OUTBOUND) ) {
          dispatchViewModeOutBound( pView )
        } else if ( pNewMode.equals( InvTrViewMode.INBOUND) ) {
          dispatchViewModeInBound( pView )
        } else if ( pNewMode.equals( InvTrViewMode.OTHER_ISSUE ) ) {
          dispatchViewModeOtherIssue( pView )
        } else if ( pNewMode.equals( InvTrViewMode.OTHER_RECEIPT ) ) {
          dispatchViewModeOtherReceipt( pView )
        } else if ( pNewMode.equals( InvTrViewMode.ISSUE_FRAMES ) ) {
          dispatchViewModeIssueFrames( pView )
        } else if ( pNewMode.equals( InvTrViewMode.ISSUE_ACCESORIES ) ) {
          dispatchViewModeIssueAccesories( pView )
        }
      } else {
        pView.notifyViewModeChangeCancelled()
      }
    }
  }

  // Requests
  void requestAdjustFile( InvTrView pView ) {
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


  void requestIssueFrames( InvTrView pView ) {
    log.debug( "[Controller] Request Issue Frames" )
    InvAdjustSheet document = null
    document = ServiceManager.getInventoryService().obtenerArmazones( )
    if ( document != null ) {
      for(InvAdjustLine doc : document.lines){
        pView.data.postQty = doc.qty
        pView.controller.requestPartTotasIssue( pView, doc.sku )
      }
      pView.driver.refreshUI( pView )
    } else {
      dispatchDocumentEmpty( pView, false, '' )
      log.debug ( 'No frames' )
    }
  }


  void requestIssueAccesories( InvTrView pView ) {
    log.debug( "[Controller] Request Issue Accesories" )
    InvAdjustSheet document = null
    document = ServiceManager.getInventoryService().obtenerAccesorios()
    if ( document != null ) {
      for(InvAdjustLine doc : document.lines){
        pView.data.postQty = doc.qty
        pView.controller.requestPartTotasIssue( pView, doc.sku )
      }
      pView.driver.refreshUI( pView )
    } else {
      dispatchDocumentEmpty( pView, false, '' )
      log.debug ( 'No frames' )
    }
  }



  void requestItem( InvTrView pView, Command pCommand ) {
    log.debug( String.format( "[Controller] Navigate to <%s>", pCommand.toString() ) )
    pView.data.qryInvTr = pView.data.qryDataset.get( pCommand )
    pView.fireRefreshUI()
  }

  void requestNewSearch( InvTrView pView ) {
    log.debug( "[Controller] New Search" )
    if ( dlgSelector == null ) {
      dlgSelector = new InvTrSelectorDialog( pView.data.qryDataset )
    }
    dlgSelector.activate()
    if ( pView.data.qryDataset.currentIndex == null ) {
      pView.data.qryInvTr = pView.data.qryDataset.first
    } else {
      pView.data.qryInvTr = pView.data.qryDataset.getCurrent()
    }
    pView.fireRefreshUI()
  }

  void requestPart( InvTrView pView ) {
    String[] part = pView.data.partSeed.split(',')
      /*if( pView.data.viewMode.equals(InvTrViewMode.ADJUST) && part.size() > 1 ){
        Integer qty = 1
        try{
          qty = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(part[1]))
          pView.data.postQty = qty
        } catch ( ParseException e ){
          println e.message
        }
      }*/
    log.debug( String.format( "[Controller] Request Part with seed <%s>", part[0] ) )
    String seed = part[0]
    List<Articulo> partList = ItemController.findPartsByQuery( seed, true )
    if( partList.size() == 0 ){
      if( seed.contains("!") ){
        String[] inputTmp = seed.split("!")
        seed = inputTmp[0]
          Integer id = 0
          try{
            id = NumberFormat.getInstance().parse( StringUtils.trimToEmpty(seed) )
          } catch ( NumberFormatException e ){
            println e.message
          }
        Articulo art = ItemController.findArticle( id )
        if( art != null ){
          partList.add( art )
        }
      }
      Boolean oneSign = false
      if( seed.contains(/$/) ){
        String[] inputTmp = seed.split(/\$/)
        if( seed.trim().contains(/$$/) ) {
          seed = inputTmp[0]
        } else {
          seed = inputTmp[0] + ',' + inputTmp[1].substring(0,3)
          oneSign = true
        }
        partList = ItemController.findPartsByQuery( seed, true )
      }
      if( !partList?.any() && oneSign ){
        String[] inputTmp = seed.split(",")
        seed = StringUtils.trimToEmpty(inputTmp[0])+"*"
        partList = ItemController.findPartsByQuery( seed, true )
      }
    }
    if(seed.startsWith('00')){
      seed = seed.replaceFirst("^0*", "")
    }
    if ( ( partList.size() == 0 ) && ( seed.length() > 6 ) ) {
      partList = ItemController.findPartsByQuery( seed.substring( 0, 6 ), true )
      if( partList.size() == 0 ){
          if( seed.contains(/$/) ){
              String[] inputTmp = seed.split(/\$/)
              if( seed.trim().contains(/$$/) ) {
                  seed = inputTmp[0]
              } else {
                  seed = inputTmp[0] + ',' + inputTmp[1].substring(0,3)
              }
            partList = ItemController.findPartsByQuery( seed, true )
          } else {
              seed = part[0]
              partList = ItemController.findPartsByQuery( seed.substring( 0, 6 ), true )
          }
      }
    }
    /*if ( partList?.any() && partList.size() == 1 )  {
      if( StringUtils.trimToEmpty(partList?.first()?.codigoColor).length() <= 0 ){
        partList.clear()
      }
    }*/
    if ( partList?.any() ) {
      if ( partList.size() == 1 )  {
        Boolean valid = false
        String genericosTmp = Registry.validGenericsByOtherTrans
        String[] genericos = genericosTmp.split(",")
        if( (pView.data.viewMode.equals(InvTrViewMode.OTHER_ISSUE) || pView.data.viewMode.equals(InvTrViewMode.OTHER_RECEIPT)) ){
          for(String gen : genericos){
            if( StringUtils.trimToEmpty(gen).equalsIgnoreCase(StringUtils.trimToEmpty(partList.first().idGenerico)) ){
              valid = true
            }
          }
        } else {
          for(String gen : genericos){
            if( !StringUtils.trimToEmpty(gen).equalsIgnoreCase(StringUtils.trimToEmpty(partList.first().idGenerico)) ){
              valid = true
            }
          }
          //valid = true
        }
        if(valid){
            if( partList.first().cantExistencia <= 0 && pView.data.viewMode.trType.tipoMov.trim().equalsIgnoreCase('S') ){
              if( pView.data.viewMode.equals(InvTrViewMode.ISSUE)){
                JOptionPane.showMessageDialog( null, "Articulo sin existencia.","Error", JOptionPane.ERROR_MESSAGE )
                pView.panel.stock = false
              } else {
                Integer question =JOptionPane.showConfirmDialog( new JDialog(), pView.panel.MSG_NO_STOCK, pView.panel.TXT_NO_STOCK,
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE )
                if( question == 0){
                  dispatchPartsSelected( pView, partList )
                } else {
                  pView.panel.stock = false
                }

              }
            } else {
                dispatchPartsSelected( pView, partList )
            }
        } else {
            JOptionPane.showMessageDialog( pView.panel, String.format( pView.panel.MSG_NO_VALID_FOUND, seed ),
                    String.format( pView.panel.TXT_QUERY_TITLE, seed ), JOptionPane.INFORMATION_MESSAGE )
        }
      } else {
        if ( dlgPartSelection == null ) {
          dlgPartSelection = new PartSelectionDialog( pView.panel )
        }
        List<Articulo> partListTmp = new ArrayList<>()
        if( pView.data.viewMode.equals(InvTrViewMode.OUTBOUND) || pView.data.viewMode.equals(InvTrViewMode.ISSUE) ||
                pView.data.viewMode.equals(InvTrViewMode.ADJUST) ){
          for(Articulo art : partList){
            if( StringUtils.trimToEmpty(art.codigoColor).length() > 0 ){
              partListTmp.add(art)
            }
          }
        }
        dlgPartSelection.setItems( partListTmp.size() > 0 ? partListTmp : partList )
        dlgPartSelection.setSeed( seed )
        if ( InvTrViewMode.ADJUST.equals( pView.data.viewMode ) ) {
          dlgPartSelection.multiSelection = false
        }
        dlgPartSelection.activate()
        List<Articulo> selection = dlgPartSelection.getSelection()
        if ( selection != null ) {
          Boolean valid = false
          String genericosTmp = Registry.validGenericsByOtherTrans
          String[] genericos = genericosTmp.split(",")
            if( (pView.data.viewMode.equals(InvTrViewMode.OTHER_ISSUE) || pView.data.viewMode.equals(InvTrViewMode.OTHER_RECEIPT)) ){
                for(String gen : genericos){
                    if( StringUtils.trimToEmpty(gen).equalsIgnoreCase(StringUtils.trimToEmpty(selection.first().idGenerico)) ){
                        valid = true
                    }
                }
            } else {
              for(String gen : genericos){
                if( !StringUtils.trimToEmpty(gen).equalsIgnoreCase(StringUtils.trimToEmpty(partListTmp.size() > 0 ? partListTmp.first().idGenerico : partList.first().idGenerico)) ){
                  valid = true
                }
              }
                //valid = true
            }
          if( valid ){
              if( selection.first().cantExistencia <= 0 && pView.data.viewMode.trType.tipoMov.trim().equalsIgnoreCase('S') ){
                if( pView.data.viewMode.equals(InvTrViewMode.ISSUE) ){
                  JOptionPane.showMessageDialog( null, "Articulo sin existencia.","Error", JOptionPane.ERROR_MESSAGE )
                  pView.panel.stock = false
                } else {
                  Integer question =JOptionPane.showConfirmDialog( new JDialog(), pView.panel.MSG_NO_STOCK, pView.panel.TXT_NO_STOCK,
                          JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE )
                  if( question == 0){
                    dispatchPartsSelected( pView, selection )
                  } else {
                    pView.panel.stock = false
                  }
                }
              } else {
                  log.debug( String.format( "[Controller] %d Selected, (%d) %s", selection.size(), selection[ 0 ].id, selection[ 0 ].descripcion ) )
                  dispatchPartsSelected( pView, selection )
              }
          } else {
              JOptionPane.showMessageDialog( pView.panel, String.format( pView.panel.MSG_NO_VALID_FOUND, seed ),
                      String.format( pView.panel.TXT_QUERY_TITLE, seed ), JOptionPane.INFORMATION_MESSAGE )
          }
        }
      }
    } else {
      JOptionPane.showMessageDialog( pView.panel, String.format( pView.panel.MSG_NO_RESULTS_FOUND, seed ),
          String.format( pView.panel.TXT_QUERY_TITLE, seed ), JOptionPane.INFORMATION_MESSAGE )
    }
  }


  void requestPartTotasIssue( InvTrView pView, Integer idArticle ){
    List<Articulo> partList = new ArrayList<>()
    Articulo art = ItemController.findArticle( idArticle )
    if( art != null ){
      partList.clear()
      partList.add( art )
      dispatchPartsSelected( pView, partList )
    }
  }


  void requestReceipt( InvTrView pView ) {
    log.debug( "[Controller] Request Receipt" )
    Shipment document = null
    //int fileAction = getDialogFile().showOpenDialog( pView.panel )
    ReceiptDialog receiptDialog = new ReceiptDialog()
    receiptDialog.show()
    log.debug(receiptDialog.getTxtClave())
    if ( StringUtils.trimToEmpty(receiptDialog.getTxtClave()).length() > 0 ) {
      String path = Registry.getInputFilePath()
      File source = new File( path )
      File rem = null
      source.eachFile { file ->
        String[] fileName = file.getName().split(/\./)
        String reference = ""
        reference = StringUtils.trimToEmpty(fileName.last())
        if ( reference.equalsIgnoreCase( StringUtils.trimToEmpty(receiptDialog.getTxtClave()) ) ) {
          rem = file
          dlgFile = new JFileChooser()
          dlgFile.setSelectedFile( rem )
        }
      }
      if( rem != null ){
        log.debug( String.format( "[Controller] File chosen: %s", rem.absolutePath ) )
        document = ServiceManager.getInventoryService().leerArchivoRemesa( rem.absolutePath )
      }
    }
    if ( document != null ) {
      InventarioService service = ServiceManager.inventoryService
      List<TransInv> trList = service.listarTransaccionesPorTipoAndReferencia( InvTrViewMode.RECEIPT.trType.idTipoTrans,
              document?.fullRef?.trim() )
      if( trList.size() <= 0 ){
          dispatchPartMasterUpdate( document )
          dispatchDocument( pView, document )
      } else {
          dispatchDocumentEmpty( pView, true, '' )
      }
    } else {
      dispatchDocumentEmpty( pView, false, '' )
    }

  }

  void requestOtherReceipt( InvTrView pView ) {
    log.debug( "[Controller] Other Request Receipt" )
    dispatchPartMasterUpdate( document )
    dispatchDocument( pView, document )
  }

  void requestInBound( InvTrView pView ) {
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

  void requestSaveAndPrint( InvTrView pView ) {
    log.debug( "[Controller] Save and Print" )
    InvTrRequest request = RequestAdapter.getRequest( pView.data )
    Boolean value = false
    if( TAG_SALIDA_TIENDA.equalsIgnoreCase(StringUtils.trimToEmpty(request?.trType)) ){
        AuthorizationAuditDialog authDialog = new AuthorizationAuditDialog(pView.panel, "Esta operacion requiere autorizaci\u00f3n")
        authDialog.show()
        if (authDialog.authorized) {
          value = true
        } else {
            OrderController.notifyAlert('Se requiere autorizacion para esta operacion', 'Se requiere autorizacion para esta operacion')
        }
    } else {
      value = true
    }
    if ( request != null && value ) {
        request.remarks = request.remarks.replaceAll("[^a-zA-Z0-9]+"," ");
      Integer trNbr = ServiceManager.getInventoryService().solicitarTransaccion( request )
      if ( trNbr != null ) {
        if(request.trType.equalsIgnoreCase(TAG_REMESA) && pView.controller.dlgFile != null){
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
            if (InvTrViewMode.OUTBOUND.equals( viewMode )) {
                pView.data.inFile.delete();
            }
            else {
                pView.data.inFile.renameTo( moved )
            }
          } catch (Exception e) {
            this.log.debug( e.getMessage() )
          }
        }
        InvTrViewMode viewMode = pView.data.viewMode
        if ( InvTrViewMode.ISSUE.equals( viewMode ) || InvTrViewMode.ADJUST.equals( viewMode )
            || InvTrViewMode.RETURN.equals( viewMode ) || InvTrViewMode.FILE_ADJUST.equals( viewMode )
            || InvTrViewMode.RECEIPT.equals( viewMode ) || InvTrViewMode.OUTBOUND.equals( viewMode )
            || InvTrViewMode.INBOUND.equals( viewMode ) || InvTrViewMode.OTHER_ISSUE.equals( viewMode )
            || InvTrViewMode.OTHER_RECEIPT.equals( viewMode ) || InvTrViewMode.ISSUE_ACCESORIES.equals( viewMode )
            || InvTrViewMode.ISSUE_FRAMES.equals( viewMode ) || InvTrViewMode.MASSIVE_RECEIPT.equals( viewMode )) {
          dispatchPrintTransaction( viewMode.trType.idTipoTrans, trNbr )
          if (InvTrViewMode.RECEIPT.equals( viewMode ) || InvTrViewMode.INBOUND.equals( viewMode )) {
            String resultado = confirmaEntrada(viewMode, pView)
          }
          if( InvTrViewMode.ADJUST.equals( viewMode ) ){
            Boolean generatedAcuse = true
            for(InvTrSku sku : pView.data.skuList){
              if( StringUtils.trimToEmpty(sku.part.idGenerico).equalsIgnoreCase("E") ){
                generatedAcuse = false
              }
            }
            if( generatedAcuse ){
              generaAcuseAjusteInventario( pView, trNbr )
            }
          }
          if( ServiceManager.getInventoryService().isReceiptDuplicate() ){
            dispatchPrintTransaction( viewMode.trType.idTipoTrans, trNbr )
          }
        }
        pView.fireResetUI()
        pView.data.clear()
        if ( InvTrViewMode.RECEIPT.equals( viewMode ) || InvTrViewMode.FILE_ADJUST.equals( viewMode ) ) {
          InvTrController controller = this
          SwingUtilities.invokeLater( new Runnable() {
            void run( ) {
              pView.uiDisabled = true
              controller.dispatchViewModeQuery( pView )
              InvTrFilter filter = pView.data.qryDataset.filter
              filter.reset()
              filter.setDateRange( new Date() )
              pView.data.qryDataset.requestTransactions()
              pView.data.txtStatus = pView.panel.MSG_TRANSACTION_POSTED
              pView.fireRefreshUI()
              pView.uiDisabled = false
            }
          } )
        } else {
          pView.data.txtStatus = pView.panel.MSG_TRANSACTION_POSTED
          pView.fireRefreshUI()
        }
      } else if( request.siteTo < 0 ){
        JOptionPane.showMessageDialog( pView.panel, pView.panel.MSG_SITE_FAILED, pView.panel.TXT_POST_TITLE, JOptionPane.ERROR_MESSAGE )
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

  void requestViewModeChange( InvTrView pView ) {
    log.debug( String.format( "[Controller] View Mode change: <%s>", pView.panel.comboViewMode.selection ) )
    fireChangeViewMode( pView, pView.panel.comboViewMode.selection )
  }

  void requestPrintTransactions( Date fechaTicket ){
    log.debug( "requestPrintTransactions" )
    ServiceManager.ticketService.imprimeTransaccionesInventario( fechaTicket )
  }

  Integer generatedIssueFile( InvTrView pView ){
    log.debug( "generatedIssueFile" )
    InvTrRequest request = RequestAdapter.getRequest( pView.data )
      return ServiceManager.getInventoryService().generaArchivoSalida(request)
  }

  Boolean showQueryTransaction( InvTrView pView ){
      InvTrController controller = this
      SwingUtilities.invokeLater( new Runnable() {
          void run( ) {
              pView.uiDisabled = true
              controller.dispatchViewModeQuery( pView )
              InvTrFilter filter = pView.data.qryDataset.filter
              filter.reset()
              filter.setDateRange( new Date() )
              pView.data.qryDataset.requestTransactions()
              pView.data.txtStatus = pView.panel.MSG_TRANSACTION_POSTED
              pView.fireRefreshUI()
              pView.uiDisabled = false
          }
      } )
  }


  void readAutIssueFile(){
    //SimpleDateFormat df = new SimpleDateFormat("HH")
    Integer maxHour = Registry.getMaximunHourToReadIssueFile()
    Date topDate = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH )
    Calendar cal = Calendar.getInstance()
    cal.setTime(topDate)
    cal.add(Calendar.HOUR, maxHour)
    topDate = cal.getTime()
    Boolean valid = true
    String term = StringUtils.trimToEmpty(Registry.terminalCaja)
    String ip = Registry.ipCurrentMachine()
    if( term.length() > 0 ){
      if( StringUtils.trimToEmpty(term).length() > 0 && (!term.contains(ip) || ip.length() <= 0) ){
        valid = false
          println "IP caja: ${term}"
        println "IP de maquina: ${ip}"
        println "IP valida procesa archivos de autorizacion de salida"
      }
    }
    if( new Date().compareTo(topDate) <= 0 && valid ){
      println "IP caja: ${term}"
      println "IP de maquina: ${ip}"
      println "IP valida procesa archivos de autorizacion de salida"
      ServiceManager.getInventoryService().leerArchivoAutorizacionSalidas( )
      readDevolutionFile( )
    }
  }

  void reprintAutIssue(){
    ServiceManager.getInventoryService().leerArchivoAutorizacionSalidas( )
  }


  void readDevolutionFile(){
    Parametro ubicacion = Registry.find( TipoParametro.RUTA_POR_RECIBIR )
    Parametro parametro = RepositoryFactory.registry.findOne( TipoParametro.RUTA_RECIBIDOS.value )
    String ubicacionSource = ubicacion.valor
    String ubicacionsDestination = parametro.valor
    File source = new File( ubicacionSource )
    File destination = new File( ubicacionsDestination )
    if ( source.exists() && destination.exists() ) {
      source.eachFile() { file ->
        String[] dataName = StringUtils.trimToEmpty(file.getName()).split(/\./)
        if ( dataName.length >= 5 && dataName[3].equalsIgnoreCase( "DS" ) ) {
          InvTr data = new InvTr()
          InvAdjustSheet document = new InvAdjustSheet()
          String filename = null
          file.eachLine { String line ->
            String[] linea = StringUtils.trimToEmpty(line).split(/\|/)
            if( linea.length == 2 ){
              document.ref = linea[0]
            } else if( linea.length >= 6 ){
              document.trReason = linea[4]
              InvAdjustLine det = new InvAdjustLine()
              try{
                det.sku = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(linea[5])).intValue()
                det.qty = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(linea[3])).intValue()
              } catch ( NumberFormatException e ){
                println e.message
              }
              det.partCode = linea[0]
              det.colorCode = linea[1]
              det
              document.lines.add( det )
            }
          }
          filename = file.absolutePath
          String filenm = file.name
          String[] strFile = StringUtils.trimToEmpty(filenm).split(/\./)
          log.debug( String.format( "[Controller] File found: %s", filename ) )
          Sucursal suc = new Sucursal()
          suc.id = 0
          data.inFile = new File( filename )
          data.postRemarks = strFile.last()+","+document.trReason
          data.postReference = document.ref
          data.postSiteTo = suc
          data.postTrType = RepositoryFactory.trTypes.findOne("SALIDA")
          Integer contador = 1
          for ( InvAdjustLine det in document.lines ) {
            Articulo part = ServiceManager.partService.obtenerArticulo( det.sku, false )
            if( part.cantExistencia-det.qty >= 0 ){
              data.skuList.add( new InvTrSku( data, contador, part, det.qty ) )
              contador = contador+1
            } else if( part.cantExistencia > 0 ){
              det.qty = part.cantExistencia
              data.skuList.add( new InvTrSku( data, contador, part, det.qty ) )
              contador = contador+1
            }
          }
          InvTrRequest request = RequestAdapter.getRequest( data )
          //request.remarks = request.remarks.replaceAll("[^a-zA-Z0-9]+"," ");
          Integer trNbr = null
          if( validReference(StringUtils.trimToEmpty(data.postTrType.idTipoTrans), StringUtils.trimToEmpty(data.postReference)) ){
            trNbr = ServiceManager.getInventoryService().solicitarTransaccion( request )

          }
          if ( trNbr != null ) {
            List<TransInv> lstTrans = RepositoryFactory.inventoryMaster.findByIdTipoTransAndReferencia( data.postTrType.idTipoTrans, StringUtils.trimToEmpty(data.postReference) )
            List<TransInvDetalle> lstTransDet = RepositoryFactory.inventoryDetail.findByIdTipoTransAndFolio( data.postTrType.idTipoTrans,
                    lstTrans.size() > 0 ? lstTrans.first().folio : 0 )
            ServiceManager.getInventoryService().registraDoctoInv( lstTransDet )
            dispatchPrintTransaction( data.postTrType.idTipoTrans, trNbr )
            def newFile = new File( destination, file.name )
            def moved = file.renameTo( newFile )
          }
        }
      }
    }
  }


  /*Boolean validReference( String idTipoTrans, String ref ){
    List<TransInv> lstTrans = RepositoryFactory.inventoryMaster.findByIdTipoTransAndReferencia( idTipoTrans, ref )
    if( lstTrans.size() > 0 ){
      return false
    } else {
      return true
    }
  }*/


  void readAdjutFile(){
    Parametro ubicacion = Registry.find( TipoParametro.RUTA_POR_RECIBIR )
    Parametro parametro = RepositoryFactory.registry.findOne( TipoParametro.RUTA_RECIBIDOS.value )
    String ubicacionSource = ubicacion.valor
    String ubicacionsDestination = parametro.valor
    File source = new File( ubicacionSource )
    File destination = new File( ubicacionsDestination )
    if ( source.exists() && destination.exists() ) {
      source.eachFile() { file ->
        if ( file.getName().endsWith( ".ajs" ) ) {
          InvTr data = new InvTr()
          InvAdjustSheet document = null
          String filename = null
          filename = file.absolutePath
          log.debug( String.format( "[Controller] File found: %s", filename ) )
          document = ServiceManager.getInventoryService().leerArchivoAjuste( filename )
          data.inFile = new File( filename )
          data.postRemarks = document.trReason
          data.postReference = document.ref
          data.postSiteTo = null
          data.postTrType = RepositoryFactory.trTypes.findOne("AJUSTE")
          Integer contador = 1
          for ( InvAdjustLine det in document.lines ) {
            Articulo part = ServiceManager.partService.obtenerArticulo( det.sku, false )
            data.skuList.add( new InvTrSku( data, contador, part, det.qty ) )
            contador = contador+1
          }
          InvTrRequest request = RequestAdapter.getRequest( data )
          request.remarks = request.remarks.replaceAll("[^a-zA-Z0-9]+"," ");
          Integer trNbr = null
          if( validReference(StringUtils.trimToEmpty(data.postTrType.idTipoTrans), StringUtils.trimToEmpty(data.postReference)) ){
            trNbr = ServiceManager.getInventoryService().solicitarTransaccion( request )
          }
          if ( trNbr != null ) {
            //File moved = new File( SettingsController.instance.processedPath, data.inFile.name )
            //data.inFile.renameTo( moved )
            generaAcuseAjusteInventario(data.inFile)
            dispatchPrintTransaction( data.postTrType.idTipoTrans, trNbr )
          }
        }
      }
    }
  }


  Boolean validReference( String idTipoTrans, String ref ){
    List<TransInv> lstTrans = RepositoryFactory.inventoryMaster.findByIdTipoTransAndReferencia( idTipoTrans, ref )
    if( lstTrans.size() > 0 ){
      return false
    } else {
      return true
    }
  }


  void automaticIssue( String idOrder, Boolean isTransfer ){
    NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById(idOrder)
    Boolean doProcess = Registry.activeDevCanOft()
    if( notaVenta != null && doProcess ){
      JbJava jb = JbQuery.buscarPorRx( StringUtils.trimToEmpty(notaVenta.factura) )
      Boolean estatusValid = false
      if( isTransfer ){
        List<mx.lux.pos.java.repository.JbTrack> lstJbTrack = JbQuery.buscarJbTrackPorRx(notaVenta.factura)
        if(lstJbTrack.size() > 1){
          Collections.sort(lstJbTrack, new Comparator<mx.lux.pos.java.repository.JbTrack>() {
            @Override
            int compare(mx.lux.pos.java.repository.JbTrack o1, mx.lux.pos.java.repository.JbTrack o2) {
              return o2.fecha.compareTo(o1.fecha)
            }
          })
          for(mx.lux.pos.java.repository.JbTrack jbTrack : lstJbTrack){
            if( jbTrack != null && (StringUtils.trimToEmpty(jbTrack.estado).equalsIgnoreCase("RS") ||
                    StringUtils.trimToEmpty(jbTrack.estado).equalsIgnoreCase("TE"))){
              estatusValid = true
            }
          }
        }
      } else {
        if( jb != null && (StringUtils.trimToEmpty(jb.estado).equalsIgnoreCase("RS") ||
                StringUtils.trimToEmpty(jb.estado).equalsIgnoreCase("TE"))){
          estatusValid = true
        }
      }
      Boolean hasLen = false
      Boolean hasFrame = false
      Integer idFrame = 0
      Integer quantity = 0
      for(DetalleNotaVentaJava det : notaVenta.detalles){
        if(StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase("A")){
          hasFrame = true
          idFrame = det.idArticulo
          quantity = quantity+det.cantidadFac.intValue()
        } else if(StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase("B")){
          hasLen = true
        }
      }
      if( hasFrame && hasLen & estatusValid ){
        InvTr data = new InvTr()
        data.postRemarks = "CANCELACION DE OFTALMICO FACTURA ${StringUtils.trimToEmpty(notaVenta.factura)}"
        data.postReference = StringUtils.trimToEmpty(notaVenta.factura)
        data.postSiteTo = null
        data.postTrType = RepositoryFactory.trTypes.findOne("SALIDA")
        Articulo part = ServiceManager.partService.obtenerArticulo( idFrame, false )
        data.skuList.add( new InvTrSku( data, 1, part, quantity ) )
        InvTrRequest request = RequestAdapter.getRequest( data )
        request.remarks = request.remarks.replaceAll("[^a-zA-Z0-9]+"," ");
        Integer trNbr = null
        trNbr = ServiceManager.getInventoryService().solicitarTransaccion( request )
        if ( trNbr != null ) {
          DoctoInv doctoInv = new DoctoInv()
          doctoInv.idDocto = StringUtils.trimToEmpty(trNbr.toString())
          doctoInv.idTipoDocto = 'DA'
          doctoInv.fecha = new Date()
          doctoInv.usuario = 'EXT'
          doctoInv.referencia = 'DEVOLUCION APLICADA'
          doctoInv.idSync = '1'
          doctoInv.idMod = '0'
          doctoInv.fechaMod = new Date()
          doctoInv.idSucursal = Registry.currentSite
          doctoInv.notas = StringUtils.trimToEmpty(String.format("P%010d", JbQuery.nextFolioJbSobre()))
          doctoInv.cantidad = StringUtils.trimToEmpty(quantity.toString())
          doctoInv.estado = 'pendiente'
          DoctoInvJava doctoInvJava = new DoctoInvJava()
          doctoInvJava.castToDoctoInvJava( doctoInv )
          DoctoInvQuery.saveDoctoInv( doctoInvJava )
          dispatchPrintTransaction( data.postTrType.idTipoTrans, trNbr )
        }
      }
    }

  }



  static List<CausaDev> findCausasDev(  ){
    List<CausaDev> lstCausasDevTmp = RepositoryFactory.causeDev.findAll( )
    List<CausaDev> lstCausasDev = new ArrayList<>()
    for(CausaDev causaDev : lstCausasDevTmp){
      if(!StringUtils.trimToEmpty(causaDev.causa).equalsIgnoreCase("Armazon solicitado")){
        lstCausasDev.add(causaDev)
      }
    }
    CausaDev causaVacia = new CausaDev()
    causaVacia.causa = ""
    lstCausasDev.add( causaVacia )
    Collections.sort(lstCausasDev, new Comparator<CausaDev>() {
        @Override
        int compare(CausaDev o1, CausaDev o2) {
            return o1.causa.compareTo(o2.causa)
        }
    })
    return lstCausasDev
  }


  protected static void generaAcuseAjusteInventario(InvTrView pView, Integer pInvTr){
    String filename = String.format( "%d.%d.aja", pInvTr, Registry.currentSite)
    String absolutePath = String.format( "%s%s%s", Registry.archivePath, File.separator, filename )
    File file = new File( absolutePath )
    PrintStream strOut = new PrintStream( file )

    StringBuffer sb = new StringBuffer()
    sb.append( "${StringUtils.trimToEmpty(Registry.currentSite.toString())}|${new Date().format("dd/MM/yyyy")}|${pView.data.skuList.size()}|0|" +
              "${StringUtils.trimToEmpty(pInvTr.toString())}|" )
    for ( InvTrSku sku : pView.data.skuList ) {
      sb.append( "\n" )
      sb.append( "${sku.sku}|${sku.qty}|" )
    }
    strOut.println sb.toString()
    strOut.close()
  }



  void readPackingList( ){
    Parametro ubicacion = Registry.find( TipoParametro.RUTA_POR_RECIBIR )
    Parametro parametro = RepositoryFactory.registry.findOne( TipoParametro.RUTA_RECIBIDOS.value )
    String ubicacionSource = ubicacion.valor
    String ubicacionsDestination = parametro.valor
    File source = new File( ubicacionSource )
    File destination = new File( ubicacionsDestination )
    if ( source.exists() && destination.exists() ) {
      source.eachFile() { file ->
        if ( file.getName().endsWith( ".pl" ) ) {
          file.eachLine {
            List<NotaVenta> lstNotas = RepositoryFactory.orders.findByFactura( StringUtils.trimToEmpty(it) )
            if( lstNotas.size() > 0 ){
              NotaVenta notaVenta = lstNotas.first()
              if( notaVenta != null && StringUtils.trimToEmpty(notaVenta.sFactura).equalsIgnoreCase("T")){
                Boolean hasSP = false
                for(DetalleNotaVenta det : notaVenta.detalles){
                  if( StringUtils.trimToEmpty(det.surte).equalsIgnoreCase("P")){
                    hasSP = true
                  }
                }
                if( hasSP ){
                  ServiceFactory.inventory.solicitarTransaccionDevolucion(notaVenta, true)
                }
              }
            }
          }
          String filename = null
          filename = file.absolutePath
          File inFile = new File( filename )
          File moved = new File( SettingsController.instance.processedPath, inFile.name )
          inFile.renameTo( moved )
        }
      }
    }
  }

}