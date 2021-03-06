package txl.contact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import txl.Handlable;
import txl.TxlActivity;
import txl.activity.R;
import txl.common.ActionBoard;
import txl.common.ActionBoard.ActionContact;
import txl.common.SideBar;
import txl.common.TxlToast;
import txl.common.login.LoginDialog;
import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.contact.adapter.ContactCompanyUserListAdapter;
import txl.contact.adapter.ContactListAdapter;
import txl.contact.adapter.ContactShareCommDirListAdapter;
import txl.contact.dao.ContactDao;
import txl.contact.po.CommDir;
import txl.contact.po.CommDirQuery;
import txl.contact.po.CompanyUser;
import txl.contact.po.ContactVo;
import txl.contact.po.Department;
import txl.contact.po.UserQuery;
import txl.contact.task.CampanyUserQueryTask;
import txl.contact.task.ShareCommDirQueryTask;
import txl.log.TxLogger;
import txl.util.ContactVoComparator;
import txl.util.Tool;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * @ClassName: CallRecord.java
 * @Description:
 * @Author JinChao
 * @Date 2013-2-17 下午3:54:19
 */
public class ContactActivity extends TxlActivity implements Handlable
{
	

	private final TxLogger log = new TxLogger(ContactActivity.class, TxlConstants.MODULE_ID_CONTACT);
    Context                       mContext                  = null;
    private TextView headerView = null;

    private LinearLayout commDirContainer;
    private LayoutInflater inflater;
    private ContactActivity me = this;
    private SideBar sideBar;
    
    
    /************************************* 个人通讯录 变量 *****************************************************/
    private List<ContactVo> contactList = new ArrayList<ContactVo>();
    private Set<String> contactPhoneSet = new HashSet<String>();
    private ListView                      personalListView                 = null;
    private ContactListAdapter            personalContactListAdapter                 = null;
    
    private TextView overlay;
    private boolean overLayVisibleFlag;
    private boolean personalCommDirLoaded = false;
    private View personalLayout;
    EditText contactSearch;
    ImageView btnClearSearch;
    public List<ContactVo> searchList = new ArrayList<ContactVo>();
    
    /************************************* 公司通讯录 变量 *****************************************************/
    private boolean companyCommDirLoaded = false;
    private List<CompanyUser> companyUserList;
    private ListView                      companyUserListView                 = null;
    private ContactCompanyUserListAdapter contactCompanyUserListAdapter = null;
    private View companyUserLayout;
    
    
    
    
    /************************************* 共享通讯录 变量 *****************************************************/
    private boolean shareCommDirLoaded = false;
    private List<CommDir> shareCommDirList;
    private ListView                      shareCommDirListView                 = null;
    private ContactShareCommDirListAdapter            shareCommDirListAdapter                 = null;
    private View shareLayout;
    
    
    
    
    
    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        inflater = LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.tab_contact, null));
        sideBar =(SideBar)findViewById(R.id.sideBar);
        //View layout = findViewById(R.id.header_layout);
        
        /*根据实际分辨率设置间距*/
        double itemHeight = (TxlConstants.tabContentHeight - 54*1.5)/26;
        log.info("itemHeight:"+itemHeight);
        sideBar.setItemHeight((int)itemHeight);
        sideBar.invalidate();
        
        commDirContainer = (LinearLayout)findViewById(R.id.contact_commdir_container);
        CommDirTypeOnCheckedChangeListener listner = new CommDirTypeOnCheckedChangeListener();
        RadioGroup personComDir = (RadioGroup)findViewById(R.id.comm_dir_type);
        personComDir.setOnCheckedChangeListener(listner);
        personComDir.check(R.id.personal_comdir);
        
		/*
		Spinner commdirTypeSpinner = (Spinner)findViewById(R.id.commdir_type);
		ArrayAdapter<String> commDirTypeAdapter = new ArrayAdapter<String>(this,R.layout.spinner_style,me.getResources().getStringArray(R.array.contact_commdir_type));
        commDirTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		commdirTypeSpinner.setAdapter(commDirTypeAdapter);
		commdirTypeSpinner.setSelection(0,false);
		默认为个人通讯录
		loadPersonalCommDir();
		commdirTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position==0){
                	loadPersonalCommDir();
                }else if(position == 1){
                    loadCompanyCommDir();
                }else if(position == 2){
                	loadShareCommDir();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });*/
        
        
    }
    
    
    /**
     * 加载个人通讯录
     */
    public void loadPersonalCommDir(){
    	
    	commDirContainer.removeAllViews();
    	
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
    	
    	
		personalLayout = inflater.inflate(R.layout.contact_personal_commdir, commDirContainer);
		contactSearch = (EditText) findViewById(R.id.contact_search);
		btnClearSearch = (ImageView) findViewById(R.id.btn_clean_search);
        btnClearSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                log.info("btnClearSearch click ....");
                Tool.hideSoftInputFromWindow(me);
                contactSearch.setText("");
                btnClearSearch.setVisibility(View.GONE);
                personalContactListAdapter.contactList = contactList;
                updateSearchHint();
                //personalContactListAdapter.isShowNum = false;
                personalContactListAdapter.notifyDataSetChanged();
                personalListView.invalidate();
            }
        });
		
        contactSearch.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                overlay.setVisibility(View.GONE);
            }
        });
        
        contactSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            //  Log.i("text changed", "start=" + start + "before" + before);
                if (s.length() != 0) {
                    btnClearSearch.setVisibility(View.VISIBLE);
                    sideBar.setVisibility(View.GONE);
                    overlay.setVisibility(View.GONE);
                    ContactDao.getSearchUser(me,searchList,s.toString());
                    personalContactListAdapter.contactList = searchList;
                    overLayVisibleFlag = false;
                    //personalContactListAdapter.isShowNum = true;
                } else {
                    btnClearSearch.setVisibility(View.GONE);
                    personalContactListAdapter.contactList = contactList;
                    sideBar.setVisibility(View.VISIBLE);
                    overlay.setVisibility(View.GONE);
                    overLayVisibleFlag = true;
                    //personalContactListAdapter.isShowNum = false;
                }
                updateSearchHint();
                personalContactListAdapter.notifyDataSetChanged();
                personalListView.invalidate();      
            }
            
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
                //Log.i("before text changed", "start=" + start );
                
            }
            
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                
                
            }
        });
        
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
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				log.info("position:"+position+" id: "+id);
				final ContactVo contactVo = contactList.get(position);
				ActionContact ac = new ActionBoard.ActionContact();
				ac.phone = contactVo.phone;
				ActionBoard.show(me, ac);
			}
		});
		personalListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				overLayVisibleFlag = true;
				if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
				    if(overlay!=null){
				        overlay.setVisibility(View.INVISIBLE);
				    }
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (overLayVisibleFlag) {
					char firstLetter = contactList.get(firstVisibleItem).firstLetter;
					if(firstLetter!='0'){
					    if(overlay!=null){
					        overlay.setText(String.valueOf(firstLetter));
					        overlay.setVisibility(View.VISIBLE);
					    }
					}
				}
			}
		});
		
		
		  
		sideBar.setVisibility(View.VISIBLE);
		if(!personalCommDirLoaded){
			sideBar.setListView(personalListView); 
			sideBar.setHandlable(this);
		}
		
    }
    public void updateSearchHint(){
        Resources res = getResources();
        String text = String.format(res.getString(R.string.contact_search_hint_text), personalContactListAdapter.getCount());     
        contactSearch.setHint(text);
    }
    
    /**
     * 加载公司通讯录
     */
    public void loadCompanyCommDir(){
    	commDirContainer.removeAllViews();
    	sideBar.setVisibility(View.INVISIBLE);
        if(this.overlay!=null){
			getWindowManager().removeView(this.overlay);	
			this.overlay = null;
		}
    	
    	companyUserLayout = inflater.inflate(R.layout.contact_company_commdir, commDirContainer);
    	final EditText searchTxtView = (EditText)companyUserLayout.findViewById(R.id.company_user_search);
    	
    	final TextView depName = (TextView)companyUserLayout.findViewById(R.id.company_depName);
    	depName.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Object obj = v.getTag();
                Integer depId =0;
                if(obj!=null){
                    depId = (Integer)obj;
                }
                Intent intent = new Intent(me,DepartmentTreeActivity.class);
                intent.putExtra(TxlConstants.INTENT_BUNDLE_DEPART_ID,depId);
                me.startActivityForResult(intent,TxlConstants.REQUEST_CODE_SELECT_DEPARTMENT);
            }
        });
    	
    	
    	//companyUserList = CommDirDao.getSingle(me).getCompUserList();
    	companyUserList = new ArrayList<CompanyUser>();
        companyUserListView                 = (ListView) companyUserLayout.findViewById(R.id.contact_company_user_list);
        companyUserListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);     
		        imm.hideSoftInputFromWindow(searchTxtView.getWindowToken(), 0);  
				return false;
			}
		});
        contactCompanyUserListAdapter =  new ContactCompanyUserListAdapter(me,companyUserList);
        companyUserListView.setAdapter(contactCompanyUserListAdapter);
        
        companyUserListView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                CompanyUser user = companyUserList.get(position);
                Intent intent = new Intent(me,CompanyUserDetailActivity.class);
                intent.putExtra(TxlConstants.INTENT_BUNDLE_COMPANY_USER, user);
                startActivity(intent);
            }
            
        });
        
        Button searchBtn = (Button)companyUserLayout.findViewById(R.id.company_search_btn);
    	searchBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                UserQuery uq = new UserQuery();
                Object tag = depName.getTag();
                uq.depId = tag==null?0:(Integer)tag;
                uq.name = searchTxtView.getText().toString().trim();
                uq.compId = Account.getSingle().compId;
                new CampanyUserQueryTask(me).execute(uq);
            }
        });
     
    }
    /**
     * 加载共享通讯录
     */
    public void loadShareCommDir(){
    	
    	commDirContainer.removeAllViews();
		//if(!shareCommDirLoaded){
			shareLayout = inflater.inflate(R.layout.contact_share_commdir, commDirContainer);
			shareCommDirListView = (ListView) shareLayout.findViewById(R.id.contact_share_commdir_list); 
			shareCommDirList = new ArrayList<CommDir>();
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
    				intent.putExtra(TxlConstants.INTENT_BUNDLE_COUNT, commDir.userCount);
    				startActivity(intent);
    			}
    		});
			Button searchBtn = (Button)shareLayout.findViewById(R.id.share_search_btn);
			searchBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CommDirQuery cdq = new CommDirQuery();
					EditText nameView = (EditText)shareLayout.findViewById(R.id.share_commdir_search);
					cdq.sbName = nameView.getText().toString().trim();
					Account account = Account.readUserFromFS();
					/* 共享库访问， 需要携带outUserId和compCode， 这两个需要用户登陆成功后，并将其加密保存在本地  
					 * 若为account 为null，则需要重新登陆
					 * */
					if(account==null || account.compCode==null){
						LoginDialog.getInstance().show(me);
					}else{
						new ShareCommDirQueryTask(me).execute(cdq);
					}
					 
				}
			});
			
			//shareCommDirListAdapter.notifyDataSetChanged();
			
		/*	shareCommDirLoaded = true;
		}*/
			
		sideBar.setVisibility(View.INVISIBLE);
		if(this.overlay!=null){
			getWindowManager().removeView(this.overlay);	
			this.overlay = null;
		}
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
    	log.info(" onActivityResult  resultCode : "+resultCode);
        switch (resultCode) { 
            case TxlConstants.REQUEST_CODE_SELECT_DEPARTMENT:
                TextView depName = (TextView)companyUserLayout.findViewById(R.id.company_depName);
                Serializable obj = data.getSerializableExtra(TxlConstants.INTENT_BUNDLE_DEPART);
                if(obj!=null){
                    Department depart = (Department)obj;
                    log.info("onActivityResult.... depName: "+depart.depName+", depId: "+depart.depId);
                    depName.setText(depart.depName); 
                    /*若为顶级部门 */
                    if(depart.depParentId==0){
                    	depName.setTag(null);
                    }else{
                    	depName.setTag(depart.depId);
                    }
                }
                break;  
            default:  
                break;  
        }  
    }  
    public Handler handler = new Handler(){
    	public void handleMessage(Message msg)
        {
            if(msg.what == TxlConstants.CONTACT_HANDLER_HIDE_OVERLAY){
            	me.overlay.setVisibility(View.INVISIBLE);
            }else if(msg.what == TxlConstants.CONTACT_HANDLER_OVERLAY_VISIBLE){
            	overLayVisibleFlag = true;
            }else if(msg.what == TxlConstants.MSG_RENDER_COMPANY_USER){
            	companyUserList.clear();
            	List<CompanyUser> users = (List<CompanyUser>)msg.obj;
            	for(CompanyUser user: users){
            		companyUserList.add(user); 
            	}
            	contactCompanyUserListAdapter.notifyDataSetChanged();
            }else if(msg.what == TxlConstants.MSG_LOAD_COMPANY_COMMDIR){
            	loadCompanyCommDir();
            }else if(msg.what == TxlConstants.MSG_SHARE_COMPANY_NOT_EXIST){
            	TxlToast.showLong(me, "您所在的公司未开启共享!");
            }else if(msg.what == TxlConstants.MSG_SHARE_USER_NOT_EXIT){
            	TxlToast.showLong(me, "您的还未共享自身信息!");
            }else if(msg.what == TxlConstants.MSG_LOAD_SHARE_COMMDIR){
            	shareCommDirList.clear();
            	List<CommDir> commDirs = (List<CommDir>)msg.obj;
            	for(CommDir commDir: commDirs){
            		shareCommDirList.add(commDir); 
            	}
            	shareCommDirListAdapter.notifyDataSetChanged();
            }else if(msg.what == TxlConstants.MSG_SYNC_SHARE_COMMDIR_USER){
            	TxlToast.showShort(me, "共享通讯录同步成功!");
            }
            
        }
    };

	@Override
	public Handler getHandler() {
		return this.handler;
	}
    
	
	
	@Override
    protected void onNewIntent (Intent intent){
       log.info("onNewIntent");
       
    } 
    
    @Override
    protected void onPause()
    {
        super.onPause();
        log.info("onPause");
        if(overlay!=null){
        	overlay.setVisibility(View.INVISIBLE);
        }
        
    }

    @Override
    protected void onStart(){
        super.onStart();
        log.info("onStart");
        
    }
    
    @Override
    protected void onRestart(){
        super.onRestart();
        log.info("onRestart");
        
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        log.info("onResume");
        
    }
    @Override
    protected void onStop(){
        super.onStop();
        log.info("onStop");
        
    }
    
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
        log.info("onDestroy");
        
        /*防止泄露*/
        if(this.overlay!=null){
        	getWindowManager().removeView(overlay);
        	overlay = null;
        }
    }

 
    private class CommDirTypeOnCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if(checkedId == R.id.personal_comdir){
				loadPersonalCommDir();
			}else if(checkedId == R.id.share_comdir){
				loadShareCommDir();
			}else if(checkedId == R.id.company_comdir){
				loadCompanyCommDir();
			}
		}
 

		 
	}
}
