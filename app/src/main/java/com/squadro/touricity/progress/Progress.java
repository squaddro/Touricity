package com.squadro.touricity.progress;

import com.google.android.gms.maps.model.LatLng;
import com.squadro.touricity.message.types.interfaces.IEntry;

import java.util.Date;
import java.util.List;

import lombok.Getter;

public class Progress {

    @Getter
    protected Date progressUpdateTime;
    @Getter
    protected LatLng progressUpdatePosition;
    @Getter
    protected boolean isOnRoute;

    @Getter
    protected Date startTime;
    @Getter
    protected Date actualEndTime;
    @Getter
    protected Date expectedFinishTime;

    @Getter
    protected double totalRouteDistance;
    @Getter
    protected double currentDistanceTraveled;

    @Getter
    protected boolean isOnPath;
    @Getter
    protected String nextGoalTitle;
    @Getter
    protected double distanceToNextGoal;
    @Getter
    protected double distanceAchievedInGoal;
    @Getter
    protected double timeToNextGoal;

    @Getter
    protected String nextPlaceTitle;
    @Getter
    protected double distanceToNextPlace;
    @Getter
    protected double timeToNextPlace;

    @Getter
    protected int placesVisitedCount;
    @Getter
    protected int placesExistsCount;

    @Getter
    protected List<LatLng> prevPositions;

    @Getter
    protected IEntry currentEntry;
    @Getter
    protected int currentEntryIndex;

    protected Progress() {

    }

    public double getProgressTimePercentage (Date currentTime) {
        long expectedTotal = expectedFinishTime.getTime() - startTime.getTime();
        long achievedTotal = currentTime.getTime() - startTime.getTime();

        return (double) achievedTotal / (double) expectedTotal;
    }

    public double getProgressTimePercentage() {
        return getProgressTimePercentage(this.progressUpdateTime);
    }

    public double getProgressDistancePercentage() {
        return this.currentDistanceTraveled / this.totalRouteDistance;
    }
}
