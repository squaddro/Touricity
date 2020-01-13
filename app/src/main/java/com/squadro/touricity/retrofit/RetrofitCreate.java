package com.squadro.touricity.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCreate {

    public Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("buraya url yazilacak") // TODO: server tarafi halledildiginde gercek url'i yaz buraya
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
