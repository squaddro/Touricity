package com.squadro.touricity.view.routeList.event;

import com.squadro.touricity.message.types.Route;

public interface IRouteSave {
    void saveRoute(Route route);

    void deleteRoute(Route route);

    void startProgress(Route route);

    void endProgress();
}
