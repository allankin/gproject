package txl.contact.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import txl.TxlActivity;
import txl.common.WebLoadingTipDialog;
import txl.config.TxlConstants;
import txl.contact.dao.CommDirDao;
import txl.contact.po.Department;
import txl.util.HttpClientUtil;
import txl.util.Tool;
import android.os.AsyncTask;

public class DepartmentQueryTask extends AsyncTask<Void,Void,Void> {
	public TxlActivity ctx ;
	public DepartmentQueryTask(TxlActivity ctx){
	   this.ctx = ctx;
	}
	@Override
	protected Void doInBackground(Void... params) {
		List<Department> departs = new ArrayList<Department>();
		String url = TxlConstants.SEARCH_DEPARTMENT_URL;
		try {
			String body = HttpClientUtil.httpPostUTF8(url, null);
			JSONObject json = new JSONObject(body.toString());
			if(json.optInt("status")==-1){
				
			}else {
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
			}
		} catch (ConnectTimeoutException e) {
			WebLoadingTipDialog.getInstance(ctx).overLoadingDismiss();
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		 WebLoadingTipDialog.getInstance(ctx).show(TxlConstants.TIP_QUERING);
	}
	@Override
	protected void onPostExecute(Void result) {
		WebLoadingTipDialog.getInstance(ctx).dismiss();
		ctx.getHandler().dispatchMessage(Tool.genMessage(TxlConstants.MSG_LOAD_DEPARTMENT));
	}
	
}
