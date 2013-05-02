package txl.socket.rmi.impl;

import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import txl.socket.NIOServer;
import txl.socket.NIOServer.WrapChannel;
import txl.socket.TxlConstants;
import txl.socket.po.PushMessage;
import txl.socket.rmi.PushMessageService;
import txl.socket.rmi.po.SendResult;
import txl.socket.util.Tool;

/**
 * @ClassName: MessageServiceImpl.java
 * @Description:
 * @Author JinChao
 * @Date 2013-4-19 下午7:20:49
 */
public class PushMessageServiceImpl extends UnicastRemoteObject implements PushMessageService
{
	public PushMessageServiceImpl() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PushMessageServiceImpl.class);

    @Override
    public SendResult send(PushMessage pushMessage) throws RemoteException
    {
        SendResult sr = new SendResult();

        Map<Integer, WrapChannel> channelMap = NIOServer.getSingle().getChannelMap();
        log.info(" channel size : "+channelMap.size());
        for(WrapChannel c:NIOServer.getSingle().getChannelList()){
            log.info("遍历wrapChannel: userId: "+c.userId);
        }

        WrapChannel wrapChannel = channelMap.get(pushMessage.getRecUserId());
        if (wrapChannel != null)
        {
            String msg = pushMessage.toJSONString();
            int excatCount = msg.length();
            int count=0;
            try
            {
                count = wrapChannel.channel.write(ByteBuffer.wrap(msg.getBytes("UTF-8")));
            }catch (Exception e)
            {
                log.error(Tool.getExceptionTrace(e));
                e.printStackTrace();
            }
            log.info("send message: " + msg + ", count:" + count + ",excatCount:" + excatCount);
            // HS_TODO: 应该比较实际长度
            if (count > 0)
            {
                sr.setStatus(TxlConstants.SEND_STATUS_SUCCESS);
            } else
            {
                sr.setStatus(TxlConstants.SEND_STATUS_FAIL);
            }

        } else
        {
            /* 用户不存在 */
            sr.setStatus(TxlConstants.SEND_STATUS_USER_NOT_EXIST);
        }
        return sr;
    }

	@Override
	public boolean isOnline(Integer userId) throws RemoteException {
		Map<Integer, WrapChannel> channelMap = NIOServer.getSingle().getChannelMap();
		if(userId==null)
			return false;
		if(channelMap.get(userId)==null){
			return false;
		}
		return true;
	}

	@Override
	public boolean isOnline(String phone) throws RemoteException {
		 List<WrapChannel> list = NIOServer.getSingle().getChannelList();
		 for(WrapChannel c: list){
			 if(phone.equals(c.phone)){
				 return true;
			 }
		 }
		return false;
	}

	@Override
	public Map<Integer, Boolean> isOnline(List<Integer> userIdList)
			throws RemoteException {
		Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
		if(userIdList!=null){
			for(Integer userId:userIdList){
				boolean isOnline = this.isOnline(userId);
				map.put(userId, isOnline);
			}
		}else{
			return null;
		}
		return map;
	}

}
