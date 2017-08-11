package cn.breaksky.rounds.publics.util.request;

import java.io.OutputStream;

import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;


public class SocketShortLinkUtil implements RequestLogic {
	private TCPSocket tcpSocket;

	public SocketShortLinkUtil(String address, int port, int tag) {
		this.tcpSocket = new TCPSocket(address, port, tag, this.getSessionid());
	}

	private long getSessionid() {
		try {
			return Long.valueOf(MainService.getInstance().roundsConfig.SESSION_VALUE);
		} catch (Exception e) {
			return 0l;
		}
	}

	@Override
	public byte[] send(Params param) throws Exception {
		return tcpSocket.send(param.getParamByte());
	}

	@Override
	public void send(RequestProgress progress, OutputStream readout, Params param) throws Exception {
		tcpSocket.send(progress, readout, param.getParamByte());
	}

	@Override
	public String getInitSessionID() throws Exception {
		byte[] b = this.send(new Params());
		if (b == null || b.length < 1) {
			return "";
		} else {
			return String.valueOf(UtilTools.bytesToLong(b));
		}
	}

	@Override
	public int getResponseTag() {
		return tcpSocket.getTag();
	}

}
