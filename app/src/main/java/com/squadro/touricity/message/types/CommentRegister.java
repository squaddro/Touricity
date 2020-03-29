package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.Comment;

public class CommentRegister{

    private String username;
    private Comment comment;
    private String routeId;

    public CommentRegister(){}

    public CommentRegister(String username, Comment comment, String routeId){
        this.username = username;
        this.comment = comment;
        this.routeId = routeId;
    }

    public Comment getComment() {
        return comment;
    }

    public String getUsername() {
        return username;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}