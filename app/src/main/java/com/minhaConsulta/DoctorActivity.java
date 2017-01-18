package com.minhaConsulta;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.minhaConsulta.AppointmentActivity;
import com.minhaConsulta.CommonFunctions;
import com.minhaConsulta.FbLoginActivity;
import com.minhaConsulta.MapActivity;
import com.minhaConsulta.ProfileActivity;
import com.minhaConsulta.R;
import com.minhaConsulta.ReviewsActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.pkmmte.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import appconfig.ConstValue;
import imgLoader.AnimateFirstDisplayListener;
import util.ConnectionDetector;

public class DoctorActivity extends ActionBarActivity {
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	DisplayImageOptions options;
	ImageLoaderConfiguration imgconfig; 
	
	HashMap<String, String> j_doctor;
	public SharedPreferences settings;
	public ConnectionDetector cd;

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor);
	
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		cd=new ConnectionDetector(this);
		
		File cacheDir = StorageUtils.getCacheDirectory(this);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new SimpleBitmapDisplayer())
		.imageScaleType(ImageScaleType.NONE)
		.build();
		
		imgconfig = new ImageLoaderConfiguration.Builder(this)
		.build();
		ImageLoader.getInstance().init(imgconfig);
		
		j_doctor = ConstValue.selected_doctor;

		TextView txtName = (TextView)findViewById(R.id.textDrName);
		TextView txtDegree = (TextView)findViewById(R.id.textDrDegree);
		TextView txtExpr = (TextView)findViewById(R.id.textDrExp);
		//TextView txtFees = (TextView)findViewById(R.id.textDrFee);
		//TextView txtDescription = (TextView)findViewById(R.id.textDescription);
		TextView txtDesignation = (TextView)findViewById(R.id.textDrDesc);
		TextView txtSpeciality = (TextView)findViewById(R.id.textDrSpeciality);
		WebView webview = (WebView)findViewById(R.id.webView1);
//		RatingBar ratingbar = (RatingBar)findViewById(R.id.ratingBar1);
//		ratingbar.setRating(0);
		ImageView imageBanner = (ImageView)findViewById(R.id.imageBanner);
		//ImageView imageCover = (ImageView)findViewById(R.id.imageView1);
		CircularImageView imageCover = (CircularImageView)findViewById(R.id.imageView1);
		try {
			ImageLoader.getInstance().displayImage(j_doctor.get("banner_path"), imageBanner, options, animateFirstListener);
			ImageLoader.getInstance().displayImage(j_doctor.get("cover_path"), imageCover, options, animateFirstListener);
			txtName.setText(j_doctor.get("dr_name"));
			txtDegree.setText(j_doctor.get("dr_degree"));
			txtDesignation.setText(j_doctor.get("dr_designation"));
//			if(!j_doctor.get("avg").equalsIgnoreCase("") && !j_doctor.get("avg").equalsIgnoreCase("null") ){
//			ratingbar.setRating(Float.parseFloat((j_doctor.get("avg"))));
//			}
			//txtDescription.setText(Html.fromHtml(j_doctor.get("dr_description").toString()));
//			WebSettings wevsettings = webview.getSettings();
//			wevsettings.setDefaultTextEncodingName("utf-8");
			//webview.loadData("<style> *{ font-size : 12px; } a{ text-decoration:none; color : #000000; } h1,h2,h3,h4,h5,h6 { font-size : 16px;  }</style>"+j_doctor.get("dr_description"), "text/html; charset=utf-8", null);
//			 try {
//				webview.loadData(URLEncoder.encode("<style> *{ font-size : 12px; } a{ text-decoration:none; color : #000000; } h1,h2,h3,h4,h5,h6 { font-size : 16px;  }</style>"+j_doctor.get("dr_description"), "utf-8").replaceAll("\\+", "%20"), "text/html; charset=utf-8", "UTF-8");
//			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			txtExpr.setText(j_doctor.get("dr_experiance")+" "+getResources().getString(R.string.yr_expr));
			//txtFees.setText(j_doctor.get("dr_fees")+getResources().getString(R.string.currency));
			txtSpeciality.setText(j_doctor.get("dr_speciality"));
			
			LinearLayout clinics_list_view = (LinearLayout)findViewById(R.id.list_clinics);

			if(!j_doctor.get("clinic").toString().equalsIgnoreCase("")){
				final JSONArray clinics = new JSONArray(j_doctor.get("clinic").toString());
				if(clinics.length() > 0){
					for (int i = 0; i < clinics.length(); i++) {
						final JSONObject clinic = clinics.getJSONObject(i);
						LayoutInflater inflator = LayoutInflater.from(this);
				        View v = inflator.inflate(R.layout.row_clinics_list, null);
				        
				        TextView textClinic = (TextView)v.findViewById(R.id.textClinicName);
				        textClinic.setText(clinic.getString("cl_name"));
				        TextView textAddress = (TextView)v.findViewById(R.id.textClinicAddress);
				        textAddress.setText(clinic.getString("cl_address"));
				        TextView textLocation = (TextView)v.findViewById(R.id.textClinicLocation);
				        textLocation.setText(clinic.getString("cl_location"));
				        
//				        final WebView webView1 = (WebView)v.findViewById(R.id.webView1);
//				        //final TextView textTimeTalbe = (TextView)v.findViewById(R.id.textTimeTable);
//				        //textTimeTalbe.setText(Html.fromHtml(clinic.getString("time_table").toString()));
//				        webView1.setVisibility(View.GONE);
//				        webView1.loadData(clinic.getString("time_table").toString(), "text/html", "UTF-8");
//				        
//				        LinearLayout timeButton = (LinearLayout)v.findViewById(R.id.layoutTimeBtn);
//				        timeButton.setOnClickListener(new OnClickListener() {
//							
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								if(webView1.getVisibility()==View.GONE)
//									webView1.setVisibility(View.VISIBLE);
//								else
//									webView1.setVisibility(View.GONE);
//							}
//						});
				        
				        LinearLayout galleryView = (LinearLayout)v.findViewById(R.id.layoutPhotos);
						if(clinic.get("photos") instanceof JSONArray){
							JSONArray photos = clinic.getJSONArray("photos");
							if(photos.length() > 0){
								int lenth = photos.length();
								//if(lenth > 3)
								//	lenth = 3;
								for (int j = 0; j < lenth; j++) {
									JSONObject jo = photos.getJSONObject(j);
									ImageView imgGal = new ImageView(this);
									ImageLoader.getInstance().displayImage(jo.getString("image_path"), imgGal, options, animateFirstListener);
									LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(45, 45);
									imgGal.setLayoutParams(layoutParams);
									imgGal.setBackgroundResource(R.drawable.xml_frame_border);
									galleryView.addView(imgGal);
								}
								
							}
						}

						ImageView imgViewCall = (ImageView) v.findViewById(R.id.imgViewCall);
						imgViewCall.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								try {
									call(clinic.getString("cl_phone"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});

						ImageView imgViewEmail = (ImageView) v.findViewById(R.id.imgViewEmail);
						imgViewEmail.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								try {
									email(clinic.getString("cl_phone"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});

						ImageView imgViewWebsite = (ImageView) v.findViewById(R.id.imgViewWebsite);
						imgViewWebsite.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								try {
									website(clinic.getString("cl_site"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});


						Button btnmap = (Button)v.findViewById(R.id.buttonLocation);
						btnmap.setContentDescription(i+"");
						btnmap.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String contentid = (String) v.getContentDescription();
								try {
									ConstValue.selected_clinic = clinics.getJSONObject(Integer.parseInt(contentid));
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Intent intent = new Intent(DoctorActivity.this,MapActivity.class);
								startActivity(intent);
							}
						});
						
						Button btnBook = (Button)v.findViewById(R.id.buttonBook);
						btnBook.setContentDescription(i+"");
						btnBook.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(settings.getString("user_id", "").equalsIgnoreCase("")){
									Intent intent = new Intent(DoctorActivity.this,FbLoginActivity.class);
									startActivity(intent);
								}else{
									if(!settings.getBoolean("mobile_confirm", false)){
										Intent intent = new Intent(DoctorActivity.this,ProfileActivity.class);
										startActivity(intent);
									}else{
										String contentid = (String) v.getContentDescription();
										try {
											ConstValue.selected_clinic = clinics.getJSONObject(Integer.parseInt(contentid));
										} catch (NumberFormatException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										Intent intent = new Intent(DoctorActivity.this,AppointmentActivity.class);
										startActivity(intent);
									}
								}
							}
						});
						
				        clinics_list_view.addView(v);
					}
				}
			}
			
	        
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Button btnbook = (Button)findViewById(R.id.buttonBook);
//		btnbook.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(DoctorActivity.this,AppointmentActivity.class);
//				startActivity(intent);
//			}
//		});
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
        common.menuItemClick(DoctorActivity.this, id);
        return super.onOptionsItemSelected(item);
	}

	private void call(String phoneNo) {
		(this).permissionCall(phoneNo);
	}

	private void website(String url) {
		if(url == null || url.length() == 0) {
			Toast.makeText(this, "No website provided.", Toast.LENGTH_SHORT).show();
			return;
		}
		String strUrl = url;
		if(!url.contains("http")) {
			strUrl = "http://" + url;
		}
		Intent webIntent = new Intent(Intent.ACTION_VIEW);
		webIntent.setData(Uri.parse(strUrl));
		this.startActivity(Intent.createChooser(webIntent, "Choose a browser :"));
	}

	private void email(String email) {
		if(email == null || email.length() == 0) {
			Toast.makeText(this, "No email provided.", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email} );
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Restaurant Inquiry");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "Your Message Here...");
		emailIntent.setType("message/rfc822");
		this.startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
	}

	public void permissionCall(String phoneNo) {

		if (phoneNo == null || phoneNo.length() == 0) {
			Toast.makeText(this, "No phone number provided.", Toast.LENGTH_SHORT).show();
			return;
		}

		String uri = "tel:" + phoneNo.trim();
		Log.i("", uri);
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse(String.valueOf(Uri.parse(uri))));
		startActivity(intent);
	}
}
