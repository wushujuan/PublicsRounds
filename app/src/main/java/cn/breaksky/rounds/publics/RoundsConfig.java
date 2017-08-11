package cn.breaksky.rounds.publics;

import java.io.File;

import cn.breaksky.rounds.publics.db.DBHelper;
import android.util.LongSparseArray;

public class RoundsConfig {
	public final static int REQ_CODE_PHOTO = 1;
	public final static int REQ_CODE_VIOCE = 2;

	public String SERVICE_URL = "";
	public String SOCKET_URL = "";
	public int SOCKET_PORT;
	public String REQUEST_TYPE;
	public String ONLINE = "1";
	public String LEADER = "1";
	public long SEND_LENGTH = 0;
	public long READ_LENGTH = 0;
	public long ROUND_LISTENER_RUN;
	public LongSparseArray<Integer> NO_READ_MESSAGE = new android.util.LongSparseArray<Integer>();
	public String SESSION_VALUE;
	public boolean IS_REGISTER = false;
	public DBHelper dataBase;
	public boolean loginSucess = false;
	/**
	 * 拍照录像路径
	 * */
	public String CAMERA_PHOTO_PATH = "";
	/**
	 * 拍照录像路径
	 * */
	public String CAMERA2_PHOTO_PATH = "";
	/**
	 * 拍照录像路径
	 * */
	public String CAMERA3_PHOTO_PATH = "";
	/**
	 * 语音路径
	 * */
	public String VOICE_PATH = "";
	/**
	 * 下载路径
	 * */
	public String DOWNLOAD_PATH = "";
	/**
	 * 对象存储路径
	 * */
	public String OBJECT_SAVE_PATH = "";

	public void setRootPath(String path) {
		CAMERA_PHOTO_PATH = path + "camera/";
		CAMERA2_PHOTO_PATH = path + "camera2/";
		CAMERA3_PHOTO_PATH = path + "camera3/";
		VOICE_PATH = path + "voice/";
		DOWNLOAD_PATH = path + "download/";
		OBJECT_SAVE_PATH = path + "object/";
		File file = new File(CAMERA_PHOTO_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(CAMERA2_PHOTO_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(CAMERA3_PHOTO_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(VOICE_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(DOWNLOAD_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(OBJECT_SAVE_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public String[] getRootPaths() {
		String[] path = new String[5];
		path[0] = CAMERA_PHOTO_PATH;
		path[1] = VOICE_PATH;
		path[2] = DOWNLOAD_PATH;
		path[2] = OBJECT_SAVE_PATH;
		path[3] = CAMERA2_PHOTO_PATH;
		path[4] = CAMERA3_PHOTO_PATH;
		return path;
	}
}
