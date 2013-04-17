package txl.message.pushmessage.core;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import txl.config.Config;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.biz.DataRunnable;
import txl.message.pushmessage.biz.HeartBeatRunnable;
import txl.message.pushmessage.biz.OfflineRunnable;
import txl.message.pushmessage.biz.RegistRunnable;
import txl.message.pushmessage.biz.RunnableManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


/**
 * @ClassName:  MessageService.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-1-30 上午10:49:38
 */
public class MessageService extends Service
{
    public static Context context;
    
    public NIOSocket client = null;
    
    private TxLogger log = new TxLogger(MessageService.class, TxlConstants.MODULE_ID_MESSAGE);    
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        if(client!=null){
            client.stop();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        
        int userId = intent.getExtras().getInt("userId");
        context = this.getApplicationContext();
        MessageManager.context = context;
        String pushIp = Config.getInstance().getPushMessageServerIP();
        Integer port = Config.getInstance().getPushMessageServerPort();
        connectServer(pushIp,port,userId);
        return START_NOT_STICKY;
    }
    
    /**
     * 注册业务
     * @param channel
     */
    private void regist(SocketChannel channel,int userId)throws Exception{
        /*RegistRunnable registRun = new RegistRunnable();
        RunnableManager.regist(2, registRun);
        HeartBeatRunnable heartBeatBun = new HeartBeatRunnable(channel);
        RunnableManager.regist(4, heartBeatBun);//心跳接收
        
        DataRunnable dataRun = new DataRunnable();
        RunnableManager.regist(6, dataRun);//接收消息内容
        
        OfflineRunnable offline = new OfflineRunnable();//下线通知
        RunnableManager.regist(7, offline);
        
        registRun.sendRequest(userId);*/
    }
    
    public void connectServer(final String ip,final int port,final int userId){
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                client = new NIOSocket();
                try
                {
                    SocketChannel channel = client.initClient(ip,port,userId);
                    //regist(channel,userId);
                    log.info("消息服务启动.....");
                    
                    client.listen();
                }catch (Exception e)
                {
                    e.printStackTrace();
                    if(client.channel!=null){
                        if(!client.channel.socket().isClosed()){
                            try
                            {
                                client.channel.socket().close();
                            } catch (IOException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                        try
                        {
                            client.channel.close();
                        } catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }
                    }
                    if(client.selector.isOpen()){
                        try
                        {
                            client.selector.close();
                        } catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }
                    }
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                    if(!Config.isKickOut){
                        log.info("re connectServer...");
                        connectServer(ip, port,userId);
                    }
                }
            }
        }).start();
    }
}
