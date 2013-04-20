package txl.message.pushmessage.po;

import java.io.Serializable;
import java.sql.Timestamp;

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
		if(type==2){
			return "{\"b\":5,\"r:\":["+this.recUserId+"],\"c\":\""+content+"\"}";
		} 
		return "";
	}
}
