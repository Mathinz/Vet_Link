package com.vet.link.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vet.link.R;
import com.vet.link.Services.ApiResource;
import com.vet.link.Services.Constant;
import com.vet.link.Services.Utility;
import com.vet.link.Services.VolleyCommon;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

import static com.vet.link.Services.Constant.USER_ID;

/**
 * Created by Abayomi S.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    Context context;
    String password;

    Button btn_fb_login;

    String login;
    Button btn_reset_password;

    Button btn_main_login;

    Button btn_sign_up;

    Button btn_guest_login;

    SharedPreferences sharedPreferences = null;

    SharedPreferences.Editor editor = null;

    GoogleApiClient mGoogleApiClient;

    public static boolean check_login_btn_click = false;

    CallbackManager callbackManager;

    private String str_fb_email = "";



    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

  public static double latitude = 0.0 , longitude = 0.0 ;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            context = this;

//            inItFacebook();

            sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
            editor = sharedPreferences.edit();

            inItUi();  //initialize UI

            mEmailView = findViewById(R.id.edt_email_id);


            mPasswordView = (EditText) findViewById(R.id.edt_password_id);


            mEmailView.setText("admin");
            mPasswordView.setText("admin");

            setListeners();  //Set Listeners for FB and Google Buttons
        } catch (Exception e) {
            Log.e("LoginActivity", e.toString());
        }

        //Getting current latitude and longitude using network provider
        Location location = getLocation(LoginActivity.this);
        if (location != null) {
             latitude = location.getLatitude();
             longitude = location.getLongitude();
            Log.d("theH", "onCreate: latitude: "+latitude +" longitude: "+longitude);
        }
    }

    private void inItFacebook() throws Exception {

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    private void setListeners() throws Exception {

        btn_fb_login.setOnClickListener(this);

        btn_main_login.setOnClickListener(this);


        btn_guest_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                attemptGuestLogin();
            }
        });


        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }

        });

        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void inItUi() throws Exception {

        btn_fb_login = (Button) findViewById(R.id.img_fb_login_btn);

        btn_main_login = (Button) findViewById(R.id.img_main_login_btn);

        btn_sign_up = (Button) findViewById(R.id.img_main_signup_btn);

        btn_guest_login = (Button) findViewById(R.id.img_main_guest_btn);

        btn_reset_password = (Button) findViewById(R.id.reset_password);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constant.REQUEST_CODE_FOR_GOOGLE_LOGIN)

        {
            try {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                getGoogleUserProfileInformation(result);
            } catch (Exception e) {
                Log.e(">", e.toString());
            }
        }

        if (requestCode == Constant.REQUEST_CODE_FOR_FACEBOOK) {

            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())

        {
            case     R.id.img_fb_login_btn:

                onDestroy();

                check_login_btn_click = true;

                if (Utility.isInternetConnectionAvailable(context))

                {
                    try {
                        fbLoginCode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else

                {
                    Utility.alertdiolog(context, getResources().getString(R.string.error_no_internet), getResources().getString(R.string.error_ok));
                }
                break;

            case R.id.img_main_login_btn:

                new SenderTask().execute();

                break;

        }

    }

    private void fbLoginCode() throws Exception {

        callbackManager = CallbackManager.Factory.create();

        List<String> permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("public_profile");

        LoginManager.getInstance().logInWithReadPermissions((Activity) context, permissions);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                try {
                    getDataFromFacebookProfile(loginResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {

                Toast.makeText(context, getResources().getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(context, getResources().getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getDataFromFacebookProfile(LoginResult loginResult) throws Exception {

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            if (object.has("email")) {

                                str_fb_email = object.getString("email");
                            } else {
                                str_fb_email = "";
                            }
                            String str_fb_name = object.getString(getResources().getString(R.string.login_fb_google_user_name));

                            String str_fb_id = object.getString(getResources().getString(R.string.login_fb_google_user_id));

                            String str_fb_profile_pic = "https://graph.facebook.com/" + str_fb_id + "/picture?type=large";

                            ServerCallForSocialLogin(str_fb_email, str_fb_name, str_fb_id, "1", str_fb_profile_pic);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void ServerCallForSocialLogin(String str_fb_email, String str_fb_name, String str_fb_id, String social_type, String str_fb_profile_pic) throws Exception {
        String json_request = "{\"data\":[{\"email\":\"" + str_fb_email + "\",\"name\":\"" + str_fb_name + "\",\"social_id\":\"" + str_fb_id + "\",\"social_type\":\"" + social_type + "\",\"profile\":\"" + str_fb_profile_pic + "\"}]}";

        sendDataToServerForSocialLogin(json_request);
        Log.e(">", json_request);
    }

    private void sendDataToServerForSocialLogin(String json_request) throws Exception {

        String url = ApiResource.LOGIN_URL;
        try {
            JSONObject jsonObjectForrequest = new JSONObject(json_request);

            Utility.callProgressDialog(LoginActivity.this);
            VolleyCommon.callVolley(null, LoginActivity.this, url, jsonObjectForrequest, "getJsonResponseOfSocialSignup", Utility.dialog);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getJsonResponseOfSocialSignup(JSONArray jsonArray) throws Exception {
        if (Utility.dialog.isShowing() || Utility.dialog != null) {
            Utility.dialog.dismiss();
        }
        try {

            JSONObject jsonObject = jsonArray.getJSONObject(0);
            Integer user_id = jsonObject.getInt("user_id");
            String email = jsonObject.getString("email");
            String name = jsonObject.getString("name");
            String social_id = jsonObject.getString("social_id");
            String profile_url = jsonObject.getString("profile");

/*
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
*/

            storeloginDetailsInSharedPreference(name, profile_url, user_id, email, social_id, "fb");

            editor.putInt("user_id", user_id);//------------------------------------get user_id from response
            editor.putString("email", email);
            editor.putString("name", name);
            editor.apply();
        /*    startActivity(i);
            finish();*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void gogleLoginCode() throws Exception {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Constant.REQUEST_CODE_FOR_GOOGLE_LOGIN);
    }

    private void getGoogleUserProfileInformation(GoogleSignInResult result) throws Exception {

        GoogleSignInAccount acct = result.getSignInAccount();

        if (acct != null)

        {
            String str_google_name = acct.getDisplayName();
            Uri uri_google_proPic = acct.getPhotoUrl();

            String str_google_proPic = "";

            if (uri_google_proPic != null)

            {
                str_google_proPic = acct.getPhotoUrl().toString();
            }
            String str_email = acct.getEmail();
            String str_id = acct.getId();

            ServerCallForSocialLogin(str_email, str_google_name, str_id, "2", str_google_proPic);

            //storeloginDetailsInSharedPreference(str_google_name, str_google_proPic, "google");
        }

        else{

            Log.d("No Google", "Not login");
        }
    }

    private void storeloginDetailsInSharedPreference(String name, String profile_pic, Integer user_id, String email, String social_id, String type) throws Exception {

        if (type.equals("fb")) {

            editor.putString(getResources().getString(R.string.login_login_user_name), name);
            editor.putString(getResources().getString(R.string.login_login_user_profile_pic), profile_pic);
            editor.putString(getResources().getString(R.string.login_login_type), type);

            editor.putInt("user_id", user_id);
            editor.putString("email", email);
            editor.putString("social_id", social_id);

            editor.putBoolean(getResources().getString(R.string.login_user_login), true);
            editor.putBoolean(getResources().getString(R.string.login_fb_login), true);

            editor.commit();

        } else {

            editor.putString(getResources().getString(R.string.login_login_user_name), name);
            editor.putString(getResources().getString(R.string.login_login_user_profile_pic), profile_pic);
            editor.putString(getResources().getString(R.string.login_login_type), type);
            editor.putInt("user_id", user_id);
            editor.putString("email", email);
            editor.putString("social_id", social_id);

            editor.putBoolean(getResources().getString(R.string.login_user_login), true);
            editor.putBoolean(getResources().getString(R.string.login_google_login), true);

            editor.commit();
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(getResources().getString(R.string.intent_lat_lng_available), "false");
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void attemptGuestLogin(){
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();


    }
    private void resetPassword(){
        Intent intent = new Intent (context, passwordReset.class);
        startActivity(intent);
        finish();

    }


    private void attemptRegister(){

        Intent intent = new Intent(context, SignUpActivity.class);
        startActivity(intent);
        finish();


    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("error");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("error");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("error");
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
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
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(getResources().getString(R.string.intent_lat_lng_available), "false");
                startActivity(intent);
                finish();
            } else {
                mPasswordView.setError("error");
                mPasswordView.requestFocus();
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
            login = mEmailView.getText().toString();
            password = mPasswordView.getText().toString();


        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://vetlink1.pvamu.edu/androidApp/androidAP_login.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("acctID", login)
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

        @SuppressLint("ShowToast")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject jsonObj = null;
            String status = "";
            String userid = null;
            String email = null;
            String fname = null;
            String phone = null;

            try {
                if(s != null) {
                    jsonObj = new JSONObject(s);
                    status = jsonObj.get("ackMsg").toString();
                    userid = jsonObj.get("id").toString();
                    email = jsonObj.get("ea").toString();
                    fname = jsonObj.get("fn").toString();
                    phone = jsonObj.get("pn").toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
            if(status.equals("LOGGEDIN")) {
                Intent intent = new Intent(getApplicationContext(), Main_Activity.class);
                USER_ID = userid;
                intent.putExtra("userid",userid);
                intent.putExtra("fname",fname);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }else Toast.makeText(getApplicationContext(),"Error!",Toast.LENGTH_LONG).show();
        }
    }

    public static Location getLocation(Context context) {
        Log.d("theH", "getLocation: is called");
        Location location = null;
        final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
        final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            try {
                LocationManager locationManager = (LocationManager) context
                        .getSystemService(Context.LOCATION_SERVICE);

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
//updating when location is changed
                        if (location != null) {
                             latitude = location.getLatitude();
                             longitude = location.getLongitude();
                            Log.d("theH", "getLocation: latitude: "+latitude +" longitude: "+longitude);
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                boolean isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                boolean isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!isNetworkEnabled && !isGPSEnabled) {
//call getLocation again in onResume method in this case
                    context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                } else {

                    if (isNetworkEnabled) {

                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }

                    } else {

                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        }

                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

//Request for the ACCESS FINE LOCATION
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//call the getLocation funtion again on permission granted callback
        }
        return location;
    }

}
