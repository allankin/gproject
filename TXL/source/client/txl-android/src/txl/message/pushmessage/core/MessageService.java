package txl.message.pushmessage.core;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SocketChannel;

import txl.common.TxlToast;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.log.TxLogger;
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
    
    public static boolean needReConnect = true;
    
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
        log.info("MessageService...onDestroy ");
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
        needReConnect = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        
        int userId = intent.getExtras().getInt("userId");
        String phone = intent.getExtras().getString("phone");
        String name = intent.getExtras().getString("name");
        
        context = this.getApplicationContext();
        MessageManager.context = context;
        String pushIp = Config.getInstance().getPushMessageServerIP();
        Integer port = Config.getInstance().getPushMessageServerPort();
        needReConnect = true;
        connectServer(pushIp,port,userId,phone,name);
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
    
    public void connectServer(final String ip,final int port,final int userId,final String phone,final String name){
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                client = new NIOSocket();
                SocketChannel channel = null;
                try
                {
                	channel = client.initClient(ip,port,userId,phone,name);
                    client.listen();
                }
                /*catch (ClosedSelectorException e){
                	e.printStackTrace();
                	
                }*/
                catch (Exception e)
                {
                    e.printStackTrace();
                    MessageManager.setConnected(false);
                    SendMessageDealer.getSingle(channel).stop();
                    ReceiveMessageDealer.getSingle().stop();
                    synchronized (SendMessageQueue.queue) {
                    	SendMessageQueue.queue.notifyAll();
					}
                    synchronized (ReceiveMessageQueue.queue) {
                    	ReceiveMessageQueue.queue.notifyAll();
                    }
                    
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
                        Thread.sleep(3000);
                    } catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                    
                    /*if(e instanceof ClosedSelectorException || e instanceof SocketException){
                    	Config.mainContext.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								TxlToast.showShort(Config.mainContext, "服务器意外停止...");
							}
						});
                    }*/
                    if(!Config.isKickOut && needReConnect){
                        log.info("re connectServer...");
                        connectServer(ip, port,userId,phone,name);
                    }
                }
            }
        }).start();
    }
}
