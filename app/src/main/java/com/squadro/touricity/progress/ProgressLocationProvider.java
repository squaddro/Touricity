package com.squadro.touricity.progress;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProgressLocationProvider extends Service implements LocationListener {

	private Context mContext;

	boolean checkGPS = false;

	boolean checkNetwork = false;

	boolean canGetLocation = false;

	Location loc;
	double latitude;
	double longitude;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;

	private static final long MIN_TIME_BW_UPDATES = 1000;

	protected LocationManager locationManager;

	private IPositionUpdateListener positionUpdateListener;
	private Handler handler;

	public ProgressLocationProvider(Context mContext) {
		this.mContext = mContext;
		initListener();
	}

	public ProgressLocationProvider() {
	}

	private Location initListener() {

		try {
			//ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
			//scheduledExecutorService.scheduleAtFixedRate(this::update, 5000, 5000, TimeUnit.MILLISECONDS);
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// get GPS status
			checkGPS = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// get network provider status
			checkNetwork = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!checkGPS) {
				Toast.makeText(mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show();
			} else {
				this.canGetLocation = true;

				// if GPS Enabled get lat/long using GPS Services
				if (checkGPS) {

					if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						// TODO: Consider calling
						//    ActivityCompat#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for ActivityCompat#requestPermissions for more details.
					}
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if (locationManager != null) {
						loc = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (loc != null) {
							latitude = loc.getLatitude();
							longitude = loc.getLongitude();
						}
					}


				}

			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		return loc;
	}

	private void update() {
		try {
			if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			}

			locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public double getLongitude() {
		if (loc != null) {
			longitude = loc.getLongitude();
		}
		return longitude;
	}

	public double getLatitude() {
		if (loc != null) {
			latitude = loc.getLatitude();
		}
		return latitude;
	}

	public void stopListener() {
		if (locationManager != null) {

			if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			locationManager.removeUpdates(this);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("o");
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (positionUpdateListener != null) {
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			positionUpdateListener.onPositionUpdated(latLng);
		}
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
		System.out.println("o");
	}

	@Override
	public void onProviderEnabled(String s) {
		System.out.println("o");
	}

	@Override
	public void onProviderDisabled(String s) {
		System.out.println("o");

	}

	public void setPositionUpdateListener(IPositionUpdateListener updateListener) {
		this.positionUpdateListener = updateListener;
	}
}
