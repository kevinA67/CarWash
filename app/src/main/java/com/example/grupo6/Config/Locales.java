package com.example.grupo6.Config;

public class Locales {

    private String nombre;
    private String ciudad;
    private String direccion;
    private String latitud_gps;
    private String longitud_gps;
//constructores

    public Locales() {
    }

    public Locales(String nombre, String ciudad, String direccion, String latitud_gps, String longitud_gps) {
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.latitud_gps = latitud_gps;
        this.longitud_gps = longitud_gps;
    }

    //setter and getter

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLatitud_gps() {
        return latitud_gps;
    }

    public void setLatitud_gps(String latitud_gps) {
        this.latitud_gps = latitud_gps;
    }

    public String getLongitud_gps() {
        return longitud_gps;
    }

    public void setLongitud_gps(String longitud_gps) {
        this.longitud_gps = longitud_gps;
    }
}
