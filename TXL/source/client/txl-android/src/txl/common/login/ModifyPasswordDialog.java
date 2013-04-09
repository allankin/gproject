package txl.common.login;

import txl.TxlActivity;
import txl.activity.R;
import txl.common.TxlToast;
import txl.common.WebLoadingTipDialog;
import txl.common.po.User;
import txl.config.Config;
import txl.config.TxlConstants;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @ClassName:  ModifyPasswordDialog.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-9 下午3:05:03
 */
public class ModifyPasswordDialog
{
    private static ModifyPasswordDialog modifyPasswordDialog;
    private ModifyPasswordDialog(){
        
    }
    public static ModifyPasswordDialog getInstance(){
        if(modifyPasswordDialog==null){
            return new ModifyPasswordDialog();
        }
        return modifyPasswordDialog;
    }
    
    public void show(final TxlActivity ctx){
        final LayoutInflater factory = LayoutInflater.from(ctx);
        final View modifyView = factory.inflate(R.layout.modify_password, null);
        
        
        CheckBox pwdVisible = (CheckBox)modifyView.findViewById(R.id.password_visible);
        pwdVisible.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                TextView password = (TextView)modifyView.findViewById(R.id.new_password); 
                if (isChecked) {
                    password.setInputType(0x90);
                } else {
                    password.setInputType(0x81);
                }
                
            }
        });
        
        
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
        builder.setTitle(Config.getInstance().getTiptitle());
        builder.setView(modifyView); 
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
                TextView untv = (TextView)modifyView.findViewById(R.id.userName); 
                if(untv.getText().length()==0){
                   Toast.makeText(ctx, "请输入用户名", TxlConstants.Toast.SHORT).show(); 
                   return;
                }
                TextView password = (TextView)modifyView.findViewById(R.id.new_password); 
                if(password.getText().length()==0){
                    Toast.makeText(ctx, "请输入密码", TxlConstants.Toast.SHORT).show();  
                    return;
                }
               
                alert.dismiss();
                
                WebLoadingTipDialog.getInstance(ctx).show("正在登陆...");
                
                User user = User.getNew();
                user.userName = untv.getText().toString();
                user.phone = user.userName;
                user.password = password.getText().toString();
                
                new ModifyPasswordTask(ctx).execute(user);
                 
                
            }
        });
    }
    
    
    class ModifyPasswordTask  extends AsyncTask<User, Void, User>{
        public TxlActivity ctx ;
        public ModifyPasswordTask(TxlActivity ctx){
            this.ctx = ctx;
        }
        @Override
        protected User doInBackground(User... users)
        {
            //HS_TODO: 远程修改密码
            
            User userRet = users[0];
            userRet.modifyPasswordStatus = 1;
            
            return userRet;
        }
        @Override
        protected void onPostExecute(User userRet)
        {
            if(userRet.modifyPasswordStatus==1){
                TxlToast.showShort(ctx, "密码修改成功!");
            }else{
                TxlToast.showLong(ctx, "密码修改失败!");
            }
                
        }
    }
    
    
}
