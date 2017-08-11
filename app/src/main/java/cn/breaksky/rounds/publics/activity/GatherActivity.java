package cn.breaksky.rounds.publics.activity;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.LongSparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.DangerFactorBean;
import cn.breaksky.rounds.publics.bean.GatherTaskBean;
import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.CItem;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendCheckLoginThread;
import cn.breaksky.rounds.publics.util.request.callback.SendRetJsonThread;

public class GatherActivity extends Activity {
	private static String TASKBEANS_PATH = MainService.getInstance().roundsConfig.OBJECT_SAVE_PATH + "GatherActivity.taskbeans";
	private static String GATHER_DATA_ROOT_PATH = MainService.getInstance().roundsConfig.OBJECT_SAVE_PATH + "GatherActivity.gatherdata/";
	
	/**<b>采集任务路径</b>*/
	//private static String GATHER_DATA_ROOT_PATH = MainService.getInstance().roundsConfig.OBJECT_SAVE_PATH + "GatherListActivity.gatherdata/";
	private LinearLayout gatherScroll;// gather_scroll_view
	
	/**<b>确认按钮</b>*/
	private Button saveButton;// gather_send_button
	private TextView cancelButton;// event_back_button
	private TextView taskButton;// gather_task_button
	private TextView taskName;// gather_task_name
	private LongSparseArray<Spinner> selectSpinner = new LongSparseArray<Spinner>();
	private LongSparseArray<EditText> textEdit = new LongSparseArray<EditText>();
	/**<b>多选按钮1声明</b>*/
	private RadioGroup contextType1;
	private GatherTaskBean nowTask;
	private String  mfactortype;
	protected void onCreate(Bundle savedInstanceState) {
		ContextManager.addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gather);
		this.initView();
		File file = new File(GATHER_DATA_ROOT_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		//从GatherListActivity中获取选中任务信息
		nowTask = (GatherTaskBean)getIntent().getSerializableExtra(GatherListActivity.TASK_KEY);
		contextType1 = (RadioGroup) findViewById(R.id.gather_context_type);
		contextType1.setOnCheckedChangeListener(new ContextTypeChecked());
		mfactortype = "bt_";
		showNowTask(nowTask);
	}
	private class ContextTypeChecked implements RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int id) {
			if (id == R.id.gather_context_type_bt) {
				mfactortype = "bt_";
				//UtilTools.toast(GatherActivity.this, "崩塌点");
			}
			if (id == R.id.gather_context_type_hp) {
				mfactortype = "hp_";
				//UtilTools.toast(GatherActivity.this, "滑坡点");
			}
			if (id == R.id.gather_context_type_ns) {
				mfactortype = "nsl_";
				//UtilTools.toast(GatherActivity.this, "泥石流");
			}
			if (id == R.id.gather_context_type_tx) {
				mfactortype = "tx_";
				//UtilTools.toast(GatherActivity.this, "塌陷点");
			}
			showNowTask(nowTask);
		}
	};
	private void showNowTask(GatherTaskBean nowTask) {
		cleanGatherScroll();
		taskName.setText(nowTask.getName());
		for (DangerFactorBean bean : nowTask.getBeans()) {
			if(bean.getCode().indexOf(mfactortype)<0){
				continue;
			}
			LinearLayout layout = new LinearLayout(GatherActivity.this);
			// 因子
			TextView text = new TextView(GatherActivity.this);
			text.setText(bean.getFactor() + "：");
			text.setTextColor(getResources().getColor(R.color.black));
			text.setTextSize(20);
			layout.addView(text);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			if (DangerFactorBean.SELECT.equals(bean.getType())) {
				// 值
				Spinner select = new Spinner(GatherActivity.this);
				select.setLayoutParams(params);
				String[] values = bean.getValue_stage().split(",");
				List<CItem> lst = new ArrayList<CItem>();
				for (String v : values) {
					lst.add(new CItem(v, v));
				}
				ArrayAdapter<CItem> adapter = new ArrayAdapter<CItem>(GatherActivity.this, android.R.layout.simple_spinner_item, lst);
				select.setAdapter(adapter);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				layout.addView(select);

				selectSpinner.put(bean.getRdf_id(), select);
			} else if (DangerFactorBean.TEXT.equals(bean.getType())) {
				EditText textView = new EditText(GatherActivity.this);
				textView.setLayoutParams(params);

				layout.addView(textView);

				textEdit.put(bean.getRdf_id(), textView);
			}
			gatherScroll.addView(layout);
		}
		
	}

	private void initView() {
		gatherScroll = (LinearLayout) findViewById(R.id.gather_scroll_view);
		saveButton = (Button) findViewById(R.id.gather_send_button);
		cancelButton = (TextView) findViewById(R.id.gather_back_button);
		/*taskButton = (TextView) findViewById(R.id.gather_task_button);*/
		taskName = (TextView) findViewById(R.id.gather_task_name);

		// OnTouchListener
		cancelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		//taskButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));

		// OnClickListener
		saveButton.setOnClickListener(new SaveButtonOnClick());
		cancelButton.setOnClickListener(new CancelButtonOnClick());
		//taskButton.setOnClickListener(new TaskButtonOnClick());
	}

	private class SaveButtonOnClick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Builder alert = new AlertDialog.Builder(GatherActivity.this);
			alert.setTitle("确认保存?");
			alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					saveDangerFactor();
				}
			});
			alert.setNegativeButton("否", null);
			alert.show();
		}

	}

	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			GatherActivity.this.finish();
		}
	}

	private class TaskButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			try {
				Request request = new Request(Request.QUERY_GATHER_TASK);
				request.sendRetJson(new Params(), new SendRetJsonThread.CallBack() {

					@Override
					public void exception(int tag, Exception e) {
						UtilTools.toast(GatherActivity.this, "查询失败");
						showTask();
					}

					@Override
					public void callBack(int tag, JSONObject json) {
						try {
							if (json.getBoolean("status")) {
								List<GatherTaskBean> taskBeans = UtilTools.jsonToBean(json.getJSONArray("data"), GatherTaskBean.class);
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
								saveCacheTask(taskBeans);
								showTask();
							} else {
								UtilTools.toast(GatherActivity.this, json.getString("message"));
							}
						} catch (JSONException e) {
							UtilTools.toast(GatherActivity.this, "查询失败");
						}
					}
				});
			} catch (Exception e) {
				UtilTools.toast(GatherActivity.this, "查询失败");
				showTask();
			}
		}
	}

	private void saveCacheTask(List<GatherTaskBean> taskBeans) {
		// 缓存
		if (!UtilTools.saveObject(TASKBEANS_PATH, taskBeans)) {
			UtilTools.toast(GatherActivity.this, "缓存失败");
		}
	}

	@SuppressWarnings("unchecked")
	private List<GatherTaskBean> getCacheTask() {
		Object tasks = UtilTools.getObject(TASKBEANS_PATH);
		if (tasks != null) {
			return (List<GatherTaskBean>) tasks;
		} else {
			return null;
		}
	}

	private void showTask() {
		List<GatherTaskBean> taskBeans = this.getCacheTask();
		if (taskBeans == null) {
			UtilTools.toast(GatherActivity.this, "任务为空!");
			return;
		}
		final RadioGroup group = new RadioGroup(GatherActivity.this);
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
			RadioButton button = new RadioButton(GatherActivity.this);
			if (nowTask != null && nowTask.getRgt_id() == bean.getRgt_id()) {
				button.setChecked(true);
			}
			button.setTag(bean);
			button.setText(str.toString());
			button.setTextColor(GatherActivity.this.getResources().getColor(R.color.white));
			group.addView(button);
		}
		
		AlertDialog alert = new AlertDialog.Builder(GatherActivity.this, R.style.Dialog).create();
		alert.setTitle("采集任务");
		alert.setView(group);
		alert.setCancelable(true);
		alert.setCanceledOnTouchOutside(true);
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "采集", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				GatherActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showFactor(group);
					}
				});
			}
		});
		alert.setButton(DialogInterface.BUTTON_NEUTRAL, "上传", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				GatherActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showUpload(group);
					}
				});
			}
		});
		alert.setButton(DialogInterface.BUTTON_NEGATIVE, "缓存", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				doQueryGather(group);
			}
		});
		alert.show();
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

	private void doQueryGather(RadioGroup group) {
		RadioButton button = (RadioButton) group.findViewById(group.getCheckedRadioButtonId());
		final GatherTaskBean bean = (GatherTaskBean) button.getTag();
		
		try {
			Request request = new Request(Request.QUER_DANGERFACTOR);
			Params param = new Params();
			param.addTextParameter("af.fac_rgtid", bean.getRgt_id().toString());
			request.sendRetJson(param, new SendRetJsonThread.CallBack() {

				@Override
				public void callBack(int tag, JSONObject json) {
					try {
						if (json.getBoolean("status")) {
							List<GatherTaskBean> taskBeans = getCacheTask();
							if (taskBeans == null) {
								UtilTools.toast(GatherActivity.this, "任务为空!");
								return;
							}
							List<DangerFactorBean> beans = UtilTools.jsonToBean(json.getJSONArray("factors"), DangerFactorBean.class);
							for (int i = 0; i < taskBeans.size(); i++) {
								if (taskBeans.get(i).getRgt_id().equals(bean.getRgt_id())) {
									taskBeans.get(i).setBeans(beans);
								}
							}
							// 保存数据
							saveCacheTask(taskBeans);
							UtilTools.alertMessage(GatherActivity.this, "查询采集因子成功!");
						} else {
							UtilTools.toast(GatherActivity.this, json.getString("message"));
						}
					} catch (JSONException e) {
						UtilTools.toast(GatherActivity.this, "查询失败");
					}
				}

				@Override
				public void exception(int tag, Exception e) {
					UtilTools.toast(GatherActivity.this, "查询失败");
				}

			});
		} catch (Exception e) {
			UtilTools.toast(GatherActivity.this, "查询失败");
		}
	}

	private GatherTaskBean getSelectTask(RadioGroup group) {
		View view = group.findViewById(group.getCheckedRadioButtonId());
		if (view == null) {
			return null;
		}
		RadioButton button = (RadioButton) view;
		GatherTaskBean selectTaskBean = (GatherTaskBean) button.getTag();
		List<GatherTaskBean> taskBeans = this.getCacheTask();
		if (taskBeans == null) {
			return null;
		}
		for (int i = 0; i < taskBeans.size(); i++) {
			if (taskBeans.get(i).getRgt_id().equals(selectTaskBean.getRgt_id())) {
				return taskBeans.get(i);
			}
		}
		return null;
	}

	private void showFactor(RadioGroup group) {
		cleanGatherScroll();
		GatherTaskBean taskBean = this.getSelectTask(group);
		if (taskBean == null) {
			UtilTools.toast(GatherActivity.this, "采集任务为空!");
			return;
		}
		nowTask = taskBean;
		if (taskBean.getBeans() == null) {
			UtilTools.toast(GatherActivity.this, "未找到采集因子数据!请先缓存.");
			return;
		}
		
		taskName.setText(nowTask.getName());
		for (DangerFactorBean bean : taskBean.getBeans()) {
			LinearLayout layout = new LinearLayout(GatherActivity.this);
			// 因子
			TextView text = new TextView(GatherActivity.this);
			text.setText(bean.getFactor() + "：");
			text.setTextColor(getResources().getColor(R.color.black));
			text.setTextSize(20);
			layout.addView(text);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			if (DangerFactorBean.SELECT.equals(bean.getType())) {
				// 值
				Spinner select = new Spinner(GatherActivity.this);
				select.setLayoutParams(params);
				String[] values = bean.getValue_stage().split(",");
				List<CItem> lst = new ArrayList<CItem>();
				for (String v : values) {
					lst.add(new CItem(v, v));
				}
				ArrayAdapter<CItem> adapter = new ArrayAdapter<CItem>(GatherActivity.this, android.R.layout.simple_spinner_item, lst);
				select.setAdapter(adapter);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				layout.addView(select);

				selectSpinner.put(bean.getRdf_id(), select);
			} else if (DangerFactorBean.TEXT.equals(bean.getType())) {
				EditText textView = new EditText(GatherActivity.this);
				textView.setLayoutParams(params);

				layout.addView(textView);

				textEdit.put(bean.getRdf_id(), textView);
			}
			gatherScroll.addView(layout);
		}
	}

	private void showUpload(RadioGroup group) {
		GatherTaskBean taskBean = this.getSelectTask(group);
		if (taskBean == null) {
			UtilTools.toast(GatherActivity.this, "采集任务为空!");
			return;
		}
		final RadioGroup upGroup = new RadioGroup(GatherActivity.this);
		File path = new File(GATHER_DATA_ROOT_PATH);
		if (!path.exists()) {
			UtilTools.toast(GatherActivity.this, "采集数据目录为空!");
			return;
		}
		File[] childFile = path.listFiles();
		for (File file : childFile) {
			String[] name = file.getName().split("_");
			if (name.length == 2 && String.valueOf(taskBean.getRgt_id()).equals(name[0])) {
				RadioButton button = new RadioButton(GatherActivity.this);
				Date date = new Date();
				try {
					date.setTime(Long.valueOf(name[1]));
				} catch (Exception e) {
					continue;
				}
				button.setTag(file.getPath());
				button.setTextColor(GatherActivity.this.getResources().getColor(R.color.white));
				button.setText(UtilTools.formatDate(date, "MM-dd HH:mm:ss"));

				upGroup.addView(button);
			}
		}
		final AlertDialog alert = new AlertDialog.Builder(GatherActivity.this, R.style.Dialog).create();
		alert.setTitle("数据上传");
		alert.setCanceledOnTouchOutside(false);
		alert.setView(upGroup);
		alert.setButton(DialogInterface.BUTTON_NEUTRAL, "上传全部", new DialogInterface.OnClickListener() {
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
					UtilTools.alertMessage(GatherActivity.this, "上传完成!");
					return;
				} else {
					final RadioButton button = (RadioButton) view;
					final String path = (String) button.getTag();
					uploadFactorValue(path, new UploadCallBack() {

						@Override
						public void success() {
							new File(path).delete();
							removed = false;
							GatherActivity.this.runOnUiThread(new Runnable() {

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
							GatherActivity.this.runOnUiThread(new Runnable() {

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
					GatherActivity.this.runOnUiThread(new Runnable() {

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
		GatherData data = (GatherData) obj;
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

	private void saveDangerFactor() {
		if (nowTask == null) {
			UtilTools.alertError(this, "未选择采集任务!");
			return;
		}
		StringBuffer ids = new StringBuffer();
		StringBuffer values = new StringBuffer();
		int size = selectSpinner.size();
		for (int i = 0; i < size; i++) {
			Long id = selectSpinner.keyAt(i);
			Spinner select = selectSpinner.get(id);
			CItem selectItem = ((CItem) select.getSelectedItem());
			ids.append(id);
			ids.append(",");
			values.append(selectItem.GetID());
			values.append(",");
		}
		size = textEdit.size();
		for (int i = 0; i < size; i++) {
			ids.append(textEdit.keyAt(i));
			ids.append(",");
			values.append(textEdit.valueAt(i).getText().toString().replaceAll(",", ""));
			values.append(",");
		}
		if (ids.length() < 1) {
			UtilTools.alertError(this, "采集指标为空!");
			return;
		}
		GatherData data = new GatherData();
		data.fdfid = ids.substring(0, ids.length() - 1);
		data.rgtid = nowTask.getRgt_id().toString();
		data.stage = values.substring(0, values.length() - 1);
		data.time = (new Date()).getTime();
		StringBuffer path = new StringBuffer(GATHER_DATA_ROOT_PATH);
		path.append(nowTask.getRgt_id());
		path.append("_");
		path.append(data.time);
		boolean is = UtilTools.saveObject(path.toString(), data);
		if (is) {
			//UtilTools.alertMessage(GatherActivity.this, "保存成功!");
			Builder alert = new AlertDialog.Builder(GatherActivity.this);
			alert.setTitle("保存成功!");
			alert.setPositiveButton("返回任务列表", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					GatherActivity.this.finish();
				}
			});
			alert.show();
			cleanGatherScroll();
			
		} else {
			UtilTools.alertMessage(GatherActivity.this, "保存失败!");
		}
		
	}

	private void cleanGatherScroll() {
		taskName.setText(getResources().getString(R.string.gather_task_name));
		selectSpinner.clear();
		textEdit.clear();
		gatherScroll.removeAllViews();
	}

	public static class GatherData implements Serializable {
		private static final long serialVersionUID = 6483336219965614580L;
		public String rgtid;
		public String fdfid;
		public String stage;
		public long time;
	}

	private interface UploadCallBack {
		public void success();

		public void faile(String message);
	}
}
