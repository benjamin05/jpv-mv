package mx.lux.pos.repository.custom

import mx.lux.pos.model.Cliente
import mx.lux.pos.model.Jb
import org.springframework.data.jpa.repository.Query

interface ClienteRepositoryCustom {

    List<Cliente> findByNombreApellidos(String nombre, String apellidoPaterno, String apellidoMaterno)

    List<Cliente> findByFechaAlta(Date fecha)

    List<Cliente> findByStartApellidoPaterno(String apellido)

    List<Cliente> findByFechaNacimiento(Date fecha)

    List<Cliente> findByStartApellidoPaternoAndFechaNacimiento(String apellido, Date fecha)
}
