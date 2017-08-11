package cn.breaksky.rounds.publics.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.BitmapUtil;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.imageselector.ImageConfig;
import cn.breaksky.rounds.publics.util.imageselector.ImageLoader;
import cn.breaksky.rounds.publics.util.imageselector.ImageSelector;
import cn.breaksky.rounds.publics.util.regionselector.IRegionSelector;
import cn.breaksky.rounds.publics.util.regionselector.RegionBean;
import cn.breaksky.rounds.publics.util.regionselector.RegionSelector;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendRetJsonThread;

import com.bumptech.glide.Glide;

public class RegisterAcitvity extends Activity implements ImageLoader {
	private final static String TAG = "RegisterAcitvity";
	private static final long serialVersionUID = 7377138553915073492L;
	public static final int SELECT_REQUEST_CODE = 1000;
	private ArrayList<String> path = new ArrayList<String>();

	private ImageView registerImage;// reister_image
	private EditText registerMobile;// reister_mobile
	private EditText registerMobileVerify;// reister_mobile_verify
	private Button registerMobileVerifyButton;// register_mobile_verify_button
	private EditText registerPassword;// reister_password
	private EditText registerName;// reister_name
	private EditText registeridcard;// reister_idcard
	private EditText registerAge;// reister_age
	private RadioGroup registerSex;// reister_sex
	private Button registerRegionButton;// reister_region
	private EditText registerAdress;// reister_address

	private Button registerButton;// register_button
	private Button cancelButton;// cancel_button
	private String imagePath;
	private String regionCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ContextManager.addActivity(this);
		MainService.getInstance().roundsConfig.IS_REGISTER = true;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		registerImage = (ImageView) findViewById(R.id.reister_image);
		registerMobile = (EditText) findViewById(R.id.reister_mobile);
		registerMobileVerify = (EditText) findViewById(R.id.reister_mobile_verify);
		registerMobileVerifyButton = (Button) findViewById(R.id.register_mobile_verify_button);
		registerPassword = (EditText) findViewById(R.id.reister_password);
		registerName = (EditText) findViewById(R.id.reister_name);
		registeridcard = (EditText) findViewById(R.id.reister_idcard);
		registerAge = (EditText) findViewById(R.id.reister_age);
		registerSex = (RadioGroup) findViewById(R.id.reister_sex);
		registerButton = (Button) findViewById(R.id.register_button);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		registerRegionButton = (Button) findViewById(R.id.reister_region_button);
		registerAdress = (EditText) findViewById(R.id.reister_address);

		registerImage.setOnClickListener(new RegisterImageOnClick());
		registerMobileVerifyButton.setOnClickListener(new RegisterMobileVerifyButtonOnClick());
		registerButton.setOnClickListener(new RegisterButtonOnClick());
		cancelButton.setOnClickListener(new CancelButtonOnClick());
		registerRegionButton.setOnClickListener(new RegionOnClick());
	}

	class RegisterImageOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// GlideLoader 可用自己用的缓存库
			ImageConfig.Builder imageConfig = new ImageConfig.Builder(RegisterAcitvity.this);
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
			imageConfig.crop(400, 399, 400, 400);
			// 多选时的最大数量 （默认 9 张）
			// imageConfig.mutiSelectMaxSize(9);
			// 已选择的图片路径
			imageConfig.pathList(path);
			// 拍照后存放的图片路径（默认 /temp/picture）
			imageConfig.filePath("/PublicsRounds/Pictures");
			// 开启拍照功能 （默认开启）
			imageConfig.showCamera().requestCode(SELECT_REQUEST_CODE);

			ImageSelector.open(RegisterAcitvity.this, imageConfig.build()); // 开启图片选择器
		}
	}

	@Override
	public void displayImage(Context context, String path, ImageView imageView) {
		Glide.with(context).load(path).placeholder(R.drawable.imageselector_photo).centerCrop().into(imageView);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK || data == null) {
			return;
		}
		if (requestCode == SELECT_REQUEST_CODE) {
			// List<String> pathList =
			// data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
			if (path.size() != 1) {
				UtilTools.toast(this, "获取图片出错");
			} else {
				Bitmap bitmap = BitmapFactory.decodeFile(path.get(0));
				registerImage.setImageBitmap(BitmapUtil.getRoundedCornerBitmap(bitmap));
				imagePath = path.get(0);
			}
			path.clear();
		}
	}

	public void finish() {
		super.finish();
		MainService.getInstance().roundsConfig.IS_REGISTER = false;
	}

	private int verifyNum = 0;

	private class RegisterMobileVerifyButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (verifyNum != 0) {
				return;
			}
			if (registerMobile.getText() == null || registerMobile.getText().length() < 1) {
				UtilTools.toast(RegisterAcitvity.this, "请输入手机号");
				return;
			}
			try {
				Request request = new Request(Request.VERIFY_MOBILE);
				Params params = new Params();
				params.addTextParameter("af.reg_devicecode", UtilTools.getIMEI(RegisterAcitvity.this));
				params.addTextParameter("af.reg_mobile", registerMobile.getText().toString());
				request.sendRetJson(params, new SendRetJsonThread.CallBack() {

					@Override
					public void callBack(int tag, JSONObject json) {
						try {
							boolean is = json.getBoolean("status");
							if (is) {
								UtilTools.toast(RegisterAcitvity.this, "验证信息已发送");
								verifyNum = 60;
								RegisterAcitvity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										showVerifyNum();
									}
								});
							} else {
								UtilTools.toast(RegisterAcitvity.this, json.getString("message"));
							}
						} catch (JSONException e) {
							UtilTools.alertError(RegisterAcitvity.this, "获取失败!");
						}
					}

					@Override
					public void exception(int tag, Exception e) {
						UtilTools.alertError(RegisterAcitvity.this, "通讯错误!");
					}
				});
			} catch (Exception e) {
				UtilTools.alertError(RegisterAcitvity.this, "获取手机验证码错误!");
			}
		}
	}

	public void showVerifyNum() {
		if (verifyNum == 0) {
			registerMobileVerifyButton.setText(getResources().getText(R.string.register_mobile_verify_button));
			return;
		}
		registerMobileVerifyButton.setText(String.format("%d秒", verifyNum));
		verifyNum--;
		RoundHandler.runActivityMethod(this, 1000, "showVerifyNum");
	}

	private class RegisterButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			final String mobile = registerMobile.getText().toString();
			if (mobile.length() < 1) {
				UtilTools.toast(RegisterAcitvity.this, "手机号空");
				return;
			}
			final String authkey = registerMobileVerify.getText().toString();
			if (authkey.length() < 1) {
				UtilTools.toast(RegisterAcitvity.this, "验证码空");
				return;
			}
			final String name = registerName.getText().toString();
			if (name.length() < 1) {
				UtilTools.toast(RegisterAcitvity.this, "姓名空");
				return;
			}
			final String password = registerPassword.getText().toString();
			if (password.length() < 1) {
				UtilTools.toast(RegisterAcitvity.this, "密码空");
				return;
			}
			final String idcard = registeridcard.getText().toString();
			if (idcard.length() < 1) {
				UtilTools.toast(RegisterAcitvity.this, "身份证号空");
				return;
			}
			final String age = registerAge.getText().toString();
			if (age.length() < 1) {
				UtilTools.toast(RegisterAcitvity.this, "年龄空");
				return;
			}
			final int sexid = registerSex.getCheckedRadioButtonId();
			if (sexid == -1) {
				UtilTools.toast(RegisterAcitvity.this, "性别未选");
				return;
			}
			final String sex = sexid == R.id.reister_sex_man ? "1" : "2";
			if (regionCode == null || regionCode.length() < 1) {
				UtilTools.toast(RegisterAcitvity.this, "区域未选");
				return;
			}
			final String address = registerAdress.getText().toString();
			if (address.length() < 1) {
				UtilTools.toast(RegisterAcitvity.this, "地址空");
				return;
			}
			if (imagePath == null) {
				UtilTools.toast(RegisterAcitvity.this, "照片未选");
				return;
			}
			final File image = new File(imagePath);
			Builder alert = new AlertDialog.Builder(RegisterAcitvity.this);
			alert.setTitle("确认注册?");
			alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					try {
						Request request = new Request(Request.REGISTER);
						Params params = new Params();
						params.addTextParameter("af.reg_devicecode", UtilTools.getIMEI(RegisterAcitvity.this));
						params.addTextParameter("af.reg_mobile", mobile);
						params.addTextParameter("af.reg_authkey", authkey);
						params.addTextParameter("af.reg_name", name);
						params.addTextParameter("af.reg_password", password);
						params.addTextParameter("af.reg_idcard", idcard);
						params.addTextParameter("af.reg_age", age);
						params.addTextParameter("af.reg_sex", sex);
						params.addTextParameter("af.reg_regioncode", regionCode);
						params.addTextParameter("af.reg_address", address);
						params.addFileParameter("af.reg_image", image);

						request.sendRetJson(params, new SendRetJsonThread.CallBack() {

							@Override
							public void exception(int tag, Exception e) {
								UtilTools.alertError(RegisterAcitvity.this, "注册失败");
							}

							@Override
							public void callBack(int tag, JSONObject json) {
								try {
									boolean is = json.getBoolean("status");
									if (is) {
										UtilTools.setConfigValue(R.string.save_password_key, password);
										Builder alert = new AlertDialog.Builder(RegisterAcitvity.this);
										alert.setTitle("注册成功,请等待管理员审核!");
										alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog, int which) {
												RegisterAcitvity.this.finish();
											}
										});
										alert.show();
									} else {
										UtilTools.toast(RegisterAcitvity.this, json.getString("message"));
									}
								} catch (JSONException e) {
									UtilTools.alertError(RegisterAcitvity.this, "注册失败");
								}
							}
						});
					} catch (Exception e) {
						Log.e(TAG, e.getMessage(), e);
						UtilTools.alertError(RegisterAcitvity.this, "注册失败");
					}
				}
			});
			alert.setNegativeButton("否", null);
			alert.show();
		}
	}

	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Builder alert = new AlertDialog.Builder(RegisterAcitvity.this);
			alert.setTitle("确认退出?");
			alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					RegisterAcitvity.this.finish();
				}
			});
			alert.setNegativeButton("否", null);
			alert.show();
		}

	}

	class RegionOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			RegionSelector selector = new RegionSelector(RegisterAcitvity.this, new IRegionSelector() {

				@Override
				public void callBack(List<RegionBean> beans) {
					if (beans != null && beans.size() > 0) {
						RegionBean selectBean = beans.get(0);
						regionCode = selectBean.getCode();
						registerRegionButton.setText(selectBean.getName());
					}
				}
			});
			selector.open();
		}
	}
}
