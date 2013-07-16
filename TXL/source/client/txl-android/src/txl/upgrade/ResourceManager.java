package txl.upgrade;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.json.JSONObject;

import txl.SplashActivity;
import txl.TxlActivity;
import txl.activity.R;
import txl.common.TxlAlertDialog;
import txl.common.TxlAlertDialog.DialogInvoker;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.util.HttpClientUtil;
import txl.util.MD5Util;
import txl.util.Tool;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

 
/**
 * @ClassName: ResourceManager.java
 * @Description:
 * @Author JinChao
 * @Date 2013-3-6 上午10:23:40
 * @Copyright: 版权由 HundSun 拥有
 */
public class ResourceManager
{
    private static TxLogger log = new TxLogger(ResourceManager.class, TxlConstants.MODULE_ID_UPGRADE);
    static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    static DocumentBuilder        builder;
    static android.app.AlertDialog alert = null;
    static TxlActivity ctx=null;
    static
    {
        try
        {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
    }

    private static ResControl fetchUpgradeRes()
    {
        ResControl resControl = new ResControl();
        String content = null;
        //String upgradeUrl = Config.getInstance().getUpgradeFileServer();
        String upgradeUrl = TxlConstants.UPGRADE_CHECK_URL;
        if(upgradeUrl==null || upgradeUrl.length()==0){
            log.error("未配置升级服务标签...");
            return resControl;
        }
        Map<String, String> params = new HashMap<String, String>();

        //DeviceUtil dutil = new DeviceUtil(context);
        /*params.put("os", "Android");
        params.put("osv", dutil.osRelease);
        params.put("res", dutil.widthPixels + "x" + dutil.heightPixels);
        params.put("model", dutil.model);
        params.put("av", context.getResources().getInteger(R.integer.apkVersionCode) + "");*/
        params.put("curApkVersionCode", String.valueOf(ctx.getResources().getInteger(R.integer.apkVersionCode)));
        try {
			content = HttpClientUtil.httpPostUTF8(upgradeUrl, params);
		} catch (HttpHostConnectException e1) {
			e1.printStackTrace();
			content= "";
		} catch (ConnectTimeoutException e1) {
			e1.printStackTrace();
			content = "";
		}
        
        try {
			JSONObject json = new JSONObject(content);
			String apkUrl = json.optString("apkUrl");
			if(apkUrl.length()>0){
				Res res = new Res();
				res.url = TxlConstants.DOWNLOAD_APK_URL_PREFIX+apkUrl;
				res.type = "apk";
				res.verifyCode = json.optString("verifyCode");
				resControl.resList.add(res);
				log.info("url: "+res.url+",verifyCode:"+res.verifyCode);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
         
        return resControl;
    }

    /**
     * 检测升级
     * 
     * @param activity
     */
    public static void checkUpgrade(final TxlActivity activity,final boolean isAuto)
    {
    	ctx = activity;
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {

                Tool.sendUpgradeMessage(ctx,TxlConstants.MSG_CHECKING_UPGRADE);
                final ResControl resControl = fetchUpgradeRes();
                int resCount = resControl.resList.size();
                // 不需要升级
                if (resCount == 0)
                {
                	Tool.sendUpgradeMessage(ctx,TxlConstants.MSG_CHECKING_UPGRADE_NEEDNOT);
                    SplashActivity.finished = true;
                } else
                {
                    // 必须升级
                    if (resControl.isEnfore)
                    {
                        doUpgrade(resControl.resList, true);
                    } else
                    {
                        activity.runOnUiThread(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                            	/*欢迎页面，自动触发检查升级包 */
                            	if(isAuto){
                            		TxlAlertDialog.show(activity, "有新版本需要更新", "确定,取消", new DialogInvoker()
                            		{
                            			
                            			@Override
                            			public void doInvoke(DialogInterface dialog, int btndex)
                            			{
                            				if (btndex == TxlAlertDialog.FIRST_BTN_INDEX)
                            				{
                            					TxlAlertDialog.alert.dismiss();
                            					doUpgrade(resControl.resList, false);
                            				}else{
                            					SplashActivity.finished = true;
                            				}
                            			}
                            		});
                            	}else{
                            		doUpgrade(resControl.resList, false);
                            	}
                            }
                        });
                    }

                }
            }
        }).start();

    }

    private static void doUpgrade(final List<Res> resList, final boolean isEnforce)
    {
        int resCount = resList.size();
        if (resCount == 1)
        {
            Res res = resList.get(0);
            if(!Environment.getExternalStorageState().equals(  
                                                            Environment.MEDIA_MOUNTED)){
            	ctx.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
					 	android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Config.launcher);
                        builder.setTitle(Config.getInstance().getTiptitle());
                        builder.setMessage("未检测到存储卡,下载取消！").setCancelable(false);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                SplashActivity.finished = true;
                                alert.dismiss();
                            }
                        });  
                        alert = builder.create();
                        alert.show();
					}
				});
            	/*
                Config.launcher.handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                       
                    }
                });*/
               
                
            }else{
                doDownload(res, isEnforce);
            }
            
        }

    }

    private static void afterApkDownload(Res res, final String apkPath, boolean isEnforce)
    {
        if (!verifyRes(res.verifyCode, apkPath))
        {
            Tool.sendUpgradeMessageWithObj(ctx,TxlConstants.MSG_DOWNLOAD_RES_NOT_INTEGRATED,isEnforce);
            // 强制升级，不完整退出
            if (isEnforce)
            {
                try
                {
                    Thread.sleep(2000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                ctx.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            } else
            {
            	try
                {
                    Thread.sleep(2000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                SplashActivity.finished = true;
            }
        } else
        {
            if (isEnforce)
            {
                install(apkPath);
            } else
            {
                ctx.runOnUiThread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                    	TxlAlertDialog.show(ctx, "升级包下载完毕,需要升级吗？", "确定,取消",
                                            new TxlAlertDialog.DialogInvoker()
                                            {

                                                @Override
                                                public void doInvoke(DialogInterface dialog, int btndex)
                                                {
                                                    if (btndex == TxlAlertDialog.FIRST_BTN_INDEX)
                                                    {
                                                        install(apkPath);
                                                    } else if (btndex == TxlAlertDialog.SENCOND_BTN_INDEX)
                                                    {
                                                    	Tool.sendUpgradeMessage(ctx,TxlConstants.MSG_NOT_INSTALL_NOW);
                                                        SplashActivity.finished = true;
                                                    }
                                                }
                                            });
                    }

                });

            }
        }
    }

    private static void install(String apkPath)
    {
        apkPath = apkPath.replace("file://", "");
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        ctx.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 根据文件长度验证
     * 
     * @param verifyCode
     * @param filePath
     * @return
     */
    private static boolean verifyRes(String verifyCode, String filePath)
    {
        boolean flag = false;
        File file = new File(filePath);
        long fileLen = file.length();
        String md5= MD5Util.md5(String.valueOf(fileLen));
        log.info("verifyCode:"+verifyCode+",fileLen:"+fileLen+",genMd5:"+md5);
        if (verifyCode.equalsIgnoreCase(md5))
        {
            flag = true;
        }
        return flag;
    }

    /*private static void backupFile(Res res)
    {
        if ("js".equals(res.type))
        {
            String fileName = res.url.substring(res.url.lastIndexOf("/") + 1);

        } else if ("css".equals(res.type))
        {

        }
    }*/

    private static void doDownload(final Res res, final boolean isEnforce)
    {
    	Tool.sendUpgradeMessage(ctx,TxlConstants.MSG_BEGIN_DOWNLOAD);
        // DESUtil encry = new DESUtil();
        // final String urlMD5 = MD5Util.md5(res.url);

        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                final String fileName = res.url.substring(res.url.lastIndexOf("/") + 1);
                String filePath = "";
                try
                {
                    int hasDownloadSize = 0;
                    
                    String saveFile = Config.getInstance().getApkDirFileString(fileName);
                    File apkFile = new File(saveFile);
                    
                    if (apkFile!=null && apkFile.exists())
                    {
                        apkFile.delete();
                        /*
                         * HS_TODO: 断点续传需要服务器支持
                        FileInputStream fis = new FileInputStream(apkFile);
                        hasDownloadSize = fis.available();
                        log.info(fileName+" has downloaded size: "+hasDownloadSize);
                        fis.close();
                        */
                    }
                    
                	log.info("begin connect  upgrade url  ...");
                    URL url = new URL(res.url);
                    url.getFile();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    if (conn.getResponseCode() == 200)
                    {
                        final int fileSize = conn.getContentLength(); 
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("Range", "bytes=" + hasDownloadSize + "-"+(fileSize-1));
                        log.info("bytes="+hasDownloadSize + "-"+(fileSize-1));
                        conn.connect();
                        log.info("upgrade url connected .... ResponseCode: "+conn.getResponseCode());
                        Tool.sendUpgradeMessage(ctx,TxlConstants.MSG_DOWNLOADING_RES_SHOW_PROCESSBAR);
                        if (conn.getResponseCode() == 200 || conn.getResponseCode() == 206)
                        {
                            if (hasDownloadSize < fileSize)
                            {
                                int byteSize = 10240;
                                byte[] buffer = new byte[byteSize];
                                int offset = 0;
                                //int startPos = hasDownloadSize > 0 ? hasDownloadSize - 1 : hasDownloadSize;
                                int startPos = hasDownloadSize;
                                if(Environment.getExternalStorageState().equals(  
                                                                                Environment.MEDIA_MOUNTED)){
                                     
                                    RandomAccessFile threadfile = new RandomAccessFile(saveFile, "rwd");
                                    log.info(" file seek to position: "+startPos);
                                    threadfile.seek(startPos);
                                    InputStream inStream = new BufferedInputStream(url.openStream());
                                    int downFlag = 0;
                                    while ((offset = inStream.read(buffer)) != -1)
                                    {
                                        hasDownloadSize += offset;
                                        threadfile.write(buffer, 0, offset);
                                        Thread.currentThread().sleep(50);
                                        int progress = Math.round(hasDownloadSize/Float.valueOf(fileSize)*100);
                                        Tool.sendUpgradeMessageWithObj(ctx,TxlConstants.MSG_DOWNLOADING_RES,progress+"%");
                                        log.info("hasDownloadSize:"+hasDownloadSize);
                                        /*int _downFlag = progress/10;
                                        if(_downFlag>downFlag){
                                            Tool.sendUpgradeMessageWithObj(ctx,Config.DOWNLOADING_RES,progress+"%");
                                            downFlag = _downFlag;
                                        }*/
                                        
                                    }
                                    threadfile.close();
                                    inStream.close();
                                }
                                

                            }
                            if (hasDownloadSize >= fileSize)
                            {
                                filePath = saveFile;
                            }
                        }
                    }
                    
                    if(filePath.length()>0){
                    	Tool.sendUpgradeMessageWithObj(ctx,TxlConstants.MSG_DOWNLOADING_RES,"100%");
                    	if("apk".equals(res.type)){
                    		Tool.sendUpgradeMessage(ctx,TxlConstants.MSG_DOWNLOADED_RES);
                    		afterApkDownload(res, filePath, isEnforce);
                    	}
                    }
                    
                } catch (Exception e)
                {
                	Tool.sendUpgradeMessage(ctx,TxlConstants.MSG_CHECK_UPGRADE_ERROR);
                    e.printStackTrace();
                }

            }

        }).start();

        /*
         * synchronized (MawActivity.waitRunnable) { MawActivity.waitRunnable.notifyAll(); }
         */

    }
    /*
     * public static void doDownload(List<Res> resList){ for(Res res:resList){ } }
     */

}
