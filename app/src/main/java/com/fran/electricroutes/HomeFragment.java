package com.fran.electricroutes;

import android.app.Fragment;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/* Section Home fragment */
public class HomeFragment extends Fragment {

    private GoogleMap mMap; // Puede ser nulo si Google Play services APK no está disponible.
    private CameraUpdate mCamera;   // Vista de la cámara

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
        setUpMapIfNeeded(); // Al ponerlo en el onResume se lee cada vez que cambias de pestaña
                            // Para que no se cargue debería ir en el onCreateView
    }

    private void setUpMapIfNeeded() {
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
        // Añadimos un marcador con posición, título, icono y descripción.
        mMap.addMarker(new MarkerOptions().position(new LatLng(40.030528, -3.596508)).title("CASA")
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
        .snippet("Aquí vivo yo"));
        //Indicamos el la posición de la cámara y el zoom
        mCamera = CameraUpdateFactory.newLatLngZoom(new LatLng(40.030528, -3.596508), 14);
        mMap.animateCamera(mCamera); // Animamos la cámara del mapa.
    }
}
