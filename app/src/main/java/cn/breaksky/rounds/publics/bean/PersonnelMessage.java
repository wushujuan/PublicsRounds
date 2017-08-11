package cn.breaksky.rounds.publics.bean;


public class PersonnelMessage {
	
	private Long rpid;
	
	private String personname;
	
	private String messagetime;
	
	private String messagetype;
	
	private String  messagecontent;

	public String getPersonname() {
		return personname;
	}

	public void setPersonname(String personname) {
		this.personname = personname;
	}

	public String getMessagetime() {
		return messagetime;
	}

	public void setMessagetime(String messagetime) {
		this.messagetime = messagetime;
	}

	public String getMessagetype() {
		return messagetype;
	}

	public void setMessagetype(String messagetype) {
		this.messagetype = messagetype;
	}

	public String getMessagecontent() {
		return messagecontent;
	}

	public void setMessagecontent(String messagecontent) {
		this.messagecontent = messagecontent;
	}

	public Long getRpid() {
		return rpid;
	}

	public void setRpid(Long rpid) {
		this.rpid = rpid;
	}

}

