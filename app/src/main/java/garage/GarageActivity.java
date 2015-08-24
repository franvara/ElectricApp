package garage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.uc3m.electricapp.R;


public class GarageActivity extends ActionBarActivity {

    private Button btnAnadirVehiculo;
    private String miVehiculo;

    private String marca;
    private String modelo;
    private String autonomia;
    private TextView txtMarca;
    private TextView txtModelo;
    private TextView txtAutonomia;
    private TextView txtCentral;
    private ScrollView scroll;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);

        btnAnadirVehiculo = (Button) findViewById(R.id.buttonAnadirVehiculo);


        btnAnadirVehiculo.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(GarageActivity.this, ListaVehiculoActivity.class);
                     startActivityForResult(intent, 0);
                 }
             }
        );

        scroll = (ScrollView) findViewById(R.id.fichaTecnica);

        txtMarca = (TextView) findViewById(R.id.textViewMarca);
        txtModelo = (TextView) findViewById(R.id.textViewModelo);
        txtAutonomia = (TextView) findViewById(R.id.textViewAutonomia);
        txtCentral = (TextView) findViewById(R.id.textCentral);


        //Habilitar el botón de retroceso de la barra de tareas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences ficha = getSharedPreferences("fichaGarage", Context.MODE_PRIVATE);

        marca = ficha.getString("marca", "null");

        if(marca == "null"){
            txtCentral.setVisibility(View.VISIBLE);

        }else{

            txtCentral.setVisibility(View.GONE);

            scroll.setVisibility(View.VISIBLE);

            obtenerPreferencias();
        }

    }

    public void obtenerPreferencias(){
        SharedPreferences ficha = getSharedPreferences("fichaGarage", Context.MODE_PRIVATE);
        marca = ficha.getString("marca", "null");
        modelo = ficha.getString("modelo", "null");
        autonomia = ficha.getString("autonomia", "null");

        cambiarTexto();
    }

    public void cambiarTexto(){
        txtMarca.setText(marca);
        txtModelo.setText(modelo);
        txtAutonomia.setText(autonomia + " km");
    }

    //Este método nos trae la información de para qué se llamó la actividad ListaVehiculoActivity,
    //cuál fue el resultado ("OK" o "CANCELED"), y el intent que nos traerá la
    //información que necesitamos de la segunda actividad.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Comprobamos si el resultado de la segunda actividad es "RESULT_OK".
        if (resultCode == android.app.Activity.RESULT_OK) {
            //Leemos los datos del nuevo alta y lanzamos el alta.
            marca = data.getStringExtra("marca");
            modelo = data.getStringExtra("modelo");
            autonomia = data.getStringExtra("autonomia");

            cambiarTexto();

            //Preferencias para controlar la publicidad
            SharedPreferences prefs = getSharedPreferences("fichaGarage", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("marca", marca);
            editor.putString("modelo", modelo);
            editor.putString("autonomia", autonomia);
            editor.commit();

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
