package com.squadro.touricity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squadro.touricity.cookie.CookieMethods;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieMethods.cleanCookies();
    }
}
