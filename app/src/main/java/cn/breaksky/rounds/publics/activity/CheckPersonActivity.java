package cn.breaksky.rounds.publics.activity;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.MyAdapter.GroupUser;
import cn.breaksky.rounds.publics.MyAdapter.MyExpandableListAdapter;
import cn.breaksky.rounds.publics.bean.RoundGroup;
import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.BadgeView;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendCheckLoginThread;

public class CheckPersonActivity extends Activity {

	private TextView chexk_back_button;
	private ExpandableListView lv_checkperson;
	private List<String> group;
	private List<List<GroupUser>> child;
	private MyExpandableListAdapter adapter;
	private List<RoundGroup> allgroup;
	
	private LongSparseArray<RoundPersonnelVO> personnels = new android.util.LongSparseArray<RoundPersonnelVO>();
	private LongSparseArray<BadgeView> personnelBadge = new android.util.LongSparseArray<BadgeView>();
	private RoundPersonnelVO nowPersonnel;
	public final static String SER_KEY = "NOWPERSONNEL";
	private EditText editText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check);
		
		allgroup=new ArrayList<RoundGroup>();
		
		editText = (EditText) findViewById(R.id.input_edit);
		editText.addTextChangedListener(new TextWatcher() {// EditText变化监听

			/**
			 * 正在输入
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!TextUtils.isEmpty(editText.getText().toString())) {// 判断输入内容是否为空，为空则跳过

					List<RoundPersonnelVO> pvos = new ArrayList<RoundPersonnelVO>();
					for (int i = 0; i < personnels.size(); i++) {
						RoundPersonnelVO m=personnels.valueAt(i);
						if(m.getName().indexOf(editText.getText().toString().trim())>-1)
						{
						pvos.add(m);
						}
						
					}
					
					setDataForListView(pvos);
					
				}
				
			}

			/**
			 * 输入之前
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			/**
			 * 输入之后
			 */
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		
		
		chexk_back_button = (TextView) findViewById(R.id.chexk_back_button);
		chexk_back_button.setOnClickListener(new CheckBackButtonOnClick());
		
		lv_checkperson = (ExpandableListView) findViewById(R.id.lv_checkperson);
//		lv_checkperson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            	nowPersonnel = new RoundPersonnelVO(); 
//                nowPersonnel = personnels.valueAt(i);
//    			
//    			Intent mIntent = new Intent(CheckPersonActivity.this, CallActivity.class);  
//    	    	Bundle mBundle = new Bundle();   
//    	    	mBundle.putSerializable(SER_KEY,nowPersonnel);   
//    	    	mIntent.putExtras(mBundle);	
//    	    	startActivity(mIntent);	
//                
//            }
//        });
		//设置item点击的监听器
		lv_checkperson.setOnChildClickListener(new OnChildClickListener() {

		           
					@Override
					public boolean onChildClick(ExpandableListView arg0, View arg1,
							int  groupPosition, int childPosition, long id) {
						// TODO Auto-generated method stub
						
						GroupUser cuser=(GroupUser) adapter.getChild(groupPosition, childPosition);
						if(cuser==null)
						{return false;
						
						}
//						
						 
						 nowPersonnel = new RoundPersonnelVO(); 
						
			                nowPersonnel.setRp_id(cuser.getId());
			                nowPersonnel.setName(cuser.getName());
		    			
		    			Intent mIntent = new Intent(CheckPersonActivity.this, CallActivity.class);  
		    	    	Bundle mBundle = new Bundle();   
		    	    	mBundle.putSerializable(SER_KEY,nowPersonnel);   
		    	    	mIntent.putExtras(mBundle);	
		    	    	startActivity(mIntent);	
						return false;
					}
		        });
		
		
		this.queryRoundGroup();
	};

	public void queryPersonner() {
		try {
			personnels.clear();
			personnelBadge.clear();
			Request request = new Request(Request.QUERY_PERSONNEL);
			RoundPersonnelVO user = MainService.getInstance().getUser();
			if(user==null){	
				return ;
			}
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
			UtilTools.alertMessage(CheckPersonActivity.this, "查询人员失败!");
		}
	}
	
	public void queryRoundGroup() {
		try {
			
			Request request = new Request(Request.QUERY_GROUP_lIST);
			request.sendCheckLogin(new Params(), new SendCheckLoginThread.CallBack() {

				@Override
				public void callBack(JSONObject json) {
					
					try {
						allgroup = UtilTools.jsonToBean(json.getJSONArray("query_data"), RoundGroup.class);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					queryPersonner();	
				}

				@Override
				public void exception(Exception e) {
					UtilTools.alertMessage(CheckPersonActivity.this, "查询人员失败!");
				}

			});

		} catch (Exception e) {
			UtilTools.alertMessage(CheckPersonActivity.this, "查询人员失败!");
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ShowPerson((RetBean) msg.obj);
		}
	};

	class RetBean {
		JSONObject json;
		String message;
	}
	
	
	private void ShowPerson(RetBean obj) {
		if (obj.json == null) {
			UtilTools.toast(CheckPersonActivity.this, obj.message);
		} else {
			try {
				if (obj.json.getBoolean("status")) {
					
					List<RoundPersonnelVO> users = UtilTools.jsonToBean(obj.json.getJSONArray("users"), RoundPersonnelVO.class);
					for (RoundPersonnelVO vo : users) {
						personnels.put(vo.getRp_id(), vo);
					}
					if (personnels == null || personnels.size() < 1) {
						UtilTools.toast(this, "联系人为空");
					return;
					}
					List<RoundPersonnelVO> pvos = new ArrayList<RoundPersonnelVO>();
					for (int i = 0; i < personnels.size(); i++) {
						pvos.add(personnels.valueAt(i));
					}
					setDataForListView(pvos);
				
					}
			}catch (Exception e) {
					UtilTools.toast(CheckPersonActivity.this, "查询人员失败!" + e.getMessage());
				}
				
		}
	}
	private class CheckBackButtonOnClick implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			CheckPersonActivity.this.finish();
		}
	}
	
	private void setDataForListView(List<RoundPersonnelVO> pvos)
	{
		if(pvos==null)
			return;
		group = new ArrayList<String>();
		child = new ArrayList<List<GroupUser>>();
		List<GroupUser> dispUsers=new ArrayList<GroupUser>();
		for (RoundPersonnelVO vo : pvos) {
			StringBuffer name = new StringBuffer(vo.getName());
			name.append("-");
			name.append(MainService.getInstance().roundsConfig.LEADER.equals(vo.getType()) ? "指挥员" : "巡逻员");
			name.append(MainService.getInstance().roundsConfig.ONLINE.equals(vo.getStatus()) ? "(在线)" : "(离线)");
			name.append("-");
			name.append(vo.getMobile());
			
			
			
			GroupUser u=new GroupUser();
			u.setDispname(name.toString());
			u.setId(vo.getRp_id());
			u.setName(vo.getName());
			u.setGroupid(vo.getGroupid());
			dispUsers.add(u);
		}
		
		
		
		for(int i=0;i<dispUsers.size();i++)
		{
			GroupUser m=dispUsers.get(i);
			String groupName=m.getGroupid();
			if(!group.contains(groupName))
			{
				group.add(groupName);
				child.add(new ArrayList<GroupUser>());
			}
			
			
			int index=group.indexOf(groupName);
			if(child.get(index)==null)
			{
				child.add(new ArrayList<GroupUser>());
			}
			child.get(index).add(m);
			
			
		}
		adapter = new MyExpandableListAdapter(this,group,child,allgroup);
		lv_checkperson.setAdapter(adapter);
		for(int i = 0; i < adapter.getGroupCount(); i++){
            
			//lv_checkperson.expandGroup(i);
			                        
			}
				
	}


}
