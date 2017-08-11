package cn.breaksky.rounds.publics.util.request.callback;

import org.json.JSONObject;

import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.RequestLogic;

public class SendCheckLoginThread {
	private RequestLogic requestImpl;
	private Params param;
	private CallBack callBack;
	private String coding;

	public SendCheckLoginThread(RequestLogic requestImpl, Params param, String coding, CallBack callBack) {
		this.requestImpl = requestImpl;
		this.param = param;
		this.callBack = callBack;
		this.coding = coding;
	}

	public void run() {
		try {
			JSONObject json = UtilTools.byteToJsonCoding(requestImpl.send(param), coding);
			if (json.has("sessionout")) {
				MainService.getInstance().roundsConfig.loginSucess = false;
			}
			callBack.callBack(json);
		} catch (Exception e) {
			MainService.getInstance().roundsConfig.loginSucess = false;
			callBack.exception(e);
		}
	}

	public static interface CallBack {

		public void callBack(JSONObject json);

		public void exception(Exception e);
	}
}
