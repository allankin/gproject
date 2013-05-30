package txl.common;

import txl.MainActivity;
import txl.activity.R;
import txl.config.TxlConstants;
import txl.message.pushmessage.po.PushMsg;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


public class TxlNotification
{
 
    private static NotificationManager mNotificationManager;
    private static boolean inited = false;
    private static int NOTIFICATION_ID = 0x0001;
    
  
    
    public static void cancel(){
        if(mNotificationManager!=null){
           mNotificationManager.cancelAll();
        }
    }
    private static void initNotification(Context context){
        
        mNotificationManager = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
     }
    
    /*public static synchronized void sendNotification(Context context,PushInfo info,RemoteViews remoteView){
        if(!inited){
            initNotification(context);
            inited = true;
        }
        Notification mNotification = new Notification(R.drawable.maw_launcher,"MAW新消息",System.currentTimeMillis());
        //将使用默认的声音来提醒用户
        mNotification.defaults = Notification.DEFAULT_SOUND;
        
        Intent mIntent = new Intent(context,MawActivity.class);
        //这里需要设置Intent.FLAG_ACTIVITY_NEW_TASK属性
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        mIntent.putExtra("action", "message");
        mIntent.putExtra("userName",info.userName);
        mIntent.putExtra("id", info.uuId);
        
        int len = info.content.length()> Config.initNoticeLen ? Config.initNoticeLen:info.content.length();
        String c = info.content.substring(0,len);
        mIntent.putExtra("content", c);
        PendingIntent mContentIntent =PendingIntent.getActivity(context,NOTIFICATION_ID, mIntent, 0);
        
        //mNotification.number=NOTIFICATION_ID;
        
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotification.setLatestEventInfo(context, "MAW", c, mContentIntent);
         
        //这里发送通知(消息ID,通知对象)
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);   
        
        NOTIFICATION_ID++; 
    }*/
    public static synchronized void sendNotification2(Context context,PushMsg info,RemoteViews remoteView){
        if(!inited){
            initNotification(context);
            inited = true;
        }
        int icon = R.drawable.ic_launcher;  
        long when = System.currentTimeMillis();  
        
         
        Notification noti = new Notification(icon, "单位通信录有新消息", when + 10000);  
        noti.defaults = Notification.DEFAULT_SOUND;
        noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;  
        
        int len = info.content.length()> TxlConstants.initNoticeLen ? TxlConstants.initNoticeLen:info.content.length();
        String c = info.content.substring(0,len);
        
        
        Intent mIntent = new Intent(context,MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        mIntent.putExtra("action", "message");
        
        //mIntent.putExtra("content", c);
        PendingIntent mContentIntent =PendingIntent.getActivity(context,NOTIFICATION_ID, mIntent, 0);
        
        //remoteView.setOnClickPendingIntent(R.id.enter, mContentIntent);
        
        remoteView.setImageViewResource(R.id.noticeImage, R.drawable.ic_launcher);  
        remoteView.setTextViewText(R.id.notification_text , c);  
        noti.contentView = remoteView;  
        
        
        
        
        /*Intent mIntent = new Intent(context,MawActivity.class);
        //这里需要设置Intent.FLAG_ACTIVITY_NEW_TASK属性
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        mIntent.putExtra("action", "message");
        mIntent.putExtra("id", info.uuId);
        mIntent.putExtra("content", c);
        
        
        
        PendingIntent mContentIntent =PendingIntent.getActivity(context,NOTIFICATION_ID, mIntent, 0);
        //noti.contentIntent = mContentIntent; 
        
        
        int len = info.content.length()> Config.initNoticeLen ? Config.initNoticeLen:info.content.length();
        String c = info.content.substring(0,len);
        
        Builder builder = new NotificationCompat.Builder(context);
        
        Intent mIntentCancel = new Intent();
        //mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        PendingIntent pendingIntentCancel = PendingIntent.getActivity(context,NOTIFICATION_ID, mIntentCancel, 0);
        
        remoteView.setTextViewText(R.id.notification_text , c);
        remoteView.setOnClickPendingIntent(R.id.enter, mContentIntent);
        remoteView.setOnClickPendingIntent(R.id.cancel, pendingIntentCancel);

        
        
        
        builder
                .setSmallIcon(R.drawable.maw_launcher)
                .setContent(remoteView)
                .setContentIntent(pendingIntentCancel);  

        Notification noti = builder.build();*/
 
        
        noti.contentIntent = mContentIntent;
        
        mNotificationManager.notify(NOTIFICATION_ID, noti);
        NOTIFICATION_ID++; 
    }
    
    
        
}
