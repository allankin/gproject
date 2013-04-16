package txl.common;

import txl.MainActivity;
import txl.activity.R;
import txl.config.Config;
import txl.message.pushmessage.po.PushInfo;
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
    
     
    public static synchronized void sendNotification2(Context context,PushInfo info,RemoteViews remoteView){
        if(!inited){
            initNotification(context);
            inited = true;
        }
        int icon = R.drawable.ic_launcher;  
        long when = System.currentTimeMillis();  
        
        String tip = "您有最新消息"; 
        Notification noti = new Notification(icon, tip, when + 10000);  
        noti.defaults = Notification.DEFAULT_SOUND;
        noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;  
        
        
        
        Intent mIntent = new Intent(context,MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        mIntent.putExtra("action", "message");
        mIntent.putExtra("uuId", info.uuId);
        mIntent.putExtra("userName",info.userName);
        
        //mIntent.putExtra("content", c);
        PendingIntent mContentIntent =PendingIntent.getActivity(context,NOTIFICATION_ID, mIntent, 0);
        
        //remoteView.setOnClickPendingIntent(R.id.enter, mContentIntent);
        
        
        remoteView.setImageViewResource(R.id.noticeImage, R.drawable.ic_launcher);  
        remoteView.setTextViewText(R.id.notification_text , tip);  
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
