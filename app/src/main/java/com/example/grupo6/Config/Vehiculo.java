package com.example.grupo6.Config;

import java.io.Serializable;

import java.io.Serializable;

public class Vehiculo implements Serializable {
    private String marca;
    private String modelo;
    private String anio;
    private String color;
    private String combustible;
    private String placa;
    private String userId;

    private String id;

    public Vehiculo() {
        // Constructor vacío requerido por Firestore
    }

    public Vehiculo(String marca, String modelo, String anio, String color, String combustible, String placa, String userId) {
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.combustible = combustible;
        this.placa = placa;
        this.userId = userId; // Asignar el ID del usuario
    }

    public Vehiculo(String id,String marca, String modelo, String anio, String color, String combustible, String placa, String userId) {
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.combustible = combustible;
        this.placa = placa;
        this.userId = userId;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCombustible() {
        return combustible;
    }

    public void setCombustible(String combustible) {
        this.combustible = combustible;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}