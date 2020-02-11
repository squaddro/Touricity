package com.squadro.touricity.view.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squadro.touricity.R;

public class MapFragmentTab3 extends Fragment implements OnMapReadyCallback {

    SupportMapFragment supportMapFragment;

    static MapLongClickListener mapLongClickListener;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3_map_view, container, false);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.tab3_map, supportMapFragment).commit();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng tobb = new LatLng(39.921260, 32.798165);
        googleMap.addMarker(new MarkerOptions().position(tobb).title("tobb"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(tobb));
        FrameLayout frameLayout = (FrameLayout)getActivity().findViewById(R.id.tab3_map);
        mapLongClickListener = new MapLongClickListener(googleMap,frameLayout);
    }

    public MapLongClickListener getMapLongClickListener() {
        return mapLongClickListener;
    }
}
