package com.squadro.touricity.message.types;

public class RouteRegister {

    private String username;
    private Route route;

    public RouteRegister(){}

    public RouteRegister(String username, Route route){
        this.username = username;
        this.route = route;
    }

    public Route getRoute() {
        return route;
    }

    public String getUsername() {
        return username;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}