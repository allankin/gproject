package txl.contact.dao;

import java.util.List;
import java.util.Set;

import txl.config.TxlConstants;
import txl.contact.po.ContactVo;
import txl.log.TxLogger;
import txl.util.GB2Alpha;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Contacts.People;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;

public class ContactDao {
	
	private final static TxLogger log = new TxLogger(ContactDao.class, TxlConstants.MODULE_ID_MESSAGE);
	
	
	
	/**
	 *  得到手机通讯录联系人信息
	 *  
	 * @param context
	 * @param contactList
	 * @param contactPhoneSet  为null表示不考虑重复
	 *
	 */
    public static void getPhoneContacts(Context context,List<ContactVo> contactList,Set<String> contactPhoneSet)
    {
        ContentResolver resolver = context.getContentResolver();
        final String[] PHONES_PROJECTION         = new String[]
                {  Phone.NUMBER,Phone.DISPLAY_NAME, Phone.CONTACT_ID };
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        GB2Alpha alpha = new GB2Alpha();
        if (phoneCursor != null)
        {
            while (phoneCursor.moveToNext())
            {	
            	ContactVo cv = new ContactVo();

                // 得到手机号码
                String phoneNumber = phoneCursor.getString(0);
                if(contactPhoneSet!=null){
                	if(contactPhoneSet.contains(phoneNumber)){
                		continue;
                	}
                	contactPhoneSet.add(phoneNumber);
                }
                
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) continue;

                String contactName = phoneCursor.getString(1);
                Long contactId = phoneCursor.getLong(2);

                // 得到联系人头像ID
                //Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

                // 得到联系人头像Bitamp
                Bitmap contactPhoto = null;

                // photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                /*if (photoid > 0)
                {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                } else
                {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.contact_photo);
                }*/
                
                cv.displayName = contactName;
                cv.firstLetterSet = alpha.String2Alpha(cv.displayName);
                cv.firstLetter = cv.firstLetterSet.charAt(0);
                cv.phone = phoneNumber;
                cv.contactId = contactId;
                
                //log.info("phone contacts ----- displayName: "+contactName+",phone: "+phoneNumber+",contactId: "+contactId);
                contactList.add(cv);
            }
            phoneCursor.close();
        }
    }
    
    
    
    
    /**
     * 得到手机SIM卡联系人人信息
     * 
     * 
     * 注意：Sim卡中没有联系人头像
     * @param context
     * @param contactList
     * @param contactPhoneSet  为null表示不考虑重复
     */
    public static void getSIMContacts(Context context,List<ContactVo> contactList,Set<String> contactPhoneSet)
    {
    	/*
    	 * ????????????
    	 * 数组次序不起作用....
    	 * 
    	 * final String[] PHONES_PROJECTION         = new String[]
                {People.NUMBER,People.NAME,People._ID};*/
    	
        ContentResolver resolver = context.getContentResolver();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, null, null, null, null);

        if (phoneCursor != null)
        {
            while (phoneCursor.moveToNext())
            {
            	ContactVo cv = new ContactVo();
            	
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex("number"));
                if(contactPhoneSet!=null){
                	if(contactPhoneSet.contains(phoneNumber)){
                		continue;
                	}
                	contactPhoneSet.add(phoneNumber);
                }
                if (TextUtils.isEmpty(phoneNumber)) continue;
                String contactName = phoneCursor.getString(phoneCursor.getColumnIndex(People.NAME));
                Long contactId = phoneCursor.getLong(phoneCursor.getColumnIndex(People._ID));
                cv.displayName = contactName;
                cv.phone = phoneNumber;
                cv.contactId = contactId;
                //log.info("SIM contacts ----- displayName: "+contactName+",phone: "+phoneNumber+",contactId: "+contactId);
                contactList.add(cv);
            }

            phoneCursor.close();
        }
    }
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
	
	
	public static void printSIMContacts(Context context){
		 
        ContentResolver resolver = context.getContentResolver();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, null, null, null, null);
        
        if (phoneCursor != null)
        {
        	String[] cNames = phoneCursor.getColumnNames();;
            while (phoneCursor.moveToNext())
            {
            	StringBuilder sb = new StringBuilder();
            	for(String cName:cNames){
            		sb.append(cName+":"+phoneCursor.getString(phoneCursor.getColumnIndex(cName))+",");
            	}
            	log.info(sb.toString());
            }
            phoneCursor.close();
        }
	}
}
