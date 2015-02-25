package com.fran.electricroutes;

//Clase para almacenar todos los puntos de la ruta del API de Rutas de Google

public class Punto {

	private double latitud;
	private double longitud;
	
	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public Punto(double latitud, double longitud) {
		super();
		this.latitud = latitud;
		this.longitud = longitud;
	}
	
	
}
