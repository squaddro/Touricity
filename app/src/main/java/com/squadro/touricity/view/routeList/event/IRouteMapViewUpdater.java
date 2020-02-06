package com.squadro.touricity.view.routeList.event;

import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Route;

public interface IRouteMapViewUpdater {
    void updateRoute(Route route);
    void highlight(AbstractEntry entry);
    void focus(AbstractEntry entry);
}
