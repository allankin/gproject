package txl.contact.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import txl.config.TxlConstants;
import txl.contact.po.ContactVo;
import txl.contact.po.PhoneContact;
import txl.contact.po.PhoneContactData;
import txl.contact.po.PhoneContactList;
import txl.log.TxLogger;
import txl.util.GB2Alpha;
import txl.util.Tool;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import android.util.Log;

public class ContactDao {
	
	private final static TxLogger log = new TxLogger(ContactDao.class, TxlConstants.MODULE_ID_CONTACT);
	private final static String TAG = ContactDao.class.getSimpleName();
	
	static String[] pinyin = { "a", "ai",  "an", "ang", "ao", "ba", "bai", "ban", "bang",   
	                           "bao", "bei", "ben", "beng", "bi", "bian", "biao", "bie", "bin",   
	                           "bing", "bo", "bu", "ca", "cai", "can", "cang", "cao",  "ce",   
	                           "ceng", "cha", "chai", "chan", "chang", "chao", "che",  "chen",   
	                           "cheng", "chi", "chong", "chou", "chu", "chuai", "chuan",
	                           "chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu", 
	                           "cuan", "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao",   
	                           "de", "deng", "di", "dian", "diao", "die", "ding", "diu", "dong",   
	                           "dou", "du", "duan", "dui", "dun", "duo", "e", "en", "er",  "fa",   
	                           "fan", "fang", "fei", "fen", "feng", "fo", "fou", "fu", "ga", 
	                           "gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng",  "gong",   
	                           "gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo",   
	                           "ha", "hai", "han", "hang", "hao", "he", "hei", "hen", "heng",   
	                           "hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun",  
	                           "huo", "ji", "jia", "jian", "jiang", "jiao", "jie", "jin", "jing",   
	                           "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan",   
	                           "kang", "kao", "ke", "ken", "keng", "kong", "kou", "ku", "kua",    
	                           "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan",    
	                           "lang", "lao", "le", "lei", "leng", "li", "lia", "lian", "liang",   
	                           "liao", "lie", "lin", "ling", "liu", "long", "lou", "lu", "lv",   
	                           "luan", "lue", "lun", "luo", "ma", "mai", "man", "mang", "mao",   
	                           "me", "mei", "men", "meng", "mi", "mian", "miao", "mie", "min",    
	                           "ming", "miu", "mo", "mou", "mu", "na", "nai", "nan", "nang",   
	                           "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang", "niao",   
	                           "nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan", "nue",   
	                           "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen",   
	                           "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", "pu",
	                           "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing", 
	                           "qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao",    
	                           "re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui",   
	                           "run", "ruo", "sa", "sai", "san", "sang", "sao", "se", "sen",    
	                           "seng", "sha", "shai", "shan", "shang", "shao", "she", "shen",    
	                           "sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang",   
	                           "shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui",    
	                           "sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng",
	                           "ti", "tian", "tiao", "tie", "ting", "tong", "tou",  "tu", "tuan",   
	                           "tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen",   
	                           "weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie",   
	                           "xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya",   
	                           "yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong",   
	                           "you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang",   
	                           "zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan",   
	                           "zhang", "zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou",    
	                           "zhu", "zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo",   
	                           "zi", "zong", "zou", "zu", "zuan", "zui", "zun", "zuo" };
	
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
                {  Phone.NUMBER,Phone.DISPLAY_NAME, Phone.CONTACT_ID,Phone.SORT_KEY_PRIMARY,Phone.SORT_KEY_ALTERNATIVE };
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
                
                //log.info("getPhoneContacts... phoneNumber:"+phoneNumber);
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
                //cv.firstChineseLetter = contactName.substring(0,1);
                String sortKey = phoneCursor.getString(3);
                String sortKeyAlt = phoneCursor.getString(4);
                log.info("phone contacts ----- displayName: "+contactName+",phone: "+phoneNumber+",contactId: "+contactId+",sort_key: "+sortKey+",sortKeyAlt:"+sortKeyAlt);
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
        try{
        	Cursor phoneCursor = resolver.query(uri, null, null, null, null);

            if (phoneCursor != null)
            {
                while (phoneCursor.moveToNext())
                {
                	ContactVo cv = new ContactVo();
                	
                    String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex("number"));
                    //log.info("getSIMContacts... phoneNumber:"+phoneNumber);
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
        }catch(Exception e){
        	log.error("getSIMContacts..."+Tool.getExceptionTrace(e));
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
	
	public static void getSearchUser(Context context,List<ContactVo> searchList,String condition ) {
        searchList.clear();
        if (condition == null || condition.equals(""))
            return ;
        String[] projection= {Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID, "sort_key"};
        String selection = Phone.NUMBER + " like '%" + condition + "%' or " 
                         + Phone.DISPLAY_NAME + " like '%" + condition + "%' or "
                         + "sort_key" + " like '%" + getPYSearchRegExp(condition, "%") + "%'";
        
        Cursor cur = context.getContentResolver().query(Phone.CONTENT_URI, projection, selection, null, Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        cur.moveToFirst();
        Set<Long> contactIdSet = new HashSet<Long>();
        
        while(cur.getCount() > cur.getPosition()) {
            ContactVo contact = new ContactVo();
            long contactId = cur.getLong(cur.getColumnIndex(Phone.CONTACT_ID));
            if(contactIdSet.contains(contactId)){
            	continue;
            }
            List<String> phoneList = new ArrayList<String>();
            String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));
            String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
            String photo_id = cur.getString(cur.getColumnIndex(Phone.PHOTO_ID));
            String sortKey = cur.getString(cur.getColumnIndex("sort_key"));
        
            log.info("name:" + name + "number:" + number + "photo:"+photo_id + "sort_key" + sortKey+",contactId:"+contactId);
            boolean show = true;
            if (isPinYin(condition) ) {
                if(containCn(sortKey)) {
                    show = pyMatches(sortKey, condition.replaceAll(" ", ""));
                } else {
                    if (name != null && name.startsWith(condition)) //如果sort_key 不包含中文 则需要用display_name匹配 英文匹配采取前缀匹配
                        show = true;
                    else
                        show = false;
                }
            }
            System.out.println("is show " + show);
            
            if (show) {
                contact.displayName = name;
                contact.sortKey = sortKey;
                contact.phone = number;
                phoneList.add(number);
                contact.phoneList = phoneList; 
                contact.contactId = contactId;
                add2List(searchList,contact);
            }
            contactIdSet.add(contactId);
            cur.moveToNext();
        }
        cur.close();
        }
	public static void add2List(List<ContactVo> list , ContactVo person) {
        for(int i = 0; i < list.size(); ++i) {
            if(list.get(i).displayName.equals(person.displayName)) {
                for(int k = 0; k < person.phoneList.size(); ++k)
                    list.get(i).phoneList.add(person.phoneList.get(k));
                return ;
            }
        }   
        list.add(person);
    }
	public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
    
    public static boolean isPinYin(String str) {
        Pattern pattern = Pattern.compile("[ a-zA-Z]*");
        return pattern.matcher(str).matches();
    }
    
    public static boolean containCn(String str) {        
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");          
        return pattern.matcher(str).find();
        /*
        while (m.find()) {      
               for (int i = 0; i <= m.groupCount(); i++) {      
                    count = count + 1;      
                }      
            }
       System.out.println("共有 " + count + "个 ");   
       */
    }
	
	/**
     * 
     * @param str 搜索字符串
     * @param exp 追加的正则表达式
     * @return 拼音搜索正则表达式
     */
    public static String getPYSearchRegExp(String str, String exp) {
        int start = 0;
        String regExp = "";
        str = str.toLowerCase();
        boolean isFirstSpell = true;
        for (int i = 0; i < str.length(); ++i) {
            String tmp = str.substring(start, i + 1);
            isFirstSpell = binSearch(tmp) ? false : true;
            
            if (isFirstSpell) {
                regExp += str.substring(start, i) + exp;
                start = i;
            } else {
                isFirstSpell = true;
            }
            
            if (i == str.length() - 1)
                regExp += str.substring(start, i + 1) + exp;        
        }
        return regExp;
    }
    
    /**
     * 2分法查找拼音列表
     * @param str 拼音字符串
     * @return 是否是存在于拼音列表
     */
    public static boolean binSearch(String str) {
        int mid = 0;
        int start = 0;
        int end = pinyin.length - 1;
        
        while (start < end) {
            mid = start + ((end - start) / 2 );
            if (pinyin[mid].matches(str + "[a-zA-Z]*"))
                return true;
            
            if (pinyin[mid].compareTo(str) < 0) 
                start = mid + 1;
            else 
                end = mid - 1;
        }
        return false;
    }

    /**
     * 拼音匹配
     * @param src 含有中文的字符串  
     * @param des 查询的拼音
     * @return 是否能匹配拼音
     */
    public static boolean pyMatches(String src, String des) {
        if (src != null) {
            src = src.replaceAll("[^ a-zA-Z]", "").toLowerCase();
            src = src.replaceAll("[ ]+", " ");
            String condition = getPYSearchRegExp(des, "[a-zA-Z]* ");
            
            /*
            Pattern pattern = Pattern.compile(condition);
            Matcher m = pattern.matcher(src);  
            return m.find(); 
            */
            String[] tmp = condition.split("[ ]");
            String[] tmp1 = src.split("[ ]");
            
            for(int i = 0; i + tmp.length <= tmp1.length; ++i) {
                String str = "";
                for (int j = 0; j < tmp.length; j++)
                    str += tmp1[i+j] + " ";
                if (str.matches(condition))
                    return true;
            }
        } 
        return false;
    }
	
    
    /**
     * 获取联系人头像
     * 
     * @param people_id
     * @return
     */
    public static byte[] getPhoto(Context context,long contactId) {
            String photo_id = null;
            String selection1 = ContactsContract.Contacts._ID + " = " + contactId;
            Cursor cur1 = context.getContentResolver().query(
                            ContactsContract.Contacts.CONTENT_URI, null, selection1, null,
                            null);
            if (cur1.getCount() > 0) {
                    cur1.moveToFirst();
                    photo_id = cur1.getString(cur1
                                    .getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
                    Log.i(TAG, "photo_id:" + photo_id);   // 如果没有头像，这里为空值
            }
            
            String selection = null;
            if(photo_id == null){                        
                    return null;
            }else{
                    selection = ContactsContract.Data._ID + " = " + photo_id;
            }
            
            String[] projection = new String[] { ContactsContract.Data.DATA15 };
            Cursor cur = context.getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI, projection, selection, null, null);
            cur.moveToFirst();
            byte[] contactIcon = cur.getBlob(0);
            Log.i(TAG, "conTactIcon:" + contactIcon);
            if (contactIcon == null) {
                    return null;
            } else {
                    return contactIcon;
            }
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
	
	public static List<Long> getGroupIdByContactId(Context context,Long contactId){
		List<Long> groupIdList = new ArrayList<Long>();
	    Uri uri = Data.CONTENT_URI;
	    String where = String.format(
	            "%s = ? AND %s = ?",
	            Data.MIMETYPE,
	            GroupMembership.CONTACT_ID);

	    String[] whereParams = new String[] {
	               GroupMembership.CONTENT_ITEM_TYPE,
	               Long.toString(contactId),
	    };

	    String[] selectColumns = new String[]{
	            GroupMembership.GROUP_ROW_ID,
	    };


	    Cursor groupIdCursor = context.getContentResolver().query(
	            uri, 
	            selectColumns, 
	            where, 
	            whereParams, 
	            null);
	    try{
	        while (groupIdCursor.moveToNext()) {
	        	groupIdList.add(groupIdCursor.getLong(0));
	        }
	        return groupIdList; // Has no group ...
	    }finally{
	        groupIdCursor.close();
	    }
	}
	
	public static String getGroupNameByGroupId(Context context,long groupId){
	    Uri uri = Groups.CONTENT_URI;
	    String where = String.format("%s = ?", Groups._ID);
	    String[] whereParams = new String[]{Long.toString(groupId)};
	    String[] selectColumns = {Groups.TITLE};
	    Cursor c = context.getContentResolver().query(
	            uri, 
	            selectColumns,
	            where, 
	            whereParams, 
	            null);

	    try{
	        if (c.moveToFirst()){
	            return c.getString(0);  
	        }
	        return null;
	    }finally{
	        c.close();
	    }
	}
	
	/**
     * 获取生日，仅一个
     * @param context
     * @param contactId
     * @return
     */
    public static String getBirthday(Context context,long contactId){
    	    
 	    String columns[] = {
 	         ContactsContract.CommonDataKinds.Event.START_DATE,
 	         ContactsContract.CommonDataKinds.Event.TYPE,
 	         ContactsContract.CommonDataKinds.Event.MIMETYPE,
 	    };

 	    String where = Event.TYPE + "=" + Event.TYPE_BIRTHDAY + 
 	                    " and " + Event.MIMETYPE + " = '" + Event.CONTENT_ITEM_TYPE + "' and "  
 	    		+ ContactsContract.Data.CONTACT_ID + " = " + contactId;
 	    String[] selectionArgs = null;
 	    Cursor birthdayCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, columns, where, selectionArgs, null); 
 	   String birthday = null;
 	    if (birthdayCur.getCount() > 0) {
 	        if (birthdayCur.moveToNext()) {
 	             birthday = birthdayCur.getString(birthdayCur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
 	        }
 	    }
 	    birthdayCur.close();
 	    return birthday;
    }
    
    /**
     * 获取该联系人地址
     * 
     * @param context
     * @param contactId
     * @param datas
     */
    public static void getContactAddress(Context context,long contactId,List<PhoneContactData> datas){
    	/* 获取该联系人地址 */
		Cursor addressCursor = context
				.getContentResolver()
				.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
		if (addressCursor.moveToFirst()) {
			do {
				String street = addressCursor
						.getString(addressCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
				String city = addressCursor
						.getString(addressCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
				String region = addressCursor
						.getString(addressCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
				String postCode = addressCursor
						.getString(addressCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
				String formatAddress = addressCursor
						.getString(addressCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
				String country = addressCursor.getString(addressCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
				String typeLabel = "";
				int type = addressCursor
						.getInt(addressCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
				if(type == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM){
					typeLabel = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL));
				}else{
					typeLabel = ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabel(context.getResources(), type, null).toString();
				}
				PhoneContactData data = new PhoneContactData();
				data.setDataCategoryId(type);
				data.setDataCustomLabel(typeLabel);
				data.setDataTotalType(TxlConstants.CONTACT_DATA_TOATAL_TYPE_ADDRESS);
				String dataValue = country+","+region+","+city+","+street+","+postCode;
				data.setDataValue(dataValue);
				datas.add(data);
				
				Log.i(TAG, "street:" + street + ",city：" + city
						+ ",region：" + region + ",postCode:" + postCode
						+ ",formatAddress:" + formatAddress+",typeLabel:"+typeLabel);

			} while (addressCursor.moveToNext());
			addressCursor.close();
		}
    }
    /**
     * 获取联系人邮箱
     * @param context
     * @param contactId
     * @param datas
     */
    public static void getContactEmail(Context context,long contactId,List<PhoneContactData> datas){
    	Cursor emailCursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID
						+ " = " + contactId, null, null);
		
		if (emailCursor.moveToFirst()) {
			do {
				int emailType = emailCursor
						.getInt(emailCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
				String emailValue = emailCursor
						.getString(emailCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				String emailTypeLabel  ="";
				if(emailType == Email.TYPE_CUSTOM){
					emailTypeLabel = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
				}else{
					emailTypeLabel = Email.getTypeLabel(context.getResources(), emailType, null).toString();
				}
				PhoneContactData data = new PhoneContactData();
				data.setDataCategoryId(emailType);
				data.setDataCustomLabel(emailTypeLabel);
				data.setDataTotalType(TxlConstants.CONTACT_DATA_TOATAL_TYPE_EMAIL);
				data.setDataValue(emailValue);
				datas.add(data);
				Log.i(TAG, "emailType:" + emailType + ",emailValue"
						+ emailValue+",emailTypeLabel:"+emailTypeLabel);
			} while (emailCursor.moveToNext());
			emailCursor.close();
		}
    }
    
    /**
     * 获取IM
     * 暂不区分协议
     * @param context
     * @param contactId
     * @param datas
     */
    public static void getContactIm(Context context,long contactId,List<PhoneContactData> datas){
    	Cursor IMCursor = context.getContentResolver().query(
				Data.CONTENT_URI,
				new String[] { Data._ID, Im.PROTOCOL, Im.DATA,Im.TYPE },
				Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
						+ Im.CONTENT_ITEM_TYPE + "'",
				new String[] { String.valueOf(contactId) }, null);
		if (IMCursor.moveToFirst()) {
			do {
				int protocol = IMCursor.getInt(IMCursor
						.getColumnIndex(Im.PROTOCOL));
				String dataValue = IMCursor.getString(IMCursor
						.getColumnIndex(Im.DATA));
				String typeLabel = "";
				int type = IMCursor.getInt(IMCursor
						.getColumnIndex(Im.TYPE));
				if(type == Im.TYPE_CUSTOM){
					typeLabel = IMCursor.getString(IMCursor
							.getColumnIndex(Im.LABEL));
				}else {
					typeLabel = Im.getTypeLabel(context.getResources(), protocol, null).toString();
                }
				PhoneContactData data = new PhoneContactData();
				data.setDataCategoryId(type);
				data.setDataCustomLabel(typeLabel);
				data.setDataTotalType(TxlConstants.CONTACT_DATA_TOATAL_TYPE_IM);
				data.setDataValue(dataValue);
				datas.add(data);
				Log.i(TAG,"protocol:"+protocol+",date:"+dataValue+",type:"+type+",typeLabel:"+typeLabel);
			} while (IMCursor.moveToNext());
			IMCursor.close();
		}
    }
	/**
	 * 获取组织信息，用逗号分隔，如：阿里巴巴,开发经理
	 *
	 * @param context
	 * @param contactId
	 * @param datas
	 */
    public static void getContactOrg(Context context,long contactId,List<PhoneContactData> datas){
    	Cursor organizationCursor = context.getContentResolver().query(  
                Data.CONTENT_URI,  
                new String[] { Data._ID, Organization.COMPANY,  
                        Organization.TITLE,Organization.TYPE,Organization.LABEL},  
                Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"  
                        + Organization.CONTENT_ITEM_TYPE + "'",  
                new String[] { String.valueOf(contactId) }, null);  
        if (organizationCursor.moveToFirst()) {  
            do {  
                String company = organizationCursor.getString(organizationCursor  
                        .getColumnIndex(Organization.COMPANY));  
                String title = organizationCursor.getString(organizationCursor  
                        .getColumnIndex(Organization.TITLE));  
                int type = organizationCursor.getInt(organizationCursor  
                        .getColumnIndex(Organization.TYPE));
                String typeLabel = "";
                
                if (type == Organization.TYPE_CUSTOM) {
                	typeLabel = organizationCursor.getString(organizationCursor
                        		.getColumnIndex(Organization.LABEL));
                }else {
                	typeLabel = Organization.getTypeLabel(context.getResources(), type, null).toString();
                }
                PhoneContactData data = new PhoneContactData();
				data.setDataCategoryId(type);
				data.setDataCustomLabel(typeLabel);
				data.setDataTotalType(TxlConstants.CONTACT_DATA_TOATAL_TYPE_ORG);
				data.setDataValue(company+","+title);
				datas.add(data);
                Log.i(TAG,"company:"+company+",title:"+title+",type:"+type+",typeLabel:"+typeLabel);
            } while (organizationCursor.moveToNext());  
            organizationCursor.close();
        }  
    }
    
    /**
     * 获取备注，仅取一个
     * @param context
     * @param contactId
     * @return
     */
    public static String getContactNote(Context context,long contactId){
    	Cursor noteCursor = context.getContentResolver().query(  
                Data.CONTENT_URI,  
                new String[] { Data._ID, Note.NOTE },  
                Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"  
                        + Note.CONTENT_ITEM_TYPE + "'",  
                new String[] { String.valueOf(contactId) }, null);  
    	String noteinfo = null;
        if (noteCursor.moveToNext()) {  
        	noteinfo = noteCursor.getString(noteCursor  
			        .getColumnIndex(Note.NOTE));  
			Log.i(TAG,"noteinfo:"+noteinfo);
        }
        noteCursor.close();
        return noteinfo;
    }
    /**
     * 获取昵称 仅取一个
     * @param context
     * @param contactId
     * @return
     */
    public static String getContactNickName(Context context,long contactId){
    	 Cursor nicknameCursor = context.getContentResolver().query(  
                 Data.CONTENT_URI,  
                 new String[] { Data._ID, Nickname.NAME},  
                 Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"  
                         + Nickname.CONTENT_ITEM_TYPE + "'",  
                 new String[] { String.valueOf(contactId) }, null); 
    	 String nickname_ = null;
         if (nicknameCursor.moveToFirst()) {  
             do {  
            	 nickname_  = nicknameCursor.getString(nicknameCursor  
                         .getColumnIndex(Nickname.NAME));  
                 Log.i(TAG,"nickname:"+nickname_);  
             } while (nicknameCursor.moveToNext());  
             nicknameCursor.close();
         }  
         return nickname_;
    }
    
    /**
     * 获取联系电话
     * @param context
     * @param contactId
     * @param datas
     */
    public static void getContactTel(Context context,long contactId,List<PhoneContactData> datas){
    	Cursor phoneCursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID
						+ " = " + contactId, null, null);
		if (phoneCursor != null && phoneCursor.moveToFirst()) {
			do {
				 
				String phoneNumber = phoneCursor  
                        .getString(phoneCursor  
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));  
                int phoneType = phoneCursor  
                        .getInt(phoneCursor  
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)); 
                String phoneTypeLabel = "";
                if(phoneType == CommonDataKinds.Phone.TYPE_CUSTOM){
                	phoneTypeLabel = phoneCursor  
                            .getString(phoneCursor  
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));  
                }else{
                	phoneTypeLabel = ContactsContract.CommonDataKinds.Phone
                    		.getTypeLabel(context.getResources(), phoneType, null).toString();
                }
                PhoneContactData data = new PhoneContactData();
				data.setDataCategoryId(phoneType);
				data.setDataCustomLabel(phoneTypeLabel);
				data.setDataTotalType(TxlConstants.CONTACT_DATA_TOATAL_TYPE_TEL);
				data.setDataValue(phoneNumber);
				datas.add(data);
                Log.i(TAG,"phoneNumber:"+phoneNumber+",phoneType:"+phoneType+",phoneTypeLabel:"+phoneTypeLabel);
			} while (phoneCursor.moveToNext());
			phoneCursor.close();
		}
    }
    
	public static PhoneContactList<PhoneContact> getAllContactsInfo(Context context) {
		PhoneContactList<PhoneContact> phoneContacts = new PhoneContactList<PhoneContact>();
		
		Cursor cur = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		while (cur != null && cur.moveToNext()) {
			PhoneContact phoneContact = new PhoneContact();
			String[] cNames = cur.getColumnNames();
			StringBuilder columns = new StringBuilder();
			for (String cName : cNames) {
				columns.append(cName + ",");
			}
			Log.i(TAG, columns.toString());

			int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);

			int displayNameColumn = cur
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			int phoneCountColumn = cur
					.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
			String customRingtone = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.CUSTOM_RINGTONE));
			
			StringBuilder contactInfo = new StringBuilder();
			long contactId = cur.getLong(idColumn);
			String disPlayName = cur.getString(displayNameColumn);
			 
			int phoneCount = cur.getInt(phoneCountColumn);
			contactInfo.append(ContactsContract.Contacts.DISPLAY_NAME + ":"+ disPlayName+
					","+ContactsContract.Contacts.HAS_PHONE_NUMBER+ ":" + phoneCount+
					",customRingtone:"+customRingtone);
			
			contactInfo.append(",<->,");
			Log.i(TAG, contactInfo.toString());
			
			phoneContact.setName(disPlayName);
			phoneContact.setCustomRingtone(customRingtone);
			
			/* 
			 * 获取头像 */
			
			byte[] photoBytes = ContactDao.getPhoto(context, contactId);
			if(photoBytes!=null){
				String photo = android.util.Base64.encodeToString(photoBytes, android.util.Base64.NO_WRAP);
				Log.i(TAG, "photo:"+photo);
				phoneContact.setPhoto(photo);
			}
			
			/* 获取生日 */
			String birthday = ContactDao.getBirthday(context, contactId);
			if(birthday!=null){
				phoneContact.setBirthday(birthday);
				Log.i(TAG,"birthday:"+birthday);
			}
			
			String groupIds = "";
			/*获取分组信息*/
			List<Long> groupIdList = ContactDao.getGroupIdByContactId(context, contactId);
			if(groupIdList.size()>0){
				for(Long groupId:groupIdList){
					groupIds+=groupId+","; 
					String groupName = ContactDao.getGroupNameByGroupId(context, groupId);
					if(groupName!=null){
						Log.i(TAG,"groupName:"+groupName+",groupId:"+groupId);
					}
				}
			}
			phoneContact.setGroupIds(groupIds);
			
			List<PhoneContactData> datas = phoneContact.getPhoneContactDatas();
			/*获取联系人地址*/
			ContactDao.getContactAddress(context, contactId, datas);
			
			/* 获取该联系人邮箱 */
			ContactDao.getContactEmail(context, contactId, datas);

			/* 获取该联系人IM */
			ContactDao.getContactIm(context, contactId, datas);
			
			/* 获取该联系人组织 */  
			ContactDao.getContactOrg(context, contactId, datas);
			
			// 获取备注信息  
			phoneContact.setNote(ContactDao.getContactNote(context, contactId));
  
                /* 获取nickname信息  */ 
			phoneContact.setNickname(ContactDao.getContactNickName(context, contactId));
            
			/* 获取联系人所有电话号码 */
			if (phoneCount > 0) {
				ContactDao.getContactTel(context, contactId, datas);
			}
			phoneContacts.add(phoneContact);
		}
		cur.close();
		return phoneContacts;
	}
}
