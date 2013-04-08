package txl.common;

import txl.TxlActivity;
import txl.config.TxlConstants;
import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;


public class WebLoadingTipDialog {
	private static WebLoadingTipDialog webLoadingTipDialog; 
	private ProgressDialog loadingTipDialog;
	
	private TxlActivity ctx;
	private WebLoadingTipDialog(TxlActivity ctx){
		this.ctx = ctx;
	}
	public static WebLoadingTipDialog getInstance(TxlActivity  ctx){
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
		if(loadingTipDialog.isShowing()){
            Toast.makeText(ctx, "加载超时", TxlConstants.Toast.SHORT).show();
            loadingTipDialog.dismiss();
        }
	}
	
	public void dismiss(){
		if(loadingTipDialog.isShowing()){
			loadingTipDialog.dismiss();
		}
	}
	
	
}
