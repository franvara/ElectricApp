package com.fran.electricapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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


public class GoToFragment extends Fragment {

    private TextView txtPointA;
    private TextView txtPointB;
    private Button btnCalcRoute;

    //private List<Punto> listaPuntos;
    private List<LatLng> listaPuntos;
    public double[] latitudes;
    public double[] longitudes;

    private ProgressDialog progressDialog;

    public static final String TAG = "go_to";

    private AppActivity mainAct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.go_to, container, false);
        //Se define un OnClickListener al botón de Calcular Ruta:
        btnCalcRoute = (Button) rootView.findViewById(R.id.btnCalcRoute);

        txtPointA = (TextView) rootView.findViewById(R.id.textPointA);
        txtPointA.setText("Aranjuez");
        txtPointB = (TextView) rootView.findViewById(R.id.textPointB);
        txtPointB.setText("Leganes");


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Se define un OnClickListener al botón de Calcular Ruta:
        btnCalcRoute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(GoToFragment.this.mainAct.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                //Obtenemos la dirección A y B obtenida por el usuario.
                String pointA = txtPointA.getText().toString();
                String pointB = txtPointB.getText().toString();

                if (pointA.equals("") || pointB.equals("")) {
                    Toast.makeText(GoToFragment.this.mainAct, "Todos los campos son obligatorios",
                            Toast.LENGTH_LONG).show();
                } else {
                    PeticionJSON peticion = new PeticionJSON();
                    peticion.execute("http://maps.googleapis.com/maps/api/directions/json?origin="
                            + (pointA).replace(" ", "+") + "&destination=" +
                            (pointB).replace(" ", "+") + "&sensor=false&mode=driving");
                }
            }
        });
    }

    //Petición y lectura a la API de Rutas de google
    public class PeticionJSON extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GoToFragment.this.mainAct);
            progressDialog.setMessage(GoToFragment.this.mainAct.getString(R.string.calculating));
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

                    //listaPuntos = new ArrayList<Punto>();
                    listaPuntos = new ArrayList<LatLng>();

                    JSONArray jsonArrayRutas = jsonObjectResult.getJSONArray("routes");
                    //String encodedPoints = ((JSONObject) jsonArrayRutas.get(0)).getJSONArray("overview_polyline").getJSONArray("points");

                    /** Recorriendo todas las rutas */
                    for (int i = 0; i < jsonArrayRutas.length(); i++) {
                        JSONArray jsonArrayLegs = ((JSONObject) jsonArrayRutas.get(i)).getJSONArray("legs");

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

                                /*JSONObject jsonObjectDistance = ((JSONObject) jsonArraySteps.get(k)).getJSONObject("distance");
                                int numMetros = convertirDistanciaMetros(jsonObjectDistance.getString("text"));*/

                                /*JSONObject jsonObjectStart = ((JSONObject) jsonArraySteps.get(k)).
                                        getJSONObject("start_location");
                                Punto puntoInicial = new Punto(jsonObjectStart.getDouble("lat"),
                                        jsonObjectStart.getDouble("lng"));

                                JSONObject jsonObjectEnd = ((JSONObject) jsonArraySteps.get(k)).getJSONObject("end_location");
                                Punto puntoFinal = new Punto(jsonObjectEnd.getDouble("lat"),
                                        jsonObjectEnd.getDouble("lng"));*/

                                //listaPuntos.add(puntoInicial);
                                //listaPuntos.add(puntoFinal);
                            }

                        }
                    }

                    latitudes = new double[listaPuntos.size()];
                    longitudes = new double[listaPuntos.size()];
                    for (int i = 0; i < listaPuntos.size(); i++) {
                        latitudes[i] = listaPuntos.get(i).latitude;
                        longitudes[i] = listaPuntos.get(i).longitude;
                    }

                    ((AppActivity) getActivity()).setArrayLatitudes(latitudes);
                    ((AppActivity) getActivity()).setArrayLongitudes(longitudes);

                    progressDialog.dismiss();

                    ((AppActivity) getActivity()).onNavigationDrawerItemSelected(2);


                } else {
                    Toast.makeText(GoToFragment.this.mainAct,
                            "No existen resultados. Por favor, intente ajustar mejor la dirección de destino.",
                            Toast.LENGTH_LONG).show();
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Obtenemos la referencia a la Actividad principal
        this.mainAct = (AppActivity) activity;
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
}
