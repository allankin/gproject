package txl.common.login;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONObject;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.NetworkAsyncTask;
import txl.common.TxlAlertDialog;
import txl.common.TxlAlertDialog.DialogInvoker;
import txl.common.TxlToast;
import txl.common.WebLoadingTipDialog;
import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.message.pushmessage.core.MessageManager;
import txl.util.HttpClientUtil;
import txl.util.Tool;
import txl.util.ValidateUtil;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginDialog {
	private static LoginDialog loginDialog;
	private android.app.AlertDialog loginAlert;
	private android.app.AlertDialog findBackAlert;
	 
	private LoginDialog(){
		
	}
	public static LoginDialog getInstance(){
		if(loginDialog==null){
			return new LoginDialog();
		}
		return loginDialog;
	}
	 
	public void dismissAllAlerts(){
		if(loginAlert!=null){
			loginAlert = null;
			loginAlert.dismiss();
		}
		if(findBackAlert!=null){
			findBackAlert = null;
			findBackAlert.dismiss();
		}
	}
	public void show(final TxlActivity ctx){
		final LayoutInflater factory = LayoutInflater.from(ctx);
        final View loginView = factory.inflate(R.layout.login, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
        builder.setTitle("用户登陆");
        builder.setView(loginView); 
        builder.setPositiveButton("登录", null);
        builder.setNegativeButton("取消", null);
        loginAlert = builder.create();
        loginAlert.show();
        
        Button cancelBtn = loginAlert.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	loginAlert.dismiss();
            }
        });
        Button theButton = loginAlert.getButton(DialogInterface.BUTTON_POSITIVE);
        
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
               
                loginAlert.dismiss();
                
                Account user = Account.getSingle();
                user.userName = untv.getText().toString();
                user.phone = user.userName;
                user.password = password.getText().toString();
                
                new LoginTask(ctx).execute(user);
                
                
            }
        });
        
        
        Button findPasswordBackBtn = (Button)loginView.findViewById(R.id.find_password_back_btn);
        findPasswordBackBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final View findBackView = factory.inflate(R.layout.find_password_back, null);
                final EditText findBack = (EditText)findBackView.findViewById(R.id.find_password_back_phone);
                String phone = Account.getSingle().phone;
                if(phone!=null){
                	findBack.setText(phone);
                }
                builder.setView(findBackView);
                builder.setPositiveButton("发送", null);
                findBackAlert = builder.create();
                findBackAlert.show(); 
                Button sendBtn = findBackAlert.getButton(DialogInterface.BUTTON_POSITIVE);
                sendBtn.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v)
                    {
                        String phone = findBack.getText().toString().trim();
                        if(ValidateUtil.isPhoneNumber(phone)){
                           Account user = Account.getNew();
                           user.phone = phone;
                           new FindBackPasswordTask(ctx).execute(user);
                        }else{
                            TxlToast.showShort(ctx, "手机号码不正确!");
                        }
                        
                    }
                    
                });
            }
        });
        
	}
	/**
	 * 异步任务：找回密码
	 * @author jinchao
	 *
	 */
	class FindBackPasswordTask extends NetworkAsyncTask<Account, Void, Account>{
        public FindBackPasswordTask(TxlActivity ctx){
            this.ctx = ctx;
        }
        @Override
        protected void onPreExecute(){
        	WebLoadingTipDialog.getInstance(ctx).show("正在查找..."); 
        }
        
        @Override
        protected Account doInBackground(Account... users)
        {
        	Account user = users[0];
            Map<String,String> params = new HashMap<String, String>();
            params.put("user.userName", user.phone);
            params.put("user.userPhone", user.phone);
            try {
				String body = HttpClientUtil.httpPostUTF8(TxlConstants.FIND_BACK_PASSWORD_URL, params);
				JSONObject jobj = new JSONObject(body);
				int status = jobj.optInt("status");
				user.findBackStatus = status;
			} catch(Exception e){
				this.dealNetworkException(e,null);
				/*若出异常，则将user赋为null*/
				user = null;
			}
            return user;
        }
        @Override
        protected void onPostExecute(Account userRet)
        {
        	WebLoadingTipDialog.getInstance(ctx).dismiss();
        	if(userRet!=null){
        		if(userRet.findBackStatus==1){
                    TxlToast.showShort(ctx, "短信已经发出,请注意查收");
                    findBackAlert.dismiss();
                }else{
                	TxlToast.showShort(ctx, "对不起，无法找到该用户，请核对！");
                }
        	}
        }
	}
	/**
	 * 异步任务：登录
	 * @author jinchao
	 *
	 */
	class LoginTask extends NetworkAsyncTask<Account, Void, Account>{
        public LoginTask(TxlActivity ctx){
            this.ctx = ctx;
        }
        @Override
        protected void onPreExecute(){
        	WebLoadingTipDialog.getInstance(ctx).show("正在登陆..."); 
        }
		@Override
		protected Account doInBackground(Account... user) {
			//HS_TODO 远程登陆
			Map<String,String> params = new HashMap<String,String>();
			params.put("user.account", user[0].userName);
			params.put("user.password", user[0].password);
			Account userRet = user[0];
			
			try {
				String body = HttpClientUtil.httpPostUTF8(TxlConstants.LOGIN_URL, params);
				body = body.trim();
				JSONObject json = new JSONObject(body);
				int loginStatus = json.optInt("status");
				userRet.loginStatus=loginStatus;
				/*登陆成功后*/
				if(loginStatus==1){
					JSONObject account = json.optJSONObject("account");
					userRet.userId= account.optInt("userId");
					userRet.compCode= account.optString("compCode");
					userRet.name= account.optString("name");
					userRet.phone= account.optString("phone");
					userRet.compId = account.optInt("compId");
					userRet.isSave = true;
					
					/*保存用户信息*/
					if(userRet.isSave){
						userRet.saveUserToFS();
					}
					
					MessageManager.startMessageService(ctx, userRet.userId, userRet.phone);
				}
			}catch(Exception e){
				this.dealNetworkException(e,null);
			}
			return userRet;
		}
		
		@Override
        protected void onPostExecute(Account userRet)
        {
            WebLoadingTipDialog.getInstance(ctx).dismiss();
            switch (userRet.loginStatus) {
                case 1:
                    TxlToast.showLong(ctx, "登陆成功!");
                    ctx.getHandler().sendMessage(Tool.genMessage(TxlConstants.SETTING_HANDLER_ONLINE_STATUS));
                    ctx.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_LOAD_COMPANY_COMMDIR));
                    break;
                case 2:
                	TxlToast.showLong(ctx, "用户名密码不能为空!");
                	loginFail();
                	break;
                case 3:
                    TxlToast.showLong(ctx, "用户名不存在 !");
                    loginFail();
                    break;
                case 4:
                    TxlToast.showLong(ctx, "密码不正确 !");
                    loginFail();
                    break;
                case 5:
                    TxlToast.showLong(ctx, "账号被冻结 !");
                    loginFail();
                    break;
                case 6:
                    TxlToast.showLong(ctx, "用户没有登陆权限 !");
                    loginFail();
                    break;
                case 7:
                	TxlToast.showLong(ctx, "产品管理员不能用手机登陆 !");
                	loginFail();
                	break;
                case 8:
                	TxlToast.showLong(ctx, "公司管理员不能用手机登陆 !");
                	loginFail();
                	break;
                case 9:
                	TxlToast.showLong(ctx, "您所在的公司已经过期，无法登陆 !");
                	loginFail();
                	break;
                default:
                    LoginDialog.getInstance().show(ctx);    
                    break;
            }
        }
		
		private void loginFail(){
			LoginDialog.getInstance().show(ctx);
			ctx.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_LOGIN_OFFLINE_STATUS));
		}
	}
}
	
	
	