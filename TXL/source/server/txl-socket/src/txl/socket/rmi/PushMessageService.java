package txl.socket.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
    public SendResult send(PushMessage pushMessage)throws RemoteException;
}
