package txl.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

import txl.common.po.Account;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.message.pushmessage.core.MessageManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;


/**
 * 工具类（字节转数字、数字转字节、字节转字符串）
 * 
 * 
 */
public class Tool {

	public static boolean isServiceRunning(Class<? extends Service> serviceClass, Context context) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceList = mActivityManager.getRunningServices(Integer.MAX_VALUE);
		if (null != serviceList && serviceList.size() > 0) {
			for (int i = 0; i < serviceList.size(); i++) {
				if (serviceClass.getName().equals(serviceList.get(i).service.getClassName())) {
					return true;// 服务已经启动
				}
			}
		}
		return false;
	}

	

	/**
	 * 判断手机网络是否已经连接
	 * 
	 * @param context
	 * @return huangcheng 2012-8-14
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (null != netinfo && netinfo.isConnected()) {// 手机网络已经连接
			return true;
		}
		return false;
	}
	
	public static boolean isWifi(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if(activeNetInfo!=null  && activeNetInfo.getType()==ConnectivityManager.TYPE_WIFI){
			return true;
		} 
		return false;
	}
	
	public static boolean is3G(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo(); 
		if(activeNetInfo!=null && activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}
	
	public static void sendLauncherMessage(int what){
        Message msg = new Message(); 
        msg.what = what;
        Config.launcher.handler.sendMessage(msg); 
    }
	
   
    
    
    public static boolean checkNetwork(final Activity context){
        boolean netConnected = false;
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        netConnected = true;     
                        break;
                    }
                }
            }
        }
        
        if(!netConnected){
            
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("温馨提示");
            builder.setMessage("网络未开启，需要打开吗？").setCancelable(false);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
            {

                public void onClick(DialogInterface dialog, int id)
                {
                    context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    android.os.Process.killProcess(android.os.Process.myPid());
                    context.finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener()
            {

                public void onClick(DialogInterface dialog, int id)
                {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    context.finish();
                }
            });

            final AlertDialog alert = builder.create();
            alert.show();
            return false;
        }else{
            return true;
        }
        
    }
    
    /**
     * 将秒转换为时间字符串
     * @return
     */
    public static String convertSecondToTimeStr(long seconds){
    	long hour = seconds/3600;
    	long minute = (seconds-hour*3600)/60;
    	long second = (seconds-hour*3600-minute*60);
    	StringBuilder sb = new StringBuilder();
    	if(hour>0){
    		sb.append(hour+"时");
    	}
    	if(minute>0){
    		sb.append(minute+"分");
    	}
    	if(second>0){
    		sb.append(second+"秒");
    	}
    	return sb.toString();
    }
    
    
    public static Message genMessage(int msg){
    	Message message = new Message();
    	message.what = msg;
    	return message;
    }
    public static Message genMessage(int msg,Object content){
    	Message message = new Message();
    	message.what = msg;
    	message.obj = content;
    	return message;
    }
    
    public static String genUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
    
    /** 
     * JSON字符串特殊字符处理，比如：“\A1;1300” 
     * @param s 
     * @return String 
     */  
    public static String string2Json(String s) {        
        StringBuffer sb = new StringBuffer();        
        for (int i=0; i<s.length(); i++) {  
            char c = s.charAt(i);    
             switch (c){  
             case '\"':        
                 sb.append("\\\"");        
                 break;        
             case '\\':        
                 sb.append("\\\\");        
                 break;        
             case '/':        
                 sb.append("\\/");        
                 break;        
             case '\b':        
                 sb.append("\\b");        
                 break;        
             case '\f':        
                 sb.append("\\f");        
                 break;        
             case '\n':        
                 sb.append("\\n");        
                 break;        
             case '\r':        
                 sb.append("\\r");        
                 break;        
             case '\t':        
                 sb.append("\\t");        
                 break;        
             default:        
                 sb.append(c);     
             }  
         }      
        return sb.toString();     
    }  
    public static String getExceptionTrace(Throwable e) {
        if (e != null) {
               StringWriter sw = new StringWriter();
               PrintWriter pw = new PrintWriter(sw);
               e.printStackTrace(pw);
               Throwable cause = e.getCause();  
               while (cause != null) {  
                   cause.printStackTrace(pw);  
                   cause = cause.getCause();  
               } 
               pw.close(); 
               return sw.toString();
        }
        return "No Exception";
    }
    
    public static int convertPushMsgTypeToDB(int pushMsgType){
        return pushMsgType+TxlConstants.PUSH_MSG_TYPE_OFFSET;
    }
    
    /**
     * 退出清理信息。
     * 如：存储的个人信息，socket连接等
     */
    public static void quitClear(Activity ctx){
    	Account.getSingle().clearUserInfo();
    	MessageManager.stopMessageService(ctx);
    }
}
