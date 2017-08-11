package cn.breaksky.rounds.publics.util;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class BitmapUtil {

	public static Bitmap decodeResource(Resources res, int resId, int width, int height) {
		// 首先设置 inJustDecodeBounds=true 来获取图片尺寸
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 计算 inSampleSize 的值
		options.inSampleSize = calculateInSampleSize(options, width, height);
		// 根据计算出的 inSampleSize 来解码图片生成Bitmap
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap decodeByteArray(byte[] bytes, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		options.inSampleSize = calculateInSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// 原始图片的宽高
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			// 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	public static void recycle(ViewGroup viewGroup) {
		int len = viewGroup.getChildCount();
		for (int i = 0; i < len; i++) {
			View view = viewGroup.getChildAt(i);
			if (view instanceof ImageView) {
				recycle((ImageView) view);
			}
		}
	}

	public static void recycle(ImageView imageView) {
		recycle(imageView.getDrawable());
		imageView.setImageBitmap(null);
	}

	public static void recycle(ImageButton imageButton) {
		recycle(imageButton.getDrawable());
		imageButton.setImageBitmap(null);
	}

	public static void recycle(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
			if (bmp != null && !bmp.isRecycled()) {
				bmp.recycle();
				bmp = null;
			}
		}
	}

	public static BitmapDrawable getDrawableFromResource(Resources res, int resid) {
		Bitmap bm = BitmapFactory.decodeResource(res, resid);
		BitmapDrawable bd = new BitmapDrawable(res, bm);
		return bd;
	}

	public static Bitmap grey(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap faceIconGreyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(faceIconGreyBitmap);
		Paint paint = new Paint();
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);
		ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
		paint.setColorFilter(colorMatrixFilter);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return faceIconGreyBitmap;
	}

	public static List<CutBitmap> cut(Bitmap source, int width, int height) throws Exception {
		List<CutBitmap> list = new ArrayList<CutBitmap>();
		int sWidth = source.getWidth(); // 图片宽度
		int sHeight = source.getHeight(); // 图片高度
		if (sWidth > width && sHeight > height) {
			int cols = 0; // 横向切片总数
			int rows = 0; // 纵向切片总数
			int eWidth = 0; // 末端切片宽度
			int eHeight = 0; // 末端切片高度
			if (sWidth % width == 0) {
				cols = sWidth / width;
			} else {
				eWidth = sWidth % width;
				cols = sWidth / width + 1;
			}
			if (sHeight % height == 0) {
				rows = sHeight / height;
			} else {
				eHeight = sHeight % height;
				rows = sHeight / height + 1;
			}
			int cWidth = 0; // 当前切片宽度
			int cHeight = 0; // 当前切片高度
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					cWidth = getWidth(j, cols, eWidth, width);
					cHeight = getHeight(i, rows, eHeight, height);
					// x坐标,y坐标,宽度,高度
					CutBitmap cutMap = new CutBitmap();
					cutMap.x = j * width;
					cutMap.y = i * height;
					cutMap.width = cWidth;
					cutMap.height = cHeight;
					cutMap.bitmap = Bitmap.createBitmap(source, cutMap.x, cutMap.y, cutMap.width, cutMap.height);
					list.add(cutMap);
				}
			}
		}
		return list;
	}

	// 生成圆角图片
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
			final float roundPx = 14;
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.BLACK);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			canvas.drawBitmap(bitmap, src, rect, paint);
			return output;
		} catch (Exception e) {
			return bitmap;
		}
	}

	public static class CutBitmap {
		public int x;
		public int y;
		public int width;
		public int height;
		public Bitmap bitmap;
	}

	private static int getWidth(int index, int cols, int endWidth, int width) {
		if (index == cols - 1) {
			if (endWidth != 0) {
				return endWidth;
			}
		}
		return width;
	}

	private static int getHeight(int index, int rows, int endHeight, int height) {
		if (index == rows - 1) {
			if (endHeight != 0) {
				return endHeight;
			}
		}
		return height;
	}

	/**
	 * 根据宽度从本地图片路径获取该图片的缩略图
	 * 
	 * @param localImagePath
	 *            本地图片的路径
	 * @param width
	 *            缩略图的宽
	 * @return bitmap 指定宽高的缩略图
	 */
	public static Bitmap thumbnail(String localImagePath, int width) {
		Bitmap temBitmap = null;
		try {
			BitmapFactory.Options outOptions = new BitmapFactory.Options();
			// 设置该属性为true，不加载图片到内存，只返回图片的宽高到options中。
			outOptions.inJustDecodeBounds = true;
			// 加载获取图片的宽高
			BitmapFactory.decodeFile(localImagePath, outOptions);
			int height = outOptions.outHeight;
			if (outOptions.outWidth > width) {
				// 根据宽设置缩放比例
				outOptions.inSampleSize = outOptions.outWidth / width + 1;
				outOptions.outWidth = width;
				// 计算缩放后的高度
				height = outOptions.outHeight / outOptions.inSampleSize;
				outOptions.outHeight = height;
			}
			// 重新设置该属性为false，加载图片返回
			outOptions.inJustDecodeBounds = false;
			outOptions.inPurgeable = true;
			outOptions.inInputShareable = true;
			temBitmap = BitmapFactory.decodeFile(localImagePath, outOptions);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return temBitmap;
	}
}
