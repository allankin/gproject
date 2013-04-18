package txl.message.pushmessage;

import java.util.ArrayList;
import java.util.List;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.TxlToast;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.contact.adapter.ContactShareUserListAdapter;
import txl.contact.po.ShareUser;
import txl.log.TxLogger;
import txl.message.pushmessage.adapter.PushMsgDetailListAdapter;
import txl.message.pushmessage.dao.PushMsgDao;
import txl.message.pushmessage.po.PushMsg;
import txl.util.Tool;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
/**
 * @author jinchao
 *
 */
public class PushMessageActivity extends TxlActivity {
	
private final TxLogger  log = new TxLogger(PushMessageActivity.class, TxlConstants.MODULE_ID_CONTACT);
	
	private PushMessageActivity me = this;
	private List<PushMsg> pushMsgList;
	private PushMsgDetailListAdapter detailListAdapter;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pushmsg_detail_list);
		TextView header = (TextView)findViewById(R.id.header);
		header.setText("");
		Intent intent = getIntent();
		int contactId = 0;
		int messageCount = 0 ;
		if(intent!=null){
			Bundle bundle = intent.getExtras();
			if(bundle!=null){
				contactId = bundle.getInt(TxlConstants.INTENT_BUNDLE_CONTACT_ID);
				messageCount = bundle.getInt(TxlConstants.INTENT_BUNDLE_COUNT);
				header.setText(contactId+"("+messageCount+")");
				
				pushMsgList = PushMsgDao.getSingle(me).getPushMsg(contactId);
				detailListAdapter = new PushMsgDetailListAdapter(me,pushMsgList);
				ListView pushMsgDetailListView = (ListView)findViewById(R.id.pushmsg_detail_list);
				pushMsgDetailListView.setAdapter(detailListAdapter);
				
				pushMsgDetailListView.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						 
						return false;
					}
				});
				boolean flag = PushMsgDao.getSingle(me).updatePushMsgReadStatusByUserId(contactId,1);
				if(flag){
					Config.mainContext.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_RECEIVE_PUSHMESSAGE));
				}
			}
		}
		
	}
 
	 
	private Handler handler = new Handler(){
		public void handleMessage(Message msg)
        {
			if(msg.what == TxlConstants.MSG_LOAD_SHARE_COMMDIR_USER){
			  
            }
        }
		
	};
	@Override
	public Handler getHandler() {
		return handler;
	}

}
