package com.rosario.hp.remisluna.Entidades;

public class parada {
    String id;
    String descripcion;
    String latitud;
    String longitud;
    String id_remiseria;

    public parada(){
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getId_remiseria() {
        return id_remiseria;
    }

    public void setId_remiseria(String id_remiseria) {
        this.id_remiseria = id_remiseria;
    }
}
