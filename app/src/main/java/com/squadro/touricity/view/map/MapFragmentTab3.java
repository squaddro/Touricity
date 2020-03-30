package com.squadro.touricity.view.map;

import android.content.Context;
import android.net.ConnectivityManager;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.squadro.touricity.MainActivity;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.map.offline.CreateOfflineDataDirectory;
import com.squadro.touricity.view.map.offline.CustomMapTileProvider;
import com.squadro.touricity.view.map.offline.LoadOfflineDataAsync;
import com.squadro.touricity.view.map.offline.WriteOfflineDataAsync;
import com.squadro.touricity.view.map.placesAPI.CustomInfoWindowAdapter;
import com.squadro.touricity.view.map.placesAPI.MapLongClickListener;
import com.squadro.touricity.view.map.placesAPI.MarkerInfo;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.event.IRouteDraw;
import com.squadro.touricity.view.routeList.event.IRouteSave;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    public static List<MyPlace> responsePlaces = new ArrayList<>();
    public static List<MarkerInfo> markerInfoList  = new ArrayList<>();
    private static ConnectivityManager connectivityManager;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMaxZoomPreference(18);
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.tab3_map);

        initializeSheetbehavior(googleMap);
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));
        xStream = new XStream();
        offlineDataFile = new CreateOfflineDataDirectory().offlineRouteFile(getContext());
        savedRouteView = getActivity().findViewById(R.id.route_save);
        LoadOfflineDataAsync loadOfflineDataAsync = new LoadOfflineDataAsync(savedRouteView, offlineDataFile,false,null,getContext());
        loadOfflineDataAsync.execute();

        savedRouteView.setIRouteSave(this);
        savedRouteView.setIRouteDraw(this);
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!MainActivity.checkConnection()) {
            map.setMapType(GoogleMap.MAP_TYPE_NONE);
            TileOverlayOptions tileOverlay = new TileOverlayOptions();
            tileOverlay.tileProvider(new CustomMapTileProvider());
            tileOverlay.zIndex(0);
            map.addTileOverlay(tileOverlay);
        }
    }

    private void initializeSheetbehavior(GoogleMap googleMap) {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(getActivity().findViewById(R.id.route_save));
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void drawHighlighted(Route route) {
        PolylineDrawer polylineDrawer = new PolylineDrawer(map, "saved");
        polylineDrawer.drawRoute(route);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(MapMaths.getRouteBoundings(route), 0));
        map.setMinZoomPreference(map.getCameraPosition().zoom);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void deleteRoute(Route route) {
        LoadOfflineDataAsync loadOfflineDataAsync = new LoadOfflineDataAsync(savedRouteView,offlineDataFile,true,route,getContext());
        loadOfflineDataAsync.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void saveRoute(Route route) {
        WriteOfflineDataAsync writeOfflineDataAsync = new WriteOfflineDataAsync(getActivity(),offlineDataFile,savedRouteView);
        writeOfflineDataAsync.execute(route);
        Toast.makeText(getContext(),"Routes saved successfully",Toast.LENGTH_LONG);
    }
}
