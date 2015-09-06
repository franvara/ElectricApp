package login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.uc3m.etrip.R;

public class SignupActivity extends Activity{
    private EditText usernameView;
    private EditText passwordView;
    private EditText passwordAgainView;
    private ProgressBar progressBar;
    private TextView loginClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Set up the signup form.
        usernameView = (EditText) findViewById(R.id.username_signup);
        passwordView = (EditText) findViewById(R.id.password_signup);
        passwordAgainView = (EditText) findViewById(R.id.password_signup2);
        loginClick = (TextView) findViewById(R.id.login_TextView);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        passwordAgainView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ocultarTeclado();

                    signUp();

                    return true;
                }
                return false;
            }
        });

        // Set up the submit button click handler
        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ocultarTeclado();
                signUp();
            }

        });

        loginClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void signUp() {

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage =
                new StringBuilder(getResources().getString(R.string.error_intro));
        if (isEmpty(usernameView)) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
        }
        if (isEmpty(passwordView)) {
            if (validationError) {
                validationErrorMessage.append(getResources().getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
        }
        if (!isMatching(passwordView, passwordAgainView)) {
            if (validationError) {
                validationErrorMessage.append(getResources().getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getResources().getString(
                    R.string.error_mismatched_passwords));
        }
        validationErrorMessage.append(getResources().getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(SignupActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(usernameView.getText().toString());
        user.setPassword(passwordView.getText().toString());
        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException e) {
                progressBar.setVisibility(View.GONE);
                if (e != null) {
                    // Show the error message
                    //Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    switch (e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            Toast.makeText(SignupActivity.this, R.string.username_taken, Toast.LENGTH_LONG).show();
                            break;
                        case ParseException.CONNECTION_FAILED:
                            Toast.makeText(SignupActivity.this, R.string.network, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(SignupActivity.this, R.string.default_exception, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isMatching(EditText etText1, EditText etText2) {
        if (etText1.getText().toString().equals(etText2.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

    public void ocultarTeclado(){
        InputMethodManager imm = (InputMethodManager) getSystemService(
                this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

}
