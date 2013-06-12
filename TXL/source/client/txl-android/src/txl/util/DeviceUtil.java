package txl.util;
 
import android.app.Activity;
import android.content.Context;
 
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

 

/**
 * @ClassName:  DeviceUtil.java
 * @Description: 
 * @Author JinChao
 * @Date 2012-3-23 下午3:29:43
 * @Copyright: 版权由 HundSun 拥有
 */
public class DeviceUtil
{
    public String osRelease = Build.VERSION.RELEASE;
    public String model = android.os.Build.MODEL;
    public int widthPixels ;
    public int heightPixels;
    public String webkitVersion ;
    public  String imei;
    public  String imsi;
    public String phoneNumber;
    
    
    
    public DeviceUtil(Activity context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        this.widthPixels = metrics.widthPixels;
        this.heightPixels = metrics.heightPixels;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.imei = tm.getDeviceId(); 
        if(this.imei==null){
        	this.imei = Tool.genUUID();
        }
        this.imsi =tm.getSubscriberId();
        /*不一定正确*/
        this.phoneNumber = tm.getLine1Number();
             
    }
}
