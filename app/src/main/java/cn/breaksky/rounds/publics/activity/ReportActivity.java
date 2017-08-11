package cn.breaksky.rounds.publics.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.ReportBean;
import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;
import cn.breaksky.rounds.publics.listener.ReportListener;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import android.support.v7.app.AppCompatActivity;
/**
 * 新增日常巡查模块
 * @author 吴淑娟 2017-7-14
 *
 */
public class ReportActivity extends Activity {
	/** <b>经度坐标声明</b> */
	private EditText cordinatesLog;
	/** <b>纬度坐标声明</b> */
	private EditText cordinatesLat;
	/** <b>地址声明</b> */
	private EditText address;
	/** <b>描述内容声明</b> */
	private EditText contextText;
	/** <b>联系人声明</b> */
	private EditText contact;
	/** <b>联系电话声明</b> */
	private EditText phone;
	/** <b>定位按钮声明</b> */
	private Button cordinatesButton;
	/** <b>提交按钮声明</b> */
	private Button submitButton;
	/** <b>返回按钮声明</b> */
	private TextView cancelButton;
	/** <b>历史按钮声明</b> */
	private TextView historyButton;
	
	private final static int CORDINATES_MAP = 1000;
	/** <b>窗口之间传递参数标志位声明</b> */
	public final static String SER_KEY = "NOWPERSONNEL";
	/** <b>日常汇报集合</b> */
	private List<ReportBean> reportBean = null;
	/** <b>当前选中的日常汇报记录</b> */
	private ReportBean nowEvent = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ContextManager.addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		
		cordinatesLog = (EditText) findViewById(R.id.report_cordinates_log);
		cordinatesLat = (EditText) findViewById(R.id.report_cordinates_lat);
		address = (EditText) findViewById(R.id.report_address);
		contextText = (EditText) findViewById(R.id.report_context_text);
		contact = (EditText) findViewById(R.id.report_contact);
		phone = (EditText) findViewById(R.id.report_phone);
		
		cordinatesButton = (Button) findViewById(R.id.report_cordinates_button);
		cordinatesButton.setOnClickListener(new CordinatesButtonOnClick());
		
		cancelButton = (TextView) findViewById(R.id.report_back_button);
		cancelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		cancelButton.setOnClickListener(new CancelButtonOnClick());
		
		submitButton = (Button) findViewById(R.id.report_submit_button);
		submitButton.setOnClickListener(new SubmitButtonOnClick());
		submitButton.setOnTouchListener(new ButtonTouchListener(R.drawable.login_button_bg_touch, R.drawable.login_button_bg));
		
		historyButton = (TextView) findViewById(R.id.report_process_history_button);
		historyButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		historyButton.setOnClickListener(new HistroyButtonOnClick());
		
		RoundPersonnelVO pvo = MainService.getInstance().getUser();
		if (pvo != null) {
			contact.setText(pvo.getName());
			phone.setText(pvo.getMobile());
		}
		
	}; 
	
	/**
	 * 历史按钮响应事件
	 *
	 */
	private class HistroyButtonOnClick implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			reportBean = ReportListener.queryEvent();//从SQLite本地数据库中获取数据
			if (reportBean == null) {
				return;
			}
			RadioGroup group = new RadioGroup(ReportActivity.this);
			for (ReportBean bean : reportBean) {
				Calendar time = Calendar.getInstance();
				time.setTimeInMillis(bean.getCreatetime());
				StringBuffer str = new StringBuffer();
				str.append(bean.getContact());
				str.append("(");
				str.append(UtilTools.formatDate(time.getTime(), "yyyy-MM-dd HH:mm:ss"));
				str.append(")");
				RadioButton button = new RadioButton(ReportActivity.this);
				button.setTag(bean);
				button.setText(str.toString());
				button.setTextColor(ReportActivity.this.getResources().getColor(R.color.white));
				group.addView(button);
			}
			AlertDialog alert = new AlertDialog.Builder(ReportActivity.this, R.style.Dialog).create();
			alert.setTitle("查看历史");
			alert.setView(group);
			alert.setButton(DialogInterface.BUTTON_POSITIVE, "关闭", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alert.show();
			group.setOnCheckedChangeListener(new HistroyRadioOnClecked(alert));//选中某一条记录
			
		}
	};
	/**
	 * 选中某条记录的响应事件
	 *
	 */
	private class HistroyRadioOnClecked implements RadioGroup.OnCheckedChangeListener {
		private AlertDialog dialog;

		public HistroyRadioOnClecked(AlertDialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void onCheckedChanged(RadioGroup group, int index) {
			RadioButton button = (RadioButton) group.findViewById(index);
			ReportBean bean = (ReportBean) button.getTag();
			nowEvent = bean;	//选中的某条记录
			Intent intent = new Intent(ReportActivity.this, ReportHistoryActivity.class);
	    	Bundle mBundle = new Bundle();   
	    	mBundle.putSerializable(SER_KEY,nowEvent);   
	    	intent.putExtras(mBundle);	
	    	startActivity(intent);	
			this.dialog.dismiss();
		}
	};
	/**
	 * 定位按钮响应事件
	 *
	 */
	private class CordinatesButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
				
			Intent intent = new Intent(ReportActivity.this, MapCordinatesActivity.class);
			startActivityForResult(intent,CORDINATES_MAP);
		}
	};
	/**
	 * 返回按钮响应事件
	 *
	 */	
	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			ReportActivity.this.finish();
		}
	};
	/**
	 * 提交按钮响应事件
	 *
	 */
	private class SubmitButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			final String report_longitude = cordinatesLog.getText().toString();
			if (report_longitude.length() < 1 || report_longitude.equals(getResources().getString(R.string.event_cordinates_log))) {
				UtilTools.toast(ReportActivity.this, "经度为空");
				return;
			}
			final String report_latitude = cordinatesLat.getText().toString();
			if (report_latitude.length() < 1 || report_latitude.equals(getResources().getString(R.string.event_cordinates_lat))) {
				UtilTools.toast(ReportActivity.this, "纬度为空");
				return;
			}
			final String report_address = address.getText().toString();
			if (report_address.length() < 1) {
				UtilTools.toast(ReportActivity.this, "地址为空");
				return;
			}
			final String report_contact = contact.getText().toString();
			if (report_contact.length() < 1) {
				UtilTools.toast(ReportActivity.this, "联系人为空");
				return;
			}
			final String report_phone = phone.getText().toString();
			if (report_phone.length() < 1) {
				UtilTools.toast(ReportActivity.this, "联系电话为空");
				return;
			}
			final String report_context = contextText.getText().toString();
			if (contextText.getText().toString().length() < 1) {
				UtilTools.toast(ReportActivity.this, "描述内容为空");
				return;
			}
			Builder alert = new AlertDialog.Builder(v.getContext());
			alert.setTitle("确认提交");
			alert.setPositiveButton("是", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					//数据提交到本地数据库
					SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
					String insertSql = "insert into reportinfo(longitude, latitude,address,contact,phone,context,time,serviceid) values(?,?,?,?,?,?,?,?)";
					Object obj[] = new Object[] { report_longitude, report_latitude, report_address, report_contact, report_phone, report_context,
							(new Date()).getTime(),0 };
					db.execSQL(insertSql, obj);
					UtilTools.toast(ReportActivity.this, "提交数据完成");
					ReportActivity.this.finish();
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
	 * 回调事件
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK || data == null) {
			return;
		}
		if (requestCode == CORDINATES_MAP) { //当关闭MapCordinatesActivity窗口时，获取窗口返回的参数值
			Bundle point = data.getBundleExtra("point");
			if (point != null) {
				cordinatesLog.setText(String.valueOf(point.getDouble("longitude")));
				cordinatesLat.setText(String.valueOf(point.getDouble("latitude")));
				this.address.setText(point.getString("address"));
			}
		}
	};
}
