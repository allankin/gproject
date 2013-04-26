package txl.contact.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import txl.config.TxlConstants;
import txl.contact.po.ContactVo;
import txl.log.TxLogger;
import txl.util.GB2Alpha;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import android.util.Log;

public class ContactDao {
	
	private final static TxLogger log = new TxLogger(ContactDao.class, TxlConstants.MODULE_ID_CONTACT);
	
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
        while(cur.getCount() > cur.getPosition()) {
            ContactVo contact = new ContactVo();
            List<String> phoneList = new ArrayList<String>();
            String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));
            String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
            String photo_id = cur.getString(cur.getColumnIndex(Phone.PHOTO_ID));
            String sortKey = cur.getString(cur.getColumnIndex("sort_key"));
        
            Log.i("contacts>>>", "name:" + name + "number:" + number + "photo:"+photo_id + "sort_key" + sortKey);
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
                add2List(searchList,contact);
            }
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
