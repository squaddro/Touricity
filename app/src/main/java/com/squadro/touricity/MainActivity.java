package com.squadro.touricity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squadro.touricity.cookie.CookieMethods;

public class MainActivity extends AppCompatActivity {

    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        setContentView(R.layout.register_view);
        Button btn = findViewById(R.id.btn_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });
        TextView signUp = (TextView) findViewById(R.id.link_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                findViewById(R.id.signin_layout).setVisibility(View.INVISIBLE);
                findViewById(R.id.register_layout).setVisibility(View.VISIBLE);
            }
        });

        TextView login = (TextView) findViewById(R.id.link_login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                findViewById(R.id.signin_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.register_layout).setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        CookieMethods.cleanCookies();
    }

}
