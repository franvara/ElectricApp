package login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.uc3m.volttrip.R;

public class LoginActivity extends Activity{

    private EditText usernameView;
    private EditText passwordView;
    private TextView signupClick;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        usernameView = (EditText) findViewById(R.id.username_log);
        passwordView = (EditText) findViewById(R.id.password_log);
        signupClick = (TextView) findViewById(R.id.signUpTextView);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    login();

                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                login();
            }
        });

        signupClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });


    }

    public void login() {

        // Validate the log in data
        boolean validationError = false;
        StringBuilder validationErrorMessage =
                new StringBuilder(getResources().getString(R.string.error_intro));
        if (usernameView.getText().toString().isEmpty()) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
        }
        if (passwordView.getText().toString().isEmpty()) {
            if (validationError) {
                validationErrorMessage.append(getResources().getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
        }
        validationErrorMessage.append(getResources().getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Call the Parse login method
        ParseUser.logInInBackground(usernameView.getText().toString(), passwordView.getText()
                .toString(), new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException e) {
                progressBar.setVisibility(View.GONE);
                if (e != null) {
                    // Show the error message
                    //Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    switch(e.getCode()){
                        case ParseException.USERNAME_TAKEN:
                            break;
                        case ParseException.OBJECT_NOT_FOUND:
                            Toast.makeText(LoginActivity.this, R.string.object_not_found, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Start an intent for the main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }


}
