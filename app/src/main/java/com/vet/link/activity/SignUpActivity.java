package com.vet.link.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

public class SignUpActivity extends AppCompatActivity {
    Button btn_submit_register;
    private EditText edt_userid;
    private EditText edt_passcode;
    private EditText edt_retypepasscode;
    private EditText edt_fullname;
    private EditText edt_emailaddress;
    private EditText edt_phonenumber;
    private boolean passwordMatch;

    String userid;
    String pass;
    String retypePass;
    String fullname;
    String email;
    String phonenumber;


    private static final String REGISTER_URL = "http://vetlink1.pvamu.edu/androidApp/androidAP_registration.php";

    int responsecode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        InitUI();

        btn_submit_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordMatch){
                    registerUser();

                }
                else {
                    Toast.makeText(getApplicationContext(), "Password does not match. Pls retype!", Toast.LENGTH_LONG).show();
                    edt_passcode.setText("");
                    edt_retypepasscode.setText("");
                }


            }
        });


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


        edt_retypepasscode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                compareTextViews();

            }
        });

    }
    void compareTextViews() {
        if (String.valueOf(edt_passcode.getText()).equals(String.valueOf(edt_retypepasscode.getText())) && !(String.valueOf(edt_passcode.getText())).equals(""))
            passwordMatch = true;
        else {
            passwordMatch = false;


        };
    }

    private void returnToLogin(){

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

    }


    void InitUI(){

        btn_submit_register = (Button) findViewById(R.id.btn_submitregister);
        edt_userid = (EditText) findViewById(R.id.edt_userid);
        edt_passcode = (EditText) findViewById(R.id.edt_passcode);
        edt_retypepasscode = (EditText) findViewById(R.id.edt_retypepasscode);
        edt_fullname = (EditText) findViewById(R.id.edt_fullname);
        edt_emailaddress = (EditText) findViewById(R.id.edt_emailaddress);
        edt_phonenumber = (EditText) findViewById(R.id.edt_phonenumber);

    }

    //void attemptRegister






    private void registerUser() {

        userid = String.valueOf(edt_userid.getText());
        pass = String.valueOf(edt_passcode.getText());
        fullname = String.valueOf(edt_fullname.getText());
        email = String.valueOf(edt_emailaddress.getText());
        phonenumber = String.valueOf(edt_phonenumber.getText());
        retypePass = String.valueOf(edt_retypepasscode.getText());

        register();
    }

    boolean cancel = false;
    View focusView = null;

   /* if (!TextUtils.isEmpty()) {
        edt_passcode.setError("error");
        focusView = edt_passcode;
        cancel = true;
    }  */


    private boolean isEmailValid(String email) {
        //if (email.contains("@")== isEmailValid(email) )
        //TODO: Replace this with your own logic
        return email.contains("@");
    }



    private void register() {
        String urlSuffix = "?acctID="+userid+"&pwd="+pass+"&fullName="+fullname+"&emailAddr="+email+"&phoneNumber="+phonenumber;
        class RegisterUser extends AsyncTask<String, Void, String> {

            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignUpActivity.this, "Please Wait", null, true, true);

                // Intent goBackToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                //startActivity(goBackToLogin);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);


                JSONObject jsonObj = null;
                String status = "Registered";
                try {
                    if(s != null) {
                        jsonObj = new JSONObject(s);
                        status = jsonObj.get("ackMsg").toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loading.dismiss();
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                if (status.equals("OK") ) {
                Toast.makeText(getApplicationContext(),"Registered on the account",Toast.LENGTH_LONG).show();
                    returnToLogin();
                }

                Log.d("json", s);
                 //   Toast.makeText(getApplicationContext(),"Registration failed! try again",Toast.LENGTH_LONG).show();


            }

            @Override
            protected String doInBackground(String... params) {
//                String s = params[0];
//                BufferedReader bufferedReader = null;
//                try {
//                    URL url = new URL(REGISTER_URL + s);
//                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
//
//                    String result;
//
//                    result = bufferedReader.readLine();
//                    responsecode = con.getResponseCode();
//
//                    return result;
//                } catch (Exception e) {
//                    return null;
//                }


                try {
                    URL url = new URL(REGISTER_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("acctID", userid)
                            .appendQueryParameter("pwd", pass)
                            .appendQueryParameter("fullName", fullname)
                            .appendQueryParameter("emailAddr", email)
                            .appendQueryParameter("phoneNumber", phonenumber);
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
        }

        RegisterUser ru = new RegisterUser();
        ru.execute(urlSuffix);
        Intent goBackToLogin = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(goBackToLogin);
    }

    @Override
    public void onBackPressed() { }

}
