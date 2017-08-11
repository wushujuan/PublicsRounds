package cn.breaksky.rounds.publics.util.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.RoundsConfig;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;

public class TCPSocket {
	private int port;
	private String address;
	private int tag;
	private long sessionid;

	public TCPSocket(String address, int port, int tag, long sessionid) {
		this.port = port;
		this.address = address;
		this.tag = tag;
		this.sessionid = sessionid;
	}

	private Socket connect() throws Exception {
		Socket SOCKET = new Socket();
		SocketAddress socAddress = new InetSocketAddress(this.address, this.port);
		SOCKET.connect(socAddress, 5000);
		return SOCKET;
	}

	public byte[] send(byte[] bytes) throws Exception {
		Socket socket = this.connect();
		try {
			byte[] b = this.getByte(bytes);
			MainService.getInstance().roundsConfig.SEND_LENGTH = MainService.getInstance().roundsConfig.SEND_LENGTH + b.length;
			socket.getOutputStream().write(b);
		} catch (IOException e) {
			this.close(socket);
			throw new Exception(UtilTools.getResource(R.string.TCPSocket_send));
		}
		ByteArrayOutputStream rout = new ByteArrayOutputStream();
		this.readBuiness(socket.getInputStream(), rout);
		byte[] retByte = rout.toByteArray();
		rout.close();
		this.close(socket);
		return retByte;
	}

	public void send(RequestProgress progress, OutputStream readout, byte[] bytes) throws Exception {
		Socket socket = this.connect();
		byte[] b = this.getByte(bytes);
		MainService.getInstance().roundsConfig.SEND_LENGTH = MainService.getInstance().roundsConfig.SEND_LENGTH + b.length;
		socket.getOutputStream().write(b);
		this.readBuiness(socket.getInputStream(), readout);
		this.close(socket);
	}

	private byte[] getByte(byte[] bytes) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(out);
		gos.write(bytes);
		gos.finish();
		try {
			gos.close();
		} catch (Exception e) {

		}
		byte[] outByte = out.toByteArray();
		out.close();

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CRC32 crc32 = new CRC32();
		crc32.update(outByte);
		bout.write(UtilTools.intToBytes(tag));// tag
		bout.write(UtilTools.intToBytes(outByte.length));// length
		bout.write(UtilTools.longToBytes(crc32.getValue()));// crc32
		bout.write(UtilTools.longToBytes(this.sessionid));// sessionid
		bout.write(outByte);
		byte[] bytes2 = bout.toByteArray();
		bout.close();
		return bytes2;
	}

	private static final int HEAD_LEN = 16;

	private void readBuiness(InputStream in, OutputStream readout) throws Exception {
		boolean end = false;
		int tag = -1;
		int length = 0;
		long crcValue = 0l;
		int len = 1;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while (!end) {
			byte[] b = new byte[len];
			if (in.read(b) == -1) {
				break;
			}
			len = in.available();
			MainService.getInstance().roundsConfig.READ_LENGTH = MainService.getInstance().roundsConfig.READ_LENGTH + b.length;
			out.write(b);
			if (tag == -1) {
				if (out.size() < HEAD_LEN) {
					continue;
				}
				byte[] buffer = out.toByteArray();
				out.reset();
				byte[] temp = new byte[4];
				System.arraycopy(buffer, 0, temp, 0, 4);
				tag = UtilTools.bytesToInt(temp);
				temp = new byte[4];
				System.arraycopy(buffer, 4, temp, 0, 4);
				length = UtilTools.bytesToInt(temp);
				temp = new byte[8];
				System.arraycopy(buffer, 8, temp, 0, 8);
				crcValue = UtilTools.bytesToLong(temp);
				if (buffer.length > HEAD_LEN) {
					temp = new byte[buffer.length - HEAD_LEN];
					System.arraycopy(buffer, 16, temp, 0, temp.length);
					out.write(temp);
				}
			} else {
				if (out.size() >= length) {
					end = true;
				}
			}
		}
		byte[] data = out.toByteArray();
		out.close();
		CRC32 crc32 = new CRC32();
		crc32.update(data);
		// crc32У�����
		if (crc32.getValue() != crcValue) {
			throw new IOException("����У�����:");
		}
		if (data.length < 1) {
			return;
		}
		// zip��ѹ
		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));
		int count;
		byte gdata[] = new byte[1024];
		while ((count = gis.read(gdata, 0, 1024)) != -1) {
			readout.write(gdata, 0, count);
		}
		this.tag = tag;
		gis.close();
	}

	private void close(Socket socket) {
		try {
			if (socket.getInputStream() != null) {
				socket.getInputStream().close();
			}
			if (socket.getOutputStream() != null) {
				socket.getOutputStream().close();
			}
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (Exception e) {
			// UtilTools.toast("close Error:" + e.getMessage());
		}
	}

	public int getTag() {
		return tag;
	}
}
