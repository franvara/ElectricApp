package com.fran.electricroutes;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* Section Home fragment */
public class HomeFragment extends Fragment {

    private GoogleMap mMap; // Puede ser nulo si Google Play services APK no está disponible.
    private CameraUpdate mCamera;   // Vista de la cámara
    private PolylineOptions rectOptions;


    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitud, longitud;
    private String city;
    private String direccionOrigen="40.031221,-3.605830";
    private String direccionDestino="40.023088,-3.621666";
    private List<Punto> listaPuntos;
    public double[] latitudes;
    public double[] longitudes;




    public HomeFragment(){}

    private static View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.home, container, false);

        } catch (InflateException e) {
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PRUEBAS", "onResume");

        Log.d("PRUEBAS", "antes del json");

        PeticionJSON peticion = new PeticionJSON();
        peticion.execute("http://maps.googleapis.com/maps/api/directions/json?origin="
                + (direccionOrigen).replace(" ", "+") + "&destination=" +
                (direccionDestino).replace(" ", "+") + "&sensor=false&mode=driving");
        Log.d("PRUEBAS", "despues del json");

        //setUpMapIfNeeded(); // Al ponerlo en el onResume se lee cada vez que cambias de pestaña
        // Para que no se cargue debería ir en el onCreateView
    }

    private void setUpMapIfNeeded() {
        Log.d("PRUEBAS", "setupmapifneeded");

        // Configuramos el objeto GoogleMaps con valores iniciales.
        if (mMap == null ) {
            // Instanciamos el objeto mMap a partir del MapFragment definido con el Id "map".
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Chequeamos si se ha obtenido correctamente una referencia al objeto GoogleMap.
            if (mMap != null) {
                // El objeto GoogleMap ha sido referenciado correctamente, ya podemos manipularlo.
                mMap.setMyLocationEnabled(true);

                setUpMap();
            }
        }
    }

    /*  Aquí podemos añadir marcadores o líneas, añadir detectores o mover la cámara.
    Sólo se debe llamar una vez y cuando estamos seguros de que mMap no es nulo.   */

    private void setUpMap() {
/*        // Añadimos un marcador con posición, título, icono y descripción.
        mMap.addMarker(new MarkerOptions().position(new LatLng(40.030528, -3.596508)).title("CASA")
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
        .snippet("Aquí vivo yo"));
        //Indicamos el la posición de la cámara y el zoom
        mCamera = CameraUpdateFactory.newLatLngZoom(new LatLng(40.030528, -3.596508), 14);
        mMap.animateCamera(mCamera); // Animamos la cámara del mapa.*/

        rectOptions = new PolylineOptions();

        for(int i = 0; i < latitudes.length; i ++) {
            rectOptions.add(new LatLng(latitudes[i], longitudes[i]));
        }

        mMap.addPolyline(rectOptions);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitudes[0], longitudes[0])));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitudes[latitudes.length - 1], longitudes[longitudes.length - 1])));

        mMap.setMyLocationEnabled(true);

        LatLng puntoCentral = new LatLng(latitudes[0], longitudes[0]);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(puntoCentral, 14));


}

/*    public void buscarMiPosicion() {

        Location location = null;

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        String provider = LocationManager.NETWORK_PROVIDER;

        location = locationManager.getLastKnownLocation(provider);

        actualizarPosicion(location);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                actualizarPosicion(location);
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}

        };

        locationManager.requestLocationUpdates(provider, 10000, 15, locationListener);
    }

    public void actualizarPosicion(Location location){
        if(location != null){
            latitud = location.getLatitude();
            longitud = location.getLongitude();

            actualizarDireccionOrigen();
        }
    }

    public void actualizarDireccionOrigen(){

        // La geocodificación es el proceso de transformación de una dirección u otra descripción
        // de un lugar en un (latitud, longitud) de coordenadas.
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitud, longitud, 1);
        } catch (IOException e) {
            Log.e("Error direccion", "Error al transformar dirección.");
        }

        String address = addresses.get(0).getAddressLine(0);
        city = addresses.get(0).getAddressLine(1);
        //String country = addresses.get(0).getAddressLine(2);

        direccionOrigen = address;
    }*/

    //Petición y lectura a la API de Rutas de google
    public class PeticionJSON extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            Log.i("result", result + "");

            Log.d("PRUEBAS", "PeticionJSON onPostExecute");

            try {
                JSONObject jsonObjectResult = new JSONObject(result);

                String status = jsonObjectResult.getString("status");

                if (status.equals("OK")) {

                    listaPuntos = new ArrayList<Punto>();

                    JSONArray jsonArrayRutas = jsonObjectResult.getJSONArray("routes");

                    /** Traversing all routes */
                    for (int i = 0; i < jsonArrayRutas.length(); i++) {
                        JSONArray jsonArrayLegs = ((JSONObject) jsonArrayRutas.get(i)).getJSONArray("legs");

                        /** Traversing all legs */
                        for (int j = 0; j < jsonArrayLegs.length(); j++) {
                            JSONArray jsonArraySteps = ((JSONObject) jsonArrayLegs.get(j)).getJSONArray("steps");

                            Log.i("length array", jsonArraySteps.length() + "");
                            /** Traversing all steps */
                            for (int k = 0; k < jsonArraySteps.length(); k++) {

                                JSONObject jsonObjectDistance = ((JSONObject) jsonArraySteps.get(k)).getJSONObject("distance");
                                //int numMetros = convertirDistanciaMetros(jsonObjectDistance.getString("text"));

                                JSONObject jsonObjectStart = ((JSONObject) jsonArraySteps.get(k)).
                                        getJSONObject("start_location");
                                Punto puntoInicial = new Punto(jsonObjectStart.getDouble("lat"),
                                        jsonObjectStart.getDouble("lng"));

                                JSONObject jsonObjectEnd = ((JSONObject) jsonArraySteps.get(k)).getJSONObject("end_location");
                                Punto puntoFinal = new Punto(jsonObjectEnd.getDouble("lat"),
                                        jsonObjectEnd.getDouble("lng"));

                                listaPuntos.add(puntoInicial);
                                listaPuntos.add(puntoFinal);
                                //segmentarRecta(numMetros, puntoInicial, puntoFinal);
                            }

                        }
                    }

                    latitudes = new double[listaPuntos.size()];
                    longitudes = new double[listaPuntos.size()];
                    for (int i = 0; i < listaPuntos.size(); i++) {
                        latitudes[i] = listaPuntos.get(i).getLatitud();
                        longitudes[i] = listaPuntos.get(i).getLongitud();
                    }
                    Log.d("PRUEBAS", "latitud=" + latitudes.length);
                    setUpMapIfNeeded();


                } else {
                    Log.d("JSON", "No hay resultado. Por favor, intente ajustar mejor la dirección de destino.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("PRUEBAS", "catch");

            }

        }


    }

    public String readJSONFeed(String URL){

        StringBuilder stringBuilder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } else {
                Log.e("JSON", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
