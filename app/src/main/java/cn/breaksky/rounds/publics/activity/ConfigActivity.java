package cn.breaksky.rounds.publics.activity;

import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;

public class ConfigActivity extends Activity {
	public void onPostCreate(Bundle savedInstanceState) {
		ContextManager.addActivity(this);
		super.onCreate(savedInstanceState);
		// 设置标题
		Window win = getWindow();
		CharSequence seq = this.getResources().getText(R.string.title_activity_config);
		win.setTitle(seq);

		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragement()).commit();
	}

	private class PrefsFragement extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.config_network);

			// thread checktime
			setDefaultValue(this, R.string.thread_check_time_key);
			// gps checktime
			setDefaultValue(this, R.string.gps_check_key);
			CheckBoxPreference cache = (CheckBoxPreference) super.findPreference(UtilTools.getResource(R.string.cache_checkbox_key));
			cache.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference arg0, Object arg1) {
					MainService.getInstance().cleanCache();
					return true;
				}
			});
			CheckBoxPreference autorun = (CheckBoxPreference) super.findPreference(UtilTools.getResource(R.string.autorun_checkbox_key));
			autorun.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference pref, Object obj) {
					UtilTools.setConfigValue(R.string.autorun_checkbox_key, (Boolean) obj);
					return true;
				}
			});
		}
	}

	private void setDefaultValue(PrefsFragement preFs, int i) {
		Preference pre = preFs.findPreference(UtilTools.getResource(i));
		String value = UtilTools.getConfigValue(i);
		if (value != null) {
			pre.setSummary(value);
			pre.setDefaultValue(value);
		}
		pre.setOnPreferenceChangeListener(new OnValueChange(i));
	}

	private class OnValueChange implements OnPreferenceChangeListener {
		private int i;

		public OnValueChange(int i) {
			this.i = i;
		}

		@Override
		public boolean onPreferenceChange(Preference pre, Object obj) {
			UtilTools.setConfigValue(i, obj.toString());
			pre.setSummary(obj.toString());
			return true;
		}
	}

	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			ConfigActivity.this.finish();
		}
	}
}
