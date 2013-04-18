package txl.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import txl.MessageReceiver;
import txl.TxlActivity;
import txl.activity.R;
import txl.common.TxlHorizontalScrollView;
import txl.common.TxlHorizontalScrollView.SizeCallback;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.PushMessageActivity;
import txl.message.pushmessage.adapter.PushMsgListAdapter;
import txl.message.pushmessage.dao.PushMsgDao;
import txl.message.pushmessage.po.PushMsg;
import txl.message.pushmessage.po.PushMsgRecord;
import txl.message.sms.adapter.SmsCategoryListAdapter;
import txl.message.sms.adapter.SmsListAdapter;
import txl.message.sms.dao.SmsDao;
import txl.message.sms.po.SmsRecord;
import txl.util.IntentUtil;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

public class MessageActivity extends TxlActivity {
	
	private final TxLogger  log = new TxLogger(MessageActivity.class, TxlConstants.MODULE_ID_MESSAGE);
	
	private MessageActivity                       me        = null;
	//private TextView headerView = null;
	
	
	
	
	
	/************************************ SMS 模块变量  start ********************************************************/
	private ListView                      smsListView       = null;
	private ListView                      smsCategoryListView       = null;
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
    private View smsCategoryListLayout;
    private SmsCategoryListAdapter categoryListAdapter ;
    private Button headerSlideBtn;
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
    LinearLayout pushMessageListViewPartsContainer;
    
    /************************************ PUSHMessage 模块变量  end ********************************************************/
    
    
    
    private TxlHorizontalScrollView hScrollView;
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
        headerSlideBtn = (Button)findViewById(R.id.header_slide);
        
        messageTypeSpinner = (Spinner)findViewById(R.id.message_type);
        ArrayAdapter<String> messageTypeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{"短信","推送消息"});
        messageTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        messageTypeSpinner.setAdapter(messageTypeAdapter);
        
        
        
        hScrollView = (TxlHorizontalScrollView)findViewById(R.id.messageHorizontalScrollContainer);
        messageListViewPartsContainer = (LinearLayout)findViewById(R.id.messageListViewPartsContainer);
        //loadSmsModule(inflater);
        
        pushMessageListViewPartsContainer = (LinearLayout)findViewById(R.id.pushMessageListViewPartsContainer);
        
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
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                
            }
            
        });
        
        
        MessageReceiver mr = new MessageReceiver(me);
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
		pushMessageListViewPartsContainer.setVisibility(View.INVISIBLE);
		
		smsScrollListLayout = inflater.inflate(R.layout.sms_scroll_list, null);
        smsCategoryListLayout = inflater.inflate(R.layout.sms_category_scroll_list, null);
		
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
		
		smsCategoryListView = (ListView)smsCategoryListLayout.findViewById(R.id.sms_category_list);
		if(!smsModuleLoaded){
			SmsDao.loadSmsCategory(smsCategoryList);
		}
		categoryListAdapter = new SmsCategoryListAdapter(this,smsCategoryList,smsRecordMap,smsUnReadRecordMap,smsDraftRecordMap,smsListAdapter);
		smsCategoryListView.setAdapter(categoryListAdapter);
		
		headerSlideBtn.setVisibility(View.VISIBLE);
		if(!smsModuleLoaded){
			headerSlideBtn.setOnClickListener(new ClickListenerForScrolling(headerSlideBtn,hScrollView, smsCategoryListLayout));
		}
		
		final View[] children = new View[] { smsCategoryListLayout, smsScrollListLayout };
        /*  Scroll to app (view[1]) when layout finished. int scrollToViewIdx = 1;*/
        hScrollView.initViews(children, 0, new SizeCallbackForMenu(headerSlideBtn));
        
        smsModuleLoaded = true;
	}
	/**
	 * 加载PushMessage模块
	 * @param inflater
	 */
	public void loadPushMessageModule(LayoutInflater inflater){
		/*清理操作*/
		messageListViewPartsContainer.removeAllViews();
		pushMessageListViewPartsContainer.setVisibility(View.VISIBLE);
		//headerView.setText("推送消息列表");
		headerSlideBtn.setVisibility(View.INVISIBLE);
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
				int contactUserId = 0;
				PushMsgRecord record =  pushMsgMap.get(position);
				int count = record.pushMsgRecordList.size();
				if(record.pushMsg.type!= TxlConstants.PUSH_MESSAGE_TYPE_RECEIVE){
					contactUserId = record.pushMsg.recUserId;
				}else{
					contactUserId = record.pushMsg.sendUserId;
				}
				log.info("loadPushMessageModule .... contactUserId :"+contactUserId+",count:"+count);
				Intent intent = new Intent(me,PushMessageActivity.class);
				intent.putExtra(TxlConstants.INTENT_BUNDLE_CONTACT_ID, contactUserId);
				intent.putExtra(TxlConstants.INTENT_BUNDLE_COUNT, count);
				startActivity(intent);
			}
		});
		//messageListViewPartsContainer.addView(pushMsgScrollListLayout);
		
		pushMessageListViewPartsContainer.addView(pushMsgScrollListLayout);
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
	
	
	 /**
     * Helper for examples with a HSV that should be scrolled by a menu View's width.
     */
    static class ClickListenerForScrolling implements OnClickListener {
        HorizontalScrollView scrollView;
        View menu;
        /**
         * Menu must NOT be out/shown to start with.
         */
        boolean menuOut = false;
        Button slideBtn;

        public ClickListenerForScrolling(Button slideBtn,HorizontalScrollView scrollView, View menu) {
            super();
            this.scrollView = scrollView;
            this.menu = menu;
            this.slideBtn = slideBtn;
        }

        @Override
        public void onClick(View v) {
            Context context = menu.getContext();
            String msg = "Slide " + new Date();
            //Toast.makeText(context, msg, 1000).show();
            //System.out.println(msg);

            int menuWidth = menu.getMeasuredWidth();

            // Ensure menu is visible
            menu.setVisibility(View.VISIBLE);

            if (!menuOut) {
                // Scroll to 0 to reveal menu
                int left = 0;
                scrollView.smoothScrollTo(left, 0);
                slideBtn.setBackgroundResource(R.drawable.menu_unfold_ori);
            } else {
                // Scroll to menuWidth so menu isn't on screen.
                int left = menuWidth;
                scrollView.smoothScrollTo(left, 0);
                slideBtn.setBackgroundResource(R.drawable.menu_fold_ori);
            }
            menuOut = !menuOut;
        }
    }

    /**
     * Helper that remembers the width of the 'slide' button, so that the 'slide' button remains in view, even when the menu is
     * showing.
     */
    static class SizeCallbackForMenu implements SizeCallback {
        int btnWidth;
        View btnSlide;

        public SizeCallbackForMenu(View btnSlide) {
            super();
            this.btnSlide = btnSlide;
        }

        @Override
        public void onGlobalLayout() {
            btnWidth = btnSlide.getMeasuredWidth();
            System.out.println("btnWidth=" + btnWidth);
        }

        @Override
        public void getViewSize(int idx, int w, int h, int[] dims) {
            dims[0] = w;
            dims[1] = h;
            final int menuIdx = 0;
            if (idx == menuIdx) {
                //dims[0] = w - btnWidth;
            	dims[0] = btnWidth+20;
            }
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
        log.info("onDestroy");
    }
    
    private Handler handler = new Handler(){
        
    };
    @Override
    public Handler getHandler()
    {
        return handler;
    } 
}
