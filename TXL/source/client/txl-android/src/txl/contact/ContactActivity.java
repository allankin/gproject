package txl.contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import txl.Handlable;
import txl.activity.R;
import txl.common.SideBar;
import txl.config.TxlConstants;
import txl.contact.adapter.ContactListAdapter;
import txl.contact.adapter.ContactShareCommDirListAdapter;
import txl.contact.dao.CommDirDao;
import txl.contact.dao.ContactDao;
import txl.contact.po.CommDir;
import txl.contact.po.ContactVo;
import txl.contact.po.ShareUser;
import txl.util.ContactVoComparator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @ClassName: CallRecord.java
 * @Description:
 * @Author JinChao
 * @Date 2013-2-17 下午3:54:19
 * @Copyright: 版权由 HundSun 拥有
 */
public class ContactActivity extends Activity implements Handlable
{
	

    private final String          TAG                       = ContactActivity.class.getSimpleName();

    Context                       mContext                  = null;
    private TextView headerView = null;

    private LinearLayout commDirContainer;
    private LayoutInflater inflater;
    private ContactActivity me = this;
    
    
    /************************************* 个人通讯录 变量 *****************************************************/
    private List<ContactVo> contactList = new ArrayList<ContactVo>();
    private Set<String> contactPhoneSet = new HashSet<String>();
    private ListView                      personalListView                 = null;
    private ContactListAdapter            personalContactListAdapter                 = null;
    
    private TextView overlay;
    private boolean visible;
    private boolean personalCommDirLoaded = false;
    private View personalLayout;
    /************************************* 公司通讯录 变量 *****************************************************/
    private boolean companyCommDirLoaded = false;

    
    
    
    
    
    
    
    /************************************* 共享通讯录 变量 *****************************************************/
    private boolean shareCommDirLoaded = false;
    private List<CommDir> shareCommDirList;
    private ListView                      shareCommDirListView                 = null;
    private ContactShareCommDirListAdapter            shareCommDirListAdapter                 = null;
    
    
    
    
    
    
    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        inflater = LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.tab_contact, null));
        commDirContainer = (LinearLayout)findViewById(R.id.contact_commdir_container);
        
        Spinner commdirTypeSpinner = (Spinner)findViewById(R.id.commdir_type);
		ArrayAdapter<String> commDirTypeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,me.getResources().getStringArray(R.array.contact_commdir_type));
		commDirTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		commdirTypeSpinner.setAdapter(commDirTypeAdapter);
		
		commdirTypeSpinner.setSelection(0,false);
		/*默认为个人通讯录*/
		loadPersonalCommDir();
		commdirTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position==0){
                	loadPersonalCommDir();
                }else if(position == 1){
                	
                }else if(position == 2){
                	loadShareCommDir();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                
            }
            
        });
        
        
    }
    
    /**
     * 加载个人通讯录
     */
    public void loadPersonalCommDir(){
    	
    	commDirContainer.removeAllViews();
    	
		personalLayout = inflater.inflate(R.layout.contact_personal_commdir, commDirContainer);
		personalListView = (ListView) personalLayout.findViewById(R.id.contact_list); 
		/** 得到手机通讯录联系人信息 **/
		ContactDao.getPhoneContacts(this,contactList,contactPhoneSet);
		ContactDao.getSIMContacts(this,contactList,contactPhoneSet);
		Collections.sort(contactList,new ContactVoComparator());
		personalContactListAdapter = new ContactListAdapter(this,contactList);
		personalListView.setAdapter(personalContactListAdapter);
		
		personalListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
			{
				Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactList.get(position).phone));
				//startActivity(dialIntent);
			}
		});
		personalListView.setOnScrollListener(new OnScrollListener() {
			
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				visible = true;
				if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
					overlay.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (visible) {
					char firstLetter = contactList.get(firstVisibleItem).firstLetter;
					if(firstLetter>0){
						overlay.setText(String.valueOf(firstLetter));
						overlay.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		
		this.overlay = (TextView) View.inflate(this, R.layout.overlay, null);
		getWindowManager()
		.addView(
				overlay,
				new WindowManager.LayoutParams(
						LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.TYPE_APPLICATION,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
						PixelFormat.TRANSLUCENT));
		
		SideBar sideBar = (SideBar) personalLayout.findViewById(R.id.sideBar);  
		sideBar.setListView(personalListView); 
		sideBar.setHandlable(this);
		
    }
    
    
    /**
     * 加载公司通讯录
     */
    public void loadCompanyCommDir(){
    	commDirContainer.removeAllViews();
    }
    /**
     * 加载共享通讯录
     */
    public void loadShareCommDir(){
    	
    	commDirContainer.removeAllViews();
		//if(!shareCommDirLoaded){
			View shareLayout = inflater.inflate(R.layout.contact_share_commdir, commDirContainer);
			shareCommDirListView = (ListView) shareLayout.findViewById(R.id.contact_share_commdir_list); 
			shareCommDirList = CommDirDao.getSingle(me).getShareCommDirList();
			shareCommDirListAdapter = new ContactShareCommDirListAdapter(this,shareCommDirList);
			shareCommDirListView.setAdapter(shareCommDirListAdapter);
			shareCommDirListView.setOnItemClickListener(new OnItemClickListener()
    		{
    			@Override
    			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
    			{
    				Intent intent = new Intent(me,ShareUserActivity.class);
    				CommDir commDir = shareCommDirList.get(position);
    				intent.putExtra(TxlConstants.INTENT_BUNDLE_HEADER_TITLE, commDir.name);
    				intent.putExtra(TxlConstants.INTENT_BUNDLE_COMMDIR_ID, commDir.dirId);
    				intent.putExtra(TxlConstants.INTENT_BUNDLE_COUNT, commDir.contactCount);
    				startActivity(intent);
    			}
    		});
			//shareCommDirListAdapter.notifyDataSetChanged();
			
		/*	shareCommDirLoaded = true;
		}*/
    }
    public Handler handler = new Handler(){
    	public void handleMessage(Message msg)
        {
            if(msg.what == TxlConstants.CONTACT_HANDLER_HIDE_OVERLAY){
            	me.overlay.setVisibility(View.INVISIBLE);
            }else if(msg.what == TxlConstants.CONTACT_HANDLER_OVERLAY_VISIBLE){
            	visible = true;
            }
            
        }
    };

	@Override
	public Handler getHandler() {
		return this.handler;
	}
    
}
