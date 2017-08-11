package cn.breaksky.rounds.publics.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.services.ParseXmlService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.HttpPostUtil;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.RequestProgress;

public class SoftUpdate {
	private Activity activity;
	private String updateUrl = "";
	private String updateName = "";
	private String updatedetail = "";
	private boolean cancelUpdate = false;
	public static final int DOWNLOAD = 1;
	public static final int DOWNLOAD_FINISH = 2;
	public ProgressBar mProgress;
	private AlertDialog mDownloadDialog;
	private String mSavePath;
	private int progress;
	private boolean showMsg = false;
	private HashMap<String, String> hashMap;// 存储跟心版本的xml信息
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SoftUpdate.DOWNLOAD:
				mProgress.setProgress(progress);
				break;
			case SoftUpdate.DOWNLOAD_FINISH:
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public SoftUpdate(Activity activity) {
		this.activity = activity;
	}

	/**
	 * 更新
	 * */
	public void checkUpdate(boolean showMsg) {
		this.showMsg = showMsg;
		RoundHandler.runMethod(this, 1, "readUpdate");
	}

	public void readUpdate() {
		String updateVersionXMLPath = UtilTools.getResource(R.string.app_update_url);
		try {
			int version = 0;
			// 把version.xml放到网络上，然后获取文件信息
			URL url = new URL(updateVersionXMLPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setRequestMethod("GET");// 必须要大写
			InputStream inputStream = conn.getInputStream();
			// 解析XML文件
			ParseXmlService service = new ParseXmlService();
			hashMap = service.parseXml(inputStream);
			version = Integer.valueOf(hashMap.get("versionCode"));
		    updateName	= hashMap.get("fileName");
		    updateUrl = hashMap.get("loadUrl");
		    

		    
			if (version > UtilTools.getVersionCode()) {
				showNoticeDialog();
				
			} else {
				if (showMsg) {
					UtilTools.alertMessage(this.activity, "软件已是最新");
				}
			}
		} catch (Exception e) {
			if (showMsg) {
				UtilTools.alertMessage(this.activity, "更新错误错误!" + e.getMessage());
			}
		}
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(this.activity);
		builder.setTitle("软件更新");
		builder.setMessage("检测到新版本,是否下载更新");
		builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("稍候更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog() {
		cancelUpdate = false;
		AlertDialog.Builder builder = new Builder(this.activity);
		builder.setTitle("更新下载中");
		final LayoutInflater inflater = LayoutInflater.from(this.activity);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.show();
		mDownloadDialog.setCanceledOnTouchOutside(false);
		// 现在文件
		new downloadApkThread().start();
	}

	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				mSavePath = MainService.getInstance().roundsConfig.DOWNLOAD_PATH;
				File file = new File(mSavePath);
				if (!file.exists()) {
					file.mkdir();
				}
				File apkFile = new File(mSavePath, updateName);
				FileOutputStream fos = new FileOutputStream(apkFile);
				HttpPostUtil http = new HttpPostUtil(updateUrl, 0);
				http.send(new RequestProgress() {

					@Override
					public void sendProgress(long length, long count) {

					}

					@Override
					public void sendComplete(long length) {

					}

					@Override
					public void readProgress(long length, long count) {
						progress = (int) (((float) count / length) * 100);
						mHandler.sendEmptyMessage(DOWNLOAD);
					}

					@Override
					public void readComplete(long length) {
						mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
					}

					@Override
					public boolean haveCancel() {
						return cancelUpdate;
					}
				}, fos, new Params());
				fos.flush();
				fos.close();
			} catch (Exception e) {
				UtilTools.toast(activity, "下载错误;" + e.getMessage());
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, this.updateName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		this.activity.startActivity(i);
	}
}
