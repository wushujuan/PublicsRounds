package cn.breaksky.rounds.publics.activity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.services.ListenerService;
import cn.breaksky.rounds.publics.services.MainService;
//import cn.breaksky.rounds.publics.services.MarsDaemon;
import cn.breaksky.rounds.publics.util.UtilTools;
import static android.support.v4.app.ActivityCompat.*;

/**
 * 欢迎页面
 * @author Administrator
 *
 */
public class WelcomeActivity extends Activity {
	private final static int PERMISSION_CHECK = 10021;

	@Override
	protected void onCreate(Bundle bundle) {
		ContextManager.addActivity(this);
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//全屏去掉标题栏
		setContentView(R.layout.activity_welcome);
		
		TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
		versionNumber.setText("Version " + UtilTools.getVersionName());
	    Log.d("现在版本号","现在版本号"+UtilTools.getVersionCode());
		checkPermission();	//打开权限
	};
	/**
	 * 判断相应的权限是否打开
	 */
	private void checkPermission() {
		Map<String, String> permission = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put("android.permission.READ_PHONE_STATE", "读取电话状态");
				put("android.permission.ACCESS_WIFI_STATE", "获取WiFi状态");
				put("android.permission.WRITE_SETTINGS", "读写系统设置");
				put("android.permission.ACCESS_NETWORK_STATE", "获取网络状态");
				put("android.permission.MOUNT_UNMOUNT_FILESYSTEMS", "挂载文件系统");
				put("android.permission.READ_EXTERNAL_STORAGE", "读取设备外部存储空间");
				put("android.permission.WRITE_EXTERNAL_STORAGE", "写入外部存储");
				put("android.permission.INTERNET", "访问网络");
				put("android.permission.ACCESS_FINE_LOCATION", "获取精确位置");
				put("android.permission.VIBRATE", "使用振动");
				put("android.permission.RECORD_AUDIO", "录音");
				put("android.permission.RECEIVE_BOOT_COMPLETED", "开机自动允许");
				put("android.permission.BROADCAST_STICKY", "连续广播");
				put("android.permission.BROADCAST_PACKAGE_REMOVED", "应用删除时广播");
				put("android.permission.WAKE_LOCK", "唤醒锁定");
				put("android.permission.CAMERA", "访问摄像头");
			}
		};
		PackageManager pm = getPackageManager();
		Set<String> keys = permission.keySet();
		List<String> error = new ArrayList<String>();
		for (String per : keys) {
			boolean is = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(per, "cn.breaksky.rounds.publics"));
			if (!is) {
				error.add(per);
			}
		}
		if (!this.isNotificationEnabled()) {
			UtilTools.alertError(this, "请启用通知栏权限.");
		}
		if (error.size() > 0) {
			// 6.0以上系统才使用权限请求

			if (Build.VERSION.SDK_INT > 22) {
				ActivityCompat.requestPermissions(this, error.toArray(new String[error.size()]), PERMISSION_CHECK);
			} else {
				start();
			}
		} else {
			start();
		}
	};
	/**
	 * 判断通知栏权限
	 *
	 * @return
	 */
	@SuppressLint("NewApi")
	private boolean isNotificationEnabled() {
		AppOpsManager mAppOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
		ApplicationInfo appInfo = this.getApplicationInfo();
		String pkg = this.getApplicationContext().getPackageName();
		int uid = appInfo.uid;
		Class<?> appOpsClass = null;
		try {
			appOpsClass = Class.forName(AppOpsManager.class.getName());
			Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
			Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
			int value = (Integer) opPostNotificationValue.get(Integer.class);
			return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(Settings.ACTION_SETTINGS);
		startActivity(intent);
		return false;
	};
	/**
	 * 打开相应的服务
	 */
	private void start() {
		ListenerService.startService(ContextManager.getInstance());
	    //MarsDaemon daemon = new MarsDaemon();
		//daemon.createDaemonClient(ContextManager.getInstance());
        //欢迎页面关闭，进入主页面
		RoundHandler.runActivityMethod(this, 3000, "welcomeEnd");
	};
	/**
	 * 欢迎页面关闭，进入主页面
	 */
	public void welcomeEnd() {
		if (MainService.getInstance().isStarted()) {
			Intent intent = new Intent(this, MainActivity.class);
			this.startActivity(intent);
			this.finish();
		} else {
			RoundHandler.runActivityMethod(this, 100, "welcomeEnd");
		}
	};
	/**
	 * 6.0以上系统请求打开权限的回调事件
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@SuppressLint("Override")
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		start();
	}

}
