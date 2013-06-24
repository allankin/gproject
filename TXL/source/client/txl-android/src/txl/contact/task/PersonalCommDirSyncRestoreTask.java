package txl.contact.task;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.ExceptionCallBack;
import txl.common.NetworkAsyncTask;
import txl.common.TxlAlertDialog;
import txl.common.TxlAlertDialog.DialogInvoker;
import txl.common.TxlProgressDialog;
import txl.common.TxlToast;
import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.contact.po.PhoneContact;
import txl.contact.po.SyncContactResult;
import txl.contact.po.SyncLog;
import txl.contact.po.SyncLogResult;
import txl.log.TxLogger;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	final private TxLogger log = new TxLogger(CampanyUserQueryTask.class, TxlConstants.MODULE_ID_CONTACT);
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
			final SyncLogResult slr = new Gson().fromJson(body,SyncLogResult.class);
			if(slr!=null){
				/*  preRestore success   */
				status = slr.getStatus();
				if(status==1){
					this.progressDialog.dismiss();
					int size = slr.getLogs().size();
					if(size>0){
						final View syncListLayout = LayoutInflater.from(me.ctx).inflate(R.layout.contact_personal_sync_log, null);
						ListView listView = (ListView)syncListLayout.findViewById(R.id.sync_log_list);
						SyncLogListAdapter logAdapter = new SyncLogListAdapter(me.ctx,slr.getLogs());
						listView.setAdapter(logAdapter);
						listView.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								final SyncLog syncLog = slr.getLogs().get(position);
								
								new Thread( new Runnable() {
									@Override
									public void run() {
										String body = "";
										try {
											Map<String,String> postParams = new HashMap<String,String>();
											postParams.put("outUserId", String.valueOf(Account.getSingle().userId));
											postParams.put("compCode",Account.getSingle().compCode);
											postParams.put("syncId",syncLog.getSyncId());
											/* fetchContactIds */
											body = HttpClientUtil.httpPostUTF8(TxlConstants.SYNC_ACTION_PERSONAL_FETCH_CONTACTID_URL, postParams);
											postParams.remove("syncId");
											JSONObject jobj = new JSONObject(body);
											int status = jobj.optInt("status");
											if(status==1){
												JSONArray array = jobj.optJSONArray("contactIds");
												if(array!=null){
													int totalCount = array.length();
													for(int i=0,len=array.length();i<len;i++){
														int contactId = array.getInt(i);
														postParams.put("contactId", String.valueOf(contactId));
														/* restore single contact */
														body = HttpClientUtil.httpPostUTF8(TxlConstants.SYNC_ACTION_PERSONAL_RESTORE_URL, postParams);
														final SyncContactResult scr = new Gson().fromJson(body,SyncContactResult.class);
														int successCount = 0;
														if(scr!=null){
															status = scr.getStatus();
															if(status == 1){
																successCount++;
																PhoneContact contact = scr.getContact();
																log.info("name:"+contact.getName()
																		+",customRingtome:"+contact.getCustomRingtone());
																
																
																
																
																
																
																if(totalCount == successCount){
																	me.ctx.runOnUiThread(new Runnable() {
																		@Override
																		public void run() {
																			dealStatus(1);
																		}
																	});
																}else{
																	me.ctx.runOnUiThread(new Runnable() {
																		@Override
																		public void run() {
																			dealStatus(-1);
																		}
																	});
																}
																 
															}else{
																me.dealFailStatus(status);
															}
														}
													}
												}
												
											}else{
												me.dealFailStatus(status);
											}
											
										}catch(Exception e){
											me.dealException(e);
										}
									}
								}).start();
								
							}
							
						});
						this.ctx.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								TxlAlertDialog.show(me.ctx, syncListLayout, "确定,取消", new DialogInvoker() {
									@Override
									public void doInvoke(DialogInterface dialog, int btndex) {
										
									}
								});
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
	
	private void dealStatus(int status){
		if(status == 1){
			TxlToast.showShort(ctx, TxlConstants.SYNC_ACTION_RESTORE_SUCCESS_LABEL);
		}else{
			TxlToast.showShort(ctx, TxlConstants.SYNC_ACTION_RESTORE_FAIL_LABEL);
		}
	}
	
	@Override
	protected void onPostExecute(Integer status) {
		
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
			String status = null;
			if(log.getStatus()==null){
				status = "未知";
			}else{
				status =log.getStatus()?"成功":"失败" ;
			}
			
			holder.logStatus.setText(status);
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
