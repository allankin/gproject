package txl.message.pushmessage.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import txl.activity.R;
import txl.common.TxlNotification;
import txl.common.po.Account;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.dao.PushMsgDao;
import txl.message.pushmessage.po.PushMsg;
import txl.util.Tool;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * @ClassName: MessageManager.java
 * @Description:
 * @Author JinChao
 * @Date 2013-2-4 上午11:05:32
 */
public class MessageManager {

	public static List<PushMsg> infoList = new ArrayList<PushMsg>();

	public static Map<String, PushMsg> infoMap = new HashMap<String, PushMsg>();

	public static Context context;

	private static Class<? extends Service> messageServiceClazz = MessageService.class;

	private static TxLogger log = new TxLogger(MessageManager.class,
			TxlConstants.MODULE_ID_MESSAGE);

	/**标识socket channel 是否连接**/
	private static boolean isConnected = false;
	
	public static boolean isConnnected(){
		return isConnected;
	}
	public static void setConnected(boolean connected){
		isConnected = connected;
	}
	/**
	 * 处理消息
	 * 
	 * @param rpm
	 */
	public static void dealData(PushMsg rpm) {
		log.info("dealData  ... context:" + context);
		PushMsgDao.getSingle(context).savePushMsg(rpm);
		// MessageManager.infoMap.put(rpm.msgId, rpm);
		Config.mainContext.getHandler().sendMessage(
				Tool.genMessage(TxlConstants.MSG_RECEIVE_PUSHMESSAGE));
		Intent intent = new Intent(TxlConstants.ACTION_MESSAGE_RECEIVED);
		intent.putExtra(TxlConstants.INTENT_BUNDLE_CONTACT_ID, rpm.sendUserId);
		intent.putExtra(TxlConstants.INTENT_BUNDLE_MSG_ID, rpm.msgId);
		Config.mainContext.sendBroadcast(intent);
		RemoteViews remoteView = new RemoteViews(context.getPackageName(),
				R.layout.notification_notice);
		TxlNotification.sendNotification2(context, rpm, remoteView);
	}

	/**
	 * 发出通知
	 * 
	 * @param info
	 */
	public static void showNotice(final PushMsg rpm) {
		/*
		 * infoMap.put(info.uuId, info); RemoteViews remoteView = new
		 * RemoteViews(context.getPackageName(), R.layout.notification_notice);
		 * TxlNotification.sendNotification2(context, info,remoteView);
		 */

	}

	public static void startMessageService(final Context context,
			final Integer _userId, final String _phone, final String _name) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Integer userId = _userId;
				String phone = _phone;
				String name = _name;
				Account account = Account.getSingle().readUserFromFS();
				if (userId == null) {
					if (account != null) {
						userId = account.userId;
					}
				}
				if (phone == null) {
					if (account != null) {
						phone = account.phone;
					}
				}
				if (name == null) {
					if (account != null) {
						name = account.name;
					}
				}
				if (userId == null || userId == 0
					|| phone == null || phone.trim().length()==0
					|| name == null || name.trim().length()==0
						) {
					log.info("startMessageService, userId ：" + userId+",phone:"+phone+",name:"+name
							+ ", 未启动消息服务....");
					return;
				}
				Intent i = new Intent();
				i.setClass(context, MessageService.class);
				i.putExtra("userId", userId);
				i.putExtra("phone", phone);
				i.putExtra("name", name);
				if (!Tool.isServiceRunning(MessageService.class, context)) {
					context.startService(i);
				} else {
					log.info("MessageService已经启动, 不能重复启动...");
				}
			}
		}).start();
	}

	/**
	 * 网络不可用，服务在运行，关闭service
	 * 
	 * @param context
	 */
	public static void stopMessageService(Context context) {
		Intent i = new Intent();
		i.setClass(context, messageServiceClazz);
		if (Tool.isServiceRunning(messageServiceClazz, context))
			context.stopService(i);
	}

	public static void showBox() {

	}

	/**
	 * 查看详细消息
	 * 
	 * @param id
	 */
	public static void showDetail(String id) {

	}

}
