package com.squadro.touricity.requests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squadro.touricity.message.types.CommentRegister;
import com.squadro.touricity.message.types.LikeRegister;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LikeRequest {

    public void postLike(LikeRegister likeRegister) throws JSONException {

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JSONObject likeReqObj = new JSONObject();    // Host object
        JSONObject routeObj = new JSONObject();         // Included object
        JSONObject likeObj = new JSONObject();       // Included object

        likeReqObj.put("username", likeRegister.getUsername());
        likeObj.put("score", likeRegister.getLike().getScore());
        likeReqObj.put("like", likeObj);
        routeObj.put("route_id", likeRegister.getRouteId());
        likeReqObj.put("routeId", routeObj);

        JSONObject object = likeReqObj;
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonObject = (JsonObject)jsonParser.parse(object.toString());

        Call<JsonObject> jsonObjectCall = restAPI.postLike(gsonObject);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                if (body != null) {
                    String statusCode = body.get("code").getAsString();
                    if(statusCode.equals("116")){

                    }
                    else if(statusCode.equals("117")){
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                String message = t.getMessage();
                System.out.println(message);
            }
        });
    }

}
