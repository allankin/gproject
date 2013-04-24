package txl.call;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import txl.TxlActivity;
import txl.activity.R;
import txl.call.adapter.CallRecordAdapter;
import txl.call.dao.CallRecordDao;
import txl.call.po.CallRecord;
import txl.common.DialWindow;
import txl.common.TxlAlertDialog;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.util.IntentUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @ClassName: CallRecordAcitivity.java
 * @Description:
 * @Author JinChao
 * @Date 2013-2-17 下午4:49:03
 */
public class CallRecordActivity extends TxlActivity
{
	private final TxLogger  log = new TxLogger(CallRecordActivity.class, TxlConstants.MODULE_ID_CONTACT);
	private CallRecordActivity                       me        = this;
	private ListView                      callRecordListView       = null;
	private CallRecordAdapter                 callRecordAdapter       = null;

    /*经过汇总后的通话记录*/
    private Map<Integer,CallRecord> callRecordMap = new HashMap<Integer,CallRecord>();
    private  DialWindow dw;
    
    
    private View layout ;
    private static boolean showDial = false;
    
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        layout =  LayoutInflater.from(this).inflate(R.layout.tab_call,null);
        setContentView(layout);
        TextView tv = (TextView)findViewById(R.id.header);
        tv.setText("通话记录");
        callRecordListView = (ListView)findViewById(R.id.call_list);
        
        CallRecordDao.getCallRecord(me,callRecordMap);
        
        callRecordAdapter = new CallRecordAdapter(this,callRecordMap);
        callRecordListView.setAdapter(callRecordAdapter);
        
        callRecordListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				log.info("position:"+position+" id: "+id);
				final CallRecord callRecord = callRecordMap.get(position);
				View actionBoardView = LayoutInflater.from(me).inflate(R.layout.contact_action_board,null);
				/*拨打电话*/
				TextView actionCall = (TextView)actionBoardView.findViewById(R.id.contact_action_call);
				actionCall.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						me.startActivity(IntentUtil.getCallIntent(callRecord.phoneNumber));
						TxlAlertDialog.alert.dismiss();
					}
				});
				/*发送sms*/
				TextView actionSendSms = (TextView)actionBoardView.findViewById(R.id.contact_action_send_sms);
				actionSendSms.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						me.startActivity(IntentUtil.getSmsSendDialogIntent("",callRecord.phoneNumber));
						TxlAlertDialog.alert.dismiss();
					}
				});
				
				
				/*发送推送消息*/
				TextView actionSendPushMessage = (TextView)actionBoardView.findViewById(R.id.contact_action_send_pushmessage);
				actionSendPushMessage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//HS_TODO: 打开消息推送编辑activity
						
						
						TxlAlertDialog.alert.dismiss();
					}
				});
				TxlAlertDialog.show(me, actionBoardView, "", null);
			}
        	
		});
        
        int width = getWindowManager().getDefaultDisplay().getWidth()-10;       
        //int height = getWindowManager().getDefaultDisplay().getHeight();   
        dw = new DialWindow(this, width, LayoutParams.WRAP_CONTENT);
        dw.getContentView().setFocusableInTouchMode(true);
        
        
        Config.tabHost.getTabWidget().getChildTabViewAt(0).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.ACTION_UP == event.getAction()){
					dw.showAtLocation(layout, Gravity.CENTER|Gravity.BOTTOM,0,0);
				}
				return false;
			}
		});
        
    }
    
    @Override
    protected void onNewIntent (Intent intent){
       log.info("onNewIntent");
       
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
    protected void onPause()
    {
        super.onPause();
        log.info("onPause");
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
        public void handleMessage(Message msg)
        {
            if(msg.what == TxlConstants.MSG_SEARCH_CALL_RECORD){
                Set<Entry<Integer, CallRecord>> entry = callRecordMap.entrySet();
                Iterator<Entry<Integer, CallRecord>>  it = entry.iterator();
                
            }
        }
    };
    @Override
    public Handler getHandler()
    {
        return handler;
    } 

}
