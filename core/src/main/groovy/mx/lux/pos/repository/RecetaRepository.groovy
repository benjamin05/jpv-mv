package mx.lux.pos.repository

import mx.lux.pos.model.Receta
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface RecetaRepository extends JpaRepository<Receta, Integer>, QueryDslPredicateExecutor<Receta> {

  List<Receta> findByIdCliente( Integer pIdCliente )

   Receta findById (Integer id)

}
