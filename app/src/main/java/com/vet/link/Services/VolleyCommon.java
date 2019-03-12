package com.vet.link.Services;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by incipientinfo_pc5 on 7/1/17.
 */

public class VolleyCommon {

    public static void callVolley(final Fragment fragment, final Context context, String url, JSONObject jsonObjectForrequest, final String func, final ProgressDialog dialog) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjectForrequest, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (dialog != null || dialog.isShowing()) {
                        dialog.dismiss();
                    } else {
                        Log.e("volley", "dia");
                    }
                } catch (Exception e) {
                    Log.e("", "dialog error");
                }
                Method m = null;
                Log.e("Response", "" + response);

                if (fragment != null) {
                    if (response.has("success")) {

                        try {

                            JSONArray json_arr = response.getJSONArray("success");
                            m = fragment.getClass().getDeclaredMethod(func, JSONArray.class);
                            m.setAccessible(true);
                            m.invoke(fragment, json_arr);

                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (response.has("message")) {
                        try {
                            String msg = response.getString("message");
                            m = fragment.getClass().getDeclaredMethod(func, String.class);
                            m.setAccessible(true);
                            m.invoke(fragment, msg);
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        try {

                            String str_error = response.getJSONArray("error").getJSONObject(0).getString("msg");
                            Toast.makeText(fragment.getActivity(), str_error, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else

                {
                    if (response.has("success")) {

                        try {

                            JSONArray json_arr = response.getJSONArray("success");
                            m = context.getClass().getDeclaredMethod(func, JSONArray.class);
                            m.setAccessible(true);
                            m.invoke(context, json_arr);

                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (response.has("message")) {
                        try {
                            String msg = response.getString("message");
                            m = context.getClass().getDeclaredMethod(func, String.class);
                            m.setAccessible(true);
                            m.invoke(context, msg);
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        try {

                            String str_error = response.getJSONArray("error").getJSONObject(0).getString("msg");
                            Toast.makeText(context, str_error, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
                , new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                error.printStackTrace();
                if (fragment != null) {
                    Toast.makeText(fragment.getActivity(), "Error while getting data, please try after some time.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Error while getting data, please try after some time.", Toast.LENGTH_SHORT).show();
                }
            }
        })

        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new

                                                 RetryPolicy() {
                                                     @Override
                                                     public int getCurrentTimeout() {
                                                         return 50000;
                                                     }

                                                     @Override
                                                     public int getCurrentRetryCount() {
                                                         return 50000;
                                                     }

                                                     @Override
                                                     public void retry(VolleyError error) throws VolleyError {

                                                         Log.i("err", error.toString());
                                                     }
                                                 });
        if (fragment != null)

        {
            RequestQueue requestQueue = Volley.newRequestQueue(fragment.getActivity());
            requestQueue.add(jsonObjectRequest);
        } else

        {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);
        }

    }

}
