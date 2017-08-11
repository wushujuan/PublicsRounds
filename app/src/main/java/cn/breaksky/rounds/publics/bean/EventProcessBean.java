package cn.breaksky.rounds.publics.bean;

public class EventProcessBean {
	private Long rpep_id;
	private Long rp_id;
	private Long rpe_id;
	private String message_type;
	private String message;
	private Long createtime;
	private String name;
	private String devicecode;
	private String mobile;
	private String image;
	private String sex;
	private String type;

	public String getDevicecode() {
		return devicecode;
	}

	public void setDevicecode(String devicecode) {
		this.devicecode = devicecode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getRpep_id() {
		return rpep_id;
	}

	public void setRpep_id(Long rpep_id) {
		this.rpep_id = rpep_id;
	}

	public Long getRp_id() {
		return rp_id;
	}

	public void setRp_id(Long rp_id) {
		this.rp_id = rp_id;
	}

	public Long getRpe_id() {
		return rpe_id;
	}

	public void setRpe_id(Long rpe_id) {
		this.rpe_id = rpe_id;
	}

	public String getMessage_type() {
		return message_type;
	}

	public void setMessage_type(String message_type) {
		this.message_type = message_type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

}
