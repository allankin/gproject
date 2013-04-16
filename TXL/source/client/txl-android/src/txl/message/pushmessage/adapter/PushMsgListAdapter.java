package txl.message.pushmessage.adapter;

import java.util.Map;

import txl.activity.R;
import txl.config.TxlConstants;
import txl.contact.dao.ContactDao;
import txl.log.TxLogger;
import txl.message.pushmessage.po.PushMsgRecord;
import txl.message.sms.po.SmsRecord;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PushMsgListAdapter extends BaseAdapter {
	private final static TxLogger log = new TxLogger(PushMsgListAdapter.class,
			TxlConstants.MODULE_ID_MESSAGE);
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

	class ViewHolder {
		TextView pushMsgNameView;
		TextView pushMsgDateView;
		TextView pushMsgContent;
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
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
 
		holder.pushMsgNameView.setText(pm.pushMsg.name);
		holder.pushMsgDateView.setText(pm.pushMsg.dateStr);
		holder.pushMsgContent.setText(pm.pushMsg.content);

		return convertView;
	}

}