package txl.message.pushmessage.biz;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.core.MessageService;
import txl.message.pushmessage.core.ReceiveMessageQueue;
import txl.message.pushmessage.core.SendMessageQueue;

import android.content.Intent;
import android.util.Log;

/**
 * @ClassName: RegistRunnable.java
 * @Description:
 * @Author JinChao
 * @Date 2013-2-25 上午09:22:33
 */
public class RegistRunnable implements BizRunnable {
	private TxLogger log = new TxLogger(RegistRunnable.class,
			TxlConstants.MODULE_ID_MESSAGE);
    private int userId;
	public void sendRequest(int userId) {
		this.userId = userId;
		String str = "{\"u\":" + userId + ",\"b\":1}";
		synchronized (SendMessageQueue.queue) {
			SendMessageQueue.queue.add(str);
		}
		log.info("sendRequest: " + str);
	}

	public void dealReply(final JSONObject jobj) {
		HeartBeatRunnable heartBeatRunnable = (HeartBeatRunnable) RunnableManager
				.getRunnable(TxlConstants.HEARTBEAT_REQUST_CODE);
		heartBeatRunnable.sendRequest(userId);
	}

}
