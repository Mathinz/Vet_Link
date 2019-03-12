package com.vet.link.Services;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vet.link.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by IncipientinfoPC 2 on 28-Mar-2016.
 */
public class Utility {

    public static ProgressDialog dialog;

    public static boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        if (network == null) {
            return false;
        } else
            return true;
    }

    public static String convertToBase64(Bitmap bm)

    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);

        byte[] byteArrayImage = baos.toByteArray();

        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        return encodedImage;

    }

    public static String convertToBase64fromUrl(String imagePath)

    {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);

        byte[] byteArrayImage = baos.toByteArray();

        String enc = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);


        return enc;

    }

    public static void alertdiolog(Context context, String message, String btnText) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setPositiveButton(btnText, null);
        dialog.setTitle(message);
        dialog.setCancelable(false);

        dialog.show();
    }

    public static void callProgressDialog(Context context) {
        dialog = new ProgressDialog(context);
        ProgressBar spinner = new android.widget.ProgressBar(context, null, android.R.attr.progressBarStyle);
        spinner.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF0C8B70"), android.graphics.PorterDuff.Mode.SRC_IN);
        dialog.setIndeterminate(true);
        dialog.setMessage("Loading..");
        dialog.setCanceledOnTouchOutside(false);

        if (!((Activity) context).isFinishing()) {
            dialog.show();
        }
    }

    public static void showSnackBarNoInternet(Context context, final CoordinatorLayout coordinatorLayout) {

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, context.getResources().getString(R.string.error_no_internet), Snackbar.LENGTH_LONG)
                .setAction(context.getResources().getString(R.string.error_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG);
                        snackbar1.show();
                        snackbar1.dismiss();
                    }
                });

        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();
    }

    public static void showSnackBarWithGps(Context context, final CoordinatorLayout coordinatorLayout) {

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, context.getResources().getString(R.string.error_on_gps_and_internet), Snackbar.LENGTH_LONG)
                .setAction(context.getResources().getString(R.string.error_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG);
                        snackbar1.show();
                        snackbar1.dismiss();
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();
    }

    public static void requestPermissionForCamera(Context c) {

        ActivityCompat.requestPermissions((Activity) c, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 123);

    }

    public static boolean checkPermissionForCamera(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);

        if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
