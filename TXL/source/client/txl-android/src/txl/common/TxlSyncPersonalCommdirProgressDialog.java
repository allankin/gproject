package txl.common;

import txl.TxlActivity;
import txl.activity.R;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TxlSyncPersonalCommdirProgressDialog extends TxlProgressDialog {
	private TxlSyncPersonalCommdirProgressDialog me = this;
	public TxlSyncPersonalCommdirProgressDialog(TxlActivity ctx) {
		super(ctx);
		Button confirmExit = (Button)this.progressDialogView.findViewById(R.id.progress_confirm_exit);
		confirmExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				me.dismiss();
			}
		});
	}
	public TxlSyncPersonalCommdirProgressDialog setProcess(int index){
		TextView processIndexView = (TextView)progressDialogView.findViewById(R.id.progress_current_index);
		processIndexView.setText(String.valueOf(index));
		return this;
	}
	public TxlSyncPersonalCommdirProgressDialog setSuccessCount(int count){
		TextView processIndexView = (TextView)progressDialogView.findViewById(R.id.progress_success_index);
		processIndexView.setText(String.valueOf(count));
		return this;
	}
	
	public TxlSyncPersonalCommdirProgressDialog setTotalCount(int count){
		LinearLayout linear = (LinearLayout)progressDialogView.findViewById(R.id.sync_personal_commdir_progress);
		linear.setVisibility(View.VISIBLE);
		TextView totalView = (TextView)progressDialogView.findViewById(R.id.progress_current_total);
		totalView.setText(String.valueOf(count));
		return this;
	}
}
