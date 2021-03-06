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
import txl.config.TxlConstants;
import txl.contact.dao.CommDirDao;
import txl.contact.po.CommDir;
import txl.contact.po.CommDirQuery;
import txl.contact.po.CompanyUser;
import txl.contact.po.UserQuery;
import txl.util.HttpClientUtil;
import txl.util.Tool;
import android.os.Message;

/**
 * 共享通讯录
 * 
 * @author jinchao
 *
 */
public class ShareCommDirQueryTask extends NetworkAsyncTask<CommDirQuery,Void,List<CommDir>> {
	public ShareCommDirQueryTask me = this;
	public ShareCommDirQueryTask(TxlActivity ctx){
	   this.ctx = ctx;
	   this.actionCode = TxlConstants.ACTION_QUERY_CODE;
	}
	 
	@Override
	protected void onPreExecute() {
		if(me.actionCode == TxlConstants.ACTION_QUERY_CODE){
			WebLoadingTipDialog.getInstance(ctx).show(TxlConstants.TIP_QUERING);
		} 
	}
	
	@Override
	protected List<CommDir> doInBackground(CommDirQuery... params) {
		final List<CommDir>  commdirList = new ArrayList<CommDir>();
		final CommDirQuery commDirQuery = params[0];
		Map<String,String> postParams = new HashMap<String,String>();
		postParams.put("filter.sbName", commDirQuery.sbName);
		postParams.put("outUserId", String.valueOf(Account.getSingle().userId));
		postParams.put("compCode",Account.getSingle().compCode);
		try {
			String body = HttpClientUtil.httpPostUTF8(TxlConstants.SEARCH_SHARE_COMMDIR_URL, postParams);
			JSONObject json = new JSONObject(body.toString());
			int status = json.optInt("status");
			if(status==1){
				JSONArray array = json.getJSONArray("shareBooks");
				if(array!=null){
					for(int i=0,len=array.length();i<len;i++){
						JSONObject commdirJson = array.getJSONObject(i);
						CommDir commdir = new CommDir();
						commdir.name = commdirJson.optString("sbName");
						commdir.dirId = commdirJson.optInt("sbId");
						commdir.userCount= commdirJson.optInt("userCount");
						commdir.type = TxlConstants.COMMDIR_SHARE_TYPE;
						commdirList.add(commdir);
					}
				}
			}
			else if(status==TxlConstants.SHARE_COMPANY_NOT_EXIST){
				ctx.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_SHARE_COMPANY_NOT_EXIST));
			}
			else if(status==TxlConstants.SHARE_USER_NOT_EXIT){
				ctx.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_SHARE_USER_NOT_EXIT));
			}
			
		}catch(Exception e){
			this.dealNetworkException(e,new ExceptionCallBack() {
				@Override
				public void deal() {
					String tipTemp ="网络未连接!";
					/*若为查询操作，则从本地加载*/
					if(me.actionCode == TxlConstants.ACTION_QUERY_CODE){
						tipTemp = "网络未连接,查询本地数据!";
						List<CommDir> commDirList1 = CommDirDao.getSingle(ctx).getShareCommDirList(commDirQuery.sbName);
						for(CommDir commDir :commDirList1){
							commdirList.add(commDir);
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
				return commdirList;
			}
		}
		
		return commdirList;
	}
	
	
	@Override
	protected void onPostExecute(List<CommDir> result) {
		WebLoadingTipDialog.getInstance(ctx).dismiss();
		if(this.actionCode == TxlConstants.ACTION_QUERY_CODE){
			Message msg = new Message();
			msg.what = TxlConstants.MSG_LOAD_SHARE_COMMDIR;
			msg.obj = result;
			ctx.getHandler().sendMessage(msg);
		} 
	}

}
