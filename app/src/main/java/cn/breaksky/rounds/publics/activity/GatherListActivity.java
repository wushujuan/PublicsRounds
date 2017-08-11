package cn.breaksky.rounds.publics.activity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.DangerFactorBean;
import cn.breaksky.rounds.publics.bean.GatherTaskBean;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendCheckLoginThread;
import cn.breaksky.rounds.publics.util.request.callback.SendRetJsonThread;
/**
 * 采集任务页面
 * @author Administrator
 *
 */
public class GatherListActivity extends Activity {
	/** <b>任务列表</b> */
	private ListView lv_gather;
	/** <b>返回按钮</b> */
	private TextView gather_back_button;
	/** <b>任务列表</b> */
	private List<GatherTaskBean> taskBeans;
	/**<b>缓存因子路径</b>*/
	private static String TASKBEANS_PATH = MainService.getInstance().roundsConfig.OBJECT_SAVE_PATH + "GatherListActivity.taskbeans";
	/**<b>采集任务路径</b>*/
	//private static String GATHER_DATA_ROOT_PATH = MainService.getInstance().roundsConfig.OBJECT_SAVE_PATH + "GatherListActivity.gatherdata/";
	/**<b>采集任务路径</b>*/
	private static String GATHER_DATA_ROOT_PATH = MainService.getInstance().roundsConfig.OBJECT_SAVE_PATH + "GatherActivity.gatherdata/";
	/**<b>当前的采集任务</b>*/
	private GatherTaskBean nowTask;
	public final static String TASK_KEY = "NOWTask";
	/**<b>当前的采集任务id</b>*/
	public long rgt_id;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gatherlist);
		
		gather_back_button = (TextView) findViewById(R.id.gather_back_button);
		gather_back_button.setOnClickListener(new GatherBackButtonOnClick());
		
		lv_gather = (ListView) findViewById(R.id.lv_gather);
		lv_gather.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            	
            	rgt_id = taskBeans.get(i).getRgt_id();	//获取任务id
            	
            	if(MainService.getInstance().mflag){
            		ShowDialog(rgt_id);	//显示数据采集功能对话框
            	}
            	if(!MainService.getInstance().mflag){
            		doQueryGather(rgt_id);	//第一次时，缓存采集因子，并显示数据采集功能对话框
            	}
            	
            }
        });
		File file = new File(GATHER_DATA_ROOT_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		this.queryGatherList();	//查询任务
		
	}
	/**
	 * 显示数据采集功能对话框
	 * @param Rgt_id 任务id
	 */
	protected void ShowDialog(final long Rgt_id) {
		Dialog dialog = new AlertDialog.Builder(this).setIcon(
			 android.R.drawable.btn_star).setTitle("数据采集").setPositiveButton("采集",new OnClickListener() {
			 @Override
			public void onClick(DialogInterface dialog, int which) {
				  showFactor(Rgt_id);
				  }
			  }).setNeutralButton("上传", new OnClickListener() {
			@Override
		   public void onClick(DialogInterface dialog, int which) {
				
				showUpload(Rgt_id);
	       }
			}).create();
		dialog.show();
		
	}
	
	/**
	 * 上传
	 * @param Rgt_id  任务id
	 */
	private void showUpload(long Rgt_id) {
		GatherTaskBean taskBean = this.getSelectTask(Rgt_id);
		if (taskBean == null) {
			UtilTools.toast(GatherListActivity.this, "采集任务为空!");
			return;
		}
		final RadioGroup upGroup = new RadioGroup(GatherListActivity.this);
		File path = new File(GATHER_DATA_ROOT_PATH);
		if (!path.exists()) {
			UtilTools.toast(GatherListActivity.this, "采集数据目录为空!");
			return;
		}
		File[] childFile = path.listFiles();
		for (File file : childFile) {
			String[] name = file.getName().split("_");
			if (name.length == 2 && String.valueOf(taskBean.getRgt_id()).equals(name[0])) {
				RadioButton button = new RadioButton(GatherListActivity.this);
				Date date = new Date();
				try {
					date.setTime(Long.valueOf(name[1]));
				} catch (Exception e) {
					continue;
				}
				button.setTag(file.getPath());
				button.setTextColor(GatherListActivity.this.getResources().getColor(R.color.white));
				button.setText(UtilTools.formatDate(date, "MM-dd HH:mm:ss"));

				upGroup.addView(button);
			}
		}
		final AlertDialog alert = new AlertDialog.Builder(GatherListActivity.this, R.style.Dialog).create();
		alert.setTitle("数据上传");
		alert.setCanceledOnTouchOutside(false);
		alert.setView(upGroup);
		alert.setButton(DialogInterface.BUTTON_NEUTRAL, "提交", new DialogInterface.OnClickListener() {
			
			private boolean removed = true;
			private int count = 0;

			@Override
			public void onClick(DialogInterface dialog, int which) {
				preventDismissDialog(alert);
				upload();
			}

			private void upload() {
				if (!removed) {
					if (count < 10000) {
						upload();
					}
					count++;
					return;
				}
				count = 0;
				View view = upGroup.getChildAt(0);
				if (view == null) {
					UtilTools.alertMessage(GatherListActivity.this, "上传完成!");
					if(MainService.getInstance().maxGather == 1){//角标隐藏
						MainService.getInstance().maxGather = 0;
					}
					return;
				} else {
					final RadioButton button = (RadioButton) view;
					final String path = (String) button.getTag();
					uploadFactorValue(path, new UploadCallBack() {

						@Override
						public void success() {
							new File(path).delete();
							removed = false;
							GatherListActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									upGroup.removeViewAt(0);
									removed = true;
								}
							});
							upload();
						}

						@Override
						public void faile(final String message) {
							GatherListActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									button.setText(button.getText().toString() + "[" + (message == null ? "Null" : message) + "]");
								}

							});
						}
					});
				}
			}
		});
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "关闭", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dismissDialog(alert);
			}
		});
		alert.setButton(DialogInterface.BUTTON_NEGATIVE, "删除", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				preventDismissDialog(alert);
				View view = upGroup.findViewById(upGroup.getCheckedRadioButtonId());
				if (view != null) {
					final RadioButton button = (RadioButton) view;
					new File(button.getTag().toString()).delete();
					GatherListActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							upGroup.removeView(button);
						}

					});
				}
			}
		});
		alert.show();
	}
	/**
	 * 关闭对话框
	 */
	private void dismissDialog(AlertDialog alertDialog) {
		try {
			Field field = alertDialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(alertDialog, true);
		} catch (Exception e) {
		}
		alertDialog.dismiss();
	}
	/**
	 * 通过反射 阻止关闭对话框
	 */
	private void preventDismissDialog(AlertDialog alertDialog) {
		try {
			Field field = alertDialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			// 设置mShowing值，欺骗android系统
			field.set(alertDialog, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void uploadFactorValue(final String path, final UploadCallBack callBack) {
		Object obj = UtilTools.getObject(path);
		if (obj == null) {
			callBack.faile("缓存文件不存在");
			return;
		}
		GatherActivity.GatherData data = (GatherActivity.GatherData)obj;
		try {
			Request request = new Request(Request.SAVE_DANGERFACTOR);
			Params param = new Params();
			param.addTextParameter("af.fac_rgtid", data.rgtid);
			param.addTextParameter("af.fac_fdfid", data.fdfid);
			param.addTextParameter("af.fac_stage", data.stage);
			param.addTextParameter("af.fac_time", String.valueOf(data.time));
			request.sendCheckLogin(param, new SendCheckLoginThread.CallBack() {

				@Override
				public void callBack(JSONObject json) {
					try {
						if (json.getBoolean("status")) {
							callBack.success();
						} else {
							callBack.faile(json.getString("message"));
						}
					} catch (Exception e) {
						callBack.faile(e.getMessage());
					}
				}

				@Override
				public void exception(Exception e) {
					callBack.faile(e.getMessage());
				}

			});

		} catch (Exception e) {
			callBack.faile(e.getMessage());
		}
	}

	private interface UploadCallBack {
		public void success();

		public void faile(String message);
	}
	protected void showFactor(long rgt_id) {
		GatherTaskBean taskBean = this.getSelectTask(rgt_id);
		if (taskBean == null) {
			UtilTools.toast(GatherListActivity.this, "采集任务为空!");
			return;
		}
		Log.d("采集任务", "进入采集任务");
		nowTask = taskBean;
		if (taskBean.getBeans() == null) {
			UtilTools.toast(GatherListActivity.this, "未找到采集因子数据!请先缓存.");
			return;
		}
		Intent mIntent = new Intent(GatherListActivity.this, GatherActivity.class);  
    	Bundle mBundle = new Bundle();   
    	mBundle.putSerializable(TASK_KEY,nowTask);   
    	mIntent.putExtras(mBundle);	
    	startActivity(mIntent);	
	}
	private GatherTaskBean getSelectTask(long rgt_id) {
		
		List<GatherTaskBean> taskBeans = this.getCacheTask();
		if (taskBeans == null) {
			return null;
		}
		for (int i = 0; i < taskBeans.size(); i++) {
			if (taskBeans.get(i).getRgt_id().equals(rgt_id)) {
				return taskBeans.get(i);
			}
		}
		return null;
	}
	public void queryGatherList() {
		try {
			
			Request request = new Request(Request.QUERY_GATHER_TASK);
			request.sendCheckLogin(new Params(), new SendCheckLoginThread.CallBack() {

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
			UtilTools.alertMessage(GatherListActivity.this, "查询任务失败!");
		}
	}
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ShowGatherList((RetBean) msg.obj);
		}
	};

	class RetBean {
		JSONObject json;
		String message;
	}
	private Handler dohandler = new Handler() {
		public void handleMessage(Message msg) {
			ShowGatherUpdate((RetBean) msg.obj);
		}
	};
	private void ShowGatherUpdate(RetBean obj) {
		if (obj.json == null) {
			UtilTools.toast(GatherListActivity.this, obj.message);
		} else {
				try {
				if (obj.json.getBoolean("status")) {
					
					List<GatherTaskBean> taskBeanstmp = getCacheTask();
					if (taskBeanstmp == null) {
						UtilTools.toast(GatherListActivity.this, "任务为空!");
						return;
					}
					List<DangerFactorBean> beans = UtilTools.jsonToBean(obj.json.getJSONArray("factors"), DangerFactorBean.class);
					for (int i = 0; i < taskBeanstmp.size(); i++) {
						if (taskBeanstmp.get(i).getRgt_id().equals(rgt_id)) {
							taskBeanstmp.get(i).setBeans(beans);
						}
					}
					// 保存数据
					saveCacheTask(taskBeanstmp);
					//UtilTools.alertMessage(GatherListActivity.this, "查询采集因子成功!");
					
					Toast.makeText(GatherListActivity.this, "查询采集因子成功!", Toast.LENGTH_SHORT).show();
					
					updateListview(rgt_id);	//显示缓存因子
					
				} else {
					UtilTools.toast(GatherListActivity.this, obj.json.getString("message"));
				}
			} catch (JSONException e) {
				UtilTools.toast(GatherListActivity.this, "查询失败");
			}			
			
		}
		
	}
	
	private void ShowGatherList(RetBean obj) {
		if (obj.json == null) {
			UtilTools.toast(GatherListActivity.this, obj.message);
		} else {
			try {
				if (obj.json.getBoolean("status")) {
					taskBeans = UtilTools.jsonToBean(obj.json.getJSONArray("data"), GatherTaskBean.class);
					List<GatherTaskBean> oldTaskBeans = getCacheTask();
					if (oldTaskBeans != null) {
						for (GatherTaskBean bean : taskBeans) {
							for (GatherTaskBean oldBean : oldTaskBeans) {
								if (bean.getRgt_id().equals(oldBean.getRgt_id()) && oldBean.getBeans() != null) {
									bean.setBeans(oldBean.getBeans());
								}
							}
						}
					}
					saveCacheTask(taskBeans);//缓存数据
					
					List<GatherTaskBean> taskBeans = this.getCacheTask();
					
					if (taskBeans == null) {
						UtilTools.toast(GatherListActivity.this, "任务为空!");
						return;
					}
					List<HashMap<String, Object>> datalist = new ArrayList<HashMap<String, Object>>();
					for (GatherTaskBean bean : taskBeans) {
						
						Calendar time = Calendar.getInstance();
						time.setTimeInMillis(bean.getBegintime());
						StringBuffer str = new StringBuffer();
						str.append(bean.getName());
						if (bean.getBeans() != null) {
							str.append("(已缓存)");
						}
						str.append("\n");
						str.append(UtilTools.formatDate(time.getTime(), "yyyy-MM-dd"));
						str.append("至");
						time.setTimeInMillis(bean.getEndtime());
						str.append(UtilTools.formatDate(time.getTime(), "yyyy-MM-dd"));
						str.append("(");
						str.append(getInterval(bean.getIntervals()));
						str.append("/次)");
						
						HashMap<String, Object> item = new HashMap<String, Object>();
						item.put("txt_name", str);
						datalist.add(item);
					}
					
					SimpleAdapter adapter = new SimpleAdapter(this,datalist,R.layout.item_list,
							new String[]{"txt_name"},new int[]{R.id.txt_name});	
					lv_gather.setAdapter(adapter); 
				
					}
			}catch (Exception e) {
					UtilTools.toast(GatherListActivity.this, "查询任务失败!" + e.getMessage());
			}
				
		}
	}
	/**
	 * 缓存
	 * @param Rgt_id 任务id
	 */
	private void doQueryGather(final long Rgt_id) {
		try {
			Request request = new Request(Request.QUER_DANGERFACTOR);
			Params param = new Params();
			param.addTextParameter("af.fac_rgtid", String.valueOf(Rgt_id) );
			request.sendRetJson(param, new SendRetJsonThread.CallBack() {

				@Override
				public void callBack(int tag, JSONObject json) {
					Message msg = new Message();
					RetBean bean = new RetBean();
					bean.json = json;
					msg.obj = bean;
					dohandler.sendMessage(msg);
					
				}
				@Override
				public void exception(int tag, Exception e) {
					UtilTools.toast(GatherListActivity.this, "查询失败");
				}

			});
		} catch (Exception e) {
			UtilTools.toast(GatherListActivity.this, "查询失败");
		}
	}
	protected void updateListview(long rgt_id) {
		
		List<GatherTaskBean> taskBeansTmp = this.getCacheTask();
		List<HashMap<String, Object>> datalist = new ArrayList<HashMap<String, Object>>();
		for (GatherTaskBean bean : taskBeansTmp) {
			
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(bean.getBegintime());
			StringBuffer str = new StringBuffer();
			str.append(bean.getName());
			if (bean.getBeans() != null) {
				str.append("(已缓存)");
				MainService.getInstance().mflag = true;
			}
			str.append("\n");
			str.append(UtilTools.formatDate(time.getTime(), "yyyy-MM-dd"));
			str.append("至");
			time.setTimeInMillis(bean.getEndtime());
			str.append(UtilTools.formatDate(time.getTime(), "yyyy-MM-dd"));
			str.append("(");
			str.append(getInterval(bean.getIntervals()));
			str.append("/次)");
			
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("txt_name", str);
			datalist.add(item);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this,datalist,R.layout.item_list,
				new String[]{"txt_name"},new int[]{R.id.txt_name});	
		lv_gather.setAdapter(adapter); 
		
		ShowDialog(rgt_id);	//显示数据采集功能对话框
	}

	private List<GatherTaskBean> getCacheTask() {
		Object tasks = UtilTools.getObject(TASKBEANS_PATH);
		if (tasks != null) {
			return (List<GatherTaskBean>) tasks;
		} else {
			return null;
		}
	}
	private void saveCacheTask(List<GatherTaskBean> taskBeans) {
		// 缓存
		if (!UtilTools.saveObject(TASKBEANS_PATH, taskBeans)) {
			UtilTools.toast(GatherListActivity.this, "缓存失败");
		}
	}
	private String getInterval(int inval) {
		if (inval >= 60 && inval < 1440 && inval % 60 == 0) {
			return inval / 60 + "小时";
		} else if (inval >= 1440 && inval % 1440 == 0) {
			return inval / 1440 + "天";
		} else {
			return inval + "分钟";
		}
	}
	
	private class GatherBackButtonOnClick implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			GatherListActivity.this.finish();
		}
	}

	
}
