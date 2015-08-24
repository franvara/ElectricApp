package login;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;
import com.uc3m.volttrip.AppActivity;

public class MainActivity extends Activity{

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
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); //para que cuando vuelvas atrás salga de la aplicación
        }
    }
}
