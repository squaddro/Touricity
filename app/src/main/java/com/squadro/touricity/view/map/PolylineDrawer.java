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

public class PolylineDrawer{

    private GoogleMap map;
    private MarkerOptions markerOptions;
    private PolylineOptions polylineOptions;

    //private Stop editingStop = null;
    //private Path editingPath = null;

    private IEntry entry;

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
            entry = (IEntry) iterator.next();

            if (entry instanceof Stop) {
                markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(((Stop) entry).getLocation().getLatitude(), ((Stop) entry).getLocation().getLongitude()));
                map.addMarker(markerOptions);
            } else if (entry instanceof Path) {
                polylineOptions = new PolylineOptions();
                List<PathVertex> vertices = ((Path) entry).getVertices();
                for (int i = 0; i < vertices.size(); i++) {
                    polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));
                    map.addPolyline(polylineOptions);
                }
            }
        }

        return map;
    }

    public GoogleMap drawRoute(Route route, Stop stop) {

        map.clear();

        //this.editingStop = stop;

        List<IEntry> entryList = route.getAbstractEntryList();
        Iterator iterator = entryList.iterator();

        while (iterator.hasNext()) {
            entry = (IEntry) iterator.next();

            if (entry instanceof Stop) {

                if(((Stop) entry).getLocation().getLatitude() == stop.getLocation().getLatitude() &&
                        ((Stop) entry).getLocation().getLongitude() == stop.getLocation().getLongitude()){

                    markerOptions = new MarkerOptions();
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    markerOptions.position(new LatLng(((Stop) entry).getLocation().getLatitude(), ((Stop) entry).getLocation().getLongitude()));
                    map.addMarker(markerOptions);
                }

                else{
                    markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(((Stop) entry).getLocation().getLatitude(), ((Stop) entry).getLocation().getLongitude()));
                    map.addMarker(markerOptions);
                }

            } else if (entry instanceof Path) {
                polylineOptions = new PolylineOptions();
                List<PathVertex> vertices = ((Path) entry).getVertices();
                for (int i = 0; i < vertices.size(); i++) {
                    polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));
                    map.addPolyline(polylineOptions);
                }
            }
        }

        return map;
    }

    public GoogleMap drawRoute(Route route, Path path) {

        map.clear();

        List<IEntry> entryList = route.getAbstractEntryList();
        Iterator iterator = entryList.iterator();

        while (iterator.hasNext()) {
            entry = (IEntry) iterator.next();

            if (entry instanceof Stop) {
                markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(((Stop) entry).getLocation().getLatitude(), ((Stop) entry).getLocation().getLongitude()));
                map.addMarker(markerOptions);
            } else if (entry instanceof Path) {

                polylineOptions = new PolylineOptions();
                if(entry.equals(path)) {
                    List<PathVertex> vertices = ((Path) entry).getVertices();
                    for (int i = 0; i < vertices.size(); i++) {
                        polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));
                        polylineOptions.color(Color.BLUE);
                        map.addPolyline(polylineOptions);
                        polylineOptions.color(Color.BLACK);
                    }
                }

                else{
                    List<PathVertex> vertices = ((Path) entry).getVertices();
                    for (int i = 0; i < vertices.size(); i++) {
                        polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));
                        map.addPolyline(polylineOptions);
                    }
                }
            }
        }
        return map;
    }


   /*
    @Override
    public void onResponseLocationInfo(Location location) {

        markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));

        if(editingStop != null && entry.equals(editingStop))
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        else
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));


        map.addMarker(markerOptions);
        markerOptions = null;
    }

    */
}