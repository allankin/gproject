package txl.common;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import txl.TxlActivity;
import txl.activity.R;
import txl.common.login.LoginDialog;

public class TxlProgressDialog {
	protected android.app.AlertDialog processAlert;
	protected View progressDialogView;
	public TxlProgressDialog(TxlActivity ctx){
		final LayoutInflater factory = LayoutInflater.from(ctx);
        this.progressDialogView = factory.inflate(R.layout.progress_dialog, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
        builder.setView(progressDialogView); 
        builder.setCancelable(false);
        processAlert = builder.create();
	}
	public void show(){
        processAlert.show();
	}
	
	public TxlProgressDialog setProcessAction(String action){
		TextView processActionView = (TextView)progressDialogView.findViewById(R.id.progress_action);
		processActionView.setText(action);
		return this;
	}
	
	public TxlProgressDialog setProcess(String percent){
		TextView processPercentView = (TextView)progressDialogView.findViewById(R.id.progress_percent);
		processPercentView.setText(percent);
		return this;
	}
	
	
	
	public void dismiss(){
		if(processAlert!=null){
			processAlert.dismiss();
			processAlert = null;
		}
	}
}
