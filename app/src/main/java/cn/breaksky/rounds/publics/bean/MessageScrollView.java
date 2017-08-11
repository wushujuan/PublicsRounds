package cn.breaksky.rounds.publics.bean;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MessageScrollView extends ScrollView {
	private MessageScrollViewListener scrollViewListener;

	public MessageScrollView(Context context) {
		super(context);
	}

	public MessageScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MessageScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnScrollViewListener(MessageScrollViewListener listener) {
		this.scrollViewListener = listener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}
}
