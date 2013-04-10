package txl.setting;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.login.LoginDialog;
import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingActivity extends TxlActivity {
	private final TxLogger  log = new TxLogger(SettingActivity.class, TxlConstants.MODULE_ID_SETTING);
	
	
	private SettingActivity me = this;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.window_bgcolor);
        this.getWindow().setBackgroundDrawable(drawable);
		
		setContentView(R.layout.tab_setting);
		TextView header = (TextView)findViewById(R.id.header);
		header.setText("软件设置");
		Setting setting = Account.getSingle().setting;
		log.info("setting    dialMode : "+setting.dialMode);
		
		TableRow statusTr = (TableRow)findViewById(R.id.setting_status);
		statusTr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginDialog.getInstance().show(me);
			}
		});
		
		/*wifi提示设置开关*/
		ToggleButton wifiTipBtn = (ToggleButton)findViewById(R.id.setting_wifi_tip_btn);
		wifiTipBtn.setChecked(setting.wifiTip==1?true:false);
		wifiTipBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SettingDao settingDao = SettingDao.getSingle(me);
				settingDao.updateWifiTip(isChecked?"1":"0");
			}
		});
		
		ToggleButton adReceiveBtn = (ToggleButton)findViewById(R.id.setting_ad_receive_btn);
		adReceiveBtn.setChecked(setting.adReceive==1?true:false);
		adReceiveBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SettingDao settingDao = SettingDao.getSingle(me);
				settingDao.updateAdReceive(isChecked?"1":"0");
			}
		});
		
		ToggleButton onlinePushBtn = (ToggleButton)findViewById(R.id.setting_online_push_btn);
		onlinePushBtn.setChecked(setting.pushMessage==1?true:false);
		onlinePushBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SettingDao settingDao = SettingDao.getSingle(me);
				settingDao.updatePushMessage(isChecked?"1":"0");
			}
		});
		
		View syncCommDirRow = (View)findViewById(R.id.setting_sync_commdir);
		syncCommDirRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(me,SyncSettingActivity.class);
				startActivity(intent);
			}
		});
		
		
		Spinner dialModeSpinner = (Spinner)findViewById(R.id.setting_dial_mode_btn);
		ArrayAdapter<String> dialModeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,me.getResources().getStringArray(R.array.setting_dial_mode_array));
		dialModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dialModeSpinner.setAdapter(dialModeAdapter);
		
		dialModeSpinner.setSelection(setting.dialMode,false);
		dialModeSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position==0){
                	SettingDao.getSingle(me).updateDialMode("0");
                }else{
                	SettingDao.getSingle(me).updateDialMode("1");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                
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
