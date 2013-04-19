package txl.message.pushmessage.core;

import java.nio.channels.SocketChannel;

import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.biz.RunnableManager;

public class ReceiveMessageDealer implements Runnable {
	private TxLogger log = new TxLogger(ReceiveMessageDealer.class, TxlConstants.MODULE_ID_MESSAGE);
	private boolean isRunning;
	@Override
	public void run() {
		log.info("Thread : ReceiveMessageDealer  is running .....");
		this.isRunning = true;
		while(isRunning){
			String jsonString = null;
			synchronized (ReceiveMessageQueue.queue) {
				jsonString = ReceiveMessageQueue.queue.poll();
				/*若消息存在，则统一处理*/
				if(jsonString!=null){
				    RunnableManager.parse(jsonString);
				}else{
				    try {
				        ReceiveMessageQueue.queue.wait();
				    } catch (InterruptedException e) {
				        e.printStackTrace();
				    }
				}
			}
		}
		log.info("Thread : ReceiveMessageDealer  is stoped .....");
		
	}
	
	public void stop(){
		this.isRunning = false;
	}
	
	private static ReceiveMessageDealer dealer ;
	public static ReceiveMessageDealer getSingle(){
		if(dealer==null){
			dealer = new ReceiveMessageDealer();
		}
		return dealer;
	}
	public void start(){
		if(!isRunning){
			isRunning  = true;
			Thread t = new Thread(this);
			t.setName("ReceiveMessageDealer");
			t.start();
		}else{
			log.info("thread:  ReceiveMessageDealer ... 不能重复启动...");
		}
	}
}
