package txl.message.pushmessage;

import txl.TxlActivity;
import txl.activity.R;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.web.TxlWebUtil;
import txl.web.TxlWebViewClient;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * @author jinchao
 * 
 */
public class ClassfiedPushMsgDetailActivity extends TxlActivity {

	private final TxLogger log = new TxLogger(ClassfiedPushMsgDetailActivity.class,
			TxlConstants.MODULE_ID_MESSAGE);

	private ClassfiedPushMsgDetailActivity me = this;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pushmsg_classfied_detail);
		TextView header = (TextView) findViewById(R.id.header);
		header.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String title = bundle.getString(TxlConstants.INTENT_BUNDLE_PUSHMSG_TITLE);
				header.setText(title);
				String url = bundle.getString(TxlConstants.INTENT_BUNDLE_PUSHMSG_URL);
				WebView wv = (WebView)findViewById(R.id.wv);
				TxlWebUtil.settingWebView(wv,me);
				wv.setWebViewClient(new TxlWebViewClient(me));
				wv.loadUrl(url);
			}
		}
		 
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
		log.info("onDestroy");
	}

}
