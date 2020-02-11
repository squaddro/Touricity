package com.squadro.touricity.view.map;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
import com.squadro.touricity.view.filter.Filter;
import com.squadro.touricity.view.filter.MinRatingBar;
import com.squadro.touricity.view.filter.TransportationCheckBox;
import com.squadro.touricity.view.routeList.RouteExploreView;
import com.squadro.touricity.view.search.SearchBar;

import java.util.ArrayList;

public class MapFragmentTab1 extends Fragment implements OnMapReadyCallback {

    SupportMapFragment supportMapFragment;
    RouteExploreView routeExploreView;
    MapLongClickListener mapLongClickListener;

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
        addFilterSearchPanel();
        LatLng tobb = new LatLng(39.921260, 32.798165);
        googleMap.addMarker(new MarkerOptions().position(tobb).title("tobb"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(tobb));

        SearchBar searchBar = new SearchBar(getActivity(), getContext());
        MinRatingBar minRatingBar = new MinRatingBar(getActivity());
        AverageCostSeekBar averageCostSeekBar = new AverageCostSeekBar(getActivity());
        DurationSeekBar durationSeekBar = new DurationSeekBar(getActivity());
        TransportationCheckBox transportationCheckBox = new TransportationCheckBox(getActivity());
        Filter filter = new Filter(getActivity(), searchBar, minRatingBar, averageCostSeekBar,
                durationSeekBar, transportationCheckBox);
        routeExploreView = getActivity().findViewById(R.id.route_explore);
        routeExploreView.setRouteList(exampleRouteList());

        TopSheetBehavior topSheetBehavior = TopSheetBehavior.from(getActivity().findViewById(R.id.filter_search));
        initTopSheetCallback(topSheetBehavior);

        FrameLayout frameLayout = (FrameLayout)getActivity().findViewById(R.id.tab1_map);
        mapLongClickListener = new MapLongClickListener(googleMap,frameLayout,topSheetBehavior.getPeekHeight());
    }

    private void initTopSheetCallback(TopSheetBehavior topSheetBehavior) {
        topSheetBehavior.setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == 3){
                    mapLongClickListener.setPeekHeight(bottomSheet.getHeight());
                }else if(newState == 4){
                    mapLongClickListener.setPeekHeight(topSheetBehavior.getPeekHeight());
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

    private ArrayList<Route> exampleRouteList(){
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
                "path_type",
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
                "path_type",
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
                "path_type",
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
                "path_type",
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
                "path_type",
                path5
        ));

        routes.add(route);
        routes.add(route2);
        return routes;
    }
    private void addFilterSearchPanel() {
        LinearLayout linearLayout = getView().findViewById(R.id.filter_search);
        TopSheetBehavior.from(linearLayout).setState(TopSheetBehavior.STATE_COLLAPSED);
    }

}
