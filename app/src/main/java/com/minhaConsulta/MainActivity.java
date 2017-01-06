package com.minhaConsulta;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import adapters.MainAdapter;
import appconfig.ConstValue;
import gcm.QuickstartPreferences;
import gcm.RegistrationIntentService;
import imgLoader.JSONParser;
import util.ConnectionDetector;
import util.ObjectSerializer;


public class MainActivity extends ActionBarActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	ArrayList<HashMap<String, String>> newsArray;
	MainAdapter adapter;
	
	AsyncTask<Void, Void, Void> mRegisterTask;
	public static String email;



	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "MainActivity";

	private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		 cd=new ConnectionDetector(this);

		email = settings.getString("user_email", "");


		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				SharedPreferences sharedPreferences =
						PreferenceManager.getDefaultSharedPreferences(context);
				boolean sentToken = sharedPreferences
						.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
				if (sentToken) {

				} else {

				}
			}
		};

		if (checkPlayServices()) {
			if(!email.equalsIgnoreCase("")) {
				// Start IntentService to register this application with GCM.
				Intent intent = new Intent(this, RegistrationIntentService.class);
				startService(intent);
			}
		}

		 newsArray = new ArrayList<HashMap<String,String>>();
	        try {
	        	newsArray = (ArrayList<HashMap<String,String>>) ObjectSerializer.deserialize(settings.getString(ConstValue.PREFS_MAIN_CAT, ObjectSerializer.serialize(new ArrayList<HashMap<String,String>>())));
			}catch (IOException e) {
				    e.printStackTrace();
			}

	        new loadNewsTask().execute(true);



	        adapter = new MainAdapter(getApplicationContext(), newsArray);
	        GridView gridview = (GridView)findViewById(R.id.gridView1);
	        gridview.setAdapter(adapter);
	        gridview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					ConstValue.selected_category = newsArray.get(position);
					Intent intent = new Intent(MainActivity.this,DoctorListActivity.class);
					startActivity(intent);
				}

				
			});
	        
	        //if(settings.getBoolean("REGISTER", false)){
	        	
	        //}else{


	        //}


    }


	public class loadNewsTask extends AsyncTask<Boolean, Void, ArrayList<HashMap<String, String>>> {

		JSONParser jParser;
		JSONObject json;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			// TODO Auto-generated method stub
			if (result!=null) {
				//adapter.notifyDataSetChanged();
				
			}	
			try {
				settings.edit().putString(ConstValue.PREFS_MAIN_CAT,ObjectSerializer.serialize(newsArray)).commit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@SuppressLint("NewApi") @Override
		protected void onCancelled(ArrayList<HashMap<String, String>> result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
		}

	
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Boolean... params) {
			// TODO Auto-generated method stub
			
			try {
				jParser = new JSONParser();
				
				if(cd.isConnectingToInternet())
				{
					json = jParser.getJSONFromUrl(ConstValue.JSON_MAINCAT);
					if (json.has("data")) {
						
					
					if(json.get("data") instanceof JSONArray){
						
					JSONArray menus = json.getJSONArray("data");
					if(menus!=null)
					{
						newsArray.clear();
						for (int i = 0; i < menus.length(); i++) {
							JSONObject d = menus.getJSONObject(i);
							HashMap<String, String> map2 = new HashMap<String, String>();
	  							map2.put("id", d.getString("id"));
	  							map2.put("title", d.getString("title"));
	  							map2.put("icon", d.getString("icon"));
	  							map2.put("iconpath", d.getString("iconpath"));
	  							
							newsArray.add(map2);
						}
					}	
					
					}
					
					}
				}else
				{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_intent_connection), Toast.LENGTH_LONG).show();
				}
					
			jParser = null;
			json = null;
			
				} catch (Exception e) {
					// TODO: handle exception
					
					return null;
				}
			return newsArray;
		}

	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        CommonFunctions common = new CommonFunctions();
        common.menuItemClick(MainActivity.this, id);
        return super.onOptionsItemSelected(item);
    }



	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
				new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}
    
}
