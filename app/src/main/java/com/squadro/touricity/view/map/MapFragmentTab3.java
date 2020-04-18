package com.squadro.touricity.view.map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.squadro.touricity.MainActivity;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.progress.ProgressController;
import com.squadro.touricity.view.map.offline.CreateOfflineDataDirectory;
import com.squadro.touricity.view.map.offline.CustomMapTileProvider;
import com.squadro.touricity.view.map.offline.DeleteOfflineDataAsync;
import com.squadro.touricity.view.map.offline.LoadOfflineDataAsync;
import com.squadro.touricity.view.map.offline.WriteOfflineDataAsync;
import com.squadro.touricity.view.map.placesAPI.CustomInfoWindowAdapter;
import com.squadro.touricity.view.map.placesAPI.MapLongClickListener;
import com.squadro.touricity.view.map.placesAPI.MarkerInfo;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.progress.BottomProgressViewer;
import com.squadro.touricity.view.progress.MapProgressView;
import com.squadro.touricity.view.progress.TopProgressView;
import com.squadro.touricity.view.routeList.RouteCardView;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.SavedRoutesItem;
import com.squadro.touricity.view.routeList.event.IRouteDraw;
import com.squadro.touricity.view.routeList.event.IRouteSave;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import lombok.Getter;

public class MapFragmentTab3 extends Fragment implements OnMapReadyCallback, IRouteDraw, IRouteSave {

	private SupportMapFragment supportMapFragment;
	public static BottomSheetBehavior bottomSheetBehavior;
	private FrameLayout frameLayout;
	@Getter
	private static GoogleMap map;
	private MapLongClickListener mapLongClickListener = null;
	private File offlineDataFile;
	@Getter
	private static SavedRouteView savedRouteView;
	private XStream xStream;
	public static List<MyPlace> responsePlaces = new ArrayList<>();
	public static List<MarkerInfo> markerInfoList = new ArrayList<>();
	private static ConnectivityManager connectivityManager;
	public static SavedRoutesItem savedRoutesItem;
	public static View rootView;
	public static List<Route> routes;

	private PolylineDrawer polylineDrawer;

	ProgressController progressController;
	BottomProgressViewer bottomProgressViewer;
	TopProgressView topProgressView;

	public static void progressDone(ProgressBar progressBar, AtomicInteger count) {
		if(count.get() == 2) progressBar.setVisibility(View.INVISIBLE);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tab3_map_view, container, false);
		if (supportMapFragment == null) {
			supportMapFragment = SupportMapFragment.newInstance();
			supportMapFragment.getMapAsync(this);
		}
		getChildFragmentManager().beginTransaction().replace(R.id.tab3_map, supportMapFragment).commit();

		return rootView;
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void onPause() {
		super.onPause();
		routes = savedRouteView.getRouteList();
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		map.setMaxZoomPreference(18);
		frameLayout = (FrameLayout) getActivity().findViewById(R.id.tab3_map);

		polylineDrawer = new PolylineDrawer(map, "saved",getContext());
		initializeSheetbehavior(googleMap);
		map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));
		xStream = new XStream();
		offlineDataFile = new CreateOfflineDataDirectory().offlineRouteFile(getContext());
		savedRouteView = getActivity().findViewById(R.id.route_save);
		bottomProgressViewer = getActivity().findViewById(R.id.route_progress);
		((CoordinatorLayout) rootView).removeView(bottomProgressViewer);

		savedRouteView.setIRouteSave(this);
		savedRouteView.setIRouteDraw(this);
		connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		if(routes == null){
			LoadOfflineDataAsync loadOfflineDataAsync = new LoadOfflineDataAsync(savedRouteView, offlineDataFile, getContext());
			loadOfflineDataAsync.execute();
		}else if(routes.size() > 0){
			savedRouteView.setRouteList(routes,responsePlaces);
		}
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
		polylineDrawer.drawRoute(route);
		map.setMinZoomPreference(5);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void deleteRoute(Route route) {
		List<Route> routes = savedRoutesItem.getRoutes();
		for (int i = 0; i < routes.size(); i++) {
			Route route1 = routes.get(i);
			if (route1.getRoute_id().equals(route.getRoute_id())) {
				routes.remove(route1);
			}
		}
		savedRoutesItem.setRoutes(routes);
		if(routes.size() == 0){
			offlineDataFile.delete();
			try {
				offlineDataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			DeleteOfflineDataAsync deleteOfflineDataAsync = new DeleteOfflineDataAsync(offlineDataFile);
			deleteOfflineDataAsync.execute(savedRoutesItem);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void startProgress(Route route) {
		progressController = new ProgressController(route, responsePlaces, null);

		MapProgressView mapProgressViewer = new MapProgressView(map);
		mapProgressViewer.setCustomPositionUpdateListener(progressController);

		bottomProgressViewer.setRoute(route);
		bottomProgressViewer.setSavedTabListeners(this);

		CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;

		topProgressView = (TopProgressView) getLayoutInflater().inflate(R.layout.top_progress_view, null);
		frameLayout.addView(topProgressView);
		coordinatorLayout.removeView(savedRouteView);
		coordinatorLayout.addView(bottomProgressViewer);

		progressController.clearProgressEventListeners();
		progressController.addProgressEventListener(mapProgressViewer);
		progressController.addProgressEventListener(bottomProgressViewer);
		progressController.addProgressEventListener(topProgressView);

	}

	@Override
	public void endProgress() {
		progressController.onEndProgress();
		frameLayout.removeView(topProgressView);
		CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;

		coordinatorLayout.removeView(bottomProgressViewer);
		coordinatorLayout.addView(savedRouteView);


	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void saveRoute(Route route) {
		RouteCardView routeCardView = savedRouteView.addRoute(route);
		WriteOfflineDataAsync writeOfflineDataAsync = new WriteOfflineDataAsync(getActivity(), offlineDataFile, routeCardView);
		writeOfflineDataAsync.execute(route);
	}


	@RequiresApi(api = Build.VERSION_CODES.N)
	public static boolean isPlaceExist(MyPlace myPlace) {
		return responsePlaces.stream().filter(myPlace1 -> myPlace.getPlace_id().equals(myPlace1.getPlace_id()))
				.collect(Collectors.toList()).size() > 0;
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	public static MyPlace getPlace(Stop stop) {
		AtomicReference<MyPlace> answer = new AtomicReference<>();
		responsePlaces.forEach(myPlace -> {if(myPlace.getPlace_id().equals(stop.getLocation().getLocation_id())) answer.set(myPlace);} );
		return answer.get();
	}
}
