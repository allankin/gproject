package txl.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import txl.socket.util.Tool;


public class NIOServer {
	
    private Logger log = Logger.getLogger(NIOServer.class);
    
    //通道管理器
	private Selector selector;
	
    private List<WrapChannel> channelList = new ArrayList<WrapChannel>();
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
	public void listen() throws IOException {
	    log.info("服务端启动成功！");
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
                    WrapChannel wrapChannel = new WrapChannel(channel);
                    channelList.add(wrapChannel);
                    // 设置成非阻塞
					channel.configureBlocking(false);

					//在这里可以给客户端发送信息哦
					//channel.write(ByteBuffer.wrap(new String("向客户端发送了一条信息").getBytes()));
					//channel.write(ByteBuffer.wrap(Tool.int2bytes(3)));
                    
                    
                    
                    //在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
					channel.register(this.selector, SelectionKey.OP_READ);
					
					// 获得了可读的事件
				} else if (key.isReadable()) {
				    read(key);
				}

			}

		}
	}
	/**
	 * 处理读取客户端发来的信息 的事件
	 * @param key
	 * @throws IOException 
	 */
	public void read(SelectionKey key){
		// 服务器可读取消息:得到事件发生的Socket通道
		SocketChannel channel = (SocketChannel) key.channel();
		// 创建读取的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int count;
        try
        {
            count = channel.read(buffer);
            byte[] data = buffer.array();
            String msg = new String(data).trim();

            log.info("read : "+msg+"  count: "+count);
            
            if(count == -1){
                try
                {
                    channel.close();
                } catch (IOException e1)
                {
                    log.error(Tool.getExceptionTrace(e1));
                }
                return;
            }
            try
            {
                JSONObject jobj = new JSONObject(msg);
                int bizId = jobj.optInt("b");
                int sendCount = 0;
                /* 注册流程 */
                if(bizId==1){
                   int userId = jobj.optInt("u");
                   
                   String registerResp = "{\"b\":2}";
                   ByteBuffer writeBuffer = ByteBuffer.wrap(registerResp.getBytes());
               
                   sendCount = channel.write(writeBuffer);
                   log.info("发送注册回馈："+registerResp+", count : "+sendCount);
                   WrapChannel existChannel = channelExist(userId);
                   if(existChannel!=null){
                       String offlineResp = "{\"b\":7}";
                       sendCount = existChannel.channel.write(ByteBuffer.wrap(offlineResp.getBytes())); 
                       log.info("发送下线通知："+offlineResp+", count : "+sendCount);
                   }
                   int i=1;
                   while(i<6){
                       
                       String dataJsonStr = "{\"b\":6,\"c\":\"服务器消息内容...."+i+"\"}";
                       writeBuffer = ByteBuffer.wrap(dataJsonStr.getBytes("UTF-8"));
                       sendCount = channel.write(writeBuffer);
                       log.info("发送内容："+dataJsonStr+" count："+sendCount);
                       i++;
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
                	
                }
                
            } catch (JSONException e)
            {
                log.error(Tool.getExceptionTrace(e));
                try
                {
                    channel.close();
                } catch (IOException e1)
                {
                    log.error(Tool.getExceptionTrace(e1));
                }
            }
            
        } catch (IOException e)
        {
            log.error(Tool.getExceptionTrace(e));
            try
            {
                channel.close();
            } catch (IOException e1)
            {
                log.error(Tool.getExceptionTrace(e1));
            }
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
    
    class WrapChannel{
        public SocketChannel channel;
        public int userId;
        public WrapChannel(SocketChannel channel){
            this.channel = channel;
        }
        
    }
    
	/**
	 * 启动服务端测试
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		final NIOServer server = new NIOServer();
        
        Runnable runnable = new Runnable(){
            public void run()
            {
                try
                {
                    server.initServer(8888);
                    server.listen();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            
        };
        
        new Thread(runnable).start();
	}

}
