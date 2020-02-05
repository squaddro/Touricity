package com.squadro.touricity.view.routeList;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.view.routeList.entry.PathCardView;
import com.squadro.touricity.view.routeList.entry.StopCardView;
import com.squadro.touricity.view.routeList.event.IEntryEventListener;

import lombok.Getter;

public class RouteCreateView extends LinearLayout implements IEntryEventListener {

    @Getter
    private Route route;

    LinearLayout entryList;
    ScrollView scrollView;

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

        for(AbstractEntry entry : route.getAbstractEntryList()){
            if(entry instanceof Stop) {
                Stop stop = (Stop) entry;
                StopCardView cardView = (StopCardView) LayoutInflater.from(context).inflate(R.layout.stopcardview, null);
                cardView.update(stop);
                entryList.addView(cardView);
            }
            else if(entry instanceof  Path) {
                Path path = (Path) entry;
                PathCardView cardView = (PathCardView) LayoutInflater.from(context).inflate(R.layout.path_card_view, null);
                cardView.update(path);
                entryList.addView(cardView);
            }
        }
    }

    private void CleanView() {
        entryList.removeAllViews();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        entryList = findViewById(R.id.route_create_list);
        scrollView = findViewById(R.id.route_create_scroll);
    }

    @Override
    public void onRemoveEntry(AbstractEntry entry) {

    }

    @Override
    public void onClickEntry(AbstractEntry entry) {

    }

    @Override
    public void onHoldEntry(AbstractEntry entry) {

    }

    @Override
    public void onMoveEntry(AbstractEntry entry, EDirection direction) {

    }
}
