package txl.call;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import txl.activity.R;
import txl.call.po.CallRecord;
import txl.common.DialWindow;
import txl.config.Config;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @ClassName: CallRecordAcitivity.java
 * @Description:
 * @Author JinChao
 * @Date 2013-2-17 下午4:49:03
 * @Copyright: 版权由 HundSun 拥有
 */
public class CallRecordActivity extends Activity
{

	private Context                       mContext        = null;
	private ListView                      mListView       = null;
	private MyListAdapter                 mAdapter       = null;

    /** 联系人名称 **/
    //private ArrayList<String>     mContactsName   = new ArrayList<String>();

    /** 联系人头像 **/
    //private ArrayList<String>     mContactsNumber = new ArrayList<String>();
    //private ArrayList<CallRecord> callRecordList  = new ArrayList<CallRecord>();
    
    /*经过汇总后的通话记录*/
    private Map<Integer,CallRecord> callRecordMap = new HashMap<Integer,CallRecord>();
    /*汇总后的通话记录次数*/
    private int callRecordCount;
    private  DialWindow dw;
    
    private SimpleDateFormat sfd = new SimpleDateFormat("MM/dd HH:mm");
    private View layout ;
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        layout =  LayoutInflater.from(this).inflate(R.layout.tab_call,null);
        setContentView(layout);
        TextView tv = (TextView)findViewById(R.id.header);
        tv.setText("通话记录");
        mContext = this;
        mListView = (ListView)findViewById(R.id.call_list);
        getCallRecord();
        mAdapter = new MyListAdapter(this);
        mListView.setAdapter(mAdapter);
        int width = getWindowManager().getDefaultDisplay().getWidth()-15;       
        //int height = getWindowManager().getDefaultDisplay().getHeight();   
        int height = 520;
        dw = new DialWindow(this, width, height);
        Config.tabHost.getTabWidget().getChildTabViewAt(0).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.ACTION_UP == event.getAction()){
					dw.showAtLocation(layout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
				}
				return false;
			}
		});
        
       
        
    }
    
    public void onResume(){
    	super.onResume();
    }

    private void getCallRecord()
    {
        ContentResolver cr = getContentResolver();
        final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]
                                       { CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE,
                                               CallLog.Calls.DATE,CallLog.Calls.DURATION }, null, null,
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
            callRecordCount = callRecordMap.keySet().size();
            cursor.close();
            phoneMap=null;
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
            return callRecordCount;
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
        	ViewHolder holder =null;
            if (convertView == null)
            {
            	holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.call_list_item, null);
                holder.callNameView = (TextView) convertView.findViewById(R.id.call_name);
                holder.callPhoneView = (TextView) convertView.findViewById(R.id.call_phone);
                holder.callDateView = (TextView)convertView.findViewById(R.id.call_date);
                holder.countView = (TextView)convertView.findViewById(R.id.call_count);
                holder.statusBtnView = (Button)convertView.findViewById(R.id.call_statusBtn);
                holder.statusBtnView.setOnClickListener(callRecordDetailClickListener);
                convertView.setTag(holder);
            }else{
            	holder = (ViewHolder) convertView.getTag();
            }
            
            CallRecord cr = callRecordMap.get(position);
            // 绘制联系人名称
            holder.callNameView.setText(cr.name);
            // 绘制联系人号码
            holder.callPhoneView.setText(cr.phoneNumber);
            holder.callDateView.setText(cr.time);
            holder.countView.setText(String.valueOf(cr.count));
            String statusStr="";
            switch (cr.type) {
				case CallLog.Calls.INCOMING_TYPE:
					statusStr = "来电";
					break;
				case CallLog.Calls.OUTGOING_TYPE:
					statusStr = "拨打";
					break;
				case CallLog.Calls.MISSED_TYPE:
					statusStr ="未接";
					break;
				default:
					break;
			}
            
            holder.statusBtnView.setText(statusStr);
            holder.statusBtnView.setTag(cr);
            return convertView;
        }

    }
    
    class ViewHolder {
        TextView callNameView = null;
        TextView callPhoneView = null;
        TextView callDateView = null;
        TextView countView = null;
        Button statusBtnView = null;
	}
    
    
    
    
    private View.OnClickListener callRecordDetailClickListener = new View.OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
        	CallRecord cr = (CallRecord)v.getTag();
        	Intent intent = new Intent(CallRecordActivity.this,CallRecordDetailActivity.class);
        	intent.putExtra(Config.INTENT_PARAM_LIST_DATA, cr);
        	startActivity(intent);
        	//Toast.makeText(CallRecordActivity.this, ((Button)v).getText(), 3000).show();
        }
        
    }; 

}
