package com.uc3m.etrip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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

import com.parse.ParseUser;

import garage.GarageActivity;
import login.MainActivity;


public class AppActivity extends AppCompatActivity {

    private TextView txtPointB;
    private TextView distanceText;
    private TextView durationText;
    private TextView resultado;
    private View clearDestino;
    private View lupa;


    private TextView txtVehiculoSelect;
    private FloatingActionButton btnGarage;
    private FloatingActionButton btnStations;
    public boolean btnStationSelect;
    private boolean botones;


    private LocationManager locManager;

    private ProgressDialog progressDialog;
    private List<LatLng> listaPuntos;
    public double[] latitudes;
    public double[] longitudes;

    private GoogleMap mMap; // Puede ser nulo si Google Play services APK no está disponible.
    private PolylineOptions rectOptions;

    private Location myLocation;
    private LocationListener locListener;

    private Marker miVehiculoMarker;

    private int distanceValue;
    private String marca;
    private String modelo;
    private String distancia;
    private double distKm;
    private int autonomia;
    private int autonomiaM;
    private int cuentaM;

    private LatLng ultimoPunto;
    private LatLng puntoSinBat;
    private boolean encontradoPuntoSinbat;



    private int gasto;

    private View info;
    private View infoCar;

    private boolean distanciaSobrepasada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        //Referencia a la nueva toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        initUIElements();

        clearDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPointB.setText("");
            }
        });

        lupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                        locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    busqueda();

                } else {
                    dialogServices();
                }

            }
        });

        comenzarLocalizacion();

        txtPointB.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    busqueda();

                    return true;
                }
                return false;
            }
        });

        btnGarage.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent intent = new Intent(AppActivity.this, GarageActivity.class);
                                             startActivityForResult(intent, 0);
                                         }
                                     }
        );

        btnStations.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {

                                               if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                                                       locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                                                   btnStationSelect = true;
                                                   Ministerio ministerio = new Ministerio();
                                                   ministerio.buscarGasolineras(new LatLng
                                                                   (myLocation.getLatitude(), myLocation.getLongitude()),
                                                           AppActivity.this, btnStationSelect);


                                               } else {
                                                   dialogServices();
                                               }


                                           }
                                       }
        );

        initElementswithServices();

    }

    public void initElementswithServices(){
        if(locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            initMap();

        }else{
            dialogServices();
        }
    }

    public void initUIElements(){
        //Se define un OnClickListener al botón de Calcular Ruta:
        txtPointB = (TextView) findViewById(R.id.inputDestino);
        clearDestino = findViewById(R.id.clearDestinoButton);
        lupa = findViewById(R.id.imageViewBuscarMapa);

        btnGarage = (FloatingActionButton) findViewById(R.id.buttonGarage);
        btnGarage.setBackgroundTintList(
                getResources().getColorStateList(R.color.color_primary));

        btnStations = (FloatingActionButton) findViewById(R.id.buttonStations);
        btnStations.setBackgroundTintList(
                getResources().getColorStateList(R.color.color_primary));
        btnStationSelect = false;

        botones = true;


        txtVehiculoSelect = (TextView) findViewById(R.id.txtVehiculoSeleccion);

        distanceText = (TextView) findViewById(R.id.campoDistance);
        durationText = (TextView) findViewById(R.id.campoDuration);
        resultado = (TextView) findViewById(R.id.resultado);

        info = findViewById(R.id.infoBox);
        infoCar = findViewById(R.id.car_info_box);


        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
    }

    public void comenzarLocalizacion(){

        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        myLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        locListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                myLocation = location;
                if(miVehiculoMarker!=null)
                    miVehiculoMarker.setPosition(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
            }

            public void onProviderDisabled(String providerDisabled){}

            public void onProviderEnabled(String providerEnabled){}

            public void onStatusChanged(String providerChanged, int status, Bundle extras){}
        };

        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);

    }

    public void initMap(){
        // El objeto GoogleMap ha sido referenciado correctamente, ya podemos manipularlo.
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                ocultarTeclado();
                if (botones) {
                    btnGarage.setVisibility(View.GONE);
                    btnStations.setVisibility(View.GONE);
                    botones = false;
                } else {
                    btnGarage.setVisibility(View.VISIBLE);
                    btnStations.setVisibility(View.VISIBLE);
                    botones = true;
                }
            }
        });

        myLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        LatLng puntoCentral = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(puntoCentral, 14));

    }


    @Override
    protected void onResume() {
        super.onResume();

        actualizarPref();
        calcularDistancia();


        if(miVehiculoMarker!=null)
            if(locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                    locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                miVehiculoMarker.setPosition(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));

            }else{
                dialogServices();
            }

    }


    @Override
    public void onBackPressed() {
        if(info.getVisibility() == View.VISIBLE){
            final AlertDialog.Builder dialogExit = new AlertDialog.Builder(AppActivity.this);
            dialogExit.setMessage(getResources().getString(R.string.app_exit));
            dialogExit.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    locManager.removeUpdates(locListener);
                    AppActivity.this.finish();
                }
            });
            dialogExit.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    paramDialogInterface.dismiss();
                }
            });


            AlertDialog alert1 = dialogExit.create();
            alert1.show();
        }else {
            super.onBackPressed();
            locManager.removeUpdates(locListener);
        }

    }

    public void busqueda(){
        //Obtenemos la dirección A y B obtenida por el usuario.
        String pointA = myLocation.getLatitude() + " " + myLocation.getLongitude();
        String pointB = txtPointB.getText().toString();

        if (pointB.equals("")) {
            Toast.makeText(AppActivity.this, R.string.campos_obligatorios, Toast.LENGTH_LONG).show();
        } else {
            String avoid = restrictions();
            PeticionJSON peticion = new PeticionJSON();
            peticion.execute("https://maps.googleapis.com/maps/api/directions/json?origin="
                    + (pointA).replace(" ", "+") + "&destination=" +
                    (pointB).replace(" ", "+") + "&mode=driving"
                    + "&avoid=" + avoid + "&region=es&language=es");
        }
        ocultarTeclado();

    }

    //Petición y lectura a la API de Rutas de google
    public class PeticionJSON extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AppActivity.this);
            progressDialog.setMessage(AppActivity.this.getString(R.string.calculating));
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.i("PRUEBAS", "JSON onProgressUpdate");
        }

        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                ultimoPunto = null;
                puntoSinBat = null;
                encontradoPuntoSinbat = false;

                JSONObject jsonObjectResult = new JSONObject(result);

                String status = jsonObjectResult.getString("status");

                if (status.equals("OK")) {

                    listaPuntos = new ArrayList<LatLng>();

                    JSONArray jsonArrayRutas = jsonObjectResult.getJSONArray("routes");

                    /** Recorriendo todas las rutas */
                    for (int i = 0; i < jsonArrayRutas.length(); i++) {
                        JSONArray jsonArrayLegs = ((JSONObject) jsonArrayRutas.get(i)).getJSONArray("legs");

                        //Recogiendo la distancia en texto y en metros
                        String distance = (String) ((JSONObject) ((JSONObject) jsonArrayLegs.get(i)).get("distance")).get("text");
                        distanceText.setText(distance);
                        distanceValue = (int) ((JSONObject) ((JSONObject) jsonArrayLegs.get(i)).get("distance")).get("value");

                        //Recogiendo la duracion en segundos
                        int duration = (int) ((JSONObject) ((JSONObject) jsonArrayLegs.get(i)).get("duration")).get("value");
                        durationText.setText(getDurationString(duration));

                        /** Recorriendo todas las fases */
                        for (int j = 0; j < jsonArrayLegs.length(); j++) {
                            JSONArray jsonArraySteps = ((JSONObject) jsonArrayLegs.get(j)).getJSONArray("steps");

                            Log.i("length array", jsonArraySteps.length() + "");

                            //FRAN
                            SharedPreferences ficha = getSharedPreferences("fichaGarage", Context.MODE_PRIVATE);
                            autonomia = ficha.getInt("autonomia", 0);
                            autonomiaM = autonomia *1000;
                            cuentaM = 0;
                            distanciaSobrepasada = false;

                            /** Recorriendo todos los pasos */
                            for (int k = 0; k < jsonArraySteps.length(); k++) {

                                //Para saber cual es el ultimo punto donde ha llegado con el 100% de
                                //la bateria
                                double stepM = 0;
                                stepM = (int) ((JSONObject) ((JSONObject) jsonArraySteps.get(k)).get("distance")).get("value");
                                cuentaM += stepM;
                                if(cuentaM > autonomiaM && autonomiaM!=0 && !distanciaSobrepasada){
                                    ultimoPunto = listaPuntos.get((listaPuntos.size())-2);
                                    //ultimoPunto = new LatLng(40.043692, -3.581320); Aranjuez
                                    distanciaSobrepasada = true;
                                    cuentaM -= stepM;
                                }


                                String polyline = "";
                                polyline = (String) ((JSONObject) ((JSONObject) jsonArraySteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                for (int z = 0; z < list.size(); z++) {

                                    listaPuntos.add(new LatLng(list.get(z).latitude, list.get(z).longitude));

                                }
                            }

                        }
                    }
                    latitudes = new double[listaPuntos.size()];
                    longitudes = new double[listaPuntos.size()];
                    for (int i = 0; i < listaPuntos.size(); i++) {
                        latitudes[i] = listaPuntos.get(i).latitude;
                        longitudes[i] = listaPuntos.get(i).longitude;
                    }
                    //Acción
                    pintarRuta(latitudes, longitudes);
                    calcularDistancia();

                    progressDialog.dismiss();

                    Ministerio ministerio = new Ministerio();
                    if(puntoSinBat != null && marca != "null"){
                        mMap.addMarker(new MarkerOptions().position(puntoSinBat)
                                .title(getString(R.string.marker_bat))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_off)));
                        ministerio.buscarGasolineras(puntoSinBat, AppActivity.this, btnStationSelect);

                    }


                } else {
                    Toast.makeText(AppActivity.this,
                            "No existen resultados. Por favor, intente ajustar mejor la dirección de destino.",
                            Toast.LENGTH_LONG).show();
                    miVehiculoMarker = null;
                    progressDialog.dismiss();
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("PRUEBAS", "catch");
            }
        }
    }

    public String readJSONFeed(String URL) {

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AppActivity.this, R.string.network, Toast.LENGTH_LONG).show();
                }
            });
            progressDialog.dismiss();
        }
        return stringBuilder.toString();
    }


    /**
     * Método para decodificar los puntos polyline
     * Cortesía de: jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        Location locAnterior = new Location("");
        Location locSiguiente = new Location("");

        if (distanciaSobrepasada){
            locAnterior.setLatitude(ultimoPunto.latitude);
            locAnterior.setLongitude(ultimoPunto.longitude);
        }

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));


            locSiguiente.setLatitude(p.latitude);
            locSiguiente.setLongitude(p.longitude);


            if(distanciaSobrepasada){

                Location targetLocation = new Location("");
                targetLocation.setLatitude(0.0d);
                targetLocation.setLongitude(0.0d);

                float distPuntos = locAnterior.distanceTo(locSiguiente);
                cuentaM += distPuntos;

                if(cuentaM > autonomiaM && !encontradoPuntoSinbat) {
                    puntoSinBat = new LatLng(locAnterior.getLatitude(), locAnterior.getLongitude());
                    encontradoPuntoSinbat = true;
                }

            }

            locAnterior.setLatitude(p.latitude);
            locAnterior.setLongitude(p.longitude);

            poly.add(p);
        }

        return poly;
    }


    private static void zoomToCoverAllMarkers(ArrayList<LatLng> latLngList, final GoogleMap googleMap)
    {
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (LatLng marker : latLngList)   builder.include(marker);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
            }
        });
    }


    public void pintarRuta(double[] latitudes, double[] longitudes){
/*
        //Indicamos el la posición de la cámara y el zoom
        mCamera = CameraUpdateFactory.newLatLngZoom(new LatLng(40.030528, -3.596508), 14);
        mMap.animateCamera(mCamera); // Animamos la cámara del mapa.*/

        mMap.clear(); //Borra la ruta al suspenderse la actividad, para que no pinte varias rutas.

        rectOptions = new PolylineOptions().color(0xFF303F9F);

        for (int i = 0; i < latitudes.length; i++) {
            rectOptions.add(new LatLng(latitudes[i], longitudes[i]));
        }

        mMap.addPolyline(rectOptions);

        LatLng latLngInicio = new LatLng(latitudes[0], longitudes[0]);
        LatLng latLngFin = new LatLng(latitudes[latitudes.length - 1], longitudes[longitudes.length - 1]);

        mMap.addMarker(new MarkerOptions().position(latLngInicio)
                .title(getString(R.string.marker_inicio))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_start)));
        mMap.addMarker(new MarkerOptions().position(latLngFin)
                .title(getString(R.string.marker_final))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_end)));

        // Añadimos un marcador con posición, título, icono y descripción.
        miVehiculoMarker = mMap.addMarker(new MarkerOptions().position(latLngInicio)
                .title(getString(R.string.posición_actual))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_car)));
        //.snippet("Subtítulo")

        ArrayList<LatLng> latLngList = new ArrayList<>();
        latLngList.add(latLngInicio);
        latLngList.add(latLngFin);

        zoomToCoverAllMarkers(latLngList, mMap);
    }

    public void actualizarPref(){
        SharedPreferences ficha = getSharedPreferences("fichaGarage", Context.MODE_PRIVATE);
        marca = ficha.getString("marca", "null");

        if(marca == "null"){
            infoCar.setVisibility(View.GONE);
            txtVehiculoSelect.setText("Seleccione un vehículo.");
        }else {
            modelo = ficha.getString("modelo", "null");

            txtVehiculoSelect.setText(marca + " " + modelo);
            infoCar.setVisibility(View.VISIBLE);

        }
    }

    public void calcularDistancia(){
        distancia = distanceText.getText().toString();

        if(!(distancia.equals("DISTANCIA"))){

            info.setVisibility(View.VISIBLE);

            if(marca != "null"){

                distKm = ((double) distanceValue) / 1000;
                Calculo calculo = new Calculo(AppActivity.this);

                gasto = round(calculo.gastoBateria(distKm));

                resultado.setText(String.valueOf(gasto) + " %");

                if(gasto == 0) {
                    resultado.setText("-");
                    resultado.setTextColor(Color.BLACK);
                }
                else if(gasto >0 && gasto <=60) resultado.setTextColor(Color.GREEN);
                else if(gasto >60 && gasto <90) resultado.setTextColor(getResources().getColor(R.color.Lime));
                else if(gasto >=90) resultado.setTextColor(Color.RED);

            }else{
                resultado.setText("-");
                resultado.setTextColor(Color.BLACK);
            }
        }else{
            info.setVisibility(View.GONE);
        }

    }

    public void ocultarTeclado(){
        InputMethodManager imm = (InputMethodManager) getSystemService(
                AppActivity.this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private int round(double d){
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if(result<0.5){
            return d<0 ? -i : i;
        }else{
            return d<0 ? -(i+1) : i+1;
        }
    }

    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        //seconds = seconds % 60;

        if(hours == 00) return twoDigitString(minutes) + " min";
        else return twoDigitString(hours) + " h " + twoDigitString(minutes) + " min";
        //return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
        //return twoDigitString(hours) + " " + R.string.hours + " "+ twoDigitString(minutes) + " " + R.string.minutes;

    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    public void setMarkerStation(List<Gasolinera> result){

        ArrayList<LatLng> StationList = new ArrayList<>();

        for(Gasolinera gasolinera:result){
            mMap.addMarker(new MarkerOptions().position(gasolinera.getlatLong())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_station))
                            .title(gasolinera.getInfo())
            );

            StationList.add(gasolinera.getlatLong());

        }

        if(btnStationSelect){
            zoomToCoverAllMarkers(StationList, mMap);
        }
        btnStationSelect = false;

    }

    public void dialogServices(){

        // notify user
        AlertDialog.Builder dialog = new AlertDialog.Builder(AppActivity.this);
        dialog.setTitle(getResources().getString(R.string.gps_not_found_title));
        dialog.setMessage(getResources().getString(R.string.gps_not_found_message));
        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(myIntent, 1);
                //get gps
            }
        });
        dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                initElementswithServices();

            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //When you touch outside of dialog bounds,
                //the dialog gets canceled and this method executes.

                if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                        locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    dialogServices();
                }
            }
        });


        AlertDialog alert1 = dialog.create();
        alert1.show();

    }

    public String restrictions(){

        String restriction = "";
        final SharedPreferences prefMenu = getSharedPreferences("menu", Context.MODE_PRIVATE);

        int i = 0;

        if(prefMenu.getBoolean("autopista", false)){
            restriction += "highways";
            i++;
        }

        if(prefMenu.getBoolean("peaje", false)){

            if(i>0) restriction += "%7C";

            i++;
            restriction += "tolls";
        }

        if(prefMenu.getBoolean("ferri", false)){

            if(i>0) restriction += "%7C";

            restriction +="ferries";
        }

        return restriction;
    }

    //Este método nos trae la información de para qué se llamó la actividad ListaVehiculoActivity,
    //cuál fue el resultado ("OK" o "CANCELED"), y el intent que nos traerá la
    //información que necesitamos de la segunda actividad.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Comprobamos si el resultado de la segunda actividad es "RESULT_OK".
        if (resultCode == android.app.Activity.RESULT_OK) {

        }

        if (requestCode == 1){ //Comprobación Localización
            initElementswithServices();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.options:
                menuOptions();
                return true;

            case R.id.info_app:
                startActivity(new Intent(this, InfoActivity.class));
                return true;

            case R.id.log_out:
                ParseUser.getCurrentUser().logOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void menuOptions(){

        AlertDialog menuDialog;
        final CharSequence[] items = {getString(R.string.avoid_highways),
                getString(R.string.avoid_tools),getString(R.string.avoid_ferries)};
        // arraylist to keep the selected items
        final ArrayList seletedItems=new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.menu_options);

        final SharedPreferences prefMenu = getSharedPreferences("menu", Context.MODE_PRIVATE);
        final boolean [] evitar = new  boolean[3];
        evitar[0] = prefMenu.getBoolean("autopista", false);
        evitar[1] = prefMenu.getBoolean("peaje", false);
        evitar[2] = prefMenu.getBoolean("ferri", false);

        builder.setMultiChoiceItems(items, evitar,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            // write your code when user checked the checkbox
                            seletedItems.add(indexSelected);
                            evitar[indexSelected] = true;
                        } else {
                            evitar[indexSelected] = false;
                            if (seletedItems.contains(indexSelected)) {
                                // Else, if the item is already in the array, remove it
                                // write your code when user Uchecked the checkbox
                                seletedItems.remove(Integer.valueOf(indexSelected));
                            }
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton(R.string.menu_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here

                        SharedPreferences.Editor editorMenu = prefMenu.edit();

                        for(int i=0; i < evitar.length; i++){

                            switch (i){
                                case 0:
                                    if(evitar[0]) editorMenu.putBoolean("autopista", true);
                                    else editorMenu.putBoolean("autopista", false);
                                    break;
                                case 1:
                                    if(evitar[1]) editorMenu.putBoolean("peaje", true);
                                    else editorMenu.putBoolean("peaje", false);
                                    break;
                                case 2:
                                    if(evitar[2]) editorMenu.putBoolean("ferri",true);
                                    else editorMenu.putBoolean("ferri",false);
                                    break;
                            }
                        }
                        editorMenu.commit();


                    }
                })
                .setNegativeButton(R.string.menu_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel

                    }
                });

        menuDialog = builder.create();//AlertDialog dialog; create like this outside onClick
        menuDialog.show();
    }

}
