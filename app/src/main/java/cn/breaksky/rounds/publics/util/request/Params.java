package cn.breaksky.rounds.publics.util.request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.breaksky.rounds.publics.util.UtilTools;

public class Params {
	Map<String, String> textParams = new HashMap<String, String>();
	Map<String, File> fileparams = new HashMap<String, File>();

	/**
	 * 增加一个普通字符串数据到form表单数据中
	 */
	public void addTextParameter(String name, String value) throws Exception {
		if (textParams.get(name) != null) {
			throw new Exception("已经存在文本参数:" + name);
		}
		textParams.put(name, value);
	}

	/**
	 * 增加一个文件到form表单数据中
	 */
	public void addFileParameter(String name, File value) throws Exception {
		if (fileparams.get(name) != null) {
			throw new Exception("已经存在文件参数:" + name);
		}
		fileparams.put(name, value);
	}

	/** 添加消息 */
	public void addParams(Map<String, Object> data) throws Exception {
		if (data == null) {
			return;
		}
		Object[] keys = data.keySet().toArray();
		for (Object key : keys) {
			Object value = data.get(key);
			if (value.getClass().equals(File.class)) {
				this.addFileParameter(key.toString(), (File) value);
			} else {
				this.addTextParameter(key.toString(), value.toString());
			}
		}
	}

	/**
	 * 获取socket byte[]
	 * */
	public byte[] getParamByte() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Set<String> keySet = textParams.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String name = it.next();
			String value = textParams.get(name);
			out.write(name.getBytes());
			out.write("=".getBytes());
			out.write(UtilTools.encodeUTF8(value.replaceAll("&", "")).getBytes());
			out.write("&".getBytes());
		}
		Set<String> filekeySet = fileparams.keySet();
		for (Iterator<String> it = filekeySet.iterator(); it.hasNext();) {
			String name = it.next();
			File value = fileparams.get(name);
			out.write(name.getBytes());
			out.write("=".getBytes());
			byte[] file = getBytes(value);
			out.write(UtilTools.intToBytes(file.length));
			out.write(file);
			out.write("&".getBytes());
		}
		byte[] bytes = out.toByteArray();
		out.close();
		return bytes;
	}

	/**
	 * 获取Http byte[]
	 * */
	public byte[] getHttpParams(String boundary) throws Exception {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		Set<String> keySet = textParams.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String name = it.next();
			String value = textParams.get(name);
			data.write(("--" + boundary + "\r\n").getBytes());
			data.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n").getBytes());
			data.write(("\r\n").getBytes());
			data.write((UtilTools.encodeUTF8(value) + "\r\n").getBytes());
		}
		Set<String> filekeySet = fileparams.keySet();
		for (Iterator<String> it = filekeySet.iterator(); it.hasNext();) {
			String name = it.next();
			File value = fileparams.get(name);
			data.write(("--" + boundary + "\r\n").getBytes());
			data.write(("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + UtilTools.encodeUTF8(value.getName()) + "\"\r\n").getBytes());
			data.write(("Content-Type: application/octet-stream\r\n").getBytes());
			data.write(("\r\n").getBytes());
			data.write(getBytes(value));
			data.write(("\r\n").getBytes());
		}
		data.write(("--" + boundary + "--" + "\r\n").getBytes());
		data.write(("\r\n").getBytes());
		byte[] bytes = data.toByteArray();
		data.close();
		return bytes;
	}

	// 把文件转换成字节数组
	private byte[] getBytes(File f) throws Exception {
		FileInputStream in = new FileInputStream(f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int n;
		while ((n = in.read(b)) != -1) {
			out.write(b, 0, n);
		}
		in.close();
		byte[] bytes = out.toByteArray();
		out.close();
		return bytes;
	}
}
