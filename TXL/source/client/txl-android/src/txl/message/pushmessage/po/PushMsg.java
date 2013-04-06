package txl.message.pushmessage.po;

import java.util.ArrayList;
import java.util.List;

public class PushMsg {
	/*对方姓名*/
	public String name;
	public String dateStr;
	public String content;
	/*1:接收   2：发送  3：草稿   */
	public int type;
	/*对方用户Id*/
	public int userId;
	
	public List<PushMsg> pushMsgRecordList = new ArrayList<PushMsg>();
}
