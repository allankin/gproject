package txl.util;

import java.io.File;

import txl.config.Config;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
 

public class IntentUtil
{

    private static final String TAG = "IntentUtil";
    private static IntentUtil   iu  = new IntentUtil();

    private IntentUtil()
    {

    }

    public static IntentUtil getInstance()
    {
        if (iu == null) iu = new IntentUtil();
        return iu;
    }

    /**
     * 获取图片浏览器
     * 
     * @param filePath
     * @return
     */
    public static Intent getImageIntent(String filePath)
    {
    	if(filePath.startsWith("file://")){
    		filePath = filePath.replace("file://", "");
    	}
        File file = new File(filePath);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "image/*");
        return intent;
    }

    /**
     * 获取pdf文件浏览器
     * 
     * @param filePath
     * @return
     */
    public static Intent getPdfFileIntent(String filePath)
    {
        Log.i(TAG, "filePath : " + filePath);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        // intent.addCategory(Intent.CATEGORY_DEFAULT);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/pdf");
        return intent;
    }

    /**
     * 获取msword文件浏览器
     * 
     * @param filePath
     * @param type doc : application/msword  
     * @return
     */
    public static Intent getWordFileIntent(String filePath,String type)
    {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = Uri.fromFile(new File(filePath));

        intent.setDataAndType(uri, type);

        return intent;

    }

    /**
     * 获取excel文件浏览器
     * 
     * @param filePath
     * @return
     */
    public static Intent getExcelFileIntent(String filePath)

    {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = Uri.fromFile(new File(filePath));

        intent.setDataAndType(uri, "application/vnd.ms-excel");

        return intent;

    }

    /**
     * 获取powerpoint文件浏览器
     * 
     * @param filePath
     * @return
     */
    public static Intent getPptFileIntent(String filePath)

    {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = Uri.fromFile(new File(filePath));

        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

        return intent;

    }

     

    /**
     * 获取txt浏览器
     * 
     * @param filePath
     * @return
     */
    public static Intent getTextFileIntent(String filePath)
    {
        Intent intent = new Intent("com.hundsun.maw.activity.FileViewerActivity");
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    /**
     * @deprecated 获取txt浏览器
     * @param param
     * @param paramBoolean true：param 需为 the given encoded URI string。 false: param 需为 文件路径格式
     * @return
     */
    public static Intent getTextFileIntent(String param, boolean paramBoolean)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean)
        {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else
        {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }

        return intent;
    }
    
    /**
     * 获取视频intent
     * @param param
     * @return
     */
    public static Intent getVideoFileIntent(String filePath)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }
    
    public static Intent getAudioFileIntent(String filePath){
    	Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    /**
     * apk安装器
     * 
     * @param filePath apk文件路径
     * @return
     */
    public static Intent getInstallApkIntent(String filePath)
    {
        filePath = filePath.replace("file://", "");
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        Log.d(TAG, " getInstallApkIntent , filePath: "+ filePath);
        return intent;
    }

    /**
     * 卸载intent
     * 
     * @param filePath
     * @return
     */
    public static Intent getUninstallApkIntent()
    {
        Uri packageURI = Uri.parse("package:" + Config.APP_PACKAGE_NAME);
        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * 获取短信列表
     * 
     * @return
     */
    public static Intent getSmsListIntent()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("vnd.android-dir/mms-sms");
        return intent;
    }
    /**
     * 根据threadId查看短信详情
     * @param threadId
     * @return
     */
    public static Intent getSmsDetailIntent(int threadId){
    	Intent intent = new Intent(Intent.ACTION_VIEW); 
    	intent.setData(Uri.parse("content://mms-sms/conversations/"+threadId)); 
    	return intent;
    }
    /**
     * 获取短信发送编辑框
     * 
     * @param content
     * @param phoneNumber 若为null，则不传接收人号码
     * @return
     */
    public static Intent getSmsSendDialogIntent(String content,String phoneNumber)
    {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW); 
        sendIntent.putExtra("sms_body", content); 
        sendIntent.setType("vnd.android-dir/mms-sms"); 
        if(phoneNumber!=null && phoneNumber.length()>0){
        	sendIntent.putExtra("address", phoneNumber);
        }
        return sendIntent;
    }

    /**
     * 拨打电话Intent
     * 
     * @param phoneNumber
     * @return
     */
    public static Intent getDialIntent(String phoneNumber)
    {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent it = new Intent(Intent.ACTION_DIAL, uri);
        return it;
    }
    public static Intent getCallIntent(String phoneNumber){
    	Intent intent=new Intent("android.intent.action.CALL",Uri.parse("tel:"+phoneNumber));
    	return intent;
    }
    /**
     * 获取文件浏览intent 通过解析实现
     * @param filePath
     * @return
     */
    public static Intent getFileViewerActivityIntent(String filePath){
        Intent intent = new Intent();
        intent.setAction("com.hundsun.maw.activity.FileViewerActivity");
        intent.setData(Uri.parse("file://com.hundsun.maw.activity"+filePath));
        return intent;
    }
    /**
     * @deprecated 发送短信
     * @param number
     * @param content
     * @return
     */
    public static Intent getSmsSendIntent(String number, String content)
    {
        Uri uri = Uri.parse("smsto:" + number);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        intent.setType("vnd.android-dir/mms-sms");
        return intent;
    }

   
}
