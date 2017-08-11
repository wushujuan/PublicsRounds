package cn.breaksky.rounds.publics.bean;

import java.io.Serializable;

public class PublicMessageBean implements Serializable{
	private Long rpm_id;
	private String title;
	private String color;
	private String context;
	private Long publicstime;
	private String publicsuser;

	public Long getRpm_id() {
		return rpm_id;
	}

	public void setRpm_id(Long rpm_id) {
		this.rpm_id = rpm_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Long getPublicstime() {
		return publicstime;
	}

	public void setPublicstime(Long publicstime) {
		this.publicstime = publicstime;
	}

	public String getPublicsuser() {
		return publicsuser;
	}

	public void setPublicsuser(String publicsuser) {
		this.publicsuser = publicsuser;
	}

}
