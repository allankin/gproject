package txl.message;

import txl.config.TxlConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * @ClassName:  BaseBroadcastReceiver.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-1-30 上午10:59:35
 */
public class MessageReceiver extends BroadcastReceiver
{
	private MessageActivity ctx;
	public MessageReceiver(MessageActivity ctx){
		this.ctx = ctx;
	}
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (TxlConstants.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            ctx.refreshPushMessageModule();
        }
    }
   
}
