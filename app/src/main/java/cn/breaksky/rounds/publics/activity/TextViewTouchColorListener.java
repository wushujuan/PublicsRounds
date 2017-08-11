package cn.breaksky.rounds.publics.activity;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class TextViewTouchColorListener implements OnTouchListener {
	private int touchColor;
	private int notTouchColor;

	public TextViewTouchColorListener(int touchColor, int notTouchColor) {
		this.touchColor = touchColor;
		this.notTouchColor = notTouchColor;
	}

	public boolean onTouch(View v, MotionEvent event) {
		int color = notTouchColor;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			color = touchColor;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			color = notTouchColor;
		}
		TextView textView = (TextView) v;
		textView.setTextColor(v.getResources().getColor(color));
		return false;
	}
}
