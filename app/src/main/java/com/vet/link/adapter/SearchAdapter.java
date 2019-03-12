package com.vet.link.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vet.link.R;
import com.vet.link.activity.SearchImageActivity;
import com.vet.link.pojo.SearchPojo;

import java.util.ArrayList;
import java.util.List;

import static com.vet.link.Services.Constants.IMAGE_PATH;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {
    List<SearchPojo> searchPojoList;
    Context context;

    public SearchAdapter(List<SearchPojo> searchPojoList, Context context) {
        this.searchPojoList = searchPojoList;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child, parent, false);
        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchHolder h, final int i) {


        final SearchPojo searchPojo = searchPojoList.get(i);
        String description = searchPojo.getKeywords();
//        String description = searchPojo.getDescription();
        final String image1 = searchPojo.getMedia1(), image2 = searchPojo.getMedia2();

//        Log.d("theH", "onBindViewHolder: 1: "+image1 +" :2: "+image2);
//        if (description != null && !description.isEmpty() &&
//                image1 != null && !image1.isEmpty() && !image1.equalsIgnoreCase("none")
//                && image2 != null && !image2.isEmpty() && !image2.equalsIgnoreCase("none")) {
            h.tv3.setText(description);


            Glide
                    .with(context)
                    .load(IMAGE_PATH + searchPojo.getMedia1())
                    .apply(new RequestOptions().placeholder(R.drawable.ripple).error(R.drawable.image_error))
                    .into(h.img1);
            Glide
                    .with(context)
                    .load(IMAGE_PATH + searchPojo.getMedia2())
                    .apply(new RequestOptions().placeholder(R.drawable.ripple).error(R.drawable.image_error))
                    .into(h.img2);


            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("the_", "onClick: "+searchPojo.getMedia1()+" "+searchPojo.getMedia2());
                    Intent intent = new Intent(context, SearchImageActivity.class);
                    intent.putExtra("img1", searchPojo.getMedia1());
                    intent.putExtra("img2", searchPojo.getMedia2());
                    context.startActivity(intent);
                }
            });

//        }
    }

    @Override
    public int getItemCount() {
        return searchPojoList.size();
    }

    public class SearchHolder extends RecyclerView.ViewHolder {
        TextView tv3;
        ImageView img1, img2;

        public SearchHolder(@NonNull View itemView) {
            super(itemView);

            tv3 = itemView.findViewById(R.id.tv_value3);

            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
        }
    }

    public void filterList(List<SearchPojo> searchPojoList) {
        Log.d("the_", "filterList: list updated : " + searchPojoList.size());
        this.searchPojoList = searchPojoList;
        notifyDataSetChanged();
    }
}
