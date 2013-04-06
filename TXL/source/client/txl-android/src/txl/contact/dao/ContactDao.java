package txl.contact.dao;

import txl.config.TxlConstants;
import txl.contact.po.ContactVo;
import txl.log.TxLogger;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

public class ContactDao {
	
	private final static TxLogger log = new TxLogger(ContactDao.class, TxlConstants.MODULE_ID_MESSAGE);
	/**
	 * 根据电话号码查询联系人信息
	 * @param context
	 * @param phoneNumber
	 * @return
	 */
	public static ContactVo getContactByPhoneNumber(Context context,String phoneNumber) { 
		ContactVo cv = new ContactVo();
		if(phoneNumber==null || phoneNumber.length()==0){
			cv.displayName = "匿名";
			return cv;
		}
	    Uri personUri = Uri.withAppendedPath(  
	            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phoneNumber);  
	    Cursor cur = context.getContentResolver().query(personUri,  
	            new String[] { PhoneLookup.DISPLAY_NAME },  
	            null, null, null );  
	    if( cur.moveToFirst() ) {  
	          
	        cv.displayName = cur.getString(0);  
	        cur.close();  
	        return cv;  
	    }  
	    return null;  
	}
	
	
	public static String getContactNameByPhoneNumber(Context context,String phoneNumber){
		if(phoneNumber==null || phoneNumber.length()==0){
			return "匿名";
		}
	    Uri personUri = Uri.withAppendedPath(  
	            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phoneNumber);  
	    Cursor cur = context.getContentResolver().query(personUri,  
	            new String[] { PhoneLookup.DISPLAY_NAME },  
	            null, null, null );  
	    if( cur.moveToFirst() ) {  
	        String displayName = cur.getString(0);  
	        cur.close();  
	        return displayName;  
	    }  
	    return null;
	}
	/**
	 * 
	 * @param context
	 * @param _id
	 * @return
	 */
	public static ContactVo getContactById(Context context,int _id){
		Cursor cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,   
                new String[] {PhoneLookup.DISPLAY_NAME},   
                " _id=?",   
                new String[] {String.valueOf(_id)}, null);  
        if(cur != null &&  
            cur.moveToFirst()) {  
            ContactVo cv = new ContactVo();  
            cv.displayName = cur.getString(0);  
            cur.close();  
            return cv;  
        }  
        return null;
	}
	
	/**
	 * 根据recipientId, 从CanonicalAddresses表中取出电话号码(address)
	 * @param context
	 * @param recipientId
	 * @return
	 */
	public static String getAddressFromCanonicalAddressesTable(Context context,String recipientId){
		Cursor addressCursor = context.getContentResolver().query(  
                Uri.parse("content://mms-sms/canonical-address/" + recipientId), null, null, null, null);  
		String address =null;
        if (addressCursor.moveToFirst()) {  
        	address= addressCursor.getString(addressCursor.getColumnIndex("address"));  
        	addressCursor.close();  
        }  
        log.info("address : "+address);
        return address;
	}
}
