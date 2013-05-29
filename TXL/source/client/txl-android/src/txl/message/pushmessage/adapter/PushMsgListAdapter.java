package txl.message.pushmessage.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import txl.activity.R;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.po.PushMsgRecord;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PushMsgListAdapter extends BaseAdapter {
	private final static TxLogger log = new TxLogger(PushMsgListAdapter.class,
			TxlConstants.MODULE_ID_MESSAGE);
	
	private SimpleDateFormat sfd = new SimpleDateFormat("MM/dd HH:mm");
	private Context mContext;
	public Map<Integer, PushMsgRecord> pushMsgMap;

	public PushMsgListAdapter(Context context, Map<Integer, PushMsgRecord> pushMsgMap) {
		this.mContext = context;
		this.pushMsgMap = pushMsgMap;
	}

	public int getCount() {
		return pushMsgMap.size();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	public Object getItem(int position) {
		return pushMsgMap.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView pushMsgNameView;
		TextView pushMsgDateView;
		TextView pushMsgContent;
		TextView pushMsgCount;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		PushMsgRecord pm = this.pushMsgMap.get(position);
		
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.pushmsg_list_item, null);
			holder = new ViewHolder();
			holder.pushMsgNameView = (TextView) convertView
					.findViewById(R.id.pushmsg_name);
			holder.pushMsgDateView = (TextView) convertView
					.findViewById(R.id.pushmsg_date);
			holder.pushMsgContent = (TextView) convertView
					.findViewById(R.id.pushmsg_content);
			holder.pushMsgCount = (TextView) convertView.findViewById(R.id.pushmsg_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(pm.pushMsg.pushMsgType==0){
		    holder.pushMsgNameView.setText(pm.pushMsg.sendName);
		}else{
		    holder.pushMsgNameView.setText(pm.pushMsg.pushMsgTypeName);
		}
		String dateStr = sfd.format(new Date(pm.pushMsg.dtime.getTime()));
		holder.pushMsgDateView.setText(dateStr);
		holder.pushMsgContent.setText(pm.pushMsg.content);
		holder.pushMsgCount.setText(String.valueOf(pm.pushMsgRecordList.size()));
		return convertView;
	}

}