package com.squadro.touricity.view.progress;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.progress.IProgressEventListener;
import com.squadro.touricity.progress.Progress;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.map.placesAPI.StopCardViewHandler;
import com.squadro.touricity.view.routeList.entry.PathCardView;
import com.squadro.touricity.view.routeList.entry.PathCardViewHandler;
import com.squadro.touricity.view.routeList.entry.StopCardView;
import com.squadro.touricity.view.routeList.event.IRouteSave;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BottomProgressViewer extends LinearLayout implements IProgressEventListener {

	private Route route;
	private LinearLayout viewList;
	private List<StopCardView> stopCardViews;
	private List<PathCardView> pathCardViews;

	public BottomProgressViewer(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSavedTabListeners(IRouteSave iRouteSave) {
		findViewById(R.id.progress_end_button).setOnClickListener(view -> iRouteSave.endProgress());
	}

	@Override
	public void progressUpdated(Progress progress) {
		boolean found = false;
		Iterator<StopCardView> stopIterator = stopCardViews.iterator();
		Iterator<PathCardView> pathIterator = pathCardViews.iterator();

		for (IEntry entry : route.getEntries()) {
			boolean isCurrent = entry == progress.getCurrentEntry();

			if(entry instanceof Stop) {
				StopCardView stopCardView = stopIterator.next();

				if(!found)
					stopCardView.changeProgressStatus(StopCardView.ProgressStatus.TRACKED);
				else
					stopCardView.changeProgressStatus(StopCardView.ProgressStatus.NOT_TRACKED);

				if(isCurrent)
					stopCardView.changeProgressStatus(StopCardView.ProgressStatus.ON_POINT);
			}
			else if(entry instanceof  Path) {
				PathCardView pathCardView = pathIterator.next();

				if(!found)
					pathCardView.changeProgressStatus(PathCardView.ProgressStatus.TRACKED);
				else
					pathCardView.changeProgressStatus(PathCardView.ProgressStatus.NOT_TRACKED);

				if(isCurrent)
					pathCardView.changeProgressStatus(PathCardView.ProgressStatus.ON_POINT);
			}

			if(isCurrent)
				found = true;
		}
	}

	@Override
	public void progressFinished() {
		clearView();
	}

	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
		viewList = findViewById(R.id.route_progress_list);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	public void setRoute(Route route) {
		clearView();
		this.route = route;

		initializeViews();
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	private void initializeViews() {
		stopCardViews = new LinkedList<>();
		pathCardViews = new LinkedList<>();
		for(IEntry entry : route.getEntries()) {
			if(entry instanceof Stop) {
				Stop stop = (Stop) entry;

				MyPlace place = MapFragmentTab3.getPlace(stop);

				StopCardView stopCardView = (StopCardView) inflate(getContext(), R.layout.stopcardview, null);
				stopCardView.setViewId("progress");

				StopCardViewHandler stopCardViewHandler = new StopCardViewHandler(stopCardView, place, getContext(), "progress", stop);
				stopCardViewHandler.putViews();

				viewList.addView(stopCardView);
				stopCardViews.add(stopCardView);

				stopCardView.collapse();
			}
			else if(entry instanceof Path) {
				Path path = (Path) entry;

				PathCardView pathCardView = (PathCardView) inflate(getContext(), R.layout.path_progress_card_view, null);
				pathCardView = new PathCardViewHandler(pathCardView, getContext(), path).putViews();

				viewList.addView(pathCardView);
				pathCardViews.add(pathCardView);
			}
		}
	}

	private void clearView() {
		viewList.removeAllViews();
	}
}
