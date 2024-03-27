package com.rosario.hp.remisluna.Entidades;

public class empresa {
    public empresa(){
        super();
    }

    String id;
    String nombre;
    String direccion;
    String localidad;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getId_tipo_empresa() {
        return id_tipo_empresa;
    }

    public void setId_tipo_empresa(String id_tipo_empresa) {
        this.id_tipo_empresa = id_tipo_empresa;
    }

    String id_tipo_empresa;

}
