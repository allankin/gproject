package txl.setting;

import java.util.List;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.TxlAlertDialog;
import txl.common.TxlToast;
import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.contact.dao.CommDirDao;
import txl.contact.po.CommDir;
import txl.contact.po.CommDirUserQuery;
import txl.contact.po.UserQuery;
import txl.contact.task.CampanyUserQueryTask;
import txl.contact.task.DepartmentQueryTask;
import txl.contact.task.PersonalCommDirSyncBackupTask;
import txl.contact.task.PersonalCommDirSyncRestoreTask;
import txl.contact.task.ShareCommDirUserQueryTask;
import txl.log.TxLogger;
import txl.util.Tool;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SyncSettingActivity extends TxlActivity {
	
private final TxLogger  log = new TxLogger(SyncSettingActivity.class, TxlConstants.MODULE_ID_SETTING);
	
	private SyncSettingActivity me = this;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_sync);
		Setting setting = Account.getSingle().setting;
		
		TextView header = (TextView)findViewById(R.id.header);
		header.setText("同步号码");
		final TableLayout syncTable = (TableLayout)findViewById(R.id.setting_sync_share_item_table);
		
		
		LinearLayout syncPersonalBackup = (LinearLayout)findViewById(R.id.setting_sync_personal_backup);
		syncPersonalBackup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new PersonalCommDirSyncBackupTask(me).execute();
			}
		});
		
		LinearLayout syncPersonalRestore = (LinearLayout)findViewById(R.id.setting_sync_personal_restore);
		syncPersonalRestore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new PersonalCommDirSyncRestoreTask(me).execute();
			}
		});
		
		
		final ToggleButton syncCompanyToggle = (ToggleButton)findViewById(R.id.setting_sync_company_comdir_toggle);
		syncCompanyToggle.setChecked(setting.syncCompany==1?true:false);
		syncCompanyToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SettingDao settingDao = SettingDao.getSingle(me);
				settingDao.updateSyncCompany(isChecked?"1":"0");
				if(isChecked){
					/*是否有wifi提醒设置*/
					if(Account.getSingle().setting.wifiTip==1){
						if(!Tool.isWifi(me)){
							TxlAlertDialog.show(me, "未连接至wifi，确定同步吗？", "确定,取消", new TxlAlertDialog.DialogInvoker(){
								@Override
								public void doInvoke(DialogInterface dialog,
										int btndex) {
									if(btndex == TxlAlertDialog.FIRST_BTN_INDEX){
										/* 先 同步部门 */
										new DepartmentQueryTask(me,TxlConstants.ACTION_SYNC_CODE).execute();
									} 
								}
								
							});
						}else{
							/* 先 同步部门 */
							new DepartmentQueryTask(me,TxlConstants.ACTION_SYNC_CODE).execute();
						}
					}else{
						/* 先 同步部门 */
						new DepartmentQueryTask(me,TxlConstants.ACTION_SYNC_CODE).execute();
					}
					
					
				}
			}
		});
		LinearLayout syncCompanyLayout = (LinearLayout)findViewById(R.id.setting_sync_company);
		syncCompanyLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				syncCompanyToggle.setChecked(syncCompanyToggle.isChecked()?false:true);
			}
		});
		
		final ToggleButton syncShareToggle = (ToggleButton)findViewById(R.id.setting_sync_share_commdir_toggle);
		syncShareToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SettingDao settingDao = SettingDao.getSingle(me);
				settingDao.updateSyncShare(isChecked?"1":"0");
				syncTable.removeAllViews();
				if(isChecked){
	            	List<CommDir> commDirList = CommDirDao.getSingle(me).getShareCommDirList(null);
	            	if(commDirList.isEmpty()){
	            		TxlToast.showLong(me, "您还未添加共享通讯录，请到联系人中添加");
	            	}else{
	            		for(int i=0,len=commDirList.size();i<len;i++){
	            			CommDir commDir = commDirList.get(i);
	            			showShareCommDirItem(syncTable,commDir,i+1,len);
	            		}
	            	}
	            	
				}
			}
		});
		
		final LinearLayout syncShareLayout = (LinearLayout)findViewById(R.id.setting_sync_share);
		syncShareLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				syncShareToggle.setChecked(syncShareToggle.isChecked()?false:true);
				if(syncShareToggle.isChecked()){
					syncShareLayout.setBackgroundResource(R.drawable.bg_base_list_top);
				}else{
					syncShareLayout.setBackgroundResource(R.drawable.bg_base_list_top_bot);
				}
					
			}
		});
		syncShareToggle.setChecked(setting.syncShare==1?true:false);
		if(syncShareToggle.isChecked()){
			syncShareLayout.setBackgroundResource(R.drawable.bg_base_list_top);
		}
	
	}
	
	
	private void showShareCommDirItem(TableLayout syncTable,final CommDir commDir,int cur,int count){
		View row = createTableRow();
		if(cur==count){
			row.setBackgroundResource(R.drawable.base_list_bot);
		}else{
			row.setBackgroundResource(R.drawable.base_list_mid);
		}
        syncTable.addView(row);
        TextView shareCommDirLabel = (TextView)row.findViewById(R.id.setting_share_commdir_label);
        shareCommDirLabel.setText(commDir.name);
        row.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	//List<ShareUser> shareUserList = CommDirDao.getSingle(me).getShareUserListByCommDirId(commDir.dirId);
            	CommDirUserQuery cdq = new CommDirUserQuery();
				cdq.dirId = commDir.dirId;
            	new ShareCommDirUserQueryTask(me,TxlConstants.ACTION_SYNC_CODE).execute(cdq);
            }
        });
		
	}
	
	
	private  View createTableRow(){
	    /*TableRow ntr = new TableRow(me);
	    ntr.setGravity(Gravity.CENTER_VERTICAL);
	    ntr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));*/
	    final LayoutInflater inflater = LayoutInflater.from(me);
	    TableLayout tableLayout = (TableLayout)inflater.inflate(R.layout.setting_row_clone, null);
	    TableRow row = (TableRow)tableLayout.findViewById(R.id.setting_sync_share_commdir_item); 
	    tableLayout.removeView(row);
	    return row;
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg)
	    {
	        if(msg.what == TxlConstants.MSG_LOAD_DEPARTMENT){
	        	UserQuery uq = new UserQuery();
	        	uq.compId = Account.getSingle().compId;
	        	/*同步公司通讯录用户*/
	        	new CampanyUserQueryTask(me,TxlConstants.ACTION_SYNC_CODE).execute(uq);
	        }else if(msg.what == TxlConstants.MSG_SYNC_SHARE_COMMDIR_USER){
            	TxlToast.showShort(me, "共享通讯录同步成功!");
            }
	        
	    }
		
	};
	@Override
	public Handler getHandler() {
		return handler;
	}
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
    }
}
