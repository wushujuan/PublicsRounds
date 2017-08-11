package cn.breaksky.rounds.publics.listener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.breaksky.rounds.publics.bean.EventBean;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.request.RequestData;

public class EventListener {
	private static Integer SENDING_ID = null;

	/**
	 * 得到需要上传事件
	 * */
	public static RequestData getNeedEvent() {
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
		Cursor cursor = db
				.rawQuery("select id,longitude,latitude,address,contact,phone,image,context_type,context,context_file,time,serviceid from eventinfo where serviceid is null",
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
			
			String[] image = cursor.getString(6).split(";");
			
			if (image[0] != null && image[0].trim().length() > 0 ) {
				data.put("af.event_image", new File(image[0]));
			}
			if (image[1] != null && image[1].trim().length() > 0) {
				data.put("af.event_image1", new File(image[1]));
			}
			if (image[2] != null && image[2].trim().length() > 0) {
				data.put("af.event_image2", new File(image[2]));
			}
			
			data.put("af.event_context_type", cursor.getString(7));
			data.put("af.event_context", cursor.getString(8));
			String contextFile = cursor.getString(9);
			if (contextFile != null && contextFile.length() > 0) {
				data.put("af.event_context_file", new File(contextFile));
			}
			data.put("af.event_time", cursor.getString(10));
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
		db.execSQL("update eventinfo set serviceid=? where id=?", new Object[] { Long.valueOf(serviceid), id });
	}

	/**
	 * 查询事件
	 * */
	public static List<EventBean> queryEvent() {
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
		Cursor cursor = db.rawQuery("select id,longitude,latitude,address,contact,phone,image,context_type,context,context_file,time,serviceid from eventinfo order by time desc",
				new String[] {});
		List<EventBean> beans = new ArrayList<EventBean>();
		while (cursor.moveToNext()) {
			EventBean bean = new EventBean();
			bean.setLongitude(Float.valueOf(cursor.getString(1)));
			bean.setLatitude(Float.valueOf(cursor.getString(2)));
			bean.setAddress(cursor.getString(3));
			bean.setContact(cursor.getString(4));
			bean.setPhone(cursor.getString(5));
			bean.setImage(cursor.getString(6));
			bean.setContext_type(cursor.getString(7));
			bean.setContext(cursor.getString(8));
			bean.setContext_file(cursor.getString(9));
			bean.setCreatetime(cursor.getLong(10));
			bean.setRpe_id(cursor.getLong(11));
			beans.add(bean);
		}
		cursor.close();
		return beans;
	}

}
