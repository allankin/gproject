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
import txl.contact.po.CompanyUser;
import txl.contact.po.User;
import txl.contact.po.UserQuery;
import txl.log.TxLogger;
import txl.util.HttpClientUtil;
import txl.util.Tool;
import android.os.Message;

/**
 * 公司用户查询兼同步操作。
 * @author jinchao
 *
 */
public class CampanyUserQueryTask extends NetworkAsyncTask<UserQuery,Void,List<CompanyUser>> {
	
	private TxLogger log = new TxLogger(CampanyUserQueryTask.class, TxlConstants.MODULE_ID_CONTACT);
	public CampanyUserQueryTask me = this;
	public CampanyUserQueryTask(TxlActivity ctx){
	   this.ctx = ctx;
	   this.actionCode = TxlConstants.ACTION_QUERY_CODE;
	}
	public CampanyUserQueryTask(TxlActivity ctx,int actionCode){
		this.ctx = ctx;
		this.actionCode = actionCode;
	}
	@Override
	protected void onPreExecute() {
		log.info("onPreExecute...  isRunning: "+ctx.isRunning);
		if(me.actionCode == TxlConstants.ACTION_QUERY_CODE){
			WebLoadingTipDialog.getInstance(this.ctx.getParent()).show(TxlConstants.TIP_QUERING);
		}else if(me.actionCode == TxlConstants.ACTION_SYNC_CODE){
			WebLoadingTipDialog.getInstance(this.ctx.getParent()).show(TxlConstants.TIP_SYNC);
		}
		
	}
	
	@Override
	protected List<CompanyUser> doInBackground(UserQuery... params) {
		final List<CompanyUser>  userList = new ArrayList<CompanyUser>();
		final UserQuery userQuery = params[0];
		Map<String,String> postParams = new HashMap<String,String>();
		postParams.put("filter.name", userQuery.name);
		if(userQuery.depId!=0){
			postParams.put("filter.depId", String.valueOf(userQuery.depId));
		}else{
			postParams.put("filter.depId", null);
		}
		postParams.put("filter.compId", String.valueOf(userQuery.compId));
		
		try {
			String body = HttpClientUtil.httpPostUTF8(TxlConstants.SEARCH_COMPANY_USER_URL, postParams);
			JSONObject json = new JSONObject(body.toString());
			if(json.optInt("status")!=-1){
				JSONArray array = json.getJSONArray("users");
				if(array!=null){
					for(int i=0,len=array.length();i<len;i++){
						JSONObject userJson = array.getJSONObject(i);
						CompanyUser user = new CompanyUser();
						user.userId = userJson.optInt("userId");
						user.depId = userJson.optInt("depId");
						user.compId = userJson.optInt("compId");
						user.userPhone = userJson.optString("userPhone");
						user.name = userJson.optString("name");
						userList.add(user);
					}
				}
				if(this.actionCode == TxlConstants.ACTION_SYNC_CODE){
					CommDirDao.getSingle(ctx).deleteCompanyUser();
					CommDirDao.getSingle(this.ctx).saveCompanyUser(userList);
				}
			}else{
				sessionTimeout = true;
			}
		}catch(Exception e){
			sessionTimeout = null;
			this.dealNetworkException(e,new ExceptionCallBack() {
				@Override
				public void deal() {
					String tipTemp ="网络未连接!";
					/*若为查询操作，则从本地加载*/
					if(me.actionCode == TxlConstants.ACTION_QUERY_CODE){
						tipTemp = "网络未连接,查询本地数据!";
						List<CompanyUser> userList1 = CommDirDao.getSingle(ctx).getCompUserList(userQuery.name,userQuery.depId);
						for(CompanyUser user :userList1){
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
	protected void onPostExecute(List<CompanyUser> result) {
		WebLoadingTipDialog.getInstance(Config.mainContext).dismiss();
		if(sessionTimeout!=null){
			/*session会话过期 */
			if(sessionTimeout){
				/*若没有打开同步公司开关*/
				TxlToast.showLong(ctx, "会话过期,请到设置中重新登陆");
			}else{
				if(this.actionCode == TxlConstants.ACTION_QUERY_CODE){
					Message msg = new Message();
					msg.what = TxlConstants.MSG_RENDER_COMPANY_USER;
					msg.obj = result;
					ctx.getHandler().sendMessage(msg);
				}else if(this.actionCode == TxlConstants.ACTION_SYNC_CODE){
					TxlToast.showShort(this.ctx, "公司通讯录联系人同步完成");
				}
			}
		}
	}
	
	 

}
