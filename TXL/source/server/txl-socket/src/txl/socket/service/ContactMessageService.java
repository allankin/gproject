package txl.socket.service;

import java.util.List;

import txl.socket.po.PushMessage;

/**
 * @ClassName:  ContactService.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-5-31 上午10:08:41
 * @Copyright: 版权由 HundSun 拥有
 */
public interface ContactMessageService
{
    public boolean save(PushMessage pushMsg,int status);
    public List<PushMessage> queryPushMessagesByRecUserId(int recUserId);
    public void delete(List<Integer> msgIdList);
}
