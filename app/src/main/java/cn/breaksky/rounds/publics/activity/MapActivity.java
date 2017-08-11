package cn.breaksky.rounds.publics.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.Area;
import cn.breaksky.rounds.publics.bean.Line;
import cn.breaksky.rounds.publics.bean.WorkBean;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.listener.GPSData;
import cn.breaksky.rounds.publics.map.BasicMapActivity;
import cn.breaksky.rounds.publics.map.Point;
import cn.breaksky.rounds.publics.map.baidu.BaiduMapActivity;
import cn.breaksky.rounds.publics.map.tianditu.TiandituMap;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.SendCheckLoginThread;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
/**
 * 地图定位页面
 * @author Administrator
 *
 */
public class MapActivity extends Activity {
	private ImageButton m_btnZoomin;
	private ImageButton m_btnZoomout;
	private TextView m_btnWork;
	private ImageButton m_btnLayer;
	private CheckBox c_showGpsPoint;
	private CheckBox c_showWork;
	private RelativeLayout mapLayout;
	private BasicMapActivity mapActivity;
	private TextView cancelButton;// map_show_back_button
	private Point iniPoint = null;
	private int mapType;
	public MapView mapView = null;
	public BaiduMap baiduMap = null;
	private double latitude;
	private double longitude;
	private Marker mMarker;//标注
	BitmapDescriptor icon;//标注图标
	Integer index = 0;
	List<LatLng> points = new ArrayList<LatLng>();// 所有路线点集合
	
	// 定位相关声明
	public LocationClient locationClient = null;
	//自定义图标
	BitmapDescriptor mCurrentMarker = null;
	boolean isFirstLoc = true;// 是否首次定位

	
	public BDLocationListener myListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mapView == null)
				return;
			
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);	//设置定位数据
			
			latitude = location.getLatitude();
			Log.d("纬度", "纬度值："+latitude);
			
			longitude = location.getLongitude();
			Log.d("经度", "经度值："+longitude);
						
			if (isFirstLoc) {
				isFirstLoc = false;

				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);	//设置地图中心点以及缩放级别
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				baiduMap.animateMapStatus(u);
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ContextManager.addActivity(this);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_newmap);
		//initVeiw();
		m_btnWork = (TextView) findViewById(R.id.map_show_work_button);
		m_btnWork.setOnClickListener(new WorkBtnListener());
		
		c_showGpsPoint = (CheckBox) this.findViewById(R.id.show_gps_point);
		c_showWork = (CheckBox) this.findViewById(R.id.show_work_status);
		
		cancelButton = (TextView) this.findViewById(R.id.map_show_back_button);
		cancelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));
		cancelButton.setOnClickListener(new CancelButtonOnClick());
		
		mapView = (MapView) this.findViewById(R.id.dmapView); // 获取地图控件引用
		baiduMap = mapView.getMap();
		//开启定位图层
		baiduMap.setMyLocationEnabled(true);
		
		locationClient = new LocationClient(getApplicationContext()); // 实例化LocationClient类
		locationClient.registerLocationListener(myListener); // 注册监听函数
		this.setLocationOption();	//设置定位参数
		locationClient.start(); // 开始定位
		c_showGpsPoint.setChecked(true);
		c_showGpsPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
            @Override 
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) { 
                // TODO Auto-generated method stub 
                if(isChecked){ 
            		baiduMap.setMyLocationEnabled(true);
            		locationClient.start(); // 开始定位
                	
                }else{ 
                	locationClient.stop();	//停止定位
                	baiduMap.setMyLocationEnabled(false);
                } 
            } 
        }); 
		c_showWork.setOnClickListener(new WorkChceckListener());

		/*mapThread();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Point point = new Point();
			point.setLatitude(extras.getFloat("latitude"));
			point.setLongitude(extras.getFloat("longitude"));
			if (point.getLatitude() != 0 && point.getLongitude() != 0) {
				this.iniPoint = point;
			}
		}
		showMap(BasicMapActivity.VECTOR);*/
	}

	// 三个状态实现地图生命周期管理
	@Override
	protected void onDestroy() {
		//退出时销毁定位
		locationClient.stop();
		baiduMap.setMyLocationEnabled(false);
		// TODO Auto-generated method stub
		super.onDestroy();
		mapView.onDestroy();
		mapView = null;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 设置定位参数
	 */
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPS
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
		
		locationClient.setLocOption(option);
	}
	private void showMap(int type) {
		this.mapType = type;
		openBaiduMap(this.mapType);
		if (mapActivity == null) {
			UtilTools.alertError(this, "地图打开失败!请检查!");
		}
		WorkBean work = MainService.getInstance().getNowWork();
		if (work != null) {
			showWorkArea();
		}
		if (this.iniPoint != null) {
			c_showGpsPoint.setChecked(false);
			mapActivity.addPoint(this.iniPoint, false);
		}

	}

	public void mapThread() {
		if (c_showGpsPoint.isChecked()) {
			GPSData data = MainService.getInstance().getLastGPS();
			if (data != null) {
				Point point = new Point();
				point.setLatitude(data.getLatitude().floatValue());
				point.setLongitude(data.getLongitude().floatValue());
				mapActivity.addPoint(point, true);
			}
		}
		RoundHandler.runActivityMethod(this, 2000, "mapThread");
	}

	// 初始化控件，绑定监听器
	private void initVeiw() {
		m_btnZoomin = (ImageButton) findViewById(R.id.zoomin);
		m_btnZoomout = (ImageButton) findViewById(R.id.zoomout);
		m_btnWork = (TextView) findViewById(R.id.map_show_work_button);
		m_btnLayer = (ImageButton) findViewById(R.id.layer);
		c_showGpsPoint = (CheckBox) this.findViewById(R.id.show_gps_point);
		c_showWork = (CheckBox) this.findViewById(R.id.show_work_status);
		cancelButton = (TextView) this.findViewById(R.id.map_show_back_button);

		mapLayout = (RelativeLayout) findViewById(R.id.map_layout);

		m_btnZoomin.setOnClickListener(new ZoominBtnListener());
		m_btnZoomout.setOnClickListener(new ZoomoutBtnListener());
		m_btnWork.setOnClickListener(new WorkBtnListener());

		cancelButton.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));

		m_btnZoomin.setOnTouchListener(new ButtonTouchListener(R.drawable.zoonin_touch, R.drawable.zoonin));
		m_btnZoomout.setOnTouchListener(new ButtonTouchListener(R.drawable.zoonout_touch, R.drawable.zoonout));
		m_btnWork.setOnTouchListener(new TextViewTouchColorListener(R.color.red, R.color.white));

		m_btnLayer.setOnClickListener(new LayerListener());
		c_showWork.setOnClickListener(new WorkChceckListener());
		cancelButton.setOnClickListener(new CancelButtonOnClick());
	}

	// 放大按钮
	class ZoominBtnListener implements OnClickListener {
		public void onClick(View v) {
			mapActivity.zoomIn();
		}
	}

	// 缩小按钮
	class ZoomoutBtnListener implements OnClickListener {
		public void onClick(View v) {
			mapActivity.zoomOut();
		}
	}

	private ScrollView view;
	private AlertDialog dialog;
	private View v;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			showRet((RetBean) msg.obj);
		}
	};

	class RetBean {
		JSONObject json;
		String message;
	}

	private void showRet(RetBean obj) {
		if (obj.json == null) {
			UtilTools.toast(MapActivity.this, obj.message);
		} else {
			try {
				if (obj.json.getBoolean("status")) {
					MainService.getInstance().cleanWork();
					List<WorkBean> works = UtilTools.jsonToBean(obj.json.getJSONArray("works"), WorkBean.class);
					RadioGroup workRadio = new RadioGroup(MapActivity.this.v.getContext());
					for (WorkBean work : works) {
						MainService.getInstance().addWork(work);
						RadioButton radio = new RadioButton(MapActivity.this.v.getContext());
						StringBuffer str = new StringBuffer(work.getWorkname());
						str.append(" ");
						str.append(UtilTools.formatDate(new Date(work.getBegindate()), "MM-dd"));
						str.append(" ");
						str.append(work.getBegintime());
						str.append("到");
						str.append(UtilTools.formatDate(new Date(work.getEnddate()), "MM-dd"));
						str.append(" ");
						str.append(work.getEndtime());
						radio.setText(str.toString());
						radio.setOnCheckedChangeListener(new WorkRadioSelectListener(work.getRw_id(), dialog));
						radio.setTextColor(this.getResources().getColor(R.color.white));
						workRadio.addView(radio);
					}
					view.addView(workRadio);
				} else {
					UtilTools.toast(MapActivity.this, "查询任务失败!" + obj.json.getString("message"));
				}
			} catch (Exception e) {
				UtilTools.toast(MapActivity.this, "查询任务失败!" + e.getMessage());
			}
		}
	}

	// 选择任务
	private class WorkBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			baiduMap.clear();
			MapActivity.this.v = v;
			view = new ScrollView(v.getContext());
			AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
			alert.setTitle("选择任务");
			alert.setView(view);
			dialog = alert.show();
			try {
				Request request = new Request(Request.QUER_WORK);
				request.sendCheckLogin(new Params(), new SendCheckLoginThread.CallBack() {

					@Override
					public void callBack(JSONObject json) {
						Message msg = new Message();
						RetBean bean = new RetBean();
						bean.json = json;
						msg.obj = bean;
						handler.sendMessage(msg);
					}

					@Override
					public void exception(Exception e) {
						Message msg = new Message();
						RetBean bean = new RetBean();
						bean.message = e.toString();
						msg.obj = bean;
						handler.sendMessage(msg);
					}

				});

			} catch (Exception e) {
				UtilTools.toast(MapActivity.this, "查询任务失败!" + e.getMessage());
			}
		}
	}

	private class WorkChceckListener implements View.OnClickListener {

		@Override
		public void onClick(View checkbox) {
			CheckBox box = (CheckBox) checkbox;
			if (!box.isChecked()) {
				Builder alert = new AlertDialog.Builder(MapActivity.this);
				alert.setTitle("确认结束任务?");
				alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int i) {
						//mapActivity.cleanOverlay();
						MainService.getInstance().setNowWork(null);
						c_showWork.setChecked(false);
						MainService.getInstance().mYuejie = false;
						c_showWork.setText(UtilTools.getResource(R.string.map_show_work));
						baiduMap.clear();
					}
				});
				alert.setNegativeButton("否", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int i) {
						c_showWork.setChecked(true);
					}
				});
				alert.setCancelable(false);
				alert.show();
			} else {
				c_showWork.setChecked(false);
			}
		}
	}

	private class LayerListener implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			if (BasicMapActivity.VECTOR == MapActivity.this.mapType) {
				showMap(BasicMapActivity.SATELLITE);
				m_btnLayer.setImageDrawable(view.getResources().getDrawable(R.drawable.vector));
			} else {
				showMap(BasicMapActivity.VECTOR);
				m_btnLayer.setImageDrawable(view.getResources().getDrawable(R.drawable.satellite));
			}
		}
	}

	private class WorkRadioSelectListener implements CompoundButton.OnCheckedChangeListener {
		private Long rwid;
		private AlertDialog alert;

		public WorkRadioSelectListener(long rwid, AlertDialog alert) {
			this.rwid = rwid;
			this.alert = alert;
			MainService.getInstance().mYuejie = true;
		}

		@Override
		public void onCheckedChanged(CompoundButton button, boolean is) {
			alert.dismiss();
			MainService.getInstance().setNowWork(this.rwid);
			showWorkArea();
		}

	}

	private class CancelButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			MapActivity.this.finish();
		}
	}

	private void showWorkArea() {
		WorkBean bean = MainService.getInstance().getNowWork();
		c_showWork.setChecked(true);
		c_showWork.setText(UtilTools.getResource(R.string.map_show_work) + bean.getWorkname());
		//mapActivity.cleanOverlay();
		try {
			for (Line l : bean.getLines()) {
				List<Point> points = new ArrayList<Point>();
				String[] pStrs = l.getPoints().split(";");
				for (String pStr : pStrs) {
					String[] ps = pStr.split(",");
					Point p = new Point();
					p.setLatitude(Float.valueOf(ps[1]));
					p.setLongitude(Float.valueOf(ps[0]));
					points.add(p);
				}
				
				addPointLine(points);
			}
			for (Area a : bean.getAreas()) {
				List<Point> points = new ArrayList<Point>();
				String[] pStrs = a.getAreapoints().split(";");
				for (String pStr : pStrs) {
					String[] ps = pStr.split(",");
					Point p = new Point();
					p.setLatitude(Float.valueOf(ps[1]));
					p.setLongitude(Float.valueOf(ps[0]));
					points.add(p);
				}
				addPolygon(points);
			}
			if (bean.getPoints() != null && bean.getPoints().length() > 0) {
				String[] pStrs = bean.getPoints().split(";");
				for (String pStr : pStrs) {
					String[] ps = pStr.split(",");
					Point p = new Point();
					p.setLatitude(Float.valueOf(ps[1]));
					p.setLongitude(Float.valueOf(ps[0]));
					addPoint(p, false);
				}
			}
		} catch (Exception e) {
			UtilTools.toast(MapActivity.this, "显示地图失败:" + e.getMessage());
		}
	}
	public void addPointLine(List<? extends Point> points) {
		List<LatLng> latLngs = new ArrayList<LatLng>();
		for (int i = 0; i < points.size(); i++) {
			
			float lat = points.get(i).getLatitude();
			float log = points.get(i).getLongitude();
			
			LatLng point = new LatLng(lat,log); 
			
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.markers_red);
			MarkerOptions option = new MarkerOptions().position(point).icon(bitmap);
			option.zIndex(3);
			baiduMap.addOverlay(option);
			latLngs.add(point);
		}
		PolylineOptions option = new PolylineOptions();
		option.points(latLngs);
		option.color(R.color.blue);
		option.zIndex(2);
		baiduMap.addOverlay(option);
		LatLng last = latLngs.get(latLngs.size() - 1);
		this.setCenter(last);
	}
	public void addPolygon(List<? extends Point> points) {
		List<LatLng> latLngs = new ArrayList<LatLng>();
		for (int i = 0; i < points.size(); i++) {
			float lat = points.get(i).getLatitude();
			float log = points.get(i).getLongitude();
			
			LatLng point = new LatLng(lat,log); 
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
		baiduMap.addOverlay(option);
		LatLng last = latLngs.get(latLngs.size() - 1);
		this.setCenter(last);
	}
	public void addPoint(Point point, boolean autoRemove) {
		
		
		if (autoRemove && mMarker != null) {
			mMarker.remove();
		}
		float lat = point.getLatitude();
		float log = point.getLongitude();
		
		LatLng latlng = new LatLng(lat,log); 
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_blue);
		MarkerOptions option = new MarkerOptions().position(latlng).icon(bitmap);
		option.zIndex(4);
		mMarker = (Marker) baiduMap.addOverlay(option);
		this.setCenter(latlng);
	}	
	private LatLng convertPoint(Point point) {
//		CoordinateConverter converter = new CoordinateConverter();
//		converter.from(CoordType.GPS);
//		converter.coord(new LatLng(point.getLatitude(), point.getLongitude()));
//		return converter.convert(); 
		LatLng latlng = new LatLng(point.getLatitude(),point.getLatitude());
		return latlng;
	}
	private void setCenter(LatLng last) {
		float z = baiduMap.getMapStatus().zoom;
		// 定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder().target(last).zoom(z > 11 ? z : 11).build();
		// 改变地图状态
		baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
	}
	/**
	 * 打开天地图地图
	 * */
	private void openTiandituMap1(int type) {
		mapLayout.removeAllViews();
		// mapActivity = new BaiduMapActivity();
		mapActivity = new TiandituMap();
		View mapView = mapActivity.getMapView(this, type);
		if (mapView == null) {
			UtilTools.toast(MapActivity.this, "地图初始化失败");
		} else {
			mapLayout.addView(mapView);
		}
	}

	/**
	 * 打开百度地图
	 * */
	private void openBaiduMap(int type) {
		mapLayout.removeAllViews();
		mapActivity = new BaiduMapActivity();
		View mapView = mapActivity.getMapView(this, type);
		if (mapView == null) {
			UtilTools.toast(MapActivity.this, "地图初始化失败");
		} else {
			mapLayout.addView(mapView);
		}
	}

	/**
	 * 打开google地图
	 * */
	public void openGoogleMap() {

	}

	/**
	 * 打开超图地图
	 * */
	public void openSuperMap() {
	}
}
