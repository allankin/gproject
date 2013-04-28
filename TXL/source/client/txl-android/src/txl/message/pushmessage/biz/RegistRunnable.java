package txl.message.pushmessage.biz;

import org.json.JSONObject;

import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.core.SendMessageQueue;

/**
 * @ClassName: RegistRunnable.java
 * @Description:
 * @Author JinChao
 * @Date 2013-2-25 上午09:22:33
 */
public class RegistRunnable implements BizRunnable {
	private TxLogger log = new TxLogger(RegistRunnable.class,
			TxlConstants.MODULE_ID_MESSAGE);
	
	public void sendRequest(int userId) {
		String str = "{\"u\":" + userId + ",\"b\":"+TxlConstants.BIZID_REQUEST_REGIST+"}";
		synchronized (SendMessageQueue.queue) {
			SendMessageQueue.queue.add(str);
			SendMessageQueue.queue.notifyAll();
		}
		log.info("sendRequest: " + str);
	}

	public void dealReply(final JSONObject jobj) {
		HeartBeatRunnable heartBeatRunnable = (HeartBeatRunnable) RunnableManager
				.getRunnable(TxlConstants.BIZID_REQUEST_HEARTBEAT);
		//HS_TODO: 要求保存账号信息(暂默认)
		int userId = Account.getSingle().userId;
		heartBeatRunnable.sendRequest(userId);
	}

}
