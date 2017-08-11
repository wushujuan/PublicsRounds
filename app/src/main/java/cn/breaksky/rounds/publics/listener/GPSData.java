package cn.breaksky.rounds.publics.listener;

import java.util.Date;

public class GPSData {
	private Double latitude;
	private Double longitude;
	private Double altitude;
	private Date time;

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Date getTime() {
		return time;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

}
