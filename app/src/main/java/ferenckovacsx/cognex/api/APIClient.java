package ferenckovacsx.cognex.api;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ferenckovacsx on 2018-03-01.
 */

public class APIClient {

    private static Retrofit retrofit = null;

    private Context context;
    private SharedPreferences sharedpreferences;
    private String cookieValue;


    public APIClient(Context context) {
        this.context = context;
    }

    public Retrofit getClient() {

        String baseURL = "https://s3.amazonaws.com/cognex-firmware/";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}