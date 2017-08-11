package cn.breaksky.rounds.publics.listener;

import java.io.File;
import java.util.Date;

public class MessageObj {

	private MessageCallBack callBack;
	private MESSAGE_TYPE type;
	private String message;
	private String reviceid;
	private String fileName;
	private File file;
	private Date sendTime;
	private String sendid;
	private Integer messageid;
	private Long serviceid;
	private Float latitude;
	private Float longitude;

	/**
	 * 文本消息
	 * */
	public MessageObj(String message, String reviceid, String sendid, Date sendTime) {
		this.message = message;
		this.reviceid = reviceid;
		this.sendTime = sendTime;
		this.sendid = sendid;
		this.type = MESSAGE_TYPE.TEXT;
	}

	/**
	 * 文件消息
	 * */
	public MessageObj(File file, MESSAGE_TYPE type, String reviceid, String sendid, Date sendTime) {
		this.fileName = file.getName();
		this.file = file;
		this.type = type;
		this.reviceid = reviceid;
		this.sendTime = sendTime;
		this.sendid = sendid;
	}

	/**
	 * 坐标消息
	 * */
	public MessageObj(Float latitude, Float longitude, String reviceid, String sendid, Date sendTime) {
		this.message = "坐标";
		this.reviceid = reviceid;
		this.sendTime = sendTime;
		this.sendid = sendid;
		this.type = MESSAGE_TYPE.COORDINATE;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * 调度消息
	 * */
	public MessageObj(Float latitude, Float longitude, String reviceid, String sendid, Date sendTime, String message) {
		this.message = message;
		this.reviceid = reviceid;
		this.sendTime = sendTime;
		this.sendid = sendid;
		this.type = MESSAGE_TYPE.DISPATCH;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public void setCallBack(MessageCallBack callBack) {
		this.callBack = callBack;
	}

	public enum MESSAGE_TYPE {
		/**
		 * 文本消息,
		 * */
		TEXT {
			public String toString() {
				return "1";
			}
		},
		/**
		 * 语音,
		 * */
		VOICE {
			public String toString() {
				return "2";
			}
		},
		/**
		 * 图片,
		 * */
		PHOTO {
			public String toString() {
				return "3";
			}
		},
		/**
		 * 视频,
		 * */
		VIDEO {
			public String toString() {
				return "5";
			}
		},
		/**
		 * 调度任务,
		 * */
		DISPATCH {
			public String toString() {
				return "6";
			}
		},
		/**
		 * 预案
		 * */
		PLAN {
			public String toString() {
				return "9";
			}
		},
		/**
		 * 坐标
		 * */
		COORDINATE {
			public String toString() {
				return "10";
			}
		},
		/**
		 * 线
		 * */
		LINE {
			public String toString() {
				return "11";
			}
		},
		/**
		 * 文件
		 * */
		FILE {
			public String toString() {
				return "12";
			}
		}
	}

	public enum MESSAGE_STATUS {
		SUCCESS {
			public String toString() {
				return "1";
			}
		},
		FAILE {
			public String toString() {
				return "2";
			}
		},
		WATI {
			public String toString() {
				return "3";
			}
		}
	}

	public Long getServiceid() {
		return serviceid;
	}

	public void setServiceid(Long serviceid) {
		this.serviceid = serviceid;
	}

	public MessageCallBack getCallBack() {
		return callBack;
	}

	public MESSAGE_TYPE getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public String getReviceid() {
		return reviceid;
	}

	public String getFileName() {
		return fileName;
	}

	public File getFile() {
		return file;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public String getSendid() {
		return sendid;
	}

	public Integer getMessageid() {
		return messageid;
	}

	public void setMessageid(Integer messageid) {
		this.messageid = messageid;
	}

	public Float getLatitude() {
		return latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

}
