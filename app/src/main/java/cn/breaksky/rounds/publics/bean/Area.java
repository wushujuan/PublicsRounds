package cn.breaksky.rounds.publics.bean;

import cn.breaksky.rounds.publics.map.Point;

public class Area {
	private Long ra_id;
	private String name;
	private String areapoints;
	private Float area;
	private Long createtime;
	private Point[] poly;

	public boolean isArea(Point pt) {
		if (poly == null) {
			return false;
		}
		int i, j;
		boolean c = false;
		for (i = 0, j = poly.length - 1; i < poly.length; j = i++) {
			if ((((poly[i].getLatitude() <= pt.getLatitude()) && (pt.getLatitude() < poly[j].getLatitude())) || ((poly[j].getLatitude() <= pt.getLatitude()) && (pt.getLatitude() < poly[i]
					.getLatitude())))
					&& (pt.getLongitude() < (poly[j].getLongitude() - poly[i].getLongitude()) * (pt.getLatitude() - poly[i].getLatitude())
							/ (poly[j].getLatitude() - poly[i].getLatitude()) + poly[i].getLongitude())) {
				c = !c;
			}
		}
		return c;
	}

	public Long getRa_id() {
		return ra_id;
	}

	public void setRa_id(Long ra_id) {
		this.ra_id = ra_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAreapoints() {
		return areapoints;
	}

	public void setAreapoints(String areapoints) {
		this.areapoints = areapoints;
		String[] pStrs = areapoints.split(";");
		this.poly = new Point[pStrs.length];
		for (int i = 0; i < pStrs.length; i++) {
			String[] ps = pStrs[i].split(",");
			Point p = new Point();
			p.setLatitude(Float.valueOf(ps[1]));
			p.setLongitude(Float.valueOf(ps[0]));
			this.poly[i] = p;
		}
	}

	public Float getArea() {
		return area;
	}

	public void setArea(Float area) {
		this.area = area;
	}

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

}
