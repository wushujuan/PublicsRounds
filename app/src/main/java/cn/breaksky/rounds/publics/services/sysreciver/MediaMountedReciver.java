package cn.breaksky.rounds.publics.services.sysreciver;

import cn.breaksky.rounds.publics.services.ListenerService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MediaMountedReciver extends BroadcastReceiver{
	private final static String TAG = "MediaMountedReciver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		ListenerService.startService(context);
	}

}
