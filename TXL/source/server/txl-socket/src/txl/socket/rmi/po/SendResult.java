package txl.socket.rmi.po;

import java.io.Serializable;


/**
 * @ClassName:  SendResult.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-19 下午7:49:19
 */
public class SendResult implements Serializable
{
    private static final long serialVersionUID = 1L;
    /*发送回执状态：1：成功  2：用户不在线  3：发送失败 */
    private int status;
    
    public int getStatus()
    {
        return status;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    

}
