package com.squadro.touricity.view.map;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.squadro.touricity.MainActivity;
import com.squadro.touricity.R;
import com.squadro.touricity.maths.MapMaths;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.requests.NearByPlaceRequest;
import com.squadro.touricity.requests.RouteRequests;
import com.squadro.touricity.view.map.DirectionsAPI.DirectionPost;
import com.squadro.touricity.view.map.DirectionsAPI.WaypointOrder;
import com.squadro.touricity.view.map.editor.IEditor;
import com.squadro.touricity.view.map.editor.PathEditor;
import com.squadro.touricity.view.map.offline.DownloadMapTiles;
import com.squadro.touricity.view.map.placesAPI.CustomInfoWindowAdapter;
import com.squadro.touricity.view.map.placesAPI.INearByResponse;
import com.squadro.touricity.view.map.placesAPI.MapLongClickListener;
import com.squadro.touricity.view.map.placesAPI.MarkerInfo;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.popupWindowView.PopupWindowParameters;
import com.squadro.touricity.view.routeList.IRouteResponse;
import com.squadro.touricity.view.routeList.RouteCreateView;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.entry.StopCardView;
import com.squadro.touricity.view.routeList.event.IRouteMapViewUpdater;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.Getter;

import static android.app.Activity.RESULT_OK;

public class MapFragmentTab2 extends Fragment implements OnMapReadyCallback, IRouteMapViewUpdater,
        INearByResponse, IRouteResponse, OnStreetViewPanoramaReadyCallback, GoogleMap.OnPolylineClickListener {

    private SupportMapFragment supportMapFragment;
    private MapLongClickListener mapLongClickListener = null;
    public static RouteCreateView routeCreateView;
    private BottomSheetBehavior bottomSheetBehavior;
    public FrameLayout frameLayout;
    @Getter
    private static GoogleMap map;
    private PopupWindowParameters popupWindowParameters;
    public static List<MyPlace> responsePlaces = new ArrayList<>();
    public static PlacesClient placesClient;
    public static List<MarkerInfo> markerInfoList = new ArrayList<>();
    public static List<Marker> markersOfNearby = new ArrayList<>();
    public static Route route;
    public static List<Stop> customStopList = new ArrayList<>();


    private IEditor editor;
    public static StreetViewPanorama streetViewPanorama;
    public static StreetViewPanoramaFragment streetViewPanoramaFragment;
    public static View rootView;
    private AutocompleteSupportFragment autocompleteFragment;

    private PolylineDrawer polylineDrawer;
    private Circle circle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPause() {
        super.onPause();
        route = routeCreateView.getRoute();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab2_map_view, container, false);
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
        if (route != null || MapFragmentTab1.routes != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle("WARNING")
                    .setMessage("Your internet connection was off suddenly. Do you want to recover your previous works?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (route != null && route.getAbstractEntryList().size() > 0) {
                            routeCreateView.setRoute(route);
                            route = null;
                        }
                        if (MapFragmentTab1.routes != null && MapFragmentTab1.routes.size() > 0) {
                            MapFragmentTab1.getRouteExploreView().setRouteList(MapFragmentTab1.routes);
                            MapFragmentTab1.routes = null;
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        route = null;
                        MapFragmentTab1.routes = null;
                    })
                    .show();
        }
        map = googleMap;
        polylineDrawer = new PolylineDrawer(map, "create");
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.tab2_map);
        createRouteCreateView();
        initializeSheetBehaviors();
        initializePlacesAutofill();
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));
        initializeInfoWindowListener();
        initializeStreetView();
        initializeSpeechToText();
        initializeMapListeners();
    }

    private void initializeSpeechToText() {
        Button micButton = rootView.findViewById(R.id.mic_places);
        micButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a place name");
            try {
                startActivityForResult(intent, 100);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result.size() > 0) {
                autocompleteFragment.setText(result.get(0));
            }
        }
    }

    private void initializeStreetView() {
        streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getActivity().getFragmentManager()
                        .findFragmentById(R.id.streetViewMap2);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeInfoWindowListener() {
        map.setOnInfoWindowLongClickListener(marker -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("WARNING")
                    .setMessage("This action will add this place to the route as stop. Do you want to continue?")
                    .setPositiveButton("Yes", (dialog, which) -> {
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
                        if (circle != null) {
                            circle.remove();
                            circle = null;
                        }

                    }).setNegativeButton("No", (dialog, which) -> {
            })
                    .show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializePlacesAutofill() {
        if (!Places.isInitialized()) {
            Places.initialize(this.getContext(), getResources().getString(R.string.api_key));
        }


        if (getActivity() != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            rootView.findViewById(R.id.autoCompleteFragment).setLayoutParams(
                    new FrameLayout.LayoutParams(width - 100, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        placesClient = Places.createClient(getContext());
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autoCompleteFragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.PHOTO_METADATAS, Place.Field.PRICE_LEVEL));

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
                                if (!isPlaceExist(myPlace)) responsePlaces.add(myPlace);
                                Location location = new Location(myPlace.getPlace_id(), myPlace.getLatLng().latitude, myPlace.getLatLng().longitude);
                                Stop stop = new Stop(null, 0, 0, "", location, null);
                                routeCreateView.onInsertStop(stop);
                            }
                        }).addOnFailureListener(Throwable::printStackTrace);
                    }
                } else {
                    MyPlace myPlace = new MyPlace(place, null);
                    if (!isPlaceExist(myPlace)) responsePlaces.add(myPlace);
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
        EditText routeTitle = routeCreateView.findViewById(R.id.routeTitleEditText);
        ColorStateList color = routeTitle.getHintTextColors();
        saveButton.setOnClickListener(v -> {
            int photoCount = 0;
            double photoSize = 0.0;
            for (IEntry entry : route.getAbstractEntryList()) {
                if (entry instanceof Stop) {
                    Stop stop = (Stop) entry;
                    List<MyPlace> responsePlaces = MapFragmentTab2.responsePlaces;
                    for (MyPlace myPlace : responsePlaces) {
                        if (myPlace.getPlace_id().equals(stop.getLocation().getLocation_id())) {
                            List<Bitmap> photos = myPlace.getPhotos();
                            if (photos != null) {
                                for (Bitmap bitmap : photos) {
                                    photoCount++;
                                    photoSize += bitmap.getAllocationByteCount();
                                }
                            }
                        }
                    }
                }
            }
            photoSize = photoSize / 1024 / 1024;
            Map<String, String> tileMap = new DownloadMapTiles().downloadTileBounds(MapMaths.getRouteBoundings(route));
            int numOfTiles = tileMap.size();
            double sizeOfTiles = 28.0 * tileMap.size() / 1024;
            String warning = photoCount != 0 ?
                    "This operation will download at most " + numOfTiles + " map tiles and " + photoCount + " photos for places"+
                            " and need up to " + new DecimalFormat("##.#").format(sizeOfTiles + photoSize) + " mb " +
                            "storage. Do you still want to continue?" :
                    "This operation will download at most " + numOfTiles + " map tiles for offline usage and may need " +
                            "up to " + new DecimalFormat("##.#").format(sizeOfTiles) + " mb storage. Do you still want to continue?";
            new AlertDialog.Builder(getContext())
                    .setTitle("WARNING")
                    .setMessage(warning)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (routeTitle.getText().toString().equals("")) {
                            routeTitle.setHintTextColor(Color.RED);
                            routeTitle.setHint("Please enter a title!");
                        } else {
                            routeTitle.setHintTextColor(color);
                            routeTitle.setHint("Set Route Title");
                            RouteRequests routeRequests = new RouteRequests();
                            Route route = routeCreateView.getRoute();
                            route.setTitle(routeTitle.getText().toString());
                            routeRequests.updateRoute(routeCreateView.getRoute(), this);
                            routeCreateView.setRoute(new Route());
                            routeTitle.getText().clear();
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                    }).show();
        });

        Button optimizeButton = routeCreateView.findViewById(R.id.route_create_optimize);
        optimizeButton.setOnClickListener(view -> {

            if (routeCreateView.getRoute().getAbstractEntryList().size() >= 7) {
                DirectionPost dp = new DirectionPost();
                String url = dp.getOptimizedDirectionsURL(routeCreateView.getRoute(), "driving");
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
        int numberOfButtons = 2;
        List<String> buttonNames = new ArrayList<>();
        buttonNames.add("Nearby");
        buttonNames.add("Custom");
        popupWindowParameters = new PopupWindowParameters(numberOfButtons, buttonNames);
        mapLongClickListener = new MapLongClickListener(map, frameLayout, 0, bottomSheetBehavior.getPeekHeight(), popupWindowParameters);
        createButtonListeners(mapLongClickListener.getButtons());
        initBottomSheetCallback(bottomSheetBehavior, mapLongClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createButtonListeners(List<Button> buttons) {
        Button nearyBy = buttons.get(0);
        nearyBy.setOnClickListener(v -> {
            LatLng latLng = mapLongClickListener.getLatLng();
            int radius = 50;
            NearByPlaceRequest nearByPlaceRequest = new NearByPlaceRequest();
            nearByPlaceRequest.findNearbyPlaces(latLng, radius, this);
            mapLongClickListener.dissmissPopUp();
        });
        Button customStop = buttons.get(1);
        customStop.setOnClickListener(v -> {
            mapLongClickListener.dissmissPopUp();
            LatLng latLng = mapLongClickListener.getLatLng();
            Location location = new Location(UUID.randomUUID().toString(), latLng.latitude, latLng.longitude);
            StopCardView customStopCardView = (StopCardView) LayoutInflater.from(getContext()).inflate(R.layout.arbitrary_stop_input, null);
            PopupWindow popupWindow = new PopupWindow(customStopCardView, RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            Point point = map.getProjection().toScreenLocation(latLng);
            popupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.LEFT, point.x, point.y);
            popupWindow.setFocusable(true);
            popupWindow.update();
            EditText title = customStopCardView.findViewById(R.id.stop_name);
            EditText desc = customStopCardView.findViewById(R.id.stop_desc);
            Button saveStopButton = customStopCardView.findViewById(R.id.arbitrary_stop_save);
            saveStopButton.setOnClickListener(v1 -> {
                String titleStr = title.getText().toString().isEmpty() ? "No title found!" : title.getText().toString();
                String descStr = desc.getText().toString().isEmpty() ? "No description found!" : desc.getText().toString();
                String comment = "Title:" + titleStr + "Desc:" + descStr;
                Stop stop = new Stop(null, 0, 0, comment, location, null);
                customStopList.add(stop);
                popupWindow.dismiss();
                routeCreateView.onInsertStop(stop);
            });
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void updateRoute(Route route) {
        Log.d("fmap", "Update the route ");
        if (editor == null)
            polylineDrawer.drawRoute(route);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void highlight(AbstractEntry entry) {
        Log.d("fmap", "highligt the entry " + entry.getComment());

        if (entry instanceof Stop) {
            polylineDrawer.drawRoute(routeCreateView.getRoute(), (Stop) entry);
        }

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(MapMaths.getRouteBoundings(routeCreateView.getRoute()), 0));
        disposeEditor();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void focus(AbstractEntry entry) {
        Log.d("fmap", "focus to the entry " + entry.getComment());

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
        initializeMapListeners();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRouteResponse(Route route) {
        SavedRouteView savedRouteView = getActivity().findViewById(R.id.route_save);
        savedRouteView.getIRouteSave().saveRoute(route);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.getTabAt(2).select();
        map.clear();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onPlacesResponse(List<String> placesIds, LatLng latLng) {
        if (placesIds.size() > 0) {
            circle = map.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(50)
                    .strokeColor(Color.RED));
        }
        for (String placeId : placesIds) {
            List<MyPlace> collect = responsePlaces.stream().filter(myPlace -> myPlace.getPlace_id().equals(placeId))
                    .collect(Collectors.toList());
            if (collect.size() > 0) continue;
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                    Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.PHOTO_METADATAS, Place.Field.PRICE_LEVEL);
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
                                if (!isPlaceExist(myPlace)) responsePlaces.add(myPlace);
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
                    if (!isPlaceExist(myPlace)) responsePlaces.add(myPlace);
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

        if (MainActivity.checkConnection()) {
            List<MarkerInfo> collect = markerInfoList.stream()
                    .filter(markerInfo1 -> markerInfo1.getMyPlace().getPlace_id().equals(markerInfo.getMyPlace().getPlace_id()))
                    .collect(Collectors.toList());
            if (collect.size() == 0) markerInfoList.add(markerInfo);
            else collect.get(0).setMarker(markerInfo.getMarker());
        } else {
            List<MarkerInfo> collect = MapFragmentTab3.markerInfoList.stream()
                    .filter(markerInfo1 -> markerInfo1.getMyPlace().getPlace_id().equals(markerInfo.getMyPlace().getPlace_id()))
                    .collect(Collectors.toList());
            if (collect.size() == 0) MapFragmentTab3.markerInfoList.add(markerInfo);
            else collect.get(0).setMarker(markerInfo.getMarker());
        }
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        MapFragmentTab2.streetViewPanorama = streetViewPanorama;
        streetViewPanorama.setOnStreetViewPanoramaChangeListener(streetViewPanoramaChangeListener);
    }

    private StreetViewPanorama.OnStreetViewPanoramaChangeListener streetViewPanoramaChangeListener = streetViewPanoramaLocation -> {
        if (streetViewPanoramaLocation == null || streetViewPanoramaLocation.links == null) {
            MapFragmentTab2.rootView.findViewById(R.id.streetCardViewMap2).setVisibility(View.INVISIBLE);
            new AlertDialog.Builder(getContext())
                    .setTitle("INFO")
                    .setMessage("This location has no street view")
                    .setNeutralButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean isPlaceExist(MyPlace myPlace) {
        return responsePlaces.stream().filter(myPlace1 -> myPlace.getPlace_id().equals(myPlace1.getPlace_id()))
                .collect(Collectors.toList()).size() > 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean isStopExist(Stop stop) {
        return customStopList.stream().filter(stop1 -> stop1.getLocation().getLocation_id().equals(stop.getLocation().getLocation_id()))
                .collect(Collectors.toList()).size() > 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPolylineClick(Polyline polyline) {
        Path path = polylineDrawer.findPath(polyline);
        if (path != null)
            focus(path);
        else
            System.out.println(0);
    }

    private void initializeMapListeners() {
        map.setOnPolylineClickListener(this);
    }
}
