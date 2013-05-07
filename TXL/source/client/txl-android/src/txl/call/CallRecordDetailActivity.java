package txl.call;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import txl.activity.R;
import txl.call.po.CallRecord;
import txl.config.Config;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CallRecordDetailActivity extends Activity {
	
	
	private Context                       mContext        = null;
	private ListView                      mListView       = null;
	
	private SimpleDateFormat sfd = new SimpleDateFormat("MM/dd HH:mm");
	private MyListAdapter                 mAdapter       = null;
	
	private int callRecordCount =0;
	private ArrayList<CallRecord> crList;
	
	private TextView callNameView;
	private TextView callPhoneView;
	private TextView callCountView;
	
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.call_detail);
	     
	     
	     mListView = (ListView)findViewById(R.id.call_list_detail);
	     
	     Intent intent = getIntent();
	     CallRecord cr = null;
	     if(intent!=null){
	    	 Bundle bundle = intent.getExtras();
	    	 Object obj = bundle.get(Config.INTENT_PARAM_LIST_DATA);
	    	 cr = (CallRecord)obj;
	    	 crList = cr.historyList;
	    	 callRecordCount = crList.size();
	    	 
		     
		     callNameView = (TextView)findViewById(R.id.call_name);
		     callCountView = (TextView)findViewById(R.id.call_count);
		     callPhoneView = (TextView)findViewById(R.id.call_phone);
		     
		     callNameView.setText(cr.name);
		     callCountView.setText(String.valueOf(callRecordCount));
		     callPhoneView.setText(cr.phoneNumber);
		     
		     
		     mAdapter = new MyListAdapter(this);
		     mListView.setAdapter(mAdapter);
	    	 
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
        TextView callDateView = null;
        TextView countView = null;
        Button statusBtnView = null;
        TextView durationView = null;
       
        public View getView(int position, View convertView, ViewGroup parent)
        {
        	
            ViewHolder holder =null;
            if (convertView == null || position < callRecordCount)
            {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.call_detail_item, null);
                holder.callDateView = (TextView)convertView.findViewById(R.id.call_date);
                holder.statusView = (ImageView)convertView.findViewById(R.id.call_statusBtn);
                holder.durationView = (TextView)convertView.findViewById(R.id.call_duration);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            
            CallRecord cr = crList.get(position);
            holder.callDateView.setText(cr.time);
            int callStatusResId = R.drawable.call_log_type_in;
            switch (cr.type) {
                case CallLog.Calls.INCOMING_TYPE:
                    callStatusResId = R.drawable.call_log_type_in;
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    callStatusResId = R.drawable.call_log_type_out;
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callStatusResId = R.drawable.call_log_type_miss;
                    break;
                default:
                    break;
            }
           
            holder.statusView.setBackgroundResource(callStatusResId);
            //statusBtnView.setText(statusStr);
            holder.durationView.setText(cr.getDurationString());
            return convertView;
        }
        
        private class ViewHolder {
            TextView callDateView = null;
            ImageView statusView = null;
            TextView durationView = null;
        }

    }
    
   
	
}
