package cn.breaksky.rounds.publics.activity;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.listener.GPSData;
import cn.breaksky.rounds.publics.listener.MessageObj.MESSAGE_TYPE;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.CameraView;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.VoiceUtil;

public class ReciverActivity extends Activity {
	public final static String ACTION_TYPE = "action_type";
	public final static String VIDEO_ALARM = "video_alarm";
	public final static String VOICE_ALARM = "voice_alarm";
	public final static String PHOTO_ALARM = "photo_alarm";
	public final static String PASSWORD_ERROR = "password_error";

	private RelativeLayout voice_layout;
	private RelativeLayout photo_layout;// reciver_photo_layout
	private RelativeLayout video_layout;// reciver_video_layout
	private RelativeLayout password_layout;// reciver_password_layout
	private ImageButton closeButton;
	// 显示
	private TextView contextView;// reciver_context
	private TextView longitudeView;// reciver_longitude
	private TextView latitudeView;// reciver_latitude
	// 语音
	private ProgressBar voiceBar;// reciver_voice_bar
	private TextView voiceTextView;// reciver_voice_text
	private ImageButton voiceRecordingButton;// reciver_voice_recording_button
	// 图片
	private RelativeLayout photoCameraLayout;// reciver_photo_camera_view
	private ImageButton photoButton;// reciver_photo_button
	// 视频
	private RelativeLayout videoCameraLayout;// reciver_video_camera_view
	private ImageButton videoButton;// reciver_video_button
	private RelativeLayout videoButtonLayout;// reciver_video_button_layout
	private RelativeLayout videoTimeoutLayout;// reciver_video_timeout_layout
	private TextView videoTimeout;// reciver_video_timeout
	// 修改密码
	private EditText passwordText;// reciver_password_text
	private Button passwordButton;// reciver_password_button

	//
	private CameraView photoCameraView;
	private String[] l_strs = { " |", " \\", "─", " /", " |", " \\", "─", " /" };
	private int loading = 0;
	private int videoTimeOut = 10 * 1000;// 10秒
	private long videoTime;
	private String eventType;
	private boolean showEventButton = false;

	@Override
	protected void onCreate(Bundle bundle) {
		ContextManager.addActivity(this);
		super.onCreate(bundle);
		setContentView(R.layout.activity_reciver);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		voice_layout = (RelativeLayout) findViewById(R.id.reciver_voice_layout);
		photo_layout = (RelativeLayout) findViewById(R.id.reciver_photo_layout);
		video_layout = (RelativeLayout) findViewById(R.id.reciver_video_layout);
		password_layout = (RelativeLayout) findViewById(R.id.reciver_password_layout);
		closeButton = (ImageButton) findViewById(R.id.reciver_close_button);

		contextView = (TextView) findViewById(R.id.reciver_context);
		longitudeView = (TextView) findViewById(R.id.reciver_longitude);
		latitudeView = (TextView) findViewById(R.id.reciver_latitude);

		voiceBar = (ProgressBar) findViewById(R.id.reciver_voice_bar);
		voiceTextView = (TextView) findViewById(R.id.reciver_voice_text);
		voiceRecordingButton = (ImageButton) findViewById(R.id.reciver_voice_recording_button);

		photoCameraLayout = (RelativeLayout) findViewById(R.id.reciver_photo_camera_view);
		photoButton = (ImageButton) findViewById(R.id.reciver_photo_button);

		videoCameraLayout = (RelativeLayout) findViewById(R.id.reciver_video_camera_view);
		videoButton = (ImageButton) findViewById(R.id.reciver_video_button);
		videoButtonLayout = (RelativeLayout) findViewById(R.id.reciver_video_button_layout);
		videoTimeoutLayout = (RelativeLayout) findViewById(R.id.reciver_video_timeout_layout);
		videoTimeout = (TextView) findViewById(R.id.reciver_video_timeout);

		passwordText = (EditText) findViewById(R.id.reciver_password_text);
		passwordButton = (Button) findViewById(R.id.reciver_password_button);

		// 获取参数
		Bundle configBundle = super.getIntent().getExtras();
		if (configBundle == null || configBundle.getString(ACTION_TYPE) == null) {
			UtilTools.alertError(this, "参数错误!");
			super.finish();
		}
		eventType = configBundle.getString(ACTION_TYPE);
		contextView.setText("请定位成功后进行报警");

		// OnTouchListener
		closeButton.setOnTouchListener(new ButtonTouchListener(R.drawable.close_touch, R.drawable.close));
		voiceRecordingButton.setOnTouchListener(new VoiceTouch(this, MainService.getInstance().roundsConfig.VOICE_PATH));
		photoButton.setOnTouchListener(new ButtonTouchListener(R.drawable.camera_photo_touch, R.drawable.camera_photo));
		videoButton.setOnTouchListener(new ButtonTouchListener(R.drawable.video_touch, R.drawable.video));
		passwordButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		// OnClickListener
		closeButton.setOnClickListener(new CloseButtonListener());
		photoButton.setOnClickListener(new PhotoButtonListener());
		videoButton.setOnClickListener(new VideoButtonListener());
		passwordButton.setOnClickListener(new PasswordButtonListener());
		// 获取最新坐标
		showCordinates();
	}

	private void openCameraView(RelativeLayout layout) {
		layout.removeAllViews();
		if (photoCameraView != null) {
			photoCameraView = null;
		}
		photoCameraView = new CameraView(this, MainService.getInstance().roundsConfig.CAMERA_PHOTO_PATH, new PhotoCallBack());
		layout.addView(photoCameraView);
	}

	private void showEventButton() {
		if (showEventButton) {
			return;
		}
		String typeName = "";
		if (VIDEO_ALARM.equals(eventType)) {
			typeName = "视频报警";
			video_layout.setVisibility(View.VISIBLE);
			videoButtonLayout.setVisibility(View.VISIBLE);
			videoTimeoutLayout.setVisibility(View.GONE);
			openCameraView(videoCameraLayout);
		} else if (PHOTO_ALARM.equals(eventType)) {
			typeName = "图片报警";
			photo_layout.setVisibility(View.VISIBLE);
			openCameraView(photoCameraLayout);
		} else if (VOICE_ALARM.equals(eventType)) {
			typeName = "语音报警";
			voice_layout.setVisibility(View.VISIBLE);
		} else if (PASSWORD_ERROR.equals(eventType)) {
			typeName = "修改密码";
			password_layout.setVisibility(View.VISIBLE);
		}
		showEventButton = true;
		contextView.setText(typeName);
	}

	private void hidenEventButton() {
		video_layout.setVisibility(View.GONE);
		photo_layout.setVisibility(View.GONE);
		voice_layout.setVisibility(View.GONE);
		password_layout.setVisibility(View.GONE);
		showEventButton = false;

	}

	/**
	 * 实时显示坐标
	 * */
	public void showCordinates() {
		GPSData data = MainService.getInstance().getLastGPS();
		if (data != null) {
			showEventButton();
			longitudeView.setText(String.valueOf(data.getLongitude().floatValue()));
			latitudeView.setText(String.valueOf(data.getLatitude().floatValue()));
		} else {
			if (PASSWORD_ERROR.equals(eventType)) {
				showEventButton();
			} else {
				hidenEventButton();
			}
			longitudeView.setText(l_strs[loading]);
			latitudeView.setText(l_strs[loading]);
			loading++;
			if (loading == l_strs.length) {
				loading = 0;
			}
		}
		RoundHandler.runActivityMethod(this, 200, "showCordinates");
	}

	/**
	 * 视频录像倒计时
	 * */
	public void recordTime() {
		long timeout = System.currentTimeMillis() - videoTime;
		if (timeout > videoTimeOut) {
			String videoPath = photoCameraView.stopRecordVideo();
			this.writeEvent(videoPath, MESSAGE_TYPE.VIDEO);
		} else {
			videoTimeout.setText(String.valueOf((videoTimeOut - timeout) / 1000));
			RoundHandler.runActivityMethod(this, 100, "recordTime");
		}
	}

	/**
	 * 图片拍照报警
	 * */
	private class PhotoButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			photoCameraView.takenPicture();
		}

	}

	private class VideoButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			videoTime = System.currentTimeMillis();
			if (photoCameraView.startRecordVideo()) {
				videoButtonLayout.setVisibility(View.GONE);
				videoTimeoutLayout.setVisibility(View.VISIBLE);
				recordTime();
			}
		}

	}

	private class PasswordButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (passwordText.getText().length() < 1) {
				UtilTools.toast(ReciverActivity.this, "密码为空!");
			} else {
				UtilTools.setConfigValue(R.string.save_password_key, passwordText.getText().toString());
				ReciverActivity.this.finish();
			}
		}

	}

	private class CloseButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			ReciverActivity.this.finish();
		}
	}

	private class VoiceTouch extends VoiceUtil {

		public VoiceTouch(Context context, String rootPath) {
			super(context, rootPath, false);
		}

		@Override
		public void volume(int db) {
			voiceBar.setProgress(db);
		}

		@Override
		public void begin() {
			voiceTextView.setText("开始录音...");
		}

		@Override
		public void end(String voicePath) {
			ReciverActivity.this.writeEvent(voicePath, MESSAGE_TYPE.VOICE);
		}

	}

	private class PhotoCallBack implements CameraView.CallBack {

		@Override
		public void TakenPicture(String path, String error) {
			if (path != null) {
				ReciverActivity.this.writeEvent(path, MESSAGE_TYPE.PHOTO);
			} else {
				UtilTools.toast(ReciverActivity.this, error);
			}

		}
	}

	/**
	 * 写事件并结束
	 * */
	private void writeEvent(String filePath, MESSAGE_TYPE type) {
		GPSData gpsdata = MainService.getInstance().getLastGPS();
		RoundPersonnelVO pvo = MainService.getInstance().getUser();
		if (pvo == null) {
			UtilTools.toast(this, "当前用户信息为空");
			return;
		}
		if (gpsdata == null) {
			UtilTools.toast(this, "未获取到坐标");
			return;
		}
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
		String insertSql = "insert into eventinfo(longitude, latitude,address,contact,phone,image,context_type,context,context_file,time) values(?,?,?,?,?,?,?,?,?,?)";
		Object obj[] = new Object[] { String.valueOf(gpsdata.getLongitude().floatValue()), String.valueOf(gpsdata.getLatitude().floatValue()), "", pvo.getName(), pvo.getMobile(),
				"", type.toString(), "", filePath, (new Date()).getTime() };
		db.execSQL(insertSql, obj);
		this.finish();
	}
}
