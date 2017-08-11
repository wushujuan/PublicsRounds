package cn.breaksky.rounds.publics;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class ContextManager extends Application {

	private static ContextManager instance;
	private static List<Activity> ACTIVITY = new ArrayList<Activity>();

	public static ContextManager getInstance() {
		return instance;
	}

	public static void addActivity(Activity acitvity) {
		ACTIVITY.add(acitvity);
	}

	public static void exitAllActivity() {
		for (Activity activity : ACTIVITY) {
			if (activity != null) {
				activity.finish();
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
	}
}
