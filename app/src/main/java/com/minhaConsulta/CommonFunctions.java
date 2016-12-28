package com.minhaConsulta;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class CommonFunctions {
	public void menuItemClick(Activity act,int id){
		Intent intent = null;
		String shareurl = "https://play.google.com/store/apps/details?id=" + act.getPackageName();
		switch (id) {
		case R.id.action_settings:
			intent = new Intent(act,SettingsActivity.class);
			break;
		case R.id.action_about:
			intent = new Intent(act,AboutusActivity.class);
			break;
		case R.id.action_profile:
			intent = new Intent(act,ProfileActivity.class);
			break;
		case R.id.action_appointment:
			intent = new Intent(act,MyAppointmentActivity.class);
			break;
		case R.id.action_share:

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, shareurl);
			sendIntent.setType("text/plain");
			act.startActivity(sendIntent);
			break;

			case R.id.action_rate:

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shareurl));
				act.startActivity(browserIntent);
				break;
			default:
				break;
		}
		
		if(intent!=null){
			act.startActivity(intent);
		}
	}
}
