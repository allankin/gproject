package txl.contact.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import txl.activity.R;
import txl.config.TxlConstants;
import txl.contact.po.ContactVo;
import txl.log.TxLogger;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContactListAdapter extends BaseAdapter implements SectionIndexer
    {
	
	private final static TxLogger log = new TxLogger(ContactListAdapter.class, TxlConstants.MODULE_ID_BASE);
    	private Context mContext;
    	private List<ContactVo> contactList;
    	private Map<Character,Integer> firstLetterMap = new HashMap<Character,Integer>();
        public ContactListAdapter(Context context,List<ContactVo> contactList)
        {
            mContext = context;
            this.contactList = contactList;
             
            for(int i=0,len=contactList.size();i<len;i++){
            	ContactVo cv = contactList.get(i);
            	if(!firstLetterMap.containsKey(cv.firstLetter)){
            		firstLetterMap.put(cv.firstLetter, i);
            		log.info("firstLetter: "+cv.firstLetter+",index: "+i);
            	}
            }
            
            
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
            ContactVo cv = contactList.get(position);
            if (convertView == null)
            {
            	holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, null);
                holder.nameView = (TextView) convertView.findViewById(R.id.contact_name);
                holder.phoneView = (TextView) convertView.findViewById(R.id.contact_phone);
                convertView.setTag(holder);
            }else{
            	holder = (ViewHolder)convertView.getTag();
            }
            holder.nameView.setText(cv.displayName);
            holder.phoneView.setText(cv.phone);
            return convertView;
        }
        
        class ViewHolder{
        	TextView nameView;
            TextView phoneView;
        }

		@Override
		public Object[] getSections() {
			return null;
		}

		@Override
		public int getPositionForSection(int section) {
			Integer idx = firstLetterMap.get((char)section);
			if(idx==null){
				idx = -1;
			}
			return idx;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

    }