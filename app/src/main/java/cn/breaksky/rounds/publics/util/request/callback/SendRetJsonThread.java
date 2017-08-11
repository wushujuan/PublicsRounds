package cn.breaksky.rounds.publics.util.request.callback;

import org.json.JSONObject;

import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.RequestLogic;

public class SendRetJsonThread {
	private RequestLogic requestImpl;
	private Params param;
	private CallBack callBack;
	private String coding;

	public SendRetJsonThread(RequestLogic requestImpl, Params param, String coding, CallBack callBack) {
		this.requestImpl = requestImpl;
		this.param = param;
		this.callBack = callBack;
		this.coding = coding;
	}

	public void run() {
		try {
			final byte[] b = requestImpl.send(param);
			if (b == null || b.length < 1) {
				throw new Exception("无数据返回");
			} else {
				callBack.callBack(requestImpl.getResponseTag(), UtilTools.byteToJsonCoding(b, coding));
			}
		} catch (final Exception e) {
			callBack.exception(requestImpl.getResponseTag(), e);
		}
	}

	public static interface CallBack {

		public void callBack(int tag, JSONObject json);

		public void exception(int tag, Exception e);
	}

}
