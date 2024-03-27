package com.rosario.hp.remisluna.Entidades;

public class solicitante {
    public solicitante(){
        super();
    }

    String nom_solicitante;
    String ape_solicitante;
    String id_empresa;
    String id_sector;
    String qr;
    String fecha_hora;
    String id;

    public String getNom_solicitante() {
        return nom_solicitante;
    }

    public void setNom_solicitante(String nom_solicitante) {
        this.nom_solicitante = nom_solicitante;
    }

    public String getApe_solicitante() {
        return ape_solicitante;
    }

    public void setApe_solicitante(String ape_solicitante) {
        this.ape_solicitante = ape_solicitante;
    }

    public String getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(String id_empresa) {
        this.id_empresa = id_empresa;
    }

    public String getId_sector() {
        return id_sector;
    }

    public void setId_sector(String id_sector) {
        this.id_sector = id_sector;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
