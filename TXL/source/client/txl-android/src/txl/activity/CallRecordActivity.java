package txl.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import txl.dial.DialWindow;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @ClassName: CallRecordAcitivity.java
 * @Description:
 * @Author JinChao
 * @Date 2013-2-17 下午4:49:03
 * @Copyright: 版权由 HundSun 拥有
 */
public class CallRecordActivity extends ListActivity
{

    Context                       mContext        = null;
    ListView                      mListView       = null;
    MyListAdapter                 myAdapter       = null;

    /** 联系人名称 **/
    private ArrayList<String>     mContactsName   = new ArrayList<String>();

    /** 联系人头像 **/
    private ArrayList<String>     mContactsNumber = new ArrayList<String>();
    private ArrayList<CallRecord> callRecordList  = new ArrayList<CallRecord>();

    private  DialWindow dw;
    
    public void onCreate(Bundle savedInstanceState)
    {
        
        mContext = this;
        mListView = this.getListView();
        getCallRecord();
        myAdapter = new MyListAdapter(this);
        setListAdapter(myAdapter);
        int width = getWindowManager().getDefaultDisplay().getWidth()-15;       
        int height = getWindowManager().getDefaultDisplay().getHeight()/2;   
        dw = new DialWindow(this, width, height);
        
        super.onCreate(savedInstanceState);
    }

    private void getCallRecord()
    {
        ContentResolver cr = getContentResolver();
        final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]
                                       { CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE,
                                               CallLog.Calls.DATE }, null, null,
                                       CallLog.Calls.DEFAULT_SORT_ORDER);
        if (cursor != null)
        {
            for (int i = 0; i < cursor.getCount(); i++)
            {
                cursor.moveToPosition(i);
                CallRecord callRecord = new CallRecord();
                callRecord.phoneNumber = cursor.getString(0);
                callRecord.name = cursor.getString(1);
                callRecord.type = cursor.getInt(2);
                SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date date = new Date(Long.parseLong(cursor.getString(3)));
                callRecord.time = sfd.format(date);
                callRecordList.add(callRecord);
            }
            cursor.close();
        }

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(!dw.isShowing()&&keyCode == KeyEvent.KEYCODE_MENU){
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            dw.showAtLocation(rootView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
        }
        else{
            dw.dismiss();
        }
        return true;
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
            return callRecordList.size();
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
            TextView title = null;
            TextView text = null;
            TextView callDate = null;
            if (convertView == null || position < callRecordList.size())
            {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.call_list, null);
                title = (TextView) convertView.findViewById(R.id.call_name);
                text = (TextView) convertView.findViewById(R.id.call_phone);
                callDate = (TextView)convertView.findViewById(R.id.call_date);
            }
            // 绘制联系人名称
            title.setText(callRecordList.get(position).name);
            // 绘制联系人号码
            text.setText(callRecordList.get(position).phoneNumber);
            callDate.setText(callRecordList.get(position).time);
            return convertView;
        }

    }

    class CallRecord
    {

        // 0:未接 1：接通电话 2：呼叫电话
        public int    status = -1;
        public int    type;
        public String name;
        public String phoneNumber;
        public String time;

    }

}
