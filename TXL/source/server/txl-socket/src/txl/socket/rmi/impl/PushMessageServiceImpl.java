package txl.socket.rmi.impl;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
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
public class PushMessageServiceImpl implements PushMessageService,Serializable
{
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

}