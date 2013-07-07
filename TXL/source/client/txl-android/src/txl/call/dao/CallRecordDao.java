package txl.call.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import txl.call.po.CallRecord;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

/**
 * @ClassName:  CallRecordDao.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-23 下午6:39:22
 * @Copyright: 版权由 HundSun 拥有
 */
public class CallRecordDao
{
    private static SimpleDateFormat sfd = new SimpleDateFormat("MM/dd HH:mm");
    
    public static void getCallRecord(Context context,Map<Integer,CallRecord> callRecordMap,String phoneNumber)
    {
        
        ContentResolver cr = context.getContentResolver();
        String selection = null;
        if(phoneNumber!=null){
        	selection = CallLog.Calls.NUMBER+" like '%"+phoneNumber+"%' ";
        }
        final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]
                                       { CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE,
                                               CallLog.Calls.DATE,CallLog.Calls.DURATION }, selection, null,
                                       CallLog.Calls.DEFAULT_SORT_ORDER);
        
        Map<String,Integer> phoneMap = new HashMap<String,Integer>();
        
        if (cursor != null)
        {
            int count=0;
            for (int i = 0; i < cursor.getCount(); i++)
            {
                cursor.moveToPosition(i);
                CallRecord callRecord = new CallRecord();
                callRecord.phoneNumber = cursor.getString(0);
                callRecord.name = cursor.getString(1);
                callRecord.type = cursor.getInt(2);
                Date date = new Date(cursor.getLong(3));
                callRecord.time = sfd.format(date);
                callRecord.duration = cursor.getLong(4);
                
                Integer phoneIndex = phoneMap.get(callRecord.phoneNumber);
                
                if(phoneIndex!=null){
                    CallRecord existCallRecord = callRecordMap.get(phoneIndex);
                    existCallRecord.count++;
                    existCallRecord.historyList.add(callRecord);
                }else{
                    callRecord.count++;
                    callRecordMap.put(count, callRecord);
                    phoneMap.put(callRecord.phoneNumber, count);
                    callRecord.historyList.add(callRecord);
                    count++;
                }
            }
            cursor.close();
            phoneMap=null;
        }

    }
}
