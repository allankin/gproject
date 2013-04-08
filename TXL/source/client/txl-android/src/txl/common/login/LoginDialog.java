package txl.common.login;

import java.util.Timer;
import java.util.TimerTask;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.WebLoadingTipDialog;
import txl.common.po.User;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.util.Tool;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginDialog {
	private static LoginDialog loginDialog;
	private LoginDialog(){
		
	}
	public static LoginDialog getInstance(){
		if(loginDialog==null){
			return new LoginDialog();
		}
		return loginDialog;
	}
	 
	
	public void show(final TxlActivity ctx){
		LayoutInflater factory = LayoutInflater.from(ctx);
        final View loginView = factory.inflate(R.layout.login, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
        builder.setTitle(Config.getInstance().getTiptitle());
        builder.setView(loginView); 
        builder.setPositiveButton("登录", null);
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
                TextView untv = (TextView)loginView.findViewById(R.id.userName); 
                if(untv.getText().length()==0){
                   Toast.makeText(ctx, "请输入用户名", TxlConstants.Toast.SHORT).show(); 
                   return;
                }
                TextView password = (TextView)loginView.findViewById(R.id.password); 
                if(password.getText().length()==0){
                    Toast.makeText(ctx, "请输入密码", TxlConstants.Toast.SHORT).show();  
                    return;
                }
               
                alert.dismiss();
                
                WebLoadingTipDialog.getInstance(ctx).show("正在登陆...");
                
                User user = new User();
                user.userName = untv.getText().toString();
                user.phone = user.userName;
                user.password = password.getText().toString();
                
                new LoginTask(ctx).execute(user);
                new Timer().schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        ctx.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                WebLoadingTipDialog.getInstance(ctx).overLoadingDismiss();
                            }
                        });
                        
                    }
                }, TxlConstants.LOADING_INTERVAL);
                
            }
        });
	}
	
	
	class LoginTask extends AsyncTask<User, Void, User>{
        public TxlActivity ctx ;
        public LoginTask(TxlActivity ctx){
            this.ctx = ctx;
        }
        @Override
        protected void onPostExecute(User userRet)
        {
        	
        	WebLoadingTipDialog.getInstance(ctx).dismiss();
        	switch (userRet.loginStatus) {
        		case 1:
        			Toast.makeText(ctx, "登陆成功!", TxlConstants.Toast.LONG).show();
        			ctx.getHandler().sendMessage(Tool.genMessage(TxlConstants.SETTING_HANDLER_ONLINE_STATUS));
        			
        			break;
 				case 2:
					Toast.makeText(ctx, "用户名密码不能为空!", TxlConstants.Toast.LONG).show();
				case 3:
					Toast.makeText(ctx, "用户名不存在 !", TxlConstants.Toast.LONG).show();
				case 4:
					Toast.makeText(ctx, "密码不正确 !", TxlConstants.Toast.LONG).show();
				case 5:
					Toast.makeText(ctx, "账号被冻结 !", TxlConstants.Toast.LONG).show();
				case 6:
					Toast.makeText(ctx, "用户没有登陆权限 !", TxlConstants.Toast.LONG).show();
				default:
					LoginDialog.getInstance().show(ctx);	
					break;
			}
        }
		@Override
		protected User doInBackground(User... user) {
			//HS_TODO 远程登陆
			User userRet = user[0];
			userRet.userId= 1;
			userRet.loginStatus=1;
			userRet.compCode="";
			userRet.userName="lisi";
			userRet.password = "lisi";
			userRet.name="李斯";
			userRet.isSave = true;
			
			
			/*登陆成功后*/
			if(user[0].loginStatus==1){
				/*保存用户信息*/
				if(userRet.isSave){
					userRet.saveUserToFS();
				}
				
			}
			
			return userRet;
		}
	}
}
	
	
	