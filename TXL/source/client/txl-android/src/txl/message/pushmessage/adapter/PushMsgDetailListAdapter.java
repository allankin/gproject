package txl.message.pushmessage.adapter;

import java.util.List;

import txl.activity.R;
import txl.config.TxlConstants;
import txl.contact.po.ShareUser;
import txl.log.TxLogger;
import txl.message.pushmessage.po.PushMsg;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PushMsgDetailListAdapter extends BaseAdapter 
{
	
	private final static TxLogger log = new TxLogger(PushMsgDetailListAdapter.class, TxlConstants.MODULE_ID_CONTACT);
    	private Context mContext;
    	private List<PushMsg> pushMsgList;
        public PushMsgDetailListAdapter(Context context,List<PushMsg> pushMsgList)
        {
            mContext = context;
            this.pushMsgList = pushMsgList;
        }

        public int getCount()
        {
            return pushMsgList.size();
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
        private ViewHolder holder ;
        public View getView(int position, View convertView, ViewGroup parent)
        {
        	PushMsg msg = pushMsgList.get(position);
            if (convertView == null)
            {
            	holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.pushmsg_detail_list_item, null);
                holder.nameView = (TextView) convertView.findViewById(R.id.pushmsg_name);
                holder.recContentView = (TextView) convertView.findViewById(R.id.pushmsg_rec_content);
                holder.sendContentView = (TextView) convertView.findViewById(R.id.pushmsg_send_content);
                holder.meView = (TextView) convertView.findViewById(R.id.pushmsg_me);
                convertView.setTag(holder);
            }else{
            	holder = (ViewHolder)convertView.getTag();
            }
            if(msg.type == TxlConstants.PUSH_MESSAGE_TYPE_RECEIVE){
            	holder.nameView.setText(msg.sendName);
            	holder.recContentView.setText(msg.content);
            	holder.sendContentView.setVisibility(View.INVISIBLE);
            	holder.recContentView.setVisibility(View.VISIBLE);
            }else if(msg.type == TxlConstants.PUSH_MESSAGE_TYPE_SEND){
            	holder.meView.setText("我");
            	holder.sendContentView.setText(msg.content);
            	holder.recContentView.setVisibility(View.INVISIBLE);
            	holder.sendContentView.setVisibility(View.VISIBLE);
            }
            /*草稿不显示*/
            else if(msg.type == TxlConstants.PUSH_MESSAGE_TYPE_DRAFT){
            	
            }
            return convertView;
        }
        
        class ViewHolder{
        	public TextView nameView;
        	public TextView recContentView;
        	public TextView sendContentView;
        	public TextView meView;
        }
    }