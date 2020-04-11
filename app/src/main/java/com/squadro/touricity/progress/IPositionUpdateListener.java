package com.squadro.touricity.progress;

import com.google.android.gms.maps.model.LatLng;

public interface IPositionUpdateListener {
	void onPositionUpdated(LatLng latLng);
	void onEndProgress();
}
