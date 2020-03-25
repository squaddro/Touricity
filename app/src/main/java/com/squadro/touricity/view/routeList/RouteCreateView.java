package com.squadro.touricity.view.routeList;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.map.DirectionsAPI.DirectionPost;
import com.squadro.touricity.view.map.DirectionsAPI.PointListReturner;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.PolylineDrawer;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.map.placesAPI.StopCardViewHandler;
import com.squadro.touricity.view.routeList.entry.StopCardView;
import com.squadro.touricity.view.routeList.event.IEntryButtonEventsListener;
import com.squadro.touricity.view.routeList.event.IRouteInsertListener;
import com.squadro.touricity.view.routeList.event.IRouteMapViewUpdater;
import com.squadro.touricity.view.routeList.event.IRouteUpdateEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@RequiresApi(api = Build.VERSION_CODES.N)
public class RouteCreateView extends LinearLayout implements IEntryButtonEventsListener, IRouteInsertListener, IRouteUpdateEventListener, ScrollView.OnScrollChangeListener {

    @Getter
    private Route route;

    @Setter
    private IRouteMapViewUpdater routeMapViewUpdater;

    LinearLayout entryList;
    NestedScrollView scrollView;
    private AbstractEntry prevHighlighted;

    public RouteCreateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRoute(Route route) {
        this.route = route;
        UpdateView();
    }

    private void UpdateView() {

        CleanView();
        if(route == null) return;
        Context context = getContext();
        List<MyPlace> placesList = MapFragmentTab2.responsePlaces;
        for (IEntry entry : route.getAbstractEntryList()) {
            if (entry instanceof Stop) {
                Stop stop = (Stop) entry;
                List<MyPlace> collect = placesList.stream().filter(myPlace -> myPlace.getPlace_id().equals(stop.getLocation().getLocation_id()))
                        .collect(Collectors.toList());
                StopCardView cardView = (StopCardView) LayoutInflater.from(context).inflate(R.layout.stopcardview, null);
                if(collect.size() > 0){
                    StopCardViewHandler stopCardViewHandler = new StopCardViewHandler(cardView,collect.get(0),context,"create",stop);
                    cardView = stopCardViewHandler.putViews();
                }
                cardView.setViewId("create");
                cardView.update(stop);
                cardView.setEntryEventListener(this);
                entryList.addView(cardView);
            }
        }
        PolylineDrawer pd = new PolylineDrawer(MapFragmentTab2.getMap());
        pd.drawRoute(route);
    }

    public void CleanView() {
        entryList.removeAllViews();
    }

    private void UpdateRouteInfo() {
        UpdateView();

        if (routeMapViewUpdater != null)
            routeMapViewUpdater.updateRoute(route);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        entryList = findViewById(R.id.route_create_list);
        scrollView = findViewById(R.id.route_create_scroll);

        scrollView.setOnScrollChangeListener(this);
    }

    private void scrollToEntry(AbstractEntry entry) {
        // TODO navigate to requested entry instead scrolling to bottom

        scrollView.smoothScrollTo(0, Integer.MAX_VALUE);
    }

    public Stop createStop() {
        if (route == null)
            return null;

        Stop stop = new Stop(null, 0, 0, "", new Location(), null);
        route.addEntry(stop);

        UpdateView();
        scrollToEntry(stop);

        return stop;
    }

    public Path createPath() {
        if (route == null)
            return null;

        Path path = new Path(null, 0, 0, "", null, Path.PathType.DRIVING, new ArrayList<>());
        route.addEntry(path);

        UpdateView();
        return path;
    }

    @Override
    public void onRemoveEntry(AbstractEntry entry) {
        Log.d("fcreate", "Remove " + entry.getComment());

        route.deleteEntry(entry);

        UpdateRouteInfo();
    }

    @Override
    public void onClickEntry(AbstractEntry entry) {
        Log.d("fcreate", "Clicked " + entry.getComment());
    }

    @Override
    public void onHoldEntry(AbstractEntry entry) {
        Log.d("fcreate", "Hold " + entry.getComment());

    }

    @Override
    public void onEditEntry(AbstractEntry entry) {

    }

    @Override
    public void onMoveEntry(AbstractEntry entry, EDirection direction) {
        Log.d("fcreate", "Move " + direction + " " + entry.getComment());
        List<IEntry> entries = route.getAbstractEntryList();
        int index = Math.min(entries.size() - 1, Math.max(0, entries.indexOf(entry) + (direction == EDirection.DOWN ? 1 : -1)));

        route.changeEntryPosition(entry, index);

        UpdateRouteInfo();
    }

    @Override
    public void onInsertLocation(Location location) {
        Stop stop = new Stop(null, 0, 0, "", location, null);

        route.addEntry(stop,route.getAbstractEntryList().size());

        UpdateRouteInfo();
    }

    @Override
    public void onInsertRoute(Route otherRoute) {

        UpdateRouteInfo();
    }

    @Override
    public void onScrollChange(View view, int x, int i1, int i2, int i3) {
        Rect rect = new Rect();
        scrollView.getHitRect(rect);
        for (int i = 0; i < entryList.getChildCount(); i++) {
            RouteListItem entryView = (RouteListItem) entryList.getChildAt(i);
            if (entryView.getLocalVisibleRect(rect)) {
                if (routeMapViewUpdater != null && (prevHighlighted != entryView.getEntry()))
                    routeMapViewUpdater.highlight(entryView.getEntry());
                prevHighlighted = entryView.getEntry();

                break;
            }
        }
    }

    @Override
    public void onPathUpdate(Path path) {
        IEntry[] entries = getRoute().getEntries();
        for (IEntry entry : entries) {
            if (entry instanceof Path) {
                if (entry == path) {
                    entry.setComment(path.getComment());
                    entry.setDuration(path.getDuration());
                    entry.setExpense(path.getExpense());
                    ((Path) entry).setPath_type(path.getPath_type());
                }
            }
        }
        route.setEntries(entries);
        UpdateView();
    }

    @Override
    public void onStopUpdate(Stop stop) {
        IEntry[] entries = getRoute().getEntries();
        for (IEntry entry : entries) {
            if (entry instanceof Stop) {
                if (entry == stop) {
                    entry.setComment(stop.getComment());
                    entry.setDuration(stop.getDuration());
                    entry.setExpense(stop.getExpense());
                }
            }
        }
        route.setEntries(entries);
        UpdateView();
    }

    public void onInsertStop(Stop stop) {
        List<IEntry> entries = route.getAbstractEntryList();

        if(entries.size() == 0){
            route.addEntry(stop);
            UpdateView();
        }
        else{
            int lastIndex = entries.size()-1;
            Stop prevStop = (Stop) entries.get(lastIndex);

            DirectionPost dp = new DirectionPost();
            String url = dp.getDirectionsURL(prevStop.getLocation().getLatLng(), stop.getLocation().getLatLng(), null, "driving");

            route.addEntry(new Path(null, 0, 0, "", null, Path.PathType.DRIVING, null));

            PointListReturner plr = new PointListReturner(url,this,lastIndex+1);

            route.addEntry(stop);
            UpdateView();
        }

    }
}
