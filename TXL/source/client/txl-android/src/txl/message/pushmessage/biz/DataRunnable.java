package txl.message.pushmessage.biz;

import java.sql.Timestamp;

import org.json.JSONObject;

import txl.common.po.Account;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.core.MessageManager;
import txl.message.pushmessage.core.SendMessageQueue;
import txl.message.pushmessage.po.PushMsg;
import txl.message.pushmessage.po.PushMsg;
import txl.util.Tool;



/**
 * @ClassName:  DataRunnable.java
 * @Description: 为文本消息处理器
 * @Author JinChao
 * @Date 2013-2-25 下午2:56:14
 */
public class DataRunnable implements BizRunnable
{
    
	private TxLogger log = new TxLogger(DataRunnable.class, TxlConstants.MODULE_ID_MESSAGE);
	
    public void receive(final JSONObject jobject){
        
        PushMsg rpm = new PushMsg();
        rpm.content = jobject.optString("c");
        //rpm.recUserId = jobject.optInt("u");
        rpm.recUserId = Account.getSingle().userId;
        rpm.sendName = jobject.optString("sn");
        rpm.sendUserId = jobject.optInt("s");
        rpm.msgId = jobject.optString("m");
        rpm.dtime = new Timestamp(System.currentTimeMillis());
        rpm.type = TxlConstants.PUSH_MESSAGE_TYPE_RECEIVE;
        log.info("receive : "+jobject.toString());
        
        MessageManager.dealData(rpm);
    }
    
    public void send(PushMsg pushMsg){
    	synchronized (SendMessageQueue.queue) {
			SendMessageQueue.queue.add(pushMsg.toJSONString());
			SendMessageQueue.queue.notifyAll();
		}
    	log.info("send : "+pushMsg.toJSONString());
    }
    
   
   
}
