package cn.breaksky.rounds.publics.activity;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.PublicMessageBean;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendRetJsonThread;

/**
 * 公告预警消息详情页面
 * @author 吴淑娟 2017-7-21
 *
 */
public class NewsActivity extends Activity {
	/**<b>返回按键</b>*/
	private TextView cancelButton;// news_back_button
	/**<b>当前选中的新闻内容</b>*/
	private PublicMessageBean nowNews = null;
	/**<b>新闻发布时间</b>*/
	private TextView news_time;
	/**<b>新闻标题</b>*/
	private TextView news_titel;
	/**<b>新闻内容</b>*/
	private TextView news_context;
	/**<b>公告预警id</b>*/
	private long rpm_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		
		news_context = (TextView) findViewById(R.id.news_context);
		news_time = (TextView) findViewById(R.id.news_time);
		news_titel = (TextView) findViewById(R.id.news_titel);
		cancelButton = (TextView) this.findViewById(R.id.news_back_button);
		cancelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		cancelButton.setOnClickListener(new CancelButtonOnClick());
		
		//从MessageActivity中获取选中信息
		nowNews = (PublicMessageBean)getIntent().getSerializableExtra(MessageActivity.SER_KEY);
		if(nowNews!=null){
			news_context.setText(nowNews.getContext());
			
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(nowNews.getPublicstime());
			news_time.setText(UtilTools.formatDate(time.getTime(), "yyyy-MM-dd HH:mm:ss"));
			
			news_titel.setText(nowNews.getTitle());
			try {
				news_titel.setTextColor(Long.valueOf("FF" + nowNews.getColor(), 16).intValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		rpm_id = nowNews.getRpm_id();
		queryMessageReadBack();	//公告预警消息是否阅读
	};
	/**
	 * 返回按钮
	 *
	 */
	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			NewsActivity.this.finish();
		}
	}
	/**
	 * 公告预警是否阅读
	 */
	public void queryMessageReadBack() {
		try {
			Request request = new Request(Request.PUBLICS_SUBMIT_MESSAGE_READ);
			Params param = new Params();
			param.addTextParameter("af.rpm_id", String.valueOf(rpm_id));
		    request.sendRetJson(param, new SendRetJsonThread.CallBack() {
				@Override
				public void callBack(int tag, JSONObject json) {
					try {
						if (json.getBoolean("status")) {//发送成功
							if(MainService.getInstance().maxPMessage == 1){//角标隐藏
								MainService.getInstance().maxPMessage = 0;
							}
							//UtilTools.toast(NewsActivity.this, "新闻是否阅读标志发送成功!");
						} else {
							//UtilTools.toast(NewsActivity.this, "发送失败!");
						}
					} catch (JSONException e) {
						//UtilTools.toast(NewsActivity.this, "发送失败!");
						e.printStackTrace();
					}
					
				}
				@Override
				public void exception(int tag, Exception e) {
					//UtilTools.toast(NewsActivity.this, "发送失败!" + e.getMessage());
				}

			});

		} catch (Exception e) {
			//UtilTools.toast(NewsActivity.this, "发送失败!" + e.getMessage());
		}
		
	}

}
