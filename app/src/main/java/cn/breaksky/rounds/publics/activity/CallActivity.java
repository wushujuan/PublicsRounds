package cn.breaksky.rounds.publics.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LongSparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.RoundsConfig;
import cn.breaksky.rounds.publics.bean.MessageScrollView;
import cn.breaksky.rounds.publics.bean.MessageView;
import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.listener.MessageListener;
import cn.breaksky.rounds.publics.listener.MessageObj;
import cn.breaksky.rounds.publics.listener.MessageObj.MESSAGE_STATUS;
import cn.breaksky.rounds.publics.listener.MessageObj.MESSAGE_TYPE;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.BadgeView;
import cn.breaksky.rounds.publics.util.BitmapUtil;
import cn.breaksky.rounds.publics.util.MessageShow;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.imageselector.ImageConfig;
import cn.breaksky.rounds.publics.util.imageselector.ImageLoader;
import cn.breaksky.rounds.publics.util.imageselector.ImageSelector;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendCheckLoginThread;

import com.baidu.location.LocationClient;
import com.bumptech.glide.Glide;

public class CallActivity extends Activity implements ImageLoader {
	private static final long serialVersionUID = 7736883006765187500L;
	private static int showmessageid = 0;

	private ImageButton m_btnPhoto;
	private ImageButton m_btnVoice;
	private ImageButton m_btnSend;
	private ImageButton m_btnAddFile;
	//private ImageButton m_btnCoordinate;
	private LinearLayout messageView;
	private LinearLayout messageAddFile;
	private EditText messageInfo;
	private MessageScrollView messageScroll;
	
	private TextView cancelButton;// call_back_button
	/** <b>查询人员按钮</b> */
	private TextView callPersonnelButton;// call_personnel_button
	private TextView callPersonnelName;// call_personnel_name
	// 定位相关声明
   public LocationClient locationClient = null;
	private float downY;
	private float downY_len;
	private boolean inMain = true;
	private MessageShow messageShow;
	
	/**<b>选择人员信息</b>*/
	private RoundPersonnelVO nowPersonnel;

	private ArrayList<String> path = new ArrayList<String>();
	private LongSparseArray<RoundPersonnelVO> personnels = new android.util.LongSparseArray<RoundPersonnelVO>();
	private LongSparseArray<BadgeView> personnelBadge = new android.util.LongSparseArray<BadgeView>();
	/*private double latitude;	//纬度
	private double longitude;	//经度
	public BDLocationListener myListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
		
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
	};*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ContextManager.addActivity(this);
		inMain = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);
		this.initView();
		//this.queryPersonner();
		showmessageid = 0;
		this.messageShow = new MessageShow(this);
		queryNewMessage();
		
		//从CheckPersonActivity中获取选中人员信息
		nowPersonnel = (RoundPersonnelVO)getIntent().getSerializableExtra(CheckPersonActivity.SER_KEY);
		
		/*locationClient = new LocationClient(this); // 实例化LocationClient类
		locationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPS
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
		
		locationClient.setLocOption(option);
		locationClient.start(); // 开始定位
		*/
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initView() {
		m_btnPhoto = (ImageButton) findViewById(R.id.message_photo_button);
		m_btnVoice = (ImageButton) findViewById(R.id.message_voice_button);
		m_btnSend = (ImageButton) findViewById(R.id.message_send_button);
		m_btnAddFile = (ImageButton) findViewById(R.id.message_addfile_button);
		//m_btnCoordinate = (ImageButton) findViewById(R.id.message_fire_button);
		cancelButton = (TextView) findViewById(R.id.call_back_button);
		//callPersonnelButton = (TextView) findViewById(R.id.call_personnel_button);
		callPersonnelName = (TextView) findViewById(R.id.call_personnel_name);

		messageView = (LinearLayout) findViewById(R.id.message_view);
		messageInfo = (EditText) findViewById(R.id.message_info);
		messageScroll = (MessageScrollView) findViewById(R.id.message_scroll_view);
		messageAddFile = (LinearLayout) findViewById(R.id.message_add_file_layout);

		cancelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		//callPersonnelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		m_btnPhoto.setOnTouchListener(new ButtonTouchListener(R.drawable.camera_touch, R.drawable.camera));
		m_btnVoice.setOnTouchListener(new ButtonTouchListener(R.drawable.voice_touch, R.drawable.voice));
		m_btnSend.setOnTouchListener(new ButtonTouchListener(R.drawable.send_touch, R.drawable.send));
		m_btnAddFile.setOnTouchListener(new ButtonTouchListener(R.drawable.addfile_touch, R.drawable.addfile));
		//m_btnCoordinate.setOnTouchListener(new ButtonTouchListener(R.drawable.cordinates_touch, R.drawable.cordinates));

		cancelButton.setOnClickListener(new CancelButtonOnClick());
		m_btnPhoto.setOnClickListener(new PhotoBtnListener());
		m_btnVoice.setOnClickListener(new VoiceBtnListener());
		m_btnSend.setOnClickListener(new SendBtnListener());
		m_btnAddFile.setOnClickListener(new AddFileListener());
	//	m_btnCoordinate.setOnClickListener(new CoordinateOnClick());
		//callPersonnelButton.setOnClickListener(new CallPersonnelOnClick());

		messageScroll.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					downY_len = event.getY() - downY;
					break;
				case MotionEvent.ACTION_UP:
					if (downY_len > 200 && v.getScrollY() <= 0) {
						queryHistoryMessage();
					}
					break;
				}
				return false;
			}
		});
	}

	private int begin = 0;

	private void queryHistoryMessage() {
		UtilTools.toast(CallActivity.this, "查询历史记录...");
		begin = begin + 9;
		RoundHandler.runActivityMethod(this, 500, "queryHistory");
	}

	public void queryHistory() {
		queryMessage(false);
	}

	public void queryNewMessage() {
		if (inMain) {
			RoundHandler.runActivityMethod(this, 1000, "queryNewMessage");
			queryMessage(true);
		}
	}

	private void queryMessage(boolean newmessage) {
		if (MainService.getInstance().roundsConfig.NO_READ_MESSAGE.size() > 0) {
			MainService.getInstance().roundsConfig.NO_READ_MESSAGE.clear();
		}
		RoundPersonnelVO user = MainService.getInstance().getUser();
		if (user != null && nowPersonnel != null) {
			callPersonnelName.setText("(" + nowPersonnel.getName() + ")");
			SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
			String selectsendid = nowPersonnel.getRp_id().toString();
			String[] params = null;
			if (newmessage) {
				params = new String[] { selectsendid, selectsendid, String.valueOf(showmessageid) };
			} else {
				params = new String[] { selectsendid, selectsendid };
			}
			StringBuffer sql = new StringBuffer("select id,message,sendid,reciveid,sendtime,status,type,file,latitude,longitude from message where ");
			sql.append(" (sendid=? or reciveid=?) ");
			if (newmessage) {
				sql.append(" and id>? ");
			}
			sql.append(" order by id desc");
			sql.append(" limit 9 offset ");
			if (newmessage) {
				sql.append(0);
			} else {
				sql.append(begin);
			}
			Cursor cursor = db.rawQuery(sql.toString(), params);
			List<MessageView> views = new ArrayList<MessageView>();
			while (cursor.moveToNext()) {
				int messageid = cursor.getInt(0);
				if (messageid > showmessageid) {
					showmessageid = messageid;
				}
				views.add(getMessageView(cursor, user.getRp_id().toString()));
			}
			cursor.close();
			showMessageView(views, newmessage);
		}
	}

	private MessageView getMessageView(Cursor cursor, String rpid) {
		int id = cursor.getInt(0);
		String type = cursor.getString(6);
		String sendid = cursor.getString(2);
		String message = cursor.getString(1);
		Date sendtime = new Date(cursor.getLong(4));
		String filePath = cursor.getString(7);
		Float latitude = cursor.getFloat(8);
		Float longitude = cursor.getFloat(9);

		MESSAGE_TYPE messageType = null;
		String str = null;
		boolean left = false;
		if (MESSAGE_TYPE.TEXT.toString().equals(type)) {
			str = message;
			messageType = MESSAGE_TYPE.TEXT;
		} else if (MESSAGE_TYPE.PHOTO.toString().equals(type)) {
			str = filePath;
			messageType = MESSAGE_TYPE.PHOTO;
		} else if (MESSAGE_TYPE.VOICE.toString().equals(type)) {
			str = filePath;
			messageType = MESSAGE_TYPE.VOICE;
		} else if (MESSAGE_TYPE.COORDINATE.toString().equals(type)) {
			str = latitude + "," + longitude;
			messageType = MESSAGE_TYPE.COORDINATE;
		} else if (MESSAGE_TYPE.DISPATCH.toString().equals(type)) {
			str = message;
			messageType = MESSAGE_TYPE.DISPATCH;
		}
		if (rpid.equals(sendid)) {
			left = false;
		} else {
			left = true;
		}
		return new MessageView(id, Long.valueOf(sendid), left, messageType, str, sendtime);
	}

	class PhotoBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			File file = new File(MainService.getInstance().roundsConfig.CAMERA_PHOTO_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}
			// GlideLoader 可用自己用的缓存库
			ImageConfig.Builder imageConfig = new ImageConfig.Builder(CallActivity.this);
			// 如果在 4.4 以上，则修改状态栏颜色 （默认黑色）
			imageConfig.steepToolBarColor(getResources().getColor(R.color.blue));
			// 标题的背景颜色 （默认黑色）
			imageConfig.titleBgColor(getResources().getColor(R.color.blue));
			// 提交按钮字体的颜色 （默认白色）
			imageConfig.titleSubmitTextColor(getResources().getColor(R.color.white));
			// 标题颜色 （默认白色）
			imageConfig.titleTextColor(getResources().getColor(R.color.white));
			// 开启多选 （默认为多选） (单选 为 singleSelect)
			imageConfig.singleSelect();
			// 已选择的图片路径
			imageConfig.pathList(path);
			// 拍照后存放的图片路径
			imageConfig.filePath(file.getPath());
			// 开启拍照功能 （默认开启）
			imageConfig.showCamera().requestCode(RoundsConfig.REQ_CODE_PHOTO);

			ImageSelector.open(CallActivity.this, imageConfig.build()); // 开启图片选择器
		}
	}

	class VoiceBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(v.getContext(), VoiceActivity.class);
			CallActivity.this.startActivityForResult(intent, RoundsConfig.REQ_CODE_VIOCE);
			messageAddFile.setVisibility(View.GONE);
		}
	}

	class AddFileListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			if (messageAddFile.getVisibility() == View.VISIBLE) {
				messageAddFile.setVisibility(View.GONE);
			} else {
				messageAddFile.setVisibility(View.VISIBLE);
			}
		}
	}

	/*class CoordinateOnClick implements OnClickListener {

		@Override
		public void onClick(View view) {
			if (nowPersonnel == null) {
				return;
			}
			String reciveId = nowPersonnel.getRp_id().toString();
			RoundPersonnelVO user = MainService.getInstance().getUser();
			if (user == null) {
				return;
			}
			
			MessageObj message = new MessageObj(new Float(latitude), new Float(longitude), reciveId, user.getRp_id().toString(), new Date());
			MessageListener.writeMessage(message, MESSAGE_STATUS.WATI);
			messageAddFile.setVisibility(View.GONE);
//			GPSData gps = MainService.getInstance().getLastGPS();
//			if (gps == null) {
//				UtilTools.toast(CallActivity.this, "定位数据为空,不能发送!");
//			} else {
//				MessageObj message = new MessageObj(gps.getLatitude().floatValue(), gps.getLongitude().floatValue(), reciveId, user.getRp_id().toString(), new Date());
//				MessageListener.writeMessage(message, MESSAGE_STATUS.WATI);
//				messageAddFile.setVisibility(View.GONE);
//			}
		}
	}
*/
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK || intent == null) {
			return;
		}
		if (intent != null) {
			switch (requestCode) {
			case RoundsConfig.REQ_CODE_PHOTO:
				photoCallBack();
				break;
			case RoundsConfig.REQ_CODE_VIOCE:
				voiceCallBack(intent.getStringExtra("path"));
				break;
			}
		}
	}

	private void photoCallBack() {
		if (path.size() == 0) {
			UtilTools.toast(this, "获取图片出错");
			path.clear();
			return;
		}
		if (nowPersonnel == null) {
			path.clear();
			return;
		}
		String reciveId = nowPersonnel.getRp_id().toString();
		RoundPersonnelVO user = MainService.getInstance().getUser();
		if (user == null) {
			return;
		}
		String imagePath = path.get(0);
		path.clear();
		Bitmap bitmap = BitmapUtil.thumbnail(imagePath, 1024);
		try {
			bitmap.compress(Bitmap.CompressFormat.JPEG, 60, new FileOutputStream(imagePath));
		} catch (FileNotFoundException e) {
			UtilTools.toast(this, "压缩图片出错");
			return;
		}
		MessageObj message = new MessageObj(new File(imagePath), MESSAGE_TYPE.PHOTO, reciveId, user.getRp_id().toString(), new Date());
		MessageListener.writeMessage(message, MESSAGE_STATUS.WATI);
	}

	private void voiceCallBack(String path) {
		if (nowPersonnel == null) {
			return;
		}
		String reciveId = nowPersonnel.getRp_id().toString();
		RoundPersonnelVO user = MainService.getInstance().getUser();
		if (user == null) {
			return;
		}
		MessageObj message = new MessageObj(new File(path), MESSAGE_TYPE.VOICE, reciveId, user.getRp_id().toString(), new Date());
		MessageListener.writeMessage(message, MESSAGE_STATUS.WATI);
	}

	class SendBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String str = messageInfo.getText().toString();
			if (nowPersonnel != null && str != null && str.length() > 0) {
				String reciveId = nowPersonnel.getRp_id().toString();
				RoundPersonnelVO user = MainService.getInstance().getUser();
				if (user == null) {
					return;
				}
				MessageObj message = new MessageObj(str, reciveId, user.getRp_id().toString(), new Date());
				MessageListener.writeMessage(message, MESSAGE_STATUS.WATI);
				messageInfo.setText("");
			} else {
				UtilTools.toast(CallActivity.this, "消息为空");
			}
		}
	}

	private void showMessageView(List<MessageView> views, boolean newmessage) {
		int l = views.size() - 1;
		for (int i = l; i >= 0; i--) {
			MessageView viewobj = views.get(i);
			RoundPersonnelVO pvo = personnels.get(viewobj.rpid);
			View viewChild = this.messageShow.getMessageView(viewobj.id, viewobj.type, viewobj.str, null);
			View view = this.messageShow.getMessageLayout(pvo == null ? "" : pvo.getName(), null, viewobj.left, viewobj.sendtime, viewChild, false);
			if (newmessage) {
				messageView.addView(view);
			} else {
				messageView.addView(view, 0);
			}
			messageScroll.fullScroll(ScrollView.FOCUS_DOWN);
		}
	}

	public void finish() {
		inMain = false;
		super.finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			inMain = false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			CallActivity.this.finish();
			/*Intent intent = new Intent(CallActivity.this, CallListActivity.class);
			CallActivity.this.startActivity(intent);*/
		}
	}

	private class CallPersonnelOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(CallActivity.this, CheckPersonActivity.class);
			startActivity(intent);
			
		}
	}

	public void queryPersonner() {
		try {
			personnels.clear();
			personnelBadge.clear();
			Request request = new Request(Request.QUERY_PERSONNEL);
			request.sendCheckLogin(new Params(), new SendCheckLoginThread.CallBack() {

				@Override
				public void callBack(JSONObject json) {
					try {
						if (json.getBoolean("status")) {
							List<RoundPersonnelVO> users = UtilTools.jsonToBean(json.getJSONArray("users"), RoundPersonnelVO.class);
							for (RoundPersonnelVO vo : users) {
								personnels.put(vo.getRp_id(), vo);
							}
						}
					} catch (JSONException e) {
						UtilTools.alertMessage(CallActivity.this, "读取人员失败!");
					}
				}

				@Override
				public void exception(Exception e) {
					UtilTools.alertMessage(CallActivity.this, "读取人员失败!");
				}

			});

		} catch (Exception e) {
			UtilTools.alertMessage(CallActivity.this, "查询人员失败!");
		}
	}

	@Override
	public void displayImage(Context context, String path, ImageView imageView) {
		Glide.with(context).load(path).placeholder(R.drawable.imageselector_photo).centerCrop().into(imageView);
	}
}