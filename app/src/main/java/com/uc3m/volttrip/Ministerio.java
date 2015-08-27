package com.uc3m.volttrip;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class Ministerio {

    Context context;
    String municipio;
    private List<Gasolinera> gasolineras;
    private String txtResultado;



    public Ministerio(Context contexto){
        context = contexto;
    }

    public void buscarGasolineras(LatLng ultimoPunto){

        try {

            //http://maps.googleapis.com/maps/api/geocode/json?latlng=39.52291,-2.24265&sensor=false
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(ultimoPunto.latitude , ultimoPunto.longitude, 1);
            if (addresses.size() > 0) {
                //Toast.makeText(context, addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                //municipio = addresses.get(0).getLocality();
                municipio = addresses.get(0).getSubAdminArea();
                Toast.makeText(context, municipio, Toast.LENGTH_LONG).show();


            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("PRUEBAS", "catch");
        }

        //http://geoportalgasolineras.es/searchAddress.do?nomProvincia=&nomMunicipio=Barcelona
        // &tipoCarburante=1&rotulo=&tipoVenta=false&nombreVia=&numVia=&codPostal=&economicas=false
        // &tipoBusqueda=0&ordenacion=&posicion=0&yui=true

        //Con Tarea Asíncrona
        CargarXmlTask tarea = new CargarXmlTask();
        tarea.execute("http://geoportalgasolineras.es/searchAddress.do?nomProvincia=" +
                "&nomMunicipio=" + municipio + "&tipoCarburante=1&rotulo=&tipoVenta=false" +
                "&nombreVia=&numVia=&codPostal=&economicas=false&tipoBusqueda=0&ordenacion=" +
                "&posicion=0&yui=true");

    }

    //Tarea Asíncrona para cargar un XML en segundo plano
    private class CargarXmlTask extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {

            RssParserDom saxparser =
                    new RssParserDom(params[0]);

            gasolineras = saxparser.parse();

            return true;
        }

        protected void onPostExecute(Boolean result) {

            txtResultado = "";
            //Tratamos la lista de gasolineras
            //Por ejemplo: escribimos los títulos en pantalla
            for(int i=0; i< gasolineras.size(); i++)
            {
                txtResultado = txtResultado+
                        System.getProperty("line.separator") +
                        gasolineras.get(i).getRotulo();

            }

            Toast.makeText(context, txtResultado, Toast.LENGTH_LONG).show();

        }
    }



}
