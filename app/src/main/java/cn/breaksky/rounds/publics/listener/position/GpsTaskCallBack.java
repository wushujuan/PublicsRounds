package cn.breaksky.rounds.publics.listener.position;

import cn.breaksky.rounds.publics.listener.position.GpsTask.GpsData;

public interface GpsTaskCallBack {

	public void gpsConnected(GpsData gpsdata);
	
	public void gpsConnectedTimeOut();
	
}
