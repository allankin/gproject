import java.rmi.Naming;
import java.util.UUID;

import org.apache.log4j.Logger;

import txl.socket.TxlConstants;
import txl.socket.po.PushMessage;
import txl.socket.rmi.PushMessageService;
import txl.socket.rmi.po.SendResult;
import txl.socket.util.Tool;


/**
 * @ClassName:  TestRmi.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-19 下午8:04:40
 * @Copyright: 版权由 HundSun 拥有
 */
public class TestRmi
{
    
    private static final Logger log = Logger.getLogger(TestRmi.class);

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        String rmiURL = "rmi://"+TxlConstants.HOST+":"+TxlConstants.RMI_PORT+"/PushMessageService";
        try
        {
            PushMessageService pushMessageService = (PushMessageService)Naming.lookup(rmiURL);
            PushMessage msg = new PushMessage();
            msg.setContent("测试数据");
            msg.setCurTime(System.currentTimeMillis());
            String msgId = UUID.randomUUID().toString().replaceAll("-", "");
            msg.setMsgId(msgId);
            msg.setRecUserId(0);
            msg.setSendName("RMI发送者");
            msg.setSendUserId(100);
            SendResult sr = pushMessageService.send(msg);
            log.info("RMI发送消息...返回结果状态：status:"+sr.getStatus());
            
        } catch (Exception e)
        { 
            e.printStackTrace();
            log.error(Tool.getExceptionTrace(e));
        }
    }

}
