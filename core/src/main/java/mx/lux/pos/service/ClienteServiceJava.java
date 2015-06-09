package mx.lux.pos.service;


import mx.lux.pos.model.ClienteProcesoEtapa;
import mx.lux.pos.querys.ClientesProcesoQuery;
import mx.lux.pos.querys.NotaVentaQuery;
import mx.lux.pos.repository.ClientesProcesoJava;
import mx.lux.pos.repository.NotaVentaJava;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClienteServiceJava {

  static final Logger log = LoggerFactory.getLogger(ClienteServiceJava.class);

  public void actualizarClienteEnProceso( Integer pIdCliente ) throws ParseException {
    ClientesProcesoJava nuevo = new ClientesProcesoJava();
    nuevo.setIdCliente(pIdCliente);
    nuevo.setEtapa("proceso");
    nuevo.setFechaMod(new Date());
    nuevo.setIdSucursal(82);
    nuevo.setIdSync("1");
    nuevo.setIdSucursal(9999);
    nuevo.setIdMod("1");
    ClientesProcesoQuery.saveOrUpdateClientesProceso(nuevo);

    log.info("cliente almacenado en ClienteProceso");
    ClientesProcesoJava clienteProc = ClientesProcesoQuery.buscaClientesProcesoPorIdCliente(pIdCliente);
    ClienteProcesoEtapa etapa = ClienteProcesoEtapa.parse( clienteProc.getEtapa() );
    if (pIdCliente == 1) {
      log.info("*prueba1");
    } else {
      this.llenarNotaVentas( clienteProc );
      for ( NotaVentaJava order : clienteProc.getNotaVentas() ) {
        if ( order.getDetalles().size() > 0 ) {
          etapa = ClienteProcesoEtapa.PAYMENT;
        }
      }
      clienteProc.setEtapa(etapa.toString());
      ClientesProcesoQuery.saveOrUpdateClientesProceso(clienteProc);
    }
  }


  void llenarNotaVentas( ClientesProcesoJava pCliente ) throws ParseException {
    List<NotaVentaJava> orders = NotaVentaQuery.busquedaNotaByIdClienteAndFacturaEmpty( pCliente.getIdCliente() );
    List<NotaVentaJava> openOrders = new ArrayList<NotaVentaJava>();
    for ( NotaVentaJava order : orders ) {
      if ( StringUtils.isBlank(order.getFactura()) ) {
        openOrders.add( order );
      }
    }
    pCliente.setNotaVentas( openOrders );
  }


}
