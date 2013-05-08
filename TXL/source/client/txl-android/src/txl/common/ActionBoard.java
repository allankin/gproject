package txl.common;

import txl.TxlActivity;
import txl.activity.R;
import txl.contact.po.ContactVo;
import txl.util.IntentUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ActionBoard {
	/**
	 * 显示拨打、发送短信两个动作
	 */
	public static void show(final TxlActivity me,final ActionContact ac){
		View actionBoardView = LayoutInflater.from(me).inflate(R.layout.contact_action_board,null);
		/*拨打电话*/
		TextView actionCall = (TextView)actionBoardView.findViewById(R.id.contact_action_call);
		actionCall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				me.startActivity(IntentUtil.getCallIntent(ac.phone));
				TxlAlertDialog.alert.dismiss();
			}
		});
		/*发送sms*/
		TextView actionSendSms = (TextView)actionBoardView.findViewById(R.id.contact_action_send_sms);
		actionSendSms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				me.startActivity(IntentUtil.getSmsSendDialogIntent("",ac.phone));
				TxlAlertDialog.alert.dismiss();
			}
		});
		
		TxlAlertDialog.show(me, actionBoardView, "", null);
	}
	
	public static class ActionContact{
		public String phone;
	}
}
