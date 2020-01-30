package com.squadro.touricity.view.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squadro.touricity.R;
import com.squadro.touricity.topSheetBehavior.TopSheetBehavior;
import com.squadro.touricity.view.filter.AverageCostSeekBar;
import com.squadro.touricity.view.filter.DurationSeekBar;
import com.squadro.touricity.view.filter.Filter;
import com.squadro.touricity.view.filter.MinRatingBar;
import com.squadro.touricity.view.filter.TransportationCheckBox;
import com.squadro.touricity.view.search.SearchBar;

public class MapFragmentTab1 extends Fragment implements OnMapReadyCallback {

    SupportMapFragment supportMapFragment;


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
    }

    private void addFilterSearchPanel() {
        LinearLayout linearLayout = getView().findViewById(R.id.filter_search);
        TopSheetBehavior.from(linearLayout).setState(TopSheetBehavior.STATE_COLLAPSED);
    }

}
