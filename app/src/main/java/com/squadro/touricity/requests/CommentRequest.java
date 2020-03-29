package com.squadro.touricity.requests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squadro.touricity.HomeActivity;
import com.squadro.touricity.MainActivity;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Credential;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentRequest {

    private final Context context;
    private final Activity activity;

    public CommentRequest(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void postComment(com.squadro.touricity.message.types.data.CommentRegister commentRegister) throws JSONException {

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JSONObject commentReqObj = new JSONObject();    // Host object
        JSONObject routeObj = new JSONObject();         // Included object
        JSONObject commentObj = new JSONObject();         // Included object

        commentReqObj.put("username", commentRegister.getUsername());

        commentObj.put("commentDesc", commentRegister.getComment().getCommentDesc());
        commentReqObj.put("comment", commentObj);

        routeObj.put("route_id", commentRegister.getRouteId());
        commentReqObj.put("routeId", routeObj);


        org.json.JSONObject object = commentReqObj;
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonObject = (JsonObject)jsonParser.parse(object.toString());

        Call<JsonObject> jsonObjectCall = restAPI.postComment(gsonObject);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                if (body != null) {
                    String statusCode = body.get("code").getAsString();
                    if(statusCode.equals("114")){

                        }
                    else if(statusCode.equals("115")){
                        Toast.makeText(activity,"Error! Comment failed.",Toast.LENGTH_LONG).show();
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
