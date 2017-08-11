package cn.breaksky.rounds.publics.map.tianditu;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import cn.breaksky.rounds.publics.map.BasicMapActivity;
import cn.breaksky.rounds.publics.map.Point;

import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapController;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.Overlay;
import com.tianditu.android.maps.TGeoAddress;
import com.tianditu.android.maps.TGeoDecode;
import com.tianditu.android.maps.TGeoDecode.OnGeoResultListener;

public class TiandituMap implements BasicMapActivity, OnGeoResultListener {
	private MapView mMapView;
	private Context context;
	private AddressNameCallBack addressNameCallBack;
	private List<Overlay> myOverlay;

	@Override
	public View getMapView(Context context, int type) {
		this.context = context;
		mMapView = new MapView(context, "");
		mMapView.setMapType(type == VECTOR ? MapView.TMapType.MAP_TYPE_VEC : MapView.TMapType.MAP_TYPE_IMG);
		// 设置启用内置的缩放控件
		mMapView.setBuiltInZoomControls(false);
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		MapController mMapController = mMapView.getController();
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		GeoPoint point = new GeoPoint((int) (30.67 * 1E6), (int) (104.06 * 1E6));
		// 设置地图中心点
		mMapController.setCenter(point);
		// 设置地图zoom级别
		mMapController.setZoom(12);

		mMapView.setDrawingCacheEnabled(true);

		myOverlay = mMapView.getOverlays();
		return mMapView;
	}

	@Override
	public void open2DMap() {

	}

	@Override
	public void open3DMap() {

	}

	@Override
	public void zoomIn() {
		mMapView.getController().zoomIn();
	}

	@Override
	public void zoomOut() {
		mMapView.getController().zoomOut();
	}

	@Override
	public void addPointLine(List<? extends Point> points) {
		List<GeoPoint> gPoint = new ArrayList<GeoPoint>();
		for (Point point : points) {
			gPoint.add(getGeoPoint(point.getLatitude(), point.getLongitude()));
		}
		myOverlay.add(new LineOverlay(this.context, gPoint, true));
		this.setCenter(points.get(0).getLatitude(), points.get(0).getLongitude());
	}

	@Override
	public void addPolygon(List<? extends Point> points) {

	}

	@Override
	public void addPoint(Point point, boolean autoRemove) {
		myOverlay.add(new MakerOverlay(this.context, getGeoPoint(point.getLatitude(), point.getLongitude())));
		this.setCenter(point.getLatitude(), point.getLongitude());
	}

	@Override
	public void cleanOverlay() {
		myOverlay.clear();
	}

	@Override
	public Bitmap getSnap() {
		Bitmap cache = mMapView.getDrawingCache();
		if (cache != null) {
			cache = cache.copy(Bitmap.Config.RGB_565, true);
		}
		mMapView.destroyDrawingCache();
		return cache;
	}

	@Override
	public void getCenter(PointCallBack callBack) {
		GeoPoint geoPoint = mMapView.getMapCenter();
		Point p = new Point();
		p.setLatitude((float) geoPoint.getLatitudeE6() / 1000000);
		p.setLongitude((float) geoPoint.getLongitudeE6() / 1000000);
		callBack.callBack(p);
	}

	@Override
	public void getAddressName(Point point, AddressNameCallBack callBack) {
		this.addressNameCallBack = callBack;
		TGeoDecode decode = new TGeoDecode(this);
		decode.search(getGeoPoint(point.getLatitude(), point.getLongitude()));
	}

	@Override
	public void onGeoDecodeResult(TGeoAddress address, int errCode) {
		if (addressNameCallBack != null) {
			this.addressNameCallBack.callBack(address.getCity(), address.getFullName());
		}
	}

	@Override
	public void setCenter(float lat, float log) {
		mMapView.getController().setCenter(getGeoPoint(lat, log));
	}

	private GeoPoint getGeoPoint(float lat, float log) {
		return new GeoPoint((int) (lat * 1000000), (int) (log * 1000000));
	}
}
