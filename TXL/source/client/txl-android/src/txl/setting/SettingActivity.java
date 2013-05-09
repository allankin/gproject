package txl.setting;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.json.JSONObject;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.TxlAlertDialog;
import txl.common.TxlToast;
import txl.common.WebLoadingTipDialog;
import txl.common.login.LoginDialog;
import txl.common.login.ModifyPasswordDialog;
import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.util.HttpClientUtil;
import txl.util.Tool;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
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
        Drawable drawable = res.getDrawable(R.color.window_bgcolor);
        this.getWindow().setBackgroundDrawable(drawable);
		
		setContentView(R.layout.tab_setting);
		TextView header = (TextView)findViewById(R.id.header);
		header.setText("软件设置");
		Setting setting = Account.getSingle().setting;
		log.info("setting    dialMode : "+setting.dialMode);
		
		TableRow statusTr = (TableRow)findViewById(R.id.setting_status);
		
		/*设置退出监听*/
		statusTr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView loginStatusView = (TextView)findViewById(R.id.setting_login_label);
				if(loginStatusView.getText().equals("登陆")){
					LoginDialog.getInstance().show(me);
				}else if(loginStatusView.getText().equals("退出")){
					TxlAlertDialog.show(me, "确定退出吗？", "确定,取消", new TxlAlertDialog.DialogInvoker(){
						
						@Override
						public void doInvoke(DialogInterface dialog, int btndex) {
							if(btndex == TxlAlertDialog.FIRST_BTN_INDEX){
								WebLoadingTipDialog.getInstance(me).show("正在退出..."); 
								new Thread(new Runnable() {
									public void run() {
										try {
											String body = HttpClientUtil.httpPostUTF8(TxlConstants.LOGOUT_URL, null);
											JSONObject jobj = new JSONObject(body);
											final int status = jobj.optInt("status");
											me.runOnUiThread(new Runnable() {
												public void run() {
													if(status==-2){
														TxlToast.showShort(me, "您已经退出！");
													}else if(status==-1){
														TxlToast.showShort(me, "会话过期，您已经退出！");
													}
												}
											});
											me.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_LOGIN_OFFLINE_STATUS));
										} catch (HttpHostConnectException e) {
											me.runOnUiThread(new Runnable() {
												public void run() {
													TxlToast.showShort(me, "网络未连接！");
												}
											});
											e.printStackTrace();
										} catch (ConnectTimeoutException e) {
											me.runOnUiThread(new Runnable() {
												public void run() {
													WebLoadingTipDialog.getInstance(me).overLoadingDismiss();
												}
											});
											e.printStackTrace();
										} catch (JSONException e) {
											e.printStackTrace();
										}
										me.runOnUiThread(new Runnable() {
											public void run() {
												WebLoadingTipDialog.getInstance(me).dismiss();
											}
										});
									}
								}).start();
							}else if(btndex == TxlAlertDialog.SENCOND_BTN_INDEX){
								
							}
						}
						
					});
				}
			}
		});
		
		
		TableRow mptr = (TableRow)findViewById(R.id.setting_modify_password);
		mptr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ModifyPasswordDialog.getInstance().show(me);
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
		
		
		/*Spinner dialModeSpinner = (Spinner)findViewById(R.id.setting_dial_mode_btn);
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
            
        });*/
        
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
            	ImageView stateImage = (ImageView)findViewById(R.id.setting_state_image);
            	stateImage.setImageResource(R.drawable.state_online);
            	loginStatusView.setText("退出");
            }else if(msg.what == TxlConstants.MSG_LOGIN_OFFLINE_STATUS){
            	TextView loginStatusView = (TextView)findViewById(R.id.setting_login_label);
            	ImageView stateImage = (ImageView)findViewById(R.id.setting_state_image);
            	stateImage.setImageResource(R.drawable.state_offline);
            	loginStatusView.setText("登陆");
            }
            
        }
	};
	
	
	@Override
    protected void onNewIntent (Intent intent){
       log.info("onNewIntent");
       
    } 
    
    @Override
    protected void onPause()
    {
        super.onPause();
        log.info("onPause");
        
    }

    @Override
    protected void onStart(){
        super.onStart();
        log.info("onStart");
        
    }
    
    @Override
    protected void onRestart(){
        super.onRestart();
        log.info("onRestart");
        
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        log.info("onResume");
        
    }
    @Override
    protected void onStop(){
        super.onStop();
        log.info("onStop");
        
    }
    
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
        log.info("onDestroy");
         
        LoginDialog.getInstance().dismissAllAlerts();
    }
}
