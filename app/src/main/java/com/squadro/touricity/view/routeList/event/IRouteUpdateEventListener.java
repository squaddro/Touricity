package com.squadro.touricity.view.routeList.event;

import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Stop;

public interface IRouteUpdateEventListener {
    void onPathUpdate(Path path);
    void onStopUpdate(Stop stop);
}
