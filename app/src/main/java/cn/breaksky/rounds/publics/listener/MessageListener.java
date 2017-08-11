package cn.breaksky.rounds.publics.listener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cn.breaksky.rounds.publics.ContextManager;
import cn.breaksky.rounds.publics.bean.MessageBean;
import cn.breaksky.rounds.publics.listener.MessageObj.MESSAGE_STATUS;
import cn.breaksky.rounds.publics.listener.MessageObj.MESSAGE_TYPE;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.RequestData;
import cn.breaksky.rounds.publics.util.request.callback.SendStreamThread;

public class MessageListener {
	/**
	 * 读取一条需要发送的消息
	 * */
	public static RequestData getNeedSendMessage() {
		return getMessage(queryOneMessage());
	}

	private static RequestData getMessage(MessageObj message) {
		if (message == null) {
			return null;
		}
		java.util.Map<String, Object> data = new java.util.HashMap<String, Object>();
		switch (message.getType()) {
		case PHOTO:
			data.put("af.msg_messagetype", MESSAGE_TYPE.PHOTO.toString());
			data.put("af.msg_messagefile", message.getFile());
			data.put("af.msg_message", message.getFileName());
			break;
		case VOICE:
			data.put("af.msg_messagetype", MESSAGE_TYPE.VOICE.toString());
			data.put("af.msg_messagefile", message.getFile());
			data.put("af.msg_message", message.getFileName());
			break;
		default:
			data.put("af.msg_messagetype", MESSAGE_TYPE.TEXT.toString());
			data.put("af.msg_message", message.getMessage());
		}
		data.put("af.msg_reviceid", message.getReviceid());
		return new RequestData(message.getMessageid(), data);
	}

	/**
	 * 查询消息参数
	 * */
	public static RequestData getQueryMessage() {
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
		Cursor cursor = db.rawQuery("select max(serviceid) from message", null);
		cursor.moveToNext();
		Long serviceid = cursor.getLong(0);
		cursor.close();
		java.util.Map<String, Object> data = new java.util.HashMap<String, Object>();
		data.put("af.query_messageid", (serviceid == null ? "0" : serviceid.toString()));
		return new RequestData(null, data);
	}

	/**
	 * 服务器返回消息
	 * */
	public static void reviceMessage(List<MessageBean> messageBean) {
		if (messageBean.size() > 0) {
			UtilTools.beginVibrator();
			UtilTools.beginVoice();
		}
		for (final MessageBean bean : messageBean) {
			if (MainService.getInstance().roundsConfig.NO_READ_MESSAGE.get(bean.getSend_id()) == null) {
				MainService.getInstance().roundsConfig.NO_READ_MESSAGE.put(bean.getSend_id(), 1);
			} else {
				MainService.getInstance().roundsConfig.NO_READ_MESSAGE.put(bean.getSend_id(), MainService.getInstance().roundsConfig.NO_READ_MESSAGE.get(bean.getSend_id()) + 1);
			}
			if (MESSAGE_TYPE.TEXT.toString().equals(bean.getType())) {
				MessageObj message = new MessageObj(bean.getMessage(), bean.getRecive_id().toString(), bean.getSend_id().toString(), UtilTools.stringToDate(bean.getSendtime(),
						null));
				message.setServiceid(bean.getRm_id());
				writeMessage(message, MESSAGE_STATUS.SUCCESS);
			}
			if (MESSAGE_TYPE.PHOTO.toString().equals(bean.getType()) || MESSAGE_TYPE.VOICE.toString().equals(bean.getType())) {
				try {
					UtilTools.toast(ContextManager.getInstance(), "读取文件中,请稍候...");
					Request request = new Request(Request.READ_FILE);
					Params param = new Params();
					param.addTextParameter("af.query_messagefileid", bean.getFileid().toString());
					final File file = new File(MainService.getInstance().roundsConfig.DOWNLOAD_PATH + "call_" + bean.getMessage());
					final FileOutputStream out = new FileOutputStream(file);
					request.send(null, out, param, new SendStreamThread.CallBack() {

						@Override
						public void exception(Exception e) {
							UtilTools.toast(ContextManager.getInstance(), "读取消息失败!" + e.getMessage());
						}

						@Override
						public void complete() {
							try {
								out.flush();
								out.close();
							} catch (IOException e) {
								Log.e("MessageListener", e.toString());
							}
							MESSAGE_TYPE type = MESSAGE_TYPE.PHOTO.toString().equals(bean.getType()) ? MESSAGE_TYPE.PHOTO : MESSAGE_TYPE.VOICE;
							MessageObj message = new MessageObj(file, type, bean.getRecive_id().toString(), bean.getSend_id().toString(), UtilTools.stringToDate(
									bean.getSendtime(), null));
							message.setServiceid(bean.getRm_id());
							writeMessage(message, MESSAGE_STATUS.SUCCESS);
							UtilTools.toast(ContextManager.getInstance(), "读取成功!");
						}
					});
				} catch (Exception e) {
					UtilTools.toast(ContextManager.getInstance(), "读取消息失败!" + e.getMessage());
				}
			}
			if (MESSAGE_TYPE.DISPATCH.toString().equals(bean.getType())) {
				String[] message = bean.getMessage().split(";");
				if (message.length != 2) {
					UtilTools.toast(ContextManager.getInstance(), "读取消息失败!调度消息内容长度不为2");
					return;
				}
				String[] coor = message[1].split(",");
				if (coor.length != 2) {
					UtilTools.toast(ContextManager.getInstance(), "调度坐标错误");
					return;
				}
				MessageObj messageObj = new MessageObj(Float.valueOf(coor[1]), Float.valueOf(coor[0]), bean.getRecive_id().toString(), bean.getSend_id().toString(),
						UtilTools.stringToDate(bean.getSendtime(), null), bean.getMessage());
				messageObj.setServiceid(bean.getRm_id());
				writeMessage(messageObj, MESSAGE_STATUS.SUCCESS);
			}
		}
	}

	/**
	 * 更新消息状态
	 * */
	public static void updateMessage(Integer messageid, MESSAGE_STATUS status) {
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getWritableDatabase();
		db.execSQL("update message set status=? where id=?", new Object[] { status.toString(), messageid });
	}

	private static MessageObj queryOneMessage() {
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select id,message,sendid,reciveid,sendtime,status,type,file,latitude,longitude from message where serviceid is null and (status=? or status=?)", new String[] {
						MESSAGE_STATUS.WATI.toString(), MESSAGE_STATUS.FAILE.toString() });
		MessageObj messageObj = null;
		if (cursor.moveToNext()) {
			int messageid = cursor.getInt(0);
			String type = cursor.getString(6);
			String reviceid = cursor.getString(3);
			String sendid = cursor.getString(2);
			String message = cursor.getString(1);
			Date sendtime = new Date(cursor.getLong(4));
			String filePath = cursor.getString(7);
			Float latitude = cursor.getFloat(8);
			Float longitude = cursor.getFloat(9);

			if (MESSAGE_TYPE.TEXT.toString().equals(type)) {
				messageObj = new MessageObj(message, reviceid, sendid, sendtime);
			} else if (MESSAGE_TYPE.PHOTO.toString().equals(type)) {
				messageObj = new MessageObj(new File(filePath), MESSAGE_TYPE.PHOTO, reviceid, sendid, sendtime);
			} else if (MESSAGE_TYPE.VOICE.toString().equals(type)) {
				messageObj = new MessageObj(new File(filePath), MESSAGE_TYPE.VOICE, reviceid, sendid, sendtime);
			} else if (MESSAGE_TYPE.DISPATCH.toString().equals(type)) {
				messageObj = new MessageObj(latitude, longitude, reviceid, sendid, sendtime, message);
			}
			if (messageObj != null) {
				messageObj.setMessageid(messageid);
			}
		}
		cursor.close();
		return messageObj;
	}

	public static synchronized void writeMessage(MessageObj message, MESSAGE_STATUS status) {
		SQLiteDatabase db = MainService.getInstance().roundsConfig.dataBase.getWritableDatabase();
		String insertSql = null;
		Object obj[] = null;
		if (MESSAGE_TYPE.TEXT.equals(message.getType())) {
			insertSql = "insert into message(message, sendid,reciveid,serviceid,sendtime,status,type) values(?,?,?,?,?,?,?)";
			obj = new Object[] { message.getMessage(), message.getSendid(), message.getReviceid(), message.getServiceid(), message.getSendTime().getTime(), status.toString(),
					message.getType().toString() };
		} else if (MESSAGE_TYPE.PHOTO.equals(message.getType()) || MESSAGE_TYPE.VOICE.equals(message.getType())) {
			File file = new File(message.getFile().getPath());
			if (!file.exists()) {
				UtilTools.toast(ContextManager.getInstance(), "文件不存在,请重新操作!");
				return;
			}
			insertSql = "insert into message(message, sendid,reciveid,serviceid,sendtime,status,type,file) values(?,?,?,?,?,?,?,?)";
			obj = new Object[] { message.getMessage(), message.getSendid(), message.getReviceid(), message.getServiceid(), message.getSendTime().getTime(), status.toString(),
					message.getType().toString(), message.getFile().getPath() };
		}
		db.execSQL(insertSql, obj);
	}
}
