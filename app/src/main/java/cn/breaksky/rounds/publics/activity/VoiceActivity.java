package cn.breaksky.rounds.publics.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.VoiceUtil;

public class VoiceActivity extends Activity {
	private ImageButton m_btnRecording;
	private ProgressBar progressBar;
	private TextView textView;
	// 语音操作对象
	private MediaPlayer mPlayer = null;
	private TextView cancelButton;// voice_back_button

	protected void onCreate(Bundle bundle) {
		ContextManager.addActivity(this);
		super.onCreate(bundle);
		super.setContentView(R.layout.activity_voice);

		m_btnRecording = (ImageButton) findViewById(R.id.voice_recording_button);
		textView = (TextView) findViewById(R.id.voice_text);
		progressBar = (ProgressBar) findViewById(R.id.volume_bar);
		cancelButton = (TextView) findViewById(R.id.voice_back_button);

		progressBar.setMax(100);
		m_btnRecording.setOnTouchListener(new VoiceTouch(this, MainService.getInstance().roundsConfig.VOICE_PATH));
		cancelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		cancelButton.setOnClickListener(new CancelButtonOnClick());
	}

	private class VoiceTouch extends VoiceUtil {

		public VoiceTouch(Context context, String rootPath) {
			super(context, rootPath, true);
		}

		@Override
		public void volume(int db) {
			progressBar.setProgress(db);
		}

		@Override
		public void begin() {
			textView.setText("开始录音....");
		}

		@Override
		public void end(String voicePath) {
			Intent intent = VoiceActivity.this.getIntent();
			intent.putExtra("path", voicePath);
			VoiceActivity.this.setResult(RESULT_OK, intent);
			VoiceActivity.this.finish();
		}

	}

	class PlayListener implements OnClickListener {
		private String voicePath;

		public PlayListener(String voicePath) {
			this.voicePath = voicePath;
		}

		@Override
		public void onClick(View view) {
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(this.voicePath);
				mPlayer.prepare();
				mPlayer.start();
			} catch (IOException e) {
				UtilTools.toast(VoiceActivity.this, e.getMessage());
			}
		}
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			VoiceActivity.this.finish();
		}
	}
}
