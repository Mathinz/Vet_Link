package com.vet.link.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vet.link.R;
import com.vet.link.model.Diseases;
import com.vet.link.pojo.SymptomsPojo;

import java.util.List;

public class SymptomsAdapter extends RecyclerView.Adapter<SymptomsAdapter.SymptomsHolder> {
    Context context;
    List<SymptomsPojo> symptomsPojoList;
    List<List<Diseases>> listList;

    public SymptomsAdapter(Context context, List<SymptomsPojo> symptomsPojoList, List<List<Diseases>> listList) {
        this.context = context;
        this.symptomsPojoList = symptomsPojoList;
        this.listList = listList;
    }

    @NonNull
    @Override
    public SymptomsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptoms_child, parent, false);
        return new SymptomsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SymptomsHolder holder, final int position) {

        final SymptomsPojo symptomsPojo = symptomsPojoList.get(position);
        holder.textViewTitle.setText(symptomsPojo.getCommonSymptoms());
        DiseaseAdapter diseaseAdapter = new DiseaseAdapter(context, listList.get(position));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        holder.recyclerView.setAdapter(diseaseAdapter);

    }

    @Override
    public int getItemCount() {
        return symptomsPojoList.size();
    }

    public class SymptomsHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        RecyclerView recyclerView;
        public SymptomsHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.s_child_tv);
            recyclerView = itemView.findViewById(R.id.s_child_recycler_view);
        }
    }

    public void filterList(List<SymptomsPojo> symptonsPojoList) {
        this.symptomsPojoList = symptonsPojoList;
        notifyDataSetChanged();
    }
}
