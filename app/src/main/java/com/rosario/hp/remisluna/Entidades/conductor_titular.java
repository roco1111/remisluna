package com.rosario.hp.remisluna.Entidades;

public class conductor_titular {

    String id;
    String nombre;
    String apellido;
    String mail;
    String estado;
    String clave;

    public conductor_titular(){
        super();
    }

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getIdfirebase() {
        return idfirebase;
    }

    public void setIdfirebase(String idfirebase) {
        this.idfirebase = idfirebase;
    }


    String idfirebase;
}
