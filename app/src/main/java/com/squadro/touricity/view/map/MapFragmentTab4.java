package com.squadro.touricity.view.map;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.squadro.touricity.requests.SuggestedRoutesRequest;
import com.squadro.touricity.view.map.placesAPI.CustomInfoWindowAdapter;
import com.squadro.touricity.view.map.placesAPI.MapLongClickListener;
import com.squadro.touricity.view.routeList.RouteSuggestionView;
import com.squadro.touricity.view.routeList.event.IRouteDraw;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.Getter;

import static android.app.Activity.RESULT_OK;

public class MapFragmentTab4 extends Fragment implements OnMapReadyCallback, IRouteDraw,
        OnStreetViewPanoramaReadyCallback{

    private SupportMapFragment supportMapFragment;
    @Getter
    private static RouteSuggestionView routeSuggestionView;
    private MapLongClickListener mapLongClickListener = null;
    public static BottomSheetBehavior bottomSheetBehavior;
    private FrameLayout frameLayout;
    @Getter
    private static GoogleMap map;
    public static StreetViewPanorama streetViewPanorama;
    public static StreetViewPanoramaFragment streetViewPanoramaFragment;
    public static View rootView;
    private EditText commentText;
    public static List<Route> routes;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab4_map_view, container, false);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.tab4_map, supportMapFragment).commit();

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPause() {
        super.onPause();
        routes = routeSuggestionView.getRouteList();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.tab4_map);

        //createFilterView();
        createRouteSuggestionView();
        initializeSheetBehaviors();
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));
        initializeStreetView();
        //initializeMapListeners();
        initializeSuggestedRoutes();
    }

    private void initializeSuggestedRoutes() {
        SuggestedRoutesRequest suggestedRoutesRequest = new SuggestedRoutesRequest(getActivity(), routeSuggestionView);
        suggestedRoutesRequest.getFavRoutes();
    }

    public void initializeSpeechToText(EditText editText) {
        this.commentText = editText;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your comment");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result.size() > 0) {
                String text = commentText.getText().toString();
                commentText.setText(text + " "+ result.get(0));
            }
        }
    }

    private void initializeStreetView() {
        streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getActivity().getFragmentManager()
                        .findFragmentById(R.id.streetViewMap4);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    private void initializeSheetBehaviors() {

        bottomSheetBehavior = BottomSheetBehavior.from(getActivity().findViewById(R.id.route_suggestion));
        initBottomSheetCallback(bottomSheetBehavior, mapLongClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createRouteSuggestionView() {
        routeSuggestionView = getActivity().findViewById(R.id.route_suggestion);
        routeSuggestionView.setRouteList(new ArrayList<>());
        routeSuggestionView.setIRouteDraw(this);
    }

    private void initBottomSheetCallback(BottomSheetBehavior bottomSheetBehavior, MapLongClickListener mapLongClickListener) {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_DRAGGING) {

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

    public MapLongClickListener getMapLongClickListener() {
        return mapLongClickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void drawHighlighted(Route route) {
        PolylineDrawer polylineDrawer = new PolylineDrawer(map, "suggested");
        polylineDrawer.drawRoute(route);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(MapMaths.getRouteBoundings(route), 0));
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        MapFragmentTab4.streetViewPanorama = streetViewPanorama;
        streetViewPanorama.setOnStreetViewPanoramaChangeListener(streetViewPanoramaChangeListener);
    }

    private StreetViewPanorama.OnStreetViewPanoramaChangeListener streetViewPanoramaChangeListener = streetViewPanoramaLocation -> {
        if (streetViewPanoramaLocation == null || streetViewPanoramaLocation.links == null) {
            MapFragmentTab4.rootView.findViewById(R.id.streetCardViewMap4).setVisibility(View.INVISIBLE);
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
