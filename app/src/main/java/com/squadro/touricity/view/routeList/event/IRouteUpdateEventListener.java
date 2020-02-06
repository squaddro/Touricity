package com.squadro.touricity.view.routeList.event;

import com.squadro.touricity.message.types.Route;

public interface IRouteUpdateEventListener {
    void onRouteUpdate(Route route);
}
