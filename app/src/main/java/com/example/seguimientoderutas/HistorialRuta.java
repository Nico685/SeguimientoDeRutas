package com.example.seguimientoderutas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class HistorialRuta {
    public String nombreRuta;
    public String fecha;
    public long duracion;
    public String posicionInicial;
    public String posicionFinal;
    public double distanciaTotal;
    public double latitudInicial;
    public double longitudInicial;

    public HistorialRuta() {
        // Constructor vacío necesario para la deserialización de Firebase
    }

    public HistorialRuta(String nombreRuta, String fecha, long duracion, String posicionInicial, String posicionFinal, double distanciaTotal, double latitudInicial, double longitudInicial) {
        this.nombreRuta = nombreRuta;
        this.fecha = fecha;
        this.duracion = duracion;
        this.posicionInicial = posicionInicial;
        this.posicionFinal = posicionFinal;
        this.distanciaTotal = distanciaTotal;
    }

    // Getters
    public String getNombreRuta() {
        return nombreRuta;
    }

    public String getFecha() {
        return fecha;
    }

    public long getDuracion() {
        return duracion;
    }

    public String getPosicionInicial() {
        return posicionInicial;
    }

    public String getPosicionFinal() {
        return posicionFinal;
    }

    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    public double getLatitudInicial() {
        return latitudInicial;
    }

    public double getLongitudInicial() {
        return longitudInicial;
    }
}
