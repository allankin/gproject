package txl.message.pushmessage.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Stack;

import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.biz.DataRunnable;
import txl.message.pushmessage.biz.HeartBeatRunnable;
import txl.message.pushmessage.biz.OfflineRunnable;
import txl.message.pushmessage.biz.PushDataClassifiedRunnable;
import txl.message.pushmessage.biz.RegistRunnable;
import txl.message.pushmessage.biz.RunnableManager;

import android.util.Log;


/**
 * 
 * @author jinchao
 *
 */
public class NIOSocket {
    public Selector selector;
    public SocketChannel channel;
    private int userId;
    private String phone;
    private String name;
    public boolean pollFlag = true;
    
    private TxLogger log = new TxLogger(NIOSocket.class, TxlConstants.MODULE_ID_MESSAGE);
    
    public Stack<Byte> jsonStack = new Stack<Byte>();
    ByteBuffer readBuffer = ByteBuffer.allocate(1);
    
    ByteBuffer contentBuffer = ByteBuffer.allocate(1024);
    ByteBuffer singleBuffer = ByteBuffer.allocate(1024);
    
    
    Charset cs = Charset.forName("UTF-8");

    
    /**
     * 获得一个Socket通道，并对该通道做一些初始化的工作
     * @param ip 连接的服务器的ip
     * @param port  连接的服务器的端口号         
     * @throws IOException
     */
    public SocketChannel initClient(String ip,int port,int userId,String phone,String name) throws IOException {
        log.info(" socket  initClient....ip:"+ip+",port:"+port+",userId:"+userId+",phone:"+phone);
        this.userId = userId;
        this.phone  = phone;
        this.name = name;
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        this.selector = Selector.open();
        channel.connect(new InetSocketAddress(ip,port));
        channel.register(selector, SelectionKey.OP_CONNECT);
        
        /*InetSocketAddress addr = new InetSocketAddress(ip, port);
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        Log.d("NIOSocket", "initiating connection");
        channel.connect(addr);
        while (!channel.finishConnect()) {
            Log.d("NIOSocket","暂时没有连接成功！");
        }
        Log.d("NIOSocket","connection established");
        this.selector = Selector.open();
        channel.register(selector, SelectionKey.OP_CONNECT);*/
        
        /*
        int count=1;
        while (!channel.finishConnect()) {
            Log.d(TAG,"暂时没有连接成功！ count: "+count);
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            if(count==5){
                break;
            }
            count++;
        }
        */
        
        /*
        
        try
        {
            Thread.sleep(3000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }*/
        /*while(!channel.finishConnect()){
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            Log.d("NIOSocket", "....is not Connected................"); 
        }*/
        /*while(!channel.isConnected())
        {
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            Log.d("NIOSocket", "....is not Connected................");
        }*/
        return channel;
    }

    /**
     * 采用监听selector上是否有需要处理的事件，如果有，则进行处理
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void listen() throws Exception {
        while (pollFlag) {
            selector.select();
            Iterator ite = this.selector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey key = (SelectionKey) ite.next();
                ite.remove();
                if (key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key
                            .channel();
                    if(channel.isConnectionPending()){
                        channel.finishConnect();
                        
                    }
                    channel.configureBlocking(false);
                    
                    RegistRunnable registRun = new RegistRunnable();
                    RunnableManager.regist(TxlConstants.BIZID_REQUEST_REGIST, registRun);
                    RunnableManager.regist(TxlConstants.BIZID_RESPONSE_REGIST, registRun);
                    HeartBeatRunnable heartBeatBun = new HeartBeatRunnable(channel);
                    RunnableManager.regist(TxlConstants.BIZID_REQUEST_HEARTBEAT, heartBeatBun);//心跳发送
                    RunnableManager.regist(TxlConstants.BIZID_RESPONSE_HEARTBEAT, heartBeatBun);//心跳接收
                    
                    DataRunnable dataRun = new DataRunnable();
                    RunnableManager.regist(TxlConstants.BIZID_RESPONSE_DATA, dataRun);//接收消息内容
                    RunnableManager.regist(TxlConstants.BIZID_REQUEST_DATA, dataRun);
                    
                    
                    PushDataClassifiedRunnable dataClassifiedRun = new PushDataClassifiedRunnable();
                    RunnableManager.regist(TxlConstants.BIZID_RESPONSE_CLASSIFIED_PUSHDATA, dataClassifiedRun);
                    
                    OfflineRunnable offline = new OfflineRunnable();//下线通知
                    RunnableManager.regist(TxlConstants.BIZID_RESPONSE_OFFLINE, offline);
                    //registRun.sendRequest(userId);
                    
                    /*发送注册消息*/
                    String str =  "{\"u\":" + userId + ",\"b\":"+TxlConstants.BIZID_REQUEST_REGIST+",\"p\":\""+phone+"\",\"n\":\""+name+"\"}";
                    ByteBuffer writeBuffer = ByteBuffer.wrap(str.getBytes("UTF-8"));
                    channel.write(writeBuffer);
                    
                    
                    SendMessageDealer.getSingle(channel).start();
                    ReceiveMessageDealer.getSingle().start();
                    
                    channel.register(this.selector, SelectionKey.OP_READ);
                    
                    log.info("isConnectable .... 消息服务启动.....");
                    
                }else if(key.isAcceptable()){
                	log.info("isAcceptable...");
                }
                else if (key.isReadable()) {
                        if(-1 == read(key))
                            throw new Exception("与服务器失去连接...."); 
                }

            }

        }
        log.info("Socket listen is ... "+pollFlag);
    }
    /**
     * 处理读取服务端发来的信息 的事件
     * @param key
     * @throws IOException 
     */
    public int read(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        
        readBuffer.clear();
        int count = channel.read(readBuffer);
        
        if(count == -1){
            try
            {
                channel.close();
                log.info("read error：count = -1，  close");
                /*
                {
                    singleBuffer.rewind();
                    String msg = new String(singleBuffer.array(),"utf-8");
                    String mm="channel close:"+msg;
                    Log.d("log",mm);
                    //Log.d(TAG, new String(singleBuffer.array()).toString());
                    singleBuffer.clear();
                }
                */
            } catch (IOException e1)
            {
                e1.printStackTrace();
            }
            return -1;
        }
        readBuffer.rewind();
        byte b = readBuffer.get(); 
        
         
        
        
        
        // {开始
        if(b==123){
            jsonStack.push(b);
            /*
            {
                singleBuffer.rewind();
                String msg = new String(singleBuffer.array(),"utf-8");
                String mm="read remove"+msg;
                Log.d("log",mm);
                //Log.d(TAG, new String(singleBuffer.array()).toString());
                singleBuffer.clear();
            }
            */
            singleBuffer.put(b);
        }else if(b==125){ // }结束
           jsonStack.pop();
           singleBuffer.put(b);
        }else {
           /*if(b == 0xff){
               singleBuffer.put((byte)0);
           }else{
               
           }*/
           singleBuffer.put(b); 
        }
        
        
        if(jsonStack.isEmpty()){
            singleBuffer.rewind();
            /**
             * byte[] bytes = singleBuffer.array();
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<bytes.length;i++){
                sb.append(Integer.toHexString(bytes[i]));
            }
             */
            
            
            //Log.d(TAG, sb.toString());
            //String msg = new String(singleBuffer.array(),"utf-32").trim();
            //String msg = String.valueOf(cs.decode(singleBuffer).array()); 
            String msg = new String(singleBuffer.array(),"utf-8");
            //Log.d(TAG, new String(singleBuffer.array()).toString());
            singleBuffer.clear();
            //RunnableManager.parse(msg);
            
            synchronized (ReceiveMessageQueue.queue) {
            	ReceiveMessageQueue.queue.add(msg);
            	ReceiveMessageQueue.queue.notifyAll();
			}
        }
        return 1;
        
    }
    
     
    
    public void stop(){
        this.pollFlag = false;
        log.info("NIOSocket ...  stop");
    }
    /**
     * 客户端测试
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        NIOSocket client = new NIOSocket();
        //String ip="192.168.84.98"; //"localhost"
        //String ip="192.168.84.101";
        String ip="192.168.84.98";
        int port = 8888;
        client.initClient(ip,port,1,"15824135596","name");
        
        
        /*for(int i=0;i<=20;i++){
            final int j = i;
            new Thread(new Runnable(){

                public void run()
                {
                    //BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));
                    String str = "{\"userId\":"+(123+j)+",\"comId\":8}";
                    while(true){
                        try
                        {
                            Thread.sleep(2000); 
                            //str = wt.readLine();
                            ByteBuffer bb = ByteBuffer.wrap(str.getBytes());
                            channel.write(bb);
                            
                            
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                
            }).start();
        }*/
        
        /*RegistRunnable registRun; 
        registRun = new RegistRunnable(channel);
        RunnableManager.regist(9, registRun);
        new Thread(registRun).start();
        
        
        client.listen();*/
    }

}
