package txl.contact;

import txl.TxlActivity;
import txl.activity.R;
import txl.config.TxlConstants;
import txl.contact.dao.CommDirDao;
import txl.contact.po.CompanyUser;
import txl.contact.po.Department;
import txl.log.TxLogger;
import txl.util.IntentUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
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
		TextView header = (TextView)findViewById(R.id.header);
		header.setText("公司用户详情");
		if(intent!=null){
			Bundle bundle = intent.getExtras();
			if(bundle!=null){
			    final CompanyUser user = (CompanyUser)bundle.getSerializable(TxlConstants.INTENT_BUNDLE_COMPANY_USER); 
			    TextView nameTv = (TextView)findViewById(R.id.company_user_name);
			    TextView departNameTv = (TextView)findViewById(R.id.company_user_departName);
			    TextView positionTv = (TextView)findViewById(R.id.company_user_position);
			    //TextView userPhoneTv = (TextView)findViewById(R.id.company_user_phone);
			    LinearLayout userPhoneLayout = (LinearLayout)findViewById(R.id.company_user_phone_layout);
			    
			    TextView virtualTelTv = (TextView)findViewById(R.id.company_user_virtualTel);
			    //TextView compTelTv = (TextView)findViewById(R.id.company_phone);
			    LinearLayout compTelLayout = (LinearLayout)findViewById(R.id.company_phone_layout);
			    TextView homeTelTv = (TextView)findViewById(R.id.company_user_homeTel);
			    TextView emailTv = (TextView)findViewById(R.id.company_user_email);
			    TextView msnTv = (TextView)findViewById(R.id.company_user_msn);
			    TextView qqTv = (TextView)findViewById(R.id.company_user_qq);
			    
			    nameTv.setText(user.name);
			    Department depart = CommDirDao.getSingle(me).getDepartByDepId(user.depId);
			    if(depart!=null){
			        departNameTv.setText(depart.depName);
			    }
			    positionTv.setText(user.position);
			    //userPhoneTv.setText(user.userPhone);
			    virtualTelTv.setText(user.virtualTel);
			    //compTelTv.setText(user.compTel);
			    
			    homeTelTv.setText(user.homeTel);
			    emailTv.setText(user.email);
			    msnTv.setText(user.msn);
			    qqTv.setText(user.qq);
			    
			    this.addPhones(userPhoneLayout, user.userPhone);
			    this.addPhones(compTelLayout, user.compTel);
			    
			    virtualTelTv.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                       Intent intent = IntentUtil.getDialIntent(user.virtualTel);
                       startActivity(intent);
                    }
                });
			    
			}
		}
		 
	}
	
	private void addPhones(LinearLayout telLayout,String phones){
	    if(phones!=null && phones.length()>0){
            String[] compTels = phones.split(",");
            for(String compTel:compTels){
                TextView tv = new TextView(me);
                tv.setText(compTel);
                tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                telLayout.addView(tv);
                final String tel = compTel;
                tv.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                       Intent intent = IntentUtil.getDialIntent(tel);
                       startActivity(intent);
                    }
                });
            }
        }
	}
	
	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return null;
	}
	 
}
