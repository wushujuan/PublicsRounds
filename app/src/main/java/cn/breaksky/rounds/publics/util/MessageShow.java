package cn.breaksky.rounds.publics.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.activity.ButtonTouchListener;
import cn.breaksky.rounds.publics.activity.MapCordinatesActivity;
import cn.breaksky.rounds.publics.listener.MessageObj.MESSAGE_TYPE;
import cn.breaksky.rounds.publics.map.PointParcelable;

public class MessageShow {
	private Activity context;

	public MessageShow(Activity context) {
		this.context = context;
	}

	public FrameLayout getMessageLayout(String nameStr, Bitmap icoSouce, boolean isLeft, Date date, View contentView, boolean showStatus) {
		// 主框架
		FrameLayout mainLayout = new FrameLayout(context);
		LinearLayout.LayoutParams mainLayoutParam = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		mainLayoutParam.gravity = isLeft ? Gravity.LEFT : Gravity.RIGHT;
		mainLayout.setLayoutParams(mainLayoutParam);
		// 头像
		ImageView icoView = new ImageView(context);
		icoView.setBackgroundResource(R.xml.bg_border1);
		icoView.setPadding(5, 5, 5, 5);
		if (icoSouce != null) {
			icoView.setImageBitmap(icoSouce);
		} else {
			icoView.setImageResource(R.drawable.callhead);
		}
		FrameLayout.LayoutParams icoViewParam = new FrameLayout.LayoutParams(120, 120);
		if (isLeft) {
			icoViewParam.gravity = Gravity.LEFT;
		} else {
			icoViewParam.gravity = Gravity.RIGHT;
		}
		icoViewParam.setMargins(0, 5, 0, 0);
		icoView.setLayoutParams(icoViewParam);
		mainLayout.addView(icoView);
		// 内容主框架
		LinearLayout contentMainlayout = new LinearLayout(context);
		contentMainlayout.setOrientation(LinearLayout.VERTICAL);
		FrameLayout.LayoutParams contentMainlayoutParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		if (isLeft) {
			contentMainlayoutParam.gravity = Gravity.RIGHT;
			contentMainlayoutParam.setMargins(120, 0, 0, 0);
		} else {
			contentMainlayoutParam.gravity = Gravity.LEFT;
			contentMainlayoutParam.setMargins(0, 0, 120, 0);
		}
		contentMainlayout.setLayoutParams(contentMainlayoutParam);
		mainLayout.addView(contentMainlayout);
		// 名称
		TextView nameView = new TextView(context);
		nameView.setTextSize(12);
		LinearLayout.LayoutParams nameViewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		if (isLeft) {
			nameViewParam.gravity = Gravity.LEFT;
			nameViewParam.setMargins(25, 0, 0, 0);
		} else {
			nameViewParam.gravity = Gravity.RIGHT;
			nameViewParam.setMargins(0, 0, 25, 0);
		}
		nameView.setLayoutParams(nameViewParam);
		nameView.setText(nameStr);
		nameView.setTextSize(12);
		contentMainlayout.addView(nameView);
		// 内容框架
		LinearLayout contentLayout = new LinearLayout(context);
		contentLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams contentLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		if (isLeft) {
			contentLayout.setBackgroundResource(R.drawable.feedback_left_dialog_bg);
		} else {
			contentLayout.setBackgroundResource(R.drawable.feedback_right_dialog_bg);
		}
		contentLayout.setLayoutParams(contentLayoutParam);
		contentMainlayout.addView(contentLayout);
		// 时间
		TextView timeView = new TextView(context);
		timeView.setTextSize(10);
		timeView.setTextColor(context.getResources().getColor(R.color.text_green));
		String d = UtilTools.formatDate(date);
		timeView.setText(d == null ? "- - -" : d);
		LinearLayout.LayoutParams timeViewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		if (isLeft) {
			timeViewParam.gravity = Gravity.LEFT;
		} else {
			timeViewParam.gravity = Gravity.RIGHT;
		}
		timeView.setLayoutParams(timeViewParam);
		contentLayout.addView(timeView);
		// 内容
		LinearLayout.LayoutParams contentViewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		contentView.setLayoutParams(contentViewParam);
		contentLayout.addView(contentView);
		// 进度条
		if (showStatus) {
			LinearLayout barLayout = new LinearLayout(context);
			barLayout.setGravity(Gravity.LEFT);
			ImageView barView = new ImageView(context);
			barView.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
			barView.setImageDrawable(BitmapUtil.getDrawableFromResource(context.getResources(), R.drawable.bar_wait));
			barLayout.addView(barView);
			contentLayout.addView(barLayout);
		}
		return mainLayout;
	}

	public void setMessageStatus(int id, final boolean status) {
		final ImageView bar = this.getBarView(id);
		if (bar == null) {
			return;
		}
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (bar.getDrawable() != null) {
					BitmapUtil.recycle(bar.getDrawable());
				}
				bar.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
				if (status) {
					bar.setImageDrawable(BitmapUtil.getDrawableFromResource(context.getResources(), R.drawable.bar_complete));
				} else {
					bar.setImageDrawable(BitmapUtil.getDrawableFromResource(context.getResources(), R.drawable.bar_faile));
				}
			}
		});
	}

	public void setMessageStatus(int id, final float percent) {
		final ImageView bar = this.getBarView(id);
		if (bar == null) {
			return;
		}
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				int width = ((LinearLayout) bar.getParent()).getWidth();
				if (width <= 0) {
					return;
				}
				if (bar.getDrawable() != null) {
					BitmapUtil.recycle(bar.getDrawable());
				}
				Bitmap percentMap = Bitmap.createBitmap(width, 5, Bitmap.Config.RGB_565);
				Canvas canvas = new Canvas();
				canvas.setBitmap(percentMap);

				Paint paint = new Paint(Paint.DITHER_FLAG);
				paint.setAntiAlias(true);
				paint.setStyle(Style.FILL);
				paint.setColor(Color.GREEN);
				canvas.drawRect(new Rect(0, 0, (int) (width * percent), 5), paint);

				bar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 20));
				bar.setImageBitmap(percentMap);
			}
		});
	}

	private ImageView getBarView(int id) {
		View view = context.findViewById(id);
		if (view == null) {
			return null;
		}
		LinearLayout layout = (LinearLayout) view.getParent();
		int count = layout.getChildCount();
		for (int i = 0; i < count; i++) {
			View childView = layout.getChildAt(i);
			if (childView instanceof LinearLayout) {
				return (ImageView) ((LinearLayout) childView).getChildAt(0);
			}
		}
		return null;
	}

	public View getMessageView(int id, MESSAGE_TYPE type, String source, ButtonOnClick callBack) {
		switch (type) {
		case PHOTO:
			ImageView imageView = new ImageView(context);
			imageView.setId(id);
			imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.photo));
			imageView.setOnClickListener(new ShowPhotoListener(id, source, callBack));
			return imageView;
		case VOICE:
			LinearLayout.LayoutParams voiceParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			ImageButton voiceButton = new ImageButton(context);
			voiceButton.setLayoutParams(voiceParam);
			voiceButton.setId(id);
			voiceButton.setImageDrawable(context.getResources().getDrawable(R.drawable.voice_min));
			voiceButton.setOnTouchListener(new ButtonTouchListener(R.drawable.voice_min_touch, R.drawable.voice_min));
			voiceButton.setOnClickListener(new VoicePlayListener(id, source, callBack));
			return voiceButton;
		case TEXT:
			TextView textView = new TextView(context);
			textView.setId(id);
			textView.setText(source);
			return textView;
		case DISPATCH:
			break;
		case FILE:
			String[] splits = source.split(";");
			TextView fileView = new TextView(context);
			fileView.setId(id);
			if (splits.length == 2) {
				FileClickLinstener fileLinstener = new FileClickLinstener(splits[1]);
				fileView.setText(splits[0]);
				fileView.setOnClickListener(fileLinstener);
			} else {
				fileView.setText("参数错误");
			}
			return fileView;
		case COORDINATE:
			TextView coordinateView = new TextView(context);
			coordinateView.setId(id);
			String[] message = source.split(";");
			if (message.length == 2) {
				coordinateView.setText("[点]地图查看");
				coordinateView.setOnClickListener(new CoordinateClickLinstener(context, Float.valueOf(message[1]), Float.valueOf(message[0])));
			} else {
				coordinateView.setText("坐标错误");
			}
			return coordinateView;
		case LINE:
			TextView lineView = new TextView(context);
			lineView.setId(id);
			lineView.setText("[线]地图查看");
			lineView.setOnClickListener(new LineClickLinstener(context, source));
			return lineView;
		case PLAN:
			break;
		case VIDEO:
			break;
		}
		return null;
	}

	private class ShowPhotoListener implements OnClickListener {
		private String path;
		private ButtonOnClick onClick;
		private int id;

		public ShowPhotoListener(int id, String path, ButtonOnClick onClick) {
			this.path = path;
			this.onClick = onClick;
			this.id = id;
		}

		@Override
		public void onClick(View view) {
			if (this.onClick != null) {
				this.onClick.onclick(this.id);
			} else {
				Builder alert = new AlertDialog.Builder(view.getContext());
				alert.setPositiveButton("关闭", null);
				ImageView img = new ImageView(view.getContext());
				Bitmap bm = BitmapFactory.decodeFile(path);
				img.setImageBitmap(bm);
				alert.setView(img);
				alert.show();
			}
		}
	}

	private class VoicePlayListener implements OnClickListener {
		private String path;
		private ButtonOnClick onClick;
		private int id;

		public VoicePlayListener(int id, String path, ButtonOnClick onClick) {
			this.path = path;
			this.onClick = onClick;
			this.id = id;
		}

		@Override
		public void onClick(View v) {
			if (this.onClick != null) {
				this.onClick.onclick(this.id);
			} else {
				MediaPlayer mPlayer = new MediaPlayer();
				try {
					mPlayer.setDataSource(path);
					mPlayer.prepare();
					mPlayer.start();
				} catch (IOException e) {
					UtilTools.toast(context, e.getMessage());
				}
			}

		}
	}

	private class CoordinateClickLinstener implements OnClickListener {
		private Float latitude;
		private Float longitude;
		private Activity context;

		public CoordinateClickLinstener(Activity context, Float latitude, Float longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.context = context;
		}

		@Override
		public void onClick(View v) {
			if (latitude == null || longitude == null) {
				UtilTools.toast(context, "坐标错误");
			} else {
				Intent intent = new Intent(context, MapCordinatesActivity.class);
				intent.putExtra("onlyshow", true);
				intent.putExtra("lat", latitude);
				intent.putExtra("log", longitude);
				context.startActivity(intent);
			}
		}
	}

	private class LineClickLinstener implements OnClickListener {
		private String cordinateStr;
		private Activity context;

		public LineClickLinstener(Activity context, String cordinateStr) {
			this.cordinateStr = cordinateStr;
			this.context = context;
		}

		@Override
		public void onClick(View v) {
			if (cordinateStr == null || cordinateStr.length() < 1) {
				UtilTools.toast(context, "坐标错误");
			} else {
				String[] cordinates = cordinateStr.split(",");
				List<PointParcelable> points = new ArrayList<PointParcelable>();
				for (String cordinate : cordinates) {
					String[] pStr = cordinate.split(";");
					if (pStr.length == 2) {
						PointParcelable point = new PointParcelable();
						point.setLatitude(UtilTools.stringToFloat(pStr[1], 0));
						point.setLongitude(UtilTools.stringToFloat(pStr[0], 0));
						if (point.getLatitude() > 0 && point.getLongitude() > 0) {
							points.add(point);
						}
					}
				}
				Intent intent = new Intent(context, MapCordinatesActivity.class);
				intent.putExtra("onlyshow", true);
				intent.putParcelableArrayListExtra("line", (ArrayList<? extends Parcelable>) points);
				context.startActivity(intent);
			}
		}
	}

	private class FileClickLinstener implements OnClickListener {
		private File file;
		private String name;

		public FileClickLinstener(String path) {
			this.file = new File(path);
			if (!this.file.exists()) {
				this.name = "文件出错";
			} else {
				this.name = this.file.getName();
			}
		}

		public String getName() {
			return this.name;
		}

		@Override
		public void onClick(View v) {
			UtilTools.alertMessage(context, "文件路径:" + this.file.getPath());
		}

	}

	public interface ButtonOnClick {
		public void onclick(Integer id);
	}
}
