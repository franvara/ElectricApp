package com.fran.electricapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/* Section Home fragment */
public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    private static View rootView;
    public static final String TAG = "home";


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

    }


}
