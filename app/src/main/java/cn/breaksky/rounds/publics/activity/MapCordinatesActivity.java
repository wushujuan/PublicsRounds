package cn.breaksky.rounds.publics.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import cn.breaksky.rounds.publics.R;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class MapCordinatesActivity extends Activity {

	/** <b>取消按钮</b> */
	private Button cancelButton;

	/** <b>确认按钮</b> */
	private Button submitButton;

	/** <b>复位按钮</b> */
	private ImageButton resetButton;

	/** <b>移动后的纬度</b> */
	private double latitude;

	/** <b>移动后的经度</b> */
	private double longitude;

	/** <b>移动后的地址</b> */
	private String address;
	
	/** <b>定位相关声明</b> */
	private LocationClient locationClient;

	/** <b>定位位置（自己所处位置）纬度</b> */
	private double lat;
	
	/** <b>定位位置（自己所处位置）经度</b> */
	private double lng;
	
	/** <b>定位位置（自己所处位置）地址</b> */
	private String address1;
	
	/** <b>是否首次定位</b> */
	private boolean isFirstLoc = true;
	
	/** <b>是否拖拽定位图标</b> */
	private boolean locationChangeFlag = false;
	private MapView mapView;
	private BaiduMap baiduMap;

	/** <b>定位监听事件</b> */
	private BDLocationListener myListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			
			// map view 销毁后不在处理新接收的位置
			if (location == null || mapView == null){
				Log.d("myListener", "地图定位失败！");
				return;
			}
			
			//定位用户地理位置
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData); // 设置定位数据
			lat = location.getLatitude();
			lng = location.getLongitude();
			address1 = location.getAddrStr();

			//首次定位才会执行地图更新
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16); // 设置地图中心点以及缩放级别
				baiduMap.animateMapStatus(u);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_location);

		cancelButton = (Button) findViewById(R.id.map_cordinates_cancel_button);
		cancelButton.setOnClickListener(new CancelButtonOnClick());

		submitButton = (Button) findViewById(R.id.map_cordinates_submit_button);
		submitButton.setOnClickListener(new SubmitButtonOnClick());

		resetButton = (ImageButton) findViewById(R.id.reset_button);
		resetButton.setOnClickListener(new ResetButtonOnClick());

		mapView = (MapView) this.findViewById(R.id.mapView); // 获取地图控件引用
		baiduMap = mapView.getMap();
		baiduMap.setMyLocationEnabled(true); // 开启定位图层

		locationClient = new LocationClient(getApplicationContext()); // 实例化LocationClient类
		locationClient.registerLocationListener(myListener); // 注册监听函数
		this.setLocationOption(); // 设置定位参数
		locationClient.start(); // 开始定位
		this.setOnMapStatusChangeListener();	//拖拽地图时监听地图的状态
	}

	// 三个状态实现地图生命周期管理
	@Override
	protected void onDestroy() {
		locationClient.stop();
		baiduMap.setMyLocationEnabled(false);
		mapView.onDestroy();
		baiduMap = null;
		mapView = null;
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 设置定位参数
	 */
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPS
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 设置定位模式
		option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
		locationClient.setLocOption(option);
	}

	/**
	 * 地图监听器：拖拽地图时监听地图的状态
	 */
	private void setOnMapStatusChangeListener() {
		baiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			
			@Override
			public void onMapStatusChangeStart(MapStatus status) {
				// updateMapState(status);
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus status) {
				updateMapState(status);
				locationChangeFlag = true;
			}

			@Override
			public void onMapStatusChange(MapStatus status) {
				// updateMapState(status);
			}
		});
	}

	/**
	 * 更新地图的地理信息
	 * @param status
	 */
	private void updateMapState(MapStatus status) {
		LatLng mCenterLatLng = status.target;
		
		//获取经纬度
		latitude = mCenterLatLng.latitude;
		longitude = mCenterLatLng.longitude;
		Log.d("实际纬度", "纬度值：" + latitude);
		Log.d("实际经度", "经度值：" + longitude);
		
		// 设置反地理编码位置坐标
		ReverseGeoCodeOption op = new ReverseGeoCodeOption();
		op.location(mCenterLatLng);
		
		// 实例化一个地理编码查询对象并发起反地理编码请求(经纬度->地址信息)
		GeoCoder geoCoder = GeoCoder.newInstance();
		geoCoder.reverseGeoCode(op);
		geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
				address = arg0.getAddress();	// 获取点击的坐标地址
				Log.d("address", "address：" + address);
			}
			@Override
			public void onGetGeoCodeResult(GeoCodeResult arg0) {
			}
		});
	}

	private class SubmitButtonOnClick implements View.OnClickListener {

		/*
		 * 返回参数到EventActivity
		 * 
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View view) {
			Intent data = new Intent();
			Bundle extras = new Bundle();
			if (locationChangeFlag) {	//拖拽后的地理位置
				extras.putDouble("latitude", latitude);
				extras.putDouble("longitude", longitude);
				extras.putString("address", address);
				data.putExtra("point", extras);
			}
			if (!locationChangeFlag) {	//用户的定位位置
				extras.putDouble("latitude", lat);
				extras.putDouble("longitude", lng);
				extras.putString("address", address1);
				data.putExtra("point", extras);
			}
			setResult(RESULT_OK, data);
			MapCordinatesActivity.this.finish();
		}
	}

	private class CancelButtonOnClick implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			MapCordinatesActivity.this.finish();
		}
	}

	private class ResetButtonOnClick implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			LatLng ll = new LatLng(lat, lng);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16); // 设置地图中心点以及缩放级别
			// MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			baiduMap.animateMapStatus(u);
		}
	}
}