package com.squadro.touricity.view.routeList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.routeList.entry.PathCardView;
import com.squadro.touricity.view.routeList.entry.StopCardView;
import com.squadro.touricity.view.routeList.event.IEntryButtonEventsListener;
import com.squadro.touricity.view.routeList.event.IRouteInsertListener;
import com.squadro.touricity.view.routeList.event.IRouteMapViewUpdater;
import com.squadro.touricity.view.routeList.event.IRouteUpdateEventListener;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@RequiresApi(api = Build.VERSION_CODES.M)
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

        Context context = getContext();

        for (IEntry entry : route.getAbstractEntryList()) {
            if (entry instanceof Stop) {
                Stop stop = (Stop) entry;
                StopCardView cardView = (StopCardView) LayoutInflater.from(context).inflate(R.layout.stopcardview, null);
                cardView.setViewId("create");
                cardView.update(stop);
                cardView.setEntryEventListener(this);
                entryList.addView(cardView);
            } else if (entry instanceof Path) {
                Path path = (Path) entry;
                PathCardView cardView = (PathCardView) LayoutInflater.from(context).inflate(R.layout.path_card_view, null);
                cardView.setViewId("create");
                cardView.update(path);
                cardView.setEntryEventListener(this);
                entryList.addView(cardView);
            }
        }
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
        findViewById(R.id.route_create_add_path).setOnClickListener(view -> createPath());
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
        scrollToEntry(path);

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
        Log.d("fcreate", "Edit " + entry.getComment());
        if (routeMapViewUpdater != null)
            routeMapViewUpdater.focus(entry);


        MapFragmentTab2 routeMapViewUpdater = (MapFragmentTab2) this.routeMapViewUpdater;
        Activity activity = routeMapViewUpdater.getActivity();
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT);

        ((CoordinatorLayout) activity.findViewById(R.id.tab2_map_view)).removeViewAt(2);

        if (entry instanceof Stop) {
            editStopView(entry, routeMapViewUpdater, activity, layoutParams);
        } else if (entry instanceof Path) {
            editPathView(entry, routeMapViewUpdater, activity, layoutParams);
        }
    }

    private void editPathView(AbstractEntry entry, MapFragmentTab2 routeMapViewUpdater, Activity activity, CoordinatorLayout.LayoutParams layoutParams) {
        View inflate = LayoutInflater.from(routeMapViewUpdater.getContext()).inflate(R.layout.path_edit_view, null);
        ((CoordinatorLayout) activity.findViewById(R.id.tab2_map_view)).addView(inflate, layoutParams);
        Button button = inflate.findViewById(R.id.path_edit_save);

        Button cancelButton = inflate.findViewById(R.id.path_edit_cancel);

        cancelButton.setOnClickListener(v -> {
            ((CoordinatorLayout) routeMapViewUpdater.getActivity().findViewById(R.id.tab2_map_view)).removeViewAt(2);
            ((CoordinatorLayout) routeMapViewUpdater.getActivity().findViewById(R.id.tab2_map_view)).addView(routeMapViewUpdater.routeCreateView);
            routeMapViewUpdater.disposeEditor();
            return;
        });
        button.setOnClickListener(v1 -> {
            Editable commentText = ((EditText) inflate.findViewById(R.id.path_edit_comment_text)).getText();
            if (!commentText.toString().isEmpty()) {
                entry.setComment(commentText.toString());
            } else {
                entry.setComment("No comment has been entered..");
            }

            Editable durationText = ((EditText) inflate.findViewById(R.id.path_edit_duration_text)).getText();
            if (!durationText.toString().isEmpty()) {
                entry.setDuration(Integer.parseInt(durationText.toString()));
            } else {
                entry.setDuration(0);
            }

            Editable expenseText = ((EditText) inflate.findViewById(R.id.path_edit_expense_text)).getText();
            if (!expenseText.toString().isEmpty()) {
                entry.setExpense(Integer.parseInt(expenseText.toString()));
            } else {
                entry.setExpense(0);
            }

            RelativeLayout relativeLayout = inflate.findViewById(R.id.transportationCheckboxes);
            for (int i = 0; i < relativeLayout.getChildCount(); i++) {
                View childAt = relativeLayout.getChildAt(i);
                if (childAt instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) childAt;
                    if (checkBox.isChecked()) {
                        ((Path) entry).setPath_type(Path.PathType.values()[i - 1]);
                        break;
                    }
                }
            }
            onPathUpdate((Path) entry);
            ((CoordinatorLayout) routeMapViewUpdater.getActivity().findViewById(R.id.tab2_map_view)).removeViewAt(2);
            ((CoordinatorLayout) routeMapViewUpdater.getActivity().findViewById(R.id.tab2_map_view)).addView(routeMapViewUpdater.routeCreateView);

        });
    }

    private void editStopView(AbstractEntry entry, MapFragmentTab2 routeMapViewUpdater, Activity activity, CoordinatorLayout.LayoutParams layoutParams) {
        View inflate = LayoutInflater.from(routeMapViewUpdater.getContext()).inflate(R.layout.stop_edit_view, null);
        ((CoordinatorLayout) activity.findViewById(R.id.tab2_map_view)).addView(inflate, layoutParams);
        Button saveButton = inflate.findViewById(R.id.stop_edit_save);
        Button cancelButton = inflate.findViewById(R.id.stop_edit_cancel);

        cancelButton.setOnClickListener(v -> {
            ((CoordinatorLayout) routeMapViewUpdater.getActivity().findViewById(R.id.tab2_map_view)).removeViewAt(2);
            ((CoordinatorLayout) routeMapViewUpdater.getActivity().findViewById(R.id.tab2_map_view)).addView(routeMapViewUpdater.routeCreateView);
            return;
        });
        saveButton.setOnClickListener(v1 -> {
            Editable commentText = ((EditText) inflate.findViewById(R.id.stop_edit_comment_text)).getText();
            if (!commentText.toString().isEmpty()) {
                entry.setComment(commentText.toString());
            } else {
                entry.setComment("No comment has been entered..");
            }

            Editable durationText = ((EditText) inflate.findViewById(R.id.stop_edit_duration_text)).getText();
            if (!durationText.toString().isEmpty()) {
                entry.setDuration(Integer.parseInt(durationText.toString()));
            } else {
                entry.setDuration(0);
            }

            Editable expenseText = ((EditText) inflate.findViewById(R.id.stop_edit_expense_text)).getText();
            if (!expenseText.toString().isEmpty()) {
                entry.setExpense(Integer.parseInt(expenseText.toString()));
            } else {
                entry.setExpense(0);
            }

            onStopUpdate((Stop) entry);
            ((CoordinatorLayout) routeMapViewUpdater.getActivity().findViewById(R.id.tab2_map_view)).removeViewAt(2);
            ((CoordinatorLayout) routeMapViewUpdater.getActivity().findViewById(R.id.tab2_map_view)).addView(routeMapViewUpdater.routeCreateView);

        });
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

        route.addEntry(stop);

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
        route.addEntry(stop);
        UpdateView();
    }
}
