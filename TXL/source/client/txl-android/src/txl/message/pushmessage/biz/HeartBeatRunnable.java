package txl.message.pushmessage.biz;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

import txl.config.TxlConstants;
import android.util.Log;


/**
 * @ClassName:  HeartBeatRunnable.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-2-27 下午2:43:24
 * @Copyright: 版权由 HundSun 拥有
 */
public class HeartBeatRunnable implements BizRunnable
{
    private final String TAG = HeartBeatRunnable.class.getSimpleName();
    public SocketChannel channel;
    public AtomicBoolean replied = new AtomicBoolean(true);
    public boolean isRunning = true;
    
    public HeartBeatRunnable(SocketChannel channel){
        this.channel = channel;
    }
    
    @Override
    public void run()
    {
        
    }
    
    public void stop(){
        isRunning = false;
    }
    
    public void sendRequest(final String userName){
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(isRunning){
                    if(!replied.get()){
                       Log.d(TAG, "与服务器已经断开连接");
                       isRunning = false;
                       continue;
                    }
                    
                    String str = "{\"userName\":\""+userName+"\",\"comId\":6}";
                    ByteBuffer bb = ByteBuffer.wrap(str.getBytes());
                    try
                    {
                        channel.write(bb);
                        Log.d(TAG, "send: "+str);
                        replied.set(false);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                        isRunning = false;
                    }
                    
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
    
    
    @Override
    public void dealReply(final JSONObject jobj)
    {
        Log.d(TAG,"receive: "+jobj.toString());
        replied.set(true);
        String userName = jobj.optString("userName");
    }

}
