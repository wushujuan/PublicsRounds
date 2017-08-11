package cn.breaksky.rounds.publics.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.LongSparseArray;
import android.widget.RemoteViews;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.RoundsConfig;
import cn.breaksky.rounds.publics.activity.MainActivity;
import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;
import cn.breaksky.rounds.publics.bean.WorkBean;
import cn.breaksky.rounds.publics.db.DBHelper;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.listener.GPSData;
import cn.breaksky.rounds.publics.listener.GPSListener;
import cn.breaksky.rounds.publics.listener.RoundListener;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.HttpPostUtil;
import cn.breaksky.rounds.publics.util.request.Params;

/**
 * 全局类
 * @author Administrator
 *
 */
public class MainService {
	public final static String SERVICE_DESTORY = "cn.breaksky.rounds.publics.services_destory";
	public final static String WIDGET_VIDEO_ALARM = "cn.breaksky.rounds.publics.services.widget_video_alarm";
	public final static String WIDGET_VOICE_ALARM = "cn.breaksky.rounds.publics.services.widget_voice_alarm";
	public final static String WIDGET_PHOTO_ALARM = "cn.breaksky.rounds.publics.services.widget_photo_alarm";
	public final static String NOTIFICATION_EVENT = "cn.breaksky.rounds.publics.services.notification_event";
	// private
	private final static String SUM_SEND_LENGTH = "sum_send_length";
	private final static String SUM_READ_LENGTH = "sum_read_length";
	private final static String TAG = "MainService";
	private static MainService instance;
	private GPSListener GPSListener;
	private RoundListener roundListener;
	private RoundPersonnelVO user = null;
	private LongSparseArray<WorkBean> works = new LongSparseArray<WorkBean>();
	private Long NOW_WORK = null;
	private Context context;
	private long SAVED_SEND = 0;
	private long SAVED_READ = 0;
	private int n_id = 10;
	private int read_config_error_id = -1;
	private boolean isStarted = false;
	private List<String> showNID = new ArrayList<String>();
	public boolean mYuejie = false;
	public boolean mflag = false;
	/** <b>组id</b> */
	public String groupid;
	
	public long maxPMessage = 0;
	public long maxGather = 0;
	// public
	public RoundsConfig roundsConfig;

	public synchronized static MainService getInstance() {
		if (instance != null) {
			return instance;
		} else {
			Log.d(TAG, "New MainService");
			instance = new MainService();
		}
		return instance;
	}

	private MainService() {
		roundsConfig = new RoundsConfig();
	}

	public synchronized void startService(Service context) {
		if (isStarted) {
			Log.d(TAG, "startService Service Is Running");
			return;
		}
		this.context = context;
		getConfig();
		String rootPath = android.os.Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/";
		roundsConfig.setRootPath(rootPath);
		// 判断是否第一次运行
		if (UtilTools.getConfigValueBoolean(R.string.have_app_run_key) == false) {
			copyDefaultSetting();
			UtilTools.setConfigValue(R.string.have_app_run_key, true);
		}
		roundsConfig.dataBase = new DBHelper(context);
		
		getAppNetByteLength();

		if (!isListenerRun()) {
			beginListener(context);
		}
		// 创建快速操作栏
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.app_ico);
		// 设置按钮
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
		// 视频报警
		Intent videoAlarmIntent = new Intent().setAction(WIDGET_VIDEO_ALARM);
		views.setOnClickPendingIntent(R.id.widget_video_alarm, PendingIntent.getBroadcast(context, 0, videoAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		// 图片报警
		Intent photoAlarmIntent = new Intent().setAction(WIDGET_PHOTO_ALARM);
		views.setOnClickPendingIntent(R.id.widget_photo_alarm, PendingIntent.getBroadcast(context, 0, photoAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		// 语音报警
		Intent voiceAlarmIntent = new Intent().setAction(WIDGET_VOICE_ALARM);
		views.setOnClickPendingIntent(R.id.widget_voice_alarm, PendingIntent.getBroadcast(context, 0, voiceAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		//
		builder.setContent(views);
		// 点击进入主页
		Intent intent = new Intent(this.context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pendingIntent);
		// 显示为系统通知
		context.startForeground(1, builder.build());
		isStarted = true;
	}

	public boolean isStarted() {
		return isStarted;
	}

	/**
	 * 监听是否运行
	 * */
	private boolean isListenerRun() {
		if (roundListener == null || GPSListener == null) {
		//if (roundListener == null ) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 启动监听
	 * */
	private synchronized void beginListener(Context activity) {
		roundListener = new RoundListener(activity);
		GPSListener = new GPSListener(activity);
	}

	/**
	 * 读取配置
	 * */
	private void getConfig() {
		// 拷贝默认配置
		roundsConfig.SERVICE_URL = UtilTools.getResource(R.string.default_service_url);
		roundsConfig.SOCKET_URL = UtilTools.getResource(R.string.default_socket_url);
		roundsConfig.SOCKET_PORT = Integer.parseInt(UtilTools.getResource(R.string.default_socket_port));
		roundsConfig.REQUEST_TYPE = UtilTools.getResource(R.string.request_type);
		// 读取网络配置
		//RoundHandler.runMethod(this, 1, "readConfig");
	}

	/**
	 * 读取网络配置文件
	 * */
	public void readConfig() {
		try {
			HttpPostUtil http = new HttpPostUtil(UtilTools.getResource(R.string.app_config_url), 0);
			Params params = new Params();
			params.addTextParameter("clientid", UtilTools.getResource(R.string.app_client_id));
			byte[] b = http.send(params);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(b));
			Element root = document.getDocumentElement();
			NodeList childNodes = root.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node childNode = (Node) childNodes.item(j);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) childNode;
					String nodelName = childElement.getNodeName();
					String value = childElement.getFirstChild() == null ? "" : childElement.getFirstChild().getNodeValue();
					if ("service_url".equals(nodelName)) {
						roundsConfig.SERVICE_URL = value;
					} else if ("socket_url".equals(nodelName)) {
						roundsConfig.SOCKET_URL = value;
					} else if ("socket_port".equals(nodelName)) {
						roundsConfig.SOCKET_PORT = UtilTools.stringToInt(value, -1);
					}
				}
			}
		} catch (Exception e) {
			read_config_error_id = showNotification(null, null, "配置参数", "读取配置参数错误，请检查网络！", read_config_error_id);
			RoundHandler.runMethod(this, 5000, "readConfig");
		}
	}

	/**
	 * 第一次运行初始化配置
	 * */
	private void copyDefaultSetting() {
		UtilTools.setConfigValue(R.string.thread_check_time_key, UtilTools.getResource(R.string.thread_check_time_default));
		UtilTools.setConfigValue(R.string.gps_check_key, UtilTools.getResource(R.string.gps_check_time_default));
		UtilTools.setConfigValue(R.string.autorun_checkbox_key, true);
	}

	/**
	 * 读取保存的app传送流量
	 * */
	private void getAppNetByteLength() {
		String sendStr = UtilTools.getConfigValue(SUM_SEND_LENGTH);
		String readStr = UtilTools.getConfigValue(SUM_READ_LENGTH);
		if (sendStr != null) {
			SAVED_SEND = Long.valueOf(sendStr);
		}
		if (readStr != null) {
			SAVED_READ = Long.valueOf(readStr);
		}
	}

	public WorkBean getNowWork() {
		if (NOW_WORK != null) {
			return works.get(NOW_WORK);
		} else {
			return null;
		}
	}

	/**
	 * 保存应用程序流量
	 * */
	public void saveAppNetByteLength() {
		UtilTools.setConfigValue(SUM_SEND_LENGTH, String.valueOf(SAVED_SEND + roundsConfig.SEND_LENGTH));
		UtilTools.setConfigValue(SUM_READ_LENGTH, String.valueOf(SAVED_READ + roundsConfig.READ_LENGTH));
	}

	/**
	 * 获取总共发送的数据
	 * */
	public long getSumSend() {
		String send = UtilTools.getConfigValue(SUM_SEND_LENGTH);
		return send == null ? 0 : Long.valueOf(send);
	}

	/**
	 * 获取总共接收的数据
	 * */
	public long getSumRead() {
		String read = UtilTools.getConfigValue(SUM_READ_LENGTH);
		return read == null ? 0 : Long.valueOf(read);
	}

	public GPSData getLastGPS() {
		return GPSListener.getLashGPSData();
	}

	public void setNowWork(Long id) {
		NOW_WORK = id;
	}

	public void cleanWork() {
		works.clear();
	}

	public void addWork(WorkBean bean) {
		works.put(bean.getRw_id(), bean);
	}

	public RoundPersonnelVO getUser() {
		if (user == null) {
			return (RoundPersonnelVO) UtilTools.getObject(this.roundsConfig.OBJECT_SAVE_PATH + "MainService.user");
		} else {
			return user;
		}
	}

	public void setUser(RoundPersonnelVO user) {
		UtilTools.saveObject(this.roundsConfig.OBJECT_SAVE_PATH + "MainService.user", user);
		UtilTools.setConfigValue(R.string.save_password_key, user.getDevicekey());
		this.user = user;
	}

	/**
	 * 清理缓存
	 * */
	public void cleanCache() {
		String[] paths = roundsConfig.getRootPaths();
		for (String path : paths) {
			File[] files = (new File(path)).listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
		}
		UtilTools.toast(ContextManager.getInstance(), "清理完成");
	}

	/**
	 * 创建通知
	 * */
	public int showNotification(NOTIFICATION_EVENT event, String eventID, String title, String message, int id) {
		int r_id = 0;
		if (id == -1) {
			if (n_id == 2147483647) {
				n_id = 0;
			}
			n_id = n_id + 1;
			r_id = n_id;
		} else {
			r_id = id;
		}
		NotificationManager nManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
		Notification.Builder builder = new Notification.Builder(context);
		builder.setContentTitle(title);
		builder.setContentText(message);
		builder.setSmallIcon(R.drawable.app_ico);
		builder.setDefaults(Notification.DEFAULT_ALL);
		builder.setAutoCancel(true);

		Intent intentReciver = new Intent(this.context, ListenerServiceReciver.class); 	//打开广播监听
		intentReciver.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 解决新的Intent不会被启动，所以还是继续唤醒旧的Activity，这样就等于参数没有被更新。
		intentReciver.addFlags(Intent.FILL_IN_DATA);
		intentReciver.putExtra("id", r_id);
		intentReciver.setAction(NOTIFICATION_EVENT);
		if (event != null) {
			intentReciver.putExtra("event", event.toString());
			intentReciver.putExtra("eventid", eventID);
		}
		PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this.context, 0, intentReciver, PendingIntent.FLAG_UPDATE_CURRENT);
		// 删除事件
		builder.setDeleteIntent(pendingIntentCancel);
		// 点击事件
		builder.setContentIntent(pendingIntentCancel);

		// 如果拥有相同id的通知已经被提交而且没有被移除，该方法会用更新的信息来替换之前的通知。
		nManager.notify(r_id, builder.build());
		showNID.add(String.valueOf(r_id));
		return r_id;
	}

	/**
	 * 通知消息是否被移除
	 * 
	 * @return true 已经被移除(不存在)
	 * */
	public boolean isNotificationRemove(int id) {
		return showNID.indexOf(String.valueOf(id)) == -1;
	}

	/**
	 * 移除通知消息
	 * */
	public void removeNotification(int id) {
		NotificationManager nManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
		nManager.cancel(id);
		showNID.remove(String.valueOf(id));
	}

	public enum NOTIFICATION_EVENT {
		SHOW_MAP {
			public String toString() {
				return "show_map";
			}
		},
		EVENT_PROCESS {
			public String toString() {
				return "event_process";
			}
		},
		MESSAGE {
			public String toString() {
				return "message";
			}
		},
		RE_PASSWORD {
			public String toString() {
				return "re_password";
			}
		},
		GATHER {
			public String toString() {
				return "gather";
			}
		},
		NEED_REGISTER {
			public String toString() {
				return "need_register";
			}
		}
	}
}
