package com.minhaConsulta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;

import adapters.TimeTableAdapter;
import appconfig.ConstValue;
import imgLoader.JSONParser;
import util.ConnectionDetector;

/**
 * Created by subhashsanghani on 9/5/16.
 */
public class ScreenSlidePageFragment extends Fragment {
    //String output;
    public SharedPreferences settings;
    public ConnectionDetector cd;
    ArrayList<HashMap<String, String>> time_table;
    ArrayList<HashMap<String, String>> newsArray;
    TimeTableAdapter adaptertime;
    JSONObject j_clinic;
    String selected_date;
    JSONParser jParser;
    JSONObject json;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.row_timetable_slide, container, false);

        newsArray = new ArrayList<HashMap<String,String>>();

        SharedPreferences settings = this.getActivity().getSharedPreferences(ConstValue.MAIN_PREF, Context.MODE_PRIVATE);

        Bundle args = getArguments();
        j_clinic =  ConstValue.selected_clinic;
        selected_date = args.getString("selected_date");
        time_table = new ArrayList<HashMap<String,String>>();
        try {
            if(j_clinic.get("times") instanceof JSONArray){
                final JSONArray times_Array = j_clinic.getJSONArray("times");
                if(times_Array.length() > 0){

                    jParser = new JSONParser();

                    json = jParser.getJSONFromUrl(ConstValue.JSON_MY_APPOINTMENT);

                    for (int i = 0; i < times_Array.length(); i++) {

                        JSONObject o = times_Array.getJSONObject(i);
                        String aux1 = o.getString("day");
                        String aux2 = ((AppointmentActivity)getActivity()).changeDateTitle(args.getInt("day"));
                        aux1 = removeAcentos(aux1);
                        aux2 = removeAcentos(aux2);
                        if(aux1.equalsIgnoreCase( aux2 )){

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("day", o.getString("day"));
                            map.put("during", o.getString("during"));
                            time_table.add(map);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        ListView listview = (ListView)rootView.findViewById(R.id.listView1);
        adaptertime = new TimeTableAdapter(getActivity(), time_table);
        listview.setAdapter(adaptertime);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(),Appointment2Activity.class);
                try {
                    intent.putExtra("clinic_id", j_clinic.getString("cl_id"));
                    intent.putExtra("dr_id", j_clinic.getString("dr_id"));
                    intent.putExtra("cl_fees", j_clinic.getString("cl_fees"));
                    intent.putExtra("cl_discount", j_clinic.getString("cl_discount"));
                    intent.putExtra("app_date", selected_date);
                    intent.putExtra("day",time_table.get(position).get("day") );
                    intent.putExtra("time",time_table.get(position).get("during") );
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
        return rootView;
    }

    public String removeAcentos(String str) {

        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("[^\\p{ASCII}]", "");
        return str;

    }

}
