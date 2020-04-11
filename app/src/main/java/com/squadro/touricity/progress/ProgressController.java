package com.squadro.touricity.progress;

import com.google.android.gms.maps.model.LatLng;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.maths.MapMaths;
import com.squadro.touricity.view.map.MapFragmentTab3;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ProgressController implements IPositionUpdateListener {


	private static class RouteProgress extends Progress {

		static RouteProgress createNewProgress() {
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

		public void setRouteDistanceInfo(double totalRouteDistance, double currentDistanceTraveled) {
			this.totalRouteDistance = totalRouteDistance;
			this.currentDistanceTraveled = currentDistanceTraveled;
		}

		public void setNextGoalInfo(boolean isOnPath, String nextGoalTitle, double distanceToNextGoal, double distanceAchievedInGoal, double timeToNextGoal) {
			this.isOnPath = isOnPath;
			this.nextGoalTitle = nextGoalTitle;
			this.distanceToNextGoal = distanceToNextGoal;
			this.distanceAchievedInGoal = distanceAchievedInGoal;
			this.timeToNextGoal = timeToNextGoal;
		}

		public void setNextPlaceInfo(String nextPlaceTitle, double distanceToNextPlace, double timeToNextPlace) {
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

		public void setCurrentEntryInfo(IEntry entry, int index) {
			this.currentEntry = entry;
			this.currentEntryIndex = index;
		}
	}

	protected static class TrackedPoint {
		public MapMaths.ClosestPoint closestPoint;

		public IEntry entry;
	}

	protected class ComputedValue {
		public double time;
		public double length;
		public double prevTotalDistance;
		public double prevTotalTime;
	}

	protected enum ProgressState {
		ON_PATH,
		ON_STOP,
		OUTSIDE
	}
	private static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs

	private Route route;
	private ArrayList<LatLng> prevPositions;
	private RouteProgress lastProgress;
	private IEntry lastVisitEntry;
	private int lastVisitIndex;

	private List<IProgressEventListener> progressEventListeners;

	private HashMap<IEntry, ComputedValue[]> precomputedTimes;

	public ProgressController(Route route) {
		this.route = route;

		prevPositions = new ArrayList<>();
		lastProgress = new RouteProgress();
		progressEventListeners = new LinkedList<>();
		lastVisitIndex = 0;
		lastVisitEntry = route.getEntries()[0];
		precomputedTimes = preComputeDistancesAndMinutes(route);
		lastProgress = calculateProgress(route, precomputedTimes, null, null, prevPositions);
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

		TrackedPoint trackedPoint = getNextPoint(position, route, 0.05, lastVisitEntry, lastVisitIndex);

		if(trackedPoint == null) {
			trackedPoint = getNextPoint(position, route, 0.05, route.getEntries()[0], 0);
		}

		if(trackedPoint != null) {
			lastVisitEntry = trackedPoint.entry;
			lastVisitIndex = trackedPoint.closestPoint.lowerIndex;
		}
		else {
			lastVisitEntry = route.getEntries()[0];
			lastVisitIndex = 0;
		}

		prevPositions.add(position);

		lastProgress = calculateProgress(route, precomputedTimes, trackedPoint, lastProgress, prevPositions);

		for (IProgressEventListener eventListener: progressEventListeners) {
			if(eventListener != null) {
				eventListener.ProgressUpdated(lastProgress);
			}
		}
	}

	private RouteProgress calculateProgress(Route route, HashMap<IEntry, ComputedValue[]> precomputedValues, TrackedPoint point,  RouteProgress previousProgress, List<LatLng> positions) {
		RouteProgress progress = RouteProgress.createNewProgress();

		IEntry[] entries = route.getEntries();
		IEntry lastEntry = entries[entries.length-1];
		ComputedValue[] lastEntryValues = precomputedValues.get(lastEntry);

		double minutesTotal = lastEntryValues[0].prevTotalTime + lastEntry.getDuration();

		double totalRouteDistance = lastEntryValues[lastEntryValues.length-1].prevTotalDistance;

		Date startTime = null;
		Date actualEndTime = null;

		Date currentTime = Calendar.getInstance().getTime();
		LatLng currentPosition = null;
		boolean isOnRoute = false;

		boolean isOnPath = false;
		String nextGoalTitle = "not set";
		double distanceToNextGoal = 1;
		double distanceAchievedInGoal = 0.5;
		double timeToNextGoal = 1;

		IEntry currentEntry = null;
		int indexInCurrentEntry = 0;

		IEntry nextPlace=null;
		String nextPlaceTitle = "not set";
		double distanceToNextPlace = 0;
		double timeToNextPlace = 0;

		double currentDistanceTraveled = 0;

		if(point == null && previousProgress == null) {
			startTime = Calendar.getInstance().getTime();
			currentPosition = ((Stop) entries[0]).getLocation().getLatLng();

			isOnRoute = false;
			isOnPath = false;

			currentEntry = entries[0];
			indexInCurrentEntry = 0;
			nextPlaceTitle = "" + currentEntry.getIndex();
			distanceToNextPlace = 0;
			currentDistanceTraveled = 0;
			timeToNextGoal = 0;
			nextPlace = entries[0];
			actualEndTime = new Date(startTime.getTime() + (long)(minutesTotal * ONE_MINUTE_IN_MILLIS));
		}
		else if(previousProgress != null){
			startTime = previousProgress.startTime;
			actualEndTime = previousProgress.actualEndTime;
			currentPosition = previousProgress.progressUpdatePosition;
			isOnRoute = false;
			isOnPath = previousProgress.isOnPath;
			currentEntry = previousProgress.currentEntry;
			indexInCurrentEntry = previousProgress.currentEntryIndex;
			nextPlaceTitle = previousProgress.nextPlaceTitle;
			currentDistanceTraveled = previousProgress.currentDistanceTraveled;
			distanceToNextPlace = previousProgress.distanceToNextPlace;
			timeToNextPlace = previousProgress.timeToNextPlace;

			if(point != null) {
				currentPosition = point.closestPoint.closestPoint;
				isOnRoute = true;
				isOnPath = point.entry instanceof Path;
				currentEntry = point.entry;
				indexInCurrentEntry = point.closestPoint.lowerIndex;
				IEntry place = findNextPlace(route, currentEntry);

				ComputedValue[] currentEntryValues = precomputedValues.get(currentEntry);

				currentDistanceTraveled = currentEntryValues[indexInCurrentEntry].prevTotalDistance;

				if(isOnPath) {
					Path path = (Path) currentEntry;
					List<PathVertex> vertices = path.getVertices();
					double distanceInPathVertex = MapMaths.distance(vertices.get(indexInCurrentEntry).toLatLong(), currentPosition);
					currentDistanceTraveled += distanceInPathVertex;
				}

				if(place != null) {
					nextPlaceTitle = "" + place.getIndex();
					//nextPlaceTitle = MapFragmentTab3.responsePlaces;

					distanceToNextPlace = currentEntryValues[0].prevTotalDistance - currentDistanceTraveled;
					if(isOnPath)
						timeToNextPlace = (distanceToNextPlace / (precomputedValues.get(place)[0].prevTotalDistance - currentEntryValues[0].prevTotalDistance)) * currentEntry.getDuration();
					else
						timeToNextPlace = 0;
					nextPlace = place;
				}
				else {
					// we should not get in here this is a fucked up area
					nextPlaceTitle = previousProgress.nextPlaceTitle;
					distanceToNextPlace = 0;
					timeToNextPlace = 0;
					nextPlace = currentEntry;
				}
			}
		}

		nextPlace = nextPlace == null ? currentEntry : nextPlace;

		ComputedValue[] nextPlaceValues = precomputedValues.get(nextPlace);
 		double minutesLeft = minutesTotal - nextPlaceValues[0].prevTotalTime + timeToNextPlace;
		Date expectedFinishTime = new Date(currentTime.getTime() + (long)(minutesLeft * ONE_MINUTE_IN_MILLIS));

		progress.setRouteTimeInfo(startTime, actualEndTime, expectedFinishTime);
		progress.setProgressInfo(currentTime, currentPosition, isOnRoute);
		progress.setNextGoalInfo(isOnPath, nextGoalTitle, distanceToNextGoal, distanceAchievedInGoal, timeToNextGoal);
		progress.setNextPlaceInfo(nextPlaceTitle, distanceToNextPlace, timeToNextPlace);
		progress.setPrevPositions(positions);
		progress.setRouteDistanceInfo(totalRouteDistance, currentDistanceTraveled);
		progress.setCurrentEntryInfo(currentEntry, indexInCurrentEntry);

		return progress;
	}

	private HashMap<IEntry, ComputedValue[]> preComputeDistancesAndMinutes(Route route) {
		HashMap<IEntry, ComputedValue[]> expectedTimes = new HashMap<>();
		double sumTime = 0;
		double sumLength = 0;
		for(IEntry entry : route.getEntries()) {
			ComputedValue[] computedValues=null;

			if(entry instanceof Stop) {
				Stop stop = (Stop) entry;
				computedValues = new ComputedValue[1];
				computedValues[0] = new ComputedValue();

				computedValues[0].length = 0;
				computedValues[0].time = stop.getDuration();
				computedValues[0].prevTotalDistance = sumLength;
				computedValues[0].prevTotalTime = sumTime;

				sumTime += stop.getDuration();
			}
			else if(entry instanceof Path) {
				Path path = (Path) entry;
				double pathDuration = path.getDuration();
				List<PathVertex> vertices = path.getVertices();
				computedValues = new ComputedValue[vertices.size()];

				for(int i=0; i<vertices.size(); i++)
					computedValues[i] = new ComputedValue();

				PathVertex prevVertex = null;
				double totalPathLength=0;
				int index=0;
				for(PathVertex vertex : vertices) {
					if(prevVertex != null) {
						double vertexLength = MapMaths.distance(vertex.toLatLong(), prevVertex.toLatLong());
						computedValues[index].length = vertexLength;
						totalPathLength += vertexLength;
						index++;
					}

					prevVertex = vertex;
				}

				for (ComputedValue computedValue : computedValues) {
					double time = pathDuration * (computedValue.length / totalPathLength);
					computedValue.time = time;
					computedValue.prevTotalTime = sumTime;
					computedValue.prevTotalDistance = sumLength;

					sumTime += time;
					sumLength += computedValue.length;
				}
			}

			expectedTimes.put(entry, computedValues);
		}

		return expectedTimes;
	}

	private IEntry findNextPlace(Route route, IEntry currentEntry) {
		boolean currentFound = false;
		IEntry nextPlace = null;
		for (IEntry entry: route.getEntries()) {
			if(entry == currentEntry) {
				currentFound = true;
				nextPlace = currentEntry instanceof Stop ? currentEntry : null;
			}
			else if(currentFound && entry instanceof Stop) {
				nextPlace = entry;
			}

			if(nextPlace != null)
				break;
		}

		return nextPlace;
	}

	public void clearProgressEventListeners() {
		progressEventListeners = new LinkedList<>();
	}
}
