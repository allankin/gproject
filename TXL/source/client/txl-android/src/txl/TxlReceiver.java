package txl;

import txl.common.TxlAlertDialog;
import txl.common.TxlAlertDialog.DialogInvoker;
import txl.common.po.Account;
import txl.config.TxlConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class TxlReceiver extends BroadcastReceiver{
	private MainActivity ctx;
	public TxlReceiver(MainActivity ctx){
		this.ctx = ctx;
	}
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (TxlConstants.ACTION_OFFLINE_NOTICE.equals(intent.getAction())) {
        	if(Account.getSingle().setting.pushMessage==1){
        		TxlAlertDialog.show(ctx, "已经在异地登陆,请退出!", "确定", new DialogInvoker() {
    				@Override
    				public void doInvoke(DialogInterface dialog, int btndex) {
    					android.os.Process.killProcess(android.os.Process.myPid());
    				}
    			});
        	}
        	
        }
    }
}
