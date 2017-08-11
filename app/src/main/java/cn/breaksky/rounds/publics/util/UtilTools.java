package cn.breaksky.rounds.publics.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.services.MainService;

import com.alibaba.fastjson.JSON;

@SuppressLint("NewApi")
public class UtilTools {

	/**
	 * 获取配置参数
	 * 
	 * @param i
	 *            key resource
	 * */
	public static String getConfigValue(int i) {
		return getConfigValue(getResource(i));
	}

	/**
	 * 获取配置参数
	 * 
	 * @param i
	 *            key resource
	 * */
	public static boolean getConfigValueBoolean(int i) {
		return getConfigValueBoolean(getResource(i));
	}

	/**
	 * 获取配置参数
	 * 
	 * @param i
	 *            key resource
	 * */
	public static String getConfigValue(String key) {
		return getPre().getString(key, null);
	}

	/**
	 * 获取配置参数
	 * 
	 * @param i
	 *            key resource
	 * */
	public static Boolean getConfigValueBoolean(String key) {
		return getPre().getBoolean(key, false);
	}

	/**
	 * 设置配置参数
	 * */
	public static void setConfigValue(int i, String value) {
		setConfigValue(UtilTools.getResource(i), value);
	}

	/**
	 * 设置配置参数
	 * */
	public static void setConfigValue(int i, boolean value) {
		setConfigValue(UtilTools.getResource(i), value);
	}

	/**
	 * 设置配置参数
	 * */
	public static void setConfigValue(String key, String value) {
		SharedPreferences.Editor sharedata = getPre().edit();
		sharedata.putString(key, value);
		sharedata.commit();
	}

	/**
	 * 设置配置参数
	 * */
	public static void setConfigValue(String key, Boolean value) {
		SharedPreferences.Editor sharedata = getPre().edit();
		sharedata.putBoolean(key, value);
		sharedata.commit();
	}

	private static SharedPreferences getPre() {
		String shareName = ContextManager.getInstance().getPackageName() + "_preferences";
		return ContextManager.getInstance().getSharedPreferences(shareName, Context.MODE_PRIVATE);
	}

	/**
	 * 获取URL地址
	 * */
	public static String getURL(int i) {
		String path = getResource(i);
		return MainService.getInstance().roundsConfig.SERVICE_URL + path;
	}

	/**
	 * 获取资源
	 * */
	public static String getResource(int i) {
		return ContextManager.getInstance().getResources().getText(i).toString();
	}

	/**
	 * bytes 转JSON
	 * */
	public static JSONObject byteToJsonCoding(byte[] bytes, String coding) throws Exception {
		JSONTokener jsonParser;
		if (coding != null) {
			jsonParser = new JSONTokener(new String(bytes, coding));
		} else {
			jsonParser = new JSONTokener(new String(bytes));
		}
		return (JSONObject) jsonParser.nextValue();
	}

	public static <T> T jsonToBean(JSONObject obj, Class<T> clazz) {
		return JSON.parseObject(obj.toString(), clazz);
	}

	public static <T> List<T> jsonToBean(JSONArray obj, Class<T> clazz) {
		return JSON.parseArray(obj.toString(), clazz);
	}

	/**
	 * 提示框
	 * */
	public static void alertMessage(Context context, String message) {
		alert(context, "提示", message, null);
	}

	/**
	 * 提示框
	 * */
	public static void alertMessage(Context context, String message, AlertCallBack callBack) {
		alert(context, "提示", message, callBack);
	}

	/**
	 * 错误框
	 * */
	public static void alertError(Context context, String message) {
		alert(context, "错误", message, null);
	}

	/**
	 * 错误框
	 * */
	public static void alertError(Context context, String message, AlertCallBack callBack) {
		alert(context, "错误", message, callBack);
	}

	public interface AlertCallBack {
		public void callBack();
	}

	private static void alert(Context context, String title, String message, final AlertCallBack callBack) {
		AlertDialog alert = new AlertDialog.Builder(context, R.style.Dialog).create();
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (callBack != null) {
					callBack.callBack();
				}
			}
		});
		alert.show();
	}

	public static ProgressDialog createProgressDialog(Context context, String message, boolean touchCancel) {
		ProgressDialog loading_Dialog = new ProgressDialog(context, R.style.Dialog);
		loading_Dialog.setMessage(message);
		loading_Dialog.show();
		loading_Dialog.setCanceledOnTouchOutside(touchCancel);
		return loading_Dialog;
	}

	public static ProgressDialog createProgressDialog(Context context, String message) {
		return createProgressDialog(context, message, true);
	}

	public static void toast(Context context, String message) {
		// Log.i("Toast", message);
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		// toast.getView().setBackgroundColor(context.getResources().getColor(R.color.white));
		toast.show();
	}

	public static String formatDate() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		return formatter.format(date);
	}

	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		return formatter.format(date);
	}

	public static String formatDate(Date date, String str) {
		SimpleDateFormat formatter = new SimpleDateFormat(str, Locale.CHINA);
		return formatter.format(date);
	}

	public static Date stringToDate(String date, String mat) {
		mat = mat == null ? "yyyy-MM-dd_HH:mm:ss" : mat;
		SimpleDateFormat format = new SimpleDateFormat(mat, Locale.CHINA);
		Date formatdate = null;
		try {
			formatdate = format.parse(date);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return formatdate;
	}

	public static String getVersionName() {
		PackageManager pm = ContextManager.getInstance().getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(ContextManager.getInstance().getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			return "未知版本";
		}
	}

	public static int getVersionCode() {
		PackageManager pm = ContextManager.getInstance().getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(ContextManager.getInstance().getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}

	public static String getSizeStr(long size) {
		try {
			if (size < 1024) {
				return size + "B";
			} else if (size >= 1024 && size < 1048576) {
				DecimalFormat df = new DecimalFormat("###.0");
				return df.format(size / 1024.0) + "KB";
			} else if (size >= 1048576 && size < 1073741824) {
				DecimalFormat df = new DecimalFormat("###.00");
				return df.format(size / 1024.0 / 1024.0) + "MB";
			} else {
				DecimalFormat df = new DecimalFormat("###.000");
				return df.format(size / 1024.0 / 1024.0 / 1024.0) + "GB";
			}
		} catch (Exception e) {
			return "NaN";
		}
	}

	public static void beginVibrator() {
		Vibrator mVibrator01 = (Vibrator) ContextManager.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
		mVibrator01.vibrate(new long[] { 100, 10, 100, 1000 }, -1);
	}

	public static void beginVoice() {
		try {
			RingtoneManager.getRingtone(ContextManager.getInstance(), RingtoneManager.getActualDefaultRingtoneUri(ContextManager.getInstance(), RingtoneManager.TYPE_NOTIFICATION)).play();
		} catch (Exception e) {

		}

	}

	private static TextToSpeech TALKING = null;

	public static void talkText(String text) {
		if (TALKING == null) {
			TALKING = new TextToSpeech(ContextManager.getInstance(), new TalkListener(text));
		} else {
			TALKING.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
		}
	}

	public static class TalkListener implements OnInitListener {
		private String text;

		public TalkListener(String text) {
			this.text = text;
		}

		@Override
		public void onInit(int status) {
			if (status == TextToSpeech.SUCCESS) {
				int result = TALKING.setLanguage(Locale.CHINESE);
				if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

				} else {
					TALKING.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
				}
			}
		}
	}

	/**
	 * 对包含中文的字符串进行转码，此为UTF-8。服务器那边要进行一次解码
	 * */
	public static String encodeUTF8(String value) throws Exception {
		return URLEncoder.encode(value, "UTF-8");
	}

	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = isIPv4(sAddr);
						if (useIPv4) {
							if (isIPv4) {
								return sAddr;
							}
						} else {
							if (!isIPv4) {
								// drop ip6 port suffix
								int delim = sAddr.indexOf('%');
								return delim < 0 ? sAddr : sAddr.substring(0, delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
		return "";
	}

	public static boolean isIPv4(String ipv4) {
		if (ipv4 == null || ipv4.length() == 0) {
			return false;// 字符串为空或者空串
		}
		// 因为java doc里已经说明, split的参数是reg,
		// 即正则表达式, 如果用"|"分割, 则需使用"\\|"
		String[] parts = ipv4.split("\\.");
		if (parts.length != 4) {
			return false;// 分割开的数组根本就不是4个数字
		}
		for (int i = 0; i < parts.length; i++) {
			try {
				int n = Integer.parseInt(parts[i]);
				if (n < 0 || n > 255) {
					return false;// 数字不在正确范围内
				}
			} catch (NumberFormatException e) {
				return false;// 转换数字不正确
			}
		}
		return true;
	}

	/**
	 * 动态生成View ID API LEVEL 17 以上View.generateViewId()生成 API LEVEL 17 以下需要手动生成
	 */
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	public static int generateViewId() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			for (;;) {
				final int result = sNextGeneratedId.get();
				// aapt-generated IDs have the high byte nonzero; clamp to the
				// range under that.
				int newValue = result + 1;
				if (newValue > 0x00FFFFFF)
					newValue = 1; // Roll over to 1, not 0.
				if (sNextGeneratedId.compareAndSet(result, newValue)) {
					return result;
				}
			}
		} else {
			return View.generateViewId();
		}
	}

	public static byte[] stringTobyte(String str, int len) {
		if (str.length() == len) {
			return str.getBytes();
		} else {
			int oldLen = str.length();
			if (len <= oldLen) {
				return (str.substring(0, len)).getBytes();
			} else {
				StringBuffer s = new StringBuffer();
				s.append(str);
				for (int i = 0; i < len - oldLen; i++) {
					s.append(" ");
				}
				return s.toString().getBytes();
			}
		}
	}

	public static int stringToInt(String str, int def) {
		try {
			return Integer.valueOf(str);
		} catch (Exception e) {
			return def;
		}
	}

	public static float stringToFloat(String str, float def) {
		try {
			return Float.valueOf(str);
		} catch (Exception e) {
			return def;
		}
	}

	public static byte[] intToBytes(int x) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(0, x);
		return buffer.array();
	}

	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(0, x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();// need flip
		return buffer.getLong();
	}

	public static int bytesToInt(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();// need flip
		return buffer.getInt();
	}

	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		} else {
			sdDir = Environment.getDataDirectory();
		}
		return sdDir.toString();
	}

	public static boolean isServiceWorked(Class<?> className, Context context) {
		ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString().equals(className.getName())) {
				return true;
			}
		}
		return false;
	}

	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String id = telephonyManager.getDeviceId();
		if (id == null) {
			id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
		}
		return id;
	}

	// 开启定时服务
	public static void startBroadcast(Context context, int seconds, Class<?> cls, String action) {
		// 获取AlarmManager系统服务
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), seconds, pendingIntent);
	}

	// 停止定时服务
	public static void stopBroadcast(Context context, Class<?> cls, String action) {
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 取消
		manager.cancel(pendingIntent);
	}

	/**
	 * 存储对象
	 * */
	public static boolean saveObject(String path, Object obj) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(path);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			return true;
		} catch (Exception e) {
			Log.e("saveObject", e.getMessage());
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 读取对象
	 * */
	public static Object getObject(String path) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(path);
			ois = new ObjectInputStream(fis);
			return ois.readObject();
		} catch (Exception e) {
			Log.e("getObject", e.getMessage());
			return null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
