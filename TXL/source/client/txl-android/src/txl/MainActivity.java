package txl;

import txl.activity.R;
import txl.call.CallRecordActivity;
import txl.common.BadgeView;
import txl.common.TxlAlertDialog;
import txl.common.TxlAlertDialog.DialogInvoker;
import txl.config.Config;
import txl.config.ConfigParser;
import txl.config.TxlConstants;
import txl.contact.ContactActivity;
import txl.guide.GuideActivity;
import txl.log.TxLogger;
import txl.message.MessageActivity;
import txl.message.pushmessage.PushMessageActivity;
import txl.message.pushmessage.core.MessageManager;
import txl.message.pushmessage.dao.PushMsgDao;
import txl.message.sms.SmsReceiver;
import txl.setting.SettingActivity;
import txl.test.sidecom.Main;
import txl.util.Tool;
import txl.util.TxlSharedPreferences;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends TabActivity
{
    private static int tabtxtsize=13;
    private TabHost tabHost;
    private  TxLogger log ;
    private MainActivity me = this;
    private BadgeView badge;
    
    private TxlReceiver txlReceiver;
    private String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ConfigParser.init(me);
        log = new TxLogger(MainActivity.class, TxlConstants.MODULE_ID_SPLASHSCREEN);
        log.info("onCreate");
        
        AppSystem.init(this);
        
        boolean isAppServiceRunning = Tool.isServiceRunning(AppService.class, this);
        Log.i(TAG, "isAppServiceRunning... :"+isAppServiceRunning);
        /*appService判断是否启动*/
        //if(!isAppServiceRunning){
        	Intent appServiceIntent = new Intent(this,AppService.class);
            startService(appServiceIntent);
        //}
        
        Intent intent = this.getIntent();
        if(intent!=null){
        	String action = intent.getStringExtra(TxlConstants.INTENT_BUNDLE_ACTION);
        	if("message".equals(action)){
        		preprocess(1);
        		/*FrameLayout tabContent = (FrameLayout)findViewById(android.R.id.tabcontent);
            RadioGroup messageType = (RadioGroup)tabContent.findViewById(R.id.message_type);
            messageType.check(R.id.pushmsg);*/
        		int pushMsgType = intent.getIntExtra(TxlConstants.INTENT_BUNDLE_PUSHMSG_TYPE, 0);
        		log.info("pushMsgType:"+pushMsgType);
        		if(pushMsgType!=0){
        			Intent i = new Intent(me,PushMessageActivity.class);
        			if(pushMsgType == TxlConstants.PUSHMSG_TYPE_NOT_CLASSFIED){
        				i.putExtra(TxlConstants.INTENT_BUNDLE_PUSHMSG_TYPE, pushMsgType);
        				i.putExtra(TxlConstants.INTENT_BUNDLE_CONTACT_ID, intent.getIntExtra(TxlConstants.INTENT_BUNDLE_CONTACT_ID,0));
        				i.putExtra(TxlConstants.INTENT_BUNDLE_CONTACT_NAME,  intent.getStringExtra(TxlConstants.INTENT_BUNDLE_CONTACT_NAME));
        			}else{
        				i.putExtra(TxlConstants.INTENT_BUNDLE_PUSHMSG_TYPE, pushMsgType);
        				i.putExtra(TxlConstants.INTENT_BUNDLE_PUSHMSG_TYPE_NAME, intent.getStringExtra(TxlConstants.INTENT_BUNDLE_PUSHMSG_TYPE_NAME));
        			}
        			me.startActivity(i);
        		}
        	}else{
        		preprocess(0);
        	}
        }else{
        	preprocess(0);
        } 
        
        txlReceiver = new TxlReceiver(me);
        IntentFilter filter = new IntentFilter(TxlConstants.ACTION_OFFLINE_NOTICE);
		me.registerReceiver(txlReceiver, filter);
		
		intent.setData(null);
    }
    
    private void preprocess(int tabIndex){
    	boolean loaded = TxlSharedPreferences.getBoolean(me, "loaded", false);
    	// HS_TODO: 暂不适合用引导页
    	loaded = true;
        if(loaded){
            final View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
            setContentView(view);
            tabHost = getTabHost();
            Config.tabHost = tabHost;
            Config.mainContext = this;
            Intent intent = new Intent().setClass(this, CallRecordActivity.class);
            setupTab(TxlConstants.TAB_ITEM_DIAL, intent);
            
            intent = new Intent().setClass(this, MessageActivity.class);
            setupTab(TxlConstants.TAB_ITEM_MESSAGE, intent);
            
            
            intent = new Intent().setClass(this, ContactActivity.class);
            setupTab(TxlConstants.TAB_ITEM_CONTACT, intent);
            
            intent = new Intent().setClass(this, SettingActivity.class);
            setupTab(TxlConstants.TAB_ITEM_SETTING, intent);
            //tabHost.setPadding(tabHost.getPaddingLeft(), tabHost.getPaddingTop(), tabHost.getPaddingRight(), tabHost.getPaddingBottom()-10); 
            
            tabHost.setCurrentTab(tabIndex); 
            setTabBackground();
            
            ajustTabContent();
            
            badge = new BadgeView(me, tabHost.getTabWidget(), 1);
            badge.hide();
            MessageManager.startMessageService(me, null,null,null);
            showMessageBadge();
        }else{
            Intent intent = new Intent(me,GuideActivity.class);
            startActivity(intent);
        }
    }
    
    private void ajustTabContent(){
    	final FrameLayout tabContent = (FrameLayout)findViewById(android.R.id.tabcontent);
    	tabContent.postDelayed(new Runnable()
        {
            
            @Override
            public void run()
            {
            	WindowManager wm = (WindowManager) me.getSystemService(Context.WINDOW_SERVICE);
            	int height = wm.getDefaultDisplay().getHeight();  
        		DisplayMetrics metrics = me.getResources().getDisplayMetrics();
        		Rect frame = new Rect();
        		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        		int statusBarHeight = frame.top;
                 
            	TabWidget tab = (TabWidget)findViewById(android.R.id.tabs);
                int ftpx = tab.getHeight();
                int tabContentHeight = height-statusBarHeight-ftpx;
                
                log.info("height: "+height+" width:"+wm.getDefaultDisplay().getWidth()
                		+", heightPixels:"+metrics.heightPixels+", widthPixels:"+metrics.widthPixels
                		+",tabContentHeight:"+tabContentHeight+",tabHeight:"+ftpx);
                TxlConstants.tabContentHeight = tabContentHeight;
                tabContent.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,tabContentHeight));
                tabContent.invalidate();
            }
        },500);
    }
    private void setTabBackground(){
    	TabWidget tabs = this.tabHost.getTabWidget();
    	int len = tabs.getChildCount();
    	for(int i=0;i<len;i++){
    		tabs.getChildAt(i).setBackgroundResource(R.drawable.bg_tab);
    	}
    }
    
    
    private void setupTab(final String tag,Intent intent) {
        /*View tabview = createTabView(tabHost.getContext(), tag);
        TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);*/
    	Drawable tabIcon = null;
    	if(tag.contains(TxlConstants.TAB_ITEM_DIAL)){
    		tabIcon = getResources().getDrawable(R.drawable.bg_tab_dial);
    	}else if(tag.contains(TxlConstants.TAB_ITEM_MESSAGE)){
    		tabIcon = getResources().getDrawable(R.drawable.bg_tab_message);
    	}else if(tag.contains(TxlConstants.TAB_ITEM_CONTACT)){
    		tabIcon = getResources().getDrawable(R.drawable.bg_tab_contact);
    	}else{
    		tabIcon = getResources().getDrawable(R.drawable.bg_tab_setting);
    	}
    	
        TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tag,tabIcon).setContent(intent);
        tabHost.addTab(setContent);

    }
    private static View createTabView(final Context context, final String text) {
    	View view = LayoutInflater.from(context).inflate(R.layout.tbitem, null);
    	if(text.contains(TxlConstants.TAB_ITEM_DIAL)){
    		view.setBackgroundResource(R.drawable.bg_tab_dial);
    	}else if(text.contains(TxlConstants.TAB_ITEM_MESSAGE)){
    		view.setBackgroundResource(R.drawable.bg_tab_dial);
    	}else if(text.contains(TxlConstants.TAB_ITEM_CONTACT)){
    		view.setBackgroundResource(R.drawable.bg_tab_dial);
    	}else{
    		view.setBackgroundResource(R.drawable.bg_tab_dial);
    	}
       
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setTextSize(tabtxtsize);
        tv.setText(text);
        return view;
    }

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
        me.unregisterReceiver(txlReceiver);
        log.info("onDestroy");
    } 
    
    private Handler handler = new Handler(){
    	public void handleMessage(Message msg)
        {
            if(msg.what == TxlConstants.MSG_RECEIVE_PUSHMESSAGE){
            	//int count = MessageManager.infoMap.size();
            	showMessageBadge();
            }
        }
    };
    
    private void showMessageBadge(){
    	int count = PushMsgDao.getSingle(me).getUnreadPushMsgCount();
    	log.info("msg count: "+count);
    	if(count!=0){
    		if(!badge.isShown()){
        		badge.show();
        	}
    		badge.setText(String.valueOf(count));
    	}else{
    		badge.hide();
    	}
    }
    
    public Handler getHandler(){
    	return this.handler;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            boolean flag = this.isTaskRoot();
            if(flag){
                TxlAlertDialog.show(me, "确定退出吗?", "确定,取消", new DialogInvoker()
                {
                    @Override
                    public void doInvoke(DialogInterface dialog, int btndex)
                    {   
                        if(btndex == TxlAlertDialog.FIRST_BTN_INDEX){
                            me.moveTaskToBack(true);
                        }
                    }
                });
            }
        } 
        return false;
    }
}
