package txl.message.pushmessage.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import txl.common.po.Account;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.dao.PushMsgDao;
import txl.message.pushmessage.po.PushMsg;
import txl.message.pushmessage.po.PushMsg;
import txl.util.Tool;
import android.content.Context;
import android.content.Intent;

/**
 * @ClassName:  MessageManager.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-2-4 上午11:05:32
 */
public class MessageManager
{
    
    public static List<PushMsg> infoList = new ArrayList<PushMsg>();
    
    public static Map<String,PushMsg> infoMap = new HashMap<String,PushMsg>();
    
    public static Context context;
    
    private static TxLogger log = new TxLogger(MessageManager.class, TxlConstants.MODULE_ID_MESSAGE);    
    
    /**
     * 处理消息
     * @param rpm
     */
    public static void dealData(PushMsg rpm){
    	log.info("dealData  ... context:"+context);
    	boolean flag = false;
    	while(!flag){
    	    flag = PushMsgDao.getSingle(context).savePushMsg(rpm);
    	    if(!flag){
    	        try
                {
                    Thread.sleep(50);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
    	    }
    	}
    	
    	
    	//MessageManager.infoMap.put(rpm.msgId, rpm);
    	Config.mainContext.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_RECEIVE_PUSHMESSAGE));
    	Intent intent = new Intent(TxlConstants.ACTION_MESSAGE_RECEIVED);
    	Config.mainContext.sendBroadcast(intent);
    }
    /**
     * 发出通知
     * @param info
     */
    public static void showNotice(final PushMsg rpm){
       /* infoMap.put(info.uuId, info);
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.notification_notice);
        TxlNotification.sendNotification2(context, info,remoteView); */
    	
        
    }
    
    
    public static void startMessageService(final Context context,final Integer _userId) {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Integer userId = _userId;
				if(userId==null){
					Account account =Account.getSingle().readUserFromFS();
					if(account!=null){
						userId= account.userId;
					}
				}
				if(userId==null || userId ==0){
					log.warn("startMessageService, userId ："+userId+", 未启动消息服务....");
					return;
				}
				Intent i = new Intent();
				i.setClass(context, MessageService.class);
				i.putExtra("userId", userId);
				if (!Tool.isServiceRunning(MessageService.class, context)){
					context.startService(i);
				}else{
					log.warn("MessageService已经启动, 不能重复启动...");
				}
			}
		}).start();
    }
    
    public static void showBox(){
        
    }
    /**
     * 查看详细消息
     * @param id
     */
    public static void showDetail(String id){
          
    }

}
