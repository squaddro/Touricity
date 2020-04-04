package com.squadro.touricity.view.map;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.squadro.touricity.R;
import com.squadro.touricity.maths.MapMaths;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.topSheetBehavior.TopSheetBehavior;
import com.squadro.touricity.view.filter.AverageCostSeekBar;
import com.squadro.touricity.view.filter.DurationSeekBar;
import com.squadro.touricity.view.filter.FilterHandler;
import com.squadro.touricity.view.filter.MinRatingBar;
import com.squadro.touricity.view.filter.TransportationCheckBox;
import com.squadro.touricity.view.map.placesAPI.CustomInfoWindowAdapter;
import com.squadro.touricity.view.map.placesAPI.MapLongClickListener;
import com.squadro.touricity.view.routeList.RouteExploreView;
import com.squadro.touricity.view.routeList.event.IRouteDraw;
import com.squadro.touricity.view.search.SearchBar;

import java.util.ArrayList;

import lombok.Getter;

public class MapFragmentTab1 extends Fragment implements OnMapReadyCallback, IRouteDraw,
        OnStreetViewPanoramaReadyCallback {

    private SupportMapFragment supportMapFragment;
    private RouteExploreView routeExploreView;
    private MapLongClickListener mapLongClickListener = null;
    private TopSheetBehavior topSheetBehavior;
    private BottomSheetBehavior bottomSheetBehavior;
    @Getter
    private static GoogleMap map;
    private FrameLayout frameLayout;

    public static StreetViewPanorama streetViewPanorama;
    public static StreetViewPanoramaFragment streetViewPanoramaFragment;
    public static View rootView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1_map_view, container, false);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.tab1_map, supportMapFragment).commit();

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.tab1_map);

        createFilterView();
        createRouteExploreView();
        initializeSheetBehaviors();
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));
        initializeStreetView();
    }

    private void initializeStreetView() {
        streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getActivity().getFragmentManager()
                        .findFragmentById(R.id.streetViewMap1);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    private void initializeSheetBehaviors() {
        topSheetBehavior = TopSheetBehavior.from(getActivity().findViewById(R.id.filter_search));
        initTopSheetCallback(topSheetBehavior, mapLongClickListener);

        bottomSheetBehavior = BottomSheetBehavior.from(getActivity().findViewById(R.id.route_explore));
        initBottomSheetCallback(bottomSheetBehavior, mapLongClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createRouteExploreView() {
        routeExploreView = getActivity().findViewById(R.id.route_explore);
        routeExploreView.setRouteList(new ArrayList<>());
        routeExploreView.setIRouteDraw(this);
    }

    private void createFilterView() {
        SearchBar searchBar = new SearchBar(getActivity(), getContext());
        MinRatingBar minRatingBar = new MinRatingBar(getActivity());
        AverageCostSeekBar averageCostSeekBar = new AverageCostSeekBar(getActivity());
        DurationSeekBar durationSeekBar = new DurationSeekBar(getActivity());
        TransportationCheckBox transportationCheckBox = new TransportationCheckBox(getActivity());
        FilterHandler filterHandler = new FilterHandler(getActivity(), searchBar, minRatingBar, averageCostSeekBar,
                durationSeekBar, transportationCheckBox);
    }

    private void initBottomSheetCallback(BottomSheetBehavior bottomSheetBehavior, MapLongClickListener mapLongClickListener) {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_DRAGGING) {
                    if (topSheetBehavior != null) {
                        topSheetBehavior.setState(TopSheetBehavior.STATE_COLLAPSED);
                    }
                } else if (i == BottomSheetBehavior.STATE_EXPANDED) {
                    if (mapLongClickListener != null) {
                        mapLongClickListener.setBottomPeekHeight(view.getHeight());
                    }
                } else if (i == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (mapLongClickListener != null) {
                        mapLongClickListener.setBottomPeekHeight(bottomSheetBehavior.getPeekHeight());
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    private void initTopSheetCallback(TopSheetBehavior topSheetBehavior, MapLongClickListener mapLongClickListener) {
        topSheetBehavior.setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == TopSheetBehavior.STATE_DRAGGING) {
                    if (bottomSheetBehavior != null) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                } else if (newState == TopSheetBehavior.STATE_EXPANDED) {
                    if (mapLongClickListener != null) {
                        mapLongClickListener.setTopPeekHeight(bottomSheet.getHeight());
                    }

                } else if (newState == TopSheetBehavior.STATE_COLLAPSED) {
                    if (mapLongClickListener != null) {
                        mapLongClickListener.setTopPeekHeight(topSheetBehavior.getPeekHeight());
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset, @Nullable Boolean isOpening) {

            }
        });
    }

    public MapLongClickListener getMapLongClickListener() {
        return mapLongClickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void drawHighlighted(Route route) {
        PolylineDrawer polylineDrawer = new PolylineDrawer(map, "explore");
        polylineDrawer.drawRoute(route);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(MapMaths.getRouteBoundings(route), 0));
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        MapFragmentTab1.streetViewPanorama = streetViewPanorama;
        streetViewPanorama.setOnStreetViewPanoramaChangeListener(streetViewPanoramaChangeListener);
    }

    private StreetViewPanorama.OnStreetViewPanoramaChangeListener streetViewPanoramaChangeListener = streetViewPanoramaLocation -> {
        if (streetViewPanoramaLocation == null || streetViewPanoramaLocation.links == null) {
            MapFragmentTab1.rootView.findViewById(R.id.streetCardViewMap1).setVisibility(View.INVISIBLE);
            new AlertDialog.Builder(getContext())
                    .setTitle("INFO")
                    .setMessage("This location has no street view")
                    .setNeutralButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        }
    };
}
