package cn.breaksky.rounds.publics.map.tianditu;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import cn.breaksky.rounds.publics.R;

import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.Overlay;

public class LineOverlay extends Overlay {
	private List<GeoPoint> points;
	private Context context;
	private boolean showMarker;

	public LineOverlay(Context context, List<GeoPoint> point, boolean showMarker) {
		this.points = point;
		this.context = context;
	}

	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		mapView.postInvalidate();
		return true;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Paint paint = new Paint(Paint.DITHER_FLAG);
		paint.setStyle(Style.STROKE);// 设置非填充
		paint.setStrokeWidth(3);// 笔宽5像素
		paint.setColor(Color.BLUE);// 设置为红笔
		paint.setAntiAlias(true);// 锯齿不显示

		int i = 0;
		for (GeoPoint mPoint : points) {
			Point point = mapView.getProjection().toPixels(mPoint, null);
			if (showMarker) {
				Drawable maker = this.context.getResources().getDrawable(R.drawable.maker_small);
				maker.setBounds(point.x - maker.getIntrinsicWidth() / 2, point.y - maker.getIntrinsicHeight(), point.x + maker.getIntrinsicWidth() / 2, point.y);
				maker.draw(canvas);
			}
			if (points.size() - 1 == i) {
				break;
			}
			Point endPoint = mapView.getProjection().toPixels(points.get(i + 1), null);
			canvas.drawLine(point.x, point.y, endPoint.x, endPoint.y, paint);// 画线
			i++;
		}
	}
}
