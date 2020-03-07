package com.squadro.touricity.view.map;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private PolylineOptions polylineOptions;

    private Stop editingStop = null;
    private Path editingPath = null;

    public PolylineDrawer(GoogleMap map) {
        this.map = map;
        markerOptions = new MarkerOptions();
        polylineOptions  = new PolylineOptions();
    }

    public GoogleMap drawRoute(Route route) {

        map.clear();

        List<IEntry> entryList = route.getAbstractEntryList();
        Iterator iterator = entryList.iterator();

        while (iterator.hasNext()) {
            IEntry entry = (IEntry) iterator.next();

            if (entry instanceof Stop) {
                LocationRequests locationRequest = new LocationRequests();
                locationRequest.getLocationInfo("5f8a2f28-c78b-47f6-ba7e-62d389062df6", this);
            } else if (entry instanceof Path) {
                List<PathVertex> vertices = ((Path) entry).getVertices();
                for (int i = 0; i < vertices.size(); i++) {
                    polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));
                }
            }
        }
        map.addPolyline(polylineOptions);

        return map;
    }

    public GoogleMap drawRoute(Route route, Stop stop) {

        map.clear();

        this.editingStop = stop;

        List<IEntry> entryList = route.getAbstractEntryList();
        Iterator iterator = entryList.iterator();

        while (iterator.hasNext()) {
            IEntry entry = (IEntry) iterator.next();

            if (entry instanceof Stop) {
                LocationRequests locationRequest = new LocationRequests();
                locationRequest.getLocationInfo("5f8a2f28-c78b-47f6-ba7e-62d389062df6", this);
            } else if (entry instanceof Path) {
                List<PathVertex> vertices = ((Path) entry).getVertices();
                for (int i = 0; i < vertices.size(); i++) {
                    polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));
                }
            }
        }
        map.addPolyline(polylineOptions);

        return map;
    }

    public GoogleMap drawRoute(Route route, Path path) {

        map.clear();

        this.editingPath = path;

        List<IEntry> entryList = route.getAbstractEntryList();
        Iterator iterator = entryList.iterator();

        while (iterator.hasNext()) {
            IEntry entry = (IEntry) iterator.next();

            if (entry instanceof Stop) {
                LocationRequests locationRequest = new LocationRequests();
                locationRequest.getLocationInfo("5f8a2f28-c78b-47f6-ba7e-62d389062df6", this);
            } else if (entry instanceof Path) {
                if(entry.equals(editingPath)) {
                    List<PathVertex> vertices = ((Path) entry).getVertices();
                    for (int i = 0; i < vertices.size(); i++) {
                        polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));
                        polylineOptions.color(Color.BLUE);
                    }
                }

                else{
                    List<PathVertex> vertices = ((Path) entry).getVertices();
                    for (int i = 0; i < vertices.size(); i++) {
                        polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));
                    }
                }


            }
        }
        map.addPolyline(polylineOptions);
        polylineOptions.color(Color.BLACK);

        return map;
    }

    @Override
    public void onResponseLocationInfo(Location location) {
        markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));

        if(editingStop != null && editingStop.getLocation_id().equals(location.getLocation_id()))
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        map.addMarker(markerOptions);
    }
}