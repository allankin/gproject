package txl.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import txl.Handlable;
import txl.TxlActivity;
import txl.activity.R;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.PushMessageActivity;
import txl.message.pushmessage.adapter.PushMsgListAdapter;
import txl.message.pushmessage.dao.PushMsgDao;
import txl.message.pushmessage.po.PushMsgRecord;
import txl.message.sms.adapter.SmsCategoryListAdapter;
import txl.message.sms.adapter.SmsListAdapter;
import txl.message.sms.dao.SmsDao;
import txl.message.sms.po.SmsRecord;
import txl.util.IntentUtil;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

public class MessageActivity extends TxlActivity implements Handlable {
	
	private final TxLogger  log = new TxLogger(MessageActivity.class, TxlConstants.MODULE_ID_MESSAGE);
	
	private MessageActivity                       me        = null;
	//private TextView headerView = null;
	
	private MessageReceiver mr;
	
	
	
	/************************************ SMS 模块变量  start ********************************************************/
	private ListView                      smsListView       = null;
	private SmsListAdapter                smsListAdapter       = null;
	/*经过汇总后的通话记录*/
	private Map<Integer,SmsRecord> smsRecordMap = new HashMap<Integer,SmsRecord>();
	/*未汇总*/
    private Map<Integer,SmsRecord> smsUnReadRecordMap = new HashMap<Integer,SmsRecord>();
    /*未汇总*/
    private Map<Integer,SmsRecord> smsDraftRecordMap = new HashMap<Integer,SmsRecord>();
    /*未汇总*/
    private Map<Integer,SmsRecord> smsFavRecordMap = new HashMap<Integer,SmsRecord>();
    
    private List<String> smsCategoryList = new ArrayList<String>();
    private View smsScrollListLayout;
    private SmsCategoryListAdapter categoryListAdapter ;
    private boolean smsModuleLoaded = false;
    
    /************************************ SMS 模块变量  end ********************************************************/
    
    
    /************************************ PUSHMessage 模块变量  start ********************************************************/
    /*模块加载标识,防止重复加载数据产生性能问题*/
    private boolean pushMesssageModuleLoaded = false;
    
    
    private View pushMsgScrollListLayout;
    /*经过汇总后. key: 对方userId */
    private Map<Integer,PushMsgRecord> pushMsgMap = new HashMap<Integer,PushMsgRecord>();
     
    private ListView pushMsgListView =null;
    private PushMsgListAdapter pushMsgListAdapter = null;
    
    
    /************************************ PUSHMessage 模块变量  end ********************************************************/
    
    
    private LinearLayout messageListViewPartsContainer;
    private Spinner messageTypeSpinner ;
    
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		me = this;
		final LayoutInflater inflater = LayoutInflater.from(this);
		//hScrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.tab_message, null);
        setContentView(inflater.inflate(R.layout.tab_message, null));
		//setContentView(R.layout.tab_message);
        //headerView = (TextView)findViewById(R.id.header);
        
       /*
        messageTypeSpinner = (Spinner)findViewById(R.id.message_type);
        ArrayAdapter<String> messageTypeAdapter = new ArrayAdapter<String>(
        		this,
        		R.layout.spinner_style,
        		new String[]{"短信","推送消息"});
        messageTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        messageTypeSpinner.setAdapter(messageTypeAdapter);
        //messageTypeSpinner.setSelection(0,false);
        messageTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position==0){
                	loadSmsModule(inflater);
                }else{
                	loadPushMessageModule(inflater);
                }
                TextView tv = (TextView)view;
                tv.setTextColor(me.getResources().getColor(R.color.white));
                tv.setTextSize(25);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                
            }
        });
        */
        messageListViewPartsContainer = (LinearLayout)findViewById(R.id.messageListViewPartsContainer);
        
        RadioGroup messageType = (RadioGroup)findViewById(R.id.message_type);
        messageType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.sms){
					loadSmsModule(inflater);
				}else if(checkedId == R.id.pushmsg){
					loadPushMessageModule(inflater);
				}
			}
		});
        messageType.check(R.id.sms);
        
        
        mr = new MessageReceiver(me);
        IntentFilter filter = new IntentFilter(TxlConstants.ACTION_MESSAGE_RECEIVED);
		me.registerReceiver(mr, filter);
	}
	/**
	 * 加载SMS模块
	 * @param inflater
	 */
	public void loadSmsModule(LayoutInflater inflater){
		/*清理操作*/
		messageListViewPartsContainer.removeAllViews();
		smsScrollListLayout = inflater.inflate(R.layout.sms_scroll_list, null);
		
		//headerView.setText("短信列表");
		
		smsListView = (ListView)smsScrollListLayout.findViewById(R.id.sms_list);
		smsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SmsRecord sr = (SmsRecord)parent.getAdapter().getItem(position);
				Intent intent = IntentUtil.getSmsDetailIntent(sr.threadId);
				me.startActivity(intent);
			}
		});
		
		if(!smsModuleLoaded){
			log.startTiming();
		    SmsDao.loadSmsList(this, smsRecordMap, smsUnReadRecordMap, smsDraftRecordMap);
			log.endTiming("loadSmsList");
		}
		
		smsListAdapter = new SmsListAdapter(this,smsRecordMap);
		smsListView.setAdapter(smsListAdapter);
		
		if(!smsModuleLoaded){
			SmsDao.loadSmsCategory(smsCategoryList);
		}
		 
		messageListViewPartsContainer.addView(smsScrollListLayout);
        smsModuleLoaded = true;
	}
	/**
	 * 加载PushMessage模块
	 * @param inflater
	 */
	public void loadPushMessageModule(LayoutInflater inflater){
		/*清理操作*/
		messageListViewPartsContainer.removeAllViews();
		//headerView.setText("推送消息列表");
		pushMsgScrollListLayout = inflater.inflate(R.layout.pushmsg_scroll_list, null); 
		pushMsgListView = (ListView)pushMsgScrollListLayout.findViewById(R.id.pushmsg_list);
		//if(!pushMesssageModuleLoaded){
			PushMsgDao.getSingle(me).loadPushMsgList(me,pushMsgMap);
		//}
		pushMsgListAdapter = new PushMsgListAdapter(this,pushMsgMap);
		pushMsgListView.setAdapter(pushMsgListAdapter);
		pushMsgListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PushMsgRecord record =  pushMsgMap.get(position);
				int count = record.pushMsgRecordList.size();
				Intent intent = new Intent(me,PushMessageActivity.class);
				if(record.pushMsg.pushMsgType== TxlConstants.PUSHMSG_TYPE_NOT_CLASSFIED){
					int contactUserId = 0;
					String contactName;
					if(record.pushMsg.type!= TxlConstants.PUSH_MESSAGE_TYPE_RECEIVE){
						contactUserId = record.pushMsg.recUserId;
						contactName = record.pushMsg.recName;
					}else{
						contactUserId = record.pushMsg.sendUserId;
						contactName = record.pushMsg.sendName;
					}
					log.info("loadPushMessageModule .... contactUserId :"+contactUserId+",count:"+count);
					intent.putExtra(TxlConstants.INTENT_BUNDLE_CONTACT_ID, contactUserId);
					intent.putExtra(TxlConstants.INTENT_BUNDLE_CONTACT_NAME, contactName);
				}else{
					int pushMsgType = record.pushMsg.pushMsgType;
					intent.putExtra(TxlConstants.INTENT_BUNDLE_PUSHMSG_TYPE, pushMsgType);
					intent.putExtra(TxlConstants.INTENT_BUNDLE_PUSHMSG_TYPE_NAME, record.pushMsg.pushMsgTypeName);
					intent.putExtra(TxlConstants.INTENT_BUNDLE_PUSHMSG_CLASSIFIED, true);
				}
				//intent.putExtra(TxlConstants.INTENT_BUNDLE_COUNT, count);
				startActivity(intent);
			}
		});
		//messageListViewPartsContainer.addView(pushMsgScrollListLayout);
		
		messageListViewPartsContainer.addView(pushMsgScrollListLayout);
		//pushMesssageModuleLoaded = true;
	}
	
	/**
	 * 更新推送消息listview
	 */
	public void refreshPushMessageModule(){
		if(pushMsgListAdapter!=null){
			PushMsgDao.getSingle(me).loadPushMsgList(me,pushMsgMap);
			pushMsgListAdapter.notifyDataSetChanged();
		}
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
		getMenuInflater().inflate(R.menu.menu_sms, menu);
		int size = menu.size();
		TxlMessageOnMenuItemClickListener listener = new TxlMessageOnMenuItemClickListener();
		for(int i=0;i<size;i++){
			MenuItem item = menu.getItem(i);
			item.setOnMenuItemClickListener(listener);
		}
		 
        return true;
    }
	
	private class TxlMessageOnMenuItemClickListener implements OnMenuItemClickListener{

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			
			if(item.getItemId() == R.id.menu_sms_all){
				smsListAdapter.smsRecordMap = smsRecordMap;
				smsListAdapter.notifyDataSetChanged();
			}else if(item.getItemId() == R.id.menu_sms_unread){
				smsListAdapter.smsRecordMap = smsUnReadRecordMap;
				smsListAdapter.notifyDataSetChanged();
			}else if(item.getItemId() == R.id.menu_sms_draft){
				smsListAdapter.smsRecordMap = smsDraftRecordMap;
				smsListAdapter.notifyDataSetChanged();
			}else if(item.getItemId() == R.id.menu_setting){
				Config.tabHost.setCurrentTab(3);
			}
			return false;
		}
		
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
        me.unregisterReceiver(mr);
        log.info("onDestroy");
    }
    
    private Handler handler = new Handler(){
        public void handleMessage(Message msg)
        {
            if(msg.what == TxlConstants.MSG_HANDLER_SELECT_MESSAGE_TYPE){
                Integer messageType = (Integer)msg.obj;
                RadioGroup messageTypeRG = (RadioGroup)findViewById(R.id.message_type);
                messageTypeRG.check(messageType);
            }
        }
    };
    @Override
    public Handler getHandler()
    {
        return handler;
    } 
}
