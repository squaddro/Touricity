package com.squadro.touricity.view.progress;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;
import com.squadro.touricity.R;
import com.squadro.touricity.progress.IPositionUpdateListener;
import com.squadro.touricity.progress.IProgressEventListener;
import com.squadro.touricity.progress.Progress;

import java.util.Arrays;
import java.util.List;

public class MapProgressView implements GoogleMap.OnMapLongClickListener, IProgressEventListener {

	private GoogleMap map;

	private IPositionUpdateListener positionUpdateListener;

	private Polyline polyline;
	private Marker positionMarker;

	public MapProgressView(GoogleMap map) {
		this.map = map;
	}

	private void initListeners() {
		map.setOnMapLongClickListener(this);
	}

	@Override
	public void onMapLongClick(LatLng latLng) {
		if(positionUpdateListener != null) {

			positionUpdateListener.onPositionUpdated(latLng);
		}
	}

	@Override
	public void progressUpdated(Progress progress) {
		if(polyline != null)
			polyline.remove();

		if(positionMarker != null)
			positionMarker.remove();

		List<PatternItem> patternItems = Arrays.asList(new Dot(), new Gap(10));

		PolylineOptions options = new PolylineOptions().addAll(progress.getPrevPositions());
		options.pattern(patternItems);
		options.jointType(JointType.ROUND);
		options.startCap(new SquareCap());
		options.endCap(new RoundCap());
		options.zIndex(1000);

		polyline = map.addPolyline(options);

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(progress.getProgressUpdatePosition());
		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.position_marker));
		markerOptions.anchor(0.5f, 0.5f);

		positionMarker = map.addMarker(markerOptions);
	}

	@Override
	public void progressFinished() {
		if(polyline != null)
			polyline.remove();
		if(positionMarker != null)
			positionMarker.remove();
	}

	public void setCustomPositionUpdateListener(IPositionUpdateListener positionUpdateListener) {
		this.positionUpdateListener = positionUpdateListener;
		initListeners();
	}
}
