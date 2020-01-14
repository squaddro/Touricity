package com.squadro.touricity.view.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squadro.touricity.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment supportMapFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_view, container, false);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(googleMap -> {
                LatLng tobb = new LatLng(39.921260, 32.798165);
                googleMap.addMarker(new MarkerOptions().position(tobb).title("tobb"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(tobb));
            });
        }
        getChildFragmentManager().beginTransaction().replace(R.id.map, supportMapFragment).commit();
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
