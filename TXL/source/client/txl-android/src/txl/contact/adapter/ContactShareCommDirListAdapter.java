package txl.contact.adapter;

import java.util.ArrayList;
import java.util.List;

import txl.TxlActivity;
import txl.activity.R;
import txl.config.TxlConstants;
import txl.contact.dao.CommDirDao;
import txl.contact.po.CommDir;
import txl.contact.po.CommDirUserQuery;
import txl.contact.task.ShareCommDirUserQueryTask;
import txl.log.TxLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ContactShareCommDirListAdapter extends BaseAdapter 
{
	
	private final static TxLogger log = new TxLogger(ContactShareCommDirListAdapter.class, TxlConstants.MODULE_ID_CONTACT);
    	private TxlActivity ctx;
    	private List<CommDir> commDirList;
        public ContactShareCommDirListAdapter(TxlActivity context,List<CommDir> commDirList)
        {
        	ctx = context;
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
        	final CommDir cv = commDirList.get(position);
            if (convertView == null)
            {
            	holder = new ViewHolder();
                convertView = LayoutInflater.from(ctx).inflate(R.layout.contact_share_commdir_list_item, null);
                holder.nameView = (TextView) convertView.findViewById(R.id.contact_share_commdir_name);
                holder.countView = (TextView) convertView.findViewById(R.id.contact_share_commdir_count);
                holder.shareCommDirSyncBtn = (Button) convertView.findViewById(R.id.share_commdir_sync_btn);
                holder.shareCommDirSyncBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CommDirUserQuery cdq = new CommDirUserQuery();
						cdq.dirId = cv.dirId;
						CommDirDao.getSingle(ctx).deleteCommDir(cv.dirId);
						saveShareCommDir(cv);
						new ShareCommDirUserQueryTask(ctx,TxlConstants.ACTION_SYNC_CODE).execute(cdq);
					}
				});
                convertView.setTag(holder);
            }else{
            	holder = (ViewHolder)convertView.getTag();
            }
            holder.nameView.setText(cv.name);
            holder.countView.setText("("+cv.userCount+")");
            return convertView;
        }
        
        
        private void saveShareCommDir(CommDir commDir){
        	List<CommDir> commDirList = new ArrayList<CommDir>();
        	commDirList.add(commDir);
        	CommDirDao.getSingle(ctx).saveCommDir(commDirList);
        }
        class ViewHolder{
        	public TextView nameView;
        	public TextView countView;
        	public Button shareCommDirSyncBtn;
        	
        }
        
        
    }