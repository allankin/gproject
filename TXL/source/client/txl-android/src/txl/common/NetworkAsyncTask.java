package txl.common;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

import txl.TxlActivity;
import txl.config.TxlConstants;
import txl.contact.dao.CommDirDao;
import txl.contact.task.CampanyUserQueryTask;
import txl.contact.task.DepartmentQueryTask;
import txl.log.TxLogger;
import android.os.AsyncTask;

public abstract class NetworkAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	protected TxlActivity ctx ;
	/* null: 表示网络无法连接    false:会话有效    true:  会话过期  */
	protected Boolean sessionTimeout = false;
	/*操作编号*/
	protected int actionCode;
	private TxLogger log = new TxLogger(NetworkAsyncTask.class,TxlConstants.MODULE_ID_BASE);
	
	protected void dealNetworkException(Exception e,ExceptionCallBack callback){
		if(e instanceof HttpHostConnectException){
			log.error("HttpHostConnectException");
			if(callback!=null){
				callback.deal();
			}else{
				ctx.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						TxlToast.showShort(ctx, "网络未连接!");
					}
				});
			}
			
		}else if(e instanceof ConnectTimeoutException){
			ctx.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					WebLoadingTipDialog.getInstance(ctx).overLoadingDismiss();
				}
			});
		}
		
		e.printStackTrace();
	}
}
