package txl.contact;

import java.util.ArrayList;
import java.util.List;

import txl.TxlActivity;
import txl.activity.R;
import txl.call.po.CallRecord;
import txl.common.ActionBoard;
import txl.common.TxlToast;
import txl.common.ActionBoard.ActionContact;
import txl.config.TxlConstants;
import txl.contact.adapter.ContactShareUserListAdapter;
import txl.contact.po.CommDirUserQuery;
import txl.contact.po.ShareUser;
import txl.contact.task.ShareCommDirUserQueryTask;
import txl.log.TxLogger;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 显示某个共享通讯录的用户列表
 * @author jinchao
 *
 */
public class ShareUserActivity extends TxlActivity {
	
private final TxLogger  log = new TxLogger(ShareUserActivity.class, TxlConstants.MODULE_ID_CONTACT);
	
	private ShareUserActivity me = this;
	private List<ShareUser> shareUserList ;
	private ContactShareUserListAdapter csla;
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
		final int dirId = commDirId;
		me.query(dirId);
		
		shareUserList = new ArrayList<ShareUser>();
		csla = new ContactShareUserListAdapter(me,shareUserList);
		Button shareCommDirUserSearchBtn = (Button)findViewById(R.id.share_commdir_user_search_btn);
		shareCommDirUserSearchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				me.query(dirId);
			}
		});
		ListView contactListView = (ListView)findViewById(R.id.contact_list);
		contactListView.setAdapter(csla);
		
		contactListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				log.info("position:"+position+" id: "+id);
				final ShareUser user = shareUserList.get(position);
				ActionContact ac = new ActionBoard.ActionContact();
				ac.phone = user.userPhone;
				ActionBoard.show(me, ac);
			}
        	
		});
		super.renderHeader();
		 
	}
	private void query(final int dirId){
		EditText searchTextView = (EditText)findViewById(R.id.share_commdir_user_search);
		CommDirUserQuery query = new CommDirUserQuery();
		query.dirId = dirId;
		query.name = searchTextView.getText().toString().trim();
		new ShareCommDirUserQueryTask(me, TxlConstants.ACTION_QUERY_CODE).execute(query);
	}
	 
	private Handler handler = new Handler(){
		public void handleMessage(Message msg)
        {
			if(msg.what == TxlConstants.MSG_LOAD_SHARE_COMMDIR_USER){
				shareUserList.clear();
            	List<ShareUser> users = (List<ShareUser>)msg.obj;
            	for(ShareUser user: users){
            		shareUserList.add(user); 
            	}
            	csla.notifyDataSetChanged();
            }
        }
		
	};
	@Override
	public Handler getHandler() {
		return handler;
	}

}
