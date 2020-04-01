package com.squadro.touricity.message.types;

import java.util.ArrayList;

public class RouteLike {

    private Route route;
    private double score;

    public RouteLike(){}

    public RouteLike(Route route, int score){
        this.route = route;
        this.score = score;
    }

    public Route getRoute(){
        return route;
    }

    public void setRoute(Route route){
        this.route = route;
    }
    public double getScore(){
        return score;
    }

    public void setScore(double score){
        this.score = score;
    }

}
