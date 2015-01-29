package mx.lux.pos.model

enum TipoParametro {
  ACTIVO_VALIDA_SP( 'activo_valida_sp', '' ),
  ACUSE_LOG_DETALLE( 'acuse_log_detalle', 'no' ),
  ACUSE_RETRASO( 'acuse_seg_ciclo', '180' ),
  ANTICIPO_MENOR_REQUIERE_AUTORIZACIN( 'anticipo_menor_requiere_autorizacion', 'si' ),
  ARCHIVO_MENSAJE( 'archivo_mensaje', 'mensajes.txt' ),
  ARCHIVO_EMPLEADOS( 'archivo_empleados', 'emp.{FECHA}.txt' ),
  ARCHIVO_PRODUCTOS( 'archivo_productos', 'Prod*_{REGION}.txt' ),
  ARCHIVO_TIPO_CAMBIO( 'archivo_tipo_cambio', 'tc.*.{FECHA}.txt' ),
  ARCHIVO_CLASIFICACION_ARTICULOS( 'archivo_clasificacion_articulos', 'articulos.*.csv' ),
  BANCO_DEPOSITO( 'banco_deposito', '' ),
  CLIENTES_ACTIVOS( 'clientes_activos', 'Público General,Cliente Nuevo,Cliente en Proceso,Cliente en Caja,Cotización' ),
  CUPON_TERCER_PAR( 'cupon_tercer_par', 'si' ),
  PAGOS_NO_TRANSFERENCIA( 'pagos_no_transf', 'C2,C3,C4,BD' ),
  GENERICOS_VALIDOS_OTRAS_TRANS( 'genericos_validos_otras_trans', 'H' ),
  CAN_MISMO_DIA( 'can_mismo_dia', '' ),
  CONV_NOMINA( 'conv_nomina' ),
  COMANDO_ZIP( 'comando_zip', 'tar -cvf' ),
  COMPANIA_NOMBRE_CORTO( 'compania_nombre_corto', 'OPTICAS LUX' ),
  COMPANIA_REGION( 'compania_region', '01' ),
  COMPANIA_RFC( 'compania_rfc', 'DIO-830602-8M5' ),
  CUPON_FF_ACTIVADO( 'cupon_ff', 'si' ),
  CUPON_FF_OTHER_DISCOUNT( 'cupon_ff_otro_descuento', 'no' ),
  DESCRIPCION_CORTA( 'descripcion_corta', 'si' ),
  DESPLIEGA_USD( 'despliega_dolares', 'no' ),
  DIA_BODEGA('dia_bodega','1'),
  DIA_PRO('DIA_PRO','7'),
  EMP_AUDITORIA( 'emp_auditoria', '' ),
  EMP_ELECTRONICO( 'emp_electronico', '' ),
  ESPERA_CIERRE( 'espera_cierre', '60' ),
  FECHA_PRIMER_ARRANQUE( 'fecha_primer_arranque', ''),
  FORMAS_PAGO_NO_CUPON( 'formas_pago_no_cupon', 'C1,TR' ),
  FORMAS_PAGO_DEV( 'formas_pago_dev', 'TC' ),
  FORMATO_ARCHIVO( 'formato_archivo_salida', 'default' ),
  GRUPO_COMPANIA( 'grupo_compania', 'lux' ),
  GENERICOS_NO_ETREGABLES( 'genericos_no_entregables', 'B' ),
  GENERICO_PRECIO_VARIABLE( 'generico_precio_variable', '' ),
  ID_CLIENTE_GENERICO( 'cli_gen', '1' ),
  ID_ESTADO( 'id_estado', '' ),
  ID_GERENTE( 'id_gerente', '' ),
  ID_SUCURSAL( 'id_sucursal', '' ),
  IMPRESORA_TICKET( 'impresora_ticket', 'lpr -P lp0' ),
  IMPRIME_DUPLICADO( 'imprime_duplicado', 'si' ),
  INVENTORY_ADJUST_PASSWORD( 'clave_ajuste_inventario', '123ez4' ),
  INV_EXCHANGE_FILE_REQUIRED( 'inventario_generar_archivo_salida', 'no' ),
  INV_EXPORT_ADJUST_TR( 'inventario_exportar_tr_ajuste', 'no' ),
  INV_EXPORT_ISSUE_TR( 'inventario_exportar_tr_salida', 'si' ),
  INV_EXPORT_RECEIPT_TR( 'inventario_exportar_tr_entrada', 'no' ),
  INV_EXPORT_RETURN_TR( 'inventario_exportar_tr_devolucion', 'no' ),
  INV_EXPORT_SALE_TR( 'inventario_exportar_tr_venta', 'no' ),
  IVA_VIGENTE( 'iva_vigente', '16' ),
  MAX_DISCOUNT_STORE( 'tope_descto_tienda', '10.0' ),
  MAX_LONG_DESC_FACTURA( 'max_long_desc_efactura', '50' ),
  METODO_BUSQUEDA_ARTICULOS( 'metodo_busqueda_articulos', 'RB*,Letras especificadas|RB*+A,Letras especificadas+generico|D+BIOMED,D+Descripcion|+Q,Generico' ),
  MOSTRAR_NO_STOCK_TICKET_LC( 'mostrar_no_stock_ticket_lc', 'no' ),
  MONTO_GENERA_FF_CUPON( 'monto_genera_ff_cupon', '1000' ),
  MONTO_APLICA_FF_CUPON( 'monto_aplica_ff_cupon', '1000' ),
  MONTO_FF_CUPON( 'monto_ff_cupon', '200' ),
  PAQUETES('paquetes','BRONCE,PLATA,ORO,PLATINO,DIAMANTE'),
  PEDIDO_LC( 'pedido_lc', '' ),
  PIDE_FACTURA( 'pide_factura', '' ),
  PORCENTAJE_ANTICIPO( 'porcentaje_anticipo', '100.0' ),
  RUTA_CIERRE( 'ruta_cierre', 'C:/Documents and Settings/mensajero/cierre' ),
  RUTA_COMPROBANTES( 'ruta_comprobantes', 'C:/Documents and Settings/mensajero/facturas' ),
  COMANDO_BKP_NOTA( 'comando_bkp_venta', 'datadb' ),
  RUTA_INVENTARIO( 'ruta_inv_tr', 'C:/Documents and Settings/mensajero/inventario' ),
  RUTA_REMISION( 'ruta_remision', 'C:/Documents and Settings/mensajero/remision' ),
  RUTA_LISTA_PRECIOS( 'ruta_lista_precios', 'C:/Documents and Settings/mensajero/lp' ),
  RUTA_POR_ENVIAR( 'ruta_por_enviar', 'C:/Documents and Settings/mensajero/por_enviar' ),
  RUTA_POR_ENVIAR_DROPBOX( 'ruta_por_enviar_dropbox', '/home/drop/Dropbox/MAE' ),
  RUTA_POR_ENVIAR_MENSAJERO( 'ruta_por_enviar_mensajero', '/home/paso/ventas' ),
  RUTA_POR_RECIBIR( 'ruta_por_recibir', 'C:/Documents and Settings/mensajero/por_recibir' ),
  RUTA_RECIBIDOS( 'ruta_recibidos', 'C:/Documents and Settings/mensajero/recibidos' ),
  RUTA_CATALOGOS( 'ruta_catalogos', 'C:/Documents and Settings/mensajero/catalogos' ),
  SALIDA_TOTAL_ACTIVA( 'salida_total_activa', 'no' ),
  TIPO_PAGO( 'tipo_pago', '' ),
  TIPO_PAGO_DOLARES( 'tipo_pago_dolares', 'EFD,TCD,TDD' ),
  TIPO_PAGO_CRE_EMP( 'tipo_pago_credito_emp', 'CRE'),
  TRANS_CAN_MISMO_DIA( 'trans_can_mismo_dia', 'si' ),
  TRANS_INV_TIPO_AJUSTE( 'trans_inv_tipo_ajuste', 'AJUSTE' ),
  TRANS_INV_TIPO_CANCELACION( 'trans_inv_tipo_cancelacion', 'DEVOLUCION' ),
  TRANS_INV_TIPO_CANCELACION_EXTRA( 'trans_inv_tipo_canc_extraordinaria', 'RETORNO' ),
  TRANS_INV_TIPO_RECIBE_REMISION( 'trans_inv_tipo_recibe_remision', 'ENTRADA' ),
  TRANS_INV_TIPO_SALIDA( 'trans_inv_tipo_salida', 'SALIDA' ),
  TRANS_INV_TIPO_OTRA_SALIDA( 'trans_inv_tipo_otra_salida', 'OTRA_SALIDA' ),
  TRANS_INV_TIPO_OTRA_ENTRADA( 'trans_inv_tipo_otra_entrada', 'OTRA_ENTRADA' ),
  TRANS_INV_TIPO_VENTA( 'trans_inv_tipo_venta', 'VENTA' ),
  TRANS_INV_TIPO_ENTRADA_SP( 'trans_inv_tipo_recibe_remision_sp', 'ENTRADA_SP' ),
  URL_ACUSE_AJUSTE_VENTA( 'url_acuse_ajuste_venta', '' ),
  URL_ACUSE_VENTA_DIA( 'url_acuse_venta_dia', '' ),
  URL_CARGA_LISTA_PRECIOS( 'url_carga_lista_precios', '' ),
  URL_RECIBE_LISTA_PRECIOS( 'url_recibe_lista_precios', '' ),
  VALIDA_SP( 'valida_sp', '' ),
  SALIDA_VENTA_SP( 'salida_venta_sp', 'no' ),
  VALIDA_EMPLEADO( 'valida_empleado', '' ),
  VALIDA_DIA_CERRADO_VENTA( 'valida_dia_cerrado_venta', 'no' ),
  VALIDA_APLICAR_CUPON_PUBLICO_GENERAL( 'valida_aplicar_cupon_publico_general', 'no' ),
  VENTA_NEGATIVA_AUTORIZACION( 'venta_negativo_aut', 'S' ),
  VIGENCIA_CUPON('vigencia_cupon','15'),
  VIGENCIA_CUPON_FF('vigencia_cupon_ff','30'),
  ALMACENES('almacenes','00000'),
  ALMACEN_POR_ACLARAR('almacen_por_aclarar','4000'),
  ACTIVE_STORE_DISCOUNT( 'desc_tienda_activo', 'no' ),
  ARCHIVO_CONSULTA_WEB('archivo_consulta_web',''),
  URL_ACUSE_RECIBIDO_LC('url_acuse_recibido_lc',''),
  URL_CANCELACION_PEDIDO_LC('cancelacion_pedido_lc',''),
  URL_REUSO_PEDIDO_LC('reuso_pedido_lc',''),
  URL_SALIDA_ALMACEN('url_salida_almacen',''),
  URL_ENTRADA_ALMACEN('url_entrada_sucursal',''),
  URL_CONFIRMA_ENTRADA('url_confirma_entrada',''),
  TRANS_INV_TIPO_SALIDA_ALMACEN( 'trans_inv_salida_almacen', 'SALIDA_ALMACEN' ),
  TRANS_INV_TIPO_ENTRADA_ALMACEN( 'trans_inv_entrada_almacen', 'ENTRADA_ALMACEN' ),
  INV_EXPORT_SALIDA_ALMACEN_TR( 'inventario_exportar_tr_salida_almacen', 'si' ),
  INV_EXPORT_ENTRADA_ALMACEN_TR( 'inventario_exportar_tr_entrada_almacen', 'no' ),


  final String value
  final String defaultValue

  private TipoParametro( String value ) {
    this( value, '' )
  }

  private TipoParametro( String value, String defaultValue ) {
    this.value = value
    this.defaultValue = defaultValue
  }

  static TipoParametro parse( String value ) {
    for ( item in values() ) {
      if ( item.value.equalsIgnoreCase( value?.trim() ) ) {
        return item
      }
    }
    return null
  }

  @Override
  String toString( ) {
    value
  }
}
