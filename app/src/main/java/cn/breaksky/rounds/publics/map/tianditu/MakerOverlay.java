package cn.breaksky.rounds.publics.map.tianditu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import cn.breaksky.rounds.publics.R;

import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.Overlay;

public class MakerOverlay extends Overlay {
	private Context context;
	private GeoPoint point;
	private Drawable maker;

	public MakerOverlay(Context context, GeoPoint point) {
		this.context = context;
		this.point = point;
		this.maker = this.context.getResources().getDrawable(R.drawable.maker_small);
	}

	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		mapView.postInvalidate();
		return true;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Point point = mapView.getProjection().toPixels(this.point, null);
		this.maker.setBounds(point.x - this.maker.getIntrinsicWidth() / 2, point.y - this.maker.getIntrinsicHeight(), point.x + this.maker.getIntrinsicWidth() / 2, point.y);
		this.maker.draw(canvas);
	}
}
