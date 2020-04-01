package com.squadro.touricity.requests;

import android.content.Context;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squadro.touricity.message.types.CommentRegister;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;
import com.squadro.touricity.view.routeList.CommentItem;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentRequest {

    private final Context context;
    private final LinearLayout likeCommentView;

    public CommentRequest(Context context, LinearLayout likeCommentView) {
        this.context = context;
        this.likeCommentView = likeCommentView;
    }

    public void postComment(CommentRegister commentRegister) throws JSONException {

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JSONObject commentReqObj = new JSONObject();    // Host object
        JSONObject routeObj = new JSONObject();         // Included object
        JSONObject commentObj = new JSONObject();       // Included object

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
                        CommentItem commentItem = new CommentItem(context);
                        commentItem.setCommentItemElements(commentRegister.getUsername(), commentRegister.getComment().getCommentDesc());
                        likeCommentView.addView(commentItem.getView());
                    }
                    else if(statusCode.equals("115")){
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
