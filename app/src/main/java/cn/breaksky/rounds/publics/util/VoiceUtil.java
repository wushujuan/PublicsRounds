package cn.breaksky.rounds.publics.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.handler.RoundHandler;

public abstract class VoiceUtil implements OnTouchListener {
	private String voicePath;
	private final static float BASE = 1.5f;
	private boolean endShow = false;
	private boolean recordering = false;
	private MediaPlayer mPlayer = null;
	private MediaRecorder mRecorder = null;
	protected Context context;
	private AlertDialog dialog;
	private boolean doConfig;

	public VoiceUtil(Context context, String rootPath, boolean doConfig) {
		this.context = context;
		this.doConfig = doConfig;
		File forder = new File(rootPath + "/voiceutil");
		if (!forder.exists()) {
			forder.mkdirs();
		}
		this.voicePath = forder.getPath() + "/" + String.valueOf((new Date()).getTime()) + ".3gp";
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			beginRecording();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			endRecording(v);
		}
		return false;
	}

	private void beginRecording() {
		if (mRecorder != null) {
			return;
		}
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mRecorder.setOutputFile(voicePath);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			UtilTools.toast(this.context, e.getMessage());
		}
		mRecorder.start();
		begin();
		endShow = false;
		showVolume();
		recordering = true;
	}

	private void endRecording(View view) {
		if (mRecorder == null || !recordering) {
			return;
		}
		recordering = false;
		try {
			mRecorder.stop();
		} catch (Exception e) {
		}
		mRecorder.release();
		mRecorder = null;
		endShow = true;

		volume(0);

		if (this.doConfig) {
			Builder alert = new AlertDialog.Builder(view.getContext());
			alert.setTitle("声音确认");
			alert.setPositiveButton("是", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					end(voicePath);
					dialog.dismiss();
				}
			});
			alert.setNegativeButton("否", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					dialog.dismiss();
				}
			});
			ImageButton m_btnPlay = new ImageButton(view.getContext());
			m_btnPlay.setImageDrawable(view.getContext().getDrawable(R.drawable.play));
			m_btnPlay.setOnClickListener(new PlayListener());
			m_btnPlay.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
			alert.setView(m_btnPlay);
			dialog = alert.show();
		} else {
			end(voicePath);
		}
	}

	public void showVolume() {
		double ratio = (double) mRecorder.getMaxAmplitude() / BASE;
		double db = 0;// 分贝
		if (ratio > 1) {
			db = 20 * Math.log10(ratio);
		}
		volume((int) db);
		if (!endShow) {
			RoundHandler.runMethod(this, 200, "showVolume");
		}
	}

	private class PlayListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(voicePath);
				mPlayer.prepare();
				mPlayer.start();
			} catch (IOException e) {
				UtilTools.toast(context, e.getMessage());
			}
		}
	}

	public abstract void volume(int db);

	public abstract void begin();

	public abstract void end(String voicePath);
}
