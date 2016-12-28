package adapters;

import imgLoader.AnimateFirstDisplayListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


import com.minhaConsulta.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAppointmentAdapter extends BaseAdapter implements ImageGetter {
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	private Context context;
	private ArrayList<HashMap<String, String>> postItems;
	public SharedPreferences settings;
	public final String PREFS_NAME = "Magazine";
	Double cLat,cLog;
	DisplayImageOptions options;
	ImageLoaderConfiguration imgconfig; 
	int count = 0;
	
	public MyAppointmentAdapter(Context context, ArrayList<HashMap<String, String>> arraylist){
		this.context = context;
		
		File cacheDir = StorageUtils.getCacheDirectory(context);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new SimpleBitmapDisplayer())
		.imageScaleType(ImageScaleType.EXACTLY)
		.build();
		
		imgconfig = new ImageLoaderConfiguration.Builder(context)
		.build();
		ImageLoader.getInstance().init(imgconfig);
			
		postItems = arraylist;
		settings = context.getSharedPreferences(PREFS_NAME, 0);		
		
	}

	@Override
	public int getCount() {
		return postItems.size();
	}
	@Override
	public Object getItem(int position) {		
		return postItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
	            LayoutInflater mInflater = (LayoutInflater)
	            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	            convertView = mInflater.inflate(R.layout.row_appointment_list, null);	
	           
	     
	        }
			HashMap<String, String> map = new HashMap<String, String>();
			map = postItems.get(position);
			
			TextView txttitle = (TextView)convertView.findViewById(R.id.textContent);
			txttitle.setText(map.get("dr_name")+"("+map.get("dr_degree")+")");
			

			TextView txtname= (TextView)convertView.findViewById(R.id.textName);
			txtname.setText(map.get("app_date")+" "+map.get("day"));

			TextView txtdate= (TextView)convertView.findViewById(R.id.textDate);
			txtdate.setText(map.get("time"));
			
        return convertView;
	}

	@Override
	public Drawable getDrawable(String source) {
		// TODO Auto-generated method stub
		return null;
	}

}

