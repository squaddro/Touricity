package com.squadro.touricity.progress;

import com.google.android.gms.maps.model.LatLng;
import com.squadro.touricity.message.types.Route;

import java.sql.Time;
import java.util.ArrayList;

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

    public ProgressController(Route route, Iterable<LatLng> prevPositions) {
        this(route);

        for(LatLng position : prevPositions) {
            UpdatePosition(position);
        }
    }

    public void UpdatePosition(LatLng location) {

    }

    public LatLng[] getPrevPositions() {
        return (LatLng[]) prevPositions.toArray();
    }

    public Progress getProgress() {
        return lastProgress;
    }
}
