package com.squadro.touricity.message.types;

public class LikeRegister {

    private String username;
    private Like like;
    private String routeId;

    public LikeRegister(){}

    public LikeRegister(String username, Like like){
        this.username = username;
        this.like = like;
    }

    public Like getLike() {
        return like;
    }

    public String getUsername() {
        return username;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setLike(Like like) {
        this.like = like;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}