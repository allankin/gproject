package txl.setting;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.NetworkAsyncTask;
import txl.common.TxlToast;
import txl.common.WebLoadingTipDialog;
import txl.common.po.Account;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.setting.po.Advise;
import txl.util.DeviceUtil;
import txl.util.HttpClientUtil;
import txl.util.Tool;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-9 下午3:05:03
 */
public class AdviseDialog
{
    private static AdviseDialog adviseDialog;
    private AdviseDialog(){
        
    }
    public static AdviseDialog getInstance(){
        if(adviseDialog==null){
            return new AdviseDialog();
        }
        return adviseDialog;
    }
    
    public void show(final TxlActivity ctx){
        final LayoutInflater factory = LayoutInflater.from(ctx);
        final View adviseView = factory.inflate(R.layout.setting_advise, null);
        
        final LinearLayout infoLayout = (LinearLayout)adviseView.findViewById(R.id.contact_info);
        final Advise advise = new Advise();
        Account account = Account.getSingle();
        if(account!=null){
        	if(account.phone!=null){
        		infoLayout.setVisibility(View.GONE);
        	}
        	advise.phone = account.phone;
        	advise.userId = account.userId;
        	advise.compCode = account.compCode;
        }
        advise.apkVersionName = ctx.getString(R.string.apkVersionName);
        advise.apkVersionCode = ctx.getResources().getInteger(R.integer.apkVersionCode);
        DeviceUtil du = new DeviceUtil(ctx);
        advise.widthPixels = du.widthPixels;
        advise.heightPixels = du.heightPixels;
        
        
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
        builder.setTitle(Config.getInstance().getTiptitle());
        builder.setView(adviseView); 
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", null);
        final android.app.AlertDialog alert = builder.create();
        alert.show();
        
        Button cancelBtn = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                alert.dismiss();
            }
        });
        Button theButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        
        theButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                TextView content = (TextView)adviseView.findViewById(R.id.content); 
                if(content.getText().length()==0){
                    Toast.makeText(ctx, "请输入您的反馈", TxlConstants.Toast.SHORT).show();  
                    return;
                }
                if(infoLayout.getVisibility() != View.GONE){
                	TextView emailOrPhoneTv = (TextView)adviseView.findViewById(R.id.emailOrPhone); 
                	if(emailOrPhoneTv.getText().length()==0){
                		Toast.makeText(ctx, "请输入您的联系方式", TxlConstants.Toast.SHORT).show(); 
                		return;
                	}
                	String emailOrPhone = emailOrPhoneTv.getText().toString().trim();
                	if(Tool.checkEmail(emailOrPhone)){
            			advise.email = emailOrPhone;
            		}else{
            			advise.phone = emailOrPhone;
            		}
                } 
                advise.content = content.getText().toString().trim();
                alert.dismiss();
                new AdviseTask(ctx).execute(advise);
                
            }
        });
    }
    
    
    class AdviseTask  extends NetworkAsyncTask<Advise, Void, Integer>{
        public TxlActivity ctx ;
        public AdviseTask(TxlActivity ctx){
            this.ctx = ctx;
        }
        
        @Override
        protected void onPreExecute(){
        	WebLoadingTipDialog.getInstance(ctx).show("正在提交反馈信息...");
        }
        @Override
        protected Integer doInBackground(Advise... advises)
        {
        	Advise advise = advises[0];
        	Map<String,String> params = new HashMap<String, String>();
        	
            params.put("advise.phone", advise.phone);
            params.put("advise.email", advise.email);
            params.put("advise.content", advise.content);
            params.put("advise.userId", String.valueOf(advise.userId));
            params.put("advise.compCode", advise.compCode);
            params.put("advise.osRelease", advise.osRelease);
            params.put("advise.model", advise.model);
            params.put("advise.widthPixels", String.valueOf(advise.widthPixels));
            params.put("advise.heightPixels", String.valueOf(advise.heightPixels));
            params.put("advise.apkVersionName", advise.apkVersionName);
            params.put("advise.apkVersionCode", String.valueOf(advise.apkVersionCode));
            int status=0;
            try {
				String body = HttpClientUtil.httpPostUTF8(TxlConstants.COMMIT_ADVISE_URL, params);
				JSONObject jobj = new JSONObject(body);
				status = jobj.optInt("status");
			} catch(Exception e){
				this.dealNetworkException(e,null);
			}
            return status;
        }
        @Override
        protected void onPostExecute(Integer status)
        {
        	WebLoadingTipDialog.getInstance(ctx).dismiss();
        	if(status==1){
        		TxlToast.showShort(ctx, "谢谢您的反馈!");
        	}else{
        		TxlToast.showShort(ctx, "反馈未成功，请到“关于我们”与我们电话联系!");
        	}
        }
    }
    
    
}
