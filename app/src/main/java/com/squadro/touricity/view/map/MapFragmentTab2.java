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
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.requests.LocationRequests;
import com.squadro.touricity.requests.StopRequests;
import com.squadro.touricity.view.routeList.RouteCreateView;

import java.util.ArrayList;

public class MapFragmentTab2 extends Fragment implements OnMapReadyCallback {

    SupportMapFragment supportMapFragment;
    RouteCreateView routeCreateView;

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
        new LocationRequests();
        new StopRequests();
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng tobb = new LatLng(10, 10);
        googleMap.addMarker(new MarkerOptions().position(tobb).title("tobb"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(tobb));

        routeCreateView = getActivity().findViewById(R.id.route_create);
        routeCreateView.setRoute(initialialRoute());
    }

    private Route initialialRoute(){
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

        return route;
    }
}
