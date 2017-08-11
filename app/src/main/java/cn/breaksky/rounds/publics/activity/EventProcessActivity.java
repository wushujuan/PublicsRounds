package cn.breaksky.rounds.publics.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.EventBean;
import cn.breaksky.rounds.publics.bean.EventProcessBean;
import cn.breaksky.rounds.publics.bean.MessageScrollView;
import cn.breaksky.rounds.publics.listener.EventListener;
import cn.breaksky.rounds.publics.listener.MessageObj.MESSAGE_TYPE;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.BitmapUtil;
import cn.breaksky.rounds.publics.util.MessageShow;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.VoiceUtil;
import cn.breaksky.rounds.publics.util.imageselector.ImageConfig;
import cn.breaksky.rounds.publics.util.imageselector.ImageLoader;
import cn.breaksky.rounds.publics.util.imageselector.ImageSelector;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendRetJsonThread;
import cn.breaksky.rounds.publics.util.request.callback.SendStreamThread;

import com.bumptech.glide.Glide;
/**
 * 报警处理页面
 * @author Administrator
 *
 */
public class EventProcessActivity extends Activity implements ImageLoader {
	
	/** <b>经纬度坐标声明</b> */
	private TextView infoCordinates;
	/** <b>地址声明</b> */
	private TextView infoAddress;
	/** <b>描述内容声明</b> */
	private TextView infoContextText;
	/** <b>联系人声明</b> */
	private TextView infoContact;
	/** <b>联系电话声明</b> */
	private TextView infoPhone;
	/** <b>历史按钮声明</b> */
	private TextView historyButton;
	/** <b>返回按钮声明</b> */
	private TextView cancelButton;
	/** <b>报警详细按钮声明</b> */
	private Button infoButton;
	/** <b>查看地图按钮声明</b> */
	private Button infoCordinatesButton;
	/** <b>查看图片按钮声明</b> */
	private Button infoImage;
	/** <b>查看文件按钮声明</b> */
	private Button infoContextFile;
	/** <b>回复按钮声明</b> */
	private Button processAnswerButton;
	/** <b>报警信息视图声明</b> */
	private LinearLayout infoLayout;
	/** <b>回复信息视图声明</b> */
	private LinearLayout processAnswerView;
	/** <b>图片视图声明</b> */
	private LinearLayout imageLayout;
	/** <b>语音视图声明</b> */
	private LinearLayout contextVoiceLayout;
	/** <b>文本声明</b> */
	private EditText contextText;
	/** <b>多选按钮声明</b> */
	private RadioGroup contextType;
	/** <b>语音声明</b> */
	private ImageButton contextVoice;
	/** <b>语音进度条声明</b> */
	private ProgressBar contextVoiceProgressBar;
	/** <b>回复的消息内容声明</b> */
	private MessageScrollView processAnswerScroll;
	/** <b>回复图片存储路径声明</b> */
	private String imagePath;
	/** <b>回复语音存储路径声明</b> */
	private String voicePath;
	/** <b>回复图片临时存储路径声明</b> */
	private ArrayList<String> path = new ArrayList<String>();
	/** <b>所有报警信息集合声明</b> */
	private Map<Integer, EventProcessBean> eventProcessBeans = new HashMap<Integer, EventProcessBean>();
	private List<EventBean> eventBeans = null;
	/** <b>当前选中的报警信息</b> */
	private EventBean nowEvent = null;
	
	private static final long serialVersionUID = -7311871426908518120L;
	private long nowEventProcessID = 0;
	private final static int EVENT_IMAGE = 1;
	private final static int EVENT_CONTEXT_FILE = 2;
	private final static int EVENT_PROCESS_FILe = 3;
	private final static int SELECT_REQUEST_CODE = 1001;
	
	public void onCreate(Bundle savedInstanceState) {
		ContextManager.addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_process);

		infoButton = (Button) findViewById(R.id.event_process_info_button);
		infoLayout = (LinearLayout) findViewById(R.id.event_process_info_layout);
		contextType = (RadioGroup) findViewById(R.id.event_process_context_type);
		contextText = (EditText) findViewById(R.id.event_process_context_text);
		contextVoice = (ImageButton) findViewById(R.id.event_process_context_voice);
		contextVoiceLayout = (LinearLayout) findViewById(R.id.event_process_context_voice_layout);
		imageLayout = (LinearLayout) findViewById(R.id.event_process_image_layout);
		contextVoiceProgressBar = (ProgressBar) findViewById(R.id.event_process_context_voice_bar);
		infoCordinatesButton = (Button) findViewById(R.id.event_process_info_cordinates_button);
		infoCordinates = (TextView) findViewById(R.id.event_process_info_cordinates);
		infoAddress = (TextView) findViewById(R.id.event_process_info_address);
		infoImage = (Button) findViewById(R.id.event_process_info_image);
		infoContextText = (TextView) findViewById(R.id.event_process_info_context_text);
		infoContextFile = (Button) findViewById(R.id.event_process_info_context_file);
		infoContact = (TextView) findViewById(R.id.event_process_info_contact);
		infoPhone = (TextView) findViewById(R.id.event_process_info_phone);
		historyButton = (TextView) findViewById(R.id.event_process_history_button);
		cancelButton = (TextView) findViewById(R.id.event_process_back_button);
		processAnswerView = (LinearLayout) findViewById(R.id.event_process_answer_view);
		processAnswerScroll = (MessageScrollView) findViewById(R.id.event_process_answer_scroll_view);
		processAnswerButton = (Button) findViewById(R.id.event_process_answer_button);

		// OnTouchListener
		historyButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		cancelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		contextVoice.setOnTouchListener(new VoiceTouch(this, MainService.getInstance().roundsConfig.VOICE_PATH));
		// OnCheckedChangeListener
		contextType.setOnCheckedChangeListener(new ContextTypeChecked());

		// OnClickListener
		infoButton.setOnClickListener(new InfoButtonOnClick());
		cancelButton.setOnClickListener(new CancelButtonOnClick());
		historyButton.setOnClickListener(new HistroyButtonOnClick());
		infoCordinatesButton.setOnClickListener(new InfoCordinatesButtonOnClick());
		processAnswerButton.setOnClickListener(new ProcessAnswerButtonOnClick());
		imageLayout.setOnClickListener(new ImageLayoutOnClick());

	}

	public void onResume() {
		super.onResume();
		this.eventBeans = EventListener.queryEvent();
		if (this.eventBeans.size() > 0) {
			showEvent(this.eventBeans.get(0));
		}
	}

	private void showEvent(EventBean bean) {
		infoContextFile.setVisibility(View.GONE);
		infoContextText.setVisibility(View.GONE);

		nowEvent = bean;
		infoCordinates.setText(bean.getLongitude() + "\n" + bean.getLatitude());
		infoAddress.setText(bean.getAddress() == null ? "" : bean.getAddress());
		infoContact.setText(bean.getContact() == null ? "" : bean.getContact());
		infoPhone.setText(bean.getPhone() == null ? "" : bean.getPhone());
		infoImage.setOnClickListener(new QueryFile("event_file", EVENT_IMAGE, bean.getRpe_id(), new ShowEventImage(), bean.getImage(), bean));
		if (MESSAGE_TYPE.VOICE.toString().equals(bean.getContext_type())) {
			infoContextFile.setVisibility(View.VISIBLE);
			infoContextFile.setOnClickListener(new QueryFile("event_file", EVENT_CONTEXT_FILE, bean.getRpe_id(), new PlayEventVoice(), bean.getContext_file(), bean));
		}
		if (MESSAGE_TYPE.PHOTO.toString().equals(bean.getContext_type())) {
			infoContextFile.setVisibility(View.VISIBLE);
			infoContextFile.setOnClickListener(new QueryFile("event_file", EVENT_CONTEXT_FILE, bean.getRpe_id(), new ShowEventImage(), bean.getContext_file(), bean));
		}
		if (MESSAGE_TYPE.VIDEO.toString().equals(bean.getContext_type())) {
			infoContextFile.setVisibility(View.VISIBLE);
			infoContextFile.setOnClickListener(new QueryFile("event_file", EVENT_CONTEXT_FILE, bean.getRpe_id(), new PlayEventVideo(), bean.getContext_file(), bean));
		} else {
			infoContextText.setVisibility(View.VISIBLE);
			infoContextText.setText(new String(bean.getContext()));
		}
		processAnswerView.removeAllViews();
		nowEventProcessID = 0;
		this.queryProcess();
	}

	private void queryProcess() {
		try {
			Request request = new Request(Request.QUERY_EVENT_PROCESS);
			Params param = new Params();
			param.addTextParameter("af.event_rpe_id", String.valueOf(nowEvent.getRpe_id()));
			param.addTextParameter("af.event_rpep_id", String.valueOf(nowEventProcessID));
			request.sendRetJson(param, new SendRetJsonThread.CallBack() {

				@Override
				public void callBack(int tag, JSONObject json) {
					try {
						if (json.getBoolean("status")) {
							final List<EventProcessBean> processBeans = UtilTools.jsonToBean(json.getJSONArray("data"), EventProcessBean.class);
							EventProcessActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									showProcess(processBeans);
								}
							});
						} else {
							UtilTools.toast(EventProcessActivity.this, json.getString("message"));
						}
					} catch (Exception e) {
						UtilTools.toast(EventProcessActivity.this, "查询错误");
					}
				}

				@Override
				public void exception(int tag, Exception e) {
					UtilTools.toast(EventProcessActivity.this, "查询失败");
				}

			});
		} catch (Exception e) {
			UtilTools.toast(this, "查询失败");
		}

	}

	private void showProcess(List<EventProcessBean> processBeans) {
		this.eventProcessBeans.clear();
		for (EventProcessBean bean : processBeans) {
			this.eventProcessBeans.put(bean.getRpep_id().intValue(), bean);
			if (bean.getRpep_id() > nowEventProcessID) {
				nowEventProcessID = bean.getRpep_id();
			}
			MESSAGE_TYPE type = MESSAGE_TYPE.TEXT;
			MessageShow.ButtonOnClick callBack = null;

			if (MESSAGE_TYPE.COORDINATE.toString().equals(bean.getMessage_type())) {
				type = MESSAGE_TYPE.COORDINATE;
			} else if (MESSAGE_TYPE.LINE.toString().equals(bean.getMessage_type())) {
				type = MESSAGE_TYPE.LINE;
			} else if (MESSAGE_TYPE.PHOTO.toString().equals(bean.getMessage_type())) {
				type = MESSAGE_TYPE.PHOTO;
				callBack = new MessageShow.ButtonOnClick() {
					

					@Override
					public void onclick(Integer id) {
						EventProcessBean bean = eventProcessBeans.get(id);
						(new QueryFile("event_process_file", EVENT_PROCESS_FILe, id, new ShowEventImage(), null, bean)).query();
					}
				};
			} else if (MESSAGE_TYPE.VOICE.toString().equals(bean.getMessage_type())) {
				type = MESSAGE_TYPE.VOICE;
				callBack = new MessageShow.ButtonOnClick() {

					@Override
					public void onclick(Integer id) {
						EventProcessBean bean = eventProcessBeans.get(id);
						(new QueryFile("event_process_file", EVENT_PROCESS_FILe, id, new PlayEventVoice(), null, bean)).query();
					}
				};
			}

			MessageShow messageShow = new MessageShow(EventProcessActivity.this);
			boolean isLeft = bean.getRp_id() == MainService.getInstance().getUser().getRp_id();
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(bean.getCreatetime());
			View view = messageShow.getMessageView(bean.getRpep_id().intValue(), type, bean.getMessage(), callBack);
			processAnswerView.addView(messageShow.getMessageLayout(bean.getName(), null, isLeft, time.getTime(), view, false));
			processAnswerScroll.fullScroll(ScrollView.FOCUS_DOWN);
		}
	}

	private class HistroyButtonOnClick implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (eventBeans == null) {
				return;
			}
			RadioGroup group = new RadioGroup(EventProcessActivity.this);
			for (EventBean bean : eventBeans) {
				Calendar time = Calendar.getInstance();
				time.setTimeInMillis(bean.getCreatetime());
				StringBuffer str = new StringBuffer();
				str.append(bean.getContact());
				str.append("(");
				str.append(UtilTools.formatDate(time.getTime(), "yyyy-MM-dd HH:mm:ss"));
				str.append(")");
				RadioButton button = new RadioButton(EventProcessActivity.this);
				if (nowEvent.getRpe_id() == bean.getRpe_id().longValue()) {
					button.setChecked(true);
				}
				button.setTag(bean);
				button.setText(str.toString());
				button.setTextColor(EventProcessActivity.this.getResources().getColor(R.color.white));
				group.addView(button);
			}
			AlertDialog alert = new AlertDialog.Builder(EventProcessActivity.this, R.style.Dialog).create();
			alert.setTitle("查看历史");
			alert.setView(group);
			alert.setButton(DialogInterface.BUTTON_POSITIVE, "关闭", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alert.show();
			group.setOnCheckedChangeListener(new HistroyRadioOnClecked(alert));
		}
	}

	private class HistroyRadioOnClecked implements RadioGroup.OnCheckedChangeListener {
		private AlertDialog dialog;

		public HistroyRadioOnClecked(AlertDialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void onCheckedChanged(RadioGroup group, int index) {
			RadioButton button = (RadioButton) group.findViewById(index);
			EventBean bean = (EventBean) button.getTag();
			showEvent(bean);
			this.dialog.dismiss();
		}
	}

	private class QueryFile implements View.OnClickListener {
		private long id;
		private int type;
		private FileCallBack callBack;
		private Object bean;
		private String header;
		private String filePath;

		public QueryFile(String header, int type, long id, FileCallBack callBack, String filePath, Object bean) {
			this.header = header;
			this.type = type;
			this.id = id;
			this.callBack = callBack;
			this.bean = bean;
			this.filePath = filePath;
		}

		@Override
		public void onClick(View v) {
			this.query();
		}

		public void query() {
			try {
				if (this.filePath != null) {//查看图片
					 String[] path = filePath.split(";");
					 if(path.length == 3){
							//打开浏览图片页面
							Intent intent = new Intent(EventProcessActivity.this, ScanImageActivity.class);
							intent.putExtra("FILEPATH", filePath);
							startActivity(intent);
					 }else{//查看文件
							File file = new File(filePath);
							if (file.exists()) {
								runCallBack(file);
							}
					 }
				} else {//查看回复的图片和语音
					Params param = new Params();
					param.addTextParameter("af.event_file_type", String.valueOf(type));
					param.addTextParameter("af.event_file_id", String.valueOf(id));

					String name = header + "_" + this.type + "_" + this.id;
					final File tempFile = new File(MainService.getInstance().roundsConfig.DOWNLOAD_PATH + "event_" + name);
					if (tempFile.exists()) {
						runCallBack(tempFile);
					} else {
						final FileOutputStream readout = new FileOutputStream(tempFile);
						Request request = new Request(Request.QUERY_EVENT_FILE);
						UtilTools.toast(ContextManager.getInstance(), "读取文件中,请稍候...");
						request.send(null, readout, param, new SendStreamThread.CallBack() {

							@Override
							public void exception(Exception e) {
								UtilTools.toast(EventProcessActivity.this, "读取文件错误");
							}

							@Override
							public void complete() {
								try {
									readout.flush();
									readout.close();
								} catch (IOException e) {
								}
								runCallBack(tempFile);
							}
						});
					}
				}
			} catch (Exception e) {
				UtilTools.toast(EventProcessActivity.this, "查询文件失败");
			}
		}

		private void runCallBack(final File file) {
			EventProcessActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					try {
						callBack.callBack(file, bean);
					} catch (Exception e) {
						UtilTools.toast(EventProcessActivity.this, e.getMessage());
					}
				}
			});
		}
	}

	private class PlayEventVoice implements FileCallBack {

		@Override
		public void callBack(File file, Object bean) throws Exception {
			if (file == null || file.length() == 0) {
				UtilTools.toast(EventProcessActivity.this, "音频文件不存在");
				return;
			}
			MediaPlayer voicePlayer = new MediaPlayer();
			voicePlayer.setDataSource(file.getPath());
			voicePlayer.prepare();
			voicePlayer.start();
		}

	}

	private class PlayEventVideo implements FileCallBack {
		private MediaPlayer videoPlayer;
		private SurfaceView view;

		@Override
		public void callBack(File file, Object bean) throws Exception {
			if (file == null || file.length() == 0) {
				UtilTools.toast(EventProcessActivity.this, "视频文件不存在");
				return;
			}
			view = new SurfaceView(EventProcessActivity.this);
			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (videoPlayer.isPlaying()) {
						videoPlayer.pause();
					} else {
						videoPlayer.start();
					}
				}
			});

			view.getHolder().addCallback(new SurfaceHolder.Callback() {

				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					if (videoPlayer.isPlaying()) {
						videoPlayer.stop();
					}
				}

				@Override
				public void surfaceCreated(SurfaceHolder holder) {
					videoPlayer.prepareAsync();
					videoPlayer.setDisplay(holder);
				}

				@Override
				public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

				}
			});

			videoPlayer = new MediaPlayer();
			videoPlayer.setDataSource(file.getPath());
			videoPlayer.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer mp) {
					videoPlayer.seekTo(0);
					videoPlayer.start();
				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(EventProcessActivity.this);
			builder.setTitle("播放视频");
			builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					videoPlayer.stop();
					dialog.dismiss();
				}
			});
			builder.setView(view);

			builder.show();
		}
	}

	private class ShowEventImage implements FileCallBack {

		@Override
		public void callBack(File file, Object bean) throws Exception {
			if (file == null || file.length() == 0) {
				UtilTools.toast(EventProcessActivity.this, "图像文件不存在");
				return;
			}

			ImageView imageView = new ImageView(EventProcessActivity.this);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
			imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
			AlertDialog alert = new AlertDialog.Builder(EventProcessActivity.this, R.style.Dialog).create();
			alert.setTitle("查看图片");
			alert.setView(imageView);
			alert.setButton(DialogInterface.BUTTON_POSITIVE, "关闭", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alert.show();
		}
	}

	private class InfoButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (infoLayout.getVisibility() == View.GONE) {
				infoLayout.setVisibility(View.VISIBLE);
				infoButton.setText(R.string.event_process_info_up);
			} else {
				infoLayout.setVisibility(View.GONE);
				infoButton.setText(R.string.event_process_info_down);
			}
		}

	}

	private class ContextTypeChecked implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int id) {
			switch (id) {
			case R.id.event_process_context_type_text:
				contextText.setVisibility(View.VISIBLE);
				contextVoiceLayout.setVisibility(View.GONE);
				imageLayout.setVisibility(View.GONE);
				break;
			case R.id.event_process_context_type_voice:
				contextText.setVisibility(View.GONE);
				contextVoiceLayout.setVisibility(View.VISIBLE);
				imageLayout.setVisibility(View.GONE);
				break;
			case R.id.event_process_context_type_photo:
				contextText.setVisibility(View.GONE);
				contextVoiceLayout.setVisibility(View.GONE);
				imageLayout.setVisibility(View.VISIBLE);
				break;
			}
		}

	}

	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			EventProcessActivity.this.finish();
		}
	}

	private class InfoCordinatesButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			if(nowEvent != null){
				Intent intent = new Intent(EventProcessActivity.this, MapShowActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean("onlyshow", true);
				bundle.putFloat("lat", nowEvent.getLatitude());
				bundle.putFloat("log", nowEvent.getLongitude());
				intent.putExtras(bundle);
				EventProcessActivity.this.startActivity(intent);
			}
			
		}

	}

	private class VoiceTouch extends VoiceUtil {

		public VoiceTouch(Context context, String rootPath) {
			super(context, rootPath, true);
		}

		@Override
		public void volume(final int db) {
			EventProcessActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					contextVoiceProgressBar.setProgress(db);
				}
			});
		}

		@Override
		public void begin() {

		}

		@Override
		public void end(String voicePath) {
			EventProcessActivity.this.voicePath = voicePath;
		}

	}

	private class ImageLayoutOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			File file = new File(MainService.getInstance().roundsConfig.CAMERA_PHOTO_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}
			// GlideLoader 可用自己用的缓存库
			ImageConfig.Builder imageConfig = new ImageConfig.Builder(EventProcessActivity.this);
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
			imageConfig.showCamera().requestCode(SELECT_REQUEST_CODE);

			ImageSelector.open(EventProcessActivity.this, imageConfig.build()); // 开启图片选择器
		}
	}

	interface FileCallBack {
		public void callBack(File file, Object bean) throws Exception;
	}

	@Override
	public void displayImage(Context context, String path, ImageView imageView) {
		Glide.with(context).load(path).placeholder(R.drawable.imageselector_photo).centerCrop().into(imageView);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK || data == null) {
			return;
		}
		if (requestCode == SELECT_REQUEST_CODE) {
			if (path.size() == 0) {
				UtilTools.toast(this, "获取图片出错");
			} else {
				imagePath = path.get(0);
				path.clear();
				Bitmap bitmap = BitmapUtil.thumbnail(imagePath, 1024);
				try {
					bitmap.compress(Bitmap.CompressFormat.JPEG, 60, new FileOutputStream(imagePath));
				} catch (FileNotFoundException e) {
					UtilTools.toast(this, "压缩图片出错");
					return;
				}
				imageLayout.removeAllViews();
				ImageView image = new ImageView(this);
				image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
				image.setPadding(3, 3, 3, 3);
				image.setImageBitmap(bitmap);
				imageLayout.addView(image);
			}
		}
	}

	private class ProcessAnswerButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			final Params param = new Params();
			try {
				param.addTextParameter("event_rpe_id", String.valueOf(nowEvent.getRpe_id()));
				if (R.id.event_process_context_type_text == contextType.getCheckedRadioButtonId()) {
					if (contextText.getText().toString().length() < 1) {
						UtilTools.toast(EventProcessActivity.this, "内容为空");
						return;
					}
					param.addTextParameter("af.event_context_type", MESSAGE_TYPE.TEXT.toString());
					param.addTextParameter("af.event_context", contextText.getText().toString());
				} else if (R.id.event_process_context_type_voice == contextType.getCheckedRadioButtonId()) {
					if (voicePath == null) {
						UtilTools.toast(EventProcessActivity.this, "音频内容为空");
						return;
					}
					File file = new File(voicePath);
					if (!file.exists()) {
						UtilTools.toast(EventProcessActivity.this, "音频文件不存在");
						return;
					}
					param.addTextParameter("af.event_context_type", MESSAGE_TYPE.VOICE.toString());
					param.addFileParameter("af.event_context_file", file);
				} else {
					if (imagePath == null) {
						UtilTools.toast(EventProcessActivity.this, "图片为空");
						return;
					}
					File event_image = new File(imagePath);
					if (!event_image.exists()) {
						UtilTools.toast(EventProcessActivity.this, "图片不存在");
						return;
					}
					param.addTextParameter("af.event_context_type", MESSAGE_TYPE.PHOTO.toString());
					param.addFileParameter("af.event_context_file", event_image);
				}
			} catch (Exception e) {
				UtilTools.toast(EventProcessActivity.this, e.getMessage());
				return;
			}

			Builder alert = new AlertDialog.Builder(v.getContext());
			alert.setTitle("确认提交");
			alert.setPositiveButton("是", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						Request request = new Request(Request.SUBMIT_EVENT_PROCESS);
						request.sendRetJson(param, new SendRetJsonThread.CallBack() {

							@Override
							public void callBack(int tag, JSONObject json) {
								try {
									if (json.getBoolean("status")) {
										UtilTools.alertMessage(EventProcessActivity.this, "提交成功", new UtilTools.AlertCallBack() {

											@Override
											public void callBack() {
												queryProcess();
											}

										});
									} else {
										UtilTools.alertError(EventProcessActivity.this, json.getString("message"));
									}
								} catch (JSONException e) {
									UtilTools.alertError(EventProcessActivity.this, "操作失败:" + e.getMessage());
								}
							}

							@Override
							public void exception(int tag, Exception e) {
								UtilTools.alertError(EventProcessActivity.this, "操作失败" + e.getMessage());
							}

						});
					} catch (Exception e) {
						UtilTools.alertError(EventProcessActivity.this, "操作失败" + e.getMessage());
					}
					dialog.dismiss();
				}
			});
			alert.setNegativeButton("否", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			});
			alert.show();
		}
	}

}
