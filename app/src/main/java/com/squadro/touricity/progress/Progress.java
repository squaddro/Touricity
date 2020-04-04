package com.squadro.touricity.progress;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;

public class Progress {

    protected Time progressUpdateTime;
    protected LatLng progressUpdatePosition;
    protected boolean isOnRoute;

    protected Time startTime;
    protected Time actualEndTime;
    protected Time expectedFinishTime;

    protected float totalPathDistance;
    protected float currentDistanceTraveled;

    protected boolean isOnPath;
    protected String nextGoalTitle;
    protected float distanceToNextGoal;
    protected float distanceAchievedInGoal;
    protected float timeToNextGoal;

    protected String nextPlaceTitle;
    protected float distanceToNextPlace;
    protected float timeToNextPlace;

    protected int placesVisitedCount;
    protected int placesExistsCount;

    protected Progress() {

    }

    public float getProgressTimePercentage (Time currentTime) {
        long expectedTotal = expectedFinishTime.getTime() - startTime.getTime();
        long achievedTotal = currentTime.getTime() - startTime.getTime();

        return (float) achievedTotal / (float) expectedTotal;
    }

    public float getProgressTimePercentage() {
        return getProgressTimePercentage(this.progressUpdateTime);
    }

    public float getProgressDistancePercentage() {
        return this.currentDistanceTraveled / this.totalPathDistance;
    }
}
