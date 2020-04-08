package com.squadro.touricity.view.progress;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squadro.touricity.converter.RouteConverter;
import com.squadro.touricity.progress.ProgressController;

import java.util.List;

public class MapProgressViewer implements GoogleMap.OnMapLongClickListener {

	private GoogleMap map;
	private ProgressController progressController;
	private boolean addGpsOnClick;

	private Polyline gpsTrack;

	public MapProgressViewer(GoogleMap map, ProgressController progressController, boolean addGpsOnClick) {
		this.map = map;
		this.progressController = progressController;
		this.addGpsOnClick = addGpsOnClick;
		this.gpsTrack = map.addPolyline(new PolylineOptions());
	}

	private void initListeners() {
		map.setOnMapLongClickListener(this);
	}

	@Override
	public void onMapLongClick(LatLng latLng) {
		List<LatLng> list = gpsTrack.getPoints();
		list.add(latLng);

		gpsTrack.setPoints(list);

		progressController.UpdatePosition(latLng);
	}
}
