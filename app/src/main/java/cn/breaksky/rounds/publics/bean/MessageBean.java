package cn.breaksky.rounds.publics.bean;

public class MessageBean {
	private java.lang.Long rm_id;
	private java.lang.Long send_id;
	private java.lang.Long recive_id;
	private java.lang.String message;
	private java.lang.String type;
	private java.lang.String objid;
	private String sendtime;
	private String recivetime;
	private String photoCode;
	private Long fileid;
	private String fileName;
	private Float latitude;
	private Float longitude;

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public java.lang.Long getRm_id() {
		return rm_id;
	}

	public void setRm_id(java.lang.Long rm_id) {
		this.rm_id = rm_id;
	}

	public java.lang.Long getSend_id() {
		return send_id;
	}

	public void setSend_id(java.lang.Long send_id) {
		this.send_id = send_id;
	}

	public java.lang.Long getRecive_id() {
		return recive_id;
	}

	public void setRecive_id(java.lang.Long recive_id) {
		this.recive_id = recive_id;
	}

	public java.lang.String getMessage() {
		return message;
	}

	public void setMessage(java.lang.String message) {
		this.message = message;
	}

	public java.lang.String getType() {
		return type;
	}

	public void setType(java.lang.String type) {
		this.type = type;
	}

	public java.lang.String getObjid() {
		return objid;
	}

	public void setObjid(java.lang.String objid) {
		this.objid = objid;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getRecivetime() {
		return recivetime;
	}

	public void setRecivetime(String recivetime) {
		this.recivetime = recivetime;
	}

	public String getPhotoCode() {
		return photoCode;
	}

	public void setPhotoCode(String photoCode) {
		this.photoCode = photoCode;
	}

	public Long getFileid() {
		return fileid;
	}

	public void setFileid(Long fileid) {
		this.fileid = fileid;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
