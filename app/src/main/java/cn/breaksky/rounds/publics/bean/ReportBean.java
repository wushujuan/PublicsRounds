package cn.breaksky.rounds.publics.bean;

import java.io.Serializable;

public class ReportBean implements Serializable{
	private static final long serialVersionUID = -3407180785967275874L;

	private Long rpe_id;
	private Float longitude;
	private Float latitude;
	private String address;
	private String context_type;
	private String context;
	private String contact;
	private String phone;
	private Long createtime;
	private String regioncode;
	private String image;
	private String context_file;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getContext_file() {
		return context_file;
	}

	public void setContext_file(String context_file) {
		this.context_file = context_file;
	}

	public Long getRpe_id() {
		return rpe_id;
	}

	public void setRpe_id(Long rpe_id) {
		this.rpe_id = rpe_id;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContext_type() {
		return context_type;
	}

	public void setContext_type(String context_type) {
		this.context_type = context_type;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

	public String getRegioncode() {
		return regioncode;
	}

	public void setRegioncode(String regioncode) {
		this.regioncode = regioncode;
	}

}
