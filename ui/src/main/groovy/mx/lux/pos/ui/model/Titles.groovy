package mx.lux.pos.ui.model

import mx.lux.pos.model.Titulo
import mx.lux.pos.service.impl.ServiceFactory
import org.apache.commons.lang3.StringUtils

class Titles {

  private static final String TAG_MR = 'Sr.'
  private static final String TAG_MRS = 'Sra.'

  List<Titulo> titulos
  Titulo mr
  Titulo mrs

  private static Titles instance
  private Titles() {
    if (ServiceFactory.customers != null) {
      this.titulos = ServiceFactory.customers.listarTitulosClientes()
    } else {
      this.titulos = new ArrayList<Titulo>()
      this.titulos.add( new Titulo(titulo: 'Arq.', sexoTitulo: 'n'))
      this.titulos.add( new Titulo(titulo: 'Sr.', sexoTitulo: 'm'))
      this.titulos.add( new Titulo(titulo: 'Sra.', sexoTitulo: 'f'))
      this.titulos.add( new Titulo(titulo: 'Srita.', sexoTitulo: 'f'))
    }
    for ( Titulo t : this.titulos ) {
      if ( TAG_MR.equalsIgnoreCase( t.titulo ) ) {
        mr = t
      } else if ( TAG_MRS.equalsIgnoreCase( t.titulo ) ) {
        mrs = t
      }
    }
  }

  static Titles getInstance() {
    if ( this.instance == null) {
      this.instance = new Titles()
    }
    return this.instance
  }

  Titulo find( String pTitulo ) {
    Titulo titulo = null
    if (StringUtils.isNotBlank( pTitulo ) ) {
      for (Titulo t : this.titulos) {
        if (t.equals(pTitulo)) {
          titulo = t
          break
        }
      }
    }
    return titulo
  }

  Titulo getDefault( GenderType pGender ) {
    Titulo t = mr
    if ( GenderType.FEMALE.equals( pGender ) ) {
      t = mrs
    }
    return t
  }

  List<String> list() {
    List<String> values = new ArrayList<String>()
    for (Titulo t : this.titulos) {
      values.add( t.titulo )
    }
    return values
  }

}
