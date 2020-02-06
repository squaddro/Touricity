package com.squadro.touricity.view.routeList.entry;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.view.routeList.RouteListItem;

public class PathCardView extends RouteListItem<Path> {

    private Path path;

    private TextView textPathId;
    private TextView textPathType;
    private TextView textComment;
    private TextView textDuration;
    private TextView textExpense;

    public PathCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initialize() {
        textPathId = findViewById(R.id.path_view_path_id_content);
        textPathType = findViewById(R.id.path_view_path_type_content);
        textComment = findViewById(R.id.path_view_comment_content);
        textDuration = findViewById(R.id.path_view_duration_content);
        textExpense = findViewById(R.id.path_view_expense_content);
    }

    @Override
    public void update(Path entry) {
        this.path = entry;

        textPathId.setText(path.getPath_id());
        textPathType.setText(path.getPath_type());
        textComment.setText(path.getComment());
        textDuration.setText(path.getDuration() + " minutes");
        textExpense.setText(path.getExpense() + "&");
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
}
