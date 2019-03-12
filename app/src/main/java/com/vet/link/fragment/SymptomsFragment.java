package com.vet.link.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.vet.link.R;
import com.vet.link.adapter.SymptomsAdapter;
import com.vet.link.model.Diseases;
import com.vet.link.pojo.SymptomsPojo;
import com.vet.link.rest.ApiClient;
import com.vet.link.rest.ApiInterface;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.vet.link.Services.Constant.symptomsPojoList;


public class SymptomsFragment extends Fragment implements View.OnClickListener {

    private static final int SELECT_IMAGE = 100;

    public static CircularImageView circularImageView;

    private CircularImageView circularImageView1;

    private EditText txt_username;

    private Context context;

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    private Button searchB;

    RecyclerView recyclerView;

    SymptomsAdapter adapter;

    TextView textViewMsg;

    RotateLoading loading;

    String TAG = "the_";
    List<List<Diseases>> listList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_symptoms, container, false);

        setHasOptionsMenu(true);

        try {

            context = container.getContext();

            listList = new ArrayList<>();

            getData();

            inIt(rootView);

            setListeners();

        } catch (Exception e) {
            Log.e("searchFragment:", e.toString());
        }

        return rootView;
    }

    private void inIt(View rootView) throws Exception {

        searchB = (Button) rootView.findViewById(R.id.btn_submit_complain);
        txt_username = (EditText) rootView.findViewById(R.id.edt_name);
        textViewMsg = rootView.findViewById(R.id.tv_symptoms);
        recyclerView = rootView.findViewById(R.id.symptoms_recycler_view);
        loading = rootView.findViewById(R.id.symptoms_rotateloading);

        Typeface myTypeface3 = Typeface.createFromAsset(context.getAssets(), "fonts/extra_bold.ttf");

        searchB.setTypeface(myTypeface3);
        sharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();


    }

    private void setListeners() throws Exception {
        searchB.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_submit_complain:
                loading.start();
                filter();
                break;


        }

    }



    void getData() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<List<SymptomsPojo>> call = apiService.getSymptomsData();
        Log.d("the_", "SymptomsAdapter: " + call.request().url());
        call.enqueue(new Callback<List<SymptomsPojo>>() {
            @Override
            public void onResponse(Call<List<SymptomsPojo>> call, Response<List<SymptomsPojo>> response) {
                Log.d("the_", "onResponse: " + response.isSuccessful());
                if (response.isSuccessful()) {
                    symptomsPojoList = response.body();

                    for (int i = 0; i < symptomsPojoList.size(); i++) {
                        String[] arrOfStr = symptomsPojoList.get(i).getPossibleDisease().split("/ ,");
//                        Log.d(TAG, "onResponse: arrOfStr size i : " + arrOfStr.length);
                        List<Diseases> diseasesList = new ArrayList<>();
                        for (int j = 0; j < arrOfStr.length; j++) {

//                            Log.d(TAG, i+" outer: arrOfStr : " + arrOfStr[j]);
                            String[] arrOfStrsb = arrOfStr[j].split(",");
//                            Log.d(TAG, "onResponse: arrOfStrsb size j : " + arrOfStrsb.length);
                            StringBuilder builder = new StringBuilder();
                            Diseases diseases = new Diseases();
                            for (int k = 0; k < arrOfStrsb.length; k++) {
                                if (arrOfStrsb[k].contains("http://")) {
                                    diseases.setLink(arrOfStrsb[k]);
//                                    Log.d(TAG, "i: " + i + " j: " + j + " k: " + k + " inner: link: " + arrOfStrsb[k]);
                                } else {
                                    builder.append(arrOfStrsb[k]);
                                    diseases.setTitle(builder.toString());
//                                    Log.d(TAG, "i: "+i+" j: "+j+" k: "+k+" inner: sb: " +arrOfStrsb[k]);
//                                    Log.d(TAG, "i: " + i + " j: " + j + " k: " + k + " inner: sb: " + builder.toString());
//                                    if ( k == (arrOfStrsb.length-1)){
//
//                                    }
                                }

                            }
//                            Log.d(TAG, "i: " + i + " j: " + j +" diseases getTitle: "+diseases.getTitle() +" getLink: "+diseases.getLink());

                            diseasesList.add(diseases);
//                            Log.d(TAG, "j: listList: "+listList.size() +" diseasesList: "+diseasesList.size());
                        }
                        listList.add(diseasesList);
//                        Log.d(TAG, "i: listList: "+listList.size() +" diseasesList: "+diseasesList.size());
//                        Log.d("the_", "-----------------------------------");
                    }

                    Log.d(TAG, "onCreateView: listList "+listList.size());
                    for (List<Diseases> diseasesList:listList){

                        Log.d(TAG, "onResponse: diseasesList: "+diseasesList.size());
                        for (Diseases diseases: diseasesList){
                            Log.d(TAG, "onCreateView: "+diseases.getTitle() +" "+diseases.getLink());
                        }
                        Log.d(TAG, "------------------------");
                    }

                    setRecyclerView(listList);
                }
            }

            @Override
            public void onFailure(Call<List<SymptomsPojo>> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                Log.d("the_", "onFailure: " + t);
            }
        });

    }

    void setRecyclerView(List<List<Diseases>> listLists) {
        Log.d("the_", "onResponse: " + symptomsPojoList.size());
        adapter = new SymptomsAdapter( getContext(), symptomsPojoList,listLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        loading.stop();
    }

    private void filter() {

        recyclerView.setVisibility(View.GONE);
        String text = txt_username.getText().toString().toLowerCase();
        List<SymptomsPojo> pojoList = new ArrayList<>();

        if (!text.isEmpty()) {
            textViewMsg.setVisibility(View.GONE);
            for (SymptomsPojo s : symptomsPojoList) {
                String matchText = s.getCommonSymptoms().toLowerCase();
                if (matchText.contains(text)) {
                    pojoList.add(s);
                }

            }
            if (pojoList.size() == 0)
                textViewMsg.setVisibility(View.VISIBLE);

            loading.stop();
            adapter.filterList(pojoList);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            loading.stop();
            textViewMsg.setVisibility(View.VISIBLE);
        }
    }

}