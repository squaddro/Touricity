package com.squadro.touricity.progress;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;

import lombok.Getter;

public class Progress {

    @Getter
    protected Time progressUpdateTime;
    @Getter
    protected LatLng progressUpdatePosition;
    @Getter
    protected boolean isOnRoute;

    @Getter
    protected Time startTime;
    @Getter
    protected Time actualEndTime;
    @Getter
    protected Time expectedFinishTime;

    @Getter
    protected float totalPathDistance;
    @Getter
    protected float currentDistanceTraveled;

    @Getter
    protected boolean isOnPath;
    @Getter
    protected String nextGoalTitle;
    @Getter
    protected float distanceToNextGoal;
    @Getter
    protected float distanceAchievedInGoal;
    @Getter
    protected float timeToNextGoal;

    @Getter
    protected String nextPlaceTitle;
    @Getter
    protected float distanceToNextPlace;
    @Getter
    protected float timeToNextPlace;

    @Getter
    protected int placesVisitedCount;
    @Getter
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
