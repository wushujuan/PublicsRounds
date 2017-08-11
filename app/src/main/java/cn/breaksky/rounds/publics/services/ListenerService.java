package cn.breaksky.rounds.publics.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cn.breaksky.rounds.publics.util.UtilTools;

/**
 * 服务：启动服务在后台运行进行监听
 * @author Administrator
 *
 */
public class ListenerService extends Service {
	private final static String TAG = "ListenerService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		Intent i = new Intent(MainService.SERVICE_DESTORY);
		sendBroadcast(i);
		super.onDestroy();
	}
    /**
     * android2.0以上则使用onstartCommand()方法
     * START_STICKY：如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象。
     * 随后系统会尝试重新创建service，由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。
     * 如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null。
     */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand()");
		MainService.getInstance().startService(this);
		return Service.START_STICKY;
	}
	public static void startService(Context context) {
		if (!UtilTools.isServiceWorked(ListenerService.class, context)) {
			Log.d(TAG, "startService");
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(context, ListenerService.class);
			context.startService(intent);	//启动ListenerService服务
		} else {
			Log.d(TAG, "startService Service is running");
		}
	}
}
