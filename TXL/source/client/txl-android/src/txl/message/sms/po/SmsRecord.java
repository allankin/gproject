package txl.message.sms.po;

import java.util.ArrayList;


public class SmsRecord {
	/*短消息序号*/
	public int _id;
	/*对话序号*/
	public int threadId;
	
	/*发件人地址,手机号*/
	public String address;
	/*发件人在联系人列表中的序号，陌生人为0*/
	public int person;
	
	public long date;
	/*协议 0：SMS 1：MMS */
	public int protocol;
	/*是否阅读 0：未读  1：已读*/
	public int read;
	/*状态： */
	public int status = -1;
	/*类型， 1：接收 2：已发出*/
	public int type;
	/*消息内容*/
	public String body;
	/*短信服务中心号码编号*/
	public String serviceCenter;
	
	/*供显示用*/
	public String displayAddress;
	public String dateStr;
	
	public ArrayList<SmsRecord> historyList = new ArrayList<SmsRecord>();
}
