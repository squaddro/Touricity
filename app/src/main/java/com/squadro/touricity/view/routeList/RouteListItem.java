package com.squadro.touricity.view.routeList;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.view.routeList.event.IEntryButtonEventsListener;

public abstract class RouteListItem<T extends AbstractEntry> extends CardView implements View.OnClickListener, OnLongClickListener {

    protected IEntryButtonEventsListener entryEventListener;

    public RouteListItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(View view) {
        if(entryEventListener != null) {
            entryEventListener.onClickEntry(getEntry());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(entryEventListener != null) {
            entryEventListener.onHoldEntry(getEntry());
        }

        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initialize();

        setOnClickListener(this);
        setOnLongClickListener(this);
        setLongClickable(true);
    }

    public void setEntryEventListener(IEntryButtonEventsListener listener) {
        entryEventListener = listener;

        findViewById(getRemoveButtonId()).setOnClickListener(view -> entryEventListener.onRemoveEntry(getEntry()));
        findViewById(getMoveUpButtonId()).setOnClickListener(view -> entryEventListener.onMoveEntry(getEntry(), IEntryButtonEventsListener.EDirection.UP));
        findViewById(getMoveDownButtonId()).setOnClickListener(view -> entryEventListener.onMoveEntry(getEntry(), IEntryButtonEventsListener.EDirection.DOWN));
        findViewById(getEditButtonId()).setOnClickListener(view -> entryEventListener.onEditEntry(getEntry()));
    }

    abstract public T getEntry();

    abstract protected void initialize();
    abstract public void update(T entry);

    abstract protected @IdRes int getRemoveButtonId();
    abstract protected @IdRes int getMoveUpButtonId();
    abstract protected @IdRes int getMoveDownButtonId();
    abstract protected @IdRes int getEditButtonId();
}
