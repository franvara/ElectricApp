package garage;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.uc3m.electricapp.R;

import java.util.ArrayList;
import java.util.List;


public class ListaVehiculoActivity extends ActionBarActivity {

    // Declare Variables
    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ListViewAdapter adapter;
    private List<Vehiculo> vehiclelist = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vehiculo);

        //Referencia a la nueva toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        //Habilitar el botón de retroceso de la barra de tareas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();
    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ListaVehiculoActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("ElectricApp");
            // Set progressdialog message
            mProgressDialog.setMessage("Cargando...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            vehiclelist = new ArrayList<Vehiculo>();
            try {
                // Locate the class table named "Cars" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "Cars");
                // Locate the column named "Marca" in Parse.com and order list
                // by ascending
                query.orderByAscending("Marca");
                ob = query.find();
                for (ParseObject vehicle : ob) {
                    // Locate images in imagen column
                    ParseFile image = (ParseFile) vehicle.get("image");

                    Vehiculo map = new Vehiculo();
                    map.setIdVehiculo((String) vehicle.getObjectId());
                    map.setMarca((String) vehicle.get("Marca"));
                    map.setModelo((String) vehicle.get("Modelo"));
                    map.setImage(image.getUrl());
                    map.setAutonomia((int) vehicle.get("Autonomia"));
                    vehiclelist.add(map);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                mProgressDialog.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(ListaVehiculoActivity.this,
                    vehiclelist);
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
