package txl;

import java.io.File;

import txl.config.AppConstants;
import txl.config.TxlConstants;
import txl.util.IntentUtil;
import android.content.Context;
import android.util.Log;
import android.webkit.WebStorage;



/**
 * 在启动客户端时，调用init方法
 * @author jinchao
 *
 */
public class AppSystem
{

    private static final String TAG =  AppSystem.class.getSimpleName();
    private boolean             ignore;
    
    private static String dataDirPath;
    private static String fileDirPath;
    public AppSystem(){
        
    }
    public AppSystem(Context context){
    }
    
    public static void init(Context context)
    {
       
        //Log.d(TAG, " data file dir : "+ context.getFilesDir().getAbsolutePath());
        
        fileDirPath = context.getFilesDir().getAbsolutePath();
        dataDirPath = fileDirPath.substring(0, fileDirPath.lastIndexOf(File.separator))+File.separator;
        
        Log.d(TAG," dataDirPath: "+ dataDirPath);

        // 目录初始化
        initDirs();
         
        
    }

    public static void initDirs()
    {
        File file = new File(AppConstants.RES_PATH);
        if (!file.exists())
        {
            file.mkdirs();
        }
        file = new File(AppConstants.AUDIO_PATH);
        if (!file.exists())
        {
            file.mkdirs();
        }
        file = new File(AppConstants.VEDIO_PATH);
        if (!file.exists())
        {
            file.mkdirs();
        }
        file = new File(AppConstants.FILE_PATH);
        if (!file.exists())
        {
            file.mkdirs();
        }
        file = new File(AppConstants.IMAGE_PATH);
        if (!file.exists())
        {
            file.mkdirs();
        }

        file = new File(AppConstants.APK_PATH);
        if (!file.exists())
        {
            file.mkdirs();
        }
    }

    public void deleteFile(File file)
    {

        File[] files = file.listFiles();
        if (files != null)
        {
            if (files.length == 0)
            {
                Log.d(TAG, " file path :" + file.getAbsolutePath());
                file.delete();
                System.out.println(file.getAbsolutePath());
                return;

            } else
            {
                for (File f : files)
                {
                    deleteFile(f);
                }
            }
        }
        // else{
        Log.d(TAG, " file path :" + file.getAbsolutePath());
        file.delete();
        System.out.println("-----" + file.getAbsolutePath());
        // }
    }

    
    /**
     * 卸载MAW客户端
     */
    public void uninstall(Context context)
    {
        this.clearAllResources();
        this.clearData(context,context.getPackageName());
        this.dropDB(context, TxlConstants.DB_NAME);
        AppSystem.clearWebViewCache(context);
        context.startActivity(IntentUtil.getUninstallApkIntent());

        // kill process
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    
    public void dropDB(Context context,String name){
        boolean flag = context.deleteDatabase(name); 
        Log.d(TAG,"drop db ....  "+flag);
    }
     

    /**
     * 删除应用下的资源
     * 
     * @param packageName 应用包名
     */
    public void clearData(Context context,String packageName)
    {

        String dataUrl = AppSystem.dataDirPath;
        //Log.d(TAG, " dataUrl:   " + dataUrl);
        this.deleteFile(new File(dataUrl));
    }

    /**
     * 清除全部音频、视频、文件资源
     * 
     * @return
     */
    public boolean clearAllResources()
    {
        return this.clearResources(AppConstants.RES_PATH);
    }
    
    public void clearResrouces(){
       this.clearResources(AppConstants.APK_PATH); 
       this.clearResources(AppConstants.AUDIO_PATH);
       this.clearResources(AppConstants.FILE_PATH);
       this.clearResources(AppConstants.IMAGE_PATH);
       this.clearResources(AppConstants.VEDIO_PATH);
    }

    /**
     * 递归清除资源文件
     * 
     * @param path
     * @return
     */
    public boolean clearResources(String path)
    {
        File dir = new File(path);
        try
        {
            deleteFile(dir);
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }
    /**
     * 清除webview缓存
     * @param context
     */
    public static void clearWebViewCache(Context context)
    {
    	/* 高版本已经舍弃CacheManager*/
    	/*
        File file = CacheManager.getCacheFileBaseDir();
        if (file != null && file.exists() && file.isDirectory())
        {
            for (File item : file.listFiles())
            {
                item.delete();
            }
            file.delete();
            Log.d(TAG, " clearWebViewCache -> delete fileDir : " + file.getName());
        }
        context.deleteDatabase("webview.db");
        Log.d(TAG, " clearWebViewCache ->  delete db : webview.db ");
        context.deleteDatabase("webviewCache.db");
        Log.d(TAG, " clearWebViewCache ->  delete db : webviewCache.db ");*/
        
        WebStorage.getInstance().deleteAllData();
    }
    
    /**
     * clear the cache in mobile dir before time numDays     
     * @param dir
     * @param numDays
     * @return
     */
    private int clearCacheFolder(File dir, long numDays) {          
        int deletedFiles = 0;         
        if (dir!= null && dir.isDirectory()) {             
            try {                
                for (File child:dir.listFiles()) {    
                    if (child.isDirectory()) {              
                        deletedFiles += clearCacheFolder(child, numDays);          
                    }    
                    if (child.lastModified() < numDays) {     
                        if (child.delete()) {                   
                            deletedFiles++;           
                        }    
                    }    
                }             
            } catch(Exception e) {       
                e.printStackTrace();    
            }     
        }       
        return deletedFiles;     
    }    
    /**
     * 清除应用相关所有信息
     */
    public void clearApp(Context context){
        Log.d(TAG," clear app :clearApp...");
        
        this.clearResrouces();
        this.clearData(context,context.getPackageName());
    }
     
}
