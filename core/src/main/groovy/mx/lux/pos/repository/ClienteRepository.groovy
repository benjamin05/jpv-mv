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

}
