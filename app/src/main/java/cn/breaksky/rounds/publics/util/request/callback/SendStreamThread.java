package cn.breaksky.rounds.publics.util.request.callback;

import java.io.OutputStream;

import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.RequestLogic;
import cn.breaksky.rounds.publics.util.request.RequestProgress;

public class SendStreamThread {
	private RequestLogic requestImpl;
	private Params param;
	private CallBack callBack;
	private RequestProgress progress;
	OutputStream readout;

	public SendStreamThread(RequestLogic requestImpl, Params param, RequestProgress progress, OutputStream readout, CallBack callBack) {
		this.requestImpl = requestImpl;
		this.param = param;
		this.progress = progress;
		this.readout = readout;
		this.callBack = callBack;
	}

	public void run() {
		try {
			requestImpl.send(progress, readout, param);
			callBack.complete();
		} catch (Exception e) {
			callBack.exception(e);
		}
	}

	public static interface CallBack {

		public void complete();

		public void exception(Exception e);
	}
}
