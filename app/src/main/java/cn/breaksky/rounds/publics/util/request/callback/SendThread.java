package cn.breaksky.rounds.publics.util.request.callback;

import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.RequestLogic;

public class SendThread {
	private RequestLogic requestImpl;
	private Params param;
	private CallBack callBack;

	public SendThread(RequestLogic requestImpl, Params param, CallBack callBack) {
		this.requestImpl = requestImpl;
		this.param = param;
		this.callBack = callBack;
	}

	public void run() {
		try {
			callBack.callBack(requestImpl.send(param));
		} catch (Exception e) {
			callBack.exception(e);
		}
	}

	public static interface CallBack {

		public void callBack(byte[] bytes);

		public void exception(Exception e);
	}
}
