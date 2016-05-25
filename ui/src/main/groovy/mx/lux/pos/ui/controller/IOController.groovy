package mx.lux.pos.ui.controller

import mx.lux.pos.model.Empleado
import mx.lux.pos.model.LogAsignaSubgerente
import mx.lux.pos.model.Sucursal
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.model.file.FileFilteredList
import mx.lux.pos.ui.resources.ServiceManager
import mx.lux.pos.ui.view.dialog.ImportClasificationArticleDialog
import mx.lux.pos.ui.view.dialog.ImportPartMasterDialog
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class IOController {

    private Logger log = LoggerFactory.getLogger(this.getClass())
    private static IOController instance

    private IOController() { }

    static IOController getInstance() {
        if (instance == null) {
            instance = new IOController()
        }
        return instance
    }

    void requestImportPartMaster() {
        log.debug(String.format('Request ImportPartMaster'))
        ImportPartMasterDialog dialog = new ImportPartMasterDialog()
        dialog.setFilenamePattern(ServiceManager.ioServices.productsFilePattern)
        dialog.activate()
    }

    void requestImportClasificationArtMaster() {
        log.debug(String.format('Request ImportClasificationArtMaster'))
        ImportClasificationArticleDialog dialog = new ImportClasificationArticleDialog()
        dialog.setFilenamePattern( ServiceManager.ioServices.clasificationsFilePattern )
        dialog.activate()
    }


    void dispatchImportPartMaster(File pFile) {
        ServiceManager.ioServices.loadPartFile(pFile)
    }

    void autoUpdateEmployeeFile() {
        this.log.debug('AutoUpdate EmployeeFile')
        String pattern = ServiceManager.ioServices.getEmployeeFilePattern()
        FileFilteredList list = new FileFilteredList(pattern)
        File incomingPath = ServiceManager.ioServices.getIncomingLocation()
        for (File f : incomingPath.listFiles()) {
            list.add(f)
        }
        File f = list.pop()
        while (f != null) {
            ServiceManager.ioServices.loadEmployeeFile(f)
            f = list.pop()
        }
    }

    void autoUpdateFxRates() {
        this.log.debug('AutoUpdate FxRates')
        String pattern = ServiceManager.ioServices.getFxRatesFilePattern()
        FileFilteredList list = new FileFilteredList(pattern)
        File incomingPath = ServiceManager.ioServices.getIncomingLocation()
        for (File f : incomingPath.listFiles()) {
            list.add(f)
        }
        File f = list.pop()
        while (f != null) {
            ServiceManager.ioServices.loadFxRatesFile(f)
            f = list.pop()
        }
    }

    void startAsyncNotifyDispatcher() {
        this.log.debug('Trigger Async Notification Dispatcher')
        ServiceManager.ioServices.startAsyncNotifyDispatcher()
    }


    void dispatchImportClasificationArt(File pFile) {
        this.log.debug( 'Importando Clasificacion de Articulos' )
        Map<String, Object> importSummary = ServiceManager.ioServices.loadPartClassFile( pFile )
    }


    void updateInitialDate(String date){
        ServiceManager.ioServices.saveActualDate( date )
    }

    void deletCustomerProcess( ){
        ServiceManager.customerService.eliminarTodoClienteProceso()
    }


    void loadMessageTicketFile( ){
        log.debug( "loadMessageTicketFile( )" )
        ServiceManager.promotionService.cargaArchivoMensajeTicket()
    }

    void cargaFoliosPendientesPedidosLc(){
        ServiceManager.orderService.cargaFoliosPendientesPedidosLc()
    }

    void loadAcusePedidoLc() {
      ServiceManager.orderService.cargaAcusesPedidosLc()
    }

    Boolean isManager( String idEmployee ){
      Boolean valid = false
      Empleado emp = ServiceManager.employeeService.obtenerEmpleado( StringUtils.trimToEmpty(idEmployee) )
      if( emp.idPuesto == 1 || emp.idPuesto == 15 ){
        valid = true
      }
      if( !valid ){
        LogAsignaSubgerente log = ServiceManager.employeeService.obtenerSubgerenteActual()
        if( log != null && StringUtils.trimToEmpty(emp.id).equalsIgnoreCase(StringUtils.trimToEmpty(log.empleadoAsignado))){
          valid = true
        }
      }
      return valid
    }


    String findNameCurrentSite(){
      Sucursal suc = ServiceManager.siteService.obtenerSucursal(Registry.currentSite)
      return suc != null ? suc.nombre : ""
    }

}

