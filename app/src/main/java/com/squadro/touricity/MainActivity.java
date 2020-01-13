package com.squadro.touricity;

import android.content.Context;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.squadro.touricity.cookie.CookieMethods;
import com.squadro.touricity.view.map.MapView;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        LinearLayout viewById = (LinearLayout) findViewById(R.id.main_activity);
        viewById.addView(new MapView(context));
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieMethods.cleanCookies();
    }
}
