package cn.breaksky.rounds.publics.util.request.callback;

import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.request.RequestLogic;

public class InitSessionThread {
	private RequestLogic requestImpl;
	private CallBack callBack;

	public InitSessionThread(RequestLogic requestImpl, CallBack callBack) {
		this.requestImpl = requestImpl;
		this.callBack = callBack;
	}

	public void run() {
		try {
			MainService.getInstance().roundsConfig.SESSION_VALUE = requestImpl.getInitSessionID();
			callBack.success();
		} catch (Exception e) {
			callBack.exception(e == null ? "null" : e.getMessage());
		}
	}

	public static interface CallBack {
		public void exception(String message);

		public void success();
	}
}
