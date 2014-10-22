package mx.lux.pos.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QAutorizaMov is a Querydsl query type for AutorizaMov
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAutorizaMov extends EntityPathBase<AutorizaMov> {

    private static final long serialVersionUID = -1675835802;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QAutorizaMov autorizaMov = new QAutorizaMov("autorizaMov");

    public final StringPath factura = createString("factura");

    public final DateTimePath<java.util.Date> fecha = createDateTime("fecha", java.util.Date.class);

    public final DateTimePath<java.util.Date> hora = createDateTime("hora", java.util.Date.class);

    public final StringPath idEmpleado = createString("idEmpleado");

    public final StringPath notas = createString("notas");

    public final QTipoTransaccion tipoTrans;

    public final NumberPath<Integer> tipoTransaccion = createNumber("tipoTransaccion", Integer.class);

    public QAutorizaMov(String variable) {
        this(AutorizaMov.class, forVariable(variable), INITS);
    }

    public QAutorizaMov(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAutorizaMov(PathMetadata<?> metadata, PathInits inits) {
        this(AutorizaMov.class, metadata, inits);
    }

    public QAutorizaMov(Class<? extends AutorizaMov> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tipoTrans = inits.isInitialized("tipoTrans") ? new QTipoTransaccion(forProperty("tipoTrans")) : null;
    }

}

