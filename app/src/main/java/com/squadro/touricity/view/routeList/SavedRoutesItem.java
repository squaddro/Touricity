package com.squadro.touricity.view.routeList;

import com.squadro.touricity.message.types.Route;

import java.util.List;

import lombok.Getter;

public class SavedRoutesItem {
    @Getter
    private List<Route> routes;

    public SavedRoutesItem(List<Route> routes) {
        this.routes = routes;
    }
}
