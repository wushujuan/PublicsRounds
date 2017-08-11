package cn.breaksky.rounds.publics.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ListenerJobService extends JobService {
	private final static String TAG = "ListenerJobService";
	private static int mJobId = 0;

	@Override
	public boolean onStartJob(JobParameters params) {
		Log.i(TAG, "onStartJob()");
		MainService.getInstance().startService(this);
		return false;
	}

	@Override
	public boolean onStopJob(JobParameters params) {
		Log.i(TAG, "onStopJob()");
		Intent i = new Intent(MainService.SERVICE_DESTORY);
		sendBroadcast(i);
		return false;
	}

	public static void startJobServive(Context context) {
		JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
		ComponentName componentName = new ComponentName(context, ListenerJobService.class);

		JobInfo.Builder builder = new JobInfo.Builder(++mJobId, componentName);
		// 设置JobService执行的最小延时时间
		builder.setMinimumLatency(1000);
		// 设置JobService执行的最晚时间
		builder.setOverrideDeadline(1000);
		// 设置执行的网络条件
		//builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
		builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
		builder.setRequiresDeviceIdle(true);// 是否要求设备为idle状态
		builder.setRequiresCharging(true);// 是否要设备为充电状态
		scheduler.schedule(builder.build());
	}
}
