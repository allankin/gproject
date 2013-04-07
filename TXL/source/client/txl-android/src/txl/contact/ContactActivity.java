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
import txl.contact.dao.ContactDao;
import txl.contact.po.ContactVo;
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
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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

    public List<ContactVo> contactList = new ArrayList<ContactVo>();
    public Set<String> contactPhoneSet = new HashSet<String>();

    ListView                      personalListView                 = null;
    ContactListAdapter            personalContactListAdapter                 = null;
    
    private ContactActivity me = this;

    private TextView overlay;
    
    private boolean visible;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        final LayoutInflater inflater = LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.tab_contact, null));
        this.overlay = (TextView) View.inflate(this, R.layout.overlay, null);
        headerView = (TextView)findViewById(R.id.header);
        headerView.setText("联系人");
        personalListView = (ListView) this.findViewById(R.id.contact_list); 
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
        
        SideBar sideBar = (SideBar) findViewById(R.id.sideBar);  
        sideBar.setListView(personalListView); 
        sideBar.setHandlable(this);
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
