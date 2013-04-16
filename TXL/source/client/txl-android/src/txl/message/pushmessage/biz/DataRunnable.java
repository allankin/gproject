package txl.message.pushmessage.biz;

import org.json.JSONObject;

import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.core.SendMessageQueue;
import txl.message.pushmessage.po.PushMsg;



/**
 * @ClassName:  DataRunnable.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-2-25 下午2:56:14
 * @Copyright: 版权由 HundSun 拥有
 */
public class DataRunnable implements BizRunnable
{
    
	private TxLogger log = new TxLogger(DataRunnable.class, TxlConstants.MODULE_ID_MESSAGE);
	
    public void receive(final JSONObject jobject){
        new Thread(new Runnable()
        {
            public void run()
            {
              /* BillData bd = new BillData();
               bd.parseJSONObject(jobject);*/
               
               //消息通知提醒
               //MessageManager.showNotice(bd);
            	
            	log.info("receive : "+jobject.toString());
            }
        }).start();
    }
    
    public void send(PushMsg pushMsg){
    	synchronized (SendMessageQueue.queue) {
			SendMessageQueue.queue.add(pushMsg.toJSONStroing());
		}
    	log.info("send : "+pushMsg.toJSONStroing());
    }
    
   
   
}
