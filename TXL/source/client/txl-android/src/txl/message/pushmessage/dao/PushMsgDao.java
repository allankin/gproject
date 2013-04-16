package txl.message.pushmessage.dao;

import java.util.Map;

import txl.message.pushmessage.po.PushMsgRecord;
import android.content.Context;

public class PushMsgDao {
	
	/**
	 * 
	 * @param context
	 * @param pushMsgRecordMap
	 */
	public static void loadPushMsgList(Context context,
			Map<Integer,PushMsgRecord> pushMsgRecordMap){
		
		for(int i=0;i<100;i++){
			for(int j=0;j<5;j++){
				PushMsgRecord pm = new PushMsgRecord();
				pm.pushMsg.content="推送消息内容"+i;
				pm.pushMsg.dateStr = "4/7 12:20";
				pm.pushMsg.name = "test"+i;
				pm.pushMsg.type = i%2;
				pushMsgRecordMap.put(i, pm);
			}
		}
	}
}
