package com.vet.link.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vet.link.R;


/**
 * Created by IncipientinfoPC 2 on 16-May-2016.
 */
public class AboutUsFragment extends Fragment {

    TextView txt1;

    TextView txt2;

    TextView txt3;

    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);

        setHasOptionsMenu(true);

        context = container.getContext();

        txt1 = (TextView) rootView.findViewById(R.id.txt1);
        Typeface myTypeface3 = Typeface.createFromAsset(context.getAssets(), "fonts/light.ttf");
        txt1.setTypeface(myTypeface3);

        txt2 = (TextView) rootView.findViewById(R.id.txt2);
        Typeface myTypeface2 = Typeface.createFromAsset(context.getAssets(), "fonts/light.ttf");
        txt2.setTypeface(myTypeface2);

        txt3 = (TextView) rootView.findViewById(R.id.txt3);
        Typeface myTypeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/light.ttf");
        txt3.setTypeface(myTypeface1);

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.search).setVisible(false);
//        menu.findItem(R.id.share).setVisible(false);

     /*   menu.findItem(R.id.map_normal).setVisible(false);
        menu.findItem(R.id.map_hybrid).setVisible(false);
        menu.findItem(R.id.map_satellite).setVisible(false);
        menu.findItem(R.id.map_terrin).setVisible(false);
*/
        super.onPrepareOptionsMenu(menu);
    }
}
