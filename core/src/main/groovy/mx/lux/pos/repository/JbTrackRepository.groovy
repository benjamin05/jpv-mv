package mx.lux.pos.repository

import mx.lux.pos.model.JbTrack
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface JbTrackRepository extends JpaRepository<JbTrack, String>, QueryDslPredicateExecutor<JbTrack> {

    @Query( value = "SELECT * FROM jb_track WHERE rx = ?1", nativeQuery = true)
    List<JbTrack> findByRx( String idFactura )

}
