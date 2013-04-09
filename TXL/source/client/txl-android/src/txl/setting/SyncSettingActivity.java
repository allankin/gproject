package txl.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import txl.TxlActivity;
import txl.activity.R;
import txl.config.TxlConstants;
import txl.log.TxLogger;

public class SyncSettingActivity extends TxlActivity {
	
private final TxLogger  log = new TxLogger(SyncSettingActivity.class, TxlConstants.MODULE_ID_SETTING);
	
	private SyncSettingActivity me = this;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_sync);
		TextView header = (TextView)findViewById(R.id.header);
		header.setText("同步号码");
		
		
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
