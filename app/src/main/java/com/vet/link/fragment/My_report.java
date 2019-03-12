package com.vet.link.fragment;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.vet.link.rest.ApiClient;
import com.vet.link.rest.ApiInterface;
import com.vet.link.R;
import com.vet.link.adapter.SearchAdapter;
import com.vet.link.pojo.SearchPojo;
import com.victor.loading.rotate.RotateLoading;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vet.link.Services.Constant.USER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class My_report extends Fragment implements View.OnClickListener {

    View view;
    RecyclerView recyclerView;
    SearchAdapter adapter;
    List<SearchPojo> searchPojoList = null;
    Calendar myCalendar;
    EditText etFrom, etTo, etReporterID, etKeywords;
    Button btnSearch;
    String TAG = "theH";
    RotateLoading loading;
    TextView textView;


    public My_report() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_report, container, false);

        myCalendar = Calendar.getInstance();

        init();

        return view;
    }

    void init() {

        etFrom = view.findViewById(R.id.edt_searcher4);
        etTo = view.findViewById(R.id.edt_searcher2);
        etReporterID = view.findViewById(R.id.edt_searcher3);
        etKeywords = view.findViewById(R.id.edt_searcher5);
        btnSearch = view.findViewById(R.id.btn_submitregister2);
        textView = view.findViewById(R.id.tvMsg);
        loading = view.findViewById(R.id.my_report_rotateloading);
        recyclerView = view.findViewById(R.id.recycler_view);

        etReporterID.setText(USER_ID);

        etFrom.setOnClickListener(this);
        etTo.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        getData();
    }

    void setRecyclerView(List<SearchPojo> pojoList) {
        textView.setVisibility(View.GONE);
        searchPojoList = new ArrayList<>();
        for (SearchPojo searchPojo : pojoList) {
            String description = searchPojo.getDescription();
            final String image1 = searchPojo.getMedia1(), image2 = searchPojo.getMedia2();

            if (description != null && !description.isEmpty() &&
                    image1 != null && !image1.isEmpty() && !image1.equalsIgnoreCase("none")
                    && image2 != null && !image2.isEmpty() && !image2.equalsIgnoreCase("none")) {

//                Log.d(TAG, "setRecyclerView: "+image1 +" "+image2);
                searchPojoList.add(searchPojo);
            }
        }

        adapter = new SearchAdapter(searchPojoList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    void getData() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<List<SearchPojo>> call = apiService.getSearchData();
        Log.d("the_", "getData: " + call.request().url());
        call.enqueue(new Callback<List<SearchPojo>>() {
            @Override
            public void onResponse(Call<List<SearchPojo>> call, Response<List<SearchPojo>> response) {
                Log.d("the_", "onResponse: " + response.isSuccessful());
                if (response.isSuccessful()) {
                    setRecyclerView(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<SearchPojo>> call, Throwable t) {
                Log.d("the_", "onFailure: " + t);
            }
        });
    }


    private void filter(Boolean isFrom, Boolean isTo, boolean isKeyword) {

        Log.d(TAG, "filter: isFrom: " + isFrom + " isTo: " + isTo + " isKeyword: " + isKeyword);
        List<SearchPojo> pojoList = new ArrayList<>();

        if (searchPojoList != null ) {

            String id, keyword, date, stId, stKeyword, stFrom, stTo;
            for (SearchPojo s : searchPojoList) {

                id = s.getReporterID().toLowerCase();
                keyword = s.getKeywords().toLowerCase();
                date = s.getDTStamp();

                stId = etReporterID.getText().toString().toLowerCase();
                stKeyword = etKeywords.getText().toString().toLowerCase();
                stFrom = etFrom.getText().toString();
                stTo = etTo.getText().toString();

                Date dateForMatch = formattedDate(date), dateFrom = formattedDate(stFrom), dateTo = formattedDate(stTo);

                if (id.equals(stId)) {

                    if (isKeyword && isFrom && isTo) {

                        Log.d(TAG, "0: isKeyword: " + isKeyword + " isFrom: " + isFrom + " isTo: " + isTo);
                        pojoList.add(s);
                    } else {
                        if (!isKeyword && isFrom && isTo) {
                            Log.d(TAG, "1: !isKeyword: " + !isKeyword + " isFrom: " + isFrom + " isTo: " + isTo);
                            if (keyword.contains(stKeyword)) {
                                pojoList.add(s);
                            }
                        } else if (isKeyword && !isFrom && isTo) {
                            Log.d(TAG, "2: isKeyword: " + isKeyword + " !isFrom: " + !isFrom + " isTo: " + isTo);
                            // display date which is after from dateFrom
                            if (dateForMatch.after(dateFrom)) {
                                pojoList.add(s);
                            }
                        } else if (isKeyword && isFrom && !isTo) {
                            Log.d(TAG, "3: isKeyword: " + isKeyword + " isFrom: " + isFrom + " !isTo: " + !isTo);

                            // display date which is before from dateTo
                            if (dateForMatch.before(dateTo)) {
                                pojoList.add(s);
                            }
                        } else if (isKeyword && !isFrom && !isTo) {
                            Log.d(TAG, "4: isKeyword: " + isKeyword + " !isFrom: " + !isFrom + " !isTo: " + !isTo);
                            if (dateForMatch.after(dateFrom) && dateForMatch.before(dateTo)) {
                                pojoList.add(s);
                            }
                        } else if (!isKeyword && !isFrom && !isTo) {
                            Log.d(TAG, "5: !isKeyword: " + !isKeyword + " !isFrom: " + !isFrom + " !isTo: " + !isTo);
                            if (dateForMatch.after(dateFrom) && dateForMatch.before(dateTo) && keyword.contains(stKeyword)) {
                                pojoList.add(s);
                            }
                        }
                    }
                }
            }
        }else {
            loading.start();
        }


        if (pojoList.size() != 0) {
            adapter.filterList(pojoList);
            loading.stop();
            recyclerView.setVisibility(View.VISIBLE);

        }else {
            loading.stop();
            textView.setVisibility(View.VISIBLE);
        }

        etKeywords.setText(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edt_searcher4:
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "yyyy-MM-dd"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        etFrom.setText(sdf.format(myCalendar.getTime()));
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.edt_searcher2:
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "yyyy-MM-dd"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        etTo.setText(sdf.format(myCalendar.getTime()));
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.btn_submitregister2:
                loading.start();
                filter(
                        isEmpty(etFrom),
                        isEmpty(etTo),
                        isEmpty(etKeywords)
                );
                break;

        }
    }

    public Date formattedDate(String dtStart) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;

        try {
            date = format.parse(dtStart);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    public boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
