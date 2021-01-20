package com.rosario.hp.remisluna.notificaciones;

import java.util.UUID;

/**
 * Representación de una promoción en forma de push notification
 */
public class PushNotification {
    private String id;
    private String mTitle;
    private String mfecha;
    private String id_pedido;
    private String mDescription;


    public PushNotification() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setFecha(String fecha) {
        this.mfecha = fecha;
    }

    public String getFecha() {
        return mfecha;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setId_pedido(String id_pedido) {
        this.id_pedido = id_pedido;
    }

    public String getId_pedido() {
        return id_pedido;
    }


}