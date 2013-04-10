package txl.contact;

import java.util.List;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.TxlToast;
import txl.config.TxlConstants;
import txl.contact.adapter.ContactShareUserListAdapter;
import txl.contact.dao.CommDirDao;
import txl.contact.po.ShareUser;
import txl.log.TxLogger;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 显示某个共享通讯录的用户列表
 * @author jinchao
 *
 */
public class ShareUserActivity extends TxlActivity {
	
private final TxLogger  log = new TxLogger(ShareUserActivity.class, TxlConstants.MODULE_ID_CONTACT);
	
	private ShareUserActivity me = this;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_share_commdir_user);
		
		Intent intent = getIntent();
		String headerTitle = "";
		int commDirId =0;
		int contactCount = 0;
		if(intent!=null){
			Bundle bundle = intent.getExtras();
			if(bundle!=null){
				headerTitle = bundle.getString(TxlConstants.INTENT_BUNDLE_HEADER_TITLE);
				TextView header = (TextView)findViewById(R.id.header);
				commDirId= bundle.getInt(TxlConstants.INTENT_BUNDLE_COMMDIR_ID);
				contactCount = bundle.getInt(TxlConstants.INTENT_BUNDLE_COUNT);
				header.setText(headerTitle+"("+contactCount+")");
			}
		}
		
		List<ShareUser> shareUserList = CommDirDao.getSingle(me).getShareUserListByCommDirId(commDirId);
		ContactShareUserListAdapter ccul = new ContactShareUserListAdapter(me,shareUserList);
		ListView contactListView = (ListView)findViewById(R.id.contact_list);
		contactListView.setAdapter(ccul);
		
		contactListView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				//ContactShareUserListAdapter.ViewHolder viewHolder = (ContactShareUserListAdapter.ViewHolder)v.getTag();
				TextView phoneView = (TextView)v.findViewById(R.id.contact_phone);
				String phone = phoneView.getText().toString();
				TxlToast.showShort(me,phone);
				return false;
			}
		});
	}
	
	 
	private Handler handler = new Handler(){
		public void handleMessage(Message msg)
        {
            
        }
		
	};
	@Override
	public Handler getHandler() {
		return handler;
	}

}
