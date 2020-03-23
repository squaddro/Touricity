package com.squadro.touricity.view.map;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.requests.RouteRequests;
import com.squadro.touricity.view.map.DirectionsAPI.DirectionPost;
import com.squadro.touricity.view.map.DirectionsAPI.PointListReturner;
import com.squadro.touricity.view.map.editor.IEditor;
import com.squadro.touricity.view.map.editor.PathEditor;
import com.squadro.touricity.view.popupWindowView.PopupWindowParameters;
import com.squadro.touricity.view.routeList.IRouteResponse;
import com.squadro.touricity.view.routeList.RouteCreateView;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.event.IRouteMapViewUpdater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragmentTab2 extends Fragment implements OnMapReadyCallback, IRouteMapViewUpdater, IRouteResponse {

    private SupportMapFragment supportMapFragment;
    private MapLongClickListener mapLongClickListener = null;
    public static RouteCreateView routeCreateView;
    private BottomSheetBehavior bottomSheetBehavior;
    public FrameLayout frameLayout;
    private GoogleMap map;
    private PopupWindowParameters popupWindowParameters;

    private IEditor editor;

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

        initializePlacesAutofill();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializePlacesAutofill() {
        if (!Places.isInitialized()) {
            Places.initialize(this.getContext(), getResources().getString(R.string.api_key));
        }

        PlacesClient placesClient = Places.createClient(getContext());
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autoCompleteFragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                if(routeCreateView.getRoute().getAbstractEntryList().size() == 0){
                    routeCreateView.onInsertLocation(new Location(place.getId(), place.getLatLng().latitude, place.getLatLng().longitude));
                }

                else if(routeCreateView.getRoute().getAbstractEntryList().size() == 1){
                    Stop prevStop = (Stop) routeCreateView.getRoute().getAbstractEntryList().get(routeCreateView.getRoute().getAbstractEntryList().size()-1);

                    DirectionPost directionPost = new DirectionPost();
                    String url = directionPost.getDirectionsURL(prevStop.getLocation().getLatLng(),place.getLatLng(),null,"driving");
                    PointListReturner plr = new PointListReturner(url, routeCreateView);
                }

                //routeCreateView.onInsertLocation(new Location(place.getId(), place.getLatLng().latitude, place.getLatLng().longitude));
            }

            @Override
            public void onError(Status status) {

            }
        });
    }

    public static RouteCreateView getRouteCreateView() {
        return routeCreateView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createRouteCreateView() {
        routeCreateView = getActivity().findViewById(R.id.route_create);
        routeCreateView.setRoute(initialRoute());
        routeCreateView.setRouteMapViewUpdater(this);
        Button saveButton = routeCreateView.findViewById(R.id.route_create_save);
        saveButton.setOnClickListener(v -> {
            RouteRequests routeRequests = new RouteRequests();
            routeRequests.updateRoute(routeCreateView.getRoute(), this);
            routeCreateView.CleanView();
        });
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
        popupWindowParameters = new PopupWindowParameters(numberOfButtons, buttonNames);
        mapLongClickListener = new MapLongClickListener(map, frameLayout, 0, bottomSheetBehavior.getPeekHeight(), popupWindowParameters);
        createButtonListeners(mapLongClickListener.getButtons());
        initBottomSheetCallback(bottomSheetBehavior, mapLongClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createButtonListeners(List<Button> buttons) {
        Button button = buttons.get(0);
        button.setOnClickListener(v -> {
            LatLng latLng = mapLongClickListener.getLatLng();
            Location location = new Location("sample_id", latLng.latitude, latLng.longitude);
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
                        Log.d("map", map.getProjection().getVisibleRegion().latLngBounds.toString());

                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    @Override
    public void updateRoute(Route route) {
        Log.d("fmap", "Update the route ");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void highlight(AbstractEntry entry) {
        Log.d("fmap", "highligt the entry " + entry.getComment());
        PolylineDrawer polylineDrawer = new PolylineDrawer(map);
        polylineDrawer.drawRoute(routeCreateView.getRoute());
        //map.animateCamera(CameraUpdateFactory.newLatLngBounds(MapMaths.getRouteBoundings(routeCreateView.getRoute()), 0));
        disposeEditor();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void focus(AbstractEntry entry) {
        Log.d("fmap", "focus to the entry " + entry.getComment());

        PolylineDrawer polylineDrawer = new PolylineDrawer(map);

        if (entry instanceof Stop)
            polylineDrawer.drawRoute(routeCreateView.getRoute(), (Stop) entry);

        else if (entry instanceof Path)
            polylineDrawer.drawRoute(routeCreateView.getRoute(), (Path) entry);

        disposeEditor();

        if (entry instanceof Path) {
            PathEditor pathEditor = new PathEditor();
            pathEditor.prepare(map, (Path) entry);
            pathEditor.setDataUpdateListener(data -> routeCreateView.onPathUpdate(data));
            editor = pathEditor;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void disposeEditor() {
        if (editor != null) {
            editor.dispose();
            mapLongClickListener = new MapLongClickListener(map, frameLayout, 0, bottomSheetBehavior.getPeekHeight(), popupWindowParameters);
            createButtonListeners(mapLongClickListener.getButtons());
            editor = null;
        }
    }

    public static Route initialRoute() {
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
                new Location(31.3, 31.3),
                null
        ));
        ArrayList path1 = new ArrayList<PathVertex>();
        path1.add(new PathVertex(39.921260, 32.796165));
        path1.add(new PathVertex(39.924260, 32.797165));
        path1.add(new PathVertex(39.922260, 32.798165));
        path1.add(new PathVertex(39.925260, 32.799165));

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
                new Location(32.3, 42.3),
                null
        ));
        ArrayList path2 = new ArrayList<PathVertex>();
        path2.add(new PathVertex(39.921260, 32.795165));
        path2.add(new PathVertex(39.924260, 32.796165));
        path2.add(new PathVertex(39.922260, 32.797165));
        path2.add(new PathVertex(39.925260, 32.798165));

        route.addEntry(new Path(
                null,
                10,
                5,
                "Bu yolu takip edin 10 dakika",
                null,
                Path.PathType.DRIVING,
                path2
        ));
        route.addEntry(new Stop(
                null,
                60,
                10,
                "burada yaklaşık 10 dakika bekleyin",
                new Location(21.3, 21.3),
                null
        ));
        ArrayList path3 = new ArrayList<PathVertex>();
        path3.add(new PathVertex(39.921260, 32.794165));
        path3.add(new PathVertex(39.924260, 32.795165));
        path3.add(new PathVertex(39.922260, 32.796165));
        path3.add(new PathVertex(39.925260, 32.797165));

        route.addEntry(new Path(
                null,
                10,
                5,
                "Bu yolu takip edin 15 dakika",
                null,
                Path.PathType.WALKING,
                path3
        ));
        route.addEntry(new Stop(
                null,
                100,
                140,
                "burada yaklaşık 140 dakika bekleyin",
                new Location(22.3, 22.3),
                null
        ));

        return route;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRouteResponse(Route route) {
        SavedRouteView savedRouteView = getActivity().findViewById(R.id.route_save);
        savedRouteView.getIRouteSave().saveRoute(route);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.getTabAt(2).select();
    }
}
