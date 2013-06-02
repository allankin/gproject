package txl;

import txl.activity.R;
import txl.common.TxlToast;
import txl.common.po.Account;
import txl.config.Config;
import txl.config.ConfigParser;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.upgrade.ResourceManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @ClassName:  LauncherActivity.java
 * @Description: 
 * @Author JinChao
 * @Date 2012-11-8 上午9:28:25
 */
public class SplashActivity extends TxlActivity
{
    public static final int SHOW_CONNECT_TOAST = 0x0001;
    private TxLogger log =null;    
    private TextView progressMessage;
    private ProgressBar progressBar;
    public static boolean finished = false;
    private int interval = 1;
    private int delayedTime = 100;
    public SplashActivity me = this;
    
    private TextView progressTv;
    private void setProgressMessage(final String msgStr) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressMessage.setText(msgStr);
            }
        });
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.splashlanch);
        /*设置异常处理类*/
        //CrashHandler.getInstance().init(this);
        Config.launcher = this;
        /*解析配置文件*/
        ConfigParser.init(this);
        //String url = Config.getInstance().getUpgradeFileServer();
        String url = TxlConstants.UPGRADE_CHECK_URL;
        if(url==null || url.trim().length()==0){
            delayedTime = 3000;
            findViewById(R.id.progress).setVisibility(View.GONE);
            findViewById(R.id.splash_message).setVisibility(View.GONE);
        }
        
        log = new TxLogger(SplashActivity.class, TxlConstants.MODULE_ID_SPLASHSCREEN);
        
        new TxlDbHelper(this).getWritableDatabase().close();
        Account.getSingle().load(this);
        
        progressTv = (TextView)findViewById(R.id.progressTv);
        checkUpgrade();
        
        /*Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            
            @Override
            public void run()
            {
                startActivity(new Intent(getApplication(),MawActivity.class));
                LauncherActivity.this.finish();
            }
        }, 2000);*/
    }
    public Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.what == Config.CHECKING_UPGRADE){
                setProgressMessage("正在检查更新...");
            }
            else if(msg.what == Config.BEGIN_DOWNLOAD){
            	setProgressMessage("开始下载升级包...");
            }
            else if(msg.what == Config.DOWNLOADING_RES){
            	String progress = (String)msg.obj;
            	progressTv.setText(progress);
                //progressBar.setProgress(pint);
                setProgressMessage("正在下载升级包...");
            }
            else if(msg.what == Config.DOWNLOADED_RES){
            	setProgressMessage("升级包下载完成");
            	//TxlToast.showShort(me, "升级包下载完成");
            }
            /*else if(msg.what == Config.LOADING_RES){
                progressBar.setProgress(1);
                setProgressMessage("正在加载资源");
            }*/else if(msg.what == Config.DOWNLOAD_RES_NOT_INTEGRATED){
            	TxlToast.showShort(me, "下载资源不完整，升级失败!");
            }
            
        }
    };
    
    
    
    public void checkUpgrade(){
        this.progressMessage = (TextView) findViewById(R.id.splash_message);
        this.progressBar = (ProgressBar) findViewById(R.id.progress);
        this.progressBar.setProgress(1);
        
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.incrementProgressBy(interval);
                if (!finished) {
                    handler.postDelayed(this, delayedTime);
                }else{
                    startActivity(new Intent(getApplication(),MainActivity.class));
                    SplashActivity.this.finish();
                }
            }
        }, delayedTime);
        
        //finished = true;
        ResourceManager.checkUpgrade(this);
         
    }
    
    
    
   /* public Handler LaunchHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SHOW_CONNECT_TOAST:
                    String tip = (String)msg.obj;
                    if(tip.equals(Constants.CONNECT_NET_ERROR)){
                        
                    }
                    
                    Toast.makeText(LauncherActivity.this, (String) msg.obj, Constants.Toast.SHORT);
                    break;
            }
        }
    };*/
    @Override
    protected void onNewIntent (Intent intent){
       log.info("onNewIntent");
       
    } 
    
    @Override
    protected void onPause()
    {
        super.onPause();
        log.info("onPause");
        
    }

    @Override
    protected void onStart(){
        super.onStart();
        log.info("onStart");
        
    }
    
    @Override
    protected void onRestart(){
        super.onRestart();
        log.info("onRestart");
        
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        log.info("onResume");
        
    }
    @Override
    protected void onStop(){
        super.onStop();
        log.info("onStop");
        
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        log.info("onDestroy");
    }

	@Override
	public Handler getHandler() {
		return this.handler;
	}
}
