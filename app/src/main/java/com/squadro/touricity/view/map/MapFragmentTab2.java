package com.squadro.touricity.view.map;

import android.graphics.Bitmap;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.requests.NearByPlaceRequest;
import com.squadro.touricity.requests.RouteRequests;
import com.squadro.touricity.view.map.DirectionsAPI.DirectionPost;
import com.squadro.touricity.view.map.DirectionsAPI.WaypointOrder;
import com.squadro.touricity.view.map.editor.IEditor;
import com.squadro.touricity.view.map.editor.PathEditor;
import com.squadro.touricity.view.map.placesAPI.CustomInfoWindowAdapter;
import com.squadro.touricity.view.map.placesAPI.INearByResponse;
import com.squadro.touricity.view.map.placesAPI.MapLongClickListener;
import com.squadro.touricity.view.map.placesAPI.MarkerInfo;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.popupWindowView.PopupWindowParameters;
import com.squadro.touricity.view.routeList.IRouteResponse;
import com.squadro.touricity.view.routeList.RouteCreateView;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.event.IRouteMapViewUpdater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.Getter;

public class MapFragmentTab2 extends Fragment implements OnMapReadyCallback, IRouteMapViewUpdater,
        INearByResponse, IRouteResponse {

    private SupportMapFragment supportMapFragment;
    private MapLongClickListener mapLongClickListener = null;
    public static RouteCreateView routeCreateView;
    private BottomSheetBehavior bottomSheetBehavior;
    public FrameLayout frameLayout;
    @Getter
    private static GoogleMap map;
    private PopupWindowParameters popupWindowParameters;
    public static List<MyPlace> responsePlaces;
    public static PlacesClient placesClient;
    public static List<MarkerInfo> markerInfoList;
    public static List<Marker> markersOfNearby;


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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.tab2_map);

        LatLng tobb = new LatLng(10, 10);
        googleMap.addMarker(new MarkerOptions().position(tobb).title("tobb"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(tobb));
        markerInfoList = new ArrayList<>();
        markersOfNearby = new ArrayList<>();
        createRouteCreateView();
        initializeSheetBehaviors();
        initializePlacesAutofill();
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));
        initializeInfoWindowListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeInfoWindowListener() {
        map.setOnInfoWindowLongClickListener(marker -> {
            List<MarkerInfo> collect = markerInfoList.stream()
                    .filter(markerInfo -> markerInfo.getMarker().getId().equals(marker.getId()))
                    .collect(Collectors.toList());
            if (collect.size() > 0) {
                if (!collect.get(0).getIsNearby()) return;
                MyPlace myPlace = collect.get(0).getMyPlace();
                Location location = new Location(myPlace.getPlace_id(), myPlace.getLatLng().latitude, myPlace.getLatLng().longitude);
                Stop stop = new Stop(null, 0, 0, "", location, null);
                MapFragmentTab2.getRouteCreateView().onInsertStop(stop);
                for (Marker m : MapFragmentTab2.markersOfNearby) {
                    m.remove();
                }
                MapFragmentTab2.markersOfNearby.clear();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializePlacesAutofill() {
        if (!Places.isInitialized()) {
            Places.initialize(this.getContext(), getResources().getString(R.string.api_key));
            responsePlaces = new ArrayList<>();
        }

        placesClient = Places.createClient(getContext());
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autoCompleteFragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.PHOTO_METADATAS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                List<PhotoMetadata> photoMetadatas = place.getPhotoMetadatas();
                List<Bitmap> photos = new ArrayList<>();
                if (photoMetadatas != null) {
                    AtomicInteger atomicInteger = new AtomicInteger(0);
                    for (; atomicInteger.get() < photoMetadatas.size(); atomicInteger.incrementAndGet()) {
                        PhotoMetadata metadata = photoMetadatas.get(atomicInteger.get());
                        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(metadata)
                                .setMaxWidth(1024) // Optional.
                                .setMaxHeight(720) // Optional.
                                .build();
                        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(fetchPhotoResponse -> {
                            Bitmap bitmap = fetchPhotoResponse.getBitmap();
                            photos.add(bitmap);
                            if (photos.size() == photoMetadatas.size()) {
                                MyPlace myPlace = new MyPlace(place, photos);
                                responsePlaces.add(myPlace);
                                Location location = new Location(myPlace.getPlace_id(), myPlace.getLatLng().latitude, myPlace.getLatLng().longitude);
                                Stop stop = new Stop(null, 0, 0, "", location, null);
                                routeCreateView.onInsertStop(stop);
                            }
                        }).addOnFailureListener(Throwable::printStackTrace);
                    }
                } else {
                    MyPlace myPlace = new MyPlace(place, null);
                    responsePlaces.add(myPlace);
                    Location location = new Location(myPlace.getPlace_id(), myPlace.getLatLng().latitude, myPlace.getLatLng().longitude);
                    Stop stop = new Stop(null, 0, 0, "", location, null);
                    routeCreateView.onInsertStop(stop);
                }
            }

            @Override
            public void onError(Status status) {

            }
        });

    }

    public static RouteCreateView getRouteCreateView() {
        return routeCreateView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createRouteCreateView() {
        routeCreateView = getActivity().findViewById(R.id.route_create);
        routeCreateView.setRoute(new Route());
        routeCreateView.setRouteMapViewUpdater(this);
        Button saveButton = routeCreateView.findViewById(R.id.route_create_save);
        saveButton.setOnClickListener(v -> {
            RouteRequests routeRequests = new RouteRequests();
            routeRequests.updateRoute(routeCreateView.getRoute(), this);
            routeCreateView.CleanView();
        });

        Button optimizeButton = routeCreateView.findViewById(R.id.route_create_optimize);
        optimizeButton.setOnClickListener(view -> {

            if(routeCreateView.getRoute().getAbstractEntryList().size() >= 7){
                DirectionPost dp = new DirectionPost();
                String url = dp.getOptimizedDirectionsURL(routeCreateView.getRoute(),"driving");
                WaypointOrder wp = new WaypointOrder(url, routeCreateView);
            }

        });
    }

    public MapLongClickListener getMapLongClickListener() {
        return mapLongClickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeSheetBehaviors() {
        bottomSheetBehavior = BottomSheetBehavior.from(getActivity().findViewById(R.id.route_create));
        int numberOfButtons = 1;
        List<String> buttonNames = new ArrayList<>();
        buttonNames.add("Find nearby places");
        popupWindowParameters = new PopupWindowParameters(numberOfButtons, buttonNames);
        mapLongClickListener = new MapLongClickListener(map, frameLayout, 0, bottomSheetBehavior.getPeekHeight(), popupWindowParameters);
        createButtonListeners(mapLongClickListener.getButtons());
        initBottomSheetCallback(bottomSheetBehavior, mapLongClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createButtonListeners(List<Button> buttons) {
        Button button = buttons.get(0);
        button.setOnClickListener(v -> {
            LatLng latLng = mapLongClickListener.getLatLng();
            Location location = new Location("sample_id", latLng.latitude, latLng.longitude);

            int radius = 50;
            NearByPlaceRequest nearByPlaceRequest = new NearByPlaceRequest();
            nearByPlaceRequest.findNearbyPlaces(latLng, radius, this);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void highlight(AbstractEntry entry) {
        Log.d("fmap", "highligt the entry " + entry.getComment());
        PolylineDrawer polylineDrawer = new PolylineDrawer(map, "create");

        if (entry instanceof Stop) {
            polylineDrawer.drawRoute(routeCreateView.getRoute(), (Stop) entry);
        }

        //map.animateCamera(CameraUpdateFactory.newLatLngBounds(MapMaths.getRouteBoundings(routeCreateView.getRoute()), 0));
        disposeEditor();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void focus(AbstractEntry entry) {
        Log.d("fmap", "focus to the entry " + entry.getComment());

        PolylineDrawer polylineDrawer = new PolylineDrawer(map, "create");

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void disposeEditor() {
        if (editor != null) {
            editor.dispose();
            mapLongClickListener = new MapLongClickListener(map, frameLayout, 0, bottomSheetBehavior.getPeekHeight(), popupWindowParameters);
            createButtonListeners(mapLongClickListener.getButtons());
            editor = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRouteResponse(Route route) {
        SavedRouteView savedRouteView = getActivity().findViewById(R.id.route_save);
        savedRouteView.getIRouteSave().saveRoute(route);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.getTabAt(2).select();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onPlacesResponse(List<String> placesIds) {
        for (String placeId : placesIds) {
            List<MyPlace> collect = responsePlaces.stream().filter(myPlace -> myPlace.getPlace_id().equals(placeId))
                    .collect(Collectors.toList());
            if (collect.size() > 0) continue;
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                    Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.PHOTO_METADATAS);
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, fields);
            MapFragmentTab2.placesClient.fetchPlace(request).addOnSuccessListener((placeResponse) -> {
                Place place = placeResponse.getPlace();
                List<PhotoMetadata> photoMetadata = place.getPhotoMetadatas();
                List<Bitmap> photos = new ArrayList<>();
                if (photoMetadata != null) {
                    AtomicInteger atomicInteger = new AtomicInteger(0);
                    for (; atomicInteger.get() < photoMetadata.size(); atomicInteger.incrementAndGet()) {
                        PhotoMetadata metadata = photoMetadata.get(atomicInteger.get());
                        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(metadata)
                                .setMaxWidth(1024) // Optional.
                                .setMaxHeight(720) // Optional.
                                .build();
                        MapFragmentTab2.placesClient.fetchPhoto(photoRequest).addOnSuccessListener(fetchPhotoResponse -> {
                            Bitmap bitmap = fetchPhotoResponse.getBitmap();
                            photos.add(bitmap);
                            if (photos.size() == photoMetadata.size()) {
                                MyPlace myPlace = new MyPlace(place, photos);
                                MapFragmentTab2.responsePlaces.add(myPlace);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(myPlace.getLatLng());
                                Marker marker = map.addMarker(markerOptions);
                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                updateMarkerInfo(new MarkerInfo(marker, myPlace, true));
                                markersOfNearby.add(marker);
                            }
                        }).addOnFailureListener(Throwable::printStackTrace);
                    }
                } else {
                    MyPlace myPlace = new MyPlace(place, null);
                    MapFragmentTab2.responsePlaces.add(myPlace);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(myPlace.getLatLng());
                    Marker marker = map.addMarker(markerOptions);
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    updateMarkerInfo(new MarkerInfo(marker, myPlace, true));
                    markersOfNearby.add(marker);
                }
            }).addOnFailureListener(Throwable::printStackTrace);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void updateMarkerInfo(MarkerInfo markerInfo) {

        if (MapFragmentTab3.checkConnection()) {
            List<MarkerInfo> collect = markerInfoList.stream()
                    .filter(markerInfo1 -> markerInfo1.getMyPlace().getPlace_id().equals(markerInfo.getMyPlace().getPlace_id()))
                    .collect(Collectors.toList());
            if (collect.size() == 0) markerInfoList.add(markerInfo);
            else collect.get(0).setMarker(markerInfo.getMarker());
        }else{
            List<MarkerInfo> collect = MapFragmentTab3.markerInfoList.stream()
                    .filter(markerInfo1 -> markerInfo1.getMyPlace().getPlace_id().equals(markerInfo.getMyPlace().getPlace_id()))
                    .collect(Collectors.toList());
            if (collect.size() == 0) MapFragmentTab3.markerInfoList.add(markerInfo);
            else collect.get(0).setMarker(markerInfo.getMarker());
        }

    }


}
