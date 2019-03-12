package com.vet.link.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.vet.link.R;
import com.vet.link.Services.Utility;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.vet.link.fragment.SymptomsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.volley.Request.Method.POST;

/**
 * Created by Abayomi S.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_CHECK_SETTINGS = 123;
    public static boolean check_search_bar_hide_or_not = false;

    static Context context;

    private TextView txt_about_us;

    private TextView txt_static_page_break_rule;

    private TextView txt_home;

    private TextView txt_edit_profile;

    private TextView txt_report;

    static ProgressDialog dialog;

    private Toolbar toolbar;

    private FrameLayout frameLayout;

    private LocationManager locationManager;

    private boolean checkInternet;

    private boolean enabledGPS;

    private double latitude;

    private double longitude;

    private LinearLayout linearLayout_search;

    private EditText edt_search;

    private ImageView iv_go_search;

    private boolean stop_calling_receiver = false;

    ListView listView;

    AQuery aQuery;

    JSONArray jsonArray = null;

    TextToSpeech txt2Speech;

    SharedPreferences sharedPreferences = null;

    SharedPreferences.Editor editor = null;

    boolean userBlindOrNot = false;

    GoogleMap googleMap;

    GoogleApiClient mGoogleApiClient;

    protected DrawerLayout drawer;

    TextView txt_logout;

    String str_street = null;

    String str_area = null;

    String str_city = null;

    CircularImageView iv_profile_pic;

    TextView txt_first_name;

    TextView h_userid;
    TextView h_fullname;
    TextView h_email;
    TextView h_phone;

    public static int int_name_of_place_position = 0;

    JSONArray jsonArray_final = null;

    String str_place_id = null;

    public static boolean enableOptionMenuWhenSearch = false;

    public static boolean disableOptionMenuWhenSearchIcon = false;



    boolean check_connection_available_or_not;

    public static int km_mile = 0;

    ArrayList<String> matches_text_array_list;

    int img_size = 0;

    public static boolean aBoolean_km_mi = false;

    private GoogleApiClient client;

    public static boolean gps_speak_problem = false;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!stop_calling_receiver) {

                stop_calling_receiver = true;

                double defaultValue = 0.00;

                latitude = intent.getDoubleExtra(getResources().getString(R.string.intent_lattitude), defaultValue);
                longitude = intent.getDoubleExtra(getResources().getString(R.string.intent_longitude), defaultValue);

                try {
//                    getAddress(context, latitude, longitude, 1);
                    getPlaceId();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        }
    };

    private StringBuilder stringBuilder;

    private TextView txt_address;

    private ImageView img_place;

    private RatingBar ratingBar;

    private FloatingActionButton fab;

    private String place_ids;

    private String place_name;
    private boolean isFromSearch = false;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            context = this;

            inItUi();  //initialize UI


            toolbar.setTitle("");

            setSupportActionBar(toolbar);

            sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
            editor = sharedPreferences.edit();


            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();


            drawer.addDrawerListener(new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    try {
                        String str_profile_pic_url = (sharedPreferences.getString(getResources().getString(R.string.login_login_user_profile_pic), null));

                        Picasso.with(context)
                                .load(str_profile_pic_url)
                                .placeholder(R.drawable.no_profile_img)
                                .into(iv_profile_pic);

                        String str_first_name = sharedPreferences.getString(getResources().getString(R.string.login_login_user_name), null);
                        if (str_first_name != null && !str_first_name.isEmpty())

                        {
                            txt_first_name.setText(str_first_name);
                        }


                    } catch (Exception e) {
                        Log.e(">", e.toString());
                    }

                }
            });


            setListners();


        } catch (Exception e) {
            Log.e("MainActivity:", e.toString());
        }
    }










    private void setListners() throws Exception {

        txt_home.setOnClickListener(this);

        txt_static_page_break_rule.setOnClickListener(this);

        txt_about_us.setOnClickListener(this);

        txt_report.setOnClickListener(this);

        txt_edit_profile.setOnClickListener(this);

        iv_go_search.setOnClickListener(this);

        txt_logout.setOnClickListener(this);

    }





    private void inItUi() throws Exception {


//        img_place = (ImageView) findViewById(R.id.h_img_locationpic);
//
//        ratingBar = (RatingBar) findViewById(R.id.h_ratingBar);
//
//
//
//        txt_home = (TextView) findViewById(R.id.txt_home);
//
//        frameLayout = (FrameLayout) findViewById(R.id.frame_layout_home);
//
//        txt_static_page_break_rule = (TextView) findViewById(R.id.txt_static_break_rule);
//
//
//        h_userid = (TextView) findViewById(R.id.h_edt_toi1);
//        h_fullname = (TextView) findViewById(R.id.h_edt_toi2);
//        h_email = (TextView) findViewById(R.id.h_edt_toi4);
//        h_phone = (TextView) findViewById(R.id.edt_toi);


        h_userid.setText(getIntent().getStringExtra("email"));
        h_fullname.setText(getIntent().getStringExtra("fname"));
        h_email.setText(getIntent().getStringExtra("email"));
        h_phone.setText(getIntent().getStringExtra("phone"));



        txt_report = (TextView) findViewById(R.id.txt_report);

        txt_edit_profile = (TextView) findViewById(R.id.txt_edit_profile);

        txt_about_us = (TextView) findViewById(R.id.txt_search);  //search

        txt_logout = (TextView) findViewById(R.id.logout); //logout

        iv_profile_pic = (CircularImageView) findViewById(R.id.img_profile_pic);

        iv_profile_pic.setBorderColor(getResources().getColor(R.color.lightsky));

        txt_first_name = (TextView) findViewById(R.id.txt_first_name);
        txt_first_name.setText(getIntent().getStringExtra("fname"));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();




//        linearLayout_search = (LinearLayout) findViewById(R.id.h_linear_search);
//
//        edt_search = (EditText) findViewById(R.id.h_edt_search);
//
//        iv_go_search = (ImageView) findViewById(R.id.h_btn_search);



        toolbar = (Toolbar) findViewById(R.id.toolbar);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Typeface myTypeface6 = Typeface.createFromAsset(getAssets(), "fonts/extra_bold.ttf");
        txt_first_name.setTypeface(myTypeface6);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        txt_home.setTypeface(myTypeface);

        Typeface myTypeface1 = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        txt_about_us.setTypeface(myTypeface1);

        Typeface myTypeface2 = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        txt_edit_profile.setTypeface(myTypeface2);

        Typeface myTypeface3 = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        txt_report.setTypeface(myTypeface3);

        Typeface myTypeface4 = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        txt_static_page_break_rule.setTypeface(myTypeface4);

        Typeface myTypeface5 = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        txt_logout.setTypeface(myTypeface5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)

        {

            case R.id.search:

//                if (Utility.isInternetConnectionAvailable(context)) {
//                    if (check_search_bar_hide_or_not == false) {
//
//                        check_search_bar_hide_or_not = true;
//                        linearLayout_search.setVisibility(View.VISIBLE);
//
//                        listView.setVisibility(View.GONE);
//
//                    } else
//
//                    {
//                        disableOptionMenuWhenSearchIcon = true;
//
//                        invalidateOptionsMenu();
//
//                        check_search_bar_hide_or_not = false;
//
//                        linearLayout_search.setVisibility(View.GONE);
//
//                        listView.setVisibility(View.GONE);
//
//                        listView.setAdapter(null);
//
//                        edt_search.setText("");
//                    }
//                } else {
//                    Utility.alertdiolog(context, "No Internet Connection!!", "Ok");
//                }

                break;

        }

        return super.onOptionsItemSelected(item);
    }




    private void showLocationOnMap(double latitude, double longitude) throws Exception {

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        LatLng latLng = new LatLng(latitude, longitude);

        googleMap.clear();

        if (str_street != null && !str_street.isEmpty())

        {
            try {
                googleMap.addMarker(new MarkerOptions().position(latLng).title(str_street)).showInfoWindow();
            } catch (Exception e) {
                Log.e(">", e.toString());
            }

        } else if (str_area != null && !str_area.isEmpty())

        {
            googleMap.addMarker(new MarkerOptions().position(latLng).title(str_area)).showInfoWindow();
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void callProgressDialog(String msg) throws Exception {

        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(msg);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())

        {


            case R.id.txt_home:

                if (Utility.isInternetConnectionAvailable(context))

                {
                    drawer.closeDrawers();

                    editor.putBoolean("home", true);
                    editor.commit();

                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("current_lat", latitude);
                    i.putExtra("current_lng", longitude);
                    i.putExtra(getResources().getString(R.string.intent_lat_lng_available), "true");

                    i.putExtra("fname",getIntent().getStringExtra("fname"));
                    i.putExtra("email", getIntent().getStringExtra("email"));
                    i.putExtra("phone", getIntent().getStringExtra("phone"));
                    startActivity(i);
                    finish();
                } else

                {
                    Utility.alertdiolog(context, getResources().getString(R.string.error_no_internet), getResources().getString(R.string.error_ok));
                }

                Log.d(" Clicked", v.getId()+"");

                break;


            case R.id.txt_search:

//                drawer.closeDrawers();
//
//                Intent search_intent = new Intent(context, SearchActivity.class);
//
//                startActivity(search_intent);
//
//                finish();



                drawer.closeDrawers();

                SymptomsFragment sActivity = new SymptomsFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout_home, sActivity)
                        .addToBackStack(sActivity.toString())
                        .commit();

                break;


            case R.id.txt_report:

                if (Utility.isInternetConnectionAvailable(context))

                {
                    drawer.closeDrawers();


//                    ReportFragment rFrag = new ReportFragment();
//
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.frame_layout, rFrag)
//                            .addToBackStack(rFrag.toString())
//                            .commit();
//
//                    break;




                    Intent report_intent = new Intent(context, WriteReprtActivity.class);

                    report_intent.putExtra("fname",getIntent().getStringExtra("fname"));
                    report_intent.putExtra("email", getIntent().getStringExtra("email"));
                    report_intent.putExtra("phone", getIntent().getStringExtra("phone"));

                    startActivity(report_intent);

                    finish();
                } else

                {
                    Utility.alertdiolog(context, getResources().getString(R.string.error_no_internet), getResources().getString(R.string.error_ok));
                }
                break;

            case R.id.txt_edit_profile:

                drawer.closeDrawers();




//                VisualizeFragment vFrag = new VisualizeFragment();
//
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.frame_layout, vFrag)
//                        .addToBackStack(vFrag.toString())
//                        .commit();
//
//                break;

                Intent visualize_intent = new Intent(context, VisualizeActivity.class);

                visualize_intent.putExtra("fname",getIntent().getStringExtra("fname"));
                visualize_intent.putExtra("email", getIntent().getStringExtra("email"));
                visualize_intent.putExtra("phone", getIntent().getStringExtra("phone"));

                startActivity(visualize_intent);

                finish();

                break;

            case R.id.logout:



                    Intent lout_intent = new Intent(context, LoginActivity.class);
                    lout_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    lout_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(lout_intent);

                    finish();

        }
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (enableOptionMenuWhenSearch)

        {
//            menu.findItem(R.id.mile_km).setVisible(true);

//            enableOptionMenuWhenSearch = false;
        }

        if (disableOptionMenuWhenSearchIcon)

        {
//            menu.findItem(R.id.mile_km).setVisible(false);
//
//            disableOptionMenuWhenSearchIcon = false;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onInit(int status) {


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {





        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!

        super.onDestroy();
    }



    private void getPlaceId() {

        double lat = latitude;
        double lng = longitude;

        String URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?location=" + lat + "," + lng + "&radius=1000&types=park|church|cafe|food|bar|night_club|stadium|store|company&keyword=&key=AIzaSyACJQGonkDj5Qa3aUTpb0gUJO2qFT4y1z0";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(POST, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String photo_reference = null;
                Log.e("response:", response.toString());

                try {
                    JSONArray jsonArray = response.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        JSONArray jsonArray1 = jsonObject.getJSONArray("photos");

                        place_ids = jsonObject.getString("place_id");

                        place_name = jsonObject.getString("name");

                        JSONObject jsonObject1 = jsonArray1.getJSONObject(i);

                        photo_reference = jsonObject1.getString("photo_reference");

                        if (photo_reference != null && place_ids != null) {
                            break;
                        }
                    }
                    //     Log.e("\n\n>", photo_reference);
                    getPhoto(photo_reference);

                    getRating(place_ids);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error:", error.toString());

            }
        });

        RequestQueue jsonRequest = Volley.newRequestQueue(this);
        jsonRequest.add(jsonObjectRequest);
    }

    private void getRating(String place_id) {

        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place_id +
                "&key=AIzaSyACJQGonkDj5Qa3aUTpb0gUJO2qFT4y1z0";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(POST, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.e("response:", response.toString());

                try {
                    JSONObject jsonObject = response.getJSONObject("result");

                    Double rating = jsonObject.getDouble("rating");

                    ratingBar.setRating(rating.floatValue());

                    Log.e("rating:", "" + rating);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error:", error.toString());

            }
        });

        RequestQueue jsonRequest = Volley.newRequestQueue(this);
        jsonRequest.add(jsonObjectRequest);
    }

    private void getPhoto(String reference) {
        String urls = "https://maps.googleapis.com/maps/api/place/photo?photoreference=" + reference + "&key=AIzaSyACJQGonkDj5Qa3aUTpb0gUJO2qFT4y1z0&maxwidth=1200";

        loadGooglePhoto(urls, this, img_place, reference);
    }

    public static void loadGooglePhoto(String url, Context c, ImageView imageView, String reference) {
        String urls = String.format(url, reference, R.string.google_app_id);
        loadIcon(c, urls, imageView);
    }

    public static void loadIcon(Context context, String url, ImageView imageView) {
        if (TextUtils.isEmpty(url))
            return;
        try {
//            Picasso.with(context).load(url).placeholder(R.drawable.plcaeholder_place).into(imageView);
        } catch (Exception e) {
            Log.e(">>", e.toString());
        }
        Log.e("url:", url);
    }



    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
//        client.disconnect();
    }

    @Override
    public void onBackPressed() {

//        android.support.v4.app.FragmentManager fragment = getSupportFragmentManager();
//
//        int count = fragment.getBackStackEntryCount();
//
//        if (count == 0)
//
//        {
//            finish();
//
//        } else {
//            getFragmentManager().popBackStack();
//        }
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();

        longitude = location.getLongitude();

        if (gps_speak_problem)

        {
            try {
                //getAddress(context, latitude, longitude, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else

        {
            try {
                //getAddress(context, latitude, longitude, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (dialog != null) {
            dialog.dismiss();
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
}
