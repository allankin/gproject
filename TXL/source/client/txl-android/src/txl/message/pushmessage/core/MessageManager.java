package txl.message.pushmessage.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import txl.activity.R;
import txl.common.TxlNotification;
import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.po.PushMsg;
import txl.util.Tool;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

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
     * 发出通知
     * @param info
     */
    public static void showNotice(final PushMsg info){
       /* infoMap.put(info.uuId, info);
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.notification_notice);
        TxlNotification.sendNotification2(context, info,remoteView); */
    }
    
    
    public static void startMessageService(Context context,Integer userId) {
    	if(userId==null){
    		Account account =Account.getSingle().readUserFromFS();
    		userId= account.userId;
    	}
    	if(userId==null || userId ==0){
    		log.info("startMessageService, userId ："+userId+", 未启动消息服务....");
    		return;
    	}
        Intent i = new Intent();
        i.setClass(context, MessageService.class);
        i.putExtra("userId", userId);
        if (!Tool.isServiceRunning(MessageService.class, context)){
        	context.startService(i);
        }else{
        	log.info("MessageService已经启动, 不能重复启动...");
        }
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
