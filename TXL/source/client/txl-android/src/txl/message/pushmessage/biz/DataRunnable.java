package txl.message.pushmessage.biz;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

import txl.message.pushmessage.core.MessageManager;
import txl.message.pushmessage.po.BillData;

import android.util.Log;



/**
 * @ClassName:  DataRunnable.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-2-25 下午2:56:14
 * @Copyright: 版权由 HundSun 拥有
 */
public class DataRunnable implements BizRunnable
{
    private final String TAG = DataRunnable.class.getSimpleName(); 
    public AtomicBoolean openResultReplied = new AtomicBoolean(false);
    public SocketChannel channel;
    public int count=1;
    
    
    public DataRunnable(SocketChannel channel){
        this.channel = channel;
    }
    @Override
    public void run()
    {
        
    }
    
    public void dealData(final JSONObject jobject){
        new Thread(new Runnable()
        {
            public void run()
            {
               BillData bd = new BillData();
               bd.parseJSONObject(jobject);
               //消息通知提醒
               MessageManager.showNotice(bd);
               
               /*MessageDaoImpl mdi = new MessageDaoImpl(Config.mainActivity);
               mdi.saveMessage(bd.uuId, bd.content);*/
               
            }
        }).start();
    }
    
    @Override
    public void dealReply(final JSONObject jobject)
    {
         

    }
    
    /**
     * 发送接收消息包
     * @param uuId
     * @param userName
     */
    public void sendReceived(String uuId,String userName){
        String receivedResp = "{\"uuId\":\""+uuId+"\",\"userName\":\""+userName+"\",\"comId\":4}";
        ByteBuffer writeBuffer = ByteBuffer.wrap(receivedResp.getBytes());
        try
        {
            this.channel.write(writeBuffer);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 发送消息打开包
     * @param uuId
     * @param userName
     */
    /*public void sendOpened(final String uuId,final String userName){
        new Thread(new Runnable()
        {
            public void run()
            {
                while(!openResultReplied.get() && count<=Config.resendCount){
                    Log.d(TAG,"第"+count+"发送消息处理结果");
                    try
                    {
                        String openedResp = "{\"uuId\":\""+uuId+"\",\"userName\":\""+userName+"\",\"comId\":5}";
                        ByteBuffer writeBuffer = ByteBuffer.wrap(openedResp.getBytes()); 
                        channel.write(writeBuffer);
                        
                        int i=1;
                        while(i<=timeSliceTotal && !openResultReplied.get()){
                           i++; 
                           Thread.sleep(timeSliceDuration); 
                        }
                        count++;
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        break;
                    }  
                }
                
                if(count > Config.resendCount){
                    Message message = new Message(); 
                    message.what = Config.OVER_RESEND_OPEN_RESULT_COUNT;
                    Config.mainActivity.handler.sendMessage(message);
                }
                
            }
        }).start();
    }*/
    
    
    public void sendOpened(final String uuId,final String userName){
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    String openedResp = "{\"uuId\":\""+uuId+"\",\"userName\":\""+userName+"\",\"comId\":5}";
                    ByteBuffer writeBuffer = ByteBuffer.wrap(openedResp.getBytes()); 
                    int size = channel.write(writeBuffer);
                    Log.d(TAG, "sendOpened : "+openedResp+"  size : "+size);
                    int i=1;
                    while(i<=timeSliceTotal && !openResultReplied.get()){
                       i++; 
                       Thread.sleep(timeSliceDuration); 
                    }
                    count++;
                } catch (Exception e)
                {
                    e.printStackTrace();
                }  
                
            }
        }).start();
    }

}
