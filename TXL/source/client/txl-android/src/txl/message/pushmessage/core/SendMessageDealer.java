package txl.message.pushmessage.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.biz.RunnableManager;

public class SendMessageDealer implements Runnable {
	private TxLogger log = new TxLogger(SendMessageDealer.class, TxlConstants.MODULE_ID_MESSAGE);
	private boolean isRunning;
	private SocketChannel channel; 
	public SendMessageDealer(SocketChannel channel){
        this.channel = channel;
    }
	@Override
	public void run() {
		log.info("Thread : SendMessageDealer  is running .....");
		this.isRunning = true;
		while(isRunning){
			String jsonString = null;
			synchronized (SendMessageQueue.queue) {
				jsonString = SendMessageQueue.queue.poll();
			}
			/*若消息存在，则统一处理*/
			if(jsonString!=null){
				 ByteBuffer writeBuffer = ByteBuffer.wrap(jsonString.getBytes());
				try {
					int size = channel.write(writeBuffer);
					log.info("SendMessageDealer  write : "+jsonString+"   size:"+size);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				synchronized (SendMessageQueue.queue) {
					try {
						SendMessageQueue.queue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		log.info("Thread : SendMessageDealer  is stoped .....");
		
	}
	
	public void stop(){
		this.isRunning = false;
	}
	
	private static SendMessageDealer dealer ;
	public static SendMessageDealer getSingle(SocketChannel channel){
		if(dealer==null){
			dealer = new SendMessageDealer(channel);
		}
		return dealer;
	}
	
	public void start(){
		if(!isRunning){
			isRunning  = true;
			Thread t = new Thread(this);
			t.setName("SendMessageDealer");
			t.start();
		}else{
			log.info("thread:  SendMessageDealer ... 不能重复启动...");
		}
	}

}
