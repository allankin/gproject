package txl.setting;

import java.util.List;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.TxlToast;
import txl.config.TxlConstants;
import txl.contact.dao.CommDirDao;
import txl.contact.po.CommDir;
import txl.contact.po.ShareUser;
import txl.log.TxLogger;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SyncSettingActivity extends TxlActivity {
	
private final TxLogger  log = new TxLogger(SyncSettingActivity.class, TxlConstants.MODULE_ID_SETTING);
	
	private SyncSettingActivity me = this;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_sync);
		TextView header = (TextView)findViewById(R.id.header);
		header.setText("同步号码");
		final TableLayout syncTable = (TableLayout)findViewById(R.id.setting_sync_share_item_table);
		View syncShareView = (View)findViewById(R.id.setting_sync_share);
		syncShareView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	
            	syncTable.removeAllViews();
            	List<CommDir> commDirList = CommDirDao.getSingle(me).getShareCommDirList();
            	
            	for(CommDir commDir:commDirList){
            		showShareCommDirItem(syncTable,commDir);
            	}
            	
            }
        });
	}
	
	
	private void showShareCommDirItem(TableLayout syncTable,final CommDir commDir){
		View row = createTableRow();
        syncTable.addView(row);
        TextView shareCommDirLabel = (TextView)row.findViewById(R.id.setting_share_commdir_label);
        shareCommDirLabel.setText(commDir.name);
        row.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	//List<ShareUser> shareUserList = CommDirDao.getSingle(me).getShareUserListByCommDirId(commDir.dirId);
            	
                TxlToast.showShort(me, "共享通讯录...");
            }
        });
	}
	
	
	private  View createTableRow(){
	    /*TableRow ntr = new TableRow(me);
	    ntr.setGravity(Gravity.CENTER_VERTICAL);
	    ntr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));*/
	    final LayoutInflater inflater = LayoutInflater.from(me);
	    TableLayout tableLayout = (TableLayout)inflater.inflate(R.layout.setting_row_clone, null);
	    TableRow row = (TableRow)tableLayout.findViewById(R.id.setting_sync_share_commdir_item); 
	    tableLayout.removeView(row);
	    return row;
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg)
        {
            
        }
		
	};
	@Override
	public Handler getHandler() {
		return handler;
	}

}
