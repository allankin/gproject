package txl.socket.po;

import java.io.Serializable;

import txl.socket.TxlConstants;

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
    
    /** below 推送消息时字段**/
    private int pushMsgType;
    private String pushMsgTypeName;
    public int getPushMsgType() {
		return pushMsgType;
	}

	public void setPushMsgType(int pushMsgType) {
		this.pushMsgType = pushMsgType;
	}

	public String getPushMsgTypeName() {
		return pushMsgTypeName;
	}

	public void setPushMsgTypeName(String pushMsgTypeName) {
		this.pushMsgTypeName = pushMsgTypeName;
	}

	public String getPushMsgUrl() {
		return pushMsgUrl;
	}

	public void setPushMsgUrl(String pushMsgUrl) {
		this.pushMsgUrl = pushMsgUrl;
	}

	private String pushMsgUrl;
    
    
    
	public String toJSONString(){
		if(pushMsgType == TxlConstants.MESSAGE_TYPE_PLAIN_TEXT){
			return "{\"b\":6,\"c\":\""+content+"\",\"m\":\""+msgId+"\",\"sn\":\""+sendName+"\",\"s\":"+sendUserId+"}";
		}else{
			if(this.pushMsgUrl!=null){
				return "{\"b\":8,\"ptn\":\""+pushMsgTypeName+"\",\"c\":\""+content+"\",\"url\":\""+pushMsgUrl+"\",\"m\":\""+msgId+"\",\"sn\":\""+sendName+"\",\"s\":"+sendUserId+"}";
			}else{
				return "{\"b\":8,\"ptn\":\""+pushMsgTypeName+"\",\"c\":\""+content+"\",\"url\":\"\",\"m\":\""+msgId+"\",\"sn\":\""+sendName+"\",\"s\":"+sendUserId+"}";
			}
		}
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
