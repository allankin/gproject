package txl.web;

import txl.common.TxlAlertDialog;
import txl.common.WebLoadingTipDialog;
import txl.config.TxlConstants;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TxlWebViewClient extends WebViewClient {
		private Activity ctx;
		private boolean timeout;

		public TxlWebViewClient(Activity ctx){
			this.ctx  = ctx;
		}
		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			WebLoadingTipDialog.getInstance(ctx).dismiss();
			timeout = false;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			WebLoadingTipDialog.getInstance(ctx).show(TxlConstants.TIP_LOADING);
			timeout = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(TxlConstants.HTTP_SO_TIMEOUT);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(timeout){
						ctx.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								WebLoadingTipDialog.getInstance(ctx).dismiss();
								TxlAlertDialog.show(ctx, TxlConstants.ERROR_NETWORK_TIMEOUT, "确定", new TxlAlertDialog.DialogInvoker() {
									@Override
									public void doInvoke(DialogInterface dialog, int btndex) {
										
									}
								});								
							}
						});
					}
				}
			}).start();
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			WebLoadingTipDialog.getInstance(ctx).dismiss();
			timeout = false;
			// HS_TODO: 网页加载错误页面
			
			
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

 

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);    
			return true;
		}  
	  
	} 