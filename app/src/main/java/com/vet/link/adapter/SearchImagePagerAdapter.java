package com.vet.link.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vet.link.R;

import java.util.ArrayList;

import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class SearchImagePagerAdapter extends PagerAdapter {
    ImageViewTouchBase imageView;
    ArrayList<String> imgPathList;
    Context context;

    public SearchImagePagerAdapter(Context context, ArrayList<String> imgPathList) {
        this.context = context;
        this.imgPathList = imgPathList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_image_pager_adapter_child, container, false);

        imageView = view.findViewById(R.id.img);
        imageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        Glide
                .with(context)
                .load(imgPathList.get(position))
                .apply(new RequestOptions().placeholder(R.drawable.ripple).error(R.drawable.image_error))
                .into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return imgPathList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }
}
