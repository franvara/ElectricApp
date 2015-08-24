package com.fran.electricapp;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class GarageFragment extends Fragment {
    private AppActivity mainAct;
    public static final String TAG = "garage";

    private TextView Garage;
    private String garageSet;

    private TextView MarcaText;
    private String Marca;
    private TextView ModeloText;
    private String Modelo;

    private ImageView carView;
    private Image imagen;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.garage, container, false);
        //Garage = (TextView) rootView.findViewById(R.id.marcaText);
        MarcaText = (TextView) rootView.findViewById(R.id.marcaText);
        ModeloText = (TextView) rootView.findViewById(R.id.modeloText);
        carView = (ImageView) rootView.findViewById(R.id.imageCar);

        Log.i("PRUEBAS", String.valueOf(((AppActivity) getActivity()).getGarageUser()));


        return rootView;
    }

    public void onResume() {
        super.onResume();
        garageSet = ((AppActivity) getActivity()).getGarageUser();

        //Garage.setText(garageSet);

        recogeInfo();

        //Se define un OnClickListener al botón de Calcular Ruta:
        getView().findViewById(R.id.add_fab).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    //Este método nos trae la información de para qué se llamó la actividad AltaAlergia,
    //cuál fue el resultado ("OK" o "CANCELED"), y el intent que nos traerá la
    //información que necesitamos de la segunda actividad.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Comprobamos si el resultado de la segunda actividad es "RESULT_OK".
        if (resultCode == android.app.Activity.RESULT_OK) {
            //Leemos los datos del nuevo alta y lanzamos el alta.
            String garage = data.getStringExtra("garage");
            ((AppActivity) getActivity()).setGarageUser(garage);

        }
    }

    public void recogeInfo(){
        // Locate the class table named "Cars" in Parse.com
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Cars");
        query.whereEqualTo("Modelo", ((AppActivity) getActivity()).getGarageUser());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("score", "The getFirst request failed.");
                } else {
                    Log.d("score", "Retrieved the object.");
                    String Marca = object.getString("Marca");
                    MarcaText.setText(Marca);
                    String Modelo = object.getString("Modelo");
                    ModeloText.setText(Modelo);


                }
            }
        });
    }
}
