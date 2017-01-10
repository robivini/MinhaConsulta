package com.minhaConsulta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import appconfig.ConstValue;

public class AppointmentActivity extends FragmentActivity  {
	private static final int NUM_PAGES = 90;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	 JSONObject j_clinic;
	TextView textDateTitle;
	String auxiliar;
	String selected_date;
	final Locale myLocale = new Locale("pt", "BR");
	int aux = 0;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment);

		textDateTitle = (TextView)findViewById(R.id.textDateTitle);


		j_clinic = ConstValue.selected_clinic;
		mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        changeDateTitle(0);
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				changeDateTitle(0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}
	@Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			Fragment fragment = new ScreenSlidePageFragment();
			Bundle b = new Bundle();
			b.putInt("day",arg0);
			b.putString("selected_date",selected_date);
			fragment.setArguments(b);
			return fragment;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return NUM_PAGES;
		}

    }


	public String changeDateTitle(int arg0){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, arg0);
		String day_of_week =c.getDisplayName(Calendar.DAY_OF_WEEK,  Calendar.LONG, myLocale);
		day_of_week = day_of_week.substring(0,3);
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String output = sdf1.format(c.getTime());

		selected_date = sdf2.format(c.getTime());
		auxiliar = output +" "+day_of_week;
		colocarTexto(auxiliar);
		return day_of_week;
	}

	private void colocarTexto(String auxiliar) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, mPager.getCurrentItem());
		String dia = c.getDisplayName(Calendar.DAY_OF_WEEK,  Calendar.LONG, myLocale);
		dia = dia.substring(0,3);
		dia = removeAcentos(dia);
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String output = sdf1.format(c.getTime());
		selected_date = sdf2.format(c.getTime());
		textDateTitle.setText(output +" "+dia);
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
        common.menuItemClick(AppointmentActivity.this, id);
        return super.onOptionsItemSelected(item);
	}

	public String removeAcentos(String str) {

		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = str.replaceAll("[^\\p{ASCII}]", "");
		return str;

	}
}
