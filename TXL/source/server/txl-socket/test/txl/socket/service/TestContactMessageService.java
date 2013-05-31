package txl.socket.service;

import java.util.UUID;

import txl.socket.SpringManager;
import txl.socket.po.PushMessage;
import txl.socket.util.Tool;

/**
 * @ClassName:  TestContactMessageService.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-5-31 上午11:16:59
 * @Copyright: 版权由 HundSun 拥有
 */
public class TestContactMessageService
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        SpringManager.init();
        ContactMessageService contactMessageService = (ContactMessageService)SpringManager.getBean("contactMessageService");
        PushMessage pushMsg = new PushMessage();
        pushMsg.setContent("测试....");
        pushMsg.setMsgId(Tool.genUUID());
        pushMsg.setSendUserId(44);
        pushMsg.setSendName("李斯");
        pushMsg.setRecUserId(7);
        
        contactMessageService.save(pushMsg, 2);
    }

}
