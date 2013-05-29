package txl.message.pushmessage.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import txl.activity.R;
import txl.config.TxlConstants;
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
	private SimpleDateFormat sfd = new SimpleDateFormat("MM/dd HH:mm");
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
            
            String dateStr = sfd.format(new Date(msg.dtime.getTime()));
            if(msg.type == TxlConstants.PUSH_MESSAGE_TYPE_RECEIVE){
            	String content = msg.content;
            	holder.sendContentView.setVisibility(View.INVISIBLE);
            	holder.recContentView.setVisibility(View.VISIBLE);
            	if(msg.pushMsgType!=TxlConstants.PUSHMSG_TYPE_NOT_CLASSFIED){
            		if(msg.pushMsgUrl.trim().length()>0){
            			content +="  >>查看详情";
            		}
            	}
            	content+="  "+dateStr;
            	holder.recContentView.setText(content);
            }else if(msg.type == TxlConstants.PUSH_MESSAGE_TYPE_SEND){
            	holder.sendContentView.setText(msg.content+"  "+dateStr);
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