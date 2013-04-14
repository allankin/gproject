package txl.common;

import txl.config.TxlConstants;
import android.app.Activity;
import android.app.ProgressDialog;


public class WebLoadingTipDialog {
	private static WebLoadingTipDialog webLoadingTipDialog; 
	private ProgressDialog loadingTipDialog;
	
	private Activity ctx;
	private WebLoadingTipDialog(Activity ctx){
		this.ctx = ctx;
	}
	public static WebLoadingTipDialog getInstance(Activity  ctx){
		if(webLoadingTipDialog==null){
			return webLoadingTipDialog = new WebLoadingTipDialog(ctx);
		}
		return webLoadingTipDialog;
	}
	
	
	public void show(String msg){
		loadingTipDialog  = new ProgressDialog(ctx);
		loadingTipDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingTipDialog.setMessage(msg);
		loadingTipDialog.setIndeterminate(false);
		loadingTipDialog.setCancelable(false);
		loadingTipDialog.show();
	}
	
	public void overLoadingDismiss(){
		if(loadingTipDialog!=null && loadingTipDialog.isShowing()){
			TxlToast.showLong(ctx, TxlConstants.ERROR_NETWORK_TIMEOUT);
            loadingTipDialog.dismiss();
            loadingTipDialog=null;
        }
	}
	
	public void dismiss(){
		if(loadingTipDialog!=null && loadingTipDialog.isShowing()){
			loadingTipDialog.dismiss();
			loadingTipDialog = null;
		}
	}
	
	
}
