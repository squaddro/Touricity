package com.squadro.touricity.requests;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Comment;
import com.squadro.touricity.message.types.CommentRegister;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;
import com.squadro.touricity.view.routeList.CommentItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lombok.SneakyThrows;
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
                        Toast.makeText((Activity)context,"Commented!",Toast.LENGTH_LONG).show();
                        EditText text = likeCommentView.findViewById(R.id.PostCommentDesc);
                        text.setText("");
                    }
                    else if(statusCode.equals("115")){
                        Toast.makeText((Activity)context,"Error! Something's wrong :(",Toast.LENGTH_LONG).show();
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

    public void getComment(String routeId) throws JSONException {

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JsonObject obj = new JsonObject();
        obj.addProperty("route_id", routeId);
        Call<JsonObject> jsonObjectCall = restAPI.getComment(obj);
        System.out.println("1. checkpoint");
        ArrayList<CommentRegister> commentRegisters = new ArrayList<>();

        jsonObjectCall.enqueue(new Callback<JsonObject>() {

            @SneakyThrows
            @Override
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    LinearLayout commentList = likeCommentView.findViewById(R.id.comment_list);
                    JsonArray commentRegisterList = response.body().getAsJsonArray("commentRegisterList");
                    for (JsonElement element : commentRegisterList) {
                        CommentRegister commentRegister = new CommentRegister();
                        commentRegister.setUsername(element.getAsJsonObject().get("username").getAsString());
                        commentRegister.setRouteId(routeId);
                        JsonObject commentObject = (JsonObject) element.getAsJsonObject().get("comment");
                        Comment comment = new Comment();
                        comment.setCommentDesc(commentObject.get("commentDesc").getAsString());
                        comment.setCommentId(commentObject.get("commentId").getAsString());
                        commentRegister.setComment(comment);
                        commentRegisters.add(commentRegister);
                    }

                    for (CommentRegister commentRegister : commentRegisters) {
                        CommentItem commentItem = new CommentItem(context);
                        commentItem.setCommentItemElements(commentRegister.getUsername(), commentRegister.getComment().getCommentDesc());
                        commentList.addView(commentItem.getView());
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
