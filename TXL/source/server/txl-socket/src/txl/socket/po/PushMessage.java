package txl.socket.po;

import java.io.Serializable;

/**
 * @ClassName:  PushMessage.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-19 下午7:22:48
 */
public class PushMessage implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private int recUserId;
    private int sendUserId;
    private String sendName;
    private String content;
    /*消息id*/
    private String msgId;
    private long curTime;
    
    
    public String toJSONString(){
        return "{\"b\":6,\"c\":\""+content+"\",\"m\":\""+msgId+"\",\"sn\":\""+sendName+"\",\"s\":"+sendUserId+"}";
    }
    
    public int getRecUserId()
    {
        return recUserId;
    }
    
    public void setRecUserId(int recUserId)
    {
        this.recUserId = recUserId;
    }
    
    public int getSendUserId()
    {
        return sendUserId;
    }
    
    public void setSendUserId(int sendUserId)
    {
        this.sendUserId = sendUserId;
    }
    
    public String getSendName()
    {
        return sendName;
    }
    
    public void setSendName(String sendName)
    {
        this.sendName = sendName;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public String getMsgId()
    {
        return msgId;
    }
    
    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
    }
    
    public long getCurTime()
    {
        return curTime;
    }
    
    public void setCurTime(long curTime)
    {
        this.curTime = curTime;
    }
    
}
