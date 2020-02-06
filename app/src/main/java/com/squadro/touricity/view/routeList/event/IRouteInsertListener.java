package com.squadro.touricity.view.routeList.event;

import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Route;

public interface IRouteInsertListener {
    void onInsertLocation(Location location);
    void onInsertRoute(Route otherRoute);
}
