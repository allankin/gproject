package txl.message.sms.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import txl.config.TxlConstants;
import txl.contact.dao.ContactDao;
import txl.log.TxLogger;
import txl.message.sms.po.SmsRecord;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

public class SmsDao {
	private final static TxLogger log = new TxLogger(SmsDao.class, TxlConstants.MODULE_ID_MESSAGE);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
	
	
	public static void loadSmsList(Context context,
			Map<Integer,SmsRecord> smsRecordMap,
			Map<Integer,SmsRecord> smsUnReadRecordMap,
			Map<Integer,SmsRecord> smsDraftRecordMap) {  
        final String SMS_URI_ALL = "content://sms/";  
        final String SMS_URI_INBOX = "content://sms/inbox";  
        final String SMS_URI_SEND = "content://sms/sent";  
        final String SMS_URI_DRAFT = "content://sms/draft";  
        final String SMS_URI_OUTBOX = "content://sms/outbox";  
        final String SMS_URI_FAILED = "content://sms/failed";  
        final String SMS_URI_QUEUED = "content://sms/queued";  
  
        
        try {  
            Uri uri = Uri.parse(SMS_URI_ALL);  
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type","status","thread_id","read" };  
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, "date desc");      // 获取手机内部短信  
            Map<String,Integer> smsMap = new HashMap<String,Integer>();
            if(cursor!=null){
            	int count=0;
            	int unReadCount =0;
            	int draftCount = 0;
            	for (int i = 0; i < cursor.getCount(); i++)
                {
            		cursor.moveToPosition(i);
            		SmsRecord sr = new SmsRecord();
            		sr._id = cursor.getInt(0);
            		sr.address = cursor.getString(1);
            		sr.person = cursor.getInt(2);
            		sr.date = cursor.getLong(4);
            		sr.type = cursor.getInt(5);
            		sr.status = cursor.getInt(6);
            		sr.threadId = cursor.getInt(7);
            		sr.read = cursor.getInt(8);
            		sr.dateStr = sdf.format(new Date(sr.date));
            		
            		if(sr.read == 0){
            			//unReadSmsRecordList.add(sr);
            			smsUnReadRecordMap.put(unReadCount, sr);
            			unReadCount++;
            		}
            		
            		
            		/*草稿信息，若在保存草稿时，又关闭了(来电，关机等)，就会出现address null的情况 */
            		if(sr.type == 3){
            			smsDraftRecordMap.put(draftCount, sr);
            			if(sr.address == null){
	            			log.info("sr.threadId: "+sr.threadId);
	        				String recipientIds = SmsDao.getRecipientIdsByThredsId(context, sr.threadId);
	        				if(recipientIds!=null){
	            				String[] recipientIdsArray = recipientIds.split(",");
	            				StringBuilder sb = new StringBuilder();
	            				for(int j=0,len=recipientIdsArray.length;j<len;j++){
	            					String recipientId = recipientIdsArray[j];
	            					String address = ContactDao.getAddressFromCanonicalAddressesTable(context, recipientId);
	            					sb.append(address+",");
	            				}
	            				sr.address  = sb.toString();
	        				}
            			}
            			draftCount++;
            		}
            		
            		//String key = sr.address;
            		String key = sr.threadId+"";
            		Integer smsIndex = smsMap.get(key);
            		if(smsIndex!=null){
            			SmsRecord _sr = smsRecordMap.get(smsIndex);
            			/*群发短信,接收人用|间隔*/
            			if(!_sr.address.contains(sr.address)){
            				_sr.address+="|"+sr.address;
            			}
            			_sr.historyList.add(sr);
            		}else{
            			smsRecordMap.put(count, sr);
            			smsMap.put(key, count);
            			sr.body = cursor.getString(3);
            			sr.historyList.add(sr);
            			count++;
            		}
                }
            	log.info("smsDraftRecordMap size : "+smsDraftRecordMap.size());
            	if (!cursor.isClosed()) {  
                	cursor.close();  
                	cursor = null;  
                }  
            }
  
        } catch (SQLiteException ex) {  
        	ex.printStackTrace();
        }
	}
	
	public static void loadSmsCategory(List<String> smsCategoryList){
		smsCategoryList.add("全部");
		smsCategoryList.add("未读");
		smsCategoryList.add("草稿");
		//smsCategoryList.add("搜藏");
	}
	/**
	 * 获取短信接收人的Id
	 * @param context
	 * @param threadId
	 * @return
	 */
	public static String getRecipientIdsByThredsId(Context context,int threadId){
		Cursor threadCursor = context.getContentResolver().query(Uri.parse("content://mms-sms/conversations?simple=true"), 
				null, "_id=?", new String[]{String.valueOf(threadId)}, null);
		String recipientIds=null;
		if (threadCursor.moveToFirst()) {
			recipientIds = threadCursor.getString(threadCursor.getColumnIndex("recipient_ids"));
			threadCursor.close();
		}
		log.info("recipientIds: "+recipientIds);
		return recipientIds;
	}
	
	public static void printThreadsTable(Context context){
		Cursor tempCursor = context.getContentResolver().query(Uri.parse("content://mms-sms/conversations?simple=true"), 
				null, null, null, null);
		String[] cNames = tempCursor.getColumnNames();
		while(tempCursor.moveToNext()){
			String str = "";
			for(String cName:cNames){
				str+=cName+":"+tempCursor.getString(tempCursor.getColumnIndex(cName))+",";
			}
			log.info("threds table: "+str);
		}
		tempCursor.close();
	}
	
	public static void printSms(Context context){
		String SMS_URI_ALL = "content://sms/";  
		 Uri uri = Uri.parse(SMS_URI_ALL);  
		 Cursor cursor = context.getContentResolver().query(uri, null, null, null, "date desc"); 
		 if(cursor!=null){
			 String[] cNames = cursor.getColumnNames();
			 while(cursor.moveToNext()){
				String str = "";
				for(String cName:cNames){
					str+=cName+":"+cursor.getString(cursor.getColumnIndex(cName))+",";
				}
				log.info("sms table: "+str);
			 }
		 }
	}
}
