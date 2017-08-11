package cn.breaksky.rounds.publics.activity;

import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.PublicMessageBean;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendRetJsonThread;
/**
 * 公告预警页面
 * @author Administrator
 *
 */
public class MessageActivity extends Activity {
	private TextView cancelButton;// message_back_button
	private LinearLayout messageViewLayout;// message_view_layout
	private List<PublicMessageBean> beans;
	

	public final static String SER_KEY = "NOWNEWS";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ContextManager.addActivity(this);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_message);

		cancelButton = (TextView) this.findViewById(R.id.message_back_button);
		messageViewLayout = (LinearLayout) this.findViewById(R.id.message_view_layout);

		cancelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));

		cancelButton.setOnClickListener(new CancelButtonOnClick());
	}

	public void onResume() {
		super.onResume();
		queryMessage();
	}

	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			MessageActivity.this.finish();
		}
	}

	private void queryMessage() {
		try {
			Request request = new Request(Request.QUERY_MESSAGE);
			request.sendRetJson(new Params(), new SendRetJsonThread.CallBack() {

				@Override
				public void exception(int tag, Exception e) {
					showErrorAndClose(e.getMessage());
				}

				@Override
				public void callBack(int tag, JSONObject json) {
					try {
						if (json.getBoolean("status")) {
							beans = UtilTools.jsonToBean(json.getJSONArray("data"), PublicMessageBean.class);
							MessageActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									showMessage();
								}
							});

						} else {
							showErrorAndClose(json.getString("message"));
						}
					} catch (Exception e) {
						showErrorAndClose("查询历史错误");
					}
				}
			});
		} catch (Exception e) {
			this.showErrorAndClose("查询历史失败");
		}
	}

	private void showMessage() {
		messageViewLayout.removeAllViews();
		int timeGroup = 0;
		LinearLayout messageLayout = null;
		for (PublicMessageBean bean : beans) {
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(bean.getPublicstime());
			int nowTimeGroup = time.get(Calendar.YEAR) + (time.get(Calendar.MONTH)+1) + time.get(Calendar.DATE);
			if (timeGroup == 0 || nowTimeGroup != timeGroup) {
				timeGroup = nowTimeGroup;
				// 创建新的日期组
				messageLayout = new LinearLayout(this);
				messageLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				messageLayout.setOrientation(LinearLayout.VERTICAL);

				TextView timeView = new TextView(this);
				timeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				timeView.setGravity(Gravity.CENTER);
				timeView.setText(time.get(Calendar.YEAR) + "-" + (time.get(Calendar.MONTH)+1) + "-" + time.get(Calendar.DATE));
				timeView.setTextColor(this.getResources().getColor(R.color.grey));
				timeView.setTextSize(12);
				messageLayout.addView(timeView);

				messageViewLayout.addView(messageLayout);
			}
			String hour_c = String.valueOf(time.get(Calendar.HOUR_OF_DAY));//时  
			String minute_c = String.valueOf(time.get(Calendar.MINUTE));//分 
			String Second_c = String.valueOf(time.get(Calendar.SECOND));//秒 
			if(Integer.parseInt(hour_c)<10){
				hour_c ="0"+hour_c;
			}
			if(Integer.parseInt(minute_c)<10){
				minute_c ="0"+minute_c;
			}
			if(Integer.parseInt(Second_c)<10){
				Second_c ="0"+Second_c;
			} 
			TextView titleView = new TextView(this);
			titleView.setGravity(Gravity.LEFT);
			StringBuffer str = new StringBuffer("• ");
			str.append(bean.getTitle());
			str.append("[");
			str.append(hour_c);
			str.append(":");
			str.append(minute_c);
			str.append(":");
			str.append(Second_c);
			str.append("]");
			titleView.setText(str.toString());
			titleView.setTextSize(20);
			try {
				titleView.setTextColor(Long.valueOf("FF" + bean.getColor(), 16).intValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// titleView.setTextColor(0xFF0000FF);

			TextView contextView = new TextView(this);
			contextView.setBackgroundResource(R.xml.bg_border1);
			contextView.setGravity(Gravity.LEFT);
			contextView.setPadding(3, 3, 3, 3);
			contextView.setText("        " + bean.getContext());
			contextView.setTextColor(this.getResources().getColor(R.color.grey));
			contextView.setTextSize(16);
			contextView.setVisibility(View.GONE);
			titleView.setOnClickListener(new MessageTitleOnClick(contextView,bean));
			messageLayout.addView(titleView);
			messageLayout.addView(contextView);
		}
	}

	private class MessageTitleOnClick implements View.OnClickListener {
		private TextView contextView;
		private PublicMessageBean nowNews = null;
		public MessageTitleOnClick(TextView contextView,PublicMessageBean nowNews) {
			this.contextView = contextView;
			this.nowNews = nowNews;
		}

		@Override
		public void onClick(View v) {
			if (contextView.getVisibility() == View.GONE) {
				if(nowNews != null){
					//打开浏览新闻页面
			        Intent intent = new Intent(MessageActivity.this, NewsActivity.class);
				    Bundle mBundle = new Bundle();   
					mBundle.putSerializable(SER_KEY,nowNews);   
					intent.putExtras(mBundle);	
					startActivity(intent);								
				}
			} else {
				contextView.setVisibility(View.GONE);
			}
		}

	}

	private void showErrorAndClose(String message) {
		UtilTools.alertError(this, message, new UtilTools.AlertCallBack() {

			@Override
			public void callBack() {
				MessageActivity.this.finish();
			}
		});
	}

}
