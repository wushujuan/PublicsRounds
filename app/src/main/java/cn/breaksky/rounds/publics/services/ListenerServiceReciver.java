package cn.breaksky.rounds.publics.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.breaksky.rounds.publics.activity.EventProcessActivity;
import cn.breaksky.rounds.publics.activity.GatherListActivity;
import cn.breaksky.rounds.publics.activity.MapActivity;
import cn.breaksky.rounds.publics.activity.MessageActivity;
import cn.breaksky.rounds.publics.activity.ReciverActivity;
import cn.breaksky.rounds.publics.activity.RegisterAcitvity;
import cn.breaksky.rounds.publics.services.MainService.NOTIFICATION_EVENT;

public class ListenerServiceReciver extends BroadcastReceiver {
	private final static String TAG = "ListenerServiceReciver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent reciverIntent = null;

		if (MainService.SERVICE_DESTORY.equals(intent.getAction())) {
			Log.d(TAG, "SERVICE_DESTORY");
			ListenerService.startService(context);
		} else if (MainService.WIDGET_VIDEO_ALARM.equals(intent.getAction())) {
			Log.d(TAG, "WIDGET_VIDEO_ALARM");
			// 一键报警
			reciverIntent = new Intent(context, ReciverActivity.class);
			reciverIntent.putExtra(ReciverActivity.ACTION_TYPE, ReciverActivity.VIDEO_ALARM);
		} else if (MainService.WIDGET_PHOTO_ALARM.equals(intent.getAction())) {
			Log.d(TAG, "WIDGET_PHOTO_ALARM");
			// 图片报警
			reciverIntent = new Intent(context, ReciverActivity.class);
			reciverIntent.putExtra(ReciverActivity.ACTION_TYPE, ReciverActivity.PHOTO_ALARM);
		} else if (MainService.WIDGET_VOICE_ALARM.equals(intent.getAction())) {
			Log.d(TAG, "WIDGET_VOICE_ALARM");
			// 语音报警
			reciverIntent = new Intent(context, ReciverActivity.class);
			reciverIntent.putExtra(ReciverActivity.ACTION_TYPE, ReciverActivity.VOICE_ALARM);
		} else if (MainService.NOTIFICATION_EVENT.equals(intent.getAction())) {
			Log.d(TAG, "NOTIFICATION_REMOVE");
			// 通知栏事件
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FILL_IN_DATA);
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				MainService.getInstance().removeNotification(bundle.getInt("id"));
				String event = bundle.getString("event");
				String eventid = bundle.getString("eventid");
				if (NOTIFICATION_EVENT.SHOW_MAP.toString().equals(event)) {
					reciverIntent = new Intent(context, MapActivity.class);
					
				} else if (NOTIFICATION_EVENT.EVENT_PROCESS.toString().equals(event)) {
					reciverIntent = new Intent(context, EventProcessActivity.class);	//打开报警处理页面
				} else if (NOTIFICATION_EVENT.MESSAGE.toString().equals(event)) {
					reciverIntent = new Intent(context, MessageActivity.class);	//打开公告预警页面
				} else if (NOTIFICATION_EVENT.RE_PASSWORD.toString().equals(event)) {
					reciverIntent = new Intent(context, ReciverActivity.class);	//打开一键报警页面
					reciverIntent.putExtra(ReciverActivity.ACTION_TYPE, ReciverActivity.PASSWORD_ERROR);
				} else if (NOTIFICATION_EVENT.NEED_REGISTER.toString().equals(event)) {
					reciverIntent = new Intent(context, RegisterAcitvity.class);	//打开注册页面
				}else if (NOTIFICATION_EVENT.GATHER.toString().equals(event)) {
					reciverIntent = new Intent(context, GatherListActivity.class);	//打开采集任务页面
				}
			}
		}

		if (reciverIntent != null) {
			reciverIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(reciverIntent);
		}
	}
}
