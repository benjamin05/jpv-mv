package mx.lux.pos.repository

import mx.lux.pos.model.FormaContacto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface FormaContactoRepository extends JpaRepository<FormaContacto, Integer>, QueryDslPredicateExecutor<FormaContacto> {

  //  @Query("select forma_contacto.id_cliente, forma_contacto.id_tipo_contacto, forma_contacto.contacto, forma_contacto.observaciones, forma_contacto.id_sucursal from FormaContacto forma_contacto where forma_contacto.id_cliente = ?1  group by forma_contacto.id_cliente, forma_contacto.id_tipo_contacto, forma_contacto.contacto, forma_contacto.observaciones, forma_contacto.id_sucursal")
  //  List<FormaContacto> formaContactosGroupBy(Integer idCliente)
}

