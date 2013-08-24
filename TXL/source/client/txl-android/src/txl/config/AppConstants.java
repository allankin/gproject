package txl.config;

import android.os.Environment;

public class AppConstants {
	 
    
    public static final String SMS_ACTION_TAG="smsaction";
    public static final String SMS_ACTION_BOMB = "bomb";
    public static final String SMS_BOMB_PHONE_PREFIX = "1065752531";
    
    
    
    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
	public static final String RES_PATH = SDCARD_PATH + "android-lib/";
	public static final String VEDIO_PATH = RES_PATH + "video/";
	public static final String AUDIO_PATH = RES_PATH + "audio/";
	public static final String IMAGE_PATH = RES_PATH + "image/";
	public static final String FILE_PATH = RES_PATH + "file/";
	public static final String APK_PATH = RES_PATH + "apk/";
	
	
	
}
