package com.fran.electricroutes;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;


public class MainActivity extends Activity {
    private DrawerLayout NavDrawerLayout;
    private ListView NavList;
    private TypedArray NavIcons;
    private String[] names;
    private ArrayList<Item_objct> NavItems;
    NavDrawerListAdapter NavAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Drawer Layout
        NavDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //List
        NavList = (ListView) findViewById(R.id.list_slidermenu);

        //Header
        View header = getLayoutInflater().inflate(R.layout.header, null);
        NavList.addHeaderView(header);

        //Get items icons
        NavIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        //Get items names
        names = getResources().getStringArray(R.array.nav_drawer_items);

        //Navigation names list
        NavItems = new ArrayList<Item_objct>();

        //Add the Item_objct objects to array
        //Inicio
        NavItems.add(new Item_objct(names[0], NavIcons.getResourceId(0, -1)));
        //Ir a...
        NavItems.add(new Item_objct(names[1], NavIcons.getResourceId(1, -1)));
        //Mis destinos
        NavItems.add(new Item_objct(names[2], NavIcons.getResourceId(2, -1)));
        //Mapa
        NavItems.add(new Item_objct(names[3], NavIcons.getResourceId(3, -1)));
        //Garaje
        NavItems.add(new Item_objct(names[4], NavIcons.getResourceId(4, -1)));
        //Configuración
        NavItems.add(new Item_objct(names[5], NavIcons.getResourceId(5, -1)));

        //Set the adapter
        NavAdapter = new NavDrawerListAdapter(this, NavItems);
        NavList.setAdapter(NavAdapter);

        //Always show the same title
        mTitle = mDrawerTitle = getTitle();

        //State the mDrawerToggle and the images to use
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                   /* host Activity */
                NavDrawerLayout,        /* DrawerLayout object */
                R.drawable.ic_drawer,   /*Navigation icon */
                R.string.app_name,      /* "open drawer" description */
                R.string.app_name    /*"close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                Log.e("Cerrado completo", "!!");
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerview) {
                Log.e("Apertura completa", "!!");
            }
        };

        //Set mDrawerToggle to DrawerListener
        NavDrawerLayout.setDrawerListener(mDrawerToggle);
        //State ActionBar to show the home button
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Set click action to menu items
        NavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                ShowFragment(position);
            }
        });

        //Section Home by default
        ShowFragment(1);


    }

    //To show the correct fragment according the position
    private void ShowFragment(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 1:
                fragment = new HomeFragment();
                break;
            case 2:
                fragment = new Seccion2();
                break;

            default:
                //If there is no option, it displays a toast and it sent to home
                Toast.makeText(getApplicationContext(), "Option '" + names[position - 1] +
                        "' not avaliable!", Toast.LENGTH_SHORT).show();
                fragment = new HomeFragment();
                position = 1;
                break;
        }

        //If the fragment is not null
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            //Update content depending on the choose option
            NavList.setItemChecked(position, true);
            NavList.setSelection(position);
            //Change name
            //setTitle(names[position - 1]);
            //Close sliding menu
            NavDrawerLayout.closeDrawer(NavList);

        } else {
            //If the fragment is null, it show a error message
            Log.e("Error ", "ShowFragment" + position);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.e("mDrawerToggle pushed", "x");
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

}

