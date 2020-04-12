package com.squadro.touricity.view.filter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.squadro.touricity.R;
import com.squadro.touricity.requests.FilterRequests;
import com.squadro.touricity.view.routeList.RouteExploreView;
import com.squadro.touricity.view.search.SearchBar;

import lombok.Getter;

public class FilterHandler {

    private Context context;
    @Getter
    private Filter filter;


    private SearchBar searchBar = null;
    private MinRatingBar minRatingBar = null;
    private CostRatingBar costRatingBar = null;
    private DurationSeekBar durationSeekBar = null;
    private TransportationCheckBox transportationCheckBox = null;
    private Button filterButton = null;
    private RouteExploreView routeExploreView = null;

    public FilterHandler(Activity activity, SearchBar searchBar, MinRatingBar minRatingBar, CostRatingBar costRatingBar,
                         DurationSeekBar durationSeekBar, TransportationCheckBox transportationCheckBox, Context context){

        this.filter = new Filter();
        this.searchBar = searchBar;
        this.minRatingBar = minRatingBar;
        this.costRatingBar = costRatingBar;
        this.durationSeekBar = durationSeekBar;
        this.transportationCheckBox = transportationCheckBox;
        this.routeExploreView = activity.findViewById(R.id.route_explore);
        this.context = context;
        initializeFilterButton(activity);

    }

    public FilterHandler(String city, double minRate, int averageCost, int duration, int transportation,Context context){
        this.filter = new Filter();
        this.filter.setCity_name(city);
        this.filter.setScore(minRate);
        this.filter.setExpense(averageCost);
        this.filter.setDuration(duration);
        this.filter.setPath_type(transportation);
        this.context = context;
    }

    private void initializeFilterButton(Activity activity) {
        filterButton = activity.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(getOnClickListener());
    }

    private View.OnClickListener getOnClickListener() {
        return view -> {
            this.filter.setCity_name(searchBar.getInputCity());
            this.filter.setScore(minRatingBar.getMinRate());
            this.filter.setExpense(costRatingBar.getAverageCost());
            this.filter.setDuration(durationSeekBar.getDuration());
            this.filter.setPath_type(transportationCheckBox.getTransportation());
            FilterRequests filterRequest = new FilterRequests(this.routeExploreView,context);
            filterRequest.filter(this.filter);
        };
    }


}
