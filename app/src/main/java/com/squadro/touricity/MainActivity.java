package com.squadro.touricity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squadro.touricity.cookie.CookieMethods;
import com.squadro.touricity.fcm.MyFirebaseMessagingService;
import com.squadro.touricity.message.types.Credential;
import com.squadro.touricity.requests.UserRequests;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static Context context;
    public static Credential credential;
    private static ConnectivityManager connectivityManager;
    private static boolean networkConnection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        context = getApplicationContext();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!checkConnection()) {
            startActivity(new Intent(MainActivity.context, HomeActivity.class));
            finish();
        }

        setContentView(R.layout.register_view);
        Button btn_login = findViewById(R.id.btn_login);
        final EditText userName = (EditText) findViewById(R.id.input_username);

        btn_login.setOnClickListener(v -> {
            Credential userInfo = getCredentialInfo(v, userName);
            credential = userInfo;
            UserRequests userRequests = new UserRequests(this, MainActivity.this);
            userRequests.signin(userInfo);
        });

        Button btn_register = findViewById(R.id.btn_register);
        final EditText userNameRegister = (EditText) findViewById(R.id.register_username);
        btn_register.setOnClickListener(v -> {
            Credential userInfo = getCredentialInfo(v, userNameRegister);
            UserRequests userRequests = new UserRequests(this, MainActivity.this);
            userRequests.signup(userInfo);
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
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::periodicNetworkCheck,1000,1000, TimeUnit.MILLISECONDS );
    }

    public Credential getCredentialInfo(View v, EditText text) {
        String user_name = null;
        String token = null;
        user_name = text.getText().toString();
        token = MyFirebaseMessagingService.getToken(this);
        Credential userInfo = new Credential(user_name, token);
        return userInfo;
    }

    private void periodicNetworkCheck(){
        boolean prev = networkConnection;
        checkConnection();
        if(prev != networkConnection){
            if(networkConnection){
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }else{
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(MainActivity.context, HomeActivity.class));
                overridePendingTransition(0, 0);
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        CookieMethods.cleanCookies();
    }

    public static boolean checkConnection() {
        if (connectivityManager != null && (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)) {
            networkConnection = true;
            return true;
        } else{
            networkConnection = false;
            return false;
        }
    }
}