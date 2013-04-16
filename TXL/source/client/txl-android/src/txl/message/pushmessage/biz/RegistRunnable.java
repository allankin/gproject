package txl.message.pushmessage.biz;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

import txl.config.TxlConstants;
import txl.message.pushmessage.core.MessageService;

import android.content.Intent;
import android.util.Log;


/**
 * @ClassName:  RegistRunnable.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-2-25 上午09:22:33
 */
public class RegistRunnable implements BizRunnable
{
    private final String TAG = RegistRunnable.class.getSimpleName(); 
    public AtomicBoolean registReplied = new AtomicBoolean(false);
    public SocketChannel channel;
    public int count=0;
 
    public RegistRunnable(SocketChannel channel){
        this.channel = channel;
    }
    
    public void run()
    {
        
    }
    
    
    /*public void sendRequest2(String userName) throws Exception{
        while(!registReplied.get() && count<=Config.resendCount){
            Log.d(TAG,"第"+count+"发送注册消息");
             
             
            while(!channel.isConnected()){
                Thread.sleep(1000);
                Log.d(TAG, " not connected ....");
            }
            String str = "{\"userName\":\""+userName+"\",\"comId\":8}";
            
            ByteBuffer bb = ByteBuffer.wrap(str.getBytes());
            channel.write(bb);
            Log.d(TAG, "send:"+str); 
            int i=1;
            while(i<=timeSliceTotal && !registReplied.get()){
                i++; 
                Thread.sleep(timeSliceDuration); 
            }
            count++;
 
        }
        
        if(count > Config.resendCount){
            Message message = new Message(); 
            message.what = Config.OVER_RESEND_COUNT;
            Config.mainActivity.handler.sendMessage(message);
            throw new Exception("注册消息重发失败");
        }
    }*/
    
    public void sendRequest(final String userName){
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int resendCount = TxlConstants.resendCount;
                while(!registReplied.get() && count<resendCount){
                    Log.d(TAG,"第"+count+"发送注册消息");
                     
                    try
                    {
                        while(!channel.isConnected()){
                            Thread.sleep(3000);
                            //Log.d(TAG, " not connected ....");
                        }
                            String str = "{\"userName\":\""+userName+"\",\"comId\":8}";
                            
                            ByteBuffer bb = ByteBuffer.wrap(str.getBytes());
                            channel.write(bb);
                            Log.d(TAG, "send:"+str); 
                            int i=1;
                            while(i<=timeSliceTotal && !registReplied.get()){
                                i++; 
                                Thread.sleep(timeSliceDuration); 
                            }
                            count++;
                    }
                    catch(NotYetConnectedException e){
                        Log.d(TAG, " "+e.getMessage());
                        try
                        {
                            channel.close();
                        } catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                        /*MessageService ms =new MessageService();
                        ms.connectServer(Config.pushIp, Config.pushPort, userName);
                        Log.d(TAG, "re connectServer....");*/
                       
                        break;
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, " "+e.getMessage());
                        e.printStackTrace();
                        break;
                    }
                }
                
                if(count > resendCount){
                    Intent intent = new Intent(TxlConstants.ACTION_OVER_RESEND_COUNT);
                    MessageService.context.sendBroadcast(intent);
                }
                
            }
        });
        t.setName("注册");
        t.start();
    }
    
    
    
    
    public void dealReply(final JSONObject jobj){
       if(!registReplied.get()){
           new Thread(new Runnable()
            {
                public void run()
                {
                    registReplied.set(true);
                    Log.d(TAG,">>>>>>>>>开始处理注册反馈");
                    String userName = jobj.optString("userName");
                    Log.d(TAG,"获取用户名:"+userName);
                    Log.d(TAG,">>>>>>>>>结束处理注册反馈");
                    
                    HeartBeatRunnable heartBeatRunnable = (HeartBeatRunnable)RunnableManager.getRunnable(TxlConstants.HEARTBEAT_REQUST_CODE);
                    heartBeatRunnable.sendRequest(userName);
                    
                }
            }).start();
       }
    }

}
