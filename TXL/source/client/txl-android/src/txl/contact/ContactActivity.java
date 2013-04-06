package txl.contact;

import java.util.ArrayList;

import txl.activity.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @ClassName: CallRecord.java
 * @Description:
 * @Author JinChao
 * @Date 2013-2-17 下午3:54:19
 * @Copyright: 版权由 HundSun 拥有
 */
public class ContactActivity extends Activity
{

    private final String          TAG                       = ContactActivity.class.getSimpleName();

    Context                       mContext                  = null;

    /** 获取库Phon表字段 **/
    /*
     * private static final String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER,
     * Photo.PHOTO_ID, Phone.CONTACT_ID };
     */
    private static final String[] PHONES_PROJECTION         = new String[]
                                                            { Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID };

    /** 联系人显示名称 **/
    private static final int      PHONES_DISPLAY_NAME_INDEX = 0;

    /** 电话号码 **/
    private static final int      PHONES_NUMBER_INDEX       = 1;

    /** 头像ID **/
    //private static final int      PHONES_PHOTO_ID_INDEX     = 2;

    /** 联系人的ID **/
    private static final int      PHONES_CONTACT_ID_INDEX   = 2;

    /** 联系人名称 **/
    private ArrayList<String>     mContactsName             = new ArrayList<String>();

    /** 联系人头像 **/
    private ArrayList<String>     mContactsNumber           = new ArrayList<String>();

    /** 联系人头像 **/
    private ArrayList<Bitmap>     mContactsPhonto           = new ArrayList<Bitmap>();

    ListView                      personalListView                 = null;
    MyListAdapter                 myAdapter                 = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        final LayoutInflater inflater = LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.tab_contact, null));
        personalListView = (ListView) this.findViewById(R.id.contact_list); 
        /** 得到手机通讯录联系人信息 **/
        getPhoneContacts();

        myAdapter = new MyListAdapter(this);
        personalListView.setAdapter(myAdapter);

        personalListView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                // 调用系统方法拨打电话
                Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mContactsNumber.get(position)));
                startActivity(dialIntent);
            }
        });
        
        
        
        
        
        
        
        
        
        
         
        
      
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

    }

    /** 得到手机通讯录联系人信息 **/
    private void getPhoneContacts()
    {
        ContentResolver resolver = mContext.getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null)
        {
            while (phoneCursor.moveToNext())
            {

                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) continue;

                // 得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                // 得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

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

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
                //mContactsPhonto.add(contactPhoto);
            }

            phoneCursor.close();
        }
    }

    /** 得到手机SIM卡联系人人信息 **/
    private void getSIMContacts()
    {
        ContentResolver resolver = mContext.getContentResolver();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null)
        {
            while (phoneCursor.moveToNext())
            {

                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) continue;
                // 得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                // Sim卡中没有联系人头像

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
            }

            phoneCursor.close();
        }
    }

    class MyListAdapter extends BaseAdapter
    {

        public MyListAdapter(Context context)
        {
            mContext = context;
        }

        public int getCount()
        {
            // 设置绘制数量
            return mContactsName.size();
        }

        @Override
        public boolean areAllItemsEnabled()
        {
            return false;
        }

        public Object getItem(int position)
        {
            return position;
        }

        public long getItemId(int position)
        {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView iamge = null;
            TextView nameView = null;
            TextView phoneView = null;
            if (convertView == null || position < mContactsNumber.size())
            {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, null);
                nameView = (TextView) convertView.findViewById(R.id.contact_name);
                phoneView = (TextView) convertView.findViewById(R.id.contact_phone);
            }
            // 绘制联系人名称
            nameView.setText(mContactsName.get(position));
            // 绘制联系人号码
            phoneView.setText(mContactsNumber.get(position));
            // 绘制联系人头像
            //iamge.setImageBitmap(mContactsPhonto.get(position));
            return convertView;
        }

    }
}
