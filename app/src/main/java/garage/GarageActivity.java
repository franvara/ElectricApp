package garage;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.uc3m.electricapp.R;

import java.util.ArrayList;
import java.util.List;


public class GarageActivity extends ActionBarActivity {

    // Declare Variables
    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    GarageAdapter adapter;
    private List<VehiculoGarage> garagelist = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);

        //Referencia a la nueva toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        //Habilitar el botón de retroceso de la barra de tareas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();
    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(GarageActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Garage");
            // Set progressdialog message
            mProgressDialog.setMessage("Cargando...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            garagelist = new ArrayList<VehiculoGarage>();

                // Locate the class table named "Cars" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "Cars");

            try {

                query.whereEqualTo("objectId", "uVEV1bkIn5");

                List<ParseObject> scoreList = query.find();

                for (int i = 0; i < scoreList.size(); i++) {
                    ParseObject row = scoreList.get(i);

                    // Locate images in imagen column
                    ParseFile image = (ParseFile) row.get("image");

                    VehiculoGarage map = new VehiculoGarage();
                    map.setIdVehiculo((String) row.getObjectId());
                    map.setMarca((String) row.get("Marca"));
                    map.setModelo((String) row.get("Modelo"));
                    map.setImage(image.getUrl());
                    map.setAutonomia((int) row.get("Autonomia"));
                    garagelist.add(map);
                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                CharSequence datos = "Error: " + e.getMessage();
                Log.e("Controlador", "ObtenerAlergiasUsuario - " + datos);

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
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Acción del botón de retroceso de la barra de tareas
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
