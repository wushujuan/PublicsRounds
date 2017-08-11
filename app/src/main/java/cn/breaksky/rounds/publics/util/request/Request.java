package cn.breaksky.rounds.publics.util.request;

import java.io.OutputStream;

import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.callback.InitSessionThread;
import cn.breaksky.rounds.publics.util.request.callback.SendCheckLoginThread;
import cn.breaksky.rounds.publics.util.request.callback.SendRetJsonThread;
import cn.breaksky.rounds.publics.util.request.callback.SendStreamThread;
import cn.breaksky.rounds.publics.util.request.callback.SendThread;

public class Request {
	public static final int INIT_SESSION = 100;
	public static final int LOGIN = 101;
	public static final int LOGOUT = 104;
	public static final int VERIFY_IMAGE = 102;
	public static final int SYNCH_DATA = 105;
	public static final int READ_FILE = 103;
	public static final int QUERY_PERSONNEL = 106;
	public static final int QUER_WORK = 107;
	public static final int QUER_DANGERFACTOR = 108;
	public static final int SAVE_DANGERFACTOR = 109;
	public static final int QUERY_GATHER_TASK = 110;
	public static final int QUERY_PERSONNEL_MESSAGElIST=111;
	public static final int QUERY_GROUP_lIST=112;
	public static final int PUBLICS_SUBMIT_MESSAGE_READ = 215;

	public static final int VERIFY_MOBILE = 202;
	public static final int REGISTER = 203;
	public static final int QUERY_REGION = 204;
	public static final int SUBMIT_EVENT = 205;
	public static final int QUERY_EVENT = 206;
	public static final int QUERY_EVENT_PROCESS = 207;
	public static final int SUBMIT_EVENT_PROCESS = 208;
	public static final int QUERY_EVENT_FILE = 209;
	public static final int QUERY_MESSAGE = 210;
	public static final int MODIFY_INFO = 211;
	public static final int QUERY_PERSONNEL_IMAGE = 212;
	// private
	private final static String HTTP = "1";
	private final static String SOCKET = "2";
	private int tag;
	private RequestLogic requestImpl;
	private String coding;

	public Request(int tag) throws Exception {
		this.tag = tag;
		this.init(MainService.getInstance().roundsConfig.SOCKET_URL, MainService.getInstance().roundsConfig.SOCKET_PORT);
	}

	public Request(int tag, String url, int port) throws Exception {
		this.tag = tag;
		this.init(url, port);
	}

	private void init(String url, int port) throws Exception {
		if (HTTP.equals(MainService.getInstance().roundsConfig.REQUEST_TYPE)) {
			requestImpl = new HttpPostUtil(this.getHttpUrl(), this.tag);
			this.coding = "UTF-8";
		} else if (SOCKET.equals(MainService.getInstance().roundsConfig.REQUEST_TYPE)) {
			requestImpl = new SocketShortLinkUtil(url, port, this.tag);
			this.coding = "GBK";
		}
	}

	public void send(Params param, SendThread.CallBack callBack) throws Exception {
		RoundHandler.runMethod(new SendThread(this.requestImpl, param, callBack), "run");
	}

	public void sendRetJson(Params param, SendRetJsonThread.CallBack callBack) throws Exception {
		RoundHandler.runMethod(new SendRetJsonThread(this.requestImpl, param, this.coding, callBack), "run");
	}

	public void sendCheckLogin(Params param, SendCheckLoginThread.CallBack callBack) throws Exception {
		RoundHandler.runMethod(new SendCheckLoginThread(this.requestImpl, param, this.coding, callBack), "run");
	}

	public void send(RequestProgress progress, OutputStream readout, Params param, SendStreamThread.CallBack callBack) throws Exception {
		RoundHandler.runMethod(new SendStreamThread(this.requestImpl, param, progress, readout, callBack), "run");
	}

	public void initSessoin(final InitSessionThread.CallBack callBack) throws Exception {
		MainService.getInstance().roundsConfig.SESSION_VALUE = "";
		RoundHandler.runMethod(new InitSessionThread(requestImpl, new InitSessionThread.CallBack() {

			@Override
			public void exception(String message) {
				callBack.exception(message);
			}

			@Override
			public void success() {
				callBack.success();
			}
		}), "run");
	}

	private String getHttpUrl() {
		switch (this.tag) {
		case LOGIN:
			return UtilTools.getURL(R.string.login_url);
		case LOGOUT:
			return UtilTools.getURL(R.string.logout_url);
		case VERIFY_IMAGE:
			return UtilTools.getURL(R.string.login_verify_url);
		case SYNCH_DATA:
			return UtilTools.getURL(R.string.synchronization_url);
		case INIT_SESSION:
			return UtilTools.getURL(R.string.login_verify_url);
		case READ_FILE:
			return UtilTools.getURL(R.string.query_message_file_url);
		case QUER_WORK:
			return UtilTools.getURL(R.string.query_work_url);
		default:
			return null;
		}
	}
}
