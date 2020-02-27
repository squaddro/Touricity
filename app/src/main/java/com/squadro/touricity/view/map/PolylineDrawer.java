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
import com.squadro.touricity.requests.ILocationRequest;
import com.squadro.touricity.requests.LocationRequests;

import java.util.Iterator;
import java.util.List;

public class PolylineDrawer implements ILocationRequest {

    private GoogleMap map;
    private MarkerOptions markerOptions;

    public PolylineDrawer(GoogleMap map) {
        this.map = map;
    }

    public GoogleMap drawRoute(Route route) {

        PolylineOptions polylineOptions = new PolylineOptions();
        markerOptions = new MarkerOptions();

        List<IEntry> entryList = route.getAbstractEntryList();
        Iterator iterator = entryList.iterator();

        while (iterator.hasNext()) {
            IEntry entry = (IEntry) iterator.next();

            if (entry instanceof Stop) {
                LocationRequests locationRequest = new LocationRequests();
                locationRequest.getLocationInfo(((Stop) entry).getLocation_id(), this);
            } else if (entry instanceof Path) {
                List<PathVertex> vertices = ((Path) entry).getVertices();
                for (int i = 0; i < vertices.size(); i++) {
                    polylineOptions.add(new LatLng(vertices.get(i).getLat(), vertices.get(i).getLon()));
                }
            }
        }

        map.addMarker(markerOptions);
        map.addPolyline(polylineOptions);
        return map;
    }

    @Override
    public void onResponseLocationInfo(Location location) {
        markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
    }
}