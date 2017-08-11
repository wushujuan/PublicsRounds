package cn.breaksky.rounds.publics.bean;

import java.io.Serializable;

public class RoundPersonnelVO implements Serializable {
	private static final long serialVersionUID = -3407180785967275874L;

	private Long rp_id;
	private String name;
	private String sex;
	private Integer age;
	private String mobile;
	private String rr_code;
	private String status;
	private String type;
	private String devicecode;
	private String devicekey;
	private String idcard;
	private String address;
	
	private String groupid;
	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDevicekey() {
		return devicekey;
	}

	public void setDevicekey(String devicekey) {
		this.devicekey = devicekey;
	}

	public Long getRp_id() {
		return rp_id;
	}

	public void setRp_id(Long rp_id) {
		this.rp_id = rp_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRr_code() {
		return rr_code;
	}

	public void setRr_code(String rr_code) {
		this.rr_code = rr_code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDevicecode() {
		return devicecode;
	}

	public void setDevicecode(String devicecode) {
		this.devicecode = devicecode;
	}

}
