package txl;

import txl.activity.R;
import txl.call.CallRecordActivity;
import txl.common.TxlToast;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.contact.ContactActivity;
import txl.log.TxLogger;
import txl.message.MessageActivity;
import txl.setting.SettingActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.Window;
import android.view.WindowManager;

public abstract class TxlActivity extends Activity {
	public abstract Handler getHandler();
	
	//private final TxLogger log = new TxLogger(TxlActivity.class, TxlConstants.MODULE_ID_BASE);
	public boolean isRunning;
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		Window window=getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.flags=WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		wl.alpha=1.0f;//这句就是设置窗口里控件的透明度的．０.０全透明．１.０不透明．
		window.setAttributes(wl);
		super.onCreate(savedInstanceState);
		
    }
	private long exitTime = 0;
	public boolean onKeyUp(int keyCode, KeyEvent event)
    {
	    /*log.info("className: "+this.getClass().getSimpleName()+"  ,onKeyUp... KEYCODE_BACK: "+keyCode);
        if(KeyEvent.KEYCODE_BACK == keyCode){
            Class clazz = this.getClass();    
            if(clazz.isAssignableFrom(CallRecordActivity.class)
                    ||clazz.isAssignableFrom(ContactActivity.class)
                    ||clazz.isAssignableFrom(MessageActivity.class)
                    ||clazz.isAssignableFrom(SettingActivity.class)
                    ){
                 moveTaskToBack(true);
                 
            }
            //return super.onKeyUp(keyCode, event); 
        }
        //return false;
        return true;*/
	    
	    if(keyCode == KeyEvent.KEYCODE_BACK   ){   
	        Class clazz = this.getClass();    
            if(clazz.isAssignableFrom(CallRecordActivity.class)
                    ||clazz.isAssignableFrom(ContactActivity.class)
                    ||clazz.isAssignableFrom(MessageActivity.class)
                    ||clazz.isAssignableFrom(SettingActivity.class)
                    ){
                if((System.currentTimeMillis()-exitTime) > 2000){  
                    TxlToast.showLong(TxlActivity.this, "再按一次退出程序");                                
                    exitTime = System.currentTimeMillis(); 
                    return true;
                } else {
                    //moveTaskToBack(true);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
                    intent.addCategory(Intent.CATEGORY_HOME);
                    this.startActivity(intent);
                    return true;
                }
            }
	    }
	    return super.onKeyUp(keyCode, event);
    }
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
		Class clazz = this.getClass();    
		if(clazz.isAssignableFrom(CallRecordActivity.class)||clazz.isAssignableFrom(ContactActivity.class)){
			getMenuInflater().inflate(R.menu.menu_common, menu);
    		int size = menu.size();
    		for(int i=0;i<size;i++){
    			MenuItem item = menu.getItem(i);
    			if(item.getItemId() == R.id.menu_setting){
    				item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							Config.tabHost.setCurrentTab(3);
							return false;
						}
					});
    			}
    		}
    	} 
        return true;
    }
	
	
	
}
