package com.fran.electricapp;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;


public class AppActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;

    private String[] names;
    public double[] ArrayLatitudes;
    public double[] ArrayLongitudes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        ParseUser currentUser = ParseUser.getCurrentUser();
        String nameUser = currentUser.getUsername();

        //Get items names
        names = getResources().getStringArray(R.array.nav_drawer_items);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
        mNavigationDrawerFragment.setUserData(nameUser, "franvaracruz@gmail.com", BitmapFactory.decodeResource(getResources(), R.drawable.avatar));

        //Section Home by default
        onNavigationDrawerItemSelected(1);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0: //Home
                fragment = getFragmentManager().findFragmentByTag(HomeFragment.TAG);
                if (fragment == null) {
                    fragment = new HomeFragment();
                }
                getFragmentManager().beginTransaction().replace(R.id.container, fragment, HomeFragment.TAG).commit();
                break;

            case 1: //GoTo
                fragment = getFragmentManager().findFragmentByTag(GoToFragment.TAG);
                if (fragment == null) {
                    fragment = new GoToFragment();
                }
                getFragmentManager().beginTransaction().replace(R.id.container, fragment, GoToFragment.TAG).commit();
                break;

            case 2: //Map
                fragment = getFragmentManager().findFragmentByTag(MyMapFragment.TAG);
                if (fragment == null) {
                    fragment = new MyMapFragment();
                }
                getFragmentManager().beginTransaction().replace(R.id.container, fragment, MyMapFragment.TAG).commit();
                break;


            default:
                //If there is no option, it displays a toast and it sent to home
                Toast.makeText(getApplicationContext(), "Option '" + names[position] +
                        "' not avaliable!", Toast.LENGTH_SHORT).show();
                fragment = new HomeFragment();
                position = 0;
                break;
        }

    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_app, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ParseUser.getCurrentUser().logOut();
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setArrayLatitudes(double[] latitudes) {
        this.ArrayLatitudes = latitudes;
    }

    public double[] getArrayLatitudes() {
        return ArrayLatitudes;
    }

    public void setArrayLongitudes(double[] longitudes) {
        this.ArrayLongitudes = longitudes;
    }

    public double[] getArrayLongitudes() {
        return ArrayLongitudes;
    }
}
