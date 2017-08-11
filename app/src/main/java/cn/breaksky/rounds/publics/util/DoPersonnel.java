package cn.breaksky.rounds.publics.util;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.RoundsConfig;
import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.services.MainService;

public class DoPersonnel {
	private PersonnelOnClick onclick;
	private Activity activity;
	private List<RoundPersonnelVO> personnels;
	private Long defaultid;

	private DoPersonnel(PersonnelOnClick onclick, Activity activity, List<RoundPersonnelVO> personnels, Long defaultid) {
		this.onclick = onclick;
		this.activity = activity;
		this.personnels = personnels;
		this.defaultid = defaultid;
	}

	// 人员选择
	public static synchronized void doPersonnel(Activity activity, List<RoundPersonnelVO> personnels, Long defaultid, PersonnelOnClick onclick) {
		if (personnels == null || personnels.size() < 1) {
			UtilTools.toast(activity, "联系人为空");
			return;
		}
		RoundHandler.runMethod(new DoPersonnel(onclick, activity, personnels, defaultid), "run");
	}

	public void run() {
		RadioGroup group = new RadioGroup(activity);
		int checkid = -1;
		for (RoundPersonnelVO vo : this.personnels) {
			StringBuffer name = new StringBuffer(vo.getName());
			name.append("-");
			name.append(MainService.getInstance().roundsConfig.LEADER.equals(vo.getType()) ? "指挥员" : "巡逻员");
			name.append(MainService.getInstance().roundsConfig.ONLINE.equals(vo.getStatus()) ? "(在线)" : "(离线)");
			name.append("-");
			name.append(vo.getMobile());
			RadioButton button = new RadioButton(activity);
			button.setText(name);
			button.setTag(vo);
			button.setId(UtilTools.generateViewId());
			if (vo.getRp_id().equals(this.defaultid)) {
				checkid = button.getId();
			}
			group.addView(button);
			button.setTextColor(activity.getResources().getColor(R.color.white));
		}
		if (checkid != -1) {
			group.check(checkid);
		}

		AlertDialog alert = new AlertDialog.Builder(activity, R.style.Dialog).create();
		alert.setTitle("选择接收人员");
		ScrollView view = new ScrollView(activity);
		view.addView(group);
		alert.setView(view);
		// Window window = alert.getWindow();
		// WindowManager.LayoutParams lp = window.getAttributes();
		// lp.alpha = 0.6f;
		// window.setAttributes(lp);
		alert.show();

		group.setOnCheckedChangeListener(new PersonnelListListener(this.onclick, this.activity, alert));
	}

	class PersonnelListListener implements RadioGroup.OnCheckedChangeListener {
		private PersonnelOnClick onclick;
		private Activity activity;
		private AlertDialog alert;

		public PersonnelListListener(PersonnelOnClick onclick, Activity activity, AlertDialog alert) {
			this.onclick = onclick;
			this.activity = activity;
			this.alert = alert;
		}

		@Override
		public void onCheckedChanged(RadioGroup group, int index) {
			alert.dismiss();
			RadioButton button = (RadioButton) group.findViewById(index);
			final RoundPersonnelVO vo = (RoundPersonnelVO) button.getTag();
			this.activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					onclick.onclick(vo);
				}
			});

		}
	}

	public interface PersonnelOnClick {
		public void onclick(RoundPersonnelVO vo);
	}
}
