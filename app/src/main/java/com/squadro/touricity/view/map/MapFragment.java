package com.squadro.touricity.view.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        LatLng tobb = new LatLng(39.921260, 32.798165);
        map.addMarker(new MarkerOptions().position(tobb).title("tobb"));
        map.moveCamera(CameraUpdateFactory.newLatLng(tobb));
    }
}
