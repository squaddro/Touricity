package com.squadro.touricity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.squadro.touricity.cookie.CookieMethods;
import com.squadro.touricity.view.PanelLayout;
import com.squadro.touricity.view.filter.FilterSearchView;
import com.squadro.touricity.view.map.MapView;
import com.squadro.touricity.view.routeList.RouteListView;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        LinearLayout linearLayout = findViewById(R.id.main_activity);

        int mainHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int mainWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        manageView(linearLayout, new FilterSearchView(context), mainWidth, (int) (mainHeight * 0.2));
        manageView(linearLayout, new MapView(context), mainWidth, (int) (mainHeight * 0.5));
        manageView(linearLayout, new RouteListView(context), mainWidth, (int) (mainHeight * 0.3));
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieMethods.cleanCookies();
    }

    private void manageView(LinearLayout mainLayout, PanelLayout panelLayout, int width, int height) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        panelLayout.setLayoutParams(layoutParams);
        mainLayout.addView(panelLayout);
    }
}
