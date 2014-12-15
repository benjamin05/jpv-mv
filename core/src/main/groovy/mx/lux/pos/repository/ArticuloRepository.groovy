package mx.lux.pos.repository

import mx.lux.pos.model.Articulo
import mx.lux.pos.repository.custom.ArticuloRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface ArticuloRepository extends JpaRepository<Articulo, Integer>, QueryDslPredicateExecutor<Articulo>, ArticuloRepositoryCustom{



    List<Articulo> findByIdIn( Collection<Integer> pId )

  List<Articulo> findByCantExistenciaGreaterThan( Integer pCantExistencia )

  List<Articulo> findByCantExistenciaLessThan( Integer pCantExistencia )

  @Query( value = "SELECT * FROM articulos WHERE existencia > 0 OR existencia < 0 AND id_gen_tipo != 'NC';", nativeQuery = true )
  List<Articulo> findArticlesWithExistence()

  @Query( value = "SELECT * FROM articulos WHERE existencia > 0 AND id_generico = 'A';", nativeQuery = true )
  List<Articulo> findFramesWithExistence()

  @Query( value = "SELECT * FROM articulos WHERE existencia > 0 AND id_generico = 'E';", nativeQuery = true )
  List<Articulo> findAccesoriesWithExistence()
  
}
