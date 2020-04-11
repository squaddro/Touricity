package com.squadro.touricity.view.routeList.entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.view.map.MapFragmentTab1;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.event.IEntryButtonEventsListener;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

public class PathCardViewHandler {

	private final Context context;
	private PathCardView pathCardView;
	private Path path;

	@RequiresApi(api = Build.VERSION_CODES.N)
	public PathCardViewHandler(PathCardView pathCardView, Context context, Path path) {
		this.pathCardView = pathCardView;
		this.context = context;
		this.path = path;
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	public PathCardView putViews() {
		LinearLayout linearLayoutHorizontal = new LinearLayout(context);
		linearLayoutHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		linearLayoutHorizontal.setOrientation(HORIZONTAL);

		LinearLayout durationAndCost = new LinearLayout(context);
		durationAndCost.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		durationAndCost.setOrientation(HORIZONTAL);

		LinearLayout textAreasLayout = pathCardView.findViewById(R.id.textAreas);

		textAreasLayout.addView(linearLayoutHorizontal);

		TextView durationTextView = getLabel("Duration: " + path.getDuration() + " minutes ",16);
		TextView expenseTextView = getLabel(" Expense: " + path.getExpense() + " $",16);

		durationAndCost.addView(durationTextView);
		durationAndCost.addView(expenseTextView);

		textAreasLayout.addView(durationAndCost);

		return pathCardView;
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	private TextView getLabel(String text,int textSize) {
		TextView label = new TextView(context);
		label.setTextSize(textSize);
		label.setTypeface(null, Typeface.BOLD);
		label.setElegantTextHeight(true);
		label.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		label.setSingleLine(false);
		label.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		label.setText(text);
		return label;
	}
}
