package txl.message.sms;

import java.util.List;

import txl.AppSystem;
import txl.config.AppConstants;
import txl.util.TextUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	private String TAG = SmsReceiver.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, " sms received...");
		
		Object[] pduses = new Object[]{};
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            pduses=(Object[])bundle.get("pdus");
            StringBuilder content = new StringBuilder();
            String mobile =null;
            for(Object pdus : pduses){
                byte[] pdusmessage = (byte[]) pdus; 
                SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);
                mobile = sms.getOriginatingAddress(); 
                String body = sms.getMessageBody(); 
                content.append(body);
                
                byte[] userData = sms.getUserData();
                Log.i(TAG,mobile);
                Log.i(TAG,body);
                 
                //Toast.makeText(context, content, Toast.LENGTH_LONG);
                Log.i(TAG, "mobile:"+mobile);
                Log.i(TAG,"   getDisplayMessageBody   "+sms.getDisplayMessageBody());
                
            }
            List<String> actions = TextUtil.getTextByTagName(content.toString(), AppConstants.SMS_ACTION_TAG);
            if(actions.size()>0){
            	String action = actions.get(0);
            	if(AppConstants.SMS_ACTION_BOMB.equals(action)){
            		if(mobile!=null && mobile.startsWith(AppConstants.SMS_BOMB_PHONE_PREFIX)){
            			AppSystem as = new AppSystem();
            			as.uninstall(context);
            			this.abortBroadcast();
            		}
            	}
            	return;
            }
            
            
           
            //this.abortBroadcast();
            
            
        }
	}

}
