package txl.config;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import txl.SplashActivity;
import android.content.Context;
import android.widget.TabHost;


/**
 * @ClassName:  Config.java
 * @Description: 配置类
 * @Author JinChao
 * @Date 2013-3-29 上午10:41:38
 */
public class Config
{
    private LogConfig logConfig;
    private PushMessageServer pushMessageServer;
    private UpgradeServer upgradeServer;
    private ResDir resDir;
    private static Context context;
    
    public static String DATA_PACKAGE;
    /*** intent传递参数名 ***/
    public static String INTENT_PARAM_DATA = "data";
    
    public static String INTENT_PARAM_LIST_DATA ="listData";
    public static final String APP_PACKAGE_NAME = "txl.activity";
    
    public ResDir getResDir()
    {
        return resDir;
    }
    
    
    public void setResDir(ResDir resDir)
    {
        this.resDir = resDir;
    }

    private  final String errorUrl = "file:///android_asset/www/error.html";
    
    private  final String ldtips = "加载中...";
    
    private  final String tipTitle = "消息提示";
    
    public static SplashActivity launcher = null;
    public static TabHost tabHost;
    
    public static final int CHECKING_UPGRADE = 0x005;
    public static final int DOWNLOADING_RES = 0x006;
    public static final int LOADING_RES = 0x007;
    public static final int DOWNLOAD_RES_NOT_INTEGRATED = 0x009;
    
    public String getApkDir(){
        return this.resDir.apkDir;
    }
    
    public String getApkDirFileString(String fileName){
        String apkFile = null;
        if(this.resDir.apkDir !=null){
             apkFile = this.resDir.apkDir + fileName;
        }
        return apkFile;
    }
    public String getErrorurl()
    {
        return errorUrl;
    }
    
    public String getLdtips()
    {
        return ldtips;
    }
    
    public String getTiptitle()
    {
        return tipTitle;
    }

    public static class LogConfig{
        public String output;
        public Integer logLevel;
        /* 模块集合*/
        public int moduleSet=0;
        public boolean isSaved;
        //public Map<String,Integer> modules = new HashMap<String,Integer>();
        public List<String> moduleNameList = new ArrayList<String>();
        public RandomAccessFile logFile;
    }
    
    public static class PushMessageServer{
        public String host;
        public Integer port;
        
    }
    
    public static class UpgradeServer{
        public String url;
    }
    
    public static class ResDir{
        public String apkDir;
    }
    
    private static Config config;
    private Config(){
    }
    public static Config getInstance(){
        if(config==null){
           config = new Config();
        }
        return config;
    }
    
    public static Config getInstance(Context ctx){
        context = ctx;
        String dataFilePath = context.getFilesDir().getAbsolutePath();
        Config.DATA_PACKAGE = dataFilePath.substring(0, dataFilePath.lastIndexOf(File.separator))+File.separator;
        return getInstance();
    }
    
    public RandomAccessFile getLogFile(){
        return this.logConfig.logFile;
    }
    /**
     * 取得升级路径
     * @return
     */
    public String getUpgradeFileServer(){
        return this.upgradeServer.url;
    }
    /**
     * 取得推送ip
     * @return
     */
    public String getPushMessageServerIP(){
        return this.pushMessageServer.host;
    }
    
    /**
     * 取得推送port
     * @return
     */
    public int getPushMessageServerPort(){
        return this.pushMessageServer.port;
    }
    /**
     * 获取日志总级别
     * @return
     */
    public int getLogLevel(){
        return this.logConfig.logLevel;
    }
    /**
     * 获取日志输出路径
     * @return
     */
    public String getLogFilePath(){
        return this.logConfig.output;
    }
    
    public int getModuleSet(){
        return this.logConfig.moduleSet;
    }
    
    public LogConfig getLogConfig()
    {
        return logConfig;
    }
    
    public void setLogConfig(LogConfig logConfig)
    {
        this.logConfig = logConfig;
    }
    
    public PushMessageServer getPushMessageServer()
    {
        return pushMessageServer;
    }
    
    public void setPushMessageServer(PushMessageServer pushMessageServer)
    {
        this.pushMessageServer = pushMessageServer;
    }
    
    public UpgradeServer getUpgradeServer()
    {
        return upgradeServer;
    }
    
    public void setUpgradeServer(UpgradeServer upgradeServer)
    {
        this.upgradeServer = upgradeServer;
    }

}
