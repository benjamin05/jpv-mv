package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;


public enum ClienteProcesoEtapa {

    UNDEFINED( "Ignorar" ),
    SALES( "Caja" ),
    PAYMENT( "Proceso" );

    private String value;

    private ClienteProcesoEtapa( String pValue ) {
        this.value = StringUtils.trimToEmpty( pValue ).toLowerCase();
    }

    // Public methods
    public boolean equals( String pEtapa ) {
        return this.value.equals( StringUtils.trimToEmpty( pEtapa ).toLowerCase() );
    }

    public boolean equals(  ClienteProceso pCliente ) {
        return this.equals( pCliente.getEtapa() );
    }

    
    public static ClienteProcesoEtapa parse( String pParseValue ) {


                ClienteProcesoEtapa etapa = ClienteProcesoEtapa.UNDEFINED;

        if ( StringUtils.isNotBlank( pParseValue ) ) {

            String parseValue = StringUtils.trimToEmpty( pParseValue ).toLowerCase();

         for ( ClienteProcesoEtapa e : ClienteProcesoEtapa.values() ) {
                if ( e.equals( parseValue ) ) {
                    etapa = e;
                    break;
                } else if ( e.value.startsWith( parseValue.substring( 0, 1 ) ) ) {
                    etapa = e;
                    break;
                }
            }

        }
        return etapa;
    }

    public String toString() {
        return this.value;
    }

}
