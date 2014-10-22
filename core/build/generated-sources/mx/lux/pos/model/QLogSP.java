package mx.lux.pos.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QLogSP is a Querydsl query type for LogSP
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QLogSP extends EntityPathBase<LogSP> {

    private static final long serialVersionUID = 1641627712;

    public static final QLogSP logSP = new QLogSP("logSP");

    public final DateTimePath<java.util.Date> fechaLlamada = createDateTime("fechaLlamada", java.util.Date.class);

    public final DateTimePath<java.util.Date> fechaRespuesta = createDateTime("fechaRespuesta", java.util.Date.class);

    public final NumberPath<Integer> idArticulo = createNumber("idArticulo", Integer.class);

    public final StringPath idFactura = createString("idFactura");

    public final BooleanPath respuesta = createBoolean("respuesta");

    public QLogSP(String variable) {
        super(LogSP.class, forVariable(variable));
    }

    public QLogSP(Path<? extends LogSP> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QLogSP(PathMetadata<?> metadata) {
        super(LogSP.class, metadata);
    }

}

