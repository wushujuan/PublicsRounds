package cn.breaksky.rounds.publics.handler;


public class ObjectBean {
	public Object activity;
	public String method;
	public HandlerCallBack callBack;

	public ObjectBean(Object activity, String method) {
		this.activity = activity;
		this.method = method;
	}

	public ObjectBean(Object activity, String method, HandlerCallBack callBack) {
		this.activity = activity;
		this.method = method;
		this.callBack = callBack;
	}

}
