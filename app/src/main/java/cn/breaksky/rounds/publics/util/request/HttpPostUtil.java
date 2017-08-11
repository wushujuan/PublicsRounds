package cn.breaksky.rounds.publics.util.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import cn.breaksky.rounds.publics.services.MainService;

public class HttpPostUtil implements RequestLogic {
	private URL url;
	private HttpURLConnection conn;
	private String boundary = "-------------httppost1234567890abc";
	private int tag;

	public HttpPostUtil(String url, int tag) throws Exception {
		this.url = new URL(url);
		this.tag = tag;
	}

	public byte[] send(Params param) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.send(null, out, param);
		byte[] b = out.toByteArray();
		out.flush();
		out.close();
		return b;
	}

	/**
	 * 发送数据到服务器
	 */
	public void send(RequestProgress progress, OutputStream readout, Params param) throws Exception {
		initConnection();
		try {
			conn.connect();
		} catch (SocketTimeoutException e) {
			throw new RuntimeException();
		}
		// 发送
		DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
		ByteArrayInputStream paramIn = new ByteArrayInputStream(param.getHttpParams(boundary));
		long sendLength = paramIn.available();
		byte[] sendbuf = new byte[1024];
		long sendCount = 0;
		int sb = 0;
		while ((sb = paramIn.read(sendbuf)) != -1) {
			sendCount = sendCount + sb;
			MainService.getInstance().roundsConfig.SEND_LENGTH = MainService.getInstance().roundsConfig.SEND_LENGTH + sb;
			ds.write(sendbuf);
			if (progress != null) {
				progress.sendProgress(sendLength, sendCount);
				if (progress.haveCancel()) {
					conn.disconnect();
					return;
				}
			}
		}
		if (progress != null) {
			progress.sendComplete(sendLength);
		}
		// 接收
		InputStream in = conn.getInputStream();
		int readLength = conn.getContentLength();
		int b = 0;
		int sunRead = 0;
		while (b != -1) {
			int read = in.available();
			if (progress != null) {
				if (progress.haveCancel()) {
					conn.disconnect();
					return;
				}
				sunRead = sunRead + read;
				progress.readProgress(readLength, sunRead);
			}
			byte[] readbuf = new byte[read];
			b = in.read(readbuf);
			readout.write(readbuf);
			MainService.getInstance().roundsConfig.READ_LENGTH = MainService.getInstance().roundsConfig.READ_LENGTH + read;
		}
		if (progress != null) {
			progress.readComplete(readLength);
		}
		conn.disconnect();
	}

	// 文件上传的connection的一些必须设置
	private void initConnection() throws Exception {
		conn = (HttpURLConnection) this.url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3000);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		conn.setRequestProperty("Cookie", MainService.getInstance().roundsConfig.SESSION_VALUE);
	}

	@Override
	public String getInitSessionID() throws Exception {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		String value = conn.getHeaderField("Set-Cookie");
		if (value == null) {
			return "";
		} else {
			return value.split(";")[0];
		}
	}

	@Override
	public int getResponseTag() {
		return this.tag;
	}
}
