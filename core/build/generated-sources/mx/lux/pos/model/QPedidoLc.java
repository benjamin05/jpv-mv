package mx.lux.pos.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QPedidoLc is a Querydsl query type for PedidoLc
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPedidoLc extends EntityPathBase<PedidoLc> {

    private static final long serialVersionUID = 1264875421;

    public static final QPedidoLc pedidoLc = new QPedidoLc("pedidoLc");

    public final StringPath cliente = createString("cliente");

    public final DateTimePath<java.util.Date> fechaAcuse = createDateTime("fechaAcuse", java.util.Date.class);

    public final DateTimePath<java.util.Date> fechaAlta = createDateTime("fechaAlta", java.util.Date.class);

    public final DateTimePath<java.util.Date> fechaEntrega = createDateTime("fechaEntrega", java.util.Date.class);

    public final DateTimePath<java.util.Date> fechaEnvio = createDateTime("fechaEnvio", java.util.Date.class);

    public final DateTimePath<java.util.Date> fechaRecepcion = createDateTime("fechaRecepcion", java.util.Date.class);

    public final StringPath folio = createString("folio");

    public final StringPath id = createString("id");

    public final SetPath<PedidoLcDet, QPedidoLcDet> pedidoLcDets = this.<PedidoLcDet, QPedidoLcDet>createSet("pedidoLcDets", PedidoLcDet.class, QPedidoLcDet.class);

    public final StringPath sucursal = createString("sucursal");

    public QPedidoLc(String variable) {
        super(PedidoLc.class, forVariable(variable));
    }

    public QPedidoLc(Path<? extends PedidoLc> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QPedidoLc(PathMetadata<?> metadata) {
        super(PedidoLc.class, metadata);
    }

}

