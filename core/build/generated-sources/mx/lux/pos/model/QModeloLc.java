package mx.lux.pos.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QModeloLc is a Querydsl query type for ModeloLc
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QModeloLc extends EntityPathBase<ModeloLc> {

    private static final long serialVersionUID = 613058174;

    public static final QModeloLc modeloLc = new QModeloLc("modeloLc");

    public final StringPath cilindro = createString("cilindro");

    public final StringPath color = createString("color");

    public final StringPath curva = createString("curva");

    public final StringPath diametro = createString("diametro");

    public final StringPath eje = createString("eje");

    public final StringPath esfera = createString("esfera");

    public final StringPath id = createString("id");

    public final NumberPath<Integer> idProveedor = createNumber("idProveedor", Integer.class);

    public final StringPath modelo = createString("modelo");

    public QModeloLc(String variable) {
        super(ModeloLc.class, forVariable(variable));
    }

    public QModeloLc(Path<? extends ModeloLc> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QModeloLc(PathMetadata<?> metadata) {
        super(ModeloLc.class, metadata);
    }

}

