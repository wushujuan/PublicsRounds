package cn.breaksky.rounds.publics.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.BadgeView;
import cn.breaksky.rounds.publics.util.UtilTools;
/**
 * 主页面
 * @author Administrator
 *
 */
public class MainActivity extends Activity {
	private final static String TAG = "MainActivity";
	private TextView appMessageText;
	/** <b>日常汇报按钮声明</b> */
	private ImageButton settingButton;
	/** <b>灾害报警按钮声明</b> */
	private ImageButton eventButton;
	/** <b>报警处理按钮声明</b> */
	private ImageButton eventProcessButton;
	/** <b>采集任务按钮声明</b> */
	private ImageButton gatherButton;
	/** <b>公告预警按钮声明</b> */
	private ImageButton messageButton;
	/** <b>视频会商按钮声明</b> */
	private ImageButton phoneButton;
	/** <b>地图定位按钮声明</b> */
	private ImageButton mapButton;
	/** <b>交流互动按钮声明</b> */
	private ImageButton callButton;
	/** <b>个人信息按钮声明</b> */
	private ImageButton personInfoButton;
	/** <b>退出按钮声明</b> */
	private ImageButton exitButton;
	/** <b>交流互动角标</b> */
	private BadgeView callBadge;
	/** <b>采集任务角标</b> */
	private BadgeView gatherBadge;
	/** <b>公告预警角标</b> */
	private BadgeView messageBadge;
	
	public void onCreate(Bundle savedInstanceState) {
		ContextManager.addActivity(this);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		appMessageText = (TextView) findViewById(R.id.main_app_message);
		eventButton = (ImageButton) findViewById(R.id.main_event_button);
		eventProcessButton = (ImageButton) findViewById(R.id.main_event_process_button);
		mapButton = (ImageButton) findViewById(R.id.main_map_button);
		personInfoButton = (ImageButton) findViewById(R.id.main_person_info_button);
		settingButton = (ImageButton) findViewById(R.id.main_setting_button);
		phoneButton = (ImageButton) findViewById(R.id.main_phone_button);
		exitButton = (ImageButton) findViewById(R.id.main_exit_button);
		
		callButton = (ImageButton) findViewById(R.id.main_call_button);
		callBadge = new BadgeView(this, callButton);	//交流互动模块显示角标
		gatherButton = (ImageButton)findViewById(R.id.main_gather_button);
		gatherBadge = new BadgeView(this, gatherButton);	//采集任务模块显示角标
		messageButton = (ImageButton) findViewById(R.id.main_message_button);
		messageBadge = new BadgeView(this, messageButton);  //公告预警模块显示角标
		
		//事件触发时，按钮颜色变化
		eventButton.setOnTouchListener(new ButtonTouchListener(R.drawable.event_touch, R.drawable.event));
		eventProcessButton.setOnTouchListener(new ButtonTouchListener(R.drawable.event_process_touch, R.drawable.event_process));
		mapButton.setOnTouchListener(new ButtonTouchListener(R.drawable.map_touch, R.drawable.map));
		messageButton.setOnTouchListener(new ButtonTouchListener(R.drawable.message_touch, R.drawable.message));
		personInfoButton.setOnTouchListener(new ButtonTouchListener(R.drawable.person_info_touch, R.drawable.person_info));
		gatherButton.setOnTouchListener(new ButtonTouchListener(R.drawable.gather_touch, R.drawable.gather));
		phoneButton.setOnTouchListener(new ButtonTouchListener(R.drawable.phone_touch, R.drawable.phone));
		settingButton.setOnTouchListener(new ButtonTouchListener(R.drawable.setting_touch, R.drawable.setting));
		callButton.setOnTouchListener(new ButtonTouchListener(R.drawable.call_touch, R.drawable.call));
		exitButton.setOnTouchListener(new ButtonTouchListener(R.drawable.exit_touch, R.drawable.exit));

		//触发事件
		eventButton.setOnClickListener(new EventButtonOnClick());
		eventProcessButton.setOnClickListener(new EventProcessButtonOnClick());
		mapButton.setOnClickListener(new MapButtonOnClick());
		messageButton.setOnClickListener(new MessageButtonOnClick());
		personInfoButton.setOnClickListener(new PersonInfoButtOnClick());
		gatherButton.setOnClickListener(new GatherButtonOnClick());
		phoneButton.setOnClickListener(new PhoneButtonOnClick());
		exitButton.setOnClickListener(new ExitButtonOnClick());
		settingButton.setOnClickListener(new SettingButtonOnClick());
		callButton.setOnClickListener(new CallButtonOnClick());
		
		(new SoftUpdate(MainActivity.this)).checkUpdate(false);//app版本自动更新
		
		mainThread();	//每隔2s启动线程
	}

	public void mainThread() {
		if (MainService.getInstance().roundsConfig.NO_READ_MESSAGE.size() > 0) {//交流互动模块角标显示
			callBadge.setText(String.valueOf(MainService.getInstance().roundsConfig.NO_READ_MESSAGE.size()));
			callBadge.setTextColor(Color.DKGRAY);
			callBadge.show();
		} else {
			callBadge.setVisibility(View.GONE);//角标隐藏
		}
		if(MainService.getInstance().maxGather > 0){//采集任务模块角标显示
			gatherBadge.setText(String.valueOf(MainService.getInstance().maxGather));
			gatherBadge.setTextColor(Color.DKGRAY);
			gatherBadge.show();
		}else{
			gatherBadge.hide();//角标隐藏
		}
		if(MainService.getInstance().maxPMessage > 0){//公告预警模块角标显示
			messageBadge.setText(String.valueOf(MainService.getInstance().maxPMessage));
			messageBadge.setTextColor(Color.DKGRAY);
			messageBadge.show();
		}else{
			messageBadge.hide();//角标隐藏
		}
		
		StringBuffer str = new StringBuffer();
		String S = "发/收:";
		str.append(S);
		str.append(UtilTools.getSizeStr(MainService.getInstance().roundsConfig.SEND_LENGTH));
		str.append("/");
		str.append(UtilTools.getSizeStr(MainService.getInstance().roundsConfig.READ_LENGTH));
		str.append(",合:");
		str.append(UtilTools.getSizeStr(MainService.getInstance().getSumSend()));
		str.append("/");
		str.append(UtilTools.getSizeStr(MainService.getInstance().getSumRead()));
		str.append(" ");
		str.append(MainService.getInstance().roundsConfig.ROUND_LISTENER_RUN);
		appMessageText.setText(str.toString());
		MainService.getInstance().saveAppNetByteLength();
		RoundHandler.runActivityMethod(this, 2000, "mainThread");
	}

	private class EventButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View view) {
			Intent intent = new Intent(MainActivity.this, EventActivity.class);
			MainActivity.this.startActivity(intent);
		}

	}

	private class EventProcessButtonOnClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			Intent intent = new Intent(MainActivity.this, EventProcessActivity.class);
			MainActivity.this.startActivity(intent);
		}
	}

	private class MapButtonOnClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			Intent intent = new Intent(MainActivity.this, MapActivity.class);
			MainActivity.this.startActivity(intent);
					
		}
	}

	private class MessageButtonOnClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			Intent intent = new Intent(MainActivity.this, MessageActivity.class);
			MainActivity.this.startActivity(intent);
		}
	}

	private class PersonInfoButtOnClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			Intent intent = new Intent(MainActivity.this, PersonInfoActivity.class);
			MainActivity.this.startActivity(intent);
		}
	}

	private class GatherButtonOnClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			//badge.hide();
			Intent intent = new Intent(MainActivity.this, GatherListActivity.class);
			MainActivity.this.startActivity(intent);
		}
	}

	private class PhoneButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			PackageInfo packageInfo = null;
			try {
				packageInfo = getPackageManager().getPackageInfo("cn.breaksky.meeting", 0);
			} catch (NameNotFoundException e) {
				UtilTools.toast(MainActivity.this, "未安装视频会议");
				return;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			String className = "cn.breaksky.meeting.activity.MeetingCallActivity";
			intent.setClassName(packageInfo.packageName, className);

			Bundle bundle = new Bundle();
			bundle.putString("username", MainService.getInstance().getUser().getMobile());
			bundle.putString("password", MainService.getInstance().getUser().getDevicekey());
			intent.putExtras(bundle);
			try {
				MainActivity.this.startActivity(intent);
			} catch (Exception e) {
				Log.e(TAG, "启动视频会议失败", e);
			}
		}
	}

	private class ExitButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Builder alert = new AlertDialog.Builder(MainActivity.this);
			alert.setTitle("确认退出?");
			alert.setPositiveButton("是", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					ContextManager.exitAllActivity();
				}

			});
			alert.setNegativeButton("否", null);
			alert.show();
		}
	}

	private class SettingButtonOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
/*			Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
			MainActivity.this.startActivity(intent);*/
			Intent intent = new Intent(MainActivity.this, ReportActivity.class);
			MainActivity.this.startActivity(intent);
		}
	}

	private class CallButtonOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
		//	Intent intent = new Intent(MainActivity.this, CallActivity.class);
			Intent intent = new Intent(MainActivity.this, CallListActivity.class);
			MainActivity.this.startActivity(intent);
		}
	}
}
