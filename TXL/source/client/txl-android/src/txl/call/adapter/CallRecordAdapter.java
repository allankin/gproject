package txl.call.adapter;

import java.util.Map;

import txl.activity.R;
import txl.call.CallRecordActivity;
import txl.call.CallRecordDetailActivity;
import txl.call.po.CallRecord;
import txl.config.Config;
import android.content.Context;
import android.content.Intent;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * @ClassName:  CallRecordAdapter.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-23 下午6:43:08
 * @Copyright: 版权由 HundSun 拥有
 */
public class CallRecordAdapter extends BaseAdapter
{
        private Context mContext;
        private Map<Integer,CallRecord> callRecordMap;
        public CallRecordAdapter(Context context,Map<Integer,CallRecord> callRecordMap)
        {
            mContext = context;
            this.callRecordMap = callRecordMap;
        }

        public int getCount()
        {
            return callRecordMap.size();
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
                //holder.countView = (TextView)convertView.findViewById(R.id.call_count);
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
            holder.statusBtnView.setText("   "+cr.count);
            
            //holder.countView.setText(String.valueOf(cr.count));
            int callStatusResId = R.drawable.ic_calllog_incomming_normal;
            switch (cr.type) {
                case CallLog.Calls.INCOMING_TYPE:
                    callStatusResId = R.drawable.btn_call_incoming;
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    callStatusResId = R.drawable.btn_call_outgoing;
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callStatusResId = R.drawable.btn_call_missed;
                    break;
                default:
                    break;
            }
            holder.statusBtnView.setBackgroundResource(callStatusResId);
            holder.statusBtnView.setTag(cr);
            return convertView;
        }

  
    private class ViewHolder {
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
            Intent intent = new Intent(mContext,CallRecordDetailActivity.class);
            intent.putExtra(Config.INTENT_PARAM_LIST_DATA, cr);
            mContext.startActivity(intent);
        }
        
    }; 
}
