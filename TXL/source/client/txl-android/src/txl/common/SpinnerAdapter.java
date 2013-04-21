package txl.common;

import txl.activity.R;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerAdapter<String> extends ArrayAdapter<String>{
	private TxLogger log = new TxLogger(SpinnerAdapter.class, TxlConstants.MODULE_ID_BASE);
	
	private Context context;
	public SpinnerAdapter(Context context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		((TextView) v).setTextSize(23);
		int color = context.getResources().getColor(R.color.white);
		log.info("color: "+color);
		((TextView) v).setTextColor(context.getResources().getColor(R.color.white));
		return v;
	}

	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {
		View v = super.getDropDownView(position, convertView, parent);
		v.setBackgroundResource(R.color.white);
		//((TextView) v).setTextColor(context.getResources().getColor(R.color.white));
		//((TextView) v).setGravity(Gravity.CENTER);
		return v;
	}
	
	 
}
