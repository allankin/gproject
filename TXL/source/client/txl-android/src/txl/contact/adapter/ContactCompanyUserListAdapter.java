package txl.contact.adapter;

import java.util.List;

import txl.activity.R;
import txl.config.TxlConstants;
import txl.contact.po.CompanyUser;
import txl.log.TxLogger;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactCompanyUserListAdapter extends BaseAdapter 
{
	
	private final static TxLogger log = new TxLogger(ContactCompanyUserListAdapter.class, TxlConstants.MODULE_ID_CONTACT);
    	private Context mContext;
    	private List<CompanyUser> contactList;
        public ContactCompanyUserListAdapter(Context context,List<CompanyUser> contactList)
        {
            mContext = context;
            this.contactList = contactList;
        }

        public int getCount()
        {
            return contactList.size();
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
        	CompanyUser cv = contactList.get(position);
            if (convertView == null)
            {
            	holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_company_user_item, null);
                holder.nameView = (TextView) convertView.findViewById(R.id.contact_name);
                holder.phoneView = (TextView) convertView.findViewById(R.id.contact_phone);
                convertView.setTag(holder);
            }else{
            	holder = (ViewHolder)convertView.getTag();
            }
            holder.nameView.setText(cv.name);
            holder.phoneView.setText(cv.userPhone);
            return convertView;
        }
        
        class ViewHolder{
        	TextView nameView;
            TextView phoneView;
        }
    }