package com.vet.link.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.vet.link.R;
import com.vet.link.fragment.My_report;
import com.vet.link.fragment.ReportFragment;
import com.vet.link.fragment.SymptomsFragment;
import com.vet.link.fragment.VisualizeFragment;
import com.vet.link.fragment.hFragment;

import org.json.JSONArray;

import java.util.ArrayList;

public class Main_Activity extends AppCompatActivity {


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

    String str_streat = null;

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
                    //getPlaceId();
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

    private hFragment hf =  new hFragment();



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);



        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_menu);

        final Bundle bdle = new Bundle();
        bdle.putString("userid", getIntent().getStringExtra("userid"));
        bdle.putString("name", getIntent().getStringExtra("fname"));
        bdle.putString("email", getIntent().getStringExtra("email"));
        bdle.putString("phone", getIntent().getStringExtra("phone"));
        bdle.putDouble("latitude", getIntent().getDoubleExtra("latitude",0));
        bdle.putDouble("longitude", getIntent().getDoubleExtra("longitude",0));

        hf.setArguments(bdle);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;


                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = hf;
                                break;
                            case R.id.action_item2:
                                selectedFragment = new SymptomsFragment();
                                break;
                            case R.id.action_item3:
                                selectedFragment = new ReportFragment();
                                break;
                            case R.id.action_item5:
                                selectedFragment = new My_report();
                                break;
                            case R.id.action_item4:
                                selectedFragment = new VisualizeFragment();
                                break;


                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_container, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, hf);
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_aboutus) {
            return true;
        }
        else if(id == R.id.action_help){
            return true;

        }
        else{
            Intent lout_intent = new Intent(this, LoginActivity.class);
            lout_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            lout_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(lout_intent);

            finish();

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

    }





//
//    private void inItUi() throws Exception {
//
//
////        img_place = (ImageView) findViewById(R.id.h_img_locationpic);
////
////        ratingBar = (RatingBar) findViewById(R.id.h_ratingBar);
////
////
////
////        txt_home = (TextView) findViewById(R.id.txt_home);
////
////        frameLayout = (FrameLayout) findViewById(R.id.frame_layout_home);
////
////        txt_static_page_break_rule = (TextView) findViewById(R.id.txt_static_break_rule);
////
////
//        h_userid = (TextView) findViewById(R.id.edt_toi1);
//        h_fullname = (TextView) findViewById(R.id.edt_toi2);
//        h_email = (TextView) findViewById(R.id.edt_toi4);
//        h_phone = (TextView) findViewById(R.id.edt_toi);
//
//
//        h_userid.setText("A");//getIntent().getStringExtra("email"));
//        h_fullname.setText("B");//getIntent().getStringExtra("fname"));
//        h_email.setText("C");//getIntent().getStringExtra("email"));
//        h_phone.setText("D");//getIntent().getStringExtra("phone"));
//
//    }



}