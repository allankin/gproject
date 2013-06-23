package txl.contact.task;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import txl.TxlActivity;
import txl.common.ExceptionCallBack;
import txl.common.NetworkAsyncTask;
import txl.common.TxlSyncPersonalCommdirProgressDialog;
import txl.common.TxlToast;
import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.contact.dao.ContactDao;
import txl.contact.po.PhoneContact;
import txl.contact.po.PhoneContactList;
import txl.util.HttpClientUtil;
import txl.util.Tool;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
/**
 * TxlConstants.SYNC_ACTION_BACKUP
 * 
 * @author jinchao
 *
 */
public class PersonalCommDirSyncBackupTask extends NetworkAsyncTask<Void,Void,Integer> implements Callback {
	public PersonalCommDirSyncBackupTask me = this;
	private TxlSyncPersonalCommdirProgressDialog progressDialog;
	
	
	public PersonalCommDirSyncBackupTask(TxlActivity ctx){
	   this.ctx = ctx;
	   this.progressDialog = new TxlSyncPersonalCommdirProgressDialog(ctx);
	}
	 
	@Override
	protected void onPreExecute() {
		this.progressDialog.setProcessAction("本地获取联系人").show();
	}
	
	@Override
	protected Integer doInBackground(Void... d) {
		Map<String,String> postParams = new HashMap<String,String>();
		postParams.put("outUserId", String.valueOf(Account.getSingle().userId));
		postParams.put("compCode",Account.getSingle().compCode);
		
		int status = TxlConstants.SYNC_ACTION_BACKUP_PERSONAL_COMMDIR_FAIL;
		/* preBackup  */
		PhoneContactList<PhoneContact> phoneContacts = ContactDao.getAllContactsInfo(ctx);
		final int totalCount = phoneContacts.size();
		this.handler.post(new Runnable() {
			@Override
			public void run() {
				me.progressDialog.setProcessAction("开始备份联系人");
				me.progressDialog.setTotalCount(totalCount);
			}
		});
		String syncId = Tool.genUUID();
		postParams.put("syncId", syncId);
		postParams.put("contactCount", String.valueOf(totalCount));
		String body;
		try {
			body = HttpClientUtil.httpPostUTF8(TxlConstants.SYNC_ACTION_PERSONAL_PREBACKUP_URL, postParams);
			JSONObject json = new JSONObject(body.toString());
			status = json.optInt("status");
			/* preBackup success */
			if(status==1){
				
				int successCount = 0;
				/* backup */
				postParams.remove("syncId");
				postParams.remove("contactCount");
				int dealCount=0;
				for(int i=0;i<totalCount;i++){
					PhoneContact contact =  phoneContacts.get(i);
					dealCount++;
					contact.setSyncId(syncId);
					postParams.put("content", contact.toJSONStringForBackUp());
					postParams.put("photo", contact.getPhoto());
					try {
						body = HttpClientUtil.httpPostUTF8(TxlConstants.SYNC_ACTION_PERSONAL_BACKUP_URL, postParams);
						json = new JSONObject(body.toString());
						status = json.optInt("status");
						/* single backup success */
						if(status==TxlConstants.SYNC_ACTION_BACKUP_PERSONAL_COMMDIR_SUCCESS){
							successCount++;
						}else{
							dealFailStatus(status);
							break;
						}
					}catch(Exception e){
						this.dealException(e);
						break;
					}finally{
						postParams.remove("content");
						postParams.remove("photo");
					}
					Log.d("PersonalCommDirSyncBackupTask","dealCount:"+dealCount+",i:"+i);
					//int progress = Math.round(dealCount/Float.valueOf(totalCount)*100);
					this.handler.sendMessage(Tool.genMessage(TxlConstants.MSG_SYNC_PERSONAL_BACKUP_SET_INDEX, dealCount));
					this.handler.sendMessage(Tool.genMessage(TxlConstants.MSG_SYNC_PERSONAL_BACKUP_SET_SUCEESS_COUNT, successCount));
				}
				
				boolean backupStatus = false;
				/* postBackup */
				if(successCount == totalCount){
					backupStatus = true;
				}
				this.handler.sendMessage(Tool.genMessage(TxlConstants.MSG_SYNC_PERSONAL_BACKUP_SET_ACTION_NAME, "联系人备份完成"));
				
				try {
					postParams.put("backupStatus", String.valueOf(backupStatus));
					body = HttpClientUtil.httpPostUTF8(TxlConstants.SYNC_ACTION_PERSONAL_POSTBACKUP_URL, postParams);
					json = new JSONObject(body.toString());
					status = json.optInt("status");
					if(status==1){
						
					}else{
						dealFailStatus(status);
					}
					
				}catch(Exception e){
					this.dealException(e);
				} 
				
			}else{
				dealFailStatus(status);
			}
		} catch(Exception e){
			this.dealException(e);
		}
		
		return status;
	}
	
	private void dealException(Exception e){
		this.dealNetworkException(e,new ExceptionCallBack() {
			@Override
			public void deal() {
				String tipTemp ="网络未连接!";
				final String tip = tipTemp;
				ctx.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						TxlToast.showShort(ctx, tip);
					}
				});
				
			}
		});
	}
	private void dealFailStatus(int status){
		if(status==TxlConstants.SHARE_COMPANY_NOT_EXIST){
			ctx.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_SHARE_COMPANY_NOT_EXIST));
		}
		else if(status==TxlConstants.SHARE_USER_NOT_EXIT){
			ctx.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_SHARE_USER_NOT_EXIT));
		}
	}
	
	@Override
	protected void onPostExecute(Integer status) {
		if(status == TxlConstants.SYNC_ACTION_BACKUP_PERSONAL_COMMDIR_SUCCESS){
			TxlToast.showShort(ctx, TxlConstants.SYNC_ACTION_BACKUP_SUCCESS_LABEL);
		}else if(status == TxlConstants.SYNC_ACTION_BACKUP_PERSONAL_COMMDIR_FAIL){
			TxlToast.showShort(ctx, TxlConstants.SYNC_ACTION_BACKUP_FAIL_LABEL);
		}
	}
	private Handler handler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == TxlConstants.MSG_SYNC_PERSONAL_BACKUP_SET_ACTION_NAME){
			String actionName = (String)msg.obj;
			this.progressDialog.setProcessAction(actionName);
		}else if(msg.what == TxlConstants.MSG_SYNC_PERSONAL_BACKUP_SET_INDEX){
			Integer dealCount = (Integer)msg.obj;
			this.progressDialog.setProcess(dealCount);
		}else if(msg.what == TxlConstants.MSG_SYNC_PERSONAL_BACKUP_SET_SUCEESS_COUNT){
			Integer count = (Integer)msg.obj;
			this.progressDialog.setSuccessCount(count);
		}
		return false;
	}

}
