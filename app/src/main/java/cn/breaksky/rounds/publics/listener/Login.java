package cn.breaksky.rounds.publics.listener;

import org.json.JSONObject;

import cn.breaksky.rounds.publics.bean.RoundPersonnelVO;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.callback.InitSessionThread;
import cn.breaksky.rounds.publics.util.request.callback.SendRetJsonThread;

public class Login {
	private boolean logining = false;
	private LoginCallBack callBack;
	private int init = 1;

	public Login(LoginCallBack callBack) {
		this.callBack = callBack;
		this.initSession();
	}

	private void initSession() {
		try {
			new Request(Request.INIT_SESSION).initSessoin(new InitSessionThread.CallBack() {

				@Override
				public void success() {
					init = 2;
				}

				@Override
				public void exception(String message) {
					init = 0;
				}
			});
		} catch (Exception e) {
			init = 0;
		}
	}

	public void login(final String username, final String password) {
		if (logining || init == 1) {
			return;
		}
		if (init == 0) {
			callBack.faile("初始化失败");
			return;
		}
		if (username.isEmpty()) {
			callBack.faile("设备号为空");
			return;
		}
		logining = true;
		try {
			Request request = new Request(Request.LOGIN);
			Params param = new Params();
			param.addTextParameter("af.devicecode", username);
			param.addTextParameter("af.devicekey", password);
			request.sendRetJson(param, new SendRetJsonThread.CallBack() {

				@Override
				public void callBack(int tag, JSONObject json) {
					try {
						if (json.getBoolean("status")) {
							MainService.getInstance().setUser(UtilTools.jsonToBean(json.getJSONObject("user"), RoundPersonnelVO.class));
							callBack.success();
							
						} else {
							if (json.has("useregister")) {
								callBack.useRegister();
							} else if (json.has("registered")) {
								callBack.registered();
							} else if (json.has("passwroderror")) {
								callBack.passwordError();
							} else {
								callBack.faile(json.getString("message"));
							}
						}
					} catch (Exception e) {
						callBack.faile(e.getMessage());
					}
					logining = false;
				}

				@Override
				public void exception(int tag, Exception e) {
					logining = false;
					callBack.faile(e.getMessage());
				}

			});

		} catch (Exception e) {
			logining = false;
			callBack.faile(e.getMessage());
		}
	}

	public boolean isLogining() {
		return logining;
	}

	public interface LoginCallBack {
		public void success();

		public void passwordError();

		public void faile(String msg);

		public void useRegister();

		public void registered();
	}
}
