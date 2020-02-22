package com.squadro.touricity.view.filter;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;

import com.squadro.touricity.R;
import com.squadro.touricity.requests.FilterRequests;
import com.squadro.touricity.view.routeList.RouteExploreView;
import com.squadro.touricity.view.search.SearchBar;

import lombok.Getter;

public class Filter {

    @Getter
    private String city = "";
    @Getter
    private double minRate = 0.0;
    @Getter
    private int averageCost = 0;
    @Getter
    private int duration = 0;
    @Getter
    private int transportation = 0;


    private SearchBar searchBar = null;
    private MinRatingBar minRatingBar = null;
    private AverageCostSeekBar averageCostSeekBar = null;
    private DurationSeekBar durationSeekBar = null;
    private TransportationCheckBox transportationCheckBox = null;
    private Button filterButton = null;
    private RouteExploreView routeExploreView = null;

    public Filter(Activity activity, SearchBar searchBar, MinRatingBar minRatingBar, AverageCostSeekBar averageCostSeekBar,
                  DurationSeekBar durationSeekBar, TransportationCheckBox transportationCheckBox){

        this.searchBar = searchBar;
        this.minRatingBar = minRatingBar;
        this.averageCostSeekBar = averageCostSeekBar;
        this.durationSeekBar = durationSeekBar;
        this.transportationCheckBox = transportationCheckBox;
        this.routeExploreView = activity.findViewById(R.id.route_explore);
        initializeFilterButton(activity);

    }

    public Filter(String city, double minRate, int averageCost, int duration, int transportation){
        this.city = city;
        this.minRate = minRate;
        this.averageCost = averageCost;
        this.duration = duration;
        this.transportation = transportation;
    }

    private void initializeFilterButton(Activity activity) {
        filterButton = activity.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(getOnClickListener());
    }

    private View.OnClickListener getOnClickListener() {
        return view -> {
            this.city = searchBar.getInputCity();
            this.minRate = minRatingBar.getMinRate();
            this.averageCost = averageCostSeekBar.getAverageCost();
            this.duration = durationSeekBar.getDuration();
            this.transportation = transportationCheckBox.getTransportation();
            FilterRequests filterRequest = new FilterRequests(this.routeExploreView);
            filterRequest.filter(new Filter(city, minRate, averageCost, duration, transportation));
        };
    }


}
