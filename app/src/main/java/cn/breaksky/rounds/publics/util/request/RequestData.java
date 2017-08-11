package cn.breaksky.rounds.publics.util.request;

import java.util.Map;

public class RequestData {
	private Map<String, Object> data;
	private Object id;

	public RequestData(Object id, Map<String, Object> data) {
		this.data = data;
		this.id = id;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public Object getId() {
		return id;
	}
}
