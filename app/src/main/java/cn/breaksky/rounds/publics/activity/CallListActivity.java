
package cn.breaksky.rounds.publics.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.PersonnelMessage;
import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;
import cn.breaksky.rounds.publics.listener.MessageObj.MESSAGE_TYPE;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendCheckLoginThread;
/**
 * 交流互动页面
 * @author Administrator
 *
 */
public class CallListActivity  extends Activity {

	private ListView lv_view;
	List<PersonnelMessage> pms;
	private RoundPersonnelVO nowPersonnel;
	public final static String SER_KEY = "NOWPERSONNEL";
	/** <b>查询人员按钮</b> */
	private TextView callPersonnelButton;
	
	/** <b>返回按钮</b> */
	private TextView call_back_button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calllistview);
		
		callPersonnelButton = (TextView) findViewById(R.id.call_personnel_button);
		callPersonnelButton.setOnClickListener(new CallPersonnelOnClick());
		
		call_back_button = (TextView) findViewById(R.id.call_back_button);
		call_back_button.setOnClickListener(new exitBackButtonOnClick());
		
		lv_view=(ListView) findViewById(R.id.lv_view);
		lv_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            	 
            	nowPersonnel = new RoundPersonnelVO(); 
				Long rpid=pms.get(i).getRpid();
                nowPersonnel.setRp_id(rpid);
                nowPersonnel.setName(pms.get(i).getPersonname());
    			
    			Intent mIntent = new Intent(CallListActivity.this, CallActivity.class);  
    	    	Bundle mBundle = new Bundle();   
    	    	mBundle.putSerializable(SER_KEY,nowPersonnel);   
    	    	mIntent.putExtras(mBundle);	
    	    	startActivity(mIntent);	
            }
        });
	
		
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getPersonMessageList();
	}


	private class CallPersonnelOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(CallListActivity.this, CheckPersonActivity.class);
			startActivity(intent);
			
		}
	}
	
	private void getPersonMessageList() {
		try {
			Request request = new Request(Request.QUERY_PERSONNEL_MESSAGElIST);
			
			RoundPersonnelVO user = MainService.getInstance().getUser();
			if(user==null)
				return ;
			
			Params param = new Params();
			param.addTextParameter("af.personid", user.getRp_id().toString());
			
			request.sendCheckLogin(param, new SendCheckLoginThread.CallBack() {

				@Override
				public void callBack(JSONObject json) {
					Message msg = new Message();
					RetBean bean = new RetBean();
					bean.json = json;
					msg.obj = bean;
					handler.sendMessage(msg);
				}

				@Override
				public void exception(Exception e) {
					Message msg = new Message();
					RetBean bean = new RetBean();
					bean.message = e.toString();
					msg.obj = bean;
					handler.sendMessage(msg);
				}

			});

		} catch (Exception e) {
			UtilTools.alertMessage(CallListActivity.this, "查询会话列表失败!");
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ShowPersonMessageList((RetBean) msg.obj);
		}
	};
	
	class RetBean {
		JSONObject json;
		String message;
	}
	private void ShowPersonMessageList(RetBean obj) {
		if (obj.json == null) {
			UtilTools.toast(CallListActivity.this, obj.message);
		} else {
			try {
				if (obj.json.getBoolean("query_status")) {
					
				 pms = UtilTools.jsonToBean(obj.json.getJSONArray("query_data"), PersonnelMessage.class);
					
					if (pms == null || pms.size() < 1) {
						//UtilTools.toast(this, "联系人为空");
					return;
					
					}
					List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
					for (PersonnelMessage vo : pms) {
						
						
						HashMap<String, Object> item = new HashMap<String, Object>();
						item.put("txt_name", vo.getPersonname());
						String type=vo.getMessagetype();
						String str = "";
						
						if (MESSAGE_TYPE.TEXT.toString().equals(type)) {
							str = vo.getMessagecontent();
							
							if(str.length()>40)
							{
								str=str.substring(0,39)+"....";
							}
							
						} else if (MESSAGE_TYPE.PHOTO.toString().equals(type)) {
							str = "图片...";
							
						} else if (MESSAGE_TYPE.VOICE.toString().equals(type)) {
							str = "语音...";
							
						} else if (MESSAGE_TYPE.COORDINATE.toString().equals(type)) {
							str = "位置...";
						}
						item.put("txt_message", str);
						item.put("txt_messagetime", vo.getMessagetime());
						data.add(item);
					}
					
					
					SimpleAdapter adapter = new SimpleAdapter(CallListActivity.this,data,R.layout.item_call,
							new String[]{"txt_name","txt_message","txt_messagetime"},new int[]{R.id.txt_name,R.id.txt_message
							,R.id.txt_messagetime});	
					lv_view.setAdapter(adapter); 
				
					}
			}catch (Exception e) {
					UtilTools.toast(CallListActivity.this, "查询对话列表失败!" + e.getMessage());
				}
				
		}
	}
	
	private class exitBackButtonOnClick implements View.OnClickListener {
		@Override
		public void onClick(View view) {
/*			Intent intent = new Intent(CallListActivity.this, MainActivity.class);
			CallListActivity.this.startActivity(intent);*/
			
			CallListActivity.this.finish();
		}
	}


}
