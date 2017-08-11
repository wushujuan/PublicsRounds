package cn.breaksky.rounds.publics.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import cn.breaksky.rounds.publics.R;


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
import com.baidu.mapapi.model.LatLng;

public class MapShowActivity extends Activity {
	public MapView mapView = null;
	public BaiduMap baiduMap = null;
	private Button cancelButton;//取消按钮
	

	
	//自定义图标
	BitmapDescriptor mCurrentMarker = null;
	
	private Marker mMarker;//标注
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_showlocation);
		
		cancelButton = (Button) findViewById(R.id.showlocation_cancel_button);
		
		cancelButton.setOnClickListener(new CancelButtonOnClick());
		
		
		mapView = (MapView) this.findViewById(R.id.showlocation_mapView); // 获取地图控件引用
		baiduMap = mapView.getMap();
		//开启定位图层
		//baiduMap.setMyLocationEnabled(true);
		
		
//		locationClient = new LocationClient(getApplicationContext()); // 实例化LocationClient类
//		locationClient.registerLocationListener(myListener); // 注册监听函数
		//this.setLocationOption();	//设置定位参数
		//locationClient.start(); // 开始定位
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			float lat = extras.getFloat("lat", 0f);
			float log = extras.getFloat("log", 0f);
			
			
				if (lat > 0 && log > 0) {
					 LatLng cenpt = new LatLng(lat,log); 
				        //定义地图状态
				        MapStatus mMapStatus = new MapStatus.Builder()
				        .target(cenpt)
				        .zoom(18)
				        .build();
				        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化


				        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
				        //改变地图状态
				        baiduMap.setMapStatus(mMapStatusUpdate);
				        addPoint(cenpt,false);
				}
				
		}
	}
	public void addPoint(LatLng latlng, boolean autoRemove) {
		if (autoRemove && mMarker != null) {
			mMarker.remove();
		}
		
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_blue);
		MarkerOptions option = new MarkerOptions().position(latlng).icon(bitmap);
		option.zIndex(4);
		mMarker = (Marker) baiduMap.addOverlay(option);
		this.setCenter(latlng);
	}	
	
	private void setCenter(LatLng last) {
		float z = baiduMap.getMapStatus().zoom;
		// 定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder().target(last).zoom(z > 11 ? z : 11).build();
		// 改变地图状态
		baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
	}
	// 三个状态实现地图生命周期管理
	@Override
	protected void onDestroy() {
		//退出时销毁定位
		
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

	

	
	private class CancelButtonOnClick implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			MapShowActivity.this.finish();
		}
	}		
}