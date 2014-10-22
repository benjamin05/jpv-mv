package mx.lux.pos.service

import mx.lux.pos.model.Jb
import mx.lux.pos.model.NotaVenta



interface JbService {

    Jb findJBbyRx( String rx )

    Jb saveJb( Jb pJb )

    Jb saveJbFamilia( NotaVenta nota1, NotaVenta nota2 )
}
