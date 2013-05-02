package txl.contact;

import txl.TxlActivity;
import txl.activity.R;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
/**
 * 显示某个共享通讯录的用户列表
 * @author jinchao
 *
 */
public class CompanyUserDetailActivity extends TxlActivity {
	
private final TxLogger  log = new TxLogger(CompanyUserDetailActivity.class, TxlConstants.MODULE_ID_CONTACT);
	
	private CompanyUserDetailActivity me = this;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_company_user_detail);
		
		Intent intent = getIntent();
		String headerTitle = "";
		int commDirId =0;
		int contactCount = 0;
		if(intent!=null){
			Bundle bundle = intent.getExtras();
			if(bundle!=null){
				 
			}
		}
		 
	}
	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return null;
	}
	 
}
