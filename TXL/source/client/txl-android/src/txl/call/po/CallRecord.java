package txl.call.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import txl.util.Tool;

public class CallRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	// 0:未接 1：接通电话 2：呼叫电话
	public int status = -1;
	public int type;
	public String phoneNumber;
	public String name;
	public String time;
	/* 通话次数 */
	public int count;
	/* 通话持续时间,秒 */
	public long duration;
	public ArrayList<CallRecord> historyList = new ArrayList<CallRecord>();

	public String getDurationString() {
		return Tool.convertSecondToTimeStr(duration);
	}

}