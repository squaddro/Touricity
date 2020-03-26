package com.squadro.touricity.view.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.map.offline.CreateOfflineDataDirectory;
import com.squadro.touricity.view.map.offline.CustomMapTileProvider;
import com.squadro.touricity.view.map.offline.DownloadMapTiles;
import com.squadro.touricity.view.map.offline.LoadOfflineDataAsync;
import com.squadro.touricity.view.map.placesAPI.CustomInfoWindowAdapter;
import com.squadro.touricity.view.map.placesAPI.MapLongClickListener;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.MyPlaceSave;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.SavedRoutesItem;
import com.squadro.touricity.view.routeList.event.IRouteDraw;
import com.squadro.touricity.view.routeList.event.IRouteSave;
import com.thoughtworks.xstream.XStream;

import java.io.ByteArrayOutputStream;
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
        LoadOfflineDataAsync loadOfflineDataAsync = new LoadOfflineDataAsync(savedRouteView,offlineDataFile);
        loadOfflineDataAsync.execute();

        savedRouteView.setIRouteSave(this);
        savedRouteView.setIRouteDraw(this);

        if (!checkConnection()) {
            map.setMapType(GoogleMap.MAP_TYPE_NONE);
            TileOverlayOptions tileOverlay = new TileOverlayOptions();
            tileOverlay.tileProvider(new CustomMapTileProvider());
            tileOverlay.zIndex(0);
            map.addTileOverlay(tileOverlay);
        }
    }

    private List<Route> getRoutesFromFile(File file) {
        if (file.length() == 0) return null;
        else {
            try {
                return ((SavedRoutesItem) xStream.fromXML(file)).getRoutes();
            } catch (Exception e) {
                return (ArrayList<Route>) (xStream.fromXML(file));
            }
        }
    }

    private List<MyPlaceSave> getPlacesFromFile(File file) {
        if (file.length() == 0) return null;
        else {
            try {
                return ((SavedRoutesItem) xStream.fromXML(file)).getMyPlaces();
            } catch (Exception e) {
                e.printStackTrace();
                return (ArrayList<MyPlaceSave>) (xStream.fromXML(file));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void writeRouteToFile(Route route) {
        if (checkConnection()) {
            DownloadMapTiles downloadMapTiles = new DownloadMapTiles();
            new Thread(() -> {
                downloadMapTiles.downloadTileBounds(MapMaths.getRouteBoundings(route));
            }).start();
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(offlineDataFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (offlineDataFile.length() == 0) {
            List<Route> routes = new ArrayList<>();
            routes.add(route);
            List<MyPlace> places = new ArrayList<>();
            for (IEntry entry : route.getAbstractEntryList()) {
                if (entry instanceof Stop) {
                    Stop stop = (Stop) entry;
                    places.addAll(MapFragmentTab2.responsePlaces.stream()
                            .filter(myPlace -> myPlace.getPlace_id().equals(stop.getLocation().getLocation_id()))
                            .collect(Collectors.toList()));
                }
            }

            List<MyPlaceSave> savePlaces = new ArrayList<>();
            for (MyPlace myPlace : places) {
                List<byte[]> bytes = new ArrayList<>();
                for (Bitmap bitmap : myPlace.getPhotos()) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    bytes.add(byteArray);
                }
                savePlaces.add(new MyPlaceSave(myPlace, bytes));
            }
            xStream.toXML(new SavedRoutesItem(routes, savePlaces), fileWriter);
            savedRouteView.setRouteList(routes, savePlaces);
        } else {
            List<Route> routes = getRoutesFromFile(offlineDataFile);
            List<MyPlaceSave> myPlaces = getPlacesFromFile(offlineDataFile);

            if (routes != null) {
                routes.add(route);
            }

            List<MyPlace> places = new ArrayList<>();
            for (IEntry entry : route.getAbstractEntryList()) {
                if (entry instanceof Stop) {
                    Stop stop = (Stop) entry;
                    places.addAll(MapFragmentTab2.responsePlaces.stream()
                            .filter(myPlace -> myPlace.getPlace_id().equals(stop.getLocation().getLocation_id()))
                            .collect(Collectors.toList()));
                }
            }

            List<MyPlaceSave> savePlaces = new ArrayList<>();
            for (MyPlace myPlace : places) {
                List<byte[]> bytes = new ArrayList<>();
                for (Bitmap bitmap : myPlace.getPhotos()) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    bytes.add(byteArray);
                }
                savePlaces.add(new MyPlaceSave(myPlace, bytes));
            }
            if (myPlaces != null) {
                myPlaces.addAll(savePlaces);
            }

            offlineDataFile.delete();
            try {
                offlineDataFile.createNewFile();
                fileWriter = new FileWriter(offlineDataFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            xStream.toXML(new SavedRoutesItem(routes, myPlaces), fileWriter);
            savedRouteView.setRouteList(getRoutesFromFile(offlineDataFile), getPlacesFromFile(offlineDataFile));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void writeRoutesToFile(List<Route> routes) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(offlineDataFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<MyPlace> places = new ArrayList<>();
        for (Route route : routes) {
            for (IEntry entry : route.getAbstractEntryList()) {
                if (entry instanceof Stop) {
                    Stop stop = (Stop) entry;
                    places.addAll(MapFragmentTab2.responsePlaces.stream()
                            .filter(myPlace -> myPlace.getPlace_id().equals(stop.getLocation().getLocation_id()))
                            .collect(Collectors.toList()));
                }
            }
        }
        List<MyPlaceSave> savePlaces = new ArrayList<>();
        for (MyPlace myPlace : places) {
            List<byte[]> bytes = new ArrayList<>();
            for (Bitmap bitmap : myPlace.getPhotos()) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                bytes.add(byteArray);
            }
            savePlaces.add(new MyPlaceSave(myPlace, bytes));
        }
        offlineDataFile.delete();
        try {
            offlineDataFile.createNewFile();
            fileWriter = new FileWriter(offlineDataFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        xStream.toXML(new SavedRoutesItem(routes, savePlaces), fileWriter);
        savedRouteView.setRouteList(getRoutesFromFile(offlineDataFile), getPlacesFromFile(offlineDataFile));
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
        List<Route> routesFromFile = getRoutesFromFile(offlineDataFile);
        List<Route> collect = routesFromFile.stream()
                .filter(route1 -> !route1.getRoute_id().equals(route.getRoute_id()))
                .collect(Collectors.toList());
        writeRoutesToFile(collect);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void saveRoute(Route route) {
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Please wait...");
        new Thread() {
            public void run() {
                try {
                    writeRouteToFile(route);
                    progressDialog.dismiss();
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
                // dismiss the progress dialog
            }
        }.start();
    }

    private boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else return false;
    }
}
