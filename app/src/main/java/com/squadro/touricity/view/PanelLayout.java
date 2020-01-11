package com.squadro.touricity.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public abstract class PanelLayout extends LinearLayout {
    public PanelLayout(Context context) {
        super(context);
        View inflatedView = View.inflate(context, getID(), null);
        addView(inflatedView);
    }

    protected abstract int getID();
}
