package cn.breaksky.rounds.publics.services.sysreciver;

import cn.breaksky.rounds.publics.services.ListenerService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReciver extends BroadcastReceiver {
	private final static String TAG = "BootCompletedReciver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		ListenerService.startService(context);
	}

}
