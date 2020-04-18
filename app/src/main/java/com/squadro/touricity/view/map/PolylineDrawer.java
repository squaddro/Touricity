package com.squadro.touricity.view.map;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.squadro.touricity.MainActivity;
import com.squadro.touricity.R;
import com.squadro.touricity.maths.MapMaths;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.map.offline.CustomMapTileProvider;
import com.squadro.touricity.view.map.placesAPI.MarkerInfo;
import com.squadro.touricity.view.map.placesAPI.MyPlace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PolylineDrawer {

    private String viewId;
    private GoogleMap map;
    private static List<Pair<Polyline, Path>> polylines = new ArrayList<>();
    private static List<Marker> markers = new ArrayList<>();
    private List<MyPlace> responsePlaces;

    private IEntry entry;

    public PolylineDrawer(GoogleMap map,String viewId) {
        this.map = map;
        this.viewId = viewId;
        revalidateResponsePlaces();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GoogleMap drawRoute(Route route) {
        revalidateResponsePlaces();
        map.clear();
        polylines.clear();
        markers.clear();
        if (!MainActivity.checkConnection()) {
            MapFragmentTab3.getMap().setMapType(GoogleMap.MAP_TYPE_NONE);
            TileOverlayOptions tileOverlay = new TileOverlayOptions();
            tileOverlay.tileProvider(new CustomMapTileProvider());
            tileOverlay.zIndex(0);
            map.addTileOverlay(tileOverlay);
        }

        List<IEntry> entryList = route.getAbstractEntryList();
        Iterator iterator = entryList.iterator();

        int counter = 0;
        int last = entryList.size()/2;

        while (iterator.hasNext()) {
            entry = (IEntry) iterator.next();

            if (entry instanceof Stop) {

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(((Stop) entry).getLocation().getLatitude(), ((Stop) entry).getLocation().getLongitude()));
                if(counter == 0){
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.first_stop));
                }
                if(last != 0 && counter == last){
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.last_stop));
                }
                counter++;

                Marker marker = map.addMarker(markerOptions);
                marker.setZIndex(1);
                markers.add(marker);

                List<MyPlace> collect = responsePlaces.stream()
                        .filter(myPlace -> myPlace.getPlace_id().equals(((Stop) entry).getLocation().getLocation_id()))
                        .collect(Collectors.toList());
                if(collect.size() > 0){
                    MapFragmentTab2.updateMarkerInfo(new MarkerInfo(marker,collect.get(0),false));
                }else{
                    MapFragmentTab2.customStopList.add((Stop)entry);
                }

            } else if (entry instanceof Path && ((Path)entry).getVertices() != null) {
                PolylineOptions polylineOptions = new PolylineOptions();
                List<PathVertex> vertices = ((Path) entry).getVertices();
                for (int i = 0; i < vertices.size(); i++) {
                    polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));

                }
                polylineOptions.width(15);
                polylineOptions.color(0xff7f00ff);
                Polyline polyline = map.addPolyline(polylineOptions);
                polyline.setZIndex(1);
                polyline.setClickable(true);
                polylines.add(new Pair<>(polyline, (Path) entry));
            }
        }
        if(route.getEntries() != null && route.getEntries().length != 0){
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(MapMaths.getRouteBoundings(route), 0));
        }
        return map;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public GoogleMap drawRoute(Route route, Stop stop) {
        revalidateResponsePlaces();
        if(viewId.equals("saved")) clearMap();
        else  {
            map.clear();
            polylines.clear();
        }
        List<IEntry> entryList = route.getAbstractEntryList();
        Iterator iterator = entryList.iterator();

        while (iterator.hasNext()) {
            entry = (IEntry) iterator.next();

            if (entry instanceof Stop) {
                if (((Stop) entry).getLocation().getLatitude() == stop.getLocation().getLatitude() &&
                        ((Stop) entry).getLocation().getLongitude() == stop.getLocation().getLongitude()) {

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    markerOptions.position(new LatLng(((Stop) entry).getLocation().getLatitude(), ((Stop) entry).getLocation().getLongitude()));
                    Marker marker = map.addMarker(markerOptions);
                    marker.setZIndex(1);
                    markers.add(marker);


                    List<MyPlace> collect = responsePlaces.stream()
                            .filter(myPlace -> myPlace.getPlace_id().equals(((Stop) entry).getLocation().getLocation_id()))
                            .collect(Collectors.toList());
                    if(collect.size() > 0){
                        MapFragmentTab2.updateMarkerInfo(new MarkerInfo(marker,collect.get(0),false));
                    }
                    else{
                        MapFragmentTab2.customStopList.add((Stop)entry);
                    }
                } else {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(((Stop) entry).getLocation().getLatitude(), ((Stop) entry).getLocation().getLongitude()));
                    Marker marker = map.addMarker(markerOptions);
                    marker.setZIndex(1);
                    markers.add(marker);
                    List<MyPlace> collect = responsePlaces.stream()
                            .filter(myPlace -> myPlace.getPlace_id().equals(((Stop) entry).getLocation().getLocation_id()))
                            .collect(Collectors.toList());
                    if(collect.size() > 0){
                        MapFragmentTab2.updateMarkerInfo(new MarkerInfo(marker,collect.get(0),false));
                    }else{
                        MapFragmentTab2.customStopList.add((Stop)entry);
                    }
                }

            } else if (entry instanceof Path && ((Path)entry).getVertices() != null) {
                PolylineOptions polylineOptions = new PolylineOptions();
                List<PathVertex> vertices = ((Path) entry).getVertices();
                for (int i = 0; i < vertices.size(); i++) {
                    polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));
                }
                Polyline polyline = map.addPolyline(polylineOptions);
                polyline.setZIndex(1);
                polyline.setClickable(true);
                polylines.add(new Pair<>(polyline, (Path) entry));
            }
        }
        return map;
    }

    private void revalidateResponsePlaces() {
        if (MainActivity.checkConnection()) {
            responsePlaces = MapFragmentTab2.responsePlaces;
        } else {
            responsePlaces = MapFragmentTab3.responsePlaces;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GoogleMap drawRoute(Route route, Path path) {

        if(viewId.equals("saved")) clearMap();
        else map.clear();

        List<IEntry> entryList = route.getAbstractEntryList();
        Iterator iterator = entryList.iterator();

        while (iterator.hasNext()) {
            entry = (IEntry) iterator.next();


             if (entry instanceof Path && ((Path)entry).getVertices() != null) {

                PolylineOptions polylineOptions = new PolylineOptions();
                if (entry.equals(path)) {
                    List<PathVertex> vertices = ((Path) entry).getVertices();
                    for (int i = 0; i < vertices.size(); i++) {
                        polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));
                        polylineOptions.color(Color.BLUE);
                        Polyline polyline = map.addPolyline(polylineOptions);
                        polyline.setZIndex(1);
                        polylines.add(new Pair<>(polyline, (Path) entry));
                        polylineOptions.color(Color.BLACK);
                    }
                } else {
                    List<PathVertex> vertices = ((Path) entry).getVertices();
                    for (int i = 0; i < vertices.size(); i++) {
                        polylineOptions.add(new LatLng(vertices.get(i).getLatitude(), vertices.get(i).getLongitude()));
                    }

                    Polyline polyline = map.addPolyline(polylineOptions);
                    polyline.setZIndex(1);
                    polyline.setClickable(true);
                    polylines.add(new Pair<>(polyline, (Path) entry));
                }
            }
        }
        return map;
    }

    private void clearMap() {
        if (polylines.size() > 0) {
            for (Pair<Polyline, Path> polyline : polylines) {
                polyline.first.remove();
            }
            polylines.clear();
        }

        if (markers.size() > 0) {
            for (Marker marker : markers) {
                marker.remove();
            }
            markers.clear();
        }
    }

    public Path findPath(Polyline poly) {
        for (Pair<Polyline, Path> pair :polylines) {
            LatLng l1 = pair.first.getPoints().get(0);
            LatLng l2 = poly.getPoints().get(0);
            if(l1.longitude == l2.longitude && l1.latitude == l2.latitude)
                return pair.second;
        }
        return null;
    }
}