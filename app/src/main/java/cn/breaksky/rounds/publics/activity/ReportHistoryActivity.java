package cn.breaksky.rounds.publics.activity;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.ReportBean;
import cn.breaksky.rounds.publics.util.UtilTools;

/**
 * 日常汇报历史查看页面
 * @author 吴淑娟 2017-7-17
 *
 */
public class ReportHistoryActivity extends Activity {
	
	/**<b>经度</b>*/
	private TextView rh_cordinates_log;
	/**<b>纬度</b>*/
	private TextView rh_cordinates_lat;
	/**<b>地址</b>*/
	private TextView rh_address;
	/**<b>描述内容</b>*/
	private TextView rh_context;
	/**<b>联系人</b>*/
	private TextView rh_contact;
	/**<b>电话</b>*/
	private TextView rh_phone;
	/**<b>上传时间</b>*/
	private TextView rh_time;
	/** <b>当前选中的日常汇报记录</b> */
	private ReportBean nowEvent = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reporthistory);

		rh_cordinates_log = (TextView) findViewById(R.id.rh_cordinates_log);	
		rh_cordinates_lat = (TextView) findViewById(R.id.rh_cordinates_lat);
		rh_address = (TextView) findViewById(R.id.rh_address);
		rh_context = (TextView) findViewById(R.id.rh_context);
		rh_contact = (TextView) findViewById(R.id.rh_contact);
		rh_phone = (TextView) findViewById(R.id.rh_phone);
		rh_time = (TextView) findViewById(R.id.rh_time);
		
		//从ReportActivity中获取选中信息
		nowEvent = (ReportBean)getIntent().getSerializableExtra(ReportActivity.SER_KEY);
		if (nowEvent == null) {
			return;
		}else{
			rh_cordinates_log.setText(nowEvent.getLongitude().toString());
			rh_cordinates_lat.setText(nowEvent.getLatitude().toString());
			rh_address.setText(nowEvent.getAddress().toString());
			rh_context.setText(nowEvent.getContext().toString());
			rh_contact.setText(nowEvent.getContact().toString());
			rh_phone.setText(nowEvent.getPhone().toString());
			
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(nowEvent.getCreatetime());
			rh_time.setText(UtilTools.formatDate(time.getTime(), "yyyy-MM-dd HH:mm:ss"));
		}
		
	};

}
