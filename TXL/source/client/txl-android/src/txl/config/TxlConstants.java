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
    
    
    public static final int MSG_HANDLER_SELECT_MESSAGE_TYPE = 0x0001;
    
    public static final int CONTACT_HANDLER_HIDE_OVERLAY = 0x0001;
    public static final int CONTACT_HANDLER_OVERLAY_VISIBLE = 0x0002;
    
    public static final int SETTING_HANDLER_ONLINE_STATUS = 0x0003;
    public static final int SETTING_HANDLER_LOGIN_FAIL = 0x0004;
    
    public static final int NETWORK_HANDLER_CONNECTION_TIMEOUT = 0x0005;
    
    public static final int MSG_LOAD_DEPARTMENT = 0x0006;
    public static final int MSG_RENDER_COMPANY_USER=0x0007;
    public static final int MSG_LOAD_COMPANY_COMMDIR = 0x0008;
    public static final int MSG_ACCOUNT_SESSTION_TIMEOUT = 0x0009;
    
    public static final int MSG_SHARE_BOOK_USER = 0x0009;
    public static final int MSG_SHARE_COMPANY_NOT_EXIST = 0x0010;
    public static final int MSG_SHARE_USER_NOT_EXIT = 0x0011;
    
    public static final int MSG_LOAD_SHARE_COMMDIR = 0x00012;
    public static final int MSG_LOAD_SHARE_COMMDIR_USER = 0x00012;
    public static final int MSG_SYNC_SHARE_COMMDIR_USER = 0x00013;
    public static final int MSG_LOGIN_OFFLINE_STATUS = 0x00014;
    public static final int MSG_LOGOUT_SUCCESS = 0x00015;
    public static final int MSG_RECEIVE_PUSHMESSAGE = 0x00016;
    public static final int MSG_SEARCH_CALL_RECORD = 0x00017;
    
    
    public static final int MSG_SYNC_PERSONAL_BACKUP_SET_ACTION_NAME = 0x0018;
    public static final int MSG_SYNC_PERSONAL_BACKUP_SET_INDEX = 0x0019;
    public static final int MSG_SYNC_PERSONAL_BACKUP_SET_SUCEESS_COUNT = 0x0020;
    
    /**升级 MSG **/
    public static final int MSG_CHECKING_UPGRADE = 0x050;
    public static final int MSG_DOWNLOADING_RES = 0x051;
    public static final int MSG_LOADING_RES = 0x052;
    public static final int MSG_DOWNLOAD_RES_NOT_INTEGRATED = 0x053;
    public static final int MSG_DOWNLOADED_RES = 0x054;
    public static final int MSG_BEGIN_DOWNLOAD = 0x055;
    public static final int MSG_CHECKING_UPGRADE_NEEDNOT = 0x056;
    public static final int MSG_NOT_INSTALL_NOW = 0x057;
    public static final int MSG_CHECK_UPGRADE_ERROR = 0x058;
    public static final int MSG_DOWNLOADING_RES_SHOW_PROCESSBAR = 0x59;
    
    public static class Toast{
        public static int LONG = 5000;
        public static int SHORT = 2000;
    }
    
    public static int LOADING_INTERVAL = 5000;
    
    public static final int HTTP_CONNECTION_TIMEOUT = 5000;
    public static final int HTTP_SO_TIMEOUT = 10000;
    
    public static String TXLMAIN_HOST = "111.1.45.158";   //192.168.2.100 111.1.45.158
    public static int WEB_PORT = 80;//8080  80
    
    public static String WEB_APP_CONTEXT_TXLMAIN = "txlmain-manage";
    public static String WEB_APP_CONTEXT_TXLSHARE = "txlshare-manage";
    public static String[] WEB_APP_CONTEXTS = {WEB_APP_CONTEXT_TXLMAIN,WEB_APP_CONTEXT_TXLSHARE};
    
    public static String LOGIN_URL = "http://"+TXLMAIN_HOST+":"+WEB_PORT+"/txlmain-manage/mobile/user/mobileLogin.txl";
    public static String LOGOUT_URL = "http://"+TXLMAIN_HOST+":"+WEB_PORT+"/txlmain-manage/mobile/user/mobileLogout.txl";
    public static String FIND_BACK_PASSWORD_URL = "http://"+TXLMAIN_HOST+":"+WEB_PORT+"/txlmain-manage/mobile/user/mobileFindBackPassword.naf";
    public static String MODIFY_PASSWORD_URL = "http://"+TXLMAIN_HOST+":"+WEB_PORT+"/txlmain-manage/mobile/user/mobileModifyPassword.naf";
    
    public static String COMMIT_ADVISE_URL = "http://"+TXLMAIN_HOST+":"+WEB_PORT+"/txlmain-manage/mobile/user/saveAdvise.naf";
    public static String UPGRADE_CHECK_URL = "http://"+TXLMAIN_HOST+":"+WEB_PORT+"/txlmain-manage/mobile/upgrade/checkUpgrade.naf";
    public static String DOWNLOAD_APK_URL_PREFIX = "http://"+TXLMAIN_HOST+":"+WEB_PORT+"/txlmain-manage";
    
    
    public static String SEARCH_DEPARTMENT_URL = "http://"+TXLMAIN_HOST+":"+WEB_PORT+"/txlmain-manage/mobile/department/mobileSearch.txl";
    public static String SEARCH_COMPANY_USER_URL = "http://"+TXLMAIN_HOST+":"+WEB_PORT+"/txlmain-manage/mobile/user/s/mobileSearch.txl";

    public static String URL_USER_ISONLINE = "http://"+TXLMAIN_HOST+":"+WEB_PORT+"/txlmain-manage/user/isOnline.txl";
    
    
    
    public static String TXLSHARE_HOST = TXLMAIN_HOST;
    public static String SEARCH_SHARE_COMMDIR_URL =  "http://"+TXLSHARE_HOST+":"+WEB_PORT+"/txlshare-manage/mobile/shareBook/mobileSearch.txl";
    public static String SYNC_SHARE_COMMDIR_URL =  "http://"+TXLSHARE_HOST+":"+WEB_PORT+"/txlshare-manage/mobile/shareBook/mobileSync.txl";
    public static String SEARCH_SHARE_COMMDIR_USER_URL = "http://"+TXLSHARE_HOST+":"+WEB_PORT+"/txlshare-manage/mobile/shareBookUser/mobileSearch.txl";
    
    public static String SYNC_ACTION_PERSONAL_BACKUP_URL = "http://"+TXLSHARE_HOST+":"+WEB_PORT+"/txlshare-manage/syncPhoneCommDir/backup.txl";
    public static String SYNC_ACTION_PERSONAL_PREBACKUP_URL = "http://"+TXLSHARE_HOST+":"+WEB_PORT+"/txlshare-manage/syncPhoneCommDir/preBackup.txl";
    public static String SYNC_ACTION_PERSONAL_POSTBACKUP_URL = "http://"+TXLSHARE_HOST+":"+WEB_PORT+"/txlshare-manage/syncPhoneCommDir/postBackup.txl";
    
    public static String SYNC_ACTION_PERSONAL_RESTORE_URL = "http://"+TXLSHARE_HOST+":"+WEB_PORT+"/txlshare-manage/syncPhoneCommDir/restore.txl";
    public static String SYNC_ACTION_PERSONAL_PRERESTORE_URL = "http://"+TXLSHARE_HOST+":"+WEB_PORT+"/txlshare-manage/syncPhoneCommDir/preRestore.txl";
    public static String SYNC_ACTION_PERSONAL_POSTRESTORE_URL = "http://"+TXLSHARE_HOST+":"+WEB_PORT+"/txlshare-manage/syncPhoneCommDir/postRestore.txl";
    public static String SYNC_ACTION_PERSONAL_FETCH_CONTACTID_URL = "http://"+TXLSHARE_HOST+":"+WEB_PORT+"/txlshare-manage/syncPhoneCommDir/fetchContactIds.txl";
    
    public static final String DB_NAME = "txl";
    public static final int DB_VERSION = 13;
    
    
    public static final String INTENT_BUNDLE_HEADER_TITLE = "headerTitle";
    public static final String INTENT_BUNDLE_COMMDIR_ID = "commDirId";
    public static final String INTENT_BUNDLE_COUNT = "count";
    public static final String INTENT_BUNDLE_REQUEST_CLASS = "requestClass";
    
    public static final String INTENT_BUNDLE_DEPART = "depart";
    public static final String INTENT_BUNDLE_DEPART_ID = "departId";
    
    public static final String INTENT_BUNDLE_CONTACT_ID = "contactId";
    public static final String INTENT_BUNDLE_CONTACT_NAME = "contactName";
    
    public static final String INTENT_BUNDLE_MSG_ID = "msgId";
    public static final String INTENT_BUNDLE_COMPANY_USER = "companyUser";
    
    public static final String INTENT_BUNDLE_PUSHMSG_TYPE = "pushMsgType";
    public static final String INTENT_BUNDLE_PUSHMSG_TYPE_NAME = "pushMsgTypeName";
    
    
    public static final String INTENT_BUNDLE_PUSHMSG_TITLE = "pushMsgTitle";
    public static final String INTENT_BUNDLE_PUSHMSG_URL = "pushMsgUrl";
    
    public static final String INTENT_BUNDLE_ACTION = "action";
    
    public static final String INTENT_BUNDLE_WEB_URL = "webUrl";
    
    public static final int REQUEST_CODE_SELECT_DEPARTMENT = 0x0001;
    
    public static final String ERROR_NETWORK_TIMEOUT = "网络超时";
    public static final String TIP_QUERING = "正在查询";
    public static final String TIP_SYNC = "正在同步";
    
    public static final String TIP_BACKUP = "正在备份";
    public static final String TIP_DOWNLOAD = "正在下载";
    
    public static final String TIP_LOADING = "正在加载...";
    
    public static final String SHARE_PREFERENCE_FILENAME = "app";
    
    public static final String TAB_ITEM_DIAL = "拨号";
    public static final String TAB_ITEM_CONTACT = "联系人";
    public static final String TAB_ITEM_MESSAGE = "信息";
    public static final String TAB_ITEM_SETTING = "设置";
    
    
    public static final int ACCOUNT_IS_ONLINE = 0x0001;
    /*查询操作*/
    public static final int ACTION_QUERY_CODE = 0x0001;
    /*同步操作*/
    public static final int ACTION_SYNC_CODE = 0x0002;
    
    
    public static final int COMMDIR_COMPANY_TYPE = 1;
    public static final int COMMDIR_SHARE_TYPE = 2;
    
    public static final int resendDuration=10000;
    public static final int heartBeatInterval = 1000000;
    public static final int resendCount = 3;
    public static final String ACTION_OFFLINE_NOTICE = "offline.notice";
    public static final String ACTION_OVER_RESEND_COUNT = "over.resend.count";
    public static final String ACTION_MESSAGE_RECEIVED = "ACTION_MESSAGE_RECEIVED";
    
    
    public static final int PUSH_MESSAGE_TYPE_RECEIVE = 1;
    public static final int PUSH_MESSAGE_TYPE_SEND = 2;
    public static final int PUSH_MESSAGE_TYPE_DRAFT = 3;
    
    
    public static final boolean IS_DAO_SINGLE_ABLE = false;
    
    public static int tabContentHeight = 0;
    
    /*注册响应包*/
    public static final int BIZID_RESPONSE_REGIST = 2;
    /*心跳响应包*/
    public static final int BIZID_RESPONSE_HEARTBEAT = 4;
    /*内容响应包*/
    public static final int BIZID_RESPONSE_DATA = 6;
    /*下线响应包*/
    public static final int BIZID_RESPONSE_OFFLINE = 7;
    
    
    /*注册请求包*/
    public static final int BIZID_REQUEST_REGIST = 1 ;
    /*心跳请求包*/
    public static final int BIZID_REQUEST_HEARTBEAT = 3;
    /*内容请求包(发送包)*/
    public static final int BIZID_REQUEST_DATA = 5;
    
    /*可分类推送消息的内容响应包*/
    public static final int BIZID_RESPONSE_CLASSIFIED_PUSHDATA = 8;
    
    public static final int COMMDIR_TYPE_COMMPANY = 1;
    public static final int COMMDIR_TYPE_SHARE = 2;
    
    public static final int PUSH_MSG_TYPE_OFFSET = 20000;
    
    /*不可分类的推送消息类型。   即：服务器端  的：   MESSAGE_TYPE_PLAIN_TEXT：-1 */
    public static final int PUSHMSG_TYPE_NOT_CLASSFIED = -1;
    
    
    public static final int initNoticeLen = 100;
    
    public static final int SHARE_COMPANY_NOT_EXIST = 0x0002;
    public static final int SHARE_USER_NOT_EXIT = 0x0003;
    
    public static final int SYNC_ACTION_BACKUP_PERSONAL_COMMDIR_SUCCESS = 0x0001;
    public static final int SYNC_ACTION_BACKUP_PERSONAL_COMMDIR_FAIL = -1;
    
    public static final int SYNC_ACTION_BACKUP_PERSONAL_NO_SYNCID_FAIL = 0x0021;
	public static final int SYNC_ACTION_PREBACK_UP_FAIL = 0x0022;
	public static final int SYNC_ACTION_POSTBACK_UP_FAIL = 0x0023;
	
    
    public static final int SYNC_ACTION_BACKUP = 0x0001;
	public static final int SYNC_ACTION_RESTORE = 0x0002;
	
	public static final int CONTACT_DATA_TOATAL_TYPE_TEL = 0x0001;
	public static final int CONTACT_DATA_TOATAL_TYPE_EMAIL = 0x0002;
	public static final int CONTACT_DATA_TOATAL_TYPE_ORG = 0x0003;
	public static final int CONTACT_DATA_TOATAL_TYPE_ADDRESS= 0x0004;
	public static final int CONTACT_DATA_TOATAL_TYPE_IM= 0x0005;
	
	public static final int CONTACT_DATA_TYPE_CUSTOM = 0x0000;
	
	public static final String SYNC_ACTION_BACKUP_SUCCESS_LABEL = "备份成功";
	public static final String SYNC_ACTION_RESTORE_SUCCESS_LABEL = "恢复成功";
	public static final String SYNC_ACTION_BACKUP_FAIL_LABEL = "备份失败";
	public static final String SYNC_ACTION_RESTORE_FAIL_LABEL = "恢复失败";
	
    
}
