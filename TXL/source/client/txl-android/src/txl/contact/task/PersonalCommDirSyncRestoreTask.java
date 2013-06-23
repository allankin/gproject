package txl.contact.task;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.ExceptionCallBack;
import txl.common.NetworkAsyncTask;
import txl.common.TxlAlertDialog;
import txl.common.TxlAlertDialog.DialogInvoker;
import txl.common.TxlProgressDialog;
import txl.common.TxlSyncPersonalCommdirProgressDialog;
import txl.common.TxlToast;
import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.contact.po.SyncLog;
import txl.contact.po.SyncLogResult;
import txl.util.HttpClientUtil;
import txl.util.Tool;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
/**
 * 
 * @author jinchao
 *
 */
public class PersonalCommDirSyncRestoreTask extends NetworkAsyncTask<Void,Void,Integer> implements Callback {
	public PersonalCommDirSyncRestoreTask me = this;
	private TxlProgressDialog progressDialog;
	
	
	public PersonalCommDirSyncRestoreTask(TxlActivity ctx){
	   this.ctx = ctx;
	   this.progressDialog = new TxlProgressDialog(ctx);
	}
	 
	@Override
	protected void onPreExecute() {
		this.progressDialog.setProcessAction("获取备份记录").show();
	}
	
	@Override
	protected Integer doInBackground(Void... d) {
		Map<String,String> postParams = new HashMap<String,String>();
		postParams.put("outUserId", String.valueOf(Account.getSingle().userId));
		postParams.put("compCode",Account.getSingle().compCode);
		
		int status = 0;
		/* preRestore  */
		String body;
		try {
			body = HttpClientUtil.httpPostUTF8(TxlConstants.SYNC_ACTION_PERSONAL_PRERESTORE_URL, postParams);
			SyncLogResult slr = new Gson().fromJson("",SyncLogResult.class);
			if(slr!=null){
				/*  preRestore success   */
				status = slr.getStatus();
				if(status==1){
					this.progressDialog.dismiss();
					int size = slr.getLogs().size();
					if(size>0){
						View syncListLayout = LayoutInflater.from(me.ctx).inflate(R.layout.contact_personal_sync_log, null);
						ListView listView = (ListView)syncListLayout.findViewById(R.id.sync_log_list);
						SyncLogListAdapter logAdapter = new SyncLogListAdapter(me.ctx,slr.getLogs());
						listView.setAdapter(logAdapter);
						TxlAlertDialog.show(me.ctx, syncListLayout, "确定,取消", new DialogInvoker() {
							@Override
							public void doInvoke(DialogInterface dialog, int btndex) {
								
							}
						});
					}
					
				}else{
					dealFailStatus(status);
				}
			}
			/*
			JSONObject json = new JSONObject(body.toString());
			status = json.optInt("status");
			 preRestore success 
			if(status==1){
				this.progressDialog.dismiss();
				JSONArray array = json.optJSONArray("logs");
				for(int i=0,len=array.length();i<len;i++){
					JSONObject jobj = array.getJSONObject(i);
					String syncId = jobj.optString("syncId");
					boolean syncStatus = jobj.optBoolean("status");
					int action = jobj.optInt("action");
					int backupSuccessCount = jobj.optInt("backupSuccessCount");
				}
				
				View syncListLayout = LayoutInflater.from(null).inflate(, null); 
				TxlAlertDialog.show(me, view, buttons, invoker)
				
			}else{
				dealFailStatus(status);
			}
			*/
		} catch(Exception e){
			this.dealException(e);
		}
		this.progressDialog.dismiss();
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
		
		return false;
	}
	
	class SyncLogListAdapter extends BaseAdapter{
		private Activity ctx;
		private List<SyncLog> logs;
		private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		public SyncLogListAdapter(Activity ctx,List<SyncLog> logs){
			this.ctx = ctx;
			this.logs = logs;
		}
		
		
		@Override
		public int getCount() {
			return this.logs.size();
		}

		@Override
		public Object getItem(int position) {
			return this.logs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		ViewHolder holder;
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(this.ctx).inflate(R.layout.contact_personal_sync_log_item, null);
				holder.createTime = (TextView)convertView.findViewById(R.id.contact_personal_sync_log_createtime);
				holder.logAction = (TextView)convertView.findViewById(R.id.contact_personal_sync_log_action);
				holder.logStatus = (TextView)convertView.findViewById(R.id.contact_personal_sync_log_status);
				holder.syncCount = (TextView)convertView.findViewById(R.id.contact_personal_sync_count);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			SyncLog log = this.logs.get(position);
			String createTime = sdf.format(log.getCreateTime());
			holder.createTime.setText(createTime);
			String action = "";
			if(log.getAction()==TxlConstants.SYNC_ACTION_BACKUP){
				action = "备份联系人";
			}else if(log.getAction() == TxlConstants.SYNC_ACTION_RESTORE){
				action = "恢复联系人";
			}
			holder.logAction.setText(action);
			holder.logStatus.setText(log.getStatus()?"成功":"失败");
			holder.syncCount.setText(String.valueOf(log.getBackupSuccessCount()));
			return convertView;
		}
		
		class ViewHolder {
			TextView createTime;
			TextView logAction;
			TextView logStatus;
			TextView syncCount;
			
		}
	}

}
