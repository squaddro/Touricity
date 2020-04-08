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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ProgressController implements IPositionUpdateListener {

	protected static class RouteProgress extends Progress {

		public static RouteProgress createNewProgress() {
			return new RouteProgress();
		}

		public void setProgressInfo(Date progressUpdateTime, LatLng progressUpdatedPosition, boolean isOnRoute) {
			this.progressUpdatePosition = progressUpdatedPosition;
			this.progressUpdateTime = progressUpdateTime;
			this.isOnRoute = isOnRoute;
		}

		public void setRouteTimeInfo(Date startTime, Date actualEndTime, Date expectedFinishTime) {
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

		public void setPrevPositions(List<LatLng> positions) {
			this.prevPositions = positions;
		}
	}

	protected static class TrackedPoint {
		public MapMaths.ClosestPoint closestPoint;

		public IEntry entry;
	}

	protected enum ProgressState {
		ON_PATH,
		ON_STOP,
		OUTSIDE
	}

	private Route route;
	private ArrayList<LatLng> prevPositions;
	private RouteProgress lastProgress;
	private IEntry lastVisitEntry;
	private int lastVisitIndex;
	private ProgressState trackState;

	private List<IProgressEventListener> progressEventListeners;

	public ProgressController(Route route) {
		this.route = route;
		this.prevPositions = new ArrayList<>();
		this.lastProgress = new RouteProgress();
		this.progressEventListeners = new LinkedList<>();
	}

	public ProgressController(Route route, LatLng[] prevPositions) {
		this(route);

		if(prevPositions != null) {
			for(LatLng position : prevPositions) {
				OnPositionUpdated(position);
			}
		}
	}

	public Progress getProgress() {
		return lastProgress;
	}

	protected static TrackedPoint getNextPoint(LatLng position, Route route, double distanceThreshold, IEntry startFrom, int startIndex) {
		IEntry closestEntry = null;
		MapMaths.ClosestPoint closestPoint = null;
		boolean startEncounter = false;
		boolean extraPass = false;

		for(IEntry entry: route.getEntries()) {

			if(!startEncounter && entry == startFrom){
				startEncounter = true;
			}

			boolean found = false;
			if(startEncounter && (extraPass || closestEntry == null))  {

				// if entry is a stop
				if(entry instanceof Stop) {
					Stop stop = (Stop) entry;
					LatLng place = new LatLng(stop.getLocation().getLatitude(), stop.getLocation().getLongitude());
					double distance = MapMaths.distance(place, position);

					// if distance is greater than the threshold do not use
					if(distance<distanceThreshold){
						// if the point was the first closest point
						// or if the place is closer than the other
						if(closestPoint == null || distance < closestPoint.distance || closestEntry instanceof Path){
							closestEntry = stop;
							closestPoint = new MapMaths.ClosestPoint();
							closestPoint.closestPoint = place;
							closestPoint.distance = distance;
							closestPoint.isPolyline = false;
							closestPoint.lowerIndex = 0;
							found = true;
						}
					}
				}
				// else if entry is a path
				else if(entry instanceof Path) {
					Path path = (Path) entry;
					List<LatLng> vertices = new LinkedList<>();
					int ignoreStart = entry == startFrom ? startIndex : 0;
					for (PathVertex vertex: path.getVertices()){
						if(ignoreStart-- <= 0)
							vertices.add(vertex.toLatLong());
					}

					MapMaths.ClosestPoint closestToPath = MapMaths.getClosestPoint(position, vertices);
					if(closestToPath.distance < distanceThreshold){
						// if the point was the first closest point
						// or if the path is closer than the other path
						// stops always has a priority
						if(closestPoint == null || (closestEntry instanceof Path && closestPoint.distance < closestToPath.distance)) {
							closestEntry = path;
							closestPoint = closestToPath;
							found = true;
						}
					}
				}
			}

			if(found && !extraPass)
				extraPass = true;
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

	public void addProgressEventListener(IProgressEventListener eventListener) {
		progressEventListeners.add(eventListener);
	}

	@Override
	public void OnPositionUpdated(LatLng position) {
		RouteProgress progress = RouteProgress.createNewProgress();

		TrackedPoint trackedPoint = getNextPoint(position, route, 0.0001, lastVisitEntry, lastVisitIndex);

		if(trackedPoint == null) {

		}
		else{
			MapMaths.ClosestPoint point = trackedPoint.closestPoint;
			IEntry entry = trackedPoint.entry;
		}

		progress.setProgressInfo(Calendar.getInstance().getTime(), position, true);

		prevPositions.add(position);
		progress.setPrevPositions(prevPositions);

		lastProgress = progress;

		for (IProgressEventListener eventListener: progressEventListeners) {
			if(eventListener != null) {
				eventListener.ProgressUpdated(progress);
			}
		}
	}
}
