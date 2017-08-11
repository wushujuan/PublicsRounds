package cn.breaksky.rounds.publics.map;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

public interface BasicMapActivity {
	// 成都
	final static float _map_center_lng = 104.06667f;
	final static float _map_center_lat = 30.66667f;

	// 旌阳
	// final static float _map_center_lng = 104.423172f;
	// final static float _map_center_lat = 31.149568f;

	public interface AddressNameCallBack {
		public void callBack(String city, String address);
	}

	public interface PointCallBack {
		public void callBack(Point point);
	}

	public static final int VECTOR = 1;
	public static final int SATELLITE = 2;

	public View getMapView(Context context, int type);

	public void open2DMap();

	public void open3DMap();

	public void zoomIn();

	public void zoomOut();

	public void addPointLine(List<? extends Point> points);

	public void addPolygon(List<? extends Point> points);

	public void addPoint(Point point, boolean autoRemove);

	public void cleanOverlay();

	public Bitmap getSnap();

	public void getCenter(PointCallBack callBack);

	public void setCenter(float lat, float log);

	public void getAddressName(Point point, AddressNameCallBack callBack);
}
