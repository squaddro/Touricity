package com.squadro.touricity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squadro.touricity.cookie.CookieMethods;
import com.squadro.touricity.fcm.MyFirebaseMessagingService;
import com.squadro.touricity.message.types.Credential;
import com.squadro.touricity.requests.UserRequests;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

public class MainActivity extends AppCompatActivity {

	public static Context context;
	public static Credential credential;
	private static ConnectivityManager connectivityManager;
	private static boolean networkConnection = true;

	@SneakyThrows
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

		boolean writeGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
		boolean locationGranted =ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
		boolean locationGrantedFine =ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
		if(!writeGranted || !locationGranted || !locationGrantedFine){
			ArrayList<String> grants = new ArrayList<>();
			if(!writeGranted)
				grants.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if(!locationGranted)
				grants.add(Manifest.permission.ACCESS_COARSE_LOCATION);
			if(!locationGrantedFine)
				grants.add(Manifest.permission.ACCESS_FINE_LOCATION);

			ActivityCompat.requestPermissions(this, grants.toArray(new String[0]),5);
		}
		;

		File file = new File(context.getFilesDir(),"UserData");
		if(file.exists()){
			File dataFile = new File(file, "userData.xml");
			credential = (Credential)new XStream().fromXML(dataFile);
			if(credential.getToken().equals(MyFirebaseMessagingService.getToken(this))) {
				startActivity(new Intent(MainActivity.context, HomeActivity.class));
				finish();
			}
		}

		setContentView(R.layout.register_view);
		Button btn_login = findViewById(R.id.btn_login);
		final EditText userName = (EditText) findViewById(R.id.input_username);

		btn_login.setOnClickListener(v -> {
			signinMethod(userName, v, this);
		});
		userName.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
					signinMethod(userName, v, v.getContext());
					return true;
				}
				return false;
			}
		});

		Button btn_register = findViewById(R.id.btn_register);
		final EditText userNameRegister = (EditText) findViewById(R.id.register_username);
		btn_register.setOnClickListener(v -> {
			registerMethod(userNameRegister, v, this);
		});
		userNameRegister.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
					registerMethod(userNameRegister, v, v.getContext());
					return true;
				}
				return false;
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
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
		scheduledExecutorService.scheduleAtFixedRate(this::periodicNetworkCheck,1000,1000, TimeUnit.MILLISECONDS );
	}

	private void registerMethod(EditText userNameRegister, View v, Context mainActivity) {
		Credential userInfo = getCredentialInfo(v, userNameRegister);
		UserRequests userRequests = new UserRequests(mainActivity, MainActivity.this);
		userRequests.signup(userInfo);
	}

	private void signinMethod(EditText userName, View v, Context mainActivity) {
		Credential userInfo = getCredentialInfo(v, userName);
		credential = userInfo;
		UserRequests userRequests = new UserRequests(mainActivity, MainActivity.this);
		try {
			userRequests.signin(userInfo);
		} catch (Exception e) {
			;
		}
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