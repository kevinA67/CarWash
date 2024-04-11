package com.example.grupo6.Config;

public class Servicios {

    private String numOrden;
    private String nombre;
    private String cantidad;
    private String precio;
    //Constructores
    public Servicios(String numOrden, String nombre, String cantidad, String precio) {
        this.numOrden = numOrden;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public Servicios(String nombre, String precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNumOrden() {
        return numOrden;
    }

    public void setNumOrden(String numOrden) {
        this.numOrden = numOrden;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
}
