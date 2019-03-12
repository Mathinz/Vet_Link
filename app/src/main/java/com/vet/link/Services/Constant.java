package com.vet.link.Services;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.vet.link.pojo.SearchPojo;
import com.vet.link.pojo.SymptomsPojo;

import java.util.List;

/**
 * Created by IncipientinfoPC 2 on 06-May-2016.
 */
public class Constant {

    public static final int REQUEST_CODE_FOR_NAME = 1234;

    public static final int REQUEST_CODE_FOR_GOOGLE_LOGIN = 1001;

    public static final int REQUEST_CODE_FOR_CHECK_DISABILITY = 1235;

    public static final int REQUEST_SEARCH_CODE = 1236;

    public static final int REQUEST_CODE_FOR_SEARCH_SPEAK_DATA = 1239;

    public static final int REQUEST_SEARCH_RIGHT_CODE = 1237;

    public static final int REQUEST_REVIEW_CODE = 1250;

    public static final int REQUEST_NO_REVIEW_CODE = 1251;

    public static final int REQUEST_WHEELCHAIR_CODE = 1252;

    public static final int REQUEST_ASSISTANCE_FRIENDLY_CODE = 1253;

    public static final int REQUEST_RATING_CODE = 1254;

    public static final int REQUEST_REVIEW_SUBMITED = 1255;

    public static final int REQUEST_CODE_FOR_FACEBOOK = 64206;

    public static final int REQUEST_CODE_FOR_SPLASH_THREAD = 1500;

    public static final float REQUEST_CODE_FOR_TEXT_2_SPEECH = 0.8f;

    public static String USER_ID = "";

    public static List<SymptomsPojo> symptomsPojoList = null;
    public static List<SearchPojo> searchPojoList = null;

    public static boolean hasPermissions(Context context, String... permissions) {

        Log.d("perm_debug", "hasPermissions: permission size= " + permissions.length);

        for(int i=0; i<permissions.length; i++) {
            if(ContextCompat.checkSelfPermission(context, permissions[i]) == PackageManager.PERMISSION_GRANTED){
                continue;
            } else {
                Log.d("fun_debug", "hasPermissions: returning false");
                return false;
            }
        }
        Log.d("fun_debug", "hasPermissions: returning true");
        return true;
    }

    // Return a substring between the two strings.
    public static String between(String value, String a, String b) {
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        int posB = value.lastIndexOf(b);
        if (posB == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= posB) {
            return "";
        }
        return value.substring(adjustedPosA, posB);
    }


}
