package com.squadro.touricity.view.routeList.entry;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.view.routeList.RouteListItem;

public class StopCardView extends RouteListItem<Stop> {

    private Stop stop;

    private TextView textStopId;
    private TextView textLocationId;
    private TextView textExpense;
    private TextView textComment;
    private TextView textDuration;

    public StopCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initialize() {
        textStopId = findViewById(R.id.card_view_stop_id_content);
        textLocationId = findViewById(R.id.card_view_location_id_content);
        textComment = findViewById(R.id.card_view_comment_content);
        textExpense = findViewById(R.id.card_view_expense_content);
        textDuration = findViewById(R.id.card_view_duration_content);
    }

    @Override
    public void update(Stop stop) {
        this.stop = stop;

        textLocationId.setText(stop.getStop_id());
        textStopId.setText(stop.getStop_id());
        textComment.setText(stop.getComment());
        textDuration.setText(stop.getDuration() + " minutes");
        textExpense.setText(stop.getExpense() + "$");
    }

    @Override
    protected int getRemoveButtonId() {
        return R.id.stop_view_remove_button;
    }

    @Override
    protected int getMoveUpButtonId() {
        return R.id.stop_view_move_up_button;
    }

    @Override
    protected int getMoveDownButtonId() {
        return R.id.stop_view_move_down_button;
    }

    @Override
    protected int getEditButtonId() {
        return R.id.stop_view_edit_button;
    }

    @Override
    public Stop getEntry() {
        return stop;
    }
}
