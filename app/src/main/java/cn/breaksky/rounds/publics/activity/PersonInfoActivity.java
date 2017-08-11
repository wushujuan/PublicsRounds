package cn.breaksky.rounds.publics.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.RoundGroup;
import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendCheckLoginThread;
import cn.breaksky.rounds.publics.util.request.callback.SendRetJsonThread;
import cn.breaksky.rounds.publics.util.request.callback.SendThread;
/**
 * 个人信息页面
 * @author Administrator
 *
 */
public class PersonInfoActivity extends Activity {
	private TextView cancelButton;// person_info_back_button
	private ImageView image;// person_info_image
	private TextView mobile;// person_info_mobile
	private EditText passwordOld;// person_info_password_old
	private EditText passwordNew;// person_info_password_new
	private TextView name;// person_info_name
	private TextView idcard;// person_info_idcard
	private EditText age;// person_info_age
	private TextView sex;// person_info_sex
	private EditText address;// person_info_address
	private Button modifyButton;// person_info_modify_button
	private TextView groupname;// person_info_sex
	
	private List<RoundGroup> allgroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ContextManager.addActivity(this);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_person_info);
		allgroup=new  ArrayList<RoundGroup>();

		cancelButton = (TextView) this.findViewById(R.id.person_info_back_button);
		image = (ImageView) this.findViewById(R.id.person_info_image);
		mobile = (TextView) this.findViewById(R.id.person_info_mobile);
		passwordOld = (EditText) this.findViewById(R.id.person_info_password_old);
		passwordNew = (EditText) this.findViewById(R.id.person_info_password_new);
		name = (TextView) this.findViewById(R.id.person_info_name);
		idcard = (TextView) this.findViewById(R.id.person_info_idcard);
		age = (EditText) this.findViewById(R.id.person_info_age);
		sex = (TextView) this.findViewById(R.id.person_info_sex);
		groupname = (TextView) this.findViewById(R.id.person_info_groupname);
		address = (EditText) this.findViewById(R.id.person_info_address);
		modifyButton = (Button) this.findViewById(R.id.person_info_modify_button);

		// OnTouchListener
		cancelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		// OnClickListener
		cancelButton.setOnClickListener(new CancelButtonOnClick());
		modifyButton.setOnClickListener(new ModifyOnClick());

		
		this.queryRoundGroup();
		
		queryImage();
	}

	private void queryImage() {
		try {
			Request request = new Request(Request.QUERY_PERSONNEL_IMAGE);
			request.send(new Params(), new SendThread.CallBack() {

				@Override
				public void exception(Exception e) {
					UtilTools.toast(PersonInfoActivity.this, "查询图片失败");
				}

				@Override
				public void callBack(final byte[] bytes) {
					PersonInfoActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
						}
					});
				}
			});
		} catch (Exception e) {
			UtilTools.toast(this, "查询图片失败");
		}
	}
	
	public void queryRoundGroup() {
		try {
			
			Request request = new Request(Request.QUERY_GROUP_lIST);
			request.sendCheckLogin(new Params(), new SendCheckLoginThread.CallBack() {

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
			UtilTools.alertMessage(PersonInfoActivity.this, "查询分组信息失败!");
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ShowGrpupname((RetBean) msg.obj);
		}
	};
	class RetBean {
		JSONObject json;
		String message;
	}
	
	private void ShowGrpupname(RetBean obj) {
		if (obj.json == null) {
			UtilTools.toast(PersonInfoActivity.this, obj.message);
		} else {
			try {
				if (obj.json.getBoolean("query_status")) {
					
					allgroup = UtilTools.jsonToBean(obj.json.getJSONArray("query_data"), RoundGroup.class);
					if (MainService.getInstance().getUser() != null) {
						RoundPersonnelVO vo = MainService.getInstance().getUser();
						mobile.setText(vo.getMobile());
						name.setText(vo.getName());
						idcard.setText(vo.getIdcard());
						age.setText(String.valueOf(vo.getAge()));
						sex.setText("1".equals(vo.getSex()) ? "男" : "女");
						address.setText(vo.getAddress());
						
						String groupid=  vo.getGroupid();
						
						for(int m=0;m<allgroup.size();m++)
						{
							String destid=allgroup.get(m).getGroupid().toString();
							if(destid.equalsIgnoreCase(vo.getGroupid()))
							{
								groupname.setText(allgroup.get(m).getGroupname());
								break;
							}
						}
						
					}
					
				}
					
					
			}catch (Exception e) {
					UtilTools.toast(PersonInfoActivity.this, "查询分组失败!" + e.getMessage());
				}
				
		}
	}
	
	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			PersonInfoActivity.this.finish();
		}
	}

	private class ModifyOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			final Params params = new Params();
			try {
				params.addTextParameter("af.reg_password", passwordOld.getText().toString());
				params.addTextParameter("af.reg_newpassword", passwordNew.getText().toString());
				params.addTextParameter("af.reg_address", address.getText().toString());
				params.addTextParameter("af.reg_age", age.getText().toString());
				Builder alert = new AlertDialog.Builder(v.getContext());
				alert.setTitle("确认提交");
				alert.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							Request request = new Request(Request.MODIFY_INFO);
							request.sendRetJson(params, new SendRetJsonThread.CallBack() {

								@Override
								public void exception(int tag, Exception e) {
									UtilTools.toast(PersonInfoActivity.this, "提交失败!");
								}

								@Override
								public void callBack(int tag, JSONObject json) {
									try {
										if (json.getBoolean("status")) {
											UtilTools.alertMessage(PersonInfoActivity.this, "修改成功", new UtilTools.AlertCallBack() {

												@Override
												public void callBack() {
													MainService.getInstance().roundsConfig.loginSucess = false;
													PersonInfoActivity.this.finish();
												}
											});
										} else {
											UtilTools.alertError(PersonInfoActivity.this, json.getString("message"));
										}
									} catch (JSONException e) {
										UtilTools.alertError(PersonInfoActivity.this, "操作失败:" + e.getMessage());
									}
								}
							});
						} catch (Exception e) {
							UtilTools.toast(PersonInfoActivity.this, "提交失败!");
						}
					}
				});
				alert.setNegativeButton("否", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
				});
				alert.show();

			} catch (Exception e) {
				UtilTools.toast(PersonInfoActivity.this, "提交失败!");
			}
		}
	}
}
