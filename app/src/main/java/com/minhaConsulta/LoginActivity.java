package com.minhaConsulta;

//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.LoginButton;
//import com.facebook.widget.LoginButton.UserInfoChangedCallback;

import util.ConnectionDetector;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
		import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import appconfig.ConstValue;

public class LoginActivity extends ActionBarActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	Button btnLogin, btnRegister;
//	LoginButton loginButton;
//	private UiLifecycleHelper uiHelper;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//this.getActionBar().hide();
		
		 settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		 cd=new ConnectionDetector(this);
		 
		 btnLogin = (Button)findViewById(R.id.buttonLogin);
		
		 btnRegister = (Button)findViewById(R.id.buttonRegister);
		 
		 btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,Login2Activity.class);
				startActivity(intent);
				
			}
		});
		 btnRegister.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
					startActivity(intent);
					
				}
			});
		 
		
//		 uiHelper = new UiLifecycleHelper(this, statusCallback);
//		uiHelper.onCreate(savedInstanceState);
//			
//		 loginButton = (LoginButton)findViewById(R.id.facebookloginbutton);
//		 loginButton.setReadPermissions(Arrays.asList("email"));
//
//		 
//		// Callback registration
//		    loginButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
//				
//				@Override
//				public void onUserInfoFetched(GraphUser user) {
//					// TODO Auto-generated method stub
//					if (user != null) {
//						
//					}
//				}
//			});
	}

//	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
//		@Override
//		public void call(Session session, SessionState state,
//				Exception exception) {
//			if (state.isOpened()) {
//				Log.d("MainActivity", "Facebook session opened.");
//			} else if (state.isClosed()) {
//				Log.d("MainActivity", "Facebook session closed.");
//			}
//		}
//	};
//	@Override
//	public void onResume() {
//		super.onResume();
//		uiHelper.onResume();
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		uiHelper.onPause();
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		uiHelper.onDestroy();
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		uiHelper.onActivityResult(requestCode, resultCode, data);
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle savedState) {
//		super.onSaveInstanceState(savedState);
//		uiHelper.onSaveInstanceState(savedState);
//	}
}
