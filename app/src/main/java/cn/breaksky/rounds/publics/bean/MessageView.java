package cn.breaksky.rounds.publics.bean;

import java.util.Date;

import cn.breaksky.rounds.publics.listener.MessageObj.MESSAGE_TYPE;

public class MessageView {
	public int id;
	public boolean left;
	public MESSAGE_TYPE type;
	public String str;
	public Date sendtime;
	public Long rpid;

	public MessageView(int id, Long rpid, boolean left, MESSAGE_TYPE messageType, String str, Date sendtime) {
		this.id = id;
		this.left = left;
		this.type = messageType;
		this.str = str;
		this.sendtime = sendtime;
		this.rpid = rpid;
	}
}
