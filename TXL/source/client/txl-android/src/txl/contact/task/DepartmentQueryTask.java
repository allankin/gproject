package txl.contact.task;

import java.util.ArrayList;
import java.util.List;

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
import txl.contact.po.CompanyUser;
import txl.contact.po.Department;
import txl.util.HttpClientUtil;
import txl.util.Tool;

public class DepartmentQueryTask extends NetworkAsyncTask<Void,Void,List<Department>> {
	public DepartmentQueryTask me = this;
	public DepartmentQueryTask(TxlActivity ctx){
	   this.ctx = ctx;
	   this.actionCode = TxlConstants.ACTION_QUERY_CODE;
	}
	public DepartmentQueryTask(TxlActivity ctx,int actionCode){
		this.ctx = ctx;
		this.actionCode = actionCode;
	}
	@Override
	protected List<Department> doInBackground(Void... params) {
		final List<Department> departs = new ArrayList<Department>();
		String url = TxlConstants.SEARCH_DEPARTMENT_URL;
		try {
			String body = HttpClientUtil.httpPostUTF8(url, null);
			JSONObject json = new JSONObject(body.toString());
			
			if(json.optInt("status")!=-1){
				JSONArray array = json.getJSONArray("departs");
				if(array!=null){
					for(int i=0,len=array.length();i<len;i++){
						JSONObject departJson = array.getJSONObject(i);
						Department depart = new Department();
						depart.depId = departJson.getInt("depId");
						depart.depName = departJson.getString("depName");
						depart.depParentId = departJson.getInt("depParentId");
						departs.add(depart);
					}
					CommDirDao.getSingle(ctx).deleteDepart();
					CommDirDao.getSingle(ctx).saveDepart(departs);
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
		}  
		return departs;
	}

	@Override
	protected void onPreExecute() {
		if(me.actionCode == TxlConstants.ACTION_QUERY_CODE){
			WebLoadingTipDialog.getInstance(ctx).show(TxlConstants.TIP_QUERING);
		}else if(me.actionCode == TxlConstants.ACTION_SYNC_CODE){
			WebLoadingTipDialog.getInstance(ctx).show(TxlConstants.TIP_SYNC);
		}
	}
	@Override
	protected void onPostExecute(List<Department> result) {
		WebLoadingTipDialog.getInstance(ctx).dismiss();
		if(sessionTimeout!=null){
			if(sessionTimeout){
				TxlToast.showLong(ctx, "会话过期,请到设置中重新登陆");
				return;
			} 
		}
		ctx.getHandler().dispatchMessage(Tool.genMessage(TxlConstants.MSG_LOAD_DEPARTMENT));
		if(this.actionCode == TxlConstants.ACTION_SYNC_CODE){
			TxlToast.showShort(this.ctx, "公司部门同步完成");
		}
	}
	
}
