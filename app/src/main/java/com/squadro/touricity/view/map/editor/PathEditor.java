package com.squadro.touricity.view.map.editor;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.maths.MapMaths;
import com.squadro.touricity.view.map.event.IDataUpdateListener;

import java.util.ArrayList;
import java.util.List;

public class PathEditor implements IEditor<Path>, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnMapLongClickListener {

    private IDataUpdateListener<Path> dataUpdateListener;
    private Polyline editPolyline;
    private ArrayList<Marker> editMarkers;

    private Path path;
    private GoogleMap map;

    private Marker markerToBeDeleted;

    private void drawPolyline() {
        clearPolyline();

        List<PathVertex> vertices = path.getVertices();

        PolylineOptions polylineOptions = getLineOptions(vertices);

        switch (path.getPath_type()) {
            case BUS:
                polylineOptions.color(Color.BLUE);
                break;
            case WALKING:
                polylineOptions.color(Color.YELLOW);
                break;
            case DRIVING:
                polylineOptions.color(Color.RED);
                break;
        }

        editPolyline = map.addPolyline(polylineOptions);

    }

    private void drawMarkers() {
        clearMarkers();

        List<MarkerOptions> markerOptionsList = getCirclesOptions(path.getVertices());
        editMarkers = new ArrayList<>();
        for(MarkerOptions markerOptions : markerOptionsList)
            editMarkers.add(map.addMarker(markerOptions));
    }

    private void clearPolyline() {
        if(editPolyline!= null) {
            editPolyline.remove();
        }
    }

    private void clearMarkers() {
        if(editMarkers != null) {
            for(Marker marker : editMarkers) {
                marker.remove();
            }
        }
    }

    private void moveCameraToPath() {
        LatLngBounds bounds = getVisibleBoundary(path.getVertices());
        bounds = MapMaths.boundsPadding(bounds, 5, 10, 65, 10);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
    }

    private PolylineOptions getLineOptions(List<PathVertex> pathVertices) {
        PolylineOptions options = new PolylineOptions();

        for(PathVertex vertex : pathVertices) {
            options.add(vertex.toLatLong()).clickable(true);
        }

        return options;
    }

    private List<MarkerOptions> getCirclesOptions(List<PathVertex> pathVertices) {
        List<MarkerOptions> options = new ArrayList<>();

        for(PathVertex vertex : pathVertices) {
            options.add(getSimpleMarker().position(vertex.toLatLong()));
        }

        return options;
    }

    private LatLngBounds getVisibleBoundary(List<PathVertex> pathVertices) {
        LatLngBounds.Builder builder = LatLngBounds.builder();

        for(PathVertex vertex : pathVertices) {
            builder.include(vertex.toLatLong());
        }

        return builder.build();
    }

    private void deleteMarker(Marker marker) {
        int index = editMarkers.indexOf(marker);
        if(index >= 0) {
            PathVertex vertex = path.getVertices().get(index);
            path.getVertices().remove(vertex);

            restoreMarkerDelete();

            drawPolyline();
            drawMarkers();
        }
    }

    private void restoreMarkerDelete() {
        if(markerToBeDeleted != null) {
            int index = editMarkers.indexOf(markerToBeDeleted);
            if(index >= 0) {
                Marker oldMarker = markerToBeDeleted;
                Marker newMarker = map.addMarker(getSimpleMarker().position(oldMarker.getPosition()));
                editMarkers.set(index, newMarker);
                oldMarker.remove();
            }
        }

        markerToBeDeleted = null;
    }

    private MarkerOptions getSimpleMarker() {
        return new MarkerOptions().draggable(true).icon(BitmapDescriptorFactory.defaultMarker());
    }

    private MarkerOptions getDisabledMarker() {
        return getSimpleMarker().alpha(0.5f);
    }

    private void initializeListeners() {
        map.setOnMarkerDragListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMapLongClickListener(this);
    }

    private void restoreListeners() {
        dataUpdateListener = null;

        map.setOnMarkerDragListener(null);
        map.setOnMarkerClickListener(null);
        map.setOnMapLongClickListener(null);
    }

    @Override
    public void prepare(GoogleMap map, Path path) {
        this.map = map;
        this.path = path;

        drawPolyline();
        drawMarkers();

        moveCameraToPath();
        initializeListeners();
    }

    @Override
    public void dispose() {
        clearPolyline();
        clearMarkers();
        restoreListeners();
    }

    @Override
    public void setDataUpdateListener(IDataUpdateListener<Path> dataUpdateListener) {
        this.dataUpdateListener = dataUpdateListener;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        int index = editMarkers.indexOf(marker);
        if(index >= 0) {
            PathVertex vertex = path.getVertices().get(index);
            vertex.setLatitude(marker.getPosition().latitude);
            vertex.setLongitude(marker.getPosition().longitude);

            drawPolyline();
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        onMarkerDrag(marker);

        drawPolyline();
        drawMarkers();

        dataUpdateListener.onDataUpdate(path);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(markerToBeDeleted)) {
            deleteMarker(marker);
        }
        else {

            int index = editMarkers.indexOf(marker);
            if(index >= 0) {
                restoreMarkerDelete();

                markerToBeDeleted = map.addMarker(getDisabledMarker().position(marker.getPosition()));

                editMarkers.set(index, markerToBeDeleted);
                marker.remove();
            }
        }

        return true;
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        restoreMarkerDelete();
        moveCameraToPath();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        restoreMarkerDelete();

        List<LatLng> points = editPolyline.getPoints();

        // check if length is 0 or 1
        if(points.size() == 0 || points.size() == 1) {
            path.getVertices().add(new PathVertex(latLng.latitude, latLng.longitude));
        }
        else {
            MapMaths.ClosestPoint closestPoint = MapMaths.basedOn(map).getClosestPoint(latLng, editPolyline.getPoints());

            double distanceToFirst = MapMaths.distance(closestPoint.closestPoint, points.get(0));
            double distanceToLast = MapMaths.distance(closestPoint.closestPoint, points.get(points.size() - 1));

            // check if closest is first
            if( distanceToFirst == 0.0d)
                path.getVertices().add(0, new PathVertex(latLng.latitude, latLng.longitude));
                // check if closest is last
            else if(distanceToLast == 0.0d)
                path.getVertices().add(new PathVertex(latLng.latitude, latLng.longitude));
                // else insert to between [lower , lower+1]
            else
                path.getVertices().add(closestPoint.lowerIndex + 1, new PathVertex(latLng.latitude, latLng.longitude));
        }

        drawMarkers();
        drawPolyline();

        dataUpdateListener.onDataUpdate(path);
    }
}
