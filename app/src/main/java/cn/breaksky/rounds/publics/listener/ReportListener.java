package cn.breaksky.rounds.publics.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.breaksky.rounds.publics.bean.ReportBean;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.request.RequestData;

public class ReportListener {
	private static Integer SENDING_ID = null;

	/**
	 * 得到需要上传事件
	 * */
	public static RequestData getNeedEvent() {
		
	
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
		Cursor cursor = db
				.rawQuery("select id,longitude,latitude,address,contact,phone,context,time,serviceid from reportinfo where serviceid=0",
						new String[] {});
		RequestData reqData = null;
		if (cursor.moveToNext()) {
			if (SENDING_ID != null) {
				return null;
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("af.event_longitude", cursor.getString(1));
			data.put("af.event_latitude", cursor.getString(2));
			data.put("af.event_address", cursor.getString(3));
			data.put("af.event_contact", cursor.getString(4));
			data.put("af.event_phone", cursor.getString(5));
			data.put("af.event_context", cursor.getString(6));
			data.put("af.event_time", cursor.getString(7));
			reqData = new RequestData(cursor.getInt(0), data);
			SENDING_ID = cursor.getInt(0);
		}
		cursor.close();
		return reqData;
	}

	/**
	 * 更新事件的服务ID
	 * */
	public static void updateServiceid(int id, String serviceid) {
		SENDING_ID = null;
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
		db.execSQL("update reportinfo set serviceid=? where id=?", new Object[] { Long.valueOf(serviceid), id });
	}

	/**
	 * 查询事件
	 * */
	public static List<ReportBean> queryEvent() {
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
		Cursor cursor = db.rawQuery("select id,longitude,latitude,address,contact,phone,context,time,serviceid from reportinfo order by time desc",
				new String[] {});
		List<ReportBean> beans = new ArrayList<ReportBean>();
		while (cursor.moveToNext()) {
			ReportBean bean = new ReportBean();
			bean.setLongitude(Float.valueOf(cursor.getString(1)));
			bean.setLatitude(Float.valueOf(cursor.getString(2)));
			bean.setAddress(cursor.getString(3));
			bean.setContact(cursor.getString(4));
			bean.setPhone(cursor.getString(5));
			bean.setContext(cursor.getString(6));
			bean.setCreatetime(cursor.getLong(7));
			bean.setRpe_id(cursor.getLong(8));
			beans.add(bean);
		}
		cursor.close();
		return beans;
	}

}
