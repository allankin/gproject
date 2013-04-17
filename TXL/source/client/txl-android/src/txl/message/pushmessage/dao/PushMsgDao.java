package txl.message.pushmessage.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import txl.BaseDao;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.po.PushMsg;
import txl.message.pushmessage.po.PushMsgRecord;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PushMsgDao extends BaseDao{
	private final TxLogger log = new TxLogger(PushMsgDao.class,
			TxlConstants.MODULE_ID_MESSAGE);
	private static PushMsgDao pushMsgDao;

	private PushMsgDao(Context context) {
		super(context);
	}

	public static PushMsgDao getSingle(Context context) {
		if (pushMsgDao == null) {
			pushMsgDao = new PushMsgDao(context);
		}
		return pushMsgDao;
	}
	/**
	 * 
	 * @param context
	 * @param pushMsgRecordMap
	 */
	public void loadPushMsgList(Context context,
			Map<Integer,PushMsgRecord> pushMsgRecordMap){
		
		List<PushMsg> pushMsgList = getPushMsg();
		int count=0;
		Map<Integer,Integer> pushMsgContactIdSet = new HashMap<Integer,Integer>();
		for(int i=0,len=pushMsgList.size();i<len;i++){
			PushMsg pushMsg = pushMsgList.get(i);
			/*接收消息*/
			if(pushMsg.type==1){
				Integer index = pushMsgContactIdSet.get(pushMsg.sendUserId);
				if(index!=null){
					PushMsgRecord record = pushMsgRecordMap.get(index);
					record.pushMsgRecordList.add(pushMsg);
				}else{
					PushMsgRecord record = new PushMsgRecord();
					record.pushMsg = pushMsg;
					record.pushMsgRecordList.add(pushMsg);
					pushMsgRecordMap.put(count, record);
					pushMsgContactIdSet.put(pushMsg.sendUserId, count);
					count++;
				}
			}
			/*发送/草稿 消息*/
			else{
				Integer index = pushMsgContactIdSet.get(pushMsg.recUserId);
				if(index!=null){
					PushMsgRecord record = pushMsgRecordMap.get(index);
					record.pushMsgRecordList.add(pushMsg);
				}else{
					PushMsgRecord record = new PushMsgRecord();
					record.pushMsg = pushMsg;
					record.pushMsgRecordList.add(pushMsg);
					pushMsgRecordMap.put(count, record);
					pushMsgContactIdSet.put(pushMsg.recUserId, count);
					count++;
				}
			}
		}
		/*for(int i=0;i<100;i++){
			for(int j=0;j<5;j++){
				PushMsgRecord pm = new PushMsgRecord();
				pm.pushMsg = new PushMsg();
				pm.pushMsg.content="推送消息内容"+i;
				pm.pushMsg.dateStr = "4/7 12:20";
				pm.pushMsg.name = "test"+i;
				pm.pushMsg.type = i%2;
				pushMsgRecordMap.put(i, pm);
			}
		}*/
	}
	
	/**
	 * 保存消息
	 * 包括接收与发送消息
	 * @param recPushMsg
	 */
	public void savePushMsg(PushMsg pushMsg){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		ContentValues cv = new ContentValues();
		cv.put("msg_id", pushMsg.msgId);
		cv.put("rec_user_id", pushMsg.recUserId);
		cv.put("send_user_id", pushMsg.sendUserId);
		cv.put("send_name", pushMsg.sendName);
		cv.put("content", pushMsg.content);
		cv.put("type", pushMsg.type);
		cv.put("dtime",pushMsg.dtime.toString());
		db.insert("txl_push_msg", null, cv);
		log.info("msg_id:"+pushMsg.msgId+",rec_user_id:"+pushMsg.recUserId+",content:"+pushMsg.content);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		log.info("saveReceivePushMsg... ");
	} 
	
	
	
	public List<PushMsg> getPushMsg(){
		String sql = "select msg_id,rec_user_id,send_user_id,send_name,content,type,dtime from txl_push_msg ";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		List<PushMsg> pushMsgList = new ArrayList<PushMsg>();
		while (cursor.moveToNext()) {
			PushMsg pushMsg = new PushMsg();
			pushMsg.msgId = cursor.getString(0);
			pushMsg.recUserId = cursor.getInt(1);
			pushMsg.sendUserId = cursor.getInt(2);
			pushMsg.sendName = cursor.getString(3);
			pushMsg.content = cursor.getString(4);
			pushMsg.type = cursor.getInt(5);
			pushMsg.dtime =  Timestamp.valueOf(cursor.getString(6));
			pushMsgList.add(pushMsg);
		}
		log.info("getPushMsg  size: "+pushMsgList.size());
		cursor.close();
		db.close();
		return pushMsgList;
	}
	
	/**
	 * 获取推送消息数目
	 * @return
	 */
	public int getPushMsgCount(){
		String sql= "select count(*) as num from txl_push_msg ";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		int count =0;
		if (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}
	
}
