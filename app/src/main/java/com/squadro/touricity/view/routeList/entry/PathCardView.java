package com.squadro.touricity.view.routeList.entry;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.routeList.RouteCreateView;
import com.squadro.touricity.view.routeList.RouteListItem;

import lombok.Getter;

public class PathCardView extends RouteListItem<Path>  {

	public enum ProgressStatus {
		NOT_TRACKED,
		ON_POINT,
		TRACKED
	}

	private Path path;

	public PathCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
	}

	@Override
	public Path getEntry() {
		return path;
	}

	@Override
	protected void initialize() {

	}

	@Override
	public void update(Path path) {
		this.path = path;

	}

	public void changeProgressStatus(PathCardView.ProgressStatus status) {
		switch (status) {
			case TRACKED:
				setCardBackgroundColor(Color.DKGRAY);
				break;
			case ON_POINT:
				setCardBackgroundColor(Color.LTGRAY);
				break;
			case NOT_TRACKED:
				setCardBackgroundColor(Color.WHITE);
				break;
		}
	}
}