package com.fran.electricroutes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginFragment extends Fragment {

    private EditText usernameView;
    private EditText passwordView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        login();
    }

    public void login() {
        // Set up the login form.
        usernameView = (EditText) getView().findViewById(R.id.username);
        passwordView = (EditText) getView().findViewById(R.id.password);

        // Set up the submit button click handler
        getView().findViewById(R.id.action_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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
                    Toast.makeText(getActivity(), validationErrorMessage.toString(), Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                // Set up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(getActivity());
                dlg.setTitle(R.string.login_dialog_title);
                dlg.setMessage(getResources().getString(R.string.login_dialog_message));
                dlg.show();
                // Call the Parse login method
                ParseUser.logInInBackground(usernameView.getText().toString(), passwordView.getText()
                        .toString(), new LogInCallback() {

                    @Override
                    public void done(ParseUser user, ParseException e) {
                        dlg.dismiss();
                        if (e != null) {
                            // Show the error message
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            // Start an intent for the main activity
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }


}
