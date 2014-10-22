package mx.lux.pos.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QCuponMv is a Querydsl query type for CuponMv
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QCuponMv extends EntityPathBase<CuponMv> {

    private static final long serialVersionUID = 2134587141;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QCuponMv cuponMv = new QCuponMv("cuponMv");

    public final StringPath claveDescuento = createString("claveDescuento");

    public final StringPath facturaDestino = createString("facturaDestino");

    public final StringPath facturaOrigen = createString("facturaOrigen");

    public final DateTimePath<java.util.Date> fechaAplicacion = createDateTime("fechaAplicacion", java.util.Date.class);

    public final DateTimePath<java.util.Date> fechaVigencia = createDateTime("fechaVigencia", java.util.Date.class);

    public final NumberPath<java.math.BigDecimal> montoCupon = createNumber("montoCupon", java.math.BigDecimal.class);

    public final QNotaVenta notaVenta;

    public QCuponMv(String variable) {
        this(CuponMv.class, forVariable(variable), INITS);
    }

    public QCuponMv(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCuponMv(PathMetadata<?> metadata, PathInits inits) {
        this(CuponMv.class, metadata, inits);
    }

    public QCuponMv(Class<? extends CuponMv> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.notaVenta = inits.isInitialized("notaVenta") ? new QNotaVenta(forProperty("notaVenta"), inits.get("notaVenta")) : null;
    }

}

