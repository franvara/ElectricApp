package com.fran.electricapp;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class ListActivity extends ActionBarActivity {

    // Declare Variables
    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ListViewAdapter adapter;
    private List<Vehicle> vehiclelist = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from listview_main.xml
        setContentView(R.layout.list);

        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();
    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ListActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("ElectricApp");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            vehiclelist = new ArrayList<Vehicle>();
            try {
                // Locate the class table named "Cars" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "Cars");
                // Locate the column named "ranknum" in Parse.com and order list
                // by ascending
                query.orderByAscending("Marca");
                ob = query.find();
                for (ParseObject vehicle : ob) {
                    // Locate images in imagen column
                    ParseFile image = (ParseFile) vehicle.get("image");

                    Vehicle map = new Vehicle();
                    map.setMarca((String) vehicle.get("Marca"));
                    map.setModelo((String) vehicle.get("Modelo"));
                    map.setImage(image.getUrl());
                    vehiclelist.add(map);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(ListActivity.this,
                    vehiclelist);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }


}
