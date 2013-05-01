package txl.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import txl.socket.rmi.PushMessageService;
import txl.socket.rmi.impl.PushMessageServiceImpl;
import txl.socket.util.Tool;

/**
 * 消息服务器
 * 
 * HS_TODO: 
 * 未采用多线程处理
 * 未对channelList、 map 做同步处理
 * 
 * @author jinchao
 *
 */
public class NIOServer {
	
    private static Logger log = Logger.getLogger(NIOServer.class);
    
    //通道管理器
	private Selector selector;
	
	
	
    private List<WrapChannel> channelList = new ArrayList<WrapChannel>();
    
    private Map<Integer,WrapChannel> channelMap = new HashMap<Integer,WrapChannel>();
    
    
    public Stack<Byte> jsonStack = new Stack<Byte>();
    ByteBuffer readBuffer = ByteBuffer.allocate(1);
    ByteBuffer singleBuffer = ByteBuffer.allocate(1025);
    
    private static NIOServer nioServer;
    private NIOServer(){
        
    }
    public static NIOServer getSingle(){
        if(nioServer==null){
            nioServer = new NIOServer();
        }
        return nioServer;
    }
    
    
	/**
	 * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
	 * @param port  绑定的端口号
	 * @throws IOException
	 */
	public void initServer(int port) throws IOException {
		// 获得一个ServerSocket通道
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		// 设置通道为非阻塞
		serverChannel.configureBlocking(false);
		// 将该通道对应的ServerSocket绑定到port端口
		serverChannel.socket().bind(new InetSocketAddress(port));
		// 获得一个通道管理器
		this.selector = Selector.open();
		//将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
		//当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	
	/**
	 * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void listen() throws Exception {
	    log.info("消息服务端开始监听.... port:"+TxlConstants.SOCKET_PORT);
		// 轮询访问selector
		while (true) {
			//当注册的事件到达时，方法返回；否则,该方法会一直阻塞
			selector.select();
			// 获得selector中选中的项的迭代器，选中的项为注册的事件
			Iterator ite = this.selector.selectedKeys().iterator();
			while (ite.hasNext()) {
				SelectionKey key = (SelectionKey) ite.next();
				// 删除已选的key,以防重复处理
				ite.remove();
				// 客户端请求连接事件
				if (key.isAcceptable()) {
					ServerSocketChannel server = (ServerSocketChannel) key
							.channel();
					// 获得和客户端连接的通道
					SocketChannel channel = server.accept();
                   
                    // 设置成非阻塞
					channel.configureBlocking(false);

					//在这里可以给客户端发送信息哦
					//channel.write(ByteBuffer.wrap(new String("向客户端发送了一条信息").getBytes()));
					//channel.write(ByteBuffer.wrap(Tool.int2bytes(3)));
                    
                    
                    
                    //在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
					channel.register(this.selector, SelectionKey.OP_READ);
					
					// 获得了可读的事件
				} else if (key.isReadable()) {
					if(-1 == read(key)){
						log.info("失去客户端连接...."); 
						key.cancel();
					}
					
				}
				

			}

		}
	}
	/**
	 * 处理读取客户端发来的信息 的事件
	 * @param key
	 * @throws IOException 
	 */
	public int read(SelectionKey key){
		// 服务器可读取消息:得到事件发生的Socket通道
		SocketChannel channel = (SocketChannel) key.channel();
		
		readBuffer.clear();
	    int count;
		try {
			count = channel.read(readBuffer);
			if(count == -1){
	            try
	            {
	                channel.close();
	                releaseChannel(channel);
	                log.info("read error：count = -1，  close");
	            } catch (IOException e1)
	            {
	                log.error(Tool.getExceptionTrace(e1));
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
	            log.info("msg :"+msg);
	            
	            try
	            {
	                JSONObject jobj = new JSONObject(msg);
	                int bizId = jobj.optInt("b");
	                int sendCount = 0;
	                /* 注册流程 */
	                if(bizId==1){
	                   int userId = jobj.optInt("u");
	                   String phone = jobj.optString("p");
	                   String name = jobj.optString("n");
	                   
	                   String registerResp = "{\"b\":2}";
	                   ByteBuffer writeBuffer = ByteBuffer.wrap(registerResp.getBytes());
	               
	                   sendCount = channel.write(writeBuffer);
	                   log.info("发送注册回馈："+registerResp+", count : "+sendCount);
	                   WrapChannel existChannel = channelExist(userId);
	                   /*已经注册，通道存在 */
	                   if(existChannel!=null){
	                       String offlineResp = "{\"b\":7}";
	                       sendCount = existChannel.channel.write(ByteBuffer.wrap(offlineResp.getBytes())); 
	                       log.info("发送下线通知："+offlineResp+", count : "+sendCount);
	                       try {
							Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
	                       existChannel.channel.close();
	                       releaseChannel(existChannel);
	                   }
	                   
                       WrapChannel wrapChannel = new WrapChannel(channel);
                       wrapChannel.userId = userId;
                       wrapChannel.phone = phone;
                       wrapChannel.name = name;
                       addChannel(wrapChannel);
	                   
	                   int j=1;
	                   while(j<8){
	                       Random r = new Random();
	                       int i = r.nextInt(50);
	                       if(i==0 || i==7){
	                    	   i++;
	                       }
	                       if(i>25){
	                    	   i = 44;
	                       }
	                       String dataJsonStr = "{\"b\":6,\"c\":\"服务器消息内容...."+i+"\",\"m\":\""+i+"\",\"sn\":\"sendName"+i+"\",\"s\":"+i+"}";
	                       writeBuffer = ByteBuffer.wrap(dataJsonStr.getBytes("UTF-8"));
	                       sendCount = channel.write(writeBuffer);
	                       log.info("发送内容："+dataJsonStr+" count："+sendCount);
	                       j++;
	                   }
	                   
	                }
	                /*心跳处理*/
	                else if(bizId==3){
	                    String registerResp = "{\"b\":4}";
	                    ByteBuffer writeBuffer = ByteBuffer.wrap(registerResp.getBytes());
	                    sendCount = channel.write(writeBuffer);
	                    log.info("发送心跳回馈："+registerResp+" count："+sendCount);
	                }
	                /*内容包处理*/
	                else if(bizId == 5){
	                    log.info("内容包："+jobj.toString());
	                    int userId = jobj.optInt("u");
	                    WrapChannel curChannel = this.channelMap.get(userId);
	                    String name = curChannel.name;
	                    JSONArray recIdArray = jobj.getJSONArray("r");
	                    String content = jobj.getString("c");
	                    if(recIdArray!=null){
	                    	for(int i=0,len=recIdArray.length();i<len;i++){
	                    		int recId = recIdArray.getInt(i);
	                    		WrapChannel wrapChannel = this.channelMap.get(recId);
	                    		String uuid = Tool.genUUID();
	                    		if(wrapChannel!=null){
	                    			String dataJsonStr = "{\"b\":6,\"c\":\""+content+"\",\"m\":\""+uuid+"\",\"sn\":\""+name+"\",\"s\":"+userId+"}";
	                    			// HS_TODO: 可以优化,公用ByteBuffer
	                    			ByteBuffer writeBuffer = ByteBuffer.wrap(dataJsonStr.getBytes("UTF-8"));
	                    			sendCount = wrapChannel.channel.write(writeBuffer);
	                    			log.info("发送内容："+dataJsonStr+" count："+sendCount);
	                    		}
	                    	}
	                    }
	                    
	                    
	                }
	                
	            } catch (JSONException e)
	            {
	                log.error(Tool.getExceptionTrace(e));
	                try
	                {
	                    channel.close();
	                    releaseChannel(channel);
	                } catch (IOException e1)
	                {
	                    log.error(Tool.getExceptionTrace(e1));
	                }
	            }
	            
	        }
			
			
		} catch (IOException e2) {
			log.error(Tool.getExceptionTrace(e2));
			releaseChannel(channel);
			return -1;
		}
	   
	    return 1;
        
		
	}
	/**
	 * 添加WrapChannel
	 * @param channel
	 */
	private void addChannel(WrapChannel channel){
	    int beforeListSize = this.channelList.size();
	    int beforeMapSize = this.channelMap.size();
	    this.channelList.add(channel);
	    this.channelMap.put(channel.userId, channel);
	    int afterListSize = this.channelList.size();
	    int afterMapSize = this.channelMap.size();
	    
	    log.info("addChannel...."+channel.userId+",beforeListSize"+beforeListSize+",beforeMapSize:"+beforeMapSize+",afterListSize:"+afterListSize+",afterMapSize:"+afterMapSize);
	}
	/**
	 * 释放WrapChannel
	 * @param channel
	 */
	private void releaseChannel(Object channel){
	    int beforeListSize = this.channelList.size();
	    int beforeMapSize = this.channelMap.size();
	    WrapChannel wc = null;

	    if(channel instanceof WrapChannel){
	        wc = (WrapChannel)channel;
	    }else if(channel instanceof Channel){
	        for(WrapChannel _wc : this.channelList){
	            if(_wc.channel == channel){
	                wc = _wc;
	                break;
	            }
	        }
	    }
	    if(wc!=null){
	        this.channelList.remove(wc);
	        this.channelMap.remove(wc.userId);
	        int afterListSize = this.channelList.size();
	        int afterMapSize = this.channelMap.size();
	        
	        log.info("releaseChannel...."+wc.userId+",beforeListSize"+beforeListSize+",beforeMapSize:"+beforeMapSize+",afterListSize:"+afterListSize+",afterMapSize:"+afterMapSize);
	    }else{
	        log.info("WrapChannel 为 null ");
	    }
        
	}
	
	
	public WrapChannel channelExist(int userId){
	    for(WrapChannel channel : channelList){
	       if(userId == channel.userId){
	           return channel;
	       }
	    }
	    return null;
	}
    
    public class WrapChannel{
        public SocketChannel channel;
        public int userId;
        public String phone;
        public String name;
        public WrapChannel(SocketChannel channel){
            this.channel = channel;
        }
        
    }
    public List<WrapChannel> getChannelList()
    {
        return channelList;
    }
    
    public void setChannelList(List<WrapChannel> channelList)
    {
        this.channelList = channelList;
    }
    
    public Map<Integer, WrapChannel> getChannelMap()
    {
        return channelMap;
    }
    
    public void setChannelMap(Map<Integer, WrapChannel> channelMap)
    {
        this.channelMap = channelMap;
    }
    
    
	/**
	 * 启动服务端测试
	 * @throws IOException 
	 */
	public static void main(String[] args){
		final NIOServer server = NIOServer.getSingle();
        
        Runnable runnable = new Runnable(){
            public void run()
            {
                try
                {
                    server.initServer(TxlConstants.SOCKET_PORT);
                    server.listen();
                } catch (Exception e)
                {
                    log.info(Tool.getExceptionTrace(e));
                }
            }
            
        };
        new Thread(runnable).start();
        
        
        try
        {
        	PushMessageService pushMessageService = new PushMessageServiceImpl();
            log.info("PushMessageService : "+pushMessageService);
            LocateRegistry.createRegistry(TxlConstants.RMI_PORT);
            Naming.bind("rmi://"+TxlConstants.HOST+":"+TxlConstants.RMI_PORT+"/PushMessageService", pushMessageService);
            log.info("消息RMI开始监听.... port:"+TxlConstants.RMI_PORT);
        } catch (Exception e)
        {
            log.error(Tool.getExceptionTrace(e));
        }  
	}

}
