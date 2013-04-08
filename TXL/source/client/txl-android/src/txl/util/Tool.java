package txl.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import txl.config.Config;
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
	
 
	
	public static void sendLauncherMessage(int what){
        Message msg = new Message(); 
        msg.what = what;
        Config.launcher.handler.sendMessage(msg); 
    }
	
   
    public static String httpPost(String path, Map<String, String> params, String enc){
        if(path==null||path.length()==0)
            return "";
        String body = "";
        List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
        if(params!=null && !params.isEmpty()){
            for(Map.Entry<String, String> entry : params.entrySet()){
                paramPairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        UrlEncodedFormEntity entitydata;
        try
        {
            entitydata = new UrlEncodedFormEntity(paramPairs, enc);
            HttpPost post = new HttpPost(path);  
            post.setEntity(entitydata);
            DefaultHttpClient client = new DefaultHttpClient();  
            HttpResponse response = client.execute(post);
            
            if(response.getStatusLine().getStatusCode()==200){
                HttpEntity entity = response.getEntity();
                body = EntityUtils.toString(entity, enc);    
            }
            
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }catch (ClientProtocolException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return body;
    }
    
    public static String httpPostUTF8(String path, Map<String, String> params){
        return httpPost(path, params, "utf-8");
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
    public static Message genMessage(int msg,String content){
    	Message message = new Message();
    	message.what = msg;
    	message.obj = content;
    	return message;
    }
}
