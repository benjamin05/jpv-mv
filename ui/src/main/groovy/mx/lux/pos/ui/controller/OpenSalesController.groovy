package mx.lux.pos.ui.controller

import mx.lux.pos.model.Apertura
import mx.lux.pos.model.MonedaDetalle
import mx.lux.pos.model.Parametro
import mx.lux.pos.model.TipoParametro
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.resources.ServiceManager
import mx.lux.pos.ui.view.dialog.AuthorizationDialog
import mx.lux.pos.ui.view.dialog.AuthorizationSPDialog
import mx.lux.pos.ui.view.dialog.EffectiveRateDialog
import mx.lux.pos.ui.view.dialog.OpenSalesDayDialog
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*

class OpenSalesController {

    private Logger logger = LoggerFactory.getLogger( OpenSalesController.class )

    private OpenSalesDayDialog newDayDialog
    private JDialog newRatesDialog

    // Constructor: Implemented as singleton
    protected static OpenSalesController instance
    protected OpenSalesController() { }
    static OpenSalesController getInstance() {
        if ( instance == null ) {
            instance = new OpenSalesController()
        }
        return instance
    }

    // Requests
    void requestNewDay() {
        if ( newDayDialog == null ) {
            newDayDialog = new OpenSalesDayDialog()
        }
        newDayDialog.activate()
        if( newDayDialog.printRequested ){
            this.logger.debug( String.format("Setup Cash for  MXN:%,.2f  USD:%,.0f",
                    newDayDialog.currentAmount, newDayDialog.currentAmountUsd) )
            Apertura opening = ServiceManager.salesDayLog.register( new Date(), newDayDialog.currentAmount,
                    newDayDialog.currentAmountUsd, newDayDialog.remarks)
            println " Opening:: $opening"
            ServiceManager.ticketService.imprimeAperturaCaja( opening.fechaApertura )
        }

    }


    void disactivateSP() {
        AuthorizationSPDialog authDialog = new AuthorizationSPDialog(new JDialog(), "Esta operacion requiere autorizaci\u00f3n")
        authDialog.show()
        logger.error('Autorizado: ' + authDialog.authorized)
        if (authDialog.authorized) {
          String title = 'Parametro actualizado'
          String text = ''
          Parametro parametro = ServiceManager.employeeService.parametro( TipoParametro.ACTIVO_VALIDA_SP.value )
          String[] valor = parametro.valor.split(/\|/)
          if( valor.length > 0 ){
            if( valor[0].trim().equalsIgnoreCase('si') ){
              parametro.valor = 'no|'+valor[1].trim()
              text = 'Surte Pino fue deshabilitado'
            } else if( valor[0].trim().equalsIgnoreCase('no') ){
              parametro.valor = 'si|'+valor[1].trim()
              text = 'Surte Pino fue habilitado'
            }
            parametro = ServiceManager.employeeService.saveParametro( parametro )
            if( parametro != null ){
              OrderController.notify( title, text )
            }
          }
        } else {
          OrderController.notifyAlert('Se requiere autorizacion para esta operacion', 'Se requiere autorizacion para esta operacion')
        }
    }

    Double requestActiveRate( String pIdMoneda ) {
      Double rate = 0
      MonedaDetalle fx = ServiceManager.fxRateService.findActiveRate( pIdMoneda )
      if ( fx != null ) {
          rate = fx.tipoCambio.doubleValue()
      }
      return rate
    }

    Double requestCurrentAmount( ) {
        Double amount = 0
        Apertura opening = ServiceManager.salesDayLog.findSalesDay( new Date( ) )
        if ( opening != null ) {
            amount = opening.efvoPesos.doubleValue()
        }
        return amount
    }

    Double requestCurrentAmountUsd( ) {
        Double amount = 0
        Apertura opening = ServiceManager.salesDayLog.findSalesDay( new Date( ) )
        if ( opening != null ) {
            amount = opening.efvoDolares.doubleValue()
        }
        return amount
    }

    void requestExchangeRateSetup() {
        if ( newRatesDialog == null ) {
            newRatesDialog = new EffectiveRateDialog( )
        }
        newRatesDialog.activate()
        if (newRatesDialog.btnOk) {
          this.logger.debug( String.format("Change Rates for  USD:%,.4f  EUR:%,.4f",
              newRatesDialog.usdRate, newRatesDialog.eurRate) )
            ServiceManager.fxRateService.register( "USD", newRatesDialog.usdRate )
            ServiceManager.fxRateService.register( "EUR", newRatesDialog.eurRate )
        }

    }
}