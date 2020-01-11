package com.squadro.touricity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.squadro.touricity.view.map.MapView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout viewById = (LinearLayout)findViewById(R.id.main_activity);
        viewById.addView(new MapView(getApplicationContext()));
    }
}
