package txl.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import txl.config.Config;
import txl.config.TxlConstants;

import android.os.Environment;
import android.util.Log;


/**
 * @ClassName:  HSLogger.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-3-29 下午2:40:36
 * @Copyright: 版权由 HundSun 拥有
 */
public class TxLogger
{
    private Class  clazz ;
    private int moduleId;
    private int curLogLevel;
    private boolean isLogModule;
    private String tag;
    private String moduleName;
    private long startTime;
    
    private TxLogger(){
        
    }
    
    /*public HSLogger(Class clazz){
        this.clazz = clazz;
        initLevel();
    }
    public HSLogger(int moduleId){
        this.moduleId = moduleId;
        initLevel();
    }*/
    public TxLogger(Class clazz,int moduleId){
        this.clazz = clazz;
        this.moduleId = moduleId;
        this.tag = clazz.getSimpleName();
        this.moduleName = TxlConstants.moduleMap2.get(moduleId);
        initLevel();
    }
    
    public void startTiming(){
    	this.startTime = System.currentTimeMillis();
    }
    public void endTiming(String _tag){
    	long endTime = System.currentTimeMillis();
    	long millTime = endTime-startTime;
    	String msg = _tag+" 供耗时："+millTime+", (秒："+(millTime/1000.0)+")";
    	Log.i(tag, msg);
    }
    private void initLevel(){
       curLogLevel = Config.getInstance().getLogLevel();
       int moduleSet = Config.getInstance().getModuleSet();
       if((moduleSet & this.moduleId) !=0){
          isLogModule = true; 
       }
    }
    
    public void verbose(String msg){
        if(isLogModule){
           if(TxlConstants.LOG_LEVEL_VERBOSE >= curLogLevel){
               Log.v(tag, msg);
               saveToFile(msg); 
           }
        } 
    }
    
    
    public void info(String msg){
        if(isLogModule){
            if(TxlConstants.LOG_LEVEL_INFO >= curLogLevel){
                Log.i(tag, msg);
                saveToFile(msg); 
            }
        } 
    }
    
    public void warn(String msg){
        if(isLogModule){
            if(TxlConstants.LOG_LEVEL_WARN >= curLogLevel){
                Log.w(tag, msg);
                saveToFile(msg); 
            }
        }
    }
    
    public void error(String msg){
        if(isLogModule){
            if(TxlConstants.LOG_LEVEL_ERROR >= curLogLevel){
                Log.e(tag, msg);
                saveToFile(msg); 
            }
        }
    }
    
    public void saveToFile(String msg){
        if(Config.getInstance().getLogConfig().isSaved){
            String logFilePath = Config.getInstance().getLogFilePath();
            if(Environment.getExternalStorageState().equals(  
                         Environment.MEDIA_MOUNTED)){
                /*try
                {
                    File logFile = new File(logFilePath);
                    if(logFile.exists()){
                        RandomAccessFile rf = new RandomAccessFile(logFilePath,"rw");
                        rf.seek(rf.length()); 
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        String dStr = sdf.format(new Date(System.currentTimeMillis()));
                        rf.write(("["+dStr+"]-[c:"+this.tag+"]-[m:"+this.moduleName+"]-------"+msg).getBytes("UTF-8"));
                        rf.writeBytes("\r\n");
                       
                        rf.close();
                    }else{
                        logFile.createNewFile();
                    }
                     
                } catch (FileNotFoundException e1)
                {
                    e1.printStackTrace();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }*/
                try
                {
                    synchronized(TxLogger.class){
                        RandomAccessFile rf = Config.getInstance().getLogFile();
                        if(rf!=null){
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            String dStr = sdf.format(new Date(System.currentTimeMillis()));
                            rf.write(("["+dStr+"]-[c:"+this.tag+"]-[m:"+this.moduleName+"]-------"+msg).getBytes("UTF-8"));
                            rf.writeBytes("\r\n");
                        }
                    }
                } catch (FileNotFoundException e1)
                {
                    e1.printStackTrace();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
