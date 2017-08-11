package cn.breaksky.rounds.publics.util.regionselector;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendRetJsonThread;

public class RegionSelector {
	private static RegionBean REGION_BEAN = new RegionBean("", "点击展开选择区域");
	private Activity context;
	private List<RegionBean> checkBean = new ArrayList<RegionBean>();
	private IRegionSelector callBack;
	private int width = 500;
	private int height = 800;
	private AlertDialog alert;

	public RegionSelector(Activity context, IRegionSelector callBack) {
		this.context = context;
		this.callBack = callBack;
	}

	public void open() {
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) as counts from region", null);
		cursor.moveToNext();
		int counts = cursor.getInt(0);
		cursor.close();
		if (counts == 0) {
			final ProgressDialog dialog = UtilTools.createProgressDialog(context, "初始化区域数据,请稍后...", false);
			try {
				Request request = new Request(Request.QUERY_REGION);
				request.sendRetJson(new Params(), new SendRetJsonThread.CallBack() {

					@Override
					public void exception(int tag, Exception e) {
						dialog.dismiss();
						UtilTools.alertError(context, "服务器未返回区域数据");
					}

					@Override
					public void callBack(int tag, JSONObject json) {
						try {
							List<cn.breaksky.rounds.publics.bean.Region> regions = UtilTools.jsonToBean(json.getJSONArray("beans"), cn.breaksky.rounds.publics.bean.Region.class);
							SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
							for (cn.breaksky.rounds.publics.bean.Region region : regions) {
								db.execSQL("insert into region(code,name) values(?,?)", new String[] { region.getCode(), region.getName() });
							}
							dialog.dismiss();
							context.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									showDialog();
								}
							});
						} catch (JSONException e) {
							dialog.dismiss();
							UtilTools.alertError(context, "更新区域数据失败");
						}
					}
				});
			} catch (Exception e) {
				dialog.dismiss();
				UtilTools.alertError(context, "初始化区域数据失败");
			}
		} else {
			showDialog();
		}
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context, AlertDialog.THEME_HOLO_LIGHT);

		FrameLayout layout = new FrameLayout(this.context);
		FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		layoutParam.gravity = Gravity.CENTER | Gravity.CENTER;
		layout.setLayoutParams(layoutParam);
		builder.setView(layout);

		ScrollView view = new ScrollView(context);
		view.addView(createChild(REGION_BEAN));
		FrameLayout.LayoutParams viewParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
		viewParams.bottomMargin = 100;
		viewParams.leftMargin = 5;
		viewParams.rightMargin = 5;
		view.setLayoutParams(viewParams);
		layout.addView(view);

		Button button = new Button(this.context);
		button.setText("确定");
		FrameLayout.LayoutParams buttonParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		buttonParam.gravity = Gravity.BOTTOM;
		button.setLayoutParams(buttonParam);
		button.setOnClickListener(new SelectOnClick());
		layout.addView(button);

		this.alert = builder.create();
		this.alert.show();
		WindowManager.LayoutParams params = this.alert.getWindow().getAttributes();
		params.alpha = 0.9f;
		this.alert.getWindow().setAttributes(params);
		this.alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		// alert.getWindow().setBackgroundDrawableResource(R.drawable.login_info_bg);
	}

	private LinearLayout createChild(RegionBean bean) {
		bean.setChilds(queryChildRegion(bean));

		LinearLayout layout = new LinearLayout(this.context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = 20;
		layout.setLayoutParams(params);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setOnClickListener(new ChildLayoutOnClick(bean));

		LinearLayout nameLayout = new LinearLayout(this.context);
		LinearLayout.LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80);
		nameLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		nameLayout.setLayoutParams(nameLayoutParams);
		nameLayout.setBackgroundResource(R.xml.regionborder);
		nameLayout.setOrientation(LinearLayout.HORIZONTAL);
		nameLayout.setGravity(Gravity.CENTER_VERTICAL);

		int textMargin = 20;
		if (bean.getChilds().size() > 0) {
			ImageView imageView = new ImageView(this.context);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
			imageView.setImageResource(R.drawable.region_close);
			nameLayout.addView(imageView);
			textMargin = 0;
		}

		View textView = null;
		if (bean.getCode() == null || bean.getCode().length() < 1 || bean.getChilds() == null || bean.getChilds().size() > 0) {
			textView = new TextView(this.context);
			((TextView) textView).setText(bean.getName());
			((TextView) textView).setTextSize(16);
			textMargin = 5;
		} else {
			textView = new CheckBox(this.context);
			((CheckBox) textView).setText(bean.getName());
			textView.setOnClickListener(new TextViewOnClick(bean));
		}
		LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		textViewParams.leftMargin = textMargin;
		textViewParams.gravity = Gravity.CENTER_VERTICAL;
		textView.setLayoutParams(textViewParams);
		nameLayout.addView(textView);

		layout.addView(nameLayout);

		LinearLayout childLayout = new LinearLayout(this.context);
		childLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		childLayout.setOrientation(LinearLayout.VERTICAL);
		childLayout.setVisibility(View.GONE);

		layout.addView(childLayout);
		return layout;
	}

	private List<RegionBean> queryChildRegion(RegionBean parent) {
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
		Cursor cursor = db.rawQuery("select code,name from region where code like ?", new String[] { parent.getCode() + "__" });
		List<RegionBean> beans = new ArrayList<RegionBean>();
		while (cursor.moveToNext()) {
			beans.add(new RegionBean(cursor.getString(0), cursor.getString(1)));
		}
		cursor.close();
		return beans;
	}

	private class ChildLayoutOnClick implements OnClickListener {
		private RegionBean bean;

		private ChildLayoutOnClick(RegionBean bean) {
			this.bean = bean;
		}

		@Override
		public void onClick(View v) {
			if (bean.getChilds().size() == 0) {
				return;
			}
			LinearLayout view = (LinearLayout) v;
			int childCount = view.getChildCount();
			if (childCount != 2) {
				return;
			}
			LinearLayout nameLayout = (LinearLayout) view.getChildAt(0);
			ImageView imageView = null;
			if (bean.getChilds().size() > 0) {
				imageView = (ImageView) nameLayout.getChildAt(0);
			}
			LinearLayout childLayout = (LinearLayout) view.getChildAt(1);
			if (childLayout.getVisibility() == View.GONE) {
				// 展开
				childLayout.setVisibility(View.VISIBLE);
				if (imageView != null) {
					imageView.setImageResource(R.drawable.region_open);
				}
				// 查询子节点,并添加
				if (childLayout.getChildCount() == 0) {
					for (RegionBean child : bean.getChilds()) {
						childLayout.addView(createChild(child));
					}
				}
			} else {
				// 收拢
				childLayout.setVisibility(View.GONE);
				if (imageView != null) {
					imageView.setImageResource(R.drawable.region_close);
				}
			}
		}
	}

	private class TextViewOnClick implements OnClickListener {
		private RegionBean bean;

		private TextViewOnClick(RegionBean bean) {
			this.bean = bean;
		}

		@Override
		public void onClick(View v) {
			CheckBox checkBox = (CheckBox) v;
			if (checkBox.isChecked()) {
				checkBean.add(bean);
			} else {
				checkBean.remove(bean);
			}
		}
	}

	private class SelectOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			alert.dismiss();
			callBack.callBack(checkBean);
		}
	}
}
