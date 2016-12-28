package com.minhaConsulta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adapters.GetCityAdapter;
import appconfig.ConstValue;
import imgLoader.JSONParser;
import util.ConnectionDetector;
import util.GPSTracker;
import util.ObjectSerializer;

public class GetCityActivity extends ActionBarActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	ArrayList<String> newsArray,searchArray;
	GetCityAdapter adapter;
	EditText txtSearch;
	GPSTracker gps;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_city);
		
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		 cd=new ConnectionDetector(this);
		 
		 searchArray = new ArrayList<String>();
		 
		 newsArray = new ArrayList<String>();
	        try {
	        	newsArray = (ArrayList<String>) ObjectSerializer.deserialize(settings.getString("cities", ObjectSerializer.serialize(new ArrayList<String>())));		
			}catch (IOException e) {
				    e.printStackTrace();
			}
	        
	        txtSearch = (EditText)findViewById(R.id.editText1);
			gps = new GPSTracker(GetCityActivity.this);
			 
	        // check if GPS enabled     
	        if(gps.canGetLocation()){
	             
	            double latitude = gps.getLatitude();
	            double longitude = gps.getLongitude();
	             
	            String locality = gps.getLocality(getApplicationContext());
	            txtSearch.setText(locality);
	            // \n is for new line
	            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
	        }else{
	            // can't get location
	            // GPS or Network is not enabled
	            // Ask user to enable GPS/network in settings
	            gps.showSettingsAlert();
	        }
	        txtSearch.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					searchArray.clear();
					for (String str : newsArray) {
						Pattern p = Pattern.compile(txtSearch.getText().toString().toLowerCase()+"(.*)");
						 Matcher m = p.matcher(str.toLowerCase());
						 if (m.find()) {
							 searchArray.add(str);
						 }
						//if(str.matches(txtSearch.getText()+".*")){
						//	 searchArray.add(str);
			             //  }
					}
					
					adapter.notifyDataSetChanged();
				}
			});
	        
	        ListView listview = (ListView)findViewById(R.id.listView1);
	        adapter = new GetCityAdapter(getApplicationContext(), searchArray);
	        listview.setAdapter(adapter);
	        listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					//settings.edit().putString("selected_city", newsArray.get(position));
					Intent intent=new Intent();  
                    intent.putExtra("CITYNAME", newsArray.get(position));  
                    setResult(-1,intent);  
					finish();
				}
			});
	        
	        new loadNewsTask().execute(true);
	}

	
	public class loadNewsTask extends AsyncTask<Boolean, Void, ArrayList<String>> {

		JSONParser jParser;
		JSONObject json;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			if (result!=null) {
				//adapter.notifyDataSetChanged();
				
			}	
			try {
				settings.edit().putString("cities",ObjectSerializer.serialize(newsArray)).commit();
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
		protected void onCancelled(ArrayList<String> result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
		}

	
		@Override
		protected ArrayList<String> doInBackground(
				Boolean... params) {
			// TODO Auto-generated method stub
			
			try {
				jParser = new JSONParser();
				
				if(cd.isConnectingToInternet())
				{
					json = jParser.getJSONFromUrl(ConstValue.JSON_GETCITY);
					if (json.has("data")) {
						
					
					if(json.get("data") instanceof JSONArray){
						
					JSONArray menus = json.getJSONArray("data");
					if(menus!=null)
					{
						newsArray.clear();
						for (int i = 0; i < menus.length(); i++) {
							JSONObject d = menus.getJSONObject(i);
							
	  							
							newsArray.add(d.getString("dr_city"));
						}
					}	
					
					}
					
					}
				}else
				{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_intent_connection), Toast.LENGTH_LONG).show();
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
        common.menuItemClick(GetCityActivity.this, id);
        return super.onOptionsItemSelected(item);
	}
}
