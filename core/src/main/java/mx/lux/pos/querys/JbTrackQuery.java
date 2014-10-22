package mx.lux.pos.querys;


import mx.lux.pos.repository.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by magno on 9/09/14.
 */
public class JbTrackQuery {

    private JbTrack jbTrack;

    public static void insertJbTrack(JbTrack jbt) {

        String sql = "INSERT INTO jb_track (rx, estado, obs, emp, id_viaje)" +
                " VALUES ('"+ jbt.getRx() + "', '"+ jbt.getEstado() + "', '" + jbt.getObs() +
                "', '" + jbt.getEmp() + "', '" + jbt.getIdViaje() +"')";

        Connections db = new Connections();
        db.insertQuery(sql);
        db.close();
        db = null;
    }
}
