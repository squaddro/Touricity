package com.squadro.touricity.view.map;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.requests.RouteRequests;
import com.squadro.touricity.view.popupWindowView.PopupWindowParameters;
import com.squadro.touricity.view.routeList.RouteCreateView;
import com.squadro.touricity.view.routeList.event.IRouteMapViewUpdater;

import java.util.ArrayList;
import java.util.List;

public class MapFragmentTab2 extends Fragment implements OnMapReadyCallback, IRouteMapViewUpdater {

    private SupportMapFragment supportMapFragment;
    private MapLongClickListener mapLongClickListener = null;
    private RouteCreateView routeCreateView;
    private BottomSheetBehavior bottomSheetBehavior;
    private FrameLayout frameLayout;
    private GoogleMap map;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_map_view, container, false);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.tab2_map, supportMapFragment).commit();
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.tab2_map);

        LatLng tobb = new LatLng(10, 10);
        googleMap.addMarker(new MarkerOptions().position(tobb).title("tobb"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(tobb));

        createRouteCreateView();
        initializeSheetBehaviors();
        RouteRequests routeRequests = new RouteRequests(routeCreateView);
        routeRequests.updateRoute(initialialRoute());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createRouteCreateView() {
        routeCreateView = getActivity().findViewById(R.id.route_create);
        routeCreateView.setRoute(initialialRoute());
        routeCreateView.setRouteMapViewUpdater(this);
    }

    public MapLongClickListener getMapLongClickListener() {
        return mapLongClickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializeSheetBehaviors() {
        bottomSheetBehavior = BottomSheetBehavior.from(getActivity().findViewById(R.id.route_create));
        int numberOfButtons = 1;
        List<String> buttonNames = new ArrayList<>();
        buttonNames.add("Add to route");
        PopupWindowParameters popupWindowParameters = new PopupWindowParameters(numberOfButtons,buttonNames);
        mapLongClickListener = new MapLongClickListener(map, frameLayout, 0, bottomSheetBehavior.getPeekHeight(),popupWindowParameters);
        createButtonListeners(mapLongClickListener.getButtons());
        initBottomSheetCallback(bottomSheetBehavior, mapLongClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createButtonListeners(List<Button> buttons) {
        Button button = buttons.get(0);
        button.setOnClickListener(v -> {
            LatLng latLng = mapLongClickListener.getLatLng();
            Location location = new Location("sample_id",latLng.latitude,latLng.longitude);
            routeCreateView.onInsertLocation(location);
            mapLongClickListener.dissmissPopUp();
        });
    }

    private void initBottomSheetCallback(BottomSheetBehavior bottomSheetBehavior, MapLongClickListener mapLongClickListener) {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_EXPANDED) {
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

    private Route initialialRoute() {
        Route route = new Route();
        route.setCreator("4c0ac9c5-ecf7-bf57-ce21-175587e8d8b6");
        route.setRoute_id(null);
        route.setCity_id("c08ac5c2-5b9f-6a8f-35bf-448917e7d8f9");
        route.setTitle("titleee");
        route.setPrivacy(2);
        route.addEntry(new Stop(
                null,
                10,
                40,
                "burada yaklaşık 40 dakika bekleyin",
                "5c0ca3bb-638d-41ef-8e36-53a5b113d044",
                null
        ));
        ArrayList path1 = new ArrayList<PathVertex>();
        path1.add(new PathVertex(1.1, 1.0));
        path1.add(new PathVertex(1.2, 1.1));
        path1.add(new PathVertex(1.4, 1.2));
        path1.add(new PathVertex(1.5, 1.3));

        route.addEntry(new Path(
                null,
                10,
                5,
                "Bu yolu takip edin 5 dakika",
                null,
                Path.PathType.BUS,
                path1
        ));
        route.addEntry(new Stop(
                null,
                20,
                50,
                "burada yaklaşık 50 dakika bekleyin",
                "5c0ca3bb-638d-41ef-8e36-53a5b113d044",
                null
        ));
        ArrayList path2 = new ArrayList<PathVertex>();
        path2.add(new PathVertex(1.1, 1.0));
        path2.add(new PathVertex(1.2, 1.1));
        path2.add(new PathVertex(1.4, 1.2));
        path2.add(new PathVertex(1.5, 1.3));

        route.addEntry(new Path(
                null,
                10,
                5,
                "Bu yolu takip edin 5 dakika",
                null,
                Path.PathType.BUS,
                path1
        ));
        route.addEntry(new Stop(
                null,
                60,
                10,
                "burada yaklaşık 10 dakika bekleyin",
                "5c0ca3bb-638d-41ef-8e36-53a5b113d044",
                null
        ));
        ArrayList path3 = new ArrayList<PathVertex>();
        path3.add(new PathVertex(1.1, 1.0));
        path3.add(new PathVertex(1.2, 1.1));
        path3.add(new PathVertex(1.4, 1.2));
        path3.add(new PathVertex(1.5, 1.3));

        route.addEntry(new Path(
                null,
                10,
                5,
                "Bu yolu takip edin 5 dakika",
                null,
                Path.PathType.BUS,
                path1
        ));
        route.addEntry(new Stop(
                null,
                100,
                140,
                "burada yaklaşık 140 dakika bekleyin",
                "5c0ca3bb-638d-41ef-8e36-53a5b113d044",
                null
        ));

        return route;
    }

    @Override
    public void updateRoute(Route route) {
        Log.d("fmap", "Update the route ");
    }

    @Override
    public void highlight(AbstractEntry entry) {
        Log.d("fmap", "highligt the entry " + entry.getComment());
    }

    @Override
    public void focus(AbstractEntry entry) {
        Log.d("fmap", "focus to the entry " + entry.getComment());

    }
}
