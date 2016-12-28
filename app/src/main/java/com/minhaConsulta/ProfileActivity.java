package com.minhaConsulta;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class ProfileActivity extends ActionBarActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	EditText txtName,txtEmail,txtPhone,txtCity,txtZip,txtCode;
	ProgressDialog dialog;
	LinearLayout layoutConfirm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		cd=new ConnectionDetector(this);
		
		txtName = (EditText)findViewById(R.id.editName);
		txtEmail = (EditText)findViewById(R.id.editEmail);
		txtPhone = (EditText)findViewById(R.id.editPhone);
		txtCity = (EditText)findViewById(R.id.editCity);
		txtZip = (EditText)findViewById(R.id.editZip);
		
		txtName.setText(settings.getString("user_name", ""));
		txtEmail.setText(settings.getString("user_email", ""));
		txtPhone.setText(settings.getString("user_phone", ""));
		txtCity.setText(settings.getString("user_city", ""));
		txtZip.setText(settings.getString("user_zip", ""));
		
		layoutConfirm = (LinearLayout)findViewById(R.id.layoutConfirm);
		layoutConfirm.setVisibility(View.GONE);
		if (!settings.getString("uniquecode", "").equalsIgnoreCase("") && !settings.getBoolean("mobile_confirm", false) ) {
			layoutConfirm.setVisibility(View.VISIBLE);
		}
		
		txtCode = (EditText)findViewById(R.id.editCode);
		
		
		Button btnConfirm = (Button)findViewById(R.id.btnConfirm);
		btnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(txtCode.getText().toString().equalsIgnoreCase(settings.getString("uniquecode", ""))){
					settings.edit().putBoolean("mobile_confirm", true).commit();
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.confirm_success),Toast.LENGTH_LONG).show();
					finish();
				}else{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.code_is_not_avaible),Toast.LENGTH_LONG).show();
				}
			}
		});
		
		Button btnsave = (Button)findViewById(R.id.button1);
		btnsave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new saveSettings().execute(true);
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
        common.menuItemClick(ProfileActivity.this, id);
        return super.onOptionsItemSelected(item);
	}
	
	
	class saveSettings extends AsyncTask<Boolean, Void, String> {
		String name, phone, city, zip;
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ProfileActivity.this, "", 
                    getResources().getString(R.string.loading_wait), true);
			name = txtName.getText().toString();
			phone = txtPhone.getText().toString();
			city = txtCity.getText().toString();
			zip = txtZip.getText().toString();
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
            HttpPost httppost = new HttpPost(ConstValue.JSON_UPDATE_PROFILE);
            //if (getEmailId()==null) {
            //	Toast.makeText(getApplicationContext(),"Create new Account Please",Toast.LENGTH_LONG).show();
            //	finish();
			//}

            try {
            	
            	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    	        nameValuePairs.add(new BasicNameValuePair("user_id", settings.getString("user_id","")));
	    	        nameValuePairs.add(new BasicNameValuePair("name",name));
	    	        nameValuePairs.add(new BasicNameValuePair("phone",phone));
	    	        nameValuePairs.add(new BasicNameValuePair("city",city));
	    	        nameValuePairs.add(new BasicNameValuePair("zip",zip));
	    	        
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
	        			JSONObject data = jObj.getJSONObject("data");
        				settings.edit().putString("user_name", data.getString("name")).apply();
        				settings.edit().putString("user_city", data.getString("city")).apply();
        				settings.edit().putString("user_zip", data.getString("zip")).apply();
        				settings.edit().putString("user_phone", data.getString("phone")).apply();
        				settings.edit().putString("uniquecode", data.getString("unique_code")).apply();
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
				try {
					//settings.edit().putString("app_mobile", textPhone.getText().toString()).commit();
					SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(settings.getString("user_phone", ""), null, getResources().getString(R.string.sms_confirmation_code).replace("#code#",settings.getString("uniquecode", "")), null, null);
                    //Toast.makeText(getApplicationContext(), "SMS Sent!"+settings.getString("reg_id", "")+" - "+settings.getString("app_id", ""),
                    //        Toast.LENGTH_LONG).show();
                    layoutConfirm.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.sms_send_failed),
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
			}
			dialog.dismiss();
		}
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
		}
	}
}
