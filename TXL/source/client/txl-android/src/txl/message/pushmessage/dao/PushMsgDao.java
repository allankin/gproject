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
import txl.util.Tool;
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
		if (!TxlConstants.IS_DAO_SINGLE_ABLE || pushMsgDao == null) {
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
		
		List<PushMsg> pushMsgList = _getContactPushMsg(null,null);
		int count=0;
		Map<Integer,Integer> pushMsgContactIdSet = new HashMap<Integer,Integer>();
		for(int i=0,len=pushMsgList.size();i<len;i++){
			PushMsg pushMsg = pushMsgList.get(i);
			/*接收消息*/
			if(pushMsg.type==TxlConstants.PUSH_MESSAGE_TYPE_RECEIVE){
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
		
		Map<Integer,Integer> pushMsgTypeSet = new HashMap<Integer,Integer>();
		List<PushMsg> classfiedPushMsgList = this.getClassfiedPushMsg(null);
          for(int i=0,len=classfiedPushMsgList.size();i<len;i++){
            PushMsg pushMsg = classfiedPushMsgList.get(i);
            int pushMsgType = pushMsg.pushMsgType;
            Integer index = pushMsgTypeSet.get(pushMsgType);
            if(index!=null){
                PushMsgRecord record = pushMsgRecordMap.get(index);
                record.pushMsgRecordList.add(pushMsg);
            }else{
                PushMsgRecord record = new PushMsgRecord();
                record.pushMsg = pushMsg;
                record.pushMsgRecordList.add(pushMsg);
                pushMsgRecordMap.put(count, record);
                pushMsgTypeSet.put(pushMsgType, count);
                count++;
            }
          }
		log.info("loadPushMsgList .... pushMsgRecordMap size :"+pushMsgRecordMap.size());
		 
	}
	
	/**
	 * 保存消息
	 * 包括接收与发送消息
	 * @param recPushMsg
	 */
	public synchronized void savePushMsg(PushMsg pushMsg){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(!db.isOpen()){
			db = this.context.openOrCreateDatabase(TxlConstants.DB_NAME, Context.MODE_PRIVATE, null);
		}
		db.beginTransaction();
		ContentValues cv = new ContentValues();
		cv.put("msg_id", pushMsg.msgId);
		cv.put("rec_user_id", pushMsg.recUserId);
		cv.put("send_user_id", pushMsg.sendUserId);
		cv.put("send_name", pushMsg.sendName);
		cv.put("content", pushMsg.content);
		cv.put("rec_name", pushMsg.recName);
		cv.put("type", pushMsg.type);
		cv.put("pushmsg_type", pushMsg.pushMsgType);
		cv.put("pushmsg_type_name", pushMsg.pushMsgTypeName);
		cv.put("pushmsg_url", pushMsg.pushMsgUrl);
		cv.put("dtime",pushMsg.dtime.toString());
		cv.put("is_read", 0);
		db.insert("txl_push_msg", null, cv);
		log.info("savePushMsg .....  msg_id:"+pushMsg.msgId+",rec_user_id:"+pushMsg.recUserId+",content:"+pushMsg.content+",recName:"+pushMsg.recName);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		log.info("saveReceivePushMsg... ");
	} 
	
	private List<PushMsg> _getContactPushMsg(Integer contactId,String orderStr){
		String sql = "select msg_id,rec_user_id,send_user_id,send_name,content," +
				"type,dtime,is_read,pushmsg_type_name,pushmsg_url," +
				"pushmsg_type,rec_name " +
				"from txl_push_msg where  pushmsg_type="+TxlConstants.PUSHMSG_TYPE_NOT_CLASSFIED;
		if(contactId!=null && contactId!=0){
			sql +=" and (rec_user_id = "+contactId+" or send_user_id = "+contactId+")";
		}
		if(orderStr==null){
			sql +=" order by dtime desc ";
		}else{
			sql +=orderStr;
		}
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
			pushMsg.isRead = cursor.getInt(7);
			pushMsg.pushMsgTypeName = cursor.getString(8);
			pushMsg.pushMsgUrl = cursor.getString(9);
			pushMsg.pushMsgType = TxlConstants.PUSHMSG_TYPE_NOT_CLASSFIED;
			pushMsg.recName = cursor.getString(11);
			pushMsgList.add(pushMsg);
			log.info("getContactPushMsg ... msgId: "+pushMsg.msgId+",recuserid: "+pushMsg.recUserId+",sendUserId:"+
					pushMsg.sendUserId+",sendName:"+pushMsg.sendName+",content:"
					+pushMsg.content+",isRead:"+pushMsg.isRead+",type:"+pushMsg.type+",pushMsgTypeName:"+pushMsg.pushMsgTypeName+
					",pushMsgUrl:"+pushMsg.pushMsgUrl+",recName: "+pushMsg.recName);
		}
		
		log.info("getContactPushMsg  size: "+pushMsgList.size());
		cursor.close();
		db.close();
		return pushMsgList;
	}
	
	/**
	 * 
	 * @param contactId 表示查找该联系人的消息记录
	 * @return
	 */
	public List<PushMsg> getContactPushMsg(Integer contactId){
		return this._getContactPushMsg(contactId," order by dtime asc ");
	}
	/**
	 * 根据推送消息类型id，查找推送消息。
	 * 若id为null，则查找所有的可分类的推送消息。
	 * @param pushMsgType
	 * @return
	 */
	public List<PushMsg> getClassfiedPushMsg(Integer pushMsgType){
	    String sql = "select msg_id,rec_user_id,send_user_id,send_name,content," +
	    		"type,dtime,is_read,pushmsg_type_name,pushmsg_url," +
	    		"pushmsg_type,rec_name " +
	    		"from txl_push_msg where ";
        if(pushMsgType!=null && pushMsgType!=TxlConstants.PUSHMSG_TYPE_NOT_CLASSFIED){
            sql +="  pushmsg_type = "+pushMsgType;
        }else{
            sql +="  pushmsg_type!="+TxlConstants.PUSHMSG_TYPE_NOT_CLASSFIED;
        }
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
            pushMsg.isRead = cursor.getInt(7);
            pushMsg.pushMsgTypeName = cursor.getString(8);
            pushMsg.pushMsgUrl = cursor.getString(9);
            pushMsg.pushMsgType = cursor.getInt(10);
            pushMsg.recName = cursor.getString(11);
            pushMsgList.add(pushMsg);
            log.info("getPushMsg ... msgId: "+pushMsg.msgId+",recuserid: "+pushMsg.recUserId+",sendUserId:"+
                    pushMsg.sendUserId+",sendName:"+pushMsg.sendName+",content:"
                    +pushMsg.content+",isRead:"+pushMsg.isRead+",type:"+pushMsg.type+",pushMsgTypeName:"+pushMsg.pushMsgTypeName+
                    ",pushMsgUrl:"+pushMsg.pushMsgUrl+",recName: "+pushMsg.recName);
        }
        
        log.info("getContactPushMsg  size: "+pushMsgList.size());
        cursor.close();
        db.close();
        return pushMsgList;
	}
	
	public PushMsg getPushMsgByMsgId(String msgId){
		PushMsg pushMsg =null;
		String sql = "select msg_id,rec_user_id,send_user_id,send_name,content,type,dtime,is_read,pushmsg_type_name,pushmsg_url from txl_push_msg where msg_id='"+msgId+"'";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.moveToNext()) {
			pushMsg = new PushMsg();
			pushMsg.msgId = cursor.getString(0);
			pushMsg.recUserId = cursor.getInt(1);
			pushMsg.sendUserId = cursor.getInt(2);
			pushMsg.sendName = cursor.getString(3);
			pushMsg.content = cursor.getString(4);
			pushMsg.type = cursor.getInt(5);
			pushMsg.dtime =  Timestamp.valueOf(cursor.getString(6));
			pushMsg.isRead = cursor.getInt(7);
			pushMsg.pushMsgTypeName = cursor.getString(8);
			pushMsg.pushMsgUrl = cursor.getString(9);
			log.info("getPushMsg ... msgId: "+pushMsg.msgId+",recuserid: "+pushMsg.recUserId+",sendUserId:"+
					pushMsg.sendUserId+",sendName:"+pushMsg.sendName+",content:"
					+pushMsg.content+",isRead:"+pushMsg.isRead+",type:"+pushMsg.type+",pushMsgTypeName:"+pushMsg.pushMsgTypeName+
					",pushMsgUrl:"+pushMsg.pushMsgUrl);
		}
		cursor.close();
		return pushMsg;
	}
	
	/**
	 * 根据msgId，更新消息是否已读
	 * (接收消息可用)
	 * @param msgId
	 * @param isRead
	 * @return
	 */
	public boolean updatePushMsgReadStatus(String msgId,int isRead){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("is_read", isRead);
        int row = db.update("txl_push_msg", cv, "msg_id=?", new String[]{msgId});
        db.close();
        return row==1;
	}
	/**
	 * 按发送人的userId更新是否已读
	 * @param userId
	 * @param isRead
	 * @return
	 */
	public boolean updatePushMsgReadStatusByUserId(int userId,int isRead){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("is_read", isRead);
        int row = db.update("txl_push_msg", cv, "send_user_id=?", new String[]{String.valueOf(userId)});
        db.close();
        return row>=1;
	}
	/**
	 * 根据推送消息的分类id更新是否已读
	 * @param pushMsgType
	 * @param isRead
	 * @return
	 */
	public boolean updatePushMsgReadStatusByPushMsgType(int pushMsgType,int isRead){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("is_read", isRead);
        
        int row = db.update("txl_push_msg", cv, "pushmsg_type=?", new String[]{String.valueOf(pushMsgType)});
        db.close();
        return row>=1;
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
	
	
	/**
	 * 查询未读信息数量
	 * @return
	 */
	public int getUnreadPushMsgCount(){
		String sql = "select count(*) as num from txl_push_msg where is_read = 0 and type=1 ";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count =0;
		if(db.isOpen()){
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}
			cursor.close();
			db.close();
		}
		return count;
	}
	
}
