package cn.breaksky.rounds.publics.util.request.callback;

import cn.breaksky.rounds.publics.util.request.TCPSocket;

public class TCPSocketSendThread {
	private TCPSocket socket;
	private byte[] data;
	private CallBack callBack;

	public TCPSocketSendThread(TCPSocket socket, byte[] data, CallBack callBack) {
		this.socket = socket;
		this.data = data;
		this.callBack = callBack;
	}

	public void run() {
		try {
			if (this.data != null) {
				this.callBack.returnByte(socket.send(this.data));
			} else {
				throw new Exception("Data IS Null");
			}
		} catch (Exception e) {
			this.callBack.exception(e.toString());
		}
	}

	public static interface CallBack {
		public void returnByte(byte[] data);

		public void exception(String message);
	}
}
