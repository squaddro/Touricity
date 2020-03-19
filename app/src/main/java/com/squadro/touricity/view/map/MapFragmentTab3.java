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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.SavedRoutesItem;
import com.squadro.touricity.view.routeList.event.IRouteDraw;
import com.squadro.touricity.view.routeList.event.IRouteSave;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

public class MapFragmentTab3 extends Fragment implements OnMapReadyCallback, IRouteDraw, IRouteSave {

    private SupportMapFragment supportMapFragment;
    private BottomSheetBehavior bottomSheetBehavior;
    private FrameLayout frameLayout;
    @Getter
    private static GoogleMap map;
    private MapLongClickListener mapLongClickListener = null;
    private File offlineDataFile;
    @Getter
    private static SavedRouteView savedRouteView;
    private XStream xStream;

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

        initializeSheetbehavior(googleMap);
        xStream = new XStream();
        offlineDataFile = new CreateOfflineDataDirectory().offlineRouteFile(getContext());
        savedRouteView = getActivity().findViewById(R.id.route_save);
        savedRouteView.setRouteList(getRoutesFromFile(offlineDataFile));
        savedRouteView.setIRouteSave(this);
        savedRouteView.setIRouteDraw(this);

        map.setMapType(GoogleMap.MAP_TYPE_NONE);
        TileOverlayOptions tileOverlay = new TileOverlayOptions();
        tileOverlay.tileProvider(new CustomMapTileProvider());
        map.addTileOverlay(tileOverlay).setZIndex(20);
    }

    private List<Route> getRoutesFromFile(File file) {
        if (file.length() == 0) return null;
        else {
            try{
                return ((SavedRoutesItem) xStream.fromXML(file)).getRoutes();
            }catch(Exception e){
                return (ArrayList<Route>)(xStream.fromXML(file));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void writeRouteToFile(Route route) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for(IEntry entry : route.getEntries()){
            if (entry instanceof Path) {
                Path path = (Path) entry;
                for(PathVertex vertex : path.getVertices()){
                    builder.include(vertex.toLatLong());
                }
            }
        }
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
        new Thread(() -> {
            for(int i = 5;i<20;i++)
            new DownloadMapTiles().downloadTiles(builder.build(),i);
        }).start();
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(offlineDataFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (offlineDataFile.length() == 0) {
            List<Route> routes = new ArrayList<>();
            routes.add(route);
            xStream.toXML(new SavedRoutesItem(routes), fileWriter);
            savedRouteView.setRouteList(routes);
        } else {
            List<Route> routes = getRoutesFromFile(offlineDataFile);
            routes.add(route);
            offlineDataFile.delete();
            try {
                offlineDataFile.createNewFile();
                fileWriter = new FileWriter(offlineDataFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            xStream.toXML(new SavedRoutesItem(routes), fileWriter);
            savedRouteView.setRouteList(getRoutesFromFile(offlineDataFile));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void writeRoutesToFile(List<Route> routes) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(offlineDataFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        offlineDataFile.delete();
        try {
            offlineDataFile.createNewFile();
            fileWriter = new FileWriter(offlineDataFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        xStream.toXML(new SavedRoutesItem(routes), fileWriter);
        savedRouteView.setRouteList(getRoutesFromFile(offlineDataFile));
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void deleteRoute(Route route) {
        List<Route> routesFromFile = getRoutesFromFile(offlineDataFile);
        List<Route> collect = routesFromFile.stream()
                .filter(route1 -> !route1.getRoute_id().equals(route.getRoute_id()))
                .collect(Collectors.toList());
        writeRoutesToFile(collect);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void saveRoute(Route route) {
        writeRouteToFile(route);
    }
}
