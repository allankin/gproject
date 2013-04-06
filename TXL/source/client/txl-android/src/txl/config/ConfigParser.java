package txl.config;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import txl.activity.R;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;


public class ConfigParser
{

    /**
     * 加载配置文件，生成配置对象。
     * 
     * @param context
     * @return
     * @throws Throwable
     */
    public static Config init(Context context)
    {
        Config config = Config.getInstance(context);

        Config.PushMessageServer pushMessageServer = new Config.PushMessageServer();
        Config.UpgradeServer upgradeServer = new Config.UpgradeServer();
        Config.LogConfig logConfig = new Config.LogConfig();
        Config.ResDir resDir = new Config.ResDir();
        config.setLogConfig(logConfig);
        config.setPushMessageServer(pushMessageServer);
        config.setUpgradeServer(upgradeServer);
        config.setResDir(resDir);

        InputStream inStream = context.getResources().openRawResource(R.raw.config);

        XmlPullParser parser = Xml.newPullParser();
        int eventType = 0;
        try
        {
            parser.setInput(inStream, "UTF-8");
            eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT)
            {

                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        /* 获取节点名 */
                        String name = parser.getName();
                        if ("updateFileServer".equals(name))
                        {
                            upgradeServer.url = parser.getAttributeValue(null, "url");
                        } else if ("pushMessageServer".equals(name))
                        {
                            pushMessageServer.host = parser.getAttributeValue(null, "host");
                            String port = parser.getAttributeValue(null, "port");
                            if (port == null || port.length() == 0)
                            {
                                pushMessageServer.port = null;
                            } else
                            {
                                pushMessageServer.port = Integer.parseInt(port);
                            }
                        } else if ("logConfig".equals(name))
                        {
                            String level = parser.getAttributeValue(null, "level");
                            level = level == null ? "error" : level;
                            logConfig.logLevel = getLevelInteger(level);
                            String output = parser.getAttributeValue(null, "output");
                            logConfig.output = output;
                            if (output != null && output.length() > 0)
                            {
                                
                                if(Environment.getExternalStorageState().equals(  
                                                                                Environment.MEDIA_MOUNTED)){
                                    output = TxlConstants.SDCARD_DIRECTORY.getAbsolutePath() + File.separator + output;
                                    File outputFile = new File(output);
                                    if(!outputFile.exists()){
                                        outputFile.createNewFile();
                                    }
                                    logConfig.output = output;
                                    logConfig.logFile = new RandomAccessFile(logConfig.output,"rw");
                                    logConfig.logFile.seek(logConfig.logFile.length());
                                    logConfig.isSaved = true;
                                }
                            }
                        } else if ("module".equals(name))
                        {
                            String moduleName = parser.getAttributeValue(null, "name");

                            if (moduleName != null)
                            {
                                logConfig.moduleNameList.add(moduleName);
                                // String level = parser.getAttributeValue(null, "level");
                                // level = level==null?"error":level;
                                // logConfig.modules.put(moduleName, getLevelInteger(level));
                            }
                        }else if("resDir".equals(name)){
                            String apkDir = parser.getAttributeValue(null,"apkDir");
                       
                            if(apkDir!=null){
                                apkDir = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + apkDir;
                                if(Environment.getExternalStorageState().equals(  
                                                                                Environment.MEDIA_MOUNTED)){
                                    File apkDirFile = new File(apkDir);
                                    if(!apkDirFile.exists()){
                                       apkDirFile.mkdirs(); 
                                    }
                                }
                                resDir.apkDir = apkDir;
                            }
                            
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }

            // 获取输出模块集合
            List<String> moduleNameList = config.getLogConfig().moduleNameList;
            if (moduleNameList.isEmpty())
            {
                config.getLogConfig().moduleSet = 0xFFFF;
            } else
            {
                for (String moduleName : moduleNameList)
                {
                    config.getLogConfig().moduleSet |= TxlConstants.moduleMap.get(moduleName);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return config;
    }

    private static int getLevelInteger(String level)
    {
        if ("verbose".equals(level))
        {
            return TxlConstants.LOG_LEVEL_VERBOSE;
        } else if ("info".equals(level))
        {
            return TxlConstants.LOG_LEVEL_INFO;
        } else if ("warn".equals(level))
        {
            return TxlConstants.LOG_LEVEL_WARN;
        } else if ("error".equals(level))
        {
            return TxlConstants.LOG_LEVEL_ERROR;
        } else if ("off".equals(level))
        {
            return TxlConstants.LOG_LEVEL_OFF;
        }
        return 0;
    }
}
