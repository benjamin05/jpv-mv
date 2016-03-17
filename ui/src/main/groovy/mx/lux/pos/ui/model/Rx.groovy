package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.Receta
import mx.lux.pos.java.repository.NotaVentaJava
import mx.lux.pos.java.repository.RecetaJava
import org.apache.commons.lang.StringUtils

//import sun.swing.StringUIClientPropertyKey
@Bindable
@ToString
@EqualsAndHashCode
class Rx {
  Integer id
  Integer exam
  String clientName
  Integer idClient
  String folio
  Date rxDate
  String useGlasses
  String optometristName
  String idOpt
  String typeOpt
  String odEsfR
  String odCilR
  String odEjeR
  String odAdcR
  String odAdiR
  String odPrismH
  String oiEsfR
  String oiCilR
  String oiEjeR
  String oiAdcR
  String oiAdiR
  String oiPrismH
  String diLejosR
  String diCercaR
  String odAvR
  String oiAvR
  String altOblR
  String observacionesR
  boolean fPrint
  String idSync
  Date DateMod
  String modId
  Integer idStore
  String diOd
  String diOi
  String materialArm
  String odPrismaV
  String oiPrismaV
  String treatment
  String udf5
  String udf6
  String idRxOri
  String dh
  String dv
  String pte
  String base
  Order order


    String getTipo(){
        String tipo = ''
        if( useGlasses.equalsIgnoreCase('l') ){
            tipo = 'LEJOS'
        } else if( useGlasses.equalsIgnoreCase('c') ){
            tipo = 'CERCA'
        } else if( useGlasses.equalsIgnoreCase('b') ){
            tipo = 'BIFOCAL'
        } else if( useGlasses.equalsIgnoreCase('p') ){
            tipo = 'PROGRESIVO'
        } else if( useGlasses.equalsIgnoreCase('i') ){
            tipo = 'INTERMEDIO'
        } else if( useGlasses.equalsIgnoreCase('t') ){
            tipo = 'BIFOCAL INTERMEDIO'
        }

        return tipo
    }

    String getOptNameFormatter(){
      String name = ''
        if(StringUtils.trimToEmpty(optometristName) != '' && StringUtils.trimToEmpty(idOpt) != ''){
          name = '['+idOpt.trim()+']'+optometristName
        } else {
          name = '['+idOpt.trim()+']'
        }
    }

    String getRxDistanceFormatter(){
        String distance = ''
        if(StringUtils.trimToEmpty(diOd) != '' && StringUtils.trimToEmpty(diOi) != ''){
            distance = StringUtils.trimToEmpty(diOd)+","+StringUtils.trimToEmpty(diOi)
        } else {
            distance = StringUtils.trimToEmpty(diLejosR)
        }
    }

    String getRxAddFormatter(){
        String add = ''
        if(StringUtils.trimToEmpty(odAdcR) != '' && StringUtils.trimToEmpty(oiAdcR) != ''){
            add = StringUtils.trimToEmpty(odAdcR)+","+StringUtils.trimToEmpty(oiAdcR)
        } else {
            add = StringUtils.trimToEmpty(odAdcR)+StringUtils.trimToEmpty(oiAdcR)
        }
    }

    String getTipoEditRx(){
        String tipo = ''
        if( useGlasses.equalsIgnoreCase('l') ){
            tipo = 'M'
        } else if( useGlasses.equalsIgnoreCase('c') ){
            tipo = 'M'
        } else if( useGlasses.equalsIgnoreCase('b') ){
            tipo = 'B'
        } else if( useGlasses.equalsIgnoreCase('p') ){
            tipo = 'P'
        }

        return tipo
    }

    String getTipoCorto( String tipo ){
        String tipoCorto = ''
        if(StringUtils.trimToEmpty( tipo ) ){
            if( tipo.trim().equalsIgnoreCase( 'LEJOS' ) ){
                tipoCorto = 'l'
            } else if( tipo.trim().equalsIgnoreCase( 'CERCA' ) ){
                tipoCorto = 'c'
            } else if( tipo.trim().equalsIgnoreCase( 'BIFOCAL' ) ){
                tipoCorto = 'b'
            } else if( tipo.trim().equalsIgnoreCase( 'PROGRESIVO' ) ){
                tipoCorto = 'p'
            } else if( tipo.trim().equalsIgnoreCase( 'INTERMEDIO' ) ){
                tipoCorto = 'i'
            } else if( tipo.trim().equalsIgnoreCase( 'BIFOCAL INTERMEDIO' ) ){
                tipoCorto = 't'
            }
        }
    }

  static Rx toRx( Receta receta ) {
    if ( receta?.id ) {
      Rx prescription = new Rx(
          id: receta.id,
          exam: receta.examen,
          clientName: receta?.cliente?.nombreCompleto,
          idClient: receta.idCliente,
          rxDate: receta.fechaReceta,
          useGlasses: receta.sUsoAnteojos,
          optometristName: StringUtils.trimToEmpty(receta?.empleado?.nombre)+' '+StringUtils.trimToEmpty(receta?.empleado?.apellidoPaterno)+' '+StringUtils.trimToEmpty(receta?.empleado?.apellidoMaterno),
          idOpt: receta.idOptometrista,
          typeOpt: StringUtils.trimToEmpty(receta.tipoOpt),
          odEsfR: StringUtils.trimToEmpty(receta.odEsfR),
          odCilR: StringUtils.trimToEmpty(receta.odCilR),
          odEjeR: StringUtils.trimToEmpty(receta.odEjeR),
          odAdcR: StringUtils.trimToEmpty(receta.odAdcR),
          odAdiR: StringUtils.trimToEmpty(receta.odAdiR),
          odPrismH: StringUtils.trimToEmpty(receta.odPrismaH),
          oiEsfR: StringUtils.trimToEmpty(receta.oiEsfR),
          oiCilR: StringUtils.trimToEmpty(receta.oiCilR),
          oiEjeR: StringUtils.trimToEmpty(receta.oiEjeR),
          oiAdcR: StringUtils.trimToEmpty(receta.oiAdcR),
          oiAdiR: StringUtils.trimToEmpty(receta.oiAdiR),
          oiPrismH: StringUtils.trimToEmpty(receta.oiPrismaH),
          diLejosR: StringUtils.trimToEmpty(receta.diLejosR),
          diCercaR: StringUtils.trimToEmpty(receta.diCercaR),
          odAvR: "20/${StringUtils.trimToEmpty(receta.odAvR)}" ?: '',
          oiAvR: "20/${StringUtils.trimToEmpty(receta.oiAvR)}" ?: '',
          altOblR: StringUtils.trimToEmpty(receta.altOblR),
          observacionesR: StringUtils.trimToEmpty(receta.observacionesR),
          fPrint: receta.fImpresa,
          idSync: receta.idSync,
          DateMod: receta.fechaMod,
          modId: receta.idMod,
          idStore: receta.idSucursal,
          diOd: StringUtils.trimToEmpty(receta.diOd),
          diOi: StringUtils.trimToEmpty(receta.diOi),
          materialArm: StringUtils.trimToEmpty(receta.material_arm),
          odPrismaV: receta.odPrismaV ?: '',
          oiPrismaV: receta.oiPrismaV ?: '',
          treatment: receta.tratamientos,
          udf5: receta.udf5,
          udf6: receta.udf6,
          idRxOri: receta.idRxOri,
          folio: receta.folio,
          dh: receta.dh,
          dv: receta.dv,
          pte: receta.pte,
          base: receta.base,
          order: Order.toOrder(receta?.notaVenta != null ? receta?.notaVenta : new NotaVenta())
      )
      return prescription
    }
    return null
  }


  static Rx toRx( RecetaJava receta ) {
    if ( receta?.idReceta ) {
      Rx prescription = new Rx(
            id: receta.idReceta,
            exam: receta.examen,
            clientName: receta?.cliNombreCompleto(),
            idClient: receta.idCliente,
            rxDate: receta.fechaReceta,
            useGlasses: receta.sUsoAnteojos,
            optometristName: StringUtils.trimToEmpty(receta?.empleado?.getNombreEmpleado())+' '+StringUtils.trimToEmpty(receta?.empleado?.apPatEmpleado)+' '+StringUtils.trimToEmpty(receta?.empleado?.apMatEmpleado),
            idOpt: receta.idOptometrista,
            typeOpt: StringUtils.trimToEmpty(receta.tipoOpt),
            odEsfR: StringUtils.trimToEmpty(receta.odEsfR),
            odCilR: StringUtils.trimToEmpty(receta.odCilR),
            odEjeR: StringUtils.trimToEmpty(receta.odEjeR),
            odAdcR: StringUtils.trimToEmpty(receta.odAdcR),
            odAdiR: StringUtils.trimToEmpty(receta.odAdiR),
            odPrismH: StringUtils.trimToEmpty(receta.odPrismaH),
            oiEsfR: StringUtils.trimToEmpty(receta.oiEsfR),
            oiCilR: StringUtils.trimToEmpty(receta.oiCilR),
            oiEjeR: StringUtils.trimToEmpty(receta.oiEjeR),
            oiAdcR: StringUtils.trimToEmpty(receta.oiAdcR),
            oiAdiR: StringUtils.trimToEmpty(receta.oiAdiR),
            oiPrismH: StringUtils.trimToEmpty(receta.oiPrismaH),
            diLejosR: StringUtils.trimToEmpty(receta.diLejosR),
            diCercaR: StringUtils.trimToEmpty(receta.diCercaR),
            odAvR: "20/${StringUtils.trimToEmpty(receta.odAvR)}" ?: '',
            oiAvR: "20/${StringUtils.trimToEmpty(receta.oiAvR)}" ?: '',
            altOblR: StringUtils.trimToEmpty(receta.altOblR),
            observacionesR: StringUtils.trimToEmpty(receta.observacionesR),
            fPrint: receta.fImpresa,
            idSync: receta.idSync,
            DateMod: receta.fechaMod,
            modId: receta.idMod,
            idStore: receta.idSucursal,
            diOd: StringUtils.trimToEmpty(receta.diOd),
            diOi: StringUtils.trimToEmpty(receta.diOi),
            materialArm: StringUtils.trimToEmpty(receta.materialArm),
            odPrismaV: receta.odPrismaV ?: '',
            oiPrismaV: receta.oiPrismaV ?: '',
            treatment: receta.tratamientos,
            udf5: receta.udf5,
            udf6: receta.udf6,
            idRxOri: receta.idRxOri,
            folio: receta.folio,
            order: Order.toOrder(receta?.notaVenta != null ? receta?.notaVenta : new NotaVentaJava())
      )
      return prescription
    }
    return null
  }

}
