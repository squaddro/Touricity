package com.squadro.touricity.progress;

import com.google.android.gms.maps.model.LatLng;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.maths.MapMaths;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

	protected static class TrackedPoint {
		public MapMaths.ClosestPoint closestPoint;

		public IEntry entry;
	}

	protected enum ProgressState {
		ON_PATH,
		ON_STOP
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

		TrackedPoint trackedPoint = getClosestPoint(location, route, 0.0001);

		MapMaths.ClosestPoint point = trackedPoint.closestPoint;
		IEntry entry = trackedPoint.entry;

		prevPositions.add(location);
	}

	public LatLng[] getPrevPositions() {
		return (LatLng[]) prevPositions.toArray();
	}

	public Progress getProgress() {
		return lastProgress;
	}

	protected static TrackedPoint getClosestPoint(LatLng position, Route route, double distanceThreshold) {
		IEntry closestEntry = null;
		MapMaths.ClosestPoint closestPoint = null;

		for(IEntry entry: route.getEntries()) {
			// if entry is a stop
			if(entry instanceof Stop) {
				Stop stop = (Stop) entry;
				LatLng place = new LatLng(stop.getLocation().getLatitude(), stop.getLocation().getLongitude());
				double distance = MapMaths.distance(place, position);

				// if distance is greater than the threshold do not use
				if(distance>distanceThreshold)
					continue;

				// if the point was the first closest point
				// or if the place is closer than the other
				if(closestPoint == null || distance < closestPoint.distance || closestEntry instanceof Path){
					closestEntry = stop;
					closestPoint = new MapMaths.ClosestPoint();
					closestPoint.closestPoint = place;
					closestPoint.distance = distance;
					closestPoint.isPolyline = false;
					closestPoint.lowerIndex = 0;
					continue;
				}
			}

			// else if entry is a path
			else if(entry instanceof Path) {
				Path path = (Path) entry;
				List<LatLng> vertices = new LinkedList<>();

				for (PathVertex vertex: path.getVertices())
					vertices.add(vertex.toLatLong());

				MapMaths.ClosestPoint closestToPath = MapMaths.getClosestPoint(position, vertices);
				if(closestToPath.distance > distanceThreshold)
					continue;

				// if the point was the first closest point
				// or if the path is closer than the other path
				// stops always has a priority
				if(closestPoint == null || (closestEntry instanceof Path && closestPoint.distance < closestToPath.distance)) {
					closestEntry = path;
					closestPoint = closestToPath;
					continue;
				}
			}

		}

		if(closestPoint != null) {
			TrackedPoint result = new TrackedPoint();
			result.closestPoint = closestPoint;
			result.entry = closestEntry;

			return result;
		}
		else
			return  null;
	}
}
