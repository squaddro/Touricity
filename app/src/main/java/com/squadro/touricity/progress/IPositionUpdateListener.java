package com.squadro.touricity.progress;

import com.google.android.gms.maps.model.LatLng;

public interface IPositionUpdateListener {
	void OnPositionUpdated(LatLng latLng);
}
