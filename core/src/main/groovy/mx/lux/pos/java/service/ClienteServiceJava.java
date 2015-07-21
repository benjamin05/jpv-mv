package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
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


  public List<ClientesProcesoJava> obtenerClientesEnCaja( Boolean pLoaded ) throws ParseException {
    List<ClientesProcesoJava> customers = new ArrayList<ClientesProcesoJava>();
    customers.addAll( ClientesProcesoQuery.buscaClientesProcesoPorEtapa(ClienteProcesoEtapa.PAYMENT.toString()) );
    if ( pLoaded ) {
      for ( ClientesProcesoJava cliente : customers ) {
        this.llenarCliente( cliente );
        this.llenarNotaVentas( cliente );
      }
    }
    return customers;
  }


  void llenarCliente( ClientesProcesoJava pCliente ) throws ParseException {
    pCliente.setCliente(ClientesQuery.busquedaClienteById(pCliente.getIdCliente()) );
  }


  public ClientesJava obtenerClientePorDefecto( ) throws ParseException {
    log.debug( "obteniendo cliente generico" );
    Parametros idClienteParametro = ParametrosQuery.BuscaParametroPorId( TipoParametro.ID_CLIENTE_GENERICO.getValue() );
    String idCliente = idClienteParametro.getValor();
    if ( StringUtils.isNumeric(idCliente) ) {
      return ClientesQuery.busquedaClienteById(NumberFormat.getInstance().parse(idCliente).intValue());
    }
    return null;
  }


  public List<DominioJava> listarDominiosClientes( ) {
    log.debug( "listando dominios frecuentes" );
    return DominioQuery.buscaTodoDominios();
  }


  public ClientesJava obtenerCliente( Integer id ) throws ParseException {
    log.debug( "obteniendo cliente id: ${id}" );
    ClientesJava cliente =  ClientesQuery.busquedaClienteById( id );
    if(cliente ==null){
      cliente = new ClientesJava();
    }
    return  cliente;
  }


}
