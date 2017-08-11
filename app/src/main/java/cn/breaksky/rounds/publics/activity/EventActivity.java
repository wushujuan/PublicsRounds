package cn.breaksky.rounds.publics.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;
import cn.breaksky.rounds.publics.listener.MessageObj.MESSAGE_TYPE;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.BitmapUtil;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.VoiceUtil;
import cn.breaksky.rounds.publics.util.imageselector.ImageConfig;
import cn.breaksky.rounds.publics.util.imageselector.ImageLoader;
import cn.breaksky.rounds.publics.util.imageselector.ImageSelector;

import com.bumptech.glide.Glide;
/**
 * 灾害报警页面
 * @author Administrator
 *
 */
public class EventActivity extends Activity implements ImageLoader {
	private static final long serialVersionUID = -4737771134901966766L;
	/** <b>定位窗口返回参数标志声明</b> */
	private final static int CORDINATES_MAP = 1000;
	/** <b>第一张图片返回标志声明</b> */
	private final static int SELECT_REQUEST_CODE = 1001;
	/** <b>第二张图片返回标志声明</b> */
	private final static int SELECT_REQUEST_CODE2 = 1002;
	/** <b>第三张图片返回标志声明</b> */
	private final static int SELECT_REQUEST_CODE3 = 1003;
	
	/**<b>第一张图片存储路径声明</b>*/
	private ArrayList<String> path = new ArrayList<String>();
	/**<b>第二张图片存储路径声明</b>*/
	private ArrayList<String> path2 = new ArrayList<String>();
	/**<b>第三张图片存储路径声明</b>*/
	private ArrayList<String> path3 = new ArrayList<String>();
	
	/**<b>经度坐标声明</b>*/
	private EditText cordinatesLog;
	/**<b>纬度坐标声明</b>*/
	private EditText cordinatesLat;
	/**<b>地址声明</b>*/
	private EditText address;
	/**<b>描述内容声明</b>*/
	private EditText contextText;
	/**<b>联系人声明</b>*/
	private EditText contact;
	/**<b>联系电话声明</b>*/
	private EditText phone;
	/**<b>提交按钮声明</b>*/
	private Button submitButton;
	/**<b>定位按钮声明</b>*/
	private Button cordinatesButton;
	/**<b>返回按钮声明</b>*/
	private TextView cancelButton;
	/**<b>多选按钮声明</b>*/
	private RadioGroup contextType;
	/**<b>语音按钮声明</b>*/
	private ImageButton contextVoiceBtton;
	private ProgressBar contextVoiceProgressBar;
	private LinearLayout contextVoice;
	
	/**<b>第一张图片</b>*/
	private LinearLayout imageLayout;// event_image_layout
	/**<b>第二张图片</b>*/
	private LinearLayout imageLayout2;// event_image_layout2
	/**<b>第三张图片</b>*/
	private LinearLayout imageLayout3;// event_image_layout3
	/**<b>第一张图片存放路径</b>*/
	private String imagePath = null;
	/**<b>第二张图片存放路径</b>*/
	private String imagePath2 = null;
	/**<b>第三张图片存放路径</b>*/
	private String imagePath3 = null;
	/**<b>语音存放路径</b>*/
	private String voicePath;
	/**<b>三张图片路径拼接成一个字符串</b>*/
	private String plus_image="";
	/**<b>第一张图片文件名</b>*/
	private File event_image = null;
	/**<b>第二张图片文件名</b>*/
	private File event_image2 = null;
	/**<b>第三张图片文件名</b>*/
	private File event_image3 = null;
	
	public void onCreate(Bundle savedInstanceState) {
		ContextManager.addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);

		cordinatesLog = (EditText) findViewById(R.id.event_cordinates_log);
		cordinatesLat = (EditText) findViewById(R.id.event_cordinates_lat);
		cordinatesButton = (Button) findViewById(R.id.event_cordinates_button);
		address = (EditText) findViewById(R.id.event_address);
		
		imageLayout = (LinearLayout) findViewById(R.id.event_image_layout);
		imageLayout2 = (LinearLayout) findViewById(R.id.event_image_layout2);
		imageLayout3 = (LinearLayout) findViewById(R.id.event_image_layout3);
		imageLayout.setOnClickListener(new EventImageOnClick());
		imageLayout2.setOnClickListener(new EventImageOnClick());
		imageLayout3.setOnClickListener(new EventImageOnClick());
		
		contextType = (RadioGroup) findViewById(R.id.event_context_type);
		contextText = (EditText) findViewById(R.id.event_context_text);
		contextVoiceBtton = (ImageButton) findViewById(R.id.event_context_voice_button);
		contextVoice = (LinearLayout) findViewById(R.id.event_context_voice);
		contextVoiceProgressBar = (ProgressBar) findViewById(R.id.event_context_voice_bar);
		contact = (EditText) findViewById(R.id.event_contact);
		phone = (EditText) findViewById(R.id.event_phone);
		cancelButton = (TextView) findViewById(R.id.event_back_button);
		submitButton = (Button) findViewById(R.id.event_submit_button);

		// OnTouchListener
		cancelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		submitButton.setOnTouchListener(new ButtonTouchListener(R.drawable.login_button_bg_touch, R.drawable.login_button_bg));
		// OnCheckedChangeListener
		contextType.setOnCheckedChangeListener(new ContextTypeChecked());
		// OnClickListener
		cancelButton.setOnClickListener(new CancelButtonOnClick());
		cordinatesLog.setOnClickListener(new CordinatesOnClick(getResources().getString(R.string.event_cordinates_log)));
		cordinatesLat.setOnClickListener(new CordinatesOnClick(getResources().getString(R.string.event_cordinates_lat)));
		cordinatesButton.setOnClickListener(new CordinatesButtonOnClick());
		contextVoiceBtton.setOnTouchListener(new VoiceTouch(this, MainService.getInstance().roundsConfig.VOICE_PATH));
		
		submitButton.setOnClickListener(new SubmitButtonOnClick());

		//登录用户信息
		RoundPersonnelVO pvo = MainService.getInstance().getUser();
		if (pvo != null) {
			contact.setText(pvo.getName());
			phone.setText(pvo.getMobile());
		}
	};

	/**
	 * 描述内容：文本和语音切换
	 *
	 */
	private class ContextTypeChecked implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int id) {
			if (id == R.id.event_context_type_text) {
				contextText.setVisibility(View.VISIBLE);
				contextVoice.setVisibility(View.GONE);
			} else {
				contextVoice.setVisibility(View.VISIBLE);
				contextText.setVisibility(View.GONE);
			}
		}
	};
	/**
	 * 提交按钮响应事件
	 *
	 */
	private class SubmitButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			final String event_longitude = cordinatesLog.getText().toString();
			if (event_longitude.length() < 1 || event_longitude.equals(getResources().getString(R.string.event_cordinates_log))) {
				UtilTools.toast(EventActivity.this, "经度为空");
				return;
			}
			final String event_latitude = cordinatesLat.getText().toString();
			if (event_latitude.length() < 1 || event_latitude.equals(getResources().getString(R.string.event_cordinates_lat))) {
				UtilTools.toast(EventActivity.this, "纬度为空");
				return;
			}
			final String event_address = address.getText().toString();
			if (event_address.length() < 1) {
				UtilTools.toast(EventActivity.this, "地址为空");
				return;
			}
			final String event_contact = contact.getText().toString();
			if (event_contact.length() < 1) {
				UtilTools.toast(EventActivity.this, "联系人为空");
				return;
			}
			final String event_phone = phone.getText().toString();
			if (event_phone.length() < 1) {
				UtilTools.toast(EventActivity.this, "联系电话为空");
				return;
			}
			if (imagePath == null&&imagePath2 == null&&imagePath3 == null) {
				UtilTools.toast(EventActivity.this, "图片为空");
				return;
			}
			if(imagePath != null){
				event_image = new File(imagePath);	
				plus_image+=event_image+";";
			}else{
				plus_image+=" ;";
			}
			
			if(imagePath2 != null){
				event_image2 = new File(imagePath2);	
				plus_image+=event_image2+";";
			}else{
				plus_image+=" ;";
			}
			
			if(imagePath3 != null){
				event_image3 = new File(imagePath3);	
				plus_image+=event_image3;
			}else{
				plus_image+=" ";
			}

			if (R.id.event_context_type_text == contextType.getCheckedRadioButtonId()) {
				if (contextText.getText().toString().length() < 1) {
					UtilTools.toast(EventActivity.this, "描述内容为空");
					return;
				}
			} else {
				if (voicePath == null) {
					UtilTools.toast(EventActivity.this, "描述音频内容为空");
					return;
				}
				if (!(new File(voicePath)).exists()) {
					UtilTools.toast(EventActivity.this, "描述音频文件不存在");
					return;
				}
			}

			Builder alert = new AlertDialog.Builder(v.getContext());
			alert.setTitle("确认提交");
			alert.setPositiveButton("是", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String event_context_type = "";
					String event_context_file = "";
					String event_context = "";
					if (R.id.event_context_type_text == contextType.getCheckedRadioButtonId()) {
						event_context_type = MESSAGE_TYPE.TEXT.toString();
						event_context = contextText.getText().toString();
					} else {
						event_context_type = MESSAGE_TYPE.VOICE.toString();
						event_context_file = voicePath;
					}
					SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
					String insertSql = "insert into eventinfo(longitude, latitude,address,contact,phone,image,context_type,context,context_file,time) values(?,?,?,?,?,?,?,?,?,?)";
					Object obj[] = new Object[] { event_longitude, event_latitude, event_address, event_contact, event_phone, plus_image, event_context_type, event_context, event_context_file,
							(new Date()).getTime() };
					db.execSQL(insertSql, obj);

					EventActivity.this.finish();
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
	};
	/**
	 * 返回按钮响应事件
	 *
	 */
	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			EventActivity.this.finish();
		}
	};
	private class CordinatesOnClick implements OnClickListener {
		private String value;

		public CordinatesOnClick(String value) {
			this.value = value;
		}

		@Override
		public void onClick(View v) {
			EditText textView = (EditText) v;
			if (value.equals(textView.getText().toString())) {
				textView.setText("");
			}
		}
	};
	/**
	 * 定位按钮响应事件
	 *
	 */
	private class CordinatesButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(EventActivity.this, MapCordinatesActivity.class);
			startActivityForResult(intent,CORDINATES_MAP);
		}
	}

	/**
	 * 图片的响应事件
	 *
	 */
	private class EventImageOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.event_image_layout:
				showImage(MainService.getInstance().roundsConfig.CAMERA_PHOTO_PATH,
						path,SELECT_REQUEST_CODE);
				break;
			case R.id.event_image_layout2:							
				showImage(MainService.getInstance().roundsConfig.CAMERA2_PHOTO_PATH,
						path2,SELECT_REQUEST_CODE2);
				break;
			case R.id.event_image_layout3:
				showImage(MainService.getInstance().roundsConfig.CAMERA3_PHOTO_PATH,
						path3,SELECT_REQUEST_CODE3);				
				break;
			}
		}
	};

	/**
	 * 语音功能
	 *
	 */
	private class VoiceTouch extends VoiceUtil {

		public VoiceTouch(Context context, String rootPath) {
			super(context, rootPath, true);
		}

		@Override
		public void volume(final int db) {
			EventActivity.this.runOnUiThread(new Runnable() {

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
			EventActivity.this.voicePath = voicePath;
		}

	};
	/**
	 * 打开图库和拍照功能
	 * @param filepath 图片存储路径
	 * @param path  图片临时存放
	 * @param equest_code 请求码
	 */
	private void showImage(String filepath,ArrayList<String> path,int equest_code){
		File file = new File(filepath);
		if (!file.exists()) {
			file.mkdirs();
		}
		// GlideLoader 可用自己用的缓存库
		ImageConfig.Builder imageConfig = new ImageConfig.Builder(EventActivity.this);
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
		imageConfig.showCamera().requestCode(equest_code);
		ImageSelector.open(EventActivity.this, imageConfig.build()); // 开启图片选择器		
	};	
	/**
	 * 图片显示功能
	 * @param filePath 保存图片路径
	 * @param imageLayout 显示图片视图
	 */
	private void FromFileShowImage(String filePath,LinearLayout imageLayout){
		Bitmap bitmap = BitmapUtil.thumbnail(filePath, 1024);
		try {
			bitmap.compress(Bitmap.CompressFormat.JPEG, 60,
					new FileOutputStream(filePath));
		} catch (FileNotFoundException e) {
			UtilTools.toast(this, "压缩图片出错");
			return;
		}
		imageLayout.removeAllViews();
		ImageView image = new ImageView(this);
		image.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		image.setPadding(3, 3, 3, 3);
		image.setImageBitmap(bitmap);
		imageLayout.addView(image);	
	};	
	/**
	 * 回调事件
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK || data == null) {
			return;
		}
		if (requestCode == CORDINATES_MAP) {
			Bundle point = data.getBundleExtra("point");
			if (point != null) {
				cordinatesLog.setText(String.valueOf(point.getDouble("longitude")));
				cordinatesLat.setText(String.valueOf(point.getDouble("latitude")));
				this.address.setText(point.getString("address"));
			}
		} else if (requestCode == SELECT_REQUEST_CODE) {
			if (path.size() == 0) {
				UtilTools.toast(this, "获取图片出错");
			} else {
				imagePath = path.get(0);
				path.clear();
				FromFileShowImage(imagePath,imageLayout);
			}
		}else if (requestCode == SELECT_REQUEST_CODE2){
			if (path2.size() == 0) {
				UtilTools.toast(this, "获取图片出错");
			} else {
				imagePath2 = path2.get(0);
				path2.clear();
				FromFileShowImage(imagePath2,imageLayout2);
			}
			
		}else if (requestCode == SELECT_REQUEST_CODE3){
			if (path3.size() == 0) {
				UtilTools.toast(this, "获取图片出错");
			} else {
				imagePath3 = path3.get(0);
				path3.clear();
				FromFileShowImage(imagePath3,imageLayout3);
			}
		}
	}
	@Override
	public void displayImage(Context context, String path, ImageView imageView) {
		Glide.with(context).load(path).placeholder(R.drawable.imageselector_photo).centerCrop().into(imageView);
	}
}
