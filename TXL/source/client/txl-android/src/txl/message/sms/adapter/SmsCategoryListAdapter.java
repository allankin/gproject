package txl.message.sms.adapter;

import java.util.List;
import java.util.Map;

import txl.activity.R;
import txl.message.sms.po.SmsRecord;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

public class SmsCategoryListAdapter extends BaseAdapter
    {
		private Context mContext;
		private List<String> smsCategoryList;
		private Map<Integer,SmsRecord> smsRecordMap;
		private Map<Integer,SmsRecord> smsUnReadRecordMap;
		private Map<Integer,SmsRecord> smsDraftRecordMap;
		private SmsListAdapter                 smsListAdapter ;
        public SmsCategoryListAdapter(Context context,
        		List<String> smsCategoryList,
        		Map<Integer,SmsRecord> smsRecordMap,
    			Map<Integer,SmsRecord> smsUnReadRecordMap,
    			Map<Integer,SmsRecord> smsDraftRecordMap,
    			SmsListAdapter  smsListAdapter)
        {
            this.mContext = context;
            this.smsCategoryList = smsCategoryList;
            this.smsDraftRecordMap = smsDraftRecordMap;
            this.smsUnReadRecordMap = smsUnReadRecordMap;
            this.smsRecordMap = smsRecordMap;
            this.smsListAdapter = smsListAdapter;
            
        }

        public int getCount()
        {
            return smsCategoryList.size();
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
        
        
        class ViewHolder{
        	Button smsCategoryBtn;
        	 
        }
    	
        public View getView(int position, View convertView, ViewGroup parent)
        {
        	String smsCategory = smsCategoryList.get(position);
        	ViewHolder holder;
        	if (convertView == null)
            {
        		holder = new ViewHolder();
        		convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_category_list_item, null);
        		holder.smsCategoryBtn =(Button) convertView.findViewById(R.id.sms_category);
        		convertView.setTag(holder);
            } else{
            	holder = (ViewHolder)convertView.getTag();
            }
        	holder.smsCategoryBtn.setTag(position);
			holder.smsCategoryBtn.setText(smsCategory);
			
			holder.smsCategoryBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Integer idx = (Integer)v.getTag();
					Toast.makeText(mContext, idx+"", 3000).show();
					if(idx!=null){
						switch (idx) {
						case 0:
							{
								smsListAdapter.smsRecordMap = smsRecordMap;
								smsListAdapter.notifyDataSetChanged();
							}
							break;
						case 1:/*未读短信*/
							{
								smsListAdapter.smsRecordMap = smsUnReadRecordMap;
								smsListAdapter.notifyDataSetChanged();
							}
							break;
						case 2: /*草稿短信*/
							{
								smsListAdapter.smsRecordMap = smsDraftRecordMap;
								smsListAdapter.notifyDataSetChanged();
							}
							break;	
						 
						default:
							break;
						}
					}
				}
			});
			return  convertView;
        }
    }