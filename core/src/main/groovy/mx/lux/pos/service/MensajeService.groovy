package mx.lux.pos.service

interface MensajeService {

  String obtenerMensajePorId( Integer id )

  String obtenerMensajePorClave( String clave )

  void importarArchivo( String pImportFile )
}
