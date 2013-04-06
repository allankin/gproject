package txl.message.pushmessage.dao;

import java.util.Map;

import txl.message.pushmessage.po.PushMsg;
import android.content.Context;

public class PushMsgDao {
	
	/**
	 * 
	 * @param context
	 * @param pushMsgMap
	 */
	public static void loadPushMsgList(Context context,
			Map<Integer,PushMsg> pushMsgMap){
		
		
		
		for(int i=0;i<100;i++){
			for(int j=0;j<5;j++){
				PushMsg pm = new PushMsg();
				pm.content="推送消息内容"+i;
				pm.dateStr = "4/7 12:20";
				pm.name = "test"+i;
				pm.type = i%2;
				pushMsgMap.put(i, pm);
			}
		}
	}
}
