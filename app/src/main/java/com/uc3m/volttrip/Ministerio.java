package com.uc3m.volttrip;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Ministerio {

    Context context;
    private ProgressDialog progressDialogStation;


    String municipio;
    public List<Gasolinera> result;
    private String txtResultado;

    private String web;

    private String pagina;

    private boolean btnStation;



    public void buscarGasolineras(LatLng ultimoPunto, Context AppContext, boolean btnStationSelect){

        context = AppContext;
        btnStation = btnStationSelect;
        //http://geoportalgasolineras.es/searchAddress.do?nomProvincia=&nomMunicipio=Barcelona
        // &tipoCarburante=1&rotulo=&tipoVenta=false&nombreVia=&numVia=&codPostal=&economicas=false
        // &tipoBusqueda=0&ordenacion=&posicion=0&yui=true

        municipio = obtenerMunicipio(ultimoPunto);

        pagina = "http://www.movele.es/index.php/mod.puntos/mem.mapa/relmenu.20/regini.0/numregs.330/filtro." + municipio;
        PeticionStation peticionS = new PeticionStation();
        peticionS.execute();

    }

    public class PeticionStation extends AsyncTask<String, Void, List<Gasolinera>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(btnStation) {
                progressDialogStation = new ProgressDialog(context);
                progressDialogStation.setMessage(context.getString(R.string.search_station));
                progressDialogStation.show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.i("PRUEBAS", "JSON onProgressUpdate");
        }

        @Override
        protected List<Gasolinera> doInBackground(String... params) {
            List<Gasolinera> lista = new ArrayList< Gasolinera>();

            String url = pagina;
            String url2= "http://www.movele.es/";

            try {
                Document doc = Jsoup.connect(url).get();
                Elements divs = doc.select("div.lst-pto-rec");

                txtResultado = "";

                for(int i = 0; i < divs.size(); i++){
                    Element fila = divs.get(i);
                    Elements as = fila.select("a");
                    Element a1 = as.get(0);
                    Element a2 = as.get(1);

                    String info = a1.text();

                    String href = a2.attr("href");

                    Document doc2 = Jsoup.connect(url2 + href).get();
                    String mapa = doc2.select("img").attr("src");

                    LatLng coordenadas = extraerLatLong(mapa);

                    lista.add(new Gasolinera(info, coordenadas));

                }



            } catch (Exception e) {
                lista = null;
            }



            return lista;
        }

        @Override
        protected void onPostExecute(List<Gasolinera> gasolineras) {

            if(btnStation)
                progressDialogStation.dismiss();

            if(gasolineras.size() == 0){
                Toast.makeText(context, R.string.noStation, Toast.LENGTH_LONG).show();

            }else{
                ((AppActivity) context).setMarkerStation(gasolineras);
            }


        }
    }

    public String obtenerMunicipio(LatLng posicion){
        String result = "";

        try {

            //http://maps.googleapis.com/maps/api/geocode/json?latlng=39.52291,-2.24265&sensor=false

            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(posicion.latitude , posicion.longitude, 1);
            if (addresses.size() > 0) {
                /*String a = addresses.get(0).getAdminArea();
                String b = addresses.get(0).getFeatureName();
                String c = addresses.get(0).getLocality();
                String d = addresses.get(0).getCountryName();*/

                result = addresses.get(0).getSubAdminArea();

                //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("PRUEBAS", "catch");
        }

        return result;
    }

    public LatLng extraerLatLong(String texto){

        //http://maps.googleapis.com/maps/api/staticmap?center=40.4581338350493,-3.6856609582901
        //&zoom=18&size=640x640&maptype=satellite&markers=40.4581338350493,-3.6856609582901&sensor=true

        LatLng latLng;

        //http://maps.googleapis.com/maps/api/staticmap?center=40.329658,-3.729704&amp;zoom=18&amp;
        // size=640x640&amp;maptype=satellite&amp;markers=40.329658,-3.729704&amp;sensor=true
        int latitud = texto.indexOf("center=");
        int longitud = texto.indexOf(",");
        int amp = texto.indexOf("&");

        String lat = texto.substring(latitud + 7, longitud);
        String lng = texto.substring(longitud + 1,amp);

        latLng = new LatLng (Double.parseDouble(lat), Double.parseDouble(lng));

        return latLng;


    }


}
