package com.squadro.touricity.message.types;

import java.util.ArrayList;

public class FilterResult {

    private ArrayList<Route> routeList;

    public FilterResult(ArrayList<Route> routeList){
        this.routeList = routeList;
    }

    public ArrayList<Route> getRouteList(){
        return routeList;
    }

    public void setRouteList(ArrayList<Route> routeList){
        this.routeList = routeList;
    }
}
