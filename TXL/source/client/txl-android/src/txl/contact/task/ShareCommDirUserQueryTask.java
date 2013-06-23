package txl.contact.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import txl.TxlActivity;
import txl.common.ExceptionCallBack;
import txl.common.NetworkAsyncTask;
import txl.common.TxlToast;
import txl.common.WebLoadingTipDialog;
import txl.common.po.Account;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.contact.dao.CommDirDao;
import txl.contact.po.CommDir;
import txl.contact.po.CommDirUserQuery;
import txl.contact.po.CompanyUser;
import txl.contact.po.ShareUser;
import txl.contact.po.User;
import txl.contact.po.UserQuery;
import txl.log.TxLogger;
import txl.util.HttpClientUtil;
import txl.util.Tool;
import android.os.Message;

/**
 * 共享通讯录用户查询兼同步操作。
 * @author jinchao
 *
 */
public class ShareCommDirUserQueryTask extends NetworkAsyncTask<CommDirUserQuery,Void,List<ShareUser>> {
	
	private TxLogger log = new TxLogger(ShareCommDirUserQueryTask.class, TxlConstants.MODULE_ID_CONTACT);
	public ShareCommDirUserQueryTask me = this;
	public ShareCommDirUserQueryTask(TxlActivity ctx){
	   this.ctx = ctx;
	   this.actionCode = TxlConstants.ACTION_QUERY_CODE;
	}
	public ShareCommDirUserQueryTask(TxlActivity ctx,int actionCode){
		this.ctx = ctx;
		this.actionCode = actionCode;
	}
	@Override
	protected void onPreExecute() {
		log.info("onPreExecute...  isRunning: "+ctx.isRunning);
		if(me.actionCode == TxlConstants.ACTION_QUERY_CODE){
			WebLoadingTipDialog.getInstance(this.ctx).show(TxlConstants.TIP_QUERING);
		}else if(me.actionCode == TxlConstants.ACTION_SYNC_CODE){
			WebLoadingTipDialog.getInstance(this.ctx).show(TxlConstants.TIP_SYNC);
		}
		
	}
	
	@Override
	protected List<ShareUser> doInBackground(CommDirUserQuery... params) {
		final List<ShareUser>  userList = new ArrayList<ShareUser>();
		final CommDirUserQuery query = params[0];
		Map<String,String> postParams = new HashMap<String,String>();
		postParams.put("filter.name", query.name);
		postParams.put("filter.sbId", String.valueOf(query.dirId));
		
		postParams.put("outUserId", String.valueOf(Account.getSingle().userId));
		postParams.put("compCode",Account.getSingle().compCode); 
		
		try {
			String body = HttpClientUtil.httpPostUTF8(TxlConstants.SEARCH_SHARE_COMMDIR_USER_URL, postParams);
			JSONObject json = new JSONObject(body.toString());
			int status = json.optInt("status");
			
			if(status==1){
				JSONArray array = json.getJSONArray("shareBookUsers");
				if(array!=null){
					for(int i=0,len=array.length();i<len;i++){
						JSONObject userJson = array.getJSONObject(i);
						ShareUser user = new ShareUser();
						user.userId = userJson.optInt("userId");
						user.dirId = userJson.optInt("sbId");
						user.compId = userJson.optInt("compId");
						user.userPhone = userJson.optString("userPhone");
						user.compCode = userJson.optString("compCode");
						user.name = userJson.optString("name");
						userList.add(user);
					}
				}
			}
			else if(status==TxlConstants.SHARE_COMPANY_NOT_EXIST){
				ctx.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_SHARE_COMPANY_NOT_EXIST));
			}
			else if(status==TxlConstants.SHARE_USER_NOT_EXIT){
				ctx.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_SHARE_USER_NOT_EXIT));
			}
			/*若为同步操作*/
			if(this.actionCode == TxlConstants.ACTION_SYNC_CODE){
				CommDirDao.getSingle(ctx).deleteShareCommDirUser(query.dirId);
				CommDirDao.getSingle(this.ctx).saveShareCommDirUser(userList);
				ctx.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_SYNC_SHARE_COMMDIR_USER));
			}
		}catch(Exception e){
			this.dealNetworkException(e,new ExceptionCallBack() {
				@Override
				public void deal() {
					String tipTemp ="网络未连接!";
					/*若为查询操作，则从本地加载*/
					if(me.actionCode == TxlConstants.ACTION_QUERY_CODE){
						tipTemp = "网络未连接,查询本地数据!";
						List<ShareUser> shareUser1 = CommDirDao.getSingle(ctx).getShareUserList(query.dirId,query.name);
						for(ShareUser user :shareUser1){
							userList.add(user);
						}
					} 
					final String tip = tipTemp;
					ctx.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							TxlToast.showShort(ctx, tip);
						}
					});
					
				}
			});
			
			if(me.actionCode == TxlConstants.ACTION_QUERY_CODE){
				return userList;
			}
		}
		
		return userList;
	}
	
	
	@Override
	protected void onPostExecute(List<ShareUser> result) {
		WebLoadingTipDialog.getInstance(ctx).dismiss();
		if(this.actionCode == TxlConstants.ACTION_QUERY_CODE){
			Message msg = new Message();
			msg.what = TxlConstants.MSG_LOAD_SHARE_COMMDIR_USER;
			msg.obj = result;
			ctx.getHandler().sendMessage(msg);
		} 
	}
	
	 

}
