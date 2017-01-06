package com.minhaConsulta;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import appconfig.ConstValue;
import util.ConnectionDetector;

public class Splash extends Activity {
	private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
	public SharedPreferences settings;
	public ConnectionDetector cd;
	@TargetApi(Build.VERSION_CODES.M)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);




		checkPermission();












		//this.getActionBar();
		/*MultiplePermissionsListener dialogMultiplePermissionsListener =
				DialogOnAnyDeniedMultiplePermissionsListener.Builder
						.withContext(this)
						.withTitle(getResources().getString(R.string.app_permission))
						.withMessage(getResources().getString(R.string.app_permission_message))
						.withButtonText(android.R.string.ok)
						.withIcon(R.drawable.ic_launcher)
						.build();
		Dexter.checkPermissions(dialogMultiplePermissionsListener, Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.INTERNET,
				Manifest.permission.GET_ACCOUNTS,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.ACCESS_NETWORK_STATE,
				Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.SEND_SMS,
				Manifest.permission.WAKE_LOCK);*/


		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		cd=new ConnectionDetector(this);
		 
		if(settings.getString("user_id", "").equalsIgnoreCase("")){
			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(Splash.this,FbLoginActivity.class);
					startActivity(intent);
					finish();

				}
			}, 1000 * 2);

		}else{
			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(Splash.this,MainActivity.class);
					startActivity(intent);
					finish();
				}
			}, 1000 * 2);

		}
	}



	private void checkPermission() {
		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(Splash.this,
				Manifest.permission.READ_PHONE_STATE)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,
					Manifest.permission.READ_PHONE_STATE)) {

				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(Splash.this,
						new String[]{Manifest.permission.READ_PHONE_STATE},
						MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

				// MY_PERMISSIONS_REQUEST_READ_PHONE_STATE is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}
	
}
