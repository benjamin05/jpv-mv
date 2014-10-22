package mx.lux.pos.ui.model;

/**
 * Created with IntelliJ IDEA.
 * User: sucursal
 * Date: 15/08/13
 * Time: 05:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class SurteSwitch {

    private Boolean agregaArticulo;
    private Boolean surteSucursal;
    private String surte;


    public String getSurte() {
        return surte;
    }

    public void setSurte(String surte) {
        this.surte = surte;
    }

    public Boolean getAgregaArticulo() {
        return agregaArticulo;
    }

    public void setAgregaArticulo(Boolean agregaArticulo) {
        this.agregaArticulo = agregaArticulo;
    }

    public Boolean getSurteSucursal() {
        return surteSucursal;
    }

    public void setSurteSucursal(Boolean surteSucursal) {
        this.surteSucursal = surteSucursal;
    }
}
