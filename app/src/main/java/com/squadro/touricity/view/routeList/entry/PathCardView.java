package com.squadro.touricity.view.routeList.entry;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.map.MapFragmentTab1;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.PolylineDrawer;
import com.squadro.touricity.view.routeList.RouteListItem;

import lombok.Getter;
import lombok.Setter;

public class PathCardView extends RouteListItem<Path> {

    private Path path;

    @Getter
    @Setter
    private String viewId;

    private TextView textPathType;
    private TextView textComment;
    private TextView textDuration;
    private TextView textExpense;
    private Route route;

    public PathCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initialize() {
        textPathType = findViewById(R.id.path_view_path_type_content);
        textComment = findViewById(R.id.path_view_comment_content);
        textDuration = findViewById(R.id.path_view_duration_content);
        textExpense = findViewById(R.id.path_view_expense_content);
    }

    @Override
    public void update(Path entry) {
        this.path = entry;

        textPathType.setText(path.getPath_type().name()+"");
        textComment.setText(path.getComment());
        textDuration.setText(path.getDuration() + " minutes");
        textExpense.setText(path.getExpense() + "$");
    }

    @Override
    public void onClick(View view) {
        if(this.getViewId().equals("explore")){

            PolylineDrawer polylineDrawer = new PolylineDrawer(MapFragmentTab1.getMap());
            polylineDrawer.drawRoute(this.route, path);
        }

        else if(this.getViewId().equals("saved")){

            PolylineDrawer polylineDrawer = new PolylineDrawer(MapFragmentTab3.getMap());
            polylineDrawer.drawRoute(this.route, path);
        }
    }

    @Override
    protected int getRemoveButtonId() {
        return R.id.path_view_remove_button;
    }

    @Override
    protected int getMoveUpButtonId() {
        return R.id.path_view_move_up_button;
    }

    @Override
    protected int getMoveDownButtonId() {
        return R.id.path_view_move_down_button;
    }

    @Override
    protected int getEditButtonId() {
        return R.id.path_view_edit_button;
    }

    @Override
    public Path getEntry() {
        return path;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
