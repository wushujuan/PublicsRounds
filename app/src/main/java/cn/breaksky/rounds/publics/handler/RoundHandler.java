package cn.breaksky.rounds.publics.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class RoundHandler {
	private final static int RUN_ACTIVITY_METHOD = 1;
	private final static int RUN_METHOD = 2;
	private static RunMethod runMethodThread = new RunMethod();

	static {
		runMethodThread.start();
	}

	/**
	 * Activity 主线程执行
	 * 
	 * @param activity
	 *            Activity
	 * @param delayed
	 *            延时时间
	 * @param method
	 *            方法名不带参数,必须为public<br>
	 *            如:public void run(){} 就填入"run"
	 * */
	public static void runActivityMethod(Activity activity, long delayed, String method) {
		Message msg = new Message();
		msg.obj = new ObjectBean(activity, method);
		msg.what = RoundHandler.RUN_ACTIVITY_METHOD;
		runHandler.sendMessageDelayed(msg, delayed);
	}

	/**
	 * 独立线程执行
	 * 
	 * @param obj
	 *            对象
	 * @param delayed
	 *            延时时间
	 * @param method
	 *            方法名不带参数,必须为public<br>
	 *            如:public void run(){} 就填入"run"
	 * */
	public static void runMethod(Object obj, long delayed, String method) {
		Message msg = new Message();
		msg.obj = new ObjectBean(obj, method);
		msg.what = RoundHandler.RUN_METHOD;
		runHandler.sendMessageDelayed(msg, delayed);
	}

	/**
	 * 独立线程执行,立即执行
	 * 
	 * @param obj
	 *            对象
	 * @param method
	 *            方法名不带参数,必须为public<br>
	 *            如:public void run(){} 就填入"run"
	 * */
	public static void runMethod(Object obj, String method) {
		Message msg = new Message();
		msg.obj = new ObjectBean(obj, method);
		msg.what = RoundHandler.RUN_METHOD;
		runHandler.sendMessage(msg);
	}

	/**
	 * 独立线程执行,立即执行
	 * 
	 * @param obj
	 *            对象
	 * @param method
	 *            方法名不带参数,必须为public<br>
	 *            如:public void run(){} 就填入"run"
	 * @param callBack
	 *            执行完成回调
	 * */
	public static void runMethod(Object obj, String method, HandlerCallBack callBack) {
		Message msg = new Message();
		msg.obj = new ObjectBean(obj, method, callBack);
		msg.what = RoundHandler.RUN_METHOD;
		runHandler.sendMessage(msg);
	}

	private static Handler runHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RUN_ACTIVITY_METHOD:
				runMethod((ObjectBean) msg.obj);
				break;
			case RUN_METHOD:
				runMethodThread.addObject((ObjectBean) msg.obj);
				break;
			}
		}

	};

	private static class RunMethod extends Thread {
		private List<ObjectBean> bean = Collections.synchronizedList(new ArrayList<ObjectBean>());
		private Handler mHandler;

		public RunMethod() {
			super("RoundHandler RunMethod");
		}

		public void addObject(ObjectBean object) {
			bean.add(object);
			mHandler.sendEmptyMessage(0);
		}

		public void run() {
			Looper.prepare();
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					if (bean.size() > 0) {
						runMethod(bean.get(0));
						bean.remove(0);
					}
				}
			};
			Looper.loop();
		}
	}

	private static void runMethod(ObjectBean bean) {
		try {
			Method method = bean.activity.getClass().getMethod(bean.method);
			method.invoke(bean.activity);
			if (bean.callBack != null) {
				bean.callBack.callBack();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
