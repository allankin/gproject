package txl.message.pushmessage;

import java.sql.Timestamp;
import java.util.List;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.po.Account;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.adapter.PushMsgDetailListAdapter;
import txl.message.pushmessage.biz.DataRunnable;
import txl.message.pushmessage.biz.RunnableManager;
import txl.message.pushmessage.dao.PushMsgDao;
import txl.message.pushmessage.po.PushMsg;
import txl.util.Tool;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author jinchao
 * 
 */
public class PushMessageActivity extends TxlActivity {

	private final TxLogger log = new TxLogger(PushMessageActivity.class,
			TxlConstants.MODULE_ID_CONTACT);

	private PushMessageActivity me = this;
	private List<PushMsg> pushMsgList;
	private PushMsgDetailListAdapter detailListAdapter;
	private Button pushMsgSendBtn;
	private int contactId;
	private MessageDetailReceiver mr;
	private ListView pushMsgDetailListView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pushmsg_detail_list);
		pushMsgDetailListView = (ListView) findViewById(R.id.pushmsg_detail_list);
		ajustListView(pushMsgDetailListView);
		TextView header = (TextView) findViewById(R.id.header);
		header.setText("");
		Intent intent = getIntent();
		contactId = 0;
		int messageCount = 0;
		String contactName = "";
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				contactId = bundle
						.getInt(TxlConstants.INTENT_BUNDLE_CONTACT_ID);
				// messageCount =
				// bundle.getInt(TxlConstants.INTENT_BUNDLE_COUNT);
				contactName = bundle
						.getString(TxlConstants.INTENT_BUNDLE_CONTACT_NAME);
				log.info("contactId:" + contactId + ",contactName:"
						+ contactName);

				pushMsgList = PushMsgDao.getSingle(me).getPushMsg(contactId);
				messageCount = pushMsgList.size();
				header.setText(contactName + "(" + messageCount + ")");

				detailListAdapter = new PushMsgDetailListAdapter(me,
						pushMsgList);

				pushMsgDetailListView.setAdapter(detailListAdapter);

				pushMsgDetailListView
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {

								return false;
							}
						});
				boolean flag = PushMsgDao.getSingle(me)
						.updatePushMsgReadStatusByUserId(contactId, 1);
				if (flag) {
					Config.mainContext
							.getHandler()
							.sendMessage(
									Tool.genMessage(TxlConstants.MSG_RECEIVE_PUSHMESSAGE));
				}

				pushMsgDetailListView
						.setSelection(detailListAdapter.getCount() - 1);
			}
		}

		final int contactIdFinal = contactId;
		final String contactNameFinal = contactName;
		final EditText pushMsgInput = (EditText) findViewById(R.id.pushmsg_input);
		// HS_TODO:暂屏蔽 键盘的回车键
		pushMsgInput.setOnEditorActionListener(editListener);
		pushMsgSendBtn = (Button) findViewById(R.id.pushmsg_send_btn);
		pushMsgSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = pushMsgInput.getText().toString().trim();
				if (content.length() > 0) {
					DataRunnable dr = (DataRunnable) RunnableManager
							.getRunnable(TxlConstants.BIZID_REQUEST_DATA);
					PushMsg pushMsg = new PushMsg();

					pushMsg.content = Tool.string2Json(content);
					pushMsg.recUserId = contactIdFinal;
					pushMsg.sendUserId = Account.getSingle().userId;
					pushMsg.msgId = Tool.genUUID();
					pushMsg.type = TxlConstants.PUSH_MESSAGE_TYPE_SEND;
					dr.send(pushMsg);
					pushMsg.recName = contactNameFinal;
					pushMsg.dtime = new Timestamp(System.currentTimeMillis());
					PushMsgDao.getSingle(me).savePushMsg(pushMsg);
					pushMsgList.add(pushMsg);
					detailListAdapter.notifyDataSetChanged();
					pushMsgDetailListView.setSelection(detailListAdapter
							.getCount() - 1);
					pushMsgInput.setText("");
					// InputMethodManager imm
					// =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					// imm.hideSoftInputFromWindow(pushMsgInput.getWindowToken(),
					// 0);

				}
			}
		});

		pushMsgInput.setFocusable(true);
		pushMsgInput.requestFocus();
		onFocusChange(pushMsgInput,true);
		mr = new MessageDetailReceiver();
		IntentFilter filter = new IntentFilter(
				TxlConstants.ACTION_MESSAGE_RECEIVED);
		me.registerReceiver(mr, filter);
	}

	private void onFocusChange(final EditText pushMsgInput, boolean hasFocus) {
		final boolean isFocus = hasFocus;
		(new Handler()).postDelayed(new Runnable() {
			public void run() {
				InputMethodManager imm = (InputMethodManager) pushMsgInput
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				if (isFocus) {
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				} else {
					imm.hideSoftInputFromWindow(pushMsgInput.getWindowToken(),
							0);
				}
			}
		}, 100);
	}

	private void ajustListView(final ListView pushMsgDetailListView) {
		pushMsgDetailListView.postDelayed(new Runnable() {

			@Override
			public void run() {
				View header = findViewById(R.id.header_layout);
				int height = TxlConstants.tabContentHeight - header.getHeight();
				pushMsgDetailListView
						.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.FILL_PARENT, height));
				//pushMsgDetailListView.scrollBy(0, 150);
			}
		}, 500);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == TxlConstants.MSG_LOAD_SHARE_COMMDIR_USER) {

			}
		}

	};

	@Override
	public Handler getHandler() {
		return handler;
	}

	private TextView.OnEditorActionListener editListener = new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				pushMsgSendBtn.performClick();
			}
			return true;
		}
	};

	private void refreshMessage() {

	}

	/**
	 * 刷新刚接收的信息
	 */
	private void refreshIncomingMessage() {

	}

	private class MessageDetailReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (TxlConstants.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				String msgId = intent
						.getStringExtra(TxlConstants.INTENT_BUNDLE_MSG_ID);
				int cId = intent.getIntExtra(
						TxlConstants.INTENT_BUNDLE_CONTACT_ID, -1);
				log.info("MessageDetailReceiver rece_contactId:" + cId
						+ ", contactId:" + contactId + ",msgId:" + msgId);
				if (contactId == cId) {
					if (msgId != null) {
						PushMsg pm = PushMsgDao.getSingle(me)
								.getPushMsgByMsgId(msgId);
						if (pm != null) {
							me.pushMsgList.add(pm);
							me.detailListAdapter.notifyDataSetChanged();
							me.pushMsgDetailListView
									.setSelection(me.detailListAdapter
											.getCount() - 1);
						}
					}
				}

			}
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		log.info("onNewIntent");

	}

	@Override
	protected void onPause() {
		super.onPause();
		log.info("onPause");

	}

	@Override
	protected void onStart() {
		super.onStart();
		log.info("onStart");

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		log.info("onRestart");
		me.unregisterReceiver(mr);
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.info("onResume");

	}

	@Override
	protected void onStop() {
		super.onStop();
		log.info("onStop");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mr != null) {
			me.unregisterReceiver(mr);
		}
		log.info("onDestroy");
	}

}
