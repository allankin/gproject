package txl.message.pushmessage.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import txl.activity.R;
import txl.common.TxlNotification;
import txl.message.pushmessage.po.PushInfo;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * @ClassName:  MessageManager.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-2-4 上午11:05:32
 * @Copyright: 版权由 HundSun 拥有
 */
public class MessageManager
{
    
    public static List<PushInfo> infoList = new ArrayList<PushInfo>();
    
    public static Map<String,PushInfo> infoMap = new HashMap<String,PushInfo>();
    
    public static Context context;
    
    public static final String TAG = MessageManager.class.getSimpleName();
     
    /**
     * 发出通知
     * @param info
     */
    public static void showNotice(final PushInfo info){
        infoMap.put(info.uuId, info);
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.notification_notice);
        TxlNotification.sendNotification2(context, info,remoteView); 
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
