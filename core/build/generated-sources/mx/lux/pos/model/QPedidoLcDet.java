package mx.lux.pos.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QPedidoLcDet is a Querydsl query type for PedidoLcDet
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPedidoLcDet extends EntityPathBase<PedidoLcDet> {

    private static final long serialVersionUID = -2139319498;

    public static final QPedidoLcDet pedidoLcDet = new QPedidoLcDet("pedidoLcDet");

    public final NumberPath<Integer> cantidad = createNumber("cantidad", Integer.class);

    public final StringPath cilindro = createString("cilindro");

    public final StringPath color = createString("color");

    public final StringPath curvaBase = createString("curvaBase");

    public final StringPath diametro = createString("diametro");

    public final StringPath eje = createString("eje");

    public final StringPath esfera = createString("esfera");

    public final StringPath id = createString("id");

    public final StringPath modelo = createString("modelo");

    public final NumberPath<Integer> numReg = createNumber("numReg", Integer.class);

    public QPedidoLcDet(String variable) {
        super(PedidoLcDet.class, forVariable(variable));
    }

    public QPedidoLcDet(Path<? extends PedidoLcDet> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QPedidoLcDet(PathMetadata<?> metadata) {
        super(PedidoLcDet.class, metadata);
    }

}

