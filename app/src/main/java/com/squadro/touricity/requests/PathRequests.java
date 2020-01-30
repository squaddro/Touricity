package com.squadro.touricity.requests;

import com.google.gson.JsonObject;
import com.squadro.touricity.converter.PathConverter;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PathRequests {

    public PathRequests() {

        PathConverter pathConverter = new PathConverter();

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JsonObject obj = new JsonObject();

        Call<JsonObject> jsonObjectCall = restAPI.sendPathRequest(obj);

        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Path path = (Path) pathConverter.jsonToObject(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                String message = t.getMessage();
            }
        });
    }
}
