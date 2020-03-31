package com.squadro.touricity.view.map.DirectionsAPI;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.routeList.RouteCreateView;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class WaypointOrder implements IAsync {

    RouteCreateView rcw = null;
    public IAsync async;



    public WaypointOrder(String url, RouteCreateView route){
        this.async = async;
        this.rcw = route;
        FetchUrl fetchUrl = new FetchUrl(this);
        fetchUrl.execute(url);
    }

    @Override
    public void onComplete(String data) {
        WaypointParser wp = new WaypointParser();
        List<Integer> waypointOrder = wp.parse(data);

        routeOrder(rcw, waypointOrder);

    }

    public void routeOrder(RouteCreateView rcw, List<Integer> order) {

        Route tmpRoute = rcw.getRoute();
        List<Stop> stopList = new ArrayList<>(order.size());
        List<Stop> orderedStopList = new ArrayList<>(20);

        for (IEntry entry:tmpRoute.getAbstractEntryList()) {
            if(entry instanceof Stop){
                stopList.add((Stop) entry);
            }
        }

      /*  while(rcw.getRoute().getAbstractEntryList().size() > 0){
            rcw.onRemoveEntry((AbstractEntry) rcw.getRoute().getAbstractEntryList().get(0));
        }

//////////////////////////////////
       */
        rcw.getRoute().getAbstractEntryList().clear();
        //rcw.updateRoute();
        //rcw.UpdateRouteInfo();

        orderedStopList.add((Stop) tmpRoute.getEntries()[0]);


        for(int i=0; i<order.size(); i++){
            orderedStopList.add(order.get(i)+1, stopList.get(i+1));
        }

        orderedStopList.add((Stop) tmpRoute.getEntries()[tmpRoute.getEntries().length-1]);

        for (Stop stop:orderedStopList) {
            rcw.onInsertStop(stop);
        }

    }
}
