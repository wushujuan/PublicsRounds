package cn.breaksky.rounds.publics.listener;

import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.util.UtilTools;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class BaseStationListener {
	private Context context;
	private CallBack callBack;

	public BaseStationListener(Context context, CallBack callBack) {
		this.context = context;
		this.callBack = callBack;
	}

	public void getLogLat() {
		SCell cell = this.getCellInfo();
		
		RoundHandler.runMethod(this, 1000, "getLogLat");
	}

	private SCell getCellInfo() {
		SCell cell = new SCell();
		/** 调用API获取基站信息 */
		TelephonyManager mTelNet = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
		if (location == null) {
			UtilTools.toast(context, "获取基站信息失败");
		}
		String operator = mTelNet.getNetworkOperator();
		int mcc = Integer.parseInt(operator.substring(0, 3));
		int mnc = Integer.parseInt(operator.substring(3));
		int cid = location.getCid();
		int lac = location.getLac();
		/** 将获得的数据放到结构体中 */
		cell.MCC = mcc;
		cell.MNC = mnc;
		cell.LAC = lac;
		cell.CID = cid;
		return cell;
	}

	private class SCell {
		public int MCC;
		public int MNC;
		public int LAC;
		public int CID;
	}

	public interface CallBack {
		public void GPSData(GPSData data);
	}
}
