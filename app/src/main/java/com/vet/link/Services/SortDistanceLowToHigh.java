package com.vet.link.Services;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

/**
 * Created by IncipientinfoPC 2 on 22-Apr-2016.
 */
public class SortDistanceLowToHigh implements Comparator<JSONObject> {
    @Override
    public int compare(JSONObject lhs, JSONObject rhs) {

        JSONObject jsonObject = lhs;
        JSONObject jsonObject1 = rhs;

        int distnce = 0;
        int distnce1 = 0;
        try {
            distnce = jsonObject.getJSONObject("location").getInt("distance");
            distnce1 = jsonObject1.getJSONObject("location").getInt("distance");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (distnce > distnce1) {
            return 1;
        } else if (distnce < distnce1) {
            return -1;
        }

        return 0;
    }
}
