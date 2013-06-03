package txl.common;

import txl.config.TxlConstants;
import txl.contact.dao.CommDirDao;
import txl.log.TxLogger;
import android.app.Activity;
import android.app.ProgressDialog;

/**
 * 由于dialog的显示关联具体的activity的context，注意context必须关联到当前的activity 的context.
 * 注意：同时需要多个dialog显示可能存在问题
 * 
 * @author jinchao
 *
 */
public class WebLoadingTipDialog {
    private final TxLogger log = new TxLogger(WebLoadingTipDialog.class,
                                              TxlConstants.MODULE_ID_BASE);
	private static WebLoadingTipDialog webLoadingTipDialog; 
	private ProgressDialog loadingTipDialog;
	
	private Activity ctx;
	private WebLoadingTipDialog(Activity ctx){
		this.ctx = ctx;
	}
	public static WebLoadingTipDialog getInstance(Activity  ctx){
		if(webLoadingTipDialog==null){
			return webLoadingTipDialog = new WebLoadingTipDialog(ctx);
		}else{
		    webLoadingTipDialog.ctx = ctx;
		}
		return webLoadingTipDialog;
	}
	
	
	public void show(String msg){
		loadingTipDialog  = new ProgressDialog(ctx);
		loadingTipDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingTipDialog.setMessage(msg);
		loadingTipDialog.setIndeterminate(false);
		loadingTipDialog.setCancelable(false);
		log.info("class name : "+ctx.getClass().getSimpleName());
		//if(!ctx.isFinishing()){
		    loadingTipDialog.show();
		//}
	}
	public void show(String msg,int style){
		if(ProgressDialog.STYLE_HORIZONTAL == style){
			loadingTipDialog  = new ProgressDialog(ctx);
			// 设置进度条风格，风格为长形
			loadingTipDialog.setProgressStyle(style);
			// 设置ProgressDialog 标题
			//loadingTipDialog.setTitle("提示");
			// 设置ProgressDialog 提示信息
			loadingTipDialog.setMessage(msg);
			// 设置ProgressDialog 标题图标
			//loadingTipDialog.setIcon(R.drawable.img2);
			// 设置ProgressDialog 进度条进度
			loadingTipDialog.setProgress(0);
			// 设置ProgressDialog 的进度条是否不明确
			loadingTipDialog.setIndeterminate(false);
		}else{
			loadingTipDialog  = new ProgressDialog(ctx);
		}
		// 设置ProgressDialog 是否可以按退回按键取消
		loadingTipDialog.setCancelable(true);
		// 让ProgressDialog显示
		loadingTipDialog.show();
	}
	
	public void setMessage(String message){
		loadingTipDialog.setMessage(message);
	}
	public void setProcess(int value){
		loadingTipDialog.setProgress(value);
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
