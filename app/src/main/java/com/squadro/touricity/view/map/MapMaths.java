package com.squadro.touricity.view.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.interfaces.IEntry;

public class MapMaths {

    public class ClosestPoint {
        public boolean isPolyline = false;
        public double distance = Double.MAX_VALUE;
        public LatLng closestPoint = null;
        public int lowerIndex = -1;

        @Override
        public String toString() {
            return "isPolyline " + isPolyline + " | "
                    + "distance " + distance + " | "
                    + "closestPoint " + closestPoint + " | "
                    + "lowerIndex " + lowerIndex;

        }
    }

    private GoogleMap map;

    private MapMaths(GoogleMap map) {
        this.map = map;
    }

    private LatLng closestPointBetween2D(LatLng p, LatLng a, LatLng b) {
        LatLng v = new LatLng(b.latitude - a.latitude, b.longitude - a.longitude);
        LatLng u = new LatLng(a.latitude - p.latitude, a.longitude - p.longitude);
        double vu = v.latitude * u.latitude + v.longitude * u.longitude;
        double vv = v.latitude * v.latitude + v.longitude * v.longitude;

        double t = -vu / vv;

        if (t >= 0 && t <= 1) {
            return vectorToSegment2D(t, new LatLng(0, 0), a, b);
        }


        double g0 = squaredMagnitude2D(vectorToSegment2D(0, p, a, b));
        double g1 = squaredMagnitude2D(vectorToSegment2D(1, p, a, b));

        return g0 <= g1 ? a : b;
    }

    private LatLng vectorToSegment2D(double t, LatLng p, LatLng a, LatLng b) {
        return new LatLng(
                (1 - t) * a.latitude + t * b.latitude - p.latitude,
                (1 - t) * a.longitude + t * b.longitude - p.longitude
        );
    }

    private double squaredMagnitude2D(LatLng p) {
        return p.latitude * p.latitude + p.longitude * p.longitude;
    }

    public ClosestPoint getClosestPoint(LatLng point, Polyline polyline) {
        ClosestPoint closest = new ClosestPoint();
        closest.isPolyline = true;

        int counter = 0;
        LatLng prevPoint = null;

        for (LatLng latLng : polyline.getPoints()) {
            if (prevPoint != null) {
                LatLng projected = closestPointBetween2D(point, latLng, prevPoint);
                double distance = distance(projected, point);

                if (distance < closest.distance) {
                    closest.distance = distance;
                    closest.closestPoint = projected;
                    closest.lowerIndex = counter - 1;
                }
            }

            prevPoint = latLng;

            counter++;
        }

        return closest;
    }

    public static MapMaths basedOn(GoogleMap map) {
        return new MapMaths(map);
    }

    public static LatLngBounds boundsPadding(LatLngBounds bounds, float percent) {
        return boundsPadding(bounds, percent, percent, percent, percent);
    }

    public static LatLngBounds boundsPadding(LatLngBounds bounds, float northPercent, float eastPercent, float southPercent, float westPercent) {
        double north = bounds.northeast.latitude;
        double east = bounds.northeast.longitude;
        double south = bounds.southwest.latitude;
        double west = bounds.southwest.longitude;

        double height = north - south;
        double width = east - west;

        double targetHeight = height / ((100f - northPercent - southPercent) / 100f);
        double targetWidth = width / ((100f - eastPercent - westPercent) / 100f);

        north += targetHeight * (northPercent / 100f);
        east += targetWidth * (eastPercent / 100f);
        south -= targetHeight * (southPercent / 100f);
        west -= targetWidth * (westPercent / 100f);

        LatLng northeast = new LatLng(north, east);
        LatLng southwest = new LatLng(south, west);

        return LatLngBounds.builder().include(northeast).include(southwest).build();
    }

    public static double distance(LatLng a, LatLng b) {
        LatLng dir = new LatLng(a.latitude - b.latitude, a.longitude - b.longitude);
        return Math.sqrt(dir.latitude * dir.latitude + dir.longitude * dir.longitude);
    }

    public static LatLngBounds getRouteBoundings(Route route) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (IEntry entry : route.getEntries()) {
            if (entry instanceof Path) {
                Path path = (Path) entry;
                for (PathVertex vertex : path.getVertices()) {
                    builder.include(vertex.toLatLong());
                }
            }
        }
        return MapMaths.boundsPadding(builder.build(), 20);
    }
}
