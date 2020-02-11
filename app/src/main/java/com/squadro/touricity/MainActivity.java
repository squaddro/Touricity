package com.squadro.touricity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.squadro.touricity.cookie.CookieMethods;
import com.squadro.touricity.view.map.MapFragmentTab1;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.MapLongClickListener;
import com.squadro.touricity.view.tabView.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Context context;
    private List<Fragment> fragments;
    MapFragmentTab1 fragment;
    MapFragmentTab2 fragment2;
    MapFragmentTab3 fragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        fragments = new ArrayList<>();

        initializeFragments();

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), context, fragments);
        viewPager.setAdapter(fragmentAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getResources().getString(R.string.tab1_name));
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.tab2_name));
        tabLayout.getTabAt(2).setText(getResources().getString(R.string.tab3_name));

    }

    private void initializeFragments() {
        fragment = new MapFragmentTab1();
        fragments.add(fragment);

        fragment2 = new MapFragmentTab2();
        fragments.add(fragment2);

        fragment3 = new MapFragmentTab3();
        fragments.add(fragment3);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            double x = ev.getX();
            double y = ev.getY();

            if (fragment != null) {
                MapLongClickListener mapLongClickListener = fragment.getMapLongClickListener();
                if(mapLongClickListener != null){
                    mapLongClickListener.setX(x);
                    mapLongClickListener.setY(y);
                }
            }
            if (fragment2 != null) {
                MapLongClickListener mapLongClickListener = fragment2.getMapLongClickListener();
                if(mapLongClickListener != null){
                    mapLongClickListener.setX(x);
                    mapLongClickListener.setY(y);
                }
            }
            if (fragment3 != null) {
                MapLongClickListener mapLongClickListener = fragment3.getMapLongClickListener();
                if(mapLongClickListener != null){
                    mapLongClickListener.setX(x);
                    mapLongClickListener.setY(y);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieMethods.cleanCookies();
    }
}
