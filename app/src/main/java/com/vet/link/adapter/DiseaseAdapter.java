package com.vet.link.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vet.link.R;
import com.vet.link.activity.SymptomsActivity;
import com.vet.link.model.Diseases;

import java.util.List;

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.DiseaseHolder> {
    Context context;
    List<Diseases> diseasesList;

    public DiseaseAdapter(Context context, List<Diseases> diseasesList) {
        this.context = context;
        this.diseasesList = diseasesList;
    }

    @NonNull
    @Override
    public DiseaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diseases_child, parent, false);
        return new DiseaseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseHolder holder, final int position) {
        Diseases diseases = diseasesList.get(position);
//        Log.d("the_", "onBindViewHolder: getTitle: "+diseases.getTitle());
        if (diseases.getTitle() != null) {
            holder.textViewDetails.setText(diseases.getTitle());
        } else {
            holder.textViewDetails.setVisibility(View.GONE);
        }

        holder.textViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Diseases diseases = null;
                if (position != 0) {
                    diseases = diseasesList.get(position - 1);
                    Intent intent = new Intent(context, SymptomsActivity.class);
                    intent.putExtra("link", diseases.getLink());
                    context.startActivity(intent);
                    Log.d("the_", "onClick: " + diseases.getLink());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return diseasesList.size();
    }

    public class DiseaseHolder extends RecyclerView.ViewHolder {
        TextView textViewDetails;

        public DiseaseHolder(View itemView) {
            super(itemView);

            textViewDetails = itemView.findViewById(R.id.d_child_tv);
            textViewDetails.setPaintFlags(textViewDetails.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }
}
