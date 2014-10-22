package mx.lux.pos.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QTipoTransaccion is a Querydsl query type for TipoTransaccion
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTipoTransaccion extends EntityPathBase<TipoTransaccion> {

    private static final long serialVersionUID = 632470938;

    public static final QTipoTransaccion tipoTransaccion1 = new QTipoTransaccion("tipoTransaccion1");

    public final StringPath descripcion = createString("descripcion");

    public final NumberPath<Integer> tipoTransaccion = createNumber("tipoTransaccion", Integer.class);

    public QTipoTransaccion(String variable) {
        super(TipoTransaccion.class, forVariable(variable));
    }

    public QTipoTransaccion(Path<? extends TipoTransaccion> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QTipoTransaccion(PathMetadata<?> metadata) {
        super(TipoTransaccion.class, metadata);
    }

}

