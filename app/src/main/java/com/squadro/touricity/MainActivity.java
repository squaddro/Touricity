package com.squadro.touricity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.squadro.touricity.cookie.CookieMethods;
import com.squadro.touricity.view.map.MapFragment;
import com.squadro.touricity.view.map.MapView;
import com.squadro.touricity.view.tabView.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Context context;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        fragments = new ArrayList<>();

        initializeMapViews();

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), context, fragments);
        viewPager.setAdapter(fragmentAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getResources().getString(R.string.tab1_name));
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.tab2_name));
        tabLayout.getTabAt(2).setText(getResources().getString(R.string.tab3_name));

    }

    private void initializeMapViews() {
        MapView.setTabIndex(0);
        MapView tab1MapView = new MapView(context);
        MapFragment.setIds(R.layout.tab1_map_view, R.id.tab1_map);
        MapFragment tab1MapFragment = new MapFragment();

        MapView.setTabIndex(1);
        MapView tab2MapView = new MapView(context);
        MapFragment.setIds(R.layout.tab2_map_view, R.id.tab2_map);
        MapFragment tab2MapFragment = new MapFragment();

        MapView.setTabIndex(2);
        MapView tab3MapView = new MapView(context);
        MapFragment.setIds(R.layout.tab3_map_view, R.id.tab3_map);
        MapFragment tab3MapFragment = new MapFragment();

        fragments.add(tab1MapFragment);
        fragments.add(tab2MapFragment);
        fragments.add(tab3MapFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieMethods.cleanCookies();
    }
}
