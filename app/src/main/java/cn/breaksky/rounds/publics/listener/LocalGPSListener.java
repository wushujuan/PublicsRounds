package cn.breaksky.rounds.publics.listener;

import java.util.Date;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.util.UtilTools;

public class LocalGPSListener implements LocationListener {
	private Context activity;
	private CallBack callBack;

	public LocalGPSListener(Context activity, CallBack callBack) {
		this.activity = activity;
		this.callBack=callBack;
		Integer time = Integer.valueOf(UtilTools.getConfigValue(R.string.gps_check_key));
		LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, 0, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			GPSData data = new GPSData();
			data.setLatitude(location.getLatitude());
			data.setLongitude(location.getLongitude());
			data.setAltitude(location.getAltitude());
			data.setTime(new Date());
			this.callBack.GPSData(data);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		UtilTools.toast(activity, "GPS未开启,正在开启...");
		this.openGPS(this.activity);
	}

	@Override
	public void onProviderEnabled(String provider) {
		UtilTools.toast(activity, "GPS定位中...");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// UtilTools.toast(activity, provider + " " + status);
	}

	public void openGPS(Context context) {
		Intent GPSIntent = new Intent();
		GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		GPSIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
		} catch (CanceledException e) {
			UtilTools.toast(activity, e.getMessage());
		}
	}

	public interface CallBack {
		public void GPSData(GPSData data);
	}
}
