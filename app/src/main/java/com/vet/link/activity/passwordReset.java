package com.vet.link.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vet.link.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class passwordReset extends AppCompatActivity {

    Context context;
    Button btn_password_resets;
    private   EditText edt_email;
    private   EditText edt_password ;
    private   EditText edt_password_retype;
    private UserResetTask mAuthTask = null;
    String email;
    String password;
    String passwordRetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {

                                                     returnToLogin();


                                                 }
                                             }
        );

    }
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private void attemptReset() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        edt_email.setError(null);
        edt_password.setError(null);
        edt_password_retype.setError(null);

        // Store values at the time of the login attempt.
        email = edt_email.getText().toString();
        password = edt_password.getText().toString();
        passwordRetype = edt_password_retype.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Check for a corresponding passwords
        if (!password.equals(passwordRetype)){
            Toast.makeText(this, "Password does not match!", Toast.LENGTH_SHORT).show();
            focusView = edt_password;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            edt_password.setError("error");
            focusView = edt_password;
            cancel = true;
        }

        // Check for a valid email address.
        else if (TextUtils.isEmpty(email)) {
            edt_email.setError("error");
            focusView = edt_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            edt_email.setError("error");
            focusView = edt_email;
            cancel = true;
        }

        else if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
               mAuthTask = new UserResetTask(email,password);
            // mAuthTask.execute((Void) null);

        }
    }
    private boolean isEmailValid(String email) {
        //if (email.contains("@")== isEmailValid(email) )
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    void InitUi(){

        btn_password_resets= (Button)findViewById(R.id.btn_reset);
        edt_email = (EditText) findViewById(R.id.edt_reset_email);
        edt_password = (EditText)findViewById(R.id.edt_password);
        edt_password_retype= (EditText)findViewById(R.id.edt_password_retype);


    }




    private void returnToLogin(){

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

    }


    public void onClick(View view) {
        attemptReset();
    }
    public class UserResetTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserResetTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
//            showProgress(false);

            if (success) {
                Intent intent = new Intent(context, LoginActivity.class);
               // intent.putExtra(getResources().getString(R.string.intent_lat_lng_available), "false");
                startActivity(intent);
                finish();
            } else {
                edt_email.setError("error");
                edt_password.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            showProgress(false);
        }
    }




    private class SenderTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //email = edt_email.getText().toString();
            password = edt_password.getText().toString();


        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://129.207.54.135/App_Reset.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                //        .appendQueryParameter("emailAddr", email)
                        .appendQueryParameter("pwd", password);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                Log.d("YAP", "1");
                InputStream is = conn.getInputStream();
                BufferedReader reader;
                Log.d("",conn.getResponseCode()+"");
                if (conn.getResponseCode() == 200) {
                    reader = new BufferedReader(new InputStreamReader(is));
                    String result;
                    result = reader.readLine();

                    return result;
                }

                else
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

                is.close();
                Log.d("YAP", "3");

                conn.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }
    }

}
