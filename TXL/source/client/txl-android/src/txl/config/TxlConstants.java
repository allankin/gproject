package txl.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Environment;

/**
 * @ClassName:  Constants.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-3-29 下午4:13:01
 */
@SuppressLint("UseSparseArrays")
public class TxlConstants
{
    public static final int MODULE_ID_LOGIN = 0x0001;
    
    public static final int MODULE_ID_UPGRADE = 0x0002;
    
    public static final int MODULE_ID_SPLASHSCREEN = 0x0004;
    
    public static final int MODULE_ID_BASE = 0x0008;
    
    public static final int MODULE_ID_MESSAGE = 0x0016;
    
    public static final int MODULE_ID_CONTACT = 0x0032;
    
    public static final int MODULE_ID_SETTING = 0x0064;
    
    public static final String MODULE_NAME_LOGIN = "login";
    public static final String MODULE_NAME_UPGRADE = "upgrade";
    public static final String MODULE_NAME_SPLASHSCREEN = "splashscreen";
    public static final String MODULE_NAME_BASE = "base";
    public static final String MODULE_NAME_MESSAGE = "message";
    public static final String MODULE_NAME_CONTACT = "contact";
    public static final String MODULE_NAME_SETTING = "setting";
    public static final Map<String,Integer> moduleMap;
    static{
        moduleMap = new HashMap<String,Integer>();
        moduleMap.put(MODULE_NAME_LOGIN, MODULE_ID_LOGIN);
        moduleMap.put(MODULE_NAME_UPGRADE, MODULE_ID_UPGRADE);
        moduleMap.put(MODULE_NAME_SPLASHSCREEN, MODULE_ID_SPLASHSCREEN);
        moduleMap.put(MODULE_NAME_BASE, MODULE_ID_BASE);
        moduleMap.put(MODULE_NAME_MESSAGE, MODULE_ID_MESSAGE);
        moduleMap.put(MODULE_NAME_CONTACT, MODULE_ID_CONTACT);
        moduleMap.put(MODULE_NAME_SETTING, MODULE_ID_SETTING);
        
    }
    public static final Map<Integer,String> moduleMap2;
    static{
        moduleMap2 = new HashMap<Integer,String>();
        moduleMap2.put(MODULE_ID_LOGIN, MODULE_NAME_LOGIN);
        moduleMap2.put(MODULE_ID_UPGRADE, MODULE_NAME_UPGRADE);
        moduleMap2.put(MODULE_ID_SPLASHSCREEN,MODULE_NAME_SPLASHSCREEN);
        moduleMap2.put(MODULE_ID_BASE,MODULE_NAME_BASE);
        moduleMap2.put(MODULE_ID_MESSAGE,MODULE_NAME_MESSAGE);
        moduleMap2.put(MODULE_ID_CONTACT, MODULE_NAME_CONTACT);
        moduleMap2.put(MODULE_ID_SETTING, MODULE_NAME_SETTING);
    }
    
    public static String[] logLevels = {"","verbose","info","warn","error","off"};
    public static final int LOG_LEVEL_VERBOSE = 0x0001;
    public static final int LOG_LEVEL_INFO = 0x0002;
    public static final int LOG_LEVEL_WARN = 0x0003;
    public static final int LOG_LEVEL_ERROR = 0x0004;
    public static final int LOG_LEVEL_OFF = 0x0005;
    
    public static final File SDCARD_DIRECTORY =  Environment.getExternalStorageDirectory();
    
    
    
    
    public static final int CONTACT_HANDLER_HIDE_OVERLAY = 0x0001;
    public static final int CONTACT_HANDLER_OVERLAY_VISIBLE = 0x0002;
    
    public static final int SETTING_HANDLER_ONLINE_STATUS = 0x0003;
    public static final int SETTING_HANDLER_LOGIN_FAIL = 0x0004;
    
    
    
    public static class Toast{
        public static int LONG = 5000;
        public static int SHORT = 2000;
    }
    
    public static int LOADING_INTERVAL = 5000;
    
    public static final int HTTP_CONNECTION_TIMEOUT = 5000;
    public static final int HTTP_SO_TIMEOUT = 10000;
    
    public static String LOGIN_URL = "http://111.1.45.158/txlmain-manage/mobile/login.txl";
    public static String FIND_BACK_PASSWORD_URL = "";
    
    public static final String DB_NAME = "txl";
    public static final int DB_VERSION = 3;
    
    
    public static final String INTENT_BUNDLE_HEADER_TITLE = "headerTitle";
    public static final String INTENT_BUNDLE_COMMDIR_ID = "commDirId";
    public static final String INTENT_BUNDLE_COUNT = "count";
    
}
