package txl.contact.adapter;

import java.util.List;

import txl.activity.R;
import txl.config.TxlConstants;
import txl.contact.po.CommDir;
import txl.contact.po.ShareUser;
import txl.log.TxLogger;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactShareCommDirListAdapter extends BaseAdapter 
{
	
	private final static TxLogger log = new TxLogger(ContactShareCommDirListAdapter.class, TxlConstants.MODULE_ID_CONTACT);
    	private Context mContext;
    	private List<CommDir> commDirList;
        public ContactShareCommDirListAdapter(Context context,List<CommDir> commDirList)
        {
            mContext = context;
            this.commDirList = commDirList;
        }

        public int getCount()
        {
            return commDirList.size();
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
        	CommDir cv = commDirList.get(position);
            if (convertView == null)
            {
            	holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_share_commdir_list_item, null);
                holder.nameView = (TextView) convertView.findViewById(R.id.contact_share_commdir_name);
                holder.countView = (TextView) convertView.findViewById(R.id.contact_share_commdir_count);
                convertView.setTag(holder);
            }else{
            	holder = (ViewHolder)convertView.getTag();
            }
            holder.nameView.setText(cv.name);
            holder.countView.setText(String.valueOf(cv.contactCount));
            return convertView;
        }
        
        class ViewHolder{
        	public TextView nameView;
        	public TextView countView;
        	
        }
    }