package com.squadro.touricity.view.routeList;

import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.map.placesAPI.MyPlace;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class RoutePlace {
    @Getter
    @Setter
    private List<Route> routes;
    @Getter
    @Setter
    private List<MyPlace> myPlaces;

    public RoutePlace(List<Route> routes, List<MyPlace> myPlaces) {
        this.routes = routes;
        this.myPlaces = myPlaces;
    }
}