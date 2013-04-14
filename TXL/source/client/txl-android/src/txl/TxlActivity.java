package txl;

import txl.call.CallRecordActivity;
import txl.config.TxlConstants;
import txl.contact.ContactActivity;
import txl.log.TxLogger;
import txl.message.MessageActivity;
import txl.setting.SettingActivity;
import android.app.Activity;
import android.os.Handler;
import android.view.KeyEvent;

public abstract class TxlActivity extends Activity {
	public abstract Handler getHandler();
	
	private final TxLogger log = new TxLogger(TxlActivity.class, TxlConstants.MODULE_ID_BASE);
	public boolean isRunning;
	public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        
        switch (keyCode) {  
            case KeyEvent.KEYCODE_BACK:
                Class clazz = this.getClass();    
            	if(clazz.isAssignableFrom(CallRecordActivity.class)
            			||clazz.isAssignableFrom(ContactActivity.class)
            			||clazz.isAssignableFrom(MessageActivity.class)
            			||clazz.isAssignableFrom(SettingActivity.class)
            			){
            		 //moveTaskToBack(true);
            		 return true;
            	}
                log.info("className: "+this.getClass().getSimpleName()+"  ,onKeyUp... KEYCODE_BACK: "+keyCode);
                return super.onKeyUp(keyCode, event); 
                
            default: 
                    log.info("default  className: "+this.getClass().getSimpleName()+"  ,onKeyUp... KEYCODE_BACK: "+keyCode);
                return super.onKeyUp(keyCode, event);    
        }
    }
}
