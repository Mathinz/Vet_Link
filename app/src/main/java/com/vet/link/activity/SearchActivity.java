package com.vet.link.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vet.link.R;

import static com.vet.link.activity.MainActivity.context;

public class SearchActivity extends AppCompatActivity {

    private Button searchB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchB = (Button) findViewById(R.id.btn_submit_search);

        Typeface myTypeface3 = Typeface.createFromAsset(context.getAssets(), "fonts/extra_bold.ttf");

        searchB.setTypeface(myTypeface3);


        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getApplicationContext(), MainActivity.class);

                in.putExtra("fname",getIntent().getStringExtra("fname"));
                in.putExtra("email",getIntent().getStringExtra("email"));
                in.putExtra("phone",getIntent().getStringExtra("phone"));
                startActivity(in);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() { }



}
