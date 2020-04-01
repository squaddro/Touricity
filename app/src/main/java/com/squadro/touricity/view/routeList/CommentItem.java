package com.squadro.touricity.view.routeList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.squadro.touricity.R;

public class CommentItem extends View {

    private View parentView;
    private TextView userNameText = null;
    private TextView commentDescText = null;

    public CommentItem(Context context) {
        super(context);
        parentView = LayoutInflater.from(context).inflate(R.layout.comment_item, null, false);
    }

    public void setCommentItemElements(String username, String commentDesc){
        userNameText = parentView.findViewById(R.id.comment_user_name);
        commentDescText = parentView.findViewById(R.id.CommentDesc);
        userNameText.setText(username);
        commentDescText.setText(commentDesc);
    }

    public View getView(){
        return parentView;
    }
}
