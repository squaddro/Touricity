package com.squadro.touricity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.squadro.touricity.view.map.MapFragmentTab4;
import com.squadro.touricity.view.map.placesAPI.MapLongClickListener;
import com.squadro.touricity.view.tabView.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    public static Context context;
    private List<Fragment> fragments;
    public static MapFragmentTab1 fragment1;
    public static MapFragmentTab2 fragment2;
    public static MapFragmentTab3 fragment3;
    public static MapFragmentTab4 fragment4;
    private boolean networkConnection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        ViewPager viewPager = getViewPager();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        setTabNames(tabLayout);
        if(checkConnection()){
            tabLayout.getTabAt(2).select();
        }else{
            tabLayout.getTabAt(0).select();
        }

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::periodicNetworkCheck,1000,1000, TimeUnit.MILLISECONDS );
    }

    private void periodicNetworkCheck(){
        boolean prev = networkConnection;
        checkConnection();
        if(prev != networkConnection){
            if(networkConnection){
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(HomeActivity.context, MainActivity.class));
                overridePendingTransition(0, 0);
            }else{
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        }
    }
    private void setTabNames(TabLayout tabLayout) {
        if(checkConnection()){
            tabLayout.getTabAt(0).setText(getResources().getString(R.string.tab1_name));
            tabLayout.getTabAt(1).setText(getResources().getString(R.string.tab2_name));
            tabLayout.getTabAt(2).setText(getResources().getString(R.string.tab3_name));
            tabLayout.getTabAt(3).setText("Suggested Routes");
        }else{
            tabLayout.getTabAt(0).setText(getResources().getString(R.string.tab3_name));
        }
    }

    private ViewPager getViewPager() {
        fragments = new ArrayList<>();
        initializeFragments();
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(fragments.size());
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), context, fragments);
        viewPager.setAdapter(fragmentAdapter);
        return viewPager;
    }

    private void initializeFragments() {
        if(checkConnection()){
            fragment1 = new MapFragmentTab1();
            fragments.add(fragment1);

            fragment2 = new MapFragmentTab2();
            fragments.add(fragment2);

            fragment3 = new MapFragmentTab3();
            fragments.add(fragment3);

            fragment4 = new MapFragmentTab4();
            fragments.add(fragment4);
        }
        else{
            fragment3 = new MapFragmentTab3();
            fragments.add(fragment3);
        }


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            double x = ev.getX();
            double y = ev.getY();

            if (fragment1 != null) {
                MapLongClickListener mapLongClickListener = fragment1.getMapLongClickListener();
                if (mapLongClickListener != null) {
                    mapLongClickListener.setX(x);
                    mapLongClickListener.setY(y);
                    mapLongClickListener.dissmissPopUp();
                }
            }
            if (fragment2 != null) {
                MapLongClickListener mapLongClickListener = fragment2.getMapLongClickListener();
                if (mapLongClickListener != null) {
                    mapLongClickListener.setX(x);
                    mapLongClickListener.setY(y);
                    mapLongClickListener.dissmissPopUp();
                }
            }
            if (fragment3 != null) {
                MapLongClickListener mapLongClickListener = fragment3.getMapLongClickListener();
                if (mapLongClickListener != null) {
                    mapLongClickListener.setX(x);
                    mapLongClickListener.setY(y);
                    mapLongClickListener.dissmissPopUp();
                }
            }
            if(fragment4 != null){
                MapLongClickListener mapLongClickListener = fragment4.getMapLongClickListener();
                if(mapLongClickListener != null){
                    mapLongClickListener.setX(x);
                    mapLongClickListener.setY(y);
                    mapLongClickListener.dissmissPopUp();
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

    private boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            networkConnection = true;
            return true;
        } else{
            networkConnection = false;
            return false;
        }
    }
}