package txl.message.pushmessage.po;

import java.io.Serializable;

public class PushMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	/*对方姓名*/
	public String name;
	public String dateStr;
	public String content;
	/*1:接收   2：发送  3：草稿   */
	public int type;
	/*对方用户Id*/
	public int userId;
	
	
	public String  toJSONStroing(){
		if(type==2){
			return "{\"b\":5,\"r:\":["+this.userId+"],\"c\":\""+content+"\"}";
		}
		return "";
	}
}
