package txl.message.sms.adapter;

import java.util.Map;

import txl.activity.R;
import txl.config.TxlConstants;
import txl.contact.dao.ContactDao;
import txl.log.TxLogger;
import txl.message.sms.dao.SmsDao;
import txl.message.sms.po.SmsRecord;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SmsListAdapter extends BaseAdapter {
	private final static TxLogger log = new TxLogger(SmsListAdapter.class,
			TxlConstants.MODULE_ID_MESSAGE);
	private Context mContext;
	public Map<Integer, SmsRecord> smsRecordMap;

	public SmsListAdapter(Context context, Map<Integer, SmsRecord> smsRecordMap) {
		this.mContext = context;
		this.smsRecordMap = smsRecordMap;
	}

	public int getCount() {
		return smsRecordMap.size();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	public Object getItem(int position) {
		return smsRecordMap.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView smsNameView;
		TextView smsPhoneView;
		TextView smsCountView;
		TextView smsBodyView;
		TextView smsDateView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		SmsRecord sr = this.smsRecordMap.get(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.sms_list_item, null);
			holder = new ViewHolder();
			holder.smsNameView = (TextView) convertView
					.findViewById(R.id.sms_name);
			// smsPhoneView = (TextView)
			// convertView.findViewById(R.id.sms_phone);
			holder.smsCountView = (TextView) convertView
					.findViewById(R.id.sms_count);
			holder.smsDateView = (TextView) convertView
					.findViewById(R.id.sms_date);
			holder.smsBodyView = (TextView) convertView
					.findViewById(R.id.sms_body);
			log.info("position: " + position + ",address:" + sr.address
					+ ",smsRecordCount: " + this.smsRecordMap.size());

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/* 为了避免反复查询，对displayAddress缓存。 */
		if (sr.displayAddress == null) {
			String name = ContactDao.getContactNameByPhoneNumber(mContext,
					sr.address);
			if (name != null) {
				sr.displayAddress = name;
			} else {
				sr.displayAddress = sr.address;
			}
		}

		/*
		 * if(sr._id!=0){ //contactVo = ContactDao.getContactById(this, sr._id);
		 * if(contactVo!=null){ sr.address = contactVo.displayName; } }
		 */

		holder.smsNameView.setText(sr.displayAddress);
		// smsPhoneView.setText(sr.address);
		holder.smsCountView.setText("(" + sr.historyList.size() + ")");
		holder.smsDateView.setText(sr.dateStr);
		holder.smsBodyView.setText(sr.body);

		return convertView;
	}

}