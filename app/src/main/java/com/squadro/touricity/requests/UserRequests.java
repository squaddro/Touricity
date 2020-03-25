package com.squadro.touricity.requests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squadro.touricity.HomeActivity;
import com.squadro.touricity.MainActivity;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Credential;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserRequests {

    private final Context context;
    private final Activity activity;

    public UserRequests(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void signin(Credential credential) {

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JsonObject obj = new JsonObject();
        obj.addProperty("user_name", credential.getUser_name());
        obj.addProperty("token", credential.getToken());

        Call<JsonObject> jsonObjectCall = restAPI.signin(obj);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                if (body != null) {
                    String statusCode = body.get("code").getAsString();
                    if(statusCode.equals("100")){
                        ((Activity)context).findViewById(R.id.signin_layout).setVisibility(View.INVISIBLE);
                        activity.startActivity(new Intent(MainActivity.context, HomeActivity.class));
                        activity.finish();
                        }
                    else if(statusCode.equals("101")){
                        Toast.makeText(activity,"Error! Could not login",Toast.LENGTH_LONG).show();
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

    public void signup(Credential credential) {

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JsonObject obj = new JsonObject();
        obj.addProperty("user_name", credential.getUser_name());
        obj.addProperty("token", credential.getToken());

        Call<JsonObject> jsonObjectCall = restAPI.signup(obj);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                if (body != null) {
                    String statusCode = body.get("code").getAsString();
                    if(statusCode.equals("102")){
                        Toast.makeText(activity,"Register successful!",Toast.LENGTH_LONG).show();
                        ((Activity)context).findViewById(R.id.signin_layout).setVisibility(View.VISIBLE);
                        ((Activity)context).findViewById(R.id.register_layout).setVisibility(View.INVISIBLE);
                    }
                    else if(statusCode.equals("103") || statusCode.equals("105")){
                        Toast.makeText(activity,"Error! Register could not be done!",Toast.LENGTH_LONG).show();
                    }
                    else if(statusCode.equals("104")){
                        Toast.makeText(activity,"Error! Username already exists!",Toast.LENGTH_LONG).show();
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
