package mx.lux.pos.service

import mx.lux.pos.model.JbTrack

interface JbTrackService {

  JbTrack saveJbTrack( JbTrack jbTrack )

  List<JbTrack> findByRx( String rx )
}
