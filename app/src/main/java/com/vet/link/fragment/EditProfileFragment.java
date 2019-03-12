package com.vet.link.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vet.link.R;
import com.vet.link.Services.ApiResource;
import com.vet.link.Services.CircleTransform;
import com.vet.link.Services.Utility;
import com.vet.link.Services.VolleyCommon;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by incipieninfopc20 on 19/9/17.
 */

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    private static final int SELECT_IMAGE = 100;

    public static CircularImageView circularImageView;

    private CircularImageView circularImageView1;

    private EditText txt_username;

    private Context context;

    private ViewPager viewPager;

    private TabLayout tabLayout;

    private SharedPreferences sharedPreferences;

    private ImageView btn_editname;

    private SharedPreferences.Editor editor;

    private LinearLayout lin_save;

    private Button btn_save;

    private Bitmap bitmap = null;

    private boolean isTextChanged = false;

    private boolean isImageChanged = false;

    private String str_user_name = null;
    private String base64Image = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        setHasOptionsMenu(true);

        try {

            context = container.getContext();

            inIt(rootView);

            setListeners();

            setupViewPager(viewPager);

            tabLayout.setupWithViewPager(viewPager);
        } catch (Exception e) {
            Log.e("EditProfileFragment:", e.toString());
        }

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) throws Exception {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
//        adapter.addFragment(new ReviewFragment(), "Review");
//        adapter.addFragment(new FavoriteFragment(), "Favorites");
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();

        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) throws Exception {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) throws Exception {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void inIt(View rootView) throws Exception {

        btn_editname = rootView.findViewById(R.id.btn_editname);

        lin_save = rootView.findViewById(R.id.lin_changes);

        btn_save = rootView.findViewById(R.id.btn_save);

        sharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String str_first_name = sharedPreferences.getString(getResources().getString(R.string.login_login_user_name), null);

        String str_profile_pic_url = (sharedPreferences.getString(getResources().getString(R.string.login_login_user_profile_pic), null));

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);

        circularImageView = (CircularImageView) rootView.findViewById(R.id.img_circular_profile);

        if (str_profile_pic_url != null && !str_profile_pic_url.isEmpty())

        {
            Picasso.with(getActivity())
                    .load(str_profile_pic_url).placeholder(R.drawable.no_profile_img)
                    .transform(new CircleTransform(0))
                    .into(circularImageView);
        } else

        {
            Picasso.with(getActivity())
                    .load(R.drawable.no_profile_img)
                    .transform(new CircleTransform(0))
                    .into(circularImageView);
        }
        circularImageView1 = (CircularImageView) rootView.findViewById(R.id.img_circular_addprofile);

        circularImageView.setBorderColor(getResources().getColor(R.color.lightsky));

        circularImageView1.setBorderColor(getResources().getColor(R.color.lightsky));

        txt_username = rootView.findViewById(R.id.txt_user_name);
        Typeface myTypeface3 = Typeface.createFromAsset(context.getAssets(), "fonts/light.ttf");
        txt_username.setTypeface(myTypeface3);

        txt_username.setText(str_first_name);

    }

    private void setListeners() throws Exception {
        circularImageView.setOnClickListener(this);
        circularImageView1.setOnClickListener(this);
        btn_editname.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.search).setVisible(false);
//        menu.findItem(R.id.share).setVisible(false);

       /* menu.findItem(R.id.map_normal).setVisible(false);
        menu.findItem(R.id.map_hybrid).setVisible(false);
        menu.findItem(R.id.map_satellite).setVisible(false);
        menu.findItem(R.id.map_terrin).setVisible(false);
*/
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_circular_addprofile:

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_IMAGE);

                break;

            case R.id.img_circular_profile:
                Intent in = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, SELECT_IMAGE);

                break;

            case R.id.btn_editname:

                txt_username.setCursorVisible(true);
                int pos = txt_username.getText().length();
                txt_username.setSelection(pos);
                txt_username.setFocusableInTouchMode(true);

                txt_username.setInputType(InputType.TYPE_CLASS_TEXT);
                txt_username.requestFocus();

                txt_username.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub

                        lin_save.setVisibility(View.VISIBLE);
                        isTextChanged = true;
                    }
                });

                break;

            case R.id.btn_save:

                if (isImageChanged) {
                    base64Image = Utility.convertToBase64(bitmap).trim();

                } else {
                    base64Image = "";

                }
                str_user_name = txt_username.getText().toString();
                if (!str_user_name.equals("") || !str_user_name.isEmpty()) {
                    try {
                        serverCallToUpdateProfile(base64Image, str_user_name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please, set your name!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            bitmap = BitmapFactory.decodeFile(picturePath);
            circularImageView.setImageBitmap(bitmap);
            lin_save.setVisibility(View.VISIBLE);

            isImageChanged = true;

            cursor.close();
        } else {
            Toast.makeText(getActivity(), "Try Again!!", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    private void serverCallToUpdateProfile(String base64Image, String user_name) throws Exception {
        String email = sharedPreferences.getString("email", "");
        int int_user_id = sharedPreferences.getInt("user_id", 0);
        String user_id = String.valueOf(int_user_id);

        String encodedImage = base64Image.replaceAll(" ", "");
        String enc1 = encodedImage.replaceAll("\\n", "");
        String enc2 = enc1.replaceAll("\\t", "");


        String json_request = "{\"data\":[{\"email\":\"" + email + "\",\"name\":\"" + user_name + "\",\"user_id\":\"" + user_id + "\",\"profile\":\"" + enc2.trim() + "\"}]}";

        sendDataToServerToUpdateProfile(json_request);
        Log.e(">", json_request);

    }

    private void sendDataToServerToUpdateProfile(String json_request) throws Exception {
        String url = ApiResource.UDATE_PROFILE;
        try {
            JSONObject jsonObjectForrequest = new JSONObject(json_request);

            Utility.callProgressDialog(getActivity());
            VolleyCommon.callVolley(EditProfileFragment.this, null, url, jsonObjectForrequest, "getJsonResponseToUpdateProfile", Utility.dialog);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getJsonResponseToUpdateProfile(JSONArray jsonArray) throws Exception {

        if (Utility.dialog.isShowing() || Utility.dialog != null) {
            Utility.dialog.dismiss();
        }

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String profile_url = jsonObject.getString("profile");
            String user_name = jsonObject.getString("name");

            editor.putString(getResources().getString(R.string.login_login_user_profile_pic), profile_url);
            editor.putString(getResources().getString(R.string.login_login_user_name), user_name);

            editor.apply();
            Toast.makeText(

                    getActivity(), "Profile Updated!!", Toast.LENGTH_SHORT).

                    show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
