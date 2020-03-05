package com.squadro.touricity.view.filter;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.squadro.touricity.R;
import com.squadro.touricity.requests.FilterRequests;
import com.squadro.touricity.view.routeList.RouteExploreView;
import com.squadro.touricity.view.search.SearchBar;

import lombok.Getter;

public class FilterHandler {

    @Getter
    private Filter filter;


    private SearchBar searchBar = null;
    private MinRatingBar minRatingBar = null;
    private AverageCostSeekBar averageCostSeekBar = null;
    private DurationSeekBar durationSeekBar = null;
    private TransportationCheckBox transportationCheckBox = null;
    private Button filterButton = null;
    private RouteExploreView routeExploreView = null;

    public FilterHandler(Activity activity, SearchBar searchBar, MinRatingBar minRatingBar, AverageCostSeekBar averageCostSeekBar,
                  DurationSeekBar durationSeekBar, TransportationCheckBox transportationCheckBox){

        this.searchBar = searchBar;
        this.minRatingBar = minRatingBar;
        this.averageCostSeekBar = averageCostSeekBar;
        this.durationSeekBar = durationSeekBar;
        this.transportationCheckBox = transportationCheckBox;
        this.routeExploreView = activity.findViewById(R.id.route_explore);
        initializeFilterButton(activity);

    }

    public FilterHandler(String city, double minRate, int averageCost, int duration, int transportation){
        this.filter = new Filter();
        this.filter.setCity_name(city);
        this.filter.setScore(minRate);
        this.filter.setExpense(averageCost);
        this.filter.setDuration(duration);
        this.filter.setPath_type(transportation);
    }

    private void initializeFilterButton(Activity activity) {
        filterButton = activity.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(getOnClickListener());
    }

    private View.OnClickListener getOnClickListener() {
        return view -> {
            this.filter.setCity_name(searchBar.getInputCity());
            this.filter.setScore(minRatingBar.getMinRate());
            this.filter.setExpense(averageCostSeekBar.getAverageCost());
            this.filter.setDuration(durationSeekBar.getDuration());
            this.filter.setPath_type(transportationCheckBox.getTransportation());
            FilterRequests filterRequest = new FilterRequests(this.routeExploreView);
            filterRequest.filter(this.filter);
        };
    }


}
