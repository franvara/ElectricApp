package com.fran.electricroutes;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;

public class LoginMainActivity extends Activity {
    // Declare Tab Variable
    ActionBar.Tab Tab1, Tab2;
    Fragment fragmentTab1 = new LoginFragment();
    Fragment fragmentTab2 = new SignUpFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main_activity);

        ActionBar actionBar = getActionBar();

        // Hide Actionbar Icon
        actionBar.setDisplayShowHomeEnabled(false);

        // Hide Actionbar Title
        actionBar.setDisplayShowTitleEnabled(false);

        // Create Actionbar Tabs
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set Tab Icon and Titles
        Tab1 = actionBar.newTab().setText(R.string.login_tab);
        Tab2 = actionBar.newTab().setText(R.string.signup_tab);

        // Set Tab Listeners
        Tab1.setTabListener(new LoginTabListener(fragmentTab1));
        Tab2.setTabListener(new LoginTabListener(fragmentTab2));

        // Add tabs to actionbar
        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);
    }
}
