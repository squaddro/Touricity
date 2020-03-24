package com.squadro.touricity.retrofit;

import com.squadro.touricity.cookie.AddCookiesInterceptor;
import com.squadro.touricity.cookie.ReceivedCookiesInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCreate {

    public Retrofit createRetrofit() {

        OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60 * 5, TimeUnit.SECONDS)
                .readTimeout(60 * 5, TimeUnit.SECONDS)
                .writeTimeout(60 * 5, TimeUnit.SECONDS);
        okHttpClient.interceptors().add(new AddCookiesInterceptor());
        okHttpClient.interceptors().add(new ReceivedCookiesInterceptor());

        return new Retrofit.Builder()
                .baseUrl("https://touricity.herokuapp.com/")
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit createRetrofitForGoogle(){
        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
