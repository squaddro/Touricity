package com.squadro.touricity.progress;

import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.map.MapMaths;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProgressController {

    protected static class RouteProgress extends Progress {

        public static RouteProgress createNewProgress() {
            return new RouteProgress();
        }

        public void setProgressInfo(Time progressUpdateTime,LatLng progressUpdatedPosition, boolean isOnRoute) {
            this.progressUpdatePosition = progressUpdatedPosition;
            this.progressUpdateTime = progressUpdateTime;
            this.isOnRoute = isOnRoute;
        }

        public void setRouteTimeInfo(Time startTime, Time actualEndTime, Time expectedFinishTime) {
            this.startTime = startTime;
            this.actualEndTime = actualEndTime;
            this.expectedFinishTime = expectedFinishTime;
        }

        public void setRouteDistanceInfo(float totalPathDistance, float currentDistanceTraveled) {
            this.totalPathDistance = totalPathDistance;
            this.currentDistanceTraveled = currentDistanceTraveled;
        }

        public void setNextGoalInfo(boolean isOnPath, String nextGoalTitle, float distanceToNextGoal, float distanceAchievedInGoal, float timeToNextGoal) {
            this.isOnPath = isOnPath;
            this.nextGoalTitle = nextGoalTitle;
            this.distanceToNextGoal = distanceToNextGoal;
            this.distanceAchievedInGoal = distanceAchievedInGoal;
            this.timeToNextGoal = timeToNextGoal;
        }

        public void setNextPlaceInfo(String nextPlaceTitle, float distanceToNextPlace, float timeToNextPlace) {
            this.nextPlaceTitle = nextPlaceTitle;
            this.distanceToNextPlace = distanceToNextPlace;
            this.timeToNextPlace = timeToNextPlace;
        }

        public void setVisitedPlacesInfo(int placesExistsCount, int placesVisitedCount) {
            this.placesExistsCount = placesExistsCount;
            this.placesVisitedCount = placesVisitedCount;
        }
    }

    private Route route;
    private ArrayList<LatLng> prevPositions;
    private RouteProgress lastProgress;

    public ProgressController(Route route) {
        this.route = route;
        this.prevPositions = new ArrayList<>();
        this.lastProgress = new RouteProgress();
    }

    public ProgressController(Route route, LatLng[] prevPositions) {
        this(route);

        for(LatLng position : prevPositions) {
            UpdatePosition(position);
        }
    }

    public void UpdatePosition(LatLng location) {
        RouteProgress progress = RouteProgress.createNewProgress();

        Pair<MapMaths.ClosestPoint, IEntry> closestPoint = MapMaths.getClosestPointToRoute(location, route);

        MapMaths.ClosestPoint point = closestPoint.first;
        IEntry entry = closestPoint.second;

        prevPositions.add(location);
    }

    public LatLng[] getPrevPositions() {
        return (LatLng[]) prevPositions.toArray();
    }

    public Progress getProgress() {
        return lastProgress;
    }

}
