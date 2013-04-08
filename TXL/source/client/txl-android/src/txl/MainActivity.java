package txl;

import txl.activity.MenuActivity;
import txl.activity.R;
import txl.activity.SiteActivity;
import txl.call.CallRecordActivity;
import txl.config.Config;
import txl.message.MessageActivity;
import txl.test.sidecom.Main;
import txl.test.tree.TreeViewActivity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends TabActivity
{
    private static int tabtxtsize=13;
    private TabHost tabHost;
    private final String TAG = MainActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        setContentView(view);
        tabHost = getTabHost();
        Config.tabHost = tabHost;
        
        Intent intent = new Intent().setClass(this, CallRecordActivity.class);
        setupTab("通话记录", intent);
        
        intent = new Intent().setClass(this, MessageActivity.class);
        setupTab("短信", intent);
        
        intent = new Intent().setClass(this, SiteActivity.class);
        setupTab("站点", intent);
        
        intent = new Intent().setClass(this, Main.class);
        setupTab("联系人", intent);
        
        intent = new Intent().setClass(this, TreeViewActivity.class);
        setupTab("菜单", intent);
        
        
        tabHost.setCurrentTab(0);
         
    }
    private void setupTab(final String tag,Intent intent) {
        View tabview = createTabView(tabHost.getContext(), tag);
        TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
        tabHost.addTab(setContent);

    }
    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tbitem, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setTextSize(tabtxtsize);
        tv.setText(text);
        return view;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
    
}
