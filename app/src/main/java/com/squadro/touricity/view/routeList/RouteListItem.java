package com.squadro.touricity.view.routeList;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;

import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.view.routeList.event.IEntryEventListener;
import com.squadro.touricity.view.routeList.event.IEntryMoveEventListener;

public abstract class RouteListItem<T extends AbstractEntry> extends CardView implements View.OnClickListener {

    protected IEntryEventListener entryEventListener;

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
    protected void onFinishInflate() {
        super.onFinishInflate();

        initialize();
    }

    public void setEntryEventListener(IEntryEventListener listener) {
        entryEventListener = listener;

        findViewById(getRemoveButtonId()).setOnClickListener(view -> entryEventListener.onRemoveEntry(getEntry()));
        findViewById(getMoveUpButtonId()).setOnClickListener(view -> entryEventListener.onMoveEntry(getEntry(), IEntryMoveEventListener.EDirection.UP));
        findViewById(getMoveDownButtonId()).setOnClickListener(view -> entryEventListener.onMoveEntry(getEntry(), IEntryMoveEventListener.EDirection.DOWN));
    }

    abstract public T getEntry();

    abstract protected void initialize();
    abstract public void update(T entry);

    abstract protected @IdRes int getRemoveButtonId();
    abstract protected @IdRes int getMoveUpButtonId();
    abstract protected @IdRes int getMoveDownButtonId();
}
