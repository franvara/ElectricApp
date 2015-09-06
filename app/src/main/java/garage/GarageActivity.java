package garage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.uc3m.etrip.R;

import java.util.ArrayList;
import java.util.List;


public class GarageActivity extends AppCompatActivity {

    // Declare Variables
    ParseUser currentUser = ParseUser.getCurrentUser();
    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    GarageAdapter adapter;
    public List<VehiculoGarage> garagelist = null;

    private TextView txtCentral;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);

        //Referencia a la nueva toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        //Habilitar el botón de retroceso de la barra de tareas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtCentral = (TextView) findViewById(R.id.textCentral);

        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(GarageActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(R.string.garage_name);
            // Set progressdialog message
            mProgressDialog.setMessage(getResources().getString(R.string.load));
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            garagelist = new ArrayList<VehiculoGarage>();

            // Locate the class table named "Cars" and "Garage" in Parse.com
            ParseQuery<ParseObject> queryGarage = new ParseQuery<ParseObject>(
                    "Garage");
            ParseQuery<ParseObject> queryVehicle = new ParseQuery<ParseObject>(
                "Cars");

            try {

                queryGarage.whereEqualTo("user", currentUser.getUsername());
                List<ParseObject> scoreList = queryGarage.find();

                for (int i = 0; i < scoreList.size(); i++) {
                    ParseObject row = scoreList.get(i);
                    String idVehicle = (String) row.get("vehicle");

                    queryVehicle.whereEqualTo("objectId", idVehicle);
                    List<ParseObject> vehicleList = queryVehicle.find();

                    for (int x = 0; x < vehicleList.size(); x++) {
                        ParseObject row2 = vehicleList.get(x);
                        // Locate images in imagen column
                        ParseFile image = (ParseFile) row2.get("image");

                        VehiculoGarage map = new VehiculoGarage();
                        map.setIdVehiculo((String) row2.getObjectId());
                        map.setMarca((String) row2.get("Marca"));
                        map.setModelo((String) row2.get("Modelo"));
                        map.setImage(image.getUrl());
                        map.setAutonomia((int) row2.get("Autonomia"));
                        map.setBateria((int) row2.get("Bateria"));
                        garagelist.add(map);
                    }

                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                CharSequence datos = "Error: " + e.getMessage();
                Log.e("Controlador", "ObtenerAlergiasUsuario - " + datos);
                mProgressDialog.dismiss();
                switch (e.getCode()) {
                    case ParseException.CONNECTION_FAILED:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GarageActivity.this, R.string.network, Toast.LENGTH_LONG).show();
                            }
                        });

                        break;
                    default:
                        Toast.makeText(GarageActivity.this, R.string.default_exception, Toast.LENGTH_LONG).show();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.garagelist);

            // Pass the results into ListViewAdapter.java
            adapter = new GarageAdapter(GarageActivity.this,
                    garagelist);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);

            if(garagelist.isEmpty()){
                txtCentral.setVisibility(View.VISIBLE);
            }else{
                txtCentral.setVisibility(View.GONE);
            }

            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_garage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Acción del botón de retroceso de la barra de tareas
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_añadir:
                Intent intent = new Intent(GarageActivity.this, ListaVehiculoActivity.class);
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Este método nos trae la información de para qué se llamó la actividad ListaVehiculoActivity,
    //cuál fue el resultado ("OK" o "CANCELED"), y el intent que nos traerá la
    //información que necesitamos de la segunda actividad.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Comprobamos si el resultado de la segunda actividad es "RESULT_OK".
        if (resultCode == android.app.Activity.RESULT_OK) {

            // Execute RemoteDataTask AsyncTask
            new RemoteDataTask().execute();

        }
    }

}
