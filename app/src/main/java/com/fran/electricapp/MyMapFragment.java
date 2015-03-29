package com.fran.electricapp;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MyMapFragment extends Fragment {
    public static final String TAG = "map";
    private static View rootView;
    private GoogleMap mMap; // Puede ser nulo si Google Play services APK no está disponible.
    private CameraUpdate mCamera;   // Vista de la cámara
    private PolylineOptions rectOptions;
    public double[] aLatitudes;
    public double[] aLongitudes;

    ArrayList<LatLng> locations;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.my_map, container, false);

        } catch (InflateException e) {
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        aLatitudes = ((AppActivity) getActivity()).getArrayLatitudes();
        aLongitudes = ((AppActivity) getActivity()).getArrayLongitudes();

        locations = new ArrayList();

        for (int i = 0; i < aLatitudes.length; i++) {
            locations.add(new LatLng(aLatitudes[i], aLongitudes[i]));
        }

        setUpMapIfNeeded(); // Al ponerlo en el onResume se lee cada vez que cambias de pestaña
        // Para que no se cargue debería ir en el onCreateView
    }

    private void setUpMapIfNeeded() {

        // Configuramos el objeto GoogleMaps con valores iniciales.
        if (mMap == null) {
            // Instanciamos el objeto mMap a partir del MapFragment definido con el Id "map".
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            // Chequeamos si se ha obtenido correctamente una referencia al objeto GoogleMap.
            if (mMap != null) {
                // El objeto GoogleMap ha sido referenciado correctamente, ya podemos manipularlo.
                mMap.setMyLocationEnabled(true);

                if (aLatitudes != null && aLongitudes != null) {
                    setUpMap();
                } else {
                    //LatLng puntoCentral = new LatLng(aLatitudes[0], aLongitudes[0]);
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(puntoCentral, 14));
                }
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

        mMap.clear(); //Borra la ruta al suspenderse la actividad, para que no pinte varias rutas.

        rectOptions = new PolylineOptions();

        for (int i = 0; i < aLatitudes.length; i++) {
            rectOptions.add(new LatLng(aLatitudes[i], aLongitudes[i]));
        }

        mMap.addPolyline(rectOptions);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(aLatitudes[0], aLongitudes[0])));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(aLatitudes[aLatitudes.length - 1], aLongitudes[aLongitudes.length - 1])));

        mMap.setMyLocationEnabled(true);

        LatLng puntoCentral = new LatLng(aLatitudes[0], aLongitudes[0]);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(puntoCentral, 14));

        zoomToCoverAllMarkers(locations,mMap);

    }

    private static void zoomToCoverAllMarkers(ArrayList<LatLng> latLngList, final GoogleMap googleMap)
    {
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (LatLng marker : latLngList)
        {
            builder.include(marker);
        }

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
            }
        });
    }



}
