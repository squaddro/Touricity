package com.squadro.touricity.view.map.DirectionsAPI;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.routeList.RouteCreateView;


public class RouteMerger {

    private RouteCreateView rcw = null;


    public RouteMerger(RouteCreateView rcw){
        this.rcw = rcw;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mergeRoute(Route r1){

        for (IEntry entry:r1.getEntries()) {
            if(entry instanceof Stop){
                rcw.onInsertStop((Stop) entry);
            }
        }
    }

}
