package cn.breaksky.rounds.publics.services.sysreciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.breaksky.rounds.publics.services.ListenerService;

public class UserPresentReciver extends BroadcastReceiver {
	private final static String TAG = "UserPresentReciver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		ListenerService.startService(context);
	}

}
