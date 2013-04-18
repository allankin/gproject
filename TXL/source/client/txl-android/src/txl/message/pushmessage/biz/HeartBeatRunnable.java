package txl.message.pushmessage.biz;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

import txl.config.TxlConstants;
import txl.log.TxLogger;


/**
 * @ClassName:  HeartBeatRunnable.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-2-27 下午2:43:24
 */
public class HeartBeatRunnable implements BizRunnable
{
	private TxLogger log = new TxLogger(HeartBeatRunnable.class, TxlConstants.MODULE_ID_MESSAGE);
	
	
	public SocketChannel channel;
    public AtomicBoolean replied = new AtomicBoolean(true);
    public boolean isRunning = true;
    
    public HeartBeatRunnable(SocketChannel channel){
        this.channel = channel;
    }
    
    
    
    public void stop(){
        isRunning = false;
    }
    
    public void sendRequest(final int userId){
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(isRunning){
                    if(!replied.get()){
                       log.info("与服务器已经断开连接");
                       isRunning = false;
                       continue;
                    }
                    
                    String str = "{\"u\":"+userId+",\"b\":3}";
                    ByteBuffer writeBuffer = ByteBuffer.wrap(str.getBytes());
                    try {
						int count = channel.write(writeBuffer);
						log.info("sendRequest  write : "+str+"   size:"+count);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                    replied.set(false);
                    
                    try
                    {
                        Thread.sleep(TxlConstants.heartBeatInterval);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        t.setName("heartBeat");
        t.start();
    }
    
    
    public void dealReply(final JSONObject jobj)
    {
    	log.info("receive: "+jobj.toString());
        replied.set(true);
    }

}
