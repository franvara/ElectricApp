package com.fran.electricroutes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.parse.ParseUser;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Login code
        ParseUser currentUser = ParseUser.getCurrentUser();
        String i = currentUser.getUsername();
        if (currentUser.getUsername() != null ) {
                // Start an intent for the logged in activity
                Intent intent = new Intent(this, AppActivity.class);
                startActivity(intent);
                finish(); //para que cuando vuelvas atrás salga de la aplicación
            } else {
                // Send user to LoginMainActivity.class
                Intent intent = new Intent(this, LoginMainActivity.class);
                startActivity(intent);
                finish();
            }
        //Login code end

    }

}


