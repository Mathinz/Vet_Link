package com.vet.link.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.vet.link.Services.Constants.BASE_URL;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        // setting custom timeouts
//        OkHttpClient.Builder client = new OkHttpClient.Builder();
//        client.connectTimeout(60, TimeUnit.SECONDS);
//        client.readTimeout(60, TimeUnit.SECONDS);
//        client.writeTimeout(60, TimeUnit.SECONDS);
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
//                  .client(client.build())
        }

        return retrofit;
    }
}
