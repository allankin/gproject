package txl.socket.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import txl.socket.po.PushMessage;
import txl.socket.rmi.po.SendResult;

/**
 * @ClassName:  MessageServiceRMI.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-19 下午7:20:09
 */
public interface PushMessageService extends Remote 
{
	/**
	 * 消息发送
	 * @param pushMessage
	 * @return
	 * @throws RemoteException
	 */
    public SendResult send(PushMessage pushMessage)throws RemoteException;
    /**
     * 判断用户是否在线
     * @param userId
     * @return
     * @throws RemoteException
     */
    public boolean isOnline(Integer userId) throws RemoteException;
    
    public boolean isOnline(String phone) throws RemoteException;
    
    /**
     *
     * @param userIdList
     * @return
     * @throws RemoteException
     */
    public Map<Integer,Boolean> isOnline(List<Integer> userIdList) throws RemoteException;
}
