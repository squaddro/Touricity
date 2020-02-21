package com.squadro.touricity.view.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.requests.LocationRequests;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PolylineDrawer {

    private GoogleMap map;

    public PolylineDrawer(GoogleMap map){
        this.map = map;
    }

    public GoogleMap drawRoute(Route route){

        PolylineOptions polylineOptions = new PolylineOptions();
        MarkerOptions markerOptions = new MarkerOptions();

        List<IEntry> entryList = route.getAbstractEntryList();
        Iterator iterator = entryList.iterator();

        while(iterator.hasNext()){
            IEntry entry = (IEntry) iterator.next();

            if(entry instanceof Stop){

                AtomicReference<Location> location = new AtomicReference<>();
                new Thread(() -> {
                    LocationRequests locationRequest = new LocationRequests();
                    location.set(locationRequest.getLocationInfo(((Stop) entry).getLocation_id()));
                });
                markerOptions.position(new LatLng(location.get().getLatitude(), location.get().getLongitude()));
            }

            else if(entry instanceof Path){
                List<PathVertex> vertices = ((Path) entry).getVertices();
                for(int i = 0; i < vertices.size(); i++){
                    polylineOptions.add(new LatLng(vertices.get(i).getLat(), vertices.get(i).getLon()));
                }
            }
        }

        map.addMarker(markerOptions);
        map.addPolyline(polylineOptions);
        return map;
    }
}