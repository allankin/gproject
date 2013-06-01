package txl.socket.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import txl.socket.SpringManager;
import txl.socket.dao.ContactMessageDao;
import txl.socket.dao.UserDao;
import txl.socket.po.PushMessage;
import txl.socket.po.User;
import txl.socket.service.ContactMessageService;


/**
 * @ClassName:  ContactMessageServiceImpl.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-5-31 上午10:10:28
 * @Copyright: 版权由 HundSun 拥有
 */
public class ContactMessageServiceImpl implements ContactMessageService
{
    private final static Logger log = Logger.getLogger(ContactMessageServiceImpl.class);
    @Resource
    private ContactMessageDao contactMessageDao;
    
    @Resource
    private UserDao userDao;
    
    @Override
    public boolean save(PushMessage pushMsg,int status)
    {
        User recUser = this.userDao.queryById(pushMsg.getRecUserId());
        if(recUser!=null){
            boolean flag = this.contactMessageDao.save(pushMsg, recUser.getCompId(), recUser.getUserPhone(), recUser.getName(), status);
            return flag;
        }else{
            log.info("recUser , userid :"+pushMsg.getRecUserId()+", is nulll, not save... ");
        }
        return false;
    }

    @Override
    public List<PushMessage> queryPushMessagesByRecUserId(int recUserId)
    {
        return this.contactMessageDao.queryPushMessagesByRecUserId(recUserId);
    }

    @Override
    public void delete(List<Integer> msgIdList)
    {
        this.contactMessageDao.delete(msgIdList);
    }
    
    
}
