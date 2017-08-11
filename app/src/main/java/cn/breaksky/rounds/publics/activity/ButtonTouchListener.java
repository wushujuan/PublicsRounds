package cn.breaksky.rounds.publics.activity;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.util.BitmapUtil;

public class ButtonTouchListener implements OnTouchListener {
	private int touch;
	private int notouch;

	public ButtonTouchListener(int touch, int notouch) {
		this.touch = touch;
		this.notouch = notouch;
	}

	public boolean onTouch(View v, MotionEvent event) {
		int resId = touch;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			resId = touch;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			resId = notouch;
		}
		if (v instanceof ImageButton) {
			((ImageButton) v).setImageBitmap(BitmapUtil.decodeResource(ContextManager.getInstance().getResources(), resId, v.getWidth(), v.getHeight()));
		} else if (v instanceof Button) {
			((Button) v).setBackgroundResource(resId);
		}
		return false;
	}

}
