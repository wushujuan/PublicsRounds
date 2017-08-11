package cn.breaksky.rounds.publics.listener;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.bean.MessageBean;
import cn.breaksky.rounds.publics.handler.RoundHandler;
import cn.breaksky.rounds.publics.listener.MessageObj.MESSAGE_STATUS;
import cn.breaksky.rounds.publics.services.MainService;
import cn.breaksky.rounds.publics.services.MainService.NOTIFICATION_EVENT;
import cn.breaksky.rounds.publics.util.UtilTools;
import cn.breaksky.rounds.publics.util.request.Params;
import cn.breaksky.rounds.publics.util.request.Request;
import cn.breaksky.rounds.publics.util.request.RequestData;
import cn.breaksky.rounds.publics.util.request.callback.SendRetJsonThread;

public class RoundListener {
	private static Long TIME = (long) 10000;
	private Context activity;
	private Login login = null;
	private RequestData sendMessage;
	private RequestData sendEvent;
	private RequestData sendReport;
	private RequestData sendEventProcess;
	private int message_id = -1;
	private long maxPublicMessageId = -1;
	private long maxGatherId = -1;
    
	public RoundListener(Context activity) {
		TIME = Long.valueOf(UtilTools.getConfigValue(R.string.thread_check_time_key));
		
		this.activity = activity;
		synchData();
	}

	public void synchData() {
		MainService.getInstance().roundsConfig.ROUND_LISTENER_RUN++;
		if (MainService.getInstance().roundsConfig.loginSucess) {
			try {
				Params param = new Params();
				// 需要发送的消息
				sendMessage = MessageListener.getNeedSendMessage();
				if (sendMessage != null && sendMessage.getData().size() > 0) {
					param.addTextParameter("af.syn_message", "1");
					param.addParams(sendMessage.getData());
				}
				// 需要查询的消息
				RequestData quermessage = MessageListener.getQueryMessage();
				if (quermessage != null && quermessage.getData().size() > 0) {
					param.addTextParameter("af.syn_querymessage", "1");
					param.addParams(quermessage.getData());
				}
				// 需要上传的坐标
				RequestData gpsData = GPSListener.getNeedSendGPS();
				if (gpsData != null && gpsData.getData().size() > 0) {
					param.addTextParameter("af.syn_gps", "1");
					param.addParams(gpsData.getData());
				}
				// 需要上传事件
				sendEvent = EventListener.getNeedEvent();
				if (sendEvent != null && sendEvent.getData().size() > 0) {
					param.addTextParameter("af.syn_event", "1");
					param.addParams(sendEvent.getData());
				}
				// 需要上传事件处理
				sendEventProcess = EventProcessListener.getNeedEventProcess();
				if (sendEventProcess != null && sendEventProcess.getData().size() > 0) {
					param.addTextParameter("af.syn_eventprocess", "1");
					param.addParams(sendEventProcess.getData());
				}
				// 需要上传日常汇报事件
				sendReport = ReportListener.getNeedEvent();
				if (sendReport != null && sendReport.getData().size() > 0) {
					param.addTextParameter("af.syn_report", "1");
					param.addParams(sendReport.getData());
				}
				// 汇总已使用的流量
				param.addTextParameter("af.sum_send", String.valueOf(MainService.getInstance().getSumSend()));
				param.addTextParameter("af.sum_read", String.valueOf(MainService.getInstance().getSumRead()));

				Request request = new Request(Request.SYNCH_DATA);
				request.sendRetJson(param, new SendRetJsonThread.CallBack() {

					@Override
					public void callBack(int tag, JSONObject json) {
						if (json.has("sessionout")) {
							MainService.getInstance().roundsConfig.loginSucess = false;
						} else {
							try {
								// 消息是否发送返回
								if (json.has("syn_msg")) {
									if (json.getBoolean("msg_status")) {
										MessageListener.updateMessage((Integer) sendMessage.getId(), MESSAGE_STATUS.SUCCESS);
										UtilTools.toast(activity, "发送成功!");
									} else {
										MessageListener.updateMessage((Integer) sendMessage.getId(), MESSAGE_STATUS.FAILE);
										UtilTools.toast(activity, "消息发送失败" + json.getString("msg_info"));
									}
								}
								// 是否有新消息
								if (json.has("syn_query")) {
									if (json.getBoolean("query_status")) {
										List<MessageBean> messageBean = UtilTools.jsonToBean(json.getJSONArray("query_data"), MessageBean.class);
										MessageListener.reviceMessage(messageBean);
									} else {
										UtilTools.toast(activity, "消息查询失败" + json.getString("query_info"));
									}
								}
								
								// 坐标是否发送成功
								if (json.has("syn_gps")) {
									if (!json.getBoolean("gps_status")) {
										UtilTools.toast(activity, "GPS发送失败" + json.getString("gps_info"));
									}
								}
								// 上传事件是否成功
								if (json.has("syn_event")) {
									if (json.getBoolean("status")) {
										EventListener.updateServiceid((Integer) sendEvent.getId(), json.getString("serviceid"));
										UtilTools.toast(activity, "发送成功!");
									} else {
										UtilTools.toast(activity, "事件上传失败!" + json.getString("message"));
									}
								}
								// 上传日常汇报是否成功
								if (json.has("syn_report")) {
									if (json.getBoolean("status")) {
										ReportListener.updateServiceid((Integer) sendReport.getId(), json.getString("serviceid"));
										UtilTools.toast(activity, "发送成功!");
									} else {
										UtilTools.toast(activity, "日常汇报上传失败!" + json.getString("message"));
									}
								}
								// 是否有新消息提示
								if (json.has("syn_new_msg")) {
									List<String> newMessage = UtilTools.jsonToBean(json.getJSONArray("syn_new_msg"), String.class);
									for (String msgStr : newMessage) {
										
										String[] msg = msgStr.split(";");
										String title = "新消息";
										String content="";
										Long pmax=Long.valueOf(-1);
										if (msg.length != 2) {
											continue;
										}
										String[] temp ;
										
										NOTIFICATION_EVENT event = null;
										if ("2".equals(msg[0])) {// MESSAGE_TYPE_WORK
											event = NOTIFICATION_EVENT.SHOW_MAP;
											content=msg[1];
											title = "新任务";
											MainService.getInstance().showNotification(event, "", title,content, -1);
										} else if ("3".equals(msg[0])) {// MESSAGE_TYPE_PUBLIC_EVENT_PROCESS
											event = NOTIFICATION_EVENT.EVENT_PROCESS;
											content=msg[1];
											title = "事件处理回复";
											MainService.getInstance().showNotification(event, "", title,content, -1);
										} else if ("4".equals(msg[0])) {// MESSAGE_TYPE_PUBLIC_MESSAGE
											event = NOTIFICATION_EVENT.MESSAGE;
											temp = msg[1].split("_");
											content="有"+temp[0]+"条公告预警没有阅读";
											title = "新公告预警";
											pmax=Long.parseLong(temp[1]);
											MainService.getInstance().maxPMessage = Long.parseLong(temp[0]);
											if(!pmax.equals(maxPublicMessageId))
											{
												maxPublicMessageId = pmax;
												MainService.getInstance().showNotification(event, "", title,content, -1);
											}
											
										} else if ("5".equals(msg[0])) {// MESSAGE_TYPE_PUBLIC_MESSAGE
											event = NOTIFICATION_EVENT.GATHER;
											temp = msg[1].split("_");
											content="有"+temp[0]+"条采集任务没有完成提交";
											title = "新采集任务";
											pmax=Long.parseLong(temp[1]);
											MainService.getInstance().maxGather = Long.parseLong(temp[0]);
											if(!pmax.equals(maxGatherId))
											{
												maxGatherId=pmax;
												MainService.getInstance().showNotification(event, "", title,content, -1);
											}
										} else {
											continue;
										}
										
									}
								}
							} catch (Exception e) {
								UtilTools.toast(activity, "读取数据错误!" + e.getMessage());
							}
						}
					}

					@Override
					public void exception(int tag, Exception e) {
						//faileNotification(null, "通讯错误", "无法与服务器通讯，请检查网络或联系管理员！");
					}

				});
			} catch (Throwable e) {
				UtilTools.toast(activity, "同步任务错误!" + e.getMessage());
			}
		} else {// 重新登录
			try {
				String username = UtilTools.getIMEI(activity);
				String password = UtilTools.getConfigValue(R.string.save_password_key);
				if (username != null && username.length() > 0) {
					if (login == null) {
						login = new Login(new LoginCallBack());
					} else {
						if (!login.isLogining()) {
							login.login(username, password == null ? "" : password);
						}
					}
				}
			} catch (Exception e) {
				UtilTools.toast(activity, "登录异常!" + e.getMessage());
			}
		}
		RoundHandler.runMethod(this, TIME, "synchData");
	}

	private class LoginCallBack implements Login.LoginCallBack {

		@Override
		public void success() {
			UtilTools.toast(activity, "登录成功");
			MainService.getInstance().removeNotification(message_id);
			MainService.getInstance().roundsConfig.loginSucess = true;
			login = null;
		}

		@Override
		public void faile(String msg) {
			//loginFaileNotification(null, "请检查网络或联系管理员！");
			login = null;
		}

		@Override
		public void passwordError() {
			loginFaileNotification(NOTIFICATION_EVENT.RE_PASSWORD, "密码错误，点击重新输入！");
		}

		@Override
		public void useRegister() {
			loginFaileNotification(NOTIFICATION_EVENT.NEED_REGISTER, "设备未注册，点击进行设备注册！");
		}

		@Override
		public void registered() {
			loginFaileNotification(null, "设备已注册，请等待管理员审批！");
		}

		private void loginFaileNotification(NOTIFICATION_EVENT event, String message) {
			faileNotification(event, "登录失败", message);
		}
	}

	private void faileNotification(NOTIFICATION_EVENT event, String title, String message) {
		if (MainService.getInstance().isNotificationRemove(message_id)) {
			message_id = MainService.getInstance().showNotification(event, "", title, message, message_id);
		}
	}
}
