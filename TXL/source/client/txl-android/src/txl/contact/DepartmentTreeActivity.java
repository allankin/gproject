package txl.contact;

import java.util.ArrayList;
import java.util.List;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.TxlToast;
import txl.config.TxlConstants;
import txl.contact.dao.CommDirDao;
import txl.contact.po.Department;
import txl.contact.task.DepartmentQueryTask;
import txl.log.TxLogger;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @ClassName: DepartmentTreeActivity.java
 * @Description:
 * @Author JinChao
 * @Date 2013-4-11 上午10:48:58
 */
public class DepartmentTreeActivity extends TxlActivity
{
    private final TxLogger log = new TxLogger(DepartmentTreeActivity.class, TxlConstants.MODULE_ID_CONTACT);
    private ArrayList<Department> mPdfOutlinesCount = new ArrayList<Department>();
    private TreeViewAdapter       treeViewAdapter   = null;

    private DepartmentTreeActivity              me                = this;
    private TextView headerView = null;
    private int depId;
    //private Class requestClass = null;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final LayoutInflater inflater = LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.contact_department_tree, null));
        headerView = (TextView)findViewById(R.id.header);
        headerView.setText("部门选择");
        Intent intent = me.getIntent();
        if(intent!=null){
            depId = intent.getIntExtra(TxlConstants.INTENT_BUNDLE_DEPART_ID, 0);
            //requestClass = (Class)bundle.get(TxlConstants.INTENT_BUNDLE_REQUEST_CLASS);
        }
         
        new DepartmentQueryTask(me).execute();
        super.renderHeader();
    }
    
    private void loadDepartment(){
    	Department topDepart = CommDirDao.getSingle(me).getTopDepartmentTree();
    	if(topDepart!=null){
    	    mPdfOutlinesCount.add(topDepart);
    	}
        
        treeViewAdapter = new TreeViewAdapter(this, R.layout.contact_department_tree_item, mPdfOutlinesCount);
        ListView listView = (ListView) findViewById(R.id.department_tree_list);
        listView.setAdapter(treeViewAdapter);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.i("TreeView", "position:" + position);
                if (!mPdfOutlinesCount.get(position).mhasChild)
                {
                    return;
                }
                Department depart = mPdfOutlinesCount.get(position);
                if (depart.expanded)
                {
                    depart.expanded = false;
                    ArrayList<Department> temp = new ArrayList<Department>();

                    for (int i = position + 1; i < mPdfOutlinesCount.size(); i++)
                    {
                        Department _depart = mPdfOutlinesCount.get(i);
                        if (depart.level >= _depart.level)
                        {
                            break;
                        }
                        temp.add(_depart);
                    }

                    mPdfOutlinesCount.removeAll(temp);

                } else
                {
                    depart.expanded = true;
                    int level = depart.level;
                    int nextLevel = level + 1;

                    for (Department element : depart.childList)
                    {
                        element.level = nextLevel;
                        element.expanded = false;
                        mPdfOutlinesCount.add(position + 1, element);

                    }
                }
                treeViewAdapter.notifyDataSetChanged();
            }
        });
    }
    
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        
        switch (keyCode) {  
            case KeyEvent.KEYCODE_BACK:
            	finish();
                return false;
            default: 
                return super.onKeyUp(keyCode, event);    
        }
    }

    private class TreeViewAdapter extends ArrayAdapter
    {
        private LayoutInflater   mInflater;
        private List<Department> mfilelist;
        private Bitmap           mIconCollapse;
        private Bitmap           mIconExpand;

        public TreeViewAdapter(Context context, int textViewResourceId, List objects)
        {
            super(context, textViewResourceId, objects);
            mInflater = LayoutInflater.from(context);
            mfilelist = objects;
            mIconCollapse = BitmapFactory.decodeResource(context.getResources(), R.drawable.fold);
            mIconExpand = BitmapFactory.decodeResource(context.getResources(), R.drawable.unfold);
        }

       
        public int getCount()
        {
            return mfilelist.size();
        }

        public Object getItem(int position)
        {
            return position;
        }

        public long getItemId(int position)
        {
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            /*if (convertView == null)
            {*/
                convertView = mInflater.inflate(R.layout.contact_department_tree_item, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.text);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);

            /*} else
            {
                holder = (ViewHolder) convertView.getTag();
            }*/

            final Department depart = mfilelist.get(position);
            holder.text.setOnLongClickListener(new OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    //if(requestClass!=null){
                        Intent intent = new Intent(me,ContactActivity.class);
                        intent.putExtra(TxlConstants.INTENT_BUNDLE_DEPART, depart);
                        setResult(TxlConstants.REQUEST_CODE_SELECT_DEPARTMENT, intent);
                        log.info("onLongClick....  depId: "+depart.depId+", depName: "+depart.depName);
                    //}
                        
                        finish();
                        return false;
                }
            });
            Log.i("TreeView", "getView :  position: "+position);
            int level = depart.level;
            holder.icon.setPadding(25 * (level + 1), holder.icon.getPaddingTop(), 0, holder.icon.getPaddingBottom());
            holder.text.setText(depart.depName);
            if (depart.mhasChild && (depart.expanded == false))
            {
                holder.icon.setImageBitmap(mIconCollapse);
            } else if (depart.mhasChild && (depart.expanded == true))
            {
                holder.icon.setImageBitmap(mIconExpand);
            } 
            return convertView;
        }

        class ViewHolder
        {

            TextView  text;
            ImageView icon;

        }
    }
    
    private Handler handler = new Handler(){
		public void handleMessage(Message msg)
	    {
	        if(msg.what == TxlConstants.MSG_LOAD_DEPARTMENT){
	        	me.loadDepartment();
	        } 
	        
	    }
    };
   
    @Override
    public Handler getHandler()
    {
        return this.handler;
    }

}
