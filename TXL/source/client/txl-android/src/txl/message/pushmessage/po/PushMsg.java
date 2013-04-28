package txl.message.pushmessage.po;

import java.io.Serializable;
import java.sql.Timestamp;

import txl.config.TxlConstants;

public class PushMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	public int recUserId;
	public int sendUserId;
	public String content;
	public String sendName;
	/*用手机，向外发送消息时设置*/
	public String recName;
	public String msgId;
	/*消息时间*/
	public Timestamp dtime;
	/*1:接收   2：发送  3：草稿   */
	public int type;
	/*是否已读  0：未读  1：已读*/
	public int isRead;
	
	public String  toJSONString(){
		if(type==TxlConstants.PUSH_MESSAGE_TYPE_SEND){
			return "{\"b\":"+TxlConstants.BIZID_REQUEST_DATA+",\"u\":"+this.sendUserId+",\"r\":["+this.recUserId+"],\"c\":\""+content+"\"}";
		}else if(type==TxlConstants.PUSH_MESSAGE_TYPE_RECEIVE){
			
		}
		return "";
	}
}
