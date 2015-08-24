package com.uc3m.electricapp;

import android.content.Context;
import android.content.SharedPreferences;

public class Calculo {

    Context context;
    public int bateria;
    public int autonomia;
    public double gastoBateria;

    public Calculo (Context contexto){
        context = contexto;
    }

    public double gastoBateria(double distancia){
        SharedPreferences ficha = context.getSharedPreferences("fichaGarage", Context.MODE_PRIVATE);
        bateria = ficha.getInt("bateria", 0);
        autonomia = ficha.getInt("autonomia", 0);

        if(bateria!=0 && autonomia!=0){
            double consumo = ((double) bateria * 100 ) / (double) autonomia;
            double consumoRuta = (consumo * distancia)/100;
            gastoBateria = (consumoRuta*100) / bateria;
        }else{
            gastoBateria=0;
        }

        return gastoBateria;
    }

}
