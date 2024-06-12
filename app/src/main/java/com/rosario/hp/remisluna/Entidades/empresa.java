package com.rosario.hp.remisluna.Entidades;

public class empresa {
    public empresa(){
        super();
    }

    String id;
    String nombre;
    String direccion;
    String localidad;
    String sectores;
    String id_sector;

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

    public String getSectores() {
        return sectores;
    }

    public void setSectores(String sectores) {
        this.sectores = sectores;
    }

    public String getId_sector() {
        return id_sector;
    }

    public void setId_sector(String id_sector) {
        this.id_sector = id_sector;
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
