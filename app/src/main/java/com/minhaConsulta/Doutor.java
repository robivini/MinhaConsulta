package com.minhaConsulta;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import adapters.DrListAdapter;
import adapters.MainAdapter;
import appconfig.ConstValue;
import gcm.QuickstartPreferences;
import gcm.RegistrationIntentService;
import imgLoader.AnimateFirstDisplayListener;
import imgLoader.JSONParser;
import util.ConnectionDetector;
import util.ObjectSerializer;

/**
 * Created by Vinicius on 03/01/2017.
 */

public class Doutor extends ActionBarActivity {
    public SharedPreferences settings;
    public ConnectionDetector cd;
    int REQUEST_PLACE_PICKER = 1;
    ListView listdoctor;
    DrListAdapter adapter;
    ArrayList<HashMap<String, String>> doctoryarray;
    public int current_page, total_page;
    ListView listview;
    FrameLayout listfooterview;
    Button btnloadmore;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
        cd=new ConnectionDetector(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        current_page = 1;
        total_page = 0;
        doctoryarray = new ArrayList<HashMap<String,String>>();
        try {
            doctoryarray = (ArrayList<HashMap<String,String>>) ObjectSerializer.deserialize(settings.getString("doctors"+ConstValue.selected_category.get("id"), ObjectSerializer.serialize(new ArrayList<HashMap<String,String>>())));
        }catch (IOException e) {
            e.printStackTrace();
        }



        new Doutor.loadDrListTask().execute(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listdoctor, menu);
        //MenuItem searchItem = menu.findItem(R.id.action_search);
       // SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //SearchManager searchManager =
        //        (SearchManager) getSystemService(Context.SEARCH_SERVICE);

       // searchView.setSearchableInfo(
        //        searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        CommonFunctions common = new CommonFunctions();
        common.menuItemClick(Doutor.this, id);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }
            Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
            //mViewName.setText(name);
            //mViewAddress.setText(address);
            //mViewAttributions.setText(Html.fromHtml(attributions));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public class loadDrListTask extends AsyncTask<Boolean, Void, ArrayList<HashMap<String, String>>> {

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
                settings.edit().putString("doctors"+ConstValue.selected_category.get("id"),ObjectSerializer.serialize(doctoryarray)).apply();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(current_page < total_page){


                btnloadmore.setText(getResources().getString(R.string.load_more));
                btnloadmore.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);


                btnloadmore.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        current_page++;
                        btnloadmore.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);

                        new Doutor.loadDrListTask().execute(true);
                    }
                });
            }else{
                btnloadmore.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);

            }
            adapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

        @Override
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
                    String query = "";
                    if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
                        query = getIntent().getStringExtra(SearchManager.QUERY);
                        //use the query to search your data somehow
                    }
                    String urlstring = ConstValue.JSON_DR_LIST+"&cat_id="+ConstValue.selected_category.get("id")+"&city="+settings.getString("selected_city", "")+"&go="+current_page+"&search="+query;

                    json = jParser.getJSONFromUrl(ConstValue.JSON_DR_LIST+"&cat_id="+ConstValue.selected_category.get("id")+"&city="+settings.getString("selected_city", "")+"&go="+current_page+"&search="+query);
                    if (json.has("data")) {

                        if(json.get("data") instanceof JSONArray){

                            JSONArray jsonDrList = json.getJSONArray("data");

                            if(current_page==1)
                                doctoryarray.clear();

                            current_page = Integer.parseInt(json.getString("current_page"));
                            total_page = Integer.parseInt(json.getString("total_pages"));

                            for (int i = 0; i < jsonDrList.length(); i++) {
                                JSONObject obj = jsonDrList.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<String, String>();


                                map.put("dr_id", obj.getString("dr_id"));

                                map.put("cat_id", obj.getString("cat_id"));
                                map.put("dr_name", obj.getString("dr_name"));
                                map.put("dr_gender", obj.getString("dr_gender"));
                                map.put("dr_email", obj.getString("dr_email"));
                                map.put("dr_degree", obj.getString("dr_degree"));
                                map.put("dr_designation", obj.getString("dr_designation"));
                                map.put("dr_experiance", obj.getString("dr_experiance"));
                                map.put("dr_fees", obj.getString("dr_fees"));
                                map.put("dr_description", obj.getString("dr_description"));
                                map.put("dr_country", obj.getString("dr_country"));
                                map.put("dr_city", obj.getString("dr_city"));
                                map.put("dr_phone", obj.getString("dr_phone"));
                                map.put("dr_speciality", obj.getString("dr_speciality"));

                                map.put("dr_cover_image", obj.getString("dr_cover_image"));
                                map.put("dr_banner_image", obj.getString("dr_banner_image"));
                                map.put("cover_path", obj.getString("cover_path"));
                                map.put("banner_path", obj.getString("banner_path"));
                                map.put("avg", obj.getString("avg"));
                                map.put("count", obj.getString("count"));
                                //map.put("time_table", obj.getString("time_table"));
                                //if(obj.get("clinic") instanceof JSONArray){
                                String clinics = obj.get("clinic").toString();
                                map.put("clinic", clinics);
                                //}
                                //if(obj.get("times") instanceof JSONArray){
                                //	String times = obj.get("times").toString();
                                //	map.put("times", times);
                                //}
                                doctoryarray.add(map);


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
            return null;
        }

    }
}
