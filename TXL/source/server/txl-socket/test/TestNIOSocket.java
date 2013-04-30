

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;


/**
 * 
 * @author jinchao
 *
 */
public class TestNIOSocket {
    public Selector selector;
    public SocketChannel channel;
    private int userId;
    private String phone;
    private String name;
    public boolean pollFlag = true;
    
    //private final static Logger log =  Logger.getLogger(TestNIOSocket.class);
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
        System.out.println(" socket  initClient....ip:"+ip+",port:"+port+",userId:"+userId+",phone:"+phone);
        this.userId = userId;
        this.phone  = phone;
        this.name = name;
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        this.selector = Selector.open();
        channel.connect(new InetSocketAddress(ip,port));
        channel.register(selector, SelectionKey.OP_CONNECT);
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
                    
                    System.out.println("isConnectable .... 消息服务启动.....");
                    
                    /*发送注册消息*/
                    String str =  "{\"u\":" + userId + ",\"b\":1,\"p\":\""+phone+"\",\"n\":\""+name+"\"}";
                    ByteBuffer writeBuffer = ByteBuffer.wrap(str.getBytes("UTF-8"));
                    int count = channel.write(writeBuffer);
                    System.out.println("发送注册消息 ...."+str+",size:"+count);
                    
                    channel.register(this.selector, SelectionKey.OP_READ);
                    
                    
                }else if(key.isAcceptable()){
                	System.out.println("isAcceptable...");
                }
                else if (key.isReadable()) {
                        if(-1 == read(key))
                            throw new Exception("与服务器失去连接...."); 
                }

            }

        }
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
                System.out.println("read error：count = -1，  close");
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
            singleBuffer.put(b);
        }else if(b==125){ // }结束
           jsonStack.pop();
           singleBuffer.put(b);
        }else {
           singleBuffer.put(b); 
        }
        
        
        if(jsonStack.isEmpty()){
            singleBuffer.rewind();
            String msg = new String(singleBuffer.array(),"utf-8");
            singleBuffer.clear();
            System.out.println("read msg:"+msg);
        }
        return 1;
        
    }
    
     
    
    public void stop(){
        this.pollFlag = false;
    }
    /**
     * 客户端测试
     * @throws IOException 
     */
    public static void main(String[] args){
        final TestNIOSocket client = new TestNIOSocket();
        //String ip="192.168.84.98"; //"localhost"
        //String ip="192.168.84.101";
        String ip="192.168.2.100";
        int userId = 44;
        int port = 8888;
        new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					Scanner scanner = new Scanner(System.in);
					String data = scanner.next();
					try {
						int count = client.channel.write(ByteBuffer.wrap(data.getBytes("UTF-8")));
						System.out.println("write:"+new String(data.getBytes("UTF-8"))+",size:"+count);
						
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
       
        try {
        	client.initClient(ip,port,userId,"15824135595","小猪");
			client.listen();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
