package mx.lux.pos.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QMontoCupon is a Querydsl query type for MontoCupon
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMontoCupon extends EntityPathBase<MontoCupon> {

    private static final long serialVersionUID = 1460763159;

    public static final QMontoCupon montoCupon = new QMontoCupon("montoCupon");

    public final NumberPath<Integer> cantidad = createNumber("cantidad", Integer.class);

    public final StringPath generico = createString("generico");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<java.math.BigDecimal> monto = createNumber("monto", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> montoMaximo = createNumber("montoMaximo", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> montoMinimo = createNumber("montoMinimo", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> montoTercerPar = createNumber("montoTercerPar", java.math.BigDecimal.class);

    public final StringPath subtipo = createString("subtipo");

    public final StringPath tipo = createString("tipo");

    public QMontoCupon(String variable) {
        super(MontoCupon.class, forVariable(variable));
    }

    public QMontoCupon(Path<? extends MontoCupon> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QMontoCupon(PathMetadata<?> metadata) {
        super(MontoCupon.class, metadata);
    }

}

