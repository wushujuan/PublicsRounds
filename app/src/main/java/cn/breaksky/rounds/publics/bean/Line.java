package cn.breaksky.rounds.publics.bean;


public class Line {
	private Long rl_id;
	private String name;
	private Long createtime;
	private Float distance;
	private String points;

	public Long getRl_id() {
		return rl_id;
	}

	public void setRl_id(Long rl_id) {
		this.rl_id = rl_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}
}
