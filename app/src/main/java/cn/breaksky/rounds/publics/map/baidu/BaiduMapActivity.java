package cn.breaksky.rounds.publics.map.baidu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.handler.HandlerCallBack;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.map.BasicMapActivity;
import cn.breaksky.rounds.publics.map.Point;
import cn.breaksky.rounds.publics.util.UtilTools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

public class BaiduMapActivity implements BasicMapActivity {
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private float maxZoom;
	private float minZoom;
	private float nowZoom;
	private MKOfflineMap mOffline = null;
	private Context context;

	@Override
	public View getMapView(Context context, int type) {
		this.context = context;
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		// mapOptions.scaleControlEnabled(false); // 隐藏比例尺控件
		mapOptions.zoomControlsEnabled(false);// 隐藏缩放按钮
		SDKInitializer.initialize(context.getApplicationContext());

		mMapView = new MapView(context, mapOptions);
		mBaiduMap = mMapView.getMap();
		if (mBaiduMap == null) {
			return null;
		}
		mBaiduMap.snapshot(new SnapshotReadyCallback() {

			@Override
			public void onSnapshotReady(Bitmap bm) {

			}
		});
		if (type == VECTOR) {
			// 普通地图
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		} else if (type == SATELLITE) {
			// 卫星地图
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		}
		// 获取最大的缩放级别
		maxZoom = mBaiduMap.getMaxZoomLevel();
		// 获取最大的缩放级别
		minZoom = mBaiduMap.getMinZoomLevel();
		// 定义地图状态
		nowZoom = minZoom;
		this.setZoom();
		mOffline = new MKOfflineMap();
		mOffline.init(new BaiduMKOfflineMapListener());
		this.setCenter(new LatLng(_map_center_lat, _map_center_lng));
		return mMapView;
	}

	class BaiduMKOfflineMapListener implements MKOfflineMapListener {

		@Override
		public void onGetOfflineMapState(int type, int state) {

		}

	}

	public void open2DMap() {
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
	}

	public void open3DMap() {
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
	}

	@Override
	public void zoomIn() {
		nowZoom = mBaiduMap.getMapStatus().zoom;
		nowZoom++;
		if (nowZoom > maxZoom) {
			nowZoom = maxZoom;
		}
		setZoom();
	}

	@Override
	public void zoomOut() {
		nowZoom = mBaiduMap.getMapStatus().zoom;
		nowZoom--;
		if (nowZoom < minZoom) {
			nowZoom = minZoom;
		}
		setZoom();
	}

	public void addPointLine(List<? extends Point> points) {
		List<LatLng> latLngs = new ArrayList<LatLng>();
		for (int i = 0; i < points.size(); i++) {
			LatLng point = this.convertPoint(points.get(i));
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.markers_red);
			MarkerOptions option = new MarkerOptions().position(point).icon(bitmap);
			option.zIndex(3);
			mBaiduMap.addOverlay(option);
			latLngs.add(point);
		}
		PolylineOptions option = new PolylineOptions();
		option.points(latLngs);
		option.color(R.color.blue);
		option.zIndex(2);
		mBaiduMap.addOverlay(option);
		LatLng last = latLngs.get(latLngs.size() - 1);
		this.setCenter(last);
	}

	private Marker marker = null;

	public void addPoint(Point point, boolean autoRemove) {
		if (autoRemove && marker != null) {
			marker.remove();
		}
		LatLng latlng = this.convertPoint(point);
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_blue);
		MarkerOptions option = new MarkerOptions().position(latlng).icon(bitmap);
		option.zIndex(4);
		marker = (Marker) mBaiduMap.addOverlay(option);
		this.setCenter(latlng);
	}

	private void setCenter(LatLng last) {
		float z = mBaiduMap.getMapStatus().zoom;
		// 定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder().target(last).zoom(z > 11 ? z : 11).build();
		// 改变地图状态
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
	}

	private void setZoom() {
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(nowZoom));
	}

	@Override
	public void addPolygon(List<? extends Point> points) {
		List<LatLng> latLngs = new ArrayList<LatLng>();
		for (int i = 0; i < points.size(); i++) {
			LatLng point = this.convertPoint(points.get(i));
			// BitmapDescriptor bitmap =
			// BitmapDescriptorFactory.fromResource(R.drawable.markers_red);
			// MarkerOptions option = new
			// MarkerOptions().position(point).icon(bitmap);
			// option.zIndex(3);
			// mBaiduMap.addOverlay(option);
			latLngs.add(point);
		}
		PolygonOptions option = new PolygonOptions();
		option.points(latLngs);
		option.fillColor(R.color.blue);
		option.zIndex(2);
		mBaiduMap.addOverlay(option);
		LatLng last = latLngs.get(latLngs.size() - 1);
		this.setCenter(last);
	}

	@Override
	public void cleanOverlay() {
		mBaiduMap.clear();
	}

	@Override
	public Bitmap getSnap() {
		return null;
	}

	@Override
	public void getCenter(PointCallBack callBack) {
		LatLng p = mBaiduMap.getMapStatus().target;
		convertBaiduPoint(p.latitude, p.longitude, callBack);
	}

	@Override
	public void setCenter(float lat, float log) {
		Point point = new Point();
		point.setLatitude(lat);
		point.setLongitude(log);
		this.setCenter(this.convertPoint(point));
	}

	@Override
	public void getAddressName(Point point, final AddressNameCallBack callBack) {
		GeoCoder mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult res) {
				callBack.callBack(res.getAddressDetail().city, res.getAddress());
			}

			@Override
			public void onGetGeoCodeResult(GeoCodeResult res) {
				// System.out.print(res.getAddress());
			}
		});
		ReverseGeoCodeOption option = new ReverseGeoCodeOption();
		option.location(new LatLng(point.getLatitude(), point.getLongitude()));
		mSearch.reverseGeoCode(option);
	}

	private LatLng convertPoint(Point point) {
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);
		converter.coord(new LatLng(point.getLatitude(), point.getLongitude()));
		return converter.convert();
	}

	private Point convertBaiduPoint2(double latitude, double longitude) {
		Point gps = new Point();
		double x_pi = Math.PI;
		double bd_x = longitude - 0.0065;
		double bd_y = latitude - 0.006;
		double z = Math.sqrt(bd_x * bd_x + bd_y * bd_y) - 0.00002 * Math.sin(bd_y * x_pi);
		double theta = Math.atan2(bd_y, bd_x) - 0.000003 * Math.cos(bd_x * x_pi);
		gps.setLongitude(Double.valueOf(z * Math.cos(theta)).floatValue());
		gps.setLatitude(Double.valueOf(z * Math.sin(theta)).floatValue());
		return gps;
	}

	private String pointUrl;
	private String pointStr;

	private void convertBaiduPoint(final double latitude, final double longitude, final PointCallBack callBack) {
		if (pointUrl != null) {
			UtilTools.toast(context, "上一个坐标转换未完成");
			callBack.callBack(convertBaiduPoint2(latitude, longitude));
		}
		StringBuffer url = new StringBuffer("http://api.zdoz.net/bd2wgs.aspx?");
		url.append("lat=");
		url.append(latitude);
		url.append("&lng=");
		url.append(longitude);

		pointUrl = url.toString();

		RoundHandler.runMethod(this, "queryPoint", new HandlerCallBack() {

			@Override
			public void callBack() {
				Point point = null;
				if (pointStr == null) {
					UtilTools.toast(context, "坐标转换失败，坐标会有偏差");
					point = convertBaiduPoint2(latitude, longitude);
				} else {
					try {
						JSONObject retObj = JSONArray.parseObject(pointStr);
						pointStr = null;
						point = new Point();
						point.setLatitude(retObj.getFloat("Lat"));
						point.setLongitude(retObj.getFloat("Lng"));
					} catch (Exception e) {
						e.printStackTrace();
						point = convertBaiduPoint2(latitude, longitude);
					}
				}
				callBack.callBack(point);
			}
		});

	}

	public void queryPoint() {
		try {
			URLConnection connection = (new URL(pointUrl)).openConnection();
			connection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
			out.flush();
			out.close();
			String sCurrentLine = "";
			InputStream l_urlStream;
			l_urlStream = connection.getInputStream();
			BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
			pointStr = "";
			while ((sCurrentLine = l_reader.readLine()) != null) {
				if (!sCurrentLine.equals("")) {
					pointStr += sCurrentLine;
				}
			}
		} catch (Exception e) {
			pointStr = null;
		}
		pointUrl = null;
	}
}
