package com.squadro.touricity.view.progress;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squadro.touricity.R;
import com.squadro.touricity.progress.IProgressEventListener;
import com.squadro.touricity.progress.Progress;

public class TopProgressView extends CardView implements IProgressEventListener, View.OnClickListener {

	LinearLayout textAreas;
	LinearLayout additionalTextAreas;

	TextView timePercentageText;
	TextView distancePercentageText;

	TextView startTimeText;
	TextView actualEndTimeText;
	TextView expectedEndTimeText;
	ImageView expandImage;

	boolean expanded = false;
	int expandButtonImage = R.drawable.ic_expand_more_24px;
	int collapseButtonImage = R.drawable.ic_expand_less_24px;

	public TopProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
		timePercentageText = findViewById(R.id.top_progress_percentage);
		expandImage = findViewById(R.id.top_progress_expand_image);
		textAreas = findViewById(R.id.top_progress_text_areas);
		additionalTextAreas = findViewById(R.id.top_progress_additional_text_areas);

		startTimeText = new TextView(getContext());
		startTimeText.setText("start: ");
		actualEndTimeText = new TextView(getContext());
		actualEndTimeText.setText("actual: ");
		expectedEndTimeText = new TextView(getContext());
		expectedEndTimeText.setText("finish: ");

		distancePercentageText = new TextView(getContext());
		distancePercentageText.setText("Distance: %0");

		textAreas.addView(distancePercentageText);

		additionalTextAreas.addView(startTimeText);
		additionalTextAreas.addView(actualEndTimeText);
		additionalTextAreas.addView(expectedEndTimeText);

		setOnClickListener(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		setLayoutParams(params);
		collapse();
	}

	@Override
	public void progressUpdated(Progress progress) {

		timePercentageText.setText("%" + String.format("%.1f", progress.getProgressTimePercentage() * 100));

		distancePercentageText.setText("Distance: %" + String.format("%.1f", progress.getProgressDistancePercentage() * 100));

		startTimeText.setText("start: " + progress.getStartTime().toString());
		actualEndTimeText.setText("actual: " + progress.getActualEndTime().toString());
		expectedEndTimeText.setText("finish: " + progress.getExpectedFinishTime().toString());
	}

	@Override
	public void progressFinished() {

	}

	@Override
	public void onClick(View view) {
		if(expanded)
			collapse();
		else
			expand();
	}

	private void expand() {
		expandImage.setBackgroundResource(collapseButtonImage);
		additionalTextAreas.setVisibility(VISIBLE);

		expanded = true;
	}

	private void collapse() {
		additionalTextAreas.setVisibility(GONE);
		expandImage.setBackgroundResource(expandButtonImage);

		expanded = false;
	}
}
