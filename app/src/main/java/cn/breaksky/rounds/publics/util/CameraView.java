package cn.breaksky.rounds.publics.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressWarnings("deprecation")
public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {
	private final static String TAG = "CameraView";
	private SurfaceHolder holder;
	private Camera camera;
	private Context context;
	private String photoFile = null;
	private String rootPath;
	private CallBack callBack;
	// 录像
	private MediaRecorder mRecorder;
	private String videoFile;
	private static int v_width = 640;
	private static int v_height = 480;
	private static int v_frame = 20;
	private static int v_rate = v_frame * v_width * v_height;

	public CameraView(Context context, String rootPath, CallBack callBack) {// 构造函数
		super(context);
		this.context = context;
		this.callBack = callBack;
		File forder = new File(rootPath + "/cameraview");
		if (!forder.exists()) {
			forder.mkdirs();
		}
		this.rootPath = forder.getPath();

		holder = getHolder();// 生成Surface Holder
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 指定Push Buffer
	}

	public void surfaceCreated(SurfaceHolder holder) {// Surface生成事件的处理
		try {
			camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);// 摄像头的初始化
			if (camera == null) {
				UtilTools.toast(this.context, "初始化摄像头失败!");
			} else {
				camera.setPreviewDisplay(holder);
			}
		} catch (Exception e) {
			UtilTools.toast(this.context, "初始化摄像头失败!");
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {// Surface改变事件的处理
		if (camera == null) {
			return;
		}
		List<Size> size = getCameraSize();
		Size maxSize = null;
		for (Size s : size) {
			if (maxSize == null) {
				maxSize = s;
			} else {
				if (s.width > maxSize.width) {
					maxSize = s;
				}
			}
		}
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(maxSize.width, maxSize.height);
		parameters.setPictureSize(maxSize.width, maxSize.height);
		camera.setParameters(parameters);
		camera.setDisplayOrientation(90);
		camera.startPreview();// 开始预览
	}

	private static List<Size> CAMERA_SIZE = null;

	private List<Size> getCameraSize() {
		if (CAMERA_SIZE == null) {
			List<Size> size = new ArrayList<Size>();
			List<Size> tempSize = camera.getParameters().getSupportedPreviewSizes();
			for (Size s : tempSize) {
				try {
					Camera.Parameters parameters = camera.getParameters();
					parameters.setPreviewSize(s.width, s.height);
					parameters.setPictureSize(s.width, s.height);
					camera.setParameters(parameters);
					size.add(s);
				} catch (RuntimeException e) {
					Log.e(TAG, "Test CameraSize Error");
				}
			}
			CAMERA_SIZE = size;
		}
		return CAMERA_SIZE;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {// Surface销毁时的处理
		if (camera == null) {
			return;
		}
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {// 屏幕触摸事件
		if (camera == null) {
			return false;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			UtilTools.toast(this.context, "对焦....");
			try {
				camera.autoFocus(null);
			} catch (Exception e) {
				UtilTools.toast(this.context, "对焦失败:" + e.getMessage());
			}
		}
		return true;
	}

	/**
	 * 拍照
	 * */
	public void takenPicture() {
		if (camera == null) {
			return;
		}
		this.photoFile = this.rootPath + "/" + String.valueOf((new Date()).getTime()) + ".jpg";
		UtilTools.toast(this.context, "拍摄....");
		camera.takePicture(null, null, this);
	}

	/**
	 * 开始录像
	 * */
	public boolean startRecordVideo() {
		if (camera == null) {
			return false;
		}
		camera.stopPreview();
		try {
			camera.unlock();
		} catch (Exception e) {
		}

		this.videoFile = this.rootPath + "/" + String.valueOf((new Date()).getTime()) + ".mp4";
		mRecorder = new MediaRecorder();
		mRecorder.reset();
		mRecorder.setCamera(camera);
		// 设置从麦克风采集声音
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 设置从摄像头采集图像
		mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// 设置视频、音频的输出格式
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// 设置音频的编码格式、
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		// 设置图像编码格式
		mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mRecorder.setOrientationHint(90);
		mRecorder.setOutputFile(this.videoFile);

		mRecorder.setVideoSize(v_width, v_height);
		mRecorder.setVideoEncodingBitRate(v_rate);
		mRecorder.setVideoFrameRate(v_frame);

		// 指定SurfaceView来预览视频
		mRecorder.setPreviewDisplay(holder.getSurface());
		try {
			mRecorder.prepare();
			// 开始录制
			mRecorder.start();
			return true;
		} catch (Exception e) {
			UtilTools.toast(context, "启动录像失败");
			camera.startPreview();
			return false;
		}
	}

	/**
	 * 停止录像
	 * */
	public String stopRecordVideo() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		return this.videoFile;
	}

	/**
	 * 拍摄完成后保存照片
	 * */
	public void onPictureTaken(byte[] data, Camera camera) {
		try {
			FileOutputStream out = new FileOutputStream(new File(this.photoFile));
			out.write(data);
			out.flush();
			out.close();
			callBack.TakenPicture(this.photoFile, null);
		} catch (Exception e) {
			callBack.TakenPicture(null, (e == null ? "null" : e.getMessage()));
		}
		camera.startPreview();
	}

	public interface CallBack {
		/**
		 * 拍照回调
		 * */
		public void TakenPicture(String path, String error);
	}
}
