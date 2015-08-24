package com.uc3m.electricapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
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

import com.parse.Parse;

import garage.GarageActivity;

public class MainActivity extends ActionBarActivity {

    private TextView txtPointB;
    private TextView distanceText;
    private TextView durationText;
    private ImageButton btnCalcRoute;
    private View clearDestino;

    private TextView txtVehiculoSelect;
    private ImageButton btnGarage;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ParseCode
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "8TUN8Pt1gNbKn41u8GwyY8coY3SyCoG4OyvFSPj8", "ytW53W5QtsXzpsL4XM82qxwKxEB3TEAnJt7eMDpx");

        initUIElements();

        txtPointB.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clearDestino.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        clearDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPointB.setText("");
                clearDestino.setVisibility(View.INVISIBLE);
            }
        });

        txtPointB.addTextChangedListener(new TextWatcherOrigenDestino(clearDestino));

        comenzarLocalizacion();

        txtPointB.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    //Obtenemos la dirección A y B obtenida por el usuario.
                    String pointA = myLocation.getLatitude() + " " + myLocation.getLongitude();
                    String pointB = txtPointB.getText().toString();

                    if (pointB.equals("")) {
                        Toast.makeText(MainActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
                    } else {
                        PeticionJSON peticion = new PeticionJSON();
                        peticion.execute("http://maps.googleapis.com/maps/api/directions/json?origin="
                                + (pointA).replace(" ", "+") + "&destination=" +
                                (pointB).replace(" ", "+") + "&sensor=false&mode=driving");
                    }
                    ocultarTeclado();

                    return true;
                }
                return false;
            }
        });

        //Se define un OnClickListener al botón de Calcular Ruta:
        btnCalcRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }
        );

        btnGarage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, GarageActivity.class);
                    startActivity(intent);
                }
             }
        );
        initMap();
    }

    public void initUIElements(){
        //Se define un OnClickListener al botón de Calcular Ruta:
        btnCalcRoute = (ImageButton) findViewById(R.id.btnCalcRoute);
        txtPointB = (TextView) findViewById(R.id.inputDestino);
        clearDestino = findViewById(R.id.clearDestinoButton);

        btnGarage = (ImageButton) findViewById(R.id.btnVehiculoSeleccion);
        txtVehiculoSelect = (TextView) findViewById(R.id.txtVehiculoSeleccion);

        distanceText = (TextView) findViewById(R.id.campoDistance);
        durationText = (TextView) findViewById(R.id.campoDuration);

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
            }
        });

        LatLng puntoCentral = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(puntoCentral, 14));
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onStop() {
        super.onStop();

        locManager.removeUpdates(locListener);
    }

    //Petición y lectura a la API de Rutas de google
    public class PeticionJSON extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(MainActivity.this.getString(R.string.calculating));
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
                JSONObject jsonObjectResult = new JSONObject(result);

                String status = jsonObjectResult.getString("status");

                if (status.equals("OK")) {

                    listaPuntos = new ArrayList<LatLng>();

                    JSONArray jsonArrayRutas = jsonObjectResult.getJSONArray("routes");

                    /** Recorriendo todas las rutas */
                    for (int i = 0; i < jsonArrayRutas.length(); i++) {
                        JSONArray jsonArrayLegs = ((JSONObject) jsonArrayRutas.get(i)).getJSONArray("legs");

                        //Recogiendo la distancia en km
                        String distance = (String) ((JSONObject) ((JSONObject) jsonArrayLegs.get(i)).get("distance")).get("text");
                        distanceText.setText(distance);
                        String duration = (String) ((JSONObject) ((JSONObject) jsonArrayLegs.get(i)).get("duration")).get("text");
                        durationText.setText(duration);

                        /** Recorriendo todas las fases */
                        for (int j = 0; j < jsonArrayLegs.length(); j++) {
                            JSONArray jsonArraySteps = ((JSONObject) jsonArrayLegs.get(j)).getJSONArray("steps");

                            Log.i("length array", jsonArraySteps.length() + "");
                            /** Recorriendo todos los pasos */
                            for (int k = 0; k < jsonArraySteps.length(); k++) {

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

                    progressDialog.dismiss();
                    //Acción
                    pintarRuta(latitudes, longitudes);
                } else {
                    Toast.makeText(MainActivity.this,
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

        rectOptions = new PolylineOptions();

        for (int i = 0; i < latitudes.length; i++) {
            rectOptions.add(new LatLng(latitudes[i], longitudes[i]));
        }

        mMap.addPolyline(rectOptions);

        LatLng latLngInicio = new LatLng(latitudes[0], longitudes[0]);
        LatLng latLngFin = new LatLng(latitudes[latitudes.length - 1], longitudes[longitudes.length - 1]);

        mMap.addMarker(new MarkerOptions().position(latLngInicio));
        mMap.addMarker(new MarkerOptions().position(latLngFin));

        // Añadimos un marcador con posición, título, icono y descripción.
        miVehiculoMarker = mMap.addMarker(new MarkerOptions().position(latLngInicio).title("Posición Actual")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_car)));
        //.snippet("Subtítulo")

        ArrayList<LatLng> latLngList = new ArrayList<>();
        latLngList.add(latLngInicio);
        latLngList.add(latLngFin);

        zoomToCoverAllMarkers(latLngList, mMap);
    }

    public void ocultarTeclado(){
        InputMethodManager imm = (InputMethodManager) getSystemService(
                MainActivity.this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
