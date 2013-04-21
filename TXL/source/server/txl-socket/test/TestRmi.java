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
            msg.setContent("测试数据.“我弟弟一直都很懂事，父母对他一向都是很放心。”林某的姐姐说，林某平时从外地打电话回家，可能是为了节省电话费，跟家人聊的时间并不长。内容基本上都是问下家里的情况，了解下父母的身体状况，很少跟家里讲自己在学校的事情。而最近这两次跟家里的通话，也跟往常一样，没有提及其他事情。林某家人在此期间也没发现林某有任何异常的表现。");
            msg.setCurTime(System.currentTimeMillis());
            String msgId = UUID.randomUUID().toString().replaceAll("-", "");
            msg.setMsgId(msgId);
            msg.setRecUserId(7);
            msg.setSendName("RMI发送者105");
            msg.setSendUserId(105);
            SendResult sr = pushMessageService.send(msg);
            log.info("RMI发送消息...返回结果状态：status:"+sr.getStatus());
            
        } catch (Exception e)
        { 
            e.printStackTrace();
            log.error(Tool.getExceptionTrace(e));
        }
    }

}
