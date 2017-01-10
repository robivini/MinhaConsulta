package com.minhaConsulta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class ConfirmActivity extends ActionBarActivity {
	Button btnConfirm;
	EditText textCode;
	public SharedPreferences settings;
	public ConnectionDetector cd;
	ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm);
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		cd=new ConnectionDetector(this);
		
		btnConfirm = (Button)findViewById(R.id.button1);
		btnConfirm.setEnabled(false);
		TextView txtCodeview = (TextView)findViewById(R.id.textCodeView);
		txtCodeview.setText(settings.getString("reg_id",""));
		textCode = (EditText)findViewById(R.id.editText1);
		textCode.addTextChangedListener(new TextWatcher() {
			
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
				if(textCode.getText().toString().equalsIgnoreCase(settings.getString("reg_id",""))){
					btnConfirm.setEnabled(true);
				}
			}
		});
		
		btnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(textCode.getText().toString().equalsIgnoreCase(settings.getString("reg_id",""))){
					new registerTask().execute(true);
				}
			}
		});
		
		/*Button again = (Button)findViewById(R.id.button2);
		again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(settings.getString("app_mobile", ""), null, "Thanks for your order confirmation code is : "+ settings.getString("reg_id", ""), null, null);
			}
		});*/
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
        common.menuItemClick(ConfirmActivity.this, id);
        return super.onOptionsItemSelected(item);
	}
	
	class registerTask extends AsyncTask<Boolean, Void, String> {
		JSONObject finalData;
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ConfirmActivity.this, "", 
                    getResources().getString(R.string.loading_wait), true);
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
            HttpPost httppost = new HttpPost(ConstValue.JSON_CONFIRM_APPOINTMENT);
            //if (getEmailId()==null) {
            //	Toast.makeText(getApplicationContext(),"Create new Account Please",Toast.LENGTH_LONG).show();
            //	finish();
			//}

            try {
            
            	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            	 nameValuePairs.add(new BasicNameValuePair("app_id", settings.getString("app_id","")));
            	 nameValuePairs.add(new BasicNameValuePair("reg_id", settings.getString("reg_id","")));
            	 
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
        				finalData = jObj.getJSONObject("data");
	        			
	        			//if (!data.getString("app_id").equalsIgnoreCase("")) {
	        				//settings.edit().putString("app_id", data.getString("app_id")).commit();
	        				//settings.edit().putString("reg_id", data.getString("reg_id")).commit();
		                    //gcmAppRegister();
		                    
						//}
	        			
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
    			responseString = e.toString();
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

				if(finalData!=null){
					SmsManager smsManager = SmsManager.getDefault();
                    try {
                    	if(!finalData.getString("dr_phone").toString().equalsIgnoreCase("")) {

							String dr_message = getResources().getString(R.string.sms_string_for_dr);

							dr_message.replace("#appno#",finalData.getString("id"))
									.replace("#username#",finalData.getString("name"))
									.replace("#mobile#",finalData.getString("phone"))
									.replace("#date",finalData.getString("app_date"))
									.replace("#time", finalData.getString("time"))
									.replace("#amount",settings.getString("totalamount", ""));

							smsManager.sendTextMessage(finalData.getString("dr_phone").toString(), null,dr_message, null, null);
						}
						if(!finalData.getString("phone").toString().equalsIgnoreCase("")) {

							String user_message = getResources().getString(R.string.sms_string_for_user);

							user_message.replace("#appno#",finalData.getString("id"))
									.replace("#drname#",finalData.getString("dr_name"))
									.replace("#date",finalData.getString("app_date"))
									.replace("#time", finalData.getString("time"))
									.replace("#amount",settings.getString("totalamount", ""));

							smsManager.sendTextMessage(finalData.getString("phone").toString(), null,user_message, null, null);
						}
					}catch (Exception e) {
	                    Toast.makeText(getApplicationContext(),
	                            getResources().getString(R.string.sms_send_failed),
	                            Toast.LENGTH_LONG).show();
	                    e.printStackTrace();
	                }
				
				Intent intent = new Intent(ConfirmActivity.this,MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
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
