package com.example.grupo6.Config;

public class Perfil {
    private String nombre;
    private String correo;
    private String celular;
    private String contrasena;

    public Perfil() {
        // Constructor vacío requerido para Firebase
    }

    public Perfil(String nombre, String correo, String celular, String contrasena) {
        this.nombre = nombre;
        this.correo = correo;
        this.celular = celular;
        this.contrasena = contrasena;
    }

    // Getters y setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}