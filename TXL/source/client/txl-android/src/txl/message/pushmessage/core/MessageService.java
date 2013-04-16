package txl.message.pushmessage.core;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import txl.config.Config;
import txl.message.pushmessage.biz.DataRunnable;
import txl.message.pushmessage.biz.HeartBeatRunnable;
import txl.message.pushmessage.biz.OfflineRunnable;
import txl.message.pushmessage.biz.RegistRunnable;
import txl.message.pushmessage.biz.RunnableManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


/**
 * @ClassName:  MessageService.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-1-30 上午10:49:38
 * @Copyright: 版权由 HundSun 拥有
 */
public class MessageService extends Service
{
    public static Context context;
    
    public NIOSocket client = null;
    
    public final String TAG = MessageService.class.getSimpleName();
    
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
        
        String userName = intent.getExtras().getString("userName");
        context = this.getApplicationContext();
        String pushIp = Config.getInstance().getPushMessageServerIP();
        Integer port = Config.getInstance().getPushMessageServerPort();
        connectServer(pushIp,port,userName);
        return START_NOT_STICKY;
    }
    
    /**
     * 注册业务
     * @param channel
     */
    private void regist(SocketChannel channel,String userName)throws Exception{
        RegistRunnable registRun = new RegistRunnable(channel);
        RunnableManager.regist(9, registRun);
        DataRunnable dataRun = new DataRunnable(channel);
        RunnableManager.regist(10, dataRun);//接收消息内容
        RunnableManager.regist(4, dataRun);//发送接收消息回馈
        RunnableManager.regist(5, dataRun);//发送已阅读回馈
        HeartBeatRunnable heartBeatBun = new HeartBeatRunnable(channel);//心跳检测
        RunnableManager.regist(6, heartBeatBun);//心跳发送
        RunnableManager.regist(7, heartBeatBun);//心跳反馈
        OfflineRunnable offline = new OfflineRunnable(channel);
        RunnableManager.regist(11, offline);
        
        registRun.sendRequest(userName);
    }
    
    public void connectServer(final String ip,final int port,final String userName){
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                client = new NIOSocket();
                try
                {
                    SocketChannel channel = client.initClient(ip,port);
                    regist(channel,userName);
                 
                    client.listen();
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d(TAG, " "+e.getMessage());
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
                        Log.d(TAG, "re connectServer...");
                        connectServer(ip, port,userName);
                    }
                }
            }
        }).start();
    }
/*    
public void connectServer(final String ip,final int port,String userName){
        
        new Thread(new Runnable()
        {
            NIOSocket client = new NIOSocket();
            @Override
            public void run()
            {
                try
                {
                    
                    client.initClient(ip,port);
                    
                    
                    RegistRunnable registRun; 
                    registRun = new RegistRunnable(client.channel);
                    RunnableManager.regist(9, registRun);
                    //registRun.seendRequest2("087");
                    System.out.println("listen......");
                    client.listen();
                    
                    
                } catch (Exception e)
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
                    System.out.println("re connectServer...");
                    connectServer(ip, port,"087");
                    
                    
                }
            }
        }).start();
        
        
        
    }*/
    
}
