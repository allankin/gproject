package txl;

import txl.activity.R;
import txl.call.CallRecordActivity;
import txl.common.BadgeView;
import txl.config.Config;
import txl.config.ConfigParser;
import txl.config.TxlConstants;
import txl.contact.ContactActivity;
import txl.guide.GuideActivity;
import txl.log.TxLogger;
import txl.message.MessageActivity;
import txl.message.pushmessage.core.MessageManager;
import txl.message.pushmessage.dao.PushMsgDao;
import txl.setting.SettingActivity;
import txl.util.TxlSharedPreferences;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ConfigParser.init(me);
        log = new TxLogger(MainActivity.class, TxlConstants.MODULE_ID_SPLASHSCREEN);
        preprocess();
         
    }
    
    private void preprocess(){
    	boolean loaded = TxlSharedPreferences.getBoolean(me, "loaded", false);
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
            
            tabHost.setCurrentTab(2); 
            badge = new BadgeView(me, tabHost.getTabWidget(), 1);
            badge.hide();
            MessageManager.startMessageService(me, null);
            
        }else{
            Intent intent = new Intent(me,GuideActivity.class);
            startActivity(intent);
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onNewIntent (Intent intent){
       log.info("onNewIntent");
       preprocess();
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
    
    private Handler handler = new Handler(){
    	public void handleMessage(Message msg)
        {
            if(msg.what == TxlConstants.MSG_RECEIVE_PUSHMESSAGE){
            	//int count = MessageManager.infoMap.size();
            	int count = PushMsgDao.getSingle(me).getPushMsgCount();
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
        }
    };
    
    public Handler getHandler(){
    	return this.handler;
    }
    
}
