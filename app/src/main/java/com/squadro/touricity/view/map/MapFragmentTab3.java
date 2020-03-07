package com.squadro.touricity.view.map;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.event.IRouteDraw;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragmentTab3 extends Fragment implements OnMapReadyCallback, IRouteDraw {

    private SupportMapFragment supportMapFragment;
    private BottomSheetBehavior bottomSheetBehavior;
    private FrameLayout frameLayout;
    private GoogleMap map;
    private MapLongClickListener mapLongClickListener = null;
    private File offlineDataFile;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.tab3_map);

        LatLng tobb = new LatLng(39.921260, 32.798165);
        googleMap.addMarker(new MarkerOptions().position(tobb).title("tobb"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(tobb));

        initializeSheetbehavior(googleMap);

        offlineDataFile = new CreateOfflineDataDirectory().offlineRouteFile(getContext());

        createExampleView();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createExampleView() {
        List<Route> routes = new ArrayList<>();
        Route route = MapFragmentTab2.initialRoute();
        routes.add(route);
        routes.add(route);
        routes.add(route);
        
        saveRoutesToFile(routes, offlineDataFile);

        SavedRouteView savedRouteView = getActivity().findViewById(R.id.route_save);
        savedRouteView.setRouteList(getRoutesFromFile(offlineDataFile));
        savedRouteView.setIRouteDraw(this);
    }

    private void saveRoutesToFile(List<Route> routes, File file) {
        XStream xStream = new XStream();
        try {
            FileWriter writer = new FileWriter(file);
            xStream.toXML(routes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Route> getRoutesFromFile(File file) {
        XStream xStream = new XStream();
        return (List<Route>) xStream.fromXML(file);
    }

    private void initializeSheetbehavior(GoogleMap googleMap) {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(getActivity().findViewById(R.id.route_save));
        /* Open this if you want to open popup when map is long clicked
                 List<String> buttonNames = new ArrayList<>();
                 buttonNames.add("Add to route");
                 PopupWindowParameters popupWindowParameters = new PopupWindowParameters(numberOfButtons,buttonNames);
                 mapLongClickListener = new MapLongClickListener(map, frameLayout, 0, bottomSheetBehavior.getPeekHeight(),popupWindowParameters);
                */
        initBottomSheetCallback(bottomSheetBehavior, mapLongClickListener);
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

    public MapLongClickListener getMapLongClickListener() {
        return mapLongClickListener;
    }

    @Override
    public void drawHighlighted(Route route) {
        PolylineDrawer polylineDrawer = new PolylineDrawer(map);
        polylineDrawer.drawRoute(route);
    }
}
