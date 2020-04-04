package com.squadro.touricity.view.routeList;

import com.squadro.touricity.message.types.Route;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class SavedRoutesItem {
    @Getter
    @Setter
    private List<Route> routes;
    @Getter
    @Setter
    private List<MyPlaceSave> myPlaces;

    public SavedRoutesItem(List<Route> routes, List<MyPlaceSave> myPlaces) {
        this.routes = routes;
        this.myPlaces = myPlaces;
    }
}
