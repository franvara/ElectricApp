package com.uc3m.etrip;

import com.google.android.gms.maps.model.LatLng;

public class Gasolinera {
    private String info;
    private LatLng latLng;

    public Gasolinera(String info, LatLng latLng){
        this.info = info;
        this.latLng = latLng;

    }

    public String getInfo() {
        return info;
    }

    public LatLng getlatLong() {
        return latLng;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

}
