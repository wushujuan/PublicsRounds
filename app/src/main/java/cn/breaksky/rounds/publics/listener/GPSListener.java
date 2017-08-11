package cn.breaksky.rounds.publics.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.content.Context;
import cn.breaksky.rounds.publics.bean.Area;
import cn.breaksky.rounds.publics.bean.WorkBean;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.listener.position.GpsTask;
import cn.breaksky.rounds.publics.listener.position.GpsTask.GpsData;
import cn.breaksky.rounds.publics.listener.position.GpsTaskCallBack;
import cn.breaksky.rounds.publics.map.Point;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.RequestData;

public class GPSListener  {
	private static List<GPSData> gpsDatas = new ArrayList<GPSData>();
	private static GPSData last_gpsData;
	private static Context context;
	// 定位相关声明
		public LocationClient locationClient = null;
	
	public BDLocationListener myListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
		
			GPSData mygpsData=new GPSData();
			mygpsData.setLatitude(location.getLatitude());
			mygpsData.setLongitude(location.getLongitude());
			mygpsData.setAltitude(location.getAltitude());
			mygpsData.setTime(new Date());
			
			 gpsDatas.add(mygpsData);
				last_gpsData = mygpsData;
		}
	};

	public GPSListener(Context activity) {
		context = activity;
		locationClient = new LocationClient(activity); // 实例化LocationClient类
		locationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPS
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
		
		locationClient.setLocOption(option);
		locationClient.start(); // 开始定位

	}

/*	public void beginGpsTask() {
		locationClient.start(); // 开始定位
		RoundHandler.runMethod(this, 5*1000, "beginGpsTask");
	}*/

	/**
	 * 获取需要上传的坐标
	 * */
	public static RequestData getNeedSendGPS() {
		Map<String, Object> gpsdata = new HashMap<String, Object>();
		if (gpsDatas.size() > 0) {
			List<GPSData> gps = new ArrayList<GPSData>(gpsDatas);
			gpsDatas.clear();

			StringBuffer latitude = new StringBuffer();
			StringBuffer longitude = new StringBuffer();
			StringBuffer altitude = new StringBuffer();
			StringBuffer time = new StringBuffer();
			StringBuffer cross = new StringBuffer();
			StringBuffer crossarea = new StringBuffer();
			for (GPSData data : gps) {
				latitude.append(data.getLatitude());
				latitude.append(",");

				longitude.append(data.getLongitude());
				longitude.append(",");

				altitude.append(data.getAltitude());
				altitude.append(",");

				time.append(data.getTime().getTime());
				time.append(",");

				WorkBean work = MainService.getInstance().getNowWork();
				if (work != null) {
					String c = "0";
					String ca = "0";
					for (Area area : work.getAreas()) {
						Point pt = new Point();
						pt.setLatitude(data.getLatitude().floatValue());
						pt.setLongitude(data.getLongitude().floatValue());
						if (area.isArea(pt)) {
							c = "0";
							ca = "0";
							break;
						} else {
							c = "1";
							ca = String.valueOf(area.getRa_id());
						}
					}
					if (!c.equals("0")||!MainService.getInstance().mYuejie) {
						UtilTools.toast(context, "已越界");
						UtilTools.beginVibrator();
						// UtilTools.talkText("注意，您已越界");
					}
					cross.append(c);
					cross.append(",");
					crossarea.append(ca);
					crossarea.append(",");
				} else {
					cross.append("0");
					cross.append(",");
					crossarea.append("0");
					crossarea.append(",");
				}
			}
			gpsdata.put("af.gps_latitude", latitude.substring(0, latitude.length() - 1));
			gpsdata.put("af.gps_longitude", longitude.substring(0, longitude.length() - 1));
			gpsdata.put("af.gps_altitude", altitude.substring(0, altitude.length() - 1));
			gpsdata.put("af.gps_date", time.substring(0, time.length() - 1));
			gpsdata.put("af.gps_cross", cross.substring(0, cross.length() - 1));
			gpsdata.put("af.gps_crossarea", crossarea.substring(0, crossarea.length() - 1));
			if (MainService.getInstance().getNowWork() != null) {
				gpsdata.put("af.gps_workid", MainService.getInstance().getNowWork().getRw_id().toString());
			}
		}
		return new RequestData(null, gpsdata);
	}

	public List<GPSData> getGPSData() {
		return gpsDatas;
	}

	public GPSData getLashGPSData() {
		return last_gpsData;
	}

	

	public static void setLast_gpsData(GPSData last_gpsData) {
		GPSListener.last_gpsData = last_gpsData;
	}
	
}
