package com.vet.link.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vet.link.R;


public class hFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userid";
    private static final String ARG_PARAM2 = "name";
    private static final String ARG_PARAM3 = "email";
    private static final String ARG_PARAM4 = "phone";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;



    public hFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_h, container, false);
    }


    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setText(mParam1, mParam2, mParam3, mParam4);
    }

    public void setText(String id, String name, String email, String phone){
        TextView textUserID = (TextView) getView().findViewById(R.id.edt_toi1);
        textUserID.setText(id);

        TextView textName = (TextView) getView().findViewById(R.id.edt_toi2);
        textName.setText(name);

        TextView textEmail = (TextView) getView().findViewById(R.id.edt_toi3);
        textEmail.setText(email);

        TextView textPhone = (TextView) getView().findViewById(R.id.edt_toi4);
        textPhone.setText(phone);
    }








}
