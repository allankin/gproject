package txl.message.pushmessage.biz;

import java.nio.channels.SocketChannel;

import org.json.JSONObject;

import txl.config.Config;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.core.MessageService;
import android.content.Intent;


/**
 * @ClassName:  OfflineRunnable.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-2-28 上午10:37:37
 */
public class OfflineRunnable implements BizRunnable
{
	private TxLogger log = new TxLogger(OfflineRunnable.class, TxlConstants.MODULE_ID_MESSAGE);    
    public void dealReply(JSONObject jobject)
    {
        Config.isKickOut = true;
        Intent intent = new Intent(TxlConstants.ACTION_OFFLINE_NOTICE);
        MessageService.context.sendBroadcast(intent);
        log.info("dealReply... ");
    }

}
