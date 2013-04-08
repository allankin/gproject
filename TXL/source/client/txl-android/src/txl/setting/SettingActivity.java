package txl.setting;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.login.LoginDialog;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

public class SettingActivity extends TxlActivity {
	private final TxLogger  log = new TxLogger(SettingActivity.class, TxlConstants.MODULE_ID_SETTING);
	
	
	private SettingActivity me = this;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_setting);
		
		TableRow statusTr = (TableRow)findViewById(R.id.setting_status);
		statusTr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginDialog.getInstance().show(me);
			}
		});
		
		
		
		
	}
	@Override
	public Handler getHandler() {
		return this.handler;
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg)
        {
            if(msg.what == TxlConstants.SETTING_HANDLER_ONLINE_STATUS){
            	TextView loginStatusView = (TextView)findViewById(R.id.setting_login_label);
            	loginStatusView.setText("已在线");
            } 
            
        }
	};
}
