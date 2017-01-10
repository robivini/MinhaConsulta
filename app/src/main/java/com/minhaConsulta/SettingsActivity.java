package com.minhaConsulta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import appconfig.ConstValue;
import util.ConnectionDetector;
import util.GPSTracker;

public class SettingsActivity extends ActionBarActivity {
	GPSTracker gps;
	EditText txtLocation;
	static final int CITY_REQ = 1;
	public SharedPreferences settings;
	public ConnectionDetector cd;
	ToggleButton btnNotification, btnNewslatter;
	ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		 cd=new ConnectionDetector(this);
//		txtLocation = (EditText)findViewById(R.id.editCity);
/*		txtLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingsActivity.this,GetCityActivity.class);
				intent.setType("CITYNAME");
				startActivityForResult(intent,CITY_REQ);
			}
		});
		
		txtLocation.setText(settings.getString("selected_city", ""));*/
        btnNewslatter = (ToggleButton)findViewById(R.id.toggleButton2);
        btnNotification = (ToggleButton)findViewById(R.id.toggleButton1);
        if(settings.getString("notification", "0").equalsIgnoreCase("1")){
        	btnNotification.setChecked(true);
        }
        if(settings.getString("newslater", "0").equalsIgnoreCase("1")){
        	btnNewslatter.setChecked(true);
        }
        
        Button btnSave = (Button)findViewById(R.id.button1);
        btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new saveSettings().execute(true);
				settings.edit().putString("selected_city", txtLocation.getText().toString()).commit();
			}
		});
        
        
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
        common.menuItemClick(SettingsActivity.this, id);
        return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == CITY_REQ) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	            // The user picked a contact.
	            // The Intent's data Uri identifies which contact was selected.
	        	txtLocation.setText(data.getStringExtra("CITYNAME"));
	            // Do something with the contact here (bigger example below)
	        }
	    }
	}
	
	
	class saveSettings extends AsyncTask<Boolean, Void, String> {
		String notification = "0";
		String newslatter = "0";
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(SettingsActivity.this, "", 
                    "Loading. Please wait...", true);
			if(btnNewslatter.isChecked()){
				newslatter = "1";
			}
			if(btnNotification.isChecked()){
				notification = "1";
			}
			super.onPreExecute();

		}
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);

		}
		@Override
		protected String doInBackground(Boolean... params) {
			// TODO Auto-generated method stub
		
			String responseString = null;
			 
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ConstValue.JSON_UPDATE_SETTING);
            //if (getEmailId()==null) {
            //	Toast.makeText(getApplicationContext(),"Create new Account Please",Toast.LENGTH_LONG).show();
            //	finish();
			//}

            try {
            	


            	
            	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    	        nameValuePairs.add(new BasicNameValuePair("user_id", settings.getString("user_id","")));
	    	        nameValuePairs.add(new BasicNameValuePair("notification",notification));
	    	        nameValuePairs.add(new BasicNameValuePair("newslatter",newslatter));
	    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

 
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                	InputStream is = r_entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
        					is, "iso-8859-1"), 8);
        			StringBuilder sb = new StringBuilder();
        			String line = null;
        			while ((line = reader.readLine()) != null) {
        				sb.append(line + "\n");
        			}
        			is.close();
        			String json = sb.toString();
        			JSONObject jObj = new JSONObject(json);
        			if (jObj.getString("responce").equalsIgnoreCase("success")) {
	        			//JSONObject data = jObj.getJSONObject("data");
        				settings.edit().putString("notification", notification).apply();
        				settings.edit().putString("newslater", newslatter).apply();
	        			
        			}else{
        				responseString = jObj.getString("data"); 
        			}
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
 
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }catch (JSONException e) {
    			Log.e("JSON Parser", "Error parsing data " + e.toString());
    		}
 
            return responseString;
			
			
		}
		@Override
		protected void onPostExecute(String result) {
			if(result!=null){
				Toast.makeText(getApplicationContext(), "Error :"+result, Toast.LENGTH_LONG).show();
				//AlertDialogManager am = new AlertDialogManager(); 
				//am.showAlertDialog(getApplicationContext(), "Error", result, true);
			}else{
				
			}
			dialog.dismiss();
		}
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
		}
	}
}
