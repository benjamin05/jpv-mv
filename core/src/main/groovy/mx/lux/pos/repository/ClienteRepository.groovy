package mx.lux.pos.repository

import mx.lux.pos.model.Cliente
import mx.lux.pos.repository.custom.ClienteRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface ClienteRepository extends JpaRepository<Cliente, Integer>, QueryDslPredicateExecutor<Cliente>, ClienteRepositoryCustom {

  @Query( nativeQuery = true,
  value = "SELECT * FROM clientes WHERE LOWER(nombre_cli || apellido_pat_cli || apellido_mat_cli) LIKE LOWER('%' || ?1 || '%')" )
  List<Cliente> listByTextContainedInName( String pHint )

    @Query( nativeQuery = true,
    value = "select * from clientes where apellido_pat_cli like upper( ?1 || '%' ) order by apellido_pat_cli, apellido_mat_cli, nombre_cli limit 50" )
    List<Cliente> listaClientesStartApellidoPatCli(String apellidoPat)

    @Query( nativeQuery = true,
            value = "select * from clientes where date(fecha_nac) = ?1 order by apellido_pat_cli, apellido_mat_cli, nombre_cli limit 50" )
    List<Cliente> listaClientesFechaNac(Date fecha)

    @Query( nativeQuery = true,
            value = "select * from clientes where apellido_pat_cli like upper( ?1 || '%' ) and date(fecha_nac) = ?2 order by apellido_pat_cli, apellido_mat_cli, nombre_cli limit 50" )
    List<Cliente> listaClientesStartApellidoPatCliFechaNac(String apellidoPat, Date fecha)
}
