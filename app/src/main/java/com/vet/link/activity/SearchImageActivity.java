package com.vet.link.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.vet.link.R;
import com.vet.link.adapter.SearchImagePagerAdapter;

import java.util.ArrayList;
import static com.vet.link.Services.Constants.IMAGE_PATH;

public class SearchImageActivity extends AppCompatActivity {

    ViewPager viewPager;
    String image1,image2;
    private ArrayList<String> imgPathList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_image);

        viewPager = findViewById(R.id.view_pager_img);

        Intent intent = getIntent();
        image1 = intent.getStringExtra("img1");
        image2 = intent.getStringExtra("img2");
        imgPathList = new ArrayList<>();
        imgPathList.add(IMAGE_PATH+image1);
        imgPathList.add(IMAGE_PATH+image2);
        viewPager.setAdapter(new SearchImagePagerAdapter(SearchImageActivity.this,imgPathList));
    }
}
