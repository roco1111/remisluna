package com.rosario.hp.remisluna.Entidades;

public class cta_cte {
    public cta_cte(){
        super();
    }
    String id;
    String fecha;
    String debe;
    String haber;
    String saldo;
    String descripcion;
    String tipo_saldo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDebe() {
        return debe;
    }

    public String getTipo_saldo() {
        return tipo_saldo;
    }

    public void setTipo_saldo(String tipo_saldo) {
        this.tipo_saldo = tipo_saldo;
    }

    public void setDebe(String debe) {
        this.debe = debe;
    }

    public String getHaber() {
        return haber;
    }

    public void setHaber(String haber) {
        this.haber = haber;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
