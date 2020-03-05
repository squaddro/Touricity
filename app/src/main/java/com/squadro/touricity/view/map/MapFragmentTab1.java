package com.squadro.touricity.view.map;

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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.topSheetBehavior.TopSheetBehavior;
import com.squadro.touricity.view.filter.AverageCostSeekBar;
import com.squadro.touricity.view.filter.DurationSeekBar;
import com.squadro.touricity.view.filter.FilterHandler;
import com.squadro.touricity.view.filter.MinRatingBar;
import com.squadro.touricity.view.filter.TransportationCheckBox;
import com.squadro.touricity.view.routeList.RouteExploreView;
import com.squadro.touricity.view.search.SearchBar;

import java.util.ArrayList;

public class MapFragmentTab1 extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment supportMapFragment;
    private RouteExploreView routeExploreView;
    private MapLongClickListener mapLongClickListener = null;
    private TopSheetBehavior topSheetBehavior;
    private BottomSheetBehavior bottomSheetBehavior;
    private GoogleMap map;
    private FrameLayout frameLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_map_view, container, false);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.tab1_map, supportMapFragment).commit();

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.tab1_map);

        LatLng tobb = new LatLng(39.921260, 32.798165);
        LatLng somewhere = new LatLng(36.921210, 31.798120);
        googleMap.addMarker(new MarkerOptions().position(tobb).title("tobb"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(tobb));

        createFilterView();
        createRouteExploreView();
        initializeSheetBehaviors();


        //This is how we draw a path between 2 points.
        String url = getDirectionsURL(tobb, somewhere, null, "driving");
  //      FetchUrl FetchUrl = new FetchUrl(map);
  //      FetchUrl.execute(url);

    }

    private String getDirectionsURL(LatLng origin, LatLng dest, LatLng[] waypoints, String mode) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        String str_mode = "mode=" + mode;

        String str_waypoints = "";

        if(waypoints != null && waypoints.length > 0){
            str_waypoints += "waypoints=";

            for(int i=0 ; i<waypoints.length ; i++){
                str_waypoints += waypoints[i].latitude + "," + waypoints[i].longitude + "|";
            }
            str_waypoints = str_waypoints.substring(0, str_waypoints.length()-1);
        }

        // Building the parameters to the web service
        String parameters = str_mode + "&" + str_origin + "&" + str_dest + "&" + sensor + "&" + str_waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBrr2iE49aWzGwLhWPYW5ABBV6Ja-8zyvE";

        //url = "https://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=walking&origin=37.757946%2C39.4048&destination=37.757954%2C38.426349&waypoints=37.746560%2C38.408328&key=AIzaSyBrr2iE49aWzGwLhWPYW5ABBV6Ja-8zyvE";
        return url;
    }

    private void initializeSheetBehaviors() {
         /* Open this if you want to open popup when map is long clicked
                 List<String> buttonNames = new ArrayList<>();
                 buttonNames.add("Add to route");
                 PopupWindowParameters popupWindowParameters = new PopupWindowParameters(numberOfButtons,buttonNames);
                 mapLongClickListener = new MapLongClickListener(map, frameLayout, 0, bottomSheetBehavior.getPeekHeight(),popupWindowParameters);
                */

        topSheetBehavior = TopSheetBehavior.from(getActivity().findViewById(R.id.filter_search));
        initTopSheetCallback(topSheetBehavior, mapLongClickListener);

        bottomSheetBehavior = BottomSheetBehavior.from(getActivity().findViewById(R.id.route_explore));
        initBottomSheetCallback(bottomSheetBehavior, mapLongClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createRouteExploreView() {
        routeExploreView = getActivity().findViewById(R.id.route_explore);
        routeExploreView.setRouteList(exampleRouteList());
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

    private ArrayList<Route> exampleRouteList() {
        ArrayList<Route> routes = new ArrayList<Route>();
        Route route = new Route();
        route.setCreator("id_creator_1");
        route.setRoute_id("id_route_id_2");
        route.addEntry(new Stop(
                "this should be null",
                10,
                40,
                "burada yaklaşık 40 dakika bekleyin",
                "id_location_1",
                "id_stop_1"
        ));
        ArrayList path1 = new ArrayList<PathVertex>();
        path1.add(new PathVertex(1.1, 1.0));
        path1.add(new PathVertex(1.2, 1.1));
        path1.add(new PathVertex(1.4, 1.2));
        path1.add(new PathVertex(1.5, 1.3));

        route.addEntry(new Path(
                "this should be null",
                10,
                5,
                "Bu yolu takip edin 5 dakika",
                "id_path_1",
                Path.PathType.BUS,
                path1
        ));
        route.addEntry(new Stop(
                "this should be null",
                20,
                50,
                "burada yaklaşık 50 dakika bekleyin",
                "id_location_2",
                "id_stop_2"
        ));
        ArrayList path2 = new ArrayList<PathVertex>();
        path2.add(new PathVertex(1.1, 1.0));
        path2.add(new PathVertex(1.2, 1.1));
        path2.add(new PathVertex(1.4, 1.2));
        path2.add(new PathVertex(1.5, 1.3));

        route.addEntry(new Path(
                "this should be null",
                10,
                5,
                "Bu yolu takip edin 5 dakika",
                "id_path_2",
                Path.PathType.BUS,
                path1
        ));
        route.addEntry(new Stop(
                "this should be null",
                60,
                10,
                "burada yaklaşık 10 dakika bekleyin",
                "id_location_3",
                "id_stop_3"
        ));
        ArrayList path3 = new ArrayList<PathVertex>();
        path3.add(new PathVertex(1.1, 1.0));
        path3.add(new PathVertex(1.2, 1.1));
        path3.add(new PathVertex(1.4, 1.2));
        path3.add(new PathVertex(1.5, 1.3));

        route.addEntry(new Path(
                "this should be null",
                10,
                5,
                "Bu yolu takip edin 5 dakika",
                "id_path_3",
                Path.PathType.BUS,
                path1
        ));
        route.addEntry(new Stop(
                "this should be null",
                100,
                140,
                "burada yaklaşık 140 dakika bekleyin",
                "id_location_4",
                "id_stop_4"
        ));

        Route route2 = new Route();
        route2.setCreator("id_creator_2");
        route2.setRoute_id("id_route_id_3");
        route2.addEntry(new Stop(
                "this should be null",
                10,
                40,
                "burada yaklaşık 40 dakika bekleyin",
                "id_location_1",
                "id_stop_1"
        ));
        ArrayList path4 = new ArrayList<PathVertex>();
        path4.add(new PathVertex(1.1, 1.0));
        path4.add(new PathVertex(1.2, 1.1));
        path4.add(new PathVertex(1.4, 1.2));
        path4.add(new PathVertex(1.5, 1.3));

        route2.addEntry(new Path(
                "this should be null",
                10,
                5,
                "Bu yolu takip edin 5 dakika",
                "id_path_1",
                Path.PathType.BUS,
                path4
        ));
        route2.addEntry(new Stop(
                "this should be null",
                20,
                50,
                "burada yaklaşık 50 dakika bekleyin",
                "id_location_2",
                "id_stop_2"
        ));
        ArrayList path5 = new ArrayList<PathVertex>();
        path5.add(new PathVertex(1.1, 1.0));
        path5.add(new PathVertex(1.2, 1.1));
        path5.add(new PathVertex(1.4, 1.2));
        path5.add(new PathVertex(1.5, 1.3));

        route2.addEntry(new Path(
                "this should be null",
                10,
                5,
                "Bu yolu takip edin 5 dakika",
                "id_path_2",
                Path.PathType.BUS,
                path5
        ));

        routes.add(route);
        routes.add(route2);
        return routes;
    }
}
